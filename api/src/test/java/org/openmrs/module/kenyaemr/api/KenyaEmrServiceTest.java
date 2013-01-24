package org.openmrs.module.kenyaemr.api;


import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.LocationAttributeType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.validator.LuhnMod25IdentifierValidator;
import org.openmrs.module.kenyaemr.KenyaEmrActivator;
import org.openmrs.module.kenyaemr.KenyaEmrConstants;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.report.ReportManager;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.ui.framework.session.Session;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KenyaEmrServiceTest extends BaseModuleContextSensitiveTest {
	
	KenyaEmrService service;
	
	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("test-data.xml");
		new KenyaEmrActivator().setupGlobalProperties();
		service = Context.getService(KenyaEmrService.class);
		//setupMetadata();
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
	 * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#getDefaultLocation()
	 * @verifies throw an exception if the default location has not been set
	 */
	@Test(expected=APIException.class)
	public void getDefaultLocation_shouldThrowAnExceptionIfTheDefaultLocationHasNotBeenSet() throws Exception {
		Assert.assertTrue(StringUtils.isEmpty(Context.getAdministrationService().getGlobalProperty(KenyaEmrConstants.GP_DEFAULT_LOCATION)));
		service.getDefaultLocation();
	}
	
	/**
	 * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#getDefaultLocation()
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
	 * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#getLocationByMflCode(String)
	 * @verifies find the location with that code
	 * @verifies return null if no location has that code
	 */
	@Test
	public void getLocationByMflCode_shouldFindLocationWithCodeOrNull() throws Exception {
		Assert.assertEquals(Context.getLocationService().getLocation(1), service.getLocationByMflCode("15001"));
		Assert.assertNull(service.getLocationByMflCode("15003")); // Location is retired
		Assert.assertNull(service.getLocationByMflCode("XXXXX")); // No such MFL code
	}

	/**
     * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#isConfigured()
     * @verifies return false before default location has been set
     */
    @Test
    public void isConfigured_shouldReturnFalseBeforeDefaultLocationHasBeenSet() throws Exception {
	    Assert.assertFalse(service.isConfigured());
    }

	/**
     * @see org.openmrs.module.kenyaemr.api.KenyaEmrService#isConfigured()
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
    	
    	// HIV ID source
    	service.setupHivUniqueIdentifierSource(null);
    	
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
	    Assert.assertNotNull(source);
	    
	    PatientIdentifierType idType = source.getIdentifierType();
	    Assert.assertEquals("M4E", Context.getService(IdentifierSourceService.class).generateIdentifier(idType, "Testing"));
	    Assert.assertEquals("M6C", Context.getService(IdentifierSourceService.class).generateIdentifier(idType, "Testing"));
	    Assert.assertEquals("M79", Context.getService(IdentifierSourceService.class).generateIdentifier(idType, "Testing"));
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

	/**
     * @see KenyaEmrService#setupHivUniqueIdentifierSource(String)
     * @verifies fail if already set up
     */
    @Test
    public void setupHivUniqueIdentifierSource_shouldFailIfAlreadySetUp() throws Exception {
    	service.setupHivUniqueIdentifierSource("00517");
    	try {
    		service.setupHivUniqueIdentifierSource("00517");
    		Assert.fail("Shouldn't be allowed to set up twice");
    	} catch (Exception ex) {
    		// pass
    	}
    }

	/**
     * @see KenyaEmrService#setupHivUniqueIdentifierSource(String)
     * @verifies set up an identifier source
     */
    @Test
    public void setupHivUniqueIdentifierSource_shouldSetUpAnIdentifierSource() throws Exception {
    	Assert.assertFalse(isHivIdentifierSourceSetup());
	    service.setupHivUniqueIdentifierSource("00517");
	    Assert.assertTrue(isHivIdentifierSourceSetup());
	    IdentifierSource source = service.getHivUniqueIdentifierSource();
	    Assert.assertNotNull(source);
	    
	    PatientIdentifierType idType = source.getIdentifierType();
	    Assert.assertEquals("00517", Context.getService(IdentifierSourceService.class).generateIdentifier(idType, "Testing"));
	    Assert.assertEquals("00518", Context.getService(IdentifierSourceService.class).generateIdentifier(idType, "Testing"));
	    Assert.assertEquals("00519", Context.getService(IdentifierSourceService.class).generateIdentifier(idType, "Testing"));
    }
    
    /**
     * @return whether the HIV identifier source has been set up
     */
    private boolean isHivIdentifierSourceSetup() {
    	try {
    		IdentifierSource source = service.getHivUniqueIdentifierSource();
    		return source != null;
    	} catch (Exception ex) {
    		return false;
    	}
    }

	/**
     * @see KenyaEmrService#getNextHivUniquePatientNumber(String)
     * @verifies get sequential numbers with mfl prefix
     */
    @Test
    public void getNextHivUniquePatientNumber_shouldGetSequentialNumbersWithMflPrefix() throws Exception {
    	Location loc = Context.getLocationService().getLocation(1);
		Assert.assertNotNull(loc);
		service.setDefaultLocation(loc);
		
    	service.setupHivUniqueIdentifierSource("00571");
    	Assert.assertEquals("1500100571", service.getNextHivUniquePatientNumber(null));
    	Assert.assertEquals("1500100572", service.getNextHivUniquePatientNumber(null));
    	Assert.assertEquals("1500100573", service.getNextHivUniquePatientNumber(null));
    	Assert.assertEquals("1500100574", service.getNextHivUniquePatientNumber(null));
    }

	/**
	 * @see KenyaEmrService#getReportManagersByTag(String)
	 */
	@Test
	public void getReportManagersByTag_shouldGetReportsByTag() {

		service.refreshReportManagers();

		final String[] TEST_TAGS = { "moh", "facility" };

		for (String tag : TEST_TAGS) {
			List<ReportManager> reports = service.getReportManagersByTag(tag);
			Assert.assertTrue(reports.size() > 0);
			for (ReportManager report : reports) {
				Assert.assertTrue(Arrays.asList(report.getTags()).contains(tag));
			}
		}
	}

	/**
	 * @see KenyaEmrService#getLocations(String, org.openmrs.Location, java.util.Map, boolean, Integer, Integer)
	 */
	@Test
	public void getLocations_shouldGetAllLocationsWithGivenAttributeValues() {
		LocationAttributeType mflCodeAttrType = Context.getLocationService().getLocationAttributeTypeByUuid(MetadataConstants.MASTER_FACILITY_CODE_LOCATION_ATTRIBUTE_TYPE_UUID);

		// Search for location #1 by MFL code and don't include retired
		Map<LocationAttributeType, Object> attrValues = new HashMap<LocationAttributeType, Object>();
		attrValues.put(mflCodeAttrType, "15001");
		List<Location> locations = service.getLocations(null, null, attrValues, false, null, null);
		Assert.assertEquals(1, locations.size());
		Assert.assertEquals(new Integer(1), locations.get(0).getLocationId());

		// Search for location #3 by MFL code and don't include retired
		attrValues = new HashMap<LocationAttributeType, Object>();
		attrValues.put(mflCodeAttrType, "15003");
		locations = service.getLocations(null, null, attrValues, false, null, null);
		Assert.assertEquals(0, locations.size());

		// Search for location #3 by MFL code and do include retired
		attrValues = new HashMap<LocationAttributeType, Object>();
		attrValues.put(mflCodeAttrType, "15003");
		locations = service.getLocations(null, null, attrValues, true, null, null);
		Assert.assertEquals(1, locations.size());
		Assert.assertEquals(new Integer(3), locations.get(0).getLocationId());
	}

	/**
	 * @see KenyaEmrService#getLocations(String, org.openmrs.Location, java.util.Map, boolean, Integer, Integer)
	 */
	@Test
	public void getLocations_shouldNotFindAnyLocationsIfNoneHaveGivenAttributeValues() {
		LocationAttributeType mflCodeAttrType = Context.getLocationService().getLocationAttributeTypeByUuid(MetadataConstants.MASTER_FACILITY_CODE_LOCATION_ATTRIBUTE_TYPE_UUID);
		Map<LocationAttributeType, Object> attrValues = new HashMap<LocationAttributeType, Object>();
		attrValues.put(mflCodeAttrType, "xxxxxx");
		List<Location> locations = service.getLocations(null, null, attrValues, true, null, null);
		Assert.assertEquals(0, locations.size());
	}
}