package org.openmrs.module.kenyaemr.reporting.builder.hiv;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.report.CohortReportDescriptor;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastReturnVisitDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.AgeAtARTInitiationCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.BaselineCd4CountAndDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.BaselineCd4PercentAndDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.Cd4CountImprovementCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.Cd4PercentImprovementCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.ChangeInCd4CountCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.ChangeInCd4PercentCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.CurrentArtLineCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.CurrentArtRegimenCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.DateARV1Calculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.DateAndReasonFirstMedicallyEligibleForArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.DateLastSeenCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.DateOfEnrollmentCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.DaysFromArtEligibilityToArtInitiationCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.DaysFromEnrollmentToArtInitiationCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtRegimenCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.IsBirthDateApproximatedCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.IsTransferInCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.IsTransferOutCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.LastCd4Calculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.LastCd4PercentCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.PatientArtOutComeCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TransferInDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TransferOutDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.ViralLoadCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.ViralSuppressionCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.DateOfDeathCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.ChangeInCd4Converter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.CurrentCd4Converter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.MedicallyEligibleConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RegimenConverter;
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
        dsd.addColumn("Enrollment into care date", hivProgramEnrollment(), "", new CalculationResultConverter());
        dsd.addColumn("DOB", new BirthdateDataDefinition(), "", new BirthdateConverter());
        dsd.addColumn("DOB approx", new CalculationDataDefinition("DOB approx", new IsBirthDateApproximatedCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("Age at ART initiation", new CalculationDataDefinition("Age at ART initiation", new AgeAtARTInitiationCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("Sex", new GenderDataDefinition(), "");
        dsd.addColumn("TI", new CalculationDataDefinition("TI", new IsTransferInCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("Date TI", new CalculationDataDefinition("Date TI", new TransferInDateCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("TO", new CalculationDataDefinition("TO", new IsTransferOutCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("Date TO", new CalculationDataDefinition("Date TO", new TransferOutDateCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("ARV Start Date", new CalculationDataDefinition("ARV Start Date", new DateARV1Calculation()), "", new CalculationResultConverter());
        dsd.addColumn("Days from enrollment in care to ART Initiation", new CalculationDataDefinition("Days from enrollment in care to ART Initiation", new DaysFromEnrollmentToArtInitiationCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("Days from ART eligibility to ART Initiation", new CalculationDataDefinition("Days from ART eligibility to ART Initiation", new DaysFromArtEligibilityToArtInitiationCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("Date first medically eligible for ART", new CalculationDataDefinition("Date first medically eligible for ART", new DateAndReasonFirstMedicallyEligibleForArtCalculation()), "", new MedicallyEligibleConverter("date"));
        dsd.addColumn("Reason first medically eligible For ART", new CalculationDataDefinition("Reason first medically eligible For ART", new DateAndReasonFirstMedicallyEligibleForArtCalculation()), "", new MedicallyEligibleConverter("reason"));
        dsd.addColumn("Baseline cd4 count", new CalculationDataDefinition("Baseline cd4 count", new BaselineCd4CountAndDateCalculation()), "", new Cd4ValueAndDateConverter("value"));
        dsd.addColumn("Date of baseline cd4 count", new CalculationDataDefinition("Date of baseline cd4 count", new BaselineCd4CountAndDateCalculation()), "", new Cd4ValueAndDateConverter("date"));
        dsd.addColumn("Baseline cd4 percent", new CalculationDataDefinition("Baseline cd4 percent", new BaselineCd4PercentAndDateCalculation()), "", new Cd4ValueAndDateConverter("value"));
        dsd.addColumn("Date of baseline cd4 percent", new CalculationDataDefinition("Date of baseline cd4 percent", new BaselineCd4PercentAndDateCalculation()), "", new Cd4ValueAndDateConverter("date"));
        dsd.addColumn("Initial ART regimen", new CalculationDataDefinition("First ART regimen", new InitialArtRegimenCalculation()), "", new RegimenConverter());
        dsd.addColumn("Current ART regimen", new CalculationDataDefinition("Current ART regimen", new CurrentArtRegimenCalculation()), "", new RegimenConverter());
        dsd.addColumn("Current ART line", new CalculationDataDefinition("Current ART line", new CurrentArtLineCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("Current cd4 count", new CalculationDataDefinition("Current cd4 count", new LastCd4Calculation()), "", new CurrentCd4Converter("value"));
        dsd.addColumn("Date current cd4 count", new CalculationDataDefinition("Date current cd4 count", new LastCd4Calculation()), "", new CurrentCd4Converter("date"));
        dsd.addColumn("Current cd4 percent", new CalculationDataDefinition("Current cd4 percent", new LastCd4PercentCalculation()), "", new CurrentCd4Converter("value"));
        dsd.addColumn("Date current cd4 percent", new CalculationDataDefinition("Date current cd4 percent", new LastCd4PercentCalculation()), "", new CurrentCd4Converter("date"));
        dsd.addColumn("Change in cd4 count", new CalculationDataDefinition("Change in cd4 count", new ChangeInCd4CountCalculation()), "", new ChangeInCd4Converter());
        dsd.addColumn("Cd4 count improvement", new CalculationDataDefinition("Cd4 count improvement", new Cd4CountImprovementCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("Change in cd4 percent", new CalculationDataDefinition("Change in cd4 percent", new ChangeInCd4PercentCalculation()), "", new ChangeInCd4Converter());
        dsd.addColumn("Cd4 percent improvement", new CalculationDataDefinition("Cd4 percent improvement", new Cd4PercentImprovementCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("Current viral load", viralLoad(report), "", new CurrentCd4Converter("value"));
        dsd.addColumn("Date of current viral load", viralLoad(report), "", new CurrentCd4Converter("date"));
        dsd.addColumn("Viral suppression", new CalculationDataDefinition("Viral suppression", new ViralSuppressionCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("Date of Last visit", new CalculationDataDefinition("Date of Last visit", new DateLastSeenCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("Date of expected next visit", new CalculationDataDefinition("Date of expected next visit", new LastReturnVisitDateCalculation()), "", new CalculationResultConverter());
        dsd.addColumn("Date of death", new CalculationDataDefinition("Date of death", new DateOfDeathCalculation()), "", new CalculationResultConverter());

        dsd.addColumn("ART Outcomes", patientOutComes(report), "onDate=${endDate}", new CalculationResultConverter());


    }

    @Override
    protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor descriptor, PatientDataSetDefinition dsd) {
        CohortDefinition cd = artCohortLibrary.netCohortMonthsBetweenDatesGivenMonths();
        return ReportUtils.map(cd, "startDate=${startDate},endDate=${endDate}");
    }

    private DataDefinition patientOutComes(HybridReportDescriptor descriptor) {
        int months = Integer.parseInt(descriptor.getId().split("\\.")[7]);
        CalculationDataDefinition cd = new CalculationDataDefinition("outcomes", new PatientArtOutComeCalculation());
        cd.addCalculationParameter("months", months);
        cd.addParameter(new Parameter("onDate", "On Date", Date.class));
        return cd;

    }

    private DataDefinition hivProgramEnrollment() {
        CalculationDataDefinition cd = new CalculationDataDefinition("careEnrollment", new DateOfEnrollmentCalculation());
        return cd;
    }

    private DataDefinition viralLoad(HybridReportDescriptor descriptor) {
        int months = Integer.parseInt(descriptor.getId().split("\\.")[7]);
        CalculationDataDefinition cd = new CalculationDataDefinition("viral load", new ViralLoadCalculation());
        cd.addCalculationParameter("months", months);
        return cd;
    }
}
