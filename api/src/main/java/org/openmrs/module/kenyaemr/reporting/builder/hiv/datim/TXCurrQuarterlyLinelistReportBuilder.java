/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.hiv.datim;

import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.Datim.TXCurrLinelistIndicatorLibrary;
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
@Builds({"kenyaemr.hiv.report.tx_curr_linelist_quarterly"})
public class TXCurrQuarterlyLinelistReportBuilder extends AbstractReportBuilder {

    @Autowired
    private TXCurrLinelistIndicatorLibrary suppressionIndicatorLibrary;

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    @Override
    protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
        return Arrays.asList(
                new Parameter("startDate", "Start Date", Date.class),
                new Parameter("endDate", "End Date", Date.class)
        );
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor reportDescriptor, ReportDefinition reportDefinition) {
        return Arrays.asList(
                ReportUtils.map(suppresion(), "startDate=${startDate},endDate=${endDate}")
        );
    }


    protected DataSetDefinition suppresion() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();

        cohortDsd.setName("TX Curr Line Lists - Quarterly Indicator");
        cohortDsd.setDescription("Shows differences between two reporting dates in terms of patients include/excluded");

        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        String indParams = "startDate=${startDate},endDate=${endDate}";

        cohortDsd.addColumn("Number of patients present in the current report but missing in previous report", "", ReportUtils.map(suppressionIndicatorLibrary.txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport(), indParams), "");
        cohortDsd.addColumn("Number of patients present in the previous report but missing in the current report", "", ReportUtils.map(suppressionIndicatorLibrary.txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport(), indParams), "");

        return cohortDsd;

    }
}
