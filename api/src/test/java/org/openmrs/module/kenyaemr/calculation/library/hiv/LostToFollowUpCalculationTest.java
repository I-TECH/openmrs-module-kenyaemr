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
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests for {@link LostToFollowUpCalculation}
 */
public class LostToFollowUpCalculationTest extends BaseModuleContextSensitiveTest {

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
	 * @see LostToFollowUpCalculation#getFlagMessage()
	 */
	@Test
	public void getFlagMessage() {
		Assert.assertThat(new LostToFollowUpCalculation().getFlagMessage(), notNullValue());
	}

	/**
	 * @see LostToFollowUpCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies determine whether patients are lost to follow up
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientsAreLostToFollowUp() throws Exception {
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Concept returnVisitDate = Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE);
		Concept reasonForDiscontinuation = Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION);
		Concept dead = Dictionary.getConcept(Dictionary.DIED);
		Concept transferout = Dictionary.getConcept(Dictionary.TRANSFERRED_OUT) ;

		// Enroll patients #6, #7, #8 in the HIV Program
		TestUtils.enrollInProgram(TestUtils.getPatient(6), hivProgram, TestUtils.date(2011, 1, 1));
		TestUtils.enrollInProgram(TestUtils.getPatient(7), hivProgram, TestUtils.date(2011, 1, 1));
		TestUtils.enrollInProgram(TestUtils.getPatient(8), hivProgram, TestUtils.date(2011, 1, 1));

		//give patient 6 and 7 return date
		Obs[] returnDate6 = {TestUtils.saveObs(TestUtils.getPatient(6), returnVisitDate, TestUtils.date(2011, 4, 1), TestUtils.date(2011, 1, 1))};
		Obs[] returnDate7 = {TestUtils.saveObs(TestUtils.getPatient(7), returnVisitDate, TestUtils.date(2011, 4, 1), TestUtils.date(2011, 1, 1))};
		Obs[] discontinuation6 = {TestUtils.saveObs(TestUtils.getPatient(6), reasonForDiscontinuation, dead, TestUtils.date(2011, 1, 1))};
		Obs[] discontinuation7 = {TestUtils.saveObs(TestUtils.getPatient(7), reasonForDiscontinuation, dead, TestUtils.date(2011, 1, 1))};
		Obs[] discontinuation8 = {TestUtils.saveObs(TestUtils.getPatient(8), reasonForDiscontinuation, transferout, TestUtils.date(2011, 1, 1))};

		// Give patient #7 a scheduled encounter 200 days ago
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -200);
		EncounterType scheduledEncType = Context.getEncounterService().getEncounterType("Scheduled");
		TestUtils.saveEncounter(TestUtils.getPatient(7), scheduledEncType, calendar.getTime(), returnDate7);

		// Give patient #8 a scheduled encounter 10 days ago
		calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -10);
		TestUtils.saveEncounter(TestUtils.getPatient(8), scheduledEncType, calendar.getTime());

		List<Integer> ptIds = Arrays.asList(6, 7, 8, 999);

		CalculationResultMap resultMap = new LostToFollowUpCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertTrue((Boolean) resultMap.get(6).getValue()); // patient in HIV program and no encounters
		Assert.assertTrue((Boolean) resultMap.get(7).getValue()); // patient in HIV program and no encounter in last X days
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // patient in HIV program and is a transfer out
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // patient not in HIV Program
	}
}
