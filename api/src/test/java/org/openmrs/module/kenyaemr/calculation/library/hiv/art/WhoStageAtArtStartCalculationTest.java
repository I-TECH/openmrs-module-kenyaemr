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
 * Tests for {@link org.openmrs.module.kenyaemr.calculation.library.hiv.art.WhoStageAtArtStartCalculation}
 */
public class WhoStageAtArtStartCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.library.hiv.art.WhoStageAtArtStartCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCalculateWhoStageAtArtStart() throws Exception {
		Concept whoStage = Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE);
		Concept whoStage1Adults = Dictionary.getConcept(Dictionary.WHO_STAGE_1_ADULT);
		Concept whoStage2Adults = Dictionary.getConcept(Dictionary.WHO_STAGE_2_ADULT);
		Concept whoStageUnknown = Dictionary.getConcept(Dictionary.UNKNOWN);

		Concept stavudine = Dictionary.getConcept(Dictionary.STAVUDINE);

		EncounterType scheduled = Context.getEncounterService().getEncounterType(1);

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



		// Give patient #2 WHO Stage 1 on same day as ART start. This patient has a drug order in standardTestDataset.xml
		// drug orders in standartTestDataset have no patients associated
		// which is determining their ART start date
		TestUtils.saveObs(TestUtils.getPatient(2), whoStage, whoStage1Adults, TestUtils.date(2007, 12, 25));

		// Give patient #6 WHO Stage 1 week before ART start
		TestUtils.saveObs(TestUtils.getPatient(6), whoStage, whoStage1Adults, TestUtils.date(2012, 1, 1));
		TestUtils.saveDrugOrder(TestUtils.getPatient(6), stavudine, TestUtils.date(2012, 1, 8), null, scheduledEncounter1);

		// Give patient #7 WHO Stage 1 but a newer UNKNOWN WHO stage (saved as NULL) before ART start
		TestUtils.saveObs(TestUtils.getPatient(7), whoStage, whoStage1Adults, TestUtils.date(2012, 1, 1));
		TestUtils.saveObs(TestUtils.getPatient(7), whoStage, whoStageUnknown, TestUtils.date(2012, 1, 3));
		TestUtils.saveDrugOrder(TestUtils.getPatient(7), stavudine, TestUtils.date(2012, 1, 8), null);

		// Give patient #8 WHO Stage 1 week before ART start and a newer WHO Stage 2 after ART start
		TestUtils.saveObs(TestUtils.getPatient(8), whoStage, whoStage1Adults, TestUtils.date(2012, 1, 1));
		TestUtils.saveObs(TestUtils.getPatient(8), whoStage, whoStage2Adults, TestUtils.date(2012, 1, 15));
		TestUtils.saveDrugOrder(TestUtils.getPatient(8), stavudine, TestUtils.date(2012, 1, 8), null, scheduledEncounter);

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8, 999);
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new WhoStageAtArtStartCalculation());
		Assert.assertNull(resultMap.get(2)); //patient 2 has no drug order
		Assert.assertEquals(1, resultMap.get(6).getValue());
		Assert.assertNull(resultMap.get(7)); //has unknown/null latest WHO Stage
		Assert.assertEquals(1, resultMap.get(8).getValue());
		Assert.assertNull(resultMap.get(999)); //has no recorded WHO stage
	}
}