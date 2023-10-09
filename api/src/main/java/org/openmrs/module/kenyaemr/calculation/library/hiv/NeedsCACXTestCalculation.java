/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils.daysSince;

/**
 * Created by codehub on 05/06/15.
 */
public class NeedsCACXTestCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {
    protected static final Log log = LogFactory.getLog(NeedsCACXTestCalculation.class);

    public static final Form cacxScreeningForm = MetadataUtils.existing(Form.class, CommonMetadata._Form.CACX_SCREENING_FORM);
    public static final Form oncologyScreeningForm = MetadataUtils.existing(Form.class, CommonMetadata._Form.ONCOLOGY_SCREENING_FORM);
    public static final Integer CACX_TEST_RESULT_QUESTION_CONCEPT_ID = 164934;
    public static final Integer CACX_SCREEENING_METHOD_QUESTION_CONCEPT_ID = 163589;

    /**
     * @see org.openmrs.module.kenyacore.calculation.PatientFlagCalculation#getFlagMessage()
     */
    @Override
    public String getFlagMessage() { return "Due for CACX Screening";}
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

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        PatientService patientService = Context.getPatientService();
        Set<Integer> aliveAndFemale = Filters.female(Filters.alive(cohort, context), context);

        CalculationResultMap ret = new CalculationResultMap();

        for(Integer ptId:aliveAndFemale) {
            Patient patient = patientService.getPatient(ptId);
            boolean needsCacxTest = false;

            List<Encounter> cacxScreeningEncounters = Context.getEncounterService().getEncounters(patientService.getPatient(ptId), null,
                    null, null, Arrays.asList(cacxScreeningForm,oncologyScreeningForm), null, null, null, null, false);

            if (cacxScreeningEncounters.size() == 0) {
                // no cervical cancer screening done
                 needsCacxTest = true;
            } else {
                // in case there are more than one, we pick the last one
                Encounter lastCacxScreeningEnc = cacxScreeningEncounters.get(cacxScreeningEncounters.size() - 1);
                ConceptService cs = Context.getConceptService();
                Concept cacxTestResultQuestion = cs.getConcept(CACX_TEST_RESULT_QUESTION_CONCEPT_ID);
                Concept cacxScreeningMethodQuestion = cs.getConcept(CACX_SCREEENING_METHOD_QUESTION_CONCEPT_ID);
                Concept cacxHpvScreeningMethod = cs.getConcept(HPV_TEST_CONCEPT_ID);
                Concept cacxPositiveResult = cs.getConcept(POSITIVE);
                Concept cacxNegativeResult = cs.getConcept(NEGATIVE);
                Concept cacxNormalResult = cs.getConcept(NORMAL);
                Concept cacxSuspiciousForCancerResult = cs.getConcept(SUSPICIOUS_FOR_CANCER);
                Concept cacxOtherResult = cs.getConcept(OTHER);
                Concept cacxAbnormalResult = cs.getConcept(ABNORMAL);
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
                if (patient.getAge() >= 18) {

                    // cacx flag should be 24 months after last cacx screening using HPV method and result is negative
                    if (lastCacxScreeningEnc != null && patientScreenedUsingHPV && patientHasNegativeTestResult && (daysSince(lastCacxScreeningEnc.getEncounterDatetime(), context) >= 730)) {
                        needsCacxTest = true;
                    }
                    // cacx flag should be 12 months after last cacx if negative or normal and cacx method is not HPV
                    if (lastCacxScreeningEnc != null && !patientScreenedUsingHPV && (patientHasNegativeTestResult || patientHasNormalTestResult) && (daysSince(lastCacxScreeningEnc.getEncounterDatetime(), context) >= 365)) {
                        needsCacxTest = true;
                    }

                    // cacx flag should be 6 months after last cacx if positive
                    if (lastCacxScreeningEnc != null && patientHasPositiveTestResult && (daysSince(lastCacxScreeningEnc.getEncounterDatetime(), context) >= 183)) {
                        needsCacxTest = true;
                    }

                    // cacx flag should remain if there is any suspicion
                    if (lastCacxScreeningEnc != null && (patientHasSuspiciousTestResult || patientHasOtherTestResult || patientHasLowGradeLesionTestResult || patientHasHighGradeLesionTestResult || patientHasInvasiveCancerTestResult || patientHasPresumedCancerTestResult || patientHasAbnormalTestResult)) {
                        needsCacxTest = true;
                    }

                    // cacx flag should remain if there are no results added
                    if (lastCacxScreeningEnc != null && (!patientHasPositiveTestResult && !patientHasNegativeTestResult && !patientHasNormalTestResult && !patientHasSuspiciousTestResult && !patientHasOtherTestResult && !patientHasLowGradeLesionTestResult && !patientHasHighGradeLesionTestResult && !patientHasInvasiveCancerTestResult && !patientHasPresumedCancerTestResult && !patientHasAbnormalTestResult)) {
                        needsCacxTest = true;
                    }

                }
            }
            ret.put(ptId, new BooleanResult(needsCacxTest, this));
        }
        return  ret;
    }

}
