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
package org.openmrs.module.kenyaemr.report.patientlist;

import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.calculation.BaseKenyaEmrCalculation;
import org.openmrs.module.kenyaemr.report.KenyaEmrCalculationCohortDefinition;
import org.openmrs.module.kenyaemr.report.ReportBuilder;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

/**
 * Base implementation for row-per-patient reports based on calculations
 */
public abstract class PatientListReportBuilder extends ReportBuilder {
	
	private BaseKenyaEmrCalculation calculation;
	
	/**
	 * @see org.openmrs.module.kenyaemr.report.ReportBuilder#getTags()
	 */
	@Override
	public String[] getTags() {
		return new String[] { "facility" };
	}

	/**
	 * @see org.openmrs.module.kenyaemr.report.ReportBuilder#getName()
	 */
	@Override
	public String getName() {
		return calculation.getShortMessage();
	}

	/**
	 * @see org.openmrs.module.kenyaemr.report.ReportBuilder#getDescription()
	 */
	@Override
	public String getDescription() {
		return calculation.getDetailedMessage();
	}
	
    /**
     * @param calculation the calculation to set
     */
    public void setCalculation(BaseKenyaEmrCalculation calculation) {
	    this.calculation = calculation;
    }
	
	/**
	 * Override this if you don't want the default (HIV ID, name, sex, age)
	 * @param dsd this will be modified by having columns added
	 */
	public void addColumns(PatientDataSetDefinition dsd) {
		dsd.addColumn("HIV Unique ID", new PatientIdentifierDataDefinition("HIV Unique ID", Context.getPatientService().getPatientIdentifierTypeByUuid(MetadataConstants.UNIQUE_PATIENT_NUMBER_UUID)), "");
		dsd.addColumn("Patient Name", new PreferredNameDataDefinition(), "");
		dsd.addColumn("Age", new AgeDataDefinition(), "");
		dsd.addColumn("Sex", new GenderDataDefinition(), "");
        addViewColumn(dsd);
    }

    protected void addViewColumn(PatientDataSetDefinition dsd) {
        dsd.addColumn("View", new PatientIdDataDefinition(), "", new DataConverter() {

            @Override
            public Class<?> getInputDataType() {
                return Integer.class;
            }

            @Override
            public Class<?> getDataType() {
                return String.class;
            }

            @Override
            public Object convert(Object input) {
                return "<a href=\"medicalChartViewPatient.page?patientId=" + input + "\">View</a>";
            }
        });
    }
	
	/**
     * Constructs the report definitions
     */
	@Override
    protected ReportDefinition buildReportDefinition() {
		PatientDataSetDefinition dsd = new PatientDataSetDefinition(calculation.getDetailedMessage());
		addColumns(dsd);
		dsd.addRowFilter(map(new KenyaEmrCalculationCohortDefinition(calculation), null));

		ReportDefinition ret = new ReportDefinition();
		ret.setName(getName());
		ret.addDataSetDefinition(dsd, null);
		return ret;
    }
}