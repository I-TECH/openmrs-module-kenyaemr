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
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link NeedsPcrTestCalculation}
 */
public class NeedsPcrTestCalculationTest extends BaseModuleContextSensitiveTest {

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
	 * @see NeedsPcrTestCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate last recorded pcr test for infant patients
	 */
	@Test
	public void evaluate_shouldCalculatePcrTest() throws Exception {
		//get mchcs program
		Program mchcsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHCS);
		//set the birth date of patient #6 to be above 6 weeks and below or equal to 9 months
		TestUtils.getPatient(6).setBirthdate(TestUtils.date(2015, 1, 1));

		// Enroll patients #6 and  #7  in the mchcs Program
		TestUtils.enrollInProgram(TestUtils.getPatient(6), mchcsProgram, TestUtils.date(2015, 4, 1));
		TestUtils.enrollInProgram(TestUtils.getPatient(7), mchcsProgram, TestUtils.date(2015, 1, 1));
		TestUtils.enrollInProgram(TestUtils.getPatient(8), mchcsProgram, TestUtils.date(2014, 1, 1));

		//get the HIV status of the infant and the if wheather pcr was done or NOT
		Concept infantHivStatus = Dictionary.getConcept(Dictionary.CHILDS_CURRENT_HIV_STATUS);
		Concept pcrStatus = Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION);

		//make patient #6 be HEI and has no pcr results
		TestUtils.saveObs(TestUtils.getPatient(6),infantHivStatus,Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV), TestUtils.date(2015, 4, 1));
		//TestUtils.saveObs(ps.getPatient(6),pcrStatus,Dictionary.getConcept(null),new Date());

		//make patient #7 be HEI and has has pcr results
		TestUtils.saveObs(TestUtils.getPatient(7),infantHivStatus,Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV), TestUtils.date(2015, 4, 1));
		TestUtils.saveObs(TestUtils.getPatient(7),pcrStatus,Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION), TestUtils.date(2015, 4, 1));

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(6, 7,8);
		PatientCalculationService service = Context.getService(PatientCalculationService.class);
		PatientCalculationContext context = service.createCalculationContext();
		context.setNow(TestUtils.date(2015, 4, 30));

		CalculationResultMap resultMap =  new NeedsPcrTestCalculation().evaluate(ptIds, null, context);
		Assert.assertTrue((Boolean) resultMap.get(6).getValue()); // HEI and has null pcr
		Assert.assertFalse((Boolean) resultMap.get(7).getValue()); // in HEI but pcr is done
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // Not HEI
	}
}