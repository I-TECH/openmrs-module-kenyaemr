/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.Form;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.api.EncounterService;

import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.Set;
import java.util.Arrays;

import static org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils.daysSince;

/**
 * Calculates whether patients are eligible for the Cacx screening services
 */
public class EligibleForCaCxScreeningCalculation extends AbstractPatientCalculation {

	protected static final Log log = LogFactory.getLog(EligibleForCaCxScreeningCalculation.class);

	public static final EncounterType cacxEncType = MetadataUtils.existing(EncounterType.class, CommonMetadata._EncounterType.CACX_SCREENING);
	public static final Form cacxScreeningForm = MetadataUtils.existing(Form.class, CommonMetadata._Form.CACX_SCREENING_FORM);
	public static final Integer CACX_TEST_RESULT_QUESTION_CONCEPT_ID = 164934;
	public static final Integer CACX_SCREEENING_METHOD_QUESTION_CONCEPT_ID = 163589;

	Integer SCREENING_RESULT = 164934;
	Integer HPV_TEST_CONCEPT_ID = 159859;
	Integer POSITIVE = 703;
	Integer NEGATIVE = 664;
	Integer NORMAL = 1115;
	Integer SUSPICIOUS_FOR_CANCER = 159008;
	Integer OTHER = 5622;
	Integer ABNORMAL = 1116;
	Integer LOW_GRADE_LESION =  145808;
	Integer HIGH_GRADE_LESION = 145805;
	Integer INVASIVE_CANCER = 155424;
	Integer PRESUMED_CANCER = 159393;

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(Collection, Map, PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		EncounterService encounterService = Context.getEncounterService();
		PatientService patientService = Context.getPatientService();

		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);
		Set<Integer> aliveAndFemale = Filters.female(Filters.alive(cohort, context), context);

		// check for last screening results
		ConceptService conceptService = Context.getConceptService();
		CalculationResultMap cacxLast = Calculations.lastObs(conceptService.getConcept(SCREENING_RESULT), cohort, context);

		CalculationResultMap ret = new CalculationResultMap();

		for(Integer ptId:aliveAndFemale) {
			Patient patient = patientService.getPatient(ptId);
			boolean needsCacxTest = false;
			List<Encounter> enrollmentEncounters = encounterService.getEncounters(
					Context.getPatientService().getPatient(ptId),
					null,
					null,
					null,
					null,
					Arrays.asList(MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_ENROLLMENT)),
					null,
					null,
					null,
					false
			);

			Encounter lastCacxScreeningEnc = EmrUtils.lastEncounter(patient, cacxEncType, cacxScreeningForm);

			ConceptService cs = Context.getConceptService();
			Concept cacxTestResultQuestion = cs.getConcept(CACX_TEST_RESULT_QUESTION_CONCEPT_ID);
			Concept cacxScreeningMethodQuestion = cs.getConcept(CACX_SCREEENING_METHOD_QUESTION_CONCEPT_ID);
			Concept cacxHpvScreeningMethod = cs.getConcept(HPV_TEST_CONCEPT_ID);
			Concept cacxPositiveResult = cs.getConcept(POSITIVE);
			Concept cacxNegativeResult = cs.getConcept(NEGATIVE);
			Concept cacxNormalResult = cs.getConcept(NORMAL);
			Concept cacxSuspiciousForCancerResult = cs.getConcept(SUSPICIOUS_FOR_CANCER);
			Concept cacxOtherResult = cs.getConcept(OTHER);
			Concept cacxAbnormalResult = cs.getConcept(ABNORMAL );
			Concept cacxLowGradeLesionResult = cs.getConcept(LOW_GRADE_LESION);
			Concept cacxHighGradeLesionResult = cs.getConcept(HIGH_GRADE_LESION);
			Concept cacxInvasiveCancerResult = cs.getConcept(INVASIVE_CANCER);
			Concept cacxPresumedCancerResult = cs.getConcept(PRESUMED_CANCER);

