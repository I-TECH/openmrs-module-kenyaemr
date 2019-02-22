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
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.RevisedDatim.DatimIndicatorLibrary;
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
 * Report builder for Datim report
 */
@Component
@Builds({"kenyaemr.etl.common.report.datim"})
public class RevisedDatimReportBuilder extends AbstractReportBuilder {
    @Autowired
    private CommonDimensionLibrary commonDimensions;

    @Autowired
    private DatimIndicatorLibrary datimIndicators;


    public static final String DATE_FORMAT = "yyyy-MM-dd";

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
        return Arrays.asList(ReportUtils.map(careAndTreatmentDataSet(), "startDate=${startDate},endDate=${endDate}")
        );
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
        cohortDsd.addDimension("age", ReportUtils.map(commonDimensions.datimFineAgeGroups(), "onDate=${endDate}"));
        cohortDsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

        ColumnParameters colInfants = new ColumnParameters(null, "<1", "age=<1");

        ColumnParameters children_1_to_9 = new ColumnParameters(null, "1-9", "age=1-9");

        ColumnParameters colTotal = new ColumnParameters(null, "Total", "");

        /*DatimQ4 Column parameters*/

        ColumnParameters colInfant = new ColumnParameters(null, "<1", "age=<1");
        ColumnParameters all0_to_2m = new ColumnParameters(null, "0-2", "age=0-2");
        ColumnParameters all2_to_12m = new ColumnParameters(null, "2-12", "age=2-12");
        ColumnParameters f1_to_9 = new ColumnParameters(null, "1-9, Female", "gender=F|age=1-9");
        ColumnParameters f10_14 = new ColumnParameters(null, "10-14, Female", "gender=F|age=10-14");
        ColumnParameters m10_14 = new ColumnParameters(null, "10-14, Male", "gender=M|age=10-14");
        ColumnParameters f15_19 = new ColumnParameters(null, "15-19, Female", "gender=F|age=15-19");
        ColumnParameters m15_19 = new ColumnParameters(null, "15-19, Male", "gender=M|age=15-19");
        ColumnParameters f20_24 = new ColumnParameters(null, "20-24, Female", "gender=F|age=20-24");
        ColumnParameters m20_24 = new ColumnParameters(null, "20-24, Male", "gender=M|age=20-24");
        ColumnParameters f25_49 = new ColumnParameters(null, "25-49, Female", "gender=F|age=20-49");
        ColumnParameters m25_49 = new ColumnParameters(null, "25-49, Male", "gender=M|age=20-49");
        ColumnParameters f_Over_50 = new ColumnParameters(null, "50+, Female", "gender=F|age=50+");
        ColumnParameters m_Over_50 = new ColumnParameters(null, "50+, Male", "gender=M|age=50+");
        ColumnParameters colTot = new ColumnParameters(null, "Total", "");

