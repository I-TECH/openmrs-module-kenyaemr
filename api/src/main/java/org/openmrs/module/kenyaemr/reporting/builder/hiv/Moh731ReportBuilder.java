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
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731.ETLMoh731IndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731.ETLPmtctCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731.ETLPmtctIndicatorLibrary;
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
 * Report builder for ETL MOH 731
 */
@Component
@Builds({"kenyaemr.etl.common.report.moh731"})
public class Moh731ReportBuilder extends AbstractReportBuilder {
    @Autowired
    private CommonDimensionLibrary commonDimensions;

    @Autowired
    private ETLMoh731GreenCardIndicatorLibrary moh731GreenCardIndicators;


    public static final String DATE_FORMAT = "yyyy-MM-dd";

    ColumnParameters colInfants = new ColumnParameters(null, "<1", "age=<1");

    ColumnParameters maleInfants = new ColumnParameters(null, "<1, Male", "gender=M|age=<1");
    ColumnParameters femaleInfants = new ColumnParameters(null, "<1, Female", "gender=F|age=<1");

    ColumnParameters children_0_to_9 = new ColumnParameters(null, "1-9", "age=0-9");
    ColumnParameters children_1_to_9 = new ColumnParameters(null, "1-9", "age=1-9");
    ColumnParameters adult_10_to_14 = new ColumnParameters(null, "10-14", "age=10-14");
    ColumnParameters adult_15_to_19 = new ColumnParameters(null, "15-19", "age=15-19");
    ColumnParameters adult_20_to_24 = new ColumnParameters(null, "20-24", "age=20-24");
    ColumnParameters adult_25_and_above = new ColumnParameters(null, "25+", "age=25+");

    // specific to pre-art
    ColumnParameters adult_0_to_14 = new ColumnParameters(null, "0-14", "age=0-14");
    ColumnParameters adult_15_and_above = new ColumnParameters(null, "15+", "age=15+");
    // end of pre-art

    ColumnParameters m_1_to_4 = new ColumnParameters(null, "1-4, Male", "gender=M|age=1-4");
    ColumnParameters f_1_to_4 = new ColumnParameters(null, "1-4, Female", "gender=F|age=1-4");

    ColumnParameters m_5_to_9 = new ColumnParameters(null, "5-9, Male", "gender=M|age=5-9");
    ColumnParameters f_5_to_9 = new ColumnParameters(null, "5-9, Female", "gender=F|age=5-9");

    ColumnParameters m_10_to_14 = new ColumnParameters(null, "10-14, Male", "gender=M|age=10-14");
    ColumnParameters f_10_to_14 = new ColumnParameters(null, "10-14, Female", "gender=F|age=10-14");

    ColumnParameters m_15_to_19 = new ColumnParameters(null, "15-19, Male", "gender=M|age=15-19");
    ColumnParameters f_15_to_19 = new ColumnParameters(null, "15-19, Female", "gender=F|age=15-19");

    ColumnParameters m_20_to_24 = new ColumnParameters(null, "20-24, Male", "gender=M|age=20-24");
    ColumnParameters f_20_to_24 = new ColumnParameters(null, "20-24, Female", "gender=F|age=20-24");

    ColumnParameters m_25_and_above = new ColumnParameters(null, "25+, Male", "gender=M|age=25+");
    ColumnParameters f_25_and_above = new ColumnParameters(null, "25+, Female", "gender=F|age=25+");

    ColumnParameters m_1_to_9 = new ColumnParameters(null, "1-9, Male", "gender=M|age=1-9");

    ColumnParameters colTotal = new ColumnParameters(null, "Total", "");

    List<ColumnParameters> standardDisaggregationAgeAndSex = Arrays.asList(
            colInfants, children_1_to_9,  m_10_to_14, f_10_to_14, m_15_to_19, f_15_to_19,
            m_20_to_24, f_20_to_24, m_25_and_above, f_25_and_above , colTotal);

