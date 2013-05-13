package org.openmrs.module.kenyaemr.identifier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.H2Dialect;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleConstants;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.processor.SequentialIdentifierGeneratorProcessor;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.kenyaemr.KenyaEmrActivator;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.TestUtil;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 *
 */
public class IdentifierManagerTest extends BaseModuleContextSensitiveTest {

	protected static final Log log = LogFactory.getLog(IdentifierManagerTest.class);

	@Autowired
	IdentifierManager identifierManager;

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");

		new KenyaEmrActivator().setupGlobalProperties();

		Context.getService(IdentifierSourceService.class).registerProcessor(SequentialIdentifierGenerator.class, new SequentialIdentifierGeneratorProcessor());
	}

	@Override
	public Properties getRuntimeProperties() {

		// cache the properties for subsequent calls
		if (runtimeProperties == null)
			runtimeProperties = TestUtil.getRuntimeProperties(getWebappName());

		// if we're using the in-memory hypersonic database, add those
		// connection properties here to override what is in the runtime
		// properties
		if (useInMemoryDatabase() == true) {
			runtimeProperties.setProperty(Environment.DIALECT, H2Dialect.class.getName());
			runtimeProperties.setProperty(Environment.URL, "jdbc:h2:mem:openmrs;MVCC=true;DB_CLOSE_DELAY=30");
			runtimeProperties.setProperty(Environment.DRIVER, "org.h2.Driver");
			runtimeProperties.setProperty(Environment.USER, "sa");
			runtimeProperties.setProperty(Environment.PASS, "");

			// these two properties need to be set in case the user has this exact
			// phrasing in their runtime file.
			runtimeProperties.setProperty("connection.username", "sa");
			runtimeProperties.setProperty("connection.password", "");

			// automatically create the tables defined in the hbm files
			runtimeProperties.setProperty(Environment.HBM2DDL_AUTO, "create-drop");
		}

		// we don't want to try to load core modules in tests
		runtimeProperties.setProperty(ModuleConstants.IGNORE_CORE_MODULES_PROPERTY, "true");

		try {
			File tempappdir = File.createTempFile("appdir-for-unit-tests-", "");
			tempappdir.delete(); // so we can make it into a directory
			tempappdir.mkdir(); // turn it into a directory
			tempappdir.deleteOnExit(); // clean up when we're done with tests

			runtimeProperties.setProperty(OpenmrsConstants.APPLICATION_DATA_DIRECTORY_RUNTIME_PROPERTY, tempappdir
					.getAbsolutePath());
			OpenmrsConstants.APPLICATION_DATA_DIRECTORY = tempappdir.getAbsolutePath();
		}
		catch (IOException e) {
			log.error("Unable to create temp dir", e);
		}

		return runtimeProperties;
	}

	/**
	 * @see IdentifierManager#setupMrnIdentifierSource(String)
	 * @verifies set up an identifier source
	 */
	@Test
	public void setupMrnIdentifierSource_shouldSetUpAnIdentifierSource() throws Exception {
		Assert.assertFalse(isMrnIdentifierSourceSetup());
		identifierManager.setupMrnIdentifierSource("4");
		Assert.assertTrue(isMrnIdentifierSourceSetup());
		IdentifierSource source = identifierManager.getMrnIdentifierSource();
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
			IdentifierSource source = identifierManager.getMrnIdentifierSource();
			return source != null;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * @see IdentifierManager#setupMrnIdentifierSource(String)
	 * @verifies fail if already set up
	 */
	@Test
	public void setupMrnIdentifierSource_shouldFailIfAlreadySetUp() throws Exception {
		identifierManager.setupMrnIdentifierSource("4");
		try {
			identifierManager.setupMrnIdentifierSource("4");
			Assert.fail("Shouldn't be allowed to set up twice");
		} catch (Exception ex) {
			// pass
		}
	}

	/**
	 * @see IdentifierManager#setupHivUniqueIdentifierSource(String)
	 * @verifies fail if already set up
	 */
	@Test
	public void setupHivUniqueIdentifierSource_shouldFailIfAlreadySetUp() throws Exception {
		identifierManager.setupHivUniqueIdentifierSource("00517");
		try {
			identifierManager.setupHivUniqueIdentifierSource("00517");
			Assert.fail("Shouldn't be allowed to set up twice");
		} catch (Exception ex) {
			// pass
		}
	}

	/**
	 * @see IdentifierManager#setupHivUniqueIdentifierSource(String)
	 * @verifies set up an identifier source
	 */
	@Test
	public void setupHivUniqueIdentifierSource_shouldSetUpAnIdentifierSource() throws Exception {
		Assert.assertFalse(isHivIdentifierSourceSetup());
		identifierManager.setupHivUniqueIdentifierSource("00517");
		Assert.assertTrue(isHivIdentifierSourceSetup());
		IdentifierSource source = identifierManager.getHivUniqueIdentifierSource();
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
			IdentifierSource source = identifierManager.getHivUniqueIdentifierSource();
			return source != null;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * @see IdentifierManager#getNextHivUniquePatientNumber(String)
	 * @verifies get sequential numbers with mfl prefix
	 */
	@Test
	public void getNextHivUniquePatientNumber_shouldGetSequentialNumbersWithMflPrefix() throws Exception {
		Location loc = Context.getLocationService().getLocation(1);
		Assert.assertNotNull(loc);
		Context.getService(KenyaEmrService.class).setDefaultLocation(loc);

		identifierManager.setupHivUniqueIdentifierSource("00571");
		Assert.assertEquals("1500100571", identifierManager.getNextHivUniquePatientNumber(null));
		Assert.assertEquals("1500100572", identifierManager.getNextHivUniquePatientNumber(null));
		Assert.assertEquals("1500100573", identifierManager.getNextHivUniquePatientNumber(null));
		Assert.assertEquals("1500100574", identifierManager.getNextHivUniquePatientNumber(null));
	}
}