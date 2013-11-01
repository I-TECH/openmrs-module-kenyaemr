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

import org.openmrs.Concept;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.CalculationCohortDefinition;
import org.openmrs.module.kenyaemr.calculation.library.mchms.MchmsHivTestDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.mchms.TestedForHivInMchmsCalculation;
import org.openmrs.module.kenyaemr.PregnancyStage;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.DateCalculationCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Library of MCH-MS related cohort definitions
 */
@Component
public class MchmsCohortLibrary {

	public CohortDefinition testedForHivInMchms(PregnancyStage stage, Concept result) {

		DateCalculationCohortDefinition dateCd = new DateCalculationCohortDefinition(new MchmsHivTestDateCalculation());
		dateCd.setName("started ART between dates");
		dateCd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		dateCd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));

		CalculationCohortDefinition calculationCd = new CalculationCohortDefinition(new TestedForHivInMchmsCalculation());
		calculationCd.setName("Mothers tested for HIV in the MCH program");
		calculationCd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
		calculationCd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));

		if (stage != null) {
			calculationCd.addCalculationParameter("stage", stage);
		}
		if (result != null) {
			calculationCd.addCalculationParameter("result", result);
		}

		CompositionCohortDefinition cohortCd = new CompositionCohortDefinition();
		cohortCd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
		cohortCd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
		cohortCd.addSearch("testedForHivWithinPeriod", ReportUtils.map((CohortDefinition) dateCd, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cohortCd.addSearch("testedForHivInMchms", ReportUtils.map((CohortDefinition) calculationCd, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}"));
		cohortCd.setCompositionString("testedForHivWithinPeriod AND testedForHivInMchms");
		return cohortCd;
	}
}