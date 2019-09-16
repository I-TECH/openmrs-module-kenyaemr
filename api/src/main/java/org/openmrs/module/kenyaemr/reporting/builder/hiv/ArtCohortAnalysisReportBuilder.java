/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.hiv;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.report.CohortReportDescriptor;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.calculation.library.hiv.IsTransferInAndHasDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.*;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.ArtCohortRegimenConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.ChangeInCd4Converter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.CurrentCd4Converter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.MedicallyEligibleConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RegimenConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RegimenLineConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.TransferInAndDateConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.CalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.Cd4ValueAndDateConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.IdentifierConverter;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtCohortLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by codehub on 11/06/15.
 */
@Component
@Builds({"kenyaemr.hiv.report.art.cohort.analysis.art.6","kenyaemr.hiv.report.art.cohort.analysis.art.12","kenyaemr.hiv.report.art.cohort.analysis.art.24","kenyaemr.hiv.report.art.cohort.analysis.art.36","kenyaemr.hiv.report.art.cohort.analysis.art.48","kenyaemr.hiv.report.art.cohort.analysis.art.60"})
public class ArtCohortAnalysisReportBuilder extends AbstractHybridReportBuilder {

    @Autowired
    private ArtCohortLibrary artCohortLibrary;

    /**
     *
     * @see org.openmrs.module.kenyacore.report.builder.AbstractCohortReportBuilder#getParameters(ReportDescriptor)
     */
    @Override
    protected List<Parameter> getParameters(ReportDescriptor descriptor) {
        return Arrays.asList(
                new Parameter("startDate", "Start Date", Date.class),
                new Parameter("endDate", "End Date", Date.class)
        );
    }

