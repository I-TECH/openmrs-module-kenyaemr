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

package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.calculation.library.hiv.NeverTakenCtxOrDapsoneCalculation}
 */
public class NeverTakenCtxOrDapsoneCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");
	}

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.library.hiv.NeverTakenCtxOrDapsoneCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate() throws Exception {

		// Get HIV Program
		Program hivProgram = MetadataUtils.getProgram(Metadata.HIV_PROGRAM);

		// Enroll patients #6, #7, #8 in the HIV Program
		PatientService ps = Context.getPatientService();
		for (int i = 6; i <= 8; ++i) {
			TestUtils.enrollInProgram(ps.getPatient(i), hivProgram, TestUtils.date(2011, 1, 1));
		}

		// Put patient #7 on Dapsone
		Concept medOrders = Dictionary.getConcept(Dictionary.MEDICATION_ORDERS);
		Concept dapsone = Dictionary.getConcept(Dictionary.DAPSONE);
		TestUtils.saveObs(ps.getPatient(7), medOrders, dapsone, TestUtils.date(2011, 1, 1));

		// Put patient #8 on Aspirin
		Concept aspirin = Context.getConceptService().getConcept(71617);
		TestUtils.saveObs(ps.getPatient(8), medOrders, aspirin, TestUtils.date(2011, 1, 1));

		Context.flushSession();
		
		List<Integer> cohort = Arrays.asList(6, 7, 8, 999);

		CalculationResultMap resultMap = new NeverTakenCtxOrDapsoneCalculation().evaluate(cohort, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertTrue((Boolean) resultMap.get(6).getValue()); // isn't on any drugs
		Assert.assertFalse((Boolean) resultMap.get(7).getValue()); // is taking Dapsone
		Assert.assertTrue((Boolean) resultMap.get(8).getValue()); // is taking just Aspirin
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // not in HIV program
	}
}