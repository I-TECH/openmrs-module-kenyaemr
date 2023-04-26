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

    StringBuilder flagMsg = new StringBuilder("");

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
        CalculationResultMap lastPcrCWCTest = Calculations.lastObs(Dictionary.getConcept(Dictionary.EID_CWC_TEST), cohort, context);
        CalculationResultMap lastDNATestQualitative = Calculations.lastObs(Dictionary.getConcept(Dictionary.RAPID_HIV_CONFIRMATORY_TEST), cohort, context);
        Set<Integer> pendingDNARapidTestResults = CalculationUtils.patientsThatPass(calculate(new PendingDNAPCRRapidTestResultCalculation(), cohort, context));
        CalculationResultMap lastBreastFeedingStatus = Calculations.lastObs(Dictionary.getConcept(Dictionary.INFANT_FEEDING_METHOD), cohort, context);

        Concept NEGATIVE = Dictionary.getConcept(Dictionary.NEGATIVE);
        Concept hivExposed = Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV);
        Concept PCR_6_WEEKS = Dictionary.getConcept(Dictionary.HIV_RAPID_TEST_1_QUALITATIVE);
        Concept PCR_6_MONTHS = Dictionary.getConcept(Dictionary.HIV_RAPID_TEST_2_QUALITATIVE);
        Concept PCR_12_MONTHS = Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION);
        Concept INFANT_NOT_BREASTFEEDING = Dictionary.getConcept(Dictionary.INFANT_NOT_BREASTFEEDING);
        Concept AB_18_MONTHS = Dictionary.getConcept(Dictionary.RAPID_HIV_ANTIBODY_TEST_AT_18_MONTHS);
        Concept AB_6_MONTHS_AFTER_CESSATION_OF_BF = Dictionary.getConcept(Dictionary.AB_TEST_6_WEEKS_AFTER_CESSATION_OF_BREASTFEEDING);

        CalculationResultMap ret = new CalculationResultMap();

        OrderService orderService = Context.getOrderService();

        for (Integer ptId : cohort) {

            boolean needsPcr = false;
            Person person = Context.getPersonService().getPerson(ptId);

            // Check if a patient is alive and is in MCHCS program
            if (inMchcsProgram.contains(ptId) && !pendingDNARapidTestResults.contains(ptId) && !inHivProgram.contains(ptId) && getAgeInMonths(person.getBirthdate(), context.getNow()) <= 24) {

                Obs hivStatusObs = EmrCalculationUtils.obsResultForPatient(lastChildHivStatus, ptId);

                System.out.println("-------------I am here----------line 89");
                if (hivStatusObs != null && hivStatusObs.getValueCoded().equals(hivExposed)) {
                    System.out.println("-------------I am here----------line 91");

                    Integer ageInWeeks = getAgeInWeeks(person.getBirthdate(), context.getNow());
                    Integer ageInMonths = getAgeInMonths(person.getBirthdate(), context.getNow());
                    System.out.println("++++++Patient:" + person.getGivenName() + " +++++Age in Months:" + ageInMonths);
                    Obs pcrTestObsQual = EmrCalculationUtils.obsResultForPatient(lastPcrTestQualitative, ptId);
                    Obs cwcDNATestObs = EmrCalculationUtils.obsResultForPatient(lastPcrCWCTest, ptId);
                    Obs dnaTestLab = EmrCalculationUtils.obsResultForPatient(lastDNATestQualitative, ptId);
                    Obs latestBFStatus = EmrCalculationUtils.obsResultForPatient(lastBreastFeedingStatus, ptId);

                    Date obsBFStatusDate = latestBFStatus != null && latestBFStatus.getValueCoded().equals(INFANT_NOT_BREASTFEEDING) ? latestBFStatus.getObsDatetime() : null;

                    Order labOrder;
                    if (ageInWeeks >= 6 && ageInMonths < 6) {
                        System.out.println("-------------I am here----------line 94");
                        if (pcrTestObsQual == null) {
                            needsPcr = true;
                            flagMsg.append("Due for week-6 PCR test");
                        }
                    } else if (ageInMonths >= 6 && ageInMonths < 12) {
                        System.out.println("I am here??? line 104");
                        // if (pcrTestObsQual != null) {
                        if (pcrTestObsQual != null) {
                            if (pcrTestObsQual.getValueCoded() == NEGATIVE) {
                                labOrder = pcrTestObsQual.getOrder();
                                if (labOrder != null) {
                                    Integer orderId = labOrder.getOrderId();
                                    Order order = orderService.getOrder(orderId);
                                    System.out.println("Before++++++++++++++++++++++++");
                                    System.out.println("order.getOrderReason().getConceptId()++++++++++++++++++++++++" + order.getOrderReason().getConceptId());
                                    System.out.println("order.getOrderReason().getConceptId()++++++++++++++++++++++++" + order.getOrderReason().getConceptId());
                                    if (!order.getOrderReason().equals(PCR_6_MONTHS) /*&& order.getOrderReason().equals(PCR_6_WEEKS)*/) {
                                        System.out.println("line 115++++++++++++++++++++++++");
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
                                            System.out.println("******obsTest.getConceptId():" + obsTest.getConceptId());
                                            System.out.println(";;;;;;;;;;;;;;;obsTestReason.getConceptId():" + obsTestReason.getConceptId());
                                            if (pcrTest == NEGATIVE && obsTest.getConceptId() == 164959 && obsTestReason.getConceptId() != 167015 /*&& obsTestReason.getConceptId() == 844*/) {
                                                System.out.println("line 131============================");
                                                needsPcr = true;
                                            }
                                        }
                                    }
                                }
                                if (needsPcr)
                                    System.out.println("line 138============================");
                                flagMsg.append("Due for month-6 PCR test ");
                            }
                        } else {
                            needsPcr = true;
                            flagMsg.append("everything else Due for month-6 PCR test ");
                        }
                    } else if (ageInMonths >= 12 && ageInMonths < 18) {
                        //System.out.println("???????????pcrTestObsQual.getId:" + pcrTestObsQual.getId());
                        // if (pcrTestObsQual != null) {
                        if (pcrTestObsQual != null) {
                            if (pcrTestObsQual.getValueCoded() == NEGATIVE) {
                                labOrder = pcrTestObsQual.getOrder();
                                System.out.println("++++Line 143");
                                // System.out.println("++++lab order.getOrderId" + labOrder.getOrderId());
                                if (labOrder != null) {
                                    Integer orderId = labOrder.getOrderId();
                                    Order order = orderService.getOrder(orderId);
                                    System.out.println("//////////////////////////////order.getOrderReason().equals(PCR_6_MONTHS):" + order.getOrderReason().getConceptId());
                                    System.out.println("//////////////////////////////pcrTestObsQual.getValueCoded():" + pcrTestObsQual.getValueCoded());
                                    if (!order.getOrderReason().equals(PCR_12_MONTHS)/* && order.getOrderReason().equals(PCR_6_MONTHS)*/) {
                                        System.out.println("||||||||||Line 151");
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
                                            System.out.println("++Line 163++++++++++obsTest.getConceptId():" + obsTest.getConceptId());
                                            System.out.println("++Line 164++++++++++obsTestReason.getConceptId():" + obsTestReason.getConceptId());
                                            if (pcrTest.equals(NEGATIVE) && obsTest.getConceptId() == 164959 && obsTestReason.getConceptId() != 165389/* && obsTestReason.getConceptId() == 167015*/) {
                                                needsPcr = true;
                                            }
                                        }
                                    }
                                }
                                if (needsPcr)
                                    System.out.println("pppppppppppppppppppppLine 172");
                                flagMsg.append("Due for month-12 PCR test ");
                            }
                        } else {
                            needsPcr = true;
                            flagMsg.append("everything else Due for month-12 PCR test ");
                        }
                    } else if (ageInMonths >= 18) {
                        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaa we are at line 198");
                        if (dnaTestLab != null) {
                            System.out.println("Line 200???????? Person " + person.getGivenName());
                            if (dnaTestLab.getValueCoded() == NEGATIVE) {
                                labOrder = dnaTestLab.getOrder();
                                System.out.println("Line 203??????????Lab order:" + labOrder + " Person " + person.getGivenName());
                                if (labOrder != null) {
                                    System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaa we are at line 205");
                                    Integer orderId = labOrder.getOrderId();
                                    Order order = orderService.getOrder(orderId);
                                    System.out.println("------------We are here 190:order.getOrderReason().getConceptId" + order.getOrderReason().getConceptId());
                                    if (!order.getOrderReason().equals(AB_18_MONTHS)/* && order.getOrderReason().equals(PCR_12_MONTHS)*/) {
                                        System.out.println("+++++We are here --line 191");
                                        needsPcr = true;
                                        flagMsg.append("-+-Due for month-18 Rapid AB test ");
                                    } else if (obsBFStatusDate != null && getAgeInWeeks(obsBFStatusDate, context.getNow()) >= 6 && /*pcrTestObsQual.getValueCoded() == NEGATIVE && order.getOrderReason().equals(AB_18_MONTHS) && */!order.getOrderReason().equals(AB_6_MONTHS_AFTER_CESSATION_OF_BF)) {
                                        needsPcr = true;
                                        flagMsg.append("--Due for week-6 Rapid AB test after cessation of breastfeeding");
                                    }
                                }
                            }
                        } else if (cwcDNATestObs != null) {
                            Encounter e = cwcDNATestObs.getEncounter();
                            Set<Obs> o = e.getObs();
                            for (Obs obs : o) {

                                Concept dnaABTest = cwcDNATestObs.getValueCoded();
                                Concept obsTestReason = obs.getValueCoded();
                                Concept obsTest = obs.getConcept();

                                if (dnaABTest != null && obsTestReason != null && obsTest != null) {
                                    System.out.println("Line 205:????????????????obs.getConcept().getConceptId():" + obs.getConcept().getConceptId());
                                    System.out.println("Line 206:????????????????obs.getValueCoded().getConceptId:" + obs.getValueCoded().getConceptId());
                                    if (obs.getConcept().getConceptId() == 164959 && !obs.getValueCoded().equals(AB_18_MONTHS)/* && obs.getValueCoded().equals(PCR_12_MONTHS)*/) {
                                        System.out.println("+++++We are here --line 208");
                                        needsPcr = true;
                                        flagMsg.append("-+-Due for month-18 Rapid AB test ");
                                    } else if (obsBFStatusDate != null && !obsTest.equals(AB_6_MONTHS_AFTER_CESSATION_OF_BF) && getAgeInWeeks(obsBFStatusDate, context.getNow()) >= 6) {
                                        needsPcr = true;
                                        flagMsg.append("--Due for week-6 Rapid AB test after cessation of breastfeeding");
                                    }
                                }
                            }
                        } else {
                            needsPcr = true;
                            flagMsg.append("ever thing else Due for month-18 Rapid AB test ");
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