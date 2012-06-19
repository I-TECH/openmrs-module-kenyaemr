package org.openmrs.module.kenyaemr.api;


import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.KenyaEmrActivator;
import org.openmrs.module.kenyaemr.KenyaEmrConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.ui.framework.session.Session;

public class KenyaEmrServiceTest extends BaseModuleContextSensitiveTest {
	
	KenyaEmrService service;
	
	@Before
	public void beforeEachTest() throws Exception {
		new KenyaEmrActivator().setupGlobalProperties();
		service = Context.getService(KenyaEmrService.class);
	}
	
	/**
	 * @see KenyaEmrService#getDefaultLocation(Session)
	 * @verifies throw an exception if the default location has not been set
	 */
	@Test(expected=APIException.class)
	public void getDefaultLocation_shouldThrowAnExceptionIfTheDefaultLocationHasNotBeenSet() throws Exception {
		Assert.assertTrue(StringUtils.isEmpty(Context.getAdministrationService().getGlobalProperty(KenyaEmrConstants.GP_DEFAULT_LOCATION)));
		service.getDefaultLocation();
	}
	
	/**
	 * @see KenyaEmrService#getDefaultLocation(Session)
	 * @verifies get the default location when set
	 */
	@Test
	public void getDefaultLocation_shouldGetTheDefaultLocationWhenSet() throws Exception {
		// setup data
		Location loc = Context.getLocationService().getLocation(1);
		Assert.assertNotNull(loc);
		service.setDefaultLocation(loc);
		
		// test
		Location defaultLocation = service.getDefaultLocation();
		Assert.assertEquals(loc, defaultLocation);
	}

	/**
     * @see KenyaEmrService#isConfigured()
     * @verifies return false before default location has been set
     */
    @Test
    public void isConfigured_shouldReturnFalseBeforeDefaultLocationHasBeenSet() throws Exception {
	    Assert.assertFalse(service.isConfigured());
    }

	/**
     * @see KenyaEmrService#isConfigured()
     * @verifies return true after everything is configured
     */
    @Test
    public void isConfigured_shouldReturnTrueAfterEverythingIsConfigured() throws Exception {
    	Assert.assertFalse(service.isConfigured());
    	Location loc = Context.getLocationService().getLocation(1);
    	Assert.assertNotNull(loc);
    	service.setDefaultLocation(loc);
    	
    	Assert.assertTrue(service.isConfigured());
    }
}