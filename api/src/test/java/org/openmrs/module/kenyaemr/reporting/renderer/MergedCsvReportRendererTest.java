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

package org.openmrs.module.kenyaemr.reporting.renderer;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link MergedCsvReportRenderer}
 */
public class MergedCsvReportRendererTest {

	/**
	 * @see MergedCsvReportRenderer#prepareVal(Object)
	 */
	@Test
	public void prepareVal_shouldFormatValueForCsvInclusion() {
		Assert.assertThat(MergedCsvReportRenderer.prepareVal(123), is("\"123\""));
		Assert.assertThat(MergedCsvReportRenderer.prepareVal("test"), is("\"test\""));
		Assert.assertThat(MergedCsvReportRenderer.prepareVal("test \" "), is("\"test \\\" \""));
	}
}