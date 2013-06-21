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
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.kenyaemr.datatype.LocationDatatype;
import org.openmrs.module.kenyaemr.form.EmrVisitAssignmentHandler;
import org.openmrs.module.kenyaemr.util.KenyaEmrUtils;
import org.openmrs.util.OpenmrsConstants;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class KenyaEmrActivator implements ModuleActivator {

	protected static final Log log = LogFactory.getLog(KenyaEmrActivator.class);

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
	 * @see ModuleActivator#willStart()
	 */
	public void willStart() {
		log.info("Kenya EMR starting...");
	}

	/**
	 * @see ModuleActivator#contextRefreshed()
	 */
	public void contextRefreshed() {
		log.info("Context refreshed. Refreshing all content managers...");

		configure();

		KenyaEmr.getInstance().refresh();

		log.info("Refreshed all content managers");
	}

	/**
	 * @see ModuleActivator#started()
	 */
	public void started() {
		checkRequirements();

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
		if (!Dictionary.hasRequiredDatabaseVersion()) {
			throw new RuntimeException("Module requires concepts version: " + Dictionary.REQUIRED_DATABASE_VERSION);
		}
		else {
			log.info("Detected concept dictionary version " + Dictionary.getDatabaseVersion());
		}
	}

	/**
	 * Setup required global properties
	 */
	protected void configure() {
		ensureGlobalPropertyExists(
				KenyaEmrConstants.GP_DEFAULT_LOCATION,
				"The facility for which this installation is configured. Visits and encounters will be created with this location value.",
				LocationDatatype.class
		);

		setExistingGlobalProperty(OpenmrsConstants.GP_VISIT_ASSIGNMENT_HANDLER, EmrVisitAssignmentHandler.class.getName());
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

	/**
	 * Saves an untyped global property
	 * @param property the property name
	 * @param value the property value
	 * @return the global property
	 */
	protected void setExistingGlobalProperty(String property, Object value) {
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(property);
		if (gp == null) {
			throw new IllegalArgumentException("Cannot find global property '" + property + "'");
		}

		gp.setPropertyValue(String.valueOf(value));
		Context.getAdministrationService().saveGlobalProperty(gp);
	}
}