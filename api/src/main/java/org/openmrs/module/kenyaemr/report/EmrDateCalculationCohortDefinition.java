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

import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.Date;

/**
 * Cohort definition based on a calculation that returns dates
 */
public class EmrDateCalculationCohortDefinition extends EmrCalculationCohortDefinition {

	@ConfigurationProperty(required = true, group = "date range")
	private Date resultOnOrAfter;

	@ConfigurationProperty(required = true, group = "date range")
	private Date resultOnOrBefore;

	public EmrDateCalculationCohortDefinition() {
	}

	/**
	 * Constructs a new calculation based cohort definition
	 * @param calculation the calculation
	 */
	public EmrDateCalculationCohortDefinition(BaseEmrCalculation calculation) {
		super(calculation);
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