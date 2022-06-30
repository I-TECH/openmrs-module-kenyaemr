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
 * Report builder for Clinical action report
 */
@Component
@Builds({"kenyaemr.etl.common.report.publicHealthAction"})
public class PublicHealthActionReportBuilder extends AbstractReportBuilder {

    @Autowired
    private PublicHealthActionIndicatorLibrary publicHealthActionIndicatorLibrary;

    @Override
    protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
        return Arrays.asList(
                new Parameter("startDate", "Start Date", Date.class),
                new Parameter("endDate", "End Date", Date.class),
                new Parameter("dateBasedReporting", "", String.class)
        );
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor reportDescriptor, ReportDefinition reportDefinition) {
        return Arrays.asList(ReportUtils.map(publicHealthAction(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    protected DataSetDefinition publicHealthAction() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cohortDsd.setName("Clinical-Action");
        cohortDsd.setDescription("Clinical Action Report");
        cohortDsd.addColumn("Current on ART without valid VL", "", ReportUtils.map(publicHealthActionIndicatorLibrary.invalidVL(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Current on ART with Unsuppressed Valid VL", "", ReportUtils.map(publicHealthActionIndicatorLibrary.unsuppressedWithValidVL(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Current on ART with Unsuppressed invalid VL", "", ReportUtils.map(publicHealthActionIndicatorLibrary.unsuppressedWithoutValidVL(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Current on ART Clients without NUPI", "", ReportUtils.map(publicHealthActionIndicatorLibrary.txCurrclientsWithoutNUPI(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Recent defaulters", " (Missed appointment within 30 days)", ReportUtils.map(publicHealthActionIndicatorLibrary.recentDefaulters(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Undocumented LTFU/IIT", "", ReportUtils.map(publicHealthActionIndicatorLibrary.undocumentedLTFU(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Not Vaccinated for Covid-19", "", ReportUtils.map(publicHealthActionIndicatorLibrary.notVaccinatedForCovid19(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Not Assessed for Covid-19 vaccination", "", ReportUtils.map(publicHealthActionIndicatorLibrary.notAssessedForCovid19(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("CALHIV not on DTG regimen", "", ReportUtils.map(publicHealthActionIndicatorLibrary.calhivNotOnDTGRegimen(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("CALHIV not enrolled in OVC", "", ReportUtils.map(publicHealthActionIndicatorLibrary.calhivNotInOVC(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Adolescents not enrolled in OTZ", "", ReportUtils.map(publicHealthActionIndicatorLibrary.adolescentsNotInOTZ(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("HEI with undocumented HIV status", "", ReportUtils.map(publicHealthActionIndicatorLibrary.undocumentedHEIStatus(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("HEI not Linked to Mothers", "", ReportUtils.map(publicHealthActionIndicatorLibrary.unlinkedHEI(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("HIV+ and NOT Linked", "", ReportUtils.map(publicHealthActionIndicatorLibrary.notLinked(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Children of HIV infected adults with undocumented HIV status", " (Please run Children of HIV infected adults with undocumented HIV status for linelist)", ReportUtils.map(publicHealthActionIndicatorLibrary.childrenContactsUndocumentedHIVStatus(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("PNS Contacts with undocumented HIV status", " (Please run PNS contacts with undocumented HIV status for linelist)", ReportUtils.map(publicHealthActionIndicatorLibrary.contactsUndocumentedHIVStatus(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("SNS Contacts with undocumented HIV status", " (Please run SNS contacts with undocumented HIV status for linelist)", ReportUtils.map(publicHealthActionIndicatorLibrary.snsContactsUndocumentedHIVStatus(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Number of deaths", " (Please run mortality linelist for details)", ReportUtils.map(publicHealthActionIndicatorLibrary.numberOfDeaths(), "startDate=${startDate},endDate=${endDate}"), "");

        return cohortDsd;

    }
}
