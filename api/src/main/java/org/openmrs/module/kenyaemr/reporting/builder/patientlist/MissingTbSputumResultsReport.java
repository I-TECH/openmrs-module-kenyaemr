package org.openmrs.module.kenyaemr.reporting.builder.patientlist;

import org.openmrs.module.kenyaemr.calculation.tb.MissingTbSputumResultsCalculation;
import org.springframework.stereotype.Component;

@Component
public class MissingTbSputumResultsReport extends BasePatientCalculationReportBuilder {
	
	public MissingTbSputumResultsReport() {
		super(new MissingTbSputumResultsCalculation());
	}
	
	/**
	 * @see org.openmrs.module.kenyaemr.reporting.builder.ReportBuilder#getTags()
	 */
	@Override
	public String[] getTags() {
		return new String[] { "facility", "tb" };
	}

}
