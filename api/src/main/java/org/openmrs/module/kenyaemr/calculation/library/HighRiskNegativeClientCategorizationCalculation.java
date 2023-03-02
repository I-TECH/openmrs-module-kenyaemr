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
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.util.HtsConstants;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * There is a need to categorize high-risk Negative PMTCT Client
 */

public class HighRiskNegativeClientCategorizationCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {
    protected static final Log log = LogFactory.getLog(HighRiskNegativeClientCategorizationCalculation.class);

    @Override
    public String getFlagMessage() {
        return "High Risk Client";
    }

    /**
     * Evaluates the calculation
     #Prerequisites
     * 1. PMTCT client enrolled in MCH
     * 2. Tested negative HTS and MCH modules: Not Enrolled

     #Criteria
     * 3. In discordant relationship :      SOURCE: HTS initial/Retest, HTS Eligibility
     * 4. Multiple sex partners :           SOURCE: Behavioural assessment form, Contact(count)
     * 5. Presence of STI                   SOURCE: Behavioural assessment form
     * 6. Intimate sexual partner violence(GBV):    SOURCE: GBV Form, HTS Eligibility,Behavioural assessment form
     *
     */

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        Set<Integer> alive = Filters.alive(cohort, context);

        Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        Set<Integer> inMchmsProgram = Filters.inProgram(mchmsProgram, cohort, context);
        CalculationResultMap ret = new CalculationResultMap();

