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
import org.openmrs.module.kenyaemr.calculation.library.ActiveInMCHProgramCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.DateConfirmedHivPositiveCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.IPTOutcomeCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.IPTOutcomeDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.IPTStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.DateOfEnrollmentArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.otz.OnOTZProgramCalculation;
import org.openmrs.module.kenyaemr.calculation.library.ovc.OnOVCProgramCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.InTbProgramCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.*;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ActivePatientsSnapshotCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.ActiveInProgramConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.CalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.ActivePatientsPopulationTypeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.MFLCodeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.*;
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
        dsd.addColumn("Population Type", new ActivePatientsPopulationTypeDataDefinition(), "");
        dsd.addColumn("Date confirmed positive", new CalculationDataDefinition("Date confirmed positive", new DateConfirmedHivPositiveCalculation()), "", new DateArtStartDateConverter());
        dsd.addColumn("Enrollment Date", new CalculationDataDefinition("Enrollment Date", new DateOfEnrollmentArtCalculation()), "", new DateArtStartDateConverter());
        dsd.addColumn("Art Start Date", new ETLArtStartDateDataDefinition(), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("First Regimen", new ETLFirstRegimenDataDefinition(), "");
        //dsd.addColumn("First Substitution Date", new ETLFirstSubstitutionDateDataDefinition(), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Current Regimen", new ETLCurrentRegimenDataDefinition(), "");
        dsd.addColumn("Current Regimen Line", new ETLCurrentRegLineDataDefinition(), "");
        dsd.addColumn("Last WHO Stage", new WHOStageArtDataDefinition(), "");
        dsd.addColumn("Last WHO Stage Date", new ETLLastWHOStageDateDataDefinition(), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Last VL Result",  lastVlResultDataDefinition, "endDate=${endDate}");
        dsd.addColumn("VL Validility", new ETLLastVLResultValidityDataDefinition(), "");
        dsd.addColumn("Last VL Justification", new ETLLastVLJustificationDataDefinition(),"");
        dsd.addColumn("Last VL Date", lastVLDateDataDefinition, "endDate=${endDate}", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Active in PMTCT",activeInMchDataDefinition, "endDate=${endDate}", new ActiveInProgramConverter());
        dsd.addColumn("Active in OVC", activeInOvcDataDefinition,"endDate=${endDate}",new ActiveInProgramConverter());
        dsd.addColumn("Active in OTZ", activeInOtzDataDefinition, "endDate=${endDate}",new ActiveInProgramConverter());
        dsd.addColumn("Active in TB", activeInTbDataDefinition, "endDate=${endDate}",new ActiveInProgramConverter());
        dsd.addColumn("IPT Start Date", new CalculationDataDefinition("IPT Start Date", new IPTStartDateCalculation()), "", new SimpleResultDateConverter());
        dsd.addColumn("IPT Outcome", new CalculationDataDefinition("IPT Outcome", new IPTOutcomeCalculation()), "", new IPTOutcomeDataConverter());
        dsd.addColumn("IPT Outcome Date", new CalculationDataDefinition("IPT Outcome Date", new IPTOutcomeDateCalculation()), "", new SimpleResultDateConverter());
        dsd.addColumn("Stability", stabilityDataDefinition, "endDate=${endDate}");
        dsd.addColumn("Differentiated care model", new ETLDifferentiatedCareModelDataDefinition(), "");
        dsd.addColumn("Last Visit Date", lastVisitDateDataDefinition,"endDate=${endDate}", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Self Visit Date", new ETLHivSelfVisitDateDataDefinition(), "", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Next Appointment Date", nextAppointmentDateDataDefinition, "endDate=${endDate}", new DateConverter(DATE_FORMAT));
        dsd.addColumn("Months of Prescription", new ETLMonthsOfPrescriptionDataDefinition(), "");
        dsd.addColumn("Refill Date", new ETLRefillDateDataDefinition(), "", new DateConverter(DATE_FORMAT));

        return dsd;
    }
}
