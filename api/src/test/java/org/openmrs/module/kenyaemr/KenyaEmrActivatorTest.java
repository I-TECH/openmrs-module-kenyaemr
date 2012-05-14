package org.openmrs.module.kenyaemr;


import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class KenyaEmrActivatorTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see KenyaEmrActivator#started()
	 * @verifies install initial data
	 */
	@Test
	public void started_shouldInstallInitialData() throws Exception {
		new KenyaEmrActivator().started();
		Assert.assertNotNull(Context.getEncounterService().getEncounterTypeByUuid("94ad2b98-5f0a-4eee-96e4-446412bf5306"));
	}
}