/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.shared.common;

import org.openmrs.Program;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of common indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
@Component
public class CommonIndicatorLibrary {

	@Autowired
	private CommonCohortLibrary commonCohorts;

	/**
	 * Number of patients enrolled in the given program (including transfers)
	 * @param program the program
	 * @return the indicator
	 */
	public CohortIndicator enrolled(Program program) {
		return cohortIndicator("new patients enrolled in " + program.getName() + " including transfers",
				ReportUtils.map(commonCohorts.enrolledExcludingTransfers(program), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients enrolled in the given program (excluding transfers)
	 * @param program the program
	 * @return the indicator
	 */
	public CohortIndicator enrolledExcludingTransfers(Program program) {
		return cohortIndicator("new patients enrolled in " + program.getName() + " excluding transfers",
				ReportUtils.map(commonCohorts.enrolledExcludingTransfers(program), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients ever enrolled in the given program (including transfers) up to ${endDate}
	 * @param program the program
	 * @return the indicator
	 */
	public CohortIndicator enrolledCumulative(Program program) {
		return cohortIndicator("total patients ever enrolled in " + program.getName() + " excluding transfers",
				ReportUtils.map(commonCohorts.enrolledExcludingTransfersOnDate(program), "onOrBefore=${endDate}"));
	}
}