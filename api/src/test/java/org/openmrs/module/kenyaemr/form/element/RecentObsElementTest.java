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

package org.openmrs.module.kenyaemr.form.element;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

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