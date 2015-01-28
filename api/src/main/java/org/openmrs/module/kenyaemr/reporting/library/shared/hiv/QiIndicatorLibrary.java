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

import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.tb.TbCohortLibrary;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of Quality Improvement indicators for HIV care. All indicators require parameter ${endDate}
 */
@Component
public class QiIndicatorLibrary {

	@Autowired
	private HivCohortLibrary hivCohorts;

	@Autowired
	private ArtCohortLibrary artCohorts;

	@Autowired
	private QiCohortLibrary qiCohorts;

	@Autowired
	private TbCohortLibrary tbCohorts;

	/**
	 * Percentage of patients with an HIV care visit who have CD4 test results in the last 6 months
	 * @return the indicator
	 */
	public CohortIndicator hivMonitoringCd4() {
		return cohortIndicator("HIV monitoring - CD4",
				map(qiCohorts.hasCD4ResultsAdult(), "onOrAfter=${startDate},onOrBefore=${endDate}"),
				map(qiCohorts.hasHivVisitAdult(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Percentage of patients who had a nutritional assessment in their last visit
	 * @return the indicator
	 */
	public CohortIndicator nutritionalAssessment() {
		return cohortIndicator("Nutritional assessment",
				map(qiCohorts.hadNutritionalAssessmentAtLastVisit(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}"),
				map(qiCohorts.hasHivVisitAdult(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Percentage eligible patients initiated on ART
	 * @return the indicator
	 */
	public CohortIndicator artInitiation() {
		return cohortIndicator("ART Initiation",
				map(artCohorts.eligibleAndStartedARTAdult(), "onOrAfter=${startDate},onOrBefore=${endDate}" ),
				map(qiCohorts.hivInfectedAndNotOnARTAndHasHivClinicalVisit(), "onOrAfter=${startDate},onOrBefore=${endDate}" )
				);
	}

	/**
	 * Clinical visit service coverage
	 * @return the indicator
	 */
	public CohortIndicator clinicalVisit() {
		return cohortIndicator("Clinical Visit",
				map(qiCohorts.inCareHasAtLeast2Visits(), "onOrBefore=${endDate}" ),
				map(qiCohorts.clinicalVisit(), "onOrAfter=${endDate},onOrBefore=${endDate}" )
		);
	}

	/**
	 * HIV Monitoring Viral Load coverage
	 * @return the indicator
	 */
	public CohortIndicator hivMonitoringViralLoad() {
		return cohortIndicator("HIV Monitoring - Viral Load Coverage",
				map(qiCohorts.onARTatLeast12MonthsAndHaveAtLeastVLResultsDuringTheLast12Months(), "onOrBefore=${endDate}" ),
				map(qiCohorts.onARTatLeast12MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview(), "onOrAfter=${startDate},onOrBefore=${endDate}" )
		);
	}

	/**
	 * HIV Monitoring Viral Load - supression outcome
	 * @return CohortIndicator
	 */
	public CohortIndicator hivMonitoringViralLoadSuppression() {
		return cohortIndicator("HIV Monitoring - Viral Load - Supression Outcome",
				map(qiCohorts.onARTatLeast12MonthsAndVlLess1000(), "onOrBefore=${endDate}" ),
				map(qiCohorts. hivMonitoringViralLoadNumAndDen(), "onOrAfter=${startDate},onOrBefore=${endDate}" )
		);
	}

	/**
	 * Tb Screening Servicess coverage
	 * @return CohortIndicator
	 */
	public CohortIndicator tbScreeningServiceCoverage() {
		return cohortIndicator("Tb screening - Service Coverage",
				map(qiCohorts.screenedForTBUsingICF(), "onOrAfter=${startDate},onOrBefore=${endDate}"),
				map(qiCohorts.hivInfectedNotOnTbTreatmentHaveAtLeastOneHivClinicalVisitDuring6Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * patients eligible for IPT who were initiated on IPT
	 * @return CohortIndicator
	 */
	public CohortIndicator patientsEligibleForIPTWhoWereInitiatedOnIPT() {
		return cohortIndicator("TB IPT - Service Coverage",
				map(qiCohorts.patientWithNegativeTbScreenWhoHaveNotHadIPT(), "onOrAfter=${endDate-24m},onOrBefore=${endDate}"),
				map(qiCohorts.patientsWhoHaveHadNoIptWithinLast2YearsTbNegativeDuring6MonthsReviewPeriod(), "onOrAfter=${endDate-6},onOrBefore=${endDate}")
		);
	}

	/**
	 * Patients eligible for nutritional support and who received
	 * @return  CohortIndicator
	 */
	public CohortIndicator patientsEligibleForNutritionalSupportAndWhoReceived() {
		return cohortIndicator("Nutritional Support - Service Coverage",
				map(qiCohorts.patientsWhoMeetCriteriaForNutritionalSupport(), "onOrBefore=${endDate}"),
				map(qiCohorts.patientsWhoMeetNutritionalSupportAtLastClinicVisit(), "onOrBefore=${endDate}")
		);
	}

	/**
	 * Partner testing - service coverage
	 * @return CohortIndicator
	 */
	public CohortIndicator partnerTesting() {
		return cohortIndicator("Partner Testing- Service Coverage",
				map(qiCohorts.hivPositivePatientsWhosePartnersAreHivPositive(), "onOrAfter=${startDate},onOrBefore=${endDate}"),
				map(qiCohorts.hivPositivePatientsWithAtLeastOnePartner(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Children Testing - services coverage
	 * @return CohortIndicator
	 */
	public CohortIndicator childrenTesting() {
		return cohortIndicator("Children Testing- Service Coverage",
				map(qiCohorts.hivPositivePatientsWhoseChildrenAreHivPositive(), "onOrAfter=${startDate},onOrBefore=${endDate}"),
				map(qiCohorts.hivPositivePatientsWithAtLeastOneChildOrMinor(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}")
		);
	}

	/**
	 * Reproductive Health family planning service coverage
	 * @return CohortIndicator
	 */
	public CohortIndicator reproductiveHealthFamilyPlanning() {
		return cohortIndicator("Reproductive Health - Service Coverage",
				map(qiCohorts.nonPregnantWomen15To49YearsOnModernContraceptives(), "onOrAfter=${startDate},onOrBefore=${endDate}"),
				map(qiCohorts.nonPregnantWomen15To49YearsWithAtLeastOneHivClinicalVisit(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}
}