package org.openmrs.module.kenyaemr.api;


import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.validator.LuhnMod25IdentifierValidator;
import org.openmrs.module.kenyaemr.KenyaEmrActivator;
import org.openmrs.module.kenyaemr.KenyaEmrConstants;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.ui.framework.session.Session;

public class KenyaEmrServiceTest extends BaseModuleContextSensitiveTest {
	
	KenyaEmrService service;
	
	@Before
	public void beforeEachTest() throws Exception {
		new KenyaEmrActivator().setupGlobalProperties();
		service = Context.getService(KenyaEmrService.class);
		setupMetadata();
	}
	
	/**
     * TODO: do this by actually loading MDS packages
     */
    private void setupMetadata() {
	    PatientIdentifierType id = new PatientIdentifierType();
	    id.setUuid(MetadataConstants.OPENMRS_ID_UUID);
	    id.setName("OpenMRS ID");
	    id.setDescription("Medical Record Number");
	    id.setValidator(LuhnMod25IdentifierValidator.class.getName());
	    Context.getPatientService().savePatientIdentifierType(id);
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

    	// default location
    	Location loc = Context.getLocationService().getLocation(1);
    	Assert.assertNotNull(loc);
    	service.setDefaultLocation(loc);
    	
    	// MRN ID source
    	service.setupMrnIdentifierSource(null);
    	
    	Assert.assertTrue(service.isConfigured());
    }

	/**
     * @see KenyaEmrService#setupMrnIdentifierSource(String)
     * @verifies set up an identifier source
     */
    @Test
    public void setupMrnIdentifierSource_shouldSetUpAnIdentifierSource() throws Exception {
    	Assert.assertFalse(isMrnIdentifierSourceSetup());
	    service.setupMrnIdentifierSource("4");
	    Assert.assertTrue(isMrnIdentifierSourceSetup());
	    IdentifierSource source = service.getMrnIdentifierSource();
	    Assert.assertEquals("M4E", Context.getService(IdentifierSourceService.class).generateIdentifier(source, "Testing"));
	    Assert.assertEquals("M6C", Context.getService(IdentifierSourceService.class).generateIdentifier(source, "Testing"));
	    Assert.assertEquals("M79", Context.getService(IdentifierSourceService.class).generateIdentifier(source, "Testing"));
    }

	/**
     * @return whether the MRN identifier source has been set up
     */
    private boolean isMrnIdentifierSourceSetup() {
    	try {
    		IdentifierSource source = service.getMrnIdentifierSource();
    		return source != null;
    	} catch (Exception ex) {
    		return false;
    	}
    }

	/**
     * @see KenyaEmrService#setupMrnIdentifierSource(String)
     * @verifies fail if already set up
     */
    @Test
    public void setupMrnIdentifierSource_shouldFailIfAlreadySetUp() throws Exception {
    	service.setupMrnIdentifierSource("4");
    	try {
    		service.setupMrnIdentifierSource("4");
    		Assert.fail("Shouldn't be allowed to set up twice");
    	} catch (Exception ex) {
    		// pass
    	}
    }
}