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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.DurationToNextAppointmentDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.KPTypeDataDefinition;
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
public class DatimReportBuilder extends AbstractReportBuilder {

    static final int FSW_CONCEPT = 160579;
    static final int MSM_CONCEPT = 160578;
    static final int PWID_CONCEPT = 105;

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

        ColumnParameters funder15 = new ColumnParameters(null, "<15, Female", "gender=F|age=<15");
        ColumnParameters munder15 = new ColumnParameters(null, "<15, Male", "gender=M|age=<15");
        ColumnParameters fabove15 = new ColumnParameters(null, "15+, Female", "gender=F|age=15+");
        ColumnParameters mabove15 = new ColumnParameters(null, "15+, Male", "gender=M|age=15+");

        List<ColumnParameters> datimNewAgeDisaggregation =
                Arrays.asList(fInfant, mInfant, f1_to4, m1_to4, f5_to9, m5_to9, f10_to14, m10_to14, f15_to19, m15_to19, f20_to24, m20_to24,
                        f25_to29, m25_to29, f30_to34, m30_to34, f35_to39, m35_to39, f40_to44, m40_to44, f45_to49, m45_to49, fAbove50, mAbove50, colTotal);

        List<ColumnParameters> datimHTSSelfTestAgeDisaggregation =
                Arrays.asList(f10_to14, m10_to14, f15_to19, m15_to19, f20_to24, m20_to24, f25_to29, m25_to29, f30_to34, m30_to34, f35_to39,
                        m35_to39, f40_to44, m40_to44, f45_to49, m45_to49, fAbove50, mAbove50);

        List<ColumnParameters> datimPMTCTANCAgeDisaggregation =
                Arrays.asList(funder10, f10_to14, f15_to19, f20_to24, f25_to29, f30_to34, f35_to39, f40_to44, f45_to49, fAbove50, colTotal);

        List<ColumnParameters> datimPMTCTCXCAAgeDisaggregation =
                Arrays.asList(f10_to14, f15_to19, f20_to24, f25_to29, f30_to34, f35_to39, f40_to44, f45_to49, fAbove50, colTotal);
        List<ColumnParameters> datimOtherReportsAgeDisaggregation = Arrays.asList(all1_to9, all10_to14, all15_to19, all20_to24, allOver25);

        List<ColumnParameters> datimDCMAgeDisaggregation =
                Arrays.asList(all0_to4, all5_to9, all10_to14, all15_to19, all20_to24, all25_to29, all30_to34, all35_to39, all40_to44, all45_to49, allAbove50);

        List<ColumnParameters> datimPAMAAgeDisaggregation = Arrays.asList(all0_to14);

        List<ColumnParameters> datimAgeDisaggregationMonths = Arrays.asList(all0_to_2m, all2_to_12m, colTotal);

        List<ColumnParameters> datimPrEPNewAgeDisaggregation =
                Arrays.asList(f15_to19, m15_to19, f20_to24, m20_to24,
                        f25_to29, m25_to29, f30_to34, m30_to34, f35_to39, m35_to39, f40_to44, m40_to44, f45_to49, m45_to49, fAbove50, mAbove50, colTotal);

        List<ColumnParameters> datimTBScreenedPositiveNewOnART =
                Arrays.asList( funder15, fabove15,munder15,mabove15 );
        List<ColumnParameters> datimTBScreenedNegativetiveNewOnART =
                Arrays.asList( funder15, fabove15, munder15,mabove15 );

        String indParams = "startDate=${startDate},endDate=${endDate}";
        String endDateParams = "endDate=${endDate}";

