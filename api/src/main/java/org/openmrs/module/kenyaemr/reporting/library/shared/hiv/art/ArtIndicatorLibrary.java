/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art;

import org.openmrs.Concept;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of ART Drugs related indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
@Component
public class ArtIndicatorLibrary {

	@Autowired
	private ArtCohortLibrary artCohorts;

	/**
	 * Number of patients who are eligible for ART
	 * @return the indicator
	 */
	public CohortIndicator eligibleForArt() {
		return cohortIndicator("patients eligible for ART", map(artCohorts.eligibleForArt(), "onDate=${endDate}"));
	}

	/**
	 * Number of patients who are on ART
	 * @return the indicator
	 */
	public CohortIndicator onArt() {
		return cohortIndicator("patients on ART", map(artCohorts.onArt(), "onDate=${endDate}"));
	}

	/**
	 * Number of patients who are on ART and pregnant
	 * @return the indicator
	 */
	public CohortIndicator onArtAndPregnant() {
		return cohortIndicator("patients on ART and pregnant", map(artCohorts.onArtAndPregnant(), "onDate=${endDate}"));
	}

	/**
	 * Number of patients who are on ART and pregnant
	 * @return the indicator
	 */
	public CohortIndicator onArtAndNotPregnant() {
		return cohortIndicator("patients on ART and not pregnant", map(artCohorts.onArtAndNotPregnant(), "onDate=${endDate}"));
	}

	/**
	 * Number of patients having the given ART regimen
	 * @return indicator
	 */
	public CohortIndicator onRegimen(List<Concept> regimen) {
		return cohortIndicator("", map(artCohorts.inHivProgramAndOnRegimen(regimen), "onDate=${endDate}"));
	}

	/**
	 * Number of patients who started ART
	 * @return the indicator
	 */
	public CohortIndicator startedArt() {
		return cohortIndicator("patients who started ART", map(artCohorts.startedArt(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients who started ART while pregnant
	 * @return the indicator
	 */
	public CohortIndicator startedArtWhilePregnant() {
		return cohortIndicator("patients who started ART while pregnant", map(artCohorts.startedArtWhilePregnant(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients who started ART while being a TB patient
	 * @return the indicator
	 */
	public CohortIndicator startedArtWhileTbPatient() {
		return cohortIndicator("patients who started ART while being a TB patient", map(artCohorts.startedArtWhileTbPatient(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients who started ART with given WHO stage
	 * @return the indicator
	 */
	public CohortIndicator startedArtWithWhoStage(int stage) {
		return cohortIndicator("patients who started ART with WHO stage " + stage, map(artCohorts.startedArtWithWhoStage(stage), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients who have ever started ART
	 * @return the indicator
	 */
	public CohortIndicator startedArtCumulative() {
		return cohortIndicator("patients who have ever started ART", map(artCohorts.startedArtExcludingTransferinsOnDate(), "onOrBefore=${endDate}"));
	}
}