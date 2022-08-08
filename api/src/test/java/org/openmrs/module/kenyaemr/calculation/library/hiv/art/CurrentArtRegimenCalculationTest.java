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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Tests for {@link CurrentArtRegimenCalculation}
 */
public class CurrentArtRegimenCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private TbMetadata tbMetadata;

	@Autowired
	private MchMetadata mchMetadata;

	protected static final Log log = LogFactory.getLog(OnArtCalculation.class);
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		hivMetadata.install();
		tbMetadata.install();
		mchMetadata.install();
	}

	/**
	 * @see CurrentArtRegimenCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCalculateCurrentArtRegimen() throws Exception {

		ConceptService cs = Context.getConceptService();
		EncounterService encounterService = Context.getEncounterService();

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);

		// Enroll patients #5, #6  in the HIV Program
		TestUtils.enrollInProgram(TestUtils.getPatient(2), hivProgram, TestUtils.date(2011, 1, 1));
		//TestUtils.enrollInProgram(TestUtils.getPatient(6), hivProgram, TestUtils.date(2011, 1, 1));

		// Give patient #6 an ARV regimen
		EncounterType type = encounterService.getEncounterTypeByUuid(CommonMetadata._EncounterType.DRUG_REGIMEN_EDITOR);
		Obs obs = new Obs();
		obs.setConcept(Dictionary.getConcept(Dictionary.ARV_TREATMENT_PLAN_EVENT_CONCEPT));
		obs.setObsDatetime(new Date());
		obs.setValueCoded(Dictionary.getConcept(Dictionary.START_DRUGS));
		TestUtils.saveEncounter(TestUtils.getPatient(6), type, new Date(), obs);

		// Enroll patients #8  in the TB Program
		TestUtils.enrollInProgram(TestUtils.getPatient(8), tbProgram, TestUtils.date(2011, 5, 15));

		// Give patient #8 an TB regimen
		Concept onTB = Dictionary.getConcept(Dictionary.TB_TREATMENT_PLAN_CONCEPT);
		TestUtils.saveObs(TestUtils.getPatient(8), onTB, 1256, TestUtils.date(2012, 1, 1));

		List<Integer> cohort = Arrays.asList(2, 6, 7, 8);

		//CalculationResultMap resultMap = new CurrentArtRegimenCalculation().evaluate(cohort, null, Context.getService(PatientCalculationService.class).createCalculationContext());

		CalculationResultMap resultMap = new EligibleForArtCalculation().evaluate(cohort, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertTrue((Boolean) resultMap.get(2).getValue()); // in HIV program with no ARV
		Assert.assertFalse((Boolean) resultMap.get(6).getValue()); // in HIV program but already on ART
		Assert.assertFalse((Boolean) resultMap.get(7).getValue()); // not in HIV Program
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // only in TB Program
	}
}