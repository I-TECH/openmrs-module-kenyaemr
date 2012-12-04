package org.openmrs.module.kenyaemr.calculation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

public class LastWHOStageCalculationTest extends BaseModuleContextSensitiveTest {

	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("org/openmrs/module/kenyaemr/include/testData.xml");
	}

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.NeedsCD4Calculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate last recorded WHO stage for all patients
	 */
	@Test
	public void evaluate_shouldCalculateLatestWHOStage() throws Exception {

		PatientService ps = Context.getPatientService();
		Concept whoStage = Context.getConceptService().getConceptByUuid(MetadataConstants.WHO_STAGE_CONCEPT_UUID);

		// Give patient #6 a recent WHO ADULT 1 STAGE recording
		Concept whoAdult1 = Context.getConceptService().getConceptByUuid(MetadataConstants.WHO_STAGE_1_ADULT_CONCEPT_UUID);
		TestUtils.saveObs(ps.getPatient(6), whoStage, whoAdult1, TestUtils.date(2012, 12, 1));

		// Give patient #6 an older WHO ADULT 2 STAGE recording
		Concept whoAdult2 = Context.getConceptService().getConceptByUuid(MetadataConstants.WHO_STAGE_2_ADULT_CONCEPT_UUID);
		TestUtils.saveObs(ps.getPatient(6), whoStage, whoAdult2, TestUtils.date(2010, 11, 1));
		
		Context.flushSession();
		
		List<Integer> ptIds = Arrays.asList(6, 7);
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new LastWHOStageCalculation());
		Assert.assertEquals(whoAdult1, ((Obs) resultMap.get(6).getValue()).getValueCoded());
		Assert.assertNull(resultMap.get(7)); // has no recorded stage
	}
}