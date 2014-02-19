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
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.util.OpenmrsConstants;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Utility class for fetching server information
 */
public class ServerInformation {

	/**
	 * Gets a map of all information sections
	 * @return the information sections
	 */
	public static Map<String, Object> getAllInformation() {
		return map(
				"system", getSystemInformation(),
				"runtime", getRuntimeInformation(),
				"openmrs", getOpenmrsInformation(),
				"kenyaemr", getKenyaemrInformation()
		);
	}

	/**
	 * Gets system information
	 * @return the data points
	 */
	public static Map<String, Object> getSystemInformation() {
		Properties properties = System.getProperties();
		return map(
				"os", map(
						"name", properties.getProperty("os.name"),
						"arch", properties.getProperty("os.arch"),
						"version", properties.getProperty("os.version")
				),
				"java", map(
						"vendor", properties.getProperty("java.vendor"),
						"version", properties.getProperty("java.version")
				),
				"user", map(
					"language", properties.getProperty("user.language"),
					"timezone", properties.getProperty("user.timezone")
				)
		);
	}

	/**
	 * Gets runtime information
	 * @return the data points
	 */
	public static Map<String, Object> getRuntimeInformation() {
		Runtime runtime = Runtime.getRuntime();
		return map(
				"freememory", runtime.freeMemory(),
				"totalmemory", runtime.totalMemory(),
				"maxmemory", runtime.maxMemory(),
				"processors", runtime.availableProcessors()
		);
	}

	/**
	 * Gets OpenMRS information
	 * @return the data points
	 */
	public static Map<String, Object> getOpenmrsInformation() {
		return map(
				"version", OpenmrsConstants.OPENMRS_VERSION
		);
	}

	/**
	 * Gets KenyaEMR information
	 * @return the data points
	 */
	public static Map<String, Object> getKenyaemrInformation() {
		BuildProperties build = Context.getRegisteredComponents(BuildProperties.class).get(0);
		return map(
				"version", build.getVersion(),
				"buildDate", build.getBuildDate()
		);
	}

	/**
	 * Helper method for constructing maps
	 * @param keyValPairs key-value pairs
	 * @return the map
	 */
	protected static Map<String, Object> map(Object... keyValPairs) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (int p = 0; p < keyValPairs.length; p += 2) {
			String key = (String) keyValPairs[p];
			Object val = keyValPairs[p + 1];
			map.put(key, val);
		}
		return map;
	}
}