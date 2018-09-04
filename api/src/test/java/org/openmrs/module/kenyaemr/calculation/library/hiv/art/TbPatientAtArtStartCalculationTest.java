/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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