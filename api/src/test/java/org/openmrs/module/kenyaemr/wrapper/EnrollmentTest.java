/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.wrapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link Enrollment}
 */
public class EnrollmentTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private TbMetadata tbMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		tbMetadata.install();
	}

	/**
	 * @see Enrollment#firstObs(org.openmrs.Concept)
	 */
	@Test
	public void firstObs_shouldFindFirstObsWithConcept() {
		Patient patient = TestUtils.getPatient(6);
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);

		Enrollment enrollment = new Enrollment(TestUtils.enrollInProgram(patient, tbProgram, TestUtils.date(2012, 1, 1), TestUtils.date(2012, 4, 1)));

		// Test with no saved obs
		Assert.assertNull(enrollment.firstObs(Dictionary.getConcept(Dictionary.CD4_COUNT)));

		// Before enrollment
		Obs obs0 = TestUtils.saveObs(patient, Dictionary.getConcept(Dictionary.CD4_COUNT), 123.0, TestUtils.date(2011, 12, 1));
		// Wrong concept
		Obs obs1 = TestUtils.saveObs(patient, Dictionary.getConcept(Dictionary.CD4_PERCENT), 50.0, TestUtils.date(2012, 1, 15));
		// During enrollment
		Obs obs2 = TestUtils.saveObs(patient, Dictionary.getConcept(Dictionary.CD4_COUNT), 234.0, TestUtils.date(2012, 2, 1));
		Obs obs3 = TestUtils.saveObs(patient, Dictionary.getConcept(Dictionary.CD4_COUNT), 345.0, TestUtils.date(2012, 3, 1));

		Assert.assertEquals(obs2, enrollment.firstObs(Dictionary.getConcept(Dictionary.CD4_COUNT)));

		// Test again with no enrollment end date
		enrollment = new Enrollment(TestUtils.enrollInProgram(patient, tbProgram, TestUtils.date(2012, 1, 1)));
		Assert.assertEquals(obs2, enrollment.firstObs(Dictionary.getConcept(Dictionary.CD4_COUNT)));
	}

	/**
	 * @see Enrollment#lastEncounter(org.openmrs.EncounterType)
	 */
	@Test
	public void lastEncounter_shouldFindLastEncounterWithType() {
		Patient patient = TestUtils.getPatient(6);
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
		EncounterType tbScreenEncType = MetadataUtils.existing(EncounterType.class, TbMetadata._EncounterType.TB_SCREENING);

		Enrollment enrollment = new Enrollment(TestUtils.enrollInProgram(patient, tbProgram, TestUtils.date(2012, 1, 1), TestUtils.date(2012, 4, 1)));

		// Test with no saved encounters
		Assert.assertNull(enrollment.lastEncounter(tbScreenEncType));

		// Before enrollment
		Encounter enc0 = TestUtils.saveEncounter(patient, tbScreenEncType, TestUtils.date(2011, 12, 1));
		// During enrollment
		Encounter enc1 = TestUtils.saveEncounter(patient, tbScreenEncType, TestUtils.date(2012, 2, 1));
		Encounter enc2 = TestUtils.saveEncounter(patient, tbScreenEncType, TestUtils.date(2012, 3, 1));
		// After enrollment
		Encounter enc3 = TestUtils.saveEncounter(patient, tbScreenEncType, TestUtils.date(2012, 5, 1));

		Assert.assertEquals(enc2, enrollment.lastEncounter(tbScreenEncType));

		// Test again with no enrollment end date
		enrollment = new Enrollment(TestUtils.enrollInProgram(patient, tbProgram, TestUtils.date(2012, 1, 1)));
		Assert.assertEquals(enc3, enrollment.lastEncounter(tbScreenEncType));
	}
}