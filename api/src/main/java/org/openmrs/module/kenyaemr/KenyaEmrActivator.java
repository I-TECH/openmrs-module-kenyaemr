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
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.GlobalProperty;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.api.db.ContextDAO;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.datatype.LocationDatatype;
import org.openmrs.module.kenyaemr.form.FormManager;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.util.ContextProvider;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class KenyaEmrActivator implements ModuleActivator {

	protected static final Log log = LogFactory.getLog(KenyaEmrActivator.class);

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

		Context.getService(KenyaEmrService.class).refreshReportManagers();
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

		log.info("Kenya OpenMRS EMR Module starting...");

		ContextDAO contextDAO = (ContextDAO)ContextProvider.getApplicationContext().getBean("contextDAO");

		try {
			// Work around until we get TRUNK-3739
			contextDAO.openSession();

			setupGlobalProperties();

			log.info(" > Setup global properties");

			boolean metadataUpdated = MetadataManager.setupMetadataPackages();

			log.info(" > Setup metadata packages (" + (metadataUpdated ? "Imported packages" : "Already up-to-date") + ")");

			FormManager.setupStandardForms();

			log.info(" > Setup form manager");

			RegimenManager.setupStandardRegimens();

			log.info(" > Setup regimen manager");

			Context.getService(KenyaEmrService.class).refreshReportManagers();

		} catch (Exception ex) {
			throw new RuntimeException("Failed to setup initial data", ex);
		} finally {
			contextDAO.closeSession();
		}

		log.info("Kenya OpenMRS EMR Module started");
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
	 * Public for testing
	 */
	public void setupGlobalProperties() {
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(KenyaEmrConstants.GP_DEFAULT_LOCATION);
		if (gp == null) {
			gp = new GlobalProperty();
			gp.setProperty(KenyaEmrConstants.GP_DEFAULT_LOCATION);
			gp.setDescription("The facility for which this installation is configured. Visits and encounters will be created with this location value.");
			gp.setDatatypeClassname(LocationDatatype.class.getName());
			Context.getAdministrationService().saveGlobalProperty(gp);
		}
	}
}