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
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests for {@link DecliningCd4Calculation}
 */
public class DecliningCd4CalculationTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		hivMetadata.install();
	}

	/**
	 * @see DecliningCd4Calculation#getFlagMessage()
	 */
	@Test
	public void getFlagMessage() {
		Assert.assertThat(new DecliningCd4Calculation().getFlagMessage(), notNullValue());
	}

	/**
	 * @see DecliningCd4Calculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies determine whether patients have a decline in CD4
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientsHasDeclinedCD4() throws Exception {
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

		// Enroll patients #6, #7 and #8 in the HIV Program
		TestUtils.enrollInProgram(TestUtils.getPatient(6), hivProgram, new Date());
		TestUtils.enrollInProgram(TestUtils.getPatient(7), hivProgram, new Date());
		TestUtils.enrollInProgram(TestUtils.getPatient(8), hivProgram, new Date());

		// Give patients #7 and #8 a CD4 count 180 days ago
		Concept cd4 = Dictionary.getConcept(Dictionary.CD4_COUNT);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -180);
		TestUtils.saveObs(TestUtils.getPatient(7), cd4, 123d, calendar.getTime());
		TestUtils.saveObs(TestUtils.getPatient(8), cd4, 123d, calendar.getTime());

		// Give patient #7 a lower CD4 count today
		TestUtils.saveObs(TestUtils.getPatient(7), cd4, 120d, new Date());

		// Give patient #8 a higher CD4 count today
		TestUtils.saveObs(TestUtils.getPatient(8), cd4, 126d, new Date());

		List<Integer> ptIds = Arrays.asList(6, 7, 8, 999);

		CalculationResultMap resultMap = new DecliningCd4Calculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertFalse((Boolean) resultMap.get(6).getValue());  //in Hiv program but without cd4 i.e needs cd4
		Assert.assertTrue((Boolean) resultMap.get(7).getValue()); // has decline in CD4
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // has increase in CD4
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // not in HIV Program
	}
}