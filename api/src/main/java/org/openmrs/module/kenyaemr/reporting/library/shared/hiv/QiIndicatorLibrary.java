/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
	 * Has cd4 results only adults numerator
	 * @return
	 */
	public CohortIndicator hasCd4Results(){
		return cohortIndicator("Has cd4 results",
				map(qiCohorts.hasCD4ResultsAndHasHivVisitAdult(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Has visits
	 * @Return indicator
	 */
	public CohortIndicator hasVisits(){
		return cohortIndicator("Has cd4 results",
				map(qiCohorts.hasHivVisitAdult(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Compliment of hasCd4Results
	 * @return CohortDefinition
	 */
	public CohortIndicator complimentHasCd4Results() {
		return cohortIndicator("Compliment of  cd4 results",
				map(qiCohorts.complimentHasCd4Results(), "onOrAfter=${startDate},onOrBefore=${endDate}")
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
	 * numerator
	 */
	public CohortIndicator nutritionalAssessmentNum() {
		return cohortIndicator("Nutritional assessment-numerator",
				map(qiCohorts.hadNutritionalAssessmentAtLastVisitAndhasHivVisitAdult(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}")
		);
	}

	/**
	 *
	 */
	public CohortIndicator nutritionalAssessmentDen() {
		return cohortIndicator("Nutritional assessment-denominator",
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
	 * Patients who are eligible and started art during 6 months review period adults numerator
	 * @return CohortIndicator
	 */
	public CohortIndicator patientsWhoAreEligibleAndStartedArt(){
		return cohortIndicator("Patients who are eligible and started art during 6 months review period adults",
				map(artCohorts.eligibleAndStartedARTAndHivInfectedAndNotOnARTAndHasHivClinicalVisit(), "onOrAfter=${startDate},onOrBefore=${endDate}" )
		);
	}

	/**
	 * HIV infected patients NOT on ART and has hiv clinical visit
	 * @return CohortIndicator
	 */
	public CohortIndicator hivInfectedPatientsNotOnArtAndHasHivClinicalVisit(){
		return cohortIndicator("HIV infected patients NOT on ART and has hiv clinical visit",
				map(qiCohorts.hivInfectedAndNotOnARTAndHasHivClinicalVisit(), "onOrAfter=${startDate},onOrBefore=${endDate}" )
		);
	}
	/**
	 * Compliment of patientsWhoAreEligibleAndStartedArt
	 * @return CohortIndicator
	 */
	public CohortIndicator complimentPatientsWhoAreEligibleAndStartedArt() {
		return cohortIndicator("Compliment of patientsWhoAreEligibleAndStartedArt",
				map(qiCohorts.complimentPatientsWhoAreEligibleAndStartedArt(), "onOrAfter=${startDate},onOrBefore=${endDate}" )
		);
	}



	/**
	 * Patients in care and has at least 2 visits
	 * @return indicator
	 */
	public CohortIndicator patientsInCareAndHasAtLeast2Visits() {
		return cohortIndicator("Patients in care and has at least 2 visits",
				map(qiCohorts.patientsInCareAndHasAtLeast2Visits(), "onOrBefore=${endDate}"));
	}

	/**
	 * Patients with a clinical visits
	 * @return indicator
	 */
	public CohortIndicator patientsWithClinicalVisits() {
		return cohortIndicator("Patients with a clinical visits",
				map(qiCohorts.clinicalVisit(), "onOrAfter=${endDate},onOrBefore=${endDate}" ));
	}

	/**
	 * Patients representing those not meeting the indicator
	 * @return indicator
	 */
	public CohortIndicator complimentPatientsWithClinicalVisits() {
		return cohortIndicator("Patients representing those not meeting the indicator",
				map(qiCohorts.complimentClinicalVisit(), "onOrAfter=${endDate},onOrBefore=${endDate}" ));
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
	 * Patients on ART for at least 12 months by the end of the review period numerator
	 * Patients have at least one Viral Load (VL) results during the last 12 months
	 * @return CohortIndicator
	 */
	public CohortIndicator patientsOnArtHavingAtLeastOneViralLoad() {
		return cohortIndicator("",
				map(qiCohorts.onARTatLeast12MonthAndHaveAtLeastOneVisitDuringTheLast6MonthsReview(), "onOrBefore=${endDate}" )
		);
	}

	/**
	 * Number of HIV infected patients on ART 12 months ago
	 * Have atleast one clinical visit during the six months review period
	 * @return CohortIndicator
	 */
	public CohortIndicator onArtWithAtLeastOneClinicalVisit() {
		return cohortIndicator("",
				map(qiCohorts.onARTatLeast12MonthsAndHaveAtLeastOneVisitDuringTheLast6MonthsReview(), "onOrAfter=${startDate},onOrBefore=${endDate}" )
		);
	}

	/**
	 *
	 */
	public CohortIndicator complimentPatientsOnArtHavingAtLeastOneViralLoad() {
		return cohortIndicator("",
				map(qiCohorts.complimentPatientsOnArtHavingAtLeastOneViralLoad(), "onOrAfter=${startDate},onOrBefore=${endDate}" )
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
	 * Number of patients on ART for at least 12 months numerator
	 * VL < 1000 copies
	 */
	public CohortIndicator onARTatLeast12MonthsAndVlLess1000() {
		return cohortIndicator("on ART with VL < 1000",
				map(qiCohorts.onARTatLeast12MonthsAndVlLess1000AndHivMonitoringViralLoadNumAndDen(), "onOrBefore=${endDate}" )
		);
	}

	/**
	 *
	 */
	public CohortIndicator hivMonitoringViralLoadNumAndDen() {
		return cohortIndicator("",
				map(qiCohorts. hivMonitoringViralLoadNumAndDen(), "onOrAfter=${startDate},onOrBefore=${endDate}" )
		);
	}

	/**
	 *
	 */
	public CohortIndicator complimentOnARTatLeast12MonthsAndVlLess1000() {
		return cohortIndicator("",
				map(qiCohorts.complimentOnARTatLeast12MonthsAndVlLess1000(), "onOrAfter=${startDate},onOrBefore=${endDate}" )
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
	 *Adult patients screened for tb using ICF form numerator
	 * @return CohortIndicator
	 */
	public CohortIndicator tbScreeningUsingIcfAdult() {
		return cohortIndicator("",
				map(qiCohorts.screenedForTBUsingICFNotOnTbTreatmentAndHsaClinicalVisits(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 *Number of HIV infected patients currently NOT on ant TB treatment
	 * Patients have at least one HIV clinical visit during the 6 months review period
	 * @return CohortIndicator
	 */
	public CohortIndicator patientsCurrentlyNotOnTbTreatmentAndHaveClinicalVisit() {
		return cohortIndicator("",
				map(qiCohorts.hivInfectedNotOnTbTreatmentHaveAtLeastOneHivClinicalVisitDuring6Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 *
	 */
	public CohortIndicator complimentTbScreeningUsingIcfAdult() {
		return cohortIndicator("",
				map(qiCohorts.complimentTbScreeningUsingIcfAdult(), "onOrAfter=${startDate},onOrBefore=${endDate}")
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

	/**
	 *
	 */
	public CohortIndicator reproductiveHealthFamilyPlanningNum() {
		return cohortIndicator("Reproductive Health - Service Coverage-numerator",
				map(qiCohorts.nonPregnantWomen15To49YearsOnModernContraceptivesAndHasVisits(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 *
	 */
	public CohortIndicator reproductiveHealthFamilyPlanningDen() {
		return cohortIndicator("Reproductive Health - Service Coverage-denominator",
				map(qiCohorts.nonPregnantWomen15To49YearsWithAtLeastOneHivClinicalVisit(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	public CohortIndicator complimentNutritionalAssessmentNum() {
		return cohortIndicator("",
				map(qiCohorts.complimentNutritionalAssessmentNum(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	public CohortIndicator complimentReproductiveHealthFamilyPlanningNum() {

		return cohortIndicator("",
				map(qiCohorts.complimentReproductiveHealthFamilyPlanningNum(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}
}