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

package org.openmrs.module.kenyaemr.rest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.kenyaemr.report.ReportManager;
import org.openmrs.module.kenyaemr.report.indicator.Moh731Report;
import org.openmrs.module.reportingrest.web.controller.DataSetDefinitionController;
import org.openmrs.module.reportingrest.web.controller.EvaluatedDataSetController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class AccessReportsByRestWebServiceTest extends BaseModuleWebContextSensitiveTest {

	@Autowired
	DataSetDefinitionController dsdController;

	@Autowired
	EvaluatedDataSetController evalController;

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");

		ReportManager.refreshReportBuilders();
	}

	@Test
	public void shouldListDataSetDefinitionsByWebService() throws Exception {
		// equivalent to doing "GET .../datasetdefinition"
		SimpleObject result = dsdController.getAll(new MockHttpServletRequest(), new MockHttpServletResponse());

		List<SimpleObject> simpleDSDs = (List<SimpleObject>) result.get("results");
		Assert.assertNotNull(simpleDSDs);

		for (SimpleObject simpleDSD : simpleDSDs) {
			Assert.assertNotNull(simpleDSD);
			Assert.assertNotNull(simpleDSD.get("uuid"));
			Assert.assertNotNull(simpleDSD.get("display"));
			Assert.assertNotNull(simpleDSD.get("links"));
		}

		//printJson(result);
	}

	@Test
	public void shouldEvaluateMoh731ReportViaRest() throws Exception {
		String uuid = Moh731Report.class.getName() + ":" + Moh731Report.NAME_PREFIX + " DSD";

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("startDate", new String[] { "2012-01-01" });
		request.addParameter("endDate", new String[] { "2012-10-31" });
		SimpleObject evaledReport = (SimpleObject) evalController.retrieve(uuid, request);

		Assert.assertNotNull(evaledReport);
		Assert.assertNotNull(evaledReport.get("uuid"));
		Assert.assertNotNull(evaledReport.get("links"));

		Map metadata = (Map) evaledReport.get("metadata");
		Assert.assertNotNull(metadata);

		Collection cols = (Collection) metadata.get("columns");
		Assert.assertNotNull(cols);
		Assert.assertTrue(cols.size() > 1);

		Collection rows = (Collection) evaledReport.get("rows");
		Assert.assertNotNull(rows);
		Assert.assertEquals(1, rows.size());

		//printJson(result);
	}
}