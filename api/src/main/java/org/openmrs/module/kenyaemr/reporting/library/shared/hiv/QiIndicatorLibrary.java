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

import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art.ArtCohortLibrary;
import org.openmrs.module.reporting.indicator.Indicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of Quality Improvement indicators for HIV care. All indicators require parameter ${endDate}
 */
@Component
public class QiIndicatorLibrary {

	@Autowired
	private HivCohortLibrary hivCohorts;

	@Autowired
	private ArtCohortLibrary artCohorts;

	@Autowired
	private QiCohortLibrary qiCohorts;

	/**
	 * Percentage of patients with an HIV care visit who have CD4 test results in the last 6 months
	 * @return the indicator
	 */
	public Indicator hivMonitoringCd4() {
		return cohortIndicator("HIV monitoring - CD4",
				map(hivCohorts.hasCd4Result(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}"),
				map(hivCohorts.hasHivVisit(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}")
		);
	}
}