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
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731Greencard.ETLMoh731GreenCardIndicatorLibrary;
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
 * Report builder for ETL MOH 731 KDoD
 */
@Component
@Builds({"kenyaemr.etl.common.report.Moh731KDoD"})
public class Moh731KDoDReportBuilder extends AbstractReportBuilder {
    @Autowired
    private CommonDimensionLibrary commonDimensions;

    @Autowired
    private ETLMoh731GreenCardIndicatorLibrary moh731GreenCardIndicators;


    public static final String DATE_FORMAT = "yyyy-MM-dd";

    ColumnParameters colInfants_T = new ColumnParameters(null, "<1, Troupe", "age=<1|cadre=T");
    ColumnParameters colInfants_C = new ColumnParameters(null, "<1, Civilian", "age=<1|cadre=C");

    ColumnParameters _10_to_14_T = new ColumnParameters(null, "10-14, Troupe", "cadre=T|age=10-14");
    ColumnParameters _10_to_14_C = new ColumnParameters(null, "10-14, Civilian", "cadre=C|age=10-14");

    ColumnParameters _15_to_19_T = new ColumnParameters(null, "15-19, Troupe", "cadre=T|age=15-19");
    ColumnParameters _15_to_19_C = new ColumnParameters(null, "15-19, Civilian", "cadre=C|age=15-19");

    ColumnParameters _20_to_24_T = new ColumnParameters(null, "20-24, Troupe", "cadre=T|age=20-24");
    ColumnParameters _20_to_24_C = new ColumnParameters(null, "20-24, Civilian", "cadre=C|age=20-24");

    ColumnParameters _25_to_49_T = new ColumnParameters(null, "25-49, Troupe", "cadre=T|age=25-49");
    ColumnParameters _25_to_49_C = new ColumnParameters(null, "25-49, Civilian", "cadre=C|age=25-49");

    ColumnParameters above_50_T = new ColumnParameters(null, "50+, Troupe", "cadre=T|age=50+");
    ColumnParameters above_50_C = new ColumnParameters(null, "50+, Civilian", "cadre=C|age=50+");

    ColumnParameters colTotal = new ColumnParameters(null, "Total", "");
    ColumnParameters children_1_to_9_T = new ColumnParameters(null, "1-9,Troupe", "age=1-9|cadre=T");
    ColumnParameters children_1_to_9_C = new ColumnParameters(null, "1-9,Civilian", "age=1-9|cadre=C");

    ColumnParameters adult_15_to_19_T = new ColumnParameters(null, "15-19,Troupe", "age=15-19|cadre=T");
    ColumnParameters adult_15_to_19_C = new ColumnParameters(null, "15-19,Civilian", "age=15-19|cadre=C");
    ColumnParameters adult_20_to_24_T = new ColumnParameters(null, "20-24,Troupe", "age=20-24|cadre=T");
    ColumnParameters adult_20_to_24_C = new ColumnParameters(null, "20-24,Civilian", "age=20-24|cadre=C");

    ColumnParameters _0_to_14_T = new ColumnParameters(null, "0-14,Troupe", "age=0-14|cadre=T");
    ColumnParameters _0_to_14_C = new ColumnParameters(null, "0-14,Civilian", "age=0-14|cadre=C");
    ColumnParameters adult_15_and_above_T = new ColumnParameters(null, "15+,Troupe", "age=15+|cadre=T");
    ColumnParameters adult_15_and_above_C = new ColumnParameters(null, "15+,Civilian", "age=15+|cadre=C");

    ColumnParameters m_10_to_14_T = new ColumnParameters(null, "10-14, Male,Troupe", "gender=M|age=10-14|cadre=T");
    ColumnParameters m_10_to_14_C = new ColumnParameters(null, "10-14, Male,Civilian", "gender=M|age=10-14|cadre=C");
    ColumnParameters f_10_to_14_T = new ColumnParameters(null, "10-14, Female,Troupe", "gender=F|age=10-14|cadre=T");
    ColumnParameters f_10_to_14_C = new ColumnParameters(null, "10-14, Female,Civilian", "gender=F|age=10-14|cadre=C");

