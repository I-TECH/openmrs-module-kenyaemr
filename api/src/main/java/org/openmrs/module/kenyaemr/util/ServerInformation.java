/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.util;

import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsConstants;

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