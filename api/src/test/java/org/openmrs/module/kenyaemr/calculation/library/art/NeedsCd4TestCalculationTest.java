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

package org.openmrs.module.kenyaemr.calculation.library.art;

import java.util.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class NeedsCd4TestCalculationTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
	}
	
	/**
	 * @see NeedsCd4TestCalculation#evaluate(Collection,Map,PatientCalculationContext)
	 * @verifies determine whether patients need a CD4
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientsNeedsCD4() throws Exception {

		// Get HIV Program
		Program hivProgram = Metadata.getProgram(Metadata.HIV_PROGRAM);

		// Enroll patients #6, #7 and #8  in the HIV Program
		PatientService ps = Context.getPatientService();
		
		TestUtils.enrollInProgram(ps.getPatient(2), hivProgram, new Date());
		TestUtils.enrollInProgram(ps.getPatient(6), hivProgram, new Date());
		TestUtils.enrollInProgram(ps.getPatient(7), hivProgram, new Date());
		TestUtils.enrollInProgram(ps.getPatient(8), hivProgram, new Date());
		
		
		// Give patient #7 a recent CD4 result obs
		Concept cd4 = Dictionary.getConcept(Dictionary.CD4_COUNT);
		TestUtils.saveObs(ps.getPatient(7), cd4, 123d, new Date());

		// Give patient #8 a CD4 result obs from a year ago
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -360);
		TestUtils.saveObs(ps.getPatient(8), cd4, 123d, calendar.getTime());
		
		//give patient #2 a recent CD4% result obs
		Concept cd4Percent = Dictionary.getConcept(Dictionary.CD4_PERCENT);
		TestUtils.saveObs(ps.getPatient(2), cd4Percent, 123d, new Date());
		
		// Give patient #6 a CD4% result obs from a year ago
		Calendar calendarP = Calendar.getInstance();
		calendarP.add(Calendar.DATE, -200);
		TestUtils.saveObs(ps.getPatient(6), cd4Percent, 123d, calendarP.getTime());
		
		Context.flushSession();
		
		List<Integer> ptIds = Arrays.asList(2,6,7,8,999);
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new NeedsCd4TestCalculation());
		Assert.assertFalse((Boolean) resultMap.get(2).getValue()); // has recent CD4%
		Assert.assertTrue((Boolean) resultMap.get(6).getValue()); // has old CD4%
		Assert.assertFalse((Boolean) resultMap.get(7).getValue()); // has recent CD4
		Assert.assertTrue((Boolean) resultMap.get(8).getValue()); // has old CD4
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // not in HIV Program
	}
}