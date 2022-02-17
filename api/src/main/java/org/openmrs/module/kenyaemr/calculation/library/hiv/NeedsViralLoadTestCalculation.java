/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.BreastFeedingStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.IsBreastFeedingCalculation;
import org.openmrs.module.kenyaemr.calculation.library.IsPregnantCalculation;
import org.openmrs.module.kenyaemr.calculation.library.PregnancyStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.LessThanThreeMonthsOnARTCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.MoreThanThreeMonthsOnARTCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnArtCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils.daysSince;

public class NeedsViralLoadTestCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {
    protected static final Log log = LogFactory.getLog(StablePatientsCalculation.class);
    /**
     * Needs vl test calculation criteria
     * ----------------------------------
     * Immediately = Pregnant + Breastfeeding + Already on ART
     * After 3 months = unsuppressed ALL
     * Aftre 6 months = Children (0-24) + Pregnant_Breastfeeding + Newly on ART
     * After 12 months = suppressed
     * @see org.openmrs.module.kenyacore.calculation.PatientFlagCalculation#getFlagMessage()
     */
    @Override
    public String getFlagMessage() {
        return "Due for Viral Load";
    }

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        PatientService patientService = Context.getPatientService();

        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);
        //Cohorts to consider
        //  Pregnant
        Set<Integer> pregnant = CalculationUtils.patientsThatPass(calculate(new IsPregnantCalculation(), cohort, context));
        // Breastfeeding
        Set<Integer> breastFeeding = CalculationUtils.patientsThatPass(calculate(new IsBreastFeedingCalculation(), cohort, context));
        // New on ART defined as less than 3 months on ART
        Set<Integer> newOnArt = CalculationUtils.patientsThatPass(calculate(new LessThanThreeMonthsOnARTCalculation(), cohort, context));
        // Already on ART defined as more than 3 months on ART
        Set<Integer> alreadyOnArt = CalculationUtils.patientsThatPass(calculate(new MoreThanThreeMonthsOnARTCalculation(), cohort, context));
        // All on ART already
        Set<Integer> allOnArt = CalculationUtils.patientsThatPass(calculate(new OnArtCalculation(), cohort, context));
        // VL Suppressed for last results
        Set<Integer> suppressed = CalculationUtils.patientsThatPass(calculate(new SuppressedVLPatientsCalculation(), cohort, context));
        // VL UnSuppressed for last results
        Set<Integer> unsuppressed = CalculationUtils.patientsThatPass(calculate(new UnSuppressedVLPatientsCalculation(), cohort, context));
        // Patients with pending vl results
        Set<Integer> pendingVlResults = CalculationUtils.patientsThatPass(calculate(new PendingViralLoadResultCalculation(), cohort, context));
        //check for last vl date
        CalculationResultMap lastVlDate = calculate(new LastViralLoadResultDateCalculation(), cohort, context);
        //check for last pregnancy start date
        CalculationResultMap pregnancyStartDate = calculate(new PregnancyStartDateCalculation(), cohort, context);
        //check for last breastfeeding start date
        CalculationResultMap breastFeedingStarDate = calculate(new BreastFeedingStartDateCalculation(), cohort, context);

        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId:cohort) {
            Patient patient = patientService.getPatient(ptId);
            boolean needsViralLoadTest = false;
            Date lastVlDateValue = EmrCalculationUtils.datetimeResultForPatient(lastVlDate, ptId);
            Date lastPregStartDate = EmrCalculationUtils.datetimeResultForPatient(pregnancyStartDate, ptId);
            Date lastBFStarDate = EmrCalculationUtils.datetimeResultForPatient(breastFeedingStarDate, ptId);

            // Needs vl immediately : In hiv + Already on ART + Pregnant + Breastfeeding + without pending vl
            if(inHivProgram.contains(ptId) && !pendingVlResults.contains(ptId) && alreadyOnArt.contains(ptId) && (pregnant.contains(ptId) || breastFeeding.contains(ptId))){
                if(lastVlDateValue == null || (lastVlDateValue != null && (daysBetweenDates(lastPregStartDate,lastVlDateValue) > 1 || daysBetweenDates(lastBFStarDate,lastVlDateValue) > 1))) {
                    needsViralLoadTest = true;
                }
            }
            // Needs vl after 3 months : In hiv +  Unsuppressed  + without pending vl
            if(inHivProgram.contains(ptId) && !pendingVlResults.contains(ptId) && unsuppressed.contains(ptId)){
                if(lastVlDateValue != null && daysSince(lastVlDateValue, context) >= 92){
                    needsViralLoadTest = true;
                }
            }
            // Needs vl after 6 months : In hiv +  without pending vl + (Newly on ART  + Pregnant + Breastfeeding + Children (< 24 years))
            if(inHivProgram.contains(ptId) && !pendingVlResults.contains(ptId)){
                if(lastVlDateValue != null){
                    if(newOnArt.contains(ptId) && (pregnant.contains(ptId) || breastFeeding.contains(ptId)) && daysSince(lastVlDateValue, context) >= 183){
                        needsViralLoadTest = true;
                    }
                    if(patient.getAge() < 24 && daysSince(lastVlDateValue, context) >= 183){
                        needsViralLoadTest = true;
                    }
               }
              if(lastVlDateValue == null && allOnArt.contains(ptId) && !(pregnant.contains(ptId) || breastFeeding.contains(ptId))){
                    needsViralLoadTest = true;
                }
            }
            // Needs vl after 12 months : In hiv +  without pending vl + suppressed
            if(inHivProgram.contains(ptId) && !pendingVlResults.contains(ptId) && suppressed.contains(ptId)){
                if(lastVlDateValue != null && daysSince(lastVlDateValue, context) >=  365){
                    needsViralLoadTest = true;
                  }
               }
            ret.put(ptId, new BooleanResult(needsViralLoadTest, this));
        }
        return  ret;
    }

    int daysBetweenDates(Date d1, Date d2) {
        DateTime dateTime1 = new DateTime(d1.getTime());
        DateTime dateTime2 = new DateTime(d2.getTime());
        return Math.abs(Days.daysBetween(dateTime1, dateTime2).getDays());
    }
}
