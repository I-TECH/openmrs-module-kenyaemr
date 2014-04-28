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
import org.openmrs.calculation.patient.PatientCalculationContext;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Test for {@link IsTransferInsCalculation}
 */
public class IsTransferInsCalculationTest extends BaseModuleContextSensitiveTest {

	@Autowired
	HivMetadata hivMetadata;

	@Autowired
	TbMetadata tbMetadata;

	@Autowired
	CommonMetadata commonMetadata;

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
	 * @see IsTransferInsCalculation#evaluate(Collection, Map, PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCalculateIsTransferInsCalculation() throws Exception {
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);

		// Enroll patients #2, and  #7 in the HIV Program
		TestUtils.enrollInProgram(TestUtils.getPatient(2), hivProgram, TestUtils.date(2014,3,1));
		TestUtils.enrollInProgram(TestUtils.getPatient(7), hivProgram, TestUtils.date(2014,3,10));
		TestUtils.enrollInProgram(TestUtils.getPatient(8), tbProgram, TestUtils.date(2014,3,15));

		Concept transferInDate = Dictionary.getConcept(Dictionary.TRANSFER_IN_DATE);
		Concept transferInStatus = Dictionary.getConcept(Dictionary.TRANSFER_IN);
		Concept yes = Dictionary.getConcept(Dictionary.YES);

		//make #2 a transfer in with the looking at the status
		TestUtils.saveObs(TestUtils.getPatient(2), transferInStatus, yes, TestUtils.date(2014, 3, 1));
		//give #7 a transfer in date
		TestUtils.saveObs(TestUtils.getPatient(7), transferInDate, TestUtils.date(2014,3,10), TestUtils.date(2014, 3, 10));

		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8, 999);
		CalculationResultMap resultMap = new IsTransferInsCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertTrue((Boolean) resultMap.get(2).getValue()); //is in hiv program and is a transfer in (yes)
		Assert.assertTrue((Boolean) resultMap.get(7).getValue()); //is in hiv program and have a transfer in date
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // is in tb program
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); //not in hiv program
	}

}
