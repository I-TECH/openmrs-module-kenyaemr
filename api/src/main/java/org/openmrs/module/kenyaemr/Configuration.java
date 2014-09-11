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