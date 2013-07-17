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
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.KenyaEmr;
import org.openmrs.module.kenyaemr.KenyaEmrConstants;
import org.openmrs.module.kenyaemr.KenyaEmrUiUtils;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.datatype.LocationDatatype;
import org.openmrs.module.kenyaemr.test.TestUiUtils;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link Moh257FragmentController}
 */
public class Moh257FragmentControllerTest extends BaseModuleWebContextSensitiveTest {

	private Moh257FragmentController controller;

	private Location location;

	@Autowired
	private KenyaEmr emr;

	@Autowired
	private UiUtils ui;

	@Autowired
	private KenyaEmrUiUtils kenyaEmrUiUtils;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");

		controller = new Moh257FragmentController();

		TestUtils.saveGlobalProperty(KenyaEmrConstants.GP_DEFAULT_LOCATION, null, LocationDatatype.class);

		location = Context.getLocationService().getLocation(1);
		Context.getService(KenyaEmrService.class).setDefaultLocation(location);
	}

	/**
	 * @see Moh257FragmentController#newRetrospectiveVisitCommandObject(org.openmrs.Patient)
	 */
	@Test
	public void newRetrospectiveVisitCommandObject() {
		Patient patient = Context.getPatientService().getPatient(7);

		Moh257FragmentController.RetrospectiveVisit reVisit = controller.newRetrospectiveVisitCommandObject(patient);

		Assert.assertEquals(patient.getPatientId(), reVisit.getPatientId());
	}

	@Test
	public void createRetrospectiveVisit() {
		Patient patient = Context.getPatientService().getPatient(7);
		VisitType outpatientType = Metadata.getVisitType(Metadata.OUTPATIENT_VISIT_TYPE);

		// Save an existing visit on 1-May-2012 10-11am
		Visit existingVisit = TestUtils.saveVisit(patient, outpatientType, TestUtils.date(2012, 5, 1, 10, 0, 0), TestUtils.date(2012, 5, 1, 11, 0, 0));

		// Check with request on day without existing visit
		Moh257FragmentController.RetrospectiveVisit reVisit = controller.newRetrospectiveVisitCommandObject(patient);
		reVisit.setLocation(location);
		reVisit.setVisitType(outpatientType);
		reVisit.setVisitDate(TestUtils.date(2012, 1, 1));

		SimpleObject simpleVisit = controller.createRetrospectiveVisit(reVisit, ui, kenyaEmrUiUtils);

		Assert.assertNotNull(simpleVisit.get("id"));
		Assert.assertEquals("Outpatient", simpleVisit.get("visitType"));
		Assert.assertEquals("01-Jan-2012", simpleVisit.get("startDatetime"));
		Assert.assertEquals("01-Jan-2012 23:59", simpleVisit.get("stopDatetime"));

		// Check with request on day with existing visit
		reVisit = controller.newRetrospectiveVisitCommandObject(patient);
		reVisit.setLocation(location);
		reVisit.setVisitType(outpatientType);
		reVisit.setVisitDate(TestUtils.date(2012, 5, 1));

		simpleVisit = controller.createRetrospectiveVisit(reVisit, ui, kenyaEmrUiUtils);

		// Assert that returned visit is the existing visit
		Assert.assertEquals(existingVisit.getVisitId(), simpleVisit.get("id"));
		Assert.assertEquals("Outpatient", simpleVisit.get("visitType"));
		Assert.assertEquals("01-May-2012 10:00", simpleVisit.get("startDatetime"));
		Assert.assertEquals("01-May-2012 11:00", simpleVisit.get("stopDatetime"));
	}
}