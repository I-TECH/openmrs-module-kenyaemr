package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Test for {@link IsTransferOutCalculation}
 */
public class IsTransferOutCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	/**
	 * @see IsTransferOutCalculation#evaluate(Collection, Map, PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCalculateIsTransferOutCalculation() throws Exception {

		Concept reasonForDiscontinue = Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION);
		Concept transferOut = Dictionary.getConcept(Dictionary.TRANSFERRED_OUT);
		Concept died = Dictionary.getConcept(Dictionary.DIED);
		Concept unknown = Dictionary.getConcept(Dictionary.UNKNOWN);

		//make #2 a transfer out with the looking transfer out status
		TestUtils.saveObs(TestUtils.getPatient(2), reasonForDiscontinue, transferOut, TestUtils.date(2014, 3, 1));
		//give #7 a transfer out with died status
		TestUtils.saveObs(TestUtils.getPatient(7), reasonForDiscontinue, died, TestUtils.date(2014, 3, 10));
		//make #8 not a transfer out with unknown status
		TestUtils.saveObs(TestUtils.getPatient(8), transferOut, unknown, TestUtils.date(2014, 3, 10));


		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8, 999);
		CalculationResultMap resultMap = new IsTransferOutCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertTrue((Boolean) resultMap.get(2).getValue()); // is a transfer out (transfer out)
		Assert.assertFalse((Boolean) resultMap.get(7).getValue()); // have a transfer out (died)
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // has transfer out (unknown)
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); //not having any obs
	}

}
