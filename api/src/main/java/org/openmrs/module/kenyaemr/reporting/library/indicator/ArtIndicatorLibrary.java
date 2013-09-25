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

package org.openmrs.module.kenyaemr.reporting.library.indicator;

import org.openmrs.Concept;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.library.cohort.ArtCohortLibrary;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;

/**
 * Library of ART related indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
@Component
public class ArtIndicatorLibrary {

	@Autowired
	private CommonIndicatorLibrary commonIndicators;

	@Autowired
	private ArtCohortLibrary artCohorts;

	/**
	 * Number of new patients enrolled in HIV care (excluding transfers)
	 * @return the indicator
	 */
	public CohortIndicator enrolledExcludingTransfers() {
		return commonIndicators.enrolledExcludingTransfers(MetadataUtils.getProgram(HivMetadata.Program.HIV));
	}

	/**
	 * Number of patients ever enrolled in HIV care (including transfers) up to ${endDate}
	 * @return the indicator
	 */
	public CohortIndicator enrolledCumulative() {
		return commonIndicators.enrolledCumulative(MetadataUtils.getProgram(HivMetadata.Program.HIV));
	}

	/**
	 * Number of patients who were enrolled (excluding transfers) after referral from the given entry points
	 * @return the indicator
	 */
	public CohortIndicator enrolledExcludingTransfersAndReferredFrom(Concept... entryPoints) {
		return CommonIndicatorLibrary.createCohortIndicator("Number of newly enrolled patients referred from",
				ReportUtils.map(artCohorts.enrolledExcludingTransfersAndReferredFrom(entryPoints), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients who were enrolled (excluding transfers) after referral from services other than the given entry points
	 * @return the indicator
	 */
	public CohortIndicator enrolledExcludingTransfersAndNotReferredFrom(Concept... entryPoints) {
		return CommonIndicatorLibrary.createCohortIndicator("Number of newly enrolled patients referred from",
				ReportUtils.map(artCohorts.enrolledExcludingTransfersAndNotReferredFrom(entryPoints), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients who started ART
	 * @return the indicator
	 */
	public CohortIndicator startedArt() {
		return CommonIndicatorLibrary.createCohortIndicator("Number of patients who started ART", ReportUtils.map(artCohorts.startedArt(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients who started ART while pregnant
	 * @return the indicator
	 */
	public CohortIndicator startedArtWhilePregnant() {
		return CommonIndicatorLibrary.createCohortIndicator("Number of patients who started ART while pregnant", ReportUtils.map(artCohorts.startedArtWhilePregnant(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients who started ART while being a TB patient
	 * @return the indicator
	 */
	public CohortIndicator startedArtWhileTbPatient() {
		return CommonIndicatorLibrary.createCohortIndicator("Number of patients who started ART while being a TB patient", ReportUtils.map(artCohorts.startedArtWhileTbPatient(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients who started ART with given WHO stage
	 * @return the indicator
	 */
	public CohortIndicator startedArtWithWhoStage(int stage) {
		return CommonIndicatorLibrary.createCohortIndicator("Number of patients who started ART with WHO stage " + stage, ReportUtils.map(artCohorts.startedArtWithWhoStage(stage), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients who have ever started ART
	 * @return the indicator
	 */
	public CohortIndicator startedArtCumulative() {
		return CommonIndicatorLibrary.createCohortIndicator("Number of patients who have ever started ART", ReportUtils.map(artCohorts.startedArt(), "onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients who are eligible for ART
	 * @return the indicator
	 */
	public CohortIndicator eligibleForArt() {
		return CommonIndicatorLibrary.createCohortIndicator("Number of patients eligible for ART", ReportUtils.map(artCohorts.eligibleForArt(), "onDate=${endDate}"));
	}

	/**
	 * Number of patients who are on ART
	 * @return the indicator
	 */
	public CohortIndicator onArt() {
		return CommonIndicatorLibrary.createCohortIndicator("Number of patients on ART", ReportUtils.map(artCohorts.onArt(), "onDate=${endDate}"));
	}

	/**
	 * Number of patients who are on ART and pregnant
	 * @return the indicator
	 */
	public CohortIndicator onArtAndPregnant() {
		return CommonIndicatorLibrary.createCohortIndicator("Number of patients on ART and pregnant", ReportUtils.map(artCohorts.onArtAndPregnant(), "onDate=${endDate}"));
	}

	/**
	 * Number of patients who are on ART and pregnant
	 * @return the indicator
	 */
	public CohortIndicator onArtAndNotPregnant() {
		return CommonIndicatorLibrary.createCohortIndicator("Number of patients on ART and not pregnant", ReportUtils.map(artCohorts.onArtAndNotPregnant(), "onDate=${endDate}"));
	}

	/**
	 * Number of patients who are on Cotrimoxazole prophylaxis
	 * @return the indicator
	 */
	public CohortIndicator onCotrimoxazoleProphylaxis() {
		Concept[] drugs = { Dictionary.getConcept(Dictionary.SULFAMETHOXAZOLE_TRIMETHOPRIM) };
		return CommonIndicatorLibrary.createCohortIndicator("Number of patients on Cotrimoxazole", ReportUtils.map(artCohorts.inHivProgramAndOnMedication(drugs), "onDate=${endDate}"));
	}

	/**
	 * Number of patients who are on Fluconazole prophylaxis
	 * @return the indicator
	 */
	public CohortIndicator onFluconazoleProphylaxis() {
		Concept[] drugs = { Dictionary.getConcept(Dictionary.FLUCONAZOLE) };
		return CommonIndicatorLibrary.createCohortIndicator("Number of patients on Fluconazole", ReportUtils.map(artCohorts.inHivProgramAndOnMedication(drugs), "onDate=${endDate}"));
	}

	/**
	 * Number of patients who are on any form of prophylaxis
	 * @return the indicator
	 */
	public CohortIndicator onProphylaxis() {
		Concept[] drugs = { Dictionary.getConcept(Dictionary.FLUCONAZOLE), Dictionary.getConcept(Dictionary.SULFAMETHOXAZOLE_TRIMETHOPRIM) };
		return CommonIndicatorLibrary.createCohortIndicator("Number of patients on prophylaxis", ReportUtils.map(artCohorts.inHivProgramAndOnMedication(drugs), "onDate=${endDate}"));
	}
}