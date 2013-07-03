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

import java.util.Map;

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
		log.info("KenyaEMR context refreshing...");
	}

	/**
	 * @see ModuleActivator#willStart()
	 */
	public void willStart() {
		log.info("KenyaEMR starting...");
	}

	/**
	 * @see ModuleActivator#contextRefreshed()
	 */
	public void contextRefreshed() {
		log.info("Context refreshed. Refreshing all content managers...");

		Configuration.configure();

		KenyaEmr.getInstance().refresh();

		log.info("Refreshed all content managers");
	}

	/**
	 * @see ModuleActivator#started()
	 */
	public void started() {
		log.info("Checking KenyaEMR requirements...");

		for (Configuration.Requirement requirement : Configuration.checkRequirements()) {
			String status = requirement.isPass() ? "PASS" : "FAIL";
			String message = " * " + requirement.getName() + " " + requirement.getVersionRequired() + ", found " + requirement.getVersionFound() + " (" + status + ")";

			if (requirement.isPass()) {
				log.info(message);
			} else {
				log.warn(message);
			}
		}

		log.info("KenyaEMR started");
	}

	/**
	 * @see ModuleActivator#willStop()
	 */
	public void willStop() {
		log.info("KenyaEMR stopping...");
	}

	/**
	 * @see ModuleActivator#stopped()
	 */
	public void stopped() {
		log.info("KenyaEMR stopped");
	}
}