package org.openmrs.module.kenyaemr.reporting.builder.common;

import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by codehub on 2/23/16.
 */
@Component
@Builds({"kenyaemr.common.report.mer-indicators"})
public class MerIndicatorsReportBuilder extends AbstractReportBuilder {

    @Override
    protected List<Parameter> getParameters(ReportDescriptor descriptor) {
        return Arrays.asList(
                new Parameter("startDate", "Start Date", Date.class),
                new Parameter("endDate", "End Date", Date.class)
        );
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {
        return Arrays.asList(
                ReportUtils.map(merIndicators(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Create a data set for the mer indicator
     * @return dataset
     */
    protected DataSetDefinition merIndicators() {
        CohortIndicatorDataSetDefinition cid = new CohortIndicatorDataSetDefinition();
        cid.setName("Mer Indicators");
        cid.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cid.addParameter(new Parameter("endDate", "End Date", Date.class));
        return cid;

    }
}