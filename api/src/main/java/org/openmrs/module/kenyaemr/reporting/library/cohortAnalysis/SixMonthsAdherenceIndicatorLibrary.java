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

package org.openmrs.module.kenyaemr.reporting.library.cohortAnalysis;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of cohorts of interest to Global funds research
 */
@Component
public class SixMonthsAdherenceIndicatorLibrary {

	@Autowired
	private SixMonthsAdherenceCohortLibrary cohorts;

	/**
	 * Number of patients enrolled in the given period
	 */
	public CohortIndicator allPatients() {
		return cohortIndicator("All patients", 
				ReportUtils.map(cohorts.allPatientsCohortDefinition(), "onDate=${startDate}"));
	}

	/**
	 * Two weeks cohort definition
	 */
	public CohortIndicator twoWeeksPatients() {
		return cohortIndicator("Two weeks patients",
				ReportUtils.map(cohorts.twoWeeksCohortDefinition(), "onDate=${startDate}"));
	}

	/**
	 * Two weeks cohort definition
	 */
	public CohortIndicator oneMonthPatients() {
		return cohortIndicator("One Month patients",
				ReportUtils.map(cohorts.oneMonthCohortDefinition(), "onDate=${startDate}"));
	}

	/**
	 * Two weeks cohort definition
	 */
	public CohortIndicator twoMonthPatients() {
		return cohortIndicator("Two month patients",
				ReportUtils.map(cohorts.twoMonthCohortDefinition(), "onDate=${startDate}"));
	}

	/**
	 * Two weeks cohort definition
	 */
	public CohortIndicator threeMonthPatients() {
		return cohortIndicator("Three month patients",
				ReportUtils.map(cohorts.threeMonthCohortDefinition(), "onDate=${startDate}"));
	}
	/**
	 * Two weeks cohort definition
	 */
	public CohortIndicator sixMonthPatients() {
		return cohortIndicator("Six month patients",
				ReportUtils.map(cohorts.sixMonthCohortDefinition(), "onDate=${startDate}"));
	}

	/**
	 * Two weeks cohort definition
	 */
	public CohortIndicator alivePatients(String key) {
		return cohortIndicator("alive patients",
				ReportUtils.map(cohorts.aliveCohortDefinition(key), "onDate=${startDate}"));
	}

	/**
	 * Two weeks cohort definition
	 */
	public CohortIndicator deadPatients(String key) {
		return cohortIndicator("dead patients",
				ReportUtils.map(cohorts.deadCohortDefinition(key), "onDate=${startDate}"));
	}

	/**
	 * Two weeks cohort definition
	 */
	public CohortIndicator ltfuPatients(String key) {
		return cohortIndicator("LFTU patients",
				ReportUtils.map(cohorts.ltfuCohortDefinition(key), "onDate=${startDate}"));
	}

	/**
	 * Two weeks cohort definition
	 */
	public CohortIndicator stoppedPatients(String key) {
		return cohortIndicator("Stopped patients",
				ReportUtils.map(cohorts.stoppedCohortDefinition(key), "onDate=${startDate}"));
	}
}