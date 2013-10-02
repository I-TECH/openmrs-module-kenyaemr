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

package org.openmrs.module.kenyaemr.reporting.library.shared.tb;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.kenyaemr.test.ReportingTestUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.reporting.library.shared.tb.TbCohortLibrary}
 */
public class TbCohortLibraryTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private TbMetadata tbMetadata;

	@Autowired
	private TbCohortLibrary tbCohortLibrary;

	private EvaluationContext context;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		tbMetadata.install();

		Concept tbDiseaseStatus = Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS);
		Concept diseaseSuspected = Dictionary.getConcept(Dictionary.DISEASE_SUSPECTED);
		Concept notAssessed = Dictionary.getConcept(Dictionary.NOT_ASSESSED);

		// Screen patient #2 on May 31st
		TestUtils.saveObs(TestUtils.getPatient(2), tbDiseaseStatus, diseaseSuspected, TestUtils.date(2012, 5, 31));

		// Screen patient #6 on June 1st
		TestUtils.saveObs(TestUtils.getPatient(6), tbDiseaseStatus, diseaseSuspected, TestUtils.date(2012, 6, 1));

		// Screen patient #7 on June 30th
		TestUtils.saveObs(TestUtils.getPatient(7), tbDiseaseStatus, diseaseSuspected, TestUtils.date(2012, 6, 30));

		// Record not-screening patient #8 on June 30th
		TestUtils.saveObs(TestUtils.getPatient(7), tbDiseaseStatus, notAssessed, TestUtils.date(2012, 6, 30));

		// Record screening patient #8 on July 1st
		TestUtils.saveObs(TestUtils.getPatient(8), tbDiseaseStatus, diseaseSuspected, TestUtils.date(2012, 7, 1));

		List<Integer> cohort = Arrays.asList(2, 6, 7, 8, 999);
		context = ReportingTestUtils.reportingContext(cohort, TestUtils.date(2012, 6, 1), TestUtils.date(2012, 6, 30));
	}

	/**
	 * @see TbCohortLibrary#screenedForTb()
	 */
	@Test
	public void screenedForTb_shouldReturnPatientsScreenedAfterDate() throws Exception {
		// Check with just start date
		CohortDefinition cd = tbCohortLibrary.screenedForTb();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6, 7, 8), evaluated);
	}

	/**
	 * @see TbCohortLibrary#screenedForTb()
	 */
	@Test
	public void screenedForTb_shouldReturnPatientsScreenedBeforeDate() throws Exception {
		// Check with just end date
		CohortDefinition cd = tbCohortLibrary.screenedForTb();
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(2, 6, 7), evaluated);
	}

	/**
	 * @see TbCohortLibrary#screenedForTb()
	 */
	@Test
	public void screenedForTb_shouldReturnPatientsScreenedBetweenDates() throws Exception {
		// Check with both start and end date
		CohortDefinition cd = tbCohortLibrary.screenedForTb();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6, 7), evaluated);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.library.shared.tb.TbCohortLibrary#died()
	 */
	@Test
	public void died_shouldReturnPatientsWhoDiedBetweenDates() throws Exception {
		Concept tbTreatmentOutcome = Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME);
		Concept died = Dictionary.getConcept(Dictionary.DIED);

		// Exit patient #6 as died on May 15th (before reporting period)
		TestUtils.saveObs(TestUtils.getPatient(6), tbTreatmentOutcome, died, TestUtils.date(2012, 5, 15));

		// Exit patient #7 as died on June 15th
		TestUtils.saveObs(TestUtils.getPatient(7), tbTreatmentOutcome, died, TestUtils.date(2012, 6, 15));

		// Exit patient #8 as died on July 15th (after reporting period)
		TestUtils.saveObs(TestUtils.getPatient(8), tbTreatmentOutcome, died, TestUtils.date(2012, 7, 15));

		// Check with both start and end date
		CohortDefinition cd = tbCohortLibrary.died();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), evaluated);
	}

	/**
	 * @see TbCohortLibrary#started12MonthsAgo()
	 */
	@Test
	public void started12MonthsAgo_shouldReturnPatientsWhoStartedTreatment12MonthsAgo() throws Exception {
		Program tbProgram = MetadataUtils.getProgram(TbMetadata._Program.TB);

		// Enroll patient #6 on May 15th 2011
		TestUtils.enrollInProgram(TestUtils.getPatient(6), tbProgram, TestUtils.date(2011, 5, 15));

		// Enroll patient #7 on June 15th 2011
		TestUtils.enrollInProgram(TestUtils.getPatient(7), tbProgram, TestUtils.date(2011, 6, 15));

		// Enroll patient #8 on July 15th 2011
		TestUtils.enrollInProgram(TestUtils.getPatient(8), tbProgram, TestUtils.date(2011, 7, 15));

		// Check with both start and end date
		CohortDefinition cd = tbCohortLibrary.started12MonthsAgo();
		context.addParameterValue("onDate", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), evaluated);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.library.shared.tb.TbCohortLibrary#diedAndStarted12MonthsAgo()
	 */
	@Test
	public void diedStarted12MonthsAgo_shouldReturnPatientsWhoDiedAndStartedTreatment12MonthsAgo() throws Exception {
		Program tbProgram = MetadataUtils.getProgram(TbMetadata._Program.TB);
		Concept tbTreatmentOutcome = Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME);
		Concept died = Dictionary.getConcept(Dictionary.DIED);

		// Enroll patient #2 on May 15th 2011
		TestUtils.enrollInProgram(TestUtils.getPatient(2), tbProgram, TestUtils.date(2011, 5, 15));

		// Enroll patients #6 and #7 on June 15th 2011
		TestUtils.enrollInProgram(TestUtils.getPatient(6), tbProgram, TestUtils.date(2011, 6, 15));
		TestUtils.enrollInProgram(TestUtils.getPatient(7), tbProgram, TestUtils.date(2011, 6, 15));

		// Exit patient #7 as died on June 15th 2012
		TestUtils.saveObs(TestUtils.getPatient(7), tbTreatmentOutcome, died, TestUtils.date(2012, 6, 15));

		// Exit patient #8 as died on June 15th 2012
		TestUtils.saveObs(TestUtils.getPatient(8), tbTreatmentOutcome, died, TestUtils.date(2012, 6, 15));

		// Check with both start and end date
		CohortDefinition cd = tbCohortLibrary.diedAndStarted12MonthsAgo();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), evaluated);
	}
}