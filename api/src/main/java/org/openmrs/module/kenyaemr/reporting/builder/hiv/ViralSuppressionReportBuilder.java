package org.openmrs.module.kenyaemr.reporting.builder.hiv;

import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.viralSuppression.ViralSuppressionIndicatorLibrary;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Report builder for Viral suppression report
 */
@Component
@Builds({"kenyaemr.etl.common.report.viralSuppression"})
public class ViralSuppressionReportBuilder extends AbstractReportBuilder {

    @Autowired
    private ViralSuppressionIndicatorLibrary suppressionIndicatorLibrary;

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    @Override
    protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
        return new ArrayList<Parameter>();
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor reportDescriptor, ReportDefinition reportDefinition) {
        return Arrays.asList(
                ReportUtils.map(suppresion(), "")
        );
    }


    protected DataSetDefinition suppresion() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("Viral-suppression");
        cohortDsd.setDescription("Viral suppression report");
        cohortDsd.addColumn("Number Suppressed", "", ReportUtils.map(suppressionIndicatorLibrary.suppressed(), ""), "");
        cohortDsd.addColumn("Number Un-suppressed", "", ReportUtils.map(suppressionIndicatorLibrary.unsuppressed(), ""), "");
        cohortDsd.addColumn("Number with no VL Results", "", ReportUtils.map(suppressionIndicatorLibrary.noVLResults(), ""), "");

        return cohortDsd;

    }
}
