/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyaemr.visit.EmrVisitAssignmentHandler;
import org.openmrs.util.OpenmrsConstants;

/**
 * Manages the KenyaEMR configuration
 */
public class Configuration {

	protected static final Log log = LogFactory.getLog(Configuration.class);

	/**
	 * Setup required global properties
	 */
	public static void configure() {
		CoreUtils.setGlobalProperty(OpenmrsConstants.GP_VISIT_ASSIGNMENT_HANDLER, EmrVisitAssignmentHandler.class.getName());
	}
}