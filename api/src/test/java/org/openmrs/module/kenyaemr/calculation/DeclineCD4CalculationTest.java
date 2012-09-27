package org.openmrs.module.kenyaemr.calculation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ningosi
 * Date: 9/20/12
 * Time: 1:05 PM
 * 
 */
public class DeclineCD4CalculationTest extends BaseModuleContextSensitiveTest {

    @Before
    public void beforeEachTest() throws Exception {
        executeDataSet("org/openmrs/module/kenyaemr/include/testData.xml");
    }

    /**
     * @see DeclineCD4Calculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
     * @verifies determine whether patients have a decline in CD4
     */
    @Test
    public void evaluate_shouldDetermineWhetherPatientsHasDeclinedCD4() throws Exception {
        ConceptService cs = Context.getConceptService();
        Concept cd4 = cs.getConcept(5497);

        // give one of these people  CD4 200 days ago
        
        Calendar calendar = Calendar.getInstance();
		calendar.add( Calendar.DATE, -200);
		
        Obs obs180DaysAgo = new Obs(Context.getPatientService().getPatient(7), cd4, calendar.getTime(), null);
        obs180DaysAgo.setValueNumeric(123d);
        Context.getObsService().saveObs(obs180DaysAgo, null);
        
        //give the same patient recent cd4
        Obs obsRecent = new Obs(Context.getPatientService().getPatient(7), cd4, new Date(), null);
        obsRecent.setValueNumeric(120d);
        Context.getObsService().saveObs(obsRecent, null);
        

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

        Context.flushSession();

        List<Integer> ptIds = Arrays.asList(6, 7, 8);
        CalculationResultMap resultMap = new DeclineCD4Calculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
        Assert.assertTrue((Boolean) resultMap.get(6).getValue());  //in Hiv program but without cd4 i.e needs cd4
        Assert.assertFalse((Boolean) resultMap.get(7).getValue()); // has decline in  CD4
        Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // not in HIV Program
    }
}
