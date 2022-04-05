/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.vmmc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.VMMCMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.util.HtsConstants;
import org.openmrs.module.kenyaemr.util.VmmcConstants;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Checks if a patient has:
 * Enrolled to VMMC
 * Does not have same day positive test
 * Other validations performed by form
 */
public class PatientsEligibleForVMMCProcedureCalculation extends AbstractPatientCalculation {

    protected static final Log log = LogFactory.getLog(PatientsEligibleForVMMCProcedureCalculation.class);

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        PatientService patientService = Context.getPatientService();
        Set<Integer> alive = Filters.alive(cohort, context);
        Program vmmcProgram = MetadataUtils.existing(Program.class, VMMCMetadata._Program.VMMC);
        Set<Integer> inVmmcProgram = Filters.inProgram(vmmcProgram, alive, context);

        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId: cohort) {
            Patient patient = patientService.getPatient(ptId);
            boolean patientHasSameDayPositiveTestResult = false;
            boolean eligible = false;

            ConceptService cs = Context.getConceptService();
            Concept vmmcMethodQuestion = cs.getConcept(VmmcConstants.METHOD);
            Concept vmmcConventionalMethodAnswer = cs.getConcept(VmmcConstants.CONVENTIONAL_METHOD);
            Concept vmmcDeviceMethodAnswer = cs.getConcept(VmmcConstants.DEVICE_METHOD);

            Concept htsFinalTestQuestion = cs.getConcept(HtsConstants.HTS_FINAL_TEST_CONCEPT_ID);
            Concept htsPositiveResult = cs.getConcept(HtsConstants.HTS_POSITIVE_RESULT_CONCEPT_ID);

            Encounter lastVmmcMedicalHistoryEnc = EmrUtils.lastEncounter(patient, VmmcConstants.vmmcMedicalHistoryEncType, VmmcConstants.vmmcMedicalHistoryExaminationForm);
            Encounter lastHtsInitialEnc = EmrUtils.lastEncounter(patient, HtsConstants.htsEncType, HtsConstants.htsInitialForm);
            Encounter lastHtsRetestEnc = EmrUtils.lastEncounter(patient, HtsConstants.htsEncType, HtsConstants.htsRetestForm);
            Encounter lastHtsEnc = null;

            if (lastHtsInitialEnc != null && lastHtsRetestEnc == null) {
                lastHtsEnc = lastHtsInitialEnc;
            } else if (lastHtsInitialEnc == null && lastHtsRetestEnc != null) {
                lastHtsEnc = lastHtsRetestEnc;
            } else if (lastHtsInitialEnc != null && lastHtsRetestEnc != null) {
                if (lastHtsInitialEnc.getEncounterDatetime().after(lastHtsRetestEnc.getEncounterDatetime())) {
                    lastHtsEnc = lastHtsInitialEnc;
                } else {
                    lastHtsEnc = lastHtsRetestEnc;
                }
            }
            if (lastHtsEnc != null && lastVmmcMedicalHistoryEnc != null && lastHtsEnc.getEncounterDatetime().equals(lastVmmcMedicalHistoryEnc.getEncounterDatetime())) {
                patientHasSameDayPositiveTestResult = lastHtsEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastHtsEnc, htsFinalTestQuestion, htsPositiveResult) : false;
            }

            boolean patientWantsConventionalMethod = lastVmmcMedicalHistoryEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastVmmcMedicalHistoryEnc, vmmcMethodQuestion, vmmcConventionalMethodAnswer) : false;
            boolean patientWantsDeviceMethod = lastVmmcMedicalHistoryEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastVmmcMedicalHistoryEnc, vmmcMethodQuestion, vmmcDeviceMethodAnswer) : false;
            Encounter lastVmmcProcedure = EmrUtils.lastEncounter(patient, VmmcConstants.vmmcCircumcisionProcedureEncType, VmmcConstants.vmmcCircumcisionProcedureForm);

            if (inVmmcProgram.contains(ptId) && lastVmmcProcedure == null && lastVmmcMedicalHistoryEnc != null && !patientHasSameDayPositiveTestResult && (patientWantsConventionalMethod || patientWantsDeviceMethod )) {
                    eligible = true;
              }
                ret.put(ptId, new BooleanResult(eligible, this));
            }
            return ret;
        }
   }