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
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.test.EmrTestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link OnOriginalFirstLineArtCalculation}
 */
@Ignore
public class OnOriginalFirstLineArtCalculationTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private RegimenManager regimenManager;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		executeDataSet("dataset/test-drugs.xml");

		regimenManager.refresh();
	}

	/**
	 * @see OnOriginalFirstLineArtCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCalculateCurrentArtRegimen() throws Exception {
		Concept azt = Context.getConceptService().getConcept(86663);
		Concept _3tc = Context.getConceptService().getConcept(78643);
		Concept efv = Context.getConceptService().getConcept(75523);
		Concept nvp = Context.getConceptService().getConcept(80586);
		Concept lpv = Context.getConceptService().getConcept(79040);
		Concept rtv = Context.getConceptService().getConcept(83412);

		// Give patient #6 initial regimen of AZT + 3TC + EFV
		EmrTestUtils.saveRegimenOrder(TestUtils.getPatient(6), Arrays.asList(azt, _3tc, efv), TestUtils.date(2011, 1, 1), TestUtils.date(2012, 1, 1));

		// Give patient #6 current regimen of same regimen
		EmrTestUtils.saveRegimenOrder(TestUtils.getPatient(6), Arrays.asList(azt, _3tc, efv), TestUtils.date(2012, 1, 1), null);

		// Give patient #7 initial regimen of AZT + 3TC + EFV
		EmrTestUtils.saveRegimenOrder(TestUtils.getPatient(7), Arrays.asList(azt, _3tc, efv), TestUtils.date(2011, 1, 1), TestUtils.date(2012, 1, 1));

		// Give patient #7 current regimen of AZT + 3TC + NVP (alternate first line)
		EmrTestUtils.saveRegimenOrder(TestUtils.getPatient(7), Arrays.asList(azt, _3tc, nvp), TestUtils.date(2012, 1, 1), null);

		// Give patient #8 initial regimen of AZT + 3TC + EFV
		EmrTestUtils.saveRegimenOrder(TestUtils.getPatient(8), Arrays.asList(azt, _3tc, efv), TestUtils.date(2011, 1, 1), TestUtils.date(2012, 1, 1));

		// Give patient #8 current regimen of AZT + 3TC + LPV/r (second line)
		EmrTestUtils.saveRegimenOrder(TestUtils.getPatient(8), Arrays.asList(azt, _3tc, lpv, rtv), TestUtils.date(2012, 1, 1), null);
		
		List<Integer> cohort = Arrays.asList(2, 6, 7, 8);

		CalculationResultMap resultMap = new OnOriginalFirstLineArtCalculation().evaluate(cohort, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertFalse((Boolean) resultMap.get(2).getValue()); // isn't on any drugs
		Assert.assertTrue((Boolean) resultMap.get(6).getValue());
		Assert.assertFalse((Boolean) resultMap.get(7).getValue());
		Assert.assertFalse((Boolean) resultMap.get(8).getValue());
	}
}