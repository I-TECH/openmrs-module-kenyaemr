package org.openmrs.module.kenyaemr.calculation.library.mchcs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;

public class VisitsWithinAPeriodCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	/**
	 * @verifies all visits between two dates
	 * @see org.openmrs.module.kenyaemr.calculation.library.mchcs.VisitsWithinAPeriodCalculation#evaluate (
	 *      java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCalculateVisitsWithinAPeriod() throws Exception {
		VisitType visitType = Context.getVisitService().getVisitType(1);

		{
			Patient patient = TestUtils.getPatient(7);
			TestUtils.saveVisit(patient, visitType, TestUtils.date(2014, 5, 10, 10, 30, 00), TestUtils.date(2014, 5, 10, 12, 30, 00) );
			TestUtils.saveVisit(patient, visitType, TestUtils.date(2014, 7, 10, 10, 30, 00), TestUtils.date(2014, 7, 10, 12, 30, 00));
			TestUtils.saveVisit(patient, visitType, TestUtils.date(2014, 8, 10, 10, 30, 00), TestUtils.date(2014, 8, 10, 12, 30, 00));
		}

		{
			Patient patient = TestUtils.getPatient(8);
			TestUtils.saveVisit(patient, visitType, TestUtils.date(2013, 9, 10, 10, 30, 00), TestUtils.date(2013, 9, 10, 12, 30, 00));
			TestUtils.saveVisit(patient, visitType, TestUtils.date(2014, 1, 10, 10, 30, 00), TestUtils.date(2014, 1, 10, 12, 30, 00));
			TestUtils.saveVisit(patient, visitType, TestUtils.date(2014, 2, 10, 10, 30, 00), TestUtils.date(2014, 2, 10, 12, 30, 00));

		}

		List<Integer> ptIds = Arrays.asList(7, 8, 999);
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("reviewPeriod", 4);

		CalculationResultMap resultMap = new VisitsWithinAPeriodCalculation().evaluate(ptIds, params, Context.getService(PatientCalculationService.class).createCalculationContext());

		Assert.assertThat(((ListResult) resultMap.get(7)).getValues().size(), is(2));
		Assert.assertThat(((ListResult) resultMap.get(8)).getValues().size(), is(0));
		Assert.assertThat(((ListResult) resultMap.get(999)).getValues().size(), is(0));
	}


}