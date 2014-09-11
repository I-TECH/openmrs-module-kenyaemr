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

package org.openmrs.module.kenyaemr.fragment.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.metadata.FacilityMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.test.TestUiUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link SearchFragmentController}
 */
public class SearchFragmentControllerTest extends BaseModuleWebContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private FacilityMetadata facilityMetadata;

	private SearchFragmentController controller;

	@Autowired
	private TestUiUtils ui;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		commonMetadata.install();
		facilityMetadata.install(false); // Don't do full facility sync

		controller = new SearchFragmentController();
	}

	/**
	 * @see SearchFragmentController#location(org.openmrs.Location, org.openmrs.ui.framework.UiUtils)
	 */
	@Test
	public void location_shouldSimplifyLocation() {
		Location location = Context.getLocationService().getLocation(1);
		SimpleObject result = controller.location(location, ui);
		Assert.assertThat(result, hasEntry("id", (Object) new Integer(1)));
		Assert.assertThat(result, hasEntry("name", (Object) "Unknown Location"));
	}

	/**
	 * @see SearchFragmentController#locations(String, org.openmrs.ui.framework.UiUtils)
	 */
	@Test
	public void locations_shouldMatchByPartialName() {
		SimpleObject[] result = controller.locations("Xan", ui);
		Assert.assertThat(result.length, is(1));
		Assert.assertThat(result[0], hasEntry("id", (Object) new Integer(2)));
		Assert.assertThat(result[0], hasEntry("name", (Object) "Xanadu"));
	}

	/**
	 * @see SearchFragmentController#locations(String, org.openmrs.ui.framework.UiUtils)
	 */
	@Test
	public void locations_shouldMatchByCompleteMflCode() {
		LocationAttributeType mflCode = MetadataUtils.existing(LocationAttributeType.class, FacilityMetadata._LocationAttributeType.MASTER_FACILITY_CODE);
		Location xanadu = Context.getLocationService().getLocation(2);

		LocationAttribute attr = new LocationAttribute();
		attr.setOwner(xanadu);
		attr.setAttributeType(mflCode);
		attr.setValue("15002");
		xanadu.addAttribute(attr);
		Context.getLocationService().saveLocation(xanadu);

		SimpleObject[] result = controller.locations("15002", ui);
		Assert.assertThat(result.length, is(1));
		Assert.assertThat(result[0], hasEntry("id", (Object) new Integer(2)));
		Assert.assertThat(result[0], hasEntry("name", (Object) "Xanadu"));
		Assert.assertThat(result[0], hasEntry("code", (Object) "15002"));
	}

	/**
	 * @see SearchFragmentController#patient(org.openmrs.Patient, org.openmrs.ui.framework.UiUtils)
	 */
	@Test
	public void patient_shouldSimplifyPatient() {
		Patient patient = TestUtils.getPatient(7);
		SimpleObject result = controller.patient(patient, ui);
		Assert.assertThat(result, hasEntry("id", (Object) new Integer(7)));
		Assert.assertThat(result, hasEntry("name", (Object) "Chebaskwony, Collet Test"));
	}
}