			boolean patientHasPositiveTestResult = lastCacxScreeningEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastCacxScreeningEnc, cacxTestResultQuestion, cacxPositiveResult) : false;
			boolean patientHasNegativeTestResult = lastCacxScreeningEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastCacxScreeningEnc, cacxTestResultQuestion, cacxNegativeResult) : false;
			boolean patientHasNormalTestResult = lastCacxScreeningEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastCacxScreeningEnc, cacxTestResultQuestion, cacxNormalResult) : false;
			boolean patientHasSuspiciousTestResult = lastCacxScreeningEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastCacxScreeningEnc, cacxTestResultQuestion, cacxSuspiciousForCancerResult) : false;
			boolean patientHasOtherTestResult = lastCacxScreeningEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastCacxScreeningEnc, cacxTestResultQuestion, cacxOtherResult) : false;
			boolean patientHasAbnormalTestResult = lastCacxScreeningEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastCacxScreeningEnc, cacxTestResultQuestion, cacxAbnormalResult) : false;
			boolean patientHasLowGradeLesionTestResult = lastCacxScreeningEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastCacxScreeningEnc, cacxTestResultQuestion, cacxLowGradeLesionResult) : false;
			boolean patientHasHighGradeLesionTestResult = lastCacxScreeningEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastCacxScreeningEnc, cacxTestResultQuestion, cacxHighGradeLesionResult) : false;
			boolean patientHasInvasiveCancerTestResult = lastCacxScreeningEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastCacxScreeningEnc, cacxTestResultQuestion, cacxInvasiveCancerResult) : false;
			boolean patientHasPresumedCancerTestResult = lastCacxScreeningEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastCacxScreeningEnc, cacxTestResultQuestion, cacxPresumedCancerResult) : false;
			boolean patientScreenedUsingHPV = lastCacxScreeningEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastCacxScreeningEnc, cacxScreeningMethodQuestion, cacxHpvScreeningMethod) : false;

			// Newly initiated and without cervical cancer test
			if(patient.getAge() >= 15){

				// no cervical cancer screening done
				if(lastCacxScreeningEnc == null) {
					needsCacxTest = true;
				}

				// cacx flag should be 24 months after last cacx screening using HPV method and result is negative
				if(lastCacxScreeningEnc != null && patientScreenedUsingHPV && patientHasNegativeTestResult && (daysSince(lastCacxScreeningEnc.getEncounterDatetime(), context) >= 730)) {
					needsCacxTest = true;
				}

				// cacx flag should be 12 months after last cacx if negative or normal and cacx method is not HPV
				if(lastCacxScreeningEnc != null && !patientScreenedUsingHPV && (patientHasNegativeTestResult || patientHasNormalTestResult)  && (daysSince(lastCacxScreeningEnc.getEncounterDatetime(), context) >= 365)) {
					needsCacxTest = true;
				}

				// cacx flag should be 6 months after last cacx if positive
				if(lastCacxScreeningEnc != null && patientHasPositiveTestResult && (daysSince(lastCacxScreeningEnc.getEncounterDatetime(), context) >= 183)) {
					needsCacxTest = true;
				}

				// cacx flag should remain if there is any suspicion
				if(lastCacxScreeningEnc != null && (patientHasSuspiciousTestResult || patientHasOtherTestResult || patientHasLowGradeLesionTestResult || patientHasHighGradeLesionTestResult|| patientHasInvasiveCancerTestResult || patientHasPresumedCancerTestResult || patientHasAbnormalTestResult)) {
					needsCacxTest = true;
				}
				// cacx flag should remain if there are no results added
				if(lastCacxScreeningEnc != null && (!patientHasPositiveTestResult && !patientHasNegativeTestResult && !patientHasNormalTestResult && !patientHasSuspiciousTestResult && !patientHasOtherTestResult && !patientHasLowGradeLesionTestResult && !patientHasHighGradeLesionTestResult && !patientHasInvasiveCancerTestResult && !patientHasPresumedCancerTestResult && !patientHasAbnormalTestResult)) {
					needsCacxTest = true;
				}

			}
			ret.put(ptId, new BooleanResult(needsCacxTest, this));
		}
		return  ret;
	}
}