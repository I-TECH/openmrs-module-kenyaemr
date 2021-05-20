/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.hiv;

import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.diffCareStability.DiffCareStabilityIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonDimensionLibrary;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Report builder for Viral suppression report
 */
@Component
@Builds({"kenyaemr.etl.common.report.diffcarestability"})
public class DiffCareStabilityReportBuilder extends AbstractReportBuilder {

    @Autowired
    private DiffCareStabilityIndicatorLibrary diffCareStabilityIndicatorLibrary;

    @Autowired
    private CommonDimensionLibrary commonDimensions;


    public static final String DATE_FORMAT = "yyyy-MM-dd";

    @Override
    protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
        return new ArrayList<Parameter>();
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor reportDescriptor, ReportDefinition reportDefinition) {
        return Arrays.asList(
                ReportUtils.map(stability(), "")
        );
    }

    protected DataSetDefinition stability() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.diffCareAgeGroups(), "onDate=${endDate}"));
        cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

        ColumnParameters all0_to14 = new ColumnParameters(null, "0-14", "age=<15");
        ColumnParameters f15Plus = new ColumnParameters(null, "15+, Female", "gender=F|age=15+");
        ColumnParameters m15Plus = new ColumnParameters(null, "15+, Male", "gender=M|age=15+");
        ColumnParameters colTot = new ColumnParameters(null, "Total", "");

        List<ColumnParameters> diffCareDisaggregations =
                Arrays.asList(all0_to14, f15Plus, m15Plus, colTot);

        cohortDsd.setName("Diff-care-stability");
        cohortDsd.setDescription("Differentiated care stability report");

        EmrReportingUtils.addRow(cohortDsd, "Stable patients with <1 month prescription", "", ReportUtils.map(diffCareStabilityIndicatorLibrary.stableUnder1Monthtca()), diffCareDisaggregations, Arrays.asList("01", "02", "03", "04"));
        EmrReportingUtils.addRow(cohortDsd, "Stable patients with 1 month prescription", "", ReportUtils.map(diffCareStabilityIndicatorLibrary.stablePatientsMultiMonthAppointments(1)), diffCareDisaggregations, Arrays.asList("01", "02", "03", "04"));
        EmrReportingUtils.addRow(cohortDsd, "Stable patients with 2 month prescription", "", ReportUtils.map(diffCareStabilityIndicatorLibrary.stablePatientsMultiMonthAppointments(2)), diffCareDisaggregations, Arrays.asList("01", "02", "03", "04"));
        EmrReportingUtils.addRow(cohortDsd, "Stable patients with 3 month prescription", "", ReportUtils.map(diffCareStabilityIndicatorLibrary.stablePatientsMultiMonthAppointments(3)), diffCareDisaggregations, Arrays.asList("01", "02", "03", "04"));
        EmrReportingUtils.addRow(cohortDsd, "Stable patients with 4 month prescription", "", ReportUtils.map(diffCareStabilityIndicatorLibrary.stablePatientsMultiMonthAppointments(4)), diffCareDisaggregations, Arrays.asList("01", "02", "03", "04"));
        EmrReportingUtils.addRow(cohortDsd, "Stable patients with 5 month prescription", "", ReportUtils.map(diffCareStabilityIndicatorLibrary.stablePatientsMultiMonthAppointments(5)), diffCareDisaggregations, Arrays.asList("01", "02", "03", "04"));
        EmrReportingUtils.addRow(cohortDsd, "Stable patients with 6 month prescription", "", ReportUtils.map(diffCareStabilityIndicatorLibrary.stablePatientsMultiMonthAppointments(6)), diffCareDisaggregations, Arrays.asList("01", "02", "03", "04"));
        EmrReportingUtils.addRow(cohortDsd, "Stable patients with 6+ months prescription", "", ReportUtils.map(diffCareStabilityIndicatorLibrary.stableOver6Monthstca()), diffCareDisaggregations, Arrays.asList("01", "02", "03", "04"));
        EmrReportingUtils.addRow(cohortDsd, "Unstable Patients", "", ReportUtils.map(diffCareStabilityIndicatorLibrary.unstable()), diffCareDisaggregations, Arrays.asList("01", "02", "03", "04"));
        EmrReportingUtils.addRow(cohortDsd, "Patients with undocumented stability", "", ReportUtils.map(diffCareStabilityIndicatorLibrary.undocumentedStability()), diffCareDisaggregations, Arrays.asList("01", "02", "03", "04"));
        return cohortDsd;

    }
}
