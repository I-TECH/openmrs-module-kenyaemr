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

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.util.OpenmrsClassLoader;
import org.springframework.stereotype.Component;


/**
 *
 */
@Component
public class Moh731Report implements IndicatorReportManager {
	
	private Boolean configured = Boolean.FALSE;
	
	private final Log log = LogFactory.getLog(getClass());
	
    private static final String NAME_PREFIX = "MOH 731 Indicator Report";
    
    ReportDefinition reportDefinition;

    Map<String, CohortDefinition> cohortDefinitions;
    
    Map<String, CohortDefinitionDimension> dimensions;
    
    Map<String, CohortIndicator> indicators;
    
    Program hivProgram;
    
    Concept transferInDate;
    
    /**
     * @see org.openmrs.module.kenyaemr.report.IndicatorReportManager#getReportDefinitionSummary()
     */
    @Override
    public DefinitionSummary getReportDefinitionSummary() {
    	DefinitionSummary ret = new DefinitionSummary();
    	ret.setName(NAME_PREFIX);
    	ret.setUuid(getClass().getName());
    	return ret;
    }
    
	public void setup() {
		log.debug("Setting up metadata");
		setupMetadata();
		log.debug("Setting up cohort definitions");
		setupCohortDefinitions();
		log.debug("Setting up dimensions");
		setupDimensions();
		log.debug("Setting up indicators");
		setupIndicators();
		log.debug("Setting up report definition");
		reportDefinition = createReportDefinition();
	}
	
    /**
     * @return the reportDefinition
     */
    public ReportDefinition getReportDefinition() {
    	synchronized (configured) {
	        if (!configured) {
	        	setup();
	        	configured = true;
	        }
        }
	    return reportDefinition;
    }
	
    private void setupMetadata() {
    	// TODO by uuid
	    hivProgram = Context.getProgramWorkflowService().getPrograms("HIV Program").get(0);
	    
	    ConceptService cs = Context.getConceptService();
	    transferInDate = cs.getConceptByUuid(MetadataConstants.TRANSFER_IN_DATE_CONCEPT_UUID);
    }

	private void setupCohortDefinitions() {
		cohortDefinitions = new HashMap<String, CohortDefinition>();
		{
			AgeCohortDefinition cd = new AgeCohortDefinition();
			cd.setName("Age < 15");
			cd.addParameter(new Parameter("effectiveDate", "Date", Date.class));
			cd.setMinAge(0);
			cd.setMaxAge(14);
			cohortDefinitions.put("age.<15", cd);
		}
		{
			AgeCohortDefinition cd = new AgeCohortDefinition();
			cd.setName("Age 15+");
			cd.addParameter(new Parameter("effectiveDate", "Date", Date.class));
			cd.setMinAge(15);
			cohortDefinitions.put("age.15+", cd);
		}
		{
			ProgramEnrollmentCohortDefinition cd = new ProgramEnrollmentCohortDefinition();
			cd.setName("Enrolled in HIV Program between dates");
			cd.addParameter(new Parameter("enrolledOnOrAfter", "From Date", Date.class));
			cd.addParameter(new Parameter("enrolledOnOrBefore", "To Date", Date.class));
			cd.setPrograms(Collections.singletonList(hivProgram));
			cohortDefinitions.put("enrolledInHivProgram", cd);
		}
		{
			DateObsCohortDefinition cd = new DateObsCohortDefinition();
			cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
			cd.setName("Transfer in before date");
			cd.setTimeModifier(TimeModifier.ANY);
			cd.setQuestion(transferInDate);
			cohortDefinitions.put("transferInBefore", cd);
		}
		{
			CompositionCohortDefinition cd = new CompositionCohortDefinition();
			cd.addParameter(new Parameter("fromDate", "From Date", Date.class));
			cd.addParameter(new Parameter("toDate", "To Date", Date.class));
			cd.addSearch("enrolled", map(cohortDefinitions.get("enrolledInHivProgram"), "enrolledOnOrAfter=${fromDate},enrolledOnOrBefore=${toDate}"));
			cd.addSearch("transferIn", map(cohortDefinitions.get("transferInBefore"), "onOrBefore=${toDate}"));
			cd.setCompositionString("enrolled AND NOT transferIn");
			cohortDefinitions.put("enrolledNoTransfers", cd);
		}
	}