        //Prevention Indicators
        // Number of people newly enrolled on Prep
        EmrReportingUtils.addRow(cohortDsd, "PrEP_NEWLY_ENROLLED", "Number of people newly enrolled on Prep", ReportUtils.map(datimIndicators.newlyEnrolledInPrEP(), indParams), datimPrEPNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17"));

        //Newly eonrolled to prep with a recent HIV positive results within 3 months into enrolment
        cohortDsd.addColumn("PrEP_NEWLY_ENROLLED_HIVPOS", "Newly eonrolled to prep with a recent HIV positive results within 3 months into enrolment", ReportUtils.map(datimIndicators.newlyEnrolledInPrEPHIVPos(), indParams), "");

        //Newly eonrolled to prep with a recent HIV negative results within 3 months into enrolment
        cohortDsd.addColumn("PrEP_NEWLY_ENROLLED_HIVNEG", "Newly eonrolled to prep with a recent HIV negative results within 3 months into enrolment", ReportUtils.map(datimIndicators.newlyEnrolledInPrEPHIVNeg(), indParams), "");

        // Number of people currently enrolled on Prep
        EmrReportingUtils.addRow(cohortDsd, "PrEP_CURR_ENROLLED", "Number of people currently enrolled on Prep", ReportUtils.map(datimIndicators.currentlyEnrolledInPrEP(), indParams), datimPrEPNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17"));

        // Proportion of ART patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy
        EmrReportingUtils.addRow(cohortDsd, "TB_PREV_ENROLLED_COMPLETED", "Proportion of ART patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy", ReportUtils.map(datimIndicators.previouslyOnIPTCompleted(), indParams), datimPrEPNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //Number of beneficiaries served by PEPFAR OVC programs for children and families affected by HIV
        cohortDsd.addColumn("DATIM_OVC_SERV", "Number of beneficiaries served by  PEPFAR OVC program", ReportUtils.map(datimIndicators.totalBeneficiaryOfOVCProgram(), indParams), "");

//Testing Indicators
        //HTS_INDEX_OFFERED Index services
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_OFFERED", "Indexes offered Index testing services", ReportUtils.map(datimIndicators.offeredIndexServices(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //HTS_INDEX_ACCEPTED Index services
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_ACCEPTED", "Indexes who accepted Index testing services", ReportUtils.map(datimIndicators.acceptedIndexServices(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //HTS_INDEX_CONTACTS_ELICITED_MALES_UNDER15
        cohortDsd.addColumn("HTS_INDEX_ELICITED_MALE_CONTACTS_UNDER15", "Male Contacts under 15 years", ReportUtils.map(datimIndicators.maleContactsUnder15(), indParams), "");

        //HTS_INDEX_CONTACTS_ELICITED_MALES_ABOVE15
        cohortDsd.addColumn("HTS_INDEX_ELICITED_MALE_CONTACTS_15+", "Male Contacts 15+ years", ReportUtils.map(datimIndicators.maleContacts15AndAbove(), indParams), "");

        //HTS_INDEX_CONTACTS_ELICITED_FEMALES_UNDER15
        cohortDsd.addColumn("HTS_INDEX_ELICITED_FEMALE_CONTACTS_UNDER15", "Female Contacts under 15 years", ReportUtils.map(datimIndicators.femaleContactsUnder15(), indParams), "");

        //HTS_INDEX_CONTACTS_ELICITED_MALES_ABOVE15
        cohortDsd.addColumn("HTS_INDEX_ELICITED_FEMALE_CONTACTS_15+", "Female Contacts 15+ years", ReportUtils.map(datimIndicators.femaleContacts15AndAbove(), indParams), "");

        //HTS_INDEX New Positives
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_POSITIVE", "Contacts tested HIV Positive", ReportUtils.map(datimIndicators.contactTestedPositive(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //HTS_INDEX New Negatives
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_NEGATIVE", "Contacts tested HIV Negative", ReportUtils.map(datimIndicators.contactTestedNegative(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //HTS_INDEX Known Positive
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_KNOWN_POSITIVE", "Contacts Known HIV Positive", ReportUtils.map(datimIndicators.contactKnownPositive(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //HTS_TST
        //1. Index Testing
        //Index Tested Negative
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Index_Negative", "Index Tested Negative", ReportUtils.map(datimIndicators.indexTestedNegative(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "25"));

        //Index Tested Positive
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Index_Positive", "Index Tested Positive", ReportUtils.map(datimIndicators.indexTestedPositive(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //2. VCT testing
        //Tested Negative at PITC VCT
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_VCT_Negative", "Tested Negative at PITC VCT", ReportUtils.map(datimIndicators.testedNegativeAtPITCVCT(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //Tested Positive at PITC VCT
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_VCT_Positive", "Tested Positive at PITC VCT", ReportUtils.map(datimIndicators.testedPositiveAtPITCVCT(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "25"));

        //3. Malnutrition Clinic
        //Tested Negative at PITC Malnutrition Clinic
        cohortDsd.addColumn("HTS_TST_Malnutrition_Negative", "Tested Negative at PITC Malnutrition Clinic", ReportUtils.map(datimIndicators.testedNegativeAtPITCMalnutritionClinic(), indParams), "");

        //Tested Positive at PITC Malnutrition Clinic
        cohortDsd.addColumn("HTS_TST_Malnutrition_Positive", "Tested Positive at PITC Malnutrition Clinic", ReportUtils.map(datimIndicators.testedPositiveAtPITCMalnutritionClinic(), indParams), "");

        //4. Paediatric Clinics
        //Tested Negative at PITC Paediatric services
        cohortDsd.addColumn("HTS_TST_Paediatric_Negative", "Tested Negative at PITC Paediatric services", ReportUtils.map(datimIndicators.testedNegativeAtPITCPaediatricServices(), indParams), "");

        //Tested Positive at PITC Paediatric services
        cohortDsd.addColumn("HTS_TST_Paediatric_Positive", "Tested Positive at PITC Paediatric Services", ReportUtils.map(datimIndicators.testedPositiveAtPITCPaediatricServices(), indParams), "");

        //5. TB Clinics

        //Tested Negative at PITC TB Clinic
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_TB_Negative", "Tested Negative at PITC TB Clinic", ReportUtils.map(datimIndicators.testedNegativeAtPITCTBClinic(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //Tested Positive at PITC TB Clinic
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_TB_Positive", "Tested Positive at PITC TB Clinic", ReportUtils.map(datimIndicators.testedPositiveAtPITCTBClinic(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //6.PMTCT_ANC-1 Only
        //Tested Negative at PITC PMTCT services ANC-1 only
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_PMTCT_ANC1_Negative", "Tested Negative at PITC PMTCT at 1st ANC", ReportUtils.map(datimIndicators.testedNegativeAtPITCPMTCTANC1(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //Tested Positive at PITC PMTCT services ANC-1 only
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_PMTCT_ANC1_Positive", "Tested Positive at PITC PMTCT at 1st ANC", ReportUtils.map(datimIndicators.testedPositiveAtPITCPMTCTANC1(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //7.PMTCT [Post ANC1, Preg/L&D/BF]
        //Tested Negative at PITC PMTCT services Post ANC-1 (including labour and delivery and BF)
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_PMTCT_POSTANC1_Negative", "Tested Negative at PITC PMTCT Post ANC-1 (Incl L&D,BF)", ReportUtils.map(datimIndicators.testedNegativeAtPITCPMTCTPostANC1(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //Tested Positive at PITC PMTCT services Post ANC-1 (including labour and delivery and BF)
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_PMTCT_POSTANC1_Positive", "Tested Positive at PITC PMTCT Post ANC-1 (Incl L&D,BF)", ReportUtils.map(datimIndicators.testedPositiveAtPITCPMTCTPostANC1(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //8.STI
        //9. Inpatient
        //Tested Negative at PITC Inpatient Services
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Inpatient_Negative", "Tested Negative at PITC Inpatient Services", ReportUtils.map(datimIndicators.testedNegativeAtPITCInpatientServices(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //Tested Positive at PITC Inpatient Services
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Inpatient_Positive", "Tested Positive at PITC Inpatient Services", ReportUtils.map(datimIndicators.testedPositiveAtPITCInpatientServices(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //10. Emergency Ward
        //11. VMMC

        //12. Other
        //Tested Negative at PITC Other
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Other_Negative", "Tested Negative at PITC Other", ReportUtils.map(datimIndicators.testedNegativeAtPITCOther(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //Tested Positive at PITC Other
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Other_Positive", "Tested Positive at PITC Other", ReportUtils.map(datimIndicators.testedPositiveAtPITCOther(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //PMTCT_STAT
        //Known positive before ANC
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_STAT_KNOWN_POSITIVE", "Positive HIV status before ANC ", ReportUtils.map(datimIndicators.clientsWithPositiveHivStatusBeforeAnc1(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //4 HIV Positive at ANC
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_STAT_ANC_Positive", "Tested HIV Positive at ANC", ReportUtils.map(datimIndicators.patientsTestPositiveAtANC(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //4 HIV Negative at ANC
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_STAT_ANC_Negative", "Tested HIV Negative at ANC", ReportUtils.map(datimIndicators.patientsTestNegativeAtANC(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //Known Negative before ANC
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_STAT_RECENT_NEGATIVE", "With Negative HIV status before ANC ", ReportUtils.map(datimIndicators.clientsWithNegativeHivStatusBeforeAnc1(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //Newly enrolled to ANC
        //cohortDsd.addColumn( "PMTCT_STAT_Denominator", "Newly enrolled to ANC", ReportUtils.map(datimIndicators.clientsNewlyEnrolledToANC(), indParams), "");
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_STAT_Denominator", "Newly enrolled to ANC", ReportUtils.map(datimIndicators.clientsNewlyEnrolledToANC(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //PMTCT_EID
        //Infants tested Negative for Virology
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_EID_SampleTaken", "Infants sample taken for Virology test", ReportUtils.map(datimIndicators.infantsSampleTakenForVirology(), indParams), datimAgeDisaggregationMonths, Arrays.asList("01", "02", "03"));

        //Infants tested Negative for Virology
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_EID_Negative", "Infants tested Negative for Virology", ReportUtils.map(datimIndicators.infantsTestedNegativeForVirology(), indParams), datimAgeDisaggregationMonths, Arrays.asList("01", "02", "03"));

        //Infants tested Positive for Virology
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_EID_Positive", "Infants tested Positive for Virology", ReportUtils.map(datimIndicators.infantsTestedPositiveForVirology(), indParams), datimAgeDisaggregationMonths, Arrays.asList("01", "02", "03"));

        //Infant Virology with no results
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_EID_No_Results", "Infants tested for Virology with no results", ReportUtils.map(datimIndicators.infantsTestedForVirologyNoResult(), indParams), datimAgeDisaggregationMonths, Arrays.asList("01", "02", "03"));

        //PMTCT_HEI_POS
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_HEI_POS", "Infants identified HIV Positive within 12 months after birth", ReportUtils.map(datimIndicators.infantsTurnedHIVPositive(), indParams), datimAgeDisaggregationMonths, Arrays.asList("01", "02", "03"));

        //PMTCT_HEI_POS_ART
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_HEI_POS_ART", "Infants identified HIV Positive within 12 months after birth and Started ART", ReportUtils.map(datimIndicators.infantsTurnedHIVPositiveOnART(), indParams), datimAgeDisaggregationMonths, Arrays.asList("01", "02", "03"));

        //CXCA_SCRN_NEGATIVE
        EmrReportingUtils.addRow(cohortDsd, "CXCA_SCRN_NEGATIVE", "HIV Positive women on ART screened Negative for cervical cancer 1st time", ReportUtils.map(datimIndicators.firstTimescreenedCXCANegative(), indParams), datimPMTCTCXCAAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10"));

        //CXCA_SCRN_POSITIVE
        EmrReportingUtils.addRow(cohortDsd, "CXCA_SCRN_POSITIVE", "HIV Positive women on ART screened Positive for cervical cancer 1st time", ReportUtils.map(datimIndicators.firstTimescreenedCXCAPositive(), indParams), datimPMTCTCXCAAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10"));

        //CXCA_SCRN_PRESUMED
        EmrReportingUtils.addRow(cohortDsd, "CXCA_SCRN_PRESUMED", "Women on ART with Presumed cervical cancer after re-screening", ReportUtils.map(datimIndicators.rescreenedCXCAPresumed(), indParams), datimPMTCTCXCAAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10"));

        //CXCA_RESCRN_NEGATIVE
        EmrReportingUtils.addRow(cohortDsd, "CXCA_RESCRN_NEGATIVE", "Women on ART re-screened Negative for cervical cancer", ReportUtils.map(datimIndicators.rescreenedCXCANegative(), indParams), datimPMTCTCXCAAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10"));

        //CXCA_RESCRN_POSITIVE
        EmrReportingUtils.addRow(cohortDsd, "CXCA_RESCRN_POSITIVE", "Women on ART re-screened Positive for cervical cancer", ReportUtils.map(datimIndicators.rescreenedCXCAPositive(), indParams), datimPMTCTCXCAAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10"));

        //CXCA_RESCRN_PRESUMED
        EmrReportingUtils.addRow(cohortDsd, "CXCA_RESCRN_PRESUMED", "HIV Positive women on ART with Presumed cervical cancer 1st time screening", ReportUtils.map(datimIndicators.firstTimescreenedCXCAPresumed(), indParams), datimPMTCTCXCAAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10"));

        //Number of OVC current on ART reported to implementing partner
        cohortDsd.addColumn("OVC_HIVSTAT_ONART", "Number of OVC Current on ART reported to implementing partner", ReportUtils.map(datimIndicators.ovcOnART(), indParams), "");

        //Number of OVC Not on ART reported to implementing partner
        cohortDsd.addColumn("OVC_HIVSTAT_NOT_ON_ART", "Number of OVC not on ART reported to implementing partner", ReportUtils.map(datimIndicators.ovcNotOnART(), indParams), "");
        //PMTCT_FO
        //HEI Cohort HIV infected
        cohortDsd.addColumn("PMTCT_FO_INFECTED_HEI", "HEI Cohort HIV+", ReportUtils.map(datimIndicators.hivInfectedHEI(), indParams), "");

        //HEI Cohort HIV uninfected
        cohortDsd.addColumn("PMTCT_FO_UNINFECTED_HEI", "HEI Cohort HIV-", ReportUtils.map(datimIndicators.hivUninfectedHEI(), indParams), "");

        //HEI Cohort HIV-final status unknown
        cohortDsd.addColumn("PMTCT_FO_HEI_UNKNOWN_HIV_STATUS", "HEI Cohort with unknown HIV Status", ReportUtils.map(datimIndicators.unknownHIVStatusHEI(), indParams), "");

        //HEI died with HIV-final status unknown
        cohortDsd.addColumn("PMTCT_FO_HEI_DIED_HIV_STATUS_UNKNOWN", "HEI died with unknown HIV Status", ReportUtils.map(datimIndicators.heiDiedWithunknownHIVStatus(), indParams), "");

        //Treatment Indicators

        //Number of clients with known HIV status at ANC
        //EmrReportingUtils.addRow(cohortDsd, "PMTCT_STAT_Known_Status", "Known HIV status at ANC", ReportUtils.map(datimIndicators.clientsWithKnownHIVStatusAtANC(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07","08","09","10","11"));
        //Number of clients with known HIV status at ANC
        //EmrReportingUtils.addRow(cohortDsd, "PMTCT_STAT_Unknown_status", "Unknown HIV status at ANC", ReportUtils.map(datimIndicators.clientsWithUnKnownHIVStatusAtANC(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07","08","09","10","11"));

        //HTS_RECENT Persons aged ≥15 years newly diagnosed with HIV-1 infection who have a test for recent infection
        // EmrReportingUtils.addRow(cohortDsd, "HTS_RECENT", "Persons aged ≥15 years newly diagnosed with HIV-1 infection who have a test for recent infection", ReportUtils.map(datimIndicators.recentHIVInfections(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        /*EmrReportingUtils.addRow(cohortDsd, "TX_New", "Started on Art", ReportUtils.map(datimIndicators.startedOnArt(), indParams), allAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19"));

        cohortDsd.addColumn("TX_New_TB_co_infected", "Started on ART and TB co-infected", ReportUtils.map(datimIndicators.startedOnARTAndTBCoinfected(), indParams), "");
        cohortDsd.addColumn("TX_New_pregnant", "Started on ART and pregnant ", ReportUtils.map(datimIndicators.startedOnARTAndPregnant(), indParams), "");*/

        //TX_New
        //Disaggregated by Age / Sex
        EmrReportingUtils.addRow(cohortDsd, "TX_New", "Newly Started ART", ReportUtils.map(datimIndicators.newlyStartedARTByAgeSex(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //Newly Started ART While Confirmed TB and / or TB Treated
        //cohortDsd.addColumn("TX_New_TB", "Newly Started ART with TB", ReportUtils.map(datimIndicators.newlyStartedARTWithTB(), indParams), "");

        //Newly Started ART While Pregnant
        //cohortDsd.addColumn("TX_New_Pregnant", "Newly Started ART While Pregnant", ReportUtils.map(datimIndicators.newlyStartedARTWhilePregnant(), indParams), "");

        //Newly Started ART While BreastFeeding
        cohortDsd.addColumn("TX_New_BF", "Newly Started ART While Breastfeeding", ReportUtils.map(datimIndicators.newlyStartedARTWhileBF(), indParams), "");

        //TX_CURR

        //Number of Adults and Children with HIV infection receiving ART By Age/Sex Disagreggation
        EmrReportingUtils.addRow(cohortDsd, "TX_CURR", "Adults and Children with HIV infection receiving ART", ReportUtils.map(datimIndicators.currentlyOnArt(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //Number of Pregnant women with HIV infection receiving antiretroviral therapy (ART)
        cohortDsd.addColumn("TX_CURR_PREGNANT", "Pregnant women with HIV receiving ART", ReportUtils.map(datimIndicators.pregnantCurrentlyOnART(), indParams), "");

        //Number of Breastfeeding mothers with HIV infection receiving antiretroviral therapy (ART)
        cohortDsd.addColumn("TX_CURR_BF", "Breast Feeding mothers with HIV receiving ART", ReportUtils.map(datimIndicators.bfMothersCurrentlyOnART(), indParams), "");

        //Number of Adults with HIV infection receiving ART By KP Type Disagreggation - FSW
        cohortDsd.addColumn("TX_CURR_FSW", "FSW with HIV receiving ART", ReportUtils.map(datimIndicators.fswCurrentlyOnART(mapKPType("FSW", FSW_CONCEPT)), indParams), "");

        //Number of Adults with HIV infection receiving ART By KP Type Disagreggation - MSM
        cohortDsd.addColumn("TX_CURR_MSM", "MSM with HIV receiving ART", ReportUtils.map(datimIndicators.msmCurrentlyOnART(mapKPType("MSM", MSM_CONCEPT)), indParams), "");

        //Number of Adults with HIV infection receiving ART By KP Type Disagreggation - PWID
        cohortDsd.addColumn("TX_CURR_PWID", "PWID with HIV receiving ART", ReportUtils.map(datimIndicators.pwidCurrentlyOnART(mapKPType("PWID", PWID_CONCEPT)), indParams), "");

        //Number of Adults with HIV infection receiving ART By Number of Months drugs dispensed Disagreggation
        cohortDsd.addColumn("TX_CURR_BF", "Breast Feeding mothers with HIV receiving ART", ReportUtils.map(datimIndicators.bfMothersCurrentlyOnART(), indParams), "");

        //One month before next appointment
        cohortDsd.addColumn("TX_CURR_ONE_MONTH_DRUGS", "One month before next appointment", ReportUtils.map(datimIndicators.currentlyOnARTOneMonthDrugsDispensed(durationMapper("1 Month", "0 and 30")), indParams), "");

        //two month before next appointment
        cohortDsd.addColumn("TX_CURR_TWO_MONTHS_DRUGS", "Two month before next appointment", ReportUtils.map(datimIndicators.currentlyOnARTTwoMonthsDrugsDispensed(durationMapper("2 Month", "31 and 60")), indParams), "");

        //three month before next appointment
        cohortDsd.addColumn("TX_CURR_THREE_MONTHS_DRUGS", "Three month before next appointment", ReportUtils.map(datimIndicators.currentlyOnARTThreeMonthsDrugsDispensed(durationMapper("3 Month", "61 and 90")), indParams), "");

        //four month before next appointment
        cohortDsd.addColumn("TX_CURR_FOUR_MONTHS_DRUGS", "Four month before next appointment", ReportUtils.map(datimIndicators.currentlyOnARTFourMonthsDrugsDispensed(durationMapper("4 Month", "91 and 120")), indParams), "");

        //five month before next appointment
        cohortDsd.addColumn("TX_CURR_FIVE_MONTHS_DRUGS", "Five month before next appointment", ReportUtils.map(datimIndicators.currentlyOnARTFiveMonthsDrugsDispensed(durationMapper("5 Month", "121 and 150")), indParams), "");

        //six month before next appointment
        cohortDsd.addColumn("TX_CURR_SIX_MONTHS_DRUGS", "Six month before next appointment", ReportUtils.map(datimIndicators.currentlyOnARTSixMonthsDrugsDispensed(durationMapper("6 Month", "151 and 180")), indParams), "");
//PMTCT_ART

        //Mothers new on ART during current pregnancy
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_ART_New", "Mothers new on ART during current pregnancy", ReportUtils.map(datimIndicators.mothersNewOnARTDuringCurrentPregnancy(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //Mothers already on ART at start of current pregnancy
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_ART_Already", "Number of Mothers Already on ART at the start of current Pregnancy", ReportUtils.map(datimIndicators.mothersAlreadyOnARTAtStartOfCurrentPregnancy(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        // TB_ART Proportion of HIV-positive new and relapsed TB cases New on ART during TB treatment
        EmrReportingUtils.addRow(cohortDsd, "TB_ART_NEW_ON_ART", "TB Patients New on ART ", ReportUtils.map(datimIndicators.newOnARTTBInfected(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        // TB_ART Proportion of HIV-positive new and relapsed TB cases already on ART during TB treatment
        EmrReportingUtils.addRow(cohortDsd, "TB_ART_ALREADY_ON_ART", "TB patients already on ART", ReportUtils.map(datimIndicators.alreadyOnARTTBInfected(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));
        //TX_TB -> Not Available//TX_TB -> Not Available

        //TX_TB(Denominator) -> Not Available
        //TX_ML
        //TX_ML_DIED Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed)
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_DIED", "ART patients with missed appointment due to death", ReportUtils.map(datimIndicators.onARTMissedAppointmentDied(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_ML_DIED_TB Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed) as a result of TB
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_DIED_TB", "ART patients with missed appointment due to death as a result of TB", ReportUtils.map(datimIndicators.onARTMissedAppointmentDiedTB(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_ML_DIED_CANCER Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed) as a result of Cancer
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_DIED_CANCER", "ART patients with missed appointment to death as a result of cancer", ReportUtils.map(datimIndicators.onARTMissedAppointmentDiedCancer(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_ML_DIED_OTHER_INFECTIOUS_DISEASE Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed) as a result of other infectious and parasitic disease
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_DIED_OTHER_INFECTIOUS_DISEASE", "ART patients with missed appointment due to death as a result of other infectious/parasitic disease", ReportUtils.map(datimIndicators.onARTMissedAppointmentDiedOtherInfectious(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_ML_DIED_OTHER_DISEASE Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed) as a result of other disease or condition
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_DIED_OTHER_DISEASE", "ART patients with missed appointment due to death as a result of other disease or condition", ReportUtils.map(datimIndicators.onARTMissedAppointmentDiedOtherDisease(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_ML_DIED_NATURAL Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed) as a result of natural cause
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_DIED_NATURAL", "ART patients with missed appointment due to natural death", ReportUtils.map(datimIndicators.onARTMissedAppointmentDiedNatural(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_ML_DIED_NONNATURAL Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed) as a result of non-natural cause*/
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_DIED_NONNATURAL", "ART patients with missed appointment due to un-natural death", ReportUtils.map(datimIndicators.onARTMissedAppointmentDiedNonNatural(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_ML_DIED_UNKNOWN Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed) as a result of unknown cause
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_DIED_UNKNOWN", "ART patients with missed appointment due to death of unknown cause", ReportUtils.map(datimIndicators.onARTMissedAppointmentDiedUnknown(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_ML_PREV_UNDOCUMENTED_TRF Number of ART patients with no clinical contact since their last expected contact due to Previously undocumented transfer
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_PREV_UNDOCUMENTED_TRF", "ART patients with missed appointment due to undocumented transfer", ReportUtils.map(datimIndicators.onARTMissedAppointmentTransferred(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_ML_STOPPED_TREATMENT Number of ART patients with no clinical contact since their last expected contact because they stopped treatment
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_STOPPED_TREATMENT", "ART patients with missed appointment because they stopped treatment", ReportUtils.map(datimIndicators.onARTMissedAppointmentStoppedTreatment(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_ML_TRACED_UNLOCATED Number of ART patients with no clinical contact since their last expected contact due to un-traceability
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_TRACED_UNLOCATED", "ART patients with missed appointment and untraceable", ReportUtils.map(datimIndicators.onARTMissedAppointmentUntraceable(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_ML_NO_TRACE_ATTEMPTED Number of ART patients with no clinical contact since their last expected contact with no tracing attempted*/
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_NO_TRACE_ATTEMPTED", "ART patients with missed appointment and no tracing attempted", ReportUtils.map(datimIndicators.onARTMissedAppointmentNotTraced(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_RTT Number of ART patients with no clinical contact (or ARV drug pick-up) for greater than 30 days since their last expected contact who restarted ARVs within the reporting period
        EmrReportingUtils.addRow(cohortDsd, "TX_RTT", "Number restarted Treatment during the reporting period", ReportUtils.map(datimIndicators.returnedToTreatment(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //90-90-90 Viral Suppression
        //TX_PVLS

        //TX_PVLS (Routine) Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) documented in the medical records and/or supporting laboratory results within the past 12 months.
        //cohortDsd.addColumn("TX_PVLS_SUPP_ROUTINE_ALL", "On ART with suppressed viral load results (<1,000 copies/ml) Routine Test", ReportUtils.map(datimIndicators.onARTSuppRoutineVLLast12Months(), indParams), "");

        //TX_PVLS (Targeted) Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) documented in the medical records and/or supporting laboratory results within the past 12 months.
        //cohortDsd.addColumn("TX_PVLS_SUPP_TARGETED_ALL", "On ART with suppressed viral load results (<1,000 copies/ml) Targeted Test", ReportUtils.map(datimIndicators.onARTSuppTargetedVLLast12Months(), indParams), "");

        //TX_PVLS (Undocumented) Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) documented in the medical records and/or supporting laboratory results within the past 12 months.
        //cohortDsd.addColumn("TX_PVLS_SUPP_UNDOCUMENTED_ALL", "Number of patients on ART with suppressed viral load results (<1,000 copies/ml) Undocumented Test", ReportUtils.map(datimIndicators.onARTSuppUndocumentedVLLast12Months(), indParams), "");

        //TX_PVLS (Routine) Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) documented in the medical records and/or supporting laboratory results within the past 12 months.
        //cohortDsd.addColumn("TX_PVLS_SUPP_ROUTINE_ALL", "On ART with suppressed viral load results (<1,000 copies/ml) Routine Test", ReportUtils.map(datimIndicators.onARTSuppRoutineVLLast12Months(), indParams), "");

        //TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by Age/Sex Routine
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_SUPP_ROUTINE", "On ART with suppressed routine viral load results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.onARTSuppRoutineVLAgeSex(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by Age/Sex Targeted
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_SUPP_TARGETED", "On ART with suppressed Targeted viral load results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.onARTSuppTargetedVLAgeSex(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by Age/Sex Undocumented
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_SUPP_UNDOCUMENTED", "On ART with undocumented viral load results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.onARTSuppUndocumentedVLAgeSex(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_PVLS Number of pregnant patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by Pregnant / Routine
        cohortDsd.addColumn("TX_PVLS_SUPP_PREGNANT_ROUTINE", "On ART with suppressed viral load results (<1,000 copies/ml) pregnant routine", ReportUtils.map(datimIndicators.pregnantOnARTWithSuppressedRoutineVLLast12Months(), indParams), "");

        //TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by Pregnant / Targeted
        cohortDsd.addColumn("TX_PVLS_SUPP_PREGNANT_TARGETED", "On ART with suppressed viral load results (<1,000 copies/ml) Pregnant Targeted", ReportUtils.map(datimIndicators.pregnantOnARTSuppTargetedVLLast12Months(), indParams), "");

        //TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by Pregnant / undocumented
        cohortDsd.addColumn("TX_PVLS_SUPP_PREGNANT_UNDOCUMENTED", "On ART with suppressed viral load results (<1,000 copies/ml) Pregnant Undocumented Test", ReportUtils.map(datimIndicators.pregnantOnARTSuppUndocumentedVLLast12Months(), indParams), "");

        //TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by BF / Routine
        cohortDsd.addColumn("TX_PVLS_SUPP_BF_ROUTINE", "On ART with suppressed viral load results (<1,000 copies/ml) BF Routine", ReportUtils.map(datimIndicators.bfOnARTSuppRoutineVLLast12Months(), indParams), "");

        //TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by BF / Targeted
        cohortDsd.addColumn("TX_PVLS_SUPP_BF_TARGETED", "On ART with suppressed viral load results (<1,000 copies/ml) BF Targeted", ReportUtils.map(datimIndicators.bfOnARTSuppTargetedVLLast12Months(), indParams), "");

        //TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by BF / Undocumented
        cohortDsd.addColumn("TX_PVLS_SUPP_BF_UNDOCUMENTED", "On ART with suppressed viral load results (<1,000 copies/ml) BF undocumented Test", ReportUtils.map(datimIndicators.bfOnARTSuppUndocumentedVLLast12Months(), indParams), "");

        //TX_PVLS Number of adults and pediatric patients on ART with viral load Routine results in the past 12 months. Disaggregated by Age/Sex
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_DENOMINATOR_ROUTINE", "On ART with VL routine test documented in the last 12 Months", ReportUtils.map(datimIndicators.onARTRoutineVLLast12MonthsbyAgeSex(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_PVLS Number of adults and pediatric patients on ART with viral load Targeted results in the past 12 months. Disaggregated by Age/Sex
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_DENOMINATOR_TARGETED", "On ART with VL targeted test documented in the last 12 Months", ReportUtils.map(datimIndicators.onARTTargetedVLLast12MonthsbyAgeSex(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_PVLS Number of adults and pediatric patients on ART with viral load undocumented results in the past 12 months. Disaggregated by Age/Sex
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_DENOMINATOR_UNDOCUMENTED", "On ART with VL undocumented in the last 12 Months", ReportUtils.map(datimIndicators.onARTUndocumentedVLLast12MonthsbyAgeSex(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_PVLS Number of  patients on ART with  viral load results  within the past 12 months. Disaggregated by Pregnant / Routine
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_PREGNANT_ROUTINE", "Number of patients on ART with  viral load results  pregnant routine", ReportUtils.map(datimIndicators.pregnantOnARTRoutineVLLast12Months(), indParams), "");

        //TX_PVLS Number of  patients on ART with  viral load results  within the past 12 months. Disaggregated by Pregnant / Targeted
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_PREGNANT_TARGETED", "On ART with  viral load results  Pregnant Targeted", ReportUtils.map(datimIndicators.pregnantOnARTTargetedVLLast12Months(), indParams), "");

        //TX_PVLS Number of  patients on ART with  viral load results  within the past 12 months. Disaggregated by Pregnant / undocumented
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_PREGNANT_UNDOCUMENTED", "On ART with  viral load results  Pregnant Undocumented Test", ReportUtils.map(datimIndicators.pregnantARTUndocumentedVLLast12Months(), indParams), "");

        //TX_PVLS Number of  patients on ART with  viral load results  within the past 12 months. Disaggregated by BF / Routine
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_BF_ROUTINE", "On ART with  viral load results  BF Routine", ReportUtils.map(datimIndicators.breastfeedingOnARTRoutineVLLast12Months(), indParams), "");

        //TX_PVLS Number of  patients on ART with  viral load results  within the past 12 months. Disaggregated by BF / Targeted
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_BF_TARGETED", "On ART with  viral load results  BF Targeted", ReportUtils.map(datimIndicators.breastfeedingOnARTTargetedVLLast12Months(), indParams), "");

        //TX_PVLS Number of patients on ART with  viral load results  within the past 12 months. Disaggregated by BF / Undocumented
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_BF_UNDOCUMENTED", "On ART with  viral load results  BF undocumented Test", ReportUtils.map(datimIndicators.breastfeedingOnARTUndocumentedVLLast12Months(), indParams), "");

        //Disaggregated by Targeted
        //TX_PVLS Denominator viral load result last 12 months with Targeted test result
        //cohortDsd.addColumn("TX_PVLS_DENOMINATOR_TARGETED_ALL", "On ART within last 12 Months and viral load Targeted test result", ReportUtils.map(datimIndicators.onARTTargetedVLLast12Months(), indParams), "");

        //Disaggregated by Undocumented
        //TX_PVLS Denominator viral load result last 12 months with Undocumented test result
        //cohortDsd.addColumn("TX_PVLS_DENOMINATOR_UNDOCUMENTED_ALL", "On ART within last 12 Months and viral load Undocumented test result", ReportUtils.map(datimIndicators.totalARTWithUndocumentedVLLast12Months(), indParams), "");

        //HTS_INDEX_OFFERED Index services
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_OFFERED", "Indexes offered Index testing services", ReportUtils.map(datimIndicators.offeredIndexServices(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //HTS_INDEX_ACCEPTED Index services
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_ACCEPTED", "Indexes who accepted Index testing services", ReportUtils.map(datimIndicators.acceptedIndexServices(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //HTS_INDEX New Positives
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_POSITIVE", "Contacts tested HIV Positive", ReportUtils.map(datimIndicators.contactTestedPositive(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //HTS_INDEX New Negatives
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_NEGATIVE", "Contacts tested HIV Negative", ReportUtils.map(datimIndicators.contactTestedNegative(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //HTS_INDEX Known Positive
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_KNOWN_POSITIVE", "Contacts Known HIV Positive", ReportUtils.map(datimIndicators.contactKnownPositive(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //HTS_TST
        //1. Index Testing
        //Index Tested Negative
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Index_Negative", "Index Tested Negative", ReportUtils.map(datimIndicators.indexTestedNegative(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "25"));

        //Index Tested Positive
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Index_Positive", "Index Tested Positive", ReportUtils.map(datimIndicators.indexTestedPositive(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //2. VCT testing
        //Tested Negative at PITC VCT
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_VCT_Negative", "Tested Negative at PITC VCT", ReportUtils.map(datimIndicators.testedNegativeAtPITCVCT(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //Tested Positive at PITC VCT
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_VCT_Positive", "Tested Positive at PITC VCT", ReportUtils.map(datimIndicators.testedPositiveAtPITCVCT(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "25"));

        //3. Malnutrition Clinic
        //Tested Negative at PITC Malnutrition Clinic
        cohortDsd.addColumn("HTS_TST_Malnutrition_Negative", "Tested Negative at PITC Malnutrition Clinic", ReportUtils.map(datimIndicators.testedNegativeAtPITCMalnutritionClinic(), indParams), "");

        //Tested Positive at PITC Malnutrition Clinic
        cohortDsd.addColumn("HTS_TST_Malnutrition_Positive", "Tested Positive at PITC Malnutrition Clinic", ReportUtils.map(datimIndicators.testedPositiveAtPITCMalnutritionClinic(), indParams), "");

        //4. Paediatric Clinics
        //Tested Negative at PITC Paediatric services
        cohortDsd.addColumn("HTS_TST_Paediatric_Negative", "Tested Negative at PITC Paediatric services", ReportUtils.map(datimIndicators.testedNegativeAtPITCPaediatricServices(), indParams), "");

        //Tested Positive at PITC Paediatric services
        cohortDsd.addColumn("HTS_TST_Paediatric_Positive", "Tested Positive at PITC Paediatric Services", ReportUtils.map(datimIndicators.testedPositiveAtPITCPaediatricServices(), indParams), "");

        //5. TB Clinics

        //Tested Negative at PITC TB Clinic
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_TB_Negative", "Tested Negative at PITC TB Clinic", ReportUtils.map(datimIndicators.testedNegativeAtPITCTBClinic(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //Tested Positive at PITC TB Clinic
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_TB_Positive", "Tested Positive at PITC TB Clinic", ReportUtils.map(datimIndicators.testedPositiveAtPITCTBClinic(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //6.PMTCT_ANC-1 Only
        //Tested Negative at PITC PMTCT services ANC-1 only
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_PMTCT_ANC1_Negative", "Tested Negative at PITC PMTCT at 1st ANC", ReportUtils.map(datimIndicators.testedNegativeAtPITCPMTCTANC1(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //Tested Positive at PITC PMTCT services ANC-1 only
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_PMTCT_ANC1_Positive", "Tested Positive at PITC PMTCT at 1st ANC", ReportUtils.map(datimIndicators.testedPositiveAtPITCPMTCTANC1(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //7.PMTCT [Post ANC1, Preg/L&D/BF]
        //Tested Negative at PITC PMTCT services Post ANC-1 (including labour and delivery and BF)
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_PMTCT_POSTANC1_Negative", "Tested Negative at PITC PMTCT Post ANC-1 (Incl L&D,BF)", ReportUtils.map(datimIndicators.testedNegativeAtPITCPMTCTPostANC1(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //Tested Positive at PITC PMTCT services Post ANC-1 (including labour and delivery and BF)
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_PMTCT_POSTANC1_Positive", "Tested Positive at PITC PMTCT Post ANC-1 (Incl L&D,BF)", ReportUtils.map(datimIndicators.testedPositiveAtPITCPMTCTPostANC1(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //8.STI
        //9. Inpatient
        //Tested Negative at PITC Inpatient Services
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Inpatient_Negative", "Tested Negative at PITC Inpatient Services", ReportUtils.map(datimIndicators.testedNegativeAtPITCInpatientServices(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //Tested Positive at PITC Inpatient Services
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Inpatient_Positive", "Tested Positive at PITC Inpatient Services", ReportUtils.map(datimIndicators.testedPositiveAtPITCInpatientServices(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //10. Emergency Ward
        //11. VMMC

        //12. Other
        //Tested Negative at PITC Other
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Other_Negative", "Tested Negative at PITC Other", ReportUtils.map(datimIndicators.testedNegativeAtPITCOther(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //Tested Positive at PITC Other
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Other_Positive", "Tested Positive at PITC Other", ReportUtils.map(datimIndicators.testedPositiveAtPITCOther(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //HTS_RECENT Persons aged ≥15 years newly diagnosed with HIV-1 infection who have a test for recent infection
         // EmrReportingUtils.addRow(cohortDsd, "HTS_RECENT", "Persons aged ≥15 years newly diagnosed with HIV-1 infection who have a test for recent infection", ReportUtils.map(datimIndicators.recentHIVInfections(),indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));


        // Number of people newly enrolled on Prep
        EmrReportingUtils.addRow(cohortDsd, "PrEP_NEWLY_ENROLLED", "Number of people newly enrolled on Prep", ReportUtils.map(datimIndicators.newlyEnrolledInPrEP(), indParams), datimPrEPNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));


        // Number of people currently enrolled on Prep
        EmrReportingUtils.addRow(cohortDsd, "PrEP_CURR_ENROLLED", "Number of people currently enrolled on Prep", ReportUtils.map(datimIndicators.currentlyEnrolledInPrEP(), indParams), datimPrEPNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));

        // Proportion of ART patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy
        EmrReportingUtils.addRow(cohortDsd, "TB_PREV_ENROLLED_COMPLETED", "Proportion of ART patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy", ReportUtils.map(datimIndicators.previouslyOnIPTCompleted(), indParams), datimPrEPNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13","14","15","16","17","18","19","20","21","22","23","24","25"));


        //Number of beneficiaries served by PEPFAR OVC programs for children and families affected by HIV
        cohortDsd.addColumn("DATIM_OVC_SERV", "Number of beneficiaries served by  PEPFAR OVC program", ReportUtils.map(datimIndicators.totalBeneficiaryOfOVCProgram(), indParams), "");


        // Number of people newly on art that tested negative to TB
        EmrReportingUtils.addRow(cohortDsd, "TX_TB_NEGATIVE", "Number of people newly on art that tested negative to TB", ReportUtils.map(datimIndicators.newlyOnArtPatientScreenedNegativeForTB(), indParams), datimTBScreenedNegativetiveNewOnART, Arrays.asList("01", "02", "03", "04"));

        // Number of people newly on art that tested positive to TB
        EmrReportingUtils.addRow(cohortDsd, "TX_TB_POSITIVE", "Number of people newly on art that tested positive to TB", ReportUtils.map(datimIndicators.newlyOnArtPatientScreenedPositiveForTB(), indParams), datimTBScreenedPositiveNewOnART, Arrays.asList("01", "02", "03", "04"));


        // Number of people previously on art that tested negative to TB
        EmrReportingUtils.addRow(cohortDsd, "PREVIOUS_TX_TB_NEGATIVE", "Number of people previously on art that tested negative to TB", ReportUtils.map(datimIndicators.previouslyOnArtPatientScreenedNegativeForTB(), indParams), datimTBScreenedNegativetiveNewOnART, Arrays.asList("01", "02", "03", "04"));

        //Number of people previously on art that tested positive to TB
        EmrReportingUtils.addRow(cohortDsd, "PREVIOUS_TX_TB_POSITIVE", "Number of people previously on art that tested positive to TB", ReportUtils.map(datimIndicators.previouslyOnArtPatientScreenedPositiveForTB(), indParams), datimTBScreenedPositiveNewOnART, Arrays.asList("01", "02", "03", "04"));


        //Number of patients previously on art enrolled on tb this reporting period
        EmrReportingUtils.addRow(cohortDsd, "TX_TB_PREV(NUMERATOR)", "Number of patients previously on art enrolled on tb this reporting period", ReportUtils.map(datimIndicators.PreviouslyOnART_EnrolledOn_TB_ThisReportingPeriod(), indParams), datimTBScreenedPositiveNewOnART, Arrays.asList("01", "02", "03", "04"));

        //Number of patients new on art enrolled on tb this reporting period
        EmrReportingUtils.addRow(cohortDsd, "TX_TB_NEW(NUMERATOR)", "Number of patients new on art enrolled on tb this reporting period", ReportUtils.map(datimIndicators.NewOnARTEnrolledOnTB_ThisReportingPeriod(), indParams), datimTBScreenedPositiveNewOnART, Arrays.asList("01", "02", "03", "04"));

        //Number of ART patients who had a specimen sent for bacteriologic diagnosis of active TB disease
        cohortDsd.addColumn("TX_TB_SPECIMEN", "Number of ART patients who had a specimen sent for bacteriologic diagnosis of active TB disease", ReportUtils.map(datimIndicators.totalPatientsWhoHadSpecimenSentToLab(), indParams), "");

        //Number of ART patients who had a positive result returned for bacteriologic diagnosis of active TB disease
        cohortDsd.addColumn("TX_TB_POSITIVE_RESULT_RETURNED", "Number of ART patients who had a positive result returned for bacteriologic diagnosis of active TB disease", ReportUtils.map(datimIndicators.patientsWithPositiveResultForBacteriologicDiagnosis(), indParams), "");

        //Number of patients whose specimens were sent for  Smear only
        cohortDsd.addColumn("TX_TB_SMEAR_SPECIMEN", "Number of patients whose specimens were sent for  Smear only", ReportUtils.map(datimIndicators.patientSWhoseSpecimenSentForSmearOnly(), indParams), "");

        return cohortDsd;
    }

    private KPTypeDataDefinition mapKPType(String name, Integer concept) {
        KPTypeDataDefinition kpType = new KPTypeDataDefinition(name, concept);
        return kpType;
    }

    private DurationToNextAppointmentDataDefinition durationMapper(String name, String duration) {
        DurationToNextAppointmentDataDefinition durationTonextVisit = new DurationToNextAppointmentDataDefinition(name, duration);
        return durationTonextVisit;
    }
}


