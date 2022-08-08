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
     * //Number of FSW with HIV infection receiving antiretroviral therapy (ART
     * @return the indicator
     */
    public CohortIndicator kpCurrentlyOnART(Integer kpType) {
        return cohortIndicator("KP Currently on ART", ReportUtils.map(datimCohorts.currentlyOnARTKP(kpType), "startDate=${startDate},endDate=${endDate}"));
    }
    /**Currently on ART by Months TCA
     * @return the indicator
     */
    public CohortIndicator currentlyOnARTUnder3MonthsMMD() {
        return cohortIndicator("Currently on ART with less than 3 Months TCA", ReportUtils.map(datimCohorts.currentlyOnARTUnder3MonthsMMD(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**Currently on ART by Months TCA
     * @return the indicator
     */
    public CohortIndicator currentlyOnART3To5MonthsMMD() {
        return cohortIndicator("Currently on ART 3-5 Months TCA", ReportUtils.map(datimCohorts.currentlyOnART3To5MonthsMMD(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**Currently on ART by Months TCA
     * @return the indicator
     */
    public CohortIndicator currentlyOnART6MonthsAndAboveMMD() {
        return cohortIndicator("Currently on ART 6+ Months TCA", ReportUtils.map(datimCohorts.currentlyOnART6MonthsAndAboveMMD(), "startDate=${startDate},endDate=${endDate}"));
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
    public CohortIndicator firstTimeCXCASCRNNegative() {
        return cohortIndicator("HIV Positive women on ART screened Negative for cervical cancer 1st time", ReportUtils.<CohortDefinition>map(datimCohorts.firstTimeCXCASCRNNegative(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV Positive women on ART screened Positive for cervical cancer 1st time
     * @return the indicator
     */
    public CohortIndicator firstTimeCXCASCRNPositive() {
        return cohortIndicator("HIV Positive women on ART screened Positive for cervical cancer 1st time", ReportUtils.<CohortDefinition>map(datimCohorts.firstTimeCXCASCRNPositive(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV Positive women on ART screened Negative for cervical cancer 1st time
     * @return the indicator
     */
    public CohortIndicator firstTimeCXCASCRNPresumed() {
        return cohortIndicator("HIV Positive women on ART with Presumed cervical cancer 1st time screening", ReportUtils.<CohortDefinition>map(datimCohorts.firstTimeCXCASCRNPresumed(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * HIV Positive women on ART re-screened Negative for cervical cancer
     * @return the indicator
     */
    public CohortIndicator rescreenedCXCASCRNNegative() {
        return cohortIndicator("HIV Positive Women on ART re-screened Negative for cervical cancere", ReportUtils.<CohortDefinition>map(datimCohorts.rescreenedCXCASCRNNegative(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * HIV Positive women on ART re-screened Positive for cervical cancer
     * @return the indicator
     */
    public CohortIndicator rescreenedCXCASCRNPositive() {
        return cohortIndicator("HIV Positive women on ART re-screened Positive for cervical cancer", ReportUtils.<CohortDefinition>map(datimCohorts.rescreenedCXCASCRNPositive(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * HIV Positive women on ART with presumed cervical cancer during re-screening
     * @return the indicator
     */
    public CohortIndicator rescreenedCXCASCRNPresumed() {
        return cohortIndicator("HIV Positive women on ART with presumed cervical cancer during re-screening", ReportUtils.<CohortDefinition>map(datimCohorts.rescreenedCXCASCRNPresumed(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Women on ART and Cx treatment screened Negative for cervical cancer
     * @return the indicator
     */
    public CohortIndicator postTreatmentCXCASCRNNegative() {
        return cohortIndicator("Women on ART and Cx treatment screened Negative for cervical cancer", ReportUtils.<CohortDefinition>map(datimCohorts.postTreatmentCXCASCRNNegative(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Women on ART and Cx treatment screened Positive for cervical cancer
     * @return the indicator
     */
    public CohortIndicator postTreatmentCXCASCRNPositive() {
        return cohortIndicator("Women on ART and Cx treatment screened Positive for cervical cancer", ReportUtils.<CohortDefinition>map(datimCohorts.postTreatmentCXCASCRNPositive(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Women on ART and Cx treatment screened Positive for cervical cancer
     * @return the indicator
     */
    public CohortIndicator postTreatmentCXCASCRNPresumed() {
        return cohortIndicator("Women on ART and Cx treatment screened Presumed for cervical cancer", ReportUtils.<CohortDefinition>map(datimCohorts.postTreatmentCXCASCRNPresumed(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV positive Women on ART and received Cryotherapy cancer treatment in their first CACX screening
     * @return
     */
    public CohortIndicator firstScreeningCXCATXCryotherapy() {
        return cohortIndicator("HIV positive Women on ART and received Cryotherapy cancer treatment in their first CACX screening", ReportUtils.<CohortDefinition>map(datimCohorts.firstScreeningCXCATXCryotherapy(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV positive Women on ART and received Thermocoagulation cancer treatment in their first CACX screening
     * @return
     */
    public CohortIndicator firstScreeningCXCATXThermocoagulation() {
        return cohortIndicator("HIV positive Women on ART and received Thermocoagulation cancer treatment in their first CACX screening", ReportUtils.<CohortDefinition>map(datimCohorts.firstScreeningCXCATXThermocoagulation(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV positive Women on ART and received LEEP cancer treatment in their first CACX screening
     * @return
     */
    public CohortIndicator firstScreeningCXCATXLEEP() {
        return cohortIndicator("HIV positive Women on ART and received LEEP cancer treatment in their first CACX screening", ReportUtils.<CohortDefinition>map(datimCohorts.firstScreeningCXCATXLEEP(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * HIV positive Women on ART and received Cryotherapy,Thermocoagulation or Leep cancer treatment when rescreened after previous negative
     * @return
     */
    public CohortIndicator rescreenedCXCATxCryotherapy() {
        return cohortIndicator("HIV positive Women on ART and received Cryotherapy,Thermocoagulation or Leep cancer treatment when rescreened after previous negative", ReportUtils.<CohortDefinition>map(datimCohorts.rescreenedCXCATXCryotherapy(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV positive Women on ART and received Thermocoagulation cancer treatment when rescreened after previous negative
     * @return
     */
    public CohortIndicator rescreenedCXCATXThermocoagulation() {
        return cohortIndicator("HIV positive Women on ART and received Thermocoagulation cancer treatment when rescreened after previous negative", ReportUtils.<CohortDefinition>map(datimCohorts.rescreenedCXCATXThermocoagulation(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV positive Women on ART and received LEEP cancer treatment when rescreened after previous negative
     * @return
     */
    public CohortIndicator rescreenedCXCATXLEEP() {
        return cohortIndicator("HIV positive Women on ART and received LEEP cancer treatment in their first CACX screening", ReportUtils.<CohortDefinition>map(datimCohorts.rescreenedCXCATXLEEP(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * HIV positive Women on ART and received Cryotherapy cancer treatment during Post-Treatment follow-up
     * @return
     */
    public CohortIndicator postTxFollowupCXCATxCryotherapy() {
        return cohortIndicator("HIV positive Women on ART and received Cryotherapy cancer treatment during Post-Treatment follow-up", ReportUtils.<CohortDefinition>map(datimCohorts.postTxFollowupCXCATxCryotherapy(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV positive Women on ART and received Thermocoagulation cancer treatment during Post-Treatment follow-up
     * @return
     */
    public CohortIndicator postTxFollowupCXCATXThermocoagulation() {
        return cohortIndicator("HIV positive Women on ART and received Thermocoagulation cancer treatment during Post-Treatment follow-up", ReportUtils.<CohortDefinition>map(datimCohorts.postTxFollowupCXCATXThermocoagulation(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV positive Women on ART and received LEEP cancer treatment during Post-Treatment follow-up
     * @return
     */
    public CohortIndicator postTxFollowupCXCATXLEEP() {
        return cohortIndicator("HIV positive Women on ART and received LEEP cancer treatment during Post-Treatment follow-up", ReportUtils.<CohortDefinition>map(datimCohorts.postTxFollowupCXCATXLEEP(), "startDate=${startDate},endDate=${endDate}"));
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
     * Starting TB treatment newly started ART
     * @return the indicator
     */
    public CohortIndicator startingTBTreatmentNewOnART() {
        return cohortIndicator("Starting TB treatment newly started ART", ReportUtils.<CohortDefinition>map(datimCohorts.startingTBTreatmentNewOnART(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Starting TB treatment previously on ART
     * @return the indicator
     */
    public CohortIndicator startingTBTreatmentPrevOnART() {
        return cohortIndicator("Starting TB treatment previously on ART", ReportUtils.<CohortDefinition>map(datimCohorts.startingTBTreatmentPrevOnART(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * New on ART Screened Positive
     * @return the indicator
     */
    public CohortIndicator newOnARTScreenedPositive() {
        return cohortIndicator("New on ART Screened Positive", ReportUtils.<CohortDefinition>map(datimCohorts.newOnARTScreenedPositive(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Previously on ART Screened Positive
     * @return the indicator
     */
    public CohortIndicator prevOnARTScreenedPositive() {
        return cohortIndicator("Previously on ART Screened Positive", ReportUtils.<CohortDefinition>map(datimCohorts.prevOnARTScreenedPositive(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * New on ART Screened Negative
     * @return the indicator
     */
    public CohortIndicator newOnARTScreenedNegative() {
        return cohortIndicator("New on ART Screened Negative", ReportUtils.<CohortDefinition>map(datimCohorts.newOnARTScreenedNegative(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Previously on ART Screened Negative
     * @return the indicator
     */
    public CohortIndicator prevOnARTScreenedNegative() {
        return cohortIndicator("Previously on ART Screened Negative", ReportUtils.<CohortDefinition>map(datimCohorts.prevOnARTScreenedNegative(), "startDate=${startDate},endDate=${endDate}"));
    }    /**
     * Specimen sent for bacteriologic diagnosis of active TB
     * @return the indicator
     */
    public CohortIndicator specimenSent() {
        return cohortIndicator("Specimen sent for bacteriologic diagnosis of active TB", ReportUtils.<CohortDefinition>map(datimCohorts.specimenSent(), "startDate=${startDate},endDate=${endDate}"));
    }    /**
     * GeneXpert MTB/RIF assay (with or without other testing)
     * @return the indicator
     */
    public CohortIndicator geneXpertMTBRIF() {
        return cohortIndicator("GeneXpert MTB/RIF assay (with or without other testing)", ReportUtils.<CohortDefinition>map(datimCohorts.geneXpertMTBRIF(), "startDate=${startDate},endDate=${endDate}"));
    }    /**
     * Smear microscopy only
     * @return the indicator
     */
    public CohortIndicator smearMicroscopy() {
        return cohortIndicator("Smear microscopy only", ReportUtils.<CohortDefinition>map(datimCohorts.smearMicroscopy(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Additional test other than GeneXpert
     * @return the indicator
     */
    public CohortIndicator additionalTBTests() {
        return cohortIndicator("Additional test other than GeneXpert", ReportUtils.<CohortDefinition>map(datimCohorts.additionalTBTests(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Positive result returned for bacteriologic diagnosis of active TB
     * @return the indicator
     */
    public CohortIndicator resultsReturned() {
        return cohortIndicator("Positive result returned for bacteriologic diagnosis of active TB", ReportUtils.<CohortDefinition>map(datimCohorts.resultsReturned(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number of Mothers new on ART during current pregnancy
     * @return the indicator
     */
    public CohortIndicator mothersNewOnARTDuringCurrentPregnancy() {
        return cohortIndicator("Mothers new on ART during current pregnancy", ReportUtils.<CohortDefinition>map(datimCohorts.newOnARTDuringPregnancy(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number Tested NegativeInpatient Services
     * @return the indicator
     */
    public CohortIndicator testedNegativeInpatientServices() {
        return cohortIndicator("Tested NegativeInpatient Services", ReportUtils.<CohortDefinition>map(datimCohorts.testedNegativeInpatientServices(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number Tested PositiveInpatient Services
     * @return the indicator
     */
    public CohortIndicator testedPositiveInpatientServices() {
        return cohortIndicator("Tested PositiveInpatient Services", ReportUtils.<CohortDefinition>map(datimCohorts.testedPositiveInpatientServices(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     *"Number of males circumcised
     * @return the indicator
     */
    public CohortIndicator malesCircumcised() {
        return cohortIndicator("Number of males circumcised", ReportUtils.<CohortDefinition>map(datimCohorts.malesCircumcised(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     *Number of males circumcised and tested HIV positive at VMMC site
     * @return the indicator
     */
    public CohortIndicator malesCircumcisedTestedHIVPositive() {
        return cohortIndicator("Number of males circumcised and tested HIV positive at VMMC site", ReportUtils.<CohortDefinition>map(datimCohorts.malesCircumcisedTestedHIVPositive(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     *Number of males circumcised and tested HIV negative at VMMC site
     * @return the indicator
     */
    public CohortIndicator malesCircumcisedTestedHIVNegative() {
        return cohortIndicator("Number of males circumcised and tested HIV negative at VMMC site", ReportUtils.<CohortDefinition>map(datimCohorts.malesCircumcisedTestedHIVNegative(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     *Number of males circumcised with indeterminate HIV result at VMMC site or not tested at VMMC site
     * @return the indicator
     */
    public CohortIndicator malesCircumcisedIndeterminateHIVResult() {
        return cohortIndicator("Number of males circumcised with indeterminate HIV result at VMMC site or not tested at VMMC site", ReportUtils.<CohortDefinition>map(datimCohorts.malesCircumcisedIndeterminateHIVResult(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     *Number of males circumcised through surgical procedure
     * @return the indicator
     */
    public CohortIndicator vmmcSurgical() {
        return cohortIndicator("Number of males circumcised through surgical procedure", ReportUtils.<CohortDefinition>map(datimCohorts.vmmcSurgical(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     *Number of males circumcised using device
     * @return the indicator
     */
    public CohortIndicator vmmcDevice() {
        return cohortIndicator("Number of males circumcised using device", ReportUtils.<CohortDefinition>map(datimCohorts.vmmcDevice(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     *Number of males circumcised through surgical procedure and followed up within 14 days
     * @return the indicator
     */
    public CohortIndicator vmmcSurgicalFollowupWithin14Days() {
        return cohortIndicator("Number of males circumcised through surgical procedure and followed up within 14 days", ReportUtils.<CohortDefinition>map(datimCohorts.vmmcSurgicalFollowupWithin14Days(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     *Number of males circumcised through surgical procedure and did not follow up within 14 days
     * @return the indicator
     */
    public CohortIndicator vmmcSurgicalNoFollowupWithin14Days() {
        return cohortIndicator("Number of males circumcised through surgical procedure and did not follow up within 14 days", ReportUtils.<CohortDefinition>map(datimCohorts.vmmcSurgicalNoFollowupWithin14Days(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     *Number of males circumcised using device and followed up within 14 days
     * @return the indicator
     */
    public CohortIndicator vmmcDeviceFollowupWithin14Days() {
        return cohortIndicator("Number of males circumcised using device and followed up within 14 days", ReportUtils.<CohortDefinition>map(datimCohorts.vmmcDeviceFollowupWithin14Days(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     *Number of males circumcised using device and did not follow up within 14 days
     * @return the indicator
     */
    public CohortIndicator vmmcDeviceNoFollowupWithin14Days() {
        return cohortIndicator("Number of males circumcised using device and did not follow up within 14 days", ReportUtils.<CohortDefinition>map(datimCohorts.vmmcDeviceNoFollowupWithin14Days(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number Tested NegativePMTCT services ANC-1 only
     * @return the indicator
     */
    public CohortIndicator testedNegativePMTCTANC1() {
        return cohortIndicator("Tested NegativePMTCT services ANC-1", ReportUtils.<CohortDefinition>map(datimCohorts.negativePMTCTANC1(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number Tested PositivePMTCT services ANC-1 only
     * @return the indicator
     */
    public CohortIndicator testedPositivePMTCTANC1() {
        return cohortIndicator("Tested PositivePMTCT services ANC-1", ReportUtils.<CohortDefinition>map(datimCohorts.positivePMTCTANC1(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number Tested NegativePMTCT services Post ANC-1 (including labour and delivery and BF)
     * @return the indicator
     */
    public CohortIndicator testedNegativePMTCTPostANC1() {
        return cohortIndicator("Tested NegativePMTCT services Post ANC-1", ReportUtils.<CohortDefinition>map(datimCohorts.negativePMTCTPostANC1(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number Tested PositivePMTCT services Post ANC-1 (including labour and delivery and BF)
     * @return the indicator
     */
    public CohortIndicator testedPositivePMTCTPostANC1() {
        return cohortIndicator("Tested PositivePMTCT services Post ANC-1", ReportUtils.<CohortDefinition>map(datimCohorts.positivePMTCTPostANC1(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number Tested PositivePaediatric services
     * @return the indicator
     */
    public CohortIndicator testedPositivePaediatricServices() {
        return cohortIndicator("Tested PositivePaediatric services", ReportUtils.<CohortDefinition>map(datimCohorts.testedPositivePaediatricServices(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number Tested NegativePaediatric services
     * @return the indicator
     */
    public CohortIndicator testedNegativePaediatricServices() {
        return cohortIndicator("Tested NegativePaediatric services", ReportUtils.<CohortDefinition>map(datimCohorts.testedNegativePaediatricServices(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * PITC Malnutrition Clinics Negative
     * @return the indicator
     */
    public CohortIndicator testedNegativeMalnutritionClinic() {
        return cohortIndicator("Tested NegativeMalnutrition Clinics", ReportUtils.<CohortDefinition>map(datimCohorts.testedNegativeMalnutritionClinics(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * PITC Malnutrition Clinics Positive
     * @return the indicator
     */
    public CohortIndicator testedPositiveMalnutritionClinic() {
        return cohortIndicator("Tested PositiveMalnutrition Clinics", ReportUtils.<CohortDefinition>map(datimCohorts.testedPositiveMalnutritionClinics(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * PITC TB Clinic Negative
     * @return the indicator
     */
    public CohortIndicator testedNegativeTBClinic() {
        return cohortIndicator("Tested NegativeTB Clinic", ReportUtils.<CohortDefinition>map(datimCohorts.testedNegativeTBClinic(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * PITC TB Clinic Positive
     * @return the indicator
     */
    public CohortIndicator testedPositiveTBClinic() {
        return cohortIndicator("Tested PositiveTB Clinic", ReportUtils.<CohortDefinition>map(datimCohorts.testedPositiveTBClinic(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Tested NegativeOther
     * @return the indicator
     */
    public CohortIndicator testedNegativeOther() {
        return cohortIndicator("Tested NegativeOther", ReportUtils.<CohortDefinition>map(datimCohorts.testedNegativeOther(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Tested Negative at the STI Clinic
     * @return the indicator
     */
    public CohortIndicator testedNegativeSTIClinic() {
        return cohortIndicator("Tested Negative STI clinic", ReportUtils.<CohortDefinition>map(datimCohorts.testedNegativeSTIClinic(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Tested Positive at the STI Clinic
     * @return the indicator
     */
    public CohortIndicator testedPositiveSTIClinic() {
        return cohortIndicator("Tested Positive STI clinic", ReportUtils.<CohortDefinition>map(datimCohorts.testedPositiveSTIClinic(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Tested Negative at the Emergency ward
     * @return the indicator
     */
    public CohortIndicator testedNegativeEmergencyWard() {
        return cohortIndicator("Tested Negative emergency ward", ReportUtils.<CohortDefinition>map(datimCohorts.testedNegativeEmergencyWard(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Tested Positive at the Emergency ward
     * @return the indicator
     */
    public CohortIndicator testedPositiveEmergencyWard() {
        return cohortIndicator("Tested Positive emergency ward", ReportUtils.<CohortDefinition>map(datimCohorts.testedPositiveEmergencyWard(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Tested Positive at the VMMC services
     * @return the indicator
     */
    public CohortIndicator testedPositveVMMCServices() {
        return cohortIndicator("Tested Positive VMMC services", ReportUtils.<CohortDefinition>map(datimCohorts.testedPositiveVMMCServices(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Tested Negative at the VMMC services
     * @return the indicator
     */
    public CohortIndicator testedNegativeVMMCServices() {
        return cohortIndicator("Tested Negative VMMC services", ReportUtils.<CohortDefinition>map(datimCohorts.testedNegativeVMMCServices(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Tested PositiveOther
     * @return the indicator
     */
    public CohortIndicator testedPositiveOther() {
        return cohortIndicator("Tested PositiveOther", ReportUtils.<CohortDefinition>map(datimCohorts.testedPositiveOther(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * PWID Tested Positive
     * @return the indicator
     */
    public CohortIndicator pwidTestedPositive() {
        return cohortIndicator("PWID Tested Positive", ReportUtils.<CohortDefinition>map(datimCohorts.pwidTestedPositive(),
                "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * PWID Tested Negative
     * @return the indicator
     */
    public CohortIndicator pwidTestedNegative() {
        return cohortIndicator("PWID Tested Negative", ReportUtils.<CohortDefinition>map(datimCohorts.pwidTestedNegative(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * MSM Tested Positive
     * @return the indicator
     */
    public CohortIndicator msmTestedPositive() {
        return cohortIndicator("MSM Tested Positive", ReportUtils.<CohortDefinition>map(datimCohorts.msmTestedPositive(),
                "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * MSM Tested Negative
     * @return the indicator
     */
    public CohortIndicator msmTestedNegative() {
        return cohortIndicator("MSM Tested Negative", ReportUtils.<CohortDefinition>map(datimCohorts.msmTestedNegative(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * FSW Tested Positive
     * @return the indicator
     */
    public CohortIndicator fswTestedPositive() {
        return cohortIndicator("FSW Tested Positive", ReportUtils.<CohortDefinition>map(datimCohorts.fswTestedPositive(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * FSW Tested Negative
     * @return the indicator
     */
    public CohortIndicator fswTestedNegative() {
        return cohortIndicator("FSW Tested Negative", ReportUtils.<CohortDefinition>map(datimCohorts.fswTestedNegative(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TG Tested Negative
     * @return the indicator
     */
    public CohortIndicator tgTestedNegative() {
        return cohortIndicator("TG Tested Negative", ReportUtils.<CohortDefinition>map(datimCohorts.tgTestedNegative(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TG Tested Negative
     * @return the indicator
     */
    public CohortIndicator tgTestedPositive() {
        return cohortIndicator("TG Tested Negative", ReportUtils.<CohortDefinition>map(datimCohorts.tgTestedPositive(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Prisoners Tested Negative
     * @return the indicator
     */
    public CohortIndicator prisonersTestedNegative() {
        return cohortIndicator("Prisoners Tested Negative", ReportUtils.<CohortDefinition>map(datimCohorts.prisonersTestedNegative(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Prisoners Tested Positive
     * @return the indicator
     */
    public CohortIndicator prisonersTestedPositive() {
        return cohortIndicator("Prisoners Tested Positive", ReportUtils.<CohortDefinition>map(datimCohorts.prisonersTestedPositive(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Tested NegativeVCT
     * @return the indicator
     */
    public CohortIndicator testedNegativeVCT() {
        return cohortIndicator("Tested NegativeVCT", ReportUtils.<CohortDefinition>map(datimCohorts.testedNegativeVCT(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Tested PositiveVCT
     * @return the indicator
     */
    public CohortIndicator testedPositiveVCT() {
        return cohortIndicator("Tested PositiveVCT", ReportUtils.<CohortDefinition>map(datimCohorts.testedPositiveVCT(),
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
        return cohortIndicator("PITC Index Positive", ReportUtils.<CohortDefinition>map(datimCohorts.indexTestedPositive(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Tested Positive Social Network
     * @return the indicator
     */
    public CohortIndicator testedPositiveSNS() {
        return cohortIndicator("Tested Positive Social Network", ReportUtils.<CohortDefinition>map(datimCohorts.testedPositiveSNS(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Tested Negative Social Network
     * @return the indicator
     */
    public CohortIndicator testedNegativeSNS() {
        return cohortIndicator("Tested Positive Social Network", ReportUtils.<CohortDefinition>map(datimCohorts.testedNegativeSNS(),
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
        return cohortIndicator("Newly Started ART While Confirmed TB and / or TB Treated", ReportUtils.<CohortDefinition>map(datimCohorts.txNew(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Disaggregated by KP
     * @return the indicator
     */
    public CohortIndicator kpNewlyStartedART(Integer kpType) {
        return cohortIndicator("KP Newly Started ART", ReportUtils.<CohortDefinition>map(datimCohorts.kpNewlyStartedART(kpType),
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
     * PMTCT_FO_DENOMINATOR
     * @return the indicator
     */
    public CohortIndicator pmtctFoDenominator() {
        return cohortIndicator("Number of HIV-exposed infants who were born 24 months prior to the reporting period and registered in the birth cohort", ReportUtils.<CohortDefinition>map(datimCohorts.pmtctFoDenominator(),
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
    public CohortIndicator txRTT() {
        return cohortIndicator("Number restarted Treatment during the reporting period", ReportUtils.<CohortDefinition>map(datimCohorts.txRTT(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_RTT_KP Number of KPs restarted Treatment during the reporting period
     * @return the indicator
     */
    public CohortIndicator txRTTKP(Integer kpType) {
        return cohortIndicator("Number KPs restarted Treatment during the reporting period", ReportUtils.<CohortDefinition>map(datimCohorts.txRTTKP(kpType),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_RTT_IIT_UNDER_3_MONTHS Number of patients restarted Treatment during the reporting period after IIT for less than 3 months
     * @return the indicator
     */
    public CohortIndicator txRTTIITBelow3Months() {
        return cohortIndicator("Number of patients restarted Treatment during the reporting period after IIT for less than 3 months", ReportUtils.<CohortDefinition>map(datimCohorts.txRTTIITBelow3Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_RTT_IIT_3_TO_5_MONTHS Number of patients restarted Treatment during the reporting period after IIT for 3 TO 5 months
     * @return the indicator
     */
    public CohortIndicator txRTTIIT3To5Months() {
        return cohortIndicator("Number of patients restarted Treatment during the reporting period after IIT for 3 to 5 months", ReportUtils.<CohortDefinition>map(datimCohorts.txRTTIIT3To5Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_RTT_IIT_ATLEAST_6_MONTHS Number of patients restarted Treatment during the reporting period after IIT for atleast 6 months
     * @return the indicator
     */
    public CohortIndicator txRTTIITAtleast6Months() {
        return cohortIndicator("Number of patients restarted Treatment during the reporting period after IIT for atleast 6 months", ReportUtils.<CohortDefinition>map(datimCohorts.txRTTIITAtleast6Months(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS KP on ART with suppressed viral load results within the past 12 months.
     */
    public CohortIndicator kpOnARTVLLast12Months(String testType,Integer kpType) {
        return cohortIndicator("KP on ART with VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.kpOnARTVLLast12Months(testType,kpType),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS_DENOMINATOR Number of Patients on ART with viral load test within the past 12 months Disaggregated by Sex/Age
     */
    public CohortIndicator onARTWithVLLast12Months(String testType) {
        return cohortIndicator("On ART with VL within last 12 Months by sex/age", ReportUtils.<CohortDefinition>map(datimCohorts.txpvlsDenominator(testType),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS_DENOMINATOR_PREGNANT Number of Pregnant Patients on ART with viral load test within the past 12 months
     */
    public CohortIndicator txpvlsDenominatorPregnant(String testType) {
        return cohortIndicator("Pregnant On ART with VL within last 12 Months by sex/age", ReportUtils.<CohortDefinition>map(datimCohorts.txpvlsDenominatorPregnant(testType),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS_DENOMINATOR_BF Number of Breastfeeding Patients on ART with viral load test within the past 12 months
     */
    public CohortIndicator txpvlsDenominatorBreastfeeding(String testType) {
        return cohortIndicator("Breastfeeding On ART with VL within last 12 Months by sex/age", ReportUtils.<CohortDefinition>map(datimCohorts.txpvlsDenominatorBreastfeeding(testType),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number pregnant women on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months and pregnant
     */
    public CohortIndicator pregnantOnARTWithSuppressedVLLast12Months(String testType) {
        return cohortIndicator("Pregnant Women on ART with Suppressed VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.pregnantOnARTWithSuppressedVLLast12Months(testType),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_PVLS Number breastfeeding women on ART with suppressed viral load results (<1,000 copies/ml) within the past 12 months.
     */
    public CohortIndicator breastFeedingOnARTWithSuppressedVLLast12Months(String testType) {
        return cohortIndicator("Breastfeeding Women on ART with Suppressed VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.breastfeedingOnARTWithSuppressedVLLast12Months(testType),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Indication by Key Population Type
     */
    public CohortIndicator kpOnARTSuppVLLast12Months(String testType,Integer kpType) {
        return cohortIndicator("Pregnant Women on ART with Suppressed undocumented VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.onARTKpWithSuppVLLast12Months(testType,kpType),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number of Patients on Routine ART test with suppressed viral load results (<1,000 copies/ml) within the past 12 months Disaggregated by Sex/Age
     */
    public CohortIndicator onARTSuppVLAgeSex(String testType) {
        return cohortIndicator("Total on ART in last 12 months by Age / Sex", ReportUtils.<CohortDefinition>map(datimCohorts.onARTSuppVLAgeSex(testType),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS  Breastfeeding women on ART with viral load results within the past 12 months.
     */
    public CohortIndicator breastfeedingOnARTVLLast12Months(String testType) {
        return cohortIndicator("Breastfeeding Women on ART with VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.breastfeedingOnARTVLLast12Months(testType),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS KP on ART with viral load results within the past 12 months.
     */
    public CohortIndicator kpWithVLLast12Months(String testType,KPTypeDataDefinition kpType) {
        return cohortIndicator("KP on ART with VL within last 12 Months", ReportUtils.<CohortDefinition>map(datimCohorts.kpWithVLLast12Months(testType,kpType),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_PVLS Number of Patients on ART with viral load test within the past 12 months Disaggregated by Sex/Age
     */
    public CohortIndicator onARTAndVLLast12MonthsbyAgeSex(String testType) {
        return cohortIndicator("On ART with  VL within last 12 Months by sex/age", ReportUtils.<CohortDefinition>map(datimCohorts.onARTAndVLLast12MonthsbyAgeSex(testType),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_ML Number of ART patients with no clinical contact since their last expected contact
     */
    public CohortIndicator txML() {
        return cohortIndicator("Number of ART patients with no clinical contact since their last expected contact", ReportUtils.<CohortDefinition>map(datimCohorts.txML(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_ML KP Stop reason
     */
    public CohortIndicator txmlKPPatientDied(Integer kpType) {
        return cohortIndicator("TX ml KPs who died", ReportUtils.<CohortDefinition>map(datimCohorts.txmlKPPatientDied(kpType),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_ML KP Stop reason other than death
     */
    public CohortIndicator txmlKPStopReason(Integer kpType) {
        return cohortIndicator("TX ml KPs by who stop treatment", ReportUtils.<CohortDefinition>map(datimCohorts.txmlKPStopReason(kpType),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_ML KP transferred out
     */
    public CohortIndicator txmlKPsTransferredOut(Integer kpType) {
        return cohortIndicator("TX ml KPs by who transferred out", ReportUtils.<CohortDefinition>map(datimCohorts.txmlKPSTransferredOut(kpType),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_ML KP IIT
     */
    public CohortIndicator txMLIITKpUnder3MonthsInTx(Integer kpType) {
        return cohortIndicator("TX ml KPs by IIT", ReportUtils.<CohortDefinition>map(datimCohorts.txMLIITKpUnder3MonthsInTx(kpType),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_ML KP IIT
     */
    public CohortIndicator txMLIITKp3To5MonthsInTx(Integer kpType) {
        return cohortIndicator("TX ml KPs by IIT", ReportUtils.<CohortDefinition>map(datimCohorts.txMLIITKp3To5MonthsInTx(kpType),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_ML KP IIT
     */
    public CohortIndicator txMLIITKpAtleast6Months(Integer kpType) {
        return cohortIndicator("TX ml KPs by IIT", ReportUtils.<CohortDefinition>map(datimCohorts.txMLIITKpAtleast6Months(kpType),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * TX_ML Cause of death
     */
    public CohortIndicator txMLCauseOfDeath(Integer deathReason) {
        return cohortIndicator("TX ml death reason", ReportUtils.<CohortDefinition>map(datimCohorts.txmlPatientByCauseOfDeath(deathReason),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_ML specific Cause of death
     */
    public CohortIndicator txMLSpecificCauseOfDeath(Integer specificDeathReason) {
        return cohortIndicator("TX ml specific death reason", ReportUtils.<CohortDefinition>map(datimCohorts.txMLSpecificCauseOfDeath(specificDeathReason),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_ML_DIED Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed)
     */
    public CohortIndicator txmlPatientByTXStopReason() {
        return cohortIndicator("ART patients with no clinical contact since their last expected contact due to death", ReportUtils.<CohortDefinition>map(datimCohorts.txmlPatientByTXStopReason(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_ML_DIED Number of ART patients with no clinical contact since their last expected contact due to Death (confirmed)
     */
    public CohortIndicator txmlPatientDied() {
        return cohortIndicator("ART patients with no clinical contact since their last expected contact due to death", ReportUtils.<CohortDefinition>map(datimCohorts.txmlPatientDied(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_ML_TO Number of ART patients with no clinical contact since their last expected contact due to Transfer out (verified)
     */
    public CohortIndicator txmlTrfOut() {
        return cohortIndicator("Number of ART patients with no clinical contact since their last expected contact due to Transfer out (verified)", ReportUtils.<CohortDefinition>map(datimCohorts.txmlTrfOut(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_ML IIT ON DRUGS <3 MONTHS Number of ART patients with no clinical contact since their last expected contact and have been on drugs for less than 3 months
     */
    public CohortIndicator txMLIITUnder3MonthsInTx() {
        return cohortIndicator("TX_ML_IIT_UNDER3_MONTHS_Tx", ReportUtils.<CohortDefinition>map(datimCohorts.txMLIITUnder3MonthsInTx(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_ML IIT ON DRUGS 3-5 MONTHS Number of ART patients with no clinical contact since their last expected contact and have been on drugs for less than 3 months
     */
    public CohortIndicator txMLIIT3To5MonthsInTx() {
        return cohortIndicator("TX_ML_IIT_UNDER3_MONTHS_Tx", ReportUtils.<CohortDefinition>map(datimCohorts.txMLIIT3To5MonthsInTx(),
                "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * TX_ML IIT ON DRUGS 6+ MONTHS Number of ART patients with no clinical contact since their last expected contact and have been on drugs for less than 3 months
     */
    public CohortIndicator txMLIITAtleast6MonthsInTx() {
        return cohortIndicator("TX_ML_IIT_ATLEAST6_MONTHS_Tx", ReportUtils.<CohortDefinition>map(datimCohorts.txMLIITAtleast6MonthsInTx(),
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
     * HTS_INDEX_CONTACTS_ELICITED
     */
    public CohortIndicator htsIndexContactsElicited() {
        return cohortIndicator("Number of male contacts elicited", ReportUtils.<CohortDefinition>map(datimCohorts.htsIndexContactsElicited(),
                "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HTS_INDEX_ACCEPTED Number of individuals who were offered and accepted index testing services
     */
    public CohortIndicator acceptedIndexServices() {
        return cohortIndicator("Number of individuals who accepted Index testing services", ReportUtils.<CohortDefinition>map(datimCohorts.contactsAcceptedIndexTesting(),
                "startDate=${startDate},endDate=${endDate}"));
    }
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
     * HTS_INDEX_DOCUMENTED_NEGATIVE Number of children with reported negative status
     * Without any documented test in the EMR
     * below 14 yrs using Index testing services
     */
    public CohortIndicator contactsReportedNegative() {
        return cohortIndicator("Number of children contacts with reported negative status without documented tests below 14 yrs using Index testing services", ReportUtils.<CohortDefinition>map(datimCohorts.contactsReportedNegativeUndocumented(),
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
     * Number of individuals who were already enrolled on oral antiretroviral pre-exposure prophylaxis (PrEP) to prevent HIV infection and came for PrEP followup or re-initiation during the reporting period
     */

    public CohortIndicator prepCT() {
        return cohortIndicator("People who returned for PrEP follow-up or re-initiation", ReportUtils.<CohortDefinition>map(datimCohorts.prepCT(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number of individuals who were already enrolled on oral antiretroviral pre-exposure prophylaxis (PrEP) to prevent HIV infection and came for PrEP followup or re-initiation during the reporting period and tested HIV negative in that visit
     */
    public CohortIndicator prepCTByHIVNegativeStatus() {
        return cohortIndicator("People who returned for PrEP follow-up or re-initiation and tested HIV Negative status", ReportUtils.<CohortDefinition>map(datimCohorts.prepCTByHIVNegativeStatus(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number of individuals who were already enrolled on oral antiretroviral pre-exposure prophylaxis (PrEP) to prevent HIV infection and came for PrEP followup or re-initiation during the reporting period and tested HIV positive in that visit
     */
    public CohortIndicator prepCTByHIVPositiveStatus() {
        return cohortIndicator("People who returned for PrEP follow-up or re-initiation", ReportUtils.<CohortDefinition>map(datimCohorts.prepCTByHIVPositiveStatus(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number of individuals who were already enrolled on oral antiretroviral pre-exposure prophylaxis (PrEP) to prevent HIV infection and came for PrEP followup or re-initiation during the reporting period without HIV test during visit
     */
    public CohortIndicator prepCTNotTestedForHIV() {
        return cohortIndicator("People who returned for PrEP follow-up or re-initiation ", ReportUtils.<CohortDefinition>map(datimCohorts.prepCTNotTestedForHIV(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of KP individuals who were already enrolled on oral antiretroviral pre-exposure prophylaxis (PrEP) to prevent HIV infection and came for PrEP followup or re-initiation during the reporting period
     */
    public CohortIndicator prepCTKP(Integer kpType) {
        return cohortIndicator("People who returned for PrEP follow-up or re-initiation", ReportUtils.<CohortDefinition>map(datimCohorts.prepCTKP(kpType), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of individuals who were already enrolled on oral antiretroviral pre-exposure prophylaxis (PrEP) to prevent HIV infection and came for PrEP followup or re-initiation during the reporting period while pregnant
     */
    public CohortIndicator prepCTPregnant() {
        return cohortIndicator("People who returned for PrEP follow-up or re-initiation", ReportUtils.<CohortDefinition>map(datimCohorts.prepCTPregnant(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number of individuals who were already enrolled on oral antiretroviral pre-exposure prophylaxis (PrEP) to prevent HIV infection and came for PrEP followup or re-initiation during the reporting period while Breastfeeding
     */
    public CohortIndicator prepCTBreastfeeding() {
        return cohortIndicator("People who returned for PrEP follow-up or re-initiation", ReportUtils.<CohortDefinition>map(datimCohorts.prepCTBreastfeeding(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Newly enrolled into PrEP
     */

    public CohortIndicator newlyEnrolledInPrEP() {
        return cohortIndicator("Number of individuals who are newly enrolled in PrEP", ReportUtils.<CohortDefinition>map(datimCohorts.newlyEnrolledInPrEP(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Newly enrolled into PrEP KPs
     */

    public CohortIndicator newlyEnrolledInPrEPKP(Integer kpType) {
        return cohortIndicator("Number of KPs who are newly enrolled in PrEP", ReportUtils.<CohortDefinition>map(datimCohorts.newlyEnrolledInPrEPKP(kpType), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Previously enrolled on IPT and have completed during this reporting period
     */
    public CohortIndicator previouslyOnIPTCompleted() {
        return cohortIndicator("Number of individuals who were previously on TPT and have completed", ReportUtils.<CohortDefinition>map(datimCohorts.previouslyOnIPTandCompleted(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Newly initiated on ART initiated on IPT
     */
    public CohortIndicator newARTpreviouslyOnIPTInitiated() {
        return cohortIndicator("Number of individuals who are newly initiated on ART and initiated on TPT", ReportUtils.<CohortDefinition>map(datimCohorts.newOnARTprevOnIPTandInitiated(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Previously initiated on ART and initiated on IPT
     */
    public CohortIndicator previouslyARTandIPTInitiated() {
        return cohortIndicator("Number of individuals who are previously initiated on ART and initiated on TPT", ReportUtils.<CohortDefinition>map(datimCohorts.previousOnARTandIPTandInitiated(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Newly initiated on ART Previously enrolled on IPT and have completed during this reporting period
     */
    public CohortIndicator newARTpreviouslyOnIPTCompleted() {
        return cohortIndicator("Number of individuals who are newly initiated on ART and were previously on TPT and have completed", ReportUtils.<CohortDefinition>map(datimCohorts.newOnARTprevOnIPTandCompleted(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Previously initiated on ART and enrolled on IPT and have completed during this reporting period
     */
    public CohortIndicator previouslyARTandIPTCompleted() {
        return cohortIndicator("Number of individuals who are previously initiated on ART and were previously on TPT and have now completed", ReportUtils.<CohortDefinition>map(datimCohorts.previousOnARTandIPTandCompleted(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number of beneficiaries served by PEPFAR OVC comprehensive programs for children and families affected by HIV
     */
    public CohortIndicator totalBeneficiaryOfOVCComprehensiveProgram() {
        return cohortIndicator("Number of beneficiaries served by  PEPFAR OVC Comprehensive program", ReportUtils.<CohortDefinition>map(datimCohorts.totalBeneficiaryOfOVCComprehensiveProgram(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of beneficiaries served by PEPFAR OVC Dreams programs for children and families affected by HIV
     */
    public CohortIndicator totalBeneficiaryOfOVCDreamsProgram() {
        return cohortIndicator("Number of beneficiaries served by  PEPFAR OVC Comprehensive program", ReportUtils.<CohortDefinition>map(datimCohorts.totalBeneficiaryOfOVCDreamsProgram(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of beneficiaries served by PEPFAR OVC preventive programs for children and families affected by HIV
     */
    public CohortIndicator totalBeneficiaryOfOVCPreventiveProgram() {
        return cohortIndicator("Number of beneficiaries served by  PEPFAR OVC preventive program", ReportUtils.<CohortDefinition>map(datimCohorts.totalBeneficiaryOfOVCPreventiveProgram(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of key populations reached with individual and/or small group-level HIV prevention interventions designed for the target population
     */
    public CohortIndicator kpPrev(String kpType) {
        return cohortIndicator("Number of KPs received prevention services",
                ReportUtils.map(datimCohorts.kpPrev(kpType), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * KP_PREV disaggregated by Number of Known Positive KPs received prevention services
     * @param kpType
     * @return
     */
    public CohortIndicator kpPrevKnownPositive(String kpType) {
        return cohortIndicator("Number of Known Positive KPs received prevention services",
                ReportUtils.map(datimCohorts.kpPrevKnownPositive(kpType), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * KP_PREV disaggregated by Number of KPs received prevention services and newly tested or referred for HTS
     * @param kpType
     * @return
     */
    public CohortIndicator kpPrevNewlyTestedOrReferred(String kpType) {
        return cohortIndicator("Number of KPs received prevention services and newly tested or referred for HTS",
                ReportUtils.map(datimCohorts.kpPrevNewlyTestedOrReferred(kpType), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * KP_PREV disaggregated by Number of KPs received prevention services and declined HTS
     * @param kpType
     * @return
     */
    public CohortIndicator kpPrevDeclinedTesting(String kpType) {
        return cohortIndicator("Number of KPs received prevention services and declined HTS",
                ReportUtils.map(datimCohorts.kpPrevDeclinedTesting(kpType), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number of beneficiaries for Sexual violence (post-rape care)
     */
    public CohortIndicator sexualGBV() {
        return cohortIndicator("Number of beneficiaries for Sexual violence (post-rape care)", ReportUtils.<CohortDefinition>map(datimCohorts.sexualGBV(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of beneficiaries for Physical and/or emotional violence (other Post-GBV) care
     */
    public CohortIndicator physicalEmotionalGBV() {
        return cohortIndicator("Number of beneficiaries for Physical and/or emotional violence (other Post-GBV) care", ReportUtils.<CohortDefinition>map(datimCohorts.physicalEmotionalGBV(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of beneficiaries for Post-exposure prophylaxis (PEP) Services
     */
    public CohortIndicator receivedPEP() {
        return cohortIndicator("Number of beneficiaries for post-exposure prophylaxis (PEP) Services", ReportUtils.<CohortDefinition>map(datimCohorts.receivedPEP(), "startDate=${startDate},endDate=${endDate}"));
    }

}
