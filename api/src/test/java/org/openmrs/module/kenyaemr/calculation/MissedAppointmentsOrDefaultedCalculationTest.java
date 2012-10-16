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
 * Date: 9/21/12
 * Time: 11:46 AM
 * 
 */
public class MissedAppointmentsOrDefaultedCalculationTest extends BaseModuleContextSensitiveTest {
    @Before
    public void beforeEachTest() throws Exception {
        executeDataSet("org/openmrs/module/kenyaemr/include/testData.xml");
    }

    /**
     * @see MissedAppointmentsOrDefaultedCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
     * @verifies determine whether patients have a Missed appointments or defaulted
     */
    @Test
    public void evaluate_shouldDetermineWhetherPatientsWhoMissedAppointmentsOrDefaulted() throws Exception {
    	
    	ConceptService cs = Context.getConceptService();
        Concept returnVisit = cs.getConcept(5096);
        
		// then we expect a patient to have visitited  10 days ago
        Calendar calendar = Calendar.getInstance();
		calendar.add( Calendar.DATE, -10);
		
		
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

        // then we expect a patient to visit 10 days
        Obs obs = new Obs(Context.getPatientService().getPatient(7), returnVisit, calendar.getTime(), null);
        obs.setValueDatetime(calendar.getTime());
        Context.getObsService().saveObs(obs, null);
         
        Context.flushSession();
        List<Integer> ptIds = Arrays.asList(6, 7, 8);
        
        CalculationResultMap resultMap = new MissedAppointmentsOrDefaultedCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
        Assert.assertFalse((Boolean)resultMap.get(6).getValue());   //in HIV program but no missed visit date
        Assert.assertTrue((Boolean) resultMap.get(7).getValue()); // has Missed visit
        Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // not in HIV Program
        
    }
}
