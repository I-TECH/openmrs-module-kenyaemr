/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.cwc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.heiCohortAnalysis.HcaIndicatorLibrary;
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
 * HCA Report
 */
@Component
@Builds({"kenyaemr.mchcs.report.hcareport"})
public class HeiCohortAnalysisReportBuilder extends AbstractReportBuilder {

    protected static final Log log = LogFactory.getLog(HeiCohortAnalysisReportBuilder.class);

    @Autowired
    private HcaIndicatorLibrary HcaIndicators;

    @Autowired
    private CommonDimensionLibrary commonDimensions;

    /**
     * @see org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder#getParameters(org.openmrs.module.kenyacore.report.ReportDescriptor)
     */
    @Override
    protected List<Parameter> getParameters(ReportDescriptor descriptor) {
        return Arrays.asList(
                new Parameter("startDate", "Start Date", Date.class),
                new Parameter("endDate", "End Date", Date.class)
                //new Parameter("dateBasedReporting", "", String.class)
        );
    }

    /**
     * @see org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder#buildDataSets(org.openmrs.module.kenyacore.report.ReportDescriptor, org.openmrs.module.reporting.report.definition.ReportDefinition)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {
        return Arrays.asList(
                ReportUtils.map(heiCohortFirstReview(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(heiCohortSecondReview(), "startDate=${startDate},endDate=${endDate}"),
                ReportUtils.map(heiCohortDenominators(), "startDate=${startDate},endDate=${endDate}")
        );
    }


    /**
     * Creates the dataset for heiCohortAnalysisDataSet
     *
     * @return the dataset
     */
    protected DataSetDefinition heiCohortFirstReview() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("First_Review");
        cohortDsd.setDescription("12 Months Cohort");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        String indParams = "startDate=${startDate},endDate=${endDate}";
        cohortDsd.addColumn("HEI Mothers who received ARVs", "HEI with mothers who received PMTCT ARVs", ReportUtils.map(HcaIndicators.mothersReceivedPMTCTARVs(), indParams),"");
        cohortDsd.addColumn("HEI who received ARVs", "HEI who received ARVs at 0-6 weeks", ReportUtils.map(HcaIndicators.infantReceivedARVsSixWeeks(), indParams),"");
        cohortDsd.addColumn("HEI tested with PCR", "HEI tested with PCR at age 6-8 weeks and results available", ReportUtils.map(HcaIndicators.heiPCRTestSixWeeks(), indParams),"");
        cohortDsd.addColumn("HEI tested positive 6-8 weeks", "HEI tested positive by first PCR at age 6-8 weeks", ReportUtils.map(HcaIndicators.heiPositiveSixWeeks(), indParams),"");
        cohortDsd.addColumn("HEI tested with Initial PCR", "HEI tested with Initial PCR and results available between 0 and 12 months", ReportUtils.map(HcaIndicators.heiInitialPCRTwelveMonths(), indParams),"");
        cohortDsd.addColumn("Eligible HEI with repeat PCR", "Eligible HEI with repeat PCR done at 6 months and results available", ReportUtils.map(HcaIndicators.heiEligibleRepeatPCRSixMonths(), indParams),"");
        cohortDsd.addColumn("HEI tested positive 0 and 12 months", "HEI tested positive by PCR between 0 and 12 months", ReportUtils.map(HcaIndicators.heiPositivePCR12Months(), indParams),"");
        cohortDsd.addColumn("HEI who were Exclusively Breastfed", "HEI who were Exclusively Breastfed within 6 months among HEI assessed", ReportUtils.map(HcaIndicators.heiExclusiveBFSixMonths(), indParams),"");
        cohortDsd.addColumn("HIV positive infants linked to CCC", "HIV positive infants identified between 0 and 12 months linked to CCC", ReportUtils.map(HcaIndicators.infantLinkedToCCCTwelveMonths(), indParams),"");
        cohortDsd.addColumn("HIV Positive infants with baseline VL", "HIV Positive infants with baseline VL done and results available", ReportUtils.map(HcaIndicators.infantBaselineVLwithResults(), indParams),"");
        cohortDsd.addColumn("Active in follow-up 12 months", "Active in follow-up 12 months", ReportUtils.map(HcaIndicators.activeFollowupTwelveMonths(), indParams),"");
        cohortDsd.addColumn("Identified positive 12 months", "Identified as positive between 0 and 12 months", ReportUtils.map(HcaIndicators.testedPositiveWithinTwelveMonths(), indParams),"");
        cohortDsd.addColumn("Transferred out 12 months", "Transferred out between 0 and 12 months", ReportUtils.map(HcaIndicators.transferredOutTwelveMonths(), indParams),"");
        cohortDsd.addColumn("Missing 12 month follow-up visit", "Missing 12 month follow-up visit", ReportUtils.map(HcaIndicators.missingVisitTwelveMonth(), indParams),"");
        cohortDsd.addColumn("Died between 0 and 9 months", "Died between 0 and 9 months", ReportUtils.map(HcaIndicators.diedWithinTwelveMonths(), indParams),"");