        /*New age disaggregations*/
        ColumnParameters fInfant = new ColumnParameters(null, "<1, Female", "gender=F|age=<1");
        ColumnParameters mInfant = new ColumnParameters(null, "<1, Male", "gender=M|age=<1");
        ColumnParameters f1_to4 = new ColumnParameters(null, "1-4, Female", "gender=F|age=1-4");
        ColumnParameters m1_to4 = new ColumnParameters(null, "1-4, Male", "gender=M|age=1-4");
        ColumnParameters f5_to9 = new ColumnParameters(null, "5-9, Female", "gender=F|age=5-9");
        ColumnParameters m5_to9 = new ColumnParameters(null, "5-9, Male", "gender=M|age=5-9");
        ColumnParameters funder10 = new ColumnParameters(null, "<10, Female", "gender=F|age=<10");
        ColumnParameters f10_to14 = new ColumnParameters(null, "10-14, Female", "gender=F|age=10-14");
        ColumnParameters m10_to14 = new ColumnParameters(null, "10-14, Male", "gender=M|age=10-14");
        ColumnParameters f15_to19 = new ColumnParameters(null, "15-19, Female", "gender=F|age=15-19");
        ColumnParameters m15_to19 = new ColumnParameters(null, "15-19, Male", "gender=M|age=15-19");
        ColumnParameters f20_to24 = new ColumnParameters(null, "20-24, Female", "gender=F|age=20-24");
        ColumnParameters m20_to24 = new ColumnParameters(null, "20-24, Male", "gender=M|age=20-24");
        ColumnParameters f25_to29 = new ColumnParameters(null, "25-29, Female", "gender=F|age=25-29");
        ColumnParameters m25_to29 = new ColumnParameters(null, "25-29, Male", "gender=M|age=25-29");
        ColumnParameters f30_to34 = new ColumnParameters(null, "30-34, Female", "gender=F|age=30-34");
        ColumnParameters m30_to34 = new ColumnParameters(null, "30-34, Male", "gender=M|age=30-34");
        ColumnParameters f35_to39 = new ColumnParameters(null, "35-39, Female", "gender=F|age=35-39");
        ColumnParameters m35_to39 = new ColumnParameters(null, "35-39, Male", "gender=M|age=35-39");
        ColumnParameters f40_to44 = new ColumnParameters(null, "40-44, Female", "gender=F|age=40-44");
        ColumnParameters m40_to44 = new ColumnParameters(null, "40-44, Male", "gender=M|age=40-44");
        ColumnParameters f45_to49 = new ColumnParameters(null, "45-49, Female", "gender=F|age=45-49");
        ColumnParameters m45_to49 = new ColumnParameters(null, "45-49, Male", "gender=M|age=45-49");
        ColumnParameters fAbove50 = new ColumnParameters(null, "50+, Female", "gender=F|age=50+");
        ColumnParameters mAbove50 = new ColumnParameters(null, "50+, Male", "gender=M|age=50+");
        ColumnParameters all0_to4 = new ColumnParameters(null, "0-4", "age=0-4");
        ColumnParameters all5_to9 = new ColumnParameters(null, "5-9", "age=5-9");
        ColumnParameters all1_to9 = new ColumnParameters(null, "1-9", "age=1-9");
        ColumnParameters all0_to14 = new ColumnParameters(null, "0-14", "age=0-14");
        ColumnParameters all10_to14 = new ColumnParameters(null, "10-14", "age=10-14");
        ColumnParameters all15_to19 = new ColumnParameters(null, "15-19", "age=15-19");
        ColumnParameters all20_to24 = new ColumnParameters(null, "20-24", "age=20-24");
        ColumnParameters allOver25 = new ColumnParameters(null, "50+", "age=50+");
        ColumnParameters all25_to29 = new ColumnParameters(null, "25-29", "age=25-29");
        ColumnParameters all30_to34 = new ColumnParameters(null, "30-34", "age=30-34");
        ColumnParameters all35_to39 = new ColumnParameters(null, "35-39", "age=35-39");
        ColumnParameters all40_to44 = new ColumnParameters(null, "40-44", "age=40-44");
        ColumnParameters all45_to49 = new ColumnParameters(null, "45-49", "age=45-49");
        ColumnParameters allAbove50 = new ColumnParameters(null, "50+", "age=50+");


        List<ColumnParameters> datimNewAgeDisaggregation =
                Arrays.asList(fInfant, mInfant, f1_to4, m1_to4, f5_to9, m5_to9, f10_to14, m10_to14, f15_to19, m15_to19, f20_to24, m20_to24,
                        f25_to29, m25_to29, f30_to34, m30_to34, f35_to39, m35_to39, f40_to44, m40_to44, f45_to49, m45_to49, fAbove50, mAbove50,colTotal );

        List<ColumnParameters> datimHTSSelfTestAgeDisaggregation =
                Arrays.asList(f10_to14, m10_to14, f15_to19, m15_to19, f20_to24, m20_to24, f25_to29, m25_to29, f30_to34, m30_to34, f35_to39,
                        m35_to39, f40_to44, m40_to44, f45_to49, m45_to49, fAbove50, mAbove50 );

        List<ColumnParameters> datimPMTCTANCAgeDisaggregation =
                Arrays.asList(funder10, f10_to14, f15_to19, f20_to24, f25_to29, f30_to34, f35_to39, f40_to44, f45_to49, fAbove50,colTotal);

        List<ColumnParameters> datimOtherReportsAgeDisaggregation = Arrays.asList(all1_to9, all10_to14, all15_to19, all20_to24, allOver25);

        List<ColumnParameters> datimDCMAgeDisaggregation =
                Arrays.asList(all0_to4, all5_to9, all10_to14, all15_to19, all20_to24, all25_to29, all30_to34, all35_to39, all40_to44, all45_to49, allAbove50);

        List<ColumnParameters> datimPAMAAgeDisaggregation = Arrays.asList(all0_to14);

        List<ColumnParameters> datimAgeDisaggregationMonths = Arrays.asList(all0_to_2m, all2_to_12m,colTotal);

