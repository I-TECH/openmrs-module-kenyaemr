/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyaemr.reporting.indicator.evaluator;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.reporting.indicator.HivCareVisitsIndicator;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Date;

public class HivCareVisitsIndicatorEvaluatorTest extends BaseModuleContextSensitiveTest {

	private EvaluationContext evaluationContext;

	private HivCareVisitsIndicator indicator;

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");

		Form hivAddendum = Context.getFormService().getFormByUuid(MetadataConstants.CLINICAL_ENCOUNTER_HIV_ADDENDUM_FORM_UUID);
		Form moh257 = Context.getFormService().getFormByUuid(MetadataConstants.MOH_257_VISIT_SUMMARY_FORM_UUID);
		Concept returnVisitDate = Context.getConceptService().getConceptByUuid(MetadataConstants.RETURN_VISIT_DATE_CONCEPT_UUID);

		// Schedule a return visit for patient #6 on 10-Jan-2012
		TestUtils.saveObs(Context.getPatientService().getPatient(6), returnVisitDate, TestUtils.date(2012, 1, 10), TestUtils.date(2012, 1, 1));

		// Submit two HIV addendum forms for patients #6 and #7 (who is female 18 +) on 10-Jan-2012
		TestUtils.saveEncounter(Context.getPatientService().getPatient(6), hivAddendum, TestUtils.date(2012, 1, 10));
		TestUtils.saveEncounter(Context.getPatientService().getPatient(7), hivAddendum, TestUtils.date(2012, 1, 10));

		// And again on 20-Jan-2012
		TestUtils.saveEncounter(Context.getPatientService().getPatient(6), hivAddendum, TestUtils.date(2012, 1, 20));
		TestUtils.saveEncounter(Context.getPatientService().getPatient(7), hivAddendum, TestUtils.date(2012, 1, 20));

		// Submit an moh257 form for patient #6 on the last day of the reporting period
		TestUtils.saveEncounter(Context.getPatientService().getPatient(6), moh257, TestUtils.date(2012, 1, 31));

		// Submit an moh257 form for patient #8 outside of the reporting period
		TestUtils.saveEncounter(Context.getPatientService().getPatient(8), moh257, TestUtils.date(2011, 1, 1));

		evaluationContext = new EvaluationContext();
		evaluationContext.addParameterValue("startDate", TestUtils.date(2012, 1, 1));
		evaluationContext.addParameterValue("endDate", TestUtils.date(2012, 1, 31));
		evaluationContext.setBaseCohort(new Cohort(Context.getPatientService().getAllPatients()));

		indicator = new HivCareVisitsIndicator();
		indicator.addParameter(new Parameter("startDate", "Date", Date.class));
		indicator.addParameter(new Parameter("endDate", "Date", Date.class));
	}

	@Test
	public void evaluate_shouldCalculateValueBasedOnDatesAndFilter() throws EvaluationException {
		// No filtering
		IndicatorResult result = Context.getService(IndicatorService.class).evaluate(indicator, evaluationContext);
		Assert.assertEquals(5, result.getValue().intValue());

		// Filter by females 18+ (i.e. only patient #7)
		indicator.setFilter(HivCareVisitsIndicator.Filter.FEMALES_18_AND_OVER);

		result = Context.getService(IndicatorService.class).evaluate(indicator, evaluationContext);
		Assert.assertEquals(2, result.getValue().intValue());

		// Filter by scheduled visits only
		indicator.setFilter(HivCareVisitsIndicator.Filter.SCHEDULED);

		result = Context.getService(IndicatorService.class).evaluate(indicator, evaluationContext);
		Assert.assertEquals(1, result.getValue().intValue());

		// Filter by unscheduled visits only
		indicator.setFilter(HivCareVisitsIndicator.Filter.UNSCHEDULED);

		result = Context.getService(IndicatorService.class).evaluate(indicator, evaluationContext);
		Assert.assertEquals(4, result.getValue().intValue());
	}
}