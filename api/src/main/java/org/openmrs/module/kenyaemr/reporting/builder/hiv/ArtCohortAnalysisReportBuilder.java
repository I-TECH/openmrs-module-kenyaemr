package org.openmrs.module.kenyaemr.reporting.builder.hiv;

import org.openmrs.module.kenyacore.report.CalculationReportDescriptor;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.builder.CalculationReportBuilder;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.springframework.stereotype.Component;

@Component
@Builds("kenyaemr.hiv.report.artCohortAnalysis")
public class ArtCohortAnalysisReportBuilder extends CalculationReportBuilder {

	@Override
	protected void addColumns(CalculationReportDescriptor report, PatientDataSetDefinition dsd) {

		addStandardColumns(report, dsd);

	}
}
