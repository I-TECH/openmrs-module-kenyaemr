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

import java.util.ArrayList;
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
    static final int PRISONERS_CLOSED_SETTINGS_CONCEPT = 162277;
    static final int HIV_DISEASE_RESULTING_IN_TB_CONCEPT = 163324;
    static final int HIV_DISEASE_RESULTING_IN_CANCER = 116030;
    static final int  HIV_DISEASE_RESULTING_IN_INFECTIOUS_PARASITIC_DISEASE_CONCEPT = 160159;
    static final int OTHER_HIV_DISEASE_CONCEPT = 160158;
    static final int OTHER_NATURAL_CAUSES_CONCEPT = 133478;
    static final int NON_NATURAL_CAUSES_CONCEPT = 123812;
    static final int UNKNOWN_CAUSE_CONCEPT = 142917;
    static final int COVID_19_CONCEPT = 165609;

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
        ColumnParameters m0_to60Days = new ColumnParameters(null, "0-60", "gender=M|age=0-60");
        ColumnParameters m2_to12Months = new ColumnParameters(null, "2-12", "gender=M|age=2-12");

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
          //MER 2.6 additional dissagregations
        ColumnParameters f50_to54 = new ColumnParameters(null, "50-54, Female", "gender=F|age=50-54");
        ColumnParameters m50_to54 = new ColumnParameters(null, "50-54, Male", "gender=M|age=50-54");
        ColumnParameters f55_to59 = new ColumnParameters(null, "55-59, Female", "gender=F|age=55-59");
        ColumnParameters m55_to59 = new ColumnParameters(null, "55-59, Male", "gender=M|age=55-59");
        ColumnParameters f60_to64 = new ColumnParameters(null, "60-64, Female", "gender=F|age=60-64");
        ColumnParameters m60_to64 = new ColumnParameters(null, "60-64, Male", "gender=M|age=60-64");
        ColumnParameters fAbove65 = new ColumnParameters(null, "65+, Female", "gender=F|age=65+");
        ColumnParameters mAbove65 = new ColumnParameters(null, "65+, Male", "gender=M|age=65+");

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

        List<ColumnParameters> datimTxCurrDisaggregation =
                Arrays.asList(fInfant, mInfant, f1_to4, m1_to4, f5_to9, m5_to9, f10_to14, m10_to14, f15_to19, m15_to19, f20_to24, m20_to24,
                        f25_to29, m25_to29, f30_to34, m30_to34, f35_to39, m35_to39, f40_to44, m40_to44, f45_to49, m45_to49,
                        f50_to54, m50_to54, f55_to59, m55_to59, f60_to64, m60_to64, fAbove65, mAbove65, colTotal);

        List<ColumnParameters> datimNewAgeDisaggregation =
                Arrays.asList(fInfant, mInfant, f1_to4, m1_to4, f5_to9, m5_to9, f10_to14, m10_to14, f15_to19, m15_to19, f20_to24, m20_to24,
                        f25_to29, m25_to29, f30_to34, m30_to34, f35_to39, m35_to39, f40_to44, m40_to44, f45_to49, m45_to49, fAbove50, mAbove50, colTotal);

        List<ColumnParameters> datimExpandedAgeDisaggregation =
                Arrays.asList(fInfant, mInfant, f1_to4, m1_to4, f5_to9, m5_to9, f10_to14, m10_to14, f15_to19, m15_to19, f20_to24, m20_to24,
                        f25_to29, m25_to29, f30_to34, m30_to34, f35_to39, m35_to39, f40_to44, m40_to44, f45_to49, m45_to49, fAbove50, mAbove50,
                        f50_to54, m50_to54, f55_to59, m55_to59, f60_to64, m60_to64, fAbove65, mAbove65,colTotal);

        List<ColumnParameters> datimAgeDisaggregation =
                Arrays.asList(fInfant, mInfant, f1_to4, m1_to4, f5_to9, m5_to9, f10_to14, m10_to14, f15_to19, m15_to19, f20_to24, m20_to24,
                        f25_to29, m25_to29, f30_to34, m30_to34, f35_to39, m35_to39, f40_to44, m40_to44, f45_to49, m45_to49,
                        f50_to54, m50_to54, f55_to59, m55_to59, f60_to64, m60_to64, fAbove65, mAbove65,colTotal);

        List<ColumnParameters> datimPMTCTANCAgeDisaggregation =
                Arrays.asList(fUnder10, f10_to14, f15_to19, f20_to24, f25_to29, f30_to34, f35_to39, f40_to44, f45_to49, fAbove50, colTotal);

        List<ColumnParameters> datimPMTCTARTAgeDisaggregation =
                Arrays.asList(fUnder10, f10_to14, f15_to19, f20_to24, f25_to29, f30_to34, f35_to39, f40_to44, f45_to49, f50_to54, f55_to59, f60_to64, fAbove65, colTotal);

        List<ColumnParameters> datimPMTCTCXCAExpandedAgeDisaggregation =
                Arrays.asList(f15_to19, f20_to24, f25_to29, f30_to34, f35_to39, f40_to44, f45_to49, f50_to54, f55_to59, f60_to64, fAbove65, colTotal);

        List<ColumnParameters> datim5To65PlusAgeDisaggregation = Arrays.asList(f5_to9, m5_to9, f10_to14, m10_to14, f15_to19, m15_to19, f20_to24, m20_to24,
                f25_to29, m25_to29, f30_to34, m30_to34, f35_to39, m35_to39, f40_to44, m40_to44, f45_to49, m45_to49,
                f50_to54, m50_to54, f55_to59, m55_to59, f60_to64, m60_to64, fAbove65, mAbove65,colTotal);

        List<ColumnParameters> datimPrEPNewAgeDisaggregation =
                Arrays.asList(f15_to19, m15_to19, f20_to24, m20_to24,
                        f25_to29, m25_to29, f30_to34, m30_to34, f35_to39, m35_to39, f40_to44, m40_to44, f45_to49, m45_to49, fAbove50, mAbove50, colTotal);

        List<ColumnParameters> datimTXTBOnART =
                Arrays.asList(fUnder15, f15AndAbove, mUnder15, m15AndAbove,colTotal);

        List<ColumnParameters>  kpAgeDisaggregation = Arrays.asList(below_15, kp15_to_19, kp20_to_24, kp25_and_above,colTotal);
        List<ColumnParameters> ppAgeGenderDisaggregation = Arrays.asList(f10_to14, m10_to14,f15_to19, m15_to19, f20_to24, m20_to24,
                f25_to29, m25_to29, f30_to34, m30_to34, f35_to39, m35_to39, f40_to44, m40_to44, f45_to49, m45_to49, fAbove50, mAbove50, colTotal);

        List<ColumnParameters> datimGBVDisaggregation =
                Arrays.asList(fUnder10,mUnder10,f10_to14, m10_to14,f15_to19, m15_to19, f20_to24, m20_to24,
                        f25_to29, m25_to29, f30_to34, m30_to34, f35_to39, m35_to39, f40_to44, m40_to44, f45_to49, m45_to49, fAbove50, mAbove50, colTotal);

        //Patient contacts disagggregations
        List<ColumnParameters> contactAgeSexDisaggregation = Arrays.asList(contacts_under_15_f, contacts_under_15_m, contacts_15_and_above_f, contacts_15_and_above_m, colTotal);
        List<ColumnParameters> contactAgeSexFineDisaggregation =
                Arrays.asList(fCInfant, mCInfant, fC1_to4, mC1_to4, fC5_to9, mC5_to9, fC10_to14, mC10_to14, fC15_to19, mC15_to19, fC20_to24, mC20_to24,
                        fC25_to29, mC25_to29, fC30_to34, mC30_to34, fC35_to39, mC35_to39, fC40_to44, mC40_to44, fC45_to49, mC45_to49, fCAbove50, mCAbove50, colTotal);
        List<ColumnParameters> contactAgeSexDocumentedNegativeDisaggregation = Arrays.asList(fC1_to4, mC1_to4, fC5_to9, mC5_to9, fC10_to14, mC10_to14, colTotal);

        List<ColumnParameters> pediatricAgeDisaggregation = Arrays.asList(fInfant, mInfant, f1_to4, m1_to4, colTotal);
        /**
         * VMMC disaggregations
         */
        List<ColumnParameters> datimVMMCStandardDisaggregation = Arrays.asList(m0_to60Days,m2_to12Months,m1_to4,m5_to9, m10_to14,m15_to19, m20_to24,
                       m25_to29, m30_to34, m35_to39, m40_to44, m45_to49, mAbove50, colTotal);
        List<ColumnParameters> datimVMMCHTSStatusDisaggregation = Arrays.asList(mInfant,m1_to4,m5_to9, m10_to14,m15_to19, m20_to24,
                m25_to29, m30_to34, m35_to39, m40_to44, m45_to49, mAbove50, colTotal);
        //End of patient contact Disaggregations
        ArrayList<String> priorityPopulation = new ArrayList<String>(Arrays.asList("\"Fisher Folk\"","\"Truck Driver\"","\"Adolescent and Young Girls\"","\"Prisoner\""));

        String indParams = "startDate=${startDate},endDate=${endDate}";
        String endDateParams = "endDate=${endDate}";

        //Prevention Indicators
        //VMMC_CIRC
        EmrReportingUtils.addRow(cohortDsd, "VMMC_CIRC", "Number of males circumcised", ReportUtils.map(datimIndicators.malesCircumcised(), indParams), datimVMMCStandardDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14"));

        EmrReportingUtils.addRow(cohortDsd, "VMMC_CIRC_HIV_POSITIVE", "Number of males circumcised and tested HIV positive at VMMC site", ReportUtils.map(datimIndicators.malesCircumcisedTestedHIVPositive(), indParams), datimVMMCHTSStatusDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13"));
        EmrReportingUtils.addRow(cohortDsd, "VMMC_CIRC_HIV_NEGATIVE", "Number of males circumcised and tested HIV negative at VMMC site", ReportUtils.map(datimIndicators.malesCircumcisedTestedHIVNegative(), indParams), datimVMMCHTSStatusDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13"));
        EmrReportingUtils.addRow(cohortDsd, "VMMC_CIRC_HIV_INDETERMINATE", "Number of males circumcised with indeterminate HIV result at VMMC site or not tested at VMMC site", ReportUtils.map(datimIndicators.malesCircumcisedIndeterminateHIVResult(), indParams), datimVMMCHTSStatusDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13"));
        cohortDsd.addColumn("VMMC_CIRC_SURGICAL", "Number of males circumcised through surgical procedure", ReportUtils.map(datimIndicators.vmmcSurgical(), indParams), "");
        cohortDsd.addColumn("VMMC_CIRC_DEVICE-BASED", "Number of males circumcised using device", ReportUtils.map(datimIndicators.vmmcDevice(), indParams), "");

        cohortDsd.addColumn("VMMC_CIRC_SURGICAL_FOLLOWED_UP_WITHIN_14_DAYS", "Number of males circumcised through surgical procedure and followed up within 14 days", ReportUtils.map(datimIndicators.vmmcSurgicalFollowupWithin14Days(), indParams), "");
        cohortDsd.addColumn("VMMC_CIRC_SURGICAL_NOT_FOLLOWED_WITHIN_14_DAYS", "Number of males circumcised through surgical procedure and did not follow up within 14 days", ReportUtils.map(datimIndicators.vmmcSurgicalNoFollowupWithin14Days(), indParams), "");
        cohortDsd.addColumn("VMMC_CIRC_DEVICE-BASED_FOLLOWED_WITHIN_14_DAYS", "Number of males circumcised using device and followed up within 14 days", ReportUtils.map(datimIndicators.vmmcDeviceFollowupWithin14Days(), indParams), "");
        cohortDsd.addColumn("VMMC_CIRC_DEVICE-BASED_NOT_FOLLOWED_WITHIN_14_DAYS", "Number of males circumcised using device and did not follow up within 14 days", ReportUtils.map(datimIndicators.vmmcDeviceNoFollowupWithin14Days(), indParams), "");

        // Number of people newly enrolled on PrEP
        EmrReportingUtils.addRow(cohortDsd, "PrEP_NEW", "Number of people newly enrolled on PrEP", ReportUtils.map(datimIndicators.newlyEnrolledInPrEP(), indParams), datimPrEPNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17"));

        // Number of KPs newly enrolled on PrEP
        cohortDsd.addColumn( "PrEP_NEW_PWID", "Number of PWIDs newly enrolled on PrEP", ReportUtils.map(datimIndicators.newlyEnrolledInPrEPKP(PWID_CONCEPT), indParams),"");
        cohortDsd.addColumn( "PrEP_NEW_MSM", "Number of MSMs newly enrolled on PrEP", ReportUtils.map(datimIndicators.newlyEnrolledInPrEPKP(MSM_CONCEPT), indParams), "");
        cohortDsd.addColumn("PrEP_NEW_TG", "Number of Transgenders newly enrolled on PrEP", ReportUtils.map(datimIndicators.newlyEnrolledInPrEPKP(TG_CONCEPT), indParams),"");
        cohortDsd.addColumn("PrEP_NEW_FSW", "Number of FSWs newly enrolled on PrEP", ReportUtils.map(datimIndicators.newlyEnrolledInPrEPKP(FSW_CONCEPT), indParams),"");
        cohortDsd.addColumn("PrEP_NEW_PRISONS_CLOSED_SETTINGS", "Number of prisoners and people in closed settings newly enrolled on PrEP", ReportUtils.map(datimIndicators.newlyEnrolledInPrEPKP(PRISONERS_CLOSED_SETTINGS_CONCEPT), indParams), "");
        cohortDsd.addColumn("PrEP_NEW_PREGNANT", "Number of pregnant women newly enrolled on PrEP", ReportUtils.map(datimIndicators.newlyEnrolledOnPrEPPregnant(), indParams), "");
        cohortDsd.addColumn("PrEP_NEW_BREASTFEEDING", "Number of breastfeeding women newly enrolled on PrEP", ReportUtils.map(datimIndicators.newlyEnrolledInPrEPBreastFeeding(), indParams), "");
        cohortDsd.addColumn("PrEP_NEW_ORAL", "Number of people newly enrolled on Oral PrEP", ReportUtils.map(datimIndicators.newlyEnrolledOnOralPrEP(), indParams), "");
        cohortDsd.addColumn("PrEP_NEW_INJECTABLE", "Number of people newly enrolled on CAB-LA Injectable PrEP", ReportUtils.map(datimIndicators.newlyEnrolledOnCABLAInjectablePrEP(), indParams), "");
        cohortDsd.addColumn("PrEP_NEW_OTHER", "Number of people newly enrolled on Other forms of PrEP", ReportUtils.map(datimIndicators.newlyEnrolledOnOtherPrEP(), indParams), "");
        //PrEP_CT
        EmrReportingUtils.addRow(cohortDsd, "PrEP_CT", "People who returned for PrEP follow-up or re-initiation", ReportUtils.map(datimIndicators.prepCT(), indParams), datimPrEPNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17"));

        //Returned for PrEP and tested HIV Positive
        cohortDsd.addColumn("PrEP_CT_HIV_POS", "Returned for PrEP and tested HIV positive", ReportUtils.map(datimIndicators.prepCTByHIVPositiveStatus(), indParams), "");

        //Returned for PrEP and tested HIV Negative
        cohortDsd.addColumn("PrEP_CT_HIV_NEG", "Returned for PrEP and tested HIV Negative", ReportUtils.map(datimIndicators.prepCTByHIVNegativeStatus(), indParams), "");

        //Returned for PrEP and not tested for HIV during the visit
        cohortDsd.addColumn("PrEP_CT_HIV_OTHER", "Returned for PrEP and NOT tested for HIV during this visit", ReportUtils.map(datimIndicators.prepCTNotTestedForHIV(), indParams), "");

        //PWID KPs who Returned for PrEP
        cohortDsd.addColumn("PrEP_CT_PWID", "PWID KPs who Returned for PrEP", ReportUtils.map(datimIndicators.prepCTKP(PWID_CONCEPT), indParams), "");
        //MSM KPs who Returned for PrEP
        cohortDsd.addColumn("PrEP_CT_MSM", "MSM KPs who Returned for PrEP", ReportUtils.map(datimIndicators.prepCTKP(MSM_CONCEPT), indParams), "");
        //Transgender KPs who Returned for PrEP
        cohortDsd.addColumn("PrEP_CT_TG", "TG KPs who Returned for PrEP", ReportUtils.map(datimIndicators.prepCTKP(TG_CONCEPT), indParams), "");
        //FSW KPs who Returned for PrEP
        cohortDsd.addColumn("PrEP_CT_FSW", "FSW KPs who Returned for PrEP", ReportUtils.map(datimIndicators.prepCTKP(FSW_CONCEPT), indParams), "");
        //Prisoners and people in closed settings KPs who Returned for PrEP
        cohortDsd.addColumn("PrEP_CT_PRISONS_CLOSED_SETTINGS", "Prisoners and closed settings KPs who Returned for PrEP", ReportUtils.map(datimIndicators.prepCTKP(PRISONERS_CLOSED_SETTINGS_CONCEPT), indParams), "");

        //Returned for PrEP and pregnant
        cohortDsd.addColumn("PrEP_CT_PREG", "Returned for PrEP and pregnant", ReportUtils.map(datimIndicators.prepCTPregnant(), indParams), "");

        //Returned for PrEP and breastfeeding
        cohortDsd.addColumn("PrEP_CT_BF", "Returned for PrEP while breastfeeding", ReportUtils.map(datimIndicators.prepCTBreastfeeding(), indParams), "");

        cohortDsd.addColumn("PrEP_CT_ORAL", "Returned and on Oral PrEP", ReportUtils.map(datimIndicators.prepCTOnOralPrEP(), indParams), "");
        cohortDsd.addColumn("PrEP_CT_INJECTABLE", "Returned and on CAB-LA Injectable PrEP", ReportUtils.map(datimIndicators.prepCTOnCABLAInjectablePrEP(), indParams), "");
        cohortDsd.addColumn("PrEP_CT_OTHER", "Returned and on Other forms of PrEP", ReportUtils.map(datimIndicators.prepCTOnOtherPrEP(), indParams), "");

        //KP_PREV by KP type
        cohortDsd.addColumn("KP_PREV_MSM", "Reached with individual and/or small group-level HIV prevention interventions designed for the target population",ReportUtils.map(datimIndicators.kpPrev("MSM"), indParams),"");
        cohortDsd.addColumn("KP_PREV_TG", "Reached with individual and/or small group-level HIV prevention interventions designed for the target population",ReportUtils.map(datimIndicators.kpPrev("Transgender"), indParams), "");
        cohortDsd.addColumn("KP_PREV_FSW", "Reached with individual and/or small group-level HIV prevention interventions designed for the target population",ReportUtils.map(datimIndicators.kpPrev("FSW"), indParams), "");
        cohortDsd.addColumn("KP_PREV_PWID", "Reached with individual and/or small group-level HIV prevention interventions designed for the target population",ReportUtils.map(datimIndicators.kpPrev("PWID"), indParams),"");
        cohortDsd.addColumn("KP_PREV_PRISONS_CLOSED_SETTINGS", "Reached with individual and/or small group-level HIV prevention interventions designed for the target population",ReportUtils.map(datimIndicators.kpPrev("People in prison and other closed settings"), indParams), "");

        //KP_PREV by KP type by Testing services

        //Known Positive
        cohortDsd.addColumn("KP_PREV_MSM_KNOWN_POSITVE", "Known Positive",ReportUtils.map(datimIndicators.kpPrevKnownPositive("MSM"), indParams),"");
        cohortDsd.addColumn("KP_PREV_TG_KNOWN_POSITVE", "Known Positive",ReportUtils.map(datimIndicators.kpPrevKnownPositive("Transgender"), indParams), "");
        cohortDsd.addColumn("KP_PREV_FSW_KNOWN_POSITVE", "Known Positive",ReportUtils.map(datimIndicators.kpPrevKnownPositive("FSW"), indParams), "");
        cohortDsd.addColumn("KP_PREV_PWID_KNOWN_POSITVE", "Known Positive",ReportUtils.map(datimIndicators.kpPrevKnownPositive("PWID"), indParams),"");
        cohortDsd.addColumn("KP_PREV_PRISONS_CLOSED_SETTINGS_KNOWN_POSITVE", "Known Positive",ReportUtils.map(datimIndicators.kpPrevKnownPositive("People in prison and other closed settings"), indParams), "");

        //Newly tested and/or referred for HTS
        cohortDsd.addColumn("KP_PREV_MSM_NEWLY_TESTED_REFERRED", "Newly tested and/or referred for HTS",ReportUtils.map(datimIndicators.kpPrevNewlyTestedOrReferred("MSM"), indParams),"");
        cohortDsd.addColumn("KP_PREV_TG_NEWLY_TESTED_REFERRED", "Newly tested and/or referred for HTS",ReportUtils.map(datimIndicators.kpPrevNewlyTestedOrReferred("Transgender"), indParams), "");
        cohortDsd.addColumn("KP_PREV_FSW_NEWLY_TESTED_REFERRED", "Newly tested and/or referred for HTS",ReportUtils.map(datimIndicators.kpPrevNewlyTestedOrReferred("FSW"), indParams), "");
        cohortDsd.addColumn("KP_PREV_PWID_NEWLY_TESTED_REFERRED", "Newly tested and/or referred for HTS",ReportUtils.map(datimIndicators.kpPrevNewlyTestedOrReferred("PWID"), indParams),"");
        cohortDsd.addColumn("KP_PREV_PRISONS_CLOSED_SETTINGS_NEWLY_TESTED_REFERRED", "Newly tested and/or referred for HTS",ReportUtils.map(datimIndicators.kpPrevNewlyTestedOrReferred("People in prison and other closed settings"), indParams), "");
        //Declined testing and/or referral
        cohortDsd.addColumn("KP_PREV_MSM_DECLINED_HTS", "Declined testing and/or referral",ReportUtils.map(datimIndicators.kpPrevDeclinedTesting("MSM"), indParams),"");
        cohortDsd.addColumn("KP_PREV_TG_DECLINED_HTS", "Declined testing and/or referral",ReportUtils.map(datimIndicators.kpPrevDeclinedTesting("Transgender"), indParams), "");
        cohortDsd.addColumn("KP_PREV_FSW_DECLINED_HTS", "Declined testing and/or referral",ReportUtils.map(datimIndicators.kpPrevDeclinedTesting("FSW"), indParams), "");
        cohortDsd.addColumn("KP_PREV_PWID_DECLINED_HTS", "Declined testing and/or referral",ReportUtils.map(datimIndicators.kpPrevDeclinedTesting("PWID"), indParams),"");
        cohortDsd.addColumn("KP_PREV_PRISONS_CLOSED_SETTINGS_DECLINED_HTS", "Declined testing and/or referral",ReportUtils.map(datimIndicators.kpPrevDeclinedTesting("People in prison and other closed settings"), indParams), "");
        /**
         * PP_PREV
         */
        /**
         * Age/sex disaggregation
         */
        EmrReportingUtils.addRow(cohortDsd, "PP_PREV", "Reached with individual and/or small group-level HIV prevention interventions designed for the target population", ReportUtils.map(datimIndicators.ppPrev(), indParams), ppAgeGenderDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19"));

        //PP_PREV by PP type by Testing services
        //Known Positive
        cohortDsd.addColumn("PP_PREV_KNOWN_POSITIVE", "Known Positive",ReportUtils.map(datimIndicators.ppPrevKnownPositive(), indParams),"");

        //Newly tested and/or referred for HTS
        cohortDsd.addColumn("PP_PREV_NEWLY_TESTED_REFERRED", "Newly tested and/or referred for HTS",ReportUtils.map(datimIndicators.ppPrevNewlyTestedOrReferred(), indParams),"");

        //Declined testing and/or referral
        cohortDsd.addColumn("PP_PREV_DECLINED_HTS", "Declined testing and/or referral",ReportUtils.map(datimIndicators.ppPrevDeclinedTesting(), indParams),"");

        //Testing not required based on risk assessment
        cohortDsd.addColumn("PP_PREV_TEST_NOT_REQUIRED", "Test not required based on risk assessment",ReportUtils.map(datimIndicators.ppPrevTestNotRequired(), indParams),"");

        // Priority population type
        cohortDsd.addColumn("PP_PREV_FISHING_COMMUNITIES", "Reached with individual and/or small group-level HIV prevention interventions designed for the target population",ReportUtils.map(datimIndicators.ppPrevByType("Fisher Folk"), indParams),"");
        cohortDsd.addColumn("PP_PREV_MOBILE_POPULATION", "Reached with individual and/or small group-level HIV prevention interventions designed for the target population",ReportUtils.map(datimIndicators.ppPrevByType("Truck Driver"), indParams), "");
        cohortDsd.addColumn("PP_PREV_MILITARY_AND_UNIFORMED_POPULATION", "Reached with individual and/or small group-level HIV prevention interventions designed for the target population",ReportUtils.map(datimIndicators.ppPrevByType("Military and other uniformed services"), indParams), "");
        cohortDsd.addColumn("PP_PREV_OTHER", "Reached with individual and/or small group-level HIV prevention interventions (Prisoners))",ReportUtils.map(datimIndicators.ppPrevOther(), indParams), "");
        /*GEND_GBV
        Number of people receiving post-gender-based violence (GBV) clinical care based on the minimum package*/
        //1. Sexual violence (post-rape care)
        EmrReportingUtils.addRow(cohortDsd, "GEND_GBV_SEXUAL_VIOLENCE", "Received Sexual violence (post-rape) care", ReportUtils.map(datimIndicators.sexualGBV(), indParams), datimGBVDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21"));

        //2. Physical and/or emotional violence (other Post-GBV care)
        EmrReportingUtils.addRow(cohortDsd, "GEND_GBV_PHY_EMOTIONAL_VIOLENCE", "Received physical and emotional violence care", ReportUtils.map(datimIndicators.physicalEmotionalGBV(), indParams), datimGBVDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21"));

        //3. Number of People Receiving Post-exposure prophylaxis (PEP) Services. Disaggregate of the Sexual Violence Service Type
        EmrReportingUtils.addRow(cohortDsd, "GEND_GBV_PEP", "Received Post-exposure prophylaxis (PEP) Services", ReportUtils.map(datimIndicators.receivedPEP(), indParams), datimGBVDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21"));

        // Proportion of ART patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy
        EmrReportingUtils.addRow(cohortDsd, "TB_PREV_ENROLLED_COMPLETED", "Proportion of ART patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy", ReportUtils.map(datimIndicators.onARTAndCompletedTPT(), indParams), datimPrEPNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));
        // Proportion of Previously enrolled on ART patients who started on a standard course of TB Preventive Treatment (TPT)
        EmrReportingUtils.addRow(cohortDsd, "TB_PREV_PREVIOUSLY_ENROLLED_ART_INITIATED_TPT", "Patients previously enrolled on ART and initiated TPT therapy", ReportUtils.map(datimIndicators.previouslyOnARTAndInitiatedTPT(), indParams),  datimTXTBOnART, Arrays.asList("01", "02", "03", "04", "05"));
        // Proportion of Newly enrolled on ART patients who started on a standard course of TB Preventive Treatment (TPT)
        EmrReportingUtils.addRow(cohortDsd, "TB_PREV_NEWLY_ENROLLED_ART_INITIATED_TPT", "Patients newly enrolled on ART and initiated TPT therapy", ReportUtils.map(datimIndicators.newOnARTAndInitiatedTPT(), indParams),  datimTXTBOnART, Arrays.asList("01", "02", "03", "04", "05"));

        // Proportion of Newly enrolled on ART patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy
        EmrReportingUtils.addRow(cohortDsd, "TB_PREV_NEWLY_ENROLLED_ART_COMPLETED_TPT", "Patients newly enrolled on ART and TPT in the previous reporting period who completed therapy", ReportUtils.map(datimIndicators.newOnARTAndCompletedTPT(), indParams),  datimTXTBOnART, Arrays.asList("01", "02", "03", "04", "05"));

        // Proportion of Previously enrolled on ART patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy
        EmrReportingUtils.addRow(cohortDsd, "TB_PREV_PREVIOUSLY_ENROLLED_ART_COMPLETED_TPT", "Patients previously enrolled on ART and TPT in the previous reporting period who completed therapy", ReportUtils.map(datimIndicators.previouslyOnARTAndCompletedTPT(), indParams),  datimTXTBOnART, Arrays.asList("01", "02", "03", "04", "05"));

        //Number of beneficiaries served by PEPFAR OVC programs for children and families affected by HIV
        cohortDsd.addColumn("OVC_SERV_COMP", "Number of beneficiaries served by PEPFAR OVC Comprehensive program", ReportUtils.map(datimIndicators.totalBeneficiaryOfOVCComprehensiveProgram(), indParams), "");
        cohortDsd.addColumn("OVC_SERV_DREAMS", "Number of beneficiaries served by PEPFAR OVC DREAMS program", ReportUtils.map(datimIndicators.totalBeneficiaryOfOVCDreamsProgram(), indParams), "");
        cohortDsd.addColumn("OVC_SERV_PREV", "Number of beneficiaries served by PEPFAR OVC preventive program", ReportUtils.map(datimIndicators.totalBeneficiaryOfOVCPreventiveProgram(), indParams), "");

        //Testing Indicators
        //HTS_INDEX_OFFERED Index services
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_OFFERED", "Indexes offered Index testing services", ReportUtils.map(datimIndicators.offeredIndexServices(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //HTS_INDEX_CONTACTS_ELICITED_MALES_UNDER15
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_ELICITED_CONTACTS", "HTS Index Elicited Contacts", ReportUtils.map(datimIndicators.htsIndexContactsElicited(), indParams), contactAgeSexDisaggregation, Arrays.asList("01", "02", "03", "04", "05"));

        //HTS_INDEX_ACCEPTED Index services
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_ACCEPTED", "Indexes who accepted Index testing services", ReportUtils.map(datimIndicators.acceptedIndexServices(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //HTS_INDEX New Positives
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_POSITIVE", "Contacts tested HIV Positive", ReportUtils.map(datimIndicators.contactTestedPositive(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //HTS_INDEX New Negatives
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_NEGATIVE", "Contacts tested HIV Negative", ReportUtils.map(datimIndicators.contactTestedNegative(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //HTS_INDEX_DOCUMENTED_NEGATIVE
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_DOCUMENTED_NEGATIVE", "Contacts Under 14 with Documented HIV Negative Status", ReportUtils.map(datimIndicators.contactsReportedNegative(), indParams), contactAgeSexDocumentedNegativeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07"));

        //HTS_INDEX Known Positive
        EmrReportingUtils.addRow(cohortDsd, "HTS_INDEX_KNOWN_POSITIVE", "Contacts Known HIV Positive", ReportUtils.map(datimIndicators.contactKnownPositive(), indParams), contactAgeSexFineDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));

        //HTS_TST
        //1. Index Testing
        //Index Tested Negative
        cohortDsd.addColumn("HTS_TST_Index_Negative", "Index Tested Negative", ReportUtils.map(datimIndicators.indexTestedNegative(), indParams), "");

        //Index Tested Positive
        cohortDsd.addColumn("HTS_TST_Index_Positive", "Index Tested Positive", ReportUtils.map(datimIndicators.indexTestedPositive(), indParams), "");

        //2. VCT testing
        //Tested Negative VCT
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_VCT_Negative", "Tested Negative VCT", ReportUtils.map(datimIndicators.testedNegativeVCT(), indParams), datimExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31","32","33"));

        //Tested Positive VCT
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_VCT_Positive", "Tested Positive VCT", ReportUtils.map(datimIndicators.testedPositiveVCT(), indParams), datimExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31","32","33"));

        //4. Paediatric Clinics
        //Tested Negative Paediatric services
        EmrReportingUtils.addRow(cohortDsd,"HTS_TST_Paediatric_Negative", "Tested Negative Paediatric services", ReportUtils.map(datimIndicators.testedNegativePaediatricServices(), indParams), pediatricAgeDisaggregation,Arrays.asList("01","02","03","04","05"));

        //Tested Positive Paediatric services
        EmrReportingUtils.addRow(cohortDsd,"HTS_TST_Paediatric_Positive", "Tested Positive Paediatric Services", ReportUtils.map(datimIndicators.testedPositivePaediatricServices(), indParams), pediatricAgeDisaggregation,Arrays.asList("01","02","03","04","05"));

        //3. Malnutrition Clinic
        //Tested Negative Malnutrition Clinic
        EmrReportingUtils.addRow(cohortDsd,"HTS_TST_Malnutrition_Negative", "Tested Negative Malnutrition Clinic", ReportUtils.map(datimIndicators.testedNegativeMalnutritionClinic(), indParams), pediatricAgeDisaggregation,Arrays.asList("01","02","03","04","05"));

        //Tested Positive Malnutrition Clinic
        EmrReportingUtils.addRow(cohortDsd,"HTS_TST_Malnutrition_Positive", "Tested Positive Malnutrition Clinic", ReportUtils.map(datimIndicators.testedPositiveMalnutritionClinic(), indParams), pediatricAgeDisaggregation,Arrays.asList("01","02","03","04","05"));
        //5.TB Clinics

        //Tested Negative TB Clinic
       cohortDsd.addColumn("HTS_TST_TB_Negative", "Tested Negative TB Clinic", ReportUtils.map(datimIndicators.testedNegativeTBClinic(), indParams), "");

        //Tested Positive TB Clinic
        cohortDsd.addColumn( "HTS_TST_TB_Positive", "Tested Positive TB Clinic", ReportUtils.map(datimIndicators.testedPositiveTBClinic(), indParams),"");

        //6.PMTCT_ANC-1 Only
        //Tested Negative PMTCT services ANC-1 only
        cohortDsd.addColumn("HTS_TST_PMTCT_ANC1_Negative", "Tested Negative PMTCT at 1st ANC", ReportUtils.map(datimIndicators.testedNegativePMTCTANC1(), indParams), "");

        //Tested Positive PMTCT services ANC-1 only
        cohortDsd.addColumn( "HTS_TST_PMTCT_ANC1_Positive", "Tested Positive PMTCT at 1st ANC", ReportUtils.map(datimIndicators.testedPositivePMTCTANC1(), indParams),"");

        //7.PMTCT [Post ANC1, Preg/L&D]
        //Tested Negative PMTCT services Post ANC-1 (including labour and delivery)
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_PMTCT_POSTANC1_PREG_LD_Negative", "Tested Negative PMTCT Post ANC-1 (Incl Preg & Labour/Delivery)", ReportUtils.map(datimIndicators.negativePMTCTPostANC1PregnantAndLabourAndDelivery(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));
        //Tested Positive PMTCT services Post ANC-1 (including labour and delivery)
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_PMTCT_POSTANC1_PREG_LD_Positive", "Tested Positive PMTCT Post ANC-1 (Incl Preg & Labour/Delivery)", ReportUtils.map(datimIndicators.positivePMTCTPostANC1PregnantAndLabourAndDelivery(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));
        //Tested Negative PMTCT services Post ANC-1 (Breastfeeding)
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_PMTCT_POSTANC1_BF_Negative", "Tested Negative PMTCT Post ANC-1 Breastfeeding", ReportUtils.map(datimIndicators.negativePMTCTPostANC1Breastfeeding(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));
        //Tested Positive PMTCT services Post ANC-1 (Breastfeeding)
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_PMTCT_POSTANC1_BF_Positive", "Tested Positive PMTCT Post ANC-1 Breastfeeding", ReportUtils.map(datimIndicators.positivePMTCTPostANC1Breastfeeding(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //8.STI
        //9. Inpatient
        //Tested Negative Inpatient Services
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Inpatient_Negative", "Tested Negative Inpatient Services", ReportUtils.map(datimIndicators.testedNegativeInpatientServices(), indParams), datimExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31","32","33"));

        //Tested Positive Inpatient Services
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Inpatient_Positive", "Tested Positive Inpatient Services", ReportUtils.map(datimIndicators.testedPositiveInpatientServices(), indParams), datimExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31","32","33"));

        //10. Emergency Ward
            //12. Other
        //Tested Negative Other
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Other_Negative", "Tested Negative Other", ReportUtils.map(datimIndicators.testedNegativeOther(), indParams), datimExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31","32","33"));

        //Tested Positive Other
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_Other_Positive", "Tested Positive Other", ReportUtils.map(datimIndicators.testedPositiveOther(), indParams), datimExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31","32","33"));

        //13. KP
        //PWID Positive
        cohortDsd.addColumn("HTS_TST_KP_PWID_POS", "PWID Tested Positive", ReportUtils.map(datimIndicators.pwidTestedPositive(), indParams), "");

        //PWID Negative
        cohortDsd.addColumn( "HTS_TST_KP_PWID_NEG", "PWID Tested Negative", ReportUtils.map(datimIndicators.pwidTestedNegative(), indParams), "");

        //MSM Positive
        cohortDsd.addColumn( "HTS_TST_KP_MSM_POS", "MSM Tested Positive", ReportUtils.map(datimIndicators.msmTestedPositive(), indParams),"");

        //MSM Negative
        cohortDsd.addColumn( "HTS_TST_KP_MSM_NEG", "MSM Tested Negative", ReportUtils.map(datimIndicators.msmTestedNegative(), indParams), "");

        //FSW Positive
        cohortDsd.addColumn( "HTS_TST_KP_FSW_POS", "FSW Tested Positive", ReportUtils.map(datimIndicators.fswTestedPositive(), indParams), "");

        //FSW Negative
        cohortDsd.addColumn( "HTS_TST_KP_FSW_NEG", "FSW Tested Negative", ReportUtils.map(datimIndicators.fswTestedNegative(), indParams), "");

        //TG Negative
        cohortDsd.addColumn( "HTS_TST_KP_TG_NEG", "TG Tested Negative", ReportUtils.map(datimIndicators.tgTestedNegative(), indParams), "");

        //TG Positive
        cohortDsd.addColumn( "HTS_TST_KP_TG_POS", "TG Tested Positive", ReportUtils.map(datimIndicators.tgTestedPositive(), indParams), "");

        //PRISONERS_CLOSED_SETTINGS_Negative
        cohortDsd.addColumn( "HTS_TST_KP_PRISONERS_CLOSED_SETTINGS_NEG", "People in prisons and other closed settings Tested Negative", ReportUtils.map(datimIndicators.prisonersTestedNegative(), indParams), "");

        //PRISONERS_CLOSED_SETTINGS POSITIVE
        cohortDsd.addColumn( "HTS_TST_KP_PRISONERS_CLOSED_SETTINGS_POS", "People in prisons and other closed settings Tested Positive", ReportUtils.map(datimIndicators.prisonersTestedPositive(), indParams), "");

        //15. Social Networks SNS
        //SNS Positive
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_SNS_POSITIVE", "Tested Positive Social Network", ReportUtils.map(datimIndicators.testedPositiveSNS(), indParams), datimExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31","32","33"));

        //SNS Negative
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_SNS_NEGATIVE", "Tested Negative Social Network", ReportUtils.map(datimIndicators.testedNegativeSNS(), indParams), datimExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31","32","33"));

        //8.STI
        //Tested Negative STI clinic
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_STI_Negative", "Tested Negative at the STI clinic", ReportUtils.map(datimIndicators.testedNegativeSTIClinic(), indParams), datimExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31","32","33"));
        //Tested Negative STI clinic
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_STI_Positive", "Tested Positive at the STI Clinic", ReportUtils.map(datimIndicators.testedPositiveSTIClinic(), indParams), datimExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31","32","33"));

        //10. Emergency Ward
        //Tested Negative Emergency ward
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_EMERGENCY_Negative", "Tested Negative at the emergency ward", ReportUtils.map(datimIndicators.testedNegativeEmergencyWard(), indParams), datimExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31","32","33"));

        //Tested Positive Emergency ward
        EmrReportingUtils.addRow(cohortDsd, "HTS_TST_EMERGENCY_Positive", "Tested Positive at the emergency ward", ReportUtils.map(datimIndicators.testedPositiveEmergencyWard(), indParams), datimExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31","32","33"));
        //11. VMMC
        cohortDsd.addColumn("HTS_TST_VMMC_Negative", "Tested Negative at VMMC clinic", ReportUtils.map(datimIndicators.testedNegativeVMMCServices(), indParams), "");

        cohortDsd.addColumn("HTS_TST_VMMC_Positive", "Tested Positive at VMMC clinic", ReportUtils.map(datimIndicators.testedPositveVMMCServices(), indParams), "");

        //PMTCT_STAT
        //Known positive before ANC
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_STAT_KNOWN_POSITIVE", "Positive HIV status before ANC ", ReportUtils.map(datimIndicators.clientsWithPositiveHivStatusBeforeAnc1(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //4 HIV Positive at ANC
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_STAT_ANC_Positive", "Tested HIV Positive at ANC", ReportUtils.map(datimIndicators.patientsTestPositiveAtANC(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //4 HIV Negative at ANC
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_STAT_ANC_Negative", "Tested HIV Negative at ANC", ReportUtils.map(datimIndicators.patientsTestNegativeAtANC(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //Newly enrolled to ANC
        //cohortDsd.addColumn( "PMTCT_STAT_Denominator", "Newly enrolled to ANC", ReportUtils.map(datimIndicators.clientsNewlyEnrolledToANC(), indParams), "");
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_STAT_Denominator", "Newly enrolled to ANC", ReportUtils.map(datimIndicators.clientsNewlyEnrolledToANC(), indParams), datimPMTCTANCAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"));

        //TB_STAT
        EmrReportingUtils.addRow(cohortDsd, "TB_STAT_KNOWN_POS", "New and relapsed TB cases who are Known positive", ReportUtils.map(datimIndicators.tbSTATKnownPositive(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));
        EmrReportingUtils.addRow(cohortDsd, "TB_STAT_NEW_POS", "New and relapsed TB cases newly tested positive", ReportUtils.map(datimIndicators.tbSTATNewPositive(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));
        EmrReportingUtils.addRow(cohortDsd, "TB_STAT_NEW_NEG", "New and relapsed TB cases newly tested negative", ReportUtils.map(datimIndicators.tbSTATNewNegative(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));
        EmrReportingUtils.addRow(cohortDsd, "TB_STAT_RECENT_NEG", "New and relapsed TB cases recently tested negative", ReportUtils.map(datimIndicators.tbSTATRecentNegative(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));
        EmrReportingUtils.addRow(cohortDsd, "TB_STAT_DENOMINATOR", "Total number of new and relapsed TB cases, during the reporting period", ReportUtils.map(datimIndicators.tbSTATDenominator(), indParams), datimNewAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));
        //PMTCT_EID
        cohortDsd.addColumn("PMTCT_EID_NUMERATOR", "Infants who had a virologic HIV test (sample collected) by 12 months of age during the reporting period", ReportUtils.map(datimIndicators.firstInfantVirologicTestsAt12Months(), indParams), "");
        cohortDsd.addColumn("PMTCT_EID_FIRST_TEST_WITHIN_2_MONTHS", "First sample collected for a virologic HIV test between birth and <= 2 months of age", ReportUtils.map(datimIndicators.infantFirstVirologicTestWithin2Months(), indParams), "");
        cohortDsd.addColumn("PMTCT_EID_FIRST_TEST_2_TO_12_MONTHS", "First sample collected for a virologic HIV test between 3-12 months of age", ReportUtils.map(datimIndicators.infantFirstVirologicTest3To12Months(), indParams), "");
        cohortDsd.addColumn("PMTCT_EID_ATLEAST_2_TESTS_WITHIN_2_MONTHS", "At least second sample collected for a virologic HIV test between birth and <= 2 months of age", ReportUtils.map(datimIndicators.atleast2InfantVirologicTestWithin2Months(), indParams), "");
        cohortDsd.addColumn("PMTCT_EID_ATLEAST_2_TESTS_2_12_MONTHS", "At least second sample collected for a virologic HIV test between birth and 3-12 months of age", ReportUtils.map(datimIndicators.atleast2InfantVirologicTestsAt3To12Months(), indParams), "");

        //PMTCT_HEI
        cohortDsd.addColumn("PMTCT_HEI_NUMERATOR", "HIV-exposed infants with a virologic HIV test result returned in the reporting period, whose diagnostic sample was collected by 12 months of age.", ReportUtils.map(datimIndicators.infantsTestedAndResultsReturned(), indParams), "");
        cohortDsd.addColumn("PMTCT_HEI_NEG_0_TO_2_MONTHS", "HIV-exposed infants with a virologic HIV Negative test result returned in the reporting period, whose diagnostic sample was collected by 2 months of age.", ReportUtils.map(datimIndicators.infantsTestedNegativeby2MonthsOfAge(), indParams), "");
        cohortDsd.addColumn("PMTCT_HEI_NEG_2_TO_12_MONTHS", "HIV-exposed infants with a virologic HIV Negative test result returned in the reporting period, whose diagnostic sample was collected at 3-12 months of age.", ReportUtils.map(datimIndicators.infantsTestedNegativeby3To12MonthsOfAge(), indParams), "");
        cohortDsd.addColumn("PMTCT_HEI_POS_0_TO_2_MONTHS", "HIV-exposed infants with a virologic HIV Positive test result returned in the reporting period, whose diagnostic sample was collected by 2 months of age.", ReportUtils.map(datimIndicators.infantsTestedPositiveby2MonthsOfAge(), indParams), "");
        cohortDsd.addColumn("PMTCT_HEI_POS_2_TO_12_MONTHS", "HIV-exposed infants with a virologic HIV Positive test result returned in the reporting period, whose diagnostic sample was collected at 3-12 months of age.", ReportUtils.map(datimIndicators.infantsTestedPositiveby3To12MonthsOfAge(), indParams), "");
        cohortDsd.addColumn("PMTCT_HEI_INITIATED_ART_0_TO_2_MONTHS", "HIV Positive started ART in the reporting period, whose diagnostic sample was collected by 2 months of age.", ReportUtils.map(datimIndicators.infantsInitiatedARTTestedPositiveby2MonthsOfAge(), indParams), "");
        cohortDsd.addColumn("PMTCT_HEI_INITIATED_ART_2_TO_12_MONTHS", "HIV Positive started ART in the reporting period, whose diagnostic sample was collected at 3-12 months of age.", ReportUtils.map(datimIndicators.infantsInitiatedARTTestedPositiveby3To12MonthsOfAge(), indParams), "");

        //CXCA_SCRN_FIRST_TIME_NEGATIVE
        EmrReportingUtils.addRow(cohortDsd, "CXCA_SCRN_FIRST_TIME_NEGATIVE", "HIV Positive women on ART screened Negative for CACX for the 1st time", ReportUtils.map(datimIndicators.firstTimeCXCASCRNNegative(), indParams), datimPMTCTCXCAExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        //CXCA_SCRN_FIRST_TIME_POSITIVE
        EmrReportingUtils.addRow(cohortDsd, "CXCA_SCRN_FIRST_TIME_POSITIVE", "HIV Positive women on ART screened Positive for CACX for the 1st time", ReportUtils.map(datimIndicators.firstTimeCXCASCRNPositive(), indParams), datimPMTCTCXCAExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        //CXCA_SCRN_FIRST_TIME_PRESUMED
        EmrReportingUtils.addRow(cohortDsd, "CXCA_SCRN_FIRST_TIME_PRESUMED", "HIV Positive Women on ART with Presumed CACX for the 1st time", ReportUtils.map(datimIndicators.firstTimeCXCASCRNPresumed(), indParams), datimPMTCTCXCAExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        //CXCA_SCRN_RESCREENED_NEGATIVE
        EmrReportingUtils.addRow(cohortDsd, "CXCA_SCRN_RESCREENED_NEGATIVE", "HIV Positive Women on ART re-screened Negative for CACX", ReportUtils.map(datimIndicators.rescreenedCXCASCRNNegative(), indParams), datimPMTCTCXCAExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        //CXCA_SCRN_RESCREENED_POSITIVE
        EmrReportingUtils.addRow(cohortDsd, "CXCA_SCRN_RESCREENED_POSITIVE", "HIV Positive Women on ART re-screened Positive for CACX", ReportUtils.map(datimIndicators.rescreenedCXCASCRNPositive(), indParams), datimPMTCTCXCAExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        //CXCA_SCRN_RESCREENED_PRESUMED
        EmrReportingUtils.addRow(cohortDsd, "CXCA_SCRN_RESCREENED_PRESUMED", "HIV Positive Women on ART re-screened with presumed CACX outcome", ReportUtils.map(datimIndicators.rescreenedCXCASCRNPresumed(), indParams), datimPMTCTCXCAExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        //CXCA_SCRN_POST_TREATMENT_NEGATIVE
        EmrReportingUtils.addRow(cohortDsd, "CXCA_SCRN_POST_TREATMENT_NEGATIVE", "HIV Positive Women on ART screened Negative for CACX after previous treatment", ReportUtils.map(datimIndicators.postTreatmentCXCASCRNNegative(), indParams), datimPMTCTCXCAExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        //CXCA_SCRN_POST_TREATMENT_POSITIVE
        EmrReportingUtils.addRow(cohortDsd, "CXCA_SCRN_POST_TREATMENT_POSITIVE", "HIV Positive Women on ART screened Positive for CACX after previous treatment", ReportUtils.map(datimIndicators.postTreatmentCXCASCRNPositive(), indParams), datimPMTCTCXCAExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        //CXCA_SCRN_POST_TREATMENT_PRESUMED
        EmrReportingUtils.addRow(cohortDsd, "CXCA_SCRN_POST_TREATMENT_PRESUMED", "HIV Positive women on ART with Presumed CACX after previous treatment", ReportUtils.map(datimIndicators.postTreatmentCXCASCRNPresumed(), indParams), datimPMTCTCXCAExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        //CXCA_TX_FIRST_TIME_CRYOTHERAPY
        EmrReportingUtils.addRow(cohortDsd, "CXCA_TX_CRYOTHERAPY_1ST_SCREENING", "HIV positive Women on ART and received Cryotherapy CACX treatment in their first CACX screening", ReportUtils.map(datimIndicators.firstScreeningCXCATXCryotherapy(), indParams), datimPMTCTCXCAExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        //CXCA_TX_FIRST_TIME_THERMOCOAGULATION
        EmrReportingUtils.addRow(cohortDsd, "CXCA_TX_THERMOCOAGULATION_1ST_SCREENING", "HIV positive Women on ART and received Thermocoagulation CACX treatment in their first CACX screening", ReportUtils.map(datimIndicators.firstScreeningCXCATXThermocoagulation(), indParams), datimPMTCTCXCAExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        //CXCA_TX_FIRST_TIME_LEEP
        EmrReportingUtils.addRow(cohortDsd, "CXCA_TX_LEEP_1ST_SCREENING", "HIV positive Women on ART and received LEEP CACX treatment in their first CACX screening", ReportUtils.map(datimIndicators.firstScreeningCXCATXLEEP(), indParams), datimPMTCTCXCAExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        //CXCA_TX_RESCREENING_CRYOTHERAPY
        EmrReportingUtils.addRow(cohortDsd, "CXCA_TX_CRYOTHERAPY_RESCREENING", "HIV positive Women on ART and received Cryotherapy CACX treatment in their CACX re-screening", ReportUtils.map(datimIndicators.rescreenedCXCATxCryotherapy(), indParams), datimPMTCTCXCAExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        //CXCA_TX_RESCREENING_THERMOCOAGULATION
        EmrReportingUtils.addRow(cohortDsd, "CXCA_TX_THERMOCOAGULATION_RESCREENING", "HIV positive Women on ART and received Thermocoagulation CACX treatment in their CACX re-screening", ReportUtils.map(datimIndicators.rescreenedCXCATXThermocoagulation(), indParams), datimPMTCTCXCAExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        //CXCA_TX_RESCRENING_LEEP
        EmrReportingUtils.addRow(cohortDsd, "CXCA_TX_LEEP_RESCREENING", "HIV positive Women on ART and received LEEP CACX treatment in their re-screening", ReportUtils.map(datimIndicators.rescreenedCXCATXLEEP(), indParams), datimPMTCTCXCAExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        //CXCA_TX_POST_TX_FOLLOWUP_CRYOTHERAPY
        EmrReportingUtils.addRow(cohortDsd, "CXCA_TX_CRYOTHERAPY_POST_Tx_FOLLOWUP", "HIV positive Women on ART and received Cryotherapy CACX treatment in their Post treatment follow-up CACX screening", ReportUtils.map(datimIndicators.postTxFollowupCXCATxCryotherapy(), indParams), datimPMTCTCXCAExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        //CXCA_TX_FIRST_TIME_THERMOCOAGULATION
        EmrReportingUtils.addRow(cohortDsd, "CXCA_TX_THERMOCOAGULATION_POST_Tx_FOLLOWUP", "HIV positive Women on ART and received Thermocoagulation CACX treatment in their Post treatment follow-up CACX screening", ReportUtils.map(datimIndicators.postTxFollowupCXCATXThermocoagulation(), indParams), datimPMTCTCXCAExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

        //CXCA_TX_POST_TX_FOLLOWUP_LEEPdatimPMTCTCXCAAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10"));
        EmrReportingUtils.addRow(cohortDsd, "CXCA_TX_LEEP_POST_Tx_FOLLOWUP", "HIV positive Women on ART and received LEEP cancer treatment in their Post treatment follow-up CACX screening", ReportUtils.map(datimIndicators.postTxFollowupCXCATXLEEP(), indParams), datimPMTCTCXCAExpandedAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));


        //Number of OVC current on ART reported to implementing partner
        cohortDsd.addColumn("OVC_HIVSTAT_ONART", "Number of OVC Current on ART reported to implementing partner", ReportUtils.map(datimIndicators.ovcOnART(), indParams), "");

        //Number of OVC Not on ART reported to implementing partner
        cohortDsd.addColumn("OVC_HIVSTAT_NOT_ON_ART", "Number of OVC not on ART reported to implementing partner", ReportUtils.map(datimIndicators.ovcNotOnART(), indParams), "");
        //PMTCT_FO
        //Denominator
        cohortDsd.addColumn("PMTCT_FO_DENOMINATOR", "Number of HIV-exposed infants who were born 24 months prior to the reporting period and registered in the birth cohort", ReportUtils.map(datimIndicators.pmtctFoDenominator(), indParams), "");
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
        //Disaggregated by CD4, Age / Sex
        EmrReportingUtils.addRow(cohortDsd, "TX_NEW_CD4_BELOW_200", "Newly Started ART baseline CD4 < 200", ReportUtils.map(datimIndicators.newlyStartedARTCD4Within200(), indParams),  datim5To65PlusAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27"));
        EmrReportingUtils.addRow(cohortDsd, "TX_NEW_CD4_200_AND_ABOVE", "Newly Started ART baseline CD4 >= 200", ReportUtils.map(datimIndicators.newlyStartedARTCD4200AndAbove(), indParams),  datim5To65PlusAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27"));
        EmrReportingUtils.addRow(cohortDsd, "TX_NEW_CD4_UNKOWN", "Newly Started ART baseline CD4 Unknown", ReportUtils.map(datimIndicators.newlyStartedARTCD4Unknown(), indParams),  datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));

        //Newly Started ART While BreastFeeding
        cohortDsd.addColumn("TX_New_BF", "Newly Started ART While Breastfeeding", ReportUtils.map(datimIndicators.newlyStartedARTWhileBF(), indParams), "");

        //Number of Adults with HIV infection receiving ART By KP Type Disagreggation - PWID
        cohortDsd.addColumn("TX_NEW_PWID", "PWID with HIV new on ART", ReportUtils.map(datimIndicators.kpNewlyStartedART(PWID_CONCEPT), indParams), "");

        //Number of Adults with HIV infection receiving ART By KP Type Disagreggation - MSM
        cohortDsd.addColumn("TX_NEW_MSM", "MSM with HIV new on ART", ReportUtils.map(datimIndicators.kpNewlyStartedART(MSM_CONCEPT), indParams), "");

        //Number of Adults with HIV infection receiving ART By KP Type Disagreggation - TG
        cohortDsd.addColumn("TX_NEW_TG", "Transgenders with HIV new on ART", ReportUtils.map(datimIndicators.kpNewlyStartedART( TG_CONCEPT), indParams), "");

        //Number of Adults with HIV infection receiving ART By KP Type Disagreggation - FSW
        cohortDsd.addColumn("TX_NEW_FSW", "FSW with HIV new on ART", ReportUtils.map(datimIndicators.kpNewlyStartedART(FSW_CONCEPT), indParams), "");

        //Number of Adults with HIV infection receiving ART By KP Type Disagreggation - TX_NEW_PRISONS_CLOSED_SETTINGS
        cohortDsd.addColumn("TX_NEW_PRISONS_CLOSED_SETTINGS", "Prisoners and Closed settings with HIV new on ART", ReportUtils.map(datimIndicators.kpNewlyStartedART(PRISONERS_CLOSED_SETTINGS_CONCEPT), indParams), "");

        //TX_CURR
        //Number of Adults and Children with HIV infection receiving ART By Age/Sex Disagreggation
        EmrReportingUtils.addRow(cohortDsd, "TX_CURR", "Adults and Children with HIV infection receiving ART", ReportUtils.map(datimIndicators.currentlyOnArt(), indParams), datimTxCurrDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"));

        //Number of Adults with HIV infection receiving ART By KP Type Disagreggation - PWID
        cohortDsd.addColumn("TX_CURR_PWID", "PWID with HIV receiving ART", ReportUtils.map(datimIndicators.kpCurrentlyOnART(PWID_CONCEPT), indParams), "");

        //Number of Adults with HIV infection receiving ART By KP Type Disagreggation - MSM
        cohortDsd.addColumn("TX_CURR_MSM", "MSM with HIV receiving ART", ReportUtils.map(datimIndicators.kpCurrentlyOnART(MSM_CONCEPT), indParams), "");

        //Number of Adults with HIV infection receiving ART By KP Type Disagreggation - TG
        cohortDsd.addColumn("TX_CURR_TG", "Transgenders with HIV receiving ART", ReportUtils.map(datimIndicators.kpCurrentlyOnART(TG_CONCEPT), indParams), "");

        //Number of Adults with HIV infection receiving ART By KP Type Disagreggation - FSW
        cohortDsd.addColumn("TX_CURR_FSW", "FSW with HIV receiving ART", ReportUtils.map(datimIndicators.kpCurrentlyOnART(FSW_CONCEPT), indParams), "");

        //Number of Adults with HIV infection receiving ART By KP Type Disagreggation - TX_CURR_PRISONS_CLOSED_SETTINGS
        cohortDsd.addColumn("TX_CURR_PRISONS_CLOSED_SETTINGS", "Prisoners and Closed settings with HIV receiving ART", ReportUtils.map(datimIndicators.kpCurrentlyOnART(PRISONERS_CLOSED_SETTINGS_CONCEPT), indParams), "");

        //One month before next appointment
        EmrReportingUtils.addRow(cohortDsd, "TX_CURR_MMD_BELOW_3_MONTHS", "Less than 3 months drugs", ReportUtils.map(datimIndicators.currentlyOnARTUnder3MonthsMMD(), indParams), datimTXTBOnART, Arrays.asList("01", "02", "03", "04", "05"));

        //two month before next appointment
        EmrReportingUtils.addRow(cohortDsd, "TX_CURR_MMD_3TO5_MONTHS", "3-5 months drugs", ReportUtils.map(datimIndicators.currentlyOnART3To5MonthsMMD(), indParams), datimTXTBOnART, Arrays.asList("01", "02", "03", "04", "05"));

        //three month before next appointment
        EmrReportingUtils.addRow(cohortDsd, "TX_CURR_MMD_6+_MONTHS", "Over 6 months drugs", ReportUtils.map(datimIndicators.currentlyOnART6MonthsAndAboveMMD(), indParams), datimTXTBOnART, Arrays.asList("01", "02", "03", "04", "05"));

        //PMTCT_ART

        //Mothers new on ART during current pregnancy
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_ART_New", "Mothers new on ART during current pregnancy", ReportUtils.map(datimIndicators.mothersNewOnARTDuringCurrentPregnancy(), indParams), datimPMTCTARTAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11","12","13","14"));

        //Mothers already on ART at start of current pregnancy
        EmrReportingUtils.addRow(cohortDsd, "PMTCT_ART_Already", "Number of Mothers Already on ART at the start of current Pregnancy", ReportUtils.map(datimIndicators.mothersAlreadyOnARTAtStartOfCurrentPregnancy(), indParams), datimPMTCTARTAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11","12","13","14"));

        // TB_ART Proportion of HIV-positive new and relapsed TB cases New on ART during TB treatment
        EmrReportingUtils.addRow(cohortDsd, "TB_ART_NEW_ON_ART", "TB Patients New on ART ", ReportUtils.map(datimIndicators.newOnARTTBInfected(), indParams),  datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));

        // TB_ART Proportion of HIV-positive new and relapsed TB cases already on ART during TB treatment
        EmrReportingUtils.addRow(cohortDsd, "TB_ART_ALREADY_ON_ART", "TB patients already on ART", ReportUtils.map(datimIndicators.alreadyOnARTTBInfected(), indParams),  datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));

        //TX_TB
        //Numerator_new_on_art
        EmrReportingUtils.addRow(cohortDsd, "TX_TB_NUM_NEW_ON_ART", "Starting TB treatment newly started ART", ReportUtils.map(datimIndicators.startingTBTreatmentNewOnART(), indParams), datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));

        //Numerator_Prev_on_art
        EmrReportingUtils.addRow(cohortDsd, "TX_TB_NUM_PREV_ON_ART", "Starting TB treatment previously on ART", ReportUtils.map(datimIndicators.startingTBTreatmentPrevOnART(), indParams), datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));

        //TX_TB(Denominator)
        //TX_TB_NEW_ON_ART_SCREENED_POSITIVE
        EmrReportingUtils.addRow(cohortDsd, "TX_TB_NEW_ON_ART_SCREENED_POSITIVE", "New on ART Screened Positive", ReportUtils.map(datimIndicators.newOnARTScreenedPositive(), indParams), datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));

        //TX_TB_PREVIOUSLY_ON_ART_SCREENED_POSITIVE
        EmrReportingUtils.addRow(cohortDsd, "TX_TB_PREV_ON_ART_SCREENED_POSITIVE", "Previously on ART Screened Positive", ReportUtils.map(datimIndicators.prevOnARTScreenedPositive(), indParams), datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));

        //TX_TB_NEW_ON_ART_SCREENED_NEGATIVE
        EmrReportingUtils.addRow(cohortDsd, "TX_TB_NEW_ON_ART_SCREENED_NEGATIVE", "New on ART Screened Negative", ReportUtils.map(datimIndicators.newOnARTScreenedNegative(), indParams), datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));

        //TX_TB_PREVIOUSLY_ON_ART_SCREENED_NEGATIVE
        EmrReportingUtils.addRow(cohortDsd, "TX_TB_PREV_ON_ART_SCREENED_NEGATIVE", "Previously on ART Screened Negative", ReportUtils.map(datimIndicators.prevOnARTScreenedNegative(), indParams), datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));

        //TX_TB_SPECIMEN_SENT
        cohortDsd.addColumn("TX_TB_SPECIMEN_SENT", "Specimen sent for bacteriologic diagnosis of active TB", ReportUtils.map(datimIndicators.specimenSent(), indParams), "");

        //TX_TB_GeneXpert MTB/RIF assay (with or without other testing)
        cohortDsd.addColumn("TX_TB_GeneXpert", "GeneXpert MTB/RIF assay-mWRD (with or without other testing)", ReportUtils.map(datimIndicators.geneXpertMTBRIF(), indParams), "");

        //TX_TB_SMEAR_MICROSCOPY_ONLY
        cohortDsd.addColumn( "TX_TB_SMEAR_MICROSCOPY_ONLY", "Smear microscopy only", ReportUtils.map(datimIndicators.smearMicroscopy(), indParams), "");

        //TX_TB_CHEST_XRAY
        cohortDsd.addColumn( "TX_TB_CXR", "Chest xRay (CXR)", ReportUtils.map(datimIndicators.onARTChestXrayDone(), indParams), "");

        //TX_TB_ADDITIONAL_TESTS (other than GeneXpert)
        cohortDsd.addColumn( "TX_TB_ADDITIONAL_TESTS", "Additional test other than GeneXpert", ReportUtils.map(datimIndicators.additionalTBTests(), indParams),"");

        //TX_TB_POSITIVE_RESULT_RETURNED
        cohortDsd.addColumn( "TX_TB_POSITIVE_RESULT_RETURNED", "Positive result returned for bacteriologic diagnosis of active TB", ReportUtils.map(datimIndicators.resultsReturned(), indParams), "");

        //TX_ML
        //TX_ML_DIED Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed)
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_DIED", "ART patients with missed appointment due to death", ReportUtils.map(datimIndicators.txmlPatientDied(), indParams),  datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));

        //TX_ML LTFU ON DRUGS <3 MONTHS Number of ART patients with no clinical contact since their last expected contact and have been on drugs for less than 3 months
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_IIT_UNDER_3_MONTHS", "IIT After being on Treatment for <3 months", ReportUtils.map(datimIndicators.txMLIITUnder3MonthsInTx(), indParams),  datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));

        //TX_ML LTFU ON DRUGS 3-5 MONTHS Number of ART patients with no clinical contact since their last expected contact and have been on drugs for 3-5 months
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_IIT_3_TO_5_MONTHS", "IIT After being on Treatment for 3-5 months", ReportUtils.map(datimIndicators.txMLIIT3To5MonthsInTx(), indParams),  datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));

        //TX_ML LTFU ON DRUGS >6 MONTHS Number of ART patients with no clinical contact since their last expected contact and have been on drugs for 3-5 months
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_IIT_6_MONTHS_AND_ABOVE", "IIT After being on Treatment for 6+ months", ReportUtils.map(datimIndicators.txMLIITAtleast6MonthsInTx(), indParams),  datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));

        //TX_ML_PREV_UNDOCUMENTED_TRF Number of ART patients with no clinical contact since their last expected contact due to Previously undocumented transfer
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_TRF_OUT", "ART patients with missed appointment due to transfer out", ReportUtils.map(datimIndicators.txmlTrfOut(), indParams),  datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));

        //TX_ML_STOPPED_TREATMENT Number of ART patients with no clinical contact since their last expected contact because they stopped treatment
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_STOPPED_TREATMENT", "ART patients with missed appointment because they stopped treatment", ReportUtils.map(datimIndicators.txmlPatientByTXStopReason(), indParams),  datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));

        //TX_ML_KPs who died
        cohortDsd.addColumn( "TX_ML_PWID_DIED", "PWID KPs who died", ReportUtils.map(datimIndicators.txmlKPPatientDied(PWID_CONCEPT), indParams),"");
        cohortDsd.addColumn("TX_ML_MSM_DIED", "MSM KPs TXML who died", ReportUtils.map(datimIndicators.txmlKPPatientDied(MSM_CONCEPT), indParams), "");
        cohortDsd.addColumn( "TX_ML_TG_DIED", "TG KPs TXML who died", ReportUtils.map(datimIndicators.txmlKPPatientDied(TG_CONCEPT), indParams), "");
        cohortDsd.addColumn("TX_ML_FSW_DIED", "FSW KPs TXML who died", ReportUtils.map(datimIndicators.txmlKPPatientDied(FSW_CONCEPT), indParams), "");
        cohortDsd.addColumn( "TX_ML_PRISONS_CLOSED_SETTINGS_DIED", "Prisoners KPs TXML who died", ReportUtils.map(datimIndicators.txmlKPPatientDied(PRISONERS_CLOSED_SETTINGS_CONCEPT), indParams),"");
        //TX_ML  KPs IIT < 3 MONTHS
        cohortDsd.addColumn( "TX_ML_PWID_IIT_UNDER_3_MONTHS", "IIT After being on Treatment for <3 months", ReportUtils.map(datimIndicators.txMLIITKpUnder3MonthsInTx(PWID_CONCEPT), indParams),"");
        cohortDsd.addColumn("TX_ML_MSM_IIT_UNDER_3_MONTHS", "IIT After being on Treatment for <3 months", ReportUtils.map(datimIndicators.txMLIITKpUnder3MonthsInTx(MSM_CONCEPT), indParams), "");
        cohortDsd.addColumn( "TX_ML_TG_IIT_UNDER_3_MONTHS", "IIT After being on Treatment for <3 months", ReportUtils.map(datimIndicators.txMLIITKpUnder3MonthsInTx(TG_CONCEPT), indParams), "");
        cohortDsd.addColumn("TX_ML_FSW_IIT_UNDER_3_MONTHS", "IIT After being on Treatment for <3 months", ReportUtils.map(datimIndicators.txMLIITKpUnder3MonthsInTx(FSW_CONCEPT), indParams), "");
        cohortDsd.addColumn( "TX_ML_PRISONS_CLOSED_SETTINGS_IIT_UNDER_3_MONTHS", "IIT After being on Treatment for <3 months", ReportUtils.map(datimIndicators.txMLIITKpUnder3MonthsInTx(PRISONERS_CLOSED_SETTINGS_CONCEPT), indParams),"");

        //TX_ML  KPs IIT 3-5 MONTHS
        cohortDsd.addColumn( "TX_ML_PWID_IIT_3_TO_5_MONTHS", "IIT After being on Treatment for 3-5 months", ReportUtils.map(datimIndicators.txMLIITKp3To5MonthsInTx(PWID_CONCEPT), indParams),"");
        cohortDsd.addColumn("TX_ML_MSM_IIT_3_TO_5_MONTHS", "IIT After being on Treatment for 3-5 months", ReportUtils.map(datimIndicators.txMLIITKp3To5MonthsInTx(MSM_CONCEPT), indParams), "");
        cohortDsd.addColumn( "TX_ML_TG_IIT_3_TO_5_MONTHS", "IIT After being on Treatment for 3-5 months", ReportUtils.map(datimIndicators.txMLIITKp3To5MonthsInTx(TG_CONCEPT), indParams), "");
        cohortDsd.addColumn("TX_ML_FSW_IIT_3_TO_5_MONTHS", "IIT After being on Treatment for 3-5 months", ReportUtils.map(datimIndicators.txMLIITKp3To5MonthsInTx(FSW_CONCEPT), indParams), "");
        cohortDsd.addColumn( "TX_ML_PRISONS_CLOSED_SETTINGS_IIT_3_TO_5_MONTHS", "IIT After being on Treatment for 3-5 months", ReportUtils.map(datimIndicators.txMLIITKp3To5MonthsInTx(PRISONERS_CLOSED_SETTINGS_CONCEPT), indParams),"");

        //TX_ML  KPs IIT 6+ MONTHS
        cohortDsd.addColumn( "TX_ML_PWID_IIT_6_MONTHS_AND_ABOVE", "IIT After being on Treatment for 6+ months", ReportUtils.map(datimIndicators.txMLIITKpAtleast6Months(PWID_CONCEPT), indParams),"");
        cohortDsd.addColumn("TX_ML_MSM_IIT_6_MONTHS_AND_ABOVE", "IIT After being on Treatment for 6+ months", ReportUtils.map(datimIndicators.txMLIITKpAtleast6Months(MSM_CONCEPT), indParams), "");
        cohortDsd.addColumn( "TX_ML_TG_IIT_6_MONTHS_AND_ABOVE", "IIT After being on Treatment for 6+ months", ReportUtils.map(datimIndicators.txMLIITKpAtleast6Months(TG_CONCEPT), indParams), "");
        cohortDsd.addColumn("TX_ML_FSW_IIT_6_MONTHS_AND_ABOVE", "IIT After being on Treatment for 6+ months", ReportUtils.map(datimIndicators.txMLIITKpAtleast6Months(FSW_CONCEPT), indParams), "");
        cohortDsd.addColumn( "TX_ML_PRISONS_CLOSED_SETTINGS_IIT_6_MONTHS_AND_ABOVE", "IIT After being on Treatment for 6+ months", ReportUtils.map(datimIndicators.txMLIITKpAtleast6Months(PRISONERS_CLOSED_SETTINGS_CONCEPT), indParams),"");

        //TX_ML  KPs Transferred out
        cohortDsd.addColumn( "TX_ML_PWID_IIT_TOUT", "Transferred Out", ReportUtils.map(datimIndicators.txmlKPsTransferredOut(PWID_CONCEPT), indParams),"");
        cohortDsd.addColumn("TX_ML_MSM_IIT_TOUT", "Transferred Out", ReportUtils.map(datimIndicators.txmlKPsTransferredOut(MSM_CONCEPT), indParams), "");
        cohortDsd.addColumn( "TX_ML_TG_IIT_TOUT", "Transferred Out", ReportUtils.map(datimIndicators.txmlKPsTransferredOut(TG_CONCEPT), indParams), "");
        cohortDsd.addColumn("TX_ML_FSW_IIT_TOUT", "Transferred Out", ReportUtils.map(datimIndicators.txmlKPsTransferredOut(FSW_CONCEPT), indParams), "");
        cohortDsd.addColumn( "TX_ML_PRISONS_CLOSED_SETTINGS_IIT_TOUT", "Transferred Out", ReportUtils.map(datimIndicators.txmlKPsTransferredOut(PRISONERS_CLOSED_SETTINGS_CONCEPT), indParams),"");

        //TX_ML KPs stopped Tx
        cohortDsd.addColumn( "TX_ML_PWID_IIT_STOPPED_TX", "Refused (Stopped) Treatment", ReportUtils.map(datimIndicators.txmlKPStopReason(PWID_CONCEPT), indParams),"");
        cohortDsd.addColumn("TX_ML_MSM_IIT_STOPPED_TX", "Refused (Stopped) Treatment", ReportUtils.map(datimIndicators.txmlKPStopReason(MSM_CONCEPT), indParams), "");
        cohortDsd.addColumn( "TX_ML_TG_IIT_STOPPED_TX", "Refused (Stopped) Treatment", ReportUtils.map(datimIndicators.txmlKPStopReason(TG_CONCEPT), indParams), "");
        cohortDsd.addColumn("TX_ML_FSW_IIT_STOPPED_TX", "Refused (Stopped) Treatment", ReportUtils.map(datimIndicators.txmlKPStopReason(FSW_CONCEPT), indParams), "");
        cohortDsd.addColumn( "TX_ML_IIT_PRISONS_CLOSED_SETTINGS_STOPPED_TX", "Refused (Stopped) Treatment", ReportUtils.map(datimIndicators.txmlKPStopReason(PRISONERS_CLOSED_SETTINGS_CONCEPT), indParams),"");

        //TX_ML Cause of death
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_HIV_TB", "HIV disease resulting in TB", ReportUtils.map(datimIndicators.txMLCauseOfDeath(HIV_DISEASE_RESULTING_IN_TB_CONCEPT), indParams),  datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_HIV_CANCER", "HIV disease resulting in Cancer", ReportUtils.map(datimIndicators.txMLCauseOfDeath(HIV_DISEASE_RESULTING_IN_CANCER), indParams),  datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_HIV_INFECTIOUS_PARASITIC", "HIV disease resulting in other infectious and parasitic disease", ReportUtils.map(datimIndicators.txMLCauseOfDeath(HIV_DISEASE_RESULTING_IN_INFECTIOUS_PARASITIC_DISEASE_CONCEPT), indParams),  datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_HIV_OTHER", "Other HIV disease, resulting in other diseases or conditions leading to death", ReportUtils.map(datimIndicators.txMLCauseOfDeath(OTHER_HIV_DISEASE_CONCEPT), indParams),  datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_OTHER_NATURAL", "Other natural causes", ReportUtils.map(datimIndicators.txMLCauseOfDeath(OTHER_NATURAL_CAUSES_CONCEPT), indParams),  datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_NON_NATURAL", "Non-natural causes", ReportUtils.map(datimIndicators.txMLCauseOfDeath(NON_NATURAL_CAUSES_CONCEPT), indParams),  datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_UNKNOWN", "Unknown Cause", ReportUtils.map(datimIndicators.txMLCauseOfDeath(UNKNOWN_CAUSE_CONCEPT), indParams),  datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));
        EmrReportingUtils.addRow(cohortDsd, "TX_ML_COVID19", "Covid-19", ReportUtils.map(datimIndicators.txMLSpecificCauseOfDeath(COVID_19_CONCEPT), indParams),  datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));

        /**
         * Number of ART patients who experienced IIT during any previous reporting period, who successfully restarted ARVs within the reporting period and remained on treatment until the end of the reporting period
         */
        EmrReportingUtils.addRow(cohortDsd, "TX_RTT_CD4_BELOW_200", "Number restarted Treatment during the reporting period with CD4 count <200", ReportUtils.map(datimIndicators.txRTTCD4Below200(), indParams),  datim5To65PlusAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27"));
        EmrReportingUtils.addRow(cohortDsd, "TX_RTT_CD4_200_AND_ABOVE", "Number restarted Treatment during the reporting period with CD4 count >=200", ReportUtils.map(datimIndicators.txRTTCD4200AndAbove(), indParams),  datim5To65PlusAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27"));
        EmrReportingUtils.addRow(cohortDsd, "TX_RTT_CD4_UNKNOWN", "Number restarted Treatment during the reporting period with CD4 unknown", ReportUtils.map(datimIndicators.txRTTCD4Unknown(), indParams),  datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));
        EmrReportingUtils.addRow(cohortDsd, "TX_RTT_INELIGIBLE_FOR_CD4", "Number restarted Treatment during the reporting period not eligible for CD4", ReportUtils.map(datimIndicators.txRTTIneligibleForCD4(), indParams),  datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));
        /**
         * Number of PWID ART patients who experienced IIT during any previous reporting period, who successfully restarted ARVs within the reporting period and remained on treatment until the end of the reporting period
         */
        cohortDsd.addColumn("TX_RTT_PWID", "PWID with HIV receiving ART", ReportUtils.map(datimIndicators.txRTTKP(PWID_CONCEPT), indParams), "");
        /**
         * Number of MSM ART patients who experienced IIT during any previous reporting period, who successfully restarted ARVs within the reporting period and remained on treatment until the end of the reporting period
         */
        cohortDsd.addColumn("TX_RTT_MSM", "MSM with HIV receiving ART", ReportUtils.map(datimIndicators.txRTTKP(MSM_CONCEPT), indParams), "");
        /**
         * Number of TG ART patients who experienced IIT during any previous reporting period, who successfully restarted ARVs within the reporting period and remained on treatment until the end of the reporting period
         */
        cohortDsd.addColumn("TX_RTT_TG", "Transgenders with HIV receiving ART", ReportUtils.map(datimIndicators.txRTTKP(TG_CONCEPT), indParams), "");
        /**
         * Number of FSW ART patients who experienced IIT during any previous reporting period, who successfully restarted ARVs within the reporting period and remained on treatment until the end of the reporting period
         */
        cohortDsd.addColumn("TX_RTT_FSW", "FSW with HIV receiving ART", ReportUtils.map(datimIndicators.txRTTKP(FSW_CONCEPT), indParams), "");
        /**
         * Number of Prisoners and people in closed settings ART patients who experienced IIT during any previous reporting period, who successfully restarted ARVs within the reporting period and remained on treatment until the end of the reporting period
         */
        cohortDsd.addColumn("TX_RTT_PRISONS_CLOSED_SETTINGS", "Prisoners and Closed settings with HIV receiving ART", ReportUtils.map(datimIndicators.txRTTKP(PRISONERS_CLOSED_SETTINGS_CONCEPT), indParams), "");
        /**
         * Number of ART patients who experienced IIT for less than 3 months, who successfully restarted ARVs within the reporting period and remained on treatment until the end of the reporting period
         */
        cohortDsd.addColumn("TX_RTT_IIT_BELOW_3_MONTHS", "Restarted ARVs within the reporting period after IIT for less than 3 months", ReportUtils.map(datimIndicators.txRTTIITBelow3Months(), indParams), "");
        /**
         * Number of ART patients who experienced IIT between 3 - 5 months, who successfully restarted ARVs within the reporting period and remained on treatment until the end of the reporting period
         */
        cohortDsd.addColumn("TX_RTT_IIT_3_TO_5_MONTHS", "Restarted ARVs within the reporting period after IIT for 3 to 5 months", ReportUtils.map(datimIndicators.txRTTIIT3To5Months(), indParams), "");
        /**
         * Number of ART patients who experienced IIT for 6+ months, who successfully restarted ARVs within the reporting period and remained on treatment until the end of the reporting period
         */
        cohortDsd.addColumn("TX_RTT_IIT_6+_MONTHS", "Restarted ARVs within the reporting period after IIT for more than 6 months", ReportUtils.map(datimIndicators.txRTTIITAtleast6Months(), indParams), "");
        //90-90-90 Viral Suppression
        //TX_PVLS Number of patients on ART with VL results within the past 12 months
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_DENOMINATOR", "On ART with current VL results", ReportUtils.map(datimIndicators.onARTWithVLLast12Months(), indParams),  datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));

        //TX_PVLS Number of Pregnant patients on ART with VL results within the past 12 months
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_PREG", "On ART with current VL results while pregnant", ReportUtils.map(datimIndicators.txpvlsDenominatorPregnant(), indParams),"");

        //TX_PVLS Number of Breastfeeding patients on ART with VL results within the past 12 months
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_BF", "On ART with current VL results while Breastfeeding", ReportUtils.map(datimIndicators.txpvlsDenominatorBreastfeeding(), indParams),"");

        //TX_PVLS_DENOMINATOR_PWID Number of PWID KPs on ART with viral load results within the past 12 months.
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_PWID", "PWID on ART with current VL results ", ReportUtils.map(datimIndicators.kpOnARTWithVLLast12Months(PWID_CONCEPT), indParams), "");

        //TX_PVLS_DENOMINATOR_MSM Number of MSM KPs on ART with viral load results  within the past 12 months
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_MSM", "MSM on ART with current VL results ", ReportUtils.map(datimIndicators.kpOnARTWithVLLast12Months(MSM_CONCEPT), indParams), "");

        //TX_PVLS_DENOMINATOR_TG Number of Transgender KPs on ART with viral load results  within the past 12 months
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_TG", "Transgender on ART with current VL results ", ReportUtils.map(datimIndicators.kpOnARTWithVLLast12Months(TG_CONCEPT), indParams), "");

        //TX_PVLS_DENOMINATOR_FSW Number of FSW KPs on ART with viral load results  within the past 12 months
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_FSW", "FSW on ART with current VL results ", ReportUtils.map(datimIndicators.kpOnARTWithVLLast12Months(FSW_CONCEPT), indParams), "");

        //TX_PVLS_DENOMINATOR_PRISONS_CLOSED_SETTINGS Number of prisoners and people in closed settings KPs on ART with viral load results  within the past 12 months
        cohortDsd.addColumn("TX_PVLS_DENOMINATOR_PRISONS_CLOSED_SETTINGS", "Prisoners and People in closed settings on ART with current VL results ", ReportUtils.map(datimIndicators.kpOnARTWithVLLast12Months(PRISONERS_CLOSED_SETTINGS_CONCEPT), indParams), "");

        //TX_PVLS Number of adults and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months disaggregated by Age/Sex
        EmrReportingUtils.addRow(cohortDsd, "TX_PVLS_SUPP", "On ART with current suppressed VL results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.onARTSuppVLAgeSex(), indParams),  datimAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27","28","29","30","31"));

        //TX_PVLS Number pregnant or breastfeeding patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months
        cohortDsd.addColumn( "TX_PVLS_SUPP_PREG", "Pregnant on ART with current suppressed VL results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.pregnantOnARTSuppressedVLLast12Months(), indParams),"");

        //TX_PVLS Number pregnant or breastfeeding patients on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months
        cohortDsd.addColumn( "TX_PVLS_SUPP_BF", "Breastfeeding on ART with current suppressed VL results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.breastfeedingOnARTSuppressedVLLast12Months(), indParams), "");

        //TX_PVLS Number of PWID KPs on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months.
        cohortDsd.addColumn("TX_PVLS_SUPP_KP_PWID", "PWID on ART with current suppressed VL results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.kpOnARTSuppVLLast12Months( PWID_CONCEPT), indParams), "");

        //TX_PVLS Number of MSM KPs on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months
        cohortDsd.addColumn("TX_PVLS_SUPP_KP_MSM", "MSM on ART with current suppressed VL results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.kpOnARTSuppVLLast12Months(MSM_CONCEPT), indParams), "");

        //TX_PVLS Number of Transgender KPs on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months
        cohortDsd.addColumn("TX_PVLS_SUPP_KP_TG", "Transgender on ART with current suppressed VL results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.kpOnARTSuppVLLast12Months(TG_CONCEPT), indParams), "");

        //TX_PVLS Number of FSW KPs on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months
        cohortDsd.addColumn("TX_PVLS_SUPP_KP_FSW", "FSW on ART with current suppressed VL results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.kpOnARTSuppVLLast12Months(FSW_CONCEPT), indParams), "");

        //TX_PVLS Number of prisoners and people in closed settings KPs on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months
        cohortDsd.addColumn("TX_PVLS_SUPP_KP_PRISONS_CLOSED_SETTINGS", "Prisoners and People in closed settings on ART with current suppressed VL results (<1,000 copies/ml)", ReportUtils.map(datimIndicators.kpOnARTSuppVLLast12Months(PRISONERS_CLOSED_SETTINGS_CONCEPT), indParams), "");

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


