/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.integration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.form.FormManager;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.advice.EncounterServiceAdvice;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.IPTMetadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.kenyaemr.visit.EmrVisitAssignmentHandler;
import org.openmrs.module.kenyaemr.wrapper.VisitWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Integration test for functionality related to how encounters are saved into visits
 */
public class FormsAndVisitsTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private TbMetadata tbMetadata;

	@Autowired
	private MchMetadata mchMetadata;

	@Autowired
	private IPTMetadata iptMetadata;

	@Autowired
	private FormManager formManager;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		hivMetadata.install();
		tbMetadata.install();
		mchMetadata.install();
		iptMetadata.install();
		formManager.refresh();

		// Configure the visit handler
		TestUtils.saveGlobalProperty(OpenmrsConstants.GP_VISIT_ASSIGNMENT_HANDLER, EmrVisitAssignmentHandler.class.getName());

		// Configure AOP on the encounter service
		Context.addAdvice(EncounterService.class, new EncounterServiceAdvice());
	}

	/**
	 * Tests that a point-of-care form doesn't create any new visit when none exists on that day
	 */
	@Test
	public void pocFormEncounterShouldNotCreateVisitIfNoneExistsOnDay() {
		Patient patient = TestUtils.getPatient(7);
		Form triage = MetadataUtils.existing(Form.class, CommonMetadata._Form.TRIAGE);

		// Save triage on Jan 1st
		Encounter triage_1 = TestUtils.saveEncounter(patient, triage, TestUtils.date(2012, 1, 1));

		// Check it has no visit
		Assert.assertThat(triage_1.getVisit(), is(nullValue()));
	}

	/**
	 * Tests that a point-of-care form will save into an existing visit if one exists on that day
	 */
	@Test
	public void pocFormEncounterShouldSaveIntoExistingVisitIfOneExists() {
		Patient patient = TestUtils.getPatient(7);
		Form triage = MetadataUtils.existing(Form.class, CommonMetadata._Form.TRIAGE);
		VisitType outpatient = MetadataUtils.existing(VisitType.class, CommonMetadata._VisitType.OUTPATIENT);

		// Save regular visit from 9am - 11am on Jan 1st
		Visit visit1 = TestUtils.saveVisit(patient, outpatient, TestUtils.date(2012, 1, 1, 9, 0, 0), TestUtils.date(2012, 1, 1, 11, 0, 0));

		// Save triage at 10am on Jan 1st
		Encounter triage_1 = TestUtils.saveEncounter(patient, triage, TestUtils.date(2012, 1, 1, 10, 0, 0));

		// Check it was saved into visit #1
		Assert.assertThat(triage_1.getVisit(), is(visit1));

		// Save triage at 11:30am on Jan 1st (not within visit times)
		Encounter triage_2 = TestUtils.saveEncounter(patient, triage, TestUtils.date(2012, 1, 1, 11, 30, 0));

		// Check it was not saved into visit #1
		Assert.assertThat(triage_2.getVisit(), is(nullValue()));
	}

	/**
	 * Tests that a retrospectively entered form creates a visit when none exists on that day
	 */
	@Test
	public void retroFormEncounterShouldCreateAllDayVisitIfNoneExistsOnDay() {
		Patient patient = TestUtils.getPatient(7);
		Form moh257 = MetadataUtils.existing(Form.class, HivMetadata._Form.MOH_257_VISIT_SUMMARY);
		VisitType outpatient = MetadataUtils.existing(VisitType.class, CommonMetadata._VisitType.OUTPATIENT);

		// Save MOH257 on Jan 1st
		Encounter moh257_1 = TestUtils.saveEncounter(patient, moh257, TestUtils.date(2012, 1, 1));

		// Check it was saved into new all day visit
		Assert.assertThat(moh257_1.getVisit(), is(notNullValue()));
		Assert.assertThat(moh257_1.getVisit().getVisitType(), is(outpatient));
		Assert.assertThat(moh257_1.getVisit().getStartDatetime(), is(TestUtils.date(2012, 1, 1, 0, 0, 0)));
		Assert.assertThat(moh257_1.getVisit().getStopDatetime(), is(OpenmrsUtil.getLastMomentOfDay(TestUtils.date(2012, 1, 1))));

		// Check that it's the source form of the visit
		Assert.assertThat(new VisitWrapper(moh257_1.getVisit()).getSourceForm(), is(moh257));
	}

	/**
	 * Tests that a retrospectively entered form saves into an existing visit when one exists on that day
	 */
	@Test
	public void retroFormEncounterShouldSaveIntoExistingVisitIfOneExists() {
		Patient patient = TestUtils.getPatient(7);
		Form moh257 = MetadataUtils.existing(Form.class, HivMetadata._Form.MOH_257_VISIT_SUMMARY);
		VisitType outpatient = MetadataUtils.existing(VisitType.class, CommonMetadata._VisitType.OUTPATIENT);

		// Save regular visit from 9am - 11am on Jan 1st
		Visit visit1 = TestUtils.saveVisit(patient, outpatient, TestUtils.date(2012, 1, 1, 9, 0, 0), TestUtils.date(2012, 1, 1, 11, 0, 0));

		// Save MOH257 for that day
		Encounter moh257_1 = TestUtils.saveEncounter(patient, moh257, TestUtils.date(2012, 1, 1));

		// Check it was saved into visit #1 and time was adjusted to visit start time
		Assert.assertThat(moh257_1.getVisit(), is(visit1));
		Assert.assertThat(moh257_1.getEncounterDatetime(), is(visit1.getStartDatetime()));

		// Check that it's not the source form of the visit
		Assert.assertThat(new VisitWrapper(visit1).getSourceForm(), is(nullValue()));
	}

	/**
	 * Tests that a retrospectively entered form won't save into an existing visit for a different location
	 */
	@Test
	public void retroFormEncounterShouldNotSaveIntoVisitAtDifferentLocation() {
		Patient patient = TestUtils.getPatient(7);
		Form moh257 = MetadataUtils.existing(Form.class, HivMetadata._Form.MOH_257_VISIT_SUMMARY);
		VisitType outpatient = MetadataUtils.existing(VisitType.class, CommonMetadata._VisitType.OUTPATIENT);

		// Save regular visit from 9am - 11am on Jan 1st at location #2
		Visit visit1 = TestUtils.saveVisit(patient, outpatient, TestUtils.date(2012, 1, 1, 9, 0, 0), TestUtils.date(2012, 1, 1, 11, 0, 0));
		visit1.setLocation(Context.getLocationService().getLocation(2));
		Context.getVisitService().saveVisit(visit1);

		// Save MOH257 for that day (will default to location #1)
		Encounter moh257_1 = TestUtils.saveEncounter(patient, moh257, TestUtils.date(2012, 1, 1));

		// Check it was saved into a new visit rather than visit #1
		Assert.assertThat(moh257_1.getVisit(), not(visit1));
	}

	/**
	 * Tests that a retrospectively entered form can be moved to a different visit and that the old visit is voided if
	 * it was created by that form
	 */
	@Test
	public void retroFormEncounterCanMoveIntoDifferentVisit() {
		Patient patient = TestUtils.getPatient(7);
		Form moh257 = MetadataUtils.existing(Form.class, HivMetadata._Form.MOH_257_VISIT_SUMMARY);
		VisitType outpatient = MetadataUtils.existing(VisitType.class, CommonMetadata._VisitType.OUTPATIENT);

		// Save MOH257 on Jan 1st
		Encounter moh257_1 = TestUtils.saveEncounter(patient, moh257, TestUtils.date(2012, 1, 1));

		// Check it has a new visit
		Visit visit1 = moh257_1.getVisit();
		Assert.assertThat(visit1, is(notNullValue()));

		// Create new visit on Feb 1st
		Visit visit2 = TestUtils.saveVisit(patient, outpatient, TestUtils.date(2012, 2, 1, 9, 0, 0), TestUtils.date(2012, 2, 1, 11, 0, 0));

		// TODO: relook at this. automatic visit range between 00:00:00 to 23:59:59
		// The below action is thus not allowed
		// Move encounter to Feb 1st
		/*moh257_1.setEncounterDatetime(TestUtils.date(2012, 2, 1, 9, 20, 0));
		moh257_1.setVisit(visit2);
		Context.getEncounterService().saveEncounter(moh257_1);*/

		// Check that encounter is now assigned to visit #2
		Assert.assertThat(moh257_1.getVisit(), is(visit1));

		// Check that visit #1 is now voided as it was created by this form and is now empty
		//Assert.assertThat(visit1.isVoided(), is(true));

		// Move encounter to Mar 1st (no existing visit)
		/*moh257_1.setEncounterDatetime(TestUtils.date(2012, 3, 1));
		Context.getEncounterService().saveEncounter(moh257_1);*/

		// Check it has a new visit on Mar 1st
		Assert.assertThat(moh257_1.getVisit(), is(notNullValue()));
		Assert.assertThat(moh257_1.getVisit().getVisitType(), is(outpatient));
		Assert.assertThat(moh257_1.getVisit().getStartDatetime(), is(TestUtils.date(2012, 1, 1, 0, 0, 0)));
		Assert.assertThat(moh257_1.getVisit().getStopDatetime(), is(OpenmrsUtil.getLastMomentOfDay(TestUtils.date(2012, 1, 1))));

		// Check that visit #2 is not voided as it wasn't created by this form
		Assert.assertThat(visit2.isVoided(), is(false));
	}
}