/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.mchms;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
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
import java.util.Date;
import java.util.List;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.calculation.library.mchms.MchmsFirstVisitDateCalculation}
 */
public class MchmsFirstVisitDateCalculationTest extends BaseModuleContextSensitiveTest {

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
	 * @verifies determine when MCH-MS patients had their first ANC visit
	 * @see org.openmrs.module.kenyaemr.calculation.library.mchms.MchmsFirstVisitDateCalculation#evaluate (
	 *      java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldDetermineWhenAPatientsFirstAncVisitDateWas() throws Exception {
		// Get the MCH-MS program, enrollment and consultation encounter types and enrollment and delivery forms
		Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);
		EncounterType enrollmentEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_ENROLLMENT);
		EncounterType consultationEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION);

		//Prepare dates to use for testing
		Date enrollmentDate = TestUtils.date(2012, 1, 1);

		Concept whoStageConcept = Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE);
		Concept cd4CountConcept = Dictionary.getConcept(Dictionary.CD4_COUNT);

		//Enroll #6, #7 and #8 into MCH-MS and cre
		for (int i = 6; i <= 8; i++) {
			TestUtils.enrollInProgram(TestUtils.getPatient(i), mchmsProgram, enrollmentDate);
			Patient patient = TestUtils.getPatient(i);
			EncounterType encounterType = null;
			if (i ==7) {
				encounterType = enrollmentEncounterType;
				Obs[] whoStagingAndCd4Obss = {TestUtils.saveObs(patient, cd4CountConcept, 500.00, enrollmentDate),
						TestUtils.saveObs(patient, whoStageConcept, Dictionary.getConcept(Dictionary.WHO_STAGE_1_ADULT), enrollmentDate)};
				TestUtils.saveEncounter(patient, encounterType, enrollmentDate, whoStagingAndCd4Obss);
			} else if (i == 8) {
				encounterType = consultationEncounterType;
				Obs[] whoStagingAndCd4Obss = {TestUtils.saveObs(patient, cd4CountConcept, 500.00, enrollmentDate),
						TestUtils.saveObs(patient, whoStageConcept, Dictionary.getConcept(Dictionary.WHO_STAGE_1_ADULT), enrollmentDate)};
				TestUtils.saveEncounter(patient, encounterType, enrollmentDate, whoStagingAndCd4Obss);
			}

		}

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8, 999);

		CalculationResultMap resultMap = new MchmsFirstVisitDateCalculation().evaluate(ptIds, null,
				Context.getService(PatientCalculationService.class).createCalculationContext());

		Assert.assertNull(resultMap.get(2)); // Not enrolled into MCHMS
		Assert.assertNull(resultMap.get(6));  //No encounters
		Assert.assertNull(resultMap.get(7));  //Only has enrollment encounter
		Assert.assertEquals(enrollmentDate, resultMap.get(8).getValue()); //has a consultation encounter recorded on the enrollment date
		Assert.assertNull(resultMap.get(999)); // Void patient
	}
}