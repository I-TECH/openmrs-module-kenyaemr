/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
