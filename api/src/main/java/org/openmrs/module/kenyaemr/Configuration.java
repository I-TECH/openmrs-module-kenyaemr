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
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.system.ExternalRequirement;
import org.openmrs.module.kenyaemr.form.EmrVisitAssignmentHandler;
import org.openmrs.util.OpenmrsConstants;

import java.util.List;

/**
 * Manages the KenyaEMR configuration
 */
public class Configuration {

	protected static final Log log = LogFactory.getLog(Configuration.class);

	/**
	 * Gets the external requirements
	 * @return the external requirements
	 */
	public static List<ExternalRequirement> getExternalRequirements() {
		return Context.getRegisteredComponents(ExternalRequirement.class);
	}

	/**
	 * Setup required global properties
	 */
	public static void configure() {
		setGlobalProperty(OpenmrsConstants.GP_VISIT_ASSIGNMENT_HANDLER, EmrVisitAssignmentHandler.class.getName());
	}

	/**
	 * Sets an untyped global property
	 * @param property the property name
	 * @param value the property value
	 */
	public static void setGlobalProperty(String property, String value) {
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(property);
		if (gp == null) {
			throw new IllegalArgumentException("Cannot find global property '" + property + "'");
		}
		gp.setPropertyValue(value);
		Context.getAdministrationService().saveGlobalProperty(gp);
	}
}