package org.openmrs.module.kenyaemr.calculation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WHOStagesAtEnrollmentsCalculationTest extends BaseModuleContextSensitiveTest {

	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("test-data.xml");
	}

	@Test
	public void evaluate_shouldCalculateWHOStagesAtEnrollments() throws Exception {

		PatientService ps = Context.getPatientService();

		// Get HIV Program amd WHO STAGE concept
		Program hivProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.HIV_PROGRAM_UUID);
		Concept whoStage = Context.getConceptService().getConceptByUuid(MetadataConstants.WHO_STAGE_CONCEPT_UUID);

		// Give patient #6 a WHO PEDS 1 STAGE obs on 2009-1-1
		Concept whoPeds1 = Context.getConceptService().getConceptByUuid(MetadataConstants.WHO_STAGE_1_PEDS_CONCEPT_UUID);
		TestUtils.saveObs(ps.getPatient(6), whoStage, whoPeds1, TestUtils.date(2009, 1, 1));

		// Enroll patient #6 HIV Program on 2010-1-1 until 2010-2-1
		PatientProgram patient6Enrollment1 = TestUtils.enrollInProgram(ps.getPatient(6), hivProgram, TestUtils.date(2010, 1, 1), TestUtils.date(2010, 2, 1));

		// Enroll patient #6 HIV Program on 2011-1-1 until 2011-2-1
		PatientProgram patient6Enrollment2 = TestUtils.enrollInProgram(ps.getPatient(6), hivProgram, TestUtils.date(2011, 1, 1), TestUtils.date(2011, 2, 1));

		// Give patient #6 a WHO ADULT 1 STAGE obs on enrollment day
		Concept whoAdult1 = Context.getConceptService().getConceptByUuid(MetadataConstants.WHO_STAGE_1_ADULT_CONCEPT_UUID);
		TestUtils.saveObs(ps.getPatient(6), whoStage, whoAdult1, TestUtils.date(2011, 1, 1));

		// Give patient #6 a WHO ADULT 2 STAGE obs the next day
		Concept whoAdult2 = Context.getConceptService().getConceptByUuid(MetadataConstants.WHO_STAGE_2_ADULT_CONCEPT_UUID);
		TestUtils.saveObs(ps.getPatient(6), whoStage, whoAdult2, TestUtils.date(2011, 1, 2));

		// Patient exits from program on 2011-2-1

		// Re-enroll patient #6 on 2011-3-1
		PatientProgram patient6Enrollment3 = TestUtils.enrollInProgram(ps.getPatient(6), hivProgram, TestUtils.date(2011, 3, 1));

		// Give patient #6 a WHO ADULT 3 STAGE obs the next day
		Concept whoAdult3 = Context.getConceptService().getConceptByUuid(MetadataConstants.WHO_STAGE_3_ADULT_CONCEPT_UUID);
		TestUtils.saveObs(ps.getPatient(6), whoStage, whoAdult3, TestUtils.date(2011, 3, 2));

		Context.flushSession();
		
		List<Integer> ptIds = Arrays.asList(6, 7);
		CalculationResultMap resultMap = new WHOStagesAtEnrollmentsCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Map<PatientProgram, Concept> patient6Results = (Map<PatientProgram, Concept>)resultMap.get(6).getValue();
		Map<PatientProgram, Concept> patient7Results = (Map<PatientProgram, Concept>)resultMap.get(7).getValue();

		Assert.assertNull(patient6Results.get(patient6Enrollment1)); // No valid WHO stage obs during that enrollment
		Assert.assertEquals(whoAdult1, patient6Results.get(patient6Enrollment2));
		Assert.assertEquals(whoAdult3, patient6Results.get(patient6Enrollment3));
		Assert.assertTrue(patient7Results.isEmpty()); // has no recorded stage
	}
}