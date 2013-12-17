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

import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of PwP related indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
@Component
public class PwpIndicatorLibrary {

	@Autowired
	private PwpCohortLibrary pwpCohorts;

	/**
	 * Number of patients provided with condoms
	 * @return the indicator
	 */
	public CohortIndicator condomsProvided() {
		return cohortIndicator("patients provided with condoms", map(pwpCohorts.condomsProvided(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients provided with modern contraceptives
	 * @return the indicator
	 */
	public CohortIndicator modernContraceptivesProvided() {
		return cohortIndicator("patients provided with modern contraceptives", map(pwpCohorts.modernContraceptivesProvided(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}
}