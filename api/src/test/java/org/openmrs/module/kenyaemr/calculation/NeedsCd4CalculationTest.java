package org.openmrs.module.kenyaemr.calculation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
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
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class NeedsCd4CalculationTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see NeedsCd4Calculation#evaluate(Collection,Map,PatientCalculationContext)
	 * @verifies determine whether patients need a CD4
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientsNeedACD4() throws Exception {
		// make sure the CD4 concept has the MVP/CIEL UUID
 		ConceptService cs = Context.getConceptService();
		Concept cd4 = cs.getConcept(5497);
		cd4.setUuid(MetadataConstants.CD4_CONCEPT_UUID);
		cs.saveConcept(cd4);
		
		// give one of these people a recent CD4
		Obs obs = new Obs(Context.getPatientService().getPatient(7), cd4, new Date(), null);
		obs.setValueNumeric(123d);
		Context.getObsService().saveObs(obs, null);
		
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
		CalculationResultMap resultMap = new NeedsCd4Calculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertTrue((Boolean) resultMap.get(6).getValue());
		Assert.assertFalse((Boolean) resultMap.get(7).getValue()); // has recent CD4
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // not in HIV Program
	}
}