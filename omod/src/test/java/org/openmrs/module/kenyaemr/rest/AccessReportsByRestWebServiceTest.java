/*
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

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.report.indicator.Moh731Report;
import org.openmrs.module.reportingrest.web.controller.DataSetDefinitionController;
import org.openmrs.module.reportingrest.web.controller.EvaluatedDataSetController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class AccessReportsByRestWebServiceTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    KenyaEmrService service;

    @Autowired
    DataSetDefinitionController dsdController;

    @Autowired
    EvaluatedDataSetController evalController;

    @Before
    public void setUp() throws Exception {
        executeDataSet("test-data.xml");
        service.refreshReportManagers();
    }

    @Test
    public void shouldListDataSetDefinitionsByWebService() throws Exception {
        // equivalent to doing "GET .../datasetdefinition"
        SimpleObject result = dsdController.getAll(new MockHttpServletRequest(), new MockHttpServletResponse());

        assertNotNull(result.get("results"));
        List results = (List) result.get("results");

        printJson(result);

        // need to assert something, but the fact that serialization was successful is meaningful
    }

    @Test
    public void shouldEvaluateMoh731ReportViaRest() throws Exception {
        String uuid = Moh731Report.class.getName() + ":" + Moh731Report.NAME_PREFIX + " DSD";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("startDate", new String[] { "2012-01-01" });
        request.addParameter("endDate", new String[] { "2012-10-31" });
        Object result = evalController.retrieve(uuid, request);

        printJson(result);

        Object rows = ((SimpleObject) result).get("rows");
        assertNotNull(rows);
        assertThat(((Collection) rows).size(), is(1));

        // should assert more, but the fact that serialization was successful is meaningful
    }

    private void printJson(Object object) throws IOException {
        System.out.println(new ObjectMapper().writeValueAsString(object));
    }

}
