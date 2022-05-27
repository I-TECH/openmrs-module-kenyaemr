/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.common;

import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.publicHealthActionReport.PublicHealthActionIndicatorLibrary;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Report builder for Public health action report
 */
@Component
@Builds({"kenyaemr.etl.common.report.publicHealthAction"})
public class PublicHealthActionReportBuilder extends AbstractReportBuilder {

    @Autowired
    private PublicHealthActionIndicatorLibrary publicHealthActionIndicatorLibrary;

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    @Override
    protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
        return Arrays.asList(new Parameter("endDate", "End Date", Date.class),
                new Parameter("dateBasedReporting", "", String.class)
        );
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor reportDescriptor, ReportDefinition reportDefinition) {
        return Arrays.asList(ReportUtils.map(publicHealthAction(), "endDate=${endDate}")
        );
    }

    protected DataSetDefinition publicHealthAction() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("PH-Action");
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        String indParams = "endDate=${endDate}";
        cohortDsd.setDescription("Public Health Action Report");
        cohortDsd.addColumn("HIV+ and NOT Linked", "", ReportUtils.map(publicHealthActionIndicatorLibrary.notLinked(), indParams), "");
        cohortDsd.addColumn("HEI with undocumented HIV status", "", ReportUtils.map(publicHealthActionIndicatorLibrary.undocumentedHEIStatus(), indParams), "");
        cohortDsd.addColumn("Current on ART without valid VL", "", ReportUtils.map(publicHealthActionIndicatorLibrary.invalidVL(), indParams), "");
        cohortDsd.addColumn("Current on ART with Unsuppressed Valid VL", "", ReportUtils.map(publicHealthActionIndicatorLibrary.unsuppressedWithValidVL(), indParams), "");
        cohortDsd.addColumn("Current on ART with Unsuppressed invalid VL", "", ReportUtils.map(publicHealthActionIndicatorLibrary.unsuppressedWithoutValidVL(), indParams), "");
        cohortDsd.addColumn("Undocumented LTFU", "", ReportUtils.map(publicHealthActionIndicatorLibrary.undocumentedLTFU(), indParams), "");
        cohortDsd.addColumn("Recent defaulters", "", ReportUtils.map(publicHealthActionIndicatorLibrary.recentDefaulters(), indParams), "");
        cohortDsd.addColumn("HEIs not Linked to Mothers", "", ReportUtils.map(publicHealthActionIndicatorLibrary.unlinkedHEI(), indParams), "");
        cohortDsd.addColumn("Mothers not Linked to HEIs", "", ReportUtils.map(publicHealthActionIndicatorLibrary.motherNotLinkedToHEI (), indParams), "");
        cohortDsd.addColumn("Adolescents not in OTZ", "", ReportUtils.map(publicHealthActionIndicatorLibrary.adolescentsNotInOTZ(), indParams), "");
        cohortDsd.addColumn("Children not in OVC", "", ReportUtils.map(publicHealthActionIndicatorLibrary.childrenNotInOVC(), indParams), "");
        cohortDsd.addColumn("Contacts with undocumented HIV status", "", ReportUtils.map(publicHealthActionIndicatorLibrary.contactsUndocumentedHIVStatus(), indParams), "");
        cohortDsd.addColumn("SNS Contacts with undocumented HIV status", "", ReportUtils.map(publicHealthActionIndicatorLibrary.snsContactsUndocumentedHIVStatus(), indParams), "");
        cohortDsd.addColumn("Clients without NUPI", "", ReportUtils.map(publicHealthActionIndicatorLibrary.clientsWithoutNUPI(), indParams), "");
        cohortDsd.addColumn("Current on ART Clients without NUPI", "", ReportUtils.map(publicHealthActionIndicatorLibrary.txCurrclientsWithoutNUPI(), indParams), "");

        return cohortDsd;

    }
}
