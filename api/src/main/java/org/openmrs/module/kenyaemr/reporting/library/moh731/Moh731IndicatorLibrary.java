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

package org.openmrs.module.kenyaemr.reporting.library.moh731;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.reporting.indicator.HivCareVisitsIndicator;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtCohortLibrary;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.Indicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Indicators specific to the MOH731 report
 */
@Component
public class Moh731IndicatorLibrary {

	@Autowired
	private ArtCohortLibrary artCohorts;

	@Autowired
	private Moh731CohortLibrary moh731Cohorts;

	/**
	 * Number of patients currently in care (includes transfers)
	 * @return the indicator
	 */
	public CohortIndicator currentlyInCare() {
		return cohortIndicator("Currently in care (includes transfers)", ReportUtils.map(moh731Cohorts.currentlyInCare(), "onDate=${endDate}"));
	}

	/**
	 * Number of patients who are ART revisits
	 * @return the indicator
	 */
	public CohortIndicator revisitsArt() {
		return cohortIndicator("Revisits ART", ReportUtils.map(moh731Cohorts.revisitsArt(), "fromDate=${startDate},toDate=${endDate}"));
	}

	/**
	 * Number of patients who are currently on ART
	 * @return the indicator
	 */
	public CohortIndicator currentlyOnArt() {
		return cohortIndicator("Currently on ART", ReportUtils.map(moh731Cohorts.currentlyOnArt(), "fromDate=${startDate},toDate=${endDate}"));
	}

	/**
	 * Cumulative number of patients on ART
	 * @return the indicator
	 */
	public CohortIndicator cumulativeOnArt() {
		return cohortIndicator("Cumulative ever on ART", ReportUtils.map(artCohorts.startedArtExcludingTransferinsOnDate(), "onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients in the ART 12 month cohort
	 * @return the indicator
	 */
	public CohortIndicator art12MonthNetCohort() {
		//add a hacky way to determine if art start date is at the end of every month then add one day
		//to avoid reporting twice in the previouse and the following month
		return cohortIndicator("ART 12 Month Net Cohort", ReportUtils.map(artCohorts.netCohortMonths(12), "onDate=${endDate + 1d}"));
	}

	/**
	 * Number of patients in the 12 month cohort who are on their original first-line regimen
	 * @return the indicator
	 */
	public CohortIndicator onOriginalFirstLineAt12Months() {
		return cohortIndicator("On original 1st line at 12 months", ReportUtils.map(moh731Cohorts.onOriginalFirstLineAt12Months(), "fromDate=${startDate},toDate=${endDate + 1d}"));
	}

	/**
	 * Number of patients in the 12 month cohort who are on an alternate first-line regimen
	 * @return the indicator
	 */
	public CohortIndicator onAlternateFirstLineAt12Months() {
		return cohortIndicator("On alternate 1st line at 12 months", ReportUtils.map(moh731Cohorts.onAlternateFirstLineAt12Months(), "fromDate=${startDate},toDate=${endDate + 1d}"));
	}

	/**
	 * Number of patients in the 12 month cohort who are on a second-line regimen
	 * @return the indicator
	 */
	public CohortIndicator onSecondLineAt12Months() {
		return cohortIndicator("On 2nd line at 12 months", ReportUtils.map(moh731Cohorts.onSecondLineAt12Months(), "fromDate=${startDate},toDate=${endDate + 1d}"));
	}

	/**
	 * Number of patients in the 12 month cohort who are on ART
	 * @return the indicator
	 */
	public CohortIndicator onTherapyAt12Months() {
		return cohortIndicator("On therapy at 12 months", ReportUtils.map(moh731Cohorts.onTherapyAt12Months(), "fromDate=${startDate},toDate=${endDate + 1d}"));
	}

	/**
	 * Number of HIV care visits for females aged 18 and over
	 * @return the indicator
	 */
	public Indicator hivCareVisitsFemale18() {
		HivCareVisitsIndicator ind = new HivCareVisitsIndicator();
		ind.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ind.addParameter(new Parameter("endDate", "End Date", Date.class));
		ind.setFilter(HivCareVisitsIndicator.Filter.FEMALES_18_AND_OVER);
		return ind;
	}

	/**
	 * Number of scheduled HIV care visits
	 * @return the indicator
	 */
	public Indicator hivCareVisitsScheduled() {
		HivCareVisitsIndicator ind = new HivCareVisitsIndicator();
		ind.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ind.addParameter(new Parameter("endDate", "End Date", Date.class));
		ind.setFilter(HivCareVisitsIndicator.Filter.SCHEDULED);
		return ind;
	}

	/**
	 * Number of unscheduled HIV care visits
	 * @return the indicator
	 */
	public Indicator hivCareVisitsUnscheduled() {
		HivCareVisitsIndicator ind = new HivCareVisitsIndicator();
		ind.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ind.addParameter(new Parameter("endDate", "End Date", Date.class));
		ind.setFilter(HivCareVisitsIndicator.Filter.UNSCHEDULED);
		return ind;
	}

	/**
	 * Total number of HIV care visits
	 * @return the indicator
	 */
	public Indicator hivCareVisitsTotal() {
		HivCareVisitsIndicator ind = new HivCareVisitsIndicator();
		ind.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ind.addParameter(new Parameter("endDate", "End Date", Date.class));
		return ind;
	}
}