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
    static final int TG_CONCEPT = 165100;

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

        //HTS dimensions
        cohortDsd.addDimension("contactAge", ReportUtils.map(commonDimensions.contactAgeGroups(), "onDate=${endDate}"));
        cohortDsd.addDimension("contactFineAge", ReportUtils.map(commonDimensions.contactsFineAgeGroups(), "onDate=${endDate}"));
        cohortDsd.addDimension("contactGender", ReportUtils.map(commonDimensions.contactGender()));

        ColumnParameters colTotal = new ColumnParameters(null, "Total", "");

        /*DatimQ4 Column parameters*/

        ColumnParameters all0_to_2m = new ColumnParameters(null, "0-2", "age=0-2");
        ColumnParameters all2_to_12m = new ColumnParameters(null, "2-12", "age=2-12");

        /*New age disaggregations*/
        ColumnParameters fInfant = new ColumnParameters(null, "<1, Female", "gender=F|age=<1");
        ColumnParameters mInfant = new ColumnParameters(null, "<1, Male", "gender=M|age=<1");
        ColumnParameters f1_to4 = new ColumnParameters(null, "1-4, Female", "gender=F|age=1-4");
        ColumnParameters m1_to4 = new ColumnParameters(null, "1-4, Male", "gender=M|age=1-4");
        ColumnParameters f5_to9 = new ColumnParameters(null, "5-9, Female", "gender=F|age=5-9");
        ColumnParameters m5_to9 = new ColumnParameters(null, "5-9, Male", "gender=M|age=5-9");
        ColumnParameters fUnder10 = new ColumnParameters(null, "<10, Female", "gender=F|age=<10");
        ColumnParameters mUnder10 = new ColumnParameters(null, "<10, Male", "gender=M|age=<10");
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

        ColumnParameters fUnder15 = new ColumnParameters(null, "<15, Female", "gender=F|age=<15");
        ColumnParameters mUnder15 = new ColumnParameters(null, "<15, Male", "gender=M|age=<15");
        ColumnParameters f15AndAbove = new ColumnParameters(null, "15+, Female", "gender=F|age=15+");
        ColumnParameters m15AndAbove = new ColumnParameters(null, "15+, Male", "gender=M|age=15+");
        //KP age groups
        ColumnParameters below_15 = new ColumnParameters(null, "<15", "age=<15");
        ColumnParameters kp15_to_19 = new ColumnParameters(null, "15-19", "age=15-19");
        ColumnParameters kp20_to_24 = new ColumnParameters(null, "20-24", "age=20-24");
        ColumnParameters kp25_and_above = new ColumnParameters(null, "25+", "age=25+");

        ColumnParameters below_15_f = new ColumnParameters(null, "<15, Female", "gender=F|age=<15");
        ColumnParameters below_15_m = new ColumnParameters(null, "<15, Male", "gender=M|age=<15");
        ColumnParameters kp15_to_19_f = new ColumnParameters(null, "15-19, Female", "gender=F|age=15-19");
        ColumnParameters kp15_to_19_m = new ColumnParameters(null, "15-19, Male", "gender=M|age=15-19");
        ColumnParameters kp20_to_24_f = new ColumnParameters(null, "20-24, Female", "gender=F|age=20-24");
        ColumnParameters kp20_to_24_m = new ColumnParameters(null, "20-24, Male", "gender=M|age=20-24");
        ColumnParameters kp25_and_above_f = new ColumnParameters(null, "25+, Female", "gender=F|age=25+");
        ColumnParameters kp25_and_above_m = new ColumnParameters(null, "25+, Male", "gender=M|age=25+");

        //Patient Contacts
        ColumnParameters contacts_under_15_f = new ColumnParameters(null, "<15, Female", "contactGender=F|contactAge=<15");
        ColumnParameters contacts_under_15_m = new ColumnParameters(null, "<15, Male", "contactGender=M|contactAge=<15");
        ColumnParameters contacts_15_and_above_f = new ColumnParameters(null, ">=15, Female", "contactGender=F|contactAge=15+");
        ColumnParameters contacts_15_and_above_m = new ColumnParameters(null, ">=15, Male", "contactGender=M|contactAge=15+");

        ColumnParameters fCInfant = new ColumnParameters(null, "<1, Female", "contactGender=F|contactFineAge=<1");
        ColumnParameters mCInfant = new ColumnParameters(null, "<1, Male", "contactGender=M|contactFineAge=<1");
        ColumnParameters fC1_to4 = new ColumnParameters(null, "1-4, Female", "contactGender=F|contactFineAge=1-4");
        ColumnParameters mC1_to4 = new ColumnParameters(null, "1-4, Male", "contactGender=M|contactFineAge=1-4");
        ColumnParameters fC5_to9 = new ColumnParameters(null, "5-9, Female", "contactGender=F|contactFineAge=5-9");
        ColumnParameters mC5_to9 = new ColumnParameters(null, "5-9, Male", "contactGender=M|contactFineAge=5-9");
        ColumnParameters fC10_to14 = new ColumnParameters(null, "10-14, Female", "contactGender=F|contactFineAge=10-14");
        ColumnParameters mC10_to14 = new ColumnParameters(null, "10-14, Male", "contactGender=M|contactFineAge=10-14");
        ColumnParameters fC15_to19 = new ColumnParameters(null, "15-19, Female", "contactGender=F|contactFineAge=15-19");
        ColumnParameters mC15_to19 = new ColumnParameters(null, "15-19, Male", "contactGender=M|contactFineAge=15-19");
        ColumnParameters fC20_to24 = new ColumnParameters(null, "20-24, Female", "contactGender=F|contactFineAge=20-24");
        ColumnParameters mC20_to24 = new ColumnParameters(null, "20-24, Male", "contactGender=M|contactFineAge=20-24");
        ColumnParameters fC25_to29 = new ColumnParameters(null, "25-29, Female", "contactGender=F|contactFineAge=25-29");
        ColumnParameters mC25_to29 = new ColumnParameters(null, "25-29, Male", "contactGender=M|contactFineAge=25-29");
        ColumnParameters fC30_to34 = new ColumnParameters(null, "30-34, Female", "contactGender=F|contactFineAge=30-34");
        ColumnParameters mC30_to34 = new ColumnParameters(null, "30-34, Male", "contactGender=M|contactFineAge=30-34");
        ColumnParameters fC35_to39 = new ColumnParameters(null, "35-39, Female", "contactGender=F|contactFineAge=35-39");
        ColumnParameters mC35_to39 = new ColumnParameters(null, "35-39, Male", "contactGender=M|contactFineAge=35-39");
        ColumnParameters fC40_to44 = new ColumnParameters(null, "40-44, Female", "contactGender=F|contactFineAge=40-44");
        ColumnParameters mC40_to44 = new ColumnParameters(null, "40-44, Male", "contactGender=M|contactFineAge=40-44");
        ColumnParameters fC45_to49 = new ColumnParameters(null, "45-49, Female", "contactGender=F|contactFineAge=45-49");
        ColumnParameters mC45_to49 = new ColumnParameters(null, "45-49, Male", "contactGender=M|contactFineAge=45-49");
        ColumnParameters fCAbove50 = new ColumnParameters(null, "50+, Female", "contactGender=F|contactFineAge=50+");
        ColumnParameters mCAbove50 = new ColumnParameters(null, "50+, Male", "contactGender=M|contactFineAge=50+");
        //End of patient contacts

        List<ColumnParameters> datimNewAgeDisaggregation =
                Arrays.asList(fInfant, mInfant, f1_to4, m1_to4, f5_to9, m5_to9, f10_to14, m10_to14, f15_to19, m15_to19, f20_to24, m20_to24,
                        f25_to29, m25_to29, f30_to34, m30_to34, f35_to39, m35_to39, f40_to44, m40_to44, f45_to49, m45_to49, fAbove50, mAbove50, colTotal);


        List<ColumnParameters> datimPMTCTANCAgeDisaggregation =
                Arrays.asList(fUnder10, f10_to14, f15_to19, f20_to24, f25_to29, f30_to34, f35_to39, f40_to44, f45_to49, fAbove50, colTotal);

        List<ColumnParameters> datimPMTCTCXCAAgeDisaggregation =
                Arrays.asList(f10_to14, f15_to19, f20_to24, f25_to29, f30_to34, f35_to39, f40_to44, f45_to49, fAbove50, colTotal);

        List<ColumnParameters> datimAgeDisaggregationMonths = Arrays.asList(all0_to_2m, all2_to_12m, colTotal);

        List<ColumnParameters> datimPrEPNewAgeDisaggregation =
                Arrays.asList(f15_to19, m15_to19, f20_to24, m20_to24,
                        f25_to29, m25_to29, f30_to34, m30_to34, f35_to39, m35_to39, f40_to44, m40_to44, f45_to49, m45_to49, fAbove50, mAbove50, colTotal);

        List<ColumnParameters> datimTXTBOnART =
                Arrays.asList(fUnder15, f15AndAbove, mUnder15, m15AndAbove,colTotal);

        List<ColumnParameters>  kpAgeDisaggregation = Arrays.asList(below_15, kp15_to_19, kp20_to_24, kp25_and_above,colTotal);
        List<ColumnParameters> kpAgeGenderDisaggregation = Arrays.asList(below_15_f, below_15_m, kp15_to_19_f, kp15_to_19_m,
                kp20_to_24_f, kp20_to_24_m, kp25_and_above_f, kp25_and_above_m,colTotal);

        List<ColumnParameters> datimGBVDisaggregation =
                Arrays.asList(fUnder10,mUnder10,f10_to14, m10_to14,f15_to19, m15_to19, f20_to24, m20_to24,
                        f25_to29, m25_to29, f30_to34, m30_to34, f35_to39, m35_to39, f40_to44, m40_to44, f45_to49, m45_to49, fAbove50, mAbove50, colTotal);

        //Patient contacts disagggregations
        List<ColumnParameters> contactAgeSexDisaggregation = Arrays.asList(contacts_under_15_f, contacts_under_15_m, contacts_15_and_above_f, contacts_15_and_above_m, colTotal);
        List<ColumnParameters> contactAgeSexFineDisaggregation =
                Arrays.asList(fCInfant, mCInfant, fC1_to4, mC1_to4, fC5_to9, mC5_to9, fC10_to14, mC10_to14, fC15_to19, mC15_to19, fC20_to24, mC20_to24,
                        fC25_to29, mC25_to29, fC30_to34, mC30_to34, fC35_to39, mC35_to39, fC40_to44, mC40_to44, fC45_to49, mC45_to49, fCAbove50, mCAbove50, colTotal);
        //End of patient contact Disaggregations

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
        cohortDsd.addColumn("OVC_SERV_COMP", "Number of beneficiaries served by PEPFAR OVC Comprehensive program", ReportUtils.map(datimIndicators.totalBeneficiaryOfOVCComprehensiveProgram(), indParams), "");
        cohortDsd.addColumn("OVC_SERV_DREAMS", "Number of beneficiaries served by PEPFAR OVC DREAMS program", ReportUtils.map(datimIndicators.totalBeneficiaryOfOVCDreamsProgram(), indParams), "");
        cohortDsd.addColumn("OVC_SERV_PREV", "Number of beneficiaries served by PEPFAR OVC preventive program", ReportUtils.map(datimIndicators.totalBeneficiaryOfOVCPreventiveProgram(), indParams), "");

        //Testing Indicators
        //HTS_INDEX_OFFERED Index services
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_OFFERED", "Indexes offered Index testing services", ReportUtils.map(datimIndicators.offeredIndexServices(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //HTS_INDEX_ACCEPTED Index services
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_ACCEPTED", "Indexes who accepted Index testing services", ReportUtils.map(datimIndicators.acceptedIndexServices(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //HTS_INDEX_CONTACTS_ELICITED_MALES_UNDER15
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_ELICITED_CONTACTS", "HTS Index Elicited Contacts", ReportUtils.map(datimIndicators.htsIndexContactsElicited(), indParams), contactAgeSexDisaggregation, Arrays.asList("01", "02", "03", "04", "05"));

        //HTS_INDEX New Positives
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_POSITIVE", "Contacts tested HIV Positive", ReportUtils.map(datimIndicators.contactTestedPositive(), indParams), contactAgeSexFineDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //HTS_INDEX New Negatives
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_NEGATIVE", "Contacts tested HIV Negative", ReportUtils.map(datimIndicators.contactTestedNegative(), indParams), contactAgeSexFineDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //HTS_INDEX Known Positive
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_KNOWN_POSITIVE", "Contacts Known HIV Positive", ReportUtils.map(datimIndicators.contactKnownPositive(), indParams), contactAgeSexFineDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //HTS_TST
        //1. Index Testing
        //Index Tested Negative
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Index_Negative", "Index Tested Negative", ReportUtils.map(datimIndicators.indexTestedNegative(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "25"));

        //Index Tested Positive
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Index_Positive", "Index Tested Positive", ReportUtils.map(datimIndicators.indexTestedPositive(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //2. VCT testing
        //Tested Negative VCT
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_VCT_Negative", "Tested Negative VCT", ReportUtils.map(datimIndicators.testedNegativeVCT(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //Tested Positive VCT
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_VCT_Positive", "Tested Positive VCT", ReportUtils.map(datimIndicators.testedPositiveVCT(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "25"));

        //3. Malnutrition Clinic
        //Tested Negative Malnutrition Clinic
        cohortDsd.addColumn("HTS_TST_Malnutrition_Negative", "Tested Negative Malnutrition Clinic", ReportUtils.map(datimIndicators.testedNegativeMalnutritionClinic(), indParams), "");

        //Tested Positive Malnutrition Clinic
        cohortDsd.addColumn("HTS_TST_Malnutrition_Positive", "Tested Positive Malnutrition Clinic", ReportUtils.map(datimIndicators.testedPositiveMalnutritionClinic(), indParams), "");

        //4. Paediatric Clinics
        //Tested Negative Paediatric services
        cohortDsd.addColumn("HTS_TST_Paediatric_Negative", "Tested Negative Paediatric services", ReportUtils.map(datimIndicators.testedNegativePaediatricServices(), indParams), "");

        //Tested Positive Paediatric services
        cohortDsd.addColumn("HTS_TST_Paediatric_Positive", "Tested Positive Paediatric Services", ReportUtils.map(datimIndicators.testedPositivePaediatricServices(), indParams), "");

        //5. TB Clinics

        //Tested Negative TB Clinic
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_TB_Negative", "Tested Negative TB Clinic", ReportUtils.map(datimIndicators.testedNegativeTBClinic(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //Tested Positive TB Clinic
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_TB_Positive", "Tested Positive TB Clinic", ReportUtils.map(datimIndicators.testedPositiveTBClinic(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //6.PMTCT_ANC-1 Only
        //Tested Negative PMTCT services ANC-1 only
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_PMTCT_ANC1_Negative", "Tested Negative PMTCT at 1st ANC", ReportUtils.map(datimIndicators.testedNegativePMTCTANC1(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //Tested Positive PMTCT services ANC-1 only
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_PMTCT_ANC1_Positive", "Tested Positive PMTCT at 1st ANC", ReportUtils.map(datimIndicators.testedPositivePMTCTANC1(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //7.PMTCT [Post ANC1, Preg/L&D/BF]
        //Tested Negative PMTCT services Post ANC-1 (including labour and delivery and BF)
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_PMTCT_POSTANC1_Negative", "Tested Negative PMTCT Post ANC-1 (Incl L&D,BF)", ReportUtils.map(datimIndicators.testedNegativePMTCTPostANC1(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //Tested Positive PMTCT services Post ANC-1 (including labour and delivery and BF)
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_PMTCT_POSTANC1_Positive", "Tested Positive PMTCT Post ANC-1 (Incl L&D,BF)", ReportUtils.map(datimIndicators.testedPositivePMTCTPostANC1(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //8.STI
        //9. Inpatient
        //Tested Negative Inpatient Services
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Inpatient_Negative", "Tested Negative Inpatient Services", ReportUtils.map(datimIndicators.testedNegativeInpatientServices(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //Tested Positive Inpatient Services
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Inpatient_Positive", "Tested Positive Inpatient Services", ReportUtils.map(datimIndicators.testedPositiveInpatientServices(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //10. Emergency Ward
        //11. VMMC

        //12. Other
        //Tested Negative Other
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Other_Negative", "Tested Negative Other", ReportUtils.map(datimIndicators.testedNegativeOther(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //Tested Positive Other
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Other_Positive", "Tested Positive Other", ReportUtils.map(datimIndicators.testedPositiveOther(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //13. KP
        //PWID Positive
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_KP_PWID_POS", "PWID Tested Positive", ReportUtils.map(datimIndicators.pwidTestedPositive(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //14. Mobile Outreach
        //MO Positive
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_MOBILE_POSITIVE", "Tested Positive Mobile Outreach", ReportUtils.map(datimIndicators.testedPositiveMobile(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //MO Negative
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_MOBILE_NEGATIVE", "Tested Negative Mobile Outreach", ReportUtils.map(datimIndicators.testedNegativeMobile(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //15. Social Networks SNS
        //SNS Positive
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_SNS_POSITIVE", "Tested Positive Social Network", ReportUtils.map(datimIndicators.testedPositiveSNS(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //SNS Negative
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_SNS_NEGATIVE", "Tested Negative Social Network", ReportUtils.map(datimIndicators.testedNegativeSNS(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //PWID Negative
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_KP_PWID_NEG", "PWID Tested Negative", ReportUtils.map(datimIndicators.pwidTestedNegative(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));
        //MSM Positive
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_KP_MSM_POS", "MSM Tested Positive", ReportUtils.map(datimIndicators.msmTestedPositive(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //MSM Negative
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_KP_MSM_NEG", "MSM Tested Negative", ReportUtils.map(datimIndicators.msmTestedNegative(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //FSW Positive
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_KP_FSW_POS", "FSW Tested Positive", ReportUtils.map(datimIndicators.fswTestedPositive(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //FSW Negative
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_KP_FSW_NEG", "FSW Tested Negative", ReportUtils.map(datimIndicators.fswTestedNegative(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

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

        //CXCA_TX_NEGATIVE
        EmrReportingUtils.addRow(cohortDsd, "CXCA_TX_NEGATIVE", "Women on ART and Cx treatment screened Negative for cervical cancer", ReportUtils.map(datimIndicators.postTreatmentscreenedCXCANegative(), indParams), datimPMTCTCXCAAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10"));

        //CXCA_TX_POSITIVE
        EmrReportingUtils.addRow(cohortDsd, "CXCA_TX_POSITIVE", "Women on ART and Cx treatment screened Positive for cervical cancer", ReportUtils.map(datimIndicators.postTreatmentscreenedCXCAPositive(), indParams), datimPMTCTCXCAAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10"));

        //CXCA_TX_PRESUMED
        EmrReportingUtils.addRow(cohortDsd, "CXCA_TX_PRESUMED", "Women on ART and Cx treatment screened presumed for cervical cancer", ReportUtils.map(datimIndicators.postTreatmentscreenedCXCAPresumed(), indParams), datimPMTCTCXCAAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10"));

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
        //TX_New
        //Disaggregated by Age / Sex
        EmrReportingUtils.addRow(cohortDsd, "TX_New", "Newly Started ART", ReportUtils.map(datimIndicators.newlyStartedARTByAgeSex(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

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
        EmrReportingUtils.addRow(cohortDsd, "TX_CURR_ONE_MONTH_DRUGS", "One month before next appointment", ReportUtils.map(datimIndicators.currentlyOnARTOneMonthDrugsDispensed(durationMapper("1 Month", "<= 30")), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //two month before next appointment
        EmrReportingUtils.addRow(cohortDsd, "TX_CURR_TWO_MONTHS_DRUGS", "Two month before next appointment", ReportUtils.map(datimIndicators.currentlyOnARTTwoMonthsDrugsDispensed(durationMapper("2 Month", "between 31 and 60")), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //three month before next appointment
        EmrReportingUtils.addRow(cohortDsd, "TX_CURR_THREE_MONTHS_DRUGS", "Three month before next appointment", ReportUtils.map(datimIndicators.currentlyOnARTThreeMonthsDrugsDispensed(durationMapper("3 Month", "between 61 and 90")), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //four month before next appointment
        EmrReportingUtils.addRow(cohortDsd, "TX_CURR_FOUR_MONTHS_DRUGS", "Four month before next appointment", ReportUtils.map(datimIndicators.currentlyOnARTFourMonthsDrugsDispensed(durationMapper("4 Month", "between 91 and 120")), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //five month before next appointment
        EmrReportingUtils.addRow(cohortDsd, "TX_CURR_FIVE_MONTHS_DRUGS", "Five month before next appointment", ReportUtils.map(datimIndicators.currentlyOnARTFiveMonthsDrugsDispensed(durationMapper("5 Month", "between 121 and 150")), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //six month before next appointment
        EmrReportingUtils.addRow(cohortDsd, "TX_CURR_SIX_MONTHS_DRUGS", "Six month before next appointment", ReportUtils.map(datimIndicators.currentlyOnARTSixMonthsDrugsDispensed(durationMapper("6 Month", "between 151 and 180")), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //Over 6 months appointment
        EmrReportingUtils.addRow(cohortDsd, "TX_CURR_OVER_SIX_MONTHS_DRUGS", "Over Six months before next appointment", ReportUtils.map(datimIndicators.currentlyOnARTOverSixMonthsDrugsDispensed(durationMapper("6 Month", "> 180")), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));
//PMTCT_ART

        //Mothers new on ART during current pregnancy
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_ART_New", "Mothers new on ART during current pregnancy", ReportUtils.map(datimIndicators.mothersNewOnARTDuringCurrentPregnancy(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //Mothers already on ART at start of current pregnancy
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_ART_Already", "Number of Mothers Already on ART at the start of current Pregnancy", ReportUtils.map(datimIndicators.mothersAlreadyOnARTAtStartOfCurrentPregnancy(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        // TB_ART Proportion of HIV-positive new and relapsed TB cases New on ART during TB treatment
        EmrReportingUtils.addRow(cohortDsd, "TB_ART_NEW_ON_ART", "TB Patients New on ART ", ReportUtils.map(datimIndicators.newOnARTTBInfected(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        // TB_ART Proportion of HIV-positive new and relapsed TB cases already on ART during TB treatment
        EmrReportingUtils.addRow(cohortDsd, "TB_ART_ALREADY_ON_ART", "TB patients already on ART", ReportUtils.map(datimIndicators.alreadyOnARTTBInfected(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_TB
        //Numerator_new_on_art
        EmrReportingUtils.addRow(cohortDsd, "TX_TB_NUM_NEW_ON_ART", "Starting TB treatment newly started ART", ReportUtils.map(datimIndicators.startingTBTreatmentNewOnART(), indParams), datimTXTBOnART, Arrays.asList("01", "02", "03", "04", "05"));

        //Numerator_Prev_on_art
        EmrReportingUtils.addRow(cohortDsd, "TX_TB_NUM_PREV_ON_ART", "Starting TB treatment previously on ART", ReportUtils.map(datimIndicators.startingTBTreatmentPrevOnART(), indParams), datimTXTBOnART, Arrays.asList("01", "02", "03", "04", "05"));

        //TX_TB(Denominator)
        //TX_TB_NEW_ON_ART_SCREENED_POSITIVE
        EmrReportingUtils.addRow(cohortDsd, "TX_TB_NEW_ON_ART_SCREENED_POSITIVE", "New on ART Screened Positive", ReportUtils.map(datimIndicators.newOnARTScreenedPositive(), indParams), datimTXTBOnART, Arrays.asList("01", "02", "03", "04", "05"));

        //TX_TB_PREVIOUSLY_ON_ART_SCREENED_POSITIVE
        EmrReportingUtils.addRow(cohortDsd, "TX_TB_PREV_ON_ART_SCREENED_POSITIVE", "Previously on ART Screened Positive", ReportUtils.map(datimIndicators.prevOnARTScreenedPositive(), indParams), datimTXTBOnART, Arrays.asList("01", "02", "03", "04", "05"));

        //TX_TB_NEW_ON_ART_SCREENED_NEGATIVE
        EmrReportingUtils.addRow(cohortDsd, "TX_TB_NEW_ON_ART_SCREENED_NEGATIVE", "New on ART Screened Negative", ReportUtils.map(datimIndicators.newOnARTScreenedNegative(), indParams), datimTXTBOnART, Arrays.asList("01", "02", "03", "04", "05"));

        //TX_TB_PREVIOUSLY_ON_ART_SCREENED_NEGATIVE
        EmrReportingUtils.addRow(cohortDsd, "TX_TB_PREV_ON_ART_SCREENED_NEGATIVE", "Previously on ART Screened Negative", ReportUtils.map(datimIndicators.prevOnARTScreenedNegative(), indParams), datimTXTBOnART, Arrays.asList("01", "02", "03", "04", "05"));

        //TX_TB_SPECIMEN_SENT
        cohortDsd.addColumn("TX_TB_SPECIMEN_SENT", "Specimen sent for bacteriologic diagnosis of active TB", ReportUtils.map(datimIndicators.specimenSent(), indParams), "");

        //TX_TB_GeneXpert MTB/RIF assay (with or without other testing)
        cohortDsd.addColumn("TX_TB_GeneXpert", "GeneXpert MTB/RIF assay (with or without other testing)", ReportUtils.map(datimIndicators.geneXpertMTBRIF(), indParams), "");

        //TX_TB_SMEAR_MICROSCOPY_ONLY
        cohortDsd.addColumn( "TX_TB_SMEAR_MICROSCOPY_ONLY", "Smear microscopy only", ReportUtils.map(datimIndicators.smearMicroscopy(), indParams), "");

        //TX_TB_ADDITIONAL_TESTS (other than GeneXpert)
        cohortDsd.addColumn( "TX_TB_ADDITIONAL_TESTS", "Additional test other than GeneXpert", ReportUtils.map(datimIndicators.additionalTBTests(), indParams),"");

        //TX_TB_POSITIVE_RESULT_RETURNED
        cohortDsd.addColumn( "TX_TB_POSITIVE_RESULT_RETURNED", "Positive result returned for bacteriologic diagnosis of active TB", ReportUtils.map(datimIndicators.resultsReturned(), indParams), "");

        //TX_ML
        //TX_ML_DIED Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed)
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_DIED", "ART patients with missed appointment due to death", ReportUtils.map(datimIndicators.txMlDied(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_ML LTFU ON DRUGS <3 MONTHS Number of ART patients with no clinical contact since their last expected contact and have been on drugs for less than 3 months
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_IIT_ONDRUGS_UNDER3MONTHS", "IIT patients who have been on drugs for less than 3 months", ReportUtils.map(datimIndicators.txMLLTFUonDrugsUnder3Months(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_ML LTFU ON DRUGS >3 MONTHS Number of ART patients with no clinical contact since their last expected contact and have been on drugs for more than 3 months
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_IIT_ONDRUGS_OVER3MONTHS", "IIT patients who have been on drugs for more than 3 months", ReportUtils.map(datimIndicators.txMLLTFUonDrugsOver3Months(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_ML_PREV_UNDOCUMENTED_TRF Number of ART patients with no clinical contact since their last expected contact due to Previously undocumented transfer
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_TRF_OUT", "ART patients with missed appointment due to transfer out", ReportUtils.map(datimIndicators.txMLTrfOut(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_ML_STOPPED_TREATMENT Number of ART patients with no clinical contact since their last expected contact because they stopped treatment
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_STOPPED_TREATMENT", "ART patients with missed appointment because they stopped treatment", ReportUtils.map(datimIndicators.txMLStoppedTreatment(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_RTT Number of ART patients with no clinical contact (or ARV drug pick-up) for greater than 30 days since their last expected contact who restarted ARVs within the reporting period
        EmrReportingUtils.addRow(cohortDsd, "TX_RTT", "Number restarted Treatment during the reporting period", ReportUtils.map(datimIndicators.txRTT(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //90-90-90 Viral Suppression
        //TX_PVLS Number of patients on ART with Routine VL results within the past 12 months
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_DENOMINATOR_ROUTINE", "On ART with current VL results", ReportUtils.map(datimIndicators.onARTAndVLLast12MonthsbyAgeSex("(\"ROUTINE\")"), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_PVLS Number of patients on ART with Targeted VL results in the past 12 months
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_DENOMINATOR_TARGETED", "On ART with current VL results", ReportUtils.map(datimIndicators.onARTAndVLLast12MonthsbyAgeSex("(\"STAT\")"), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_PVLS Number of Pregnant or breastfeeding patients on ART with routine VL results within the past 12 months
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_DENOMINATOR_PG/BF_ROUTINE", "Pregnant or Breastfeeding on ART with current VL results", ReportUtils.map(datimIndicators.breastfeedingOnARTVLLast12Months("(\"ROUTINE\")"), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //TX_PVLS Number of Pregnant or breastfeeding patients on ART with targeted VL results within the past 12 months
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_DENOMINATOR_PG/BF_TARGETED", "Pregnant or Breastfeeding on ART with current VL results", ReportUtils.map(datimIndicators.breastfeedingOnARTVLLast12Months("(\"STAT\")"), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //TX_PVLS Number of PWID KPs on ART with Routine VL results within the past 12 months
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_KP_PWID_ROUTINE", "PWID on ART with current VL results", ReportUtils.map(datimIndicators.kpWithVLLast12Months("(\"ROUTINE\")",mapKPType("PWID", PWID_CONCEPT)), indParams), "");

        //TX_PVLS Number of MSMS KPs on ART with Routine VL results within the past 12 months
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_KP_MSM_ROUTINE", "MSM on ART with current VL results", ReportUtils.map(datimIndicators.kpWithVLLast12Months("(\"ROUTINE\")",mapKPType("MSM", MSM_CONCEPT)), indParams), "");

        //TX_PVLS Number of TG KPs on ART with Routine VL results within the past 12 months
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_KP_TG_ROUTINE", "Transgender on ART with current VL results", ReportUtils.map(datimIndicators.kpWithVLLast12Months("(\"ROUTINE\")",mapKPType("TG", TG_CONCEPT)), indParams), "");

        //TX_PVLS Number of FSW KPs on ART with Routine VL results within the past 12 months
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_KP_FSW_ROUTINE", "FSW on ART with current VL results", ReportUtils.map(datimIndicators.kpWithVLLast12Months("(\"ROUTINE\")",mapKPType("FSW", FSW_CONCEPT)), indParams), "");

        //TX_PVLS Number of PWID KPs on ART with Targeted VL results within the past 12 months
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_KP_PWID_TARGETED", "PWID on ART with current VL results", ReportUtils.map(datimIndicators.kpWithVLLast12Months("(\"STAT\")",mapKPType("PWID", PWID_CONCEPT)), indParams), "");

        //TX_PVLS Number of MSM KPs on ART with Targeted VL results within the past 12 months
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_KP_MSM_TARGETED", "MSM on ART with current VL results", ReportUtils.map(datimIndicators.kpWithVLLast12Months("(\"STAT\")",mapKPType("MSM", MSM_CONCEPT)), indParams), "");

        //TX_PVLS Number of TG KPs on ART with VL results within the past 12 months
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_KP_TG_TARGETED", "Transgender on ART with current VL results", ReportUtils.map(datimIndicators.kpWithVLLast12Months("(\"STAT\")",mapKPType("TG", TG_CONCEPT)), indParams), "");

        //TX_PVLS Number of FSW KPs on ART with Targeted VL results within the past 12 months
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_KP_FSW_TARGETED", "FSW on ART with current VL results", ReportUtils.map(datimIndicators.kpWithVLLast12Months("(\"STAT\")",mapKPType("FSW", FSW_CONCEPT)), indParams), "");

        //TX_PVLS Number of adults and pediatric patients on ART with suppressed routine viral load results (<1,000 copies/ml) within the past 12 months disaggregated by Age/Sex
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_SUPP_ROUTINE", "On ART with current suppressed VL results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.onARTSuppVLAgeSex("(\"ROUTINE\")"), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_PVLS Number of adults and pediatric patients on ART with suppressed targeted viral load results (<1,000 copies/ml) within the past 12 months disaggregated by Age/Sex
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_SUPP_TARGETED", "On ART with current suppressed VL results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.onARTSuppVLAgeSex("(\"STAT\")"), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //TX_PVLS Number pregnant or breastfeeding patients on ART with suppressed routine viral load results (<1,000 copies/ml) within the past 12 months
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_SUPP_PG/BF_ROUTINE", "Pregnant or Breastfeeding on ART with current suppressed VL results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.pregnantAndBFOnARTWithSuppressedVLLast12Months("(\"ROUTINE\")"), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //TX_PVLS Number pregnant or breastfeeding patients on ART with suppressed targeted viral load results (<1,000 copies/ml) within the past 12 months
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_SUPP_PG/BF_TARGETED", "Pregnant or Breastfeeding on ART with current suppressed VL results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.pregnantAndBFOnARTWithSuppressedVLLast12Months("(\"STAT\")"), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months. Disaggregated by Pregnant / undocumented
        cohortDsd.addColumn("TX_PVLS_SUPP_KP_PWID_ROUTINE", "PWID on ART with current suppressed VL results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.kpOnARTSuppVLLast12Months("(\"ROUTINE\")",mapKPType("PWID", PWID_CONCEPT)), indParams), "");

        //TX_PVLS Number of MSM KPs on ART with suppressed routine viral load results (<1,000 copies/ml) within the past 12 months
        cohortDsd.addColumn("TX_PVLS_SUPP_KP_MSM_ROUTINE", "MSM on ART with current suppressed VL results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.kpOnARTSuppVLLast12Months("(\"ROUTINE\")",mapKPType("MSM", MSM_CONCEPT)), indParams), "");

        //TX_PVLS Number of Transgender KPs on ART with suppressed routine viral load results (<1,000 copies/ml) within the past 12 months
        cohortDsd.addColumn("TX_PVLS_SUPP_KP_TG_ROUTINE", "Transgender on ART with current suppressed VL results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.kpOnARTSuppVLLast12Months("(\"ROUTINE\")",mapKPType("TG", TG_CONCEPT)), indParams), "");

        //TX_PVLS Number of FSW KPs on ART with suppressed routine viral load results (<1,000 copies/ml) within the past 12 months
        cohortDsd.addColumn("TX_PVLS_SUPP_KP_FSW_ROUTINE", "FSW on ART with current suppressed VL results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.kpOnARTSuppVLLast12Months("(\"ROUTINE\")",mapKPType("FSW", FSW_CONCEPT)), indParams), "");

        //TX_PVLS Number of PWID KPs on ART with suppressed targeted viral load results (<1,000 copies/ml) within the past 12 months
        cohortDsd.addColumn("TX_PVLS_SUPP_KP_PWID_TARGETED", "PWID on ART with current suppressed VL results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.kpOnARTSuppVLLast12Months("(\"STAT\")",mapKPType("PWID", PWID_CONCEPT)), indParams), "");

        //TX_PVLS Number of MSM KPs on ART with suppressed targeted viral load results (<1,000 copies/ml) within the past 12 months
        cohortDsd.addColumn("TX_PVLS_SUPP_KP_MSM_TARGETED", "MSM on ART with current suppressed VL results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.kpOnARTSuppVLLast12Months("(\"STAT\")",mapKPType("MSM", MSM_CONCEPT)), indParams), "");

        //TX_PVLS Number of Transgender KPs on ART with targeted suppressed viral load results (<1,000 copies/ml) within the past 12 months
        cohortDsd.addColumn("TX_PVLS_SUPP_KP_TG_TARGETED", "Transgender on ART with current suppressed VL results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.kpOnARTSuppVLLast12Months("(\"STAT\")",mapKPType("TG", TG_CONCEPT)), indParams), "");

        //TX_PVLS Number of FSW KPs on ART with targeted suppressed VL results (<1,000 copies/ml) within the past 12 months
        cohortDsd.addColumn("TX_PVLS_SUPP_KP_FSW_TARGETED", "FSW on ART with current suppressed VL results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.kpOnARTSuppVLLast12Months("(\"STAT\")",mapKPType("FSW", FSW_CONCEPT)), indParams), "");

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
        //Tested Negative VCT
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_VCT_Negative", "Tested Negative VCT", ReportUtils.map(datimIndicators.testedNegativeVCT(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //Tested Positive VCT
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_VCT_Positive", "Tested Positive VCT", ReportUtils.map(datimIndicators.testedPositiveVCT(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "25"));

        //3. Malnutrition Clinic
        //Tested Negative Malnutrition Clinic
        cohortDsd.addColumn("HTS_TST_Malnutrition_Negative", "Tested Negative Malnutrition Clinic", ReportUtils.map(datimIndicators.testedNegativeMalnutritionClinic(), indParams), "");

        //Tested Positive Malnutrition Clinic
        cohortDsd.addColumn("HTS_TST_Malnutrition_Positive", "Tested Positive Malnutrition Clinic", ReportUtils.map(datimIndicators.testedPositiveMalnutritionClinic(), indParams), "");

        //4. Paediatric Clinics
        //Tested Negative Paediatric services
        cohortDsd.addColumn("HTS_TST_Paediatric_Negative", "Tested Negative Paediatric services", ReportUtils.map(datimIndicators.testedNegativePaediatricServices(), indParams), "");

        //Tested Positive Paediatric services
        cohortDsd.addColumn("HTS_TST_Paediatric_Positive", "Tested Positive Paediatric Services", ReportUtils.map(datimIndicators.testedPositivePaediatricServices(), indParams), "");

        //5. TB Clinics

        //Tested Negative TB Clinic
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_TB_Negative", "Tested Negative TB Clinic", ReportUtils.map(datimIndicators.testedNegativeTBClinic(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //Tested Positive TB Clinic
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_TB_Positive", "Tested Positive TB Clinic", ReportUtils.map(datimIndicators.testedPositiveTBClinic(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //6.PMTCT_ANC-1 Only
        //Tested Negative PMTCT services ANC-1 only
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_PMTCT_ANC1_Negative", "Tested Negative PMTCT at 1st ANC", ReportUtils.map(datimIndicators.testedNegativePMTCTANC1(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //Tested Positive PMTCT services ANC-1 only
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_PMTCT_ANC1_Positive", "Tested Positive PMTCT at 1st ANC", ReportUtils.map(datimIndicators.testedPositivePMTCTANC1(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //7.PMTCT [Post ANC1, Preg/L&D/BF]
        //Tested Negative PMTCT services Post ANC-1 (including labour and delivery and BF)
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_PMTCT_POSTANC1_Negative", "Tested Negative PMTCT Post ANC-1 (Incl L&D,BF)", ReportUtils.map(datimIndicators.testedNegativePMTCTPostANC1(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //Tested Positive PMTCT services Post ANC-1 (including labour and delivery and BF)
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_PMTCT_POSTANC1_Positive", "Tested Positive PMTCT Post ANC-1 (Incl L&D,BF)", ReportUtils.map(datimIndicators.testedPositivePMTCTPostANC1(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //8.STI
        //9. Inpatient
        //Tested Negative Inpatient Services
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Inpatient_Negative", "Tested Negative Inpatient Services", ReportUtils.map(datimIndicators.testedNegativeInpatientServices(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //Tested Positive Inpatient Services
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Inpatient_Positive", "Tested Positive Inpatient Services", ReportUtils.map(datimIndicators.testedPositiveInpatientServices(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //10. Emergency Ward
        //11. VMMC

        //12. Other
        //Tested Negative Other
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Other_Negative", "Tested Negative Other", ReportUtils.map(datimIndicators.testedNegativeOther(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //Tested Positive Other
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Other_Positive", "Tested Positive Other", ReportUtils.map(datimIndicators.testedPositiveOther(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        // Number of people newly enrolled on Prep
        EmrReportingUtils.addRow(cohortDsd, "PrEP_NEWLY_ENROLLED", "Number of people newly enrolled on Prep", ReportUtils.map(datimIndicators.newlyEnrolledInPrEP(), indParams), datimPrEPNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        // Number of people currently enrolled on Prep
        EmrReportingUtils.addRow(cohortDsd, "PrEP_CURR_ENROLLED", "Number of people currently enrolled on Prep", ReportUtils.map(datimIndicators.currentlyEnrolledInPrEP(), indParams), datimPrEPNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        // Proportion of ART patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy
        EmrReportingUtils.addRow(cohortDsd, "TB_PREV_ENROLLED_COMPLETED", "Proportion of ART patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy", ReportUtils.map(datimIndicators.previouslyOnIPTCompleted(), indParams), datimPrEPNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        // Proportion of Newly enrolled on ART patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy
        EmrReportingUtils.addRow(cohortDsd, "TB_PREV_NEWLY_ENROLLED_ART_COMPLETED_TPT", "Proportion of Newly enrolled on ART patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy", ReportUtils.map(datimIndicators.newARTpreviouslyOnIPTCompleted(), indParams),  datimTXTBOnART, Arrays.asList("01", "02", "03", "04", "05"));

        // Proportion of Previously enrolled on ART patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy
        EmrReportingUtils.addRow(cohortDsd, "TB_PREV_ENROLLED_ART_COMPLETED_TPT", "Proportion of Previously enrolled on ART patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy", ReportUtils.map(datimIndicators.previouslyARTandIPTCompleted(), indParams),  datimTXTBOnART, Arrays.asList("01", "02", "03", "04", "05"));

        //3. KP_PREV
        EmrReportingUtils.addRow(cohortDsd, "KP_PREV_FSW", "Received care for the first time this year",
                ReportUtils.map(datimIndicators.kpPrev("(\"FSW\")"), indParams), kpAgeDisaggregation,
                Arrays.asList("01", "02", "03", "04", "05"));
        EmrReportingUtils.addRow(cohortDsd, "KP_PREV_MSM", "Received care for the first time this year",
                ReportUtils.map(datimIndicators.kpPrev("(\"MSM\")"), indParams), kpAgeDisaggregation,
                Arrays.asList("01", "02", "03", "04", "05"));
        EmrReportingUtils.addRow(cohortDsd, "KP_PREV_MSW", "Received care for the first time this year",
                ReportUtils.map(datimIndicators.kpPrev("(\"MSW\")"), indParams), kpAgeDisaggregation,
                Arrays.asList("01", "02", "03", "04", "05"));
        EmrReportingUtils.addRow(cohortDsd, "KP_PREV_PWUD", "Received care for the first time this year",
                ReportUtils.map(datimIndicators.kpPrev("(\"PWUD\")"), indParams), kpAgeDisaggregation,
                Arrays.asList("01", "02", "03", "04", "05"));
        EmrReportingUtils.addRow(cohortDsd, "KP_PREV_PWID", "Received care for the first time this year",
                ReportUtils.map(datimIndicators.kpPrev("(\"PWID\")"), indParams), kpAgeGenderDisaggregation,
                Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09"));
        EmrReportingUtils.addRow(cohortDsd, "KP_PREV_TG_SW", "Received care for the first time this year",
                ReportUtils.map(datimIndicators.kpPrev("TRANSGENDER_SW"), indParams), kpAgeDisaggregation,
                Arrays.asList("01", "02", "03", "04", "05"));
        EmrReportingUtils.addRow(cohortDsd, "KP_PREV_TG_NOT_SW", "Received care for the first time this year",
                ReportUtils.map(datimIndicators.kpPrev("TRANSGENDER_NOT_SW"), indParams), kpAgeDisaggregation,
                Arrays.asList("01", "02", "03", "04", "05"));

        /*GEND_GBV
        Number of people receiving post-gender-based violence (GBV) clinical care based on the minimum package*/
        //1. Sexual violence (post-rape care)
        EmrReportingUtils.addRow(cohortDsd, "GEND_GBV_SEXUAL_VIOLENCE", "Received Sexual violence (post-rape) care", ReportUtils.map(datimIndicators.sexualGBV(), indParams), datimGBVDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21"));

        //2. Physical and/or emotional violence (other Post-GBV care)
        EmrReportingUtils.addRow(cohortDsd, "GEND_GBV_PHY_EMOTIONAL_VIOLENCE", "Received physical and emotional violence care", ReportUtils.map(datimIndicators.physicalEmotionalGBV(), indParams), datimGBVDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21"));

        //3. Number of People Receiving Post-exposure prophylaxis (PEP) Services. Disaggregate of the Sexual Violence Service Type
        EmrReportingUtils.addRow(cohortDsd, "GEND_GBV_PEP", "Received Post-exposure prophylaxis (PEP) Services", ReportUtils.map(datimIndicators.receivedPEP(), indParams), datimGBVDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21"));
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


