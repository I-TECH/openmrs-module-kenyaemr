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
import org.junit.Ignore;
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
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Tests for {@link EligibleForArtCalculation}
 */

/**
 * The calculation should be reviewed to reflect the current guidelines of test and treat.
 * This test will thus be ignored for v16.0
 */
@Ignore
public class EligibleForArtCalculationTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private TbMetadata tbMetadata;

	@Autowired
	private MchMetadata mchMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		hivMetadata.install();
		tbMetadata.install();
		mchMetadata.install();
	}
	
	/**
	 * @see EligibleForArtCalculation#evaluate(Collection,Map,PatientCalculationContext)
	 * @verifies calculate eligibility
	 */
	@Test
	public void evaluate_shouldCalculateEligibility() throws Exception {
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

		// Enroll patients #6, #7 and #8 in the HIV Program
		TestUtils.enrollInProgram(TestUtils.getPatient(6), hivProgram, TestUtils.date(2011, 1, 1));
		TestUtils.enrollInProgram(TestUtils.getPatient(7), hivProgram, TestUtils.date(2011, 1, 1));
		TestUtils.enrollInProgram(TestUtils.getPatient(8), hivProgram, TestUtils.date(2011, 1, 1));

		// Give patient #6 a high CD4 count today
		Concept cd4 = Dictionary.getConcept(Dictionary.CD4_COUNT);
		TestUtils.saveObs(TestUtils.getPatient(6), cd4, 1001, TestUtils.date(2015, 1, 1));

		// Give patients #7 and #8 a low CD4 count today
		TestUtils.saveObs(TestUtils.getPatient(7), cd4, 101, TestUtils.date(2011, 1, 1));
		TestUtils.saveObs(TestUtils.getPatient(8), cd4, 101, TestUtils.date(2011, 1, 1));

		// Put patient #8 already on ARTs
		Concept stavudine = Dictionary.getConcept(Dictionary.STAVUDINE);
		TestUtils.saveDrugOrder(TestUtils.getPatient(8), stavudine, TestUtils.date(2011, 1, 1), null);

		List<Integer> cohort = Arrays.asList(6, 7, 8, 999);

		CalculationResultMap resultMap = new EligibleForArtCalculation().evaluate(cohort, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertTrue((Boolean) resultMap.get(6).getValue()); // below 10 years should be put on art regardless of other factors
		Assert.assertTrue((Boolean) resultMap.get(7).getValue()); // has low CD4
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // already on ART
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // not in HIV Program
	}
}