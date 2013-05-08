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

package org.openmrs.module.kenyaemr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.kenyaemr.datatype.LocationDatatype;
import org.openmrs.module.kenyaemr.util.KenyaEmrUtils;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class KenyaEmrActivator implements ModuleActivator {

	protected static final Log log = LogFactory.getLog(KenyaEmrActivator.class);

	private static final String PACKAGES_FILENAME = "packages.xml";

	private static final String REGIMENS_FILENAME = "regimens.xml";

	private static final String LABTESTS_FILENAME = "lab.xml";

	static {
		// Possibly bad practice but we really want to see the log messages
		LogManager.getLogger(KenyaEmrActivator.class).setLevel(Level.INFO);
	}

	/**
	 * @see ModuleActivator#willRefreshContext()
	 */
	public void willRefreshContext() {
		log.info("Kenya EMR context refreshing...");
	}

	/**
	 * @see ModuleActivator#contextRefreshed()
	 */
	public void contextRefreshed() {
		log.info("Kenya EMR context refreshed");
	}

	/**
	 * @see ModuleActivator#willStart()
	 */
	public void willStart() {
		log.info("Kenya EMR starting...");
	}

	/**
	 * @see ModuleActivator#started()
	 * @should install initial data only once
	 */
	public void started() {
		try {
			checkRequirements();

			setupGlobalProperties();

			log.info("Setup core global properties");

			boolean metadataUpdated = setupStandardMetadata();

			log.info("Setup core metadata packages (" + (metadataUpdated ? "imported packages" : "already up-to-date") + ")");

			setupStandardRegimens();

			log.info("Setup core regimens");

			setupStandardLabTests();

			log.info("Setup core lab tests");

		} catch (Exception ex) {
			log.error("Cancelling module startup due to error");

			// Stop module if exception was thrown
			Module mod = ModuleFactory.getModuleById(KenyaEmrConstants.MODULE_ID);
			ModuleFactory.stopModule(mod);

			throw new RuntimeException("Failed to start Kenya EMR module", ex);
		}

		log.info("Kenya EMR started");
	}

	/**
	 * @see ModuleActivator#willStop()
	 */
	public void willStop() {
		log.info("Kenya EMR stopping...");
	}

	/**
	 * @see ModuleActivator#stopped()
	 */
	public void stopped() {
		log.info("Kenya EMR stopped");
	}

	/**
	 * Checks the requirements of this module
	 */
	protected void checkRequirements() {
		// Check concept dictionary version
   		String conceptsVersion = Context.getAdministrationService().getGlobalProperty(KenyaEmrConstants.GP_CONCEPTS_VERSION);
		if (conceptsVersion == null || !KenyaEmrUtils.checkCielVersions(KenyaEmrConstants.REQUIRED_CONCEPTS_VERSION, conceptsVersion)) {
			throw new RuntimeException("Module requires concepts version: " + KenyaEmrConstants.REQUIRED_CONCEPTS_VERSION);
		}
		else {
			log.info("Detected concept dictionary version " + conceptsVersion);
		}
	}

	/**
	 * Setup required global properties
	 *
	 * (Public for testing)
	 */
	public void setupGlobalProperties() {
		ensureGlobalPropertyExists(
				KenyaEmrConstants.GP_DEFAULT_LOCATION,
				"The facility for which this installation is configured. Visits and encounters will be created with this location value.",
				LocationDatatype.class
		);
	}

	/**
	 * Setup the standard metadata packages
	 * @return
	 */
	protected boolean setupStandardMetadata() {
		try {
			InputStream stream = getClass().getClassLoader().getResourceAsStream(PACKAGES_FILENAME);
			return KenyaEmr.getInstance().getMetadataManager().loadPackagesFromXML(stream, null);
		}
		catch (Exception ex) {
			throw new RuntimeException("Cannot find " + PACKAGES_FILENAME + ". Make sure it's in api/src/main/resources");
		}
	}

	/**
	 * Setup the standard lab tests
	 */
	protected void setupStandardLabTests() {
		try {
			InputStream stream = getClass().getClassLoader().getResourceAsStream(LABTESTS_FILENAME);
			KenyaEmr.getInstance().getLabManager().loadTestsFromXML(stream);
		}
		catch (Exception ex) {
			throw new RuntimeException("Cannot find " + LABTESTS_FILENAME + ". Make sure it's in api/src/main/resources");
		}
	}

	/**
	 * Setup the standard regimens from XML
	 * @throws Exception if error occurs
	 */
	protected void setupStandardRegimens() {
		try {
			InputStream stream = getClass().getClassLoader().getResourceAsStream(REGIMENS_FILENAME);
			KenyaEmr.getInstance().getRegimenManager().loadDefinitionsFromXML(stream);
		}
		catch (Exception ex) {
			throw new RuntimeException("Cannot find " + REGIMENS_FILENAME + ". Make sure it's in api/src/main/resources", ex);
		}
	}

	/**
	 * Creates an empty global property if it doesn't exist
	 * @param property the property name
	 * @param description the property description
	 * @param dataType the property value data type
	 */
	protected void ensureGlobalPropertyExists(String property, String description, Class dataType) {
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(property);
		if (gp == null) {
			gp = new GlobalProperty();
			gp.setProperty(property);
			gp.setDescription(description);
			gp.setDatatypeClassname(dataType.getName());
			Context.getAdministrationService().saveGlobalProperty(gp);
		}
	}
}