/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.CareSetting;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.*;

import java.util.*;

/**
 * Created by codehub on 05/06/15.
 */
public class NeedsNewVLOrderCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {
    protected static final Log log = LogFactory.getLog(StablePatientsCalculation.class);
    /**
     * @see PatientFlagCalculation#getFlagMessage()
     */
    @Override
    public String getFlagMessage() {
        return "Collect new sample";
    }

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        // Get those due for VL
        Set<Integer> dueForVl = CalculationUtils.patientsThatPass(calculate(new NeedsViralLoadTestCalculation(), cohort, context));

        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId:cohort) {
            boolean needsNewVLOrder = false;
            OrderService orderService = Context.getOrderService();
            PatientService patientService= Context.getPatientService();
            OrderType labOrderType = orderService.getOrderTypeByUuid(OrderType.TEST_ORDER_TYPE_UUID);
            CareSetting careSetting = orderService.getCareSetting(1);
            List<Order> labOrders = orderService.getOrders(patientService.getPatient(ptId), careSetting, labOrderType, true);
            List<Integer> vlOrders = new ArrayList<Integer>();
            for (Order order : labOrders) {
                // only get vl orders
                if(order.getConcept().getConceptId() == 856 || order.getConcept().getConceptId() == 1305 ) {
                    vlOrders.add(order.getOrderId());
                }
            }
            if (vlOrders.size() > 0) {
                Integer latestVlOrder = Collections.max(vlOrders);
                if (dueForVl.contains(ptId) && orderService.getOrder(latestVlOrder).isVoided()
                        && (orderService.getOrder(latestVlOrder).getVoidReason().equalsIgnoreCase("Sample not taken")
                        || orderService.getOrder(latestVlOrder).getVoidReason().equalsIgnoreCase("Collect new sample")
                        || orderService.getOrder(latestVlOrder).getVoidReason().equalsIgnoreCase("Sample rejected")
                        || orderService.getOrder(latestVlOrder).getVoidReason().equalsIgnoreCase("No reagents"))) {
                    needsNewVLOrder = true;

                }
            }
            ret.put(ptId, new BooleanResult(needsNewVLOrder, this));
        }
        return  ret;
    }

}
