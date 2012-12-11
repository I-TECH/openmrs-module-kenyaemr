package org.openmrs.module.kenyaemr.regimen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.io.InputStream;
import java.util.List;

public class RegimenManagerTest extends BaseModuleContextSensitiveTest {

	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("org/openmrs/module/kenyaemr/include/testData.xml");
	}

	/**
	 * @see org.openmrs.module.kenyaemr.regimen.RegimenManager#loadDefinitionsFromXML(java.io.InputStream)
	 * @verifies load all definitions
	 */
	@Test
	public void loadDefinitionsFromXML_shouldLoadAllDefinitions() throws Exception {

		InputStream stream = getClass().getClassLoader().getResourceAsStream("test-regimens.xml");

		RegimenManager.loadDefinitionsFromXML(stream);

		Assert.assertEquals(1, RegimenManager.getDefinitionsVersion());
		Assert.assertEquals(1, RegimenManager.getCategoryCodes().size());
		Assert.assertEquals(3, RegimenManager.getDrugConceptIds("ARV").size());
		Assert.assertEquals(3, RegimenManager.getRegimenDefinitions("ARV").size());

		List<RegimenDefinition> arvRegs = RegimenManager.getRegimenDefinitions("ARV");
		Assert.assertEquals("regimen1", arvRegs.get(0).getName());
		Assert.assertEquals("group1", arvRegs.get(0).getGroup());
		Assert.assertEquals(86663, arvRegs.get(0).getComponents().get(0).getConceptId()); // zidovudine
		Assert.assertEquals(300d, arvRegs.get(0).getComponents().get(0).getDose(), 0d);
		Assert.assertEquals("mg", arvRegs.get(0).getComponents().get(0).getUnits());
		Assert.assertEquals("OD", arvRegs.get(0).getComponents().get(0).getFrequency());
		Assert.assertEquals(78643, arvRegs.get(0).getComponents().get(1).getConceptId()); // lamivudine
		Assert.assertEquals(150d, arvRegs.get(0).getComponents().get(1).getDose(), 0d);
		Assert.assertEquals("mg", arvRegs.get(0).getComponents().get(1).getUnits());
		Assert.assertEquals("BD", arvRegs.get(0).getComponents().get(1).getFrequency());

		Assert.assertEquals("regimen2", arvRegs.get(1).getName());
		Assert.assertEquals("group1", arvRegs.get(1).getGroup());

		Assert.assertEquals("regimen3", arvRegs.get(2).getName());
		Assert.assertEquals("group2", arvRegs.get(2).getGroup());
		Assert.assertEquals(84309, arvRegs.get(2).getComponents().get(0).getConceptId());
		Assert.assertNull(arvRegs.get(2).getComponents().get(0).getDose());
		Assert.assertEquals("tab", arvRegs.get(2).getComponents().get(0).getUnits());
		Assert.assertNull(arvRegs.get(2).getComponents().get(0).getFrequency());
	}
}