/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.art;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of ART related indicator definitions.
 */
@Component
public class ETLArtRegisterIndicatorLibrary {

@Autowired
ETLArtRegisterCohortLibrary artRegisterCohorts;

	/**
	 * Number of patients who were started on Art in facility
	 *
	 * @return the indicator
	 */
	public CohortIndicator originalArtCohort() {
		return cohortIndicator("Original ART Cohort", ReportUtils.map(artRegisterCohorts.originalArtCohort(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * Number of patients who were started on Art in transferring facility
	 *
	 * @return the indicator
	 */
	public CohortIndicator transferINArtCohort() {
		return cohortIndicator("TransferIn ART Cohort", ReportUtils.map(artRegisterCohorts.transferInArtCohort(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * Number of patients who were started on Art and subsequently transferred out facility
	 *
	 * @return the indicator
	 */
	public CohortIndicator transferOUTArtCohort() {
		return cohortIndicator("TransferOut ART Cohort", ReportUtils.map(artRegisterCohorts.transferOutArtCohort(), "startDate=${startDate},endDate=${endDate}"));
	}


	/**
	 * Number of patients who are on their original first line ART
	 *
	 * @return the indicator
	 */
	public CohortIndicator onOriginalFirstLineArtCohort() {
		return cohortIndicator("Original First Line ART Cohort", ReportUtils.map(artRegisterCohorts.originalFirstLineArtCohort(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * Number of patients who are on their alternate first line ART
	 *
	 * @return the indicator
	 */
	public CohortIndicator onAlternateFirstLineArtCohort() {
		return cohortIndicator("Alternate First Line ART Cohort", ReportUtils.map(artRegisterCohorts.alternateFirstLineArtCohort(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * Number of patients who are on their second line ART
	 *
	 * @return the indicator
	 */
	public CohortIndicator onSecondLineArtCohort() {
		return cohortIndicator("Second Line ART Cohort", ReportUtils.map(artRegisterCohorts.secondLineArtCohort(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * Number of patients who are on their third line ART
	 *
	 * @return the indicator
	 */
	public CohortIndicator onThirdLineArtCohort() {
		return cohortIndicator("Third Line ART Cohort", ReportUtils.map(artRegisterCohorts.thirdLineArtCohort(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * Number of patients in ART Cohort with VL Results
	 *
	 * @return the indicator
	 */
	public CohortIndicator withViralLoadResultsArtCohort() {
		return cohortIndicator("ART Cohort with VL Results", ReportUtils.map(artRegisterCohorts.withVlResultsArtCohort(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Number of patients in ART Cohort with Suppressed VL Results
	 *
	 * @return the indicator
	 */
	public CohortIndicator withSuppressedViralLoadResultsArtCohort() {
		return cohortIndicator("ART Cohort with Suppressed VL Results", ReportUtils.map(artRegisterCohorts.suppressedArtCohort(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * Number of patients in ART Cohort who stopped ART
	 *
	 * @return the indicator
	 */
	public CohortIndicator stoppedArtCohort() {
		return cohortIndicator("ART Cohort Stopped ART ", ReportUtils.map(artRegisterCohorts.stoppedArt(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * Number of patients in ART Cohort who are missed appointments
	 *
	 * @return the indicator
	 */
	public CohortIndicator defaulterArtCohort() {
		return cohortIndicator("ART Cohort Missed Appointments ", ReportUtils.map(artRegisterCohorts.defaulterArtCohort(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * Number of patients in ART Cohort who are dead
	 *
	 * @return the indicator
	 */
	public CohortIndicator deadOnArtCohort() {
		return cohortIndicator("ART Cohort Dead ", ReportUtils.map(artRegisterCohorts.deadArtCohort(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * Number of patients in ART Cohort who are ltfu
	 *
	 * @return the indicator
	 */
	public CohortIndicator ltfuOnArtCohort() {
		return cohortIndicator("ART Cohort Ltfu ", ReportUtils.map(artRegisterCohorts.ltfuArtCohort(), "startDate=${startDate},endDate=${endDate}"));
	}


}