    ColumnParameters m_15_to_19_T = new ColumnParameters(null, "15-19, Male,Troupe", "gender=M|age=15-19|cadre=T");
    ColumnParameters m_15_to_19_C = new ColumnParameters(null, "15-19, Male,Civilian", "gender=M|age=15-19|cadre=C");
    ColumnParameters f_15_to_19_T = new ColumnParameters(null, "15-19, Female,Troupe", "gender=F|age=15-19|cadre=T");
    ColumnParameters f_15_to_19_C = new ColumnParameters(null, "15-19, Female,Civilian", "gender=F|age=15-19|cadre=C");

    ColumnParameters m_20_to_24_T = new ColumnParameters(null, "20-24, Male,Troupe", "gender=M|age=20-24|cadre=T");
    ColumnParameters m_20_to_24_C = new ColumnParameters(null, "20-24, Male,Civilian", "gender=M|age=20-24|cadre=C");
    ColumnParameters f_20_to_24_T = new ColumnParameters(null, "20-24, Female,Troupe", "gender=F|age=20-24|cadre=T");
    ColumnParameters f_20_to_24_C = new ColumnParameters(null, "20-24, Female,Civilian", "gender=F|age=20-24|cadre=C");


    ColumnParameters m_25_to_49_T = new ColumnParameters(null, "25-49, Male,Troupe", "gender=M|age=25-49|cadre=T");
    ColumnParameters m_25_to_49_C = new ColumnParameters(null, "25-49, Male,Civilian", "gender=M|age=25-49|cadre=C");
    ColumnParameters f_25_to_49_T = new ColumnParameters(null, "25-49, Female,Troupe", "gender=F|age=25-49|cadre=T");
    ColumnParameters f_25_to_49_C = new ColumnParameters(null, "25-49, Female,Civilian", "gender=F|age=25-49|cadre=C");
    ColumnParameters m_50_and_above_T = new ColumnParameters(null, "50+, Male,Troupe", "gender=M|age=50+|cadre=T");
    ColumnParameters m_50_and_above_C = new ColumnParameters(null, "50+, Male,Civilian", "gender=M|age=50+|cadre=C");
    ColumnParameters f_50_and_above_T = new ColumnParameters(null, "50+, Female,Troupe", "gender=F|age=50+|cadre=T");
    ColumnParameters f_50_and_above_C = new ColumnParameters(null, "50+, Female,Civilian", "gender=F|age=50+|cadre=C");
    ColumnParameters troupe_dimension = new ColumnParameters(null, "Troupe", "cadre=T");
    ColumnParameters civilian_dimension = new ColumnParameters(null, "Civilian", "cadre=C");

    List<ColumnParameters> kdodDisaggregation = Arrays.asList(
            children_1_to_9_T,children_1_to_9_C,  m_10_to_14_T,m_10_to_14_C, f_10_to_14_T,f_10_to_14_C, m_15_to_19_T,m_15_to_19_C,f_15_to_19_T,f_15_to_19_C,
            m_20_to_24_T,m_20_to_24_C, f_20_to_24_T,f_20_to_24_C, m_25_to_49_T,m_25_to_49_C, f_25_to_49_T,f_25_to_49_C,m_50_and_above_T,
            m_50_and_above_C,f_50_and_above_T,f_50_and_above_C,colTotal);

    List<ColumnParameters> standardKDoDDisaggregation = Arrays.asList(
            colInfants_T,colInfants_C, children_1_to_9_T,children_1_to_9_C,  m_10_to_14_T, m_10_to_14_C, f_10_to_14_T, f_10_to_14_C,
            m_15_to_19_T, m_15_to_19_C, f_15_to_19_T, f_15_to_19_C ,m_20_to_24_T,m_20_to_24_C,f_20_to_24_T,f_20_to_24_C,m_25_to_49_T,m_25_to_49_C,f_25_to_49_T,f_25_to_49_C,m_50_and_above_T,
            m_50_and_above_C,f_50_and_above_T,f_50_and_above_C, colTotal);

    List<ColumnParameters> ageAndCadreDisaggregation = Arrays.asList(
            colInfants_T,colInfants_C, children_1_to_9_T,children_1_to_9_C, _10_to_14_T,_10_to_14_C, adult_15_to_19_T,adult_15_to_19_C,
            adult_20_to_24_T,adult_20_to_24_C,_25_to_49_T,_25_to_49_C, above_50_T,above_50_C, colTotal);

