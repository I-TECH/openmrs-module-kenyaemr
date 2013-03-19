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

package org.openmrs.module.kenyaemr.report;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.calculation.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.art.OnArtCalculation;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Date;

public class EmrCalculationCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");

		Concept efv = Context.getConceptService().getConcept(75523);
		Concept nvp = Context.getConceptService().getConcept(80586);

		// Give patient #6 drug order of EFV starting 2011-1-1
		TestUtils.saveDrugOrder(Context.getPatientService().getPatient(6), efv, TestUtils.date(2011, 6, 1), null);

		// Give patient #7 drug order of NVP starting 2012-1-1
		TestUtils.saveDrugOrder(Context.getPatientService().getPatient(7), nvp, TestUtils.date(2012, 6, 1), null);
	}

	/**
	 * Tests evaluation of the OnArt calculation
	 */
	@Test
	public void evaluate_regularCalculation() throws EvaluationException {
	 	EmrCalculationCohortDefinition cohortDefinition = new EmrCalculationCohortDefinition(new OnArtCalculation());

		EmrCalculationCohortDefinitionEvaluator evaluator = new EmrCalculationCohortDefinitionEvaluator();
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("date", new Date());
		context.setBaseCohort(new Cohort(Context.getPatientService().getAllPatients()));

		EvaluatedCohort evaluatedCohort = evaluator.evaluate(cohortDefinition, context);

		Assert.assertEquals(2, evaluatedCohort.getSize());
		Assert.assertTrue(evaluatedCohort.contains(6));
		Assert.assertTrue(evaluatedCohort.contains(7));
	}

	/**
	 * Tests evaluation of the initialArtStartDate calculation
	 */
	@Test
	public void evaluate_dateCalculation() throws EvaluationException {
		EmrDateCalculationCohortDefinition cohortDefinition = new EmrDateCalculationCohortDefinition(new InitialArtStartDateCalculation());
		cohortDefinition.setResultOnOrAfter(TestUtils.date(2012, 1, 1));
		cohortDefinition.setResultOnOrBefore(TestUtils.date(2012, 12, 31));

		EmrCalculationCohortDefinitionEvaluator evaluator = new EmrCalculationCohortDefinitionEvaluator();
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("date", new Date());
		context.setBaseCohort(new Cohort(Context.getPatientService().getAllPatients()));

		EvaluatedCohort evaluatedCohort = evaluator.evaluate(cohortDefinition, context);

		Assert.assertEquals(1, evaluatedCohort.getSize());
		Assert.assertTrue(evaluatedCohort.contains(7));
	}
}
