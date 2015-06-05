package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.IsPregnantCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastCd4CountCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastCd4PercentageCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastWhoStageCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
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
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);

		Set<Integer> inHivProgram = Filters.inProgram(hivProgram, cohort, context);

		CalculationResultMap ages = Calculations.ages(cohort, context);

		CalculationResultMap lastWhoStage = calculate(new LastWhoStageCalculation(), cohort, context);
		CalculationResultMap lastCd4 = calculate(new LastCd4CountCalculation(), cohort, context);
		CalculationResultMap lastCd4Percent = calculate(new LastCd4PercentageCalculation(), cohort, context);

		//find hepatits status
		CalculationResultMap hepatitisMap = Calculations.lastObs(Dictionary.getConcept(Dictionary.PROBLEM_ADDED), cohort, context);
		//in tb program
		Set<Integer> inTbProgram = Filters.inProgram(tbProgram, cohort, context);
		//find those who have status for tb
		CalculationResultMap tbStatus = Calculations.lastObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS), cohort, context);
		//find pregnant women
		Set<Integer> pregnantWomen = CalculationUtils.patientsThatPass(calculate(new IsPregnantCalculation(), cohort, context));
		//finding those at risk for hiv
		CalculationResultMap hivRiskFactor = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_RISK_FACTOR), cohort, context);
		//DNA PCR
		CalculationResultMap dnaPcrQualitative = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION_QUALITATIVE), cohort, context);
		CalculationResultMap dnaPcrReaction = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION), cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean eligible = false;
			boolean hasHepatitis = false;
			boolean hasTb = false;
			boolean isPregnant = false;
			boolean isDiscordant = false;

			Concept hepatitisConcept = EmrCalculationUtils.codedObsResultForPatient(hepatitisMap, ptId);
			if(hepatitisConcept != null && hepatitisConcept.equals(Dictionary.getConcept(Dictionary.HEPATITIS_B))) {
				hasHepatitis = true;
			}
			Concept hasTbConcpt = EmrCalculationUtils.codedObsResultForPatient(tbStatus, ptId);
			if(inTbProgram.contains(ptId) || (hasTbConcpt != null && hasTbConcpt.equals(Dictionary.getConcept(Dictionary.DISEASE_DIAGNOSED))) || (hasTbConcpt != null && hasTbConcpt.equals(Dictionary.getConcept(Dictionary.ON_TREATMENT_FOR_DISEASE)))) {
				hasTb = true;
			}
			if(pregnantWomen.contains(ptId)) {
				isPregnant = true;
			}
			Concept isDiscodantCouple = EmrCalculationUtils.codedObsResultForPatient(hivRiskFactor, ptId);
			if(isDiscodantCouple != null && isDiscodantCouple.equals(Dictionary.getConcept(Dictionary.DISCORDANT_COUPLE))) {
				isDiscordant = true;
			}
			//finding results for pcr
			Concept dnaPcrQual = EmrCalculationUtils.codedObsResultForPatient(dnaPcrQualitative, ptId);
			Concept dnaPcrRea = EmrCalculationUtils.codedObsResultForPatient(dnaPcrReaction, ptId);

			if(dnaPcrQual != null && dnaPcrQual.equals(Dictionary.getConcept(Dictionary.POSITIVE))) {
				eligible = true;
			}

			if(dnaPcrRea != null && dnaPcrRea.equals(Dictionary.getConcept(Dictionary.DETECTED))) {
				eligible = true;
			}
			if (inHivProgram.contains(ptId)) {
				int ageInMonths = ((Age) ages.get(ptId).getValue()).getFullMonths();
				Double cd4 = EmrCalculationUtils.numericObsResultForPatient(lastCd4, ptId);
				Double cd4Percent = EmrCalculationUtils.numericObsResultForPatient(lastCd4Percent, ptId);
				Integer whoStage = EmrUtils.whoStage(EmrCalculationUtils.codedObsResultForPatient(lastWhoStage, ptId));
				eligible = isEligible(ageInMonths, cd4, cd4Percent, whoStage, hasHepatitis, hasTb, isPregnant, isDiscordant);
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
	protected boolean isEligible(int ageInMonths, Double cd4, Double cd4Percent, Integer whoStage, Boolean hasHepatitis, Boolean hasTb, Boolean isPregnant, Boolean isaSeroDiscordant) {
		if (ageInMonths <= 120) {//children less than 10 years
			return true;
		}
		else if (ageInMonths > 120 && ageInMonths <= 180) { //children above 10 years and not above 15 years
			if (whoStage != null && (whoStage == 3 || whoStage == 4)) {
				return true;
			}
			if (hasHepatitis || hasTb) {
				return true;
			}

		}

		else { // 15+ years
			if (cd4 != null && cd4 < 500) {
				return true;
			}
			if(isPregnant || isaSeroDiscordant) {
				return true;
			}
		}
		return false;
	}
}
