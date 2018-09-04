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
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.test.EmrTestUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link OnSecondLineArtCalculation}
 */
@Ignore
public class OnSecondLineArtCalculationTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private RegimenManager regimenManager;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private CommonMetadata commonMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		executeDataSet("dataset/test-drugs.xml");

		commonMetadata.install();
		hivMetadata.install();

		regimenManager.refresh();
	}

	/**
	 * @see OnSecondLineArtCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCalculateCurrentArtRegimen() throws Exception {
		/**
		 * encounter#3,4,5 for patient 7
		 * encounter#6 for patient 2
		 *
		 */
		Concept azt = Context.getConceptService().getConcept(86663);
		Concept _3tc = Context.getConceptService().getConcept(78643);
		Concept efv = Context.getConceptService().getConcept(75523);
		Concept lpv = Context.getConceptService().getConcept(79040);
		Concept rtv = Context.getConceptService().getConcept(83412);

		// Put patient #7 on AZT + 3TC + EFV
		Encounter enc1 = Context.getEncounterService().getEncounter(3);
		EmrTestUtils.saveRegimenOrder(TestUtils.getPatient(7), Arrays.asList(azt, _3tc, efv), TestUtils.date(2011, 1, 1), null);

		// Put patient #8 on AZT + 3TC + LPV/r
		Encounter enc2 = Context.getEncounterService().getEncounter(6);
		EmrTestUtils.saveRegimenOrder(TestUtils.getPatient(2), Arrays.asList(azt, _3tc, lpv, rtv), TestUtils.date(2011, 1, 1), null, enc2);

		List<Integer> cohort = Arrays.asList(2, 7, 8);

		CalculationResultMap resultMap = new OnSecondLineArtCalculation().evaluate(cohort, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertFalse((Boolean) resultMap.get(6).getValue()); // isn't on any drugs
		Assert.assertFalse((Boolean) resultMap.get(7).getValue()); // is on first line regimen
		Assert.assertTrue((Boolean) resultMap.get(8).getValue()); // is on second line regimen
	}
}