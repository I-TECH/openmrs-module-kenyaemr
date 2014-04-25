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
import org.openmrs.module.kenyacore.report.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.Date;

/**
 * Cohort definition based on a calculation that returns dates
 */
public class DateCalculationCohortDefinition extends CalculationCohortDefinition {

	@ConfigurationProperty(required = false, group = "date range")
	private Date onOrAfter;

	@ConfigurationProperty(required = false, group = "date range")
	private Date onOrBefore;

	public DateCalculationCohortDefinition() {
	}

	/**
	 * Constructs a new calculation based cohort definition
	 * @param calculation the calculation
	 */
	public DateCalculationCohortDefinition(PatientCalculation calculation) {
		super(calculation);
	}
	
	/**
	 * @return the onOrAfter
	 */
	public Date getOnOrAfter() {
		return onOrAfter;
	}
	
	/**
	 * @param onOrAfter the onOrAfter to set
	 */
	public void setOnOrAfter(Date onOrAfter) {
		this.onOrAfter = onOrAfter;
	}
	
	/**
	 * @return the onOrBefore
	 */
	public Date getOnOrBefore() {
		return onOrBefore;
	}
	
	/**
	 * @param onOrBefore the onOrBefore to set
	 */
	public void setOnOrBefore(Date onOrBefore) {
		this.onOrBefore = onOrBefore;
	}
}