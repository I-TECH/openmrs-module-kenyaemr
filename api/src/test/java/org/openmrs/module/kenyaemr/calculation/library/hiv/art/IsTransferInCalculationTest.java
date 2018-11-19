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
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Test for {@link IsTransferInCalculation}
 */
public class IsTransferInCalculationTest extends BaseModuleContextSensitiveTest {
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
	 * @see IsTransferInCalculation#evaluate(Collection, Map, PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCalculateIsTransferInCalculation() throws Exception {

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Concept transferInStatus = Dictionary.getConcept(Dictionary.TRANSFER_IN);
		Concept yes = Dictionary.getConcept(Dictionary.YES);
		Concept no = Dictionary.getConcept(Dictionary.NO);

		//enroll patients into hiv program
		TestUtils.enrollInProgram(TestUtils.getPatient(2), hivProgram, TestUtils.date(2014, 3, 1));
		TestUtils.enrollInProgram(TestUtils.getPatient(7), hivProgram, TestUtils.date(2014, 3, 1));

		//make #2 a transfer in with the looking at the status
		TestUtils.saveObs(TestUtils.getPatient(2), transferInStatus, yes, TestUtils.date(2014, 3, 1));
		//give #7 a transfer in date
		TestUtils.saveObs(TestUtils.getPatient(7), transferInStatus, yes, TestUtils.date(2014, 3, 10));
		//make #8 not a transfer in
		TestUtils.saveObs(TestUtils.getPatient(8), transferInStatus, no, TestUtils.date(2014, 3, 10));


		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8, 999);
		CalculationResultMap resultMap = new IsTransferInCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertTrue((Boolean) resultMap.get(2).getValue()); // is a transfer in (yes)
		Assert.assertTrue((Boolean) resultMap.get(7).getValue()); // have a transfer in (yes)
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // not transfer in
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); //not having any obs
	}

}
