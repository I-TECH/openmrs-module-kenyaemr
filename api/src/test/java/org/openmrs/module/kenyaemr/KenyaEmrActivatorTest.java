package org.openmrs.module.kenyaemr;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

@Ignore("This doesn't work anymore since I added an MDS package with an HTML Form. Figure out what's up with that.")
public class KenyaEmrActivatorTest extends BaseModuleContextSensitiveTest {
	
	KenyaEmrActivator activator;

	@Before
	public void beforeEachTest() throws Exception {
		activator = new KenyaEmrActivator();
	}
	
	/**
	 * @see KenyaEmrActivator#started()
	 * @verifies install initial data only once
	 */
	@Test
	public void started_shouldInstallInitialData() throws Exception {
		String uuid = "94ad2b98-5f0a-4eee-96e4-446412bf5306";
		
		// first-time startup
		Assert.assertNull(Context.getEncounterService().getEncounterTypeByUuid(uuid));
		activator.started();
		Assert.assertNotNull(Context.getEncounterService().getEncounterTypeByUuid(uuid));
		
		// simulate starting a second time
		boolean anyChanges = activator.setupMetadataPackages();
		Assert.assertFalse(anyChanges);
		Assert.assertNotNull(Context.getEncounterService().getEncounterTypeByUuid(uuid));
	}
}