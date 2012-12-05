package org.openmrs.module.kenyaemr.calculation.cd4;

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
import org.openmrs.module.kenyaemr.calculation.LastWHOStageCalculation;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

public class LastCD4CalculationTest extends BaseModuleContextSensitiveTest {

	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("org/openmrs/module/kenyaemr/include/testData.xml");
	}

	/**
	 * @see NeedsCD4Calculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate last recorded CD4 for all patients
	 */
	@Test
	public void evaluate_shouldCalculateLastCD4() throws Exception {

		PatientService ps = Context.getPatientService();
		Concept cd4 = Context.getConceptService().getConceptByUuid(MetadataConstants.CD4_CONCEPT_UUID);

		// Give patient #6 some CD4 obs
		TestUtils.saveObs(ps.getPatient(6), cd4, 123d, TestUtils.date(2012, 12, 1));
		TestUtils.saveObs(ps.getPatient(6), cd4, 150d, TestUtils.date(2010, 11, 1));
		
		Context.flushSession();
		
		List<Integer> ptIds = Arrays.asList(6, 999);
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new LastCD4Calculation());
		Assert.assertEquals(new Double(123d), ((Obs) resultMap.get(6).getValue()).getValueNumeric());
		Assert.assertNull(resultMap.get(999)); // has no recorded CD4
	}
}