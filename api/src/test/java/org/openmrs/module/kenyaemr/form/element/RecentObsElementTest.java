/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.form.element;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link RecentObsElement}
 */
public class RecentObsElementTest extends BaseModuleContextSensitiveTest {

	private FormEntryContext context;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		// Setup form context
		context = new FormEntryContext(FormEntryContext.Mode.ENTER);
	}

	/**
	 * @see RecentObsElement#RecentObsElement(org.openmrs.module.htmlformentry.FormEntryContext, java.util.Map)
	 */
	@Test(expected = RuntimeException.class)
	public void RecentObsElement_shouldThrowExceptionIfConceptIdentifierEmpty() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("conceptId", "");

		new RecentObsElement(context, parameters);
	}

	/**
	 * @see RecentObsElement#generateHtml(org.openmrs.module.htmlformentry.FormEntryContext)
	 */
	@Test
	public void generateHtml_shouldReturnEmptyIfNoPatient() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("conceptId", "a09ab2c5-878e-4905-b25d-5784167d0216"); // CD4

		Assert.assertThat(new RecentObsElement(context, parameters).generateHtml(context), is(""));
	}

	/**
	 * @see RecentObsElement#generateHtml(org.openmrs.module.htmlformentry.FormEntryContext)
	 */
	@Test
	public void generateHtml_shouldReturnHandleNoPreviousObs() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("conceptId", "a09ab2c5-878e-4905-b25d-5784167d0216"); // CD4

		context.setupExistingData(TestUtils.getPatient(6));

		// Check without a none-message
		Assert.assertThat(new RecentObsElement(context, parameters).generateHtml(context), is("<span></span>"));

		parameters.put("noneMessage", "None");

		// Check with a none-message
		Assert.assertThat(new RecentObsElement(context, parameters).generateHtml(context), is("<span>None</span>"));
	}

	/**
	 * @see RecentObsElement#generateHtml(org.openmrs.module.htmlformentry.FormEntryContext)
	 */
	@Test
	public void generateHtml_shouldGenerateHtml() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("conceptId", "a09ab2c5-878e-4905-b25d-5784167d0216"); // CD4

		context.setupExistingData(TestUtils.getPatient(7));

		Assert.assertThat(new RecentObsElement(context, parameters).generateHtml(context),
				is("<span>175 cells/mmL <small>(15-Aug-2008)</small></span>")
		);

		parameters.put("showDate", "false");

		Assert.assertThat(new RecentObsElement(context, parameters).generateHtml(context),
				is("<span>175 cells/mmL</span>")
		);
	}
}