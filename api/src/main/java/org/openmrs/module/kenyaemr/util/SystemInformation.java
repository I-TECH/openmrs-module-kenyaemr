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

package org.openmrs.module.kenyaemr.util;

import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.util.OpenmrsConstants;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility class for fetching system information
 */
public class SystemInformation {

	/**
	 * Gets the module version
	 * @return the version
	 */
	public static String getModuleVersion() {
		return ModuleFactory.getModuleById(EmrConstants.MODULE_ID).getVersion();
	}

	/**
	 * Gets the module build properties
	 * @return the build properties map or null if not available
	 */
	public static BuildProperties getModuleBuildProperties() {
		return Context.getRegisteredComponents(BuildProperties.class).get(0);
	}

	/**
	 * Gets a map of system information data points
	 * @return the map
	 */
	public static Map<String, Object> getData() {
		Map<String, Object> info = new LinkedHashMap<String, Object>();
		info.put("openmrs.version", OpenmrsConstants.OPENMRS_VERSION);
		info.put("kenyaemr.version", getModuleVersion());

		info.put("server.timezone", Calendar.getInstance().getTimeZone().getID());

		Runtime jvm = Runtime.getRuntime();
		info.put("jvm.freememory", jvm.freeMemory());
		info.put("jvm.totalmemory", jvm.totalMemory());
		info.put("jvm.maxmemory", jvm.maxMemory());
		info.put("jvm.processors", jvm.availableProcessors());
		return info;
	}
}