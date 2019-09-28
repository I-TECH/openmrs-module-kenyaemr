/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.form.velocity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.GlobalProperty;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSession;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Tests for {@link EmrVelocityFunctions}
 */
public class EmrVelocityFunctionsTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	private EmrVelocityFunctions functionsForSession1, functionsForSession2;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		hivMetadata.install();

		HttpSession httpSession = new MockHttpSession();
		String formXml = "<htmlform></htmlform>";

		// Create a session for dummy form with patient #6
		FormEntrySession formSession1 = new FormEntrySession(TestUtils.getPatient(6), formXml, httpSession);
		functionsForSession1 = new EmrVelocityFunctions(formSession1);

		// Create a session for dummy form with patient #7
		FormEntrySession formSession2 = new FormEntrySession(TestUtils.getPatient(7), formXml, httpSession);
		functionsForSession2 = new EmrVelocityFunctions(formSession2);
	}

	/**
	 * @see EmrVelocityFunctions#hasHivUniquePatientNumber()
	 */
	@Test
	public void hasHivUniquePatientNumber() {
		// Give patient #7 a UPN
		PatientIdentifierType upn = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
		TestUtils.savePatientIdentifier(TestUtils.getPatient(7), upn, "1234567890");

		// Patient #7 now has a UPN
		Assert.assertThat(functionsForSession2.hasHivUniquePatientNumber(), is(true));

		// Patient #6 doesn't have a UPN
		Assert.assertThat(functionsForSession1.hasHivUniquePatientNumber(), is(false));
	}

	/**
	 * @see EmrVelocityFunctions#getConcept(String)
	 */
	@Test
	public void getConcept_shouldReturnConcept() {
		Concept cd4 = Dictionary.getConcept(Dictionary.CD4_COUNT);
		Assert.assertThat(functionsForSession1.getConcept(Dictionary.CD4_COUNT), is(cd4));
	}

	/**
	 * @see EmrVelocityFunctions#getGlobalProperty(String)
	 */
	@Test
	public void getGlobalProperty_shouldReturnPropertyValue() {
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(EmrConstants.GP_DEFAULT_LOCATION);
		Assert.assertThat(functionsForSession1.getGlobalProperty("kenyaemr.defaultLocation"), is(gp.getValue()));

		// Check no exception for non-existent
		Assert.assertThat(functionsForSession1.getGlobalProperty("xxx.xxx"), is(nullValue()));
	}
}