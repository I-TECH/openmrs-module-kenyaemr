/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.mchms;

import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyacore.report.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.calculation.library.hiv.IPTStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtRegimenCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.LastCd4Calculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.PatientArtOutComeCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.ViralLoadResultCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchcs.PersonAddressCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.DateOfFirstCTXCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.HeightAtArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.WeightAtArtStartDateCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.ArtCohortStartMonthYearDateConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.CurrentCd4Converter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.DateArtStartDateConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.HeightConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RDQACalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.RDQASimpleObjectRegimenConverter;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.WeightConverter;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ARTRegisterCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.MortalityLineListCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.pmtct.MaternalAnalysisCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.CalculationResultConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.IdentifierConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.ObsMonthYearConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.TBScreeningConverter;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.TBScreeningAtLastVisitDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.anc.EDCandANCNumberPreg1DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.anc.EDCandANCNumberPreg2DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.anc.EDCandANCNumberPreg3DataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ARTFirstSubstitutionDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ARTFirstSwitchDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ARTSecondSubstitutionDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ARTSecondSwitchDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.HTSDiscordanceArtDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.PopulationTypeArtDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.TbStartDateArtDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.WHOStageArtDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.WeightAtArtDataDefinition;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.maternalCohortAnalysis.MaternalAnalysisIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.publicHealthActionReport.PublicHealthActionIndicatorLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Report builder for Maternal Cohort review report
 */
@Component
@Builds({"kenyaemr.mchms.report.cohort.analysis"})
public class MaternalCohortAnalysisReportBuilder extends AbstractReportBuilder {

    @Autowired
    private MaternalAnalysisIndicatorLibrary maternalAnalysisIndicatorLibrary;

    @Override
    protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
        return Arrays.asList(
                new Parameter("startDate", "Start Date", Date.class),
                new Parameter("endDate", "End Date", Date.class)
        );
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor reportDescriptor, ReportDefinition reportDefinition) {
        return Arrays.asList(ReportUtils.map(maternalCohortFirstReview(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    protected DataSetDefinition maternalCohortFirstReview() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cohortDsd.setName("Maternal-Cohort-Analysis");
        cohortDsd.setDescription("First review (12 months cohort)");
        cohortDsd.addColumn("Original cohort-KP (12 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.originaCohortKp12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Original cohort-NP (12 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.originaCohortNp12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Transfer-in(TI) KP (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.transferInMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Transfer-in(TI) NP (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.transferInMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Transfer-out(TO) KP (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.transferOutMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Transfer-out(TO) NP (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.transferOutMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Discharged to CCC KP (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.dischargedToCCCMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Discharged to CCC NP (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.dischargedToCCCMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Net Cohort KP (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.netCohortMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Net Cohort NP (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.netCohortMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Interruption In treatment (LTFU) KP (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.ltfuMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Interruption In treatment (LTFU) NP (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.ltfuMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Reported Dead KP (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.deceasedMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Reported Dead NP (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.deceasedMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Stopped treatment KP (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.stoppedTreatmentMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Stopped treatment NP (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.stoppedTreatmentMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Stopped treatment KP (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.stoppedTreatmentMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Stopped treatment NP (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.stoppedTreatmentMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Alive and active on treatment KP (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.aliveAndActiveOnTreatmentMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Alive and active on treatment NP (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.aliveAndActiveOnTreatmentMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load samples collected maternal Kp (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithViralLoadSamplesCollectedKp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load samples collected maternal Np (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithViralLoadSamplesCollectedNp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results received maternal Kp (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithViralLoadResultsReceivedKp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results received maternal Np (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithViralLoadResultsReceivedNp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results  <1000 copies/ml Kp (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan1000Kp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results  <1000 copies/ml Np (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan1000Np3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results  <400 copies/ml Kp (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan400Kp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results  <400 copies/ml Np (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan400Np3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results  <50 copies/ml Kp (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan50Kp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results  <50 copies/ml Np (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan50Np3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results  >=1000 copies/ml Kp (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsMoreThan1000Kp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results  >=1000 copies/ml Np (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsMoreThan1000Np3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load reviewed (EAC) >=400 copies/ml Kp (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan400WithEACsKp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load reviewed (EAC)  >=400 copies/ml Np (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan400WithEACsNp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load collected after STF >=1000 copies/ml Kp (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFKp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load collected after STF >=1000 copies/ml Np (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFNp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load collected after STF and Repeat Vl >=1000 copies/ml Kp (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlKp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load collected after STF and Repeat VL >=1000 copies/ml Np (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlNp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Number Switched Regimen Line after Confirmed Treatment Failure Kp (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortSwitchedRegimenLineAfterConfirmedSTFKp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Number Switched Regimen Line after Confirmed Treatment Failure Np (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortSwitchedRegimenLineAfterConfirmedSTFNp3Months(), "startDate=${startDate},endDate=${endDate}"), "");




        return cohortDsd;

    }
    protected DataSetDefinition maternalCohortSecondReview() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cohortDsd.setName("Maternal-Cohort-Analysis");
        cohortDsd.setDescription("Second review (24 months cohort)");
        cohortDsd.addColumn("Original cohort-KP (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.originaCohortKp12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        return cohortDsd;

    }
}
