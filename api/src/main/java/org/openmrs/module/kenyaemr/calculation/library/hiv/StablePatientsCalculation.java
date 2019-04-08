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
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A calculation that returns patients who are stable
 * Eligibility criteria include:
 * have been on current regimen for >=12 months
 * no active OI
 * no missed appointments in the last 6 months
 * most recent vl < 1000 copies/ml
 * bmi >= 18.5
 * has completed 6 months ipt
 * age >= 20 years old
 * longer follow-up intervals (depends on clinician ==>longer than 4 months)
 *
 * Adds "Stable" flag on patient's dashboard
 */
public class StablePatientsCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

    protected static final Log log = LogFactory.getLog(StablePatientsCalculation.class);
    static ConceptService conceptService = Context.getConceptService();

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        /* Commented this autocalculated section untill we get more input  */
        /* Instead we use the stability concept input below on the greencard form    */
        /*
        Concept latestHeight = Dictionary.getConcept(Dictionary.WEIGHT_KG);
        Concept latestWeight = Dictionary.getConcept(Dictionary.HEIGHT_CM);
        Concept latestVL = Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD);
        Concept TCAdate = Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE);
        Concept LDLQuestion = Context.getConceptService().getConcept(1305);
        Concept LDLAnswer = Context.getConceptService().getConcept(1302);
        Concept iptStart = Context.getConceptService().getConcept(1265);

        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);

        CalculationResultMap lastHeightObs = Calculations.lastObs(latestHeight, inHivProgram, context);
        CalculationResultMap lastWeightObs = Calculations.lastObs(latestWeight, inHivProgram, context);
        CalculationResultMap lastVLObs = Calculations.lastObs(latestVL, inHivProgram, context);
        CalculationResultMap lastLDLObs = Calculations.lastObs(LDLQuestion, inHivProgram, context);
        CalculationResultMap artStartDate = calculate(new CurrentARTStartDateCalculation(), cohort, context);
        CalculationResultMap iptStarted = Calculations.lastObs(iptStart, inHivProgram, context);
        CalculationResultMap nextAppointmentMap = Calculations.lastObs(TCAdate, cohort, context);

        Set<Integer> ltfu = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));
        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId: cohort){
            Double height = EmrCalculationUtils.numericObsResultForPatient(lastHeightObs, ptId);
            Double weight = EmrCalculationUtils.numericObsResultForPatient(lastWeightObs, ptId);
            Double vl = EmrCalculationUtils.numericObsResultForPatient(lastVLObs, ptId);

            Concept ldl = EmrCalculationUtils.codedObsResultForPatient(lastLDLObs, ptId);
            Double bmi= 0.0;
            Obs lastVLObsResult = null;
            Integer inRegimenFor12months = 0;
            Integer iptCompletionDays = 0;
            Integer tcaPlus90days = 0;
            Date iptStartDate = null;
            Date tcaDate = null;
            Date tcaObsDate = null;
            boolean patientInHivProgram = false;
            boolean patientInHivProgram12Months = false;
            boolean patientInIPT6Months = false;
            boolean bmiAndAgeQualifier = false;
            boolean vlResultsQualifer = false;
            boolean patientActive = false;
            boolean stable = false;
            Date currentDate =new Date();
         //With TCA more than 4 months
            Obs nextOfVisitObs = EmrCalculationUtils.obsResultForPatient(nextAppointmentMap, ptId);

            if(nextOfVisitObs != null){
                tcaDate = nextOfVisitObs.getValueDatetime();
                tcaObsDate = nextOfVisitObs.getObsDatetime();
                tcaPlus90days= daysBetween(tcaDate,tcaObsDate);
            }
            if(tcaDate != null && tcaObsDate != null && tcaPlus90days >= 120) {
                stable = true;

            }else {

                // get latest of ldl or vl
                if (ldl != null && vl != null) {
                    Obs vlObs = EmrCalculationUtils.obsResultForPatient(lastVLObs, ptId);
                    Obs ldlObs = EmrCalculationUtils.obsResultForPatient(lastLDLObs, ptId);
                    lastVLObsResult = EmrCalculationUtils.findLastOnOrBefore(Arrays.asList(vlObs, ldlObs), context.getNow());
                } else if (ldl != null && vl == null) {
                    lastVLObsResult = EmrCalculationUtils.obsResultForPatient(lastLDLObs, ptId);
                } else if (ldl == null && vl != null) {
                    lastVLObsResult = EmrCalculationUtils.obsResultForPatient(lastVLObs, ptId);
                }
                // compute BMI
                if (height != null && weight != null) {
                    double heightInMetres = height / 100;
                    double heightSquared = heightInMetres * heightInMetres;
                    bmi = weight / heightSquared;

                }

                // get patient's age
                Integer age = Context.getPatientService().getPatient(ptId).getAge(new Date());
                if (inHivProgram.contains(ptId)) {
                    patientInHivProgram = true;
                }
                //have been on current regimen for >=12 months
                Date currentARTStartDate = EmrCalculationUtils.datetimeResultForPatient(artStartDate, ptId);
                if (currentARTStartDate != null) {
                    inRegimenFor12months = daysBetween(currentDate, currentARTStartDate);
                }
                if(inRegimenFor12months >= 365) {
                    patientInHivProgram12Months = true;
                }
                //have completed 6 months IPT
                Obs iptObs = EmrCalculationUtils.obsResultForPatient(iptStarted, ptId);
                if (iptObs != null) {

                    iptStartDate = iptObs.getObsDatetime();
                    iptCompletionDays = daysBetween(currentDate, iptStartDate);
                }
                if (iptObs == null || iptObs.getValueCoded().getConceptId().equals(1066)) {
                    stable = false;
                }

                if (iptObs != null && iptObs.getValueCoded().getConceptId().equals(1065) && iptCompletionDays >= 182) {

                    patientInIPT6Months = true;
                }

                if (bmi != 0.0 && bmi >= 18.5 && age >= 20) {
                    bmiAndAgeQualifier = true;
                }

                if (lastVLObsResult != null && ((lastVLObsResult.getConcept() == latestVL && lastVLObsResult.getValueNumeric() < 1000) || (lastVLObsResult.getConcept() == LDLQuestion && lastVLObsResult.getValueCoded() == LDLAnswer))) {
                    vlResultsQualifer = true;
                }

                if (!ltfu.contains(ptId)) {
                    patientActive = true;
                }

                if (patientInHivProgram && patientInHivProgram12Months && patientInIPT6Months && bmiAndAgeQualifier && vlResultsQualifer && patientActive)
                    stable = true;

               }
            ret.put(ptId, new BooleanResult(stable, this));
        }
        return ret;

        */

      /*This is the stability determination using the stability concept for a client from the previous visit */



        Integer StabilityQuestion = 1855;

        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);
        Set<Integer> ltfu = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));

        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {

            boolean stable = false;
            boolean reportedStable = false;
            boolean patientActive = false;
            boolean patientInHivProgram = false;

            Encounter lastFollowUpEncounter = EmrUtils.lastEncounter(Context.getPatientService().getPatient(ptId), Context.getEncounterService().getEncounterTypeByUuid("a0034eee-1940-4e35-847f-97537a35d05e"));   //last greencard followup form
            if (lastFollowUpEncounter != null) {
                for (Obs obs : lastFollowUpEncounter.getObs()) {
                    if (obs.getConcept().getConceptId().equals(StabilityQuestion) && (obs.getValueBoolean().equals(true) || obs.getValueCoded().getConceptId().equals(1))) {
                        reportedStable = true;
                    }
                }
            }
            if (!ltfu.contains(ptId)) {
                patientActive = true;
            }
            if (inHivProgram.contains(ptId)) {
                patientInHivProgram = true;
            }

            if (patientInHivProgram && patientActive && reportedStable){
                stable = true;
              }
        ret.put(ptId, new BooleanResult(stable, this));
        }
        return ret;
    }
        @Override
    public String getFlagMessage() {
        return "Stable";
    }
}