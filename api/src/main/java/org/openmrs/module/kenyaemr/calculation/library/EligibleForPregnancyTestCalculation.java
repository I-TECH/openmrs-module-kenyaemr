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
 * Calculates the eligibility for pregnancy test flag for female patients
 *
 * @should calculate null for deceased patients
 * @should calculate null for patients with no recorded status
 * @should not have abstained from sex
 * @should not have had LMP in last 7 days
 * @should not be on FP
 * @should not have had a miscarriage
 * @should not have had a recent baby
 * @should not have referred for pregnancy test
 */


        public class EligibleForPregnancyTestCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {
        protected static final Log log = LogFactory.getLog(EligibleForPregnancyTestCalculation.class);

       public static final EncounterType triageEncType = MetadataUtils.existing(EncounterType.class, CommonMetadata._EncounterType.TRIAGE);
       public static final Form triageForm = MetadataUtils.existing(Form.class, CommonMetadata._Form.TRIAGE);

        @Override
        public String getFlagMessage() {
        return "Eligible for Pregnancy Test";
        }
        Integer SEXUAL_ABSTAINED = 160109;
        Integer LMP = 162877;
        Integer FAMILY_PLANNING = 160653;
        Integer MISCARRIAGE  = 48;
        Integer RECENT_BIRTH  = 1657;
        Integer REFERRED_FOR_PREGNANCY_TEST = 1282;
        Integer NO = 1066;

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

                Encounter lastTriageEnc = EmrUtils.lastEncounter(patient, triageEncType, triageForm);

                ConceptService cs = Context.getConceptService();
                Concept sexualAbstainedResult = cs.getConcept(SEXUAL_ABSTAINED);
                Concept lmpResult = cs.getConcept(LMP);
                Concept familyPlanningResult = cs.getConcept(FAMILY_PLANNING);
                Concept miscarriageResult = cs.getConcept(MISCARRIAGE);
                Concept recentBirthResult = cs.getConcept(RECENT_BIRTH);
                Concept referredForPregnancyTest = cs.getConcept(REFERRED_FOR_PREGNANCY_TEST);
                Concept no = cs.getConcept(NO);

                boolean patientSexualAbstainedResult = lastTriageEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastTriageEnc, sexualAbstainedResult, no) : false;
                boolean pantientLmpResult = lastTriageEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastTriageEnc, lmpResult, no) : false;
                boolean patientFamilyPlanningResult = lastTriageEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastTriageEnc, familyPlanningResult, no) : false;
                boolean patientMiscariageResultTestResult = lastTriageEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastTriageEnc, miscarriageResult, no) : false;
                boolean patientRecentBirthResult = lastTriageEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastTriageEnc, recentBirthResult, no) : false;
                boolean referredForPregnancyTestResult = lastTriageEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastTriageEnc, referredForPregnancyTest, no) : false;

                //Age should be between 15 - 49 years
                if(patient.getAge() >= 15 && patient.getAge() <= 49) {
                    if (patientSexualAbstainedResult && pantientLmpResult && patientFamilyPlanningResult && patientMiscariageResultTestResult && patientRecentBirthResult && referredForPregnancyTestResult) {
                        result = true;
                    }
                }

            }
            ret.put(ptId, new BooleanResult(result, this));
            }

            return ret;
        }}