	private void setupDimensions() {
		dimensions = new HashMap<String, CohortDefinitionDimension>();
		{
		    CohortDefinitionDimension dim = new CohortDefinitionDimension();
		    dim.setName("Age (<15, 15+)");
		    dim.addParameter(new Parameter("date", "Date", Date.class));
		    dim.addCohortDefinition("<15", map(cohortDefinitions.get("age.<15"), "effectiveDate=${date}"));
		    dim.addCohortDefinition("15+", map(cohortDefinitions.get("age.15+"), "effectiveDate=${date}"));
		    dimensions.put("age", dim);
		}
	}
	
	private void setupIndicators() {
		indicators = new HashMap<String, CohortIndicator>();
		{
			CohortIndicator ind = new CohortIndicator("Enrolled in Care (no transfers)");
			ind.addParameter(new Parameter("startDate", "Start Date", Date.class));
			ind.addParameter(new Parameter("endDate", "End Date", Date.class));
			ind.setCohortDefinition(map(cohortDefinitions.get("enrolledNoTransfers"), "fromDate=${startDate},toDate=${endDate}"));
			indicators.put("enrolledInCare", ind);
		}
	}

	public ReportDefinition createReportDefinition() {
	    ReportDefinition rd = new ReportDefinition();
	    rd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		rd.addParameter(new Parameter("endDate", "End Date", Date.class));
	    rd.setName(NAME_PREFIX);
	    rd.addDataSetDefinition(createDataSet(),
		    ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
	    return rd;
    }

    private DataSetDefinition createDataSet() {
	    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
	    dsd.setName(NAME_PREFIX + " DSD");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

		dsd.addDimension("age", map(dimensions.get("age"), "date=${endDate}"));
		
		dsd.addColumn("3.2", "Enrolled in Care", map(indicators.get("enrolledInCare"), "startDate=${startDate},endDate=${endDate}"), "");
		dsd.addColumn("3.2 (15+)", "Enrolled in Care (15+)", map(indicators.get("enrolledInCare"), "startDate=${startDate},endDate=${endDate}"), "age=15+");
		dsd.addColumn("3.2 (<15)", "Enrolled in Care (<15)", map(indicators.get("enrolledInCare"), "startDate=${startDate},endDate=${endDate}"), "age=<15");
	    
	    return dsd;
    }

    private <T extends Parameterizable> Mapped<T> map(T parameterizable, String mappings) {
    	return new Mapped<T>(parameterizable, ParameterizableUtil.createParameterMappings(mappings));
    }
    
    /**
     * @see org.openmrs.module.kenyaemr.report.IndicatorReportManager#getExcelTemplate()
     */
    @Override
    public byte[] getExcelTemplate() {
    	try {
	    	InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream("Moh731Report.xls");
	    	byte[] contents = IOUtils.toByteArray(is);
			IOUtils.closeQuietly(is);
			return contents;
    	} catch (IOException ex) {
    		throw new RuntimeException("Error loading excel template", ex);
    	}
    }
    
    /**
     * @see org.openmrs.module.kenyaemr.report.IndicatorReportManager#getExcelFilename(org.openmrs.module.reporting.evaluation.EvaluationContext)
     */
    @Override
    public String getExcelFilename(EvaluationContext ec) {
        SimpleDateFormat df = new SimpleDateFormat("yM");
        return NAME_PREFIX + " " + df.format(ec.getParameterValue("startDate")) + ".xls";
    }

}
