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
//Updates
    /**
     * Number of patients who did First ANC visit during that period {@link org.openmrs.module.kenyaemr.PregnancyStage}
     *First ANC visit  HV02-01
     * @return the indicator
     */
    public CohortIndicator firstANCVisitMchmsAntenatal() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.firstANCVisitMchmsAntenatal(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Number of patients who tested for HIV in MCHMS during the DELIVERY {@link org.openmrs.module.kenyaemr.PregnancyStage}
     *Delivery for HIV Positive mothers HV02-02
     * @return the indicator
     */
    public CohortIndicator deliveryFromHIVPositiveMothers() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.deliveryFromHIVPositiveMothers(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who are known positive at First ANC {@link org.openmrs.module.kenyaemr.PregnancyStage}
     *Known Positive at 1st ANC HV02-03
     * @return the indicator
     */
    public CohortIndicator knownPositiveAtFirstANC() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.knownPositiveAtFirstANC(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Number of patients who tested for HIV in MCHMS during the ANTENATAL {@link org.openmrs.module.kenyaemr.PregnancyStage}
     *Initial test at ANC  HV02-04
     * @return the indicator
     */
    public CohortIndicator initialHIVTestInMchmsAntenatal() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.initialHIVTestInMchmsAntenatal(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested for HIV in MCHMS during the DELIVERY {@link org.openmrs.module.kenyaemr.PregnancyStage}
     *Initial test at labour and delivery HV02-05
     * @return the indicator
     */
    public CohortIndicator testedForHivInMchmsDelivery() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.testedForHivInMchmsDelivery(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested for HIV in MCHMS during the POSTNATAL {@link org.openmrs.module.kenyaemr.PregnancyStage}
     * Initial Test at PNC<=6 weeks HV02-06
     * @return the indicator
     */
    public CohortIndicator initialTestAtPNCUpto6Weeks() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.initialTestAtPNCUpto6Weeks(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Number of patients who tested for HIV in MCHMS during any {@link org.openmrs.module.kenyaemr.PregnancyStage} after enrollment
     * Known HIV Status total HV02-07
     * @return the indicator
     */
    public CohortIndicator testedForHivInMchms() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.testedForHivInMchmsTotal(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Number of patients who tested for HIV in MCHMS during any {@link org.openmrs.module.kenyaemr.PregnancyStage} after enrollment
     * Retesting_PNC <=6 weeks HV02-08
     * @return the indicator
     */
    public CohortIndicator pncRetestUpto6Weeks() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.pncRetestUpto6Weeks(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Number of patients who tested for HIV in MCHMS during any {@link org.openmrs.module.kenyaemr.PregnancyStage} after enrollment
     * Tested_PNC>6 Weeks to 6 months HV02-09
     * @return the indicator
     */
    public CohortIndicator pncTestBtwn6WeeksAnd6Months() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.pncTestBtwn6WeeksAnd6Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Number of patients who tested HIV +ve before MCHMS
     * Known HIV Positive at 1st ANC HV02-10
     * @return the indicator
     */
    public CohortIndicator knownHivPositiveAtFirstANC() {

        return cohortIndicator(null,
                map(pmtctCohortLibrary.knownPositiveAtFirstANC(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Number of patients who tested HIV Positive in MCHMS during the ANTENATAL {@link org.openmrs.module.kenyaemr.PregnancyStage}
     * Positive results ANC HV02-11
     * @return the indicator
     */
    public CohortIndicator testedHivPositiveInMchmsAntenatal() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.testedHivPositiveInMchmsAntenatal(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested for HIV in MCHMS during the DELIVERY {@link org.openmrs.module.kenyaemr.PregnancyStage}
     * Delivery for HIV Positive mothers HV02-12
     * @return the indicator
     */
    public CohortIndicator testedHivPositiveInMchmsDelivery() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.positiveHIVResultsAtLabourAndDelivery(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested for HIV in MCHMS during the DELIVERY {@link org.openmrs.module.kenyaemr.PregnancyStage}
     *Positive Results PNC <=6 weeks HV02-13
     * @return the indicator
     */
    public CohortIndicator testedHivPositiveInANCWithin6Weeks() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.testedHivPositiveInPNCWithin6Weeks(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of patients who tested HIV Positive in MCHMS during any {@link org.openmrs.module.kenyaemr.PregnancyStage} after enrollment
     * Mothers positive total HV02-14
     * @return the indicator
     */
    public CohortIndicator totalHivPositiveInMchms() {
        return cohortIndicator(null,
                map(pmtctCohortLibrary.totalHivPositiveMothersInMchms(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Number of infant patients who took pcr test aged between 7 weeks and 6 months and confirmed Positive
     * Positive PNC >6 Weeks to 6 months HV02-15
     * @return the indicator
     */
    public CohortIndicator pncHIVPositiveBetween7weeksAnd6Months() {
        return cohortIndicator("PNC HIV Positive between 7 weeks and 6 months",
                map(pmtctCohortLibrary.totalHivPositivePNC6WeeksTo6monthsInMchms(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Number of Patients on HAART At First ANC
     * On HAART At First ANC  HV02-16
     * @return the indicator
     */
    public CohortIndicator onHAARTAtFirstANC() {
        return cohortIndicator("On HAART At first ANC",
                map(pmtctCohortLibrary.totalOnHAARTAtFirstANC(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Patients started HAART At ANC
     * Start HAART ANC HV02-17
     * @return the indicator
     */
    public CohortIndicator startHAARTANC() {
        return cohortIndicator("Started HAART At ANC",
                map(pmtctCohortLibrary.startedHAARTAtANC(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Patients started HAART during Labour and delivery
     * Start HAART L&D HV02-18
     * @return the indicator
     */
    public CohortIndicator startedHAARTLabourAndDelivery() {
        return cohortIndicator("Started HAART during labour and delivery",
                map(pmtctCohortLibrary.totalStartedHAARTAtLabourAndDelivery(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Patients started HAART during first 6 weeks of PNC
     * Start HAART PNC <=6 weeks HV02-19
     * @return the indicator
     */
    public CohortIndicator startedHAARTPNCUpto6Weeks() {
        return cohortIndicator("Started HAART within 6 weeks of PNC",
                map(pmtctCohortLibrary.totalStartedHAARTAtPNCUpto6Weeks(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Patients started on maternal HAART
     * Total maternal HAART HV02-20
     * @return the indicator
     */
    public CohortIndicator totalMaternalHAART() {
        return cohortIndicator("Total maternal HAART",
                map(pmtctCohortLibrary.totalMaternalHAART(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Patients started HAART between 7 weeks and 6 months
     * Start HAART_PNC>6 weeks to 6 months HV02-21
     * * @return the indicator
     */
    public CohortIndicator onHAARTFrom7WeeksTo6Months() {
        return cohortIndicator("Started HAART between 7 weeks and 6 months",
                map(pmtctCohortLibrary.totalStartedOnHAARTBtw7WeeksAnd6Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Patients started HAART during first 6 weeks of PNC
     * On maternal HAART_12 Months HV02-22
     * @return the indicator
     */
    public CohortIndicator onHAARTUpto12Months() {
        return cohortIndicator("On Maternal HAART upto 12 months",
                map(pmtctCohortLibrary.netCohortAt12Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Net cohort at 12 months
     * Net cohort_12 months HV02-23
     * @return the indicator
     */
    public CohortIndicator netCohortAt12Months() {
        return cohortIndicator("Net cohort at 12 months",
                map(pmtctCohortLibrary.netCohortAt12Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Number of Patients screened for syphilis at 1st ANC
     * Syphilis screened 1st ANC HV02-24
     * @return the indicator
     */
    public CohortIndicator syphilisScreenedAt1stANC() {
        return cohortIndicator("Screened for Syphilis at 1st ANC",
                map(pmtctCohortLibrary.syphilisScreenedAt1stANC(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Patients screened positive for syphilis
     * Syphilis screened positive HV02-25
     * @return the indicator
     */
    public CohortIndicator syphilisScreenedPositive() {
        return cohortIndicator("Syphilis Screened Positive",
                map(pmtctCohortLibrary.syphilisScreenedPositive(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Patients Treated for syphilis
     * Syphilis treated HV02-26
     * @return the indicator
     */
    public CohortIndicator syphilisTreated() {
        return cohortIndicator("Treated for syphilis",
                map(pmtctCohortLibrary.treatedForSyphilis(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of HIV+ Patients on modern FP at 6 weeks
     * HIV+ On modern FP at 6 weeks HV02-27
     * @return the indicator
     */
    public CohortIndicator HIVPositiveOnModernFPUpto6Weeks() {
        return cohortIndicator("HIVPositiveOnModernFPUpto6Weeks",
                map(pmtctCohortLibrary.HIVPositiveOnModernFPUpto6Weeks(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of HIV+ Patients with PNC visits at 6 weeks
     * HIV+ PNC Visits at 6 weeks HV02-28
     * @return the indicator
     */
    public CohortIndicator HIVPositivePNCVisitsAt6Weeks() {
        return cohortIndicator("HIVPositivePNCVisitsAt6Weeks",
                map(pmtctCohortLibrary.HIVPositivePNCVisitsAt6Weeks(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Known HIV+ First contacts
     * Known positive status 1st contact HV02-29
     * @return the indicator
     */
    public CohortIndicator knownHIVPositive1stContact() {
        return cohortIndicator("knownHIVPositive1stContact",
                map(pmtctCohortLibrary.knownHIVPositive1stContact(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Number of Male contacts tested at ANC
     * Initial test at ANC male HV02-30
     * @return the indicator
     */
    public CohortIndicator initialTestAtANCForMale() {
        return cohortIndicator("initialTestAtANCForMale",
                map(pmtctCohortLibrary.initialTestAtANCForMale(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Male contacts tested at PNC
     * Initial test at PNC Male HV02-31
     * @return the indicator
     */
    public CohortIndicator initialTestAtPNCForMale() {
        return cohortIndicator("initialTestAtPNCForMale",
                map(pmtctCohortLibrary.initialTestAtPNCForMale(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Male contacts with known HIV status
     * Total known status male HV02-32
     * @return the indicator
     */
    public CohortIndicator totalKnownHIVStatusMale() {
        return cohortIndicator("totalKnownHIVStatusMale",
                map(pmtctCohortLibrary.totalKnownHIVStatusMale(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of 1st ANC KP Adolescents
     * 1st ANC KP Adolescents HV02-33
     * @return the indicator
     */
    public CohortIndicator firstANCKPAdolescents() {
        return cohortIndicator("firstANCKPAdolescents",
                map(pmtctCohortLibrary.firstANCKPAdolescents(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Number of Adolescents with HIV Positive results
     * Positive result adolescents_Total HV02-34
     * @return the indicator
     */
    public CohortIndicator adolescentsHIVPositive() {
        return cohortIndicator("adolescentsHIVPositive",
                map(pmtctCohortLibrary.adolescentsHIVPositive(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of Adolescents started on HAART
     * Started HAART adolescents_Total HV02-35
     * @return the indicator
     */
    public CohortIndicator adolescentsStartedOnHAART() {
        return cohortIndicator("adolescentsStartedOnHAART",
                map(pmtctCohortLibrary.adolescentsStartedOnHAART(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Number of known exposed infants at penta 1
     * Known exposure at penta 1 HV02-36
     * @return the indicator
     */
    public CohortIndicator knownExposureAtPenta1() {
        return cohortIndicator("knownExposureAtPenta1",
                map(pmtctCohortLibrary.knownExposureAtPenta1(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Number of infants due for penta 1
     * Total due for penta 1 HV02-37
     * @return the indicator
     */
    public CohortIndicator totalDueForPenta1() {
        return cohortIndicator("totalDueForPenta1",
                map(pmtctCohortLibrary.totalDueForPenta1(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Infant ARV Prophylaxis at ANC
     * Infant ARV Prophylaxis ANC HV02-38
     * @return the indicator
     */
    public CohortIndicator infantArvProphylaxisANC() {
        return cohortIndicator("infantArvProphylaxisANC",
                map(pmtctCohortLibrary.infantArvProphylaxisANC(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Infant ARV Prophylaxis during Labour and Delivery
     * Infant ARV prophylaxis L&D HV02-39
     * @return the indicator
     */
    public CohortIndicator infantArvProphylaxisLabourAndDelivery() {
        return cohortIndicator("infantArvProphylaxisLabourAndDelivery",
                map(pmtctCohortLibrary.infantArvProphylaxisLabourAndDelivery(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Infant ARV Prophylaxis PNC < 8 weeks
     * Infant ARV Prophylaxis <8 weeks PNC HV02-40
     * @return the indicator
     */
    public CohortIndicator infantArvProphylaxisPNCLessThan8Weeks() {
        return cohortIndicator("infantArvProphylaxisPNCLessThan8Weeks",
                map(pmtctCohortLibrary.infantArvProphylaxisPNCLessThan8Weeks(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Total Infant ARV Prophylaxis
     * Total ARV Prophylaxis total HV02-41
     * @return the indicator
     */
    public CohortIndicator totalARVProphylaxis() {
        return cohortIndicator("totalARVProphylaxis",
                map(pmtctCohortLibrary.totalARVProphylaxis(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Total HEI DDS/CTX Start less than 2 months
     * HEI CTX/DDS Start <2 months HV02-42
     * @return the indicator
     */
    public CohortIndicator heiDDSCTXStartLessThan2Months() {
        return cohortIndicator("heiDDSCTXStartLessThan2Months",
                map(pmtctCohortLibrary.heiDDSCTXStartLessThan2Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Total Initial PCR < 8 Weeks
     * Initial PCR <8 Weeks HV02-43
     * @return the indicator
     */
    public CohortIndicator initialPCRLessThan8Weeks() {
        return cohortIndicator("initialPCRLessThan8Weeks",
                map(pmtctCohortLibrary.initialPCRLessThan8Weeks(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Total Initial PCR > 8 Weeks to 12 months
     * Initial PCR >8 Weeks - 12 months HV02-44
     * @return the indicator
     */
    public CohortIndicator initialPCROver8WeeksTo12Months() {
        return cohortIndicator("initialPCROver8WeeksTo12Months",
                map(pmtctCohortLibrary.initialPCROver8WeeksTo12Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Total Initial PCR <12 months
     * Initial PCR Test <12 months Total HV02-45
     * @return the indicator
     */
    public CohortIndicator totalInitialPCRTestLessThan12Months() {
        return cohortIndicator("totalInitialPCRTestLessThan12Months",
                map(pmtctCohortLibrary.totalInitialPCRTestLessThan12Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Total Infected in 24 months
     * Infected 24 months HV02-46
     * @return the indicator
     */
    public CohortIndicator infectedIn24Months() {
        return cohortIndicator("totalInfected24Months",
                map(pmtctCohortLibrary.totalInfected24Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Total uninfected in 24 months
     * Uninfected 24 months HV02-47
     * @return the indicator
     */
    public CohortIndicator uninfectedIn24Months() {
        return cohortIndicator("totalUninfectedIn24Months",
                map(pmtctCohortLibrary.totalUninfectedIn24Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Total unknown outcomes in 24 months
     * Unknown Outcome 24 months HV02-48
     * @return the indicator
     */
    public CohortIndicator unknownOutcomesIn24Months() {
        return cohortIndicator("unknownOutcomesIn24Months",
                map(pmtctCohortLibrary.unknownOutcomesIn24Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Net cohort HEI in 24 months
     * Net cohort HEI 24 months HV02-49
     * @return the indicator
     */
    public CohortIndicator netCohortHeiIn24Months() {
        return cohortIndicator("netCohortHeiIn24Months",
                map(pmtctCohortLibrary.netCohortHeiIn24Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Mother-baby pairs in 24 months
     * Mother-baby pairs 24 months HV02-50
     * @return the indicator
     */
    public CohortIndicator motherBabyPairsIn24Months() {
        return cohortIndicator("motherBabyPairsIn24Months",
                map(pmtctCohortLibrary.motherBabyPairsIn24Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Pair Net cohorts at 24 months
     * Pair net cohort 24 months HV02-51
     * @return the indicator
     */
    public CohortIndicator pairNetCohortIn24Months() {
        return cohortIndicator("pairNetCohortIn24Months",
                map(pmtctCohortLibrary.pairNetCohortIn24Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Exclusive Breastfeeding at 6 months
     * EBF(at 6 months) HV02-52
     * @return the indicator
     */
    public CohortIndicator exclusiveBFAt6Months() {
        return cohortIndicator("exclusiveBFAt6Months",
                map(pmtctCohortLibrary.exclusiveBFAt6Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /**
     * Exclusive Replacement feeding at 6 months
     * ERF(at 6 months) HV02-53
     * @return the indicator
     */
    public CohortIndicator exclusiveRFAt6Months() {
        return cohortIndicator("exclusiveRFAt6Months",
                map(pmtctCohortLibrary.exclusiveRFAt6Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Mixed feeding at 6 months
     * MF(at 6 months) HV02-54
     * @return the indicator
     */
    public CohortIndicator mixedFeedingAt6Months() {
        return cohortIndicator("mixedFeedingAt6Months",
                map(pmtctCohortLibrary.mixedFeedingAt6Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Breastfeeding at 12 months
     * BF(12 months) HV02-55
     * @return the indicator
     */
    public CohortIndicator breastFeedingAt12Months() {
        return cohortIndicator("breastFeedingAt12Months",
                map(pmtctCohortLibrary.breastFeedingAt12Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Not Breastfeeding in 12 months
     * Not BF (12 Months) HV02-56
     * @return the indicator
     */
    public CohortIndicator notBreastFeedingAt12Months() {
        return cohortIndicator("notBreastFeedingAt12Months",
                map(pmtctCohortLibrary.notBreastFeedingAt12Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Breastfeeding at 18 months
     * BF (18 Months) HV02-57
     * @return the indicator
     */
    public CohortIndicator breastFeedingAt18Months() {
        return cohortIndicator("breastFeedingAt18Months",
                map(pmtctCohortLibrary.breastFeedingAt18Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }
    /**
     * Not Breastfeeding at 18 months
     * Not BF(18 Months) HV02-58
     * @return the indicator
     */
    public CohortIndicator notBreastFeedingAt18Months() {
        return cohortIndicator("notBreastFeedingAt18Months",
                map(pmtctCohortLibrary.notBreastFeedingAt18Months(), "startDate=${startDate},endDate=${endDate}")
        );
    }



//End updates



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
//    public CohortIndicator testedForHivInMchms() {
//        return cohortIndicator(null,
//                map(pmtctCohortLibrary.testedForHivInMchmsTotal(), "startDate=${startDate},endDate=${endDate}")
//        );
//    }

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
//    public CohortIndicator testedForHivInMchmsDelivery() {
//        return cohortIndicator(null,
//                map(pmtctCohortLibrary.testedForHivInMchmsDelivery(), "startDate=${startDate},endDate=${endDate}")
//        );
//    }

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

//    /**
//     * Number of patients who tested HIV Positive in MCHMS during the ANTENATAL {@link org.openmrs.module.kenyaemr.PregnancyStage}
//     *
//     * @return the indicator
//     */
//    public CohortIndicator testedHivPositiveInMchmsAntenatal() {
//        return cohortIndicator(null,
//                map(pmtctCohortLibrary.testedHivPositiveInMchmsAntenatal(), "startDate=${startDate},endDate=${endDate}")
//        );
//    }

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
