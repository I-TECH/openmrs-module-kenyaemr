package org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731Greencard;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731.ETLMoh731CohortLibrary;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Created by dev on 1/14/17.
 */

/**
 * Library of HIV related indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
@Component
public class ETLMoh731GreenCardIndicatorLibrary {
    @Autowired
    private ETLMoh731GreenCardCohortLibrary moh731Cohorts;

    /**
     * Number of patients currently in care (includes transfers)
     * @return the indicator
     */
    public CohortIndicator currentlyInCare() {
        return cohortIndicator("Currently in care (includes transfers)", ReportUtils.map(moh731Cohorts.currentlyInCare(), "startDate=${startDate},endDate=${endDate}"));
    }

    public  CohortIndicator newHivEnrollment() {
        return cohortIndicator("New Enrollment in care (excludes transfers)", ReportUtils.map(moh731Cohorts.hivEnrollment(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number of patients who are currently on ART
     * @return the indicator
     */
    public CohortIndicator currentlyOnArt() {
        return cohortIndicator("Currently on ART", ReportUtils.map(moh731Cohorts.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients who are ART revisits
     * @return the indicator
     */
    public CohortIndicator revisitsArt() {
        return cohortIndicator("Revisits ART", ReportUtils.map(moh731Cohorts.revisitsArt(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients who were started on Art
     * @return the indicator
     */
    public CohortIndicator startedOnArt() {
        return cohortIndicator("Started on ART", ReportUtils.map(moh731Cohorts.startedOnART(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Cumulative number of patients on ART
     * @return the indicator
     */
    public CohortIndicator cumulativeOnArt() {
        return cohortIndicator("Cumulative ever on ART", ReportUtils.map(moh731Cohorts.cummulativeOnArt(), "endDate=${endDate}"));
    }

    /**
     * Number of patients in the ART 12 month cohort
     * @return the indicator
     */
    public CohortIndicator art12MonthNetCohort() {
        return cohortIndicator("ART 12 Month Net Cohort", ReportUtils.map(moh731Cohorts.art12MonthNetCohort(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients in the 12 month cohort who are on their original first-line regimen
     * @return the indicator
     */
    public CohortIndicator onOriginalFirstLineAt12Months() {
        return cohortIndicator("On original 1st line at 12 months", ReportUtils.map(moh731Cohorts.onOriginalFirstLineAt12Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients in the 12 month cohort who are on an alternate first-line regimen
     * @return the indicator
     */
    public CohortIndicator onAlternateFirstLineAt12Months() {
        return cohortIndicator("On alternate 1st line at 12 months", ReportUtils.map(moh731Cohorts.onAlternateFirstLineAt12Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients in the 12 month cohort who are on a second-line regimen
     * @return the indicator
     */
    public CohortIndicator onSecondLineAt12Months() {
        return cohortIndicator("On 2nd line at 12 months", ReportUtils.map(moh731Cohorts.onSecondLineAt12Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients in the 12 month cohort who are on ART
     * @return the indicator
     */
    public CohortIndicator onTherapyAt12Months() {
        return cohortIndicator("On therapy at 12 months", ReportUtils.map(moh731Cohorts.onTherapyAt12Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Cumulative number of patients screened for TB
     * @return the indicator
     */
    public CohortIndicator screenedForTb() {
        return cohortIndicator("Screen for TB", ReportUtils.map(moh731Cohorts.tbScreening(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients who were started on Art while pregnant
     * @return the indicator
     */
    public CohortIndicator startedArtWhilePregnant() {
        return cohortIndicator("Started on ART Pregnant", ReportUtils.map(moh731Cohorts.startingARTPregnant(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients who were started on Art while pregnant
     * @return the indicator
     */
    public CohortIndicator startedArtWhileTbPatient() {
        return cohortIndicator("Started on ART - Tb Patient", ReportUtils.map(moh731Cohorts.startingARTWhileTbPatient(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number of patients provided with condoms
     * @return the indicator
     */
    public CohortIndicator condomsProvided() {
        return cohortIndicator("patients provided with condoms", map(moh731Cohorts.condomsProvided(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients provided with modern contraceptives
     * @return the indicator
     */
    public CohortIndicator modernContraceptivesProvided() {
        return cohortIndicator("patients provided with modern contraceptives", map(moh731Cohorts.modernContraceptivesProvided(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of HIV care visits for females aged 18 and over
     * @return the indicator
     */
    public CohortIndicator hivCareVisitsFemale18() {
        return cohortIndicator("HIV care visits for females aged 18 and over", map(moh731Cohorts.hivCareVisitsFemale18(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of scheduled HIV care visits
     * @return the indicator
     */
    public CohortIndicator hivCareVisitsScheduled() {
        return cohortIndicator("Scheduled HIV care visits", map(moh731Cohorts.hivCareVisitsScheduled(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of unscheduled HIV care visits
     * @return the indicator
     */
    public CohortIndicator hivCareVisitsUnscheduled() {
        return cohortIndicator("Unscheduled HIV care visits", map(moh731Cohorts.hivCareVisitsUnscheduled(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Total number of HIV care visits
     * @return the indicator
     */
    public CohortIndicator hivCareVisitsTotal() {
        return cohortIndicator("HIV care visits", map(moh731Cohorts.hivCareVisitsTotal(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients who are on Cotrimoxazole prophylaxis
     * @return the indicator
     */
    public CohortIndicator onCotrimoxazoleProphylaxis() {
        return cohortIndicator("patients on CTX prophylaxis", map(moh731Cohorts.inHivProgramAndOnCtxProphylaxis(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV exposed infants within 2 months
     * @return indicator
     */
    public CohortIndicator hivExposedInfantsWithin2Months() {
        return cohortIndicator("Hiv Exposed Infants within 2 months", map(moh731Cohorts.hivExposedInfantsWithin2Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV exposed infants within 2 months and are eligible for ctx
     * @return indicator
     */
    public  CohortIndicator hivExposedInfantsWithin2MonthsAndEligibleForCTX() {
        return cohortIndicator("Hiv Exposed Infants within 2 months", map(moh731Cohorts.hivExposedInfantsWithin2MonthsAndEligibleForCTX(), "startDate=${startDate},endDate=${endDate}"));
    }

    // Green card additions

    /**
     * HIV counseling and testing
     * covers indicators HV01-01 - HV01-10
     * @return indicator
     */
    public  CohortIndicator htsNumberTested() {
        return cohortIndicator("Individuals tested", map(moh731Cohorts.htsNumberTested(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV counseling and testing at health facility
     * covers indicators  HV01-11
     * @return indicator
     */
    public  CohortIndicator htsNumberTestedAtFacility() {
        return cohortIndicator("Individuals tested at the facility", map(moh731Cohorts.htsNumberTestedAtFacility(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV counseling and testing at community
     * covers indicators  HV01-12
     * @return indicator
     */
    public  CohortIndicator htsNumberTestedAtCommunity() {
        return cohortIndicator("Individuals tested at the community", map(moh731Cohorts.htsNumberTestedAtCommunity(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * New tests
     * covers indicators  HV01-13
     * @return indicator
     */
    public  CohortIndicator htsNumberTestedNew() {
        return cohortIndicator("New tests", map(moh731Cohorts.htsNumberTestedNew(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Repeat tests
     * covers indicators  HV01-14
     * @return indicator
     */
    public  CohortIndicator htsNumberTestedRepeat() {
        return cohortIndicator("Repeat tests", map(moh731Cohorts.htsNumberTestedRepeat(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV couseling and testing: couples
     * covers indicators HV01-15
     * @return indicator
     */
    public  CohortIndicator htsNumberTestedAsCouple() {
        return cohortIndicator("Couple testing", map(moh731Cohorts.htsNumberTestedAsCouple(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV couseling and testing: Key population
     * covers indicators  HV01-16
     * @return indicator
     */
    public  CohortIndicator htsNumberTestedKeyPopulation() {
        return cohortIndicator("Key population testing", map(moh731Cohorts.htsNumberTestedKeyPopulation(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV couseling and testing: Positive results
     * covers indicators HV01-17 - HV01-26
     * @return indicator
     */
    public  CohortIndicator htsNumberTestedPositive() {
        return cohortIndicator("HIV Positive tests", map(moh731Cohorts.htsNumberTestedPositive(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV couseling and testing: Negative total
     * covers indicators HV01-27
     * @return indicator
     */
    public  CohortIndicator htsNumberTestedNegative() {
        return cohortIndicator("HIV Negative tests", map(moh731Cohorts.htsNumberTestedNegative(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV couseling and testing: Discordant couples
     * covers indicators HV01-28
     * @return indicator
     */
    public  CohortIndicator htsNumberTestedDiscordant() {
        return cohortIndicator("Discordant couples", map(moh731Cohorts.htsNumberTestedDiscordant(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV couseling and testing: Negative total
     * covers indicators HV01-29
     * @return indicator
     */
    public  CohortIndicator htsNumberTestedKeypopPositive() {
        return cohortIndicator("Key Pop - positives", map(moh731Cohorts.htsNumberTestedKeypopPositive(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV couseling and testing: turned positive within last 3 months and linked to care during reporting period
     * covers indicators HV01-30 - HV01-35
     * @return indicator
     */
    public  CohortIndicator htsNumberTestedPositiveAndLinked() {
        return cohortIndicator("Positive and linked to care", map(moh731Cohorts.htsNumberTestedPositiveAndLinked(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV couseling and testing: number tested positive in last 3 months
     * covers indicators HV01-36
     * @return indicator
     */
    public  CohortIndicator htsNumberTestedPositiveInLastThreeMonths() {
        return cohortIndicator("tested Positive in last 3 months", map(moh731Cohorts.htsNumberTestedPositiveInLastThreeMonths(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Pre-art
     * covers indicators HV03-013 to  HV03-015
     * @return indicator
     */
    public  CohortIndicator preArtCohort() {
        return cohortIndicator("pre-art cohort", map(moh731Cohorts.preArtCohort(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * patients started on ipt
     *
     * @return indicator
     */
    public  CohortIndicator startedOnIPT() {
        return cohortIndicator("Started on IPT", map(moh731Cohorts.startedOnIPT(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * patients who started ipt 12 months ago and have completed
     */
    public  CohortIndicator ipt12MonthsCohort() {
        return cohortIndicator("IPT 12 months cohort", map(moh731Cohorts.completedIPT12Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * 3.10 HIV in TB clinic
     */
    // tb new cases
    public  CohortIndicator tbEnrollment() {
        return cohortIndicator("New TB cases", map(moh731Cohorts.tbEnrollment(), "startDate=${startDate},endDate=${endDate}"));
    }

    // New TB cases known positive
    public  CohortIndicator tbNewKnownPositive() {
        return cohortIndicator("New TB cases with KP status", map(moh731Cohorts.tbNewKnownPositive(), "startDate=${startDate},endDate=${endDate}"));
    }

    // New TB cases tested positive
    public  CohortIndicator tbTestedForHIV() {
        return cohortIndicator("New TB cases tested for HIV", map(moh731Cohorts.tbTestedForHIV(), "startDate=${startDate},endDate=${endDate}"));
    }

    // new TB cases tested HIV positive
    public  CohortIndicator tbNewTestedHIVPositive() {
        return cohortIndicator("New TB cases tested HIV Positive", map(moh731Cohorts.tbNewTestedHIVPositive(), "startDate=${startDate},endDate=${endDate}"));
    }

    // new TB cases already on HAART
    public  CohortIndicator tbNewAlreadyOnHAART() {
        return cohortIndicator("New TB cases already on HAART", map(moh731Cohorts.tbNewAlreadyOnHAART(), "startDate=${startDate},endDate=${endDate}"));
    }

    // new TB cases started on HAART
    public  CohortIndicator tbNewStartingHAART() {
        return cohortIndicator("New TB cases starting on HAART", map(moh731Cohorts.tbNewStartingHAART(), "startDate=${startDate},endDate=${endDate}"));
    }

    // total TB on HAART
    public  CohortIndicator tbTotalOnHAART() {
        return cohortIndicator("Total TB cases on HAART", map(moh731Cohorts.tbTotalOnHAART(), "startDate=${startDate},endDate=${endDate}"));
    }

    // screened for cacx
    public  CohortIndicator screenedforCaCx() {
        return cohortIndicator("Screened for Cacx", map(moh731Cohorts.screenedForCaCx(), "startDate=${startDate},endDate=${endDate}"));
    }

}
