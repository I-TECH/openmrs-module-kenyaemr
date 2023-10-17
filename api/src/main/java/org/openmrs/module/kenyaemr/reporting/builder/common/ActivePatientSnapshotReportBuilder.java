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
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.calculation.library.hiv.DateConfirmedHivPositiveCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.IPTOutcomeCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.IPTOutcomeDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.IPTStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.PatientsWithAdvancedHivDiseaseCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.DateOfEnrollmentArtCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.DateArtStartDateConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.IPTOutcomeDataConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.SimpleResultDateConverter;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ActivePatientsSnapshotCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.ActiveInProgramConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.BooleanResultsConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.ActivePatientsPopulationTypeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.MFLCodeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ActiveInMchDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ActiveInOtzDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ActiveInOvcDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ActiveInTbDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.AgeAtReportingDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.BaselineCD4CountDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.BaselineCD4DateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.BloodPressureDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLArtStartDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLCaseManagerDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLCurrentRegLineDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLCurrentRegimenDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLDifferentiatedCareModelDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLFirstRegimenDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLHivSelfVisitDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLLastCD4DateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLLastCD4ResultDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLLastVLDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLLastVLJustificationDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLLastVLResultDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLLastVLResultValidityDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLLastVisitDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLLastWHOStageDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLMonthsOfPrescriptionDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLNextAppointmentDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLRefillDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLStabilityDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.HeightAtArtDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.MedicalCoverDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.MedicalCoverStatusDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.NCDDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.NCDStatusDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.NCDsDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.WHOStageArtDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.WeightAtArtDataDefinition;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
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
@Builds({"kenyaemr.common.report.activePatientsLinelist"})
public class ActivePatientSnapshotReportBuilder extends AbstractHybridReportBuilder {
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    @Override
    protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
        return Arrays.asList(
                new Parameter("startDate", "Start Date", Date.class),
                new Parameter("endDate", "End Date", Date.class),
                new Parameter("dateBasedReporting", "", String.class)
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
        CohortDefinition cd = new ActivePatientsSnapshotCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setName("Active Patients");
        return ReportUtils.map(cd, "startDate=${startDate},endDate=${endDate}");
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {

        PatientDataSetDefinition allVisits = activePatientsDataSetDefinition("activePatients");
        allVisits.addRowFilter(allPatientsCohort());
        DataSetDefinition allPatientsDSD = allVisits;

        return Arrays.asList(
                ReportUtils.map(allPatientsDSD, "startDate=${startDate},endDate=${endDate}")
        );
    }

    protected PatientDataSetDefinition activePatientsDataSetDefinition(String datasetName) {

        PatientDataSetDefinition dsd = new PatientDataSetDefinition(datasetName);
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        String defParam = "startDate=${startDate},endDate=${endDate}";

        PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
        PatientIdentifierType nupi = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_UNIQUE_PATIENT_IDENTIFIER);
        DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), identifierFormatter);
        DataDefinition nupiDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(nupi.getName(), nupi), identifierFormatter);
        AgeAtReportingDataDefinition ageAtReportingDataDefinition = new AgeAtReportingDataDefinition();
        ageAtReportingDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ETLLastVLResultDataDefinition lastVlResultDataDefinition = new ETLLastVLResultDataDefinition();
        lastVlResultDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ETLLastVLDateDataDefinition lastVLDateDataDefinition = new ETLLastVLDateDataDefinition();
        lastVLDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ETLLastVLResultValidityDataDefinition lastVLResultValidityDataDefinition = new ETLLastVLResultValidityDataDefinition();
        lastVLResultValidityDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ETLStabilityDataDefinition stabilityDataDefinition = new ETLStabilityDataDefinition();
        stabilityDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ETLLastVisitDateDataDefinition lastVisitDateDataDefinition = new ETLLastVisitDateDataDefinition();
        lastVisitDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ETLNextAppointmentDateDataDefinition nextAppointmentDateDataDefinition = new ETLNextAppointmentDateDataDefinition();
        nextAppointmentDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ActiveInMchDataDefinition activeInMchDataDefinition = new ActiveInMchDataDefinition();
        activeInMchDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ActiveInOvcDataDefinition activeInOvcDataDefinition = new ActiveInOvcDataDefinition();
        activeInOvcDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ActiveInOtzDataDefinition activeInOtzDataDefinition = new ActiveInOtzDataDefinition();
        activeInOtzDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ActiveInTbDataDefinition activeInTbDataDefinition = new ActiveInTbDataDefinition();
        activeInTbDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ETLCaseManagerDataDefinition etlCaseManagerDataDefinition = new ETLCaseManagerDataDefinition();
        etlCaseManagerDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ETLDifferentiatedCareModelDataDefinition eTLDifferentiatedCareModelDataDefinition = new ETLDifferentiatedCareModelDataDefinition();
        eTLDifferentiatedCareModelDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ETLRefillDateDataDefinition eTLRefillDateDataDefinition = new ETLRefillDateDataDefinition();
        eTLRefillDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ETLMonthsOfPrescriptionDataDefinition eTLMonthsOfPrescriptionDataDefinition = new ETLMonthsOfPrescriptionDataDefinition();
        eTLMonthsOfPrescriptionDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ETLHivSelfVisitDateDataDefinition eTLHivSelfVisitDateDataDefinition = new ETLHivSelfVisitDateDataDefinition();
        eTLHivSelfVisitDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ETLLastVLJustificationDataDefinition eTLLastVLJustificationDataDefinition = new ETLLastVLJustificationDataDefinition();
        eTLLastVLJustificationDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        NCDsDataDefinition ncDsDataDefinition = new NCDsDataDefinition();
        ncDsDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        NCDDateDataDefinition ncdDateDataDefinition = new NCDDateDataDefinition();
        ncdDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        NCDStatusDataDefinition ncdStatusDataDefinition = new NCDStatusDataDefinition();
        ncdStatusDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        BloodPressureDataDefinition bloodPressureDataDefinition = new BloodPressureDataDefinition();
        bloodPressureDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        bloodPressureDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        MedicalCoverDataDefinition medicalCoverDataDefinition = new MedicalCoverDataDefinition();
        medicalCoverDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        medicalCoverDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        MedicalCoverStatusDataDefinition medicalCoverStatusDataDefinition = new MedicalCoverStatusDataDefinition();
        medicalCoverStatusDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        medicalCoverStatusDataDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
        ETLLastCD4ResultDataDefinition lastCD4ResultDataDefinition = new ETLLastCD4ResultDataDefinition();
        lastCD4ResultDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
        ETLLastCD4DateDataDefinition etlLastCD4DateDataDefinition = new ETLLastCD4DateDataDefinition();
        etlLastCD4DateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));


        DataConverter formatter = new ObjectFormatter("{familyName}, {givenName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), formatter);
        dsd.addColumn("MFL Code", new MFLCodeDataDefinition(), "");
        dsd.addColumn("id", new PersonIdDataDefinition(), "");
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("CCC No", identifierDef, "");
        dsd.addColumn("NUPI", nupiDef, "");
        dsd.addColumn("Sex", new GenderDataDefinition(), "", null);
        dsd.addColumn("DOB", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));
        dsd.addColumn("Age at reporting", ageAtReportingDataDefinition, "endDate=${endDate}");
        //dsd.addColumn("Age", new AgeDataDefinition(), "", new DataConverter[0]);
        dsd.addColumn("Weight", new WeightAtArtDataDefinition(), "");
        dsd.addColumn("Height", new HeightAtArtDataDefinition(), "");
        dsd.addColumn("Blood Pressure", bloodPressureDataDefinition, "endDate=${endDate}");
        dsd.addColumn("Population Type", new ActivePatientsPopulationTypeDataDefinition(), "");
        dsd.addColumn("Date confirmed positive", new CalculationDataDefinition("Date confirmed positive", new DateConfirmedHivPositiveCalculation()), "", new DateArtStartDateConverter());
        dsd.addColumn("Enrollment Date", new CalculationDataDefinition("Enrollment Date", new DateOfEnrollmentArtCalculation()), "", new DateArtStartDateConverter());
        dsd.addColumn("Art Start Date", new ETLArtStartDateDataDefinition(), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("First Regimen", new ETLFirstRegimenDataDefinition(), "");
        //dsd.addColumn("First Substitution Date", new ETLFirstSubstitutionDateDataDefinition(), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Current Regimen", new ETLCurrentRegimenDataDefinition(), "");
        dsd.addColumn("Current Regimen Line", new ETLCurrentRegLineDataDefinition(), "");
        dsd.addColumn("Baseline CD4", new BaselineCD4CountDataDefinition(), "");
        dsd.addColumn("Date of Baseline CD4 test", new BaselineCD4DateDataDefinition(), "");
        dsd.addColumn("Latest CD4 Count",  lastCD4ResultDataDefinition, "endDate=${endDate}");
        dsd.addColumn("Latest CD4 Count Date ",etlLastCD4DateDataDefinition,"endDate=${endDate}");
        dsd.addColumn("Last WHO Stage", new WHOStageArtDataDefinition(), "");
        dsd.addColumn("Last WHO Stage Date", new ETLLastWHOStageDateDataDefinition(), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Last VL Result",  lastVlResultDataDefinition, "endDate=${endDate}");
        dsd.addColumn("VL Validility", lastVLResultValidityDataDefinition, "endDate=${endDate}");
        dsd.addColumn("Last VL Justification", eTLLastVLJustificationDataDefinition,"endDate=${endDate}");
        dsd.addColumn("Last VL Date", lastVLDateDataDefinition, "endDate=${endDate}", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Active in PMTCT",activeInMchDataDefinition, "endDate=${endDate}", new ActiveInProgramConverter());
        dsd.addColumn("Active in OVC", activeInOvcDataDefinition,"endDate=${endDate}",new ActiveInProgramConverter());
        dsd.addColumn("Active in OTZ", activeInOtzDataDefinition, "endDate=${endDate}",new ActiveInProgramConverter());
        dsd.addColumn("Active in TB", activeInTbDataDefinition, "endDate=${endDate}",new ActiveInProgramConverter());
        dsd.addColumn("TPT Start Date", new CalculationDataDefinition("IPT Start Date", new IPTStartDateCalculation()), "", new SimpleResultDateConverter());
        dsd.addColumn("TPT Outcome", new CalculationDataDefinition("IPT Outcome", new IPTOutcomeCalculation()), "", new IPTOutcomeDataConverter());
        dsd.addColumn("TPT Outcome Date", new CalculationDataDefinition("IPT Outcome Date", new IPTOutcomeDateCalculation()), "", new SimpleResultDateConverter());
        dsd.addColumn("Establishment", stabilityDataDefinition, "endDate=${endDate}");
        dsd.addColumn("Differentiated care model", eTLDifferentiatedCareModelDataDefinition, "endDate=${endDate}");
        dsd.addColumn("Last Visit Date", lastVisitDateDataDefinition,"endDate=${endDate}", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Self Visit Date", eTLHivSelfVisitDateDataDefinition, "endDate=${endDate}", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Next Appointment Date", nextAppointmentDateDataDefinition, "endDate=${endDate}", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Months of Prescription", eTLMonthsOfPrescriptionDataDefinition, "endDate=${endDate}");
        dsd.addColumn("Refill Date", eTLRefillDateDataDefinition, "endDate=${endDate}", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Case Manager", etlCaseManagerDataDefinition, "endDate=${endDate}");
        dsd.addColumn("NCDs", ncDsDataDefinition, "endDate=${endDate}");
        dsd.addColumn("NCDs Onset Date", ncdDateDataDefinition, "endDate=${endDate}");
        dsd.addColumn("NCDs status", ncdStatusDataDefinition, "endDate=${endDate}");
        dsd.addColumn("AHD Client", new CalculationDataDefinition("AHD Client", new PatientsWithAdvancedHivDiseaseCalculation()), "", new BooleanResultsConverter());
        dsd.addColumn("Medical cover", medicalCoverDataDefinition, "endDate=${endDate}");
        dsd.addColumn("Medical cover status", medicalCoverStatusDataDefinition, "endDate=${endDate}");


        return dsd;
    }
}
