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
import org.openmrs.module.kenyacore.report.ReportUtils;
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

    static final String[] VILI_VIA_SCREENING = {"VIA", "VILI"};
    static final String[] COLPOSCOPY_SCREENING = {"Colposcopy"};
    static final String[] HPV_TEST_SCREENING = {"HPV Test"};
    static final String[] PAP_SMEAR_SCREENING = {"Pap Smear"};

    static final String[] CRYOTHERAPY = {"Cryotherapy postponed", "Cryotherapy performed (single Visit)", "Cryotherapy performed"};
    static final String[] THERMOCOAGUlation = {"Thermocoagulation"};
    static final String[] LEEP = {"LEEP"};
    static final String[] OTHER = {"Other"};

    static final String POSITIVE = "Positive";
    static final String PRESUMED = "Presumed";

    static final String INITIAL_VISIT = "Initial visit";
    static final String ROUTINE_VISIT = "Routine visit";
    static final String POST_TREATMENT_VISIT = "Post treatment visit";

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
                ReportUtils.map(intialCacxScreenDataSet(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(routineCacxScreenDataSet(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(postCacxScreenDataSet(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    ColumnParameters lessThan25 = new ColumnParameters(null, "<25", "age=<25");
    ColumnParameters adultAbove25 = new ColumnParameters(null, "25-49", "age=25-49");
    ColumnParameters adultAbove50 = new ColumnParameters(null, ">=50", "age=>=50");

    List<ColumnParameters> moh745Disaggregations = Arrays.asList(lessThan25, adultAbove25, adultAbove50);

    String indParams = "startDate=${startDate},endDate=${endDate}";

    /**
     * Creates the dataset for section #1: Cervical Cancer Initial Screening
     *
     * @return the dataset
     */
    protected DataSetDefinition intialCacxScreenDataSet() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("1");
        dsd.setDescription("Initial Cervical Cancer Screening");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.addDimension("age", ReportUtils.map(commonDimensions.moh745AgeGroups(), "onDate=${endDate}"));

        //initial routing
        EmrReportingUtils.addRow(dsd,"VIA/VILI-01", "Received VIA or VIA/ VILI Screening", ReportUtils.map(moh745Indicators.receivedScreening(VILI_VIA_SCREENING, INITIAL_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"PAP SMEAR-01", "Received Pap Smear Screening", ReportUtils.map(moh745Indicators.receivedScreening(PAP_SMEAR_SCREENING, INITIAL_VISIT), indParams),moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"HPV Test-01", "Received HPV Test", ReportUtils.map(moh745Indicators.receivedScreening(HPV_TEST_SCREENING, INITIAL_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));

        EmrReportingUtils.addRow(dsd,"Positive VIA or VIA/VILI-01", "Positive VIA or VIA/VILI Result", ReportUtils.map(moh745Indicators.receivedPositiveScreening(VILI_VIA_SCREENING, INITIAL_VISIT ), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Positive Cytology-01", "Positive Cytology Result", ReportUtils.map(moh745Indicators.receivedPositiveScreening(COLPOSCOPY_SCREENING, INITIAL_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Positive HPV-01", "Positive HPV Result", ReportUtils.map(moh745Indicators.receivedPositiveScreening(HPV_TEST_SCREENING, INITIAL_VISIT), indParams),moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Suspicious Cancer Lesions-01", "Have Suspicious Cancer Lesions", ReportUtils.map(moh745Indicators.receivedSuspiciousScreening(PRESUMED, INITIAL_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));

        EmrReportingUtils.addRow(dsd,"Cryotherapy Treatment-01", "Treated Using Cryotherapy", ReportUtils.map(moh745Indicators.treatedMethod(CRYOTHERAPY, INITIAL_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Thermocoagulation Treatment-01", "Treated Using Thermocoagulation", ReportUtils.map(moh745Indicators.treatedMethod(THERMOCOAGUlation, INITIAL_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"LEEP Treatment-01", "Treated using LEEP", ReportUtils.map(moh745Indicators.treatedMethod(LEEP, INITIAL_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Other Treatment-01", "Other Treatment Given", ReportUtils.map(moh745Indicators.treatedMethod(OTHER, INITIAL_VISIT), indParams),moh745Disaggregations, Arrays.asList("01", "02", "03"));

        EmrReportingUtils.addRow(dsd,"HIV Positive Clients Screened-01", "HIV Positive Clients Screened", ReportUtils.map(moh745Indicators.HIVPositiveClientsScreened(INITIAL_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"HIV Positive With Positive Screening Results-01", "HIV Positive With Positive Screening Results", ReportUtils.map(moh745Indicators.HIVPositiveClientsScreenedWithPositiveResults(INITIAL_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));

        return dsd;
    }

    /**
     * Creates the dataset for section #2: Cervical Cancer Routine Screening
     *
     * @return the dataset
     */
    protected DataSetDefinition routineCacxScreenDataSet() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("2");
        dsd.setDescription("Routine Cervical Cancer Screening");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.addDimension("age", ReportUtils.map(commonDimensions.moh745AgeGroups(), "onDate=${endDate}"));

        //Routine
        EmrReportingUtils.addRow(dsd,"VIA/VILI-02", "Received VIA or VIA/ VILI Screening", ReportUtils.map(moh745Indicators.receivedScreening(VILI_VIA_SCREENING, ROUTINE_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"PAP SMEAR-02", "Received Pap Smear Screening", ReportUtils.map(moh745Indicators.receivedScreening(PAP_SMEAR_SCREENING, ROUTINE_VISIT), indParams),moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"HPV Test-02", "Received HPV Test", ReportUtils.map(moh745Indicators.receivedScreening(HPV_TEST_SCREENING, ROUTINE_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));

        EmrReportingUtils.addRow(dsd,"Positive VIA or VIA/VILI-02", "Positive VIA or VIA/VILI Result", ReportUtils.map(moh745Indicators.receivedPositiveScreening(VILI_VIA_SCREENING, ROUTINE_VISIT ), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Positive Cytology-02", "Positive Cytology Result", ReportUtils.map(moh745Indicators.receivedPositiveScreening(COLPOSCOPY_SCREENING, ROUTINE_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Positive HPV-02", "Positive HPV Result", ReportUtils.map(moh745Indicators.receivedPositiveScreening(HPV_TEST_SCREENING, ROUTINE_VISIT), indParams),moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Suspicious Cancer Lesions-02", "Have Suspicious Cancer Lesions", ReportUtils.map(moh745Indicators.receivedSuspiciousScreening(PRESUMED, ROUTINE_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));

        EmrReportingUtils.addRow(dsd,"Cryotherapy Treatment-02", "Treated Using Cryotherapy", ReportUtils.map(moh745Indicators.treatedMethod(CRYOTHERAPY, ROUTINE_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Thermocoagulation Treatment-02", "Treated Using Thermocoagulation", ReportUtils.map(moh745Indicators.treatedMethod(THERMOCOAGUlation, ROUTINE_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"LEEP Treatment-02", "Treated using LEEP", ReportUtils.map(moh745Indicators.treatedMethod(LEEP, ROUTINE_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Other Treatment-02", "Other Treatment Given", ReportUtils.map(moh745Indicators.treatedMethod(OTHER, ROUTINE_VISIT), indParams),moh745Disaggregations, Arrays.asList("01", "02", "03"));

        EmrReportingUtils.addRow(dsd,"HIV Positive Clients Screened-02", "HIV Positive Clients Screened", ReportUtils.map(moh745Indicators.HIVPositiveClientsScreened(ROUTINE_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"HIV Positive With Positive Screening Results-02", "HIV Positive With Positive Screening Results", ReportUtils.map(moh745Indicators.HIVPositiveClientsScreenedWithPositiveResults(ROUTINE_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));

        return dsd;
    }

    /**
     * Creates the dataset for section #3: Cervical Cancer Post Treatment Screening
     *
     * @return the dataset
     */
    protected DataSetDefinition postCacxScreenDataSet() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("3");
        dsd.setDescription("Post Treatment Cervical Cancer Screening");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.addDimension("age", ReportUtils.map(commonDimensions.moh745AgeGroups(), "onDate=${endDate}"));

        //Post-treatment
        EmrReportingUtils.addRow(dsd,"VIA/VILI-03", "Received VIA or VIA/ VILI Screening", ReportUtils.map(moh745Indicators.receivedScreening(VILI_VIA_SCREENING, POST_TREATMENT_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"PAP SMEAR-03", "Received Pap Smear Screening", ReportUtils.map(moh745Indicators.receivedScreening(PAP_SMEAR_SCREENING, POST_TREATMENT_VISIT), indParams),moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"HPV Test-03", "Received HPV Test", ReportUtils.map(moh745Indicators.receivedScreening(HPV_TEST_SCREENING, POST_TREATMENT_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));

        EmrReportingUtils.addRow(dsd,"Positive VIA or VIA/VILI-03", "Positive VIA or VIA/VILI Result", ReportUtils.map(moh745Indicators.receivedPositiveScreening(VILI_VIA_SCREENING, POST_TREATMENT_VISIT ), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Positive Cytology-03", "Positive Cytology Result", ReportUtils.map(moh745Indicators.receivedPositiveScreening(COLPOSCOPY_SCREENING, POST_TREATMENT_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Positive HPV-03", "Positive HPV Result", ReportUtils.map(moh745Indicators.receivedPositiveScreening(HPV_TEST_SCREENING, POST_TREATMENT_VISIT), indParams),moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Suspicious Cancer Lesions-03", "Have Suspicious Cancer Lesions", ReportUtils.map(moh745Indicators.receivedSuspiciousScreening(PRESUMED, POST_TREATMENT_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));

        EmrReportingUtils.addRow(dsd,"Cryotherapy Treatment-03", "Treated Using Cryotherapy", ReportUtils.map(moh745Indicators.treatedMethod(CRYOTHERAPY, POST_TREATMENT_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Thermocoagulation Treatment-03", "Treated Using Thermocoagulation", ReportUtils.map(moh745Indicators.treatedMethod(THERMOCOAGUlation, POST_TREATMENT_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"LEEP Treatment-03", "Treated using LEEP", ReportUtils.map(moh745Indicators.treatedMethod(LEEP, POST_TREATMENT_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"Other Treatment-03", "Other Treatment Given", ReportUtils.map(moh745Indicators.treatedMethod(OTHER, POST_TREATMENT_VISIT), indParams),moh745Disaggregations, Arrays.asList("01", "02", "03"));

        EmrReportingUtils.addRow(dsd,"HIV Positive Clients Screened-03", "HIV Positive Clients Screened", ReportUtils.map(moh745Indicators.HIVPositiveClientsScreened(POST_TREATMENT_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd,"HIV Positive With Positive Screening Results-03", "HIV Positive With Positive Screening Results", ReportUtils.map(moh745Indicators.HIVPositiveClientsScreenedWithPositiveResults(POST_TREATMENT_VISIT), indParams), moh745Disaggregations, Arrays.asList("01", "02", "03"));

        return dsd;
    }

}