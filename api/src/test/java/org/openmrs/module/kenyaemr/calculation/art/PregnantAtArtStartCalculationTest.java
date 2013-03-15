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
import org.openmrs.module.kenyaemr.calculation.IsPregnantCalculation;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

public class PregnantAtArtStartCalculationTest extends BaseModuleContextSensitiveTest {

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");
	}

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.art.PregnantAtArtStartCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate recorded pregnancy status at ART start for all patients
	 */
	@Test
	public void evaluate_shouldCalculatePregnancyStatusAtArtStart() throws Exception {

		PatientService ps = Context.getPatientService();
		Concept pregnancyStatus = Context.getConceptService().getConceptByUuid(MetadataConstants.PREGNANCY_STATUS_CONCEPT_UUID);
		Concept yes = Context.getConceptService().getConceptByUuid(MetadataConstants.YES_CONCEPT_UUID);
		Concept no = Context.getConceptService().getConceptByUuid(MetadataConstants.NO_CONCEPT_UUID);
		Concept stavudine = Context.getConceptService().getConcept(84309);

		// Give patient #2 a YES status on same day as ART start
		TestUtils.saveObs(ps.getPatient(2), pregnancyStatus, yes, TestUtils.date(2012, 1, 1));
		TestUtils.saveDrugOrder(Context.getPatientService().getPatient(2), stavudine, TestUtils.date(2012, 1, 1), null);

		// Give patient #6 a YES status week before ART start
		TestUtils.saveObs(ps.getPatient(6), pregnancyStatus, yes, TestUtils.date(2012, 1, 1));
		TestUtils.saveDrugOrder(Context.getPatientService().getPatient(6), stavudine, TestUtils.date(2012, 1, 8), null);

		// Give patient #7 a YES but a newer NO status before ART start
		TestUtils.saveObs(ps.getPatient(7), pregnancyStatus, yes, TestUtils.date(2012, 1, 1));
		TestUtils.saveObs(ps.getPatient(7), pregnancyStatus, no, TestUtils.date(2012, 1, 3));
		TestUtils.saveDrugOrder(Context.getPatientService().getPatient(7), stavudine, TestUtils.date(2012, 1, 8), null);

		// Give patient #8 a YES status week before ART start and a newer NO status after ART start
		TestUtils.saveObs(ps.getPatient(8), pregnancyStatus, yes, TestUtils.date(2012, 1, 1));
		TestUtils.saveObs(ps.getPatient(8), pregnancyStatus, no, TestUtils.date(2012, 1, 15));
		TestUtils.saveDrugOrder(Context.getPatientService().getPatient(8), stavudine, TestUtils.date(2012, 1, 8), null);
		
		Context.flushSession();
		
		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8, 999);
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new PregnantAtArtStartCalculation());
		Assert.assertTrue((Boolean) resultMap.get(2).getValue());
		Assert.assertTrue((Boolean) resultMap.get(6).getValue());
		Assert.assertFalse((Boolean) resultMap.get(7).getValue());
		Assert.assertTrue((Boolean) resultMap.get(8).getValue());
		Assert.assertNull(resultMap.get(999)); // has no recorded status
	}
}