    List<ColumnParameters> kdodCadreWithAge = Arrays.asList(
            children_1_to_9_T,children_1_to_9_C, _10_to_14_T,_10_to_14_C, _15_to_19_T,_15_to_19_C, _20_to_24_T,_20_to_24_C,_25_to_49_T,_25_to_49_C, above_50_T,above_50_C,colTotal);

    List<ColumnParameters> preARTDisaggregation = Arrays.asList(
            _0_to_14_T,_0_to_14_C,  adult_15_and_above_T,adult_15_and_above_C , colTotal);

    List<ColumnParameters> cadreDisaggregation = Arrays.asList(
            troupe_dimension,  civilian_dimension , colTotal);

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
                ReportUtils.map(hivTestingAndCouselingDatasetDefinition(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(pmtctDataSetKDoD(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(careAndTreatmentDataSet(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Creates the dataset for section #2: Prevention of Mother-to-Child Transmission
     *
     * @return the dataset
     */
    protected DataSetDefinition pmtctDataSetKDoD() {
        CohortIndicatorDataSetDefinition indicatorDsd = new CohortIndicatorDataSetDefinition();
        indicatorDsd.setName("2");
        indicatorDsd.setDescription("Prevention of Mother-to-Child Transmission");
        indicatorDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        indicatorDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        indicatorDsd.addDimension("cadre", ReportUtils.map(commonDimensions.cadre()));

        String indParams = "startDate=${startDate},endDate=${endDate}";
//Updates
        EmrReportingUtils.addRow(indicatorDsd,"HV02-01", "First ANC Visit", ReportUtils.map(moh731GreenCardIndicators.firstANCVisitMchmsAntenatal(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-02", "Delivery from HIV+ Mothers(Labor and Delivery)", ReportUtils.map(moh731GreenCardIndicators.deliveryFromHIVPositiveMothers(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-03", "Known Positive at 1st ANC (Antenatal)", ReportUtils.map(moh731GreenCardIndicators.knownPositiveAtFirstANC(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-04", "Initial test at ANC (Antenatal)", ReportUtils.map(moh731GreenCardIndicators.initialHIVTestInMchmsAntenatal(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-05", "Initial Test at Labor and Delivery", ReportUtils.map(moh731GreenCardIndicators.testedForHivInMchmsDelivery(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-06", "Initial Test at PNC <=6 Weeks)", ReportUtils.map(moh731GreenCardIndicators.initialTestAtPNCUpto6Weeks(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-08", "PNC Retest within 6 weeks)", ReportUtils.map(moh731GreenCardIndicators.pncRetestUpto6Weeks(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-09", "PNC Testing between 7 weeks - 6 months)", ReportUtils.map(moh731GreenCardIndicators.pncTestBtwn6WeeksAnd6Months(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-10", "HIV positive Before First ANC)", ReportUtils.map(moh731GreenCardIndicators.knownHivPositiveAtFirstANC(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-11", "HIV positive results ANC", ReportUtils.map(moh731GreenCardIndicators.testedHivPositiveInMchmsAntenatal(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-12", "HIV positive results (Labor and Delivery)", ReportUtils.map(moh731GreenCardIndicators.testedHivPositiveInMchmsDelivery(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-13", "HIV positive results <=6 weeks)", ReportUtils.map(moh731GreenCardIndicators.testedHivPositiveInANCWithin6Weeks(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-14", "Total HIV positive Mothers)", ReportUtils.map(moh731GreenCardIndicators.totalHivPositiveInMchms(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-15", "PNC >6 weeks and <=6 months", ReportUtils.map(moh731GreenCardIndicators.pncHIVPositiveBetween7weeksAnd6Months(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-16", "On HAART at 1st ANC", ReportUtils.map(moh731GreenCardIndicators.onHAARTAtFirstANC(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-17", "Start HAART during ANC", ReportUtils.map(moh731GreenCardIndicators.startHAARTANC(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-18", "Start HAART During Labour and Delivery", ReportUtils.map(moh731GreenCardIndicators.startedHAARTLabourAndDelivery(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-19", "Started HAART upto 6 weeks", ReportUtils.map(moh731GreenCardIndicators.startedHAARTPNCUpto6Weeks(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-21", "Started HAART from 7 weeks to 6 months", ReportUtils.map(moh731GreenCardIndicators.onHAARTFrom7WeeksTo6Months(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-22", "On HAART Upto 12 months", ReportUtils.map(moh731GreenCardIndicators.onHAARTUpto12Months(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-23", "Net Cohort at 12 months", ReportUtils.map(moh731GreenCardIndicators.netCohortAt12Months(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-24", "Syphilis screened at 1st ANC", ReportUtils.map(moh731GreenCardIndicators.syphilisScreenedAt1stANC(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-25", "Syphilis screened Positive", ReportUtils.map(moh731GreenCardIndicators.syphilisScreenedPositive(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-26", "Syphilis Treated", ReportUtils.map(moh731GreenCardIndicators.syphilisTreated(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-27", "HIV+ on Modern FP at 6 weeks", ReportUtils.map(moh731GreenCardIndicators.HIVPositiveOnModernFPUpto6Weeks(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-28", "HIV+ PNC visits at 6 weeks", ReportUtils.map(moh731GreenCardIndicators.HIVPositivePNCVisitsAt6Weeks(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-29", "Known HIV+ 1st Contact", ReportUtils.map(moh731GreenCardIndicators.knownHIVPositive1stContact(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-30", "Initial Test at ANC Male", ReportUtils.map(moh731GreenCardIndicators.initialTestAtANCForMale(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-31", "Initial Test at Delivery Male", ReportUtils.map(moh731GreenCardIndicators.initialTestAtDeliveryForMale(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-32", "Initial Test at PNC Male", ReportUtils.map(moh731GreenCardIndicators.initialTestAtPNCForMale(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-34", "1st ANC KP Adolescent (10-19)", ReportUtils.map(moh731GreenCardIndicators.firstANCKPAdolescents(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-35", "HIV Positive Adolescents", ReportUtils.map(moh731GreenCardIndicators.adolescentsHIVPositive(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-36", "Adolescents started on HAART", ReportUtils.map(moh731GreenCardIndicators.adolescentsStartedOnHAART(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        //Infant HIV Exposure status at Penta 1
        EmrReportingUtils.addRow(indicatorDsd,"HV02-37", "Known Exposure at Penta 1", ReportUtils.map(moh731GreenCardIndicators.knownExposureAtPenta1(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-38", "Total due for Penta 1", ReportUtils.map(moh731GreenCardIndicators.totalDueForPenta1(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        //Infant ARV Prophylaxis
        EmrReportingUtils.addRow(indicatorDsd,"HV02-39", "Infant ARV Prophylaxis at ANC", ReportUtils.map(moh731GreenCardIndicators.infantArvProphylaxisANC(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-40", "Infant ARV Prophylaxis at Labour and Delivery", ReportUtils.map(moh731GreenCardIndicators.infantArvProphylaxisLabourAndDelivery(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-41", "Infant ARV Prophylaxis <8 weeks PNC", ReportUtils.map(moh731GreenCardIndicators.infantArvProphylaxisPNCLessThan8Weeks(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-43", "HEI CTS/DDS Start <2 months", ReportUtils.map(moh731GreenCardIndicators.heiDDSCTXStartLessThan2Months(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-44", "Initial PCR <8 weeks", ReportUtils.map(moh731GreenCardIndicators.initialPCRLessThan8Weeks(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-45", "Initial PCR >8 weeks to 12 months", ReportUtils.map(moh731GreenCardIndicators.initialPCROver8WeeksTo12Months(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-46", "Total Initial PCR Test <12 months", ReportUtils.map(moh731GreenCardIndicators.totalInitialPCRTestLessThan12Months(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-47", "Infected in 24 months", ReportUtils.map(moh731GreenCardIndicators.infectedIn24Months(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-48", "Uninfected in 24 months", ReportUtils.map(moh731GreenCardIndicators.uninfectedIn24Months(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-49", "Unknown Outcomes in 24 months", ReportUtils.map(moh731GreenCardIndicators.unknownOutcomesIn24Months(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-50", "Net Cohort HEI in 24 months", ReportUtils.map(moh731GreenCardIndicators.netCohortHeiIn24Months(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-51", "Mother-baby pairs in 24 months", ReportUtils.map(moh731GreenCardIndicators.motherBabyPairsIn24Months(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-53", "Pair net cohort in 24 months", ReportUtils.map(moh731GreenCardIndicators.pairNetCohortIn24Months(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-53", "Exclusive Breastfeeding at 6 months", ReportUtils.map(moh731GreenCardIndicators.exclusiveBFAt6Months(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-54", "Exclusive Replacement Feeding at 6 months", ReportUtils.map(moh731GreenCardIndicators.exclusiveRFAt6Months(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-55", "Mixed Feeding at 6 months", ReportUtils.map(moh731GreenCardIndicators.mixedFeedingAt6Months(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-56", "Breast Feeding at 12 months", ReportUtils.map(moh731GreenCardIndicators.breastFeedingAt12Months(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-57", "Not Breast Feeding at 12 months", ReportUtils.map(moh731GreenCardIndicators.notBreastFeedingAt12Months(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-58", "Breast Feeding at 18 months", ReportUtils.map(moh731GreenCardIndicators.breastFeedingAt18Months(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));
        EmrReportingUtils.addRow(indicatorDsd,"HV02-59", "Not Breast Feeding at 18 months", ReportUtils.map(moh731GreenCardIndicators.notBreastFeedingAt18Months(), indParams), cadreDisaggregation,Arrays.asList("1","2","3"));

        return indicatorDsd;
    }
    /**
     * Creates the dataset for section #3: Care and Treatment
     *
     * @return the dataset
     */
    protected DataSetDefinition careAndTreatmentDataSet() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("3");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.moh731GreenCardAgeGroups(), "onDate=${endDate}"));
        cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));
        cohortDsd.addDimension("cadre", ReportUtils.map(commonDimensions.cadre()));

        String indParams = "startDate=${startDate},endDate=${endDate}";

        // 3.1 (Enrolled in Care)
        EmrReportingUtils.addRow(cohortDsd, "HV03", "Enrolled in care", ReportUtils.map(moh731GreenCardIndicators.newHivEnrollment(), indParams), standardKDoDDisaggregation, Arrays.asList("001", "002", "003", "004", "005", "006", "007", "008", "009", "010", "011","012","013","014","015","016","017","018","019","020","021","022","023","024","025"));
        // 3.2 (Pre-ART)
        EmrReportingUtils.addRow(cohortDsd, "HV03", "Pre-Art", ReportUtils.map(moh731GreenCardIndicators.preArtCohort(), indParams), preARTDisaggregation, Arrays.asList("15", "16", "17","18","19"));

        // 3.3 (Starting ART)
        EmrReportingUtils.addRow(cohortDsd, "HV03", "Starting ART", ReportUtils.map(moh731GreenCardIndicators.startedOnArt(), indParams), standardKDoDDisaggregation, Arrays.asList("026", "027", "028", "029", "030", "031", "032", "033", "034", "035", "036","037","038","039","040","041","042","043","044","045","046","047","048","049","050"));

        // 3.4 (Currently on ART [All])
        EmrReportingUtils.addRow(cohortDsd, "HV03", "Current on ART", ReportUtils.map(moh731GreenCardIndicators.currentlyOnArt(), indParams), standardKDoDDisaggregation, Arrays.asList("051","052","053","054","055","056", "057", "058", "059", "060", "061", "062", "063", "064", "065", "066","067","068","069","070","071","072","073","074","075"));

        // 3.5 (Survival and Retention on ART at 12 months)
        EmrReportingUtils.addRow(cohortDsd,"HV03-046", "On therapy at 12 months", ReportUtils.map(moh731GreenCardIndicators.onTherapyAt12Months(), indParams), cadreDisaggregation,Arrays.asList("4","5","6"));
        EmrReportingUtils.addRow(cohortDsd,"HV03-047", "ART Net Cohort at 12 months", ReportUtils.map(moh731GreenCardIndicators.art12MonthNetCohort(), indParams), cadreDisaggregation,Arrays.asList("7","8","9"));

        EmrReportingUtils.addRow(cohortDsd,"HV03-048", " Viral load Suppressed <1000cp/mls last 12 mths ", ReportUtils.map(moh731GreenCardIndicators.patientsWithSuppressedVlLast12Months(), indParams), cadreDisaggregation,Arrays.asList("10","11","12"));

        EmrReportingUtils.addRow(cohortDsd,"HV03-049", " Patients with Viral load results last 12 mths ", ReportUtils.map(moh731GreenCardIndicators.patientsWithVLResultsLast12Months(), indParams), cadreDisaggregation,Arrays.asList("13","14","15"));

        // 3.6 on CTX/Dapsone
        EmrReportingUtils.addRow(cohortDsd, "HV03", "On CTX/Dapsone", ReportUtils.map(moh731GreenCardIndicators.onCotrimoxazoleProphylaxis(), indParams), ageAndCadreDisaggregation, Arrays.asList("073", "074", "075", "076", "077", "078", "079","080","081","082","083","084","085","086","087"));

        // 3.7 TB Screening and presumed TB
        EmrReportingUtils.addRow(cohortDsd, "HV03", "TB Screening", ReportUtils.map(moh731GreenCardIndicators.screenedForTb(), indParams), ageAndCadreDisaggregation, Arrays.asList("088", "089", "090", "091", "092", "093", "094","095","096","097","098","099","100","101","102"));
        EmrReportingUtils.addRow(cohortDsd,"HV03-058", "Presumed TB_Total", ReportUtils.map(moh731GreenCardIndicators.presumedForTb(), indParams), cadreDisaggregation,Arrays.asList("16","17","18"));

        // 3.8
        EmrReportingUtils.addRow(cohortDsd, "HV03", "Started on IPT", ReportUtils.map(moh731GreenCardIndicators.startedOnIPT(), indParams), ageAndCadreDisaggregation, Arrays.asList("103", "104", "105", "106", "107", "108", "109","110","111","112","113","114","115","116","117"));
        EmrReportingUtils.addRow(cohortDsd,"HV03-066", "Completed IPT 12 months", ReportUtils.map(moh731GreenCardIndicators.ipt12MonthsCohort(), indParams),cadreDisaggregation,Arrays.asList("19","20","21"));
        // 3.10
        EmrReportingUtils.addRow(cohortDsd,"HV03-076", "TB new cases", ReportUtils.map(moh731GreenCardIndicators.tbEnrollment(), indParams),cadreDisaggregation,Arrays.asList("22","23","24"));
        EmrReportingUtils.addRow(cohortDsd,"HV03-077", "TB new cases, Known Positive", ReportUtils.map(moh731GreenCardIndicators.tbNewKnownPositive(), indParams),cadreDisaggregation,Arrays.asList("25","26","27"));
        EmrReportingUtils.addRow(cohortDsd,"HV03-078", "TB new cases, tested for HIV", ReportUtils.map(moh731GreenCardIndicators.tbTestedForHIV(), indParams),cadreDisaggregation,Arrays.asList("28","29","30"));
        EmrReportingUtils.addRow(cohortDsd,"HV03-080", "TB new cases, HIV positive", ReportUtils.map(moh731GreenCardIndicators.tbNewTestedHIVPositive(), indParams),cadreDisaggregation,Arrays.asList("31","32","33"));
        EmrReportingUtils.addRow(cohortDsd,"HV03-082", "TB already on HAART", ReportUtils.map(moh731GreenCardIndicators.tbNewAlreadyOnHAART(), indParams),cadreDisaggregation,Arrays.asList("34","35","36"));
        EmrReportingUtils.addRow(cohortDsd,"HV03-083", "TB new cases start HAART", ReportUtils.map(moh731GreenCardIndicators.tbNewStartingHAART(), indParams),cadreDisaggregation,Arrays.asList("37","38","39"));
        EmrReportingUtils.addRow(cohortDsd,"HV03-084", "TB total on HAART", ReportUtils.map(moh731GreenCardIndicators.tbTotalOnHAART(), indParams),cadreDisaggregation,Arrays.asList("40","41","42"));
        // 3.12
        EmrReportingUtils.addRow(cohortDsd,"HV03-087", "Screen Cacx new F18+", ReportUtils.map(moh731GreenCardIndicators.screenedforCaCx(), indParams),cadreDisaggregation,Arrays.asList("43","44","45"));
        EmrReportingUtils.addRow(cohortDsd,"HV03-088", "Clinical Visits (F18+)", ReportUtils.map(moh731GreenCardIndicators.hivCareVisitsTotal(), indParams),cadreDisaggregation,Arrays.asList("46","47","48"));
        EmrReportingUtils.addRow(cohortDsd,"HV03-089", "Modern contraceptive methods", ReportUtils.map(moh731GreenCardIndicators.modernContraceptivesProvided(), indParams), cadreDisaggregation,Arrays.asList("49","50","51"));
        return cohortDsd;

    }
    /**
     * Creates the dataset for section #1: hiv testing and counseling
     *
     * @return the dataset
     */
    protected DataSetDefinition hivTestingAndCouselingDatasetDefinition() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("1");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.moh731GreenCardAgeGroups(), "onDate=${endDate}"));
        cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));
        cohortDsd.addDimension("cadre", ReportUtils.map(commonDimensions.cadre()));
        String indParams = "startDate=${startDate},endDate=${endDate}";

        // 3.1 HIV testing and counseling
        EmrReportingUtils.addRow(cohortDsd, "HV01", "Tested", ReportUtils.map(moh731GreenCardIndicators.htsNumberTested(), indParams), kdodDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10","11","12","13","14","15","16","17","18","19","20","21","22","23"));
        EmrReportingUtils.addRow(cohortDsd,"HV01-13", "Tested Facility", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedAtFacility(), indParams),cadreDisaggregation,Arrays.asList("52","53","54"));
        EmrReportingUtils.addRow(cohortDsd,"HV01-14", "Tested Community", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedAtCommunity(), indParams),cadreDisaggregation,Arrays.asList("55","56","57"));
        EmrReportingUtils.addRow(cohortDsd,"HV01-15", "Tested New", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedNew(), indParams),cadreDisaggregation,Arrays.asList("58","59","60"));
        EmrReportingUtils.addRow(cohortDsd,"HV01-16", "Tested Repeat", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedRepeat(), indParams),cadreDisaggregation,Arrays.asList("61","62","63"));
        EmrReportingUtils.addRow(cohortDsd,"HV01-17", "Tested Couples", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedAsCouple(), indParams),cadreDisaggregation,Arrays.asList("64","65","66"));
        EmrReportingUtils.addRow(cohortDsd,"HV01-18", "Tested Key Pop", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedKeyPopulation(), indParams),cadreDisaggregation,Arrays.asList("67","68","69"));

        EmrReportingUtils.addRow(cohortDsd, "HV01", "Positive", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedPositive(), indParams), kdodDisaggregation, Arrays.asList("24", "25", "26", "27", "28", "29", "30", "31", "32", "33","34","35","36","37","38","39","40","41","42","43","44","45","46"));
        EmrReportingUtils.addRow(cohortDsd,"HV01-27", "Negative Total", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedNegative(), indParams),cadreDisaggregation,Arrays.asList("70","71","72"));
        EmrReportingUtils.addRow(cohortDsd,"HV01-28", "Discordant", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedDiscordant(), indParams),cadreDisaggregation,Arrays.asList("73","74","75"));
        EmrReportingUtils.addRow(cohortDsd,"HV01-29", "Positive Key Pop", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedKeypopPositive(), indParams),cadreDisaggregation,Arrays.asList("76","77","78"));

        // number linked
        EmrReportingUtils.addRow(cohortDsd, "HV01", "Linked", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedPositiveAndLinked(), indParams), kdodDisaggregation, Arrays.asList("47", "48", "49", "50", "51", "52","53","54","55","56","57","58","59","60","61","62","63","64","65","66","67","68","69"));
        EmrReportingUtils.addRow(cohortDsd,"HV01-36", "Total tested positive (3 months ago)", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedPositiveInLastThreeMonths(), indParams),kdodCadreWithAge,Arrays.asList("79","80","81","82","83","84","85","86","87","88","89","90","91"));

        return cohortDsd;

    }

}
