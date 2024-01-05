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

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.models.PatientEligibility;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.Age;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 10/7/15.
 */
public class EligibleForArtDateAndReasonCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);

        Set<Integer> alive = Filters.alive(cohort, context);
        CalculationResultMap inHivProgram = Calculations.activeEnrollment(hivProgram, alive, context);

        // need to exclude those on ART already
        Set<Integer> onArt = CalculationUtils.patientsThatPass(calculate(new OnArtCalculation(), cohort, context));
        CalculationResultMap ret = new CalculationResultMap();

        CalculationResultMap ages = Calculations.ages(cohort, context);

        CalculationResultMap lastWhoStage = calculate(new LastWhoStageCalculation(), cohort, context);
        CalculationResultMap lastCd4 = calculate(new LastCd4CountCalculation(), cohort, context);

        //find hepatits status
        CalculationResultMap hepatitisMap = Calculations.lastObs(Dictionary.getConcept(Dictionary.PROBLEM_ADDED), cohort, context);
        //in tb program
        CalculationResultMap inTbProgram = Calculations.activeEnrollment(tbProgram, alive, context);
        //find those who have status for tb
        CalculationResultMap tbStatus = Calculations.lastObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS), cohort, context);

        //finding those at risk for hiv
        CalculationResultMap hivRiskFactor = Calculations.allObs(Dictionary.getConcept(Dictionary.HIV_RISK_FACTOR), cohort, context);

        //find breast feeding map
        CalculationResultMap breastFeedingMap = Calculations.lastObs(Dictionary.getConcept(Dictionary.INFANT_FEEDING_METHOD), cohort, context);
        Concept hepatitisB = Dictionary.getConcept(Dictionary.HEPATITIS_B);
        Concept acuteTypeBViralHepatitis = Dictionary.getConcept(Dictionary.ACUTE_TYPE_B_VIRAL_HEPATITIS);
        Concept acuteFulminatingTypeBViralHepatitis = Dictionary.getConcept(Dictionary.ACUTE_FULMINATING_TYPE_B_VIRAL_HEPATITIS);
        Concept chronicActiveTypeBViralHepatitis = Dictionary.getConcept(Dictionary.CHRONIC_ACTIVE_TYPE_B_VIRAL_HEPATITIS);


        //pregnancy status
        Set<Integer> aliveAndFemale = Filters.female(Filters.alive(cohort, context), context);
        EncounterType mchEnrollment = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_ENROLLMENT);

        Concept yes = Dictionary.getConcept(Dictionary.YES);
        CalculationResultMap pregStatusObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.PREGNANCY_STATUS), aliveAndFemale, context);
        CalculationResultMap enrollmentMap = Calculations.lastEncounter(mchEnrollment, aliveAndFemale, context);

        for (Integer ptId : cohort) {
            PatientEligibility patientEligibility = null;
            Date hasHepatitisDate = null;
            Date hasTbDate = null;
            Date hasPregnantDate = null;
            Date hasDiscordantDate = null;
            Date hasBreastFeedingDate = null;
            PatientProgram patientProgramHiv = EmrCalculationUtils.resultForPatient(inHivProgram, ptId);
            PatientProgram patientProgramTb = EmrCalculationUtils.resultForPatient(inTbProgram, ptId);

            //pregnancy issues
            Obs pregStatusObs = EmrCalculationUtils.obsResultForPatient(pregStatusObss, ptId);
            Encounter encounter = EmrCalculationUtils.encounterResultForPatient(enrollmentMap, ptId);

            if (patientProgramHiv != null && !onArt.contains(ptId)) {

                //check for Tb
                Obs hasTbObs = EmrCalculationUtils.obsResultForPatient(tbStatus, ptId);
                Date tbStartDateProg = null, tbStartDateObs = null;
                if(patientProgramTb != null){
                    tbStartDateProg = patientProgramTb.getDateEnrolled();
                }
                if((hasTbObs != null && hasTbObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.DISEASE_DIAGNOSED))) || (hasTbObs != null && hasTbObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.ON_TREATMENT_FOR_DISEASE)))) {
                    tbStartDateObs = hasTbObs.getObsDatetime();
                }
                if(tbStartDateProg != null && tbStartDateObs == null){
                    hasTbDate = tbStartDateProg;
                }
                else if(tbStartDateObs != null && tbStartDateProg == null){
                    hasTbDate =tbStartDateObs;
                }
                else if(tbStartDateProg != null && tbStartDateObs != null){
                    //pick the first one
                    hasTbDate = CoreUtils.earliest(tbStartDateProg, tbStartDateObs);
                }

                //check for HPV
                Obs hepatitisObs = EmrCalculationUtils.obsResultForPatient(hepatitisMap, ptId);
                if(hepatitisObs != null && (hepatitisObs.getValueCoded().equals(hepatitisB) || hepatitisObs.getValueCoded().equals(acuteTypeBViralHepatitis) || hepatitisObs.getValueCoded().equals(acuteFulminatingTypeBViralHepatitis) || hepatitisObs.getValueCoded().equals(chronicActiveTypeBViralHepatitis))) {
                    hasHepatitisDate = hepatitisObs.getObsDatetime();
                }

                //discordant couples
                ListResult listResult = (ListResult) hivRiskFactor.get(ptId);
                List<Obs> isDiscodantCoupleObs = CalculationUtils.extractResultValues(listResult);
                if(isDiscodantCoupleObs.size() > 0 ){
                    for(Obs obs: isDiscodantCoupleObs){
                        if(obs.getValueCoded().equals(Dictionary.getConcept(Dictionary.DISCORDANT_COUPLE))) {
                            hasDiscordantDate = obs.getObsDatetime();
                            break;
                        }
                    }
                }
                //breast feeding
                Obs infantFeedingObs = EmrCalculationUtils.obsResultForPatient(breastFeedingMap, ptId);
                if(infantFeedingObs != null && (infantFeedingObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.BREASTFED_EXCLUSIVELY)) || infantFeedingObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.MIXED_FEEDING)))) {
                    hasBreastFeedingDate = infantFeedingObs.getObsDatetime();
                }

                //check for pregnancy
                if (pregStatusObs != null && pregStatusObs.getValueCoded().equals(yes)) {
                    hasPregnantDate = pregStatusObs.getObsDatetime();
                }

                if(encounter != null) {
                    hasPregnantDate = encounter.getEncounterDatetime();
                }

                int ageInMonths = ((Age) ages.get(ptId).getValue()).getFullMonths();
                Obs cd4 = EmrCalculationUtils.obsResultForPatient(lastCd4, ptId);
                Obs whoStage = EmrCalculationUtils.obsResultForPatient(lastWhoStage, ptId);

                patientEligibility = getCriteriaAndDate(ageInMonths, cd4, whoStage, hasHepatitisDate, hasTbDate, hasPregnantDate, hasDiscordantDate, hasBreastFeedingDate, patientProgramHiv.getDateEnrolled() );

            }
            ret.put(ptId, new SimpleResult(patientEligibility, this));

        }
        return ret;
    }

    /**
     * Checks eligibility based on age, CD4 and WHO stage
     * @param ageInMonths the patient age in months
     * @param cd4 the last CD4 count
     * @param whoStage the last WHO stage
     * @return true if patient is eligible
     */
    protected PatientEligibility getCriteriaAndDate(int ageInMonths, Obs cd4, Obs whoStage, Date hasHepatitisDate, Date hasTbDate, Date hasPregnantDate, Date hasSeroDiscordantDate, Date hasBreastFeedingDate, Date hivEnrollmentDate) {
        if (ageInMonths <= 120) {//children less than 10 years
            return new PatientEligibility("Age 10 years and below", hivEnrollmentDate);
        }
        else if (ageInMonths > 120 && ageInMonths <= 180) { //children above 10 years and not above 15 years
            if (whoStage != null && (EmrUtils.whoStage(whoStage.getValueCoded()) == 3 || EmrUtils.whoStage(whoStage.getValueCoded()) == 4)) {
                return new PatientEligibility("WHO STAGE IV", whoStage.getObsDatetime());
            }

            if(hasPregnantDate != null){
                return new PatientEligibility("Pregnant", hasPregnantDate);
            }
            if(hasSeroDiscordantDate != null){
                return new PatientEligibility("Discordant couple (HIV-negative partner)", hasSeroDiscordantDate);
            }
            if(hasBreastFeedingDate != null){
                return new PatientEligibility("Breastfeeding", hasBreastFeedingDate);
            }
            if(hasHepatitisDate != null){
                return new PatientEligibility("HPV/HIV Coinfection", hasHepatitisDate);
            }
            if(hasTbDate != null) {
                return new PatientEligibility("TB/HIV Coinfection", hasTbDate);
            }

        }

        else { // 15+ years
            if (cd4 != null && cd4.getValueNumeric() < 500) {
                return new PatientEligibility("CD4 count<=500", cd4.getObsDatetime());
            }
            if (whoStage != null && (EmrUtils.whoStage(whoStage.getValueCoded()) == 3 || EmrUtils.whoStage(whoStage.getValueCoded()) == 4)) {
                return new PatientEligibility("WHO STAGE IV", whoStage.getObsDatetime());
            }

            if(hasPregnantDate != null){
                return new PatientEligibility("Pregnant", hasPregnantDate);
            }
            if(hasSeroDiscordantDate != null){
                return new PatientEligibility("Discordant couple (HIV-negative partner)", hasSeroDiscordantDate);
            }
            if(hasBreastFeedingDate != null){
                return new PatientEligibility("Breastfeeding", hasBreastFeedingDate);
            }
            if(hasHepatitisDate != null){
                return new PatientEligibility("HPV/HIV Coinfection", hasHepatitisDate);
            }
            if(hasTbDate != null) {
                return new PatientEligibility("TB/HIV Coinfection", hasTbDate);
            }
        }
        return null;
    }
}
