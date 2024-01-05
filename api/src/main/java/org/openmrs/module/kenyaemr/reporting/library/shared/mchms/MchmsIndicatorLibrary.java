/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
				map(mchmsCohortLibrary.testedForHivBeforeOrDuringMchms(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested for HIV in MCHMS during any {@link org.openmrs.module.kenyaemr.PregnancyStage} after enrollment
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedForHivInMchms() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedForHivInMchms(), "onOrAfter=${startDate},onOrBefore=${endDate}")
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
				map(mchmsCohortLibrary.testedHivPositiveInMchms(), "onOrAfter=${startDate},onOrBefore=${endDate}")
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

	/**
	 *
	 */
	public CohortIndicator assessedForArtEligibilityTotal() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.assessedForArtEligibilityTotal(), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}
}
