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
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.Dictionary;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by schege on 28/03/23.
 * Retired flag
 */
public class PendingHepatitisBResultCalculation extends AbstractPatientCalculation  {

    /**
     * @see PatientFlagCalculation#getFlagMessage()
     */
//    @Override
//    public String getFlagMessage() {
//        return "Pending HepB result";
//    }

    protected static final Log log = LogFactory.getLog(PendingHepatitisBResultCalculation.class);

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Set<Integer> alive = Filters.alive(cohort, context);

        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
            boolean pendingHepatitisBResult = false;

            OrderService orderService = Context.getOrderService();

            if (alive.contains(ptId)) {
                //Check whether client has active Hepatitis B order
                OrderType patientLabOrders = orderService.getOrderTypeByUuid(OrderType.TEST_ORDER_TYPE_UUID);
                if (patientLabOrders != null) {
                    //Get active lab orders
                    List<Order> activeHepatitisBTestOrders = orderService.getActiveOrders(Context.getPatientService().getPatient(ptId), patientLabOrders, null, null);
                    if (activeHepatitisBTestOrders.size() > 0) {
                        for (Order o : activeHepatitisBTestOrders) {
                            if (o.getConcept().equals(Dictionary.getConcept(Dictionary.HEPATITITS_B))) {
                                pendingHepatitisBResult = true;
                            }

                        }
                    }

                }
            }

            ret.put(ptId, new BooleanResult(pendingHepatitisBResult, this));
        }
        return ret;
    }
}