    /**
     *
     * @see org.openmrs.module.kenyacore.report.builder.AbstractCohortReportBuilder#addColumns(CohortReportDescriptor, PatientDataSetDefinition)
     */
    @Override
    protected void addColumns(HybridReportDescriptor report, PatientDataSetDefinition dsd) {

        PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
        DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(upn.getName(), upn), new IdentifierConverter());
        DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}");
        DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);

        dsd.setName("artCohortAnalysis");
        dsd.addColumn("id", new PatientIdDataDefinition(), "");
        dsd.addColumn("Name", nameDef, "");
        dsd.addColumn("UPN", identifierDef, "");
        dsd.addColumn("Enrollment into care date", hivProgramEnrollment(), "onDate=${endDate}", new CalculationResultConverter());
        dsd.addColumn("DOB", new BirthdateDataDefinition(), "", new BirthdateConverter());
        dsd.addColumn("DOB approx", new CalculationDataDefinition("DOB approx", new IsBirthDateApproximatedCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("Age at ART initiation", new CalculationDataDefinition("Age at ART initiation", new AgeAtARTInitiationCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("Sex", new GenderDataDefinition(), "");
        dsd.addColumn("TI", ti(), "", new TransferInAndDateConverter("state"));
        dsd.addColumn("Date TI", ti(), "", new TransferInAndDateConverter("date"));
        dsd.addColumn("TO", to(report), "onDate=${endDate}", new TransferInAndDateConverter("state"));
        dsd.addColumn("Date TO", to(report), "onDate=${endDate}", new TransferInAndDateConverter("date"));
        dsd.addColumn("ARV Start Date", new CalculationDataDefinition("ARV Start Date", new DateARV1Calculation()), "", new CalculationResultConverter());
        dsd.addColumn("Days from enrollment in care to ART Initiation", new CalculationDataDefinition("Days from enrollment in care to ART Initiation", new DaysFromEnrollmentToArtInitiationCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("Days from ART eligibility to ART Initiation", fromEligibilityToArtStart(report), "onDate=${endDate}", new CalculationResultConverter());
        dsd.addColumn("Date first medically eligible for ART", dateAndReasonFirstMedicallyEligibleForArtCalculation(report), "onDate=${endDate}", new MedicallyEligibleConverter("date"));
        dsd.addColumn("Reason first medically eligible For ART", dateAndReasonFirstMedicallyEligibleForArtCalculation(report), "onDate=${endDate}", new MedicallyEligibleConverter("reason"));
        dsd.addColumn("ART baseline CD4 count", baselineCd4(report), "onDate=${endDate}", new Cd4ValueAndDateConverter("value"));
        dsd.addColumn("Date of ART baseline CD4 count", baselineCd4(report), "onDate=${endDate}", new Cd4ValueAndDateConverter("date"));
        dsd.addColumn("Initial ART regimen", new CalculationDataDefinition("First ART regimen", new InitialArtRegimenCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("Current ART regimen", new CalculationDataDefinition("Current ART regimen", new CurrentArtRegimenCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("Current ART line", new CalculationDataDefinition("Current ART Line", new CurrentArtRegimenLineCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("CD4 at end of follow up", currentCd4Count(report), "onDate=${endDate}", new CurrentCd4Converter("value"));
        dsd.addColumn("CD4 at end of follow up date", currentCd4Count(report), "onDate=${endDate}", new CurrentCd4Converter("date"));
        dsd.addColumn("Change in cd4 count", changeInCd4Count(report), "onDate=${endDate}", new ChangeInCd4Converter());
        dsd.addColumn("Viral load at end of follow up", viralLoad(report), "onDate=${endDate}", new CurrentCd4Converter("value"));
        dsd.addColumn("Date viral load at end of follow up", viralLoad(report), "onDate=${endDate}", new CurrentCd4Converter("date"));
        dsd.addColumn("Viral suppression", viralSuppression(report), "onDate=${endDate}", new CalculationResultConverter());
        dsd.addColumn("Date of Last visit", lastSeen(report), "onDate=${endDate}", new CalculationResultConverter());
        dsd.addColumn("Date of expected next visit", nextAppointmentDate(report), "onDate=${endDate}", new CalculationResultConverter());
        dsd.addColumn("Date of death", death(report), "onDate=${endDate}", new CalculationResultConverter());
        dsd.addColumn("ART Outcomes", patientOutComes(report), "onDate=${endDate}", new CalculationResultConverter());


    }

    @Override
    protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
        Integer period = Integer.parseInt(descriptor.getId().split("\\.")[7]);
        CohortDefinition cd = artCohortLibrary.netCohortMonthsBetweenDatesGivenMonths(period);
        return ReportUtils.map(cd, "startDate=${startDate},endDate=${endDate}");
    }

    private DataDefinition patientOutComes(HybridReportDescriptor descriptor) {
        int months = Integer.parseInt(descriptor.getId().split("\\.")[7]);
        CalculationDataDefinition cd = new CalculationDataDefinition("outcomes", new PatientArtOutComeCalculation());
        cd.addCalculationParameter("outcomePeriod", months);
        cd.addParameter(new Parameter("onDate", "On Date", Date.class));
        return cd;

    }

    private DataDefinition baselineCd4(HybridReportDescriptor descriptor) {
        int months = Integer.parseInt(descriptor.getId().split("\\.")[7]);
        CalculationDataDefinition cd = new CalculationDataDefinition("baselinecd4", new BaselineCd4CountAndDateCalculation());
        cd.addCalculationParameter("outcomePeriod", months);
        cd.addParameter(new Parameter("onDate", "On Date", Date.class));
        return cd;

    }

    private DataDefinition hivProgramEnrollment() {
        CalculationDataDefinition cd = new CalculationDataDefinition("careEnrollment", new DateOfEnrollmentArtCalculation());
        cd.addParameter(new Parameter("onDate", "On Date", Date.class));
        return cd;
    }

    private DataDefinition viralLoad(HybridReportDescriptor descriptor) {
        CalculationDataDefinition cd = new CalculationDataDefinition("viral load", new ViralLoadCalculation());
        cd.addCalculationParameter("outcomePeriod", Integer.parseInt(descriptor.getId().split("\\.")[7]));
        cd.addParameter(new Parameter("onDate", "On Date", Date.class));
        return cd;
    }

    private DataDefinition death(HybridReportDescriptor descriptor) {
        CalculationDataDefinition cd = new CalculationDataDefinition("death", new DateOfDeathArtAnalysisCalculation());
        cd.addParameter(new Parameter("onDate", "On Date", Date.class));
        cd.addCalculationParameter("outcomePeriod", Integer.parseInt(descriptor.getId().split("\\.")[7]));
        return cd;
    }

    private DataDefinition to(HybridReportDescriptor descriptor) {
        CalculationDataDefinition cd = new CalculationDataDefinition("to", new IsArtTransferOutAndHasDateCalculation());
        cd.addParameter(new Parameter("onDate", "On Date", Date.class));
        cd.addCalculationParameter("outcomePeriod", Integer.parseInt(descriptor.getId().split("\\.")[7]));
        return cd;
    }

    private DataDefinition ti() {
        CalculationDataDefinition cd = new CalculationDataDefinition("tiAndDate", new IsTransferInAndHasDateCalculation());
        cd.addParameter(new Parameter("onDate", "On Date", Date.class));
        return cd;
    }

    private DataDefinition fromEligibilityToArtStart(HybridReportDescriptor descriptor) {
        CalculationDataDefinition cd = new CalculationDataDefinition("eligibilityToArtStart", new DaysFromArtEligibilityToArtInitiationCalculation());
        cd.addParameter(new Parameter("onDate", "On Date", Date.class));
        cd.addCalculationParameter("outcomePeriod", Integer.parseInt(descriptor.getId().split("\\.")[7]));
        return cd;
    }

    private DataDefinition dateAndReasonFirstMedicallyEligibleForArtCalculation(HybridReportDescriptor descriptor) {
        CalculationDataDefinition cd = new CalculationDataDefinition("date and reason", new DateAndReasonFirstMedicallyEligibleForArtARTCalculation());
        cd.addParameter(new Parameter("onDate", "On Date", Date.class));
        cd.addCalculationParameter("outcomePeriod", Integer.parseInt(descriptor.getId().split("\\.")[7]));
        return cd;
    }

    private DataDefinition currentARTRegimen(HybridReportDescriptor descriptor) {
        CalculationDataDefinition cd = new CalculationDataDefinition("currentRegimen", new CurrentArtRegimenCohortAnalysisCalculation());
        cd.addParameter(new Parameter("onDate", "On Date", Date.class));
        cd.addCalculationParameter("outcomePeriod", Integer.parseInt(descriptor.getId().split("\\.")[7]));
        return cd;
    }

    private DataDefinition currentCd4Count(HybridReportDescriptor descriptor) {
        CalculationDataDefinition cd = new CalculationDataDefinition("currentCd4", new LastCd4Calculation());
        cd.addParameter(new Parameter("onDate", "On Date", Date.class));
        cd.addCalculationParameter("outcomePeriod", Integer.parseInt(descriptor.getId().split("\\.")[7]));
        return cd;
    }

    private DataDefinition changeInCd4Count(HybridReportDescriptor descriptor) {
        CalculationDataDefinition cd = new CalculationDataDefinition("changeInCd4Count", new ChangeInCd4CountCalculation());
        cd.addParameter(new Parameter("onDate", "On Date", Date.class));
        cd.addCalculationParameter("outcomePeriod", Integer.parseInt(descriptor.getId().split("\\.")[7]));
        return cd;
    }

    private DataDefinition viralSuppression(HybridReportDescriptor descriptor) {
        CalculationDataDefinition cd = new CalculationDataDefinition("viralSuppression", new ViralSuppressionCalculation());
        cd.addParameter(new Parameter("onDate", "On Date", Date.class));
        cd.addCalculationParameter("outcomePeriod", Integer.parseInt(descriptor.getId().split("\\.")[7]));
        return cd;
    }

    private DataDefinition lastSeen(HybridReportDescriptor descriptor) {
        CalculationDataDefinition cd = new CalculationDataDefinition("lastSeenArt", new DateLastSeenArtCalculation());
        cd.addParameter(new Parameter("onDate", "On Date", Date.class));
        cd.addCalculationParameter("outcomePeriod", Integer.parseInt(descriptor.getId().split("\\.")[7]));
        return cd;
    }

    private DataDefinition nextAppointmentDate(HybridReportDescriptor descriptor) {
        CalculationDataDefinition cd = new CalculationDataDefinition("nextAppointmentArt", new LastReturnVisitDateArtAnalysisCalculation());
        cd.addParameter(new Parameter("onDate", "On Date", Date.class));
        cd.addCalculationParameter("outcomePeriod", Integer.parseInt(descriptor.getId().split("\\.")[7]));
        return cd;
    }


}
