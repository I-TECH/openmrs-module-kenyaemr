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

		List<DrugOrder> allOrders = Context.getOrderService().getDrugOrdersByPatient(patient);
		List<DrugOrder> currentAndFutureOrders = new ArrayList<DrugOrder>();
		List<DrugOrder> completedOrders = new ArrayList<DrugOrder>();

		for (DrugOrder order : allOrders) {
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