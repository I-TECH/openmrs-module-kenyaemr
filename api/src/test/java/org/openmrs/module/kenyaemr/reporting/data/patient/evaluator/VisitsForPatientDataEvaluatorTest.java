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

package org.openmrs.module.kenyaemr.reporting.data.patient.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.reporting.data.patient.definition.VisitsForPatientDataDefinition;
import org.openmrs.module.kenyaemr.test.ReportingTestUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link VisitsForPatientDataEvaluator}
 */
public class VisitsForPatientDataEvaluatorTest extends BaseModuleContextSensitiveTest {

	private EvaluationContext context;

	private VisitsForPatientDataEvaluator evaluator;

	@Autowired
	private CommonMetadata commonMetadata;

	private Visit visitP7a, visitP7b;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() {
		commonMetadata.install();

		VisitType outpatient = MetadataUtils.getVisitType(CommonMetadata._VisitType.OUTPATIENT);

		visitP7a = TestUtils.saveVisit(TestUtils.getPatient(7), outpatient, TestUtils.date(2012, 1, 1), TestUtils.date(2012, 1, 2));
		visitP7b = TestUtils.saveVisit(TestUtils.getPatient(7), outpatient, TestUtils.date(2012, 2, 1), TestUtils.date(2012, 2, 2));

		context = ReportingTestUtils.reportingContext(Arrays.asList(2, 6, 7, 8, 999), TestUtils.date(2012, 1, 1), TestUtils.date(2012, 1, 31));
		evaluator = new VisitsForPatientDataEvaluator();
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.data.patient.evaluator.VisitsForPatientDataEvaluator#evaluate(org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_any() throws EvaluationException {
		VisitsForPatientDataDefinition def = new VisitsForPatientDataDefinition();
		def.setWhich(TimeQualifier.ANY);
		EvaluatedPatientData data = evaluator.evaluate(def, context);
		Assert.assertThat((List<Visit>) data.getData().get(7), contains(visitP7a, visitP7b));
		Assert.assertThat(data.getData().get(8), nullValue());

		// Check with after date
		def.setStartedOnOrAfter(TestUtils.date(2012, 1, 2));
		def.setStartedOnOrBefore(null);

		data = evaluator.evaluate(def, context);
		Assert.assertThat((List<Visit>) data.getData().get(7), contains(visitP7b));

		// Check with before date
		def.setStartedOnOrAfter(null);
		def.setStartedOnOrBefore(TestUtils.date(2012, 1, 31));

		data = evaluator.evaluate(def, context);
		Assert.assertThat((List<Visit>) data.getData().get(7), contains(visitP7a));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.data.patient.evaluator.VisitsForPatientDataEvaluator#evaluate(org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_first() throws EvaluationException {
		VisitsForPatientDataDefinition def = new VisitsForPatientDataDefinition();
		def.setWhich(TimeQualifier.FIRST);
		EvaluatedPatientData data = evaluator.evaluate(def, context);
		Assert.assertThat((Visit) data.getData().get(7), is(visitP7a));

		// Check with after date
		def.setStartedOnOrAfter(TestUtils.date(2012, 1, 2));
		def.setStartedOnOrBefore(null);

		data = evaluator.evaluate(def, context);
		Assert.assertThat((Visit) data.getData().get(7), is(visitP7b));

		// Check with before date
		def.setStartedOnOrAfter(null);
		def.setStartedOnOrBefore(TestUtils.date(2012, 1, 31));

		data = evaluator.evaluate(def, context);
		Assert.assertThat((Visit) data.getData().get(7), is(visitP7a));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.data.patient.evaluator.VisitsForPatientDataEvaluator#evaluate(org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate_last() throws EvaluationException {
		VisitsForPatientDataDefinition def = new VisitsForPatientDataDefinition();
		def.setWhich(TimeQualifier.LAST);
		EvaluatedPatientData data = evaluator.evaluate(def, context);
		Assert.assertThat((Visit) data.getData().get(7), is(visitP7b));
	}
}