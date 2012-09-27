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

import java.util.LinkedHashSet;
import java.util.Set;

import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.calculation.KenyaEmrCalculation;
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
 * Base implementation for row-per-patient reports who trigger a certain alert
 */
public abstract class PatientAlertListReportManager implements PatientListReportManager {
	
	private Boolean configured = Boolean.FALSE;
	
	private KenyaEmrCalculation alertCalculation;
	
	protected ReportDefinition reportDefinition;
	
	/**
	 * @see org.openmrs.module.kenyaemr.report.ReportManager#getTags()
	 */
	@Override
	public Set<String> getTags() {
	    Set<String> ret = new LinkedHashSet<String>();
	    ret.add("alert");
	    ret.add("facility");
	    return ret;
	}
	
    /**
     * @param alertCalculation the alertCalculation to set
     */
    public void setAlertCalculation(KenyaEmrCalculation alertCalculation) {
	    this.alertCalculation = alertCalculation;
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
	 * @see org.openmrs.module.kenyaemr.report.ReportManager#getReportDefinitionSummary()
	 */
	@Override
	public DefinitionSummary getReportDefinitionSummary() {
		DefinitionSummary ret = new DefinitionSummary();
		ret.setName(alertCalculation.getShortMessage());
		ret.setDescription(alertCalculation.getDetailedMessage());
		return ret;
	}
	
	/**
	 * @see org.openmrs.module.kenyaemr.report.ReportManager#getReportDefinition()
	 */
	@Override
	public ReportDefinition getReportDefinition() {
		synchronized (configured) {
	        if (!configured) {
	        	reportDefinition = buildReportDefinition();
	        	configured = true;
	        }
        }
		
		return reportDefinition;
	}
	
	/**
     * Constructs the report definitions
     */
    private ReportDefinition buildReportDefinition() {
		PatientDataSetDefinition dsd = new PatientDataSetDefinition(alertCalculation.getDetailedMessage());
		addColumns(dsd);
		dsd.addRowFilter(map(new KenyaEmrCalculationCohortDefinition(alertCalculation), null));

		ReportDefinition ret = new ReportDefinition();
		ret.setName(alertCalculation.getShortMessage());
		ret.addDataSetDefinition(dsd, null);
		return ret;
    }

	/**
	 * @see org.openmrs.module.kenyaemr.report.ReportManager#getExcelTemplate()
	 */
	@Override
	public byte[] getExcelTemplate() {
		return null;
	}
	
	/**
	 * @see org.openmrs.module.kenyaemr.report.ReportManager#getExcelFilename(org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Override
	public String getExcelFilename(EvaluationContext ec) {
		return null;
	}
	
	private <T extends Parameterizable> Mapped<T> map(T parameterizable, String mappings) {
    	if (parameterizable == null) {
    		throw new NullPointerException("Programming error: missing parameterizable");
    	}
    	if (mappings == null) {
    		mappings = ""; // probably not necessary, just to be safe
    	}
    	return new Mapped<T>(parameterizable, ParameterizableUtil.createParameterMappings(mappings));
    }
	
}
