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

import java.util.ArrayList;
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

    @Override
    protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
        return new ArrayList<Parameter>();
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor reportDescriptor, ReportDefinition reportDefinition) {
        return Arrays.asList(ReportUtils.map(publicHealthAction(), "")
        );
    }

    protected DataSetDefinition publicHealthAction() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("PH-Action");
        cohortDsd.setDescription("Public Health Action Report");
        cohortDsd.addColumn("HIV+ and NOT Linked", "", ReportUtils.map(publicHealthActionIndicatorLibrary.notLinked(), ""), "");
        cohortDsd.addColumn("HEI with undocumented HIV status", "", ReportUtils.map(publicHealthActionIndicatorLibrary.undocumentedHEIStatus(), ""), "");
        cohortDsd.addColumn("Current on ART without valid VL", "", ReportUtils.map(publicHealthActionIndicatorLibrary.invalidVL(), ""), "");
        cohortDsd.addColumn("Current on ART with Unsuppressed Valid VL", "", ReportUtils.map(publicHealthActionIndicatorLibrary.unsuppressedWithValidVL(), ""), "");
        cohortDsd.addColumn("Current on ART with Unsuppressed invalid VL", "", ReportUtils.map(publicHealthActionIndicatorLibrary.unsuppressedWithoutValidVL(), ""), "");
        cohortDsd.addColumn("Undocumented LTFU", "", ReportUtils.map(publicHealthActionIndicatorLibrary.undocumentedLTFU(), ""), "");
        cohortDsd.addColumn("Recent defaulters", "", ReportUtils.map(publicHealthActionIndicatorLibrary.recentDefaulters(), ""), "");
        cohortDsd.addColumn("HEIs not Linked to Mothers", "", ReportUtils.map(publicHealthActionIndicatorLibrary.unlinkedHEI(), ""), "");
        cohortDsd.addColumn("Adolescents not in OTZ", "", ReportUtils.map(publicHealthActionIndicatorLibrary.adolescentsNotInOTZ(), ""), "");
        cohortDsd.addColumn("Children not in OVC", "", ReportUtils.map(publicHealthActionIndicatorLibrary.childrenNotInOVC(), ""), "");
        cohortDsd.addColumn("Contacts with undocumented HIV status", "", ReportUtils.map(publicHealthActionIndicatorLibrary.contactsUndocumentedHIVStatus(), ""), "");
        cohortDsd.addColumn("SNS Contacts with undocumented HIV status", "", ReportUtils.map(publicHealthActionIndicatorLibrary.snsContactsUndocumentedHIVStatus(), ""), "");
        cohortDsd.addColumn("Current on ART Clients without NUPI", "", ReportUtils.map(publicHealthActionIndicatorLibrary.txCurrclientsWithoutNUPI(), ""), "");

        return cohortDsd;

    }
}
