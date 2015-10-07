package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.IsPregnantCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.models.PatientEligibility;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.Age;

import java.util.Collection;
import java.util.Date;
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
        Set<Integer> inTbProgram = Filters.inProgram(tbProgram, alive, context);
        //find those who have status for tb
        CalculationResultMap tbStatus = Calculations.lastObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS), cohort, context);
        //find pregnant women
        Set<Integer> pregnantWomen = CalculationUtils.patientsThatPass(calculate(new IsPregnantCalculation(), cohort, context));
        //finding those at risk for hiv
        CalculationResultMap hivRiskFactor = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_RISK_FACTOR), cohort, context);
        //DNA PCR
        CalculationResultMap dnaPcrQualitative = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION_QUALITATIVE), cohort, context);
        CalculationResultMap dnaPcrReaction = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION), cohort, context);

        //find breast feeding map
        CalculationResultMap breastFeedingMap = Calculations.lastObs(Dictionary.getConcept(Dictionary.INFANT_FEEDING_METHOD), cohort, context);
        Concept hepatitisB = Dictionary.getConcept(Dictionary.HEPATITIS_B);
        Concept acuteTypeBViralHepatitis = Dictionary.getConcept(Dictionary.ACUTE_TYPE_B_VIRAL_HEPATITIS);
        Concept acuteFulminatingTypeBViralHepatitis = Dictionary.getConcept(Dictionary.ACUTE_FULMINATING_TYPE_B_VIRAL_HEPATITIS);
        Concept chronicActiveTypeBViralHepatitis = Dictionary.getConcept(Dictionary.CHRONIC_ACTIVE_TYPE_B_VIRAL_HEPATITIS);

        for (Integer ptId : cohort) {
            PatientEligibility patientEligibility = null;
            Date hasHepatitisDate = null;
            Date hasTbDate = null;
            Date hasPregnantDate = null;
            Date hasDiscordant = null;
            Date hasBreastFeedingDate = null;
            PatientProgram patientProgram = EmrCalculationUtils.resultForPatient(inHivProgram, ptId);

            if (patientProgram != null && !onArt.contains(ptId)) {

                int ageInMonths = ((Age) ages.get(ptId).getValue()).getFullMonths();
                Obs cd4 = EmrCalculationUtils.obsResultForPatient(lastCd4, ptId);
                Obs whoStage = EmrCalculationUtils.obsResultForPatient(lastWhoStage, ptId);

                patientEligibility = getCriteriaAndDate(ageInMonths, cd4, whoStage, hasHepatitisDate, hasTbDate, hasPregnantDate, hasDiscordant, hasBreastFeedingDate, patientProgram.getDateEnrolled() );

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
