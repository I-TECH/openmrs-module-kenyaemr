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
