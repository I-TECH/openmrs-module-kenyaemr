package org.openmrs.module.kenyaemr.reporting.library.shared.hiv;

import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtCohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.shared.tb.TbCohortLibrary;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of Quality Improvement indicators for HIV care paeds. All indicators require parameter ${endDate}
 */
@Component
public class QiPaedsIndicatorLibrary {

	@Autowired
	private HivCohortLibrary hivCohorts;

	@Autowired
	private ArtCohortLibrary artCohorts;

	@Autowired
	private QiPaedsCohortLibrary qiCohorts;

	@Autowired
	private TbCohortLibrary tbCohorts;

	/**
	 * Clinical visit service coverage
	 * @return the indicator
	 */
	public CohortIndicator clinicalVisit() {
		return cohortIndicator("Clinical Visit - Child",
				map(qiCohorts.inCareHasAtLeast2Visits(), "onOrBefore=${endDate}" ),
				map(qiCohorts.clinicalVisit(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}" )
		);
	}

	/**
	 * Percentage of patients with an HIV care visit who have CD4 test results in the last 6 months
	 * @return the indicator
	 */
	public CohortIndicator hivMonitoringCd4() {
		return cohortIndicator("HIV monitoring - CD4 Child",
				map(qiCohorts.hasCD4ResultsPaeds(), "onOrAfter=${startDate},onOrBefore=${endDate}"),
				map(qiCohorts.hasHivVisitPaeds(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Percentage eligible patients initiated on ART
	 * @return the indicator
	 */
	public CohortIndicator artInitiation() {
		return cohortIndicator("ART Initiation - Child",
				map(artCohorts.startedArt(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}" ),
				map(qiCohorts.hivInfectedAndNotOnARTAndHasHivClinicalVisit(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}")
		);
	}

	/**
	 * HIV Monitoring Viral Load coverage
	 * @return the indicator
	 */
	public CohortIndicator hivMonitoringViralLoad() {
		return cohortIndicator("HIV Monitoring - Viral Load Child",
				map(qiCohorts.onARTatLeast12MonthsAndHaveAtLeastVLResultsDuringTheLast12Months(), "onOrBefore=${endDate}" ),
				map(qiCohorts.onARTatLeast12MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}")
		);
	}

	/**
	 * HIV Monitoring Viral Load - suppression outcome
	 * @return CohortIndicator
	 */
	public CohortIndicator hivMonitoringViralLoadSupression() {
		return cohortIndicator("HIV Monitoring - Viral Load - Suppression Outcome Child",
				map(qiCohorts.onARTatLeast12MonthsAndVlLess1000(), "onOrBefore=${endDate}"),
				map(qiCohorts.onARTatLeast12MonthsAndAtLeastVlResults(), "onOrBefore=${endDate}")
		);
	}

	/**
	 * Tb Screening Servicess coverage
	 * @return CohortIndicator
	 */
	public CohortIndicator tbScreeningServiceCoverage() {
		return cohortIndicator("Tb screening - Service Coverage Child",
				map(tbCohorts.screenedForTb(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}"),
				map(qiCohorts.hivInfectedNotOnTbTreatmentHaveAtLeastOneHivClinicalVisitDuring6Months(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}")
		);
	}

	/**
	 * patients eligible for IPT who were initiated on IPT
	 * @return CohortIndicator
	 */
	public CohortIndicator patientsEligibleForIPTWhoWereInitiatedOnIPT() {
		return cohortIndicator("TB IPT - Service Coverage Child",
				map(qiCohorts.patientWithNegativeTbScreenWhoHaveNotHadIPT(), "onOrAfter=${endDate-24m},onOrBefore=${endDate}"),
				map(qiCohorts.patientsWhoHaveHadNoIptWithinLast2YearsTbNegativeDuring6MonthsReviewPeriod(), "onOrAfter=${endDate-6},onOrBefore=${endDate}")
		);
	}

	/**
	 * Percentage of patients who had a nutritional assessment in their last visit
	 * @return the indicator
	 */
	public CohortIndicator nutritionalAssessment() {
		return cohortIndicator("Nutritional assessment Child",
				map(qiCohorts.hadNutritionalAssessmentAtLastVisit(), "onOrBefore=${endDate}"),
				map(qiCohorts.hasHivVisitPaeds(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Patients eligible for nutritional support and who received
	 * @return  CohortIndicator
	 */
	public CohortIndicator patientsEligibleForNutritionalSupportAndWhoReceived() {
		return cohortIndicator("Nutritional Support - Service Coverage Child",
				map(qiCohorts.patientsWhoMeetCriteriaForNutritionalSupport(), "onOrBefore=${endDate}"),
				map(qiCohorts.patientsWhoMeetNutritionalSupportAtLastClinicVisit(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}")
		);
	}

	/**
	 * HIV infected children aged 8-14 years should have their HIV status disclosed
	 * @return CohortIndicator
	 */
	public CohortIndicator childrenBetween8And14WhoseHivStatusDisclosedToThem() {
		return cohortIndicator("Disclosure - Service Coverage Child",
				map(qiCohorts.childrenInfected8to14YearsWhoseStatusDisclosedToThem(), "onOrBefore=${endDate}"),
				map(qiCohorts.childrenInfected8to14YearsEnrolledInCareWithAtLeastOneHivVisit(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}")
		);
	}

}
