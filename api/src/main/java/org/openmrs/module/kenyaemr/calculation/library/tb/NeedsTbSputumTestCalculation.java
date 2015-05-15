/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
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
import org.openmrs.module.kenyaemr.TbConstants;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Arrays;
import java.util.Calendar;
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
		CalculationResultMap lastSputumResults = Calculations.lastObs(SPUTUM_FOR_ACID_FAST_BACILLI, inTbProgram, context);

		// get the date when Tb treatment was started, the patient should be in tb program to have this date
		CalculationResultMap tbStartTreatmentDate = Calculations.lastObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_DRUG_TREATMENT_START_DATE), inTbProgram, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean needsSputum = false;

			// check if a patient is alive
			if (alive.contains(ptId)) {
				// is the patient suspected of TB?
				Obs lastObsTbDiseaseResults = EmrCalculationUtils.obsResultForPatient(lastObsTbDiseaseStatus, ptId);
				Obs lastSputumResultsObs = EmrCalculationUtils.obsResultForPatient(lastSputumResults, ptId);
				if ((lastObsTbDiseaseResults != null) && (lastObsTbDiseaseResults.getValueCoded().equals(tbsuspect)) && lastSputumResultsObs == null) {
						needsSputum = true;
					if(inTbProgram.contains(ptId)) {
						needsSputum = false;
					}
				}

				//find those patients who have positive sputum results
				Obs diseaseClassification = EmrCalculationUtils.obsResultForPatient(lastDiseaseClassiffication, ptId);
				Obs tbResults = EmrCalculationUtils.obsResultForPatient(lastTbPulmonayResult, ptId);
				Date  treatmentStartDate = EmrCalculationUtils.datetimeObsResultForPatient(tbStartTreatmentDate, ptId);

				if(inTbProgram.contains(ptId) && diseaseClassification != null && tbResults != null && (diseaseClassification.getValueCoded().equals(pulmonaryTb)) && (tbResults.getValueCoded().equals(smearPositive)) && treatmentStartDate != null) {

					if(lastSputumResultsObs != null && !(lastSputumResultsObs.getValueCoded().equals(NEGATIVE))) {

						//get date after 2,4 and 6 months
						Calendar months2 = Calendar.getInstance();
						months2.setTime(treatmentStartDate);
						months2.add(Calendar.MONTH, 2);

						//find first sputum results after 2 months. If the results is null activate the alert
						CalculationResultMap resuts2Months = Calculations.firstObsOnOrAfter(SPUTUM_FOR_ACID_FAST_BACILLI, months2.getTime(), Arrays.asList(ptId), context);
						if(EmrCalculationUtils.obsResultForPatient(resuts2Months, ptId) == null ) {
							needsSputum = true;
						}

						Calendar months4 = Calendar.getInstance();
						months4.setTime(treatmentStartDate);
						months4.add(Calendar.MONTH, 4);
						//repeat after 4 months
						CalculationResultMap resuts4Months = Calculations.firstObsOnOrAfter(SPUTUM_FOR_ACID_FAST_BACILLI, months4.getTime(), Arrays.asList(ptId), context);

						if(EmrCalculationUtils.obsResultForPatient(resuts4Months, ptId) == null ) {
							needsSputum = true;
						}

						//repeat for 6 months

						Calendar months6 = Calendar.getInstance();
						months6.setTime(treatmentStartDate);
						months6.add(Calendar.MONTH, 6);

						CalculationResultMap resuts6Months = Calculations.firstObsOnOrAfter(SPUTUM_FOR_ACID_FAST_BACILLI, months6.getTime(), Arrays.asList(ptId), context);
						if(EmrCalculationUtils.obsResultForPatient(resuts6Months, ptId) == null ) {
							needsSputum = true;
						}
					}
				}

			}
			ret.put(ptId, new BooleanResult(needsSputum, this, context));
		}
		return ret;
	}
}