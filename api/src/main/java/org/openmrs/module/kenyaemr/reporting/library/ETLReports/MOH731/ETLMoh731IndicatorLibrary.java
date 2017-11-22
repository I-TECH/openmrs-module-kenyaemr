package org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731;

import org.openmrs.module.kenyacore.report.ReportUtils;
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
public class ETLMoh731IndicatorLibrary {
    @Autowired
    private ETLMoh731CohortLibrary moh731Cohorts;

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

}
