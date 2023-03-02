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
import org.joda.time.Days;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
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
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.util.HtsConstants;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;



/**
 * There is a need to categorize high-risk PMTCT Client
 */

public class HighRiskPositiveClientCategorizationCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {
    protected static final Log log = LogFactory.getLog(HighRiskPositiveClientCategorizationCalculation.class);

    @Override
    public String getFlagMessage() {
        return "High Risk Client";
    }

    /**
     * Evaluates the calculation
     * 1. PMTCT client enrolled in MCH
     * 2. Tested within the last 30 days (HTS + MCH module)
     * 3. All AGYW HIV tested positive  10-19
     * 4. Reported positive at latest MCH enrollment
     * 5. All enrolled in last 30 days
     * 6. All AGYW HIV enrolled  10-19
     * 7. All Enrolled clients with detectable VL > 200 copies/ml
     * 8. Tranfer Ins or Transist clients
     */

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        Set<Integer> alive = Filters.alive(cohort, context);
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);

        PatientService patientService = Context.getPatientService();

        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);
        Set<Integer> inMchmsProgram = Filters.inProgram(mchmsProgram, cohort, context);

        CalculationResultMap inHivProgramResultMap = Calculations.activeEnrollment(hivProgram, alive, context);
        CalculationResultMap ret = new CalculationResultMap();

        Concept latestVL = Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD);
        CalculationResultMap lastVLObs = Calculations.lastObs(latestVL, inHivProgram, context);

        for (Integer ptId : cohort) {
            boolean result = false;
            Integer hivEnrollmentDiffDays = 0;
            Integer htsEncounterDiffDays = 0;
            Integer mchReportedTestDiffDays = 0;
            Date currentDate = new Date();
            Patient patient = patientService.getPatient(ptId);
            Double vl = EmrCalculationUtils.numericObsResultForPatient(lastVLObs, ptId);
            ConceptService cs = Context.getConceptService();
            Concept htsFinalTestQuestion = cs.getConcept(HtsConstants.HTS_FINAL_TEST_CONCEPT_ID);
            Concept htsPositiveResult = cs.getConcept(HtsConstants.HTS_POSITIVE_RESULT_CONCEPT_ID);
            Concept patientTypeQuestion = cs.getConcept(164932);
            Concept PatientTypeTIAnswer = Dictionary.getConcept(Dictionary.TRANSFER_IN);
            Concept dateReportedTestedHiv = Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS);
            // 1. PMTCT client enrolled in MCH
            if (inMchmsProgram.contains(ptId)) {

                // Check new Tested HIV+ clients in HTS module in the past one month
                Encounter lastHtsInitialEnc = EmrUtils.lastEncounter(patient, HtsConstants.htsEncType, HtsConstants.htsInitialForm);
                Encounter lastHtsRetestEnc = EmrUtils.lastEncounter(patient, HtsConstants.htsEncType, HtsConstants.htsRetestForm);

                //  Check new Tested HIV+ clients in MCH module in the past one month
                Form mchEnrollmentForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_ENROLLMENT);
                Form antenatalVisitForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_ANTENATAL_VISIT);
                Form matVisitForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_DELIVERY);
                Form pncVisitForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_POSTNATAL_VISIT);
                EncounterType mchEnrollmentEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_ENROLLMENT);
                EncounterType mchConsultationEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION);
                Encounter lastMCHEnrollmentEnc = EmrUtils.lastEncounter(patient,mchEnrollmentEncounterType,mchEnrollmentForm );
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
                    Date lastHtsEncounterDate = lastHtsEnc.getEncounterDatetime();
                    htsEncounterDiffDays = daysBetween(currentDate, lastHtsEncounterDate);
                    boolean patientHasPositiveTestResult = lastHtsEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastHtsEnc, htsFinalTestQuestion, htsPositiveResult) : false;

                    // 2. Recently tested positive <= 30 days
                    if (patientHasPositiveTestResult && htsEncounterDiffDays <= 30) {
                        result = true;
                    }
                    // 3. Tested positive AGYW
                    if(patientHasPositiveTestResult && patient.getAge() >= 10 && patient.getAge() <= 19){
                        result = true;
                    }

                }
                //  MCH enrollment form reported status
                if(lastMCHEnrollmentEnc != null) {
                    Date dateReportedTested = null;
                    for (Obs obs : lastMCHEnrollmentEnc.getObs()) {
                        if (obs.getConcept().equals(dateReportedTestedHiv)) {
                            dateReportedTested = obs.getValueDatetime();
                        }
                    }
                    boolean patientHasPositiveReportedResult = lastMCHEnrollmentEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastMCHEnrollmentEnc, htsFinalTestQuestion, htsPositiveResult) : false;
                    // Reported positive at latest MCH enrollment
                    if (patientHasPositiveReportedResult) {
                        // 4. Reported positive at latest MCH enrollment and AGYW
                        if (patient.getAge() >= 10 && patient.getAge() <= 19) {
                            result = true;
                        }
                        // 4.1  Reported positive at latest MCH enrollment test done within last 30 days
                        if (dateReportedTested != null) {
                            mchReportedTestDiffDays = daysBetween(currentDate, dateReportedTested);
                            if (mchReportedTestDiffDays <= 30) {
                                result = true;
                            }
                        }
                    }
                }


                //Check Enrolled
                // 5. New Enrolled clients enrolled in the past one month
                if(inHivProgram.contains(ptId)) {
                    PatientProgram patientProgramHiv = EmrCalculationUtils.resultForPatient(inHivProgramResultMap, ptId);
                    Date hivEnrolmentDate = patientProgramHiv.getDateEnrolled();
                    hivEnrollmentDiffDays = daysBetween(currentDate, hivEnrolmentDate);
                    if (hivEnrollmentDiffDays <= 30) {
                        result = true;
                    }
                    // 6. All Enrolled on age group 10-19
                    if (patient.getAge() >= 10 && patient.getAge() <= 19) {
                        result = true;
                    }
                    // 7. All Enrolled with detectable VL > 200 copies/ml
                    if (vl != null && vl >= 200) {
                        result = true;
                    }
                    //8. All transfer in clients
                    EncounterService encounterService = Context.getEncounterService();
                    EncounterType hivEnrolmentEncounter = encounterService.getEncounterTypeByUuid(HivMetadata._EncounterType.HIV_ENROLLMENT);
                    Encounter lastHivEnrollmentEncounter = EmrUtils.lastEncounter(patientService.getPatient(ptId), hivEnrolmentEncounter);
                    boolean latestEnrollmentPatientTypeIsTI = lastHivEnrollmentEncounter != null ? EmrUtils.encounterThatPassCodedAnswer(lastHivEnrollmentEncounter, patientTypeQuestion, PatientTypeTIAnswer) : false;
                    if (latestEnrollmentPatientTypeIsTI) {
                        result = true;
                    }
                }
            }

            ret.put(ptId, new BooleanResult(result, this));
        }
        return ret;
    }

    private int daysBetween(Date date1, Date date2) {
        DateTime d1 = new DateTime(date1.getTime());
        DateTime d2 = new DateTime(date2.getTime());
        return Math.abs(Days.daysBetween(d1, d2).getDays());
    }
}

