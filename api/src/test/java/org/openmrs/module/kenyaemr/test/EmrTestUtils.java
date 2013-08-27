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

package org.openmrs.module.kenyaemr.test;

import org.junit.Assert;
import org.junit.Ignore;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Utility methods for unit tests
 */
@Ignore
public class EmrTestUtils {

	/**
	 * Saves a regimen order
	 * @param patient the patient
	 * @param concepts the drug concepts
	 * @param start the start date
	 * @param end the end date
	 * @return the drug order
	 */
	public static RegimenOrder saveRegimenOrder(Patient patient, Collection<Concept> concepts, Date start, Date end) {
		Set<DrugOrder> orders = new LinkedHashSet<DrugOrder>();

		for (Concept concept : concepts) {
			DrugOrder order = new DrugOrder();
			order.setOrderType(Context.getOrderService().getOrderType(2));
			order.setPatient(patient);
			order.setOrderer(Context.getUserService().getUser(1));
			order.setConcept(concept);
			order.setStartDate(start);
			order.setDiscontinued(end != null);
			order.setDiscontinuedDate(end);
			orders.add((DrugOrder) Context.getOrderService().saveOrder(order));
		}

		return new RegimenOrder(orders);
	}

	/**
	 * Asserts that the given regimen contains only the given drug orders
	 * @param reg
	 * @param drugOrders
	 */
	public static void assertRegimenContainsDrugOrders(RegimenOrder reg, DrugOrder... drugOrders) {
		Assert.assertEquals(drugOrders.length, reg.getDrugOrders().size());
		for (DrugOrder o : drugOrders) {
			Assert.assertTrue(reg.getDrugOrders().contains(o));
		}
	}

	/**
	 * Creates a calculation context
	 * @param now the now date
	 * @return the context
	 */
	public static PatientCalculationContext calculationContext(Date now) {
		PatientCalculationContext context = Context.getService(PatientCalculationService.class).createCalculationContext();
		context.setNow(now);
		return context;
	}
}