        for (Integer ptId : cohort) {
            boolean result = false;

            PatientService patientService = Context.getPatientService();
            Patient patient = patientService.getPatient(ptId);

            ConceptService cs = Context.getConceptService();
            Concept htsFinalTestQuestion = cs.getConcept(HtsConstants.HTS_FINAL_TEST_CONCEPT_ID);
            Concept htsNegativeResult = cs.getConcept(HtsConstants.HTS_NEGATIVE_RESULT_CONCEPT_ID);
            Concept discordantCouple = Dictionary.getConcept(Dictionary.DISCORDANT_COUPLE);
            Concept behavioralAssessmentOutcomeQuestion = cs.getConcept(165091);
            Concept assessmentOutcomeHighRiskAnswer = cs.getConcept(138643);
            Concept ipvGBVSexualQuestion = cs.getConcept(160658);
            Concept ipvHTSEligibilitySexualQuestion = cs.getConcept(167145);
            Concept yesAnswer = Dictionary.getConcept(Dictionary.YES);
            Concept ipvGbvSexualYesAnswer = cs.getConcept(152370);

            // 1. PMTCT client enrolled in MCH
            if (inMchmsProgram.contains(ptId)) {
                // Check  Tested HIV- clients in HTS module
                Encounter lastHtsInitialEnc = EmrUtils.lastEncounter(patient, HtsConstants.htsEncType, HtsConstants.htsInitialForm);
                Encounter lastHtsRetestEnc = EmrUtils.lastEncounter(patient, HtsConstants.htsEncType, HtsConstants.htsRetestForm);

                //  Check new Tested HIV- clients in MCH module
                Form antenatalVisitForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_ANTENATAL_VISIT);
                Form matVisitForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_DELIVERY);
                Form pncVisitForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_POSTNATAL_VISIT);
                EncounterType mchConsultationEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION);
                Encounter lastANCHtsEnc = EmrUtils.lastEncounter(patient,mchConsultationEncounterType,antenatalVisitForm );
                Encounter lastMatHtsEnc = EmrUtils.lastEncounter(patient,mchConsultationEncounterType,matVisitForm );
                Encounter lastPNCHtsEnc = EmrUtils.lastEncounter(patient,mchConsultationEncounterType,pncVisitForm );

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
                // MCH module testing
                // If there are no HIV HTS module tests, check MCH HIV tests
                if (lastHtsInitialEnc == null && lastHtsRetestEnc == null) {
                    if (lastANCHtsEnc != null) {
                        lastHtsEnc = lastANCHtsEnc;
                    } else if (lastMatHtsEnc != null) {
                        lastHtsEnc = lastMatHtsEnc;
                    } else if (lastPNCHtsEnc != null) {
                        lastHtsEnc = lastPNCHtsEnc;
                    }
                }

                if(lastHtsEnc != null) {
                    boolean patientHasNegativeTestResult = lastHtsEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastHtsEnc, htsFinalTestQuestion, htsNegativeResult) : false;

                    // 2. Tested negative
                    if (patientHasNegativeTestResult) {
                       // 3. In discordant relationship
                        boolean patientInDiscordantRelationship = lastHtsEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastHtsEnc, discordantCouple, yesAnswer) : false;
                        if (patientInDiscordantRelationship) {
                            result = true;
                        }

                        // Find last observation for :Behavior Risk Assessment multiple sex partners
                        Form behaviorRiskAssessmentForm = MetadataUtils.existing(Form.class, "40374909-05fc-4af8-b789-ed9c394ac785");
                        EncounterType behaviorRiskAssessmentEncType = MetadataUtils.existing(EncounterType.class, "6e5ec039-8d2a-4172-b3fb-ee9d0ba647b7");
                        Encounter lastBehaviourRiskEnc = EmrUtils.lastEncounter(patient, behaviorRiskAssessmentEncType, behaviorRiskAssessmentForm);

                        if (lastBehaviourRiskEnc != null) {
                            boolean behavioralAssessmentRiskOutcome = lastBehaviourRiskEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastBehaviourRiskEnc, behavioralAssessmentOutcomeQuestion, assessmentOutcomeHighRiskAnswer) : false;

                            //3,4,5 Multiple sex partners or Has Recent STI or Intimate Partner Violence : Risk Outcome in Behavior assessment form
                            if (behavioralAssessmentRiskOutcome) {
                                result = true;
                            }

                        }
                        // Find last observation for :Gender Based Violence Screening Form:  sexual gender based violence
                        Form gbvAssessmentForm = MetadataUtils.existing(Form.class, HivMetadata._Form.GBV_SCREENING);
                        EncounterType gbvRiskAssessmentEncType = MetadataUtils.existing(EncounterType.class, "f091b067-bea5-4657-8445-cfec05dc46a2");
                        Encounter lastGbvEnc = EmrUtils.lastEncounter(patient, gbvRiskAssessmentEncType, gbvAssessmentForm);

                        if (lastGbvEnc != null) {
                              boolean patientGbvIPVSexual = lastGbvEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastGbvEnc, ipvGBVSexualQuestion, ipvGbvSexualYesAnswer) : false;

                            // 6.0 Intimate sexual partner violence(GBV)
                            if (patientGbvIPVSexual) {
                                result = true;
                            }
                        }

                        // Find last observation for :HTS Eligibility Screening Form:  Sexual violence
                        Form htsEligibilityAssessmentForm = MetadataUtils.existing(Form.class,"04295648-7606-11e8-adc0-fa7ae01bbebc" );
                        EncounterType htsEligibilityEncType = MetadataUtils.existing(EncounterType.class, "9c0a7a57-62ff-4f75-babe-5835b0e921b7");
                        Encounter lastHtsEligibilityEnc = EmrUtils.lastEncounter(patient, htsEligibilityEncType, htsEligibilityAssessmentForm);

                        if (lastHtsEligibilityEnc != null) {
                            boolean patientHTSEligibilityIPVSexual = lastHtsEligibilityEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastHtsEligibilityEnc, ipvHTSEligibilitySexualQuestion, yesAnswer) : false;
                            boolean patientHTSEligibilityDiscordant = lastHtsEligibilityEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastHtsEligibilityEnc, discordantCouple, yesAnswer) : false;

                            // 6.1 Intimate sexual partner violence(HTS Eligibility screening)
                            if (patientHTSEligibilityIPVSexual) {
                                result = true;
                            }
                            // 3.1 Discordant couple(HTS Eligibility screening)
                            if (patientHTSEligibilityDiscordant) {
                                result = true;
                            }
                        }
                    }
                }

            }

            ret.put(ptId, new BooleanResult(result, this));
        }
        return ret;
    }

}