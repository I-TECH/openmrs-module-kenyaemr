/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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