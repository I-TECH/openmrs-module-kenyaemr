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

package org.openmrs.module.kenyaemr.reporting.library.indicator;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.library.cohort.CommonCohortLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.map;

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
		return createCohortIndicator("Number of new patients enrolled in " + program.getName() + " including transfers",
				EmrReportingUtils.map(commonCohorts.enrolledExcludingTransfers(program), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients enrolled in the given program (excluding transfers)
	 * @param program the program
	 * @return the indicator
	 */
	public CohortIndicator enrolledExcludingTransfers(Program program) {
		return createCohortIndicator("Number of new patients enrolled in " + program.getName() + " excluding transfers",
				EmrReportingUtils.map(commonCohorts.enrolledExcludingTransfers(program), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}

	/**
	 * Number of patients ever enrolled in the given program (including transfers) up to ${endDate}
	 * @param program the program
	 * @return the indicator
	 */
	public CohortIndicator enrolledCumulative(Program program) {
		return createCohortIndicator("Number of patients ever enrolled in " + program.getName() + " excluding transfers",
				EmrReportingUtils.map(commonCohorts.enrolled(program), "enrolledOnOrBefore=${endDate}"));
	}

	/**
	 * Number of patients on the specified medication
	 * @param concepts the drug concepts
	 * @return the indicator
	 */
	public CohortIndicator onMedication(Concept... concepts) {
		return createCohortIndicator("Number of patients on medication", EmrReportingUtils.map(commonCohorts.onMedication(concepts), "onDate=${endDate}"));
	}

	/**
	 * Utility method to create a new cohort indicator
	 * @param description the indicator description
	 * @return the cohort indicator
	 */
	public static CohortIndicator createCohortIndicator(String description, Mapped<CohortDefinition> mappedCohort) {
		CohortIndicator ind = new CohortIndicator(description);
		ind.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ind.addParameter(new Parameter("endDate", "End Date", Date.class));
		ind.setCohortDefinition(mappedCohort);
		return ind;
	}
}