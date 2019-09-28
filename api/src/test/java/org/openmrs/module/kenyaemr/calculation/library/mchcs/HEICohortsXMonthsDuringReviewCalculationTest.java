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
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HEICohortsXMonthsDuringReviewCalculationTest extends BaseModuleContextSensitiveTest {

	@Before
	public void setUp() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	/**
	 * @verifies hei patients of a given cohort month during a specified review period
	 * @see org.openmrs.module.kenyaemr.calculation.library.mchcs.HEICohortsXMonthsDuringReviewCalculation#evaluate (
	 *      java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void valuate_shouldEvaluatePatientsInAGivenCohortMonthSinceBirth() throws Exception {

		{
			/**
			 * set up patients with relevant birth date for test
			 */
			Patient patient = TestUtils.getPatient(6);
			patient.setBirthdate(computeWithinRangeEncounterDate(49));

			Patient patient_two = TestUtils.getPatient(7);
			patient_two.setBirthdate(computeWithinRangeEncounterDate(15));

			Patient patient_three = TestUtils.getPatient(8);
			patient_three.setBirthdate(computeWithinRangeEncounterDate(3));
		}

		List<Integer> ptIds = Arrays.asList(7, 8, 999, 6);
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("turnedMonths", 12);
		params.put("reviewMonths", 6);

		CalculationResultMap resultMap = new HEICohortsXMonthsDuringReviewCalculation().evaluate(ptIds, params, Context.getService(PatientCalculationService.class).createCalculationContext());

		Assert.assertTrue((Boolean) resultMap.get(7).getValue());
		Assert.assertFalse((Boolean) resultMap.get(8).getValue());
		Assert.assertNull(resultMap.get(999)); // voided, no dob
		Assert.assertFalse((Boolean) resultMap.get(6).getValue());

	}

	private Date computeWithinRangeEncounterDate(int months){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -months);
		cal.clear(Calendar.HOUR);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.MILLISECOND);
		return cal.getTime();
	}

}