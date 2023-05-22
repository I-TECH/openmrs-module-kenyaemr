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
import org.openmrs.*;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.*;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.*;

public class DualHIVSyphilisCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {
    private String dualMessage;
    private Date currentDate = new Date();
    private Date lmpDate = null;
    private Date syphilisTestDate = null;
    private Date hivTestDate = null;
    private Date hepatitisTestDate = null;
    private int hepBTestMonths;
    private int syphilisTestMonths;
    private int hivTestMonths;


    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> map, PatientCalculationContext context) {
        Set<Integer> aliveAndFemale = Filters.female(Filters.alive(cohort, context), context);
        Concept yes = Dictionary.getConcept(Dictionary.YES);
        CalculationResultMap pregStatusObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.PREGNANCY_STATUS), aliveAndFemale, context);
        CalculationResultMap ret = new CalculationResultMap();
        CalculationResultMap lmp = Calculations.lastObs(Dictionary.getConcept(Dictionary.LAST_MONTHLY_PERIOD), aliveAndFemale, context);
        ConceptService cs = Context.getConceptService();
        Concept hepatitisB = cs.getConcept(165040);
        Concept negative = Dictionary.getConcept(Dictionary.NEGATIVE);
        Concept syphilisQ = cs.getConcept(299);
        Concept syphilisNegative = cs.getConcept(1299);
        CalculationResultMap resultOfHivTesting = Calculations.lastObs(Dictionary.getConcept(Dictionary.RESULT_OF_HIV_TEST), aliveAndFemale, context);

        CalculationResultMap lastHepatitisTesting = Calculations.lastObs(hepatitisB, aliveAndFemale, context);
        CalculationResultMap lastSyphilisTesting = Calculations.lastObs(syphilisQ, aliveAndFemale, context);

        for (Integer ptId : aliveAndFemale) {
            boolean eligibleDualHivSyphilisFlag = false;
            PatientService patientService = Context.getPatientService();
            Patient patient = patientService.getPatient(ptId);

            Form antenatalVisitForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_ANTENATAL_VISIT);
            EncounterType mchConsultationEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION);
            Encounter lastANCEnc = EmrUtils.lastEncounter(patient,mchConsultationEncounterType,antenatalVisitForm );
            Obs pregStatusObs = EmrCalculationUtils.obsResultForPatient(pregStatusObss, ptId);

            Obs hepBObs = EmrCalculationUtils.obsResultForPatient(lastHepatitisTesting, ptId);
            Obs syphilsObs = EmrCalculationUtils.obsResultForPatient(lastSyphilisTesting, ptId);
            Obs resultOfHivTestingObs = EmrCalculationUtils.obsResultForPatient(resultOfHivTesting, ptId);
            Obs lmpObs = EmrCalculationUtils.obsResultForPatient(lmp, ptId);

            lmpDate = lmpObs.getObsDatetime();

            if(hepBObs !=null){
                if (hepBObs.getObsDatetime() !=null){
                    hepatitisTestDate = hepBObs.getObsDatetime();
                    hepBTestMonths = monthsBetween(hepatitisTestDate, lmpDate);
                }
            }
            if (syphilsObs != null) {
                if (syphilsObs.getObsDatetime() != null) {
                    syphilisTestDate = syphilsObs.getObsDatetime();
                    syphilisTestMonths = monthsBetween(syphilisTestDate, lmpDate);
                }
            }

            if(resultOfHivTestingObs !=null) {
                if (resultOfHivTestingObs.getObsDatetime() != null) {
                    hivTestDate = resultOfHivTestingObs.getObsDatetime();
                    hivTestMonths = monthsBetween(hivTestDate, lmpDate);

                }
            }

            int pregnancyMonths = monthsBetween(currentDate, lmpDate);
            int monthsSinceFirstTrimester = monthsBetween(lmpDate, currentDate);
            boolean syphilisQuestion = lastANCEnc != null ? EmrUtils.encounterThatPassCodedAnswer(lastANCEnc, syphilisQ, syphilisNegative) : false;

            if(pregStatusObs != null && pregStatusObs.getValueCoded().equals(yes)){
               if(lastANCEnc !=null) {

                   if (pregnancyMonths >= 1 && pregnancyMonths <= 3) {
                       if (hepBObs ==null && (hepBTestMonths < 1 || hepBTestMonths > 3)) {
                           eligibleDualHivSyphilisFlag = true;
                        dualMessage = "Due for HepB";
                       }
                       if (syphilsObs ==null && (syphilisTestMonths < 1 || syphilisTestMonths > 3)) {
                           eligibleDualHivSyphilisFlag = true;
                           dualMessage = "Due for Syphils";
                       }
                       if (resultOfHivTestingObs ==null && (hivTestMonths < 1 || hivTestMonths > 3)) {
                           eligibleDualHivSyphilisFlag = true;
                           dualMessage = "Due for Hiv Test";
                       }
                   }
                if (pregnancyMonths >= 7 && pregnancyMonths <= 9){
                       if(resultOfHivTestingObs != null && syphilisQuestion &&  resultOfHivTestingObs.getValueCoded().equals(negative)  && monthsSinceFirstTrimester > 3) {
                             eligibleDualHivSyphilisFlag = true;
                             dualMessage = "Due for Hiv-Syphilis";
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
        return dualMessage;
    }
}
