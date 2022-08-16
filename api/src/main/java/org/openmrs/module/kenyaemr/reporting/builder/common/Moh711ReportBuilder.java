/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.ColumnParameters;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.library.moh711.Moh711IndicatorLibrary;
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

import static org.openmrs.module.kenyacore.report.ReportUtils.map;

/**
 * MOH 711 report
 */
@Component
@Builds({"kenyaemr.common.report.moh711"})
public class Moh711ReportBuilder extends AbstractReportBuilder {

    protected static final Log log = LogFactory.getLog(Moh711ReportBuilder.class);

    @Autowired
    private CommonDimensionLibrary commonDimensions;

    @Autowired
    private Moh711IndicatorLibrary moh711Indicators;

    /**
     * @see org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder#getParameters(org.openmrs.module.kenyacore.report.ReportDescriptor)
     */
    @Override
    protected List<Parameter> getParameters(ReportDescriptor descriptor) {
        return Arrays.asList(
                new Parameter("startDate", "Start Date", Date.class),
                new Parameter("endDate", "End Date", Date.class),
                new Parameter("dateBasedReporting", "", String.class)
        );
    }

    String indParams = "startDate=${startDate},endDate=${endDate}";

    /**
     * @see org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder#buildDataSets(org.openmrs.module.kenyacore.report.ReportDescriptor, org.openmrs.module.reporting.report.definition.ReportDefinition)
     */
    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {
        return Arrays.asList(
                ReportUtils.map(createANCPMTCTDataSet(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(createCacxScreeningDataSet(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(createPNCDataSet(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(createMaternityNewbornDataSet(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(createChildHealthAndNutritionDataSet(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(createTBScreeningDataSet(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    ColumnParameters f10_14 = new ColumnParameters(null, "10-14 years", "gender=F|age=10-14");
    ColumnParameters f15_19 = new ColumnParameters(null, "15-19 years", "gender=F|age=15-19");
    ColumnParameters f20_24 = new ColumnParameters(null, "20-24 years", "gender=F|age=20-24");
    ColumnParameters f25AndAbove = new ColumnParameters(null, "25+ years", "gender=F|age=25+");

    ColumnParameters fUnder25 = new ColumnParameters(null, "<25 years", "gender=F|age=<25");
    ColumnParameters f25_49 = new ColumnParameters(null, "25-49 years", "gender=F|age=25-49");
    ColumnParameters f50AndAbove = new ColumnParameters(null, "50+ years", "gender=F|age=>=50");

    ColumnParameters f0To5Months = new ColumnParameters(null, "0-5 months, Female", "gender=F|age=0-5");
    ColumnParameters m0To5Months = new ColumnParameters(null, "0-5 months, Male", "gender=M|age=0-5");
    ColumnParameters f6To23Months = new ColumnParameters(null, "6-23 months, Female", "gender=F|age=6-23");
    ColumnParameters m6To23Months = new ColumnParameters(null, "6-23 months, Male", "gender=M|age=6-23");
    ColumnParameters f24To59Months = new ColumnParameters(null, "24-59 months, Female", "gender=F|age=24-59");
    ColumnParameters m24To59Months = new ColumnParameters(null, "24-59 months, Male", "gender=M|age=24-59");
    ColumnParameters f6To59Months = new ColumnParameters(null, "6-59 months, Female", "gender=F|age=6-59");
    ColumnParameters m6To59Months = new ColumnParameters(null, "6-59 months, Male", "gender=M|age=6-59");
    ColumnParameters f0To59Months = new ColumnParameters(null, "0-59 months, Female", "gender=F|age=0-59");
    ColumnParameters m0To59Months = new ColumnParameters(null, "0-59 months, Male", "gender=M|age=0-59");
    ColumnParameters f12To59Months = new ColumnParameters(null, "12-59 months, Female", "gender=F|age=12-59");
    ColumnParameters m12To59Months = new ColumnParameters(null, "12-59 months, Male", "gender=M|age=12-59");
    ColumnParameters all0To59Months = new ColumnParameters(null, "0-59 months", "age=0-59");
    ColumnParameters colTotal = new ColumnParameters(null, "Total", "");

    List<ColumnParameters> ancAgeDisaggregations = Arrays.asList(f10_14, f15_19, f20_24);
    List<ColumnParameters> cacxScreeningAgeDisaggregations = Arrays.asList(fUnder25, f25_49, f50AndAbove);
    List<ColumnParameters> maternalAgeDisaggregations = Arrays.asList(f10_14, f15_19, f20_24, f25AndAbove);
    List<ColumnParameters> childWeightAgeDisaggregations = Arrays.asList(f0To5Months, m0To5Months, f6To23Months,
            m6To23Months, f24To59Months, m24To59Months, colTotal);
    List<ColumnParameters> childGrowthAgeDisaggregations = Arrays.asList(f24To59Months, m24To59Months, colTotal);
    List<ColumnParameters> childFollowupTypeAgeDisaggregations = Arrays.asList(f0To59Months, m0To59Months, colTotal);
    List<ColumnParameters> childExclusiveBFAgeDisaggregations = Arrays.asList(f0To5Months, m0To5Months, colTotal);
    List<ColumnParameters> childMUACAgeDisaggregations = Arrays.asList(f6To59Months, m6To59Months, colTotal);
    List<ColumnParameters> childMNPsAgeDisaggregations = Arrays.asList(f6To23Months, m6To23Months, colTotal);
    List<ColumnParameters> childDewormingAgeDisaggregations = Arrays.asList(f12To59Months, m12To59Months, colTotal);
    List<ColumnParameters> childDelayedGrowthAgeDisaggregations = Arrays.asList(all0To59Months);


    /**
     * A. ANC / PMCT
     * Creates ANC/PMTCT dataset
     */
    private DataSetDefinition createANCPMTCTDataSet() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("ANC_PMTCT");
        dsd.setDescription("ANC PMTCT");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addDimension("age", map(commonDimensions.datimFineAgeGroups(), "onDate=${endDate}"));
        dsd.addDimension("gender", map(commonDimensions.gender()));

        dsd.addColumn("New ANC Clients", "", ReportUtils.map(moh711Indicators.noOfNewANCClients(), indParams), "");
        dsd.addColumn("Revisiting ANC Clients", "", ReportUtils.map(moh711Indicators.noOfANCClientsRevisits(), indParams), "");
        dsd.addColumn("Clients given IPT (1st dose)", "", ReportUtils.map(moh711Indicators.noOfANCClientsGivenIPT1stDose(), indParams), "");
        dsd.addColumn("Clients given IPT (2nd dose)", "", ReportUtils.map(moh711Indicators.noOfANCClientsGivenIPT2ndDose(), indParams), "");
        dsd.addColumn("Clients given IPT (3rd dose)", "", ReportUtils.map(moh711Indicators.noOfANCClientsGivenIPT3rdDose(), indParams), "");
        dsd.addColumn("Clients with Hb less than 11 g per dl", "", ReportUtils.map(moh711Indicators.noOfANCClientsLowHB(), indParams), "");
        dsd.addColumn("Clients completed 4 Antenatal Visits", "", ReportUtils.map(moh711Indicators.ancClientsCompleted4Visits(), indParams), "");
        //dsd.addColumn("LLINs distributed to under 1 year", "", ReportUtils.map(moh711Indicators.distributedLLINsUnder1Year(), indParams), "");
        dsd.addColumn("LLINs distributed to ANC clients", "", ReportUtils.map(moh711Indicators.distributedLLINsToANCClients(), indParams), "");

        dsd.addColumn("Clients tested for Syphilis", "", ReportUtils.map(moh711Indicators.ancClientsTestedForSyphillis(), indParams), "");
        dsd.addColumn("Clients tested Positive for Syphilis", "", ReportUtils.map(moh711Indicators.ancClientsTestedSyphillisPositive(), indParams), "");
        dsd.addColumn("Total women done breast examination", "", ReportUtils.map(moh711Indicators.breastExaminationDone(), indParams), "");
        EmrReportingUtils.addRow(dsd, "ANC1", "Presenting with pregnancy at 1st ANC Visit", ReportUtils.map(moh711Indicators.noOfNewANCClients(), indParams), ancAgeDisaggregations, Arrays.asList("01", "02", "03"));
        dsd.addColumn("Women presenting with pregnancy at 1ST ANC in the First Trimeseter(<= 12 Weeks)", "", ReportUtils.map(moh711Indicators.presentingPregnancy1stANC1stTrimester(), indParams), "");
        dsd.addColumn("Clients issued with Iron", "", ReportUtils.map(moh711Indicators.ancClientsIssuedWithIron(), indParams), "");
        dsd.addColumn("Clients issued with Folic", "", ReportUtils.map(moh711Indicators.ancClientsIssuedWithFolic(), indParams), "");
        dsd.addColumn("Clients issued with Combined Ferrous Folate", "", ReportUtils.map(moh711Indicators.ancClientsIssuedWithFerrousFolic(), indParams), "");
        //dsd.addColumn("Pregnant women presenting in ANC with complication associated with FGM", "", ReportUtils.map(moh711Indicators.ancClientsWithFGMRelatedComplications(), indParams), "");

        return dsd;
    }

    /**
     * G. Cervical Cancer Screening Dataset
     * @return the data set
     */
    private DataSetDefinition createCacxScreeningDataSet() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("CACX-SCREENING");
        dsd.setDescription("CACX Screening");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addDimension("age", ReportUtils.map(commonDimensions.moh745AgeGroups(), "onDate=${endDate}"));
        dsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));
        EmrReportingUtils.addRow(dsd, "ANC_CACX", "No.of Client receiving VIA /VILI /HPV VILI / HPV", ReportUtils.map(moh711Indicators.cacxScreened(), indParams), cacxScreeningAgeDisaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd, "CACX_Pap_Smear", "No.Screened for Pap smear", ReportUtils.map(moh711Indicators.cacxScreenedWithMethod("Pap Smear", 885), indParams), cacxScreeningAgeDisaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd, "CACX_HPV", "No.Screened for HPV test", ReportUtils.map(moh711Indicators.cacxScreenedWithMethod("HPV Test", 159895), indParams), cacxScreeningAgeDisaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd, "VIA_VILI", "Number of clients with Positive VIA/VILI result", ReportUtils.map(moh711Indicators.viaViliPositive(), indParams), cacxScreeningAgeDisaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd, "HPV", "Number of clients with Positive HPV result", ReportUtils.map(moh711Indicators.hpvPositive(), indParams), cacxScreeningAgeDisaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd, "Suspicious_CACX_Lessions", "Number of clients with suspicious cancer lesions", ReportUtils.map(moh711Indicators.suspiciousCancerLessions(), indParams), cacxScreeningAgeDisaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd, "Cryotherapy", "Number of clients treated using Cryotherapy", ReportUtils.map(moh711Indicators.treatedUsingCyrotherapy(), indParams), cacxScreeningAgeDisaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd, "LEEP", "Number of clients treated using LEEP", ReportUtils.map(moh711Indicators.treatedUsingLEEP(), indParams), cacxScreeningAgeDisaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd, "HIV+_CACX_Screened", "Number of HIV positive clients screened", ReportUtils.map(moh711Indicators.cacxScreenedAndHIVPositive(), indParams), cacxScreeningAgeDisaggregations, Arrays.asList("01", "02", "03"));
        return dsd;
    }

    /**
     *  H. Post Natal Care (PNC) Dataset
     * @return
     */
    private DataSetDefinition createPNCDataSet() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("PNC");
        dsd.setDescription("Post Natal Care (PNC)");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addDimension("age", ReportUtils.map(commonDimensions.moh745AgeGroups(), "onDate=${endDate}"));
        dsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

        dsd.addColumn("New PNC Clients", "", ReportUtils.map(moh711Indicators.noOfNewPNCClients(), indParams), "");
        dsd.addColumn("Revisiting PNC Clients", "", ReportUtils.map(moh711Indicators.noOfPNCClientsRevisits(), indParams), "");
        dsd.addColumn("Mothers received PostParturm care within 48 hrs", "", ReportUtils.map(moh711Indicators.motherPPCWithin48hrs(), indParams), "");
        dsd.addColumn("Mothers received PostParturm care btw 3 days and 6 weeks", "", ReportUtils.map(moh711Indicators.motherPPCbtw3And42Days(), indParams), "");
        dsd.addColumn("Mothers received PostParturm care after 6 weeks", "", ReportUtils.map(moh711Indicators.motherPPCAfter6weeks(), indParams), "");

        dsd.addColumn("Babies received PostParturm care within 48 hrs", "", ReportUtils.map(moh711Indicators.babyPPCWithin48hrs(), indParams), "");
        dsd.addColumn("Babies received PostParturm care btw 3 days and 6 weeks", "", ReportUtils.map(moh711Indicators.babyPPCbtw3And42Days(), indParams), "");
        dsd.addColumn("Babies received PostParturm care after 6 weeks", "", ReportUtils.map(moh711Indicators.babyPPCAfter6weeks(), indParams), "");

        dsd.addColumn("Number of Cases of Fistula", "", ReportUtils.map(moh711Indicators.noOfFistulaCasesPNC(), indParams), "");
        dsd.addColumn("No Referred from the community unit for PNC", "", ReportUtils.map(moh711Indicators.noReferredFromCommunityForPNC(), indParams), "");

        return dsd;
    }

    /**
     * B. Maternity and newborn
     * @return
     */
    private DataSetDefinition createMaternityNewbornDataSet() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("Maternity_Newborn");
        dsd.setDescription("Maternity and Newborn");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addDimension("age", ReportUtils.map(commonDimensions.datimFineAgeGroups(), "onDate=${endDate}"));
        dsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

        dsd.addColumn("Normal Deliveries", "", ReportUtils.map(moh711Indicators.normalDelivery(1170), indParams), "");
        dsd.addColumn("Caesarean Sections", "", ReportUtils.map(moh711Indicators.caesareanSection(1171), indParams), "");
        dsd.addColumn("Breech Delivery", "", ReportUtils.map(moh711Indicators.breechDelivery(1172), indParams), "");
        dsd.addColumn("Assisted Vaginal Deliveries (Vacuum Extraction)", "", ReportUtils.map(moh711Indicators.assistedVaginalDelivery(118159), indParams), "");
        dsd.addColumn("Live Births", "", ReportUtils.map(moh711Indicators.liveBirths(), indParams), "");
        dsd.addColumn("Low birth weight Babies (below 2500 grams)", "", ReportUtils.map(moh711Indicators.lowBirthWeight(), indParams), "");
        dsd.addColumn("Births with deformities", "", ReportUtils.map(moh711Indicators.deformities(), indParams), "");
        //dsd.addColumn("Neonates given Vit K", "", ReportUtils.map(moh711Indicators.givenVitaminK(), indParams), "");
        //dsd.addColumn("Babies applied chlorhexidine for cord care", "", ReportUtils.map(moh711Indicators.chlorhexidineForCordCaregiven(), indParams), "");
        //dsd.addColumn("Neonates 0-28 days put on Continous Positive Airway Pressure(CPAP)", "", ReportUtils.map(moh711Indicators.continousPositiveAirwayPressureAt0To28Days(), indParams), "");
        dsd.addColumn("Babies given tetracycline at birth", "", ReportUtils.map(moh711Indicators.givenTetracyclineAtBirth(), indParams), "");
        dsd.addColumn("Pre-Term babies", "", ReportUtils.map(moh711Indicators.preTermBabies(), indParams), "");
        dsd.addColumn("Babies discharged alive", "", ReportUtils.map(moh711Indicators.dischargedAlive(), indParams), "");
        dsd.addColumn("Infants initiated on breastfeeding within 1 hour after birth", "", ReportUtils.map(moh711Indicators.initiatedBFWithinOneHour(), indParams), "");
        dsd.addColumn("Total Deliveries from HIV+ mother", "", ReportUtils.map(moh711Indicators.deliveryFromHIVPosMother(), indParams), "");
        dsd.addColumn("Perinatal Deaths (Fresh still birth)", "", ReportUtils.map(moh711Indicators.perinatalFreshStillBirth(), indParams), "");
        dsd.addColumn("Perinatal Deaths(Macerated still birth)", "", ReportUtils.map(moh711Indicators.perinatalMaceratedStillBirth(), indParams), "");
        dsd.addColumn("Perinatal Deaths 0 to 7 days", "", ReportUtils.map(moh711Indicators.perinatalDeathWithin0To7Days(), indParams), "");
        dsd.addColumn("Neonatal Deaths", "Death 0-28 days", ReportUtils.map(moh711Indicators.perinatalDeathWithin0To28Days(), indParams), "");
        EmrReportingUtils.addRow(dsd, "Maternal deaths", "", ReportUtils.map(moh711Indicators.maternalDeath(), indParams), maternalAgeDisaggregations, Arrays.asList("01", "02", "03", "04"));
        dsd.addColumn("Maternal deaths audited within 7 days", "", ReportUtils.map(moh711Indicators.maternalDeathAuditedWithin7Days(), indParams), "");
        dsd.addColumn("Ante Partum Haemorrhage(APH) Alive", "", ReportUtils.map(moh711Indicators.antePartumHaemorrhage(160429), indParams), "");
        dsd.addColumn("Ante Partum Haemorrhage(APH) Dead", "", ReportUtils.map(moh711Indicators.antePartumHaemorrhage(134612), indParams), "");
        dsd.addColumn("Post Partum Haemorrhage(PPH) Alive", "", ReportUtils.map(moh711Indicators.postPartumHaemorrhage(160429), indParams), "");
        dsd.addColumn("Post Partum Haemorrhage(PPH) Dead", "", ReportUtils.map(moh711Indicators.postPartumHaemorrhage(134612), indParams), "");
        dsd.addColumn("Eclampsia Alive", "", ReportUtils.map(moh711Indicators.eclampsia(160429), indParams), "");
        dsd.addColumn("Eclampsia Dead", "", ReportUtils.map(moh711Indicators.eclampsia(134612), indParams), "");
        dsd.addColumn("Ruptured Uterus Alive", "", ReportUtils.map(moh711Indicators.rupturedUterus(160429), indParams), "");
        dsd.addColumn("Ruptured Uterus Dead", "", ReportUtils.map(moh711Indicators.rupturedUterus(134612), indParams), "");
        dsd.addColumn("Obstructed Labour Alive", "", ReportUtils.map(moh711Indicators.obstructedLabour(160429), indParams), "");
        dsd.addColumn("Obstructed Labour Dead", "", ReportUtils.map(moh711Indicators.obstructedLabour(134612), indParams), "");
        dsd.addColumn("Sepsis Alive", "", ReportUtils.map(moh711Indicators.sepsis(160429), indParams), "");
        dsd.addColumn("Sepsis Dead", "", ReportUtils.map(moh711Indicators.sepsis(134612), indParams), "");
        //dsd.addColumn("Number of Mothers with delivery complications associated with FGM", "Alive", ReportUtils.map(moh711Indicators.fgmRelatedComplicatiobs(), indParams), "");
        //dsd.addColumn("Number of Mothers with delivery complications associated with FGM", "Dead", ReportUtils.map(moh711Indicators.fgmRelatedComplicatiobs(), indParams), "");

        return dsd;
    }

    /**
     *F. Child Health and Nutrition Information System
     * @return
     */
    private DataSetDefinition createChildHealthAndNutritionDataSet() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("Child_Health_Nutrition");
        dsd.setDescription("Child Health and Nutrition Information System");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addDimension("age", ReportUtils.map(commonDimensions.childAgeGroups(), "onDate=${endDate}"));
        dsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

        EmrReportingUtils.addRow(dsd, "Normal_Weight_for_Age", "", ReportUtils.map(moh711Indicators.normalWeightForAge(), indParams), childWeightAgeDisaggregations, Arrays.asList("01", "02", "03", "04", "05", "06", "07"));
        EmrReportingUtils.addRow(dsd, "Underweight", "", ReportUtils.map(moh711Indicators.underWeight(), indParams), childWeightAgeDisaggregations, Arrays.asList("01", "02", "03", "04", "05", "06", "07"));
        EmrReportingUtils.addRow(dsd, "Severe_Underweight", "", ReportUtils.map(moh711Indicators.severeUnderWeight(), indParams), childWeightAgeDisaggregations, Arrays.asList("01", "02", "03", "04", "05", "06", "07"));
        EmrReportingUtils.addRow(dsd, "Overweight", "", ReportUtils.map(moh711Indicators.overweight(), indParams), childWeightAgeDisaggregations, Arrays.asList("01", "02", "03", "04", "05", "06", "07"));
        EmrReportingUtils.addRow(dsd, "Obese", "", ReportUtils.map(moh711Indicators.obese(), indParams), childWeightAgeDisaggregations, Arrays.asList("01", "02", "03", "04", "05", "06", "07"));
        // EmrReportingUtils.addRow(dsd, "Total_Weighed ", "", ReportUtils.map(moh711Indicators.totalWeighed(), indParams), childAgeDisaggregations, Arrays.asList("01", "02", "03", "04","05","06","07","08","09","10","11","12","13"));
        EmrReportingUtils.addRow(dsd, "MUAC_Normal(Green)", "", ReportUtils.map(moh711Indicators.normalMUAC(), indParams), childMUACAgeDisaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd, "MUAC_Moderate(Yellow)", "", ReportUtils.map(moh711Indicators.moderateMUAC(), indParams), childMUACAgeDisaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd, "MUAC_Severe(Red)", "", ReportUtils.map(moh711Indicators.severeMUAC(), indParams), childMUACAgeDisaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd, "Stunted", "", ReportUtils.map(moh711Indicators.stuntedGrowth(), indParams), childGrowthAgeDisaggregations, Arrays.asList("01", "02", "03"));
        //EmrReportingUtils.addRow(dsd, "Total_Measured", "", ReportUtils.map(moh711Indicators.totalMeasured(), indParams), childAgeDisaggregations, Arrays.asList("01", "02", "03","04"));
        EmrReportingUtils.addRow(dsd, "New_Enrollment", "", ReportUtils.map(moh711Indicators.newlyEnrolledMchs(), indParams), childFollowupTypeAgeDisaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd, "Followup_type_Kwashiorkor", "", ReportUtils.map(moh711Indicators.kwashiorkor(), indParams), childFollowupTypeAgeDisaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd, "Followup_type_Marasmus", "", ReportUtils.map(moh711Indicators.marasmus(), indParams), childFollowupTypeAgeDisaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd, "Exclusive_breast_feeding", "", ReportUtils.map(moh711Indicators.exclusiveBreastFeeding(), indParams), childExclusiveBFAgeDisaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd, "Dewormed", "", ReportUtils.map(moh711Indicators.dewormed(), indParams), childDewormingAgeDisaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd, "MNPs_Supplementation", "", ReportUtils.map(moh711Indicators.mnpsSupplementation(), indParams), childMNPsAgeDisaggregations, Arrays.asList("01", "02", "03"));
        EmrReportingUtils.addRow(dsd, "Child Mortality", "", ReportUtils.map(moh711Indicators.childrenDiscontinuationReasonDied(), indParams), childDelayedGrowthAgeDisaggregations, Arrays.asList("01"));
        EmrReportingUtils.addRow(dsd, "Children_With_Disability_Any_Form", "", ReportUtils.map(moh711Indicators.childrenWithDisability(), indParams), childDelayedGrowthAgeDisaggregations, Arrays.asList("01"));
        EmrReportingUtils.addRow(dsd, "Children_with_delayed_developmental_milestones", "", ReportUtils.map(moh711Indicators.childrenWithDelayedDevelopmentalMilestones(), indParams), childDelayedGrowthAgeDisaggregations, Arrays.asList("01"));
        return dsd;
    }

    private DataSetDefinition createTBScreeningDataSet() {
        CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
        dsd.setName("TB_Screening");
        dsd.setDescription("TB Screening");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        dsd.addDimension("age", ReportUtils.map(commonDimensions.childAgeGroups(), "onDate=${endDate}"));
        dsd.addDimension("gender", ReportUtils.map(commonDimensions.gender()));

        dsd.addColumn("Total Number of people screened", "", ReportUtils.map(moh711Indicators.clientTbScreening(), indParams), "");
        dsd.addColumn("Total Number of presumptive TB cases", "", ReportUtils.map(moh711Indicators.clientWithPresumptiveTb(), indParams), "");
        dsd.addColumn("Total Number already on TB treatment", "", ReportUtils.map(moh711Indicators.clientonTbTreatment(), indParams), "");
        dsd.addColumn("Total Number of people not screened", "", ReportUtils.map(moh711Indicators.clientTbNotScreened(), indParams), "");

        return dsd;
    }
}