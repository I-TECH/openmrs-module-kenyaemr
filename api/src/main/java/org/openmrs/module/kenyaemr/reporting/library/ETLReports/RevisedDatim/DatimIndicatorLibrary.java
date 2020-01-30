/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.RevisedDatim;

import org.openmrs.module.kenyacore.report.ReportUtils;

import org.openmrs.module.kenyaemr.reporting.data.converter.definition.KPTypeDataDefinition;

import org.openmrs.module.kenyaemr.reporting.data.converter.definition.DurationToNextAppointmentDataDefinition;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.RevisedDatim.DatimCohortLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;


/**
 * Library of DATIM related indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
@Component
public class DatimIndicatorLibrary {
    @Autowired
    private DatimCohortLibrary datimCohorts;


    /**
     * Number of patients who are currently on ART
     * @return the indicator
     */
    public CohortIndicator currentlyOnArt() {
        return cohortIndicator("Currently on ART", ReportUtils.map(datimCohorts.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of Pregnant women with HIV infection receiving antiretroviral therapy (ART)
     * @return the indicator
     */
    public CohortIndicator pregnantCurrentlyOnART() {
        return cohortIndicator("Pregnant Currently on ART", ReportUtils.map(datimCohorts.pregnantCurrentOnArt(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * //Number of Breastfeeding mothers with HIV infection receiving antiretroviral therapy (ART
     * @return the indicator
     */
    public CohortIndicator bfMothersCurrentlyOnART() {
        return cohortIndicator("BF Currently on ART", ReportUtils.map(datimCohorts.bfCurrentOnArt(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * //Number of FSW with HIV infection receiving antiretroviral therapy (ART
     * @return the indicator
     */
    public CohortIndicator fswCurrentlyOnART(KPTypeDataDefinition fsw) {
        return cohortIndicator("FSW Currently on ART", ReportUtils.map(datimCohorts.kpCurrentOnArt(fsw), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * //Number of MSM with HIV infection receiving antiretroviral therapy (ART
     * @return the indicator
     */
    public CohortIndicator msmCurrentlyOnART(KPTypeDataDefinition msm) {
        return cohortIndicator("MSM Currently on ART", ReportUtils.map(datimCohorts.kpCurrentOnArt(msm), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * //Number PWID with HIV infection receiving antiretroviral therapy (ART
     * @return the indicator
     */
    public CohortIndicator pwidCurrentlyOnART(KPTypeDataDefinition pwid) {
        return cohortIndicator("PWID Currently on ART", ReportUtils.map(datimCohorts.kpCurrentOnArt(pwid), "startDate=${startDate},endDate=${endDate}"));
    }

     /**One Month to next appointment
     * @return the indicator
     */
    public CohortIndicator currentlyOnARTOneMonthDrugsDispensed(DurationToNextAppointmentDataDefinition duration) {
        return cohortIndicator("Currently on ART one Month Drugs Dispensed", ReportUtils.map(datimCohorts.drugDurationCurrentOnArt(duration), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * //Two Months to next appointment
     * @return the indicator
     */
    public CohortIndicator currentlyOnARTTwoMonthsDrugsDispensed(DurationToNextAppointmentDataDefinition duration) {
        return cohortIndicator("Currently on ART two Months Drugs Dispensed", ReportUtils.map(datimCohorts.drugDurationCurrentOnArt(duration), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * //Three Months to next appointment
     * @return the indicator
     */
    public CohortIndicator currentlyOnARTThreeMonthsDrugsDispensed(DurationToNextAppointmentDataDefinition duration) {
        return cohortIndicator("Currently on ART three Months Drugs Dispensed", ReportUtils.map(datimCohorts.drugDurationCurrentOnArt(duration), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * //Four months to next appointment
     * @return the indicator
     */
    public CohortIndicator currentlyOnARTFourMonthsDrugsDispensed(DurationToNextAppointmentDataDefinition duration) {
        return cohortIndicator("Currently on ART Four Months Drugs Dispensed", ReportUtils.map(datimCohorts.drugDurationCurrentOnArt(duration), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * //Five months to next appointment
     * @return the indicator
     */
    public CohortIndicator currentlyOnARTFiveMonthsDrugsDispensed(DurationToNextAppointmentDataDefinition duration) {
        return cohortIndicator("Currently on ART Five Months Drugs Dispensed", ReportUtils.map(datimCohorts.drugDurationCurrentOnArt(duration), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * //Six Months to next appointment
     * @return the indicator
     */
    public CohortIndicator currentlyOnARTSixMonthsDrugsDispensed(DurationToNextAppointmentDataDefinition duration) {
        return cohortIndicator("Currently on ART six Month Drugs Dispensed", ReportUtils.map(datimCohorts.drugDurationCurrentOnArt(duration), "startDate=${startDate},endDate=${endDate}"));
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
    /**
     * Number of clients with known HIV status at ANC
     * @return the indicator
     */
    public CohortIndicator clientsWithUnKnownHIVStatusAtANC() {
        return cohortIndicator("Clients with Unknown HIV Status at ANC", ReportUtils.<CohortDefinition>map(datimCohorts.unKnownStatusAtANC(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of clients with positive HIV status before ANC-1
     * @return the indicator
     */
    public CohortIndicator clientsWithPositiveHivStatusBeforeAnc1() {
        return cohortIndicator("Clients with positive HIV Status before ANC-1", ReportUtils.<CohortDefinition>map(datimCohorts.positiveHivStatusBeforeAnc1(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of clients with Negative HIV status before ANC-1
     * @return the indicator
     */
    public CohortIndicator clientsWithNegativeHivStatusBeforeAnc1() {
        return cohortIndicator("Clients with Negative HIV Status before ANC-1", ReportUtils.<CohortDefinition>map(datimCohorts.negativeHivStatusBeforeAnc1(), "startDate=${startDate},endDate=${endDate}"));
    }
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
    public CohortIndicator infantsSampleTakenForVirology() {
        return cohortIndicator("Infants sample taken for Virology", ReportUtils.<CohortDefinition>map(datimCohorts.infantVirologySampleTaken(), "startDate=${startDate},endDate=${endDate}"));
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
    public CohortIndicator infantsTestedPositiveForVirology() {
        return cohortIndicator("Infants tested positive for Virology", ReportUtils.<CohortDefinition>map(datimCohorts.infantVirologyPositiveResults(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of Infants with a virology test but no result
     * @return the indicator
     */
    public CohortIndicator infantsTestedForVirologyNoResult() {
        return cohortIndicator("Infants tested positive for Virology", ReportUtils.<CohortDefinition>map(datimCohorts.infantVirologyNoResults(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of Infants turned HIV Positive 12 months after birth
     * @return the indicator
     */
    public CohortIndicator infantsTurnedHIVPositive() {
        return cohortIndicator("Infants identified HIV Positive within 12 months after birth", ReportUtils.<CohortDefinition>map(datimCohorts.infantsTurnedHIVPositive(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV Positive women on ART screened Negative for cervical cancer 1st time
     * @return the indicator
     */
    public CohortIndicator firstTimescreenedCXCANegative() {
        return cohortIndicator("HIV Positive women on ART screened Negative for cervical cancer 1st time", ReportUtils.<CohortDefinition>map(datimCohorts.firstTimescreenedCXCANegative(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV Positive women on ART screened Positive for cervical cancer 1st time
     * @return the indicator
     */
    public CohortIndicator firstTimescreenedCXCAPositive() {
        return cohortIndicator("HIV Positive women on ART screened Positive for cervical cancer 1st time", ReportUtils.<CohortDefinition>map(datimCohorts.firstTimescreenedCXCAPositive(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV Positive women on ART screened Negative for cervical cancer 1st time
     * @return the indicator
     */
    public CohortIndicator firstTimescreenedCXCAPresumed() {
        return cohortIndicator("HIV Positive women on ART with Presumed cervical cancer 1st time screening", ReportUtils.<CohortDefinition>map(datimCohorts.firstTimescreenedCXCAPresumed(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number of OVC Current on ART reported to implementing partner
     * @return the indicator
     */
    public CohortIndicator ovcOnART() {
        return cohortIndicator("Number of OVC Current on ART reported to implementing partner", ReportUtils.<CohortDefinition>map(datimCohorts.ovcOnART(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number of OVC Not on ART reported to implementing partner
     * @return the indicator
     */
    public CohortIndicator ovcNotOnART() {
        return cohortIndicator("Number of OVC Not on ART reported to implementing partner", ReportUtils.<CohortDefinition>map(datimCohorts.ovcNotOnART(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * HIV Positive women on ART re-screened Negative for cervical cancer
     * @return the indicator
     */
    public CohortIndicator rescreenedCXCANegative() {
        return cohortIndicator("HIV Positive Women on ART re-screened Negative for cervical cancere", ReportUtils.<CohortDefinition>map(datimCohorts.rescreenedCXCANegative(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * HIV Positive women on ART re-screened Positive for cervical cancer
     * @return the indicator
     */
    public CohortIndicator rescreenedCXCAPositive() {
        return cohortIndicator("HIV Positive women on ART re-screened Positive for cervical cancer", ReportUtils.<CohortDefinition>map(datimCohorts.rescreenedCXCAPositive(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * HIV Positive women on ART with presumed cervical cancer during re-screening
     * @return the indicator
     */
    public CohortIndicator rescreenedCXCAPresumed() {
        return cohortIndicator("HIV Positive women on ART with presumed cervical cancer during re-screening", ReportUtils.<CohortDefinition>map(datimCohorts.rescreenedCXCAPresumed(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of Infants turned HIV Positive 12 months after birth and started on ART
     * @return the indicator
     */
    public CohortIndicator infantsTurnedHIVPositiveOnART() {
        return cohortIndicator("Infants identified HIV Positive within 12 months after birth and Started ART", ReportUtils.<CohortDefinition>map(datimCohorts.infantsTurnedHIVPositiveOnART(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number of Mothers Already on ART at the start of current Pregnancy
     * @return the indicator
     */
    public CohortIndicator mothersAlreadyOnARTAtStartOfCurrentPregnancy() {
        return cohortIndicator("Mothers Already on ART at the start of current Pregnancy", ReportUtils.<CohortDefinition>map(datimCohorts.alreadyOnARTAtBeginningOfPregnacy(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     *TB_ART Proportion of HIV-positive new and relapsed TB cases New on ART during TB treatment
     * @return the indicator
     */
    public CohortIndicator newOnARTTBInfected() {
        return cohortIndicator("TB patients new on ART", ReportUtils.<CohortDefinition>map(datimCohorts.newOnARTTBInfected(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TB_ART Proportion of HIV-positive new and relapsed TB cases Already on ART during TB treatment
     * @return the indicator
     */
    public CohortIndicator alreadyOnARTTBInfected() {
        return cohortIndicator("TB patients already on ART", ReportUtils.<CohortDefinition>map(datimCohorts.alreadyOnARTTBInfected(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of Mothers new on ART during current pregnancy
     * @return the indicator
     */
    public CohortIndicator mothersNewOnARTDuringCurrentPregnancy() {
        return cohortIndicator("Mothers new on ART during current pregnancy", ReportUtils.<CohortDefinition>map(datimCohorts.newOnARTDuringPregnancy(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number Tested Negative at PITC Inpatient Services
     * @return the indicator
     */
    public CohortIndicator testedNegativeAtPITCInpatientServices() {
        return cohortIndicator("Tested Negative at PITC Inpatient Services", ReportUtils.<CohortDefinition>map(datimCohorts.testedNegativeAtPITCInpatientServices(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number Tested Positive at PITC Inpatient Services
     * @return the indicator
     */
    public CohortIndicator testedPositiveAtPITCInpatientServices() {
        return cohortIndicator("Tested Positive at PITC Inpatient Services", ReportUtils.<CohortDefinition>map(datimCohorts.testedPositiveAtPITCInpatientServices(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number Tested Negative at PITC PMTCT services ANC-1 only
     * @return the indicator
     */
    public CohortIndicator testedNegativeAtPITCPMTCTANC1() {
        return cohortIndicator("Tested Negative at PITC PMTCT services ANC-1", ReportUtils.<CohortDefinition>map(datimCohorts.negativeAtPITCPMTCTANC1(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number Tested Positive at PITC PMTCT services ANC-1 only
     * @return the indicator
     */
    public CohortIndicator testedPositiveAtPITCPMTCTANC1() {
        return cohortIndicator("Tested Positive at PITC PMTCT services ANC-1", ReportUtils.<CohortDefinition>map(datimCohorts.positiveAtPITCPMTCTANC1(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number Tested Negative at PITC PMTCT services Post ANC-1 (including labour and delivery and BF)
     * @return the indicator
     */
    public CohortIndicator testedNegativeAtPITCPMTCTPostANC1() {
        return cohortIndicator("Tested Negative at PITC PMTCT services Post ANC-1", ReportUtils.<CohortDefinition>map(datimCohorts.negativeAtPITCPMTCTPostANC1(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number Tested Positive at PITC PMTCT services Post ANC-1 (including labour and delivery and BF)
     * @return the indicator
     */
    public CohortIndicator testedPositiveAtPITCPMTCTPostANC1() {
        return cohortIndicator("Tested Positive at PITC PMTCT services Post ANC-1", ReportUtils.<CohortDefinition>map(datimCohorts.positiveAtPITCPMTCTPostANC1(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number Tested Positive at PITC Paediatric services
     * @return the indicator
     */
    public CohortIndicator testedPositiveAtPITCPaediatricServices() {
        return cohortIndicator("Tested Positive at PITC Paediatric services", ReportUtils.<CohortDefinition>map(datimCohorts.testedPositiveAtPITCPaediatricServices(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number Tested Negative at PITC Paediatric services
     * @return the indicator
     */
    public CohortIndicator testedNegativeAtPITCPaediatricServices() {
        return cohortIndicator("Tested Negative at PITC Paediatric services", ReportUtils.<CohortDefinition>map(datimCohorts.testedNegativeAtPITCPaediatricServices(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * PITC Malnutrition Clinics Negative
     * @return the indicator
     */
    public CohortIndicator testedNegativeAtPITCMalnutritionClinic() {
        return cohortIndicator("Tested Negative at PITC Malnutrition Clinics", ReportUtils.<CohortDefinition>map(datimCohorts.testedNegativeAtPITCMalnutritionClinics(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * PITC Malnutrition Clinics Positive
     * @return the indicator
     */
    public CohortIndicator testedPositiveAtPITCMalnutritionClinic() {
        return cohortIndicator("Tested Positive at PITC Malnutrition Clinics", ReportUtils.<CohortDefinition>map(datimCohorts.testedPositiveAtPITCMalnutritionClinics(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * PITC TB Clinic Negative
     * @return the indicator
     */
    public CohortIndicator testedNegativeAtPITCTBClinic() {
        return cohortIndicator("Tested Negative at PITC TB Clinic", ReportUtils.<CohortDefinition>map(datimCohorts.testedNegativeAtPITCTBClinic(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * PITC TB Clinic Positive
     * @return the indicator
     */
    public CohortIndicator testedPositiveAtPITCTBClinic() {
        return cohortIndicator("Tested Positive at PITC TB Clinic", ReportUtils.<CohortDefinition>map(datimCohorts.testedPositiveAtPITCTBClinic(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Tested Negative at PITC Other
     * @return the indicator
     */
    public CohortIndicator testedNegativeAtPITCOther() {
        return cohortIndicator("Tested Negative at PITC Other", ReportUtils.<CohortDefinition>map(datimCohorts.testedNagativeAtPITCOther(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Tested Positive at PITC Other
     * @return the indicator
     */
    public CohortIndicator testedPositiveAtPITCOther() {
        return cohortIndicator("Tested Positive at PITC Other", ReportUtils.<CohortDefinition>map(datimCohorts.testedPositiveAtPITCOther(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Tested Negative at PITC VCT
     * @return the indicator
     */
    public CohortIndicator testedNegativeAtPITCVCT() {
        return cohortIndicator("Tested Negative at PITC VCT", ReportUtils.<CohortDefinition>map(datimCohorts.testedNagativeAtPITCVCT(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Tested Positive at PITC VCT
     * @return the indicator
     */
    public CohortIndicator testedPositiveAtPITCVCT() {
        return cohortIndicator("Tested Positive at PITC VCT", ReportUtils.<CohortDefinition>map(datimCohorts.testedPositiveAtPITCVCT(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * PITC Index Negative
     * @return the indicator
     */
    public CohortIndicator indexTestedNegative() {
        return cohortIndicator("PITC Index Negative", ReportUtils.<CohortDefinition>map(datimCohorts.indexTestedNegative(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * PITC Index Positive
     * @return the indicator
     */
    public CohortIndicator indexTestedPositive() {
        return cohortIndicator("PITC Index Positive", ReportUtils.<CohortDefinition>map(datimCohorts.indextestedPositive(),
                "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number Newly Started ART While BreastFeeding
     * @return the indicator
     */
    public CohortIndicator newlyStartedARTWhileBF() {
        return cohortIndicator("Newly Started ART While Breastfeeding", ReportUtils.<CohortDefinition>map(datimCohorts.newlyStartedARTWhileBreastFeeding(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number who Newly Started ART While Pregnant
     * @return the indicator
     */
    public CohortIndicator newlyStartedARTWhilePregnant() {
        return cohortIndicator("Newly Started ART While Pregnant", ReportUtils.<CohortDefinition>map(datimCohorts.newlyStartedARTWhilePregnant(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number Newly Started ART While Confirmed TB and / or TB Treated
     * @return the indicator
     */
    public CohortIndicator newlyStartedARTWithTB() {
        return cohortIndicator("Newly Started ART While Confirmed TB and / or TB Treated", ReportUtils.<CohortDefinition>map(datimCohorts.newlyStartedARTWithTB(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Disaggregated by Age / Sex
     * @return the indicator
     */
    public CohortIndicator newlyStartedARTByAgeSex() {
        return cohortIndicator("Newly Started ART While Confirmed TB and / or TB Treated", ReportUtils.<CohortDefinition>map(datimCohorts.newlyStartedARTByAgeSex(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /*Annual Cohort Indicators*/
    /**
     * PMTCT_FO Number of HIV-exposed infants who were born 24 months prior to the reporting period
     * @return the indicator
     */
    public CohortIndicator totalHEI() {
        return cohortIndicator("Total HEI Cohort", ReportUtils.<CohortDefinition>map(datimCohorts.totalHEICohort(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * PMTCT_FO HEI Cohort HIV infected
     * @return the indicator
     */
    public CohortIndicator hivInfectedHEI() {
        return cohortIndicator("HIV Infected HEI Cohort", ReportUtils.<CohortDefinition>map(datimCohorts.hivInfectedHEICohort(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * PMTCT_FO HEI Cohort HIV uninfected
     * @return the indicator
     */
    public CohortIndicator hivUninfectedHEI() {
        return cohortIndicator("Uninfected HEI Cohort", ReportUtils.<CohortDefinition>map(datimCohorts.hivUninfectedHEICohort(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * PMTCT_FO HEI Cohort HIV-final status unknown
     * @return the indicator
     */
    public CohortIndicator unknownHIVStatusHEI() {
        return cohortIndicator("Unknown HIV Status HEI Cohort", ReportUtils.<CohortDefinition>map(datimCohorts.unknownHIVStatusHEICohort(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * PMTCT_FO HEI died with HIV-final status unknown
     * @return the indicator
     */
    public CohortIndicator heiDiedWithunknownHIVStatus() {
        return cohortIndicator("HEI Died with Unknown HIV Status", ReportUtils.<CohortDefinition>map(datimCohorts.heiDiedWithUnknownStatus(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_RTT Number restarted Treatment during the reporting period
     * @return the indicator
     */
    public CohortIndicator returnedToTreatment() {
        return cohortIndicator("Number restarted Treatment during the reporting period", ReportUtils.<CohortDefinition>map(datimCohorts.returnedToTreatment(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_RET Number of pregnant women who are still alive and on treatment at 12 months after initiating ART
     * @return the indicator
     */
    public CohortIndicator alivePregnantOnARTLast12Months() {
        return cohortIndicator("Alive, Pregnant and on ART for last 12 months", ReportUtils.<CohortDefinition>map(datimCohorts.pregnantAliveOnARTLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_RET Number of breastfeeding mothers who are still alive and on treatment at 12 months after initiating ART
     * @return the indicator
     */
    public CohortIndicator aliveBfOnARTLast12Months() {
        return cohortIndicator("Alive, Breastfeeding and on ART for last 12 months", ReportUtils.<CohortDefinition>map(datimCohorts.bfAliveOnARTLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_RET Number of adults and children who are still alive and on treatment at 12 months after initiating ART Disaggregated by age / sex
     * @return the indicator
     */
    public CohortIndicator aliveOnlyOnARTInLast12MonthsByAgeSex() {
        return cohortIndicator("Alive on ART in last 12 months by Age / Sex", ReportUtils.<CohortDefinition>map(datimCohorts.aliveOnARTInLast12MonthsByAgeSex(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_RET Denominator Started ART last 12 months and breastfeeding includes dead, stopped, lost follow-up
     * @return the indicator
     */
    public CohortIndicator totalBFStartedARTLast12Months() {
        return cohortIndicator("Total started ART in last 12 months and Breastfeeding", ReportUtils.<CohortDefinition>map(datimCohorts.breastfeedingAndstartedARTinLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_RET Denominator Started ART last 12 months and Pregnant includes dead, stopped, lost follow-up
     * @return the indicator
     */
    public CohortIndicator totalPregnantStartedARTLast12Months() {
        return cohortIndicator("Total started ART in last 12 months and Pregnant", ReportUtils.<CohortDefinition>map(datimCohorts.pregnantAndstartedARTinLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_RET (Denominator) All started ART last 12 months disaggregated by Age/sex includes dead, stopped, lost follow-up
     * @return the indicator
     */
    public CohortIndicator allOnARTLast12MonthsByAgeSex() {
        return cohortIndicator("Total on ART in last 12 months by Age / Sex", ReportUtils.<CohortDefinition>map(datimCohorts.totalOnARTLast12MonthsByAgeSex(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number of adults and pediatric patients on ART with suppressed Routine viral load results (<1,000 copies/ml) results within the past 12 months
     * @return the indicator
     */
    public CohortIndicator onARTSuppRoutineVLLast12Months() {
        return cohortIndicator("Patients on ART with Suppressed routine VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.onARTWithSuppressedRoutineVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number of adults and pediatric patients on ART with suppressed Targeted viral load results (<1,000 copies/ml) results within the past 12 months
     * @return the indicator
     */
    public CohortIndicator onARTSuppTargetedVLLast12Months() {
        return cohortIndicator("Patients on ART with Suppressed targeted VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.onARTWithSuppressedTargetedVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS Number of adults and pediatric patients on ART with suppressed undocumented viral load results (<1,000 copies/ml) results within the past 12 months
     * @return the indicator
     */
    public CohortIndicator onARTSuppUndocumentedVLLast12Months() {
        return cohortIndicator("Patients on ART with SuppresbfOnARTWithSuppressedTargetedVLLast12Monthssed undocumented VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.onARTWithSuppressedUndocumentedVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number pregnant women on ART with suppressed Routine viral load results (<1,000 copies/ml) within the past 12 months.
     */
    public CohortIndicator pregnantOnARTWithSuppressedRoutineVLLast12Months() {
        return cohortIndicator("Pregnant Women on ART with Suppressed Routine VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.pregnantOnARTWithSuppressedRoutineVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS Number pregnant women on ART with suppressed targeted viral load results (<1,000 copies/ml) within the past 12 months.
     */
    public CohortIndicator pregnantOnARTSuppTargetedVLLast12Months() {
        return cohortIndicator("Pregnant Women on ART with Suppressed targeted VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.pregnantOnARTWithSuppressedTargetedVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS Number pregnant women with Undocumented ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months.
     */
    public CohortIndicator pregnantOnARTSuppUndocumentedVLLast12Months() {
        return cohortIndicator("Pregnant Women on ART with Suppressed undocumented VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.pregnantOnARTWithSuppressedUndocumentedVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number breastfeeding mother on routine ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months.
     */
    public CohortIndicator bfOnARTSuppRoutineVLLast12Months() {
        return cohortIndicator("Breastfeeding mother on ART with Suppressed Routine VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.bfOnARTSuppRoutineVL(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number breastfeeding mother on targeted ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months.
     */
    public CohortIndicator bfOnARTSuppTargetedVLLast12Months() {
        return cohortIndicator("Breastfeeding mother on ART with Suppressed Targeted VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.bfOnARTSuppTargetedVL(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number breastfeeding mothers on ART with suppressed undocumented viral load results (<1,000 copies/ml) within the past 12 months.
     */
    public CohortIndicator bfOnARTSuppUndocumentedVLLast12Months() {
        return cohortIndicator("Breastfeeding mother on ART with Suppressed undocumented VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.bfOnARTSuppUndocumentedVL(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS Number of Patients on Routine ART test with suppressed viral load results (<1,000 copies/ml) within the past 12 months Disaggregated by Sex/Age
     */
    public CohortIndicator onARTSuppRoutineVLAgeSex() {
        return cohortIndicator("Total on ART in last 12 months by Age / Sex", ReportUtils.<CohortDefinition>map(datimCohorts.onARTSuppRoutineVLBySex(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS Number of Patients on Targeted ART test with suppressed viral load results (<1,000 copies/ml) within the past 12 months Disaggregated by Sex/Age
     */
    public CohortIndicator onARTSuppTargetedVLAgeSex() {
        return cohortIndicator("On ART with Suppressed Targeted VL within last 12 Months by sex/age", ReportUtils.<CohortDefinition>map(datimCohorts.onARTSuppTargetedVLBySex(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number of Patients on ART with undocumented suppressed viral load results (<1,000 copies/ml) within the past 12 months Disaggregated by Sex/Age
     */
    public CohortIndicator onARTSuppUndocumentedVLAgeSex() {
        return cohortIndicator("Patients on ART with Suppressed undocumented VL within last 12 Months by sex/age", ReportUtils.<CohortDefinition>map(datimCohorts.onARTSuppUndocumentedVLBySex(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /*TX_PVLS (Denominator)*//*
     *//**
     * TX_PVLS Total Patients on Routine ART with a viral load result in the past 12 months.
     */
    public CohortIndicator onARTRoutineVLLast12Months() {
        return cohortIndicator("Patients on ART with Routine VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.onARTWithRoutineVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS Total Patients on ART with targeted viral load result in the past 12 months.
     */
    public CohortIndicator onARTTargetedVLLast12Months() {
        return cohortIndicator("Patients on ART with Targeted VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.onARTWithTargetedVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS Total Patients ART with undocumented viral load result in the past 12 months.
     */
    public CohortIndicator totalARTWithUndocumentedVLLast12Months() {
        return cohortIndicator("Patients on ART with undocumented VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.totalOnARTWithUndocumentedVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS  Pregnant women on Routine ART with viral load results within the past 12 months.
     */
    public CohortIndicator pregnantOnARTRoutineVLLast12Months() {
        return cohortIndicator("Pregnant Women on ART with Routine VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.pregnantOnARTWithRoutineVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS  Pregnant women on ART with Targeted viral load results within the past 12 months.
     */
    public CohortIndicator pregnantOnARTTargetedVLLast12Months() {
        return cohortIndicator("Pregnant Women on ART with Targeted VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.pregnantOnARTWithTargetedVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS  Pregnant women on ART with undocumented viral load results within the past 12 months.
     */
    public CohortIndicator pregnantARTUndocumentedVLLast12Months() {
        return cohortIndicator("Pregnant Women on ART with Undocumented VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.pregnantARTWithUndocumentedVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS  Breastfeeding women on ART with Routine viral load results within the past 12 months.
     */
    public CohortIndicator breastfeedingOnARTRoutineVLLast12Months() {
        return cohortIndicator("Breastfeeding Women on ART with Routine VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.breastfeedingOnARTWithRoutineVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS  Breastfeeding women on ART with Targeted viral load results within the past 12 months.
     */
    public CohortIndicator breastfeedingOnARTTargetedVLLast12Months() {
        return cohortIndicator("Breastfeeding Women on ART with Targeted VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.breastfeedingOnARTWithTargetedVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS  Breastfeeding women on ART with undocumented viral load results within the past 12 months.
     */
    public CohortIndicator breastfeedingOnARTUndocumentedVLLast12Months() {
        return cohortIndicator("Breastfeeding Women on ART with Undocumented VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.breastfeedingOnARTWithUndocumentedVLLast12Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number of Patients on ART with Routine viral load test within the past 12 months Disaggregated by Sex/Age
     */
    public CohortIndicator onARTRoutineVLLast12MonthsbyAgeSex() {
        return cohortIndicator("On ART with Routine VL within last 12 Months by sex/age", ReportUtils.<CohortDefinition>map(datimCohorts.onARTWithRoutineVLLast12MonthsByAgeSex(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number of Patients on ART with Targeted viral load test within the past 12 months Disaggregated by Sex/Age
     */
    public CohortIndicator onARTTargetedVLLast12MonthsbyAgeSex() {
        return cohortIndicator("Patients on ART with Targeted VL within last 12 Months by sex/age", ReportUtils.<CohortDefinition>map(datimCohorts.onARTWithTargetedVLLast12MonthsByAgeSex(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number of Patients on ART with Undocumented viral load test within the past 12 months Disaggregated by Sex/Age
     */
    public CohortIndicator onARTUndocumentedVLLast12MonthsbyAgeSex() {
        return cohortIndicator("Patients on ART with undocumented VL within last 12 Months by sex/age", ReportUtils.<CohortDefinition>map(datimCohorts.onARTWithUndocumentedVLLast12MonthsByAgeSex(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_ML Number of ART patients with no clinical contact since their last expected contact
     */
    public CohortIndicator onARTMissedAppointment() {
        return cohortIndicator("Number of ART patients with no clinical contact since their last expected contact", ReportUtils.<CohortDefinition>map(datimCohorts.onARTMissedAppointment(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_ML_DIED Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed)
     */
    public CohortIndicator onARTMissedAppointmentDied() {
        return cohortIndicator("ART patients with no clinical contact since their last expected contact due to death", ReportUtils.<CohortDefinition>map(datimCohorts.onARTMissedAppointmentDied(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_ML_DIED_TB Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed) as a result of TB
     */
    public CohortIndicator onARTMissedAppointmentDiedTB() {
        return cohortIndicator("ART patients with no clinical contact since their last expected contact due to death as a result of TB", ReportUtils.<CohortDefinition>map(datimCohorts.onARTMissedAppointmentDiedTB(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_ML_DIED_CANCER Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed) as a result of Cancer
     */
    public CohortIndicator onARTMissedAppointmentDiedCancer() {
        return cohortIndicator("ART patients with no clinical contact since their last expected contact due to death as a result of Cancer", ReportUtils.<CohortDefinition>map(datimCohorts.onARTMissedAppointmentDiedCancer(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_ML_DIED_OTHER_INFECTIOUS_DISEASE Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed) as a result of other infectious disease
     */
    public CohortIndicator onARTMissedAppointmentDiedOtherInfectious() {
        return cohortIndicator("ART patients with no clinical contact since their last expected contact due to death as a result of other Infectious disease", ReportUtils.<CohortDefinition>map(datimCohorts.onARTMissedAppointmentDiedOtherInfectious(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_ML_DIED_OTHER_DISEASE Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed) as a result of other disease/condition
     */
    public CohortIndicator onARTMissedAppointmentDiedOtherDisease() {
        return cohortIndicator("ART patients with no clinical contact since their last expected contact due to death as a result of other Infectious disease", ReportUtils.<CohortDefinition>map(datimCohorts.onARTMissedAppointmentDiedOtherDisease(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_ML_DIED_NATURAL Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed) as a result of natural causes
     */
    public CohortIndicator onARTMissedAppointmentDiedNatural() {
        return cohortIndicator("ART patients with no clinical contact since their last expected contact due to death as a result of natural causes", ReportUtils.<CohortDefinition>map(datimCohorts.onARTMissedAppointmentDiedNatural(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_ML_DIED_NONNATURAL Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed) as a result of non-natural causes
     */
    public CohortIndicator onARTMissedAppointmentDiedNonNatural() {
        return cohortIndicator("ART patients with no clinical contact since their last expected contact due to death as a result of non-natural causes", ReportUtils.<CohortDefinition>map(datimCohorts.onARTMissedAppointmentDiedNonNatural(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_ML_DIED_UNKNOWN Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed) as a result of unknown causes
     */
    public CohortIndicator onARTMissedAppointmentDiedUnknown() {
        return cohortIndicator("ART patients with no clinical contact since their last expected contact due to death as a result of unknown causes", ReportUtils.<CohortDefinition>map(datimCohorts.onARTMissedAppointmentDiedUnknown(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_ML_PREV_UNDOCUMENTED_TRF Number of ART patients with no clinical contact since their last expected contact due to Previously undocumented transfer
     */
    public CohortIndicator onARTMissedAppointmentTransferred() {
        return cohortIndicator("ART patients with no clinical contact since their last expected contact due to undocumented transfer", ReportUtils.<CohortDefinition>map(datimCohorts.onARTMissedAppointmentTransferred(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_ML_STOPPED_TREATMENT Number of ART patients with no clinical contact since their last expected contact because they stopped treatment
     */
    public CohortIndicator onARTMissedAppointmentStoppedTreatment() {
        return cohortIndicator("ART patients with no clinical contact since their last expected contact because they stopped treatment", ReportUtils.<CohortDefinition>map(datimCohorts.onARTMissedAppointmentStoppedTreatment(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_ML_TRACED_UNLOCATED Number of ART patients with no clinical contact since their last expected contact due to un-traceability
     */
    public CohortIndicator onARTMissedAppointmentUntraceable() {
        return cohortIndicator("ART patients with no clinical contact since their last expected contact and were untraceable", ReportUtils.<CohortDefinition>map(datimCohorts.onARTMissedAppointmentUntraceable(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_ML_NO_TRACE_ATTEMPTED Number of ART patients with no clinical contact since their last expected contact with no tracing attempted
     */
    public CohortIndicator onARTMissedAppointmentNotTraced() {
        return cohortIndicator("Number of ART patients with no clinical contact since their last expected contact and no tracing attempted", ReportUtils.<CohortDefinition>map(datimCohorts.onARTMissedAppointmentNotTraced(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * HTS_INDEX_OFFERED Number of individuals who were offered index testing services
     */
    public CohortIndicator offeredIndexServices() {
        return cohortIndicator("Number of individuals who were offered Index testing services", ReportUtils.<CohortDefinition>map(datimCohorts.offeredIndexServices(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * HTS_INDEX_CONTACTS_ELICITED_MALES_UNDER15
     */
    public CohortIndicator maleContactsUnder15() {
        return cohortIndicator("Number of male contacts under 15 years elicited", ReportUtils.<CohortDefinition>map(datimCohorts.maleContactsUnder15(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HTS_INDEX_CONTACTS_ELICITED_MALES_15+
     */
    public CohortIndicator maleContacts15AndAbove() {
        return cohortIndicator("Number of male contacts over 15 years elicited", ReportUtils.<CohortDefinition>map(datimCohorts.maleContacts15AndAbove(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HTS_INDEX_CONTACTS_ELICITED_FEMALES_UNDER15
     */
    public CohortIndicator femaleContactsUnder15() {
        return cohortIndicator("Number of female contacts under 15 years elicited", ReportUtils.<CohortDefinition>map(datimCohorts.femaleContactsUnder15(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HTS_INDEX_CONTACTS_ELICITED_FEMALES_15+
     */
    public CohortIndicator femaleContacts15AndAbove() {
        return cohortIndicator("Number of female contacts over 15 years elicited", ReportUtils.<CohortDefinition>map(datimCohorts.femaleContacts15AndAbove(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * HTS_INDEX_ACCEPTED Number of individuals who were offered and accepted index testing services
     */
    public CohortIndicator acceptedIndexServices() {
        return cohortIndicator("Number of individuals who accepted Index testing services", ReportUtils.<CohortDefinition>map(datimCohorts.acceptedIndexServices(),
                "startDate=${startDate},endDate=${endDate}"));
    }
/*
    *//**
     * HTS_INDEX_CONTACTS_MALE_POSITIVE_UNDER15 HIV+ male contacts under 15 years
     *//*
    public CohortIndicator positiveMaleContactsUnder15() {
        return cohortIndicator("Male Contacts under 15 years and HIV+", ReportUtils.<CohortDefinition>map(datimCohorts.positiveMaleContactsUnder15(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    *//**
     *HTS_INDEX_CONTACTS_MALE_POSITIVE_OVER15 HIV+ male contacts over 15 years
     *//*
    public CohortIndicator positiveMaleContactsOver15() {
        return cohortIndicator("Male Contacts over 15 years and HIV+", ReportUtils.<CohortDefinition>map(datimCohorts.positiveMaleContactsOver15(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    *//**
     * HTS_INDEX_CONTACTS_MALE_NEGATIVE_UNDER15 HIV Negative male contacts under 15 years
     *//*
    public CohortIndicator negativeMaleContactsUnder15() {
        return cohortIndicator("Male Contacts under 15 years and HIV negative", ReportUtils.<CohortDefinition>map(datimCohorts.negativeMaleContactsUnder15(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    *//**
     * HTS_INDEX_CONTACTS_MALE_NEGATIVE_OVER15 HIV Negative male contacts over 15 years
     *//*
    public CohortIndicator negativeMaleContactsOver15() {
        return cohortIndicator("Male Contacts over 15 years and HIV negative", ReportUtils.<CohortDefinition>map(datimCohorts.negativeMaleContactsOver15(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    *//**
     * HTS_INDEX_CONTACTS_MALE_UNKNOWN_UNDER15 HIV Unknown status male contacts under 15 years
     *//*
    public CohortIndicator unknownStatusMaleContactsUnder15() {
        return cohortIndicator("Male Contacts under 15 years with Unknown HIV status", ReportUtils.<CohortDefinition>map(datimCohorts.unknownStatusMaleContactsUnder15(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    *//**
     *HTS_INDEX_CONTACTS_MALE_UNKNOWN_OVER15 HIV Unknown status male contacts Over 15 years
     *//*
    public CohortIndicator unknownStatusMaleContactsOver15() {
        return cohortIndicator("Male Contacts over 15 years with Unknown HIV status", ReportUtils.<CohortDefinition>map(datimCohorts.unknownStatusMaleContactsOver15(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    *//**
     * HTS_INDEX_CONTACTS_FEMALE_POSITIVE_UNDER15 HIV+ female contacts under 15 years
     *//*
    public CohortIndicator positiveFemaleContactsUnder15() {
        return cohortIndicator("Female Contacts under 15 years and HIV+", ReportUtils.<CohortDefinition>map(datimCohorts.positiveFemaleContactsUnder15(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    *//**
     *HTS_INDEX_CONTACTS_FEMALE_POSITIVE_OVER15 HIV+ female contacts over 15 years
     *//*
    public CohortIndicator positiveFemaleContactsOver15() {
        return cohortIndicator("Female Contacts over 15 years and HIV+", ReportUtils.<CohortDefinition>map(datimCohorts.positiveFemaleContactsOver15(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    *//**
     * HTS_INDEX_CONTACTS_FEMALE_NEGATIVE_UNDER15 HIV Negative female contacts under 15 years
     *//*
    public CohortIndicator negativeFemaleContactsUnder15() {
        return cohortIndicator("Female Contacts under 15 years and HIV negative", ReportUtils.<CohortDefinition>map(datimCohorts.negativeFemaleContactsUnder15(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    *//**
     * HTS_INDEX_CONTACTS_FEMALE_NEGATIVE_OVER15 HIV Negative female contacts over 15 years
     *//*
    public CohortIndicator negativeFemaleContactsOver15() {
        return cohortIndicator("Female Contacts over 15 years and HIV negative", ReportUtils.<CohortDefinition>map(datimCohorts.negativeFemaleContactsOver15(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    *//**
     * HTS_INDEX_CONTACTS_FEMALE_UNKNOWN_UNDER15 HIV Unknown status female contacts under 15 years
     *//*
    public CohortIndicator unknownStatusFemaleContactsUnder15() {
        return cohortIndicator("Female Contacts under 15 years with Unknown HIV status", ReportUtils.<CohortDefinition>map(datimCohorts.unknownStatusFemaleContactsUnder15(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    *//**
     *HTS_INDEX_CONTACTS_FEMALE_UNKNOWN_OVER15 HIV Unknown status female contacts Over 15 years
     *//*
    public CohortIndicator unknownStatusFemaleContactsOver15() {
        return cohortIndicator("Female Contacts over 15 years with Unknown HIV status", ReportUtils.<CohortDefinition>map(datimCohorts.unknownStatusFemaleContactsOver15(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    *//**
     * HTS_INDEX Number of individuals who were identified and tested using Index testing services and received their results
     *//*
    public CohortIndicator testedThroughIndexServices() {
        return cohortIndicator("Number of individuals who were identified and tested using Index testing services", ReportUtils.<CohortDefinition>map(datimCohorts.contactIndexTesting(),
                "startDate=${startDate},endDate=${endDate}"));
    }*/
    /**
     * HTS_INDEX_POSITIVE Number of individuals who were tested Positive using Index testing services
     */
    public CohortIndicator contactTestedPositive() {
        return cohortIndicator("Number of individuals who were tested Positive using Index testing services", ReportUtils.<CohortDefinition>map(datimCohorts.hivPositiveContact(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * HTS_INDEX_NEGATIVE Number of individuals who were tested HIV Negative using Index testing services
     */
    public CohortIndicator contactTestedNegative() {
        return cohortIndicator("Number of individuals who were tested HIV Negative using Index testing services", ReportUtils.<CohortDefinition>map(datimCohorts.hivNegativeContact(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * HTS_INDEX_KNOWN_POSITIVE Known HIV Positive contacts
     */
    public CohortIndicator contactKnownPositive() {
        return cohortIndicator("Known HIV Positive contacts", ReportUtils.<CohortDefinition>map(datimCohorts.knownPositiveContact(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HTS_RECENT Persons aged ≥15 years newly diagnosed with HIV-1 infection who have a test for recent infection
     */
    public CohortIndicator recentHIVInfections() {
        return cohortIndicator("Persons aged ≥15 years newly diagnosed with HIV-1 infection", ReportUtils.<CohortDefinition>map(datimCohorts.recentHIVInfections(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Newly enrolled into PrEP
     */

    public CohortIndicator newlyEnrolledInPrEP() {
        return cohortIndicator("Number of individuals who are newly enrolled in PrEP", ReportUtils.<CohortDefinition>map(datimCohorts.newlyEnrolledInPrEP(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Newly eonrolled to prep with a recent HIV Positive results within 3 months into enrolment
     */

    public CohortIndicator newlyEnrolledInPrEPHIVPos() {
        return cohortIndicator("Newly eonrolled to prep with a recent HIV Positive results within 3 months into enrolment", ReportUtils.<CohortDefinition>map(datimCohorts.newlyEnrolledInPrEPHIVPos(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Newly eonrolled to prep with a recent HIV negative results within 3 months into enrolment
     */

    public CohortIndicator newlyEnrolledInPrEPHIVNeg() {
        return cohortIndicator("Newly eonrolled to prep with a recent HIV negative results within 3 months into enrolment", ReportUtils.<CohortDefinition>map(datimCohorts.newlyEnrolledInPrEPHIVNeg(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Newly enrolled into PrEP
     */

    public CohortIndicator currentlyEnrolledInPrEP() {
        return cohortIndicator("Number of individuals who are newly enrolled in PrEP", ReportUtils.<CohortDefinition>map(datimCohorts.currEnrolledInPrEP(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Previously enrolled on IPT and have completed during this reporting period
     */

    public CohortIndicator previouslyOnIPTCompleted() {
        return cohortIndicator("Number of individuals who were previously on IPT and have completed", ReportUtils.<CohortDefinition>map(datimCohorts.previouslyOnIPTandCompleted(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of beneficiaries served by PEPFAR OVC programs for children and families affected by HIV
     */
    public CohortIndicator totalBeneficiaryOfOVCProgram() {
        return cohortIndicator("Number of beneficiaries served by  PEPFAR OVC program", ReportUtils.<CohortDefinition>map(datimCohorts.beneficiaryOfOVCProgram(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Auto-Calculate Number of ART patients who were screened for TB at least once during the reporting period (negative).
     */
    public CohortIndicator newlyOnArtPatientScreenedNegativeForTB() {
        return cohortIndicator("Number of people on art that tested negative to TB", ReportUtils.<CohortDefinition>map(datimCohorts.artPatientScreenedForTBandResultNegative(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Auto-Calculate Number of ART patients who were screened for TB at least once during the reporting period (positive).
     */
    public CohortIndicator newlyOnArtPatientScreenedPositiveForTB() {
        return cohortIndicator("Number of people on art that tested positive to TB", ReportUtils.<CohortDefinition>map(datimCohorts.artPatientScreenedForTBResultPositive(), "startDate=${startDate},endDate=${endDate}"));

    }

    /**
     * Auto-Calculate Number of ART patients who were screened for TB at least once during the reporting period (negative).
     */
    public CohortIndicator previouslyOnArtPatientScreenedNegativeForTB() {
        return cohortIndicator("Number of people on art that tested negative to TB", ReportUtils.<CohortDefinition>map(datimCohorts.previouslyOnArtPatientScreenedForTBandResultNegative(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Auto-Calculate Number of ART patients who were screened for TB at least once during the reporting period (positive).
     */
    public CohortIndicator previouslyOnArtPatientScreenedPositiveForTB() {
        return cohortIndicator("Number of people on art that tested positive to TB", ReportUtils.<CohortDefinition>map(datimCohorts.previouslyOnArtPatientScreenedForTBResultPositive(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Auto-Calculate Number of ART patients who were screened for TB at least once during the reporting period (negative).
     */
    public CohortIndicator PreviouslyOnART_EnrolledOn_TB_ThisReportingPeriod() {
        return cohortIndicator("Number of patients on art enrolled on tb previous reporting period", ReportUtils.<CohortDefinition>map(datimCohorts.patientPreviouslyOnART_EnrolledOn_TB_ThisReportingPeriod(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Auto-Calculate Number of ART patients who were screened for TB at least once during the reporting period (negative).
     */
    public CohortIndicator NewOnARTEnrolledOnTB_ThisReportingPeriod() {
        return cohortIndicator("Number of patients new on art enrolled on tb this reporting period", ReportUtils.<CohortDefinition>map(datimCohorts.patientNewOnARTEnrolledOnTB_ThisReportingPeriod(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number of ART patients who had a specimen sent for bacteriologic diagnosis of active TB disease
     */
    public CohortIndicator totalPatientsWhoHadSpecimenSentToLab() {
        return cohortIndicator("Number of ART patients who had a specimen sent for bacteriologic diagnosis of active TB disease", ReportUtils.<CohortDefinition>map(datimCohorts.patientsWhoHadSpecimenSentFor_TB_ThisReportingPeriod(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of ART patients who had a positive result returned for bacteriologic diagnosis of active TB disease
     */
    public CohortIndicator patientsWithPositiveResultForBacteriologicDiagnosis() {
        return cohortIndicator("Number of ART patients who had a positive result returned for bacteriologic diagnosis of active TB disease", ReportUtils.<CohortDefinition>map(datimCohorts.patientsWhoHadPositiveResultForBacteriologicDiagnosis(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients whose specimens were sent for  Smear only
     */
    public CohortIndicator patientSWhoseSpecimenSentForSmearOnly() {
        return cohortIndicator("Number of patients whose specimens were sent for  Smear only", ReportUtils.<CohortDefinition>map(datimCohorts.patientsSpecimenSentForSmearOnly(), "startDate=${startDate},endDate=${endDate}"));
    }



}
