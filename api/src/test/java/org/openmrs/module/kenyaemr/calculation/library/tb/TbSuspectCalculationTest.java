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
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link TBSuspectCalculation}
 */

public class TbSuspectCalculationTest extends BaseModuleContextSensitiveTest{
	
	@Autowired
	private TbMetadata tbMetadata;
	
	@Before
	public void setup() throws Exception{
		
		executeDataSet("dataset/test-concepts.xml");

		tbMetadata.install();
		
	}
	
	@Test
	public void evaluate_shouldDetermineWhetherPatientIsTbSuspectAndNotEnrolledInTBProgram() {
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);

		Concept tbDiseaseStatus = Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS);		
		Concept tbSuspect = Dictionary.getConcept(Dictionary.DISEASE_SUSPECTED);		
		Concept onTbTreatment = Dictionary.getConcept(Dictionary.ON_TREATMENT_FOR_DISEASE);
		
		//Enroll patient #6 in TB Program
		TestUtils.enrollInProgram(TestUtils.getPatient(6), tbProgram, TestUtils.date(2012, 1, 1));		
		TestUtils.saveObs(TestUtils.getPatient(6), tbDiseaseStatus, tbSuspect, TestUtils.date(2012, 5, 1));
		
		//Enroll Patient #7 in TB Program but also discontinue them
		TestUtils.enrollInProgram(TestUtils.getPatient(7), tbProgram, TestUtils.date(2012, 1, 1),TestUtils.date(2012,  6, 1));
		TestUtils.saveObs(TestUtils.getPatient(7), tbDiseaseStatus, tbSuspect, TestUtils.date(2012, 5, 1));

		//Enroll patient #8 in TB Program
		TestUtils.enrollInProgram(TestUtils.getPatient(8), tbProgram, TestUtils.date(2012, 1, 1));
		TestUtils.saveObs(TestUtils.getPatient(8), tbDiseaseStatus, onTbTreatment, TestUtils.date(2012, 5, 1));

		List<Integer> cohort = Arrays.asList(6,7,8);						
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(cohort, new TbSuspectCalculation());
		
		Assert.assertFalse((Boolean) resultMap.get(6).getValue());		
		Assert.assertTrue((Boolean) resultMap.get(7).getValue());		
		Assert.assertFalse((Boolean) resultMap.get(8).getValue());
		
	}

}
