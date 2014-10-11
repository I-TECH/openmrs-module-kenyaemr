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

package org.openmrs.module.kenyaemr.visit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.form.FormManager;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link EmrVisitAssignmentHandler}. Most of the functionality provided by this class is actually tested in
 * {@link org.openmrs.module.kenyaemr.integration.FormsAndVisitsTest}
 */
public class EmrVisitAssignmentHandlerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private TbMetadata tbMetadata;

	@Autowired
	private MchMetadata mchMetadata;

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

		formManager.refresh();
	}

	/**
	 * @see EmrVisitAssignmentHandler#getAutoCreateVisitType(org.openmrs.Encounter)
	 */
	@Test
	public void getAutoCreateVisitType_shouldReturnAutoCreateVisitTypeIfSpecified() {
		VisitType outpatient = MetadataUtils.existing(VisitType.class, CommonMetadata._VisitType.OUTPATIENT);

		// Check form that doesn't specify one
		Encounter hivAddendum = new Encounter();
		hivAddendum.setForm(MetadataUtils.existing(Form.class, HivMetadata._Form.CLINICAL_ENCOUNTER_HIV_ADDENDUM));

		Assert.assertThat(EmrVisitAssignmentHandler.getAutoCreateVisitType(hivAddendum), is(nullValue()));

		// Check form that does specify one
		Encounter moh257 = new Encounter();
		moh257.setForm(MetadataUtils.existing(Form.class, HivMetadata._Form.MOH_257_VISIT_SUMMARY));

		Assert.assertThat(EmrVisitAssignmentHandler.getAutoCreateVisitType(moh257), is(outpatient));
	}

	/**
	 * @see EmrVisitAssignmentHandler#checkLocations(org.openmrs.Visit, org.openmrs.Encounter)
	 */
	@Test
	public void checkLocations() {
		Patient patient = TestUtils.getPatient(7);
		Form moh257 = MetadataUtils.existing(Form.class, HivMetadata._Form.MOH_257_VISIT_SUMMARY);
		VisitType outpatient = MetadataUtils.existing(VisitType.class, CommonMetadata._VisitType.OUTPATIENT);

		// Save regular visit on Jan 1st at no specific location
		Visit visit0 = TestUtils.saveVisit(patient, outpatient, TestUtils.date(2012, 1, 1), null);

		// Save regular visit on Jan 1st at location #2
		Visit visit1 = TestUtils.saveVisit(patient, outpatient, TestUtils.date(2012, 1, 1), null);
		visit1.setLocation(Context.getLocationService().getLocation(1));

		// Save regular visit on Jan 1st at location #2
		Visit visit2 = TestUtils.saveVisit(patient, outpatient, TestUtils.date(2012, 1, 1), null);
		visit2.setLocation(Context.getLocationService().getLocation(2));

		// Save MOH257 for that day (will default to location #1)
		Encounter encounter = TestUtils.saveEncounter(patient, moh257, TestUtils.date(2012, 1, 1));

		Assert.assertThat(EmrVisitAssignmentHandler.checkLocations(visit0, encounter), is(true));
		Assert.assertThat(EmrVisitAssignmentHandler.checkLocations(visit1, encounter), is(true));
		Assert.assertThat(EmrVisitAssignmentHandler.checkLocations(visit2, encounter), is(false));
	}
}