        String indParams = "startDate=${startDate},endDate=${endDate}";
        String endDateParams = "endDate=${endDate}";
        // 3.1 (On CTX Prophylaxis)

        /*EmrReportingUtils.addRow(cohortDsd, "TX_New", "Started on Art", ReportUtils.map(datimIndicators.startedOnArt(), indParams), allAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19"));

        cohortDsd.addColumn("TX_New_TB_co_infected", "Started on ART and TB co-infected", ReportUtils.map(datimIndicators.startedOnARTAndTBCoinfected(), indParams), "");
        cohortDsd.addColumn("TX_New_pregnant", "Started on ART and pregnant ", ReportUtils.map(datimIndicators.startedOnARTAndPregnant(), indParams), "");*/

        //Known positive before ANC
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_STAT_RECENT_POSITIVE", "Clients with Positive HIV status before ANC ", ReportUtils.map(datimIndicators.clientsWithPositiveHivStatusBeforeAnc1(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07","08","09","10","11"));
        //4 HIV Negative at ANC
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_STAT_ANC_Negative", "Clients tested HIV Negative at ANC", ReportUtils.map(datimIndicators.patientsTestNegativeAtANC(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07","08","09","10","11"));

        //4 HIV Positive at ANC
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_STAT_ANC_Positive", "Clients tested HIV Positive at ANC", ReportUtils.map(datimIndicators.patientsTestPositiveAtANC(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07","08","09","10","11"));

        //Known Negative before ANC
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_STAT_RECENT_NEGATIVE", "Clients with Negative HIV status before ANC ", ReportUtils.map(datimIndicators.clientsWithNegativeHivStatusBeforeAnc1(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07","08","09","10","11"));

        //Number of Adults and Children with HIV infection receiving ART By Age/Sex Disagreggation
        EmrReportingUtils.addRow(cohortDsd, "TX_CURR", "Number of Adults and Children with HIV infection receiving ART", ReportUtils.map(datimIndicators.currentlyOnArt(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //Number of Pregnant women with HIV infection receiving antiretroviral therapy (ART)
        cohortDsd.addColumn("TX_CURR_PREGNANT", "Pregnant women with HIV infection receiving ART", ReportUtils.map(datimIndicators.pregnantCurrentlyOnART(), indParams), "");

        //Number of Breastfeeding mothers with HIV infection receiving antiretroviral therapy (ART)
        cohortDsd.addColumn("TX_CURR_BF", "Breast Feeding mothers with HIV infection receiving ART", ReportUtils.map(datimIndicators.bfMothersCurrentlyOnART(), indParams), "");

        //Number of clients with known HIV status at ANC
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_STAT_Known_Status", "Clients with Known HIV status at ANC", ReportUtils.map(datimIndicators.clientsWithKnownHIVStatusAtANC(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07","08","09","10","11"));
        //Number of clients with known HIV status at ANC
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_STAT_Unknown_status", "Clients with Unknown HIV status at ANC", ReportUtils.map(datimIndicators.clientsWithUnKnownHIVStatusAtANC(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07","08","09","10","11"));
        //Newly enrolled to ANC
        cohortDsd.addColumn( "PMTCT_STAT_Denominator", "Clients newly enrolled to ANC", ReportUtils.map(datimIndicators.clientsNewlyEnrolledToANC(), indParams), "");

        //Infants tested Negative for Virology
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_EID_SampleTaken", "Infants tested Negative for Virology", ReportUtils.map(datimIndicators.infantsSampleTakenForVirology(), indParams), datimAgeDisaggregationMonths, Arrays.asList("01", "02","03"));

        //Infants tested Negative for Virology
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_EID_Negative", "Infants tested Negative for Virology", ReportUtils.map(datimIndicators.infantsTestedNegativeForVirology(), indParams), datimAgeDisaggregationMonths, Arrays.asList("01", "02","03"));

        //Infants tested Positive for Virology
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_EID_Positive", "Infants tested Positive for Virology", ReportUtils.map(datimIndicators.infantsTestedPositiveForVirology(), indParams), datimAgeDisaggregationMonths, Arrays.asList("01", "02","03"));

        //Infant Virology with no results
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_EID_No_Results", "Infants tested for Virology with no results", ReportUtils.map(datimIndicators.infantsTestedForVirologyNoResult(), indParams), datimAgeDisaggregationMonths, Arrays.asList("01", "02","03"));

        //Mothers already on ART at start of current pregnancy
        //EmrReportingUtils.addRow(cohortDsd, "PMTCT_ART_Already", "Number of Mothers Already on ART at the start of current Pregnancy", ReportUtils.map(datimIndicators.mothersAlreadyOnARTAtStartOfCurrentPregnancy(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07","08","09","10","11"));

        //Mothers new on ART during current pregnancy
        //EmrReportingUtils.addRow(cohortDsd, "PMTCT_ART_New", "Mothers new on ART during current pregnancy", ReportUtils.map(datimIndicators.mothersNewOnARTDuringCurrentPregnancy(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07","08","09","10","11"));

        //Tested Negative at PITC Inpatient Services
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Inpatient_Negative", "Tested Negative at PITC Inpatient Services", ReportUtils.map(datimIndicators.testedNegativeAtPITCInpatientServices(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //Tested Positive at PITC Inpatient Services
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Inpatient_Positive", "Tested Positive at PITC Inpatient Services", ReportUtils.map(datimIndicators.testedPositiveAtPITCInpatientServices(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //Tested Negative at PITC PMTCT services ANC-1 only
        EmrReportingUtils.addRow(cohortDsd,"HTS_TST_PMTCT_ANC1_Negative", "Tested Negative at PITC PMTCT ANC1", ReportUtils.map(datimIndicators.testedNegativeAtPITCPMTCTANC1(), indParams),datimPMTCTANCAgeDisaggregation,Arrays.asList("01", "02", "03", "04", "05", "06", "07","08","09","10","11"));

        //Tested Positive at PITC PMTCT services ANC-1 only
        EmrReportingUtils.addRow(cohortDsd,"HTS_TST_PMTCT_ANC1_Positive", "Tested Positive at PITC PMTCT ANC1", ReportUtils.map(datimIndicators.testedPositiveAtPITCPMTCTANC1(), indParams), datimPMTCTANCAgeDisaggregation,Arrays.asList("01", "02", "03", "04", "05", "06", "07","08","09","10","11"));

        //Tested Negative at PITC PMTCT services Post ANC-1 (including labour and delivery and BF)
        EmrReportingUtils.addRow(cohortDsd,"HTS_TST_PMTCT_POSTANC1_Negative", "Tested Negative at PITC PMTCT Post ANC1 (Incl L&D,BF)", ReportUtils.map(datimIndicators.testedNegativeAtPITCPMTCTPostANC1(), indParams), datimPMTCTANCAgeDisaggregation,Arrays.asList("01", "02", "03", "04", "05", "06", "07","08","09","10","11"));

        //Tested Positive at PITC PMTCT services Post ANC-1 (including labour and delivery and BF)
        EmrReportingUtils.addRow(cohortDsd,"HTS_TST_PMTCT_POSTANC1_Positive", "Tested Positive at PITC PMTCT Post ANC1 (Incl L&D,BF)", ReportUtils.map(datimIndicators.testedPositiveAtPITCPMTCTPostANC1(), indParams), datimPMTCTANCAgeDisaggregation,Arrays.asList("01", "02", "03", "04", "05", "06", "07","08","09","10","11"));

        //Tested Negative at PITC Paediatric services
        cohortDsd.addColumn("HTS_TST_Paediatric_Negative", "Tested Negative at PITC Paediatric services", ReportUtils.map(datimIndicators.testedNegativeAtPITCPaediatricServices(), indParams), "");

        //Tested Positive at PITC Paediatric services
        cohortDsd.addColumn("HTS_TST_Paediatric_Positive", "Tested Positive at PITC Paediatric Services", ReportUtils.map(datimIndicators.testedPositiveAtPITCPaediatricServices(), indParams), "");

        //Tested Negative at PITC Malnutrition Clinic
        cohortDsd.addColumn("HTS_TST_Malnutrition_Negative", "Tested Negative at PITC Malnutrition Clinic", ReportUtils.map(datimIndicators.testedNegativeAtPITCMalnutritionClinic(), indParams), "");

        //Tested Positive at PITC Malnutrition Clinic
        cohortDsd.addColumn("HTS_TST_Malnutrition_Positive", "Tested Positive at PITC Malnutrition Clinic", ReportUtils.map(datimIndicators.testedPositiveAtPITCMalnutritionClinic(), indParams), "");

        //Tested Negative at PITC TB Clinic
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_TB_Negative", "Tested Negative at PITC TB Clinic", ReportUtils.map(datimIndicators.testedNegativeAtPITCTBClinic(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //Tested Positive at PITC TB Clinic
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_TB_Positive", "Tested Positive at PITC TB Clinic", ReportUtils.map(datimIndicators.testedPositiveAtPITCTBClinic(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //Tested Negative at PITC Other
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Other_Negative", "Tested Negative at PITC Other", ReportUtils.map(datimIndicators.testedNegativeAtPITCOther(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //Tested Positive at PITC Other
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Other_Positive", "Tested Positive at PITC Other", ReportUtils.map(datimIndicators.testedPositiveAtPITCOther(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //Tested Negative at PITC VCT
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_VCT_Negative", "Tested Negative at PITC VCT", ReportUtils.map(datimIndicators.testedNegativeAtPITCVCT(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //Tested Positive at PITC VCT
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_VCT_Positive", "Tested Positive at PITC VCT", ReportUtils.map(datimIndicators.testedPositiveAtPITCVCT(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25","25"));

        //Index Tested Negative
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Index_Negative", "Index Tested Negative", ReportUtils.map(datimIndicators.indexTestedNegative(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25","25"));

        //Index Tested Positive
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Index_Positive", "Index Tested Positive", ReportUtils.map(datimIndicators.indexTestedPositive(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        /*TX_New
        Newly Started ART While Pregnant*/
        cohortDsd.addColumn("TX_New_Pregnant", "Newly Started ART While Pregnant", ReportUtils.map(datimIndicators.newlyStartedARTWhilePregnant(), indParams), "");

        //Newly Started ART While BreastFeeding
        cohortDsd.addColumn("TX_New_BF", "Newly Started ART While Breastfeeding", ReportUtils.map(datimIndicators.newlyStartedARTWhileBF(), indParams), "");

        //Newly Started ART While Confirmed TB and / or TB Treated
        cohortDsd.addColumn("TX_New_TB", "Newly Started ART with TB", ReportUtils.map(datimIndicators.newlyStartedARTWithTB(), indParams), "");

        //Disaggregated by Age / Sex
        EmrReportingUtils.addRow(cohortDsd, "TX_New_Sex_Age", "Newly Started ART Disaggregated by Age / Sex ", ReportUtils.map(datimIndicators.newlyStartedARTByAgeSex(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //Annual Cohort Indicators
        /*PMTCT_FO Number of HIV-exposed infants who were born 24 months prior to the reporting period and registered in the birth cohort.*/
        cohortDsd.addColumn("PMTCT_FO_HEI_COHORT", "HEI Cohort", ReportUtils.map(datimIndicators.totalHEI(), indParams), "");

        //HEI Cohort HIV infected
        cohortDsd.addColumn("PMTCT_FO_INFECTED_HEI", "HEI Cohort HIV+", ReportUtils.map(datimIndicators.hivInfectedHEI(), indParams), "");

        //HEI Cohort HIV uninfected
        cohortDsd.addColumn("PMTCT_FO_UNINFECTED_HEI", "HEI Cohort HIV-", ReportUtils.map(datimIndicators.hivUninfectedHEI(), indParams), "");

        //HEI Cohort HIV-final status unknown
        cohortDsd.addColumn("PMTCT_FO_HEI_UNKNOWN_HIV_STATUS", "HEI Cohort with unknown HIV Status", ReportUtils.map(datimIndicators.unknownHIVStatusHEI(), indParams), "");

        //HEI died with HIV-final status unknown
        cohortDsd.addColumn("PMTCT_FO_HEI_DIED_HIV_STATUS_UNKNOWN", "HEI died with unknown HIV Status", ReportUtils.map(datimIndicators.heiDiedWithunknownHIVStatus(), indParams), "");

        /* TX_RET has been retired
        //TX_RET Number of mothers who are still alive and on treatment at 12 months after initiating ART
        cohortDsd.addColumn("TX_RET_PREGNANT", "Mothers pregnant and Still on ART upto 12 months since start", ReportUtils.map(datimIndicators.alivePregnantOnARTLast12Months(), indParams), "");
        cohortDsd.addColumn("TX_RET_BREASTFEEDING", "Mothers breastfeeding and still on ART for 12 months since start", ReportUtils.map(datimIndicators.aliveBfOnARTLast12Months(), indParams), "");
        //12 months retention Disaggregated by age/gender
        EmrReportingUtils.addRow(cohortDsd, "TX_RET_ALIVE", "12 Months ART retention by Age / sex", ReportUtils.map(datimIndicators.aliveOnlyOnARTInLast12MonthsByAgeSex(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));
        //TX_RET Denominator Started ART last 12 months and breastfeeding
        cohortDsd.addColumn("TX_RET_BF", "Started ART within last 12 Months and Breastfeeding", ReportUtils.map(datimIndicators.totalBFStartedARTLast12Months(), indParams), "");
        //TX_RET Denominator Started ART last 12 months and pregnant
        cohortDsd.addColumn("TX_RET_DENOMINATOR_PREGNANT", "Started ART with past 12 Months and pregnant", ReportUtils.map(datimIndicators.totalPregnantStartedARTLast12Months(), indParams), "");
        //TX_RET (Denominator) All started ART last 12 months disaggregated by Age/sex
        EmrReportingUtils.addRow(cohortDsd, "TX_RET_ART_ALL", "All started ART with last 12 Months by Age / sex", ReportUtils.map(datimIndicators.allOnARTLast12MonthsByAgeSex(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));
        */

        //TX_PVLS (Routine) Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) documented in the medical records and/or supporting laboratory results within the past 12 months.
        cohortDsd.addColumn("TX_PVLS_SUPP_ROUTINE_ALL", "Number of patients on ART with suppressed viral load results (<1,000 copies/ml) Routine Test", ReportUtils.map(datimIndicators.onARTSuppRoutineVLLast12Months(), indParams), "");

        //TX_PVLS (Targeted) Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) documented in the medical records and/or supporting laboratory results within the past 12 months.
        cohortDsd.addColumn("TX_PVLS_SUPP_TARGETED_ALL", "Number of patients on ART with suppressed viral load results (<1,000 copies/ml) Targeted Test", ReportUtils.map(datimIndicators.onARTSuppTargetedVLLast12Months(), indParams), "");

        //TX_PVLS (Undocumented) Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) documented in the medical records and/or supporting laboratory results within the past 12 months.
        cohortDsd.addColumn("TX_PVLS_SUPP_UNDOCUMENTED_ALL", "Number of patients on ART with suppressed viral load results (<1,000 copies/ml) Undocumented Test", ReportUtils.map(datimIndicators.onARTSuppUndocumentedVLLast12Months(), indParams), "");

        //TX_PVLS Number of pregnant patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by Pregnant / Routine
        cohortDsd.addColumn("TX_PVLS_SUPP_PREGNANT_ROUTINE", "Number of patients on ART with suppressed viral load results (<1,000 copies/ml) pregnant routine", ReportUtils.map(datimIndicators.pregnantOnARTWithSuppressedRoutineVLLast12Months(), indParams), "");

        //TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by Pregnant / Targeted
        cohortDsd.addColumn("TX_PVLS_SUPP_PREGNANT_TARGETED", "Number of patients on ART with suppressed viral load results (<1,000 copies/ml) Pregnant Targeted", ReportUtils.map(datimIndicators.pregnantOnARTSuppTargetedVLLast12Months(), indParams), "");

        //TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by Pregnant / undocumented
        cohortDsd.addColumn("TX_PVLS_SUPP_PREGNANT_UNDOCUMENTED", "Number of patients on ART with suppressed viral load results (<1,000 copies/ml) Pregnant Undocumented Test", ReportUtils.map(datimIndicators.pregnantOnARTSuppUndocumentedVLLast12Months(), indParams), "");

        //TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by BF / Routine
        cohortDsd.addColumn("TX_PVLS_SUPP_BF_ROUTINE", "Number of patients on ART with suppressed viral load results (<1,000 copies/ml) BF Routine", ReportUtils.map(datimIndicators.bfOnARTSuppRoutineVLLast12Months(), indParams), "");

        //TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by BF / Targeted
        cohortDsd.addColumn("TX_PVLS_SUPP_BF_TARGETED", "Number of patients on ART with suppressed viral load results (<1,000 copies/ml) BF Targeted", ReportUtils.map(datimIndicators.bfOnARTSuppTargetedVLLast12Months(), indParams), "");

        //TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by BF / Undocumented
        cohortDsd.addColumn("TX_PVLS_SUPP_BF_UNDOCUMENTED", "Number of patients on ART with suppressed viral load results (<1,000 copies/ml) BF undocumented Test", ReportUtils.map(datimIndicators.bfOnARTSuppUndocumentedVLLast12Months(), indParams), "");

        //TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by Age/Sex Routine
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_SUPP_ROUTINE", "All started ART within last 12 Months by Age / sex", ReportUtils.map(datimIndicators.onARTSuppRoutineVLAgeSex(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by Age/Sex Targeted
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_SUPP_TARGETED", "All started ART within last 12 Months by Age / sex", ReportUtils.map(datimIndicators.onARTSuppTargetedVLAgeSex(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by Age/Sex Undocumented
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_SUPP_UNDOCUMENTED", "All started ART within last 12 Months by Age / sex", ReportUtils.map(datimIndicators.onARTSuppUndocumentedVLAgeSex(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //TX_PVLS (Denominator)

        /*Number of adult and pediatric ART patients with a viral load result documented in the patient medical record and /or laboratory records in the past 12 months.
        Disaggregated by Routine*/
        //TX_PVLS Denominator viral load result last 12 months with Routine test result
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_ROUTINE_ALL", "On ART within last 12 Months and viral load Routine test result", ReportUtils.map(datimIndicators.onARTRoutineVLLast12Months(), indParams), "");

        //Disaggregated by Targeted
        //TX_PVLS Denominator viral load result last 12 months with Targeted test result
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_TARGETED_ALL", "On ART within last 12 Months and viral load Targeted test result", ReportUtils.map(datimIndicators.onARTTargetedVLLast12Months(), indParams), "");

        //Disaggregated by Undocumented
        //TX_PVLS Denominator viral load result last 12 months with Undocumented test result
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_UNDOCUMENTED_ALL", "On ART within last 12 Months and viral load Undocumented test result", ReportUtils.map(datimIndicators.totalARTWithUndocumentedVLLast12Months(), indParams), "");

        //TX_PVLS Number of  patients on ART with  viral load results  within the past 12 months. Disaggregated by Pregnant / Routine
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_PREGNANT_ROUTINE", "Number of patients on ART with  viral load results  pregnant routine", ReportUtils.map(datimIndicators.pregnantOnARTRoutineVLLast12Months(), indParams), "");

        //TX_PVLS Number of  patients on ART with  viral load results  within the past 12 months. Disaggregated by Pregnant / Targeted
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_PREGNANT_TARGETED", "Number of patients on ART with  viral load results  Pregnant Targeted", ReportUtils.map(datimIndicators.pregnantOnARTTargetedVLLast12Months(), indParams), "");

        //TX_PVLS Number of  patients on ART with  viral load results  within the past 12 months. Disaggregated by Pregnant / undocumented
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_PREGNANT_UNDOCUMENTED", "Number of patients on ART with  viral load results  Pregnant Undocumented Test", ReportUtils.map(datimIndicators.pregnantARTUndocumentedVLLast12Months(), indParams), "");

        //TX_PVLS Number of  patients on ART with  viral load results  within the past 12 months. Disaggregated by BF / Routine
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_BF_ROUTINE", "Number of patients on ART with  viral load results  BF Routine", ReportUtils.map(datimIndicators.breastfeedingOnARTRoutineVLLast12Months(), indParams), "");

        //TX_PVLS Number of  patients on ART with  viral load results  within the past 12 months. Disaggregated by BF / Targeted
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_BF_TARGETED", "Number of patients on ART with  viral load results  BF Targeted", ReportUtils.map(datimIndicators.breastfeedingOnARTTargetedVLLast12Months(), indParams), "");

        //TX_PVLS Number of patients on ART with  viral load results  within the past 12 months. Disaggregated by BF / Undocumented
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_BF_UNDOCUMENTED", "Number of patients on ART with  viral load results  BF undocumented Test", ReportUtils.map(datimIndicators.breastfeedingOnARTUndocumentedVLLast12Months(), indParams), "");

        //TX_PVLS Number of adults and pediatric patients on ART with viral load Routine results in the past 12 months. Disaggregated by Age/Sex
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_DENOMINATOR_ROUTINE", "On ART with VL routine test documented in the last 12 Months by Age / sex / Indication", ReportUtils.map(datimIndicators.onARTRoutineVLLast12MonthsbyAgeSex(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //TX_PVLS Number of adults and pediatric patients on ART with viral load Targeted results in the past 12 months. Disaggregated by Age/Sex
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_DENOMINATOR_TARGETED", "On ART with VL targeted test documented in the last 12 Months by Age / sex / Indication", ReportUtils.map(datimIndicators.onARTTargetedVLLast12MonthsbyAgeSex(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //TX_PVLS Number of adults and pediatric patients on ART with viral load undocumented results in the past 12 months. Disaggregated by Age/Sex
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_DENOMINATOR_UNDOCUMENTED", "On ART with VL undocumented in the last 12 Months by Age / sex / Indication", ReportUtils.map(datimIndicators.onARTUndocumentedVLLast12MonthsbyAgeSex(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //TX_ML Number of ART patients with no clinical contact since their last expected contact
        //EmrReportingUtils.addRow(cohortDsd, "TX_ML", "Number of ART patients with no clinical contact since their last expected contact", ReportUtils.map(datimIndicators.onARTMissedAppointment(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //TX_ML_DIED Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed)
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_DIED", "ART patients with no clinical contact since their last expected contact due to death", ReportUtils.map(datimIndicators.onARTMissedAppointmentDied(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //TX_ML_DIED_TB Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed) as a result of TB
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_DIED_TB", "ART patients with no clinical contact since their last expected contact due to death as a result of TB", ReportUtils.map(datimIndicators.onARTMissedAppointmentDiedTB(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //TX_ML_DIED_CANCER Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed) as a result of Cancer
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_DIED_CANCER", "ART patients with no clinical contact since their last expected contact due to death as a result of cancer", ReportUtils.map(datimIndicators.onARTMissedAppointmentDiedCancer(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //TX_ML_DIED_OTHER_INFECTIOUS_DISEASE Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed) as a result of other infectious and parasitic disease
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_DIED_OTHER_INFECTIOUS_DISEASE", "ART patients with no clinical contact since their last expected contact due to death as a result of other infectious and parasitic disease", ReportUtils.map(datimIndicators.onARTMissedAppointmentDiedOtherInfectious(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //TX_ML_DIED_OTHER_DISEASE Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed) as a result of other disease or condition
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_DIED_OTHER_DISEASE", "ART patients with no clinical contact since their last expected contact due to death as a result of other disease or condition", ReportUtils.map(datimIndicators.onARTMissedAppointmentDiedOtherDisease(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //TX_ML_DIED_NATURAL Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed) as a result of natural cause
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_DIED_NATURAL", "ART patients with no clinical contact since their last expected contact due to death as a result of natural cause", ReportUtils.map(datimIndicators.onARTMissedAppointmentDiedNatural(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //TX_ML_DIED_NONNATURAL Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed) as a result of non-natural cause*/
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_DIED_NONNATURAL", "ART patients with no clinical contact since their last expected contact due to death as a result of non-natural cause", ReportUtils.map(datimIndicators.onARTMissedAppointmentDiedNonNatural(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //TX_ML_DIED_UNKNOWN Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed) as a result of unknown cause
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_DIED_UNKNOWN", "ART patients with no clinical contact since their last expected contact due to death as a result of unknown cause", ReportUtils.map(datimIndicators.onARTMissedAppointmentDiedUnknown(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //TX_ML_PREV_UNDOCUMENTED_TRF Number of ART patients with no clinical contact since their last expected contact due to Previously undocumented transfer
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_PREV_UNDOCUMENTED_TRF", "ART patients with no clinical contact since their last expected contact due to undocumented transfer", ReportUtils.map(datimIndicators.onARTMissedAppointmentTransferred(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //TX_ML_TRACED_UNLOCATED Number of ART patients with no clinical contact since their last expected contact due to un-traceability
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_TRACED_UNLOCATED", "ART patients with no clinical contact since their last expected contact and were untraceable", ReportUtils.map(datimIndicators.onARTMissedAppointmentUntraceable(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //TX_ML_NO_TRACE_ATTEMPTED Number of ART patients with no clinical contact since their last expected contact with no tracing attempted*/
        EmrReportingUtils.addRow(cohortDsd, "TX_ML", "Number of ART patients with no clinical contact since their last expected contact cohort definitions, dataset definitions and ", ReportUtils.map(datimIndicators.onARTMissedAppointmentNotTraced(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //HTS_INDEX Number of individuals who were identified and tested using Index testing services and received their results
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_NO_TRACE_ATTEMPTED", "No. of individuals identified and tested & received results using Index testing services", ReportUtils.map(datimIndicators.testedThroughIndexServices(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        //HTS_RECENT Persons aged ≥15 years newly diagnosed with HIV-1 infection who have a test for recent infection
        EmrReportingUtils.addRow(cohortDsd, "HTS_RECENT", "Persons aged ≥15 years newly diagnosed with HIV-1 infection who have a test for recent infection", ReportUtils.map(datimIndicators.recentHIVInfections(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        return cohortDsd;

    }
}


