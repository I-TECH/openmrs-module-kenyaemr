/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link OnCtxWithinDurationCalculation}
 */
public class OnCtxWithinDurationCalculationTest extends BaseModuleContextSensitiveTest {

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
	}


	/**
	 * @see NeverTakenCtxOrDapsoneCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate() throws Exception {
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

		// Enroll patients #2, #6, #7 in the HIV Program
		TestUtils.enrollInProgram(TestUtils.getPatient(2), hivProgram, TestUtils.date(2013, 1, 1));
		TestUtils.enrollInProgram(TestUtils.getPatient(6), hivProgram, TestUtils.date(2013, 1, 1));
		TestUtils.enrollInProgram(TestUtils.getPatient(7), hivProgram, TestUtils.date(2013, 1, 1));

		// Give patient #6 ctx med order obs
		Concept medOrders = Dictionary.getConcept(Dictionary.MEDICATION_ORDERS);
		Concept ctx = Dictionary.getConcept(Dictionary.SULFAMETHOXAZOLE_TRIMETHOPRIM);
		TestUtils.saveObs(TestUtils.getPatient(6), medOrders, ctx, TestUtils.date(2013, 1, 1));

		//give patient #7 med dispensed
		Concept medDispensed = Dictionary.getConcept(Dictionary.COTRIMOXAZOLE_DISPENSED);
		Concept yes = Dictionary.getConcept(Dictionary.YES);
		TestUtils.saveObs(TestUtils.getPatient(7), medDispensed, yes, TestUtils.date(2013, 1, 1));

		//give patient 6 and 7 a TCA date
		Concept tca = Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE);
		TestUtils.saveObs(TestUtils.getPatient(6), tca, TestUtils.date(2013, 4, 30), TestUtils.date(2013, 1, 1));
		TestUtils.saveObs(TestUtils.getPatient(7), tca, TestUtils.date(2013, 4, 30), TestUtils.date(2013, 1, 1));


		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8);
		PatientCalculationContext context = Context.getService(PatientCalculationService.class).createCalculationContext();
		context.setNow(TestUtils.date(2013, 1, 31));

		CalculationResultMap resultMap = new OnCtxWithinDurationCalculation().evaluate(ptIds, null, context);
		Assert.assertFalse((Boolean) resultMap.get(2).getValue());
		Assert.assertTrue((Boolean) resultMap.get(6).getValue());
		Assert.assertTrue((Boolean) resultMap.get(7).getValue());
		Assert.assertFalse((Boolean) resultMap.get(8).getValue());
	}
}
