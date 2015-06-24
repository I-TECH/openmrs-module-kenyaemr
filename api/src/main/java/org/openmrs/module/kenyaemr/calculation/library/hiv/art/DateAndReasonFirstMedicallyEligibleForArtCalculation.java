package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.IsPregnantCalculation;
import org.openmrs.module.kenyaemr.calculation.library.models.PatientEligibility;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.Age;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 23/06/15.
 * Calculates the date and reason a patient was eligible for ART
 */
public class DateAndReasonFirstMedicallyEligibleForArtCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        CalculationResultMap eligibleDateMap = Calculations.lastObs(Dictionary.getConcept(Dictionary.DATE_MEDICALLY_ELIGIBLE_FOR_ART), cohort, context);
        CalculationResultMap eligibleReasonMap = Calculations.lastObs(Dictionary.getConcept(Dictionary.REASON_ELIGIBLE_FOR_ART), cohort, context);

        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);

        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, cohort, context);
        //in tb program
        Set<Integer> inTbProgram = Filters.inProgram(tbProgram, cohort, context);

        CalculationResultMap ages = Calculations.ages(cohort, context);

        Set<Integer> female = Filters.female(cohort, context);

        CalculationResultMap allWhoStage = Calculations.allObs(Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE), cohort, context);
        CalculationResultMap allCd4 = Calculations.allObs(Dictionary.getConcept(Dictionary.CD4_COUNT), cohort, context);
        CalculationResultMap artStartDateMap = calculate(new InitialArtStartDateCalculation(), cohort, context);

        //find hepatits status
        CalculationResultMap hepatitisMap = Calculations.lastObs(Dictionary.getConcept(Dictionary.PROBLEM_ADDED), cohort, context);

        //find pregnant women
        CalculationResultMap pregStatusObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.PREGNANCY_STATUS), female, context);
        //finding those at risk for hiv
        CalculationResultMap hivRiskFactor = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_RISK_FACTOR), cohort, context);

        //find those who have status for tb
        CalculationResultMap tbStatus = Calculations.lastObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS), cohort, context);

        //DNA PCR
        CalculationResultMap dnaPcrQualitative = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION_QUALITATIVE), cohort, context);
        CalculationResultMap dnaPcrReaction = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION), cohort, context);

        for(Integer ptId: cohort) {

            PatientEligibility patientEligibility = null;

            Date eligibleDateObs = EmrCalculationUtils.datetimeResultForPatient(eligibleDateMap, ptId);
            Obs  eligibleReasonObs = EmrCalculationUtils.obsResultForPatient(eligibleReasonMap, ptId);

            Obs hepatitisConcept = EmrCalculationUtils.obsResultForPatient(hepatitisMap, ptId);
            Obs isDiscodantCouple = EmrCalculationUtils.obsResultForPatient(hivRiskFactor, ptId);
            Obs hasTbConcpt = EmrCalculationUtils.obsResultForPatient(tbStatus, ptId);
            //finding results for pcr
            Obs dnaPcrQual = EmrCalculationUtils.obsResultForPatient(dnaPcrQualitative, ptId);
            Obs dnaPcrRea = EmrCalculationUtils.obsResultForPatient(dnaPcrReaction, ptId);
            Obs pregnancyObs = EmrCalculationUtils.obsResultForPatient(pregStatusObss, ptId);
            Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(artStartDateMap, ptId);

            if(inHivProgram.contains(ptId) && pregnancyObs != null && pregnancyObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.YES))) {
                patientEligibility = new PatientEligibility("PREGNANT", pregnancyObs.getObsDatetime());
            }

            else if(inHivProgram.contains(ptId) && hepatitisConcept != null && hepatitisConcept.getValueCoded().equals(Dictionary.getConcept(Dictionary.HEPATITIS_B))) {
                patientEligibility = new PatientEligibility("TB/HPV", hepatitisConcept.getObsDatetime());
            }

            else if(inHivProgram.contains(ptId) && inTbProgram.contains(ptId) || (hasTbConcpt != null && hasTbConcpt.getValueCoded().equals(Dictionary.getConcept(Dictionary.DISEASE_DIAGNOSED))) || (hasTbConcpt != null && hasTbConcpt.getValueCoded().equals(Dictionary.getConcept(Dictionary.ON_TREATMENT_FOR_DISEASE)))) {
                patientEligibility = new PatientEligibility("TB/HPV", hasTbConcpt.getObsDatetime());
            }

           else if(inHivProgram.contains(ptId) && isDiscodantCouple != null && isDiscodantCouple.getValueCoded().equals(Dictionary.getConcept(Dictionary.DISCORDANT_COUPLE))) {
                patientEligibility = new PatientEligibility("DISCORDANT", isDiscodantCouple.getObsDatetime());
            }

            else if(eligibleDateObs != null && eligibleReasonObs != null ) {
                patientEligibility = new PatientEligibility(eligibleReason(eligibleReasonObs.getValueCoded()), eligibleDateObs);
            }

           else  if(dnaPcrQual != null && dnaPcrQual.getValueCoded().equals(Dictionary.getConcept(Dictionary.POSITIVE))) {
                patientEligibility = new PatientEligibility("PCR", dnaPcrQual.getObsDatetime());
            }

            else if(dnaPcrRea != null && dnaPcrRea.getValueCoded().equals(Dictionary.getConcept(Dictionary.DETECTED))) {
                patientEligibility = new PatientEligibility("PCR", dnaPcrRea.getObsDatetime());
            }
            else {
                if (inHivProgram.contains(ptId)) {
                    int ageInMonths = ((Age) ages.get(ptId).getValue()).getFullMonths();
                    Date birthDate = Context.getPersonService().getPerson(ptId).getBirthdate();
                    List<Obs> whoStages = CalculationUtils.extractResultValues((ListResult) allWhoStage.get(ptId));
                    List<Obs> cd4s = CalculationUtils.extractResultValues((ListResult) allCd4.get(ptId));

                    patientEligibility = getCriteriaAndDate(ageInMonths, cd4s, whoStages, birthDate, artStartDate);
                }
            }
            ret.put(ptId, new SimpleResult(patientEligibility, this));
        }

        return ret;
    }

    String eligibleReason(Concept concept) {
        String reason = null;
        if(concept.equals(Dictionary.getConcept(Dictionary.CD4_COUNT)) || concept.equals(Dictionary.getConcept(Dictionary.CD4_PERCENT))) {
            reason = "CD4";
        }
        else if(concept.equals(Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE))) {
            reason = "WHO";
        }
        else if(concept.equals(Dictionary.getConcept(Dictionary.OPTION_B_PLUS_PROGRAM))) {
            reason = "PREGNANT";
        }
        return  reason;
    }

    private PatientEligibility getCriteriaAndDate(int ageInMonths, List<Obs> cd4, List<Obs> whoStag, Date birthDate, Date artStartDate) {

        if (ageInMonths <= 120) {//children less than 10 years
             return new PatientEligibility("AGE", birthDate);
        }

        else if (ageInMonths > 120 && ageInMonths <= 180) { //children above 10 years and not above 15 years

            if(checkIfOnArtBeforeOthers(artStartDate, cd4, whoStag) != null && whoDate(whoStag) != null && checkIfOnArtBeforeOthers(artStartDate, cd4, whoStag).before(whoDate(whoStag))) {
                return new PatientEligibility(null, artStartDate);
            }
            else if(whoDate(whoStag) != null) {

                return new PatientEligibility("WHO", whoDate(whoStag));
            }
        }

        else if (ageInMonths > 180){

            if(checkIfOnArtBeforeOthers(artStartDate, cd4, whoStag) != null && cd4Date(cd4) != null && checkIfOnArtBeforeOthers(artStartDate, cd4, whoStag).before(cd4Date(cd4))) {
                return new PatientEligibility(null, artStartDate);
            }

             else if(cd4Date(cd4) != null) {
                return new PatientEligibility("CD4", cd4Date(cd4));
            }

        }

        return  null;
    }

    Date whoDate(List<Obs> whoStage) {
        Date whoStageDate = null;

        if(whoStage.isEmpty()) {
            whoStageDate = null;
        }
        else {

            for (Obs obsWhoStage : whoStage) {

                Integer stage = EmrUtils.whoStage(obsWhoStage.getValueCoded());

                if (stage == 3 || stage == 4) {
                    whoStageDate = obsWhoStage.getObsDatetime();
                    break;

                }
            }

        }

        return whoStageDate;

    }

    Date cd4Date(List<Obs> cd4) {
        Date cd4Date = null;

        if(cd4.isEmpty()) {
            cd4Date = null;
        }
        else {

            for (Obs obsCd4 : cd4) {

                double cd4Value = obsCd4.getValueNumeric();

                if (cd4Value <= 500) {
                    cd4Date = obsCd4.getObsDatetime();
                    break;

                }

            }

        }

        return cd4Date;

    }

    Date checkIfOnArtBeforeOthers(Date artDate, List<Obs> cd4, List<Obs> whoStage) {
        Date isOnARTDate = null;

        if(whoDate(whoStage) == null && cd4Date(cd4) == null && artDate != null) {
            isOnARTDate = artDate;
        }

        if(whoDate(whoStage) != null && cd4Date(cd4) == null && artDate != null && whoDate(whoStage).after(artDate)) {
            isOnARTDate = artDate;
        }

        if(cd4Date(cd4) != null && whoDate(whoStage) == null &&  artDate != null && cd4Date(cd4).after(artDate)) {
            isOnARTDate = artDate;
        }

        if(cd4Date(cd4) != null && whoDate(whoStage) != null &&  artDate != null) {
            if(artDate.before(whoDate(whoStage)) && artDate.before(cd4Date(cd4))) {
                isOnARTDate = artDate;
            }
        }



        return isOnARTDate;
    }

}
