/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.kenyaemr.reporting.library.shared.hiv;

import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of Quality Improvement indicators for HIV care patients in MCHMS and MCHCS
 */
@Component
public class QiEmtctIndicatorLibrary {

	@Autowired
	private QiEmtctCohortLibrary qiEmtctCohortLibrary;

	@Autowired
	private QiCohortLibrary qiCohortLibrary;

	/**
	 *% of pregnant women attending at least four ANC visits
	 * @return org.openmrs.module.reporting.indicator.CohortIndicator
	 */
	public CohortIndicator patientsAttendingAtLeast4AncVisitsAndPregnant() {
		return cohortIndicator("4th ANC visit (FANC) Service coverage ",
				map(qiEmtctCohortLibrary.patientsAttendingAtLeastAncVisitsAndPregnant(4), "onDate=${endDate}"),
				map(qiEmtctCohortLibrary.patientsAttendingAtLeastAncVisitsAndPregnant(1), "onDate=${endDate}")
		);
	}

	/**
	 * % of skilled deliveries within the facility catchment population
	 * @return CohortIndicator
	 */
	public CohortIndicator skilledDeliveriesWithinFacility() {
		return cohortIndicator("Skilled deliveries within the facility",
				map(qiEmtctCohortLibrary.womenDeliveredInFacility(), "onOrBefore=${endDate}"),
				map(qiEmtctCohortLibrary.numberOfExpectedDeliveriesInTheFacilityCatchmentPopulationDuringTheReviewPeriod(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}")
		);
	}

	/**
	 * % of pregnant women whose partners have been tested for HIV or who are known positive.
	 * @return CohortIndicator
	 */
	public CohortIndicator numberOfNewAnClients() {
		return cohortIndicator("Partner testing - (Service coverage)",
				map(qiEmtctCohortLibrary.pregnantWomenWhosePartnersHaveBeenTestedForHivOrWhoAreKnownPositive(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}"),
				map(qiEmtctCohortLibrary.numberOfNewAnClients(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}")
		);
	}

	/**
	 * % of Mother-newborn pairs reviewed  by health care provider 7-14 days of birth
	 * @return CohortIndicator
	 */
	public CohortIndicator mothersNewBornPairReview() {
		return cohortIndicator("Mother-baby pair postnatal follow-up",
				map(qiEmtctCohortLibrary.mothersNewBornPairReview(), "onDate=${endDate}"),
				map(qiEmtctCohortLibrary.numberOfExpectedDeliveriesInTheFacilityCatchmentPopulationDuringTheReviewPeriod(), "onOrBefore=${endDate}")
		);
	}

	/**
	 *% of HIV-infected pregnant women receiving  HAART .
	 * @return CohortIndicator
	 */
	public CohortIndicator HIVInfectedPregnantWomenReceivingHAART() {
		return cohortIndicator("ART Provision (Service coverage))",
				map(qiEmtctCohortLibrary.hivInfectedPregnantWomenReceivingHaart(), "onOrBefore=${endDate}"),
				map(qiEmtctCohortLibrary.hIVInfectedPregnantWomenWhoHadAtLeastOneAncVisitDuring6MonthsReviewPeriod(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}")
		);
	}

	/**
	 *% of HIV-infected pregnant or lactating women on ART for at least 6 months who had a VL assessment done
	 * @return CohortIndicator
	 */
	public CohortIndicator hIVInfectedPregnantOrLactatingWomenOnArtForAtLeast6MonthsWhoHadAvlAssessmentDone() {
		return cohortIndicator("HIV Monitoring Viral Load (Service coverage))",
				map(qiEmtctCohortLibrary.hivInfectedOrLactatingWomenOnARTForAtLeast6MonthsWithVLResultsNotOlderThan6monthsAtTheirLastVisit(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}"),
				map(qiEmtctCohortLibrary.hivInfectedOrLactatingWomenOnARTForAtLeast6MonthsWithAtLeastOneAncVisitDuringThe6MonthsReviewPeriod(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}")
		);
	}

	/**
	 *All deliveries should be monitored using an accurately filled partograph
	 * @return CohortIndicator
	 */
	public CohortIndicator allDeliveriesShouldBeMonitoredUsingAnAccuratelyFilledPartograph() {
		return cohortIndicator("Quality of Delivery (Service delivery)",
				map(qiEmtctCohortLibrary.numberOfDeliveriesWithPartographsAccuratelyFilled(6), "onDate=${endDate}"),
				map(qiEmtctCohortLibrary.numberOfDeliveriesInTheFacilityDuringTheReviewPeriod(6), "onDate=${endDate}")
		);
	}

	/**
	 *% of HIV-infected pregnant or lactating women on ART for at least 6 months with VL suppression
	 * @return CohortIndicator
	 */
	public CohortIndicator hivInfectedPregnantOrLactatingWomenOnArtForAtLeast6MonthsWithVlSuppression() {
		return cohortIndicator("HIV Monitoring Viral Load  suppression (Outcome))",
				map(qiEmtctCohortLibrary.numberOfHivInfectedPregnantOrLactatingWomenOnArtForAtLeast6MonthsWhoHaveVlLess1000CopiesOnTheirMostRecentVlResult(), "onOrBefore=${endDate}"),
				map(qiEmtctCohortLibrary.numberOfHivInfectedPregnantOrLactatingWomenOnArtForAtLeast6MonthsWithVlResultNotOlderThan6MonthsFromTheEndOfTheReviewPeriod(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}")
		);
	}

	/**
	 * % HEI who received HIV DNA PCR testing by age 6 weeks and results are available
	 * 3.9
	 * @return CohortIndicator
	 */
	public  CohortIndicator heiWhoReceivedHivDnaPCRTestingByAge6WeeksAndResultsAreAvailable() {
		return cohortIndicator("Early Infant Diagnosis  (Service coverage)",
				map(qiEmtctCohortLibrary.numberOfExposedInfantsWhoReceivedDNAPCRTest(6), "onOrBefore=${endDate}"),
				map(qiEmtctCohortLibrary.heiCohortWhoTurnedXMonthsDuringXMonthsReviewPeriod(12, 6), "onDate=${endDate}")
		);
	}

	/**
	 * % HIV exposed infants on exclusive breast  feeding at age 6 months
	 * 3.10
	 * @return CohortIndicator
	 */
	public CohortIndicator hivExposedInfantsOnExclusiveBreastFeedingAtAge6Months() {
		return cohortIndicator("Infant Feeding Practices (Service Coverage))",
				map(qiEmtctCohortLibrary.heiInfantsOnExclusiveBreastFeedingAtAge6Months(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}"),
				map(qiEmtctCohortLibrary.heiCohortWhoTurnedXMonthsDuringXMonthsReviewPeriod(12, 6), "onDate=${endDate}")
		);
	}

	/**
	 * % HIV exposed mother baby pair (0-18 months) in active care among facility registered
	 * 3.11
	 * @return Cohort
	 */
	public CohortIndicator hivExposedMotherBabyPair0to18MonthsInActiveCareAmongFacilityRegistered() {
		return cohortIndicator("Retention of Mother baby pair - Facility estimate (Retention)",
				map(qiEmtctCohortLibrary.numberOfInfantsSeenInFacilityDuringReviewPeriodWhoseMotherOrGuardianAlsoHaveDocumentedVisitOnSameDayDuringReviewPeriod(6), "onDate=${endDate}"),
				map(qiEmtctCohortLibrary.heiPatientsInFollowUp(), "onOrBefore=${endDate}")
		);
	}

	/**
	 * % HIV exposed mother baby pair (0-18 months) in active care among population estimate
	 * 3.12
	 * @return CohortIndicator
	 */
	public CohortIndicator hivExposedMotherBabyPair0To18MonthsInActiveCareAmongPopulationEstimate() {
		return cohortIndicator("Retention of Mother baby pair - Population estimate (Retention)",
				map(qiEmtctCohortLibrary.numberOfInfantsSeenInFacilityDuringReviewPeriodWhoseMotherOrGuardianAlsoHaveDocumentedVisitOnSameDayDuringReviewPeriod(6), "onDate=${endDate}"),
				map(qiEmtctCohortLibrary.heiPatients0To18Months(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}")
		);
	}

	/**
	 * % HIV exposed infants diagnosed with HIV between 0 and 18 months
	 * 3.13
	 * @return CohortIndicator
	 */
	public CohortIndicator hivExposedInfantsDiagnosedWithHivBetween0And18Months() {
		return cohortIndicator("% HIV exposed infants diagnosed with HIV between 0 and 18 months",
				map(qiEmtctCohortLibrary.heiIdentifiedHivPositiveBy18MonthsOfAge(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}"),
				map(qiEmtctCohortLibrary.heiCohortWhoTurnedXMonthsDuringXMonthsReviewPeriod(24, 6), "onDate=${endDate}")
		);
	}
}
