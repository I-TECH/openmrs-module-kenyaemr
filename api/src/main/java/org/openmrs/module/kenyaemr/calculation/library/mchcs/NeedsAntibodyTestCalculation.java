/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.mchcs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.openmrs.*;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.*;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Determines whether a child at 9 months and above has had antibody test
 */
public class NeedsAntibodyTestCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {
    protected static final Log log = LogFactory.getLog(NeedsPcrTestCalculation.class);

    /**
     * @see org.openmrs.module.kenyacore.calculation.PatientFlagCalculation#getFlagMessage()
     */
    @Override
    public String getFlagMessage() {
        return flagMsg.toString();
    }

    StringBuilder flagMsg = new StringBuilder();

    /**
     * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
     */
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Program mchcsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHCS);
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

        // Get all patients who are alive and in MCH-CS program
        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inMchcsProgram = Filters.inProgram(mchcsProgram, alive, context);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);
        // Get whether the child is HIV Exposed
        CalculationResultMap lastChildHivStatus = Calculations.lastObs(Dictionary.getConcept(Dictionary.CHILDS_CURRENT_HIV_STATUS), cohort, context);
        CalculationResultMap lastHivRapidTest1 = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_RAPID_TEST_1_QUALITATIVE), cohort, context);
        Set<Integer> pendingDNARapidTestResults = CalculationUtils.patientsThatPass(calculate(new PendingDNAPCRRapidTestResultCalculation(), cohort, context));
        Concept hivExposedUnknown = Dictionary.getConcept(Dictionary.UNKNOWN);
        Concept hivExposed = Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV);
        CalculationResultMap ret = new CalculationResultMap();
        OrderService orderService = Context.getOrderService();
        Concept NEGATIVE = Dictionary.getConcept(Dictionary.NEGATIVE);
        Concept PCR_12_MONTHS = Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION);
        Concept AB_18_MONTHS = Dictionary.getConcept(Dictionary.RAPID_HIV_ANTIBODY_TEST_AT_18_MONTHS);
        Concept AB_6_MONTHS_AFTER_CESSATION_OF_BF = Dictionary.getConcept(Dictionary.AB_TEST_6_WEEKS_AFTER_CESSATION_OF_BREASTFEEDING);

        Concept INFANT_NOT_BREASTFEEDING = Dictionary.getConcept(Dictionary.INFANT_NOT_BREASTFEEDING);

        CalculationResultMap lastPcrTestQualitative = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION_QUALITATIVE), cohort, context);
        CalculationResultMap lastBreastFeedingStatus = Calculations.lastObs(Dictionary.getConcept(Dictionary.INFANT_FEEDING_METHOD), cohort, context);

        for (Integer ptId : cohort) {
            boolean needsAntibodyAt18Months = false;
            boolean needsAntibodyAfterBF = false;
            Order labOrder = new Order();

            Person person = Context.getPersonService().getPerson(ptId);

            Obs pcrTestObsQual = EmrCalculationUtils.obsResultForPatient(lastPcrTestQualitative, ptId);
            Obs latestBFStatus = EmrCalculationUtils.obsResultForPatient(lastBreastFeedingStatus, ptId);

            Date obsBFStatusDate = latestBFStatus != null && latestBFStatus.getValueCoded().equals(INFANT_NOT_BREASTFEEDING) ? latestBFStatus.getObsDatetime() : null;

            Integer ageInMonths = getAge(person.getBirthdate(), context.getNow());
            if (inMchcsProgram.contains(ptId) && !pendingDNARapidTestResults.contains(ptId) && !inHivProgram.contains(ptId) && ageInMonths <=24) {

                Obs hivStatusObs = EmrCalculationUtils.obsResultForPatient(lastChildHivStatus, ptId);

                if(hivStatusObs!= null && (hivStatusObs.getValueCoded().equals(hivExposedUnknown) || hivStatusObs.getValueCoded().equals(hivExposed))){
                    if(pcrTestObsQual != null ){
                        labOrder = pcrTestObsQual.getOrder();
                        if(labOrder!= null){
                            Integer orderId = labOrder.getOrderId();
                            Order order = orderService.getOrder(orderId);
                            if(pcrTestObsQual.getValueCoded() == NEGATIVE && !order.getOrderReason().equals(AB_18_MONTHS) && order.getOrderReason().equals(PCR_12_MONTHS)){
                                needsAntibodyAt18Months = true;
                            }
                        }
                     else{
                            Encounter e = pcrTestObsQual.getEncounter();
                            Set<Obs> o = e.getObs();
                            for(Obs obs : o) {

                                Concept pcrTest = pcrTestObsQual.getValueCoded();
                                Concept obsTestReason = obs.getValueCoded();
                                Concept obsTest = obs.getConcept();

                                if (pcrTest != null && obsTestReason != null && obsTest != null) {
                                    if (pcrTestObsQual.getValueCoded() == NEGATIVE && obs.getConcept().getConceptId() == 164959 && !obs.getValueCoded().equals(AB_18_MONTHS) && obs.getValueCoded().equals(PCR_12_MONTHS) && ageInMonths >= 18) {
                                        needsAntibodyAt18Months = true;
                                    }
                                }
                            }
                        }
                       // if(needsAntibodyAt18Months)
                         //   flagMsg.append("Due for month-18 Rapid AB test ");
                    }
                }

                if(hivStatusObs!= null && (hivStatusObs.getValueCoded().equals(hivExposedUnknown) || hivStatusObs.getValueCoded().equals(hivExposed))){
                    if(pcrTestObsQual != null){
                        labOrder = pcrTestObsQual.getOrder();
                        if(labOrder!= null){
                            Integer orderId = labOrder.getOrderId();
                            Order order = orderService.getOrder(orderId);
                            if(pcrTestObsQual.getValueCoded() == NEGATIVE && order.getOrderReason().equals(AB_18_MONTHS) && !order.getOrderReason().equals(AB_6_MONTHS_AFTER_CESSATION_OF_BF) && getAge(person.getBirthdate(), context.getNow()) >= 18){
                                needsAntibodyAfterBF = true;
                            }
                        }
                        else{
                            Encounter e = pcrTestObsQual.getEncounter();
                            Set<Obs> o = e.getObs();
                            for(Obs obs : o){

                               Concept pcrTest = pcrTestObsQual.getValueCoded();
                               Concept obsTestReason = obs.getValueCoded();
                               Concept obsTest = obs.getConcept();

                                if (obsBFStatusDate != null & pcrTest != null && obsTestReason != null && obsTest != null) {
                                    if (pcrTest.equals(NEGATIVE) && obsTestReason.equals(AB_18_MONTHS) && !obsTest.equals(AB_6_MONTHS_AFTER_CESSATION_OF_BF) && NeedsPcrTestCalculation.getAgeInWeeks(obsBFStatusDate, context.getNow()) >= 6)
                                        needsAntibodyAfterBF = true;
                                }
                            }
                        }
                        if (needsAntibodyAfterBF)
                            flagMsg.append("Due for week-6 Rapid AB test after cessation of breastfeeding");
                    }
                }
            }
         //   ret.put(ptId, new BooleanResult(needsAntibody, this, context));

        }
        return ret;
    }

    Integer getAge(Date birtDate, Date context) {
        DateTime d1 = new DateTime(birtDate.getTime());
        DateTime d2 = new DateTime(context.getTime());
        return Months.monthsBetween(d1, d2).getMonths();
    }
}