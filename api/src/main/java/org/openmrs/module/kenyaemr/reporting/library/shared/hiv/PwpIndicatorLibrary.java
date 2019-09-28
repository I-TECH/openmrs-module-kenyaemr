/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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