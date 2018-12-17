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
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link LastCd4CountCalculation}
 */
public class LastCd4CountCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	/**
	 * @see LastCd4CountCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate last CD4 count for all patients
	 */
	@Test
	public void evaluate_shouldCalculateLastCD4() throws Exception {
		Concept cd4 = Dictionary.getConcept(Dictionary.CD4_COUNT);

		// Give patient #7 some CD4 obs
		TestUtils.saveObs(TestUtils.getPatient(7), cd4, 123d, TestUtils.date(2012, 12, 1));
		TestUtils.saveObs(TestUtils.getPatient(7), cd4, 150d, TestUtils.date(2010, 11, 1));
		
		List<Integer> ptIds = Arrays.asList(6, 7);
		CalculationResultMap resultMap = new LastCd4CountCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertNull(resultMap.get(6)); // has no recorded CD4
		Assert.assertEquals(new Double(123d), ((Obs) resultMap.get(7).getValue()).getValueNumeric());
	}
}