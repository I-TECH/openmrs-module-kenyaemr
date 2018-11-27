package org.openmrs.module.kenyaemr.reporting.builder.hiv;

import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.Datim.ETLDatimIndicatorLibrary;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.DatimQ4.ETLDatimQ4IndicatorLibrary;
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
@Builds({"kenyaemr.etl.common.report.datimQ4"})
public class DatimQ4ReportBuilder extends AbstractReportBuilder {
    @Autowired
    private CommonDimensionLibrary commonDimensions;

    @Autowired
    private ETLDatimQ4IndicatorLibrary datimQ4Indicators;


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
        ColumnParameters f25_49 = new ColumnParameters(null, "20-49, Female", "gender=F|age=20-49");
        ColumnParameters m25_49 = new ColumnParameters(null, "20-49, Male", "gender=M|age=20-49");
        ColumnParameters f_Over_50 = new ColumnParameters(null, "50+, Female", "gender=F|age=50+");
        ColumnParameters m_Over_50 = new ColumnParameters(null, "50+, Male", "gender=M|age=50+");
        ColumnParameters colTot = new ColumnParameters(null, "Total", "");

        List<ColumnParameters> datimQ4AgeDisaggregation =
                Arrays.asList(colInfant,children_1_to_9,f10_14,m10_14,f15_19,m15_19,f20_24,m20_24,f25_49,m25_49,f_Over_50,m_Over_50);

        List<ColumnParameters> datimQ4AgeDisaggregationMonths = Arrays.asList(all0_to_2m, all2_to_12m);

        List<ColumnParameters> datimAgeDisaggregationANC = Arrays.asList(colInfant,f1_to_9,f10_14,f15_19,f20_24,f25_49,f_Over_50);

        List<ColumnParameters> allAgeDisaggregation = Arrays.asList(
                colInfants, children_1_to_9, f10_14, m10_14,f15_19, m15_19,
                f20_24,m20_24,f25_49, m25_49,f_Over_50,m_Over_50);

        String indParams = "startDate=${startDate},endDate=${endDate}";
        String endDateParams = "endDate=${endDate}";
        // 3.1 (On CTX Prophylaxis)

        /*EmrReportingUtils.addRow(cohortDsd, "TX_New", "Started on Art", ReportUtils.map(datimQ4Indicators.startedOnArt(), indParams), allAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19"));*/

        cohortDsd.addColumn("TX_New_TB_co_infected", "Started on ART and TB co-infected", ReportUtils.map(datimQ4Indicators.startedOnARTAndTBCoinfected(), indParams), "");
        cohortDsd.addColumn("TX_New_pregnant", "Started on ART and pregnant ", ReportUtils.map(datimQ4Indicators.startedOnARTAndPregnant(), indParams), "");

        //4 HIV Negative at ANC
        EmrReportingUtils.addRow(cohortDsd, "HTC_TST_Negative", "Clients tested HIV Negative at ANC", ReportUtils.map(datimQ4Indicators.patientsTestNegativeAtANC(), indParams), datimAgeDisaggregationANC, Arrays.asList("01", "02", "03", "04", "05", "06","07"));

        //4 HIV Positive at ANC
        EmrReportingUtils.addRow(cohortDsd, "HTC_TST_Positive", "Clients tested HIV Positive at ANC", ReportUtils.map(datimQ4Indicators.patientsTestPositiveAtANC(), indParams), datimAgeDisaggregationANC, Arrays.asList("01", "02", "03", "04", "05", "06","07"));

        //Number of clients with known HIV status at ANC
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_STA_Numerator", "Clients with Known HIV status at ANC", ReportUtils.map(datimQ4Indicators.clientsWithKnownHIVStatusAtANC(), indParams), datimAgeDisaggregationANC, Arrays.asList("01", "02", "03", "04", "05", "06","07"));

        //Newly enrolled to ANC
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_STA_Denominator", "Clients newly enrolled to ANC", ReportUtils.map(datimQ4Indicators.clientsNewlyEnrolledToANC(), indParams), datimAgeDisaggregationANC, Arrays.asList("01", "02", "03", "04", "05", "06","07"));

        //Infants tested Negative for Virology
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_EID_Negative", "Infants tested Negative for Virology", ReportUtils.map(datimQ4Indicators.infantsTestedNegativeForVirology(), indParams), datimQ4AgeDisaggregationMonths, Arrays.asList("01", "02"));

        //Infants tested Positive for Virology
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_EID_Positive", "Infants tested Positive for Virology", ReportUtils.map(datimQ4Indicators.infantsTestedPositiveForVirology(), indParams), datimQ4AgeDisaggregationMonths, Arrays.asList("01", "02"));

