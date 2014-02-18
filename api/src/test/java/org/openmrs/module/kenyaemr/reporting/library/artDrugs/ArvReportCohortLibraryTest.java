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

package org.openmrs.module.kenyaemr.reporting.library.artDrugs;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.test.EmrTestUtils;
import org.openmrs.module.kenyaemr.test.ReportingTestUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.reporting.library.artDrugs.ArvReportCohortLibrary}
 */

public class ArvReportCohortLibraryTest extends BaseModuleContextSensitiveTest {

	private EvaluationContext context;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		executeDataSet("dataset/test-drugs.xml");


		List<Integer> cohort = Arrays.asList(2, 6, 7, 8, 999);
		context = ReportingTestUtils.reportingContext(cohort, TestUtils.date(2012, 6, 1), TestUtils.date(2012, 6, 30));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.library.artDrugs.ArvReportCohortLibrary#onRegimen(List)
	 */
	@Test
	public void onRegimen_shouldReturnPatientsOnGivenRegimen() throws Exception{
		PatientService ps = Context.getPatientService();

		Concept azt = Context.getConceptService().getConcept(86663);
		Concept _3tc = Context.getConceptService().getConcept(78643);
		Concept efv = Context.getConceptService().getConcept(75523);

		DrugOrder aztOrder = new DrugOrder();
		aztOrder.setConcept(azt);
		DrugOrder t3cOrder = new DrugOrder();
		t3cOrder.setConcept(_3tc);
		DrugOrder efvOrder = new DrugOrder();
		efvOrder.setConcept(efv);
		// Put patient #6 on AZT + 3TC + EFV from June 1st to June 30th
		EmrTestUtils.saveRegimenOrder(ps.getPatient(6), Arrays.asList(azt, _3tc, efv), TestUtils.date(2012, 6, 1), TestUtils.date(2012, 6, 30));
		// Put patient #7 on AZT + 3TC + EFV from June 1st to June 30th
		EmrTestUtils.saveRegimenOrder(ps.getPatient(7), Arrays.asList(azt, _3tc, efv), TestUtils.date(2012, 6, 1), TestUtils.date(2012, 6, 30));
		CohortDefinition cd = ArvReportCohortLibrary.onRegimen(Arrays.asList(azt,_3tc,efv));
		context.addParameterValue("onDate", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6,7), evaluated);
	}
}
