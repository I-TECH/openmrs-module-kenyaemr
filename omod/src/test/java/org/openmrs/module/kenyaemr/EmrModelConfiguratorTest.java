/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.util.EmrUiUtils;
import org.openmrs.ui.framework.fragment.FragmentContext;
import org.openmrs.ui.framework.fragment.FragmentRequest;
import org.openmrs.ui.framework.page.PageContext;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.nullValue;

/**
 * Tests for {@link EmrModelConfigurator}
 */
public class EmrModelConfiguratorTest extends BaseModuleWebContextSensitiveTest {

	private MockHttpServletRequest request;

	private PageContext pageContext;

	@Autowired
	private EmrModelConfigurator configurator;

	@Autowired
	private EmrUiUtils kenyaEmrUi;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() {
		request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		PageRequest pageRequest = new PageRequest("kenyaemr", "test", request, response, null);
		pageContext = new PageContext(pageRequest);
	}

	/**
	 * @see EmrModelConfigurator#configureModel(org.openmrs.ui.framework.page.PageContext)
	 */
	@Test
	public void configureModel_page_shouldIncludeNullsForNoParams() {
		configurator.configureModel(pageContext);

		Assert.assertThat(pageContext.getModel(), hasEntry("currentPatient", null));
		Assert.assertThat(pageContext.getModel(), hasEntry("currentVisit", null));
		Assert.assertThat(pageContext.getModel(), hasEntry("activeVisit", null));

		Assert.assertThat(pageContext.getModel(), hasEntry("kenyaEmrUi", (Object) kenyaEmrUi));
	}

	/**
	 * @see EmrModelConfigurator#configureModel(org.openmrs.ui.framework.page.PageContext)
	 */
	@Test
	public void configureModel_page_shouldAcceptParamsForAll() {
		request.setParameter("patientId", "6");
		request.setParameter("visitId", "4");

		configurator.configureModel(pageContext);

		Assert.assertThat(pageContext.getModel(), hasEntry("currentPatient", (Object) TestUtils.getPatient(6)));
		Assert.assertThat(pageContext.getModel(), hasEntry("currentVisit", (Object) Context.getVisitService().getVisit(4)));
		Assert.assertThat(pageContext.getModel(), hasEntry("activeVisit", (Object) Context.getVisitService().getVisit(5)));

		Assert.assertThat(pageContext.getModel(), hasEntry("kenyaEmrUi", (Object) kenyaEmrUi));
	}

	/**
	 * @see EmrModelConfigurator#configureModel(org.openmrs.ui.framework.page.PageContext)
	 */
	@Test
	public void configureModel_page_shouldInferPatientFromCurrentVisit() {
		request.setParameter("visitId", "4");

		configurator.configureModel(pageContext);

		Assert.assertThat(pageContext.getModel(), hasEntry("currentPatient", (Object) TestUtils.getPatient(6)));
	}

	/**
	 * @see EmrModelConfigurator#configureModel(org.openmrs.ui.framework.page.PageContext)
	 */
	@Test(expected = RuntimeException.class)
	public void configureModel_page_shouldThrowExceptionOnPatientVisitMismatch() {
		request.setParameter("patientId", "6");
		request.setParameter("visitId", "2"); // belongs to patient #2

		configurator.configureModel(pageContext);
	}

	/**
	 * @see EmrModelConfigurator#configureModel(org.openmrs.ui.framework.page.PageContext)
	 */
	@Test
	public void configureModel_page_shouldInferPatientAndVisitFromEncounter() {
		request.setParameter("encounterId", "3");

		configurator.configureModel(pageContext);

		Assert.assertThat(pageContext.getModel(), hasEntry("currentPatient", (Object) TestUtils.getPatient(7)));
		Assert.assertThat(pageContext.getModel(), hasEntry("currentVisit", null));
		Assert.assertThat(pageContext.getModel(), hasEntry("activeVisit", null));
	}

	/**
	 * @see EmrModelConfigurator#configureModel(org.openmrs.ui.framework.page.PageContext)
	 */
	@Test
	public void configureModel_page_shouldSwallowAuthenticationExceptions() {
		request.setParameter("patientId", "6");
		request.setParameter("visitId", "4");

		Context.getUserContext().logout();

		configurator.configureModel(pageContext);

		Assert.assertThat(pageContext.getModel(), hasEntry("currentPatient", null));
		Assert.assertThat(pageContext.getModel(), hasEntry("currentVisit", null));
		Assert.assertThat(pageContext.getModel(), hasEntry("activeVisit", null));
	}

	/**
	 * @see EmrModelConfigurator#configureModel(org.openmrs.ui.framework.fragment.FragmentContext)
	 */
	@Test
	public void configureModel_fragment() {
		FragmentRequest fragRequest = new FragmentRequest("kenyaemr", "test");
		FragmentContext fragContext = new FragmentContext(fragRequest, pageContext);

		configurator.configureModel(fragContext);

		Assert.assertThat(fragContext.getModel(), hasEntry("kenyaEmrUi", (Object) kenyaEmrUi));
	}

	/**
	 * @see EmrModelConfigurator#patientFromParam(String)
	 */
	@Test
	public void patientFromParam_shouldHandleEmptyString() {
		Assert.assertThat(configurator.patientFromParam(""), nullValue());
	}

	/**
	 * @see EmrModelConfigurator#visitFromParam(String)
	 */
	@Test
	public void visitFromParam_shouldHandleEmptyString() {
		Assert.assertThat(configurator.visitFromParam(""), nullValue());
	}

	/**
	 * @see EmrModelConfigurator#encounterFromParam(String)
	 */
	@Test
	public void encounterFromParam_shouldHandleEmptyString() {
		Assert.assertThat(configurator.encounterFromParam(""), nullValue());
	}

	/**
	 * @see EmrModelConfigurator#encounterFromParam(String)
	 */
	@Test
	public void encounterFromParam_shouldReturnNullForAuthenticationException() {
		Context.getUserContext().logout();

		Assert.assertThat(configurator.encounterFromParam("3"), nullValue());
	}

	/**
	 * @see EmrModelConfigurator#getActiveVisit(org.openmrs.Patient)
	 */
	@Test
	public void getActiveVisit_shouldReturnNullForAuthenticationException() {
		Patient patient = TestUtils.getPatient(6);

		Context.getUserContext().logout();

		Assert.assertThat(configurator.getActiveVisit(patient), nullValue());
	}
}