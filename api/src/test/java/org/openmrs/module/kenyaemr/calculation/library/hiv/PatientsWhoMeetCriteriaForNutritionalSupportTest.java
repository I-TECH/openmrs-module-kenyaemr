package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

/**
 * Test for {@link PatientsWhoMeetCriteriaForNutritionalSupport}
 */
public class PatientsWhoMeetCriteriaForNutritionalSupportTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	/**
	 * @see PatientsWhoMeetCriteriaForNutritionalSupport#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldFindPatientsWhoMeetCriteriaForNutritionalSupport() throws Exception {

		//get the concepts that are necessary for calculation
		Concept weight = Dictionary.getConcept(Dictionary.WEIGHT_KG);
		Concept height = Dictionary.getConcept(Dictionary.HEIGHT_CM);
		Concept muac = Dictionary.getConcept(Dictionary.MUAC);

		//give patient #7 a weight and height
		TestUtils.saveObs(TestUtils.getPatient(7), weight, 50d, TestUtils.date(2014, 7, 1));
		TestUtils.saveObs(TestUtils.getPatient(7), height, 180d, TestUtils.date(2014, 7, 1));

		//give patient #2 a weight and height
		TestUtils.saveObs(TestUtils.getPatient(2), weight, 60d, TestUtils.date(2014, 7, 1));
		TestUtils.saveObs(TestUtils.getPatient(2), height, 120d, TestUtils.date(2014, 7, 1));

		//give patient # 6 a muac
		TestUtils.saveObs(TestUtils.getPatient(6), muac, 22d, TestUtils.date(2014, 7, 1));

		//give patient #8 higher muac
		TestUtils.saveObs(TestUtils.getPatient(8), muac, 30d, TestUtils.date(2014, 7, 1));

		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8, 999);
		CalculationResultMap resultMap = new PatientsWhoMeetCriteriaForNutritionalSupport().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertTrue((Boolean) resultMap.get(7).getValue()); // bmi < 18.5
		Assert.assertTrue((Boolean) resultMap.get(6).getValue()); // muac < 23
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // muac is higher than 23
		Assert.assertFalse((Boolean) resultMap.get(2).getValue()); // bmi > 18.5
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // has no observations
	}

}
