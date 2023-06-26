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

import static org.openmrs.module.kenyaemr.calculation.library.mchcs.NeedsPcrTestCalculation.getAgeInWeeks;
import static org.openmrs.module.kenyaemrorderentry.util.Utils.getLatestObs;

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
        Set<Integer> pendingDNARapidTestResults = CalculationUtils.patientsThatPass(calculate(new PendingDNAPCRRapidTestResultCalculation(), cohort, context));
        CalculationResultMap lastPcrCWCTest = Calculations.lastObs(Dictionary.getConcept(Dictionary.EID_CWC_TEST), cohort, context);
        CalculationResultMap lastDNATestQualitative = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION_QUALITATIVE), cohort, context);
        CalculationResultMap lastABTest = Calculations.lastObs(Dictionary.getConcept(Dictionary.RAPID_HIV_CONFIRMATORY_TEST), cohort, context);
        Concept hivExposedUnknown = Dictionary.getConcept(Dictionary.UNKNOWN);
        Concept hivExposed = Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV);
        CalculationResultMap ret = new CalculationResultMap();
        OrderService orderService = Context.getOrderService();
        Concept NEGATIVE = Dictionary.getConcept(Dictionary.NEGATIVE);
        Concept EID_CWC_TEST = Dictionary.getConcept(Dictionary.EID_CWC_TEST);
        Concept AB_18_MONTHS = Dictionary.getConcept(Dictionary.RAPID_HIV_ANTIBODY_TEST_AT_18_MONTHS);
        Concept AB_6_MONTHS_AFTER_CESSATION_OF_BF = Dictionary.getConcept(Dictionary.AB_TEST_6_WEEKS_AFTER_CESSATION_OF_BREASTFEEDING);

        Concept INFANT_NOT_BREASTFEEDING = Dictionary.getConcept(Dictionary.INFANT_NOT_BREASTFEEDING);
        Concept HIV_NEG_NO_LONGER_AT_RISK = Dictionary.getConcept(Dictionary.HIV_NEG_NOT_AT_RISK);

        CalculationResultMap lastBreastFeedingStatus = Calculations.lastObs(Dictionary.getConcept(Dictionary.INFANT_FEEDING_METHOD), cohort, context);

        for (Integer ptId : cohort) {
            boolean needsAntibodyTest = false;
            Order labOrder;

            Person person = Context.getPersonService().getPerson(ptId);

            Obs latestBFStatus = EmrCalculationUtils.obsResultForPatient(lastBreastFeedingStatus, ptId);
            Obs cwcDNATestObs = EmrCalculationUtils.obsResultForPatient(lastPcrCWCTest, ptId);
            Obs dnaTestLab = EmrCalculationUtils.obsResultForPatient(lastDNATestQualitative, ptId);
            Obs rapidABTestLab = EmrCalculationUtils.obsResultForPatient(lastABTest, ptId);
            Date obsBFStatusDate = latestBFStatus != null && latestBFStatus.getValueCoded().equals(INFANT_NOT_BREASTFEEDING) ? latestBFStatus.getObsDatetime() : null;

            Integer ageInMonths = getAge(person.getBirthdate(), context.getNow());
            if (inMchcsProgram.contains(ptId) && !pendingDNARapidTestResults.contains(ptId) && !inHivProgram.contains(ptId) && ageInMonths <= 24) {

                Obs hivStatusObs = EmrCalculationUtils.obsResultForPatient(lastChildHivStatus, ptId);

                if (hivStatusObs != null && (hivStatusObs.getValueCoded().equals(hivExposedUnknown) || hivStatusObs.getValueCoded().equals(hivExposed))) {

                    Obs heiOutCome = getLatestObs((Patient) person, Dictionary.HEI_OUTCOME);

                    if (ageInMonths >= 18 && (heiOutCome == null || !heiOutCome.getValueCoded().equals(HIV_NEG_NO_LONGER_AT_RISK))) {

                        if (dnaTestLab != null) {

                            if (dnaTestLab.getValueCoded() == NEGATIVE) {

                                labOrder = rapidABTestLab != null ? rapidABTestLab.getOrder() : null;

                                if (labOrder != null) {

                                    Integer orderId = labOrder.getOrderId();
                                    Order order = orderService.getOrder(orderId);

                                    if (order.getOrderReason()!= null && !order.getOrderReason().equals(AB_18_MONTHS)) {

                                        needsAntibodyTest = true;
                                        flagMsg.append("Due for month-18 Rapid AB test ");
                                    } else if (obsBFStatusDate != null && getAgeInWeeks(obsBFStatusDate, context.getNow()) >= 6 && order.getOrderReason()!= null && !order.getOrderReason().equals(AB_6_MONTHS_AFTER_CESSATION_OF_BF)) {
                                        needsAntibodyTest = true;
                                        flagMsg.append("Due for week-6 Rapid AB test after cessation of breastfeeding");
                                    }
                                }
                                else {
                                    needsAntibodyTest = true;
                                    flagMsg.append("Due for month-18 Rapid AB test");
                                }
                            }
                        } else if (cwcDNATestObs != null) {
                            Encounter e = cwcDNATestObs.getEncounter();
                            boolean needsABTestAfterCessationOfBF = false;
                            boolean needsMonth18ABTest = false;
                            Set<Obs> o = e.getObs();
                            for (Obs obs : o) {
                                Concept dnaABTest = cwcDNATestObs.getValueCoded();
                                Concept obsTestReason = obs.getValueCoded();
                                Concept obsTest = obs.getConcept();

                                if (dnaABTest != null && obsTestReason != null && obsTest != null) {

                                    if (obs.getConcept().getConceptId().equals(EID_CWC_TEST.getConceptId()) && !obs.getValueCoded().equals(AB_18_MONTHS)) {
                                        needsMonth18ABTest = true;
                                        needsAntibodyTest = true;
                                    } else if (obsBFStatusDate != null && !obsTest.equals(AB_6_MONTHS_AFTER_CESSATION_OF_BF) && getAgeInWeeks(obsBFStatusDate, context.getNow()) >= 6) {
                                        needsABTestAfterCessationOfBF = true;
                                        needsAntibodyTest = true;
                                    }
                                }
                            }
                            if (needsABTestAfterCessationOfBF && needsMonth18ABTest) {
                                flagMsg.append("Due for week-6 Rapid AB test after cessation of breastfeeding");
                            }
                            else if(needsMonth18ABTest){
                                flagMsg.append("Due for month-18 Rapid AB test ");
                            }
                        }
                        else if (rapidABTestLab == null){
                            needsAntibodyTest = true;
                            flagMsg.append("Due for month-18 Rapid AB test");
                        }
                    }
                }
            }
            ret.put(ptId, new BooleanResult(needsAntibodyTest, this, context));

        }
        return ret;
    }

    Integer getAge(Date birtDate, Date context) {
        DateTime d1 = new DateTime(birtDate.getTime());
        DateTime d2 = new DateTime(context.getTime());
        return Months.monthsBetween(d1, d2).getMonths();
    }
}