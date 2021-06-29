/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.otz;

import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.otz.ETLOtzIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonDimensionLibrary;
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
 * Report builder for ETL OTZ
 */
@Component
@Builds({"kenyaemr.etl.common.report.otz"})
public class OtzReportBuilder extends AbstractReportBuilder {
    @Autowired
    private CommonDimensionLibrary commonDimensions;

    @Autowired
    private ETLOtzIndicatorLibrary otzIndicators;

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    ColumnParameters m_10_to_19 = new ColumnParameters(null, "10-19, Male", "gender=M|age=10-19");
    ColumnParameters f_10_to_19 = new ColumnParameters(null, "10-19, Female", "gender=F|age=10-19");


    ColumnParameters m_20_to_24 = new ColumnParameters(null, "20-24, Male", "gender=M|age=20-24");
    ColumnParameters f_20_to_24 = new ColumnParameters(null, "20-24, Female", "gender=F|age=20-24");

    List<ColumnParameters> standardDisaggregationAgeAndSex = Arrays.asList(
            m_10_to_19, f_10_to_19, m_20_to_24, f_20_to_24);
    List<ColumnParameters> AdolecenceStandardDisaggregationAgeAndSex = Arrays.asList(
            m_10_to_19, f_10_to_19);
    List<ColumnParameters> AdultsStandardDisaggregationAgeAndSex = Arrays.asList(
            m_20_to_24, f_20_to_24);


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
                ReportUtils.map(baselineInformationDataSet(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(additionalMonitoringDataSet(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(routineVLMonitoringTestingUptakeSupressionDataSet(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(otzModulesTrackingDataSet(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(routineVLMonitoringOverallSupressionDataSet(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(trackingSuspectedTreatmentFailureDataSet(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(trackingAttritionsDataSet(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(continuingServicesDataSet(), "startDate=${startDate},endDate=${endDate}")


        );
    }

    /**
     * Creates the dataset for otz baseline information
     *
     * @return the dataset
     */
    protected DataSetDefinition baselineInformationDataSet() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("Baseline Information");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.otzAgeGroups(), "onDate=${endDate}"));
        cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

        String indParams = "startDate=${startDate},endDate=${endDate}";

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Enrolled in OTZ", ReportUtils.map(otzIndicators.newOtzEnrollment(), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("01", "02"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Enrolled in OTZ with VL results(VL within the last 12 months) at Enrolment ", ReportUtils.map(otzIndicators.patientWithVLResultsLast6MonthsAtEnrollment(), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("03", "04"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Enrolled in OTZ with VL < 1000 copies/ml at Enrolment", ReportUtils.map(otzIndicators.patientWithVLResultsLessThan1000AtEnrollment(), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("05", "06"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Enrolled in OTZ with VL < 400 copies/ml at Enrolment", ReportUtils.map(otzIndicators.patientWithVLResultsLessThan400AtEnrollment(), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("07", "08"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Enrolled in OTZ with VL= LDL at Enrolment", ReportUtils.map(otzIndicators.patientWithVLResultsLDLAtEnrollment(), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("09", "10"));

        return cohortDsd;

    }

    protected DataSetDefinition additionalMonitoringDataSet() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("Additional Monitoring");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.otzAgeGroups(), "onDate=${endDate}"));
        cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

        String indParams = "startDate=${startDate},endDate=${endDate}";

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Booked Appointments in the month", ReportUtils.map(otzIndicators.bookedAppointments(), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("11", "12"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Honored Appointments(came on/before the scheduled date)", ReportUtils.map(otzIndicators.appointmentsHonored(), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("13", "14"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Good Adherence(adherence > 95%)", ReportUtils.map(otzIndicators.adolescentsAdherenceGood(), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("15", "16"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Attended support Group", ReportUtils.map(otzIndicators.attendSupportGroup(), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("17", "18"));

        return cohortDsd;

    }


    protected DataSetDefinition routineVLMonitoringTestingUptakeSupressionDataSet() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("Routine VL monitoring Testing Uptake and suppression");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.otzAgeGroups(), "onDate=${endDate}"));
        cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

        String indParams = "startDate=${startDate},endDate=${endDate}";

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total OTZ  eligible for routine viral load testing (cohort reporting month 6)", ReportUtils.map(otzIndicators.patientEligibleForRoutineVL(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("19", "20"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "OTZ whose samples taken for routine viral load testing (cohort reporting month 6)", ReportUtils.map(otzIndicators.patientSamplesTakenForRoutineVL(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("21", "22"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "OTZ with routine follow up VL results at the end of the (cohort reporting month 6)", ReportUtils.map(otzIndicators.patientWithRoutineFollowupVL(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("23", "24"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with follow up VL > 1000 copies/ml (cohort reporting month 6)", ReportUtils.map(otzIndicators.patientWithRoutineFollowupVLGreaterThan1000(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("25", "26"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with routine follow up VL < 1000 copies/ml (cohort reporting month 6)", ReportUtils.map(otzIndicators.patientWithRoutineFollowupVLLessThan1000(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("27", "28"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with routine follow up VL < 400 copies/ml (cohort reporting month 6)", ReportUtils.map(otzIndicators.patientWithRoutineFollowupVLLessThan400(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("29", "30"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with routine VL results reported as LDL (cohort reporting month 6)", ReportUtils.map(otzIndicators.patientWithRoutineVLResultsLDL(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("31", "32"));

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total OTZ  eligible for routine viral load testing (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientEligibleForRoutineVL(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("33", "34"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "OTZ whose samples taken for routine viral load testing (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientSamplesTakenForRoutineVL(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("35", "36"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "OTZ with routine follow up VL results (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientWithRoutineFollowupVL(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("37", "38"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with follow up VL > 1000 copies/ml (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientWithRoutineFollowupVLGreaterThan1000(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("39", "40"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with routine follow up VL < 1000 copies/ml (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientWithRoutineFollowupVLLessThan1000(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("41", "42"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with routine follow up VL < 400 copies/ml (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientWithRoutineFollowupVLLessThan400(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("43", "44"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with routine VL results reported as LDL (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientWithRoutineVLResultsLDL(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("45", "46"));

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total OTZ  eligible for routine viral load testing (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientEligibleForRoutineVL(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("47", "48"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "OTZ whose samples taken for routine viral load testing (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientSamplesTakenForRoutineVL(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("49", "50"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "OTZ with routine follow up VL results (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientWithRoutineFollowupVL(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("51", "52"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with follow up VL > 1000 copies/ml (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientWithRoutineFollowupVLGreaterThan1000(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("53", "54"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with routine follow up VL < 1000 copies/ml (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientWithRoutineFollowupVLLessThan1000(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("55", "56"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with routine follow up VL < 400 copies/ml (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientWithRoutineFollowupVLLessThan400(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("57", "58"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with routine VL results reported as LDL (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientWithRoutineVLResultsLDL(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("59", "60"));

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total OTZ  eligible for routine viral load testing (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientEligibleForRoutineVL(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("61", "62"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "OTZ whose samples taken for routine viral load testing (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientSamplesTakenForRoutineVL(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("63", "64"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "OTZ with routine follow up VL results (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientWithRoutineFollowupVL(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("65", "66"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with follow up VL > 1000 copies/ml (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientWithRoutineFollowupVLGreaterThan1000(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("67", "68"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with routine follow up VL < 1000 copies/ml (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientWithRoutineFollowupVLLessThan1000(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("69", "70"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with routine follow up VL < 400 copies/ml (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientWithRoutineFollowupVLLessThan400(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("71", "72"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with routine VL results reported as LDL (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientWithRoutineVLResultsLDL(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("73", "74"));


        // dsd.addColumn("Active in PMTCT", new CalculationDataDefinition("Active in PMTCT", new NeedsViralLoadTestCalculation()), "", new CalculationResultConverter());

        return cohortDsd;

    }


    protected DataSetDefinition otzModulesTrackingDataSet() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("OTZ modules' tracking");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.otzAgeGroups(), "onDate=${endDate}"));
        cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

        String indParams = "startDate=${startDate},endDate=${endDate}";
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Number of OTZ members who have completed the 8 modules (cohort reporting month 6)", ReportUtils.map(otzIndicators.otzMembersWhoCompletedAllModules(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("75", "76"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Number of OTZ members who have completed the 8 modules (cohort reporting month 12)", ReportUtils.map(otzIndicators.otzMembersWhoCompletedAllModules(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("77", "78"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Number of OTZ members who have completed the 8 modules (cohort reporting month 18)", ReportUtils.map(otzIndicators.otzMembersWhoCompletedAllModules(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("79", "80"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Number of OTZ members who have completed the 8 modules (cohort reporting month 24)", ReportUtils.map(otzIndicators.otzMembersWhoCompletedAllModules(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("81", "82"));

        return cohortDsd;

    }

    protected DataSetDefinition routineVLMonitoringOverallSupressionDataSet() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("Routine VL monitoring overall suppression");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.otzAgeGroups(), "onDate=${endDate}"));
        cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

        String indParams = "startDate=${startDate},endDate=${endDate}";

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid viral load results (cohort reporting month 6)", ReportUtils.map(otzIndicators.patientsWithValidVLOnReportingPeriod(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("83", "84"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid viral load results ≥ 1000 copies/ml (cohort reporting month 6)", ReportUtils.map(otzIndicators.patientWithValidVLGreaterThan1000(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("85", "86"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid viral load results < 1000 copies/ml (cohort reporting month 6)", ReportUtils.map(otzIndicators.patientsWithValidVLLess1000(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("87", "88"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid viral load results <400 copies/ml (cohort reporting month 6)", ReportUtils.map(otzIndicators.patientsWithValidVLLess400(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("89", "90"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid viral load results reported as LDL (cohort reporting month 6)", ReportUtils.map(otzIndicators.patientWithValidVLasLDL(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("91", "92"));

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid viral load results (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientsWithValidVLOnReportingPeriod(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("93", "94"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid viral load results ≥ 1000 copies/ml (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientWithValidVLGreaterThan1000(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("95", "96"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid viral load results < 1000 copies/ml (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientsWithValidVLLess1000(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("97", "98"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid viral load results <400 copies/ml (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientsWithValidVLLess400(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("99", "100"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid viral load results reported as LDL (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientWithValidVLasLDL(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("101", "102"));

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid viral load results (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientsWithValidVLOnReportingPeriod(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("103", "104"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid viral load results ≥ 1000 copies/ml (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientWithValidVLGreaterThan1000(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("105", "106"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid viral load results < 1000 copies/ml (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientsWithValidVLLess1000(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("107", "108"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid viral load results <400 copies/ml (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientsWithValidVLLess400(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("109", "110"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid viral load results reported as LDL (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientWithValidVLasLDL(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("111", "112"));

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid viral load results (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientsWithValidVLOnReportingPeriod(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("113", "114"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid viral load results ≥ 1000 copies/ml (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientWithValidVLGreaterThan1000(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("115", "116"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid viral load results < 1000 copies/ml (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientsWithValidVLLess1000(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("117", "118"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid viral load results <400 copies/ml (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientsWithValidVLLess400(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("119", "120"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid viral load results reported as LDL (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientWithValidVLasLDL(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("121", "122"));

        return cohortDsd;

    }


    protected DataSetDefinition trackingSuspectedTreatmentFailureDataSet() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("Tracking those with suspected treatment failure at baseline or within the reporting period");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.otzAgeGroups(), "onDate=${endDate}"));
        cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

        String indParams = "startDate=${startDate},endDate=${endDate}";

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total number of ALHIV in OTZ with VL > 1000 copies/ml at enrolment (cohort reporting month 6)", ReportUtils.map(otzIndicators.patientWithRepeatVLMoreThan1000(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("123", "124"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV for the period of review who had repeat VL test results (cohort reporting month 6) ", ReportUtils.map(otzIndicators.patientWithRepeatVLResults(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("125", "126"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with repeat VL < 1000 copies/ml (cohort month reporting 6)", ReportUtils.map(otzIndicators.patientWithRepeatVLLessThan1000(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("127", "128"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with repeat VL < 400 copies/ml (cohort month reporting 6)", ReportUtils.map(otzIndicators.patientWithRepeatVLLessThan400(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("129", "130"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with repeat VL = LDL (cohort month reporting 6)", ReportUtils.map(otzIndicators.patientWithRepeatVLasLDL(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("131", "132"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with repeat VL ≥ 1000 copies/ml (cohort reporting month 6)", ReportUtils.map(otzIndicators.patientWithRepeatVLMoreThan1000(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("133", "134"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# switched to second line ART (cohort reporting month 6)", ReportUtils.map(otzIndicators.patientsSwithedToSecondlineArt(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("135", "136"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# switched to third line ART (cohort reporting month 6)", ReportUtils.map(otzIndicators.patientsSwithedToThirdlineArt(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("137", "138"));

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total number of ALHIV in OTZ with VL > 1000 copies/ml (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientWithRepeatVLMoreThan1000(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("139", "140"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV for the period of review who had repeat VL test results (cohort reporting month 12) ", ReportUtils.map(otzIndicators.patientWithRepeatVLResults(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("141", "142"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with repeat VL < 1000 copies/ml (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientWithRepeatVLLessThan1000(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("143", "144"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with repeat VL < 400 copies/ml (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientWithRepeatVLLessThan400(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("145", "146"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with repeat VL = LDL (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientWithRepeatVLasLDL(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("147", "148"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with repeat VL ≥ 1000 copies/ml (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientWithRepeatVLMoreThan1000(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("149", "150"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# switched to second line ART (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientsSwithedToSecondlineArt(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("151", "152"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# switched to third line ART (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientsSwithedToThirdlineArt(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("153", "154"));

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total number of ALHIV in OTZ with VL > 1000 copies/ml (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientWithRepeatVLMoreThan1000(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("155", "156"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV for the period of review who had repeat VL test results (cohort reporting month 18) ", ReportUtils.map(otzIndicators.patientWithRepeatVLResults(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("157", "158"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with repeat VL < 1000 copies/ml (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientWithRepeatVLLessThan1000(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("159", "160"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with repeat VL < 400 copies/ml (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientWithRepeatVLLessThan400(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("161", "162"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with repeat VL = LDL (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientWithRepeatVLasLDL(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("163", "164"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with repeat VL ≥ 1000 copies/ml (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientWithRepeatVLMoreThan1000(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("165", "166"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# switched to second line ART (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientsSwithedToSecondlineArt(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("167", "168"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# switched to third line ART (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientsSwithedToThirdlineArt(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("169", "170"));

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total number of ALHIV in OTZ with VL > 1000 copies/ml (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientWithRepeatVLMoreThan1000(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("171", "172"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV for the period of review who had repeat VL test results (cohort reporting month 24) ", ReportUtils.map(otzIndicators.patientWithRepeatVLResults(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("173", "174"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with repeat VL < 1000 copies/ml (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientWithRepeatVLLessThan1000(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("175", "176"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with repeat VL < 400 copies/ml (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientWithRepeatVLLessThan400(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("177", "178"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with repeat VL = LDL (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientWithRepeatVLasLDL(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("179", "180"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# with repeat VL ≥ 1000 copies/ml (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientWithRepeatVLMoreThan1000(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("181", "182"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# switched to second line ART (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientsSwithedToSecondlineArt(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("183", "184"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "# switched to third line ART (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientsSwithedToThirdlineArt(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("185", "186"));

        return cohortDsd;

    }

    protected DataSetDefinition trackingAttritionsDataSet() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("Tracking Attritions");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.otzAgeGroups(), "onDate=${endDate}"));
        cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

        String indParams = "startDate=${startDate},endDate=${endDate}";
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Transferred out (cohort reporting month 6)", ReportUtils.map(otzIndicators.patientTransferredOut(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("001", "002"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Lost to Follow up (cohort reporting month 6)", ReportUtils.map(otzIndicators.ltfu(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("003", "004"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Transitioned to Adult Care (cohort reporting month 6) ", ReportUtils.map(otzIndicators.adultCare(6), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("005", "006"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Reported Dead (cohort reporting month 6)", ReportUtils.map(otzIndicators.dead(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("007", "008"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Opted Out (cohort reporting month 6)", ReportUtils.map(otzIndicators.optOut(6), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("009", "010"));

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Transfer Out (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientTransferredOut(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("011", "012"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Lost to Follow up (cohort reporting month 12)", ReportUtils.map(otzIndicators.ltfu(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("013", "014"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Transitioned to Adult Care (cohort reporting month 12) ", ReportUtils.map(otzIndicators.adultCare(12), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("015", "016"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Reported Dead (cohort reporting month 12)", ReportUtils.map(otzIndicators.dead(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("017", "018"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Opted Out (cohort reporting month 12)", ReportUtils.map(otzIndicators.optOut(12), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("019", "020"));

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Transfer Out (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientTransferredOut(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("021", "022"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Lost to Follow up (cohort reporting month 18)", ReportUtils.map(otzIndicators.ltfu(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("023", "024"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Transitioned to Adult Care (cohort reporting month 18) ", ReportUtils.map(otzIndicators.adultCare(18), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("025", "026"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Reported Dead (cohort reporting month 18)", ReportUtils.map(otzIndicators.dead(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("027", "028"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Opted Out (cohort reporting month 18)", ReportUtils.map(otzIndicators.optOut(18), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("029", "030"));

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Transfer Out (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientTransferredOut(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("031", "032"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Lost to Follow up (cohort reporting month 24)", ReportUtils.map(otzIndicators.ltfu(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("033", "034"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Transitioned to Adult Care (cohort reporting month 24) ", ReportUtils.map(otzIndicators.adultCare(24), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("035", "036"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Reported Dead (cohort reporting month 24)", ReportUtils.map(otzIndicators.dead(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("037", "038"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Opted Out (cohort reporting month 24)", ReportUtils.map(otzIndicators.optOut(24), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("039", "040"));

        return cohortDsd;
    }

    protected DataSetDefinition continuingServicesDataSet() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("Continuing Services");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.otzAgeGroups(), "onDate=${endDate}"));
        cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

        String indParams = "startDate=${startDate},endDate=${endDate}";

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Number of youths and young persons aged 20-24yrs and still  in the program (cohort reporting month 6)", ReportUtils.map(otzIndicators.adolecentsInProgram(6), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("041", "042"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total youth aged 20-24yrs and still in OTZ who had valid viral load results during the reporting period (cohort reporting month 6)", ReportUtils.map(otzIndicators.patientsWithValidVLOnReportingPeriod(6), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("043", "044"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total Youths aged 20-24yrs in OTZ who had valid viral load results <1000 copies/ml (cohort reporting month 6)", ReportUtils.map(otzIndicators.patientsWithValidVLLess1000(6), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("045", "046"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total Youths aged 20-24yrs in OTZ who had valid viral load results <400 copies/ml (cohort reporting month 6)", ReportUtils.map(otzIndicators.patientsWithValidVLLess400(6), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("047", "048"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Number of Youths aged 20-24yrs who exited from Post OTZ group during the review period (cohort reporting month 6)", ReportUtils.map(otzIndicators.exitedPostotz(6), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("049", "050"));

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Number of youths and young persons aged 20-24yrs and still  in the program (cohort reporting month 12)", ReportUtils.map(otzIndicators.adolecentsInProgram(12), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("051", "052"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total youth aged 20-24yrs and still in OTZ who had valid viral load results during the reporting period (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientsWithValidVLOnReportingPeriod(12), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("053", "054"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total Youths aged 20-24yrs in OTZ who had valid viral load results <1000 copies/ml (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientsWithValidVLLess1000(12), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("055", "056"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total Youths aged 20-24yrs in OTZ who had valid viral load results <400 copies/ml (cohort reporting month 12)", ReportUtils.map(otzIndicators.patientsWithValidVLLess400(12), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("057", "058"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Number of Youths aged 20-24yrs who exited from Post OTZ group during the review period (cohort reporting month 12)", ReportUtils.map(otzIndicators.exitedPostotz(12), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("059", "060"));

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Number of youths and young persons aged 20-24yrs and still  in the program (cohort reporting month 18)", ReportUtils.map(otzIndicators.adolecentsInProgram(18), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("061", "062"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total youth aged 20-24yrs and still in OTZ who had valid viral load results during the reporting period (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientsWithValidVLOnReportingPeriod(18), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("063", "064"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total Youths aged 20-24yrs in OTZ who had valid viral load results <1000 copies/ml (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientsWithValidVLLess1000(18), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("065", "066"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total Youths aged 20-24yrs in OTZ who had valid viral load results <400 copies/ml (cohort reporting month 18)", ReportUtils.map(otzIndicators.patientsWithValidVLLess400(18), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("067", "068"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Number of Youths aged 20-24yrs who exited from Post OTZ group during the review period (cohort reporting month 18)", ReportUtils.map(otzIndicators.exitedPostotz(18), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("069", "070"));

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Number of youths and young persons aged 20-24yrs and still  in the program (cohort reporting month 24)", ReportUtils.map(otzIndicators.adolecentsInProgram(24), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("071", "072"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total youth aged 20-24yrs and still in OTZ who had valid viral load results during the reporting period (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientsWithValidVLOnReportingPeriod(24), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("073", "074"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total Youths aged 20-24yrs in OTZ who had valid viral load results <1000 copies/ml (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientsWithValidVLLess1000(24), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("075", "076"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total Youths aged 20-24yrs in OTZ who had valid viral load results <400 copies/ml (cohort reporting month 24)", ReportUtils.map(otzIndicators.patientsWithValidVLLess400(24), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("077", "078"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Number of Youths aged 20-24yrs who exited from Post OTZ group during the review period (cohort reporting month 24)", ReportUtils.map(otzIndicators.exitedPostotz(24), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("079", "080"));

        return cohortDsd;

    }


}
