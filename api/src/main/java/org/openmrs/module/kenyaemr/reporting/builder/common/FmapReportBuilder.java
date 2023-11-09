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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.FmapIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonDimensionLibrary;
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
 * FMAP MOH-729 Report
 */
@Component
@Builds({"kenyaemr.etl.common.report.fmap"})
public class FmapReportBuilder extends AbstractReportBuilder {

    protected static final Log log = LogFactory.getLog(FmapReportBuilder.class);

    @Autowired
    private FmapIndicatorLibrary fmapIndicators;

    @Autowired
    private CommonDimensionLibrary commonDimensions;

    /**
     * @see org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder#getParameters(org.openmrs.module.kenyacore.report.ReportDescriptor)
     */
    @Override
    protected List<Parameter> getParameters(ReportDescriptor descriptor) {
        return Arrays.asList(
                new Parameter("startDate", "Start Date", Date.class),
                new Parameter("endDate", "End Date", Date.class)
                //new Parameter("dateBasedReporting", "", String.class)
        );
    }

    /**
     * @see org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder#buildDataSets(org.openmrs.module.kenyacore.report.ReportDescriptor, org.openmrs.module.reporting.report.definition.ReportDefinition)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {
        return Arrays.asList(
                ReportUtils.map(fmapPatientRegimens(), "startDate=${startDate},endDate=${endDate}")
//                ReportUtils.map(fmapAdultSecondLineRegimens(), "startDate=${startDate},endDate=${endDate}"),
//                ReportUtils.map(fmapAdultThirdLineRegimens(), "startDate=${startDate},endDate=${endDate}"),
//                ReportUtils.map(fmapChildFirstLineRegimens(), "startDate=${startDate},endDate=${endDate}"),
//                ReportUtils.map(fmapChildSecondLineRegimens(), "startDate=${startDate},endDate=${endDate}"),
//                ReportUtils.map(fmapChildThirdLineRegimens(), "startDate=${startDate},endDate=${endDate}")
        );
    }


    /**
     * Creates the dataset for Adult first line regimens
     *
     * @return the dataset
     */
    protected DataSetDefinition fmapPatientRegimens() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("ACTIVE CLIENTS");
        cohortDsd.setDescription("ARV Treatment Regimen");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        String indParams = "startDate=${startDate},endDate=${endDate}";
        cohortDsd.addColumn("AF1A", "AZT+3TC+NVP", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/NVP","First line","adult"), indParams),"");
        cohortDsd.addColumn("AF1B", "AZT+3TC+EFV", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/EFV","First line","adult"), indParams),"");
        cohortDsd.addColumn("AF1D", "AZT+3TC+DTG", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/DTG","First line","adult"), indParams),"");
        cohortDsd.addColumn("AF1E", "AZT+3TC+LPVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/LPV/r","First line","adult"), indParams),"");
        cohortDsd.addColumn("AF1F", "AZT+3TC+ATVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/ATV/r","First line","adult"), indParams),"");
        cohortDsd.addColumn("AF2A", "TDF+3TC+NVP", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("TDF/3TC/NVP","First line","adult"), indParams),"");
        cohortDsd.addColumn("AF2B", "TDF+3TC+EFV", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("TDF/3TC/EFV","First line","adult"), indParams),"");
        cohortDsd.addColumn("AF2D", "TDF+3TC+ATVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/ATV/r","First line","adult"), indParams),"");
        cohortDsd.addColumn("AF2E", "TDF+3TC+DTG", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("TDF/3TC/DTG","First line","adult"), indParams),"");
        cohortDsd.addColumn("AF2F", "TDF+3TC+LPVr(1L Adults < 40kg)", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("TDF/3TC/LPV/r","First line","adult"), indParams),"");
        cohortDsd.addColumn("AF4A", "ABC+3TC+NVP", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/NVP","First line","adult"), indParams),"");
        cohortDsd.addColumn("AF4B", "ABC+3TC+EFV", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/EFV","First line","adult"), indParams),"");
        cohortDsd.addColumn("AF4C", "ABC+3TC+DTG", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/DTG","First line","adult"), indParams),"");
        cohortDsd.addColumn("AF5X", "Any other first line adult regimens", ReportUtils.map(fmapIndicators.patientsOnOtherRegimen("Other","First line","adult"), indParams),"");
        cohortDsd.addColumn("AS1A", "AZT+3TC+LPVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/LPV/r","Second line","adult"), indParams),"");
        cohortDsd.addColumn("AS1B", "AZT+3TC+ATVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/ATV/r","Second line","adult"), indParams),"");
        cohortDsd.addColumn("AS1C", "AZT+3TC+DTG", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/DTG","Second line","adult"), indParams),"");
        cohortDsd.addColumn("AS2A", "TDF+3TC+LPVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("TDF/3TC/LPV/r","Second line","adult"), indParams),"");
        cohortDsd.addColumn("AS2B", "TDF+3TC+DTG", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("TDF/3TC/DTG","Second line","adult"), indParams),"");
        cohortDsd.addColumn("AS2C", "TDF+3TC+ATVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("TDF/3TC/ATV/r","Second line","adult"), indParams),"");
        cohortDsd.addColumn("AS5A", "ABC+3TC+LPVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/LPV/r","Second line","adult"), indParams),"");
        cohortDsd.addColumn("AS5B", "ABC+3TC+ATVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/ATV/r","Second line","adult"), indParams),"");
        cohortDsd.addColumn("AS5C", "ABC+3TC+DTG", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/DTG","Second line","adult"), indParams),"");
        cohortDsd.addColumn("AF5X", "Any other second line adult regimens", ReportUtils.map(fmapIndicators.patientsOnOtherRegimen("Other","Second line","adult"), indParams),"");
        cohortDsd.addColumn("AT2D", "TDF+3TC+DTG+DRV+RTV", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("TDF/3TC/DTG/DRV/RTV","Third line","adult"), indParams),"");
        cohortDsd.addColumn("AT2E", "TDF+3TC+RAL+DRV+RTV", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("TDF/3TC/RAL/DRV/RTV","Third line","adult"), indParams),"");
        cohortDsd.addColumn("AT2F", "TDF+3TC+DTG+ETV+DRV+RTV", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("TDF/3TC/DTG/ETV/DRV/RTV","Third line","adult"), indParams),"");
        cohortDsd.addColumn("AT2X", "Any other thirdline adult regimens", ReportUtils.map(fmapIndicators.patientsOnOtherRegimen("Other","Third line","adult"), indParams),"");
        cohortDsd.addColumn("CF1A", "AZT+3TC+NVP", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/NVP","First line","child"), indParams),"");
        cohortDsd.addColumn("CF1B", "AZT+3TC+EFV", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/EFV","First line","child"), indParams),"");
        cohortDsd.addColumn("CF1C", "AZT+3TC+LPVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/LPV/r","First line","child"), indParams),"");
        cohortDsd.addColumn("CF2A", "ABC+3TC+NVP", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/NVP","First line","child"), indParams),"");
        cohortDsd.addColumn("CF2B", "ABC+3TC+EFV", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/EFV","First line","child"), indParams),"");
        cohortDsd.addColumn("CF2D", "AZT+3TC+LPVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/LPV/r","First line","child"), indParams),"");
        cohortDsd.addColumn("CF2G", "ABC+3TC+DTG", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/DTG","First line","child"), indParams),"");
        cohortDsd.addColumn("CF4E", "TDF+3TC+DTG", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("TDF/3TC/DTG","First line","child"), indParams),"");
        cohortDsd.addColumn("CF5X", "Any other first line adult regimens", ReportUtils.map(fmapIndicators.patientsOnOtherRegimen("Other","First line","child"), indParams),"");
        cohortDsd.addColumn("CS1A", "AZT+3TC+LPVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/LPV/r","Second line","child"), indParams),"");
        cohortDsd.addColumn("CS1C", "AZT+3TC+DRV+RTV+RAL", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/DRV/RTV/RAL","Second line","child"), indParams),"");
        cohortDsd.addColumn("CS2A", "ABC+3TC+LPVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/LPV/r","Second line","child"), indParams),"");
        cohortDsd.addColumn("CS2B", "ABC+3TC+DTG", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/DTG","Second line","child"), indParams),"");
        cohortDsd.addColumn("CS2D", "ABC+3TC+DRV+RTV+RAL", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/DRV/RTV/RAL","Second line","child"), indParams),"");
        cohortDsd.addColumn("AF5X", "Any other second line adult regimens", ReportUtils.map(fmapIndicators.patientsOnOtherRegimen("Other","Second line","child"), indParams),"");
        cohortDsd.addColumn("CT1H", "AZT+3TC+DRV+RTV+RAL", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/DRV/RTV/RAL","Third line","child"), indParams),"");
        cohortDsd.addColumn("CT2D", "ABC+3TC+DRV+RTV+RAL", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/DRV/RTV/RAL","Third line","child"), indParams),"");
        cohortDsd.addColumn("CT3X", "Any other thirdline adult regimens", ReportUtils.map(fmapIndicators.patientsOnOtherRegimen("Other","Third line","child"), indParams),"");

        return cohortDsd;
    }

    protected DataSetDefinition fmapAdultSecondLineRegimens() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("ADULT ART");
        cohortDsd.setDescription("2nd Line regimens");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        String indParams = "startDate=${startDate},endDate=${endDate}";
        cohortDsd.addColumn("AS1A", "AZT+3TC+LPVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/LPV/r","Second line","adult"), indParams),"");
        cohortDsd.addColumn("AS1B", "AZT+3TC+ATVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/ATV/r","Second line","adult"), indParams),"");
        cohortDsd.addColumn("AS1C", "AZT+3TC+DTG", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/DTG","Second line","adult"), indParams),"");
        cohortDsd.addColumn("AS2A", "TDF+3TC+LPVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("TDF/3TC/LPV/r","Second line","adult"), indParams),"");
        cohortDsd.addColumn("AS2B", "TDF+3TC+DTG", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("TDF/3TC/DTG","Second line","adult"), indParams),"");
        cohortDsd.addColumn("AS2C", "TDF+3TC+ATVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("TDF/3TC/ATV/r","Second line","adult"), indParams),"");
        cohortDsd.addColumn("AS5A", "ABC+3TC+LPVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/LPV/r","Second line","adult"), indParams),"");
        cohortDsd.addColumn("AS5B", "ABC+3TC+ATVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/ATV/r","Second line","adult"), indParams),"");
        cohortDsd.addColumn("AS5C", "ABC+3TC+DTG", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/DTG","Second line","adult"), indParams),"");
        cohortDsd.addColumn("AF5X", "Any other second line adult regimens", ReportUtils.map(fmapIndicators.patientsOnOtherRegimen("Other","Second line","adult"), indParams),"");

        return cohortDsd;
    }

    protected DataSetDefinition fmapAdultThirdLineRegimens() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("ADULT ART");
        cohortDsd.setDescription("3rd Line regimens");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        String indParams = "startDate=${startDate},endDate=${endDate}";
        cohortDsd.addColumn("AT2D", "TDF+3TC+DTG+DRV+RTV", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("TDF/3TC/DTG/DRV/RTV","Third line","adult"), indParams),"");
        cohortDsd.addColumn("AT2E", "TDF+3TC+RAL+DRV+RTV", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("TDF/3TC/RAL/DRV/RTV","Third line","adult"), indParams),"");
        cohortDsd.addColumn("AT2F", "TDF+3TC+DTG+ETV+DRV+RTV", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("TDF/3TC/DTG/ETV/DRV/RTV","Third line","adult"), indParams),"");
        cohortDsd.addColumn("AT2X", "Any other thirdline adult regimens", ReportUtils.map(fmapIndicators.patientsOnOtherRegimen("Other","Third line","adult"), indParams),"");

        return cohortDsd;
    }

    protected DataSetDefinition fmapChildFirstLineRegimens() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("PAEDIATRIC ART");
        cohortDsd.setDescription("1st Line regimens");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        String indParams = "startDate=${startDate},endDate=${endDate}";
        cohortDsd.addColumn("CF1A", "AZT+3TC+NVP", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/NVP","First line","child"), indParams),"");
        cohortDsd.addColumn("CF1B", "AZT+3TC+EFV", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/EFV","First line","child"), indParams),"");
        cohortDsd.addColumn("CF1C", "AZT+3TC+LPVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/LPV/r","First line","child"), indParams),"");
        cohortDsd.addColumn("CF2A", "ABC+3TC+NVP", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/NVP","First line","child"), indParams),"");
        cohortDsd.addColumn("CF2B", "ABC+3TC+EFV", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/EFV","First line","child"), indParams),"");
        cohortDsd.addColumn("CF2D", "AZT+3TC+LPVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/LPV/r","First line","child"), indParams),"");
        cohortDsd.addColumn("CF2G", "ABC+3TC+DTG", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/DTG","First line","child"), indParams),"");
        cohortDsd.addColumn("CF4E", "TDF+3TC+DTG", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("TDF/3TC/DTG","First line","child"), indParams),"");
        cohortDsd.addColumn("CF5X", "Any other first line adult regimens", ReportUtils.map(fmapIndicators.patientsOnOtherRegimen("Other","First line","child"), indParams),"");

        return cohortDsd;
    }

    protected DataSetDefinition fmapChildSecondLineRegimens() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("PAEDIATRIC ART");
        cohortDsd.setDescription("2nd Line regimens");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        String indParams = "startDate=${startDate},endDate=${endDate}";
        cohortDsd.addColumn("CS1A", "AZT+3TC+LPVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/LPV/r","Second line","child"), indParams),"");
        cohortDsd.addColumn("CS1C", "AZT+3TC+DRV+RTV+RAL", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/DRV/RTV/RAL","Second line","child"), indParams),"");
        cohortDsd.addColumn("CS2A", "ABC+3TC+LPVr", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/LPV/r","Second line","child"), indParams),"");
        cohortDsd.addColumn("CS2B", "ABC+3TC+DTG", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/DTG","Second line","child"), indParams),"");
        cohortDsd.addColumn("CS2D", "ABC+3TC+DRV+RTV+RAL", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/DRV/RTV/RAL","Second line","child"), indParams),"");
        cohortDsd.addColumn("AF5X", "Any other second line adult regimens", ReportUtils.map(fmapIndicators.patientsOnOtherRegimen("Other","Second line","child"), indParams),"");

        return cohortDsd;
    }

    protected DataSetDefinition fmapChildThirdLineRegimens() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("PAEDIATRIC ART");
        cohortDsd.setDescription("3rd Line regimens");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        String indParams = "startDate=${startDate},endDate=${endDate}";
        cohortDsd.addColumn("CT1H", "AZT+3TC+DRV+RTV+RAL", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("AZT/3TC/DRV/RTV/RAL","Third line","child"), indParams),"");
        cohortDsd.addColumn("CT2D", "ABC+3TC+DRV+RTV+RAL", ReportUtils.map(fmapIndicators.patientsOnSpecificRegimen("ABC/3TC/DRV/RTV/RAL","Third line","child"), indParams),"");
        cohortDsd.addColumn("CT3X", "Any other thirdline adult regimens", ReportUtils.map(fmapIndicators.patientsOnOtherRegimen("Other","Third line","child"), indParams),"");

        return cohortDsd;
    }
}