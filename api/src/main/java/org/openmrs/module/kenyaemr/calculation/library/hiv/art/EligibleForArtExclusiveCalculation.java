package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastCd4CountCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastCd4PercentageCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastWhoStageCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.Age;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 12/01/15.
 */
public class EligibleForArtExclusiveCalculation extends AbstractPatientCalculation {
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @should calculate eligibility
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
										 PatientCalculationContext context) {

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

		Set<Integer> inHivProgram = Filters.inProgram(hivProgram, cohort, context);

		CalculationResultMap ages = Calculations.ages(cohort, context);

		CalculationResultMap lastWhoStage = calculate(new LastWhoStageCalculation(), cohort, context);
		CalculationResultMap lastCd4 = calculate(new LastCd4CountCalculation(), cohort, context);
		CalculationResultMap lastCd4Percent = calculate(new LastCd4PercentageCalculation(), cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean eligible = false;
			if (inHivProgram.contains(ptId)) {
				int ageInMonths = ((Age) ages.get(ptId).getValue()).getFullMonths();
				Double cd4 = EmrCalculationUtils.numericObsResultForPatient(lastCd4, ptId);
				Double cd4Percent = EmrCalculationUtils.numericObsResultForPatient(lastCd4Percent, ptId);
				Integer whoStage = EmrUtils.whoStage(EmrCalculationUtils.codedObsResultForPatient(lastWhoStage, ptId));
				eligible = isEligible(ageInMonths, cd4, cd4Percent, whoStage);
			}
			ret.put(ptId, new BooleanResult(eligible, this));
		}
		return ret;
	}

	/**
	 * Checks eligibility based on age, CD4 and WHO stage
	 * @param ageInMonths the patient age in months
	 * @param cd4 the last CD4 count
	 * @param cd4Percent the last CD4 percentage
	 * @param whoStage the last WHO stage
	 * @return true if patient is eligible
	 */
	protected boolean isEligible(int ageInMonths, Double cd4, Double cd4Percent, Integer whoStage) {
		if (ageInMonths < 24) {
			return true;
		}
		else if (ageInMonths < 60) { // 24-59 months
			if (whoStage != null && (whoStage == 3 || whoStage == 4)) {
				return true;
			}
			if (cd4Percent != null && cd4Percent < 25) {
				return true;
			}
			if (cd4 != null && cd4 < 1000) {
				return true;
			}
		}
		else if (ageInMonths < 155) { // 5-12 years
			if (whoStage != null && (whoStage == 3 || whoStage == 4)) {
				return true;
			}
			if (cd4Percent != null && cd4Percent < 20) {
				return true;
			}
			if (cd4 != null && cd4 < 500) {
				return true;
			}
		}
		else { // 13+ years
			if (whoStage != null && (whoStage == 3 || whoStage == 4)) {
				return true;
			}
			if (cd4 != null && cd4 < 350) {
				return true;
			}
		}
		return false;
	}
}
