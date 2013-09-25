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
package org.openmrs.module.kenyaemr.calculation.library.mchcs;

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
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.calculation.library.mchcs.InfantFeedingOptionsCalculation }
 */
public class InfantFeedingOptionsCalculationTest extends BaseModuleContextSensitiveTest {

	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		executeDataSet("dataset/test-metadata.xml");
	}
	/**
	 * @see org.openmrs.module.kenyaemr.calculation.library.InfantFeedingOptionsCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate last recorded feeding options
	 */
	@Test
	public void evaluate_shouldCalculateInfantFeedingOptions() throws Exception {
		//get mchcs program
		Program mchcsProgram = MetadataUtils.getProgram(MchMetadata.Program.MCHCS);
		// Enroll patients #6 and  #7  in the mchcs Program
		PatientService ps = Context.getPatientService();
		TestUtils.enrollInProgram(ps.getPatient(6), mchcsProgram, new Date());
		TestUtils.enrollInProgram(ps.getPatient(7), mchcsProgram, new Date());
		TestUtils.enrollInProgram(ps.getPatient(8), mchcsProgram, new Date());

		//infants not necessarily  in HEI
		//get the feeding options concept
		Concept feedingOptions = Dictionary.getConcept(Dictionary.FEEDING_OPTIONS);
		//give patient #6 and #7 recent feeding options and patient #8 not having any
		TestUtils.saveObs(ps.getPatient(6), feedingOptions, Dictionary.getConcept(Dictionary.BREASTFED_EXCLUSIVELY), new Date());
		TestUtils.saveObs(ps.getPatient(7), feedingOptions, Dictionary.getConcept(Dictionary.REPLACEMENT_FEEDING), new Date());

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(6,7,8);
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new InfantFeedingOptionsCalculation());
		Assert.assertTrue((Boolean) resultMap.get(6).getValue()); // On feeding program
		Assert.assertTrue((Boolean) resultMap.get(7).getValue()); // On feeding program
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // Not on any feeding options
	}
}
