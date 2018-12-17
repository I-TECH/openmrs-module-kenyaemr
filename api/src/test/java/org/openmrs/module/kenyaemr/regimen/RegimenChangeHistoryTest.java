/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.regimen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
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
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.test.EmrTestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tests for {@link RegimenChangeHistory}
 */

@Ignore
public class RegimenChangeHistoryTest extends BaseModuleContextSensitiveTest {

	final Date t0 = TestUtils.date(2008, 8, 1);
	final Date t1 = TestUtils.date(2008, 9, 1);
	final Date t2 = TestUtils.date(2008, 10, 1);
	final Date t3 = TestUtils.date(2008, 11, 1);

	Concept drug1, drug2, drug3, drug4;

	DrugOrder order1, order2, order3, order4;


	/**
	 * Saves a drug order
	 * @param patient the patient
	 * @param concept the drug concept
	 * @param start the start date
	 * @param end the end date
	 * @return the drug order
	 */
	public static DrugOrder saveDrugOrder(Patient patient, Concept concept, Date start, Date end) {

		CareSetting outpatient = Context.getOrderService().getCareSettingByName("OUTPATIENT");
		OrderType drugOrderType = Context.getOrderService().getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID);

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

		return (DrugOrder) Context.getOrderService().saveOrder(order, orderContext);
	}

	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		executeDataSet("dataset/test-drugs.xml");

		/* Test case like this:
		 * 3TC: <---->
		 * AZT: |  <----->
		 * D4T: |  |  <-----...
		 * Dap: |  |  <-----... (this drug is not relevant)
		 *      |  |  |  |
		 *      t0 t1 t2 t3
		 */

		drug1 = Dictionary.getConcept("78643AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"); // 3TC
		drug2 = Dictionary.getConcept("86663AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"); // AZT
		drug3 = Dictionary.getConcept("84309AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"); // D4T
		drug4 = Dictionary.getConcept(Dictionary.DAPSONE); // Dapsone

		order1 = saveDrugOrder(TestUtils.getPatient(7), drug1, t0, t2);
		//order1.setOrderReasonNonCoded("Because I felt like it");

		order2 = saveDrugOrder(TestUtils.getPatient(7), drug2, t1, t3);
		//order2.setOrderReasonNonCoded("Died");

		order3 = saveDrugOrder(TestUtils.getPatient(7), drug3, t2, null);

		order4 = saveDrugOrder(TestUtils.getPatient(7), drug4, t2, null);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory#RegimenChangeHistory(java.util.Set, java.util.List)
	 * @verifies create regimen history based on all relevant drug orders
	 */
	@Test
	public void constructor_shouldCreateRegimenHistory() throws Exception {
		List<DrugOrder> allDrugOrders = Arrays.asList(order1, order2, order3, order4);
		Set<Concept> relevantDrugs = new HashSet<Concept>(Arrays.asList(drug1, drug2, drug3));
		RegimenChangeHistory regimenHistory = new RegimenChangeHistory(relevantDrugs, allDrugOrders);
		List<RegimenChange> changes = regimenHistory.getChanges();

		// Should be 4 changes in total
		Assert.assertEquals(3, changes.size());

		// Change #1 should be null > drug1
		Assert.assertEquals(t0, changes.get(0).getDate());
		Assert.assertNull(changes.get(0).getStopped());
		EmrTestUtils.assertRegimenContainsDrugOrders(changes.get(0).getStarted(), order1);

		// Change #2 should be drug1 > drug1, drug2
		Assert.assertEquals(t1, changes.get(1).getDate());
		Assert.assertSame(changes.get(0).getStarted(), changes.get(1).getStopped());
		EmrTestUtils.assertRegimenContainsDrugOrders(changes.get(1).getStarted(), order1, order2);

		// Change #3 should be drug1, drug2 > drug2, drug3
		Assert.assertEquals(t2, changes.get(2).getDate());
		Assert.assertSame(changes.get(1).getStarted(), changes.get(2).getStopped());
		EmrTestUtils.assertRegimenContainsDrugOrders(changes.get(2).getStarted(), order2, order3);
		Assert.assertEquals(1, changes.get(2).getChangeReasonsNonCoded().size());

		// Change #4 should be drug2, drug3 > drug3
		Assert.assertEquals(t3, changes.get(3).getDate());
		Assert.assertSame(changes.get(2).getStarted(), changes.get(3).getStopped());
		EmrTestUtils.assertRegimenContainsDrugOrders(changes.get(3).getStarted(), order3);
		Assert.assertEquals(0, changes.get(3).getChangeReasons().size());
	}

	@Test
	public void forPatient_shouldCreateRegimenHistoryForPatient() {
		Patient patient6 = Context.getPatientService().getPatient(6);
		Concept arvs = Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_DRUGS);
		RegimenChangeHistory regimenHistory = RegimenChangeHistory.forPatient(patient6, arvs);

		// Should be 4 changes in total
		Assert.assertEquals(3, regimenHistory.getChanges().size());
	}

	/**
	 * @see RegimenChangeHistory#undoLastChange()
	 */
	@Test
	public void undoLastChange() {
		List<DrugOrder> allDrugOrders = Arrays.asList(order1, order2, order3, order4);
		Set<Concept> relevantDrugs = new HashSet<Concept>(Arrays.asList(drug1, drug2, drug3));
		RegimenChangeHistory regimenHistory = new RegimenChangeHistory(relevantDrugs, allDrugOrders);

		regimenHistory.undoLastChange();

		List<RegimenChange> changes = regimenHistory.getChanges();

		// Should be 3 changes in total
		Assert.assertEquals(2, changes.size());

		// Change #3 should still start drug2, drug3
		EmrTestUtils.assertRegimenContainsDrugOrders(changes.get(2).getStarted(), order2, order3);
		Assert.assertNull(order2.getDateStopped());
	}
}