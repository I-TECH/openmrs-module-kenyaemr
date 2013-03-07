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
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.kenyaemr.datatype.LocationDatatype;
import org.openmrs.module.kenyaemr.form.FormManager;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.report.ReportManager;
import org.openmrs.module.kenyaemr.util.KenyaEmrUtils;

import java.io.InputStream;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class KenyaEmrActivator implements ModuleActivator {

	protected static final Log log = LogFactory.getLog(KenyaEmrActivator.class);

	private static final String PACKAGES_FILENAME = "packages.xml";

	private static final String REGIMENS_FILENAME = "regimens.xml";

	/**
	 * @see ModuleActivator#willRefreshContext()
	 */
	public void willRefreshContext() {
		log.info("Refreshing Kenya OpenMRS EMR Module");
	}

	/**
	 * @see ModuleActivator#contextRefreshed()
	 */
	public void contextRefreshed() {
		log.info("Kenya OpenMRS EMR Module refreshed");

		ReportManager.refreshReportBuilders();
	}

	/**
	 * @see ModuleActivator#willStart()
	 */
	public void willStart() {
		log.info("Starting Kenya OpenMRS EMR Module");
	}

	/**
	 * @see ModuleActivator#started()
	 * @should install initial data only once
	 */
	public void started() {
		// Temporarily override logging settings to make sure our messages get out
		Level logLevel = KenyaEmrUtils.changeLogLevel(KenyaEmrActivator.class, Level.INFO);

		log.info("=========== Kenya OpenMRS EMR Module startup ===========");

		try {
			setupGlobalProperties();

			log.info(" > Setup core global properties");

			boolean metadataUpdated = setupStandardMetadata();

			log.info(" > Setup core metadata packages (" + (metadataUpdated ? "imported packages" : "already up-to-date") + ")");

			setupStandardForms();

			log.info(" > Setup core forms");

			setupStandardRegimens();

			log.info(" > Setup core regimens");

			ReportManager.refreshReportBuilders();

			log.info(" > Setup core reports (found " + ReportManager.getAllReportBuilders().size() +" report builders)");

		} catch (Exception ex) {
			throw new RuntimeException("Failed to setup initial data", ex);
		}

		log.info("=========== Kenya OpenMRS EMR Module started ===========");

		// Restore existing logging settings
		KenyaEmrUtils.changeLogLevel(KenyaEmrActivator.class, logLevel);
	}

	/**
	 * @see ModuleActivator#willStop()
	 */
	public void willStop() {
		log.info("Stopping Kenya OpenMRS EMR Module");
	}

	/**
	 * @see ModuleActivator#stopped()
	 */
	public void stopped() {
		log.info("Kenya OpenMRS EMR Module stopped");
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

			return MetadataManager.loadPackagesFromXML(stream, null);
		}
		catch (Exception ex) {
			throw new RuntimeException("Cannot find " + PACKAGES_FILENAME + ". Make sure it's in api/src/main/resources");
		}
	}

	/**
	 * Setup the standard forms
	 */
	protected void setupStandardForms() {
		//FormManager.clear();

		// These could be loaded from XML instead of hard-coding in the manager class
		FormManager.setupStandardForms();
	}

	/**
	 * Setup the standard regimens from XML
	 * @throws Exception if error occurs
	 */
	protected void setupStandardRegimens() {
		try {
			//RegimenManager.clear();

			InputStream stream = getClass().getClassLoader().getResourceAsStream(REGIMENS_FILENAME);
			RegimenManager.loadDefinitionsFromXML(stream);
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