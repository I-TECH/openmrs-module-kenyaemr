/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.renderer;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.is;

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