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

package org.openmrs.module.kenyaemr.reporting.builder.patientlist;

import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.EmrCalculationCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.map;

/**
 * Base implementation for row-per-patient reports based on calculations
 */
public abstract class BasePatientCalculationReportBuilder extends BasePatientListReportBuilder {
	
	private BaseEmrCalculation calculation;

	/**
	 * Constructs a calculation based patient list report
	 * @param calculation the calculation
	 */
	public BasePatientCalculationReportBuilder(BaseEmrCalculation calculation) {
		this.calculation = calculation;
	}

	/**
	 * Gets the calculation
	 * @return the calculation
	 */
	public BaseEmrCalculation getCalculation() {
		return calculation;
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.builder.ReportBuilder#getName()
	 */
	@Override
	public String getName() {
		return calculation.getName();
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.builder.ReportBuilder#getDescription()
	 */
	@Override
	public String getDescription() {
		return calculation.getDescription();
	}

	/**
	 * Builds the data set
	 * @return the data set
	 */
	@Override
	protected PatientDataSetDefinition buildDataSet() {
		PatientDataSetDefinition dsd = new PatientDataSetDefinition(calculation.getName() + " DSD");
		addColumns(dsd);
		dsd.addRowFilter(map(new EmrCalculationCohortDefinition(calculation)));
		return dsd;
	}
}