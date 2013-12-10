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

import org.openmrs.Concept;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonIndicatorLibrary;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of HIV related indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
@Component
public class HivIndicatorLibrary {

	@Autowired
	private CommonIndicatorLibrary commonIndicators;

	@Autowired
	private HivCohortLibrary artCohorts;

	/**
	 * Number of new patients enrolled in HIV care (excluding transfers)
	 * @return the indicator
	 */
	public CohortIndicator enrolledExcludingTransfers() {
		return commonIndicators.enrolledExcludingTransfers(MetadataUtils.getProgram(HivMetadata._Program.HIV));
	}

	/**
	 * Number of patients ever enrolled in HIV care (including transfers) up to ${endDate}
	 * @return the indicator
	 */
	public CohortIndicator enrolledCumulative() {
		return commonIndicators.enrolledCumulative(MetadataUtils.getProgram(HivMetadata._Program.HIV));
	}

	/**
	 * Number of patients who were enrolled (excluding transfers) after referral from the given entry points
	 * @return the indicator
	 */
	public CohortIndicator enrolledExcludingTransfersAndReferredFrom(Concept... entryPoints) {
		return cohortIndicator("Number of newly enrolled patients referred from",
				map(artCohorts.enrolledExcludingTransfersAndReferredFrom(entryPoints), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients who were enrolled (excluding transfers) after referral from services other than the given entry points
	 * @return the indicator
	 */
	public CohortIndicator enrolledExcludingTransfersAndNotReferredFrom(Concept... entryPoints) {
		return cohortIndicator("Number of newly enrolled patients referred from",
				map(artCohorts.enrolledExcludingTransfersAndNotReferredFrom(entryPoints), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients who started ART
	 * @return the indicator
	 */
	public CohortIndicator startedArt() {
		return cohortIndicator("Number of patients who started ART", map(artCohorts.startedArt(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients who started ART while pregnant
	 * @return the indicator
	 */
	public CohortIndicator startedArtWhilePregnant() {
		return cohortIndicator("Number of patients who started ART while pregnant", map(artCohorts.startedArtWhilePregnant(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients who started ART while being a TB patient
	 * @return the indicator
	 */
	public CohortIndicator startedArtWhileTbPatient() {
		return cohortIndicator("Number of patients who started ART while being a TB patient", map(artCohorts.startedArtWhileTbPatient(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients who started ART with given WHO stage
	 * @return the indicator
	 */
	public CohortIndicator startedArtWithWhoStage(int stage) {
		return cohortIndicator("Number of patients who started ART with WHO stage " + stage, map(artCohorts.startedArtWithWhoStage(stage), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients who have ever started ART
	 * @return the indicator
	 */
	public CohortIndicator startedArtCumulative() {
		return cohortIndicator("Number of patients who have ever started ART", map(artCohorts.startedArt(), "onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients who are eligible for ART
	 * @return the indicator
	 */
	public CohortIndicator eligibleForArt() {
		return cohortIndicator("Number of patients eligible for ART", map(artCohorts.eligibleForArt(), "onDate=${endDate}"));
	}

	/**
	 * Number of patients who are on ART
	 * @return the indicator
	 */
	public CohortIndicator onArt() {
		return cohortIndicator("Number of patients on ART", map(artCohorts.onArt(), "onDate=${endDate}"));
	}

	/**
	 * Number of patients who are on ART and pregnant
	 * @return the indicator
	 */
	public CohortIndicator onArtAndPregnant() {
		return cohortIndicator("Number of patients on ART and pregnant", map(artCohorts.onArtAndPregnant(), "onDate=${endDate}"));
	}

	/**
	 * Number of patients who are on ART and pregnant
	 * @return the indicator
	 */
	public CohortIndicator onArtAndNotPregnant() {
		return cohortIndicator("Number of patients on ART and not pregnant", map(artCohorts.onArtAndNotPregnant(), "onDate=${endDate}"));
	}

	/**
	 * Number of patients who are on Cotrimoxazole prophylaxis
	 * @return the indicator
	 */
	public CohortIndicator onCotrimoxazoleProphylaxis() {
		return cohortIndicator("Number of patients on CTX prophylaxis", map(artCohorts.inHivProgramAndOnCtxProphylaxis(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients who are on Fluconazole prophylaxis
	 * @return the indicator
	 */
	public CohortIndicator onFluconazoleProphylaxis() {
		return cohortIndicator("Number of patients on Fluconazole prophylaxis", map(artCohorts.inHivProgramAndOnFluconazoleProphylaxis(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients who are on any form of prophylaxis
	 * @return the indicator
	 */
	public CohortIndicator onProphylaxis() {
		return cohortIndicator("Number of patients on prophylaxis", map(artCohorts.inHivProgramAndOnAnyProphylaxis(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}
}