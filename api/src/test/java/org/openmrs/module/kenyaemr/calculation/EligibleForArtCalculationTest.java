package org.openmrs.module.kenyaemr.calculation;


import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class EligibleForArtCalculationTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("org/openmrs/module/kenyaemr/include/testData.xml");
	}
	
	/**
	 * @see EligibleForArtCalculation#evaluate(Collection,Map,PatientCalculationContext)
	 * @verifies calculate eligibility
	 */
	@Test
	public void evaluate_shouldCalculateEligibility() throws Exception {
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
		
		List<Integer> ptIds = Arrays.asList(6, 7, 8);
		CalculationResultMap resultMap = new EligibleForArtCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		for (Integer ptId : resultMap.keySet()) {
			System.out.println(ptId + " -> " + resultMap.get(ptId));
		}
	}
}