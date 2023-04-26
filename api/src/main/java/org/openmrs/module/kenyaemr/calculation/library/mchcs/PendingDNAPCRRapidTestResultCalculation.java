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
import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Program;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by schege on 14/03/2023.
 */
public class PendingDNAPCRRapidTestResultCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

    /**
     * @see PatientFlagCalculation#getFlagMessage()
     */
    @Override
    public String getFlagMessage() {
        return msgFlag.toString();
    }

    StringBuilder msgFlag = new StringBuilder();

    protected static final Log log = LogFactory.getLog(PendingDNAPCRRapidTestResultCalculation.class);

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        Program heiProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHCS);
        OrderService orderService = Context.getOrderService();
        String TEST_ORDER_TYPE_UUID = "52a447d3-a64a-11e3-9aeb-50e549534c5e";

        Concept PCR_6_WEEKS = Dictionary.getConcept(Dictionary.HIV_RAPID_TEST_1_QUALITATIVE);
        Concept PCR_6_MONTHS = Dictionary.getConcept(Dictionary.HIV_RAPID_TEST_2_QUALITATIVE);
        Concept PCR_12_MONTHS = Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION);
        Concept AB_TEST_6_WEEKS_AFTER_CESSATION_OF_BREASTFEEDING = Dictionary.getConcept(Dictionary.AB_TEST_6_WEEKS_AFTER_CESSATION_OF_BREASTFEEDING);
        Concept RAPID_HIV_ANTIBODY_TEST_AT_18_MONTHS = Dictionary.getConcept(Dictionary.RAPID_HIV_ANTIBODY_TEST_AT_18_MONTHS);

        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inHEIProgram = Filters.inProgram(heiProgram, alive, context);

        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
            boolean pendingHIVTestResult = false;

            //In HEI program
            if (inHEIProgram.contains(ptId)) {
                //Check whether client has active DNA PCR or Rapid HIV test order
                OrderType patientLabOrders = orderService.getOrderTypeByUuid(TEST_ORDER_TYPE_UUID);
                if (patientLabOrders != null) {
                    //Get active lab orders
                    List<Order> activeVLTestOrders = orderService.getActiveOrders(Context.getPatientService().getPatient(ptId), patientLabOrders, null, null);
                    if (activeVLTestOrders.size() > 0) {
                        for (Order o : activeVLTestOrders) {
                            if (o.getOrderReason()!= null && o.getConcept().getConceptId().equals(1030) && o.getOrderReason().equals(PCR_6_WEEKS)) {
                                pendingHIVTestResult = true;
                                msgFlag.append("Pending week-6 PCR results");
                            } else if (o.getOrderReason()!= null && o.getConcept().getConceptId().equals(1030) && o.getOrderReason().equals(PCR_6_MONTHS)) {
                                pendingHIVTestResult = true;
                                msgFlag.append("Pending month-6 PCR results");
                            } else if (o.getOrderReason()!= null && o.getConcept().getConceptId().equals(1030) && o.getOrderReason().equals(PCR_12_MONTHS)) {
                                pendingHIVTestResult = true;
                                msgFlag.append("Pending month-12 PCR results");
                            } else if (o.getOrderReason()!= null && o.getOrderReason()!= null && o.getConcept().getConceptId().equals(163722) && o.getOrderReason().equals(RAPID_HIV_ANTIBODY_TEST_AT_18_MONTHS)) {
                                pendingHIVTestResult = true;
                                msgFlag.append("Pending month-18 HIV antibody results");
                            } else if (o.getOrderReason()!= null && o.getConcept().getConceptId().equals(163722) && o.getOrderReason().equals(AB_TEST_6_WEEKS_AFTER_CESSATION_OF_BREASTFEEDING)) {
                                pendingHIVTestResult = true;
                                msgFlag.append("Pending week-6 after breastfeeding HIV antibody results");
                            }

                        }
                    }

                }
            }

            ret.put(ptId, new BooleanResult(pendingHIVTestResult, this));
        }
        return ret;
    }
}
