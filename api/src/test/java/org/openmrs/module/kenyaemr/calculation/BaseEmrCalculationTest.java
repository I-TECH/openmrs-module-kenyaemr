/*
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
package org.openmrs.module.kenyaemr.calculation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.*;

public class BaseEmrCalculationTest extends BaseModuleContextSensitiveTest {

	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("test-data.xml");
	}

	@Test
	public void lastEncounter_shouldReturnLastEncountersForPatients() throws Exception {

		List<Integer> ptIds = Arrays.asList(6, 7);

		// Get last encounter
		CalculationResultMap resultMap = BaseEmrCalculation.lastEncounter(null, ptIds, Context.getService(PatientCalculationService.class).createCalculationContext());
		CalculationResult patient6Result = resultMap.get(6);
		CalculationResult patient7Result = resultMap.get(7);

		Assert.assertNull(patient6Result); // patient has no encounters
		Assert.assertNotNull(patient7Result);
		Assert.assertTrue(patient7Result.getValue() instanceof Encounter);
		Assert.assertEquals(TestUtils.date(2008, 8, 19), ((Encounter) patient7Result.getValue()).getEncounterDatetime());

		// Get last 'Emergency' encounter
		EncounterType emergencyEncType = Context.getEncounterService().getEncounterType("Emergency");
		resultMap = BaseEmrCalculation.lastEncounter(emergencyEncType, ptIds, Context.getService(PatientCalculationService.class).createCalculationContext());
		patient6Result = resultMap.get(6);
		patient7Result = resultMap.get(7);

		Assert.assertNull(patient6Result); // patient has no encounters
		Assert.assertNotNull(patient7Result);
		Assert.assertTrue(patient7Result.getValue() instanceof Encounter);
		Assert.assertEquals(TestUtils.date(2008, 8, 1), ((Encounter) patient7Result.getValue()).getEncounterDatetime());
	}

	@Test
	public void allEncounters_shouldReturnAllEncountersForPatients() throws Exception {

		List<Integer> ptIds = Arrays.asList(6, 7);

		// Get total encounters
		CalculationResultMap resultMap = BaseEmrCalculation.allEncounters(null, ptIds, Context.getService(PatientCalculationService.class).createCalculationContext());
		CalculationResult patient6Result = resultMap.get(6);
		CalculationResult patient7Result = resultMap.get(7);

		Assert.assertNull(patient6Result); // patient has no encounters
		Assert.assertNotNull(patient7Result);
		Assert.assertEquals(3, ((Collection) patient7Result.getValue()).size()); // patient has 3 encounters

		// Get 'Scheduled' encounters
		EncounterType scheduledEncType = Context.getEncounterService().getEncounterType("Scheduled");
		resultMap = BaseEmrCalculation.allEncounters(scheduledEncType, ptIds, Context.getService(PatientCalculationService.class).createCalculationContext());
		patient6Result = resultMap.get(6);
		patient7Result = resultMap.get(7);

		Assert.assertNull(patient6Result); // patient has no encounters
		Assert.assertNotNull(patient7Result);
		Assert.assertEquals(2, ((Collection) patient7Result.getValue()).size()); // patient has 2 encounters of type 'Scheduled'
	}
}
