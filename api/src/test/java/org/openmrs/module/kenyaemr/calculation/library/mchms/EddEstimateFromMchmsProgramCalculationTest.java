/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.mchms;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.calculation.library.mchms.EddEstimateFromMchmsProgramCalculation}
 */
public class EddEstimateFromMchmsProgramCalculationTest extends BaseModuleContextSensitiveTest {

	 @Autowired
	 private MchMetadata mchMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		mchMetadata.install();
	}

	@Test
	public void evaluate_shouldEstimateEddFromLmp() {

		Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);

		//get female patients
		Patient p7 = TestUtils.getPatient(7);
		Patient p8 = TestUtils.getPatient(8);
		//enroll patient 7 into program
		TestUtils.enrollInProgram(p7, mchmsProgram, TestUtils.date(2014, 6, 1));
		TestUtils.enrollInProgram(p8, mchmsProgram, TestUtils.date(2014, 6, 1));
		//give p7 an lmp on date and expect edd to be 280 days later
		Date lmp =  TestUtils.date(2014, 5, 1);
		Date edd = new Date();
		TestUtils.saveObs(p7, Dictionary.getConcept(Dictionary.LAST_MONTHLY_PERIOD), lmp , TestUtils.date(2014, 6, 1));
		//add 208 days on lmp to get edd
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(lmp);
		calendar.add(Calendar.DATE, 280);
		edd = calendar.getTime();


		List<Integer> cohort = Arrays.asList(6, 7, 8, 9);
		CalculationResultMap resultMap = new EddEstimateFromMchmsProgramCalculation().evaluate(cohort, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertEquals(edd, resultMap.get(7).getValue()); //had lmp hence edd and eventually tally to date 280 days
		Assert.assertThat(resultMap.get(8).isEmpty(), is(false));


	}
}
