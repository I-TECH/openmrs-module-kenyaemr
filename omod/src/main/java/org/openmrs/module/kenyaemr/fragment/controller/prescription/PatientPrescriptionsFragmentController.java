/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyaemr.fragment.controller.prescription;

import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Patient drugs orders fragment
 */
public class PatientPrescriptionsFragmentController {

	public void controller(@FragmentParam("patient") Patient patient, FragmentModel model) {

		List<Order> allOrders = Context.getOrderService().getAllOrdersByPatient(patient);
		List<DrugOrder> allDrugOrders = new ArrayList<DrugOrder>();
		// TODO optimize code further
		String DRUG_ORDER_TYPE_UUID = "131168f4-15f5-102d-96e4-000c29c2a5d7";
		OrderType orderType = Context.getOrderService().getOrderTypeByUuid(DRUG_ORDER_TYPE_UUID);
		for (Order o: allOrders) {
			DrugOrder order = null;
			if (o.getOrderType().equals(orderType)) {
				order = (DrugOrder) o;
				allDrugOrders.add(order);
			}

		}
		List<DrugOrder> currentAndFutureOrders = new ArrayList<DrugOrder>();
		List<DrugOrder> completedOrders = new ArrayList<DrugOrder>();

		for (DrugOrder order : allDrugOrders) {
			if (order.isCurrent() || order.isFuture()) {
				currentAndFutureOrders.add(order);
			} else {
				completedOrders.add(order);
			}
		}

		model.put("currentAndFutureOrders", currentAndFutureOrders);
		model.put("completedOrders", completedOrders);
	}
}