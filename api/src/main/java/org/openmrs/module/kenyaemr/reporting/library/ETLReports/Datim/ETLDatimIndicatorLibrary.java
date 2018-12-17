/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.Datim;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLPatientsWithSuppressedVLInLast12MonthsCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ETLPatientsWithVLInLast12MonthsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;


/**
 * Library of DATIM related indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
@Component
public class ETLDatimIndicatorLibrary {
    @Autowired
    private ETLDatimCohortLibrary datimCohorts;


    /**
     * Number of patients who are currently on ART
     * @return the indicator
     */
    public CohortIndicator currentlyOnArt() {
        return cohortIndicator("Currently on ART", ReportUtils.map(datimCohorts.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number of patients who were started on Art
     * @return the indicator
     */
    public CohortIndicator startedOnArt() {
        return cohortIndicator("Started on ART", ReportUtils.map(datimCohorts.startedOnART(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients who were started on Art and are pregnant
     * @return the indicator
     */
    public CohortIndicator startedOnARTAndPregnant() {
        return cohortIndicator("Started on ART", ReportUtils.map(datimCohorts.startedOnARTAndPregnant(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients who were started on Art and are TB co-infected
     * @return the indicator
     */
    public CohortIndicator startedOnARTAndTBCoinfected() {
        return cohortIndicator("Started on ART", ReportUtils.map(datimCohorts.startedOnARTAndTBCoinfected(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients in the ART 12 month cohort
     * @return the indicator
     */
    public CohortIndicator art12MonthCohort() {
        return cohortIndicator("ART 12 Month Net Cohort", ReportUtils.map(datimCohorts.art12MonthCohort(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients in the 12 month cohort who are on ART
     * @return the indicator
     */
    public CohortIndicator onTherapyAt12Months() {
        return cohortIndicator("On therapy at 12 months", ReportUtils.map(datimCohorts.onTherapyAt12Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * No of patients with successful VL test/result in the last 12 months
     * @return the indicator
     */
    public CohortIndicator patientsWithVLResults() {
        return cohortIndicator("VL Results", ReportUtils.<CohortDefinition>map(datimCohorts.viralLoadResultsInLast12Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients with viral suppression in the last 12 months
     * @return the indicator
     */
    public CohortIndicator patientsWithViralLoadSuppression() {
        return cohortIndicator("Viral Suppression", ReportUtils.<CohortDefinition>map(datimCohorts.viralSuppressionInLast12Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number of patients tested Negative for HIV at ANC
     * @return the indicator
     */
    public CohortIndicator patientsTestNegativeAtANC() {
        return cohortIndicator("HIV Negative Results at ANC", ReportUtils.<CohortDefinition>map(datimCohorts.patientHIVNegativeResultsATANC(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients tested positive for HIV at ANC
     * @return the indicator
     */
    public CohortIndicator patientsTestPositiveAtANC() {
        return cohortIndicator("HIV Positive Results at ANC", ReportUtils.<CohortDefinition>map(datimCohorts.patientHIVPositiveResultsAtANC(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of clients with known HIV status at ANC
     * @return the indicator
     */
    public CohortIndicator clientsWithKnownHIVStatusAtANC() {
        return cohortIndicator("Clients with Known HIV Status at ANC", ReportUtils.<CohortDefinition>map(datimCohorts.knownStatusAtANC(), "startDate=${startDate},endDate=${endDate}"));
    }
    /*   *//**
     * Number of clients with known Positive HIV status
     * @return the indicator
     *//*
    public CohortIndicator clientsWithKnownPositiveHIVStatus() {
        return cohortIndicator("Clients with Known HIV Positive Status", ReportUtils.<CohortDefinition>map(datimCohorts.knownHIVPositive(), "startDate=${startDate},endDate=${endDate}"));
    }
    *//**
     * Number of clients newly tested HIV Positive
     * @return the indicator
     *//*
    public CohortIndicator clientsNewlyTestedHIVPositive() {
        return cohortIndicator("Clients with Known HIV Positive Status", ReportUtils.<CohortDefinition>map(datimCohorts.newlyTestedHIVPositive(), "startDate=${startDate},endDate=${endDate}"));
    }

    *//**
     * Number of clients newly tested HIV Negative
     * @return the indicator
     *//*
    public CohortIndicator clientsNewlyTestedHIVNegative() {
        return cohortIndicator("Clients with Known HIV Positive Status", ReportUtils.<CohortDefinition>map(datimCohorts.newlyTestedHIVNegative(), "startDate=${startDate},endDate=${endDate}"));
    }*/

    /**
     * Number of clients newly enrolled for ANC
     * @return the indicator
     */
    public CohortIndicator clientsNewlyEnrolledToANC() {
        return cohortIndicator("Clients newly Enrolled For ANC", ReportUtils.<CohortDefinition>map(datimCohorts.newANCClients(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of Infants with a Negative virology test result
     * @return the indicator
     */
    public CohortIndicator infantsTestedNegativeForVirology() {
        return cohortIndicator("Infants tested negative for Virology", ReportUtils.<CohortDefinition>map(datimCohorts.infantVirologyNegativeResults(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of Infants with a positive virology test result
     * @return the indicator
     */
    public CohortIndicator infantVirologyWithNoResults() {
        return cohortIndicator("Infants tested for Virology but no results", ReportUtils.<CohortDefinition>map(datimCohorts.infantVirologyNoResults(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of Infants with a positive virology test result
     * @return the indicator
     */
    public CohortIndicator infantsTestedPositiveForVirology() {
        return cohortIndicator("Infants tested positive for Virology", ReportUtils.<CohortDefinition>map(datimCohorts.infantVirologyPositiveResults(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number of Mothers Already on ART at the start of current Pregnancy
     * @return the indicator
     */
    public CohortIndicator mothersAlreadyOnARTAtStartOfCurrentPregnancy() {
        return cohortIndicator("Mothers Already on ART at the start of current Pregnancy", ReportUtils.<CohortDefinition>map(datimCohorts.alreadyOnARTAtBeginningOfPregnancy(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of Mothers new on ART during current pregnancy
     * @return the indicator
     */
    public CohortIndicator mothersNewOnARTDuringCurrentPregnancy() {
        return cohortIndicator("Mothers new on ART during current pregnancy", ReportUtils.<CohortDefinition>map(datimCohorts.newOnARTDuringPregnancy(), "startDate=${startDate},endDate=${endDate}"));
    }

}
