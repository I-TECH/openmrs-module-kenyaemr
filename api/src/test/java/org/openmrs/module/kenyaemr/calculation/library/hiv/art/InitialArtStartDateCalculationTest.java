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
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link InitialArtStartDateCalculation}
 */
public class InitialArtStartDateCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	/**
	 * @see InitialArtStartDateCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCalculateInitialArtStartDate() throws Exception {
		Concept dapsone = Dictionary.getConcept(Dictionary.DAPSONE);
		Concept azt = Context.getConceptService().getConcept(86663);
		Concept _3tc = Context.getConceptService().getConcept(78643);
		Concept efv = Context.getConceptService().getConcept(75523);

		EncounterType scheduled = Context.getEncounterService().getEncounterType(1);
		Encounter scheduledEncounter1 = new Encounter();
		scheduledEncounter1.setPatient(TestUtils.getPatient(6));
		scheduledEncounter1.setEncounterType(scheduled);
		scheduledEncounter1.setEncounterDatetime(TestUtils.date(2011, 1, 1));
		scheduledEncounter1 = Context.getEncounterService().saveEncounter(scheduledEncounter1);

		// Put patient #6 on Dapsone
		TestUtils.saveDrugOrder(TestUtils.getPatient(6), dapsone, TestUtils.date(2011, 1, 1), null, scheduledEncounter1);

		// Put patient #7 on AZT, then 3TC, then EFV
		TestUtils.saveDrugOrder(TestUtils.getPatient(7), azt, TestUtils.date(2010, 1, 1), null);
		TestUtils.saveDrugOrder(TestUtils.getPatient(7), _3tc, TestUtils.date(2011, 1, 1), null);
		TestUtils.saveDrugOrder(TestUtils.getPatient(7), efv, TestUtils.date(2011, 1, 1), null);

		List<Integer> cohort = Arrays.asList(6, 7, 8);

		CalculationResultMap resultMap = new InitialArtStartDateCalculation().evaluate(cohort, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertNull(resultMap.get(6)); // isn't on any ART drugs
		Assert.assertEquals(TestUtils.date(2010, 1, 1), resultMap.get(7).getValue());
		Assert.assertNull(resultMap.get(8)); // isn't on any drugs
	}
}