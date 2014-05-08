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
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Tests for {@link InfantFeedingOptionsCalculation}
 */
public class InfantFeedingOptionsCalculationTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private MchMetadata mchMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		mchMetadata.install();
	}

	/**
	 * @see InfantFeedingOptionsCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate last recorded feeding options
	 */
	@Test
	public void evaluate_shouldCalculateInfantFeedingOptions() throws Exception {
		// Get MCHCS program
		Program mchcsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHCS);

		// Enroll patients #6 and #7 in the MCHCS Program
		TestUtils.enrollInProgram(TestUtils.getPatient(6), mchcsProgram, new Date());
		TestUtils.enrollInProgram(TestUtils.getPatient(7), mchcsProgram, new Date());
		TestUtils.enrollInProgram(TestUtils.getPatient(8), mchcsProgram, new Date());

		// infants not necessarily in HEI
		// get the feeding options concept
		Concept feedingOptions = Dictionary.getConcept(Dictionary.INFANT_FEEDING_METHOD);
		// give patient #6 and #7 recent feeding options and patient #8 not having any
		TestUtils.saveObs(TestUtils.getPatient(6), feedingOptions, Dictionary.getConcept(Dictionary.BREASTFED_EXCLUSIVELY), new Date());
		TestUtils.saveObs(TestUtils.getPatient(7), feedingOptions, Dictionary.getConcept(Dictionary.REPLACEMENT_FEEDING), new Date());

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(6, 7, 8);
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new InfantFeedingOptionsCalculation());
		Assert.assertTrue((Boolean) resultMap.get(6).getValue()); // On feeding program
		Assert.assertTrue((Boolean) resultMap.get(7).getValue()); // On feeding program
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // Not on any feeding options
	}
}
