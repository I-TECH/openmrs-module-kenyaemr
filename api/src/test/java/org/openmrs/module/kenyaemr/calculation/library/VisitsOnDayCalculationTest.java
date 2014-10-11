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

package org.openmrs.module.kenyaemr.calculation.library;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link VisitsOnDayCalculation}
 */
public class VisitsOnDayCalculationTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		commonMetadata.install();

		VisitType outpatient = MetadataUtils.existing(VisitType.class, CommonMetadata._VisitType.OUTPATIENT);

		// Patient #7 has two visits on 1-Jan-2012 (from 9am to 10am and another from 11am to 12pm)
		TestUtils.saveVisit(TestUtils.getPatient(7), outpatient, TestUtils.date(2012, 1, 1, 9, 0, 0), TestUtils.date(2012, 1, 1, 10, 0, 0));
		TestUtils.saveVisit(TestUtils.getPatient(7), outpatient, TestUtils.date(2012, 1, 1, 11, 0, 0), TestUtils.date(2012, 1, 1, 12, 0, 0));

		// Patient #8 has visit on 2-Jan-2012
		TestUtils.saveVisit(TestUtils.getPatient(8), outpatient, TestUtils.date(2012, 1, 2, 9, 0, 0), TestUtils.date(2012, 1, 2, 10, 0, 0));
	}

	/**
	 * @see VisitsOnDayCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldFetchAllPatientVisitsOnDate() throws Exception {
		List<Integer> ptIds = Arrays.asList(7, 8);
		Map<String, Object> paramValues = new HashMap<String, Object>();
		paramValues.put("date", TestUtils.date(2012, 1, 1));

		CalculationResultMap resultMap = new VisitsOnDayCalculation().evaluate(ptIds, paramValues, Context.getService(PatientCalculationService.class).createCalculationContext());

		Assert.assertThat(((ListResult) resultMap.get(7)).getValues().size(), is(2)); // Has two visit
		Assert.assertThat(resultMap.get(8).isEmpty(), is(true)); // No visits on that day
	}

	/**
	 * @see VisitsOnDayCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldDefaultToToday() throws Exception {
		List<Integer> ptIds = Arrays.asList(7, 8);
		Map<String, Object> paramValues = new HashMap<String, Object>();

		CalculationResultMap resultMap = new VisitsOnDayCalculation().evaluate(ptIds, paramValues, Context.getService(PatientCalculationService.class).createCalculationContext());

		Assert.assertThat(resultMap.get(7).isEmpty(), is(true)); // No visits on that day
		Assert.assertThat(resultMap.get(8).isEmpty(), is(true)); // No visits on that day
	}
}