    List<ColumnParameters> standardAgeOnlyDisaggregation = Arrays.asList(
            children_0_to_9,  adult_10_to_14, adult_15_to_19,
            adult_20_to_24, adult_25_and_above , colTotal);

    List<ColumnParameters> standardAgeOnlyDisaggregationWithInfants = Arrays.asList(
            colInfants, children_1_to_9,  adult_10_to_14, adult_15_to_19,
            adult_20_to_24, adult_25_and_above , colTotal);

    List<ColumnParameters> standardDisaggregationWithoutInfants = Arrays.asList(
            children_1_to_9,  m_10_to_14, f_10_to_14,m_15_to_19, f_15_to_19,
            m_20_to_24,f_20_to_24,m_25_and_above, f_25_and_above , colTotal);

    List<ColumnParameters> disaggregationWithInfants = Arrays.asList(
            children_0_to_9,  m_10_to_14, f_10_to_14,m_15_to_19, f_15_to_19,
            m_20_to_24,f_20_to_24,m_25_and_above, f_25_and_above , colTotal);

    List<ColumnParameters> allAgeDisaggregation = Arrays.asList(
            maleInfants, femaleInfants, m_1_to_4,  f_1_to_4, m_5_to_9, f_5_to_9, m_10_to_14, f_10_to_14,m_15_to_19, f_15_to_19,
            m_20_to_24, f_20_to_24, m_25_and_above, f_25_and_above , colTotal);

    List<ColumnParameters> preARTDisaggregation = Arrays.asList(
            adult_0_to_14,  adult_15_and_above , colTotal);

