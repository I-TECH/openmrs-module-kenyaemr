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
	 * Number of patients who tested for HIV in MCHMS during any period
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedForHivInMchms() {
		Map<String, Object> calculationParameters = new HashMap<String, Object>();
		calculationParameters.put("stage", MchMetadata.Stage.ANY);
		calculationParameters.put("result", null);
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedForHivInMchms(calculationParameters), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested for HIV in MCHMS during the ANTENATAL period
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedForHivInMchmsAntenatal() {
		Map<String, Object> calculationParameters = new HashMap<String, Object>();
		calculationParameters.put("stage", MchMetadata.Stage.ANTENATAL);
		calculationParameters.put("result", null);
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedForHivInMchms(calculationParameters), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested for HIV in MCHMS during the DELIVERY period
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedForHivInMchmsDelivery() {
		Map<String, Object> calculationParameters = new HashMap<String, Object>();
		calculationParameters.put("stage", MchMetadata.Stage.DELIVERY);
		calculationParameters.put("result", null);
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedForHivInMchms(calculationParameters), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested for HIV in MCHMS during the POSTNATAL period
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedForHivInMchmsPostnatal() {
		Map<String, Object> calculationParameters = new HashMap<String, Object>();
		calculationParameters.put("stage", MchMetadata.Stage.POSTNATAL);
		calculationParameters.put("result", null);
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedForHivInMchms(calculationParameters), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested HIV +ve in MCHMS during any period
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedHivPositiveInMchms() {
		Map<String, Object> calculationParameters = new HashMap<String, Object>();
		calculationParameters.put("stage", MchMetadata.Stage.ANY);
		calculationParameters.put("result", Dictionary.getConcept(Dictionary.POSITIVE));
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedForHivInMchms(calculationParameters), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested HIV +ve in MCHMS during the ANTENATAL period
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedHivPositiveInMchmsAntenatal() {
		Map<String, Object> calculationParameters = new HashMap<String, Object>();
		calculationParameters.put("stage", MchMetadata.Stage.ANTENATAL);
		calculationParameters.put("result", Dictionary.getConcept(Dictionary.POSITIVE));
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedForHivInMchms(calculationParameters), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested HIV +ve in MCHMS during the DELIVERY period
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedHivPositiveInMchmsDelivery() {
		Map<String, Object> calculationParameters = new HashMap<String, Object>();
		calculationParameters.put("stage", MchMetadata.Stage.DELIVERY);
		calculationParameters.put("result", Dictionary.getConcept(Dictionary.POSITIVE));
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedForHivInMchms(calculationParameters), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}

	/**
	 * Number of patients who tested HIV +ve in MCHMS during the POSTNATAL period
	 *
	 * @return the indicator
	 */
	public CohortIndicator testedHivPositiveInMchmsPostnatal() {
		Map<String, Object> calculationParameters = new HashMap<String, Object>();
		calculationParameters.put("stage", MchMetadata.Stage.POSTNATAL);
		calculationParameters.put("result", Dictionary.getConcept(Dictionary.POSITIVE));
		return cohortIndicator(null,
				map(mchmsCohortLibrary.testedForHivInMchms(calculationParameters), "onOrAfter=${startDate},onOrBefore=${endDate}")
		);
	}
}
