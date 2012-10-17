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
package org.openmrs.module.kenyaemr.report;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.TsvReportRenderer;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DeclinedCD4ReportTest extends BaseModuleContextSensitiveTest {

    @Before
    public void beforeEachTest() throws Exception {
        executeDataSet("org/openmrs/module/kenyaemr/include/testData.xml");
    }

    @Test
    public void testReport() throws Exception {
        // enroll 6 and 7 in the HIV Program
        PatientService ps = Context.getPatientService();
        ProgramWorkflowService pws = Context.getProgramWorkflowService();
        Program hivProgram = pws.getPrograms("HIV Program").get(0);
        for (int i = 6; i <= 7; ++i) {
            PatientProgram pp = new PatientProgram();
            pp.setPatient(ps.getPatient(i));
            pp.setProgram(hivProgram);
            pp.setDateEnrolled(new Date());
            pws.savePatientProgram(pp);
        }

        ReportManager report = new DeclinedCD4Report();
        ReportDefinition rd = report.getReportDefinition();


        EvaluationContext ec = new EvaluationContext();
        SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
        try {
            ReportData data = Context.getService(ReportDefinitionService.class).evaluate(rd, ec);
            printOutput(data);
        } catch (Exception e) {
            e.toString();
        }
    }

    private void printOutput(ReportData data) throws Exception {
        System.out.println(data.getDefinition().getName());
        new TsvReportRenderer().render(data, null, System.out);
    }

}
