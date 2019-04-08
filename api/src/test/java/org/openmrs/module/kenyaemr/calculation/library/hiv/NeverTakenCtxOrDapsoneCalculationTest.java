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
import org.openmrs.EncounterType;
import org.openmrs.Obs;
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
import java.util.Date;
import java.util.List;

/**
 * Tests for {@link NeverTakenCtxOrDapsoneCalculation}
 */
public class NeverTakenCtxOrDapsoneCalculationTest extends BaseModuleContextSensitiveTest {

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
	 * @see NeverTakenCtxOrDapsoneCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate() throws Exception {
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

		// Enroll patients #2, #6, #7 in the HIV Program
		TestUtils.enrollInProgram(TestUtils.getPatient(2), hivProgram, TestUtils.date(2011, 1, 1));
		TestUtils.enrollInProgram(TestUtils.getPatient(6), hivProgram, TestUtils.date(2011, 1, 1));
		TestUtils.enrollInProgram(TestUtils.getPatient(7), hivProgram, TestUtils.date(2011, 1, 1));
		//give patient # 7 an encounter
		EncounterType type = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_CONSULTATION);
		Obs obs = new Obs();
		obs.setConcept(Dictionary.getConcept(Dictionary.CIVIL_STATUS));
		obs.setObsDatetime(new Date());
		obs.setValueCoded(Dictionary.getConcept(Dictionary.MARRIED_MONOGAMOUS));
		TestUtils.saveEncounter(TestUtils.getPatient(7), type, new Date(), obs);

		// Give patient #2 CTX dispensed obs
		Concept ctxDispensed = Dictionary.getConcept(Dictionary.COTRIMOXAZOLE_DISPENSED);
		Concept yes = Dictionary.getConcept(Dictionary.YES);
		TestUtils.saveObs(TestUtils.getPatient(2), ctxDispensed, yes, TestUtils.date(2011, 1, 1));

		// Give patient #6 Dapsone med order obs
		Concept medOrders = Dictionary.getConcept(Dictionary.MEDICATION_ORDERS);
		Concept dapsone = Dictionary.getConcept(Dictionary.DAPSONE);
		TestUtils.saveObs(TestUtils.getPatient(6), medOrders, dapsone, TestUtils.date(2011, 1, 1));
		
		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8);

		CalculationResultMap resultMap = new NeverTakenCtxOrDapsoneCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertFalse((Boolean) resultMap.get(2).getValue()); // has prophalaxis obs = yes
		Assert.assertFalse((Boolean) resultMap.get(6).getValue()); // has med order for Dapsone
		Assert.assertTrue((Boolean) resultMap.get(7).getValue());
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // is not in HIV program
	}
}