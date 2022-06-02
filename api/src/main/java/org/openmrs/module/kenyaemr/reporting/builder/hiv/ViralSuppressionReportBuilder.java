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
        return Arrays.asList(new Parameter("endDate", "End Date", Date.class),
                new Parameter("dateBasedReporting", "", String.class)
        );
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor reportDescriptor, ReportDefinition reportDefinition) {
        return Arrays.asList(ReportUtils.map(suppresion(), "endDate=${endDate}")
        );
    }


    protected DataSetDefinition suppresion() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("Viral-suppression");
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        String indParams = "endDate=${endDate}";
        cohortDsd.setDescription("Viral suppression report");
        cohortDsd.addColumn("Number Suppressed", "", ReportUtils.map(suppressionIndicatorLibrary.suppressed(), indParams),"");
        cohortDsd.addColumn("Number Un-suppressed", "", ReportUtils.map(suppressionIndicatorLibrary.unsuppressed(), indParams),"");
        cohortDsd.addColumn("Number with no current VL", "", ReportUtils.map(suppressionIndicatorLibrary.noCurrentVLResults(), indParams),"");
        cohortDsd.addColumn("Number with no VL", "", ReportUtils.map(suppressionIndicatorLibrary.noVLResults(), indParams),"");

        return cohortDsd;

    }
}
