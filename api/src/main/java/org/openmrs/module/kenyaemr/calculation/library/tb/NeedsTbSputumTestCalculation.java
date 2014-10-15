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

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculate whether patients are due for a sputum test. Calculation returns
 * true if the patient is alive, and screened for tb, and has cough of any
 * duration probably 2 weeks during the 2 weeks then there should have been no
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

		// get patient classification concepts for new smear positive, sm
		// relapse,failure and resuming after defaulting
		Concept smearPositiveNew = Dictionary.getConcept(Dictionary.SMEAR_POSITIVE_NEW_TUBERCULOSIS_PATIENT);
		Concept relapseSmearPositive = Dictionary.getConcept(Dictionary.RELAPSE_SMEAR_POSITIVE_TUBERCULOSIS);
		Concept treatmentFailure = Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_FAILURE);
		Concept retreatmentAfterDefault = Dictionary.getConcept(Dictionary.RETREATMENT_AFTER_DEFAULT_TUBERCULOSIS);

		// check if there is any observation recorded per the tuberculosis disease status
		CalculationResultMap lastObsTbDiseaseStatus = Calculations.lastObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS), inTbProgram, context);

		// get last observations for disease classification, patient classification
		// and pulmonary tb positive to determine when sputum will be due for patients in future
		CalculationResultMap lastDiseaseClassiffication = Calculations.lastObs(Dictionary.getConcept(Dictionary.SITE_OF_TUBERCULOSIS_DISEASE), inTbProgram, context);
		CalculationResultMap lastPatientClassification = Calculations.lastObs(Dictionary.getConcept(Dictionary.TYPE_OF_TB_PATIENT), inTbProgram, context);
		CalculationResultMap lastTbPulmonayResult = Calculations.lastObs(Dictionary.getConcept(Dictionary.RESULTS_TUBERCULOSIS_CULTURE), inTbProgram, context);

		// get the first observation ever the patient had a sputum results for month 0
		CalculationResultMap sputumResultsForMonthZero = Calculations.firstObs(Dictionary.getConcept(Dictionary.SPUTUM_FOR_ACID_FAST_BACILLI), inTbProgram, context);

		// get the date when Tb treatment was started, the patient should be in tb program to have this date
		CalculationResultMap tbStartTreatmentDate = Calculations.lastObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_DRUG_TREATMENT_START_DATE), inTbProgram, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean needsSputum = false;

			// check if a patient is alive
			if (alive.contains(ptId)) {
				// is the patient suspected of TB?
				Obs lastObsTbDiseaseResults = EmrCalculationUtils.obsResultForPatient(lastObsTbDiseaseStatus, ptId);
				if ((lastObsTbDiseaseResults != null) && (lastObsTbDiseaseResults.getValueCoded().equals(tbsuspect))) {
					// get the last observation of sputum since tb was suspected
					CalculationResultMap firstObsSinceSuspected = Calculations.firstObsOnOrAfter(Dictionary.getConcept(Dictionary.SPUTUM_FOR_ACID_FAST_BACILLI), lastObsTbDiseaseResults.getObsDatetime(), inTbProgram, context);

					// get the first observation of sputum since the patient was
					// suspected
					Obs firstObsSinceSuspectedResults = EmrCalculationUtils.obsResultForPatient(firstObsSinceSuspected, ptId);

					if (firstObsSinceSuspectedResults == null) {
						needsSputum = true;
					}
				}
				// getting sputum alerts for already enrolled patients
				// get the observations based on disease classification,patient
				// classification and results of tuberculosis
				Obs diseaseClassification = EmrCalculationUtils.obsResultForPatient(lastDiseaseClassiffication, ptId);
				Obs patientClassification = EmrCalculationUtils.obsResultForPatient(lastPatientClassification, ptId);
				Obs tbResults = EmrCalculationUtils.obsResultForPatient(lastTbPulmonayResult, ptId);
				// get the obsresult for month zero
				Obs resultsForMonthZero = EmrCalculationUtils.obsResultForPatient(sputumResultsForMonthZero, ptId);
				// get obsresults for tb treatment start date
				Date  treatmentStartDate = EmrCalculationUtils.datetimeObsResultForPatient(tbStartTreatmentDate, ptId);
				// get calendar instance and set the date to the tb treatment
				// start
				// date
				Calendar c = Calendar.getInstance();
				

				if ((resultsForMonthZero != null)
						&& (treatmentStartDate != null)
						&& (diseaseClassification != null)
						&& (tbResults != null)
						&& (diseaseClassification.getValueCoded().equals(pulmonaryTb))
						&& (tbResults.getValueCoded().equals(smearPositive))) {
							c.setTime(treatmentStartDate);
							Integer numberOfDaysSinceTreatmentStarted = EmrCalculationUtils.daysSince(treatmentStartDate, context);
					// patients with patient classification as new have a repeat
					// sputum in month 2,5 and 6
					// repeating sputum test at month 2 for new patient
					// classification
					if ((patientClassification != null)
							&& (patientClassification.getValueCoded().equals(smearPositiveNew))
							&& (numberOfDaysSinceTreatmentStarted >= TbConstants.MONTH_TWO_SPUTUM_TEST)) {
						// check for the first obs since
						// numberOfDaysSinceTreatmentStarted elaspses, first
						// encounter on or after 2 month
						// get the date two months after start of treatment
						c.add(Calendar.DATE, TbConstants.MONTH_TWO_SPUTUM_TEST);
						Date dateAfterTwomonths = c.getTime();
						// now find the first obs recorded on or after
						// dateAfterTwomonths based on sputum ie it should be
						// null
						// for alert to remain active otherwise it has to go off
						CalculationResultMap firstObsAfterTwomonthsOnOrAfterdateAfterTwomonths = Calculations.firstObsOnOrAfter(Dictionary.getConcept(Dictionary.SPUTUM_FOR_ACID_FAST_BACILLI), dateAfterTwomonths, inTbProgram, context);
						// get the observation results
						Obs resultAfterTwomonthsOnOrAfterdateAfterTwomonths = EmrCalculationUtils.obsResultForPatient(firstObsAfterTwomonthsOnOrAfterdateAfterTwomonths, ptId);
						// check if
						// resultAfterTwomonthsOnOrAfterdateAfterTwomonths is
						// null
						// or empty have the alert persist otherwise not
						if (resultAfterTwomonthsOnOrAfterdateAfterTwomonths == null) {
							System.out.println("This is the status "+tbsuspect.getName() + "and patient is"+ptId);
							needsSputum = true;
						}

					}
					// Repeat for month 5 for new patient classification
					if ((patientClassification != null)
							&& (patientClassification.getValueCoded().equals(smearPositiveNew))
							&& (numberOfDaysSinceTreatmentStarted >= TbConstants.MONTH_FIVE_SPUTUM_TEST)) {
						// get the date at month 5 since treatment started
						c.add(Calendar.DATE, TbConstants.MONTH_FIVE_SPUTUM_TEST);
						Date dateAfterFiveMonths = c.getTime();
						// check if any obs is collected on or after this date
						CalculationResultMap firstObsAfterFivemonthsOnOrAfterdateAfterFivemonths = Calculations.firstObsOnOrAfter(
								Dictionary.getConcept(Dictionary.SPUTUM_FOR_ACID_FAST_BACILLI),	dateAfterFiveMonths, inTbProgram, context);
						// get the observation results
						Obs resultAfterFivemonthsOnOrAfterdateAfterFivemonths = EmrCalculationUtils.obsResultForPatient(firstObsAfterFivemonthsOnOrAfterdateAfterFivemonths, ptId);
						// check if this value is empty then the alert should
						// persist
						if (resultAfterFivemonthsOnOrAfterdateAfterFivemonths == null) {
							needsSputum = true;
						}

					}
					// Repeat for month 6 for new patient classification and
					// sputum
					// is said to be completed
					if ((patientClassification != null)
							&& (patientClassification.getValueCoded().equals(smearPositiveNew))
							&& (numberOfDaysSinceTreatmentStarted >= TbConstants.MONTH_SIX_SPUTUM_TEST)) {
						// get the date at month 6 since treatment started
						c.add(Calendar.DATE, TbConstants.MONTH_SIX_SPUTUM_TEST);
						Date dateAfterSixMonths = c.getTime();
						// check if there is any observation on or after this
						// date
						CalculationResultMap firstObsAfterSixmonthsOnOrAfterdateAfterSixmonths = Calculations.firstObsOnOrAfter(
								Dictionary.getConcept(Dictionary.SPUTUM_FOR_ACID_FAST_BACILLI), dateAfterSixMonths, inTbProgram, context);
						// get the observation results
						Obs resultAfterSixmonthsOnOrAfterdateAfterSixmonths = EmrCalculationUtils.obsResultForPatient(firstObsAfterSixmonthsOnOrAfterdateAfterSixmonths,ptId);
						// if the value is empty or null, then the alert has to
						// persist
						if (resultAfterSixmonthsOnOrAfterdateAfterSixmonths == null) {
							needsSputum = true;
						}

					}
					// now to check for the repeat sputum tests for patient
					// classification smear positive relapse, failure and
					// resumed
					// test repeat in month 3,5 and 8
					if ((patientClassification != null)
							&& ((patientClassification.getValueCoded().equals(relapseSmearPositive))
							|| (patientClassification.getValueCoded().equals(treatmentFailure))
							|| (patientClassification.getValueCoded().equals(retreatmentAfterDefault)))) {
						// check for the days elapsed since treatment was commenced
						// will target 3rd month
						if (numberOfDaysSinceTreatmentStarted >= TbConstants.MONTH_THREE_SPUTUM_TEST) {
							// get the date at Month 3 since the treatment started
							c.add(Calendar.DATE, TbConstants.MONTH_THREE_SPUTUM_TEST);
							Date dateAfterThreeMonths = c.getTime();
							// get the first observation of sputum on or after the
							// date
							CalculationResultMap firstObsAfterThreeMonthOnOrAfterdateAfterThreeMonths = Calculations.firstObsOnOrAfter(
									Dictionary.getConcept(Dictionary.SPUTUM_FOR_ACID_FAST_BACILLI),	dateAfterThreeMonths, inTbProgram, context);
							// get the observation results
							Obs resultAfterThreemonthsOnOrAfterdateAfterThreemonths = EmrCalculationUtils.obsResultForPatient(firstObsAfterThreeMonthOnOrAfterdateAfterThreeMonths, ptId);
							// check if this contain any value, it null or empty
							// then alert has to persist
							if (resultAfterThreemonthsOnOrAfterdateAfterThreemonths == null) {
								needsSputum = true;
							}

						}
						// check for the days in the 5th month
						if (numberOfDaysSinceTreatmentStarted >= TbConstants.MONTH_FIVE_SPUTUM_TEST) {
							// get the date after 5 month since the retreatment
							// started
							c.add(Calendar.DATE, TbConstants.MONTH_FIVE_SPUTUM_TEST);
							Date dateAfterFiveMonths = c.getTime();
							// get the first observation of sputum on or after the
							// date
							CalculationResultMap firstObsAfterFiveMonthOnOrAfterdateAfterFiveMonths = Calculations.firstObsOnOrAfter(
									Dictionary.getConcept(Dictionary.SPUTUM_FOR_ACID_FAST_BACILLI),	dateAfterFiveMonths, inTbProgram, context);
							// get the observation results
							Obs resultAfterFivemonthsOnOrAfterdateAfterFivemonths = EmrCalculationUtils.obsResultForPatient(firstObsAfterFiveMonthOnOrAfterdateAfterFiveMonths, ptId);
							// check if this contain any value, it null or empty
							// then alert has to persist
							if (resultAfterFivemonthsOnOrAfterdateAfterFivemonths == null) {
								needsSputum = true;
							}

						}
						// check for the days in the 8th month and it will be
						// considered complete treatment
						if (numberOfDaysSinceTreatmentStarted >= TbConstants.MONTH_EIGHT_SPUTUM_TEST) {
							// get the date after 8 month since the retreatment
							// started
							c.add(Calendar.DATE, TbConstants.MONTH_EIGHT_SPUTUM_TEST);
							Date dateAfterEightMonths = c.getTime();
							// get the first observation of sputum on or after the
							// date
							CalculationResultMap firstObsAfterEightMonthOnOrAfterdateAfterEightMonths = Calculations.firstObsOnOrAfter(
									Dictionary.getConcept(Dictionary.SPUTUM_FOR_ACID_FAST_BACILLI), 	dateAfterEightMonths, inTbProgram, context);
							// get the observation results
							Obs resultAfterEightmonthsOnOrAfterdateAfterEightmonths = EmrCalculationUtils.obsResultForPatient(firstObsAfterEightMonthOnOrAfterdateAfterEightMonths, ptId);
							// check if this contain any value, it null or empty
							// then alert has to persist
							if (resultAfterEightmonthsOnOrAfterdateAfterEightmonths == null) {
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
}