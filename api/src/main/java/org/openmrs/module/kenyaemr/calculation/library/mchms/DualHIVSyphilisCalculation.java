/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.mchms;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
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

public class DualHIVSyphilisCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {
    StringBuilder dualMessage = new StringBuilder();

    private Date currentDate = new Date();
    private Date lmpDate = null;
    private Date syphilisTestDate = null;
    private Date vdrlTestDate = null;
    private Date hivTestDate = null;
    private Date hepatitisTestDate = null;
    private Date hepBAntigenTestDate = null;
    private int hepBTestMonths;
    private int hepBAntigenTestMonths;
    private int syphilisTestMonths;
    private int vdrlTestMonths;
    private int hivTestMonths;
    boolean patientHasNegativeTestResult = false;

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> map, PatientCalculationContext context) {
        Set<Integer> aliveAndFemale = Filters.female(Filters.alive(cohort, context), context);
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, aliveAndFemale, context);
        Set<Integer> inMchmsProgram = Filters.inProgram(mchmsProgram, aliveAndFemale, context);
        ConceptService cs = Context.getConceptService();
        Concept negative = Dictionary.getConcept(Dictionary.NEGATIVE);
        Concept positive = Dictionary.getConcept(Dictionary.POSITIVE);
        Integer pregnancyTestConcept = 45;
        CalculationResultMap pregnancyTest = Calculations.lastObs(cs.getConcept(pregnancyTestConcept), aliveAndFemale, context);
        CalculationResultMap ret = new CalculationResultMap();
        CalculationResultMap lmp = Calculations.lastObs(Dictionary.getConcept(Dictionary.LAST_MONTHLY_PERIOD), aliveAndFemale, context);

        Concept hepatitisB = cs.getConcept(165040);
        Concept hepatitiBAntigen = cs.getConcept(159430);

        Concept syphilisQ = cs.getConcept(299);
        Concept vdrlQ = cs.getConcept(1029);
        Concept syphilisNegative = cs.getConcept(1299);
        Concept vdrlNegative = cs.getConcept(664);
        Concept htsFinalTestQuestion = cs.getConcept(HtsConstants.HTS_FINAL_TEST_CONCEPT_ID);
        Concept htsNegativeResult = cs.getConcept(HtsConstants.HTS_NEGATIVE_RESULT_CONCEPT_ID);

        CalculationResultMap lastHepatitisTesting = Calculations.lastObs(hepatitisB, aliveAndFemale, context);
        CalculationResultMap lastHepatitisAntigenTesting = Calculations.lastObs(hepatitiBAntigen, aliveAndFemale, context);
        CalculationResultMap lastSyphilisTesting = Calculations.lastObs(syphilisQ, aliveAndFemale, context);
        CalculationResultMap lastVdrlTesting = Calculations.lastObs(vdrlQ, aliveAndFemale, context);

        for (Integer ptId : aliveAndFemale) {
            boolean eligibleDualHivSyphilisFlag = false;
            Obs pregTestObs = EmrCalculationUtils.obsResultForPatient(pregnancyTest, ptId);
            Obs lmpObs = EmrCalculationUtils.obsResultForPatient(lmp, ptId);
            PatientService patientService = Context.getPatientService();
            Patient patient = patientService.getPatient(ptId);
                    //Either enrolled in MCH or Tested Positive
           if((!inHivProgram.contains(ptId) && lmpObs != null) && (inMchmsProgram.contains(ptId) || (pregTestObs != null && pregTestObs.getValueCoded().equals(positive)))){
                lmpDate = lmpObs.getValueDate();
                Obs hepBObs = EmrCalculationUtils.obsResultForPatient(lastHepatitisTesting, ptId);
                Obs hepBAntigenObs = EmrCalculationUtils.obsResultForPatient(lastHepatitisAntigenTesting, ptId);
                Obs syphilsObs = EmrCalculationUtils.obsResultForPatient(lastSyphilisTesting, ptId);
                Obs vdrlObs = EmrCalculationUtils.obsResultForPatient(lastVdrlTesting, ptId);
                  //Helper functions
                                        if(hepBObs !=null){
                                            if (hepBObs.getObsDatetime() != null){
                                                hepatitisTestDate = hepBObs.getObsDatetime();
                                                hepBTestMonths = monthsBetween(hepatitisTestDate, lmpDate);
                                            }
                                        }
                                       if(hepBAntigenObs !=null){
                                           if (hepBAntigenObs.getObsDatetime() != null){
                                               hepBAntigenTestDate = hepBAntigenObs.getObsDatetime();
                                               hepBAntigenTestMonths = monthsBetween(hepBAntigenTestDate, lmpDate);
                                           }
                                       }
                                        if (syphilsObs != null) {
                                            if (syphilsObs.getObsDatetime() != null) {
                                                syphilisTestDate = syphilsObs.getObsDatetime();
                                                syphilisTestMonths = monthsBetween(syphilisTestDate, lmpDate);
                                            }
                                        }
                                       if (vdrlObs != null) {
                                           if (vdrlObs.getObsDatetime() != null) {
                                               vdrlTestDate = vdrlObs.getObsDatetime();
                                               vdrlTestMonths = monthsBetween(vdrlTestDate, lmpDate);
                                           }
                                       }

                               // Check  Tested HIV- clients in HTS module
                               Form antenatalVisitForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_ANTENATAL_VISIT);
                               EncounterType mchConsultationEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION);
                               Encounter lastANCHtsEnc = EmrUtils.lastEncounter(patient,mchConsultationEncounterType,antenatalVisitForm );
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
                       // Check  Tested HIV- clients in MCH - ANC module
                              boolean patientHasNegativeTestHivAncResult = lastANCHtsEnc != null && EmrUtils.encounterThatPassCodedAnswer(lastANCHtsEnc, htsFinalTestQuestion, htsNegativeResult);
                                 if(patientHasNegativeTestHivAncResult ) {
                                     if(lastHtsInitialEnc != null) {
                                         if (lastANCHtsEnc.getEncounterDatetime().after(lastHtsInitialEnc.getEncounterDatetime())) {
                                             lastHtsEnc = lastANCHtsEnc;
                                         }
                                     }
                                         if (lastHtsRetestEnc != null) {
                                             if (lastANCHtsEnc.getEncounterDatetime().after(lastHtsRetestEnc.getEncounterDatetime())) {
                                                 lastHtsEnc = lastANCHtsEnc;
                                             }
                                         }
                                     }

                           if(lastHtsEnc !=null) {
                                patientHasNegativeTestResult = EmrUtils.encounterThatPassCodedAnswer(lastHtsEnc, htsFinalTestQuestion, htsNegativeResult);
                               if (lastHtsEnc.getEncounterDatetime() != null) {
                                   hivTestDate = lastHtsEnc.getEncounterDatetime();
                                   hivTestMonths = monthsBetween(hivTestDate, lmpDate);
                               }
                           }
               int pregnancyMonths = monthsBetween(currentDate, lmpDate);
                   if (pregnancyMonths >= 1 && pregnancyMonths <= 3) {
                               if ((hepBObs == null && hepBAntigenObs == null) || (hepBObs != null && hepBTestMonths > 3) || (hepBAntigenObs != null && hepBAntigenTestMonths > 3)) {
                                   eligibleDualHivSyphilisFlag = true;
                                   dualMessage.append("Due for HepB Test");
                               }
                              if ((syphilsObs == null && vdrlObs == null)  || (syphilsObs != null && syphilisTestMonths > 3) ||(vdrlObs != null && vdrlTestMonths > 3)) {
                                   eligibleDualHivSyphilisFlag = true;
                                   if(dualMessage.length() == 0){
                                       dualMessage.append("Due for Syphilis Test");
                                   } else{
                                       dualMessage.append(", ").append("Due for Syphilis Test");
                                   }
                               }
                               if (lastHtsEnc == null || (lastHtsEnc != null && hivTestMonths > 3)) {
                                   eligibleDualHivSyphilisFlag = true;
                                   if(dualMessage.length() == 0){
                                       dualMessage.append("Due for Hiv Test");
                                   }else{
                                       dualMessage.append(", ").append("Due for Hiv Test");
                                   }
                                  }
                               }

                        if (pregnancyMonths >= 7 && pregnancyMonths <= 9){
                            if ((syphilsObs == null && vdrlObs == null) || (syphilsObs != null && syphilisTestDate.after(lmpDate) && syphilsObs.getValueCoded().equals(syphilisNegative)) ||
                                     (vdrlObs != null && vdrlTestDate.after(lmpDate) && vdrlObs.getValueCoded().equals(vdrlNegative))){
                                eligibleDualHivSyphilisFlag = true;
                                dualMessage.append("Due for Syphilis Test");
                            }

                         if(lastHtsEnc == null  || (lastHtsEnc != null && hivTestDate.after(lmpDate) && patientHasNegativeTestResult)) {
                               eligibleDualHivSyphilisFlag = true;
                               if(dualMessage.length() > 0){
                                 dualMessage.append(", ").append("Due for HIV test");
                               }else{
                                 dualMessage.append("Due for HIV test");
                               }
                           }
                         }
                   }

            ret.put(ptId, new BooleanResult(eligibleDualHivSyphilisFlag, this));
        }
        return ret;
    }
    private int monthsBetween(Date d1, Date d2) {
        DateTime dateTime1 = new DateTime(d1.getTime());
        DateTime dateTime2 = new DateTime(d2.getTime());
        return Math.abs(Months.monthsBetween(dateTime1, dateTime2).getMonths());
    }
    @Override
    public String getFlagMessage() {
        return dualMessage.toString();
    }
}
