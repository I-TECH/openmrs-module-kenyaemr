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

	public final static String OPENMRS_VERSION = "openmrs.version";
	public final static String KENYAEMR_VERSION = "kenyaemr.version";
	public final static String SERVER_TIMEZONE = "server.timezone";
	public final static String JVM_FREEMEMORY = "jvm.freememory";
	public final static String JVM_TOTALMEMORY = "jvm.totalmemory";
	public final static String JVM_MAXMEMORY = "jvm.maxmemory";
	public final static String JVM_PROCESSORS = "jvm.processors";

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
		info.put(OPENMRS_VERSION, OpenmrsConstants.OPENMRS_VERSION);
		info.put(KENYAEMR_VERSION, getModuleVersion());

		info.put(SERVER_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

		Runtime jvm = Runtime.getRuntime();
		info.put(JVM_FREEMEMORY, jvm.freeMemory());
		info.put(JVM_TOTALMEMORY, jvm.totalMemory());
		info.put(JVM_MAXMEMORY, jvm.maxMemory());
		info.put(JVM_PROCESSORS, jvm.availableProcessors());
		return info;
	}
}