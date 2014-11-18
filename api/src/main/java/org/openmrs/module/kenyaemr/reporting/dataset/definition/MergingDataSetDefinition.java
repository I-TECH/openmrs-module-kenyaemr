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