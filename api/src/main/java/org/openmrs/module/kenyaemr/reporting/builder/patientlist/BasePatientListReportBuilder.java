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

import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyacore.report.ReportBuilder;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

/**
 * Base implementation for row-per-patient reports
 */
public abstract class BasePatientListReportBuilder extends ReportBuilder {

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
		dsd.addColumn("UPN", new PatientIdentifierDataDefinition("UPN", MetadataUtils.getPatientIdentifierType(Metadata.PatientIdentifierType.UNIQUE_PATIENT_NUMBER)), "");
	}

	/**
	 * Builds the report definition
	 *
	 */
	@Override
	protected ReportDefinition buildReportDefinition() {
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
	protected abstract PatientDataSetDefinition buildDataSet();
}