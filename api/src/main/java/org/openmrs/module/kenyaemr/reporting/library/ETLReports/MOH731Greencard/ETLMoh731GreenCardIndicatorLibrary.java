/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731Greencard;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.RevisedDatim.DatimCohortLibrary;
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
    @Autowired
    private DatimCohortLibrary datimCohorts;

    // Green card additions

    /**
     * HIV counseling and testing
     * covers indicators HV01-01 - HV01-10
     *
     * @return indicator
     */
    public CohortIndicator htsNumberTested() {
        return cohortIndicator("Individuals tested", map(moh731Cohorts.htsNumberTested(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV counseling and testing at health facility
     * covers indicators  HV01-11
     *
     * @return indicator
     */
    public CohortIndicator htsNumberTestedAtFacility() {
        return cohortIndicator("Individuals tested at the facility", map(moh731Cohorts.htsNumberTestedAtFacility(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV counseling and testing at community
     * covers indicators  HV01-12
     *
     * @return indicator
     */
    public CohortIndicator htsNumberTestedAtCommunity() {
        return cohortIndicator("Individuals tested at the community", map(moh731Cohorts.htsNumberTestedAtCommunity(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * New tests
     * covers indicators  HV01-13
     *
     * @return indicator
     */
    public CohortIndicator htsNumberTestedNew() {
        return cohortIndicator("New tests", map(moh731Cohorts.htsNumberTestedNew(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Repeat tests
     * covers indicators  HV01-14
     *
     * @return indicator
     */
    public CohortIndicator htsNumberTestedRepeat() {
        return cohortIndicator("Repeat tests", map(moh731Cohorts.htsNumberTestedRepeat(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV couseling and testing: couples
     * covers indicators HV01-15
     *
     * @return indicator
     */
    public CohortIndicator htsNumberTestedAsCouple() {
        return cohortIndicator("Couple testing", map(moh731Cohorts.htsNumberTestedAsCouple(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV couseling and testing: Key population
     * covers indicators  HV01-16
     *
     * @return indicator
     */
    public CohortIndicator htsNumberTestedKeyPopulation() {
        return cohortIndicator("Key population testing", map(moh731Cohorts.htsNumberTestedKeyPopulation(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV couseling and testing: Positive results
     * covers indicators HV01-17 - HV01-26
     *
     * @return indicator
     */
    public CohortIndicator htsNumberTestedPositive() {
        return cohortIndicator("HIV Positive tests", map(moh731Cohorts.htsNumberTestedPositive(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV couseling and testing: Negative total
     * covers indicators HV01-27
     *
     * @return indicator
     */
    public CohortIndicator htsNumberTestedNegative() {
        return cohortIndicator("HIV Negative tests", map(moh731Cohorts.htsNumberTestedNegative(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV couseling and testing: Discordant couples
     * covers indicators HV01-28
     *
     * @return indicator
     */
    public CohortIndicator htsNumberTestedDiscordant() {
        return cohortIndicator("Discordant couples", map(moh731Cohorts.htsNumberTestedDiscordant(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV couseling and testing: Negative total
     * covers indicators HV01-29
     *
     * @return indicator
     */
    public CohortIndicator htsNumberTestedKeypopPositive() {
        return cohortIndicator("Key Pop - positives", map(moh731Cohorts.htsNumberTestedKeypopPositive(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV couseling and testing: turned positive within last 3 months and linked to care during reporting period
     * covers indicators HV01-30 - HV01-35
     *
     * @return indicator
     */
    public CohortIndicator htsNumberTestedPositiveAndLinked() {
        return cohortIndicator("Positive and linked to care", map(moh731Cohorts.htsNumberTestedPositiveAndLinked(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV couseling and testing: number tested positive in last 3 months
     * covers indicators HV01-36
     *
     * @return indicator
     */
    public CohortIndicator htsNumberTestedPositiveInLastThreeMonths() {
        return cohortIndicator("tested Positive in last 3 months", map(moh731Cohorts.htsNumberTestedPositiveInLastThreeMonths(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients who did First ANC visit during that period {@link org.openmrs.module.kenyaemr.PregnancyStage}
     * First ANC visit  HV02-01
     *
     * @return the indicator
     */
    public CohortIndicator firstANCVisitMchmsAntenatal() {
        return cohortIndicator(null,
                map(moh731Cohorts.firstANCVisitMchmsAntenatal(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested for HIV in MCHMS during the DELIVERY {@link org.openmrs.module.kenyaemr.PregnancyStage}
     * Delivery for HIV Positive mothers HV02-02
     *
     * @return the indicator
     */
    public CohortIndicator deliveryFromHIVPositiveMothers() {
        return cohortIndicator(null,
                map(moh731Cohorts.deliveryFromHIVPositiveMothers(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who are known positive at First ANC {@link org.openmrs.module.kenyaemr.PregnancyStage}
     * Known Positive at 1st ANC HV02-03
     *
     * @return the indicator
     */
    public CohortIndicator knownPositiveAtFirstANC() {
        return cohortIndicator(null,
                map(moh731Cohorts.knownPositiveAtFirstANC(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested for HIV in MCHMS during the ANTENATAL {@link org.openmrs.module.kenyaemr.PregnancyStage}
     * Initial test at ANC  HV02-04
     *
     * @return the indicator
     */
    public CohortIndicator initialHIVTestInMchmsAntenatal() {
        return cohortIndicator(null,
                map(moh731Cohorts.initialHIVTestInMchmsAntenatal(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested for HIV in MCHMS during the DELIVERY {@link org.openmrs.module.kenyaemr.PregnancyStage}
     * Initial test at labour and delivery HV02-05
     *
     * @return the indicator
     */
    public CohortIndicator testedForHivInMchmsDelivery() {
        return cohortIndicator(null,
                map(moh731Cohorts.testedForHivInMchmsDelivery(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested for HIV in MCHMS during the POSTNATAL {@link org.openmrs.module.kenyaemr.PregnancyStage}
     * Initial Test at PNC<=6 weeks HV02-06
     *
     * @return the indicator
     */
    public CohortIndicator initialTestAtPNCUpto6Weeks() {
        return cohortIndicator(null,
                map(moh731Cohorts.initialTestAtPNCUpto6Weeks(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested for HIV in MCHMS during any {@link org.openmrs.module.kenyaemr.PregnancyStage} after enrollment
     * Known HIV Status total HV02-07
     *
     * @return the indicator
     */
    public CohortIndicator testedForHivInMchms() {
        return cohortIndicator(null,
                map(moh731Cohorts.testedForHivInMchmsTotal(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested for HIV in MCHMS during any {@link org.openmrs.module.kenyaemr.PregnancyStage} after enrollment
     * Retesting_PNC <=6 weeks HV02-08
     *
     * @return the indicator
     */
    public CohortIndicator pncRetestUpto6Weeks() {
        return cohortIndicator(null,
                map(moh731Cohorts.pncRetestUpto6Weeks(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested for HIV in MCHMS during any {@link org.openmrs.module.kenyaemr.PregnancyStage} after enrollment
     * Tested_PNC>6 Weeks to 6 months HV02-09
     *
     * @return the indicator
     */
    public CohortIndicator pncTestBtwn6WeeksAnd6Months() {
        return cohortIndicator(null,
                map(moh731Cohorts.pncTestBtwn6WeeksAnd6Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested HIV +ve before MCHMS
     * Known HIV Positive at 1st ANC HV02-10
     *
     * @return the indicator
     */
    public CohortIndicator knownHivPositiveAtFirstANC() {

        return cohortIndicator(null,
                map(moh731Cohorts.knownPositiveAtFirstANC(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested HIV Positive in MCHMS during the ANTENATAL {@link org.openmrs.module.kenyaemr.PregnancyStage}
     * Positive results ANC HV02-11
     *
     * @return the indicator
     */
    public CohortIndicator testedHivPositiveInMchmsAntenatal() {
        return cohortIndicator(null,
                map(moh731Cohorts.testedHivPositiveInMchmsAntenatal(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested for HIV in MCHMS during the DELIVERY {@link org.openmrs.module.kenyaemr.PregnancyStage}
     * Delivery for HIV Positive mothers HV02-12
     *
     * @return the indicator
     */
    public CohortIndicator testedHivPositiveInMchmsDelivery() {
        return cohortIndicator(null,
                map(moh731Cohorts.positiveHIVResultsAtLabourAndDelivery(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested for HIV in MCHMS during the DELIVERY {@link org.openmrs.module.kenyaemr.PregnancyStage}
     * Positive Results PNC <=6 weeks HV02-13
     *
     * @return the indicator
     */
    public CohortIndicator testedHivPositiveInANCWithin6Weeks() {
        return cohortIndicator(null,
                map(moh731Cohorts.testedHivPositiveInPNCWithin6Weeks(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested HIV Positive in MCHMS during any {@link org.openmrs.module.kenyaemr.PregnancyStage} after enrollment
     * Mothers positive total HV02-14
     *
     * @return the indicator
     */
    public CohortIndicator totalHivPositiveInMchms() {
        return cohortIndicator(null,
                map(moh731Cohorts.totalHivPositiveMothersInMchms(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of infant patients who took pcr test aged between 7 weeks and 6 months and confirmed Positive
     * Positive PNC >6 Weeks to 6 months HV02-15
     *
     * @return the indicator
     */
    public CohortIndicator pncHIVPositiveBetween7weeksAnd6Months() {
        return cohortIndicator("PNC HIV Positive between 7 weeks and 6 months",
                map(moh731Cohorts.totalHivPositivePNC6WeeksTo6monthsInMchms(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Patients on HAART At First ANC
     * On HAART At First ANC  HV02-16
     *
     * @return the indicator
     */
    public CohortIndicator onHAARTAtFirstANC() {
        return cohortIndicator("On HAART At first ANC",
                map(moh731Cohorts.totalOnHAARTAtFirstANC(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Patients started HAART At ANC
     * Start HAART ANC HV02-17
     *
     * @return the indicator
     */
    public CohortIndicator startHAARTANC() {
        return cohortIndicator("Started HAART At ANC",
                map(moh731Cohorts.startedHAARTAtANC(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Patients started HAART during Labour and delivery
     * Start HAART L&D HV02-18
     *
     * @return the indicator
     */
    public CohortIndicator startedHAARTLabourAndDelivery() {
        return cohortIndicator("Started HAART during labour and delivery",
                map(moh731Cohorts.totalStartedHAARTAtLabourAndDelivery(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Patients started HAART during first 6 weeks of PNC
     * Start HAART PNC <=6 weeks HV02-19
     *
     * @return the indicator
     */
    public CohortIndicator startedHAARTPNCUpto6Weeks() {
        return cohortIndicator("Started HAART within 6 weeks of PNC",
                map(moh731Cohorts.totalStartedHAARTAtPNCUpto6Weeks(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Patients started on maternal HAART
     * Total maternal HAART HV02-20
     *
     * @return the indicator
     *
    public CohortIndicator totalMaternalHAART() {
        return cohortIndicator("Total maternal HAART",
                map(moh731Cohorts.totalMaternalHAART(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Patients started HAART between 7 weeks and 6 months
     * Start HAART_PNC>6 weeks to 6 months HV02-21
     * * @return the indicator
     */
    public CohortIndicator onHAARTFrom7WeeksTo6Months() {
        return cohortIndicator("Started HAART between 7 weeks and 6 months",
                map(moh731Cohorts.totalStartedOnHAARTBtw7WeeksAnd6Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Patients started HAART during first 6 weeks of PNC
     * On maternal HAART_12 Months HV02-22
     *
     * @return the indicator
     */
    public CohortIndicator onHAARTUpto12Months() {
        return cohortIndicator("On Maternal HAART upto 12 months",
                map(moh731Cohorts.onHAARTUpto12Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Net cohort at 12 months
     * Net cohort_12 months HV02-23
     *
     * @return the indicator
     */
    public CohortIndicator netCohortAt12Months() {
        return cohortIndicator("Net cohort at 12 months",
                map(moh731Cohorts.netCohortAt12Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Patients screened for syphilis at 1st ANC
     * Syphilis screened 1st ANC HV02-24
     *
     * @return the indicator
     */
    public CohortIndicator syphilisScreenedAt1stANC() {
        return cohortIndicator("Screened for Syphilis at 1st ANC",
                map(moh731Cohorts.syphilisScreenedAt1stANC(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Patients screened positive for syphilis
     * Syphilis screened positive HV02-25
     *
     * @return the indicator
     */
    public CohortIndicator syphilisScreenedPositive() {
        return cohortIndicator("Syphilis Screened Positive",
                map(moh731Cohorts.syphilisScreenedPositive(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Patients Treated for syphilis
     * Syphilis treated HV02-26
     *
     * @return the indicator
     */
    public CohortIndicator syphilisTreated() {
        return cohortIndicator("Treated for syphilis",
                map(moh731Cohorts.treatedForSyphilis(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of HIV+ Patients on modern FP at 6 weeks
     * HIV+ On modern FP at 6 weeks HV02-27
     *
     * @return the indicator
     */
    public CohortIndicator HIVPositiveOnModernFPUpto6Weeks() {
        return cohortIndicator("HIVPositiveOnModernFPUpto6Weeks",
                map(moh731Cohorts.HIVPositiveOnModernFPUpto6Weeks(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of HIV+ Patients with PNC visits at 6 weeks
     * HIV+ PNC Visits at 6 weeks HV02-28
     *
     * @return the indicator
     */
    public CohortIndicator HIVPositivePNCVisitsAt6Weeks() {
        return cohortIndicator("HIVPositivePNCVisitsAt6Weeks",
                map(moh731Cohorts.HIVPositivePNCVisitsAt6Weeks(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Known HIV+ First contacts
     * Known positive status 1st contact HV02-29
     *
     * @return the indicator
     */
    public CohortIndicator knownHIVPositive1stContact() {
        return cohortIndicator("knownHIVPositive1stContact",
                map(moh731Cohorts.knownHIVPositive1stContact(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Male contacts tested at ANC
     * Initial test at ANC male HV02-30
     *
     * @return the indicator
     */
    public CohortIndicator initialTestAtANCForMale() {
        return cohortIndicator("initialTestAtANCForMale",
                map(moh731Cohorts.initialTestAtANCForMale(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Number of Male contacts tested at L&D
     * Initial test at L&D male HV02-30
     *
     * @return the indicator
     */
    public CohortIndicator initialTestAtDeliveryForMale() {
        return cohortIndicator("initialTestAtDeliveryForMale",
                map(moh731Cohorts.initialTestAtDeliveryForMale(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Male contacts tested at PNC
     * Initial test at PNC Male HV02-31
     *
     * @return the indicator
     */
    public CohortIndicator initialTestAtPNCForMale() {
        return cohortIndicator("initialTestAtPNCForMale",
                map(moh731Cohorts.initialTestAtPNCForMale(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Male contacts with known HIV status
     * Total known status male HV02-32
     *
     * @return the indicator
     *
    public CohortIndicator totalKnownHIVStatusMale() {
        return cohortIndicator("totalKnownHIVStatusMale",
                map(moh731Cohorts.totalKnownHIVStatusMale(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of 1st ANC KP Adolescents
     * 1st ANC KP Adolescents HV02-33
     *
     * @return the indicator
     */
    public CohortIndicator firstANCKPAdolescents() {
        return cohortIndicator("firstANCKPAdolescents",
                map(moh731Cohorts.firstANCKPAdolescents(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Adolescents with HIV Positive results
     * Positive result adolescents_Total HV02-34
     *
     * @return the indicator
     */
    public CohortIndicator adolescentsHIVPositive() {
        return cohortIndicator("adolescentsHIVPositive",
                map(moh731Cohorts.adolescentsHIVPositive(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Adolescents started on HAART
     * Started HAART adolescents_Total HV02-35
     *
     * @return the indicator
     */
    public CohortIndicator adolescentsStartedOnHAART() {
        return cohortIndicator("adolescentsStartedOnHAART",
                map(moh731Cohorts.adolescentsStartedOnHAART(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of known exposed infants at penta 1
     * Known exposure at penta 1 HV02-36
     *
     * @return the indicator
     */
    public CohortIndicator knownExposureAtPenta1() {
        return cohortIndicator("knownExposureAtPenta1",
                map(moh731Cohorts.knownExposureAtPenta1(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of infants due for penta 1
     * Total due for penta 1 HV02-37
     *
     * @return the indicator
     */
    public CohortIndicator totalDueForPenta1() {
        return cohortIndicator("totalDueForPenta1",
                map(moh731Cohorts.totalDueForPenta1(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Infant ARV Prophylaxis at ANC
     * Infant ARV Prophylaxis ANC HV02-38
     *
     * @return the indicator
     */
    public CohortIndicator infantArvProphylaxisANC() {
        return cohortIndicator("infantArvProphylaxisANC",
                map(moh731Cohorts.infantArvProphylaxisANC(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Infant ARV Prophylaxis during Labour and Delivery
     * Infant ARV prophylaxis L&D HV02-39
     *
     * @return the indicator
     */
    public CohortIndicator infantArvProphylaxisLabourAndDelivery() {
        return cohortIndicator("infantArvProphylaxisLabourAndDelivery",
                map(moh731Cohorts.infantArvProphylaxisLabourAndDelivery(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Infant ARV Prophylaxis PNC < 8 weeks
     * Infant ARV Prophylaxis <8 weeks PNC HV02-40
     *
     * @return the indicator
     */
    public CohortIndicator infantArvProphylaxisPNCLessThan8Weeks() {
        return cohortIndicator("infantArvProphylaxisPNCLessThan8Weeks",
                map(moh731Cohorts.infantArvProphylaxisPNCLessThan8Weeks(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Total Infant ARV Prophylaxis
     * Total ARV Prophylaxis total HV02-41
     *
     * @return the indicator
     *
    public CohortIndicator totalARVProphylaxis() {
        return cohortIndicator("totalARVProphylaxis",
                map(moh731Cohorts.totalARVProphylaxis(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Total HEI DDS/CTX Start less than 2 months
     * HEI CTX/DDS Start <2 months HV02-42
     *
     * @return the indicator
     */
    public CohortIndicator heiDDSCTXStartLessThan2Months() {
        return cohortIndicator("heiDDSCTXStartLessThan2Months",
                map(moh731Cohorts.heiDDSCTXStartLessThan2Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Total Initial PCR < 8 Weeks
     * Initial PCR <8 Weeks HV02-43
     *
     * @return the indicator
     */
    public CohortIndicator initialPCRLessThan8Weeks() {
        return cohortIndicator("initialPCRLessThan8Weeks",
                map(moh731Cohorts.initialPCRLessThan8Weeks(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Total Initial PCR > 8 Weeks to 12 months
     * Initial PCR >8 Weeks - 12 months HV02-44
     *
     * @return the indicator
     */
    public CohortIndicator initialPCROver8WeeksTo12Months() {
        return cohortIndicator("initialPCROver8WeeksTo12Months",
                map(moh731Cohorts.initialPCROver8WeeksTo12Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Total Initial PCR <12 months
     * Initial PCR Test <12 months Total HV02-45
     *
     * @return the indicator
     */
    public CohortIndicator totalInitialPCRTestLessThan12Months() {
        return cohortIndicator("totalInitialPCRTestLessThan12Months",
                map(moh731Cohorts.totalInitialPCRTestLessThan12Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Total Infected in 24 months
     * Infected 24 months HV02-46
     *
     * @return the indicator
     */
    public CohortIndicator infectedIn24Months() {
        return cohortIndicator("totalInfected24Months",
                map(moh731Cohorts.totalInfected24Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Total uninfected in 24 months
     * Uninfected 24 months HV02-47
     *
     * @return the indicator
     */
    public CohortIndicator uninfectedIn24Months() {
        return cohortIndicator("totalUninfectedIn24Months",
                map(moh731Cohorts.totalUninfectedIn24Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Total unknown outcomes in 24 months
     * Unknown Outcome 24 months HV02-48
     *
     * @return the indicator
     */
    public CohortIndicator unknownOutcomesIn24Months() {
        return cohortIndicator("unknownOutcomesIn24Months",
                map(moh731Cohorts.unknownOutcomesIn24Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Net cohort HEI in 24 months
     * Net cohort HEI 24 months HV02-49
     *
     * @return the indicator
     */
    public CohortIndicator netCohortHeiIn24Months() {
        return cohortIndicator("netCohortHeiIn24Months",
                map(moh731Cohorts.netCohortHeiIn24Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Mother-baby pairs in 24 months
     * Mother-baby pairs 24 months HV02-50
     *
     * @return the indicator
     */
    public CohortIndicator motherBabyPairsIn24Months() {
        return cohortIndicator("motherBabyPairsIn24Months",
                map(moh731Cohorts.motherBabyPairsIn24Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Pair Net cohorts at 24 months
     * Pair net cohort 24 months HV02-51
     *
     * @return the indicator
     */
    public CohortIndicator pairNetCohortIn24Months() {
        return cohortIndicator("pairNetCohortIn24Months",
                map(moh731Cohorts.pairNetCohortIn24Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Exclusive Breastfeeding at 6 months
     * EBF(at 6 months) HV02-52
     *
     * @return the indicator
     */
    public CohortIndicator exclusiveBFAt6Months() {
        return cohortIndicator("exclusiveBFAt6Months",
                map(moh731Cohorts.exclusiveBFAt6Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Exclusive Replacement feeding at 6 months
     * ERF(at 6 months) HV02-53
     *
     * @return the indicator
     */
    public CohortIndicator exclusiveRFAt6Months() {
        return cohortIndicator("exclusiveRFAt6Months",
                map(moh731Cohorts.exclusiveRFAt6Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Mixed feeding at 6 months
     * MF(at 6 months) HV02-54
     *
     * @return the indicator
     */
    public CohortIndicator mixedFeedingAt6Months() {
        return cohortIndicator("mixedFeedingAt6Months",
                map(moh731Cohorts.mixedFeedingAt6Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Breastfeeding at 12 months
     * BF(12 months) HV02-55
     *
     * @return the indicator
     */
    public CohortIndicator breastFeedingAt12Months() {
        return cohortIndicator("breastFeedingAt12Months",
                map(moh731Cohorts.breastFeedingAt12Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Not Breastfeeding in 12 months
     * Not BF (12 Months) HV02-56
     *
     * @return the indicator
     */
    public CohortIndicator notBreastFeedingAt12Months() {
        return cohortIndicator("notBreastFeedingAt12Months",
                map(moh731Cohorts.notBreastFeedingAt12Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Breastfeeding at 18 months
     * BF (18 Months) HV02-57
     *
     * @return the indicator
     */
    public CohortIndicator breastFeedingAt18Months() {
        return cohortIndicator("breastFeedingAt18Months",
                map(moh731Cohorts.breastFeedingAt18Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Not Breastfeeding at 18 months
     * Not BF(18 Months) HV02-58
     *
     * @return the indicator
     */
    public CohortIndicator notBreastFeedingAt18Months() {
        return cohortIndicator("notBreastFeedingAt18Months",
                map(moh731Cohorts.notBreastFeedingAt18Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Pre-art
     * covers indicators HV03-013 to  HV03-015
     *
     * @return indicator
     */
    public CohortIndicator preArtCohort() {
        return cohortIndicator("pre-art cohort", map(moh731Cohorts.preArtCohort(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * patients in HIV program assessed for nutrition
     *
     * @return indicator
     */
    public CohortIndicator assessedForNutritionInHIV() {
        return cohortIndicator("Started on IPT", map(moh731Cohorts.assessedForNutritionInHIV(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * patients in HIV program who are malnourished
     *
     * @return indicator
     */
    public CohortIndicator malnourishedInHIV() {
        return cohortIndicator("Started on IPT", map(moh731Cohorts.malnourishedInHIV(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * patients started on ipt
     *
     * @return indicator
     */
    public CohortIndicator startedOnIPT() {
        return cohortIndicator("Started on IPT", map(moh731Cohorts.startedOnIPT(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * patients who started ipt 12 months ago and have completed
     */
    public CohortIndicator ipt12MonthsCohort() {
        return cohortIndicator("IPT 12 months cohort", map(moh731Cohorts.completedIPT12Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * 3.10 HIV in TB clinic
     */
    // tb new cases
    public CohortIndicator tbEnrollment() {
        return cohortIndicator("New TB cases", map(moh731Cohorts.tbEnrollment(), "startDate=${startDate},endDate=${endDate}"));
    }

    // New TB cases known positive
    public CohortIndicator tbNewKnownPositive() {
        return cohortIndicator("New TB cases with KP status", map(moh731Cohorts.tbNewKnownPositive(), "startDate=${startDate},endDate=${endDate}"));
    }

    // New TB cases tested positive
    public CohortIndicator tbTestedForHIV() {
        return cohortIndicator("New TB cases tested for HIV", map(moh731Cohorts.tbTestedForHIV(), "startDate=${startDate},endDate=${endDate}"));
    }

    // new TB cases tested HIV positive
    public CohortIndicator tbNewTestedHIVPositive() {
        return cohortIndicator("New TB cases tested HIV Positive", map(moh731Cohorts.tbNewTestedHIVPositive(), "startDate=${startDate},endDate=${endDate}"));
    }

    // new TB cases already on HAART
    public CohortIndicator tbNewAlreadyOnHAART() {
        return cohortIndicator("New TB cases already on HAART", map(moh731Cohorts.tbNewAlreadyOnHAART(), "startDate=${startDate},endDate=${endDate}"));
    }

    // new TB cases started on HAART
    public CohortIndicator tbNewStartingHAART() {
        return cohortIndicator("New TB cases starting on HAART", map(moh731Cohorts.tbNewStartingHAART(), "startDate=${startDate},endDate=${endDate}"));
    }

    // total TB on HAART
    public CohortIndicator tbTotalOnHAART() {
        return cohortIndicator("Total TB cases on HAART", map(moh731Cohorts.tbTotalOnHAART(), "startDate=${startDate},endDate=${endDate}"));
    }

    // screened for cacx
    public CohortIndicator screenedforCaCx() {
        return cohortIndicator("Screened for Cacx", map(moh731Cohorts.screenedForCaCx(), "startDate=${startDate},endDate=${endDate}"));
    }




    /**
     * 1.1 - 1.4 HIV Care and treatment
     * <p>
     * Number of patients currently in care (includes transfers)
     *
     * @return the indicator
     */
    public CohortIndicator currentlyInCare() {
        return cohortIndicator("Currently in care (includes transfers)", ReportUtils.map(moh731Cohorts.currentlyInCare(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator newHivEnrollment() {
        return cohortIndicator("New Enrollment in care (excludes transfers)", ReportUtils.map(moh731Cohorts.hivEnrollment(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number of patients who are currently on ART
     *
     * @return the indicator
     */
    public CohortIndicator currentlyOnArt() {
        return cohortIndicator("Currently on ART", ReportUtils.map(moh731Cohorts.currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients who are ART revisits
     *
     * @return the indicator
     */
    public CohortIndicator revisitsArt() {
        return cohortIndicator("Revisits ART", ReportUtils.map(moh731Cohorts.revisitsArt(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients who were started on Art
     *
     * @return the indicator
     */
    public CohortIndicator startedOnArt() {
        return cohortIndicator("Started on ART", ReportUtils.map(moh731Cohorts.startedOnART(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Cumulative number of patients on ART
     *
     * @return the indicator
     */
    public CohortIndicator cumulativeOnArt() {
        return cohortIndicator("Cumulative ever on ART", ReportUtils.map(moh731Cohorts.cummulativeOnArt(), "endDate=${endDate}"));
    }

    /**
     * Number of patients in the ART 12 month cohort
     *
     * @return the indicator
     */
    public CohortIndicator art12MonthNetCohort() {
        return cohortIndicator("ART 12 Month Net Cohort", ReportUtils.map(moh731Cohorts.art12MonthNetCohort(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients in the 12 month cohort who are on their original first-line regimen
     *
     * @return the indicator
     */
    public CohortIndicator onOriginalFirstLineAt12Months() {
        return cohortIndicator("On original 1st line at 12 months", ReportUtils.map(moh731Cohorts.onOriginalFirstLineAt12Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients in the 12 month cohort who are on an alternate first-line regimen
     *
     * @return the indicator
     */
    public CohortIndicator onAlternateFirstLineAt12Months() {
        return cohortIndicator("On alternate 1st line at 12 months", ReportUtils.map(moh731Cohorts.onAlternateFirstLineAt12Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients in the 12 month cohort who are on a second-line regimen
     *
     * @return the indicator
     */
    public CohortIndicator onSecondLineAt12Months() {
        return cohortIndicator("On 2nd line at 12 months", ReportUtils.map(moh731Cohorts.onSecondLineAt12Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients in the 12 month cohort who are on ART
     *
     * @return the indicator
     */
    public CohortIndicator onTherapyAt12Months() {
        return cohortIndicator("On therapy at 12 months", ReportUtils.map(moh731Cohorts.onTherapyAt12Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number of patients with suppressed vl last 12 months     *
     * @return the indicator
     */
    public CohortIndicator patientsWithSuppressedVlLast12Months() {
        return cohortIndicator("Patients with suppressed vl last 12 months", ReportUtils.map(moh731Cohorts.patientsWithSuppressedVlLast12Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number of patients with vl results last 12 months     *
     * @return the indicator
     */
    public CohortIndicator patientsWithVLResultsLast12Months() {
        return cohortIndicator("Patients with vl results last 12 months", ReportUtils.map(moh731Cohorts.patientsWithVLResultsLast12Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Cumulative number of patients screened for TB
     *
     * @return the indicator
     */
    public CohortIndicator screenedForTb() {
        return cohortIndicator("Screen for TB", ReportUtils.map(moh731Cohorts.tbScreening(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator presumedForTb() {
        return cohortIndicator("Presumed for TB", ReportUtils.map(moh731Cohorts.presumedTb(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients who were started on Art while pregnant
     *
     * @return the indicator
     */
    public CohortIndicator startedArtWhilePregnant() {
        return cohortIndicator("Started on ART Pregnant", ReportUtils.map(moh731Cohorts.startingARTPregnant(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients who were started on Art while pregnant
     *
     * @return the indicator
     */
    public CohortIndicator startedArtWhileTbPatient() {
        return cohortIndicator("Started on ART - Tb Patient", ReportUtils.map(moh731Cohorts.startingARTWhileTbPatient(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number of patients provided with condoms
     *
     * @return the indicator
     */
    public CohortIndicator condomsProvided() {
        return cohortIndicator("patients provided with condoms", map(moh731Cohorts.condomsProvided(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients provided with modern contraceptives
     *
     * @return the indicator
     */
    public CohortIndicator modernContraceptivesProvided() {
        return cohortIndicator("patients provided with modern contraceptives", map(moh731Cohorts.modernContraceptivesProvided(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of HIV care visits for females aged 18 and over
     *
     * @return the indicator
     */
    public CohortIndicator hivCareVisitsFemale18() {
        return cohortIndicator("HIV care visits for females aged 18 and over", map(moh731Cohorts.hivCareVisitsFemale18(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of scheduled HIV care visits
     *
     * @return the indicator
     */
    public CohortIndicator hivCareVisitsScheduled() {
        return cohortIndicator("Scheduled HIV care visits", map(moh731Cohorts.hivCareVisitsScheduled(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of unscheduled HIV care visits
     *
     * @return the indicator
     */
    public CohortIndicator hivCareVisitsUnscheduled() {
        return cohortIndicator("Unscheduled HIV care visits", map(moh731Cohorts.hivCareVisitsUnscheduled(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Total number of HIV care visits
     *
     * @return the indicator
     */
    public CohortIndicator hivCareVisitsTotal() {
        return cohortIndicator("HIV care visits", map(moh731Cohorts.hivCareVisitsTotal(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number of patients who are on Cotrimoxazole prophylaxis
     *
     * @return the indicator
     */
    public CohortIndicator onCotrimoxazoleProphylaxis() {
        return cohortIndicator("patients on CTX prophylaxis", map(moh731Cohorts.inHivProgramAndOnCtxProphylaxis(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV exposed infants within 2 months
     *
     * @return indicator
     */
    public CohortIndicator hivExposedInfantsWithin2Months() {
        return cohortIndicator("Hiv Exposed Infants within 2 months", map(moh731Cohorts.hivExposedInfantsWithin2Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * HIV exposed infants within 2 months and are eligible for ctx
     *
     * @return indicator
     */
    public CohortIndicator hivExposedInfantsWithin2MonthsAndEligibleForCTX() {
        return cohortIndicator("Hiv Exposed Infants within 2 months", map(moh731Cohorts.hivExposedInfantsWithin2MonthsAndEligibleForCTX(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * VMMC
     * Number circumcised  HV04-01
     *
     * @return indicator
     */
    public CohortIndicator numberCircumcised() {
        return cohortIndicator("Number circumcised", map(datimCohorts.malesCircumcised(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * VMMC
     * Number circumcised  HIV Positive HV04-08
     *
     * @return indicator
     */
    public CohortIndicator numberCircumcisedHivPositive() {
        return cohortIndicator("Number circumcised Hiv Positive", map(datimCohorts.malesCircumcisedTestedHIVPositive(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * VMMC
     * Number circumcised Hiv Negative HV04-09
     *
     * @return indicator
     */
    public CohortIndicator numberCircumcisedHivNegative() {
        return cohortIndicator("Number circumcised Hiv Negative", map(datimCohorts.malesCircumcisedTestedHIVNegative(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * VMMC
     * Number circumcised Hiv Unknown HV04-10
     *
     * @return indicator
     */
    public CohortIndicator numberCircumcisedHivUnknown() {
        return cohortIndicator("Number circumcised Hiv Unknown", map(datimCohorts.malesCircumcisedIndeterminateHIVResult(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * VMMC
     * Number circumcised Surgically HV04-11
     *
     * @return indicator
     */
    public CohortIndicator numberCircumcisedSurgically() {
        return cohortIndicator("Number circumcised surgically", map(datimCohorts.vmmcSurgical(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * VMMC
     * Number circumcised using device method HV04-12
     *
     * @return indicator
     */
    public CohortIndicator numberCircumcisedUsingDevice() {
        return cohortIndicator("Number circumcised using device method", map(datimCohorts.vmmcDevice(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * VMMC
     * Number of males circumcised with moderate Adverse Events during procedure
     * HV04-13
     *
     * @return indicator
     */
    public CohortIndicator circumcisedWithModerateAEDuringProcedure() {
        return cohortIndicator("Number circumcised with moderate Adverse Events during procedutre", map(moh731Cohorts.circumcisedWithModerateAEDuringProcedure(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * VMMC
     * Number of males circumcised with severe Adverse Events during procedure
     * HV04-14
     *
     * @return indicator
     */
    public CohortIndicator circumcisedWithSevereAEDuringProcedure() {
        return cohortIndicator("Number circumcised with sever Adverse Events during procedutre", map(moh731Cohorts.circumcisedWithSevereAEDuringProcedure(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * VMMC
     * Number of males circumcised with moderate Adverse Events post procedure
     * HV04-15
     *
     * @return indicator
     */
    public CohortIndicator circumcisedWithModerateAEPostProcedure() {
        return cohortIndicator("Number circumcised with moderate Adverse Events post procedutre", map(moh731Cohorts.circumcisedWithModerateAEPostProcedure(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * VMMC
     * Number of males circumcised with severe Adverse Events post procedure
     * HV04-16
     *
     * @return indicator
     */
    public CohortIndicator circumcisedWithSevereAEPostProcedure() {
        return cohortIndicator("Number circumcised with severe Adverse Events post procedutre", map(moh731Cohorts.circumcisedWithSevereAEPostProcedure(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * VMMC
     * Number of males circumcised followed up within 14 days of VMMC procedure
     * HV04-17
     *
     * @return indicator
     */
    public CohortIndicator followedUpWithin14daysOfVMMCProcedure() {
        return cohortIndicator("Number circumcised have a followup visit withn 14 days post procedutre", map(datimCohorts.followedUpWithin14daysOfVMMCProcedure(), "startDate=${startDate},endDate=${endDate}"));
    }
}


