package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Obs;
import org.openmrs.Program;
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
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.model.PatientEligibility;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.Age;

import java.util.*;

/**
 * Calculates the date when a patient was medically eligible and the reasons
 */
public class DateMedicallyEligibleForARTCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        CalculationResultMap ret = new CalculationResultMap();
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, cohort, context);

        CalculationResultMap ages = Calculations.ages(cohort, context);

        CalculationResultMap allWhoStage = Calculations.allObs(Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE), cohort, context);
        CalculationResultMap allCd4 = Calculations.allObs(Dictionary.getConcept(Dictionary.CD4_COUNT), cohort, context);
        CalculationResultMap allCd4Percent = Calculations.allObs(Dictionary.getConcept(Dictionary.CD4_PERCENT), cohort, context);
        CalculationResultMap artStartDateMap = calculate(new InitialArtStartDateCalculation(), cohort, context);

        for (Integer ptId : cohort) {
            PatientEligibility eligibilityInfo = null;
            if (inHivProgram.contains(ptId)) {
                int ageInMonths = ((Age) ages.get(ptId).getValue()).getFullMonths();
                Date birthDate = Context.getPersonService().getPerson(ptId).getBirthdate();
                List<Obs> whoStages = CalculationUtils.extractResultValues((ListResult) allWhoStage.get(ptId));
                List<Obs> cd4s = CalculationUtils.extractResultValues((ListResult) allCd4.get(ptId));
                List<Obs> cd4Percents = CalculationUtils.extractResultValues((ListResult) allCd4Percent.get(ptId));
                Date arvStartDate = EmrCalculationUtils.datetimeResultForPatient(artStartDateMap, ptId);

                eligibilityInfo = getCriteriaAndDate(ageInMonths, cd4s, cd4Percents, whoStages, birthDate, arvStartDate);
            }
            ret.put(ptId, new SimpleResult(eligibilityInfo, this));
        }
        return ret;
    }

	/**
	 * Checks eligibility based on age, CD4 and WHO stage
	 * @param ageInMonths
	 * @param cd4
	 * @param cd4Percent
	 * @param whoStage
	 * @param birthDate
	 * @param arvStartDate
	 * @return PatientEligibility information
	 */
    private PatientEligibility getCriteriaAndDate(int ageInMonths, List<Obs> cd4, List<Obs> cd4Percent, List<Obs> whoStage, Date birthDate, Date arvStartDate) {

        if (ageInMonths < 24) {
			return new PatientEligibility("age", birthDate, null);
        }
        else if (ageInMonths < 60) { // 24-59 months

				EligibilityDateReason dateReason = dateEligible(cd4, cd4Percent, whoStage, 25, 1000, arvStartDate);

                Date whoDate = whoDate(whoStage);

                if(cd4.isEmpty() && cd4Percent.isEmpty() && whoDate != null && arvStartDate != null && (whoDate.before(arvStartDate) || whoDate.equals(arvStartDate))) {
                    return new PatientEligibility("who", whoDate, null);
                }

                if(cd4.isEmpty() && cd4Percent.isEmpty() && whoDate != null && arvStartDate != null && arvStartDate.before(whoDate)) {
                    return new PatientEligibility("", arvStartDate, null);
                }

                if(cd4.isEmpty() && cd4Percent.isEmpty() && whoDate == null && arvStartDate != null) {
                    return new PatientEligibility("", arvStartDate, null);
                }

                if(cd4.isEmpty() && cd4Percent.isEmpty() && whoDate != null && arvStartDate == null) {
                    return new PatientEligibility("who", whoDate, null);
                }
            if(!(cd4.isEmpty()) && cd4Percent.isEmpty()) {
                Date dateVal = null;
                Double val = null;
                Date whoDateIn = whoDate(whoStage);
                for(Obs obs: cd4) {
                    if(obs.getValueNumeric() < 1000) {
                        dateVal = obs.getObsDatetime();
                        val = obs.getValueNumeric();
                        break;
                    }
                }

                if(dateVal != null && whoDateIn == null && arvStartDate == null) {
                    return new PatientEligibility("cd4", dateVal, val );
                }

                if(dateVal == null && whoDateIn != null && arvStartDate == null) {
                    return new PatientEligibility("who", whoDateIn, null );
                }

                if(dateVal == null && whoDateIn == null && arvStartDate != null) {
                    return new PatientEligibility("", arvStartDate, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate == null && dateVal.before(whoDateIn)) {
                    return new PatientEligibility("cd4", dateVal, val );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate == null && whoDateIn.before(dateVal)) {
                    return new PatientEligibility("who", whoDateIn, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate == null && whoDateIn.equals(dateVal)) {
                    return new PatientEligibility("who", whoDateIn, null );
                }

                if(dateVal != null && whoDateIn == null && arvStartDate != null && dateVal.before(arvStartDate)){
                    return new PatientEligibility("cd4", dateVal, val );
                }

                if(dateVal != null && whoDateIn == null && arvStartDate != null && dateVal.equals(arvStartDate)){
                    return new PatientEligibility("cd4", dateVal, val );
                }

                if(dateVal != null && whoDateIn == null && arvStartDate != null && arvStartDate.before(whoDateIn)){
                    return new PatientEligibility("", arvStartDate, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate != null && dateVal.before(whoDateIn) && dateVal.before(arvStartDate)) {
                    return new PatientEligibility("cd4", dateVal, val );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate != null && whoDateIn.before(dateVal) && whoDateIn.before(arvStartDate)) {
                    return new PatientEligibility("who", whoDateIn, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate != null && arvStartDate.before(dateVal) && arvStartDate.before(whoDateIn)) {
                    return new PatientEligibility("", arvStartDate, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate != null && dateVal.equals(whoDateIn) && dateVal.equals(arvStartDate)){
                    return new PatientEligibility("who", whoDateIn, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate != null && dateVal.equals(whoDateIn) && dateVal.before(arvStartDate)){
                    return new PatientEligibility("who", whoDateIn, null );
                }


            }
            if(cd4.isEmpty() && !cd4Percent.isEmpty()) {

                Date dateVal = null;
                Double val = null;
                Date whoDateIn = whoDate(whoStage);
                for(Obs obs: cd4Percent) {
                    if(obs.getValueNumeric() < 25) {
                        dateVal = obs.getObsDatetime();
                        val = obs.getValueNumeric();
                        break;
                    }
                }

                if(dateVal != null && whoDateIn == null && arvStartDate == null) {
                    return new PatientEligibility("cd4", dateVal, val );
                }

                if(dateVal == null && whoDateIn != null && arvStartDate == null) {
                    return new PatientEligibility("who", whoDateIn, null );
                }

                if(dateVal == null && whoDateIn == null && arvStartDate != null) {
                    return new PatientEligibility("", arvStartDate, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate == null && dateVal.before(whoDateIn)) {
                    return new PatientEligibility("cd4", dateVal, val );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate == null && whoDateIn.before(dateVal)) {
                    return new PatientEligibility("who", whoDateIn, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate == null && whoDateIn.equals(dateVal)) {
                    return new PatientEligibility("who", whoDateIn, null );
                }

                if(dateVal != null && whoDateIn == null && arvStartDate != null && dateVal.before(arvStartDate)){
                    return new PatientEligibility("cd4", dateVal, val );
                }

                if(dateVal != null && whoDateIn == null && arvStartDate != null && dateVal.equals(arvStartDate)){
                    return new PatientEligibility("cd4", dateVal, val );
                }

                if(dateVal != null && whoDateIn == null && arvStartDate != null && arvStartDate.before(whoDateIn)){
                    return new PatientEligibility("", arvStartDate, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate != null && dateVal.before(whoDateIn) && dateVal.before(arvStartDate)) {
                    return new PatientEligibility("cd4", dateVal, val );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate != null && whoDateIn.before(dateVal) && whoDateIn.before(arvStartDate)) {
                    return new PatientEligibility("who", whoDateIn, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate != null && arvStartDate.before(dateVal) && arvStartDate.before(whoDateIn)) {
                    return new PatientEligibility("", arvStartDate, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate != null && dateVal.equals(whoDateIn) && dateVal.equals(arvStartDate)){
                    return new PatientEligibility("who", whoDateIn, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate != null && dateVal.equals(whoDateIn) && dateVal.before(arvStartDate)){
                    return new PatientEligibility("who", whoDateIn, null );
                }

            }

				if (dateReason.getDateEligible() != null) {
					return new PatientEligibility(dateReason.getReason(), dateReason.getDateEligible(), dateReason.getCd4());
				}

        }
        else if (ageInMonths < 155) {

				EligibilityDateReason dateReason = dateEligible(cd4, cd4Percent, whoStage, 20, 500, arvStartDate);

                Date whoDate = whoDate(whoStage);

                if(cd4.isEmpty() && cd4Percent.isEmpty() && whoDate != null && arvStartDate != null && (whoDate.before(arvStartDate) || whoDate.equals(arvStartDate))) {
                    return new PatientEligibility("who", whoDate, null);
                }

                if(cd4.isEmpty() && cd4Percent.isEmpty() && whoDate != null && arvStartDate != null && arvStartDate.before(whoDate)) {
                    return new PatientEligibility("", arvStartDate, null);
                }

                if(cd4.isEmpty() && cd4Percent.isEmpty() && whoDate == null && arvStartDate != null) {
                    return new PatientEligibility("", arvStartDate, null);
                }

                if(cd4.isEmpty() && cd4Percent.isEmpty() && whoDate != null && arvStartDate == null) {
                    return new PatientEligibility("who", whoDate, null);
                }
            if(!(cd4.isEmpty()) && cd4Percent.isEmpty()) {
                Date dateVal = null;
                Double val = null;
                Date whoDateIn = whoDate(whoStage);
                for(Obs obs: cd4) {
                    if(obs.getValueNumeric() < 500) {
                        dateVal = obs.getObsDatetime();
                        val = obs.getValueNumeric();
                        break;
                    }
                }

                if(dateVal != null && whoDateIn == null && arvStartDate == null) {
                    return new PatientEligibility("cd4", dateVal, val );
                }

                if(dateVal == null && whoDateIn != null && arvStartDate == null) {
                    return new PatientEligibility("who", whoDateIn, null );
                }

                if(dateVal == null && whoDateIn == null && arvStartDate != null) {
                    return new PatientEligibility("", arvStartDate, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate == null && dateVal.before(whoDateIn)) {
                    return new PatientEligibility("cd4", dateVal, val );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate == null && whoDateIn.before(dateVal)) {
                    return new PatientEligibility("who", whoDateIn, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate == null && whoDateIn.equals(dateVal)) {
                    return new PatientEligibility("who", whoDateIn, null );
                }

                if(dateVal != null && whoDateIn == null && arvStartDate != null && dateVal.before(arvStartDate)){
                    return new PatientEligibility("cd4", dateVal, val );
                }

                if(dateVal != null && whoDateIn == null && arvStartDate != null && dateVal.equals(arvStartDate)){
                    return new PatientEligibility("cd4", dateVal, val );
                }

                if(dateVal != null && whoDateIn == null && arvStartDate != null && arvStartDate.before(dateVal)){
                    return new PatientEligibility("", arvStartDate, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate != null && dateVal.before(whoDateIn) && dateVal.before(arvStartDate)) {
                    return new PatientEligibility("cd4", dateVal, val );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate != null && whoDateIn.before(dateVal) && whoDateIn.before(arvStartDate)) {
                    return new PatientEligibility("who", whoDateIn, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate != null && arvStartDate.before(dateVal) && arvStartDate.before(whoDateIn)) {
                    return new PatientEligibility("", arvStartDate, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate != null && dateVal.equals(whoDateIn) && dateVal.equals(arvStartDate)){
                    return new PatientEligibility("who", whoDateIn, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate != null && dateVal.equals(whoDateIn) && dateVal.before(arvStartDate)){
                    return new PatientEligibility("who", whoDateIn, null );
                }


            }
            if(cd4.isEmpty() && !cd4Percent.isEmpty()) {

                Date dateVal = null;
                Double val = null;
                Date whoDateIn = whoDate(whoStage);
                for(Obs obs: cd4Percent) {
                    if(obs.getValueNumeric() < 20) {
                        dateVal = obs.getObsDatetime();
                        val = obs.getValueNumeric();
                        break;
                    }
                }

                if(dateVal != null && whoDateIn == null && arvStartDate == null) {
                    return new PatientEligibility("cd4", dateVal, val );
                }

                if(dateVal == null && whoDateIn != null && arvStartDate == null) {
                    return new PatientEligibility("who", whoDateIn, null );
                }

                if(dateVal == null && whoDateIn == null && arvStartDate != null) {
                    return new PatientEligibility("", arvStartDate, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate == null && dateVal.before(whoDateIn)) {
                    return new PatientEligibility("cd4", dateVal, val );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate == null && whoDateIn.before(dateVal)) {
                    return new PatientEligibility("who", whoDateIn, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate == null && whoDateIn.equals(dateVal)) {
                    return new PatientEligibility("who", whoDateIn, null );
                }

                if(dateVal != null && whoDateIn == null && arvStartDate != null && dateVal.before(arvStartDate)){
                    return new PatientEligibility("cd4", dateVal, val );
                }

                if(dateVal != null && whoDateIn == null && arvStartDate != null && dateVal.equals(arvStartDate)){
                    return new PatientEligibility("cd4", dateVal, val );
                }

                if(dateVal != null && whoDateIn == null && arvStartDate != null && arvStartDate.before(dateVal)){
                    return new PatientEligibility("", arvStartDate, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate != null && dateVal.before(whoDateIn) && dateVal.before(arvStartDate)) {
                    return new PatientEligibility("cd4", dateVal, val );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate != null && whoDateIn.before(dateVal) && whoDateIn.before(arvStartDate)) {
                    return new PatientEligibility("who", whoDateIn, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate != null && arvStartDate.before(dateVal) && arvStartDate.before(whoDateIn)) {
                    return new PatientEligibility("", arvStartDate, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate != null && dateVal.equals(whoDateIn) && dateVal.equals(arvStartDate)){
                    return new PatientEligibility("who", whoDateIn, null );
                }

                if(dateVal != null && whoDateIn != null && arvStartDate != null && dateVal.equals(whoDateIn) && dateVal.before(arvStartDate)){
                    return new PatientEligibility("who", whoDateIn, null );
                }

            }



            if (dateReason.getDateEligible() !=null) {
                return new PatientEligibility(dateReason.getReason(), dateReason.getDateEligible(), dateReason.getCd4());
            }
        }
        else {

				EligibilityDateReason dateReason = dateEligible(cd4, cd4Percent, whoStage, 0, 350, arvStartDate);

                Date whoDate = whoDate(whoStage);

                if(cd4.isEmpty() && cd4Percent.isEmpty() && whoDate != null && arvStartDate != null && (whoDate.before(arvStartDate) || whoDate.equals(arvStartDate))) {
                    return new PatientEligibility("who", whoDate, null);
                }

                if(cd4.isEmpty() && cd4Percent.isEmpty() && whoDate != null && arvStartDate != null && whoDate.before(arvStartDate)) {
                    return new PatientEligibility("who", whoDate, null);
                }

                if(cd4.isEmpty() && cd4Percent.isEmpty() && whoDate != null && arvStartDate != null && whoDate.before(arvStartDate) && whoDate.equals(arvStartDate)) {
                    return new PatientEligibility("who", whoDate, null);
                }

                if(cd4.isEmpty() && cd4Percent.isEmpty() && whoDate != null && arvStartDate != null && arvStartDate.before(whoDate)) {
                    return new PatientEligibility("", arvStartDate, null);
                }

                if(cd4.isEmpty() && cd4Percent.isEmpty() && whoDate == null && arvStartDate != null) {
                    return new PatientEligibility("", arvStartDate, null);
                }

                if(cd4.isEmpty() && cd4Percent.isEmpty() && whoDate != null && arvStartDate == null) {
                    return new PatientEligibility("who", whoDate, null);
                }

                if(!(cd4.isEmpty()) && cd4Percent.isEmpty()) {
                    Date dateVal = null;
                    Double val = null;
                    Date whoDateIn = whoDate(whoStage);
                    for(Obs obs: cd4) {
                        if(obs.getValueNumeric() < 350) {
                            dateVal = obs.getObsDatetime();
                            val = obs.getValueNumeric();
                            break;
                        }
                    }

                    if(dateVal != null && whoDateIn == null && arvStartDate == null) {
                       return new PatientEligibility("cd4", dateVal, val );
                    }

                    if(dateVal == null && whoDateIn != null && arvStartDate == null) {
                        return new PatientEligibility("who", whoDateIn, null );
                    }

                    if(dateVal == null && whoDateIn == null && arvStartDate != null) {
                        return new PatientEligibility("", arvStartDate, null );
                    }

                    if(dateVal != null && whoDateIn != null && arvStartDate == null && dateVal.before(whoDateIn)) {
                        return new PatientEligibility("cd4", dateVal, val );
                    }

                    if(dateVal != null && whoDateIn != null && arvStartDate == null && whoDateIn.before(dateVal)) {
                        return new PatientEligibility("who", whoDateIn, null );
                    }

                    if(dateVal != null && whoDateIn != null && arvStartDate == null && whoDateIn.equals(dateVal)) {
                        return new PatientEligibility("who", whoDateIn, null );
                    }

                    if(dateVal != null && whoDateIn == null && arvStartDate != null && dateVal.before(arvStartDate)){
                        return new PatientEligibility("cd4", dateVal, val );
                    }

                    if(dateVal != null && whoDateIn == null && arvStartDate != null && dateVal.equals(arvStartDate)){
                        return new PatientEligibility("cd4", dateVal, val );
                    }

                    if(dateVal != null && whoDateIn == null && arvStartDate != null && arvStartDate.before(dateVal)){
                        return new PatientEligibility("", arvStartDate, null );
                    }

                    if(dateVal != null && whoDateIn != null && arvStartDate != null && dateVal.before(whoDateIn) && dateVal.before(arvStartDate)) {
                        return new PatientEligibility("cd4", dateVal, val );
                    }

                    if(dateVal != null && whoDateIn != null && arvStartDate != null && whoDateIn.before(dateVal) && whoDateIn.before(arvStartDate)) {
                        return new PatientEligibility("who", whoDateIn, null );
                    }

                    if(dateVal != null && whoDateIn != null && arvStartDate != null && arvStartDate.before(dateVal) && arvStartDate.before(whoDateIn)) {
                        return new PatientEligibility("", arvStartDate, null );
                    }

                    if(dateVal != null && whoDateIn != null && arvStartDate != null && dateVal.equals(whoDateIn) && dateVal.equals(arvStartDate)){
                        return new PatientEligibility("who", whoDateIn, null );
                    }

                    if(dateVal != null && whoDateIn != null && arvStartDate != null && dateVal.equals(whoDateIn) && dateVal.before(arvStartDate)){
                        return new PatientEligibility("who", whoDateIn, null );
                    }

                }

                if (dateReason.getDateEligible() !=null) {
                    return new PatientEligibility(dateReason.getReason(), dateReason.getDateEligible(), dateReason.getCd4());
                }

        }
        return null;
    }

	private EligibilityDateReason dateEligible(List<Obs> cd4, List<Obs> cd4Percent, List<Obs> whoStage, int cd4PercentThreshold, int cd4CountThreshold, Date arvStartDate) {

		String reason = null;
		Date dateEligible = null;
		Double cd4Value = null;
        Map<Date, Double> cd4ValuesAndDate = compareCD4CountAndPercent(cd4, cd4Percent, cd4PercentThreshold, cd4CountThreshold);
        Date whoDate = whoDate(whoStage);
        Date initialCd4DateValue = null;
        Double initialCd4Value = null;

        for(Map.Entry<Date, Double> cd4Values : cd4ValuesAndDate.entrySet()) {
            initialCd4DateValue = cd4Values.getKey();
            initialCd4Value = cd4Values.getValue();
        }

        if (whoDate == null  && initialCd4DateValue == null && arvStartDate != null){
                dateEligible = arvStartDate;
                reason = "";
                cd4Value = null;
		}

        if (whoDate != null && initialCd4DateValue == null &&  arvStartDate == null){
            dateEligible = whoDate;
            reason = "who";
            cd4Value = null;
        }

        if (whoDate == null && initialCd4DateValue != null  && arvStartDate == null){

            dateEligible = initialCd4DateValue;
            reason = "cd4";
            cd4Value = initialCd4Value;
        }

        if (whoDate == null && initialCd4DateValue != null && arvStartDate != null && (initialCd4DateValue.before(arvStartDate) || initialCd4DateValue.equals(arvStartDate))) {


            dateEligible = initialCd4DateValue;
            reason = "cd4";
            cd4Value = initialCd4Value;
        }

        if(whoDate == null && initialCd4DateValue != null && arvStartDate != null && arvStartDate.before(initialCd4DateValue)) {
            dateEligible = arvStartDate;
            reason = "";
            cd4Value = null;
        }


        if (whoDate != null && initialCd4DateValue == null && arvStartDate != null && (whoDate.before(arvStartDate) || whoDate.equals(arvStartDate))) {

            dateEligible = whoDate;
            reason = "who";
            cd4Value = null;
        }

        if (whoDate != null && initialCd4DateValue == null && arvStartDate != null && arvStartDate.before(whoDate)){
            dateEligible = arvStartDate;
            reason = "";
            cd4Value = null;
        }

        if (whoDate != null && initialCd4DateValue != null && arvStartDate == null && initialCd4DateValue.before(whoDate)){

                dateEligible = initialCd4DateValue;
                reason = "cd4";
                cd4Value = initialCd4Value;
            }

        if(whoDate != null && initialCd4DateValue != null && arvStartDate == null && (whoDate.before(initialCd4DateValue) || whoDate.equals(initialCd4DateValue))) {
            dateEligible = whoDate;
            reason = "who";
            cd4Value = null;
        }


        if (whoDate != null && initialCd4DateValue != null && arvStartDate != null && (whoDate.before(initialCd4DateValue) || whoDate.equals(initialCd4DateValue)) && (whoDate.before(arvStartDate) || whoDate.equals(arvStartDate))){
            dateEligible = whoDate;
            reason = "who";
            cd4Value = null;
        }

        if (whoDate != null && initialCd4DateValue != null && arvStartDate != null && initialCd4DateValue.before(whoDate) && (initialCd4DateValue.before(arvStartDate) || initialCd4DateValue.equals(arvStartDate))){
            dateEligible = initialCd4DateValue;
            reason = "cd4";
            cd4Value = initialCd4Value;
        }

        if (whoDate != null && initialCd4DateValue != null && arvStartDate != null && (arvStartDate.before(whoDate) || arvStartDate.before(initialCd4DateValue))){
            dateEligible = arvStartDate;
            reason = "";
            cd4Value = null;
        }

        if (whoDate != null && initialCd4DateValue != null && arvStartDate != null && arvStartDate.before(whoDate) && arvStartDate.before(initialCd4DateValue)){
            dateEligible = arvStartDate;
            reason = "";
            cd4Value = null;
        }

        if (whoDate != null && initialCd4DateValue != null && arvStartDate != null && arvStartDate.before(whoDate) && arvStartDate.before(initialCd4DateValue)){
            dateEligible = arvStartDate;
            reason = "";
            cd4Value = null;
        }

        if (whoDate != null && initialCd4DateValue != null && arvStartDate != null && whoDate.equals(initialCd4DateValue) && whoDate.equals(arvStartDate)){
            dateEligible = whoDate;
            reason = "who";
            cd4Value = null;
        }

        if (whoDate != null && initialCd4DateValue != null && arvStartDate != null && (whoDate.equals(initialCd4DateValue) || whoDate.equals(arvStartDate))){
            dateEligible = whoDate;
            reason = "who";
            cd4Value = null;

        }

		return new EligibilityDateReason(reason,dateEligible, cd4Value);
	}

    private Date whoDate(List<Obs> whoStage) {
        Date whoStageDate = null;
        if(whoStage.isEmpty()) {
            whoStageDate = null;
        }
        if(!whoStage.isEmpty()) {

            for (Obs obsWhoStage : whoStage) {

                if ((obsWhoStage.getValueCoded().equals(Dictionary.getConcept(Dictionary.WHO_STAGE_3_ADULT))) || (obsWhoStage.getValueCoded().equals(Dictionary.getConcept(Dictionary.WHO_STAGE_4_ADULT)))) {
                    whoStageDate = obsWhoStage.getObsDatetime();
                        break;

                }
                else {
                    whoStageDate = null;
                }

                if ((obsWhoStage.getValueCoded().equals(Dictionary.getConcept(Dictionary.WHO_STAGE_3_PEDS))) || (obsWhoStage.getValueCoded().equals(Dictionary.getConcept(Dictionary.WHO_STAGE_4_PEDS)))) {
                    whoStageDate = obsWhoStage.getObsDatetime();
                        break;
                }
                else {
                    whoStageDate = null;
                }

            }

        }

        return whoStageDate;

    }
	private Map<Date, Double> compareCD4CountAndPercent (List<Obs> cd4, List<Obs> cd4Percent, int cd4PercentThreshold, int cd4CountThreshold) {
		Date eligibilityDate;
		Date cd4PercentDate = null;
		Date cd4CountDate = null;

        Map<Date, Double> cd4DateAndValue = new HashMap<Date, Double>();;

		if (!(cd4.isEmpty()) && cd4Percent.isEmpty()) {
			for(Obs obsCount : cd4) {
				if (obsCount.getValueNumeric() < cd4CountThreshold) {

                    cd4DateAndValue.put(obsCount.getObsDatetime(), obsCount.getValueNumeric());
					break;
				}
                else {
                    cd4DateAndValue.put(null, null);
                }
			}
		}

		if (cd4.isEmpty() && !(cd4Percent.isEmpty())) {
            for (Obs obsPercent : cd4Percent) {
                if (obsPercent.getValueNumeric() < cd4PercentThreshold) {
                    cd4DateAndValue.put(obsPercent.getObsDatetime(), obsPercent.getValueNumeric());
                    break;
                } else {
                    cd4DateAndValue.put(null, null);
                }
            }
        }

		if (!cd4.isEmpty() && !cd4Percent.isEmpty()) {
            Double cdCount4Value = null;
            for (Obs obsPercent : cd4Percent) {
                if (obsPercent.getValueNumeric() < cd4PercentThreshold) {
                    cd4PercentDate = obsPercent.getObsDatetime();
                    cdCount4Value = obsPercent.getValueNumeric();
                    break;
                }
            }

            for (Obs obsCount : cd4) {
                if (obsCount.getValueNumeric() < cd4CountThreshold) {
                    cd4CountDate = obsCount.getObsDatetime();
                    cdCount4Value = obsCount.getValueNumeric();
                    break;
                }
            }


            if (cd4CountDate != null && cd4PercentDate == null) {
                eligibilityDate = cd4CountDate;
            } else if (cd4PercentDate != null && cd4CountDate == null) {
                eligibilityDate = cd4PercentDate;
            } else if (cd4CountDate != null && cd4PercentDate != null) {
                eligibilityDate = cd4PercentDate.before(cd4CountDate) ? cd4PercentDate : cd4CountDate;
            } else if (cd4CountDate != null && cd4PercentDate != null && cd4CountDate.equals(cd4PercentDate)) {
                eligibilityDate = cd4CountDate;
            } else {
                eligibilityDate = null;
                cdCount4Value = null;
            }

                cd4DateAndValue.put(eligibilityDate, cdCount4Value);

        }

		return cd4DateAndValue;
	}

	class EligibilityDateReason {
		private String reason;
		private Date dateEligible;
        private Double cd4;


        public EligibilityDateReason(String reason, Date dateEligible, Double cd4) {
            this.reason = reason;
            this.dateEligible = dateEligible;
            this.cd4 = cd4;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public Date getDateEligible() {
            return dateEligible;
        }

        public void setDateEligible(Date dateEligible) {
            this.dateEligible = dateEligible;
        }

        public Double getCd4() {
            return cd4;
        }

        public void setCd4(Double cd4) {
            this.cd4 = cd4;
        }
    }


}