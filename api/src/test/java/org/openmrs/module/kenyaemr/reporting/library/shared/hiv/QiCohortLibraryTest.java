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

package org.openmrs.module.kenyaemr.reporting.library.shared.hiv;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyacore.test.ReportingTestUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link QiCohortLibrary}
 */
public class QiCohortLibraryTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private QiCohortLibrary qiICohortLibrary;

	private EvaluationContext context;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		hivMetadata.install();

		List<Integer> cohort = Arrays.asList(2, 6, 7, 8, 999);
		context = ReportingTestUtils.reportingContext(cohort, TestUtils.date(2012, 6, 1), TestUtils.date(2012, 6, 30));
	}

	/**
	 * @see QiCohortLibrary#hadNutritionalAssessmentAtLastVisit()
	 */
	@Test
	public void hadNutritionalAssessmentAtLastVisit() throws Exception {
		Concept weight = Dictionary.getConcept(Dictionary.WEIGHT_KG);
		Concept height = Dictionary.getConcept(Dictionary.HEIGHT_CM);
		Concept muac = Dictionary.getConcept(Dictionary.MUAC);
		EncounterType hivConsultation = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_CONSULTATION);
		VisitType outpatient = MetadataUtils.existing(VisitType.class, CommonMetadata._VisitType.OUTPATIENT);

		// Give patient #6 an HIV visit on June 1st with a BMI recording (i.e. weight and height)
		TestUtils.saveVisit(TestUtils.getPatient(6), outpatient, TestUtils.date(2012, 6, 1, 8, 0, 0), TestUtils.date(2012, 6, 1, 10, 0, 0),
				TestUtils.saveEncounter(TestUtils.getPatient(6), hivConsultation, TestUtils.date(2012, 6, 1, 9, 0, 0),
						TestUtils.saveObs(TestUtils.getPatient(6), weight, 80, TestUtils.date(2012, 6, 1, 9, 0, 0)),
						TestUtils.saveObs(TestUtils.getPatient(6), height, 150, TestUtils.date(2012, 6, 1, 9, 0, 0))
				)
		);

		// Give patient #7 an HIV visit on June 1st with a MUAC recording
		TestUtils.saveVisit(TestUtils.getPatient(7), outpatient, TestUtils.date(2012, 6, 1, 8, 0, 0), TestUtils.date(2012, 6, 1, 10, 0, 0),
				TestUtils.saveEncounter(TestUtils.getPatient(7), hivConsultation, TestUtils.date(2012, 6, 1, 9, 0, 0),
						TestUtils.saveObs(TestUtils.getPatient(7), muac, 10, TestUtils.date(2012, 6, 1, 9, 0, 0))
				)
		);

		// Give patient #8 an HIV visit on June 1st with a MUAC recording
		TestUtils.saveVisit(TestUtils.getPatient(8), outpatient, TestUtils.date(2012, 6, 1, 8, 0, 0), TestUtils.date(2012, 6, 1, 10, 0, 0),
				TestUtils.saveEncounter(TestUtils.getPatient(8), hivConsultation, TestUtils.date(2012, 6, 1, 9, 0, 0),
						TestUtils.saveObs(TestUtils.getPatient(8), muac, 10, TestUtils.date(2012, 6, 1, 9, 0, 0))
				)
		);

		// But a more recent visit on June 2nd with no relevant obs
		TestUtils.saveVisit(TestUtils.getPatient(8), outpatient, TestUtils.date(2012, 6, 2, 8, 0, 0), TestUtils.date(2012, 6, 2, 10, 0, 0),
				TestUtils.saveEncounter(TestUtils.getPatient(8), hivConsultation, TestUtils.date(2012, 6, 2, 9, 0, 0))
		);

		// Give patient #2 an HIV visit on July 1st (after the reporting period) with a MUAC recording
		TestUtils.saveVisit(TestUtils.getPatient(2), outpatient, TestUtils.date(2012, 7, 1, 8, 0, 0), TestUtils.date(2012, 7, 1, 10, 0, 0),
				TestUtils.saveEncounter(TestUtils.getPatient(2), hivConsultation, TestUtils.date(2012, 7, 1, 9, 0, 0),
						TestUtils.saveObs(TestUtils.getPatient(2), muac, 10, TestUtils.date(2012, 7, 1, 9, 0, 0))
				)
		);

		CohortDefinition cd = qiICohortLibrary.hadNutritionalAssessmentAtLastVisit();
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort cohort = Context.getService(CohortDefinitionService.class).evaluate(cd, context);

		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), cohort);
	}
}