/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Tests for {@link NeedsCd4TestCalculation}
 */
public class NeedsCd4TestCalculationTest extends BaseModuleContextSensitiveTest {

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
	 * @see NeedsCd4TestCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies determine whether patients need a CD4
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientsNeedsCD4() throws Exception {
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

		// Enroll patients #6, #7 and #8  in the HIV Program
		TestUtils.enrollInProgram(TestUtils.getPatient(2), hivProgram, new Date());
		TestUtils.enrollInProgram(TestUtils.getPatient(6), hivProgram, new Date());
		TestUtils.enrollInProgram(TestUtils.getPatient(7), hivProgram, new Date());
		TestUtils.enrollInProgram(TestUtils.getPatient(8), hivProgram, new Date());

		// Give patient #7 a recent CD4 result obs
		Concept cd4 = Dictionary.getConcept(Dictionary.CD4_COUNT);
		TestUtils.saveObs(TestUtils.getPatient(7), cd4, 123d, new Date());

		// Give patient #8 a CD4 result obs from a year ago
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -360);
		TestUtils.saveObs(TestUtils.getPatient(8), cd4, 123d, calendar.getTime());

		//give patient #2 a recent CD4% result obs
		Concept cd4Percent = Dictionary.getConcept(Dictionary.CD4_PERCENT);
		TestUtils.saveObs(TestUtils.getPatient(2), cd4Percent, 80d, new Date());

		// Give patient #6 a CD4% result obs from a year ago
		Calendar calendarP = Calendar.getInstance();
		calendarP.add(Calendar.DATE, -200);
		TestUtils.saveObs(TestUtils.getPatient(6), cd4Percent, 89d, calendarP.getTime());

		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8, 999);
		CalculationResultMap resultMap = new NeedsCd4TestCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertFalse((Boolean) resultMap.get(2).getValue()); // has recent CD4%
		Assert.assertTrue((Boolean) resultMap.get(6).getValue()); // has old CD4%
		Assert.assertFalse((Boolean) resultMap.get(7).getValue()); // has recent CD4
		Assert.assertTrue((Boolean) resultMap.get(8).getValue()); // has old CD4
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // not in HIV Program
	}
}