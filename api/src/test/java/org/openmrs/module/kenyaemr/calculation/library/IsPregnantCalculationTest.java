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
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests for {@link IsPregnantCalculation}
 */
public class IsPregnantCalculationTest extends BaseModuleContextSensitiveTest {
	@Autowired
	private MchMetadata mchMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		mchMetadata.install();
	}

	/**
	 * @see IsPregnantCalculation#getFlagMessage()
	 */
	@Test
	public void getFlagMessage() {
		Assert.assertThat(new IsPregnantCalculation().getFlagMessage(), notNullValue());
	}

	/**
	 * @see IsPregnantCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate last recorded pregnancy status for all patients
	 */
	@Test
	public void evaluate_shouldCalculatePregnancyStatus() throws Exception {
		Concept pregnancyStatus = Dictionary.getConcept(Dictionary.PREGNANCY_STATUS);
		Concept yes = Dictionary.getConcept(Dictionary.YES);
		Concept no = Dictionary.getConcept(Dictionary.NO);

		// Give patient #6 a recent YES recording which should be ignored as they are male
		TestUtils.saveObs(TestUtils.getPatient(6), pregnancyStatus, yes, TestUtils.date(2012, 12, 1));

		// Give patient #7 an older YES recording
		TestUtils.saveObs(TestUtils.getPatient(7), pregnancyStatus, yes, TestUtils.date(2010, 11, 1));

		// Give patient #7 a recent NO recording
		TestUtils.saveObs(TestUtils.getPatient(7), pregnancyStatus, no, TestUtils.date(2012, 12, 1));

		// Give patient #8 a recent YES recording
		TestUtils.saveObs(TestUtils.getPatient(8), pregnancyStatus, yes, TestUtils.date(2012, 12, 1));
		
		List<Integer> ptIds = Arrays.asList(6, 7, 8);
		CalculationResultMap resultMap = new IsPregnantCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertFalse((Boolean) resultMap.get(6).getValue()); // is male
		Assert.assertFalse((Boolean) resultMap.get(7).getValue());
		Assert.assertTrue((Boolean) resultMap.get(8).getValue());
	}
}