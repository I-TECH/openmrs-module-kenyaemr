package org.openmrs.module.kenyaemr.report;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Program;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.util.OpenmrsClassLoader;
import org.springframework.stereotype.Component;

@Component
public class PatientSummaryReport implements ReportManager {

private Boolean configured = Boolean.FALSE;
	
	private final Log log = LogFactory.getLog(getClass());
	
    private static final String NAME_PREFIX = "Patient Summary";
    
    ReportDefinition reportDefinition;

    Map<String, CohortDefinition> cohortDefinitions;
    
    Map<String, CohortDefinitionDimension> dimensions;
    
    Map<String, CohortIndicator> indicators;
    
    Program hivProgram;
    /**
     * @see org.openmrs.module.kenyaemr.report.ReportManager#getTags()
     */
    @Override
    public Set<String> getTags() {
        Set<String> ret = new LinkedHashSet<String>();
        ret.add("HIV");
        ret.add("summary");
        return ret;
    }
    /**
     * @see org.openmrs.module.kenyaemr.report.ReportManager#getReportDefinitionSummary()
     */
    @Override
    public DefinitionSummary getReportDefinitionSummary() {
    	DefinitionSummary ret = new DefinitionSummary();
    	ret.setName(NAME_PREFIX);
    	ret.setUuid(getClass().getName());
    	return ret;
    }
    @Override
    public byte[] getExcelTemplate() {
    	try {
	    	InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream("patient_summary.xls");
	    	byte[] contents = IOUtils.toByteArray(is);
			IOUtils.closeQuietly(is);
			return contents;
    	} catch (IOException ex) {
    		throw new RuntimeException("Error loading excel template", ex);
    	}
    }
    
    /**
     * @see org.openmrs.module.kenyaemr.report.ReportManager#getExcelFilename(org.openmrs.module.reporting.evaluation.EvaluationContext)
     */
    @Override
    public String getExcelFilename(EvaluationContext ec) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
        return NAME_PREFIX + " " + df.format(ec.getParameterValue("startDate")) + ".xls";
    }
    /**
     * @return the reportDefinition
     */
    @Override
    public ReportDefinition getReportDefinition() {
    	synchronized (configured) {
	        if (!configured) {
	        	//setup();
	        	configured = true;
	        }
        }
	    return reportDefinition;
    }


}
