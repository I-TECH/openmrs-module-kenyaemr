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

package org.openmrs.module.kenyaemr.reporting;

/**
 * Describes a column in a CohortIndicatorDataSetDefinition
 */
public class ColumnParameters {

	private String name;
	private String label;
	private String dimensions;

	/**
	 * Default constructor
	 * @param name the name
	 * @param label the label
	 * @param dimensions the dimension parameters
	 */
	public ColumnParameters(String name, String label, String dimensions) {
		this.name = name;
		this.label = label;
		this.dimensions = dimensions;
	}

	/**
	 * Gets the name
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the label
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Gets the dimension parameters
	 * @return the dimension parameters
	 */
	public String getDimensions() {
		return dimensions;
	}
}