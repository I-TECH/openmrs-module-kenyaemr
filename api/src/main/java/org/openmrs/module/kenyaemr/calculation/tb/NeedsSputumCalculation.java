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
package org.openmrs.module.kenyaemr.calculation.tb;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ObsResult;
import org.openmrs.module.kenyaemr.KenyaEmrConstants;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.calculation.BaseAlertCalculation;
import org.openmrs.module.kenyaemr.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.CalculationUtils;

/**
 * Calculate whether patients are due for a sputum test. Calculation returns
 * true if the patient is alive, and screened for tb, and has cough of any
 * duration probably 2 weeks during the 2 weeks then there should have been no
 * sputum results recorded
 */
public class NeedsSputumCalculation extends BaseAlertCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 *      java.util.Map,
	 *      org.openmrs.calculation.patient.PatientCalculationContext)
	 * @should determine whether patients need sputum test
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort,
			Map<String, Object> parameterValues,
			PatientCalculationContext context) {
		// get the Tb progarm patients
		Program tbProgram = Context.getProgramWorkflowService()
				.getProgramByUuid(MetadataConstants.TB_PROGRAM_UUID);

		// get set of patients who are alive
		Set<Integer> alive = alivePatients(cohort, context);
		Set<Integer> inTbProgram = CalculationUtils
				.patientsThatPass(lastProgramEnrollment(tbProgram, alive,
						context));

		// get concept for disease suspect
		Concept tbsuspect = getConcept(MetadataConstants.DISEASE_SUSPECTED_CONCEPT_UUID);
		// get concept for pulmonary tb
		Concept pulmonaryTb = getConcept(MetadataConstants.PULMONARY_TB_CONCEPT_UUID);
		// get smear positive concept
		Concept smearPositive = getConcept(MetadataConstants.POSITIVE_CONCEPT_UUID);
		// get patient classification concepts for new smear positive, sm
		// relapse,failure and resuming after defaulting
		Concept smearPositiveNew = getConcept(MetadataConstants.SMEAR_POSITIVE_NEW_TUBERCULOSIS_PATIENT_CONCEPT_UUID);
		Concept relapseSmearPositive = getConcept(MetadataConstants.RELAPSE_SMEAR_POSITIVE_TUBERCULOSIS_CONCEPT_UUID);
		Concept treatmentFailure = getConcept(MetadataConstants.TUBERCULOSIS_TREATMENT_FAILURE_CONCEPT_UUID);
		Concept retreatmentAfterDefault = getConcept(MetadataConstants.RETREATMENT_AFTER_DEFAULT_TUBERCULOSIS_CONCEPT_UUID);

		// check if there is any observation recorded per the tuberculosis
		// disease status
		CalculationResultMap lastObsTbDiseaseStatus = lastObs(
				getConcept(MetadataConstants.TUBERCULOSIS_DISEASE_STATUS_CONCEPT_UUID),
				cohort, context);
		// get last observations for disease classification, patient
		// classification
		// and pulmonary tb positive to determine when sputum will be due for
		// patients
		// in future
		CalculationResultMap lastDiseaseClassiffication = lastObs(
				getConcept(MetadataConstants.SITE_OF_TUBERCULOSIS_DISEASE_CONCEPT_UUID),
				alive, context);
		CalculationResultMap lastPatientClassification = lastObs(
				getConcept(MetadataConstants.TYPE_OF_TB_PATIENT_CONCEPT_UUID),
				alive, context);
		CalculationResultMap lastTbPulmonayResult = lastObs(
				getConcept(MetadataConstants.RESULTS_TUBERCULOSIS_CULTURE_CONCEPT_UUID),
				alive, context);
		// get the first observation ever the patient had a sputum results for
		// month 0
		CalculationResultMap sputumResultsForMonthZero = firstObs(
				getConcept(MetadataConstants.SPUTUM_FOR_ACID_FAST_BACILLI_CONCEPT_UUID),
				alive, context);
		// get the date when Tb treatment was started, the patient should be in
		// tb program to have this date
		CalculationResultMap tbStartTreatmentDate = lastObs(
				getConcept(MetadataConstants.TUBERCULOSIS_DRUG_TREATMENT_START_DATE_CONCEPT_UUID),
				inTbProgram, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean needsSputum = false;

			// check if a patient is alive
			if (alive.contains(ptId)) {
				// is the patient suspected of TB?
				ObsResult r = (ObsResult) lastObsTbDiseaseStatus.get(ptId);
				if (r != null
						&& (r.getValue().getValueCoded().equals(tbsuspect))) {

					// get the last observation of sputum since tb was suspected
					CalculationResultMap firstObsSinceSuspected = firstObsOnOrAfterDate(
							getConcept(MetadataConstants.SPUTUM_FOR_ACID_FAST_BACILLI_CONCEPT_UUID),
							r.getDateOfResult(), cohort, context);
					// get the first observation of sputum since the patient was
					// suspected
					ObsResult results = (ObsResult) firstObsSinceSuspected
							.get(ptId);

					if (results == null) {
						needsSputum = true;
					}

				}
				// getting sputum alerts for already enrolled patients
				// get the observations based on disease classification,patient
				// classification and results of tuberculosis
				ObsResult diseaseClassification = (ObsResult) lastDiseaseClassiffication
						.get(ptId);
				ObsResult patientClassification = (ObsResult) lastPatientClassification
						.get(ptId);
				ObsResult tbResults = (ObsResult) lastTbPulmonayResult
						.get(ptId);
				// get the obsresult for month zero
				ObsResult resultsForMonthZero = (ObsResult) sputumResultsForMonthZero
						.get(ptId);
				// get obsresults for tb treatment start date
				ObsResult treatmentStartDateObs = (ObsResult) tbStartTreatmentDate
						.get(ptId);
				// get calendar instance and set the date to the tb treatment
				// start
				// date
				Calendar c = Calendar.getInstance();

				if ((resultsForMonthZero != null)
						&& (treatmentStartDateObs != null)
						&& (diseaseClassification.getValue().getValueCoded()
								.equals(pulmonaryTb))
						&& (tbResults.getValue().getValueCoded()
								.equals(smearPositive))) {

					c.setTime(treatmentStartDateObs.getValue().getValueDate());

					Integer numberOfDaysSinceTreatmentStarted = daysSince(
							treatmentStartDateObs.getValue().getValueDate(),
							context);
					// patients with patient classification as new have a repeat
					// sputum in month 2,5 and 6
					// repeating sputum test at month 2 for new patient
					// classification
					if ((patientClassification.getValue().getValueCoded()
							.equals(smearPositiveNew))
							&& (numberOfDaysSinceTreatmentStarted >= KenyaEmrConstants.MONTH_TWO_SPUTUM_TEST)) {
						// check for the first obs since
						// numberOfDaysSinceTreatmentStarted elaspses, first
						// encounter on or after 2 month
						// get the date two months after start of treatment
						c.add(Calendar.DATE,
								KenyaEmrConstants.MONTH_TWO_SPUTUM_TEST);
						Date dateAfterTwomonths = c.getTime();
						// now find the first obs recorded on or after
						// dateAfterTwomonths based on sputum ie it should be
						// null
						// for alert to remain active otherwise it has to go off
						CalculationResultMap firstObsAfterTwomonthsOnOrAfterdateAfterTwomonths = firstObsOnOrAfterDate(
								getConcept(MetadataConstants.SPUTUM_FOR_ACID_FAST_BACILLI_CONCEPT_UUID),
								dateAfterTwomonths, inTbProgram, context);
						// get the observation results
						ObsResult resultAfterTwomonthsOnOrAfterdateAfterTwomonths = (ObsResult) firstObsAfterTwomonthsOnOrAfterdateAfterTwomonths
								.get(ptId);
						// check if
						// resultAfterTwomonthsOnOrAfterdateAfterTwomonths is
						// null
						// or empty have the alert persist otherwise not
						if (resultAfterTwomonthsOnOrAfterdateAfterTwomonths == null
								|| resultAfterTwomonthsOnOrAfterdateAfterTwomonths
										.isEmpty()) {

							needsSputum = true;
						}

					}
					// Repeat for month 5 for new patient classification
					if ((patientClassification.getValue().getValueCoded()
							.equals(smearPositiveNew))
							&& (numberOfDaysSinceTreatmentStarted >= KenyaEmrConstants.MONTH_FIVE_SPUTUM_TEST)) {
						// get the date at month 5 since treatment started
						c.add(Calendar.DATE,
								KenyaEmrConstants.MONTH_FIVE_SPUTUM_TEST);
						Date dateAfterFiveMonths = c.getTime();
						// check if any obs is collected on or after this date
						CalculationResultMap firstObsAfterFivemonthsOnOrAfterdateAfterFivemonths = firstObsOnOrAfterDate(
								getConcept(MetadataConstants.SPUTUM_FOR_ACID_FAST_BACILLI_CONCEPT_UUID),
								dateAfterFiveMonths, inTbProgram, context);
						// get the observation results
						ObsResult resultAfterFivemonthsOnOrAfterdateAfterFivemonths = (ObsResult) firstObsAfterFivemonthsOnOrAfterdateAfterFivemonths
								.get(ptId);
						// check if this value is empty then the alert should
						// persist
						if (resultAfterFivemonthsOnOrAfterdateAfterFivemonths == null
								|| resultAfterFivemonthsOnOrAfterdateAfterFivemonths
										.isEmpty()) {
							needsSputum = true;

						}

					}
					// Repeat for month 6 for new patient classification and
					// sputum
					// is said to be completed
					if ((patientClassification.getValue().getValueCoded()
							.equals(smearPositiveNew))
							&& (numberOfDaysSinceTreatmentStarted >= KenyaEmrConstants.MONTH_SIX_SPUTUM_TEST)) {
						// get the date at month 6 since treatment started
						c.add(Calendar.DATE,
								KenyaEmrConstants.MONTH_SIX_SPUTUM_TEST);
						Date dateAfterSixMonths = c.getTime();
						// check if there is any observation on or after this
						// date
						CalculationResultMap firstObsAfterSixmonthsOnOrAfterdateAfterSixmonths = firstObsOnOrAfterDate(
								getConcept(MetadataConstants.SPUTUM_FOR_ACID_FAST_BACILLI_CONCEPT_UUID),
								dateAfterSixMonths, inTbProgram, context);
						// get the observation results
						ObsResult resultAfterSixmonthsOnOrAfterdateAfterSixmonths = (ObsResult) firstObsAfterSixmonthsOnOrAfterdateAfterSixmonths
								.get(ptId);
						// if the value is empty or null, then the alert has to
						// persist
						if (resultAfterSixmonthsOnOrAfterdateAfterSixmonths == null
								|| resultAfterSixmonthsOnOrAfterdateAfterSixmonths
										.isEmpty()) {
							needsSputum = true;

						}

					}
					// now to check for the repeat sputum tests for patient
					// classification smear positive relapse, failure and
					// resumed
					// test repeat in month 3,5 and 8
					if ((patientClassification.getValue().getValueCoded()
							.equals(relapseSmearPositive))
							|| (patientClassification.getValue().getValueCoded()
									.equals(treatmentFailure))
							|| (patientClassification.getValue().getValueCoded()
									.equals(retreatmentAfterDefault))) {
						// check for the days elapsed since treatment was commenced
						// will target 3rd month
						if (numberOfDaysSinceTreatmentStarted >= KenyaEmrConstants.MONTH_THREE_SPUTUM_TEST) {
							// get the date at Month 3 since the treatment started
							c.add(Calendar.DATE,
									KenyaEmrConstants.MONTH_THREE_SPUTUM_TEST);
							Date dateAfterThreeMonths = c.getTime();
							// get the first observation of sputum on or after the
							// date
							CalculationResultMap firstObsAfterThreeMonthOnOrAfterdateAfterThreeMonths = firstObsOnOrAfterDate(
									getConcept(MetadataConstants.SPUTUM_FOR_ACID_FAST_BACILLI_CONCEPT_UUID),
									dateAfterThreeMonths, inTbProgram, context);
							// get the observation results
							ObsResult resultAfterThreemonthsOnOrAfterdateAfterThreemonths = (ObsResult) firstObsAfterThreeMonthOnOrAfterdateAfterThreeMonths
									.get(ptId);
							// check if this contain any value, it null or empty
							// then alert has to persist
							if (resultAfterThreemonthsOnOrAfterdateAfterThreemonths == null
									|| resultAfterThreemonthsOnOrAfterdateAfterThreemonths
											.isEmpty()) {
								needsSputum = true;

							}

						}
						// check for the days in the 5th month
						if (numberOfDaysSinceTreatmentStarted >= KenyaEmrConstants.MONTH_FIVE_SPUTUM_TEST) {
							// get the date after 5 month since the retreatment
							// started
							c.add(Calendar.DATE,
									KenyaEmrConstants.MONTH_FIVE_SPUTUM_TEST);
							Date dateAfterFiveMonths = c.getTime();
							// get the first observation of sputum on or after the
							// date
							CalculationResultMap firstObsAfterFiveMonthOnOrAfterdateAfterFiveMonths = firstObsOnOrAfterDate(
									getConcept(MetadataConstants.SPUTUM_FOR_ACID_FAST_BACILLI_CONCEPT_UUID),
									dateAfterFiveMonths, inTbProgram, context);
							// get the observation results
							ObsResult resultAfterFivemonthsOnOrAfterdateAfterFivemonths = (ObsResult) firstObsAfterFiveMonthOnOrAfterdateAfterFiveMonths
									.get(ptId);
							// check if this contain any value, it null or empty
							// then alert has to persist
							if (resultAfterFivemonthsOnOrAfterdateAfterFivemonths == null
									|| resultAfterFivemonthsOnOrAfterdateAfterFivemonths
											.isEmpty()) {
								needsSputum = true;

							}

						}
						// check for the days in the 8th month and it will be
						// considered complete treatment
						if (numberOfDaysSinceTreatmentStarted >= KenyaEmrConstants.MONTH_EIGHT_SPUTUM_TEST) {
							// get the date after 8 month since the retreatment
							// started
							c.add(Calendar.DATE,
									KenyaEmrConstants.MONTH_EIGHT_SPUTUM_TEST);
							Date dateAfterEightMonths = c.getTime();
							// get the first observation of sputum on or after the
							// date
							CalculationResultMap firstObsAfterEightMonthOnOrAfterdateAfterEightMonths = firstObsOnOrAfterDate(
									getConcept(MetadataConstants.SPUTUM_FOR_ACID_FAST_BACILLI_CONCEPT_UUID),
									dateAfterEightMonths, inTbProgram, context);
							// get the observation results
							ObsResult resultAfterEightmonthsOnOrAfterdateAfterEightmonths = (ObsResult) firstObsAfterEightMonthOnOrAfterdateAfterEightMonths
									.get(ptId);
							// check if this contain any value, it null or empty
							// then alert has to persist
							if (resultAfterEightmonthsOnOrAfterdateAfterEightmonths == null
									|| resultAfterEightmonthsOnOrAfterdateAfterEightmonths
											.isEmpty()) {
								needsSputum = true;

							}

						}
					}

				}

			}
			ret.put(ptId, new BooleanResult(needsSputum, this, context));
		}
		return ret;
	}

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.BaseAlertCalculation#getAlertMessage()
	 */
	@Override
	public String getAlertMessage() {
		return "Due for Sputum";
	}

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation#getName()
	 */
	@Override
	public String getName() {
		return "Patients Due for Sputum";
	}

	@Override
	public String[] getTags() {
		return new String[] { "alert", "hiv" };
	}

}
