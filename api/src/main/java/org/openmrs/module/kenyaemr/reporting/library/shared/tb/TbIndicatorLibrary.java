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

	/**
	 * Total enrolled patients into tb program and have ptb smear not done results at 2 months
	 * @return cohort indicator
	 */
	public CohortIndicator totalEnrolled() {
		return cohortIndicator("Total Enrolled",
				map(tbCohorts.totalEnrolledPtbSmearNotDoneResultsAtMonths(12, 8), "onOrAfter=${startDate},onOrBefore=${endDate}")
				);
	}

	/**
	 * Total patients who finalized their treatment
	 * @return Indicator
	 */
	public  CohortIndicator finalizedInitialTreatment() {
		return cohortIndicator("Finalized Initial Treatment",
				map(tbCohorts.ptbSmearNotDoneResults2MonthsFinalizedInitialtreatment(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total patients who died
	 * @return Indicator
	 */
	public  CohortIndicator died() {
		return cohortIndicator("Died",
				map(tbCohorts.ptbSmearNotDoneResults2MonthsDied(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total patients who died
	 * @return Indicator
	 */
	public  CohortIndicator absconded() {
		return cohortIndicator("Absconded",
				map(tbCohorts.ptbSmearNotDoneResults2MonthsAbsconded(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total patients who Transferred out
	 * @return Indicator
	 */
	public  CohortIndicator transferredOut() {
		return cohortIndicator("Transferred Out",
				map(tbCohorts.ptbSmearNotDoneResults2MonthsTransferredOut(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total patients evaluated
	 * @return Indicator
	 */
	public  CohortIndicator totalEvaluated() {
		return cohortIndicator("Total evaluated",
				map(tbCohorts.ptbSmearNotDoneResults2MonthsTotalEvaluated(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of patients enrollment
	 * hiv positive
	 * results 8 months
	 * 8-12 months earlier
	 * @return Indicator
	 */
	public CohortIndicator totalEnrolled8MonthsHivPositive() {
		return cohortIndicator("Total Enrolled 8 months HIV Positive",
				map(tbCohorts.totalEnrolled8MonthsHivPositive(), "onOrAfter=${startDate},onOrBefore=${endDate}")
				);
	}

	/**
	 * Total number of patients enrollment
	 * hiv negative
	 * results 8 months
	 * 8-12 months earlier
	 * @return Indicator
	 */
	public CohortIndicator totalEnrolled8MonthsHivNegative() {
		return cohortIndicator("Total Enrolled 8 months HIV Negative",
				map(tbCohorts.totalEnrolled8MonthsHivNegative(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of patients enrollment
	 * hiv test not done
	 * results 8 months
	 * 8-12 months earlier
	 * @return Indicator
	 */
	public CohortIndicator totalEnrolled8MonthsHivTestNotDone() {
		return cohortIndicator("Total Enrolled 8 months HIV test NOT done",
				map(tbCohorts.totalEnrolled8MonthsHivTestNotDone(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of patients enrollment
	 * hiv test not done, hiv+, hiv-
	 * results 8 months
	 * 8-12 months earlier
	 * @return Indicator
	 */
	public CohortIndicator totalEnrolled8MonthsHivPositiveNegativeTestNotDone() {
		return cohortIndicator("Total Enrolled 8 months HIV test NOT done hiv pos,neg",
				map(tbCohorts.totalEnrolled8MonthsHivPositiveNegativeTestNotDone(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of patients enrollment
	 * hiv positive
	 * on cpt
	 * results 8 months
	 * 8-12 months earlier
	 * @return Indicator
	 */
	public CohortIndicator totalEnrolled8MonthsHivPositiveOnCpt() {
		return cohortIndicator("Total Enrolled 8 months HIV Positive and on cpt",
				map(tbCohorts.totalEnrolled8MonthsHivPositiveOnCpt(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of patients enrollment
	 * hiv positive
	 * on art
	 * results 8 months
	 * 8-12 months earlier
	 * @return Indicator
	 */
	public CohortIndicator totalEnrolled8MonthsHivPositiveOnArt() {
		return cohortIndicator("Total Enrolled 8 months HIV Positive and on art",
				map(tbCohorts.totalEnrolled8MonthsHivPositiveOnArt(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Patients who finalized initial treatment
	 * Results at 8 months
	 * 8-12 months earlier
	 * HIV positive
	 * completed the treatment
	 * @return Indicator
	 */
	public CohortIndicator finalizedInitialTreatmentResults8monthsHivPositive() {
		return cohortIndicator("Total Enrolled 8 months HIV Positive and finalized initial treatment ",
				map(tbCohorts.finalizedInitialTreatmentResults8monthsHivPositive(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Patients who finalized initial treatment
	 * Results at 8 months
	 * 8-12 months earlier
	 * HIV positive
	 * died
	 * @return Indicator
	 */
	public CohortIndicator diedResults8monthsHivPositive() {
		return cohortIndicator("Total Enrolled 8 months HIV Positive and Died ",
				map(tbCohorts.diedResults8monthsHivPositive(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Patients who finalized initial treatment
	 * Results at 8 months
	 * 8-12 months earlier
	 * HIV positive
	 * absconded
	 * @return Indicator
	 */
	public CohortIndicator abscondedResults8monthsHivPositive() {
		return cohortIndicator("Total Enrolled 8 months HIV Positive and Absconded ",
				map(tbCohorts.abscondedResults8monthsHivPositive(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Patients who finalized initial treatment
	 * Results at 8 months
	 * 8-12 months earlier
	 * HIV positive
	 * Transferred out
	 * @return Indicator
	 */
	public CohortIndicator transferredOutResults8monthsHivPositive() {
		return cohortIndicator("Total Enrolled 8 months HIV Positive and Transferred Out ",
				map(tbCohorts.transferredOutResults8monthsHivPositive(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * * Patients who finalized initial treatment
	 * Results at 8 months
	 * 8-12 months earlier
	 * HIV positive
	 * Transferred out, absconded, died, finished initial treatment
	 * @return Indicator
	 */
	public CohortIndicator finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivPositive() {
		return cohortIndicator("Total Enrolled 8 months HIV Positive and Transferred out, absconded, died, finished initial treatment ",
				map(tbCohorts.finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivPositive(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * * Patients who finalized initial treatment
	 * Results at 8 months
	 * 8-12 months earlier
	 * HIV negative
	 * finished initial treatment
	 * @return Indicator
	 */
	public CohortIndicator finalizedInitialTreatmentTotalEnrolled8MonthsHivNegative() {
		return cohortIndicator("Total Enrolled 8 months HIV Negative and finished initial treatment ",
				map(tbCohorts.finalizedInitialTreatmentTotalEnrolled8MonthsHivNegative(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * * Patients who finalized initial treatment
	 * Results at 8 months
	 * 8-12 months earlier
	 * HIV negative
	 * died
	 * @return Indicator
	 */
	public CohortIndicator diedTotalEnrolled8MonthsHivNegative() {
		return cohortIndicator("Total Enrolled 8 months HIV Negative and died ",
				map(tbCohorts.diedTotalEnrolled8MonthsHivNegative(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * * Patients who finalized initial treatment
	 * Results at 8 months
	 * 8-12 months earlier
	 * HIV negative
	 * absconded
	 * @return Indicator
	 */
	public CohortIndicator abscondedTotalEnrolled8MonthsHivNegative() {
		return cohortIndicator("Total Enrolled 8 months HIV Negative and absconded ",
				map(tbCohorts.abscondedTotalEnrolled8MonthsHivNegative(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * * Patients who finalized initial treatment
	 * Results at 8 months
	 * 8-12 months earlier
	 * HIV negative
	 * Transferred out
	 * @return Indicator
	 */
	public CohortIndicator transferredOutTotalEnrolled8MonthsHivNegative() {
		return cohortIndicator("Total Enrolled 8 months HIV Negative and transferred out ",
				map(tbCohorts.transferredOutTotalEnrolled8MonthsHivNegative(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * * Patients who finalized initial treatment
	 * Results at 8 months
	 * 8-12 months earlier
	 * HIV negative
	 * Transferred out, absconded, died, finished initial treatment
	 * @return Indicator
	 */
	public CohortIndicator finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivNegative() {
		return cohortIndicator("Total Enrolled 8 months HIV Negative and Transferred out, absconded, died, finished initial treatment ",
				map(tbCohorts.finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivNegative(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of patients enrollment
	 * hiv test not done
	 * results 8 months
	 * 8-12 months earlier
	 * completed initial treatment
	 * @return Indicator
	 */
	public CohortIndicator finishedInitialTreatmentTotalEnrolled8MonthsHivTestNotDone() {
		return cohortIndicator("Total Enrolled 8 months HIV test NOT done and finished initial treatment",
				map(tbCohorts.finalizedInitialTreatmentTotalEnrolled8MonthsHivTestNotDone(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of patients enrollment
	 * hiv test not done
	 * results 8 months
	 * 8-12 months earlier
	 * died
	 * @return Indicator
	 */
	public CohortIndicator diedTotalEnrolled8MonthsHivTestNotDone() {
		return cohortIndicator("Total Enrolled 8 months HIV test NOT done and died",
				map(tbCohorts.diedTotalEnrolled8MonthsHivTestNotDone(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of patients enrollment
	 * hiv test not done
	 * results 8 months
	 * 8-12 months earlier
	 * absconded
	 * @return Indicator
	 */
	public CohortIndicator abscondedTotalEnrolled8MonthsHivTestNotDone() {
		return cohortIndicator("Total Enrolled 8 months HIV test NOT done and absconded",
				map(tbCohorts.abscondedTotalEnrolled8MonthsHivTestNotDone(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of patients enrollment
	 * hiv test not done
	 * results 8 months
	 * 8-12 months earlier
	 * transferred out
	 * @return Indicator
	 */
	public CohortIndicator transferredOutTotalEnrolled8MonthsHivTestNotDone() {
		return cohortIndicator("Total Enrolled 8 months HIV test NOT done and transferred out",
				map(tbCohorts.transferredOutTotalEnrolled8MonthsHivTestNotDone(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * * Patients who finalized initial treatment
	 * Results at 8 months
	 * 8-12 months earlier
	 * HIV test not done
	 * Transferred out, absconded, died, finished initial treatment
	 * @return Indicator
	 */
	public CohortIndicator finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivTestNotDone() {
		return cohortIndicator("Total Enrolled 8 months HIV test not done and Transferred out, absconded, died, finished initial treatment ",
				map(tbCohorts.finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivTestNotDone(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of patients enrollment
	 * hiv test not done, hiv+, hiv-
	 * completed initial treatment
	 * results 8 months
	 * 8-12 months earlier
	 * @return Indicator
	 */
	public CohortIndicator finalizedInitialTreatmentTotalEnrolled8MonthsHivPositiveNegativeTestNotDone() {
		return cohortIndicator("Total Enrolled 8 months HIV test total and hiv pos,neg completed treatment",
				map(tbCohorts.finalizedInitialTreatmentTotalEnrolled8MonthsHivPositiveNegativeTestNotDone(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of patients enrollment
	 * hiv test not done, hiv+, hiv-
	 * died
	 * results 8 months
	 * 8-12 months earlier
	 * @return Indicator
	 */
	public CohortIndicator diedTotalEnrolled8MonthsHivPositiveNegativeTestNotDone() {
		return cohortIndicator("Total Enrolled 8 months HIV test total and hiv pos,neg and died",
				map(tbCohorts.diedTotalEnrolled8MonthsHivPositiveNegativeTestNotDone(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of patients enrollment
	 * hiv test not done, hiv+, hiv-
	 * absconded
	 * results 8 months
	 * 8-12 months earlier
	 * @return Indicator
	 */
	public CohortIndicator abscondedTotalEnrolled8MonthsHivPositiveNegativeTestNotDone() {
		return cohortIndicator("Total Enrolled 8 months HIV test total and hiv pos,neg and absconded",
				map(tbCohorts.abscondedTotalEnrolled8MonthsHivPositiveNegativeTestNotDone(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of patients enrollment
	 * hiv test not done, hiv+, hiv-
	 * transferred out
	 * results 8 months
	 * 8-12 months earlier
	 * @return Indicator
	 */
	public CohortIndicator transferredOutTotalEnrolled8MonthsHivPositiveNegativeTestNotDone() {
		return cohortIndicator("Total Enrolled 8 months HIV test total and hiv pos,neg and transferred out",
				map(tbCohorts.transferredOutTotalEnrolled8MonthsHivPositiveNegativeTestNotDone(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * * Patients who finalized initial treatment
	 * Results at 8 months
	 * 8-12 months earlier
	 * HIV test totals
	 * Transferred out, absconded, died, finished initial treatment
	 * @return Indicator
	 */
	public CohortIndicator finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivPositiveNegativeTestNotDone() {
		return cohortIndicator("Total Enrolled 8 months HIV test total and Transferred out, absconded, died, finished initial treatment ",
				map(tbCohorts.finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivPositiveNegativeTestNotDone(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of patients enrollment
	 * hiv positive
	 * on cpt
	 * results 8 months
	 * 8-12 months earlier
	 * finalized initial treatment
	 * @return Indicator
	 */
	public CohortIndicator finalizeInitialTreatmentTotalEnrolled8MonthsHivPositiveOnCpt() {
		return cohortIndicator("Total Enrolled 8 months HIV Positive and on cpt and finalized initial treatment",
				map(tbCohorts.finalizedInitialTreatmentTotalEnrolled8MonthsHivPositiveOnCpt(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of patients enrollment
	 * hiv positive
	 * on cpt
	 * results 8 months
	 * 8-12 months earlier
	 * died
	 * @return Indicator
	 */
	public CohortIndicator diedTotalEnrolled8MonthsHivPositiveOnCpt() {
		return cohortIndicator("Total Enrolled 8 months HIV Positive and on cpt and died",
				map(tbCohorts.diedTotalEnrolled8MonthsHivPositiveOnCpt(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of patients enrollment
	 * hiv positive
	 * on cpt
	 * results 8 months
	 * 8-12 months earlier
	 * absconded
	 * @return Indicator
	 */
	public CohortIndicator abscondedTotalEnrolled8MonthsHivPositiveOnCpt() {
		return cohortIndicator("Total Enrolled 8 months HIV Positive and on cpt and absconded",
				map(tbCohorts.abscondedTotalEnrolled8MonthsHivPositiveOnCpt(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of patients enrollment
	 * hiv positive
	 * on cpt
	 * results 8 months
	 * 8-12 months earlier
	 * transferred out
	 * @return Indicator
	 */
	public CohortIndicator transferredOutTotalEnrolled8MonthsHivPositiveOnCpt() {
		return cohortIndicator("Total Enrolled 8 months HIV Positive and on cpt and transfer out",
				map(tbCohorts.transferredOutTotalEnrolled8MonthsHivPositiveOnCpt(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * * Patients who finalized initial treatment
	 * Results at 8 months
	 * 8-12 months earlier
	 * HIV positive
	 * Transferred out, absconded, died, finished initial treatment
	 * @return Indicator
	 */
	public CohortIndicator finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivPositiveOnCpt() {
		return cohortIndicator("Total Enrolled 8 months HIV Positive and on cpt and Transferred out, absconded, died, finished initial treatment ",
				map(tbCohorts.finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivPositiveOnCpt(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of patients enrollment
	 * hiv positive
	 * on art
	 * results 8 months
	 * 8-12 months earlier
	 * finalized initial treatment
	 * @return Indicator
	 */
	public CohortIndicator finalizedInitialTreatmentTotalEnrolled8MonthsHivPositiveOnArt() {
		return cohortIndicator("Total Enrolled 8 months HIV Positive and on art and finalized initial treatment",
				map(tbCohorts.finalizedInitialTreatmentTotalEnrolled8MonthsHivPositiveOnArt(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of patients enrollment
	 * hiv positive
	 * on art
	 * results 8 months
	 * 8-12 months earlier
	 * died
	 * @return Indicator
	 */
	public CohortIndicator diedTotalEnrolled8MonthsHivPositiveOnArt() {
		return cohortIndicator("Total Enrolled 8 months HIV Positive and on art and died",
				map(tbCohorts.diedTotalEnrolled8MonthsHivPositiveOnArt(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of patients enrollment
	 * hiv positive
	 * on art
	 * results 8 months
	 * 8-12 months earlier
	 * absconded
	 * @return Indicator
	 */
	public CohortIndicator abscondedTotalEnrolled8MonthsHivPositiveOnArt() {
		return cohortIndicator("Total Enrolled 8 months HIV Positive and on art and absconded",
				map(tbCohorts.abscondedTotalEnrolled8MonthsHivPositiveOnArt(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Total number of patients enrollment
	 * hiv positive
	 * on art
	 * results 8 months
	 * 8-12 months earlier
	 * absconded
	 * @return Indicator
	 */
	public CohortIndicator transferredOutTotalEnrolled8MonthsHivPositiveOnArt() {
		return cohortIndicator("Total Enrolled 8 months HIV Positive and on art and transferred out",
				map(tbCohorts.transferOutTotalEnrolled8MonthsHivPositiveOnArt(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Patients who finalized initial treatment
	 * Results at 8 months
	 * 8-12 months earlier
	 * HIV positive
	 * Transferred out, absconded, died, finished initial treatment
	 * on art
	 * @return Indicator
	 */
	public CohortIndicator finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivPositiveOnArt() {
		return cohortIndicator("Total Enrolled 8 months HIV Positive and on art and Transferred out, absconded, died, finished initial treatment ",
				map(tbCohorts.finalizedInitialTreatmentDiedAbscondedTransferredOutResults8monthsHivPositiveOnArt(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Treatment of new sputum smear negative pulmonary
	 * patients registered 12 to 15 months earlier
	 * pulmonary tb
	 * results at 2 months
	 * @return Indicator
	 */
	public CohortIndicator newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months() {
		return cohortIndicator("Total Enrolled 2 months results new smear negative PTB ",
				map(tbCohorts.newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months(15, 12), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Treatment of new sputum smear negative pulmonary
	 * patients registered 12 to 15 months earlier
	 * pulmonary tb
	 * results at 2 months
	 * finalized treatment
	 * @return Indicator
	 */
	public CohortIndicator finalizedInitialTreatmentNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months() {
		return cohortIndicator("Total Enrolled 2 months results new smear negative PTB - Finalized Initial Treatment ",
				map(tbCohorts.finalizedInitialTreatmentNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Treatment of new sputum smear negative pulmonary
	 * patients registered 12 to 15 months earlier
	 * pulmonary tb
	 * results at 2 months
	 * died
	 * @return Indicator
	 */
	public CohortIndicator diedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months() {
		return cohortIndicator("Total Enrolled 2 months results new smear negative PTB - Died",
				map(tbCohorts.diedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Treatment of new sputum smear negative pulmonary
	 * patients registered 12 to 15 months earlier
	 * pulmonary tb
	 * results at 2 months
	 * absconded
	 * @return Indicator
	 */
	public CohortIndicator abscondedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months() {
		return cohortIndicator("Total Enrolled 2 months results new smear negative PTB - Absconded",
				map(tbCohorts.abscondedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Treatment of new sputum smear negative pulmonary
	 * patients registered 12 to 15 months earlier
	 * pulmonary tb
	 * results at 2 months
	 * transferred out
	 * @return Indicator
	 */
	public CohortIndicator transferredOutNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months() {
		return cohortIndicator("Total Enrolled 2 months results new smear negative PTB - Transferred Out",
				map(tbCohorts.transferOutNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Treatment of new sputum smear negative pulmonary
	 * patients registered 12 to 15 months earlier
	 * pulmonary tb
	 * results at 2 months
	 * transferred out, absconded, died, finalized initial treatment
	 * @return Indicator
	 */
	public CohortIndicator transferOutAbscondedDiedFinalizedInitialTreatmentNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months() {
		return cohortIndicator("Total Enrolled 2 months results new smear negative PTB - All Outcomes",
				map(tbCohorts.transferOutAbscondedDiedFinalizedInitialTreatmentNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults2Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Treatment of new sputum smear negative pulmonary
	 * patients registered 12 to 15 months earlier
	 * pulmonary tb
	 * results at 8 months
	 * @return Indicator
	 */
	public CohortIndicator newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months() {
		return cohortIndicator("Total Enrolled 8 months results new smear negative PTB ",
				map(tbCohorts.newSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(15, 12), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Treatment of new sputum smear negative pulmonary
	 * patients registered 12 to 15 months earlier
	 * pulmonary tb
	 * results at 8 months
	 * treatment complete
	 * @return Indicator
	 */
	public CohortIndicator treatmentCompletedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months() {
		return cohortIndicator("Total Enrolled 8 months results new smear negative PTB - Treatment Complete ",
				map(tbCohorts.treatmentCompletedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Treatment of new sputum smear negative pulmonary
	 * patients registered 12 to 15 months earlier
	 * pulmonary tb
	 * results at 8 months
	 * died
	 * @return Indicator
	 */
	public CohortIndicator diedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months() {
		return cohortIndicator("Total Enrolled 8 months results new smear negative PTB - Died ",
				map(tbCohorts.diedNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Treatment of new sputum smear negative pulmonary
	 * patients registered 12 to 15 months earlier
	 * pulmonary tb
	 * results at 8 months
	 * out of control
	 * @return Indicator
	 */
	public CohortIndicator outOfControlNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months() {
		return cohortIndicator("Total Enrolled 8 months results new smear negative PTB - Out Of Control ",
				map(tbCohorts.outOfControlNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Treatment of new sputum smear negative pulmonary
	 * patients registered 12 to 15 months earlier
	 * pulmonary tb
	 * results at 8 months
	 * transferred out
	 * @return Indicator
	 */
	public CohortIndicator transferOutNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months() {
		return cohortIndicator("Total Enrolled 8 months results new smear negative PTB - Transferred Out ",
				map(tbCohorts.transferOutNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Treatment of new sputum smear negative pulmonary
	 * patients registered 12 to 15 months earlier
	 * pulmonary tb
	 * results at 8 months
	 * became smear positive
	 * @return Indicator
	 */
	public CohortIndicator becameSmearPositiveNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months() {
		return cohortIndicator("Total Enrolled 8 months results new smear negative PTB - Transferred Out ",
				map(tbCohorts.becameSmearPositiveNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Treatment of new sputum smear negative pulmonary
	 * patients registered 12 to 15 months earlier
	 * pulmonary tb
	 * results at 8 months
	 * became smear positive,transferred out,out of control,treatment complete, died
	 * @return Indicator
	 */
	public CohortIndicator transferOutOutOfControlDiedCompletedTreatmentNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months() {
		return cohortIndicator("Total Enrolled 8 months results new smear negative PTB - Outcomes all ",
				map(tbCohorts.transferOutOutOfControlDiedCompletedTreatmentNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}


	/**
	 * patients registered 12 to 15 months earlier
	 * extra pulmonary TB
	 * results at 2 months
	 * @return Indicator
	 */
	public CohortIndicator extraPulmonaryTbResultsAt2Months() {
		return cohortIndicator("Total Enrolled 2 months results Extra-Pulmonary TB ",
				map(tbCohorts.extraPulmonaryTbResultsAt2Months(15, 12), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * patients registered 12 to 15 months earlier
	 * extra pulmonary TB
	 * results at 2 months
	 * finalized initial treatment
	 * @return Indicator
	 */
	public CohortIndicator finalizedInitialTreatmentExtraPulmonaryTbResultsAt2Months() {
		return cohortIndicator("Total Enrolled 2 months results Extra-Pulmonary TB - Finalized Initial Treatment ",
				map(tbCohorts.finalizedInitialTreatmentExtraPulmonaryTbResultsAt2Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * patients registered 12 to 15 months earlier
	 * extra pulmonary TB
	 * results at 2 months
	 * died
	 * @return Indicator
	 */
	public CohortIndicator diedExtraPulmonaryTbResultsAt2Months() {
		return cohortIndicator("Total Enrolled 2 months results Extra-Pulmonary TB - Died ",
				map(tbCohorts.diedExtraPulmonaryTbResultsAt2Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * patients registered 12 to 15 months earlier
	 * extra pulmonary TB
	 * results at 2 months
	 * absconded
	 * @return Indicator
	 */
	public CohortIndicator abscondedExtraPulmonaryTbResultsAt2Months() {
		return cohortIndicator("Total Enrolled 2 months results Extra-Pulmonary TB - Absconded ",
				map(tbCohorts.abscondedExtraPulmonaryTbResultsAt2Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * patients registered 12 to 15 months earlier
	 * extra pulmonary TB
	 * results at 2 months
	 * absconded
	 * @return Indicator
	 */
	public CohortIndicator transferredOutExtraPulmonaryTbResultsAt2Months() {
		return cohortIndicator("Total Enrolled 2 months results Extra-Pulmonary TB - Transferred Out ",
				map(tbCohorts.transferredOutExtraPulmonaryTbResultsAt2Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * patients registered 12 to 15 months earlier
	 * extra pulmonary TB
	 * results at 2 months
	 * all outcomes
	 * @return Indicator
	 */
	public CohortIndicator transferOutAbscondedDiedCompletedTreatmentNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months() {
		return cohortIndicator("Total Enrolled 2 months results Extra-Pulmonary TB - Outcomes all ",
				map(tbCohorts.transferOutAbscondedDiedCompletedTreatmentNewSputumSmearNegative12to15MonthsEarlierPulmonaryTbResults8Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * patients registered 12 to 15 months earlier
	 * extra pulmonary TB
	 * results at 8 months
	 * @return Indicator
	 */
	public CohortIndicator extraPulmonaryTbResultsAt8Months() {
		return cohortIndicator("Total Enrolled 8 months results Extra-Pulmonary TB ",
				map(tbCohorts.extraPulmonaryTbResultsAt8Months(15, 12), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * patients registered 12 to 15 months earlier
	 * extra pulmonary TB
	 * results at 8 months
	 * treatment completed
	 * @return Indicator
	 */
	public CohortIndicator treatmentCompletedExtraPulmonaryTbResultsAt8Months() {
		return cohortIndicator("Total Enrolled 8 months results Extra-Pulmonary TB - Treatment Complete ",
				map(tbCohorts.treatmentCompleteExtraPulmonaryTbResultsAt8Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * patients registered 12 to 15 months earlier
	 * extra pulmonary TB
	 * results at 8 months
	 * died
	 * @return Indicator
	 */
	public CohortIndicator diedExtraPulmonaryTbResultsAt8Months() {
		return cohortIndicator("Total Enrolled 8 months results Extra-Pulmonary TB - Died",
				map(tbCohorts.diedExtraPulmonaryTbResultsAt8Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * patients registered 12 to 15 months earlier
	 * extra pulmonary TB
	 * results at 8 months
	 * out of control
	 * @return Indicator
	 */
	public CohortIndicator outOfControlExtraPulmonaryTbResultsAt8Months() {
		return cohortIndicator("Total Enrolled 8 months results Extra-Pulmonary TB - Out of Control",
				map(tbCohorts.outOfControlExtraPulmonaryTbResultsAt8Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * patients registered 12 to 15 months earlier
	 * extra pulmonary TB
	 * results at 8 months
	 * transferred out
	 * @return Indicator
	 */
	public CohortIndicator transferredOutExtraPulmonaryTbResultsAt8Months() {
		return cohortIndicator("Total Enrolled 8 months results Extra-Pulmonary TB - Transferred Out",
				map(tbCohorts.transferredOutExtraPulmonaryTbResultsAt8Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * patients registered 12 to 15 months earlier
	 * extra pulmonary TB
	 * results at 8 months
	 * outcomes all
	 * @return Indicator
	 */
	public CohortIndicator transferOutOutOfControlDiedCompletedTreatmentExtraPulmonaryTbResultsAt8Months() {
		return cohortIndicator("Total Enrolled 8 months results Extra-Pulmonary TB - Outcomes all",
				map(tbCohorts.transferOutOutOfControlDiedCompletedTreatmentExtraPulmonaryTbResultsAt8Months(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

}