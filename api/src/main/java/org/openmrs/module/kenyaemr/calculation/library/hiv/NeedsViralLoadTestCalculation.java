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
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
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
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.LastViralLoadResultCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnArtCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;

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
     * After 3 months = unsuppressed ALL + Pregnant_Breastfeeding + Newly on ART
     * Aftre 6 months = Children (0-24) + Without first VL
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
        // All on ART already
        Set<Integer> allOnArt = CalculationUtils.patientsThatPass(calculate(new OnArtCalculation(), cohort, context));
        // Patients with pending vl results
        Set<Integer> pendingVlResults = CalculationUtils.patientsThatPass(calculate(new PendingViralLoadResultCalculation(), cohort, context));
        //check for last pregnancy start date
        CalculationResultMap pregnancyStartDate = calculate(new PregnancyStartDateCalculation(), cohort, context);
        //check for last breastfeeding start date
        CalculationResultMap breastFeedingStarDate = calculate(new BreastFeedingStartDateCalculation(), cohort, context);
        //get the initial art start date
        CalculationResultMap dateInitiatedART = calculate(new InitialArtStartDateCalculation(), cohort, context);
        //check for last vl and date
        LastViralLoadResultCalculation lastVlResultCalculation = new LastViralLoadResultCalculation();
        CalculationResultMap lastVlResults = lastVlResultCalculation.evaluate(cohort, null, context);
        //Checks for ltfu
        Set<Integer> ltfu = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));

        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId:cohort) {
            Patient patient = patientService.getPatient(ptId);
            boolean needsViralLoadTest = false;
            String lastVlResult = null;
            String lastVlResultLDL = null;
            Double lastVlResultValue = null;
            Date lastVLResultDate = null;
            Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(dateInitiatedART, ptId);
            Date lastPregStartDate = EmrCalculationUtils.datetimeResultForPatient(pregnancyStartDate, ptId);
            Date lastBFStartDate = EmrCalculationUtils.datetimeResultForPatient(breastFeedingStarDate, ptId);
            //Check for latest vl and if it exists (vl is only valid if its for the last 12 months)
            CalculationResult lastvlresult = lastVlResults.get(ptId);
            if (lastvlresult != null && lastvlresult.getValue() != null) {
                Object lastVl =  lastvlresult.getValue();
                SimpleObject res = (SimpleObject) lastVl;
                lastVlResult = res.get("lastVl").toString();
                lastVLResultDate = (Date) res.get("lastVlDate");
                // Differentiate between LDL and values for Viral load results
                if(lastVlResult =="LDL"){
                    lastVlResultLDL = "LDL";
                }else{
                    lastVlResultValue =  Double.parseDouble(lastVlResult);
                }
            }
            // Confirm that patient is on hiv and there are no pending vls
            if(inHivProgram.contains(ptId) && !pendingVlResults.contains(ptId) && allOnArt.contains(ptId) && !ltfu.contains(ptId)) {
                // Classification for pregnant and breastfeeding
                if (pregnant.contains(ptId) || breastFeeding.contains(ptId)) {
                    // Needs vl immediately : Already on ART (more than 3 months on ART)
                    if (daysSince(artStartDate, context) >= 92) {
                        if (lastVLResultDate == null ) {
                            needsViralLoadTest = true;
                        }
                        if (lastVLResultDate != null && ((lastPregStartDate != null && lastPregStartDate.after(lastVLResultDate)) || (lastBFStartDate != null && lastBFStartDate.after(lastVLResultDate)))) {
                            needsViralLoadTest = true;
                        }
                    }
                    //Needs vl after 3 months : Newly on ART (less than 3 months on ART)  -- will never show
                    if (daysSince(artStartDate, context) < 92) {
                        if (lastVLResultDate == null && daysSince(artStartDate, context) >= 92) {
                            needsViralLoadTest = true;
                        }
                    }
                    //Needs vl after 6 months : If last vl is suppressed    For follow up
                    if (lastVlResult != null && (lastVlResultLDL != null || lastVlResultValue < 1000) && daysSince(lastVLResultDate, context) > 183) {
                        needsViralLoadTest = true;
                    }
                }
                // Classification for General population and kids <24 years
                if (!pregnant.contains(ptId) || !breastFeeding.contains(ptId)) {
                    // Needs vl after 6 months :  Without initial vl
                    if (lastVLResultDate == null && daysSince(artStartDate, context) >= 183){
                        needsViralLoadTest = true;
                    }
                    // Needs vl after 6 months :  Children (< 24 years) for followup
                    if (lastVLResultDate != null  && patient.getAge() < 24 && daysSince(lastVLResultDate, context) >= 183) {
                        needsViralLoadTest = true;
                    }
                    // Needs vl after 12 months : Suppressed
                    if (lastVlResult != null && (lastVlResultLDL != null || lastVlResultValue < 1000) && daysSince(lastVLResultDate, context) >= 365) {
                        needsViralLoadTest = true;
                    }
                    // Needs vl after 3 months : All Unsuppressed
                    if (lastVLResultDate !=null && lastVlResultValue != null && lastVlResultValue >= 1000 && daysSince(lastVLResultDate, context) >= 92) {
                        needsViralLoadTest = true;
                    }
                    ret.put(ptId, new BooleanResult(needsViralLoadTest, this));
                }

            }
        }
        return  ret;
    }

}
