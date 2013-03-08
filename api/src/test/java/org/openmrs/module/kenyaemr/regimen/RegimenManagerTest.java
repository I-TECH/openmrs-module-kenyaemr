package org.openmrs.module.kenyaemr.regimen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.KenyaEmr;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.List;

public class RegimenManagerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	RegimenManager regimenManager;

	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");

		regimenManager.clear();

		InputStream stream = getClass().getClassLoader().getResourceAsStream("test-regimens.xml");
		regimenManager.loadDefinitionsFromXML(stream);
	}

	/**
	 * @see RegimenManager#loadDefinitionsFromXML(java.io.InputStream)
	 * @verifies load all definitions
	 */
	@Test
	public void loadDefinitionsFromXML_shouldLoadAllDefinitions() throws Exception {
		Assert.assertEquals(1, regimenManager.getCategoryCodes().size());

		Assert.assertEquals(MetadataConstants.ANTIRETROVIRAL_DRUGS_CONCEPT_UUID, regimenManager.getMasterSetConcept("category1").getUuid());

		List<RegimenDefinitionGroup> groups = regimenManager.getRegimenGroups("category1");

		Assert.assertEquals(2, groups.size());
		RegimenDefinitionGroup group1 = groups.get(0);
		RegimenDefinitionGroup group2 = groups.get(1);

		Assert.assertEquals("group1", group1.getCode());
		Assert.assertEquals("Group #1", group1.getName());

		Assert.assertEquals(2, group1.getRegimens().size());
		RegimenDefinition regimen1 = group1.getRegimens().get(0);
		RegimenDefinition regimen2 = group1.getRegimens().get(1);

		Assert.assertEquals("regimen1", regimen1.getName());
		Assert.assertEquals(new Integer(86663), regimen1.getComponents().get(0).getDrugRef().getConcept().getConceptId()); // zidovudine
		Assert.assertEquals(300d, regimen1.getComponents().get(0).getDose(), 0d);
		Assert.assertEquals("mg", regimen1.getComponents().get(0).getUnits());
		Assert.assertEquals("OD", regimen1.getComponents().get(0).getFrequency());

		Assert.assertEquals(new Integer(78643), regimen1.getComponents().get(1).getDrugRef().getConcept().getConceptId()); // lamivudine
		Assert.assertEquals(150d, regimen1.getComponents().get(1).getDose(), 0d);
		Assert.assertEquals("mg", regimen1.getComponents().get(1).getUnits());
		Assert.assertEquals("BD", regimen1.getComponents().get(1).getFrequency());

		Assert.assertEquals("regimen2", regimen2.getName());

		Assert.assertEquals("group2", group2.getCode());
		Assert.assertEquals("Group #2", group2.getName());

		Assert.assertEquals(1, group2.getRegimens().size());
		RegimenDefinition regimen3 = group2.getRegimens().get(0);

		Assert.assertEquals("regimen3", regimen3.getName());

		Assert.assertEquals(new Integer(84309), regimen3.getComponents().get(0).getDrugRef().getConcept().getConceptId());
		Assert.assertNull(regimen3.getComponents().get(0).getDose());
		Assert.assertEquals("tab", regimen3.getComponents().get(0).getUnits());
		Assert.assertNull(regimen3.getComponents().get(0).getFrequency());
	}

	/**
	 * @see RegimenManager#findDefinitions(String, RegimenOrder, boolean)
	 */
	@Test
	public void findDefinitions_shouldFindDefinitionsForRegimen() {
		// Create regimen that matches the regimen2 definition exactly
		DrugOrder lamivudine = new DrugOrder();
		lamivudine.setConcept(Context.getConceptService().getConcept(78643));
		lamivudine.setDose(150d);
		lamivudine.setUnits("mg");
		lamivudine.setFrequency("BD");
		DrugOrder stavudine = new DrugOrder();
		stavudine.setConcept(Context.getConceptService().getConcept(84309));
		stavudine.setDose(30d);
		stavudine.setUnits("mg");
		stavudine.setFrequency("OD");
		RegimenOrder regimen = new RegimenOrder();
		regimen.addDrugOrder(lamivudine);
		regimen.addDrugOrder(stavudine);

		// Test exact match
		List<RegimenDefinition> defsExact = regimenManager.findDefinitions("category1", regimen, true);
		Assert.assertEquals(1, defsExact.size());
		Assert.assertEquals("regimen2", defsExact.get(0).getName());

		// Test non-exact match
		List<RegimenDefinition> defsNonExact = regimenManager.findDefinitions("category1", regimen, false);
		Assert.assertEquals(2, defsNonExact.size());
		Assert.assertEquals("regimen2", defsNonExact.get(0).getName());
		Assert.assertEquals("regimen3", defsNonExact.get(1).getName());
	}

	/**
	 * @see org.openmrs.module.kenyaemr.regimen.RegimenManager#clear()
	 */
	@Test
	public void clear_shouldClearAllRegimenData() {
		regimenManager.clear();

		Assert.assertEquals(0, regimenManager.getCategoryCodes().size());
		Assert.assertNull(regimenManager.getMasterSetConcept("category1"));
		Assert.assertNull(regimenManager.getDrugs("category1"));
		Assert.assertNull(regimenManager.getRegimenGroups("category1"));
	}
}