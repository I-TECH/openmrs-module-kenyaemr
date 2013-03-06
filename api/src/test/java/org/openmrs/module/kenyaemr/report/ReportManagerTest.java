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

package org.openmrs.module.kenyaemr.report;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class ReportManagerTest {
	/**
	 * @see ReportManager#getReportBuildersByTag(String)
	 */
	@Test
	public void getReportBuildersByTag_shouldGetReportBuildersWithTag() {

		ReportManager.refreshReportBuilders();

		final String[] TEST_TAGS = { "moh", "facility" };

		for (String tag : TEST_TAGS) {
			List<ReportBuilder> reports = ReportManager.getReportBuildersByTag(tag);
			Assert.assertTrue(reports.size() > 0);
			for (ReportBuilder report : reports) {
				Assert.assertTrue(Arrays.asList(report.getTags()).contains(tag));
			}
		}
	}
}