/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.mchcs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Tests for {@link NeedsAntibodyTestCalculation}
 */
public class NeedsAntibodyTestCalculationTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private MchMetadata mchMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		mchMetadata.install();
	}

	/**
	 * @see NeedsAntibodyTestCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate last recorded antibody test at 9 months
	 */
	@Test
	public void evaluate_shouldCalculateNeedsAntibodyTest() throws Exception {
		//get mchcs program
		Program mchcsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHCS);

		//set the birthdate of #6 to be this year
		TestUtils.getPatient(6).setBirthdate(TestUtils.date(2013, 6, 1));

		// Enroll patients #6 and  #7 in the mchcs Program
		TestUtils.enrollInProgram(TestUtils.getPatient(6), mchcsProgram, TestUtils.date(2015, 1, 1));
		TestUtils.enrollInProgram(TestUtils.getPatient(7), mchcsProgram, TestUtils.date(2011, 1, 1));
		TestUtils.enrollInProgram(TestUtils.getPatient(8), mchcsProgram, TestUtils.date(2011, 1, 1));

		//get the HIV status of the infant and the if wheather antibody test was done or NOT
		Concept infantHivStatus = Dictionary.getConcept(Dictionary.CHILDS_CURRENT_HIV_STATUS);
		Concept antibodytest1 = Dictionary.getConcept(Dictionary.HIV_RAPID_TEST_1_QUALITATIVE);


		//make #6 HEI and has no antibody test
		TestUtils.saveObs(TestUtils.getPatient(6),infantHivStatus,Dictionary.getConcept(Dictionary.UNKNOWN),TestUtils.date(2015, 1 ,1));
		//make #7 HEI and has antibody test2
		TestUtils.saveObs(TestUtils.getPatient(7),infantHivStatus,Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV),new Date());
		TestUtils.saveObs(TestUtils.getPatient(7),antibodytest1,Dictionary.getConcept(Dictionary.NEGATIVE),new Date());
		//make #8 HEI and has antibody test2
		TestUtils.saveObs(TestUtils.getPatient(8),infantHivStatus,Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV),new Date());
		TestUtils.saveObs(TestUtils.getPatient(8),antibodytest1,Dictionary.getConcept(Dictionary.POSITIVE),new Date());

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(6, 7, 8);
		PatientCalculationService patientCalculationService = Context.getService(PatientCalculationService.class);
		PatientCalculationContext context = patientCalculationService.createCalculationContext();
		context.setNow(TestUtils.date(2015, 6 ,1));

		CalculationResultMap resultMap = new NeedsAntibodyTestCalculation().evaluate(ptIds, null, context);
		Assert.assertTrue((Boolean) resultMap.get(6).getValue()); // HEI and has null antibody and  age >=18 months
		Assert.assertFalse((Boolean) resultMap.get(7).getValue()); //has antibody 1
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // has antibody 2
	}
}