        return cohortDsd;
    }

    protected DataSetDefinition heiCohortSecondReview() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("Second_Review");
        cohortDsd.setDescription("24 Months Cohort");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        String indParams = "startDate=${startDate},endDate=${endDate}";
        cohortDsd.addColumn("HEI PCR test eligible repeat PCR 12 months", "HEI eligible with repeat PCR done at 12 months", ReportUtils.map(HcaIndicators.heiEligibleRepeatPCRTwelveMonths(), indParams),"");
        cohortDsd.addColumn("HEI tested positive 12 months", "HEI tested positive by PCR at 12", ReportUtils.map(HcaIndicators.heiTestedPositiveTwelveMonths(), indParams),"");
        cohortDsd.addColumn("HEI tested positive confirmatory PCR  12 and 18 months", "HEI tested positive by confirmatory PCR between 12 and 18 months", ReportUtils.map(HcaIndicators.heiPositiveConfirmatoryPCRTwelveMonths(), indParams),"");
        cohortDsd.addColumn("HEI eligible tested by AB test at >= 18 months", "HEI eligible tested by AB test at >= 18 months and results are available", ReportUtils.map(HcaIndicators.heiEligibleABEigteenMonths(), indParams),"");
        cohortDsd.addColumn("HIV positive and  linked to CCC 12 and 18 months", "HIV positive infants linked to CCC among those testing positive between 12 and 18 months", ReportUtils.map(HcaIndicators.infantLinkedtoCCCTwelveMonths(), indParams),"");
        cohortDsd.addColumn("HEI AB negative at 18 months", "HEI AB negative at 18 months", ReportUtils.map(HcaIndicators.heiABNegativeEighteenMonths(), indParams),"");
        cohortDsd.addColumn("HEI Active at 18 months but no AB test done", "", ReportUtils.map(HcaIndicators.activeNoABTestEighteenMonths(), indParams),"");
        cohortDsd.addColumn("HEI Identified as positive between 0 and 18 months", "HEI Identified as positive between 0 and 18 months", ReportUtils.map(HcaIndicators.positiveIdentifiedEighteenMonths(), indParams),"");
        cohortDsd.addColumn("HEI Transferred out between 0 and 18 months", "HEI Transferred out between 0 and 18 months", ReportUtils.map(HcaIndicators.transferredOutEighteenMonths(), indParams),"");
        cohortDsd.addColumn("Lost to Follow-Up between 0 and 18 months", "Lost to Follow-Up between 0 and 18 months", ReportUtils.map(HcaIndicators.ltfuEighteenMonths(), indParams),"");
        cohortDsd.addColumn("Died between 0 and 18 months", "Died between 0 and 18 months", ReportUtils.map(HcaIndicators.diedEighteenMonths(), indParams),"");

        return cohortDsd;
    }
    protected DataSetDefinition heiCohortDenominators() {
        CohortIndicatorDataSetDefinition cohortDsd = new CohortIndicatorDataSetDefinition();
        cohortDsd.setName("Denominator");
        cohortDsd.setDescription("HCA Denominators");
        cohortDsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cohortDsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        String indParams = "startDate=${startDate},endDate=${endDate}";
        cohortDsd.addColumn("HEI registered cohort 12 Months", "HEI registered in original 12 months cohort", ReportUtils.map(HcaIndicators.heiCohortRegisteredDenominator12Months(), indParams),"");
        cohortDsd.addColumn("Active HEI 6 months", "HEI registered less positive, transferred out, or dead before 6 months", ReportUtils.map(HcaIndicators.activeHeiLessPositiveToDiedSixMonths12MonthsCohort(), indParams),"");
        cohortDsd.addColumn("HEI feeding assessed 6 months", "HEI who had feeding status assessed at 6 months", ReportUtils.map(HcaIndicators.heiWithDocumentedFeedingMethodSixMonths12MonthsCohort(), indParams),"");
        cohortDsd.addColumn("HEI registered cohort 24 Months", "HEI registered in original 24 monthscohort", ReportUtils.map(HcaIndicators.heiCohortRegisteredDenominator24Months(), indParams),"");
        cohortDsd.addColumn("Active HEI 12 months", "HEI registered less positive, transferred out, or dead before 12 months", ReportUtils.map(HcaIndicators.activeHeiLessPositiveToDiedTwelveMonths24MonthsCohort(), indParams),"");
        cohortDsd.addColumn("HEI AB Positive with PCR", "HEI AB test positive between 12 and 18 months who had a confirmatory DNA PCR", ReportUtils.map(HcaIndicators.heiWithABTestPositiveAndPCRBetween12And18Months24MonthsCohort(), indParams),"");
        cohortDsd.addColumn("HEI AB test who is active or ltfu", "HEI AB test who is active or ltfu", ReportUtils.map(HcaIndicators.heiWithABTestActiveAndLTFU24MonthsCohort(), indParams),"");
        return cohortDsd;
    }
}