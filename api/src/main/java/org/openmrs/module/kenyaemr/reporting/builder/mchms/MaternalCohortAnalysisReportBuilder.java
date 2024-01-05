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
        return Arrays.asList(
                ReportUtils.map(maternalCohortFirstReview(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(maternalCohortSecondReview(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    protected DataSetDefinition maternalCohortFirstReview() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cohortDsd.setName("First Review");
        cohortDsd.setDescription("(12 Months Cohort)");
        cohortDsd.addColumn("Original cohort-KP (12 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.originalCohortKp12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Original cohort-NP (12 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.originalCohortNp12Months(), "startDate=${startDate},endDate=${endDate}"), "");
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
        cohortDsd.addColumn("Alive and active on treatment KP (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.aliveAndActiveOnTreatmentMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Alive and active on treatment NP (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.aliveAndActiveOnTreatmentMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load samples collected maternal Kp (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithViralLoadSamplesCollectedKp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load samples collected maternal Np (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithViralLoadSamplesCollectedNp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results received maternal Kp (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithViralLoadResultsReceivedKp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results received maternal Np (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithViralLoadResultsReceivedNp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <1000 copies/ml Kp (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan1000Kp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <1000 copies/ml Np (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan1000Np3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <400 copies/ml Kp (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan400Kp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <400 copies/ml Np (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan400Np3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <50 copies/ml Kp (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan50Kp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <50 copies/ml Np (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan50Np3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results >=1000 copies/ml Kp (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsMoreThan1000Kp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results >=1000 copies/ml Np (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsMoreThan1000Np3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load reviewed (EAC) >=400 copies/ml Kp (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan400WithEACsKp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load reviewed (EAC) >=400 copies/ml Np (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan400WithEACsNp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load collected after STF >=1000 copies/ml Kp (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFKp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load collected after STF >=1000 copies/ml Np (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFNp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load collected after STF and Repeat Vl >=1000 copies/ml Kp (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlKp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load collected after STF and Repeat VL >=1000 copies/ml Np (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlNp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Number Switched Regimen Line after Confirmed Treatment Failure Kp (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortSwitchedRegimenLineAfterConfirmedSTFKp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Number Switched Regimen Line after Confirmed Treatment Failure Np (3 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortSwitchedRegimenLineAfterConfirmedSTFNp3Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Transfer-in(TI) KP (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.transferInMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Transfer-in(TI) NP (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.transferInMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Transfer-out(TO) KP (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.transferOutMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Transfer-out(TO) NP (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.transferOutMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Discharged to CCC KP (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.dischargedToCCCMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Discharged to CCC NP (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.dischargedToCCCMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Net Cohort KP (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.netCohortMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Net Cohort NP (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.netCohortMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Interruption In treatment (LTFU) KP (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.ltfuMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Interruption In treatment (LTFU) NP (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.ltfuMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Reported Dead KP (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.deceasedMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Reported Dead NP (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.deceasedMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Stopped treatment KP (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.stoppedTreatmentMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Stopped treatment NP (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.stoppedTreatmentMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Alive and active on treatment KP (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.aliveAndActiveOnTreatmentMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Alive and active on treatment NP (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.aliveAndActiveOnTreatmentMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load samples collected maternal Kp (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithViralLoadSamplesCollectedKp6Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load samples collected maternal Np (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithViralLoadSamplesCollectedNp6Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results received maternal Kp (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithViralLoadResultsReceivedKp6Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results received maternal Np (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithViralLoadResultsReceivedNp6Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <1000 copies/ml Kp (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan1000Kp6Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <1000 copies/ml Np (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan1000Np6Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <400 copies/ml Kp (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan400Kp6Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <400 copies/ml Np (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan400Np6Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <50 copies/ml Kp (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan50Kp6Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <50 copies/ml Np (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan50Np6Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results >=1000 copies/ml Kp (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsMoreThan1000Kp6Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results >=1000 copies/ml Np (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsMoreThan1000Np6Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load reviewed (EAC) >=400 copies/ml Kp (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan400WithEACsKp6Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load reviewed (EAC) >=400 copies/ml Np (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan400WithEACsNp6Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load collected after STF >=1000 copies/ml Kp (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFKp6Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load collected after STF >=1000 copies/ml Np (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFNp6Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load collected after STF and Repeat Vl >=1000 copies/ml Kp (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlKp6Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load collected after STF and Repeat VL >=1000 copies/ml Np (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlNp6Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Number Switched Regimen Line after Confirmed Treatment Failure Kp (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortSwitchedRegimenLineAfterConfirmedSTFKp6Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Number Switched Regimen Line after Confirmed Treatment Failure Np (6 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortSwitchedRegimenLineAfterConfirmedSTFNp6Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Transfer-in(TI) KP (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.transferInMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Transfer-in(TI) NP (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.transferInMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Transfer-out(TO) KP (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.transferOutMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Transfer-out(TO) NP (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.transferOutMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Discharged to CCC KP (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.dischargedToCCCMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Discharged to CCC NP (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.dischargedToCCCMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Net Cohort KP (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.netCohortMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Net Cohort NP (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.netCohortMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Interruption In treatment (LTFU) KP (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.ltfuMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Interruption In treatment (LTFU) NP (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.ltfuMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Reported Dead KP (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.deceasedMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Reported Dead NP (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.deceasedMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Stopped treatment KP (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.stoppedTreatmentMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Stopped treatment NP (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.stoppedTreatmentMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Alive and active on treatment KP (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.aliveAndActiveOnTreatmentMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Alive and active on treatment NP (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.aliveAndActiveOnTreatmentMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load samples collected maternal Kp (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithViralLoadSamplesCollectedKp12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load samples collected maternal Np (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithViralLoadSamplesCollectedNp12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results received maternal Kp (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithViralLoadResultsReceivedKp12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results received maternal Np (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithViralLoadResultsReceivedNp12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <1000 copies/ml Kp (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan1000Kp12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <1000 copies/ml Np (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan1000Np12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <400 copies/ml Kp (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan400Kp12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <400 copies/ml Np (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan400Np12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <50 copies/ml Kp (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan50Kp12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <50 copies/ml Np (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan50Np12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results >=1000 copies/ml Kp (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsMoreThan1000Kp12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results >=1000 copies/ml Np (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsMoreThan1000Np12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load reviewed (EAC) >=400 copies/ml Kp (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan400WithEACsKp12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load reviewed (EAC) >=400 copies/ml Np (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan400WithEACsNp12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load collected after STF >=1000 copies/ml Kp (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFKp12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load collected after STF >=1000 copies/ml Np (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFNp12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load collected after STF and Repeat Vl >=1000 copies/ml Kp (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlKp12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load collected after STF and Repeat VL >=1000 copies/ml Np (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlNp12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Number Switched Regimen Line after Confirmed Treatment Failure Kp (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortSwitchedRegimenLineAfterConfirmedSTFKp12Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Number Switched Regimen Line after Confirmed Treatment Failure Np (12 Months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortSwitchedRegimenLineAfterConfirmedSTFNp12Months(), "startDate=${startDate},endDate=${endDate}"), "");



        return cohortDsd;

    }


    protected DataSetDefinition maternalCohortSecondReview() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cohortDsd.setName("Second Review");
        cohortDsd.setDescription("(24 Months Cohort)");
        cohortDsd.addColumn("Original cohort-KP (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.originalCohortKp24Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Original cohort-NP (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.originalCohortNp24Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Transfer-in(TI) KP (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.transferInMaternalKp24MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Transfer-in(TI) NP (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.transferInMaternalNp24MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Transfer-out(TO) KP (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.transferOutMaternalKp24MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Transfer-out(TO) NP (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.transferOutMaternalNp24MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Discharged to CCC KP (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.dischargedToCCCMaternalKp24MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Discharged to CCC NP (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.dischargedToCCCMaternalNp24MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Net Cohort KP (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.netCohortMaternalKp24MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Net Cohort NP (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.netCohortMaternalNp24MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Interruption In treatment (LTFU) KP (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.ltfuMaternalKp24MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Interruption In treatment (LTFU) NP (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.ltfuMaternalNp24MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Reported Dead KP (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.deceasedMaternalKp24MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Reported Dead NP (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.deceasedMaternalNp24MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Stopped treatment KP (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.stoppedTreatmentMaternalKp24MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Stopped treatment NP (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.stoppedTreatmentMaternalNp24MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Alive and active on treatment KP (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.aliveAndActiveOnTreatmentMaternalKp24MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Alive and active on treatment NP (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.aliveAndActiveOnTreatmentMaternalNp24MonthsCohort(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load samples collected maternal Kp (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithViralLoadSamplesCollectedKp24Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load samples collected maternal Np (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithViralLoadSamplesCollectedNp24Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results received maternal Kp (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithViralLoadResultsReceivedKp24Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results received maternal Np (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithViralLoadResultsReceivedNp24Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <1000 copies/ml Kp (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan1000Kp24Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <1000 copies/ml Np (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan1000Np24Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <400 copies/ml Kp (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan400Kp24Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <400 copies/ml Np (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan400Np24Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <50 copies/ml Kp (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan50Kp24Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results <50 copies/ml Np (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsLessThan50Np24Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results >=1000 copies/ml Kp (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsMoreThan1000Kp24Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load results >=1000 copies/ml Np (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.viralLoadResultsMoreThan1000Np24Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load reviewed (EAC) >=400 copies/ml Kp (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan400WithEACsKp24Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load reviewed (EAC) >=400 copies/ml Np (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan400WithEACsNp24Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load collected after STF >=1000 copies/ml Kp (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFKp24Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load collected after STF >=1000 copies/ml Np (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFNp24Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load collected after STF and Repeat Vl >=1000 copies/ml Kp (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlKp24Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Viral Load collected after STF and Repeat VL >=1000 copies/ml Np (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlNp24Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Number Switched Regimen Line after Confirmed Treatment Failure Kp (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortSwitchedRegimenLineAfterConfirmedSTFKp24Months(), "startDate=${startDate},endDate=${endDate}"), "");
        cohortDsd.addColumn("Number Switched Regimen Line after Confirmed Treatment Failure Np (24 months)", "", ReportUtils.map(maternalAnalysisIndicatorLibrary.maternalCohortSwitchedRegimenLineAfterConfirmedSTFNp24Months(), "startDate=${startDate},endDate=${endDate}"), "");

        return cohortDsd;

    }
}
