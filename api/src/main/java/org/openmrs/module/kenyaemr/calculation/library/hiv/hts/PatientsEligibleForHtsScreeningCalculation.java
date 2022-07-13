/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv.hts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.util.HtsConstants;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Checks if a patient is negative, not enrolled and has no previous linkage encounters
 * Client should be
 */
public class PatientsEligibleForHtsScreeningCalculation extends AbstractPatientCalculation {

    protected static final Log log = LogFactory.getLog(PatientsEligibleForHtsScreeningCalculation.class);

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        EncounterService encounterService = Context.getEncounterService();
        PatientService patientService = Context.getPatientService();

        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId: cohort){
            Patient patient = patientService.getPatient(ptId);
            boolean eligible = false;

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

            Encounter lastHtsInitialEnc = EmrUtils.lastEncounter(patient, HtsConstants.htsEncType, HtsConstants.htsInitialForm);
            Encounter lastHtsRetestEnc = EmrUtils.lastEncounter(patient, HtsConstants.htsEncType, HtsConstants.htsRetestForm);
            Encounter lastLinkageEnc = EmrUtils.lastEncounter(patient, HtsConstants.htsEncType, HtsConstants.htsLinkageForm);
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

            ConceptService cs = Context.getConceptService();
            Concept htsFinalTestQuestion = cs.getConcept(HtsConstants.HTS_FINAL_TEST_CONCEPT_ID);
            Concept htsPositiveResult = cs.getConcept(HtsConstants.HTS_POSITIVE_RESULT_CONCEPT_ID);

            boolean patientHasPositiveTestResult = lastHtsEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastHtsEnc, htsFinalTestQuestion, htsPositiveResult) : false;

            if(enrollmentEncounters.size() <= 0 && !patientHasPositiveTestResult && lastLinkageEnc == null) {
                eligible = true;
            }

            ret.put(ptId, new BooleanResult(eligible, this));
        }
        return ret;
    }

}