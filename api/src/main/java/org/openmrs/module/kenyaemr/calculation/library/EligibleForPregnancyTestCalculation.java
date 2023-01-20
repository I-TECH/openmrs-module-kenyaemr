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
import org.openmrs.*;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.*;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Calculates the eligibility for pregnancy test of female patients
 */

        public class EligibleForPregnancyTestCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {
        protected static final Log log = LogFactory.getLog(EligibleForPregnancyTestCalculation.class);

       public static final EncounterType triageEncType = MetadataUtils.existing(EncounterType.class, CommonMetadata._EncounterType.TRIAGE);
       public static final Form triageScreeningForm = MetadataUtils.existing(Form.class, CommonMetadata._Form.TRIAGE);

        @Override
        public String getFlagMessage() {
        return "Eligible for Pregnancy Test";
        }
        Integer SEXUAL_ABSTAINED = 160109;
        Integer LMP = 48;
        Integer FAMILY_PLANNING = 160653;
        Integer MISCARIAGE = 1657;
        Integer NEGATIVE = 1066;
        /**
         * Evaluates the calculation
         * @should calculate null for deceased patients
         * @should calculate null for patients with no recorded status
         * @should calculate last recorded pregnancy status for all patients
         */

        @Override
        public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

            Set<Integer> aliveAndFemale = Filters.female(Filters.alive(cohort, context), context);
            PatientService patientService = Context.getPatientService();

            CalculationResultMap ret = new CalculationResultMap();

        for (Integer ptId :aliveAndFemale) {
            boolean result = false;
            Patient patient = patientService.getPatient(ptId);
            Set<Integer> isPregnant = CalculationUtils.patientsThatPass(calculate(new IsPregnantCalculation(), cohort, context));
            if(isPregnant.contains(ptId)){
                result = false;
               }else {

                Encounter lastTriageEnc = EmrUtils.lastEncounter(patient, triageEncType, triageScreeningForm);

                ConceptService cs = Context.getConceptService();
                Concept sexualAbstainedResult = cs.getConcept(SEXUAL_ABSTAINED);
                Concept lmpResult = cs.getConcept(LMP);
                Concept familyPlanningResult = cs.getConcept(FAMILY_PLANNING);
                Concept miscariageResult = cs.getConcept(MISCARIAGE);
                Concept negative = cs.getConcept(NEGATIVE);
                boolean patientSexualAbstainedResult = lastTriageEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastTriageEnc, sexualAbstainedResult, negative) : false;
                boolean pantientLmpResult = lastTriageEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastTriageEnc, lmpResult, negative) : false;
                boolean patientFamilyPlanningResult = lastTriageEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastTriageEnc, familyPlanningResult, negative) : false;
                boolean patientMiscariageResultTestResult = lastTriageEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastTriageEnc, miscariageResult, negative) : false;

                if (patientSexualAbstainedResult && pantientLmpResult && patientFamilyPlanningResult && patientMiscariageResultTestResult) {
                    result = true;
                }

            }
            ret.put(ptId, new BooleanResult(result, this));
            }

            return ret;
        }}
