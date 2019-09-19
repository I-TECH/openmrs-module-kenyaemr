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

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.CurrentArtRegimenCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TransferInDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TransferOutDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.*;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.*;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.DARCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.dar.DarCurrentOnArtDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.dar.DarEnrolledInCareDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.dar.DarOnTreatmentPreparationDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.dar.DarStartingArtDataDefinition;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
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
        dsd.addColumn("Unique Patient No", identifierDef, "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "", null);
        dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));
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

        //current on art
        dsd.addColumn("Current on ART(<1 yrs)", mapCurrentOnArtDataDefinition("Current on ART(<1 yrs)", null, 0, null), defParam, null);
        dsd.addColumn("Current on ART(1-9 yrs)", mapCurrentOnArtDataDefinition("Current on ART(1-9 yrs)", 1, 9, null), defParam, null);
        dsd.addColumn("Current on ART(10-14 yrs(M))", mapCurrentOnArtDataDefinition("Current on ART(10-14 yrs(M))", 10, 14, "M"), defParam, null);
        dsd.addColumn("Current on ART(10-14 yrs(F)", mapCurrentOnArtDataDefinition("Current on ART(10-14 yrs(F))", 10, 14, "F"), defParam, null);
        dsd.addColumn("Current on ART(15-19 yrs(M))", mapCurrentOnArtDataDefinition("Current on ART(15-19 yrs(M))", 15, 19, "M"), defParam, null);
        dsd.addColumn("Current on ART(15-19 yrs(F)", mapCurrentOnArtDataDefinition("Current on ART(15-19 yrs(F))", 15, 19, "F"), defParam, null);
        dsd.addColumn("Current on ART(20-24 yrs(M))", mapCurrentOnArtDataDefinition("Current on ART(20-24 yrs(M))", 20, 24, "M"), defParam, null);
        dsd.addColumn("Current on ART(20-24 yrs(F)", mapCurrentOnArtDataDefinition("Current on ART(20-24 yrs(F))", 20, 24, "F"), defParam, null);
        dsd.addColumn("Current on ART(25+ yrs(M))", mapCurrentOnArtDataDefinition("Current on ART(25+ yrs(M))", 25, null, "M"), defParam, null);
        dsd.addColumn("Current on ART(25+ yrs(F)", mapCurrentOnArtDataDefinition("Current on ART(25+ yrs(F))", 25, null, "F"), defParam, null);

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
}
