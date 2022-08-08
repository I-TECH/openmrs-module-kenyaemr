/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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