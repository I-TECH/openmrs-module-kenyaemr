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

package org.openmrs.module.kenyaemr.calculation.art;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class InitialArtStartDateCalculationTest extends BaseModuleContextSensitiveTest {

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");
	}

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.art.InitialArtStartDateCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCalculateInitialArtStartDate() throws Exception {

		PatientService ps = Context.getPatientService();
		Concept arvStartDate = Context.getConceptService().getConceptByUuid(MetadataConstants.ANTIRETROVIRAL_TREATMENT_START_DATE_CONCEPT_UUID);

		// Put patient #6 on Aspirin
		Concept aspirin = Context.getConceptService().getConcept(71617);
		TestUtils.saveDrugOrder(ps.getPatient(6), aspirin, TestUtils.date(2011, 1, 1), null);

		// Put patient #7 and #8 on Stavudine
		Concept stavudine = Context.getConceptService().getConcept(84309);
		TestUtils.saveDrugOrder(ps.getPatient(7), stavudine, TestUtils.date(2011, 1, 1), null);
		TestUtils.saveDrugOrder(ps.getPatient(8), stavudine, TestUtils.date(2011, 1, 1), null);

		// Give patient #8 an earlier date in a recent START DATE obs
		TestUtils.saveObs(Context.getPatientService().getPatient(8), arvStartDate, TestUtils.date(2007, 7, 7), new Date());

		// Give patient #999 only a date in a recent START DATE obs
		TestUtils.saveObs(Context.getPatientService().getPatient(999), arvStartDate, TestUtils.date(2008, 8, 8), new Date());

		Context.flushSession();
		
		List<Integer> cohort = Arrays.asList(2, 6, 7, 8, 999);

		CalculationResultMap resultMap = new InitialArtStartDateCalculation().evaluate(cohort, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertNull(resultMap.get(2)); // isn't on any drugs
		Assert.assertNull(resultMap.get(6)); // isn't on any ART drugs
		Assert.assertEquals(TestUtils.date(2011, 1, 1), resultMap.get(7).getValue());
		Assert.assertEquals(TestUtils.date(2007, 7, 7), resultMap.get(8).getValue());
		Assert.assertEquals(TestUtils.date(2008, 8, 8), resultMap.get(999).getValue());
	}
}