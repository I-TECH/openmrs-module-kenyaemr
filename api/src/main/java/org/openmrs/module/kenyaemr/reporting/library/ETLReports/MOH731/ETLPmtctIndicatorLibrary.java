package org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731;

import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Created by dev on 1/18/17.
 */
@Component
public class ETLPmtctIndicatorLibrary {

    @Autowired
    private ETLPmtctCohortLibrary pmtctCohortLibrary;

    /**
     * Number of patients who tested for HIV in MCHMS during any {@link org.openmrs.module.kenyaemr.PregnancyStage} before or after enrollment
     *
     * @return the indicator
     */
    public CohortIndicator testedForHivBeforeOrDuringMchms() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.mchKnownPositiveTotal(), "startDate=${startDate},endDate=${endDate}")
        );
    }


    /**
     * Number of patients who tested for HIV in MCHMS during any {@link org.openmrs.module.kenyaemr.PregnancyStage} after enrollment
     *
     * @return the indicator
     */
    public CohortIndicator testedForHivInMchms() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.testedForHivInMchmsTotal(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested for HIV in MCHMS during the ANTENATAL {@link org.openmrs.module.kenyaemr.PregnancyStage}
     *
     * @return the indicator
     */
    public CohortIndicator testedForHivInMchmsAntenatal() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.testedForHivInMchmsAntenatal(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested for HIV in MCHMS during the DELIVERY {@link org.openmrs.module.kenyaemr.PregnancyStage}
     *
     * @return the indicator
     */
    public CohortIndicator testedForHivInMchmsDelivery() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.testedForHivInMchmsDelivery(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested for HIV in MCHMS during the POSTNATAL {@link org.openmrs.module.kenyaemr.PregnancyStage}
     *
     * @return the indicator
     */
    public CohortIndicator testedForHivInMchmsPostnatal() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.testedForHivInMchmsPostnatal(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested HIV Positive in MCHMS during any {@link org.openmrs.module.kenyaemr.PregnancyStage} after enrollment
     *
     * @return the indicator
     */
    public CohortIndicator testedHivPositiveInMchms() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.testedHivPositiveInMchmsTotal(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested HIV Positive in MCHMS during the ANTENATAL {@link org.openmrs.module.kenyaemr.PregnancyStage}
     *
     * @return the indicator
     */
    public CohortIndicator testedHivPositiveInMchmsAntenatal() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.testedHivPositiveInMchmsAntenatal(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested for HIV in MCHMS during the DELIVERY {@link org.openmrs.module.kenyaemr.PregnancyStage}
     *
     * @return the indicator
     */
    public CohortIndicator testedHivPositiveInMchmsDelivery() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.testedHivPositiveInMchmsDelivery(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested HIV Positive in MCHMS during the POSTNATAL {@link org.openmrs.module.kenyaemr.PregnancyStage}
     *
     * @return the indicator
     */
    public CohortIndicator testedHivPositiveInMchmsPostnatal() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.testedHivPositiveInMchmsPostnatal(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested HIV +ve before MCHMS
     *
     * @return the indicator
     */
    public CohortIndicator testedHivPositiveBeforeMchms() {

        return cohortIndicator(null,
                map(pmtctCohortLibrary.testedHivPositiveBeforeMchms(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients whose partners tested HIV +ve or -ve in MCHMS during either their ANTENATAL or DELIVERY
     * {@link org.openmrs.module.kenyaemr.PregnancyStage}
     *
     * @return the indicator
     */
    public CohortIndicator partnerTestedDuringAncOrDelivery() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.partnerTestedDuringAncOrDelivery(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of MCHMS patients whose HIV status is discordant with that of their male partners
     *
     * @return the cohort definition
     */

    public CohortIndicator discordantCouples() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.discordantCouples(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator assessedForArtEligibilityWho() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.assessedForArtEligibilityWho(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    public CohortIndicator assessedForArtEligibilityCd4() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.assessedForArtEligibilityCd4(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     *
     */
    public CohortIndicator assessedForArtEligibilityTotal() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.assessedForArtEligibilityTotal(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of infant patients who took pcr test aged 2 months and below
     * @return the indicator
     */
    public CohortIndicator pcrWithInitialIn2Months() {
        return cohortIndicator("Infants given pcr within 2 months",
                map(pmtctCohortLibrary.pcrWithInitialIn2Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of infant patients who took pcr test aged between 3 and 8 months
     * @return the indicator
     */
    public CohortIndicator pcrWithInitialBetween3And8MonthsOfAge() {
        return cohortIndicator("Infants given pcr between 3 and 8 months of age",
                map(pmtctCohortLibrary.pcrWithInitialBetween3And8MonthsOfAge(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of infant patients who took antibody test aged between 9 and 12 months
     * @return the indicator
     */
    public CohortIndicator serologyAntBodyTestBetween9And12Months() {
        return cohortIndicator("Infants given antibody aged between 9 and 12 months",
                map(pmtctCohortLibrary.serologyAntBodyTestBetween9And12Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of infant patients who took PCR test aged between 9 and 12 months
     * @return the indicator
     */
    public CohortIndicator pcrTestBetween9And12Months() {
        return cohortIndicator("Infants given pcr aged between 9 and 12 months",
                map(pmtctCohortLibrary.pcrTestBetween9And12MonthsAge(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Total number HEI tested by 12 months
     * @return the indicator
     */
    public CohortIndicator totalHeiTestedBy12Months() {
        return cohortIndicator("Total HEI tested by 12 months",
                map(pmtctCohortLibrary.totalHeitestedBy12Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of infant patients who took pcr test aged 2 months and below and confirmed Positive
     * @return the indicator
     */
    public CohortIndicator pcrConfirmedPositive2Months() {
        return cohortIndicator("Infants pcr confirmed Psoitive within 2 months",
                map(pmtctCohortLibrary.pcrConfirmedPositive2Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of infant patients who took pcr test aged between 3 and 8 months and confirmed Positive
     * @return the indicator
     */
    public CohortIndicator pcrConfirmedPositiveBetween3To8Months() {
        return cohortIndicator("Infants pcr confirmed Psoitive between 3 and 8 months of age",
                map(pmtctCohortLibrary.pcrConfirmedPositiveBetween3To8Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*
     * Number of infant patients who took PCR test aged between 9 and 12 months and Confirmed Positive
     * @return the indicator
     */
    public CohortIndicator pcrConfirmedPositiveBetween9To12Months() {
        return cohortIndicator("Infants pcr confirmed Psoitive aged between 9 and 12 months",
                map(pmtctCohortLibrary.pcrConfirmedPositiveBetween9To12Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Total number HEI tested by 12 months
     * @return the indicator
     */
    public CohortIndicator pcrTotalConfirmedPositive() {
        return cohortIndicator("Total HEI confirmed Psoitive by 12 months",
                map(pmtctCohortLibrary.totalHeiConfirmedPositiveBy12Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * exclusive breast feeding at 6 months
     * @return indicator
     */
    public CohortIndicator exclusiveBreastFeedingAtSixMonths() {
        return cohortIndicator("Exclusive Breast Feeding at 6 months",
                map(pmtctCohortLibrary.exclusiveBreastFeedingAtSixMonths(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * exclusive replacement feeding at 6 months
     * @return indicator
     */
    public CohortIndicator exclusiveReplacementFeedingAtSixMonths() {
        return cohortIndicator("Exclusive Replacement Breast Feeding at 6 Months",
                map(pmtctCohortLibrary.exclusiveReplacementFeedingAtSixMonths(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * mixed feeding at 6 months
     * @return indicator
     */
    public CohortIndicator mixedFeedingAtSixMonths() {
        return cohortIndicator("Mixed Feeding at 6 Months",
                map(pmtctCohortLibrary.mixedFeedingAtSixMonths(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Total Exposed at 6 months
     * @return indicator
     */
    public CohortIndicator totalExposedAgedSixMoths() {
        return cohortIndicator("Total Exposed at 6 Months",
                map(pmtctCohortLibrary.totalExposedAgedSixMonths(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Mother on ARV treatment and breast feeding
     * @return indicator
     */
    public CohortIndicator motherOnTreatmentAndBreastFeeding() {
        return cohortIndicator("Mother on treatment and breast feeding", map(pmtctCohortLibrary.motherOnTreatmentAndBreastFeeding(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Mother on ARV treatment and NOT breast feeding
     * @return indicator
     */
    public CohortIndicator motherOnTreatmentAndNotBreastFeeding() {
        return cohortIndicator("Mother on treatment and NOT breast feeding", map(pmtctCohortLibrary.motherOnTreatmentAndNotBreastFeeding(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Mother on ARV treatment and if breastfeeding NOT known
     * @return indicator
     */
    public CohortIndicator motherOnTreatmentAndNotBreastFeedingUnknown() {
        return cohortIndicator("Mother on treatment and breast feeding unknown", map(pmtctCohortLibrary.motherOnTreatmentAndNotBreastFeedingUnknown(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Mother on ARV treatment and if breastfeeding NOT known
     * @return indicator
     */
    public CohortIndicator totalBreastFeedingMotherOnTreatment() {
        return cohortIndicator("Mother on treatment and breast feeding totals", map(pmtctCohortLibrary.totalBreastFeedingMotherOnTreatment(), "startDate=${startDate},endDate=${endDate}"));
    }


}
