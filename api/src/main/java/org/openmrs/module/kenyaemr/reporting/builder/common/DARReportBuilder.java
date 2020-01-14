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

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.DARCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.DarAppointmentPeriodConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.dar.*;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.*;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Builds({"kenyaemr.common.report.cccDar"})
public class DARReportBuilder extends AbstractHybridReportBuilder {
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    @Override
    protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
        return Arrays.asList(
                new Parameter("startDate", "Start Date", Date.class)
        );
    }

    @Override
    protected void addColumns(HybridReportDescriptor report, PatientDataSetDefinition dsd) {
    }

    @Override
    protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
        return null;
    }


    protected Mapped<CohortDefinition> allPatientsCohort() {
        CohortDefinition cd = new DARCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.setName("All Patient Visits");
        return ReportUtils.map(cd, "startDate=${startDate}");
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {

        PatientDataSetDefinition allVisits = patientVisitsDataSetDefinition("allVisit");
        allVisits.addRowFilter(allPatientsCohort());
        DataSetDefinition allPatientsDSD = allVisits;

        return Arrays.asList(
                ReportUtils.map(allPatientsDSD, "startDate=${startDate}")
        );
    }

    protected PatientDataSetDefinition patientVisitsDataSetDefinition(String datasetName) {

        PatientDataSetDefinition dsd = new PatientDataSetDefinition(datasetName);
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        String defParam = "startDate=${startDate}";

        PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
        DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);

        DataConverter formatter = new ObjectFormatter("{familyName}, {givenName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), formatter);
        dsd.addColumn("id", new PersonIdDataDefinition(), "");
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("CCC No", identifierDef, "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "", null);
        dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));
        DarVisitDateDataDefinition visitDateDataDefinition= new DarVisitDateDataDefinition();
        visitDateDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));

        dsd.addColumn("Date", visitDateDataDefinition, defParam, new DateConverter(DATE_FORMAT));
        // enrolled in care
        dsd.addColumn("Enrolled in Care(<1 yrs)", mapEnrolledInCareDataDefinition("Enrolled in Care(<1 yrs)", null, 0, null), defParam, null);
        dsd.addColumn("Enrolled in Care(1-9 yrs)", mapEnrolledInCareDataDefinition("Enrolled in Care(1-9 yrs)", 1, 9, null), defParam, null);
        dsd.addColumn("Enrolled in Care(10-14 yrs(M))", mapEnrolledInCareDataDefinition("Enrolled in Care(10-14 yrs(M))", 10, 14, "M"), defParam, null);
        dsd.addColumn("Enrolled in Care(10-14 yrs(F)", mapEnrolledInCareDataDefinition("Enrolled in Care(10-14 yrs(F))", 10, 14, "F"), defParam, null);
        dsd.addColumn("Enrolled in Care(15-19 yrs(M))", mapEnrolledInCareDataDefinition("Enrolled in Care(15-19 yrs(M))", 15, 19, "M"), defParam, null);
        dsd.addColumn("Enrolled in Care(15-19 yrs(F)", mapEnrolledInCareDataDefinition("Enrolled in Care(15-19 yrs(F))", 15, 19, "F"), defParam, null);
        dsd.addColumn("Enrolled in Care(20-24 yrs(M))", mapEnrolledInCareDataDefinition("Enrolled in Care(20-24 yrs(M))", 20, 24, "M"), defParam, null);
        dsd.addColumn("Enrolled in Care(20-24 yrs(F)", mapEnrolledInCareDataDefinition("Enrolled in Care(20-24 yrs(F))", 20, 24, "F"), defParam, null);
        dsd.addColumn("Enrolled in Care(25+ yrs(M))", mapEnrolledInCareDataDefinition("Enrolled in Care(25+ yrs(M))", 25, null, "M"), defParam, null);
        dsd.addColumn("Enrolled in Care(25+ yrs(F)", mapEnrolledInCareDataDefinition("Enrolled in Care(25+ yrs(F))", 25, null, "F"), defParam, null);
        dsd.addColumn("Enrolled in Care(Key Population)", mapKpDataDefinition("Enrolled in Care(Key Population)", "Enrollment"), defParam, null);

        // on treatment preparation
        dsd.addColumn("On treatment preparation(0-14 yrs)", mapOnTreatmentPreparationDataDefinition("On treatment preparation(0-14 yrs)", 0, 14, null), defParam, null);
        dsd.addColumn("On treatment preparation(15+ yrs)", mapOnTreatmentPreparationDataDefinition("On treatment preparation(15+ yrs)", 15, null, null), defParam, null);
        //starting art
        dsd.addColumn("Starting ART(<1 yrs)", mapDarStartingArtDataDefinition("Starting ART(<1 yrs)", null, 0, null), defParam, null);
        dsd.addColumn("Starting ART(1-9 yrs)", mapDarStartingArtDataDefinition("Starting ART(1-9 yrs)", 1, 9, null), defParam, null);
        dsd.addColumn("Starting ART(10-14 yrs(M))", mapDarStartingArtDataDefinition("Starting ART(10-14 yrs(M))", 10, 14, "M"), defParam, null);
        dsd.addColumn("Starting ART(10-14 yrs(F)", mapDarStartingArtDataDefinition("Starting ART(10-14 yrs(F))", 10, 14, "F"), defParam, null);
        dsd.addColumn("Starting ART(15-19 yrs(M))", mapDarStartingArtDataDefinition("Starting ART(15-19 yrs(M))", 15, 19, "M"), defParam, null);
        dsd.addColumn("Starting ART(15-19 yrs(F)", mapDarStartingArtDataDefinition("Starting ART(15-19 yrs(F))", 15, 19, "F"), defParam, null);
        dsd.addColumn("Starting ART(20-24 yrs(M))", mapDarStartingArtDataDefinition("Starting ART(20-24 yrs(M))", 20, 24, "M"), defParam, null);
        dsd.addColumn("Starting ART(20-24 yrs(F)", mapDarStartingArtDataDefinition("Starting ART(20-24 yrs(F))", 20, 24, "F"), defParam, null);
        dsd.addColumn("Starting ART(25+ yrs(M))", mapDarStartingArtDataDefinition("Starting ART(25+ yrs(M))", 25, null, "M"), defParam, null);
        dsd.addColumn("Starting ART(25+ yrs(F)", mapDarStartingArtDataDefinition("Starting ART(25+ yrs(F))", 25, null, "F"), defParam, null);
        dsd.addColumn("Starting ART(Key Population)", mapKpDataDefinition("Starting ART(Key Population)", "Starting ART"), defParam, null);

        //current on art
        dsd.addColumn("Current on ART(<1 yrs)", mapCurrentOnArtDataDefinition("Current on ART(<1 yrs)", null, 0, null), defParam, new DarAppointmentPeriodConverter());
        dsd.addColumn("Current on ART(1-9 yrs)", mapCurrentOnArtDataDefinition("Current on ART(1-9 yrs)", 1, 9, null), defParam, new DarAppointmentPeriodConverter());
        dsd.addColumn("Current on ART(10-14 yrs(M))", mapCurrentOnArtDataDefinition("Current on ART(10-14 yrs(M))", 10, 14, "M"), defParam, new DarAppointmentPeriodConverter());
        dsd.addColumn("Current on ART(10-14 yrs(F)", mapCurrentOnArtDataDefinition("Current on ART(10-14 yrs(F))", 10, 14, "F"), defParam, new DarAppointmentPeriodConverter());
        dsd.addColumn("Current on ART(15-19 yrs(M))", mapCurrentOnArtDataDefinition("Current on ART(15-19 yrs(M))", 15, 19, "M"), defParam, new DarAppointmentPeriodConverter());
        dsd.addColumn("Current on ART(15-19 yrs(F)", mapCurrentOnArtDataDefinition("Current on ART(15-19 yrs(F))", 15, 19, "F"), defParam, new DarAppointmentPeriodConverter());
        dsd.addColumn("Current on ART(20-24 yrs(M))", mapCurrentOnArtDataDefinition("Current on ART(20-24 yrs(M))", 20, 24, "M"), defParam, new DarAppointmentPeriodConverter());
        dsd.addColumn("Current on ART(20-24 yrs(F)", mapCurrentOnArtDataDefinition("Current on ART(20-24 yrs(F))", 20, 24, "F"), defParam, new DarAppointmentPeriodConverter());
        dsd.addColumn("Current on ART(25+ yrs(M))", mapCurrentOnArtDataDefinition("Current on ART(25+ yrs(M))", 25, null, "M"), defParam, new DarAppointmentPeriodConverter());
        dsd.addColumn("Current on ART(25+ yrs(F)", mapCurrentOnArtDataDefinition("Current on ART(25+ yrs(F))", 25, null, "F"), defParam, new DarAppointmentPeriodConverter());
        dsd.addColumn("Current on ART(Key Population)", mapKpDataDefinition("Current on ART(Key Population)", "On ART"), defParam, null);

        //On Ctx/Dapsone
        dsd.addColumn("CTX/Dapsone(<1 yrs)", mapCtxDapsoneDataDefinition("CTX/Dapsone(<1 yrs)", null, 0, null), defParam, new DarAppointmentPeriodConverter());
        dsd.addColumn("CTX/Dapsone(1-9 yrs)", mapCtxDapsoneDataDefinition("CTX/Dapsone(1-9 yrs)", 1, 9, null), defParam, new DarAppointmentPeriodConverter());
        dsd.addColumn("CTX/Dapsone(10-14 yrs)", mapCtxDapsoneDataDefinition("CTX/Dapsone(10-14 yrs)", 10, 14, null), defParam, new DarAppointmentPeriodConverter());
        dsd.addColumn("CTX/Dapsone(15-19 yrs)", mapCtxDapsoneDataDefinition("CTX/Dapsone(15-19 yrs)", 15, 19, null), defParam, new DarAppointmentPeriodConverter());
        dsd.addColumn("CTX/Dapsone(20-24 yrs)", mapCtxDapsoneDataDefinition("CTX/Dapsone(20-24 yrs)", 20, 24, null), defParam, new DarAppointmentPeriodConverter());
        dsd.addColumn("CTX/Dapsone(25+ yrs)", mapCtxDapsoneDataDefinition("CTX/Dapsone(25+ yrs)", 25, null, null), defParam, new DarAppointmentPeriodConverter());

        //TB Screening and Results
        dsd.addColumn("TB Screening(<1 yrs)", mapTbScreeningDataDefinition("TB Screening(<1 yrs)", null, 0, null), defParam, new DarAppointmentPeriodConverter());
        dsd.addColumn("TB Screening(1-9 yrs)", mapTbScreeningDataDefinition("TB Screening(1-9 yrs)", 1, 9, null), defParam, new DarAppointmentPeriodConverter());
        dsd.addColumn("TB Screening(10-14 yrs)", mapTbScreeningDataDefinition("TB Screening(10-14 yrs)", 10, 14, null), defParam, new DarAppointmentPeriodConverter());
        dsd.addColumn("TB Screening(15-19 yrs)", mapTbScreeningDataDefinition("TB Screening(15-19 yrs)", 15, 19, null), defParam, new DarAppointmentPeriodConverter());
        dsd.addColumn("TB Screening(20-24 yrs)", mapTbScreeningDataDefinition("TB Screening(20-24 yrs)", 20, 24, null), defParam, new DarAppointmentPeriodConverter());
        dsd.addColumn("TB Screening(25+ yrs)", mapTbScreeningDataDefinition("TB Screening(25+ yrs)", 25, null, null), defParam, new DarAppointmentPeriodConverter());

        DarTbScreeningResultDataDefinition tbResult = new DarTbScreeningResultDataDefinition("Presumed TB");
        tbResult.addParameter(new Parameter("startDate", "Start Date", Date.class));

        dsd.addColumn("Presumed TB", tbResult, defParam, null);

        //IPT Initiation
        dsd.addColumn("Started on IPT(<1 yrs)", mapIptInitiationDataDefinition("Started on IPT(<1 yrs)", null, 0, null), defParam, null);
        dsd.addColumn("Started on IPT(1-9 yrs)", mapIptInitiationDataDefinition("Started on IPT(1-9 yrs)", 1, 9, null), defParam, null);
        dsd.addColumn("Started on IPT(10-14 yrs)", mapIptInitiationDataDefinition("Started on IPT(10-14 yrs)", 10, 14, null), defParam, null);
        dsd.addColumn("Started on IPT(15-19 yrs)", mapIptInitiationDataDefinition("Started on IPT(15-19 yrs)", 15, 19, null), defParam, null);
        dsd.addColumn("Started on IPT(20-24 yrs)", mapIptInitiationDataDefinition("Started on IPT(20-24 yrs)", 20, 24, null), defParam, null);
        dsd.addColumn("Started on IPT(25+ yrs)", mapIptInitiationDataDefinition("Started on IPT(25+ yrs)", 25, null, null), defParam, null);
        // Cacx screening for F 18+
        dsd.addColumn("Screened for Cervical Cancer F 18+", mapCacxScreeningDataDefinition("Screened for Cervical Cancer F 18+"), defParam, null);
        dsd.addColumn("Female 15+ on Modern FP", mapOnModernFpDataDefinition("Female 15+ on Modern FP"), defParam, null);

        return dsd;
    }

    private DarStartingArtDataDefinition mapDarStartingArtDataDefinition(String name, Integer minAge, Integer maxAge, String sex) {
        DarStartingArtDataDefinition newOnArtDef = new DarStartingArtDataDefinition(name, minAge, maxAge, sex);
        newOnArtDef.addParameter(new Parameter("startDate", "Start Date", Date.class));
        return newOnArtDef;

    }

    private DarEnrolledInCareDataDefinition mapEnrolledInCareDataDefinition(String name, Integer minAge, Integer maxAge, String sex) {
        DarEnrolledInCareDataDefinition enrolledInCareDataDefinition = new DarEnrolledInCareDataDefinition(name, minAge, maxAge, sex);
        enrolledInCareDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        return enrolledInCareDataDefinition;

    }

    private DarOnTreatmentPreparationDataDefinition mapOnTreatmentPreparationDataDefinition(String name, Integer minAge, Integer maxAge, String sex) {
        DarOnTreatmentPreparationDataDefinition onTreatmentPrepDataDefinition = new DarOnTreatmentPreparationDataDefinition(name, minAge, maxAge, sex);
        onTreatmentPrepDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        return onTreatmentPrepDataDefinition;

    }

    private DarCurrentOnArtDataDefinition mapCurrentOnArtDataDefinition(String name, Integer minAge, Integer maxAge, String sex) {
        DarCurrentOnArtDataDefinition currentOnArtDataDefinition = new DarCurrentOnArtDataDefinition(name, minAge, maxAge, sex);
        currentOnArtDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        return currentOnArtDataDefinition;

    }

    private DarCtxDapsoneDataDefinition mapCtxDapsoneDataDefinition(String name, Integer minAge, Integer maxAge, String sex) {
        DarCtxDapsoneDataDefinition onCtxDapsone = new DarCtxDapsoneDataDefinition(name, minAge, maxAge, sex);
        onCtxDapsone.addParameter(new Parameter("startDate", "Start Date", Date.class));
        return onCtxDapsone;

    }

    private DarTbScreeningDataDefinition mapTbScreeningDataDefinition(String name, Integer minAge, Integer maxAge, String sex) {
        DarTbScreeningDataDefinition tbScreeningDataDefinition = new DarTbScreeningDataDefinition(name, minAge, maxAge, sex);
        tbScreeningDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        return tbScreeningDataDefinition;

    }

    private DarStartedIptDataDefinition mapIptInitiationDataDefinition(String name, Integer minAge, Integer maxAge, String sex) {
        DarStartedIptDataDefinition iptInitiationDataDefinition = new DarStartedIptDataDefinition(name, minAge, maxAge, sex);
        iptInitiationDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        return iptInitiationDataDefinition;

    }

    private DarKeyPopulationDataDefinition mapKpDataDefinition(String name, String section) {
        DarKeyPopulationDataDefinition iptInitiationDataDefinition = new DarKeyPopulationDataDefinition(name, section);
        iptInitiationDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        return iptInitiationDataDefinition;

    }

    private DarCacxScreeningDataDefinition mapCacxScreeningDataDefinition(String name) {
        DarCacxScreeningDataDefinition cacxScreeningDataDefinition = new DarCacxScreeningDataDefinition(name);
        cacxScreeningDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        return cacxScreeningDataDefinition;

    }

    private DarOnModernFPDataDefinition mapOnModernFpDataDefinition(String name) {
        DarOnModernFPDataDefinition onModernFPDataDefinition = new DarOnModernFPDataDefinition(name);
        onModernFPDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        return onModernFPDataDefinition;

    }
}
