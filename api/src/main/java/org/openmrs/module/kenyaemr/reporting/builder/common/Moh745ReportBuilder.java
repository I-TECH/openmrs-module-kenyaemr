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
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.library.MOH745.Moh745IndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonDimensionLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
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
 * MOH 745 Report
 */
@Component
@Builds({"kenyaemr.common.report.moh745"})
public class Moh745ReportBuilder extends AbstractReportBuilder {

    protected static final Log log = LogFactory.getLog(Moh745ReportBuilder.class);

    @Autowired
    private Moh745IndicatorLibrary moh745Indicators;

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
        );
    }

    /**
     * @see org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder#buildDataSets(org.openmrs.module.kenyacore.report.ReportDescriptor, org.openmrs.module.reporting.report.definition.ReportDefinition)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {
        return Arrays.asList(
                ReportUtils.map(createCacxDataSet(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    ColumnParameters lessThan25 = new ColumnParameters(null, "<25", "age=<25");
    ColumnParameters adultAbove25 = new ColumnParameters(null, "25-49", "age=25-49");
    ColumnParameters adultAbove50 = new ColumnParameters(null, ">=50", "age=>=50");

    /**
     * Creates the dataset for section #1: Cervical Cancer Screening
     *
     * @return the dataset
     */
    protected DataSetDefinition createCacxDataSet() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("CervicalCancer");
        dsd.setDescription("MOH 745");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.addDimension("age", ReportUtils.map(commonDimensions.moh745AgeGroups(), "onDate=${endDate}"));

        DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName} {middleName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
        PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
        DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

        List<ColumnParameters> moh745Disaggregations = Arrays.asList(lessThan25, adultAbove25, adultAbove50);

        String indParams = "startDate=${startDate},endDate=${endDate}";

        //initial routine
        EmrReportingUtils.addRow(dsd,"VIA/VILI", "Received VIA or VIA/ VILI Screening", ReportUtils.map(moh745Indicators.receivedVIAScreening(), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"PAP SMEAR", "Received Pap Smear Screening", ReportUtils.map(moh745Indicators.receivedPapSmearScreening(), indParams),moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"HPV Test", "Received HPV Test", ReportUtils.map(moh745Indicators.receivedHPVTest(), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Positive VIA or VIA/VILI", "Positive VIA or VIA/VILI Result", ReportUtils.map(moh745Indicators.positiveVIAresult(), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Positive Cytology", "Positive Cytology Result", ReportUtils.map(moh745Indicators.positiveCytologyResult(), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Positive HPV", "Positive HPV Result", ReportUtils.map(moh745Indicators.positiveHPV(), indParams),moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Suspicious Cancer Lesions", "Have Suspicious Cancer Lesions", ReportUtils.map(moh745Indicators.suspiciousCancerLesions(), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Cryotherapy Treatment", "Treated Using Cryotherapy", ReportUtils.map(moh745Indicators.treatedUsingCryotherapy(), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Thermocoagulation Treatment", "Treated Using Thermocoagulation", ReportUtils.map(moh745Indicators.treatedUsingThermocoagulation(), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"LEEP Treatment", "Treated using LEEP", ReportUtils.map(moh745Indicators.treatedUsingLEEP(), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Other Treatment", "Other Treatment Given", ReportUtils.map(moh745Indicators.otherTreatmentGiven(), indParams),moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"HIV Positive Clients Screened", "HIV Positive Clients Screened", ReportUtils.map(moh745Indicators.HIVPositiveClientsScreened(), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"HIV Positive With Positive Screening Results", "HIV Positive With Positive Screening Results", ReportUtils.map(moh745Indicators.HIVPositiveClientsScreened(), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));

        return dsd;
    }

}