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
     * Number of Pregnant women with HIV infection receiving antiretroviral therapy (ART)
     * @return the indicator
     */
    public CohortIndicator pregnantCurrentlyOnART() {
        return cohortIndicator("Pregnant Currently on ART", ReportUtils.map(datimQ4Cohorts.pregnantCurrentOnArt(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * //Number of Breastfeeding mothers with HIV infection receiving antiretroviral therapy (ART
     * @return the indicator
     */
    public CohortIndicator bfMothersCurrentlyOnART() {
        return cohortIndicator("BF Currently on ART", ReportUtils.map(datimQ4Cohorts.bfCurrentOnArt(), "startDate=${startDate},endDate=${endDate}"));
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
    /**
     * Number of clients with known HIV status at ANC
     * @return the indicator
     */
    public CohortIndicator clientsWithUnKnownHIVStatusAtANC() {
        return cohortIndicator("Clients with Unknown HIV Status at ANC", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.unKnownStatusAtANC(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of clients with positive HIV status before ANC-1
     * @return the indicator
     */
    public CohortIndicator clientsWithPositiveHivStatusBeforeAnc1() {
        return cohortIndicator("Clients with positive HIV Status before ANC-1", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.positiveHivStatusBeforeAnc1(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of clients with Negative HIV status before ANC-1
     * @return the indicator
     */
    public CohortIndicator clientsWithNegativeHivStatusBeforeAnc1() {
        return cohortIndicator("Clients with Negative HIV Status before ANC-1", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.negativeHivStatusBeforeAnc1(), "startDate=${startDate},endDate=${endDate}"));
    }
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
    public CohortIndicator infantsSampleTakenForVirology() {
        return cohortIndicator("Infants sample taken for Virology", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.infantVirologySampleTaken(), "startDate=${startDate},endDate=${endDate}"));
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
     * Number Tested Negative at PITC PMTCT services ANC-1 only
     * @return the indicator
     */
    public CohortIndicator testedNegativeAtPITCPMTCTANC1() {
        return cohortIndicator("Tested Negative at PITC PMTCT services ANC-1", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.negativeAtPITCPMTCTANC1(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number Tested Positive at PITC PMTCT services ANC-1 only
     * @return the indicator
     */
    public CohortIndicator testedPositiveAtPITCPMTCTANC1() {
        return cohortIndicator("Tested Positive at PITC PMTCT services ANC-1", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.positiveAtPITCPMTCTANC1(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number Tested Negative at PITC PMTCT services Post ANC-1 (including labour and delivery and BF)
     * @return the indicator
     */
    public CohortIndicator testedNegativeAtPITCPMTCTPostANC1() {
        return cohortIndicator("Tested Negative at PITC PMTCT services Post ANC-1", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.negativeAtPITCPMTCTPostANC1(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number Tested Positive at PITC PMTCT services Post ANC-1 (including labour and delivery and BF)
     * @return the indicator
     */
    public CohortIndicator testedPositiveAtPITCPMTCTPostANC1() {
        return cohortIndicator("Tested Positive at PITC PMTCT services Post ANC-1", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.positiveAtPITCPMTCTPostANC1(), "startDate=${startDate},endDate=${endDate}"));
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
    /*Annual Cohort Indicators*/
    /**
     * PMTCT_FO Number of HIV-exposed infants who were born 24 months prior to the reporting period
     * @return the indicator
     */
    public CohortIndicator totalHEI() {
        return cohortIndicator("Total HEI Cohort", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.totalHEICohort(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * PMTCT_FO HEI Cohort HIV infected
     * @return the indicator
     */
    public CohortIndicator hivInfectedHEI() {
        return cohortIndicator("HIV Infected HEI Cohort", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.hivInfectedHEICohort(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * PMTCT_FO HEI Cohort HIV uninfected
     * @return the indicator
     */
    public CohortIndicator hivUninfectedHEI() {
        return cohortIndicator("Uninfected HEI Cohort", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.hivUninfectedHEICohort(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * PMTCT_FO HEI Cohort HIV-final status unknown
     * @return the indicator
     */
    public CohortIndicator unknownHIVStatusHEI() {
        return cohortIndicator("Unknown HIV Status HEI Cohort", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.unknownHIVStatusHEICohort(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * PMTCT_FO HEI died with HIV-final status unknown
     * @return the indicator
     */
    public CohortIndicator heiDiedWithunknownHIVStatus() {
        return cohortIndicator("HEI Died with Unknown HIV Status", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.heiDiedWithUnknownStatus(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_RET Number of pregnant women who are still alive and on treatment at 12 months after initiating ART
     * @return the indicator
     */
    public CohortIndicator alivePregnantOnARTLast12Months() {
        return cohortIndicator("Alive, Pregnant and on ART for last 12 months", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.pregnantAliveOnARTLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_RET Number of breastfeeding mothers who are still alive and on treatment at 12 months after initiating ART
     * @return the indicator
     */
    public CohortIndicator aliveBfOnARTLast12Months() {
        return cohortIndicator("Alive, Breastfeeding and on ART for last 12 months", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.bfAliveOnARTLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_RET Number of adults and children who are still alive and on treatment at 12 months after initiating ART Disaggregated by age / sex
     * @return the indicator
     */
    public CohortIndicator aliveOnlyOnARTInLast12MonthsByAgeSex() {
        return cohortIndicator("Alive on ART in last 12 months by Age / Sex", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.aliveOnARTInLast12MonthsByAgeSex(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_RET Denominator Started ART last 12 months and breastfeeding includes dead, stopped, lost follow-up
     * @return the indicator
     */
    public CohortIndicator totalBFStartedARTLast12Months() {
        return cohortIndicator("Total started ART in last 12 months and Breastfeeding", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.breastfeedingAndstartedARTinLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_RET Denominator Started ART last 12 months and Pregnant includes dead, stopped, lost follow-up
     * @return the indicator
     */
    public CohortIndicator totalPregnantStartedARTLast12Months() {
        return cohortIndicator("Total started ART in last 12 months and Pregnant", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.pregnantAndstartedARTinLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_RET (Denominator) All started ART last 12 months disaggregated by Age/sex includes dead, stopped, lost follow-up
     * @return the indicator
     */
    public CohortIndicator allOnARTLast12MonthsByAgeSex() {
        return cohortIndicator("Total on ART in last 12 months by Age / Sex", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.totalOnARTLast12MonthsByAgeSex(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number of adults and pediatric patients on ART with suppressed Routine viral load results (<1,000 copies/ml) results within the past 12 months
     * @return the indicator
     */
    public CohortIndicator onARTSuppRoutineVLLast12Months() {
        return cohortIndicator("Patients on ART with Suppressed routine VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.onARTWithSuppressedRoutineVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number of adults and pediatric patients on ART with suppressed Targeted viral load results (<1,000 copies/ml) results within the past 12 months
     * @return the indicator
     */
    public CohortIndicator onARTSuppTargetedVLLast12Months() {
        return cohortIndicator("Patients on ART with Suppressed targeted VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.onARTWithSuppressedTargetedVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS Number of adults and pediatric patients on ART with suppressed undocumented viral load results (<1,000 copies/ml) results within the past 12 months
     * @return the indicator
     */
    public CohortIndicator onARTSuppUndocumentedVLLast12Months() {
        return cohortIndicator("Patients on ART with SuppresbfOnARTWithSuppressedTargetedVLLast12Monthssed undocumented VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.onARTWithSuppressedUndocumentedVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number pregnant women on ART with suppressed Routine viral load results (<1,000 copies/ml) within the past 12 months.
     */
    public CohortIndicator pregnantOnARTWithSuppressedRoutineVLLast12Months() {
        return cohortIndicator("Pregnant Women on ART with Suppressed Routine VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.pregnantOnARTWithSuppressedRoutineVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS Number pregnant women on ART with suppressed targeted viral load results (<1,000 copies/ml) within the past 12 months.
     */
    public CohortIndicator pregnantOnARTSuppTargetedVLLast12Months() {
        return cohortIndicator("Pregnant Women on ART with Suppressed targeted VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.pregnantOnARTWithSuppressedTargetedVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS Number pregnant women with Undocumented ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months.
     */
    public CohortIndicator pregnantOnARTSuppUndocumentedVLLast12Months() {
        return cohortIndicator("Pregnant Women on ART with Suppressed undocumented VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.pregnantOnARTWithSuppressedUndocumentedVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number breastfeeding mother on routine ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months.
     */
    public CohortIndicator bfOnARTSuppRoutineVLLast12Months() {
        return cohortIndicator("Breastfeeding mother on ART with Suppressed Routine VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.bfOnARTSuppRoutineVL(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number breastfeeding mother on targeted ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months.
     */
    public CohortIndicator bfOnARTSuppTargetedVLLast12Months() {
        return cohortIndicator("Breastfeeding mother on ART with Suppressed Targeted VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.bfOnARTSuppTargetedVL(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number breastfeeding mothers on ART with suppressed undocumented viral load results (<1,000 copies/ml) within the past 12 months.
     */
    public CohortIndicator bfOnARTSuppUndocumentedVLLast12Months() {
        return cohortIndicator("Breastfeeding mother on ART with Suppressed undocumented VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.bfOnARTSuppUndocumentedVL(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS Number of Patients on Routine ART test with suppressed viral load results (<1,000 copies/ml) within the past 12 months Disaggregated by Sex/Age
     */
    public CohortIndicator onARTSuppRoutineVLAgeSex() {
        return cohortIndicator("Total on ART in last 12 months by Age / Sex", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.onARTSuppRoutineVLBySex(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS Number of Patients on Targeted ART test with suppressed viral load results (<1,000 copies/ml) within the past 12 months Disaggregated by Sex/Age
     */
    public CohortIndicator onARTSuppTargetedVLAgeSex() {
        return cohortIndicator("On ART with Suppressed Targeted VL within last 12 Months by sex/age", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.onARTSuppTargetedVLBySex(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number of Patients on ART with undocumented suppressed viral load results (<1,000 copies/ml) within the past 12 months Disaggregated by Sex/Age
     */
    public CohortIndicator onARTSuppUndocumentedVLAgeSex() {
        return cohortIndicator("Patients on ART with Suppressed undocumented VL within last 12 Months by sex/age", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.onARTSuppUndocumentedVLBySex(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /*TX_PVLS (Denominator)*//*
     *//**
     * TX_PVLS Total Patients on Routine ART with a viral load result in the past 12 months.
     */
    public CohortIndicator onARTRoutineVLLast12Months() {
        return cohortIndicator("Patients on ART with Routine VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.onARTWithRoutineVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS Total Patients on ART with targeted viral load result in the past 12 months.
     */
    public CohortIndicator onARTTargetedVLLast12Months() {
        return cohortIndicator("Patients on ART with Targeted VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.onARTWithTargetedVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS Total Patients ART with undocumented viral load result in the past 12 months.
     */
    public CohortIndicator totalARTWithUndocumentedVLLast12Months() {
        return cohortIndicator("Patients on ART with undocumented VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.totalOnARTWithUndocumentedVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS  Pregnant women on Routine ART with viral load results within the past 12 months.
     */
    public CohortIndicator pregnantOnARTRoutineVLLast12Months() {
        return cohortIndicator("Pregnant Women on ART with Routine VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.pregnantOnARTWithRoutineVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS  Pregnant women on ART with Targeted viral load results within the past 12 months.
     */
    public CohortIndicator pregnantOnARTTargetedVLLast12Months() {
        return cohortIndicator("Pregnant Women on ART with Targeted VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.pregnantOnARTWithTargetedVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS  Pregnant women on ART with undocumented viral load results within the past 12 months.
     */
    public CohortIndicator pregnantARTUndocumentedVLLast12Months() {
        return cohortIndicator("Pregnant Women on ART with Undocumented VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.pregnantARTWithUndocumentedVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS  Breastfeeding women on ART with Routine viral load results within the past 12 months.
     */
    public CohortIndicator breastfeedingOnARTRoutineVLLast12Months() {
        return cohortIndicator("Breastfeeding Women on ART with Routine VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.breastfeedingOnARTWithRoutineVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS  Breastfeeding women on ART with Targeted viral load results within the past 12 months.
     */
    public CohortIndicator breastfeedingOnARTTargetedVLLast12Months() {
        return cohortIndicator("Breastfeeding Women on ART with Targeted VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.breastfeedingOnARTWithTargetedVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS  Breastfeeding women on ART with undocumented viral load results within the past 12 months.
     */
    public CohortIndicator breastfeedingOnARTUndocumentedVLLast12Months() {
        return cohortIndicator("Breastfeeding Women on ART with Undocumented VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.breastfeedingOnARTWithUndocumentedVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number of Patients on ART with Routine viral load test within the past 12 months Disaggregated by Sex/Age
     */
    public CohortIndicator onARTRoutineVLLast12MonthsbyAgeSex() {
        return cohortIndicator("On ART with Routine VL within last 12 Months by sex/age", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.onARTWithRoutineVLLast12MonthsByAgeSex(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number of Patients on ART with Targeted viral load test within the past 12 months Disaggregated by Sex/Age
     */
    public CohortIndicator onARTTargetedVLLast12MonthsbyAgeSex() {
        return cohortIndicator("Patients on ART with Targeted VL within last 12 Months by sex/age", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.onARTWithTargetedVLLast12MonthsByAgeSex(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number of Patients on ART with Undocumented viral load test within the past 12 months Disaggregated by Sex/Age
     */
    public CohortIndicator onARTUndocumentedVLLast12MonthsbyAgeSex() {
        return cohortIndicator("Patients on ART with undocumented VL within last 12 Months by sex/age", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.onARTWithUndocumentedVLLast12MonthsByAgeSex(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_ML Number of ART patients with no clinical contact since their last expected contact
     */
    public CohortIndicator onARTMissedAppointment() {
        return cohortIndicator("Number of ART patients with no clinical contact since their last expected contact", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.onARTMissedAppointment(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HTS_INDEX Number of individuals who were identified and tested using Index testing services and received their results
     */
    public CohortIndicator testedThroughIndexServices() {
        return cohortIndicator("Number of individuals who were identified and tested using Index testing services", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.contactIndexTesting(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HTS_RECENT Persons aged ≥15 years newly diagnosed with HIV-1 infection who have a test for recent infection
     */
    public CohortIndicator recentHIVInfections() {
        return cohortIndicator("Persons aged ≥15 years newly diagnosed with HIV-1 infection", ReportUtils.<CohortDefinition>map(datimQ4Cohorts.recentHIVInfections(),
                "startDate=${startDate},endDate=${endDate}"));
    }

}
