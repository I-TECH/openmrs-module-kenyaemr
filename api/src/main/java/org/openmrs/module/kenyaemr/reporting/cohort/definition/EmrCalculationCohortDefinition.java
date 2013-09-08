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

package org.openmrs.module.kenyaemr.reporting.cohort.definition;

import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Cohort definition based on a calculation
 */
public class EmrCalculationCohortDefinition extends BaseCohortDefinition {
	
	@ConfigurationProperty(required = true, group = "calculation")
	private PatientCalculation calculation;

	@ConfigurationProperty(group = "calculation")
	private Date onDate;

	@ConfigurationProperty(group = "calculation")
	private Object withResult;

	@ConfigurationProperty(group = "calculation")
	private Map<String, Object> calculationParameters;

	/**
	 * Default constructor
	 */
	public EmrCalculationCohortDefinition() {
	}

	/**
	 * Constructs a new calculation based cohort definition
	 * @param calculation the calculation
	 */
	public EmrCalculationCohortDefinition(PatientCalculation calculation) {
		setCalculation(calculation);
	}
	
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
	 * Gets the date for which to calculate
	 * @return the date
	 */
	public Date getOnDate() {
		return onDate;

	}

	/**
	 * Sets the date for which to calculate
	 * @param onDate the date
	 */
	public void setOnDate(Date onDate) {
		this.onDate = onDate;
	}

	/**
	 * Gets the result value required for inclusion in the cohort
	 * @return the result value
	 */
	public Object getWithResult() {
		return withResult;
	}

	/**
	 * Sets the result value required for inclusion in the cohort
	 * @param withResult the result value
	 */
	public void setWithResult(Object withResult) {
		this.withResult = withResult;
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
}