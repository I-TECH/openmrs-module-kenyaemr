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

package org.openmrs.module.kenyaemr.reporting.library.shared.tb;

import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of TB related indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
@Component
public class TbIndicatorLibrary {

	@Autowired
	private TbCohortLibrary tbCohorts;

	/**
	 * Number of patients screened for TB
	 * @return the indicator
	 */
	public CohortIndicator screenedForTb() {
		return cohortIndicator("patients screened for TB",
				map(tbCohorts.screenedForTb(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who died and started TB treatment 12 months ago
	 * @return the indicator
	 */
	public CohortIndicator diedAndStarted12MonthsAgo() {
		return cohortIndicator("patients who started TB treatment 12 months ago and died",
				map(tbCohorts.diedAndStarted12MonthsAgo(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who completed Tb Treatment and are in Tb program
	 * @return the indicator
	 */
	public CohortIndicator completedTbTreatment() {
		return cohortIndicator("patients who completed TB treatment",
				map(tbCohorts.completedTreatment(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}


	/**
	 * Number of patients who defaulted
	 * @return the indicator
	 */
	public CohortIndicator defaulted() {
		return cohortIndicator("patients who defaulted", map(tbCohorts.defaulted(), "onDate=${endDate}"));
	}

	/**
	 * Number of patients in Tb and HIV programs who are taking CTX prophylaxis
	 * @return the indicator
	 */
	public CohortIndicator inTbAndHivProgramsAndOnCtxProphylaxis() {
		return cohortIndicator("in TB and HIV programs and on CTX prophylaxis",
				map(tbCohorts.inTbAndHivProgramsAndOnCtxProphylaxis(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients in Tb and are HIV tested
	 * @return the indicator
	 */
	public CohortIndicator inTbAndTestedForHiv() {
		return cohortIndicator("in TB program and tested for HIV",
				map(tbCohorts.testedForHivAndInTbProgram(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients in Tb and are HIV tested and their result is positive
	 * @return the indicator
	 */
	public CohortIndicator inTbAndTestedForHivPositive() {
		return cohortIndicator("in TB program and tested positive for HIV",
				map(tbCohorts.testedHivPositiveAndInTbProgram(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients with Tb retreatments
	 * @return the indicator
	 */
	public CohortIndicator tbRetreatmentsPatients() {
		return cohortIndicator("TB re-treatment patients",
				map(tbCohorts.tbRetreatments(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients with Extra pulmonary Tb
	 * @return the indicator
	 */
	public CohortIndicator extraPulmonaryTbPatients() {
		return cohortIndicator("patients with extra pulmonary TB",
				map(tbCohorts.extraPulmonaryTbPatients(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of pulmonary TB patients with smear negative results
	 * @return the indicator
	 */
	public CohortIndicator pulmonaryTbSmearNegative() {
		return cohortIndicator("patients with pulmonary TB smear negative results",
				map(tbCohorts.pulmonaryTbSmearNegative(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of pulmonary TB patients with smear positive results
	 * @return the indicator
	 */
	public CohortIndicator pulmonaryTbSmearPositive() {
		return cohortIndicator("patients with pulmonary TB smear positive results",
				map(tbCohorts.pulmonaryTbSmearPositive(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Patients with new Tb detected cases
	 * @return the indicator
	 */
	public CohortIndicator tbNewDetectedCases() {
		return cohortIndicator("new TB cases detected",
				map(tbCohorts.tbNewDetectedCases(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}
}