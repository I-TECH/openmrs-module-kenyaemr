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

package org.openmrs.module.kenyaemr.calculation.library;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link RecordedDeceasedCalculation}
 */
public class RecordedDeceasedCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	/**
	 * @see RecordedDeceasedCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate() throws Exception {
		Concept discontinueReason = Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION);
		Concept died = Dictionary.getConcept(Dictionary.DIED);
		Concept unknown = Dictionary.getConcept(Dictionary.UNKNOWN);

		// Give patient #6 a UNKNOWN reason obs
		TestUtils.saveObs(TestUtils.getPatient(6), discontinueReason, unknown, TestUtils.date(2012, 12, 1));

		// Give patient #7 a DIED reason obs
		TestUtils.saveObs(TestUtils.getPatient(7), discontinueReason, died, TestUtils.date(2010, 12, 1));
		
		List<Integer> ptIds = Arrays.asList(6, 7, 8);
		CalculationResultMap resultMap = new RecordedDeceasedCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertFalse((Boolean) resultMap.get(6).getValue()); // has only an UNKNOWN reason
		Assert.assertTrue((Boolean) resultMap.get(7).getValue());
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // has no recorded reason
	}
}