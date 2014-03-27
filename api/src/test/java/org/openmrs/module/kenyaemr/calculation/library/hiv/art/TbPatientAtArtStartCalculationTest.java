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
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link TbPatientAtArtStartCalculation}
 */
public class TbPatientAtArtStartCalculationTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private TbMetadata tbMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		tbMetadata.install();
	}

	/**
	 * @see TbPatientAtArtStartCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCalculateTbEnrollmentStatusAtArtStart() throws Exception {
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
		Concept stavudine = Dictionary.getConcept(Dictionary.STAVUDINE);

		// Enroll patient #2 on same day as ART start. This patient has a drug order in standardTestDataset.xml
		// which is determining their ART start date
		TestUtils.enrollInProgram(TestUtils.getPatient(2), tbProgram, TestUtils.date(2007, 12, 25));

		// Discontinue patient #6 before ART start
		TestUtils.enrollInProgram(TestUtils.getPatient(7), tbProgram, TestUtils.date(2012, 1, 1), TestUtils.date(2012, 1, 3));
		TestUtils.saveDrugOrder(TestUtils.getPatient(7), stavudine, TestUtils.date(2012, 1, 8), null);

		// Enroll patient #7 a week before ART start
		TestUtils.enrollInProgram(TestUtils.getPatient(7), tbProgram, TestUtils.date(2012, 1, 1));
		TestUtils.saveDrugOrder(TestUtils.getPatient(7), stavudine, TestUtils.date(2012, 1, 8), null);
		
		List<Integer> ptIds = Arrays.asList(2, 6, 7);
		CalculationResultMap resultMap = new TbPatientAtArtStartCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertTrue((Boolean) resultMap.get(2).getValue());
		Assert.assertFalse((Boolean) resultMap.get(6).getValue());
		Assert.assertTrue((Boolean) resultMap.get(7).getValue());
	}
}