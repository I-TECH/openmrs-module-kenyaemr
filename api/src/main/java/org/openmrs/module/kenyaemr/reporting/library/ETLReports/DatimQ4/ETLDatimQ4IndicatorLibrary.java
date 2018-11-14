/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.DatimQ4;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;


/**
 * Library of DATIM related indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
@Component
public class ETLDatimQ4IndicatorLibrary {
    @Autowired
    private ETLDatimQ4CohortLibrary datimQ4Cohorts;


    /**
     * Number of patients who are currently on ART
     * @return the indicator
     */
    public CohortIndicator currentlyOnArt() {
        return cohortIndicator("Currently on ART", ReportUtils.map(datimQ4Cohorts.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number of patients who were started on Art
     * @return the indicator
     */
    public CohortIndicator startedOnArt() {
        return cohortIndicator("Started on ART", ReportUtils.map(datimQ4Cohorts.startedOnART(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients who were started on Art and are pregnant
     * @return the indicator
     */
    public CohortIndicator startedOnARTAndPregnant() {
        return cohortIndicator("Started on ART", ReportUtils.map(datimQ4Cohorts.startedOnARTAndPregnant(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients who were started on Art and are TB co-infected
     * @return the indicator
     */
    public CohortIndicator startedOnARTAndTBCoinfected() {
        return cohortIndicator("Started on ART", ReportUtils.map(datimQ4Cohorts.startedOnARTAndTBCoinfected(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients in the ART 12 month cohort
     * @return the indicator
     */
    public CohortIndicator art12MonthCohort() {
        return cohortIndicator("ART 12 Month Net Cohort", ReportUtils.map(datimQ4Cohorts.art12MonthCohort(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients in the 12 month cohort who are on ART
     * @return the indicator
     */
    public CohortIndicator onTherapyAt12Months() {
        return cohortIndicator("On therapy at 12 months", ReportUtils.map(datimQ4Cohorts.onTherapyAt12Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * No of patients with successful VL test/result in the last 12 months
     * @return the indicator
     */
    public CohortIndicator patientsWithVLResults() {
        return cohortIndicator("VL Results", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.viralLoadResultsInLast12Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients with viral suppression in the last 12 months
     * @return the indicator
     */
    public CohortIndicator patientsWithViralLoadSuppression() {
        return cohortIndicator("Viral Suppression", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.viralSuppressionInLast12Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients tested Negative for HIV at ANC
     * @return the indicator
     */
    public CohortIndicator patientsTestNegativeAtANC() {
        return cohortIndicator("HIV Negative Results at ANC", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.patientHIVNegativeResultsATANC(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients tested positive for HIV at ANC
     * @return the indicator
     */
    public CohortIndicator patientsTestPositiveAtANC() {
        return cohortIndicator("HIV Positive Results at ANC", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.patientHIVPositiveResultsAtANC(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of clients with known HIV status at ANC
     * @return the indicator
     */
    public CohortIndicator clientsWithKnownHIVStatusAtANC() {
        return cohortIndicator("Clients with Known HIV Status at ANC", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.knownStatusAtANC(), "startDate=${startDate},endDate=${endDate}"));
    }
   /* *//**
     * Number of clients with known Positive HIV status
     * @return the indicator
     *//*
    public CohortIndicator clientsWithKnownPositiveHIVStatus() {
        return cohortIndicator("Clients with Known HIV Positive Status", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.knownHIVPositive(), "startDate=${startDate},endDate=${endDate}"));
    }
    *//**
     * Number of clients newly tested HIV Positive
     * @return the indicator
     *//*
    public CohortIndicator clientsNewlyTestedHIVPositive() {
        return cohortIndicator("Clients with Known HIV Positive Status", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.newlyTestedHIVPositive(), "startDate=${startDate},endDate=${endDate}"));
    }

    *//**
     * Number of clients newly tested HIV Negative
     * @return the indicator
     *//*
    public CohortIndicator clientsNewlyTestedHIVNegative() {
        return cohortIndicator("Clients with Known HIV Positive Status", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.newlyTestedHIVNegative(), "startDate=${startDate},endDate=${endDate}"));
    }
*/
    /**
     * Number of clients newly enrolled for ANC
     * @return the indicator
     */
    public CohortIndicator clientsNewlyEnrolledToANC() {
        return cohortIndicator("Clients newly Enrolled For ANC", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.newANCClients(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of Infants with a Negative virology test result
     * @return the indicator
     */
    public CohortIndicator infantsTestedNegativeForVirology() {
        return cohortIndicator("Infants tested negative for Virology", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.infantVirologyNegativeResults(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of Infants with a positive virology test result
     * @return the indicator
     */
    public CohortIndicator infantsTestedPositiveForVirology() {
        return cohortIndicator("Infants tested positive for Virology", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.infantVirologyPositiveResults(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of Infants with a virology test but no result
     * @return the indicator
     */
    public CohortIndicator infantsTestedForVirologyNoResult() {
        return cohortIndicator("Infants tested positive for Virology", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.infantVirologyNoResults(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number of Mothers Already on ART at the start of current Pregnancy
     * @return the indicator
     */
    public CohortIndicator mothersAlreadyOnARTAtStartOfCurrentPregnancy() {
        return cohortIndicator("Mothers Already on ART at the start of current Pregnancy", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.alreadyOnARTAtBeginningOfPregnacy(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of Mothers new on ART during current pregnancy
     * @return the indicator
     */
    public CohortIndicator mothersNewOnARTDuringCurrentPregnancy() {
        return cohortIndicator("Mothers new on ART during current pregnancy", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.newOnARTDuringPregnancy(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number Tested Negative at PITC Inpatient Services
     * @return the indicator
     */
    public CohortIndicator testedNegativeAtPITCInpatientServices() {
        return cohortIndicator("Tested Negative at PITC Inpatient Services", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.testedNegativeAtPITCInpatientServices(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number Tested Positive at PITC Inpatient Services
     * @return the indicator
     */
    public CohortIndicator testedPositiveAtPITCInpatientServices() {
        return cohortIndicator("Tested Positive at PITC Inpatient Services", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.testedPositiveAtPITCInpatientServices(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number Tested Positive at PITC Paediatric services
     * @return the indicator
     */
    public CohortIndicator testedPositiveAtPITCPaediatricServices() {
        return cohortIndicator("Tested Positive at PITC Paediatric services", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.testedPositiveAtPITCPaediatricServices(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number Tested Negative at PITC Paediatric services
     * @return the indicator
     */
    public CohortIndicator testedNegativeAtPITCPaediatricServices() {
        return cohortIndicator("Tested Negative at PITC Paediatric services", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.testedNegativeAtPITCPaediatricServices(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * PITC Malnutrition Clinics Negative
     * @return the indicator
     */
    public CohortIndicator testedNegativeAtPITCMalnutritionClinic() {
        return cohortIndicator("Tested Negative at PITC Malnutrition Clinics", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.testedNegativeAtPITCMalnutritionClinics(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * PITC Malnutrition Clinics Positive
     * @return the indicator
     */
    public CohortIndicator testedPositiveAtPITCMalnutritionClinic() {
        return cohortIndicator("Tested Positive at PITC Malnutrition Clinics", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.testedPositiveAtPITCMalnutritionClinics(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * PITC TB Clinic Negative
     * @return the indicator
     */
    public CohortIndicator testedNegativeAtPITCTBClinic() {
        return cohortIndicator("Tested Negative at PITC TB Clinic", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.testedNegativeAtPITCTBClinic(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * PITC TB Clinic Positive
     * @return the indicator
     */
    public CohortIndicator testedPositiveAtPITCTBClinic() {
        return cohortIndicator("Tested Positive at PITC TB Clinic", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.testedPositiveAtPITCTBClinic(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Tested Negative at PITC Other
     * @return the indicator
     */
    public CohortIndicator testedNegativeAtPITCOther() {
        return cohortIndicator("Tested Negative at PITC Other", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.testedNagativeAtPITCOther(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Tested Positive at PITC Other
     * @return the indicator
     */
    public CohortIndicator testedPositiveAtPITCOther() {
        return cohortIndicator("Tested Positive at PITC Other", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.testedPositiveAtPITCOther(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Tested Negative at PITC VCT
     * @return the indicator
     */
    public CohortIndicator testedNegativeAtPITCVCT() {
        return cohortIndicator("Tested Negative at PITC VCT", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.testedNagativeAtPITCVCT(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Tested Positive at PITC VCT
     * @return the indicator
     */
    public CohortIndicator testedPositiveAtPITCVCT() {
        return cohortIndicator("Tested Positive at PITC VCT", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.testedPositiveAtPITCVCT(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * PITC Index Negative
     * @return the indicator
     */
    public CohortIndicator indexTestedNegative() {
        return cohortIndicator("PITC Index Negative", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.indexTestedNegative(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * PITC Index Positive
     * @return the indicator
     */
    public CohortIndicator indexTestedPositive() {
        return cohortIndicator("PITC Index Positive", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.indextestedPositive(),
                "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number Newly Started ART While BreastFeeding
     * @return the indicator
     */
    public CohortIndicator newlyStartedARTWhileBF() {
        return cohortIndicator("Newly Started ART While Breastfeeding", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.newlyStartedARTWhileBreastFeeding(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number who Newly Started ART While Pregnant
     * @return the indicator
     */
    public CohortIndicator newlyStartedARTWhilePregnant() {
        return cohortIndicator("Newly Started ART While Pregnant", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.newlyStartedARTWhilePregnant(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number Newly Started ART While Confirmed TB and / or TB Treated
     * @return the indicator
     */
    public CohortIndicator newlyStartedARTWithTB() {
        return cohortIndicator("Newly Started ART While Confirmed TB and / or TB Treated", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.newlyStartedARTWithTB(),
                "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Disaggregated by Age / Sex
     * @return the indicator
     */
    public CohortIndicator newlyStartedARTByAgeSex() {
        return cohortIndicator("Newly Started ART While Confirmed TB and / or TB Treated", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.newlyStartedARTByAgeSex(),
                "startDate=${startDate},endDate=${endDate}"));
    }

}
