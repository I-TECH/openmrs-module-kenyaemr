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

import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyacore.report.CalculationReportDescriptor;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.EmrCalculationCohortDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

/**
 * Base implementation for calculation based patient list reports
 */
public abstract class BasePatientListReport extends CalculationReportDescriptor implements ReportBuilder {

	/**
	 * @see ReportBuilder#isExcelRenderable()
	 */
	@Override
	public boolean isExcelRenderable() {
		return false;
	}

	/**
	 * @see ReportBuilder#getDefinition()
	 */
	@Override
	public ReportDefinition getDefinition() {
		ReportDefinition rd = new ReportDefinition();
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.addDataSetDefinition(buildDataSet(), null);
		return rd;
	}

	/**
	 * Builds the data set
	 * @return the data set
	 */
	protected PatientDataSetDefinition buildDataSet() {
		PatientCalculation calc = CalculationUtils.instantiateCalculation(getCalculation(), null);
		EmrCalculationCohortDefinition cd = new EmrCalculationCohortDefinition(calc);
		cd.setName(getName());

		PatientDataSetDefinition dsd = new PatientDataSetDefinition(getName() + " DSD");
		dsd.addRowFilter(EmrReportingUtils.map(cd));
		addColumns(dsd);
		return dsd;
	}

	/**
	 * Override this if you don't want the default (HIV ID, name, sex, age)
	 * @param dsd this will be modified by having columns added
	 */
	protected void addColumns(PatientDataSetDefinition dsd) {
		addStandardColumns(dsd);
	}

	/**
	 * Adds the standard patient list columns
	 * @param dsd the data set definition
	 */
	protected void addStandardColumns(PatientDataSetDefinition dsd) {
		dsd.addColumn("id", new PatientIdDataDefinition(), "");
		dsd.addColumn("Name", new PreferredNameDataDefinition(), "");
		dsd.addColumn("Age", new AgeDataDefinition(), "");
		dsd.addColumn("Sex", new GenderDataDefinition(), "");

		// TODO change displayed identifier using getDisplayIdentifier().getTarget()
		dsd.addColumn("UPN", new PatientIdentifierDataDefinition("UPN", MetadataUtils.getPatientIdentifierType(Metadata.PatientIdentifierType.UNIQUE_PATIENT_NUMBER)), "");
	}
}