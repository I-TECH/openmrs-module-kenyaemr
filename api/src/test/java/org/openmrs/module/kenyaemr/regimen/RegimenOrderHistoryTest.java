package org.openmrs.module.kenyaemr.regimen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.*;

public class RegimenOrderHistoryTest extends BaseModuleContextSensitiveTest {

	final Date t0 = TestUtils.date(2006, 1, 1);
	final Date t1 = TestUtils.date(2006, 2, 1);
	final Date t2 = TestUtils.date(2006, 3, 1);

	Concept drug1, drug2, drug3, drug4;

	DrugOrder order1, order2, order3, order4;

	RegimenOrderHistory regimenHistory;

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");

		/* Test case like this:
		 * 3TC: <----->
		 * AZT: <----->
		 * D4T: |  <-----...
		 * Asp: |  <-----... (this drug is not relevant)
		 *      |  |  |
		 *      t0 t1 t2
		 */

		drug1 = Context.getConceptService().getConcept(78643); // 3TC
		drug2 = Context.getConceptService().getConcept(86663); // AZT
		drug3 = Context.getConceptService().getConcept(84309); // D4T
		drug4 = Context.getConceptService().getConcept(71617); // Aspirin

		order1 = TestUtils.saveDrugOrder(Context.getPatientService().getPatient(6), drug1, t0, t2);
		order2 = TestUtils.saveDrugOrder(Context.getPatientService().getPatient(6), drug2, t0, t2);
		order3 = TestUtils.saveDrugOrder(Context.getPatientService().getPatient(6), drug3, t1, null);
		order4 = TestUtils.saveDrugOrder(Context.getPatientService().getPatient(6), drug4, t1, null);

		List<DrugOrder> allDrugOrders = Arrays.asList(order1, order2, order3, order4);
		Set<Concept> relevantDrugs = new HashSet<Concept>(Arrays.asList(drug1, drug2, drug3));
		regimenHistory = new RegimenOrderHistory(relevantDrugs, allDrugOrders);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.regimen.RegimenOrderHistory#RegimenOrderHistory(java.util.Set, java.util.List)
	 * @verifies create regimen history based on all relevant drug orders
	 */
	@Test
	public void constructor_shouldCreateRegimenOrderHistory() {
		List<RegimenOrder> regimenOrders = regimenHistory.getOrders();

		// Should be 2 orders in total
		Assert.assertEquals(2, regimenOrders.size());

		// Order #1 should be 3TC+AZT from t0 to t2
		Assert.assertEquals(t0, regimenOrders.get(0).getStartDate());
		TestUtils.assertRegimenContainsDrugOrders(regimenOrders.get(0), order1, order2);

		// Order #2 should be D4T from t1
		Assert.assertEquals(t1, regimenOrders.get(1).getStartDate());
		TestUtils.assertRegimenContainsDrugOrders(regimenOrders.get(1), order3);
	}

	/**
	 * @see RegimenOrderHistory#getOrdersOnDate(java.util.Date)
	 * @verifies return all active orders on date
	 */
	@Test
	public void getOrdersOnDate_shouldReturnAllOrdersActiveOnDate() {
		List<RegimenOrder> t0_orders = regimenHistory.getOrdersOnDate(t0);
		Assert.assertEquals(1, t0_orders.size());
		TestUtils.assertRegimenContainsDrugOrders(t0_orders.get(0), order1, order2);

		List<RegimenOrder> t1_orders = regimenHistory.getOrdersOnDate(t1);
		Assert.assertEquals(2, t1_orders.size());
		TestUtils.assertRegimenContainsDrugOrders(t1_orders.get(0), order1, order2);
		TestUtils.assertRegimenContainsDrugOrders(t1_orders.get(1), order3);

		List<RegimenOrder> t2_orders = regimenHistory.getOrdersOnDate(t2);
		Assert.assertEquals(1, t2_orders.size());
		TestUtils.assertRegimenContainsDrugOrders(t2_orders.get(0), order3);
	}

	/**
	 * @see RegimenOrderHistory#getCurrentOrders()
	 * @verifies return all currently active orders
	 */
	@Test
	public void getOrdersOnDate_shouldReturnAllCurrentlyActiveOrders() {
		List<RegimenOrder> orders = regimenHistory.getCurrentOrders();
		Assert.assertEquals(1, orders.size());
		TestUtils.assertRegimenContainsDrugOrders(orders.get(0), order3);
	}
}