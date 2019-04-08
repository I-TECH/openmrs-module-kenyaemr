/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.tb;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculate whether patients are due for a sputum test. Calculation returns
 * true if the patient is alive, and screened for tb, and is a suspect,
 * those on treatment with pulmonary tb positive, a repeat is done 2, 4, and 6 months
 * sputum results recorded
 */
public class NeedsTbSputumTestCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

	/**
	 * @see org.openmrs.module.kenyacore.calculation.PatientFlagCalculation#getFlagMessage()
	 */
	@Override
	public String getFlagMessage() {
		return "Due for TB Sputum";
	}

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @should determine whether patients need sputum test
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
		// Get TB program
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);

		// Get all patients who are alive and in TB program
		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inTbProgram = Filters.inProgram(tbProgram, alive, context);

		// Get concepts
		Concept tbsuspect = Dictionary.getConcept(Dictionary.DISEASE_SUSPECTED);
		Concept pulmonaryTb = Dictionary.getConcept(Dictionary.PULMONARY_TB);
		Concept smearPositive = Dictionary.getConcept(Dictionary.POSITIVE);
		Concept NEGATIVE = Dictionary.getConcept(Dictionary.NEGATIVE);
		Concept SPUTUM_FOR_ACID_FAST_BACILLI = Dictionary.getConcept(Dictionary.SPUTUM_FOR_ACID_FAST_BACILLI);

		// check if there is any observation recorded per the tuberculosis disease status
		CalculationResultMap lastObsTbDiseaseStatus = Calculations.lastObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS), cohort, context);

		// get last observations for disease classification, patient classification
		// and pulmonary tb positive to determine when sputum will be due for patients in future
		CalculationResultMap lastDiseaseClassiffication = Calculations.lastObs(Dictionary.getConcept(Dictionary.SITE_OF_TUBERCULOSIS_DISEASE), inTbProgram, context);
		CalculationResultMap lastTbPulmonayResult = Calculations.lastObs(Dictionary.getConcept(Dictionary.RESULTS_TUBERCULOSIS_CULTURE), inTbProgram, context);

		// get the first observation ever the patient had a sputum results for month 0
		CalculationResultMap lastSputumResults = Calculations.lastObs(SPUTUM_FOR_ACID_FAST_BACILLI, cohort, context);

		// get the date when Tb treatment was started, the patient should be in tb program to have this date
		CalculationResultMap tbStartTreatmentDate = Calculations.lastObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_DRUG_TREATMENT_START_DATE), cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean needsSputum = false;
			//find those patients who have positive sputum results
			Obs diseaseClassification = EmrCalculationUtils.obsResultForPatient(lastDiseaseClassiffication, ptId);
			Obs tbResults = EmrCalculationUtils.obsResultForPatient(lastTbPulmonayResult, ptId);
			Date  treatmentStartDate = EmrCalculationUtils.datetimeObsResultForPatient(tbStartTreatmentDate, ptId);
			Obs lastObsTbDiseaseResults = EmrCalculationUtils.obsResultForPatient(lastObsTbDiseaseStatus, ptId);
			Obs lastSputumResultsObs = EmrCalculationUtils.obsResultForPatient(lastSputumResults, ptId);

			// check if a patient is alive
			if (alive.contains(ptId)) {
				if ((lastObsTbDiseaseResults != null) && (lastObsTbDiseaseResults.getValueCoded().equals(tbsuspect)) && lastSputumResultsObs == null && !(inTbProgram.contains(ptId))) {
						needsSputum = true;
				}
				else if(inTbProgram.contains(ptId) && diseaseClassification != null && tbResults != null && (diseaseClassification.getValueCoded().equals(pulmonaryTb)) && (tbResults.getValueCoded().equals(smearPositive)) && treatmentStartDate != null) {

					if(lastSputumResultsObs != null && !(lastSputumResultsObs.getValueCoded().equals(NEGATIVE))) {

						//get date after 2,4 and 6 months

						//find first sputum results after 2 months. If the results is null activate the alert
						Date months2 = DateUtil.adjustDate(treatmentStartDate, 2, DurationUnit.MONTHS);
						CalculationResultMap resuts2Months = Calculations.firstObsOnOrAfter(SPUTUM_FOR_ACID_FAST_BACILLI, months2, Arrays.asList(ptId), context);

						//repeat after 4 months
						Date months4 = DateUtil.adjustDate(treatmentStartDate, 4, DurationUnit.MONTHS);
						CalculationResultMap resuts4Months = Calculations.firstObsOnOrAfter(SPUTUM_FOR_ACID_FAST_BACILLI, months4, Arrays.asList(ptId), context);

						//repeat for months 6
						Date months6 = DateUtil.adjustDate(treatmentStartDate, 6, DurationUnit.MONTHS);
						CalculationResultMap resuts6Months = Calculations.firstObsOnOrAfter(SPUTUM_FOR_ACID_FAST_BACILLI, months6, Arrays.asList(ptId), context);

						if(EmrCalculationUtils.obsResultForPatient(resuts2Months, ptId) == null && months2.before(context.getNow())) {
							needsSputum = true;
						}

						if (EmrCalculationUtils.obsResultForPatient(resuts4Months, ptId) == null && months4.before(context.getNow())) {
							needsSputum = true;
						}

						//repeat for 6 months

						if(EmrCalculationUtils.obsResultForPatient(resuts6Months, ptId) == null && months6.before(context.getNow()) ) {
							needsSputum = true;
						}

						//if any of the prior sputum results are given ignore the preceding ones
						if(EmrCalculationUtils.obsResultForPatient(resuts6Months, ptId) != null && (EmrCalculationUtils.obsResultForPatient(resuts4Months, ptId) == null || EmrCalculationUtils.obsResultForPatient(resuts2Months, ptId) == null)) {
							needsSputum = false;
						}

						if(EmrCalculationUtils.obsResultForPatient(resuts4Months, ptId) != null && EmrCalculationUtils.obsResultForPatient(resuts2Months, ptId) == null) {
							needsSputum = false;
						}
					}
				}

			}
			ret.put(ptId, new BooleanResult(needsSputum, this, context));
		}
		return ret;
	}
}