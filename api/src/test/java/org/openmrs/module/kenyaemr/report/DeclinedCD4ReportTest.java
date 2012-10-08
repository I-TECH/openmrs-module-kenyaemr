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
 * Date: 9/20/12
 * Time: 1:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeclinedCD4ReportTest  extends BaseModuleContextSensitiveTest {

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
        try{
	       ReportData data = Context.getService(ReportDefinitionService.class).evaluate(rd, ec);
	       printOutput(data);
        }
        catch(Exception e){
        	e.toString();
        }
    }

    private void printOutput(ReportData data) throws Exception {
        System.out.println(data.getDefinition().getName());
        new TsvReportRenderer().render(data, null, System.out);
    }
}
