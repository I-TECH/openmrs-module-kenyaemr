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
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by schege on 28/03/23.
 * Retired flag
 */
public class PendingCrAgResultCalculation extends AbstractPatientCalculation  {

    /**
     * @see PatientFlagCalculation#getFlagMessage()
     */
//    @Override
//    public String getFlagMessage() {
//        return "Pending Serum CrAg result";
//    }
    protected static final Log log = LogFactory.getLog(PendingCrAgResultCalculation.class);
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        String TEST_ORDER_TYPE_UUID = "52a447d3-a64a-11e3-9aeb-50e549534c5e";
        Set<Integer> alive = Filters.alive(cohort, context);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);

        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
            boolean pendingCrAgLoadResult = false;

            OrderService orderService = Context.getOrderService();
             //In HIV program
            if (inHivProgram.contains(ptId)) {
                //Check whether client has active Serum CrAg order
                OrderType patientLabOrders = orderService.getOrderTypeByUuid(TEST_ORDER_TYPE_UUID);
                if (patientLabOrders != null) {
                       //Get active lab orders
                    List<Order> activeCrAgTestOrders = orderService.getActiveOrders(Context.getPatientService().getPatient(ptId), patientLabOrders, null, null);
                    if (activeCrAgTestOrders.size() > 0) {
                        for (Order o : activeCrAgTestOrders) {
                            if (o.getConcept().getConceptId().equals(167452)) {
                                pendingCrAgLoadResult = true;
                            }

                        }
                    }

                }
            }

            ret.put(ptId, new BooleanResult(pendingCrAgLoadResult, this));
        }
        return ret;
    }
}
