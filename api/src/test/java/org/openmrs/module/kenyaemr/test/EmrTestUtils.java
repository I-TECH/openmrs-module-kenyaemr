/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.test;

import org.junit.Assert;
import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.OrderFrequency;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.OrderContext;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility methods for unit tests
 */

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
		CareSetting outpatient = Context.getOrderService().getCareSettingByName("OUTPATIENT");
		OrderType drugOrderType = Context.getOrderService().getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID);

		for (Concept concept : concepts) {
			/*DrugOrder order = new DrugOrder();
			order.setOrderType(Context.getOrderService().getOrderType(2));
			order.setPatient(patient);
			order.setOrderer(Context.getUserService().getUser(1));
			order.setConcept(concept);
			order.setDateActivated(start);
			order.setDiscontinued(end != null);
			order.setAction(end);
			orders.add((DrugOrder) Context.getOrderService().saveOrder(order));
*/

			DrugOrder order = new DrugOrder();
			order.setPatient(patient);
			List<Provider> provider = (List<Provider>) Context.getProviderService().getProvidersByPerson(Context.getUserService().getUser(1).getPerson());
			Encounter e = Context.getEncounterService().getEncounter(3);
			order.setEncounter(e);
			order.setOrderer(provider.get(0));
			order.setConcept(concept);
			order.setDateActivated(start);
			order.setDose(2.0);
			order.setDoseUnits(Context.getConceptService().getConcept(51));
			order.setRoute(Context.getConceptService().getConcept(22));
			OrderFrequency orderFrequency = Context.getOrderService().getOrderFrequency(1);
			order.setFrequency(orderFrequency);


			if (end != null) {
				order.setAction(Order.Action.DISCONTINUE);
			}

			OrderContext orderContext = new OrderContext();
			orderContext.setCareSetting(outpatient);
			orderContext.setOrderType(drugOrderType);
		}
		return new RegimenOrder(orders);
	}

	/**
	 * Saves a regimen order
	 * @param patient the patient
	 * @param concepts the drug concepts
	 * @param start the start date
	 * @param end the end date
	 * @return the drug order
	 */
	public static RegimenOrder saveRegimenOrder(Patient patient, Collection<Concept> concepts, Date start, Date end, Encounter encounter) {
		Set<DrugOrder> orders = new LinkedHashSet<DrugOrder>();
		CareSetting outpatient = Context.getOrderService().getCareSettingByName("OUTPATIENT");
		OrderType drugOrderType = Context.getOrderService().getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID);

		for (Concept concept : concepts) {
			/*DrugOrder order = new DrugOrder();
			order.setOrderType(Context.getOrderService().getOrderType(2));
			order.setPatient(patient);
			order.setOrderer(Context.getUserService().getUser(1));
			order.setConcept(concept);
			order.setDateActivated(start);
			order.setDiscontinued(end != null);
			order.setAction(end);
			orders.add((DrugOrder) Context.getOrderService().saveOrder(order));
*/

			DrugOrder order = new DrugOrder();
			order.setPatient(patient);
			List<Provider> provider = (List<Provider>) Context.getProviderService().getProvidersByPerson(Context.getUserService().getUser(1).getPerson());
			order.setEncounter(encounter);
			order.setOrderer(provider.get(0));
			order.setConcept(concept);
			order.setDateActivated(start);
			order.setDose(2.0);
			order.setDoseUnits(Context.getConceptService().getConcept(51));
			order.setRoute(Context.getConceptService().getConcept(22));
			OrderFrequency orderFrequency = Context.getOrderService().getOrderFrequency(1);
			order.setFrequency(orderFrequency);


			if (end != null) {
				order.setAction(Order.Action.DISCONTINUE);
			}

			OrderContext orderContext = new OrderContext();
			orderContext.setCareSetting(outpatient);
			orderContext.setOrderType(drugOrderType);
			orders.add((DrugOrder) Context.getOrderService().saveOrder(order, orderContext));
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