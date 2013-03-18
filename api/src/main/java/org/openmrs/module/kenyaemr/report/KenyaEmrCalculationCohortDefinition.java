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
package org.openmrs.module.kenyaemr.report;

import java.util.Date;

import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Cohort definition based on a calculation
 */
public class KenyaEmrCalculationCohortDefinition extends BaseCohortDefinition {
	
	@ConfigurationProperty(required = true, group = "calculation")
	private BaseEmrCalculation calculation;
	
	@ConfigurationProperty(required = false, group = "date range")
	private Date resultOnOrAfter;
	
	@ConfigurationProperty(required = false, group = "date range")
	private Date resultOnOrBefore;

	public KenyaEmrCalculationCohortDefinition() {
	}
	
	/**
	 * Constructs a new calculation based cohort definition
	 * @param calculation the calculation
	 */
	public KenyaEmrCalculationCohortDefinition(BaseEmrCalculation calculation) {
		setCalculation(calculation);
		setName(calculation.getName());
		setDescription(calculation.getDescription());
	}
	
	/**
	 * @return the calculation
	 */
	public BaseEmrCalculation getCalculation() {
		return calculation;
	}

	/**
	 * @param calculation the calculation to set
	 */
	public void setCalculation(BaseEmrCalculation calculation) {
		this.calculation = calculation;
	}
	
	/**
	 * @return the resultOnOrAfter
	 */
	public Date getResultOnOrAfter() {
		return resultOnOrAfter;
	}
	
	/**
	 * @param resultOnOrAfter the resultOnOrAfter to set
	 */
	public void setResultOnOrAfter(Date resultOnOrAfter) {
		this.resultOnOrAfter = resultOnOrAfter;
	}
	
	/**
	 * @return the resultOnOrBefore
	 */
	public Date getResultOnOrBefore() {
		return resultOnOrBefore;
	}
	
	/**
	 * @param resultOnOrBefore the resultOnOrBefore to set
	 */
	public void setResultOnOrBefore(Date resultOnOrBefore) {
		this.resultOnOrBefore = resultOnOrBefore;
	}
}