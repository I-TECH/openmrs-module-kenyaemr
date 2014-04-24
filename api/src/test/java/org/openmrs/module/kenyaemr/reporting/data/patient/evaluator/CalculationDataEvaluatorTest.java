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
import org.openmrs.Program;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.calculation.library.InProgramCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.data.patient.definition.CalculationDataDefinition;
import org.openmrs.module.kenyaemr.test.ReportingTestUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link CalculationDataEvaluator}
 */
public class CalculationDataEvaluatorTest extends BaseModuleContextSensitiveTest {

	private EvaluationContext context;

	private CalculationDataEvaluator evaluator;

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		hivMetadata.install();

		context = ReportingTestUtils.reportingContext(Arrays.asList(2, 6, 7, 8, 999), TestUtils.date(2012, 1, 1), TestUtils.date(2012, 1, 31));
		evaluator = new CalculationDataEvaluator();
	}

	/**
	 * @see CalculationDataEvaluator#evaluate(org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Test
	public void evaluate() throws EvaluationException {
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

		// Enroll patient #7 into HIV program
		TestUtils.enrollInProgram(TestUtils.getPatient(7), hivProgram, TestUtils.date(2012, 1, 1));

		InProgramCalculation calculation = new InProgramCalculation();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("program", hivProgram);

		CalculationDataDefinition def = new CalculationDataDefinition("in HIV program", calculation);
		def.setCalculationParameters(params);


		EvaluatedPatientData data = evaluator.evaluate(def, context);
		Assert.assertThat((Boolean)((CalculationResult) data.getData().get(2)).getValue(), is(false));
		Assert.assertThat((Boolean)((CalculationResult) data.getData().get(6)).getValue(), is(false));
		Assert.assertThat((Boolean)((CalculationResult) data.getData().get(7)).getValue(), is(true));
		Assert.assertThat((Boolean)((CalculationResult) data.getData().get(8)).getValue(), is(false));
	}
}