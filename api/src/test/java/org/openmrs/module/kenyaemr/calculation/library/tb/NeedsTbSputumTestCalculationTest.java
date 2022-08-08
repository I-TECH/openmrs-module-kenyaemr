/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.tb;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests for {@link NeedsTbSputumTestCalculation}
 */
public class NeedsTbSputumTestCalculationTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private TbMetadata tbMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		hivMetadata.install();
		tbMetadata.install();
	}

	/**
	 * @see NeedsTbSputumTestCalculation#getFlagMessage()
	 */
	@Test
	public void getFlagMessage() {
		Assert.assertThat(new NeedsTbSputumTestCalculation().getFlagMessage(), notNullValue());
	}

	/**
	 * @see NeedsTbSputumTestCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientsNeedsTbSputumTest() throws Exception {

		// Get concepts
		Concept tbDiseaseStatus = Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS);
		Concept diseaseSuspected = Dictionary.getConcept(Dictionary.DISEASE_SUSPECTED);
		Concept diseaseunknown = Dictionary.getConcept(Dictionary.UNKNOWN);

		//get Tb program
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

		//enroll patient 2 into tb program
		TestUtils.enrollInProgram(TestUtils.getPatient(2), tbProgram, TestUtils.date(2014, 7, 1));
		TestUtils.enrollInProgram(TestUtils.getPatient(6), hivProgram, TestUtils.date(2014, 7, 1));

		// Screen patient #2 on May 31st
		TestUtils.saveObs(TestUtils.getPatient(2), tbDiseaseStatus, diseaseSuspected, TestUtils.date(2014, 7, 10));

		// Screen patient #6 on June 1st
		TestUtils.saveObs(TestUtils.getPatient(6), tbDiseaseStatus, diseaseSuspected, TestUtils.date(2014, 7, 15));

		// Screen patient #7 on June 30th
		TestUtils.saveObs(TestUtils.getPatient(7), tbDiseaseStatus, diseaseunknown, TestUtils.date(2014, 7, 30));

		List<Integer> ptIds = Arrays.asList(2,6, 7, 8, 999);
		CalculationResultMap resultMap = new NeedsTbSputumTestCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertFalse((Boolean) resultMap.get(2).getValue()); // is in Tb program, meaning tb status is known
		Assert.assertTrue((Boolean) resultMap.get(6).getValue()); // is a suspect and in HIV program
		Assert.assertFalse((Boolean) resultMap.get(7).getValue()); // NOT a Tb suspect
	}

}
