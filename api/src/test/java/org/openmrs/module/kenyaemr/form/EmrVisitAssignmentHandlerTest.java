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
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.form.EmrVisitAssignmentHandler}
 */
public class EmrVisitAssignmentHandlerTest extends BaseModuleContextSensitiveTest {

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
		Visit visit1 = TestUtils.saveVisit(TestUtils.getPatient(2), MetadataUtils.getVisitType(CommonMetadata._VisitType.OUTPATIENT), TestUtils.date(2012, 6, 5, 10, 0, 0), TestUtils.date(2012, 6, 5, 12, 0, 0));

		// Give patient #2 an unclosed visit from 10am onward on 7th June
		Visit visit2 = TestUtils.saveVisit(TestUtils.getPatient(2), MetadataUtils.getVisitType(CommonMetadata._VisitType.OUTPATIENT), TestUtils.date(2012, 6, 7, 10, 0, 0), null);

		// Give patient #2 an encounter at 11am on 5th June
		Encounter enc1 = TestUtils.saveEncounter(TestUtils.getPatient(2), MetadataUtils.getEncounterType(TbMetadata._EncounterType.TB_SCREENING), TestUtils.date(2012, 6, 5, 11, 0, 0));

		// Check that encounter was saved into visit #1
		Assert.assertThat(enc1.getVisit(), is(visit1));

		// Give patient #2 an encounter at 9am on 10th June
		Encounter enc2 = TestUtils.saveEncounter(TestUtils.getPatient(2), MetadataUtils.getEncounterType(TbMetadata._EncounterType.TB_SCREENING), TestUtils.date(2012, 6, 10, 9, 0, 0));

		// Check that encounter was saved into visit #2
		Assert.assertThat(enc2.getVisit(), is(visit2));
	}

	/**
	 * @see EmrVisitAssignmentHandler#getAutoCreateVisitType(org.openmrs.Encounter)
	 */
	@Test
	public void getAutoCreateVisitType_shouldReturnAutoCreateVisitTypeIfSpecified() {
		// Check form that doesn't specify one
		Encounter hivAddendumEnc = new Encounter();
		hivAddendumEnc.setForm(MetadataUtils.getForm(HivMetadata._Form.CLINICAL_ENCOUNTER_HIV_ADDENDUM));

		Assert.assertThat(EmrVisitAssignmentHandler.getAutoCreateVisitType(hivAddendumEnc), is(nullValue()));

		// TODO figure out how to mock FormManager so we can test a form with an auto-create visit type

		// Check form that does specify one
		//Encounter moh257Enc = new Encounter();
		//moh257Enc.setForm(MetadataUtils.getForm(Metadata.MOH_257_VISIT_SUMMARY));

		//Assert.assertThat(EmrVisitAssignmentHandler.getAutoCreateVisitType(moh257Enc), is(MetadataUtils.getVisitType(Metadata.OUTPATIENT)));
	}

	/**
	 * @see EmrVisitAssignmentHandler#canBeSavedInVisit(org.openmrs.Encounter, org.openmrs.Visit)
	 */
	@Test
	public void canBeSavedInVisit() {
		Encounter enc = new Encounter();

		// Visit starts at 10am on 1-Jun-2012
		Visit visit = new Visit();
		visit.setStartDatetime(TestUtils.date(2012, 6, 1, 10, 0, 0));

		// Check encounter at 9am can't be saved into that
		enc.setEncounterDatetime(TestUtils.date(2012, 6, 1, 9, 0, 0));
		Assert.assertThat(EmrVisitAssignmentHandler.canBeSavedInVisit(enc, visit), is(false));

		// Check encounter at 10am can
		enc.setEncounterDatetime(TestUtils.date(2012, 6, 1, 10, 0, 0));
		Assert.assertThat(EmrVisitAssignmentHandler.canBeSavedInVisit(enc, visit), is(true));

		// Check encounter at 11am can
		enc.setEncounterDatetime(TestUtils.date(2012, 6, 1, 11, 0, 0));
		Assert.assertThat(EmrVisitAssignmentHandler.canBeSavedInVisit(enc, visit), is(true));

		// Even if visit now ends at 11am
		visit.setStopDatetime(TestUtils.date(2012, 6, 1, 11, 0, 0));
		Assert.assertThat(EmrVisitAssignmentHandler.canBeSavedInVisit(enc, visit), is(true));

		// Check encounter at 12pm can't
		enc.setEncounterDatetime(TestUtils.date(2012, 6, 1, 12, 0, 0));
		Assert.assertThat(EmrVisitAssignmentHandler.canBeSavedInVisit(enc, visit), is(false));
	}
}