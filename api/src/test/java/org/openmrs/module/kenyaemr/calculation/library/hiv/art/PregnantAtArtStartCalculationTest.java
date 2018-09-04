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
import org.junit.Ignore;
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
 * Tests for {@link PregnantAtArtStartCalculation}
 */
public class PregnantAtArtStartCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	/**
	 * @see PregnantAtArtStartCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate recorded pregnancy status at ART start for all patients
	 */
	@Test
	public void evaluate_shouldCalculatePregnancyStatusAtArtStart() throws Exception {
		Concept pregnancyStatus = Dictionary.getConcept(Dictionary.PREGNANCY_STATUS);
		Concept yes = Dictionary.getConcept(Dictionary.YES);
		Concept no = Dictionary.getConcept(Dictionary.NO);
		Concept stavudine = Dictionary.getConcept(Dictionary.STAVUDINE);

		// For the purposes of this test, everyone is a woman
		TestUtils.getPatient(2).setGender("F");
		TestUtils.getPatient(6).setGender("F");

		EncounterType scheduled = Context.getEncounterService().getEncounterType(1);
		EncounterType emergency = Context.getEncounterService().getEncounterType(2);

		Encounter scheduledEncounter = new Encounter();
		scheduledEncounter.setPatient(TestUtils.getPatient(8));
		scheduledEncounter.setEncounterType(scheduled);
		scheduledEncounter.setEncounterDatetime(TestUtils.date(2012, 1, 1));
		scheduledEncounter = Context.getEncounterService().saveEncounter(scheduledEncounter);

		Encounter scheduledEncounter1 = new Encounter();
		scheduledEncounter1.setPatient(TestUtils.getPatient(6));
		scheduledEncounter1.setEncounterType(scheduled);
		scheduledEncounter1.setEncounterDatetime(TestUtils.date(2012, 1, 1));
		scheduledEncounter1 = Context.getEncounterService().saveEncounter(scheduledEncounter1);


		// Give patient #2 a YES status on same day as ART start. This patient has a drug order in standardTestDataset.xml
		// the patient does not have orders in the current standardtestdataset
		// which is determining their ART start date
		TestUtils.saveObs(TestUtils.getPatient(2), pregnancyStatus, yes, TestUtils.date(2007, 12, 25));

		// Give patient #6 a YES status week before ART start
		TestUtils.saveObs(TestUtils.getPatient(6), pregnancyStatus, yes, TestUtils.date(2012, 1, 1));
		TestUtils.saveDrugOrder(TestUtils.getPatient(6), stavudine, TestUtils.date(2012, 1, 8), null, scheduledEncounter1);

		// Give patient #7 a YES but a newer NO status before ART start
		TestUtils.saveObs(TestUtils.getPatient(7), pregnancyStatus, yes, TestUtils.date(2012, 1, 1));
		TestUtils.saveObs(TestUtils.getPatient(7), pregnancyStatus, no, TestUtils.date(2012, 1, 3));
		TestUtils.saveDrugOrder(TestUtils.getPatient(7), stavudine, TestUtils.date(2012, 1, 8), null);

		// Give patient #8 a YES status week before ART start and a newer NO status after ART start
		TestUtils.saveObs(TestUtils.getPatient(8), pregnancyStatus, yes, TestUtils.date(2012, 1, 1));
		TestUtils.saveObs(TestUtils.getPatient(8), pregnancyStatus, no, TestUtils.date(2012, 1, 15));
		TestUtils.saveDrugOrder(TestUtils.getPatient(8), stavudine, TestUtils.date(2012, 1, 8), null, scheduledEncounter);

		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8, 999);
		CalculationResultMap resultMap = new PregnantAtArtStartCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertFalse((Boolean) resultMap.get(2).getValue());
		Assert.assertTrue((Boolean) resultMap.get(6).getValue());
		Assert.assertFalse((Boolean) resultMap.get(7).getValue());
		Assert.assertTrue((Boolean) resultMap.get(8).getValue());
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // has no recorded status
	}
}