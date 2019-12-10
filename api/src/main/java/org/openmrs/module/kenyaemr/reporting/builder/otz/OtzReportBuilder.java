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
                new Parameter("endDate", "End Date", Date.class),
                new Parameter("dateBasedReporting", "", String.class)
        );
    }

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor reportDescriptor, ReportDefinition reportDefinition) {
        return Arrays.asList(
                ReportUtils.map(baselineInformationDataSet(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(additionalMonitoringDataSet(), "startDate=${startDate},endDate=${endDate}"),
              //  ReportUtils.map(routineVLMonitoringTestingUptakeSupressionDataSet(), "startDate=${startDate},endDate=${endDate}"),
              //  ReportUtils.map(otzModulesTrackingDataSet(), "startDate=${startDate},endDate=${endDate}"),
              //  ReportUtils.map(routineVLMonitoringOverallSupressionDataSet(), "startDate=${startDate},endDate=${endDate}"),
               // ReportUtils.map(trackingSuspectedTreatmentFailureDataSet(), "startDate=${startDate},endDate=${endDate}"),
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
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "VL results(VL within the last 12 months) at Enrolment ", ReportUtils.map(otzIndicators.patientWithVLResultsLast6MonthsAtEnrollment(), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList( "03", "04"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "VL < 1000 copies/ml at Enrolment", ReportUtils.map(otzIndicators.patientWithVLResultsLessThan1000AtEnrollment(), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("05", "06"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "VL < 400 copies/ml at Enrolment", ReportUtils.map(otzIndicators.patientWithVLResultsLessThan400AtEnrollment(), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("07", "08"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "VL= LDL at Enrolment", ReportUtils.map(otzIndicators.patientWithVLResultsLDLAtEnrollment(), indParams), AdolecenceStandardDisaggregationAgeAndSex, Arrays.asList("09", "10"));

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


        EmrReportingUtils.addRow(cohortDsd, "#", "Booked Appointments in the month", ReportUtils.map(otzIndicators.bookedAppointments(), indParams), standardDisaggregationAgeAndSex, Arrays.asList("11", "12","13", "14"));
        EmrReportingUtils.addRow(cohortDsd, "#", "Honored Appointments(came on/before the scheduled date)", ReportUtils.map(otzIndicators.appointmentsHonored(), indParams), standardDisaggregationAgeAndSex, Arrays.asList("15", "16","17", "18"));
        EmrReportingUtils.addRow(cohortDsd, "#", "Good Adherence(adherence > 95%)", ReportUtils.map(otzIndicators.adolescentsAdherenceGood(), indParams), standardDisaggregationAgeAndSex, Arrays.asList("19", "20","21", "22"));
        EmrReportingUtils.addRow(cohortDsd, "#", "Attended support Group", ReportUtils.map(otzIndicators.attendSupportGroup(), indParams), standardDisaggregationAgeAndSex, Arrays.asList("23", "24","25", "26"));

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


        return cohortDsd;

    }

    protected DataSetDefinition routineVLMonitoringTestingUptakeSupressionDataSet() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("Routine VL monitoring to assess the VL Testing Uptake and suppression");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.otzAgeGroups(), "onDate=${endDate}"));
        cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

        String indParams = "startDate=${startDate},endDate=${endDate}";


        return cohortDsd;

    }


    protected DataSetDefinition routineVLMonitoringOverallSupressionDataSet() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("Routine VL monitoring to assess actual overall suppression for the cohort");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.otzAgeGroups(), "onDate=${endDate}"));
        cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

        String indParams = "startDate=${startDate},endDate=${endDate}";


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
        EmrReportingUtils.addRow(cohortDsd, "#", "Transferred out", ReportUtils.map(otzIndicators.transferout(), indParams), standardDisaggregationAgeAndSex, Arrays.asList("001", "002","003","004"));
        EmrReportingUtils.addRow(cohortDsd, "#", "Lost to Follow up", ReportUtils.map(otzIndicators.ltfu(), indParams), standardDisaggregationAgeAndSex, Arrays.asList("005", "006","007","008"));
        EmrReportingUtils.addRow(cohortDsd, "#", "Transitioned to Adult Care ", ReportUtils.map(otzIndicators.adultCare(), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("009", "010"));
        EmrReportingUtils.addRow(cohortDsd, "#", "Reported Dead", ReportUtils.map(otzIndicators.dead(), indParams), standardDisaggregationAgeAndSex, Arrays.asList("011", "012","013","014"));
        EmrReportingUtils.addRow(cohortDsd, "#", "Opted Out", ReportUtils.map(otzIndicators.optOut(), indParams), standardDisaggregationAgeAndSex, Arrays.asList("015", "016","017","018"));



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

        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Adolescents aged 20-24 Yrs and still in the program", ReportUtils.map(otzIndicators.adolecentsInProgram(), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("019", "020"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid VL during the reporting period", ReportUtils.map(otzIndicators.patientsWithValidVLOnReportingPeriod(), indParams), standardDisaggregationAgeAndSex, Arrays.asList("021", "022","023", "024"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid VL < 1000 copies/ml", ReportUtils.map(otzIndicators.patientsWithValidVLLess1000(), indParams), standardDisaggregationAgeAndSex, Arrays.asList("025", "026","027", "028"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Total ALHIV in OTZ who had valid VL < 400 copies/ml", ReportUtils.map(otzIndicators.patientsWithValidVLLess400(), indParams), standardDisaggregationAgeAndSex, Arrays.asList("029", "030","031", "032"));
        EmrReportingUtils.addRow(cohortDsd, "ALHIV", "Exited from Post OTZ", ReportUtils.map(otzIndicators.exitedPostotz(), indParams), AdultsStandardDisaggregationAgeAndSex, Arrays.asList("033", "034","035", "036"));

        return cohortDsd;

    }

}
