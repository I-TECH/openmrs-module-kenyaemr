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
import org.joda.time.Weeks;
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
 * Determines whether a child at 6 week and above has had PCR test
 */
public class NeedsPcrTestCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {
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
        CalculationResultMap lastPcrTestQualitative = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION_QUALITATIVE), cohort, context);
        Set<Integer> pendingDNARapidTestResults = CalculationUtils.patientsThatPass(calculate(new PendingDNAPCRRapidTestResultCalculation(), cohort, context));

        Concept NEGATIVE = Dictionary.getConcept(Dictionary.NEGATIVE);
        Concept hivExposed = Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV);
        Concept PCR_6_MONTHS = Dictionary.getConcept(Dictionary.HIV_RAPID_TEST_2_QUALITATIVE);
        Concept PCR_12_MONTHS = Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION);
        Concept EID_CWC_TEST = Dictionary.getConcept(Dictionary.EID_CWC_TEST);
        Concept MONTH_SIX_FOLLOWUP = Dictionary.getConcept(Dictionary.MONTH_SIX_FOLLOWUP);
        Concept MONTH_12_FOLLOWUP = Dictionary.getConcept(Dictionary.MONTH_12_FOLLOWUP);

        CalculationResultMap ret = new CalculationResultMap();

        OrderService orderService = Context.getOrderService();

        for (Integer ptId : cohort) {

            boolean needsPcr = false;
            Person person = Context.getPersonService().getPerson(ptId);

            // Check if a patient is alive and is in MCHCS program
            if (inMchcsProgram.contains(ptId) && !pendingDNARapidTestResults.contains(ptId) && !inHivProgram.contains(ptId) && getAgeInMonths(person.getBirthdate(), context.getNow()) <= 24) {

                Obs hivStatusObs = EmrCalculationUtils.obsResultForPatient(lastChildHivStatus, ptId);

                if (hivStatusObs != null && hivStatusObs.getValueCoded().equals(hivExposed)) {

                    Integer ageInWeeks = getAgeInWeeks(person.getBirthdate(), context.getNow());
                    Integer ageInMonths = getAgeInMonths(person.getBirthdate(), context.getNow());

                    Obs pcrTestObsQual = EmrCalculationUtils.obsResultForPatient(lastPcrTestQualitative, ptId);

                    Order labOrder;
                    if (ageInWeeks >= 6 && ageInMonths < 6) {

                        if (pcrTestObsQual == null) {
                            needsPcr = true;
                            flagMsg.append("Due for week-6 PCR test");
                        }
                    } else if (ageInMonths >= 6 && ageInMonths < 12) {

                        if (pcrTestObsQual != null) {
                            if (pcrTestObsQual.getValueCoded() == NEGATIVE) {
                                labOrder = pcrTestObsQual.getOrder();
                                if (labOrder != null) {
                                    Integer orderId = labOrder.getOrderId();
                                    Order order = orderService.getOrder(orderId);

                                    if (order.getOrderReason()!= null && !order.getOrderReason().equals(PCR_6_MONTHS)) {

                                        needsPcr = true;
                                    }
                                } else {
                                    Encounter e = pcrTestObsQual.getEncounter();
                                    Set<Obs> o = e.getObs();
                                    for (Obs obs : o) {

                                        Concept pcrTest = pcrTestObsQual.getValueCoded();
                                        Concept obsTestReason = obs.getValueCoded();
                                        Concept obsTest = obs.getConcept();

                                        if (pcrTest != null && obsTestReason != null && obsTest != null) {

                                            if (pcrTest == NEGATIVE && obsTest.getConceptId().equals(EID_CWC_TEST.getConceptId()) && !obsTestReason.getConceptId().equals(MONTH_SIX_FOLLOWUP.getConceptId())) {

                                                needsPcr = true;
                                            }
                                        }
                                    }
                                }
                                if (needsPcr)

                                    flagMsg.append("Due for month-6 PCR test ");
                            }
                        } else {
                            needsPcr = true;
                            flagMsg.append("Due for month-6 PCR test ");
                        }
                    } else if (ageInMonths >= 12 && ageInMonths < 18) {

                        if (pcrTestObsQual != null) {
                            if (pcrTestObsQual.getValueCoded() == NEGATIVE) {
                                labOrder = pcrTestObsQual.getOrder();

                                if (labOrder != null) {
                                    Integer orderId = labOrder.getOrderId();
                                    Order order = orderService.getOrder(orderId);

                                    if (order.getOrderReason()!= null && !order.getOrderReason().equals(PCR_12_MONTHS)) {

                                        needsPcr = true;
                                    }
                                } else {
                                    Encounter e = pcrTestObsQual.getEncounter();
                                    Set<Obs> o = e.getObs();
                                    for (Obs obs : o) {
                                        Concept pcrTest = pcrTestObsQual.getValueCoded();
                                        Concept obsTestReason = obs.getValueCoded();
                                        Concept obsTest = obs.getConcept();
                                        if (pcrTest != null && obsTestReason != null && obsTest != null) {

                                            if (pcrTest.equals(NEGATIVE) && obsTest.getConceptId().equals(EID_CWC_TEST.getConceptId()) && !obsTestReason.getConceptId().equals(MONTH_12_FOLLOWUP.getConceptId())) {
                                                needsPcr = true;
                                            }
                                        }
                                    }
                                }
                                if (needsPcr)

                                    flagMsg.append("Due for month-12 PCR test ");
                            }
                        } else {
                            needsPcr = true;
                            flagMsg.append("Due for month-12 PCR test ");
                        }
                    }
                }
            }

            ret.put(ptId, new BooleanResult(needsPcr, this, context));
        }

        return ret;
    }

    public static Integer getAgeInWeeks(Date birtDate, Date context) {
        DateTime d1 = new DateTime(birtDate.getTime());
        DateTime d2 = new DateTime(context.getTime());
        return Weeks.weeksBetween(d1, d2).getWeeks();
    }

    Integer getAgeInMonths(Date birtDate, Date context) {
        DateTime d1 = new DateTime(birtDate.getTime());
        DateTime d2 = new DateTime(context.getTime());
        return Months.monthsBetween(d1, d2).getMonths();
    }
}