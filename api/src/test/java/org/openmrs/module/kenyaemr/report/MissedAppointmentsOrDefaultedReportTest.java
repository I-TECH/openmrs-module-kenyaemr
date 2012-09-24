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

/**
 * Created with IntelliJ IDEA.
 * User: ningosi
 * Date: 9/21/12
 * Time: 1:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class MissedAppointmentsOrDefaultedReportTest extends BaseModuleContextSensitiveTest {

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

        ReportManager report = new MissedAppointmentsOrDefaultedReport();
        ReportDefinition rd = report.getReportDefinition();

        EvaluationContext ec = new EvaluationContext();
        SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
        ReportData data = Context.getService(ReportDefinitionService.class).evaluate(rd, ec);
        printOutput(data);
    }

    private void printOutput(ReportData data) throws Exception {
        System.out.println(data.getDefinition().getName());
        new TsvReportRenderer().render(data, null, System.out);
    }
}