        //Infant Virology with no results
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_EID_No_Results", "Infants tested for Virology with no results", ReportUtils.map(datimQ4Indicators.infantsTestedForVirologyNoResult(), indParams), datimQ4AgeDisaggregationMonths, Arrays.asList("01", "02"));

        //Mothers already on ART at start of current pregnancy
        cohortDsd.addColumn("PMTCT_ART_Already", "Number of Mothers Already on ART at the start of current Pregnancy", ReportUtils.map(datimQ4Indicators.mothersAlreadyOnARTAtStartOfCurrentPregnancy(), indParams), "");

        //Mothers new on ART during current pregnancy
        cohortDsd.addColumn("PMTCT_ART_New", "Mothers new on ART during current pregnancy", ReportUtils.map(datimQ4Indicators.mothersNewOnARTDuringCurrentPregnancy(), indParams), "");

        /*Tested Negative at PITC Inpatient Services*/
        EmrReportingUtils.addRow(cohortDsd,"HTC_TST_Inpatient_Negative", "Tested Negative at PITC Inpatient Services", ReportUtils.map(datimQ4Indicators.testedNegativeAtPITCInpatientServices(),indParams), datimQ4AgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        /*Tested Positive at PITC Inpatient Services*/
        EmrReportingUtils.addRow(cohortDsd,"HTC_TST_Inpatient_Positive", "Tested Positive at PITC Inpatient Services", ReportUtils.map(datimQ4Indicators.testedPositiveAtPITCInpatientServices(),indParams), datimQ4AgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        /*Tested Negative at PITC Paediatric services*/
        cohortDsd.addColumn("HTC_TST_Paediatric_Negative", "Tested Negative at PITC Paediatric services", ReportUtils.map(datimQ4Indicators.testedNegativeAtPITCPaediatricServices(), indParams), "");

        /*Tested Positive at PITC Paediatric services*/
        cohortDsd.addColumn("HTC_TST_Paediatric_Positive", "Tested Positive at PITC Paediatric Services", ReportUtils.map(datimQ4Indicators.testedPositiveAtPITCPaediatricServices(), indParams), "");

        /*Tested Negative at PITC Malnutrition Clinic*/
        cohortDsd.addColumn("HTC_TST_Malnutrition_Negative", "Tested Negative at PITC Malnutrition Clinic", ReportUtils.map(datimQ4Indicators.testedNegativeAtPITCMalnutritionClinic(), indParams), "");

        /*Tested Positive at PITC Malnutrition Clinic*/
        cohortDsd.addColumn("HTC_TST_Malnutrition_Positive", "Tested Positive at PITC Malnutrition Clinic", ReportUtils.map(datimQ4Indicators.testedPositiveAtPITCMalnutritionClinic(), indParams), "");

        /*Tested Negative at PITC TB Clinic*/
        EmrReportingUtils.addRow(cohortDsd,"HTC_TST_TB_Negative", "Tested Negative at PITC TB Clinic", ReportUtils.map(datimQ4Indicators.testedNegativeAtPITCTBClinic(),indParams), datimQ4AgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        /*Tested Positive at PITC TB Clinic*/
        EmrReportingUtils.addRow(cohortDsd,"HTC_TST_TB_Positive", "Tested Positive at PITC TB Clinic", ReportUtils.map(datimQ4Indicators.testedPositiveAtPITCTBClinic(),indParams), datimQ4AgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        /*Tested Negative at PITC Other*/
        EmrReportingUtils.addRow(cohortDsd,"HTC_TST_Other_Negative", "Tested Negative at PITC Other", ReportUtils.map(datimQ4Indicators.testedNegativeAtPITCOther(),indParams), datimQ4AgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        /*Tested Positive at PITC Other*/
        EmrReportingUtils.addRow(cohortDsd,"HTC_TST_Other_Positive", "Tested Positive at PITC Other", ReportUtils.map(datimQ4Indicators.testedPositiveAtPITCOther(),indParams), datimQ4AgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        /*Tested Negative at PITC VCT*/
        EmrReportingUtils.addRow(cohortDsd,"HTC_TST_VCT_Negative", "Tested Negative at PITC VCT", ReportUtils.map(datimQ4Indicators.testedNegativeAtPITCVCT(),indParams), datimQ4AgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        /*Tested Positive at PITC VCT*/
        EmrReportingUtils.addRow(cohortDsd,"HTC_TST_VCT_Positive", "Tested Positive at PITC VCT", ReportUtils.map(datimQ4Indicators.testedPositiveAtPITCVCT(),indParams), datimQ4AgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        /*Index Tested Negative*/
        EmrReportingUtils.addRow(cohortDsd,"HTC_TST_Index_Negative", "Index Tested Negative", ReportUtils.map(datimQ4Indicators.indexTestedNegative(),indParams), datimQ4AgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        /*Index Tested Positive*/
        EmrReportingUtils.addRow(cohortDsd,"HTC_TST_Index_Positive", "Index Tested Positive", ReportUtils.map(datimQ4Indicators.indexTestedPositive(),indParams), datimQ4AgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        //TX_New
        /*Newly Started ART While Pregnant*/
        cohortDsd.addColumn("TX_New_Pregnant", "Newly Started ART While Pregnant", ReportUtils.map(datimQ4Indicators.newlyStartedARTWhilePregnant(), indParams), "");

        /*Newly Started ART While BreastFeeding*/
        cohortDsd.addColumn("TX_New_BF", "Newly Started ART While Breastfeeding", ReportUtils.map(datimQ4Indicators.newlyStartedARTWhileBF(), indParams), "");

        /*Newly Started ART While Confirmed TB and / or TB Treated*/
        cohortDsd.addColumn("TX_New_TB", "Newly Started ART with TB", ReportUtils.map(datimQ4Indicators.newlyStartedARTWithTB(), indParams), "");

        /*Disaggregated by Age / Sex*/
        EmrReportingUtils.addRow(cohortDsd, "TX_New_Sex_Age", "Newly Started ART Disaggregated by Age / Sex ", ReportUtils.map(datimQ4Indicators.newlyStartedARTByAgeSex(),indParams), allAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        /*Annual Cohort Indicators*/
        /*PMTCT_FO Number of HIV-exposed infants who were born 24 months prior to the reporting period and registered in the birth cohort.*/
        cohortDsd.addColumn("PMTCT_FO", "HEI Cohort", ReportUtils.map(datimQ4Indicators.totalHEICohort(), indParams), "");

        /*HEI Cohort HIV infected*/
        cohortDsd.addColumn("PMTCT_FO", "HEI Cohort HIV+", ReportUtils.map(datimQ4Indicators.hivInfectedHEICohort(), indParams), "");

        /*HEI Cohort HIV uninfected*/
        cohortDsd.addColumn("PMTCT_FO", "HEI Cohort HIV-", ReportUtils.map(datimQ4Indicators.hivUninfectedHEICohort(), indParams), "");

        /*HEI Cohort HIV-final status unknown*/
        cohortDsd.addColumn("PMTCT_FO", "HEI Cohort with unknown HIV Status", ReportUtils.map(datimQ4Indicators.unknownHIVStatusHEICohort(), indParams), "");

        /*HEI died with HIV-final status unknown*/
        cohortDsd.addColumn("PMTCT_FO", "HEI died with unknown HIV Status", ReportUtils.map(datimQ4Indicators.heiDiedWithunknownHIVStatus(), indParams), "");

        /*TX_RET Number of mothers who are still alive and on treatment at 12 months after initiating ART*/
        cohortDsd.addColumn("TX_RET_Pregnant", "Mothers pregnant and Still on ART upto 12 months since start", ReportUtils.map(datimQ4Indicators.alivePregnantOnARTLast12Months(), indParams), "");

        cohortDsd.addColumn("TX_RET_Breastfeeding", "Mothers breastfeeding and still on ART for 12 months since start", ReportUtils.map(datimQ4Indicators.aliveBfOnARTLast12Months(), indParams), "");

        /*12 months retention by Disaggregated by age/gender*/
        EmrReportingUtils.addRow(cohortDsd, "TX_RET", "12 Months ART retention by Age / sex", ReportUtils.map(datimQ4Indicators.aliveOnARTInLast12MonthsByAgeSex(),indParams), allAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        /*TX_RET Denominator Started ART last 12 months and breastfeeding*/
        cohortDsd.addColumn("TX_RET", "Started ART within last 12 Months and Breastfeeding", ReportUtils.map(datimQ4Indicators.totalBFStartedARTLast12Months(), indParams), "");

        /*TX_RET Denominator Started ART last 12 months and pregnant*/
        cohortDsd.addColumn("TX_RET", "Started ART with past 12 Months and pregnant", ReportUtils.map(datimQ4Indicators.totalPregnantStartedARTLast12Months(), indParams), "");

        /*TX_RET (Denominator) All started ART last 12 months disaggregated by Age/sex*/
        EmrReportingUtils.addRow(cohortDsd, "TX_RET", "All started ART with last 12 Months by Age / sex", ReportUtils.map(datimQ4Indicators.totalOnARTLast12MonthsByAgeSex(),indParams), allAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

       /*TX_PVLS (Routine) Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) documented in the medical records and/or supporting laboratory results within the past 12 months.*/
        cohortDsd.addColumn("TX_PVLS_Routine", "Number of patients on ART with suppressed viral load results (<1,000 copies/ml) Routine Test", ReportUtils.map(datimQ4Indicators.onARTWithSuppressedRoutineVLLast12Months(), indParams), "");

        /*TX_PVLS (Targeted) Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) documented in the medical records and/or supporting laboratory results within the past 12 months.*/
        cohortDsd.addColumn("TX_PVLS_Targeted", "Number of patients on ART with suppressed viral load results (<1,000 copies/ml) Targeted Test", ReportUtils.map(datimQ4Indicators.onARTWithSuppressedTargetedVLLast12Months(), indParams), "");

        /*TX_PVLS (Undocumented) Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) documented in the medical records and/or supporting laboratory results within the past 12 months.*/
        cohortDsd.addColumn("TX_PVLS_Targeted", "Number of patients on ART with suppressed viral load results (<1,000 copies/ml) Undocumented Test", ReportUtils.map(datimQ4Indicators.onARTWithSuppressedUndocumentedVLLast12Months(), indParams), "");

        /*TX_PVLS Number of pregnant patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by Pregnant / Routine*/
         cohortDsd.addColumn("TX_PVLS_Pregnant_routine", "Number of patients on ART with suppressed viral load results (<1,000 copies/ml) pregnant routine", ReportUtils.map(datimQ4Indicators.pregnantOnARTWithSuppressedRoutineVLLast12Months(), indParams), "");

        /*TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by Pregnant / Targeted*/
        cohortDsd.addColumn("TX_PVLS_Pregnant_Targeted", "Number of patients on ART with suppressed viral load results (<1,000 copies/ml) Pregnant Targeted", ReportUtils.map(datimQ4Indicators.pregnantOnARTWithSuppressedTargetedVLLast12Months(), indParams), "");

        /*TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by Pregnant / undocumented*/
        cohortDsd.addColumn("TX_PVLS_Pregnant_Undocumented", "Number of patients on ART with suppressed viral load results (<1,000 copies/ml) Pregnant Undocumented Test", ReportUtils.map(datimQ4Indicators.pregnantOnARTWithSuppressedUndocumentedVLLast12Months(), indParams), "");

        /*TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by BF / Routine*/
        cohortDsd.addColumn("TX_PVLS_BF_Routine", "Number of patients on ART with suppressed viral load results (<1,000 copies/ml) BF Routine", ReportUtils.map(datimQ4Indicators.bfOnARTWithSuppressedRoutineVLLast12Months(), indParams), "");

        /*TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by BF / Targeted*/
        cohortDsd.addColumn("TX_PVLS_BF_Targeted", "Number of patients on ART with suppressed viral load results (<1,000 copies/ml) BF Targeted", ReportUtils.map(datimQ4Indicators.bfOnARTWithSuppressedTargetedVLLast12Months(), indParams), "");

        /*TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by BF / Undocumented*/
        cohortDsd.addColumn("TX_PVLS_BF_Undocumented", "Number of patients on ART with suppressed viral load results (<1,000 copies/ml) BF undocumented Test", ReportUtils.map(datimQ4Indicators.bfOnARTWithSuppressedUndocumentedVLLast12Months(), indParams), "");

        /*TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by Age/Sex Routine*/
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_Routine", "All started ART with last 12 Months by Age / sex", ReportUtils.map(datimQ4Indicators.routineARTWithSuppressedVLLast12MonthsbyAgeSex(),indParams), allAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        /*TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by Age/Sex Targeted*/
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_Targeted", "All started ART with last 12 Months by Age / sex", ReportUtils.map(datimQ4Indicators.onARTWithSuppressedTargetedVLLast12MonthsbyAgeSex(),indParams), allAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));


        /*TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by Age/Sex Undocumented*/
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_Undocumented", "All started ART with last 12 Months by Age / sex", ReportUtils.map(datimQ4Indicators.onARTWithSuppressedUndocumentedVLLast12MonthsbyAgeSex(),indParams), allAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        /*TX_PVLS (Denominator)*/
        /*Number of adult and pediatric ART patients with a viral load result documented in the patient medical record and /or laboratory records in the past 12 months.*/
        /*Disaggregated by Routine*/
        /*TX_PVLS Denominator viral load result last 12 months with Routine test result*/
        cohortDsd.addColumn("TX_PVLS_Routine", "On ART within last 12 Months and viral load Routine test result", ReportUtils.map(datimQ4Indicators.onARTWithRoutineVLLast12Months(), indParams), "");

        /*Disaggregated by Targeted*/
        /*TX_PVLS Denominator viral load result last 12 months with Targeted test result*/
        cohortDsd.addColumn("TX_PVLS_Targeted", "On ART within last 12 Months and viral load Targeted test result", ReportUtils.map(datimQ4Indicators.onARTWithTargetedVLLast12Months(), indParams), "");

        /*Disaggregated by Undocumented*/
        /*TX_PVLS Denominator viral load result last 12 months with Undocumented test result*/
        cohortDsd.addColumn("TX_PVLS_Undocumented", "On ART within last 12 Months and viral load Undocumented test result", ReportUtils.map(datimQ4Indicators.totalARTWithUndocumentedVLLast12Months(), indParams), "");

        /*TX_PVLS Number of  patients on ART with  viral load results  within the past 12 months. Disaggregated by Pregnant / Routine*/
        cohortDsd.addColumn("TX_PVLS_Pregnant_routine", "Number of patients on ART with  viral load results  pregnant routine", ReportUtils.map(datimQ4Indicators.pregnantOnARTWithRoutineVLLast12Months(), indParams), "");

        /*TX_PVLS Number of  patients on ART with  viral load results  within the past 12 months. Disaggregated by Pregnant / Targeted*/
        cohortDsd.addColumn("TX_PVLS_Pregnant_Targeted", "Number of patients on ART with  viral load results  Pregnant Targeted", ReportUtils.map(datimQ4Indicators.pregnantOnARTWithTargetedVLLast12Months(), indParams), "");

        /*TX_PVLS Number of  patients on ART with  viral load results  within the past 12 months. Disaggregated by Pregnant / undocumented*/
        cohortDsd.addColumn("TX_PVLS_Pregnant_Undocumented", "Number of patients on ART with  viral load results  Pregnant Undocumented Test", ReportUtils.map(datimQ4Indicators.pregnantARTWithUndocumentedVLLast12Months(), indParams), "");

        /*TX_PVLS Number of  patients on ART with  viral load results  within the past 12 months. Disaggregated by BF / Routine*/
        cohortDsd.addColumn("TX_PVLS_BF_Routine", "Number of patients on ART with  viral load results  BF Routine", ReportUtils.map(datimQ4Indicators.breastfeedingOnARTWithRoutineVLLast12Months(), indParams), "");

        /*TX_PVLS Number of  patients on ART with  viral load results  within the past 12 months. Disaggregated by BF / Targeted*/
        cohortDsd.addColumn("TX_PVLS_BF_Targeted", "Number of patients on ART with  viral load results  BF Targeted", ReportUtils.map(datimQ4Indicators.breastfeedingOnARTWithTargetedVLLast12Months(), indParams), "");

        /*TX_PVLS Number of patients on ART with  viral load results  within the past 12 months. Disaggregated by BF / Undocumented*/
        cohortDsd.addColumn("TX_PVLS_BF_Undocumented", "Number of patients on ART with  viral load results  BF undocumented Test", ReportUtils.map(datimQ4Indicators.breastfeedingOnARTWithUndocumentedVLLast12Months(), indParams), "");


        /*TX_PVLS Number of adults and pediatric patients on ART with viral load Routine results in the past 12 months. Disaggregated by Age/Sex*/
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_Routine", "On ART with VL routine test documented in the last 12 Months by Age / sex / Indication", ReportUtils.map(datimQ4Indicators.routineARTWithRoutineVLLast12MonthsbyAgeSex(),indParams), allAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        /*TX_PVLS Number of adults and pediatric patients on ART with viral load Targeted results in the past 12 months. Disaggregated by Age/Sex*/
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_Targeted", "On ART with VL targeted test documented in the last 12 Months by Age / sex / Indication", ReportUtils.map(datimQ4Indicators.onARTWithTargetedVLLast12MonthsbyAgeSex(),indParams), allAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        /*TX_PVLS Number of adults and pediatric patients on ART with viral load undocumented results in the past 12 months. Disaggregated by Age/Sex*/
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_Undocumented", "On ART with VL undocumented in the last 12 Months by Age / sex / Indication", ReportUtils.map(datimQ4Indicators.onARTWithUndocumentedVLLast12MonthsbyAgeSex(),indParams), allAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        return cohortDsd;

    }
}