    List<ColumnParameters> vmmcDisaggregation = Arrays.asList(
            maleInfants, m_1_to_9, m_10_to_14, m_15_to_19, m_20_to_24, m_25_and_above, colTotal);

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
                ReportUtils.map(pmtctDataSet(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(careAndTreatmentDataSet(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(voluntaryMaleCircumcisionDatasetDefinition(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Creates the dataset for section #2: Prevention of Mother-to-Child Transmission
     *
     * @return the dataset
     */
    protected DataSetDefinition pmtctDataSet() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("2");
        dsd.setDescription("Prevention of Mother-to-Child Transmission");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        String indParams = "startDate=${startDate},endDate=${endDate}";
//Updates
        dsd.addColumn("HV02-01", "First ANC Visit", ReportUtils.map(moh731GreenCardIndicators.firstANCVisitMchmsAntenatal(), indParams), "");
        dsd.addColumn("HV02-02", "Delivery from HIV+ Mothers(Labor and Delivery)", ReportUtils.map(moh731GreenCardIndicators.deliveryFromHIVPositiveMothers(), indParams), "");
        dsd.addColumn("HV02-03", "Known Positive at 1st ANC (Antenatal)", ReportUtils.map(moh731GreenCardIndicators.knownPositiveAtFirstANC(), indParams), "");
        dsd.addColumn("HV02-04", "Initial test at ANC (Antenatal)", ReportUtils.map(moh731GreenCardIndicators.initialHIVTestInMchmsAntenatal(), indParams), "");
        dsd.addColumn("HV02-05", "Initial Test at Labor and Delivery", ReportUtils.map(moh731GreenCardIndicators.testedForHivInMchmsDelivery(), indParams), "");
        dsd.addColumn("HV02-06", "Initial Test at PNC <=6 Weeks)", ReportUtils.map(moh731GreenCardIndicators.initialTestAtPNCUpto6Weeks(), indParams), "");
        //dsd.addColumn("HV02-07", "Known HIV Status Total)", ReportUtils.map(moh731GreenCardIndicators.testedForHivInMchms(), indParams), "");
        dsd.addColumn("HV02-08", "PNC Retest within 6 weeks)", ReportUtils.map(moh731GreenCardIndicators.pncRetestUpto6Weeks(), indParams), "");
        dsd.addColumn("HV02-09", "PNC Testing between 7 weeks - 6 months)", ReportUtils.map(moh731GreenCardIndicators.pncTestBtwn6WeeksAnd6Months(), indParams), "");
        dsd.addColumn("HV02-10", "HIV positive Before First ANC)", ReportUtils.map(moh731GreenCardIndicators.knownHivPositiveAtFirstANC(), indParams), "");
        dsd.addColumn("HV02-11", "HIV positive results ANC", ReportUtils.map(moh731GreenCardIndicators.testedHivPositiveInMchmsAntenatal(), indParams), "");
        dsd.addColumn("HV02-12", "HIV positive results (Labor and Delivery)", ReportUtils.map(moh731GreenCardIndicators.testedHivPositiveInMchmsDelivery(), indParams), "");
        dsd.addColumn("HV02-13", "HIV positive results <=6 weeks)", ReportUtils.map(moh731GreenCardIndicators.testedHivPositiveInANCWithin6Weeks(), indParams), "");
        dsd.addColumn("HV02-14", "Total HIV positive Mothers)", ReportUtils.map(moh731GreenCardIndicators.totalHivPositiveInMchms(), indParams), "");
        dsd.addColumn("HV02-15", "PNC >6 weeks and <=6 months", ReportUtils.map(moh731GreenCardIndicators.pncHIVPositiveBetween7weeksAnd6Months(), indParams), "");
        dsd.addColumn("HV02-16", "On HAART at 1st ANC", ReportUtils.map(moh731GreenCardIndicators.onHAARTAtFirstANC(), indParams), "");
        dsd.addColumn("HV02-17", "Start HAART during ANC", ReportUtils.map(moh731GreenCardIndicators.startHAARTANC(), indParams), "");
        dsd.addColumn("HV02-18", "Start HAART During Labour and Delivery", ReportUtils.map(moh731GreenCardIndicators.startedHAARTLabourAndDelivery(), indParams), "");
        dsd.addColumn("HV02-19", "Started HAART upto 6 weeks", ReportUtils.map(moh731GreenCardIndicators.startedHAARTPNCUpto6Weeks(), indParams), "");
        //dsd.addColumn("HV02-20", "Total Maternal HAART", ReportUtils.map(moh731GreenCardIndicators.totalMaternalHAART(), indParams), "");
        dsd.addColumn("HV02-21", "Started HAART from 7 weeks to 6 months", ReportUtils.map(moh731GreenCardIndicators.onHAARTFrom7WeeksTo6Months(), indParams), "");
        dsd.addColumn("HV02-22", "On HAART Upto 12 months", ReportUtils.map(moh731GreenCardIndicators.onHAARTUpto12Months(), indParams), "");
        dsd.addColumn("HV02-23", "Net Cohort at 12 months", ReportUtils.map(moh731GreenCardIndicators.netCohortAt12Months(), indParams), "");
        dsd.addColumn("HV02-24", "Syphilis screened at 1st ANC", ReportUtils.map(moh731GreenCardIndicators.syphilisScreenedAt1stANC(), indParams), "");
        dsd.addColumn("HV02-25", "Syphilis screened Positive", ReportUtils.map(moh731GreenCardIndicators.syphilisScreenedPositive(), indParams), "");
        dsd.addColumn("HV02-26", "Syphilis Treated", ReportUtils.map(moh731GreenCardIndicators.syphilisTreated(), indParams), "");
        dsd.addColumn("HV02-27", "HIV+ on Modern FP at 6 weeks", ReportUtils.map(moh731GreenCardIndicators.HIVPositiveOnModernFPUpto6Weeks(), indParams), "");
        dsd.addColumn("HV02-28", "HIV+ PNC visits at 6 weeks", ReportUtils.map(moh731GreenCardIndicators.HIVPositivePNCVisitsAt6Weeks(), indParams), "");
        dsd.addColumn("HV02-29", "Known HIV+ 1st Contact", ReportUtils.map(moh731GreenCardIndicators.knownHIVPositive1stContact(), indParams), "");
        dsd.addColumn("HV02-30", "Initial Test at ANC Male", ReportUtils.map(moh731GreenCardIndicators.initialTestAtANCForMale(), indParams), "");
        dsd.addColumn("HV02-31", "Initial Test at Delivery Male", ReportUtils.map(moh731GreenCardIndicators.initialTestAtDeliveryForMale(), indParams), "");
        dsd.addColumn("HV02-32", "Initial Test at PNC Male", ReportUtils.map(moh731GreenCardIndicators.initialTestAtPNCForMale(), indParams), "");
        //dsd.addColumn("HV02-32", "Total Known status Male", ReportUtils.map(moh731GreenCardIndicators.totalKnownHIVStatusMale(), indParams), "");
        /*Adolescents (10-19 years) Testing results*/
        dsd.addColumn("HV02-34", "1st ANC KP Adolescent (10-19)", ReportUtils.map(moh731GreenCardIndicators.firstANCKPAdolescents(), indParams), "");
        dsd.addColumn("HV02-35", "HIV Positive Adolescents", ReportUtils.map(moh731GreenCardIndicators.adolescentsHIVPositive(), indParams), "");
        dsd.addColumn("HV02-36", "Adolescents started on HAART", ReportUtils.map(moh731GreenCardIndicators.adolescentsStartedOnHAART(), indParams), "");
        /*Infant HIV Exposure status at Penta 1*/
        dsd.addColumn("HV02-37", "Known Exposure at Penta 1", ReportUtils.map(moh731GreenCardIndicators.knownExposureAtPenta1(), indParams), "");
        dsd.addColumn("HV02-38", "Total due for Penta 1", ReportUtils.map(moh731GreenCardIndicators.totalDueForPenta1(), indParams), "");
        /*Infant ARV Prophylaxis*/
        dsd.addColumn("HV02-39", "Infant ARV Prophylaxis at ANC", ReportUtils.map(moh731GreenCardIndicators.infantArvProphylaxisANC(), indParams), "");
        dsd.addColumn("HV02-40", "Infant ARV Prophylaxis at Labour and Delivery", ReportUtils.map(moh731GreenCardIndicators.infantArvProphylaxisLabourAndDelivery(), indParams), "");
        dsd.addColumn("HV02-41", "Infant ARV Prophylaxis <8 weeks PNC", ReportUtils.map(moh731GreenCardIndicators.infantArvProphylaxisPNCLessThan8Weeks(), indParams), "");
        //dsd.addColumn("HV02-41", "Total ARV Prophylaxis", ReportUtils.map(moh731GreenCardIndicators.totalARVProphylaxis(), indParams), "");
        dsd.addColumn("HV02-43", "HEI CTS/DDS Start <2 months", ReportUtils.map(moh731GreenCardIndicators.heiDDSCTXStartLessThan2Months(), indParams), "");
        dsd.addColumn("HV02-44", "Initial PCR <8 weeks", ReportUtils.map(moh731GreenCardIndicators.initialPCRLessThan8Weeks(), indParams), "");
        dsd.addColumn("HV02-45", "Initial PCR >8 weeks to 12 months", ReportUtils.map(moh731GreenCardIndicators.initialPCROver8WeeksTo12Months(), indParams), "");
        dsd.addColumn("HV02-46", "Total Initial PCR Test <12 months", ReportUtils.map(moh731GreenCardIndicators.totalInitialPCRTestLessThan12Months(), indParams), "");
        dsd.addColumn("HV02-47", "Infected in 24 months", ReportUtils.map(moh731GreenCardIndicators.infectedIn24Months(), indParams), "");
        dsd.addColumn("HV02-48", "Uninfected in 24 months", ReportUtils.map(moh731GreenCardIndicators.uninfectedIn24Months(), indParams), "");
        dsd.addColumn("HV02-49", "Unknown Outcomes in 24 months", ReportUtils.map(moh731GreenCardIndicators.unknownOutcomesIn24Months(), indParams), "");
        dsd.addColumn("HV02-50", "Net Cohort HEI in 24 months", ReportUtils.map(moh731GreenCardIndicators.netCohortHeiIn24Months(), indParams), "");
        dsd.addColumn("HV02-51", "Mother-baby pairs in 24 months", ReportUtils.map(moh731GreenCardIndicators.motherBabyPairsIn24Months(), indParams), "");
        dsd.addColumn("HV02-53", "Pair net cohort in 24 months", ReportUtils.map(moh731GreenCardIndicators.pairNetCohortIn24Months(), indParams), "");
        dsd.addColumn("HV02-53", "Exclusive Breastfeeding at 6 months", ReportUtils.map(moh731GreenCardIndicators.exclusiveBFAt6Months(), indParams), "");
        dsd.addColumn("HV02-54", "Exclusive Replacement Feeding at 6 months", ReportUtils.map(moh731GreenCardIndicators.exclusiveRFAt6Months(), indParams), "");
        dsd.addColumn("HV02-55", "Mixed Feeding at 6 months", ReportUtils.map(moh731GreenCardIndicators.mixedFeedingAt6Months(), indParams), "");
        dsd.addColumn("HV02-56", "Breast Feeding at 12 months", ReportUtils.map(moh731GreenCardIndicators.breastFeedingAt12Months(), indParams), "");
        dsd.addColumn("HV02-57", "Not Breast Feeding at 12 months", ReportUtils.map(moh731GreenCardIndicators.notBreastFeedingAt12Months(), indParams), "");
        dsd.addColumn("HV02-58", "Breast Feeding at 18 months", ReportUtils.map(moh731GreenCardIndicators.breastFeedingAt18Months(), indParams), "");
        dsd.addColumn("HV02-59", "Not Breast Feeding at 18 months", ReportUtils.map(moh731GreenCardIndicators.notBreastFeedingAt18Months(), indParams), "");

        //End updates

        return dsd;
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

        String indParams = "startDate=${startDate},endDate=${endDate}";

       /* cohortDsd.addColumn("HV03-01", "HIV Exposed Infants (within 2 months)", ReportUtils.map(moh731GreenCardIndicators.hivExposedInfantsWithin2Months(), indParams), "");
        cohortDsd.addColumn("HV03-02", "HIV Exposed Infants (Eligible for CTX at 2 months)", ReportUtils.map(moh731GreenCardIndicators.hivExposedInfantsWithin2MonthsAndEligibleForCTX(), indParams), "");*/


        // 3.1 (Enrolled in Care)
        EmrReportingUtils.addRow(cohortDsd, "HV03", "Enrolled in care", ReportUtils.map(moh731GreenCardIndicators.newHivEnrollment(), indParams), standardDisaggregationAgeAndSex, Arrays.asList("001", "002", "003", "004", "005", "006", "007", "008", "009", "010", "011"));
        // 3.2 (Pre-ART)
        EmrReportingUtils.addRow(cohortDsd, "HV03", "Pre-Art", ReportUtils.map(moh731GreenCardIndicators.preArtCohort(), indParams), preARTDisaggregation, Arrays.asList("013", "014", "015"));

        // 3.3 (Starting ART)
        EmrReportingUtils.addRow(cohortDsd, "HV03", "Starting ART", ReportUtils.map(moh731GreenCardIndicators.startedOnArt(), indParams), standardDisaggregationAgeAndSex, Arrays.asList("016", "017", "018", "019", "020", "021", "022", "023", "024", "025", "026"));

        // 3.4 (Currently on ART [All])
        EmrReportingUtils.addRow(cohortDsd, "HV03", "Current on ART", ReportUtils.map(moh731GreenCardIndicators.currentlyOnArt(), indParams), standardDisaggregationAgeAndSex, Arrays.asList("028", "029", "030", "031", "032", "033", "034", "035", "036", "037", "038"));

        // 3.5 (Survival and Retention on ART at 12 months)
        cohortDsd.addColumn("HV03-040", "On therapy at 12 months (Total) ", ReportUtils.map(moh731GreenCardIndicators.onTherapyAt12Months(), indParams), "");
        cohortDsd.addColumn("HV03-041", "ART Net Cohort at 12 months", ReportUtils.map(moh731GreenCardIndicators.art12MonthNetCohort(), indParams), "");


        cohortDsd.addColumn("HV03-042", " Viral load Suppressed <1000cp/mls last 12 mths ", ReportUtils.map(moh731GreenCardIndicators.patientsWithSuppressedVlLast12Months(), indParams), "");
        cohortDsd.addColumn("HV03-043", " Patients with Viral load results last 12 mths ", ReportUtils.map(moh731GreenCardIndicators.patientsWithVLResultsLast12Months(), indParams), "");


        // 3.6 on CTX/Dapsone
        EmrReportingUtils.addRow(cohortDsd, "HV03", "On CTX/Dapsone", ReportUtils.map(moh731GreenCardIndicators.onCotrimoxazoleProphylaxis(), indParams), standardAgeOnlyDisaggregationWithInfants, Arrays.asList("044", "045", "046", "047", "048", "049", "050"));

        // 3.7 TB Screening and presumed TB
        EmrReportingUtils.addRow(cohortDsd, "HV03", "TB Screening", ReportUtils.map(moh731GreenCardIndicators.screenedForTb(), indParams), standardAgeOnlyDisaggregationWithInfants, Arrays.asList("051", "052", "053", "054", "055", "056", "057"));
        cohortDsd.addColumn("HV03-058", "Presumed TB_Total", ReportUtils.map(moh731GreenCardIndicators.presumedForTb(), indParams),"");

        // 3.8
        EmrReportingUtils.addRow(cohortDsd, "HV03", "Started on TPT", ReportUtils.map(moh731GreenCardIndicators.startedOnIPT(), indParams), standardAgeOnlyDisaggregationWithInfants, Arrays.asList("059", "060", "061", "062", "063", "064", "065"));
        cohortDsd.addColumn("HV03-066", "Completed TPT 12 months", ReportUtils.map(moh731GreenCardIndicators.ipt12MonthsCohort(), indParams),"");

        //3.9 Nutrition and HIV
        EmrReportingUtils.addRow(cohortDsd, "HV03", "Nutrition assessment", ReportUtils.map(moh731GreenCardIndicators.assessedForNutritionInHIV(), indParams), preARTDisaggregation, Arrays.asList("067", "068","069"));
        EmrReportingUtils.addRow(cohortDsd, "HV03", "Malnourished", ReportUtils.map(moh731GreenCardIndicators.malnourishedInHIV(), indParams), preARTDisaggregation, Arrays.asList("070","071","072"));
        // 3.10
        cohortDsd.addColumn("HV03-076", "TB new cases", ReportUtils.map(moh731GreenCardIndicators.tbEnrollment(), indParams),"");
        cohortDsd.addColumn("HV03-077", "TB new cases, Known Positive", ReportUtils.map(moh731GreenCardIndicators.tbNewKnownPositive(), indParams),"");
        cohortDsd.addColumn("HV03-078", "TB new cases, tested for HIV", ReportUtils.map(moh731GreenCardIndicators.tbTestedForHIV(), indParams),"");
        cohortDsd.addColumn("HV03-080", "TB new cases, HIV positive", ReportUtils.map(moh731GreenCardIndicators.tbNewTestedHIVPositive(), indParams),"");
        cohortDsd.addColumn("HV03-082", "TB already on HAART", ReportUtils.map(moh731GreenCardIndicators.tbNewAlreadyOnHAART(), indParams),"");
        cohortDsd.addColumn("HV03-083", "TB new cases start HAART", ReportUtils.map(moh731GreenCardIndicators.tbNewStartingHAART(), indParams),"");
        cohortDsd.addColumn("HV03-084", "TB total on HAART", ReportUtils.map(moh731GreenCardIndicators.tbTotalOnHAART(), indParams),"");
        // 3.12
        cohortDsd.addColumn("HV03-087", "Screen Cacx new F18+", ReportUtils.map(moh731GreenCardIndicators.screenedforCaCx(), indParams),"");
        cohortDsd.addColumn("HV03-088", "Clinical Visits (F18+)", ReportUtils.map(moh731GreenCardIndicators.hivCareVisitsTotal(), indParams),"");
        cohortDsd.addColumn("HV03-089", "Modern contraceptive methods", ReportUtils.map(moh731GreenCardIndicators.modernContraceptivesProvided(), indParams), "");
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
        String indParams = "startDate=${startDate},endDate=${endDate}";

        // 3.1 HIV testing and counseling
        EmrReportingUtils.addRow(cohortDsd, "HV01", "Tested", ReportUtils.map(moh731GreenCardIndicators.htsNumberTested(), indParams), disaggregationWithInfants, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10"));
        cohortDsd.addColumn("HV01-11", "Tested Facility", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedAtFacility(), indParams),"");
        cohortDsd.addColumn("HV01-12", "Tested Community", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedAtCommunity(), indParams),"");
        cohortDsd.addColumn("HV01-13", "Tested New", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedNew(), indParams),"");
        cohortDsd.addColumn("HV01-14", "Tested Repeat", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedRepeat(), indParams),"");
        cohortDsd.addColumn("HV01-15", "Tested Couples", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedAsCouple(), indParams),"");
        cohortDsd.addColumn("HV01-16", "Tested Key Pop", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedKeyPopulation(), indParams),"");

        EmrReportingUtils.addRow(cohortDsd, "HV01", "Positive", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedPositive(), indParams), disaggregationWithInfants, Arrays.asList("17", "18", "19", "20", "21", "22", "23", "24", "25", "26"));
        cohortDsd.addColumn("HV01-27", "Negative Total", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedNegative(), indParams),"");
        cohortDsd.addColumn("HV01-28", "Discordant", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedDiscordant(), indParams),"");
        cohortDsd.addColumn("HV01-29", "Positive Key Pop", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedKeypopPositive(), indParams),"");

        // number linked
        EmrReportingUtils.addRow(cohortDsd, "HV01", "Linked", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedPositiveAndLinked(), indParams), standardAgeOnlyDisaggregation, Arrays.asList("30", "31", "32", "33", "34", "35"));
        cohortDsd.addColumn("HV01-36", "Total tested positive (3 months ago)", ReportUtils.map(moh731GreenCardIndicators.htsNumberTestedPositiveInLastThreeMonths(), indParams),"");

        return cohortDsd;

    }

    /**
     * Creates the dataset for section #4: Voluntary Male Circumcision
     *
     * @return the dataset
     */
    protected DataSetDefinition voluntaryMaleCircumcisionDatasetDefinition() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("4");
        cohortDsd.setDescription("Voluntary Male Circumcision");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.moh731GreenCardAgeGroups(), "onDate=${endDate}"));
        cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));
        String indParams = "startDate=${startDate},endDate=${endDate}";

        // 4.1 Voluntary Male Circumcision
        EmrReportingUtils.addRow(cohortDsd, "HV04", "Tested", ReportUtils.map(moh731GreenCardIndicators.numberCircumcised(), indParams), vmmcDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07"));
        cohortDsd.addColumn("HV04-08", "Circumcised HIV+", ReportUtils.map(moh731GreenCardIndicators.numberCircumcisedHivPositive(), indParams),"");
        cohortDsd.addColumn("HV04-09", "Circumcised HIV-", ReportUtils.map(moh731GreenCardIndicators.numberCircumcisedHivNegative(), indParams),"");
        cohortDsd.addColumn("HV04-10", "Circumcised HIV NK", ReportUtils.map(moh731GreenCardIndicators.numberCircumcisedHivUnknown(), indParams),"");
        cohortDsd.addColumn("HV04-11", "Circumcised Surgical", ReportUtils.map(moh731GreenCardIndicators.numberCircumcisedSurgically(), indParams),"");
        cohortDsd.addColumn("HV04-12", "Circumcised Device", ReportUtils.map(moh731GreenCardIndicators.numberCircumcisedUsingDevice(), indParams),"");
        return cohortDsd;

    }
}
