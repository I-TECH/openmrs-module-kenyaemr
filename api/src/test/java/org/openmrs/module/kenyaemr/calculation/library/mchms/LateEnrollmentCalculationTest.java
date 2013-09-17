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

package org.openmrs.module.kenyaemr.calculation.library.mchms;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.calculation.library.mchms.LateEnrollmentCalculation}
 */
public class LateEnrollmentCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		executeDataSet("dataset/test-metadata.xml");
	}

	/**
	 * @verifies determine whether MCH-MS patients enrolled at gestation greater than 28 weeks
	 * @see org.openmrs.module.kenyaemr.calculation.library.mchms.LateEnrollmentCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientsEnrolledLate() throws Exception {

		// Get the MCH-MS program, enrollment encounter type and enrollment form
		Program mchmsProgram = MetadataUtils.getProgram(Metadata.Program.MCHMS);
		EncounterType enrollmentEncounterType = MetadataUtils.getEncounterType(Metadata.EncounterType.MCHMS_ENROLLMENT);
		Form enrollmentForm = MetadataUtils.getForm(Metadata.Form.MCHMS_ENROLLMENT);

		//Enroll #6 and #7 into MCH-MS
		PatientService patientService = Context.getPatientService();

		for (int i = 6; i <= 7; i++) {
			TestUtils.enrollInProgram(patientService.getPatient(i), mchmsProgram, new Date());
		}

		//Get the LMP concept
		Concept lmp = Dictionary.getConcept(Dictionary.LAST_MONTHLY_PERIOD);

		//Prepare times since last LMP (10 weeks ago and 30 weeks ago)
		Calendar tenWeeksAgo = Calendar.getInstance();
		tenWeeksAgo.add(Calendar.DATE, -70);
		Calendar twentyWeeksAgo = Calendar.getInstance();
		twentyWeeksAgo.add(Calendar.DATE, -210);

		//Create enrollment encounter for Pat#6 indicating HIV status + and LMP 10 weeks ago
		Obs[] encounterObss6 = {TestUtils.saveObs(patientService.getPatient(6), lmp, tenWeeksAgo.getTime(), new Date())};
		TestUtils.saveEncounter(patientService.getPatient(6), enrollmentEncounterType, enrollmentForm, new Date(), encounterObss6);

		//Create enrollment encounter for Pat#7 indicating HIV status + and LMP 30 weeks ago
		Obs[] encounterObss7 = {TestUtils.saveObs(patientService.getPatient(7), lmp, twentyWeeksAgo.getTime(), new Date())};
		TestUtils.saveEncounter(patientService.getPatient(7), enrollmentEncounterType, enrollmentForm, new Date(), encounterObss7);

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(6, 7, 999);

		//Run LateEnrollmentCalculation with these test patients
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new LateEnrollmentCalculation());

		Assert.assertFalse((Boolean) resultMap.get(6).getValue()); //Enrolled at 10 weeks gestation - not late enrollment
		Assert.assertTrue((Boolean) resultMap.get(7).getValue()); //Enrolled at 30 weeks gestation - late enrollment
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // Not in MCH-MS Program
	}
}
