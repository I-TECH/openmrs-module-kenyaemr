package org.openmrs.module.kenyaemr.calculation;

import java.util.*;

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
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class NeedsCD4CalculationTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("org/openmrs/module/kenyaemr/include/testData.xml");
	}
	
	/**
	 * @see NeedsCD4Calculation#evaluate(Collection,Map,PatientCalculationContext)
	 * @verifies determine whether patients need a CD4
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientsNeedsCD4() throws Exception {

		// Get HIV Program
		ProgramWorkflowService pws = Context.getProgramWorkflowService();
		Program hivProgram = pws.getPrograms("HIV Program").get(0);

		// Enroll patients #6, #7 and #8 in the HIV Program
		PatientService ps = Context.getPatientService();
		for (int i = 6; i <= 8; ++i) {
			TestUtils.enrollInProgram(ps.getPatient(i), hivProgram, new Date());
		}
		
		// Give patient #7 a recent CD4 result obs
		Concept cd4 = Context.getConceptService().getConcept(5497);
		TestUtils.saveObs(Context.getPatientService().getPatient(7), cd4, 123d, new Date());

		// Give patient #8 a CD4 result obs from a year ago
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -360);
		TestUtils.saveObs(Context.getPatientService().getPatient(8), cd4, 123d, calendar.getTime());
		
		Context.flushSession();
		
		List<Integer> ptIds = Arrays.asList(6, 7, 8, 999);
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new NeedsCD4Calculation());
		Assert.assertTrue((Boolean) resultMap.get(6).getValue()); // has no CD4
		Assert.assertFalse((Boolean) resultMap.get(7).getValue()); // has recent CD4
		Assert.assertTrue((Boolean) resultMap.get(8).getValue()); // has old CD4
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // not in HIV Program
	}
}