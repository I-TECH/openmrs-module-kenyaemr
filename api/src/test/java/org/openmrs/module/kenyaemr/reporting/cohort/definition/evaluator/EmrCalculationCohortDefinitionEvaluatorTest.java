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

package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnArtCalculation;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.EmrCalculationCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.EmrDateCalculationCohortDefinition;
import org.openmrs.module.kenyaemr.test.ReportingTestUtils;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator.EmrCalculationCohortDefinitionEvaluator}
 */
public class EmrCalculationCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

	private EvaluationContext context;

	private EmrCalculationCohortDefinitionEvaluator evaluator;

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");

		Concept efv = Context.getConceptService().getConcept(75523);
		Concept nvp = Context.getConceptService().getConcept(80586);

		// Give patient #6 drug order of EFV starting Dec 31st 2011
		TestUtils.saveDrugOrder(Context.getPatientService().getPatient(6), efv, TestUtils.date(2011, 12, 31), null);

		// Give patient #7 drug order of NVP starting Jan 1st
		TestUtils.saveDrugOrder(Context.getPatientService().getPatient(7), nvp, TestUtils.date(2012, 1, 1), null);

		context = ReportingTestUtils.reportingContext(Arrays.asList(2, 6, 7, 8, 999), TestUtils.date(2012, 1, 1), TestUtils.date(2012, 1, 31));
		evaluator = new EmrCalculationCohortDefinitionEvaluator();
	}

	/**
	 * Tests evaluation of the onArt calculation
	 */
	@Test
	public void evaluate_regularCalculation() throws EvaluationException {
	 	EmrCalculationCohortDefinition cohortDefinition = new EmrCalculationCohortDefinition(new OnArtCalculation());

		EvaluatedCohort evaluated = evaluator.evaluate(cohortDefinition, context);

		ReportingTestUtils.assertCohortEquals(Arrays.asList(6, 7), evaluated);
	}

	/**
	 * Tests evaluation of the initialArtStartDate calculation
	 */
	@Test
	public void evaluate_dateCalculation() throws EvaluationException {
		EmrDateCalculationCohortDefinition cohortDefinition = new EmrDateCalculationCohortDefinition(new InitialArtStartDateCalculation());
		cohortDefinition.setOnOrAfter(TestUtils.date(2012, 1, 1));
		cohortDefinition.setOnOrBefore(TestUtils.date(2012, 12, 31));

		EvaluatedCohort evaluated = evaluator.evaluate(cohortDefinition, context);

		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), evaluated);
	}
}
