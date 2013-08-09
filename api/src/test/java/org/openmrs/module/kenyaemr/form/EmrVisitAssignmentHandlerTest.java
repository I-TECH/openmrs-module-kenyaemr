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

package org.openmrs.module.kenyaemr.form;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.module.kenyacore.form.FormManager;
import org.openmrs.module.kenyautil.MetadataUtils;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.form.EmrVisitAssignmentHandler}
 */
public class EmrVisitAssignmentHandlerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private FormManager formManager;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
	}

	@Test
	public void integration() throws Exception {

		TestUtils.saveGlobalProperty(OpenmrsConstants.GP_VISIT_ASSIGNMENT_HANDLER, EmrVisitAssignmentHandler.class.getName());

		// Give patient #2 a visit from 10am-12pm on 5th June
		Visit visit1 = TestUtils.saveVisit(TestUtils.getPatient(2), MetadataUtils.getVisitType(Metadata.OUTPATIENT_VISIT_TYPE), TestUtils.date(2012, 6, 5, 10, 0, 0), TestUtils.date(2012, 6, 5, 12, 0, 0));

		// Give patient #2 an unclosed visit from 10am onward on 7th June
		Visit visit2 = TestUtils.saveVisit(TestUtils.getPatient(2), MetadataUtils.getVisitType(Metadata.OUTPATIENT_VISIT_TYPE), TestUtils.date(2012, 6, 7, 10, 0, 0), null);

		// Give patient #2 an encounter at 11am on 5th June
		Encounter enc1 = TestUtils.saveEncounter(TestUtils.getPatient(2), MetadataUtils.getEncounterType(Metadata.TB_SCREENING_ENCOUNTER_TYPE), TestUtils.date(2012, 6, 5, 11, 0, 0));

		// Check that encounter was saved into visit #1
		Assert.assertThat(enc1.getVisit(), is(visit1));

		// Give patient #2 an encounter at 9am on 10th June
		Encounter enc2 = TestUtils.saveEncounter(TestUtils.getPatient(2), MetadataUtils.getEncounterType(Metadata.TB_SCREENING_ENCOUNTER_TYPE), TestUtils.date(2012, 6, 10, 9, 0, 0));

		// Check that encounter was saved into visit #2
		Assert.assertThat(enc2.getVisit(), is(visit2));
	}
}