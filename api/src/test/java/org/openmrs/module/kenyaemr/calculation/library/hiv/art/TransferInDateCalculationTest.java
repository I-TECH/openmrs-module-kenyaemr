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

package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Test for {@link TransferInDateCalculation}
 */

public class TransferInDateCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	/**
	 * @see IsTransferInCalculation#evaluate(Collection, Map, PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCalculateTransferInDateCalculation() throws Exception {

		Concept transferInDate = Dictionary.getConcept(Dictionary.TRANSFER_IN_DATE);

		//make #2 a transfer in and give the date transferred in
		TestUtils.saveObs(TestUtils.getPatient(2), transferInDate, TestUtils.date(2014, 3, 1), TestUtils.date(2014, 3, 1));
		//give #7 a transfer in date
		TestUtils.saveObs(TestUtils.getPatient(7), transferInDate, TestUtils.date(2014, 3, 10), TestUtils.date(2014, 3, 10));

		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8, 999);
		CalculationResultMap resultMap = new TransferInDateCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertEquals(TestUtils.date(2014, 3, 1), resultMap.get(2).getValue()); // is a transfer in has date
		Assert.assertEquals(TestUtils.date(2014, 3, 10), resultMap.get(7).getValue()); // have a transfer has date
	}
}
