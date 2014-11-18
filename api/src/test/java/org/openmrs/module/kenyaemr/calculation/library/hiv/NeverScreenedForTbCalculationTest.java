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
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link NeverScreenedForTbCalculation}
 */
public class NeverScreenedForTbCalculationTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private TbMetadata tbMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		hivMetadata.install();
		tbMetadata.install();
	}

	/**
	 * @see NeverScreenedForTbCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldReturnHivPatientsNotScreenedForTb() throws Exception {
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

		// Enroll patients #2, #6, #7 in the HIV program
		TestUtils.enrollInProgram(TestUtils.getPatient(2), hivProgram, TestUtils.date(2011, 1, 1));
		TestUtils.enrollInProgram(TestUtils.getPatient(6), hivProgram, TestUtils.date(2011, 1, 1));
		TestUtils.enrollInProgram(TestUtils.getPatient(7), hivProgram, TestUtils.date(2011, 1, 1));

		Concept tbDiseaseStatus = Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS);
		Concept diseaseSuspected = Dictionary.getConcept(Dictionary.DISEASE_SUSPECTED);
		Concept notAssessed = Dictionary.getConcept(Dictionary.NOT_ASSESSED);

		// Don't screen patient #2
		TestUtils.saveObs(TestUtils.getPatient(2), tbDiseaseStatus, notAssessed, TestUtils.date(2012, 6, 1));

		// Screen patients #7 and #8
		TestUtils.saveObs(TestUtils.getPatient(7), tbDiseaseStatus, diseaseSuspected, TestUtils.date(2012, 6, 1));
		TestUtils.saveObs(TestUtils.getPatient(8), tbDiseaseStatus, diseaseSuspected, TestUtils.date(2012, 6, 1));
		
		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8);
		CalculationResultMap resultMap = new NeverScreenedForTbCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertTrue((Boolean) resultMap.get(2).getValue()); // obs is not assessed
		Assert.assertTrue((Boolean) resultMap.get(6).getValue()); // no disease status obs
		Assert.assertFalse((Boolean) resultMap.get(7).getValue()); // is screened
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // not in HIV program
	}
}