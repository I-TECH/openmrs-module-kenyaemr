/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.prescription;

import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Patient drugs orders fragment
 */
public class PatientPrescriptionsFragmentController {

	public void controller(@FragmentParam("patient") Patient patient, FragmentModel model) {

		List<DrugOrder> allDrugOrders = EmrUtils.drugOrdersFromOrders(patient, null);

		List<DrugOrder> currentAndFutureOrders = new ArrayList<DrugOrder>();
		List<DrugOrder> completedOrders = new ArrayList<DrugOrder>();

		for (DrugOrder order : allDrugOrders) {
			//TODO Probably wrong look into this
		// if (order.isCurrent() || order.isFuture()) {
			if (order.isActive()) {
				currentAndFutureOrders.add(order);
			} else {
				completedOrders.add(order);
			}
		}

		model.put("currentAndFutureOrders", currentAndFutureOrders);
		model.put("completedOrders", completedOrders);
	}
}