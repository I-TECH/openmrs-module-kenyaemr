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


package org.openmrs.module.kenyaemr.reporting.library.shared.mchms;

import org.openmrs.module.kenyaemr.ArtAssessmentMethod;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.PregnancyStage;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of MCH-MS related indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
@Component
public class MchmsIndicatorLibrary {

	@Autowired
	private MchmsCohortLibrary mchmsCohortLibrary;

	/**
	 * Number of patients who tested for HIV in MCHMS during any {@link org.openmrs.module.kenyaemr.PregnancyStage} before or after enrollment
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedForHivBeforeOrDuringMchms() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedForHivInMchms(null, null), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested for HIV in MCHMS during any {@link org.openmrs.module.kenyaemr.PregnancyStage} after enrollment
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedForHivInMchms() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedForHivInMchms(PregnancyStage.AFTER_ENROLLMENT, null), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested for HIV in MCHMS during the ANTENATAL {@link org.openmrs.module.kenyaemr.PregnancyStage}
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedForHivInMchmsAntenatal() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedForHivInMchms(PregnancyStage.ANTENATAL, null), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested for HIV in MCHMS during the DELIVERY {@link org.openmrs.module.kenyaemr.PregnancyStage}
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedForHivInMchmsDelivery() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedForHivInMchms(PregnancyStage.DELIVERY, null), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested for HIV in MCHMS during the POSTNATAL {@link org.openmrs.module.kenyaemr.PregnancyStage}
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedForHivInMchmsPostnatal() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedForHivInMchms(PregnancyStage.POSTNATAL, null), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested HIV +ve before MCHMS
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedHivPositiveBeforeMchms() {

		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedForHivInMchms(PregnancyStage.BEFORE_ENROLLMENT, Dictionary.getConcept(Dictionary.POSITIVE)), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested HIV +ve in MCHMS during any {@link org.openmrs.module.kenyaemr.PregnancyStage}
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedHivPositiveInMchms() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedForHivInMchms(null, Dictionary.getConcept(Dictionary.POSITIVE)), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested HIV +ve in MCHMS during the ANTENATAL {@link org.openmrs.module.kenyaemr.PregnancyStage}
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedHivPositiveInMchmsAntenatal() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedForHivInMchms(PregnancyStage.ANTENATAL, Dictionary.getConcept(Dictionary.POSITIVE)), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested HIV +ve in MCHMS during the DELIVERY {@link org.openmrs.module.kenyaemr.PregnancyStage}
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedHivPositiveInMchmsDelivery() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedForHivInMchms(PregnancyStage.DELIVERY, Dictionary.getConcept(Dictionary.POSITIVE)), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested HIV +ve in MCHMS during the POSTNATAL
	 * {@link org.openmrs.module.kenyaemr.PregnancyStage}
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedHivPositiveInMchmsPostnatal() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedForHivInMchms(PregnancyStage.POSTNATAL, Dictionary.getConcept(Dictionary.POSITIVE)), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients whose partners tested HIV +ve or -ve in MCHMS during either their ANTENATAL or DELIVERY
	 * {@link org.openmrs.module.kenyaemr.PregnancyStage}
	 *
	 * @return the indicator
	 */
	public CohortIndicator partnerTestedDuringAncOrDelivery() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedDuringAncOrDelivery(Boolean.TRUE), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of MCHMS patients whose HIV status is discordant with that of their male partners
	 *
	 * @return the cohort definition
	 */

	public CohortIndicator discordantCouples() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.discordantCouples(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	public CohortIndicator assessedForArtEligibility(ArtAssessmentMethod artAssessmentMethod) {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.assessedForArtEligibility(artAssessmentMethod), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}
}
