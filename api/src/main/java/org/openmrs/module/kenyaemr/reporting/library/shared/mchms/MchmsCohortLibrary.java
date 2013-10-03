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

import org.openmrs.module.kenyacore.report.builder.CalculationCohortDefinition;
import org.openmrs.module.kenyaemr.calculation.library.mchms.TestedForHivInMchmsCalculation;
import org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * Library of MCH-MS related cohort definitions
 */
@Component
public class MchmsCohortLibrary {

	@Autowired
	private CommonCohortLibrary commonCohortLibrary;

	/**
	 * Patients who were enrolled in MCH-MS program (including transfers) between ${enrolledOnOrAfter} and ${enrolledOnOrBefore}
	 * and were tested for HIV in the MCH program
	 *
	 * @return the cohort definition
	 */
	public CohortDefinition testedForHivInMchms(Map<String, Object> calculationParameters) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition(new TestedForHivInMchmsCalculation());
		cd.setName("Mothers tested for HIV in the MCH program");
		cd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
		cd.setCalculationParameters(calculationParameters);
		return cd;
	}
}
