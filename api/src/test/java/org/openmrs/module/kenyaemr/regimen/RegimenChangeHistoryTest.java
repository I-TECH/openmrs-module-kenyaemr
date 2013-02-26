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
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class RegimenChangeHistoryTest extends BaseModuleContextSensitiveTest {

	final Date t0 = TestUtils.date(2006, 1, 1);
	final Date t1 = TestUtils.date(2006, 2, 1);
	final Date t2 = TestUtils.date(2006, 3, 1);
	final Date t3 = TestUtils.date(2006, 4, 1);

	Concept drug1, drug2, drug3, drug4;

	DrugOrder order1, order2, order3, order4;

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");

		/* Test case like this:
		 * 3TC: <---->
		 * AZT: |  <----->
		 * D4T: |  |  <-----...
		 * Asp: |  |  <-----... (this drug is not relevant)
		 *      |  |  |  |
		 *      t0 t1 t2 t3
		 */

		drug1 = Context.getConceptService().getConcept(78643); // 3TC
		drug2 = Context.getConceptService().getConcept(86663); // AZT
		drug3 = Context.getConceptService().getConcept(84309); // D4T
		drug4 = Context.getConceptService().getConcept(71617); // Aspirin

		order1 = TestUtils.saveDrugOrder(Context.getPatientService().getPatient(6), drug1, t0, t2);
		order1.setDiscontinuedReasonNonCoded("Because I felt like it");

		order2 = TestUtils.saveDrugOrder(Context.getPatientService().getPatient(6), drug2, t1, t3);
		order2.setDiscontinuedReason(Context.getConceptService().getConcept(16)); // DIED

		order3 = TestUtils.saveDrugOrder(Context.getPatientService().getPatient(6), drug3, t2, null);

		order4 = TestUtils.saveDrugOrder(Context.getPatientService().getPatient(6), drug4, t2, null);
	}

	/**
	 * @see RegimenChangeHistory#RegimenChangeHistory(java.util.Set, java.util.List)
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
		TestUtils.assertRegimenContainsDrugOrders(changes.get(0).getStarted(), order1);

		// Change #2 should be drug1 > drug1, drug2
		Assert.assertEquals(t1, changes.get(1).getDate());
		Assert.assertSame(changes.get(0).getStarted(), changes.get(1).getStopped());
		TestUtils.assertRegimenContainsDrugOrders(changes.get(1).getStarted(), order1, order2);

		// Change #3 should be drug1, drug2 > drug2, drug3
		Assert.assertEquals(t2, changes.get(2).getDate());
		Assert.assertSame(changes.get(1).getStarted(), changes.get(2).getStopped());
		TestUtils.assertRegimenContainsDrugOrders(changes.get(2).getStarted(), order2, order3);
		Assert.assertEquals(1, changes.get(2).getChangeReasonsNonCoded().size());

		// Change #4 should be drug2, drug3 > drug3
		Assert.assertEquals(t3, changes.get(3).getDate());
		Assert.assertSame(changes.get(2).getStarted(), changes.get(3).getStopped());
		TestUtils.assertRegimenContainsDrugOrders(changes.get(3).getStarted(), order3);
		Assert.assertEquals(1, changes.get(3).getChangeReasons().size());
	}

	@Test
	public void forPatient_shouldCreateRegimenHistoryForPatient() {
		Patient patient6 = Context.getPatientService().getPatient(6);
		Concept arvs = Context.getConceptService().getConceptByUuid(MetadataConstants.ANTIRETROVIRAL_DRUGS_CONCEPT_UUID);
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
		TestUtils.assertRegimenContainsDrugOrders(changes.get(2).getStarted(), order2, order3);

		// But drug2 doesn't discontinue now
		Assert.assertFalse(order2.getDiscontinued());
		Assert.assertNull(order2.getDiscontinuedDate());
		Assert.assertNull(order2.getDiscontinuedBy());
		Assert.assertNull(order2.getDiscontinuedReason());
		Assert.assertNull(order2.getDiscontinuedReasonNonCoded());
	}
}