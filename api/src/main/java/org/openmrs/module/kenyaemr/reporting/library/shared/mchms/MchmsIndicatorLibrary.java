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

import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

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
	 * Number of patients who tested for HIV in MCHMS during any period before or after enrollment
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedForHivBeforeOrDuringMchms() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedAtStageWithResult(MchMetadata.Stage.ANY, null), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested for HIV in MCHMS during any period after enrollment
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedForHivInMchms() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedAtStageWithResult(MchMetadata.Stage.AFTER_ENROLLMENT, null), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested for HIV in MCHMS during the ANTENATAL period
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedForHivInMchmsAntenatal() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedAtStageWithResult(MchMetadata.Stage.ANTENATAL, null), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested for HIV in MCHMS during the DELIVERY period
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedForHivInMchmsDelivery() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedAtStageWithResult(MchMetadata.Stage.DELIVERY, null), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested for HIV in MCHMS during the POSTNATAL period
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedForHivInMchmsPostnatal() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedAtStageWithResult(MchMetadata.Stage.POSTNATAL, null), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested HIV +ve before MCHMS
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedHivPositiveBeforeMchms() {

		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedAtStageWithResult(MchMetadata.Stage.BEFORE_ENROLLMENT, Dictionary.getConcept(Dictionary.POSITIVE)), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested HIV +ve in MCHMS during any period
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedHivPositiveInMchms() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedAtStageWithResult(MchMetadata.Stage.ANY, Dictionary.getConcept(Dictionary.POSITIVE)), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested HIV +ve in MCHMS during the ANTENATAL period
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedHivPositiveInMchmsAntenatal() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedAtStageWithResult(MchMetadata.Stage.ANTENATAL, Dictionary.getConcept(Dictionary.POSITIVE)), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested HIV +ve in MCHMS during the DELIVERY period
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedHivPositiveInMchmsDelivery() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedAtStageWithResult(MchMetadata.Stage.DELIVERY, Dictionary.getConcept(Dictionary.POSITIVE)), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested HIV +ve in MCHMS during the POSTNATAL period
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedHivPositiveInMchmsPostnatal() {
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedAtStageWithResult(MchMetadata.Stage.POSTNATAL, Dictionary.getConcept(Dictionary.POSITIVE)), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}
}
