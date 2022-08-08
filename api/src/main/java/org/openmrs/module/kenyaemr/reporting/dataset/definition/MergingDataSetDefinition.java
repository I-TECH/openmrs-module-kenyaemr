/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.dataset.definition;

import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * A data set made by merging other data sets
 */
public class MergingDataSetDefinition extends BaseDataSetDefinition {

	/**
	 * Merge order options
	 */
	public enum MergeOrder {
		NONE,
		NAME,
		LABEL
	}

	@ConfigurationProperty
	private List<DataSetDefinition> dataSetDefinitions;

	@ConfigurationProperty
	private MergeOrder mergeOrder;

	/**
	 * Gets the contained data set definitions
	 * @return the data set definitions
	 */
	public List<DataSetDefinition> getDataSetDefinitions() {
		if (dataSetDefinitions == null) {
			dataSetDefinitions = new ArrayList<DataSetDefinition>();
		}
		return dataSetDefinitions;
	}

	/**
	 * Sets the contained data set definitions
	 * @param dataSetDefinitions the data set definitions
	 */
	public void setDataSetDefinitions(List<DataSetDefinition> dataSetDefinitions) {
		this.dataSetDefinitions = dataSetDefinitions;
	}

	/**
	 * Adds a contained data set definition
	 * @param dataSetDefinition the data set definition
	 */
	public void addDataSetDefinition(DataSetDefinition dataSetDefinition) {
		getDataSetDefinitions().add(dataSetDefinition);
	}

	/**
	 * Gets the merge order
	 * @return the merge order
	 */
	public MergeOrder getMergeOrder() {
		return mergeOrder;
	}

	/**
	 * Sets the merge order
	 * @param mergeOrder the merge order
	 */
	public void setMergeOrder(MergeOrder mergeOrder) {
		this.mergeOrder = mergeOrder;
	}
}