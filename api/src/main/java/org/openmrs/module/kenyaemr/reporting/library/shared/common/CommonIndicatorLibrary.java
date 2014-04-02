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