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

import org.junit.Test;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class AvoidReportingBugTest extends BaseModuleContextSensitiveTest {

    /**
     * This is a regression test for REPORT-414, which was fixed in the reporting module 0.7.3.
     * (We don't really need this test around anymore--I wrote it as a test to verify the bug existed and is fixed.)
     */
    @Test
    public void testReportingBug() throws Exception {
        ConceptService conceptService = Context.getConceptService();

        Program program = new Program();
        program.setName("TB Program");
        program.setDescription("(testing)");
        program.setUuid(MetadataConstants.TB_PROGRAM_UUID);
        program.setConcept(conceptService.getConcept(5089));
        Context.getProgramWorkflowService().saveProgram(program);

        TBPatientsReport report = new TBPatientsReport();
        EvaluationContext ec = new EvaluationContext();

        // nobody should match this report. This test is to ensure that we don't hit this reporting bug:
        // ERROR - TransactionAspectSupport.completeTransactionAfterThrowing(439) |2012-10-16 22:03:41,930| Application exception overridden by commit exception
        // org.openmrs.module.reporting.evaluation.EvaluationException: Failed to evaluate data set 'Eligible for ART' because:
        // unexpected end of subtree [from org.openmrs.PersonName where voided = false and person.personId in () order by preferred asc]
        ReportData data = Context.getService(ReportDefinitionService.class).evaluate(report.getReportDefinition(), ec);

        // if we get here, we've passed
    }
}
