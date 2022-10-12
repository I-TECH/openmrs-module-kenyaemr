/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.heiCohortAnalysis;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HcaIndicatorLibrary {

    @Autowired
    private HcaCohortLibrary HcaCohortLibrary;

    //Indicator Libraries based on Queries and MOH710 dimensions

     public CohortIndicator mothersReceivedPMTCTARVs() {

        return cohortIndicator("HEIs with mothers who received PMTCT ARVs",map(HcaCohortLibrary.mothersReceivedARV12MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator infantReceivedARVsSixWeeks() {

        return cohortIndicator("Infants who received ARVs at 0-6 weeks",map(HcaCohortLibrary.infantReceivedARVsWithinSixWeeks12MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator heiPCRTestSixWeeks() {

        return cohortIndicator("HEI tested with PCR at age 6-8 weeks and results available",map(HcaCohortLibrary.heiPCRTestedSixWeeks12MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator heiPositiveSixWeeks() {

        return cohortIndicator("4. HEI tested positive by first PCR at age 6-8 weeks",map(HcaCohortLibrary.heiPCRTestedPositiveSixWeeks12MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator heiInitialPCRTwelveMonths() {

        return cohortIndicator("HEI tested with Initial PCR and results available between 0 and 12 months",map(HcaCohortLibrary.heiInitialPCRTestWithinTwelveMonths12MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator heiEligibleRepeatPCRSixMonths() {

        return cohortIndicator("Eligible HEI  with repeat PCR done at 6 months and results available",map(HcaCohortLibrary.heiEligibleRepeatPCRSixMonths12MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator heiPositivePCR12Months() {

        return cohortIndicator("HEI tested positive by PCR between 0 and 12 months",map(HcaCohortLibrary.heiPCRTestedPositivePCR12Months12MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator heiExclusiveBFSixMonths() {

        return cohortIndicator("8. HEI who were Exclusively Breastfed at 6 months among HEI assessed",map(HcaCohortLibrary.heiExclusiveBFWithinSixMonths12MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator infantLinkedToCCCTwelveMonths() {

        return cohortIndicator("9. HIV positive infants identified between 0 and 12 months linked to CCC",map(HcaCohortLibrary.heiPCRTestedPositiveAt12MonthsLinkedToCCC12MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator infantBaselineVLwithResults() {

        return cohortIndicator("HIV Positive infants with baseline VL done and results available",map(HcaCohortLibrary.heiWithBaselineDocumented12MonthsVLResults12MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator activeFollowupTwelveMonths() {

        return cohortIndicator("Active in follow-up",map(HcaCohortLibrary.heiActiveFollowupAtTwelveMonths12MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator testedPositiveWithinTwelveMonths() {

        return cohortIndicator("Identified as positive between 0 and 9 months",map(HcaCohortLibrary.heiTestedPositiveWithinTwelveMonths12MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator transferredOutTwelveMonths() {

        return cohortIndicator("Transferred out between 0 and 12 mon",map(HcaCohortLibrary.heiTransferredOutWithinTwelveMonths12MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator missingVisitTwelveMonth() {

        return cohortIndicator("Missing 12 month follow-up visit",map(HcaCohortLibrary.heiMissingVisitTwelveMonths12MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator diedWithinTwelveMonths() {

        return cohortIndicator("Died between 0 and 12 months",map(HcaCohortLibrary.heiDiedWithinTwelveMonths12MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator heiEligibleRepeatPCRTwelveMonths() {

        return cohortIndicator("HEI eligible with repeat PCR done at 12 months",map(HcaCohortLibrary.heiEligibleRepeatPCRTwelveMonths24MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator heiTestedPositiveTwelveMonths() {

        return cohortIndicator("HEI tested positive by PCR at 12",map(HcaCohortLibrary.heiTestedPositiveTwelveMonths24MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator heiPositiveConfirmatoryPCRTwelveMonths() {

        return cohortIndicator("HEI tested positive by confirmatory PCR between 12 and 18 months",map(HcaCohortLibrary.heiPositiveConfirmatoryPCRTwelveMonths24MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator heiEligibleABEigteenMonths() {

        return cohortIndicator("HEI eligible tested by AB test at >= 18 months and results are available",map(HcaCohortLibrary.heiEligibleABEigteenMonths24MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator infantLinkedtoCCCTwelveMonths() {

        return cohortIndicator("HIV positive infants linked to CCC among those testing positive between 12 and 18 months",map(HcaCohortLibrary.heiTestedPositiveLinkedtoCCCTwelveMonths24MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator heiABNegativeEighteenMonths() {

        return cohortIndicator("AB negative at 18 months",map(HcaCohortLibrary.heiABNegativeEighteenMonths24MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator activeNoABTestEighteenMonths() {

        return cohortIndicator("Active at 18 months but no AB test done",map(HcaCohortLibrary.activeHeiNoABTestEighteenMonths24MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator positiveIdentifiedEighteenMonths() {

        return cohortIndicator("Identified as positive between 0 and 18 months",map(HcaCohortLibrary.positiveIdentifiedEighteenMonths24MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator transferredOutEighteenMonths() {

        return cohortIndicator("Transferred out between 0 and 18 months",map(HcaCohortLibrary.transferredOutEighteenMonths24MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator ltfuEighteenMonths() {

        return cohortIndicator("Lost to Follow-Up between 0 and 18 months",map(HcaCohortLibrary.ltfuEighteenMonths24MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator diedEighteenMonths() {

        return cohortIndicator("Died between 0 and 18 months",map(HcaCohortLibrary.diedEighteenMonths24MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator heiCohortRegisteredDenominator12Months() {

        return cohortIndicator("HEI registered in cohort",map(HcaCohortLibrary.heiCohort12Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator activeHeiLessPositiveToDiedSixMonths12MonthsCohort() {

        return cohortIndicator("HEI registered less positive, transferred out, or dead before 6 months",map(HcaCohortLibrary.activeHeiLessPositiveToDiedSixMonths12MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    public CohortIndicator heiWithDocumentedFeedingMethodSixMonths12MonthsCohort() {

        return cohortIndicator("HEI who had feeding status assessed at 6 months",map(HcaCohortLibrary.heiWithDocumentedFeedingMethodSixMonths12MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    public CohortIndicator heiCohortRegisteredDenominator24Months() {

        return cohortIndicator("HEI registered in 24 monthscohort",map(HcaCohortLibrary.heiCohort24Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    public CohortIndicator activeHeiLessPositiveToDiedTwelveMonths24MonthsCohort() {

        return cohortIndicator("HEI registered less positive, transferred out, or dead before 12 months",map(HcaCohortLibrary.activeHeiLessPositiveToDiedTwelveMonths24MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    public CohortIndicator heiWithABTestPositiveAndPCRBetween12And18Months24MonthsCohort() {

        return cohortIndicator("HEI AB test positive between 12 and 18 months who had a confirmatory DNA PCR",map(HcaCohortLibrary.heiWithABTestPositiveAndPCRBetween12And18Months24MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    public CohortIndicator heiWithABTestActiveAndLTFU24MonthsCohort() {

        return cohortIndicator("HEI AB test who is active or ltfu",map(HcaCohortLibrary.heiWithABTestActiveAndLTFU24MonthsCohort(), "startDate=${startDate},endDate=${endDate}")
        );
    }
}

