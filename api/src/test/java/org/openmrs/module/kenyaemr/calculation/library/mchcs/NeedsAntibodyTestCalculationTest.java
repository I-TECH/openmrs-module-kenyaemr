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
package org.openmrs.module.kenyaemr.calculation.library.mchcs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Patient;
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
import java.util.Date;
import java.util.List;

/**
 * Tests for {@link NeedsAntibodyTestCalculation}
 */

public class NeedsAntibodyTestCalculationTest extends BaseModuleContextSensitiveTest {

	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		executeDataSet("dataset/test-metadata.xml");
	}
	/**
	 * @see org.openmrs.module.kenyaemr.calculation.library.NeedsAntibodyTestCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate last recorded antibody test at 9 months
	 */
	@Test
	public void evaluate_shouldCalculateNeedsAntibodyTest() throws Exception {
		//get mchcs program
		Program mchcsProgram = MetadataUtils.getProgram(Metadata.Program.MCHCS);
		//get the patient age from the birthdate
		Patient patient = TestUtils.getPatient(6);
		patient.setBirthdate(TestUtils.date(2011, 10, 1));// more than 9 months old


		// Enroll patients #6 and  #7  in the mchcs Program
		PatientService ps = Context.getPatientService();
		TestUtils.enrollInProgram(ps.getPatient(6), mchcsProgram, new Date());
		TestUtils.enrollInProgram(ps.getPatient(7), mchcsProgram, new Date());
		TestUtils.enrollInProgram(ps.getPatient(8), mchcsProgram, new Date());

		//get the HIV status of the infant and the if wheather antibody test was done or NOT
		Concept infantHivStatus = Dictionary.getConcept(Dictionary.CHILDS_CURRENT_HIV_STATUS);
		Concept antibodytest1 = Dictionary.getConcept(Dictionary.HIV_RAPID_TEST_1_QUALITATIVE);
		Concept antibodytest2 = Dictionary.getConcept(Dictionary.HIV_RAPID_TEST_2_QUALITATIVE);

		//make #6 HEI and has no antibody test
		TestUtils.saveObs(ps.getPatient(6),infantHivStatus,Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV),new Date());
		//make #7 HEI and has antibody test2
		TestUtils.saveObs(ps.getPatient(7),infantHivStatus,Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV),new Date());
		TestUtils.saveObs(ps.getPatient(7),antibodytest1,Dictionary.getConcept(Dictionary.NEGATIVE),new Date());
		//make #8 HEI and has antibody test2
		TestUtils.saveObs(ps.getPatient(8),infantHivStatus,Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV),new Date());
		TestUtils.saveObs(ps.getPatient(8),antibodytest2,Dictionary.getConcept(Dictionary.POSITIVE),new Date());

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(6, 7, 8);
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new NeedsAntibodyTestCalculation());
		Assert.assertTrue((Boolean) resultMap.get(6).getValue()); // HEI and has null antibody and is >=9 months
		Assert.assertFalse((Boolean) resultMap.get(7).getValue()); //has antibody 1
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // has antibody 2

	}
}
