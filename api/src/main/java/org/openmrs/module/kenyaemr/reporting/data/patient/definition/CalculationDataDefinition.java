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

package org.openmrs.module.kenyaemr.reporting.data.patient.definition;

import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.HashMap;
import java.util.Map;

/**
 * Patient data definition based on a calculation
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class CalculationDataDefinition extends BaseDataDefinition implements PatientDataDefinition {

	public static final long serialVersionUID = 1L;

	@ConfigurationProperty(required = true, group = "calculation")
	private PatientCalculation calculation;

	@ConfigurationProperty(group = "calculation")
	private Map<String, Object> calculationParameters;

	/**
	 * Default Constructor
	 */
	public CalculationDataDefinition() {
		super();
	}

	/**
	 * Constructor to populate name and calculation
	 */
	public CalculationDataDefinition(String name, PatientCalculation calculation) {
		super(name);

		setCalculation(calculation);
	}

	//***** INSTANCE METHODS *****

	/**
	 * @return the calculation
	 */
	public PatientCalculation getCalculation() {
		return calculation;
	}

	/**
	 * @param calculation the calculation to set
	 */
	public void setCalculation(PatientCalculation calculation) {
		this.calculation = calculation;
	}

	/**
	 * Gets the calculation parameters
	 * @return the calculation parameters
	 */
	public Map<String, Object> getCalculationParameters() {
		return calculationParameters;
	}

	/**
	 * Sets the calculation parameters
	 * @param calculationParameters the calculation parameters
	 */
	public void setCalculationParameters(Map<String, Object> calculationParameters) {
		this.calculationParameters = calculationParameters;
	}

	/**
	 * Adds a calculation parameter
	 * @param name the name
	 * @param value the value
	 */
	public void addCalculationParameter(String name, Object value) {
		if (calculationParameters == null) {
			calculationParameters = new HashMap<String, Object>();
		}

		calculationParameters.put(name, value);
	}

	/**
	 * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
	 */
	@Override
	public Class<?> getDataType() {
		return CalculationResult.class;
	}
}
