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

package org.openmrs.module.kenyaemr.regimen;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.test.EmrTestUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests for {@link RegimenChangeHistory}
 */
public class RegimenChangeHistoryTest extends BaseModuleContextSensitiveTest {

	final Date t0 = TestUtils.date(2006, 1, 1);
	final Date t1 = TestUtils.date(2006, 2, 1);
	final Date t2 = TestUtils.date(2006, 3, 1);
	final Date t3 = TestUtils.date(2006, 4, 1);

	Concept drug1, drug2, drug3, drug4;

	DrugOrder order1, order2, order3, order4;

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

		order1 = TestUtils.saveDrugOrder(TestUtils.getPatient(6), drug1, t0, t2);
		order1.setDiscontinuedReasonNonCoded("Because I felt like it");

		order2 = TestUtils.saveDrugOrder(TestUtils.getPatient(6), drug2, t1, t3);
		order2.setDiscontinuedReason(Dictionary.getConcept(Dictionary.DIED));

		order3 = TestUtils.saveDrugOrder(TestUtils.getPatient(6), drug3, t2, null);

		order4 = TestUtils.saveDrugOrder(TestUtils.getPatient(6), drug4, t2, null);
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
		Assert.assertEquals(4, changes.size());

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
		Assert.assertEquals(1, changes.get(3).getChangeReasons().size());
	}

	@Test
	public void forPatient_shouldCreateRegimenHistoryForPatient() {
		Patient patient6 = Context.getPatientService().getPatient(6);
		Concept arvs = Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_DRUGS);
		RegimenChangeHistory regimenHistory = RegimenChangeHistory.forPatient(patient6, arvs);

		// Should be 4 changes in total
		Assert.assertEquals(4, regimenHistory.getChanges().size());
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
		Assert.assertEquals(3, changes.size());

		// Change #3 should still start drug2, drug3
		EmrTestUtils.assertRegimenContainsDrugOrders(changes.get(2).getStarted(), order2, order3);

		// But drug2 doesn't discontinue now
		Assert.assertFalse(order2.getDiscontinued());
		Assert.assertNull(order2.getDiscontinuedDate());
		Assert.assertNull(order2.getDiscontinuedBy());
		Assert.assertNull(order2.getDiscontinuedReason());
		Assert.assertNull(order2.getDiscontinuedReasonNonCoded());
	}
}