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

package org.openmrs.module.kenyaemr.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.util.EmrUtils}
 */
public class EmrUtilsTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
	}

	@Test
	public void integration() {
		new EmrUtils();
	}

	/**
	 * @see EmrUtils#getModuleVersion()
	 *
	 * TODO figure out how we can mock ModuleFactory from a BaseModuleContextSensitiveTest
	 */
	@Test(expected = NullPointerException.class)
	public void getModuleVersion() {
		EmrUtils.getModuleVersion();
	}

	/**
	 * @see EmrUtils#getModuleBuildProperties()
	 * @verifies return build properties
	 */
	@Test
	public void getModuleBuildProperties_shouldGetBuildProperties() {
		BuildProperties properties = EmrUtils.getModuleBuildProperties();

		Assert.assertNotNull(properties);
		Assert.assertNotNull(properties.getBuildDate());
		Assert.assertNotNull(properties.getDeveloper());
	}

	/**
	 * @see EmrUtils#dateHasTime(java.util.Date)
	 * @verifies return true only if date has time
	 */
	@Test
	public void dateHasTime_shouldReturnTrueOnlyInDateHasTime() {
		Assert.assertFalse(EmrUtils.dateHasTime(TestUtils.date(2012, 1, 1)));
		Assert.assertTrue(EmrUtils.dateHasTime(TestUtils.date(2012, 1, 1, 10, 0, 0)));
		Assert.assertTrue(EmrUtils.dateHasTime(TestUtils.date(2012, 1, 1, 0, 10, 0)));
		Assert.assertTrue(EmrUtils.dateHasTime(TestUtils.date(2012, 1, 1, 0, 0, 10)));
	}

	/**
	 * @see EmrUtils#isSameDay(java.util.Date, java.util.Date)
	 * @verifies return false if either date is null
	 */
	@Test
	public void isSameDay_shouldReturnFalseIfEitherDateIsNull() {
		Assert.assertFalse(EmrUtils.isSameDay(null, TestUtils.date(2012, 1, 2)));
		Assert.assertFalse(EmrUtils.isSameDay(TestUtils.date(2012, 1, 2), null));
	}

	/**
	 * @see EmrUtils#isSameDay(java.util.Date, java.util.Date)
	 * @verifies return true only for two dates that are on the same day
	 */
	@Test
	public void isSameDay_shouldReturnTrueOnlyForDatesOnSameDay() {
		Assert.assertTrue(EmrUtils.isSameDay(TestUtils.date(2012, 1, 1), TestUtils.date(2012, 1, 1)));
		Assert.assertTrue(EmrUtils.isSameDay(TestUtils.date(2012, 1, 1, 10, 30, 0), TestUtils.date(2012, 1, 1, 11, 45, 0)));
		Assert.assertFalse(EmrUtils.isSameDay(TestUtils.date(2012, 1, 1), TestUtils.date(2012, 1, 2)));
	}

	/**
	 * @see EmrUtils#isToday(java.util.Date)
	 * @verifies return true only for dates that are today
	 */
	@Test
	public void isToday_shouldReturnTrueOnlyForDatesThatAreToday() {
		Assert.assertTrue(EmrUtils.isToday(new Date()));
		Assert.assertFalse(EmrUtils.isToday(TestUtils.date(2012, 1, 1)));
	}

	/**
	 * @see EmrUtils#getVisitSourceForm(org.openmrs.Visit)
	 */
	@Test
	public void getVisitSourceForm_shouldReturnTheSourceFormIfThereIsOne() {
		Patient patient = Context.getPatientService().getPatient(8);
		VisitType outpatient = MetadataUtils.getVisitType(Metadata.VisitType.OUTPATIENT);
		Visit visit = TestUtils.saveVisit(patient, outpatient, TestUtils.date(2011, 1, 1), null);

		// Check no attribute returns null
		Assert.assertThat(EmrUtils.getVisitSourceForm(visit), is(nullValue()));

		Form moh257 = MetadataUtils.getForm(Metadata.Form.MOH_257_VISIT_SUMMARY);

		VisitAttribute sourceAttr = new VisitAttribute();
		sourceAttr.setAttributeType(MetadataUtils.getVisitAttributeType(Metadata.VisitAttributeType.SOURCE_FORM));
		sourceAttr.setOwner(visit);
		sourceAttr.setValue(moh257);
		visit.addAttribute(sourceAttr);

		Context.getVisitService().saveVisit(visit);

		Assert.assertThat(EmrUtils.getVisitSourceForm(visit), is(moh257));
	}

	/**
	 * @see EmrUtils#visitWillOverlap(org.openmrs.Visit)
	 */
	@Test
	public void visitWillOverlap_shouldReturnTrueIfVisitOverlaps() {

		Patient patient8 = Context.getPatientService().getPatient(8);
		VisitType outpatient = MetadataUtils.getVisitType(Metadata.VisitType.OUTPATIENT);

		Visit visit1 = TestUtils.saveVisit(patient8, outpatient, TestUtils.date(2011, 1, 1), TestUtils.date(2011, 1, 3));
		Visit visit2 = TestUtils.saveVisit(patient8, outpatient, TestUtils.date(2011, 1, 7), TestUtils.date(2011, 1, 10));
		Visit visit3 = TestUtils.saveVisit(patient8, outpatient, TestUtils.date(2011, 1, 13), null);
		Context.flushSession();

		// Test visit in between #1 and #2
		Visit visit = new Visit();
		visit.setPatient(patient8);
		visit.setVisitType(outpatient);
		visit.setStartDatetime(TestUtils.date(2011, 1, 4));
		visit.setStopDatetime(TestUtils.date(2011, 1, 5));

		Assert.assertFalse(EmrUtils.visitWillOverlap(visit));

		// Test overlap with #1
		visit.setStartDatetime(TestUtils.date(2011, 1, 2));
		visit.setStopDatetime(TestUtils.date(2011, 1, 4));

		Assert.assertTrue(EmrUtils.visitWillOverlap(visit));

		// Test touching #1 (visit dates are inclusive so counts as overlap)
		visit.setStartDatetime(TestUtils.date(2011, 1, 3));
		visit.setStopDatetime(TestUtils.date(2011, 1, 4));

		Assert.assertTrue(EmrUtils.visitWillOverlap(visit));

		// Test overlap with unstopped #3
		visit.setStartDatetime(TestUtils.date(2011, 2, 2));
		visit.setStopDatetime(TestUtils.date(2011, 2, 4));

		Assert.assertTrue(EmrUtils.visitWillOverlap(visit));

		// Check overlapping itself doesn't return true
		Assert.assertFalse(EmrUtils.visitWillOverlap(visit2));
	}

	@Test
	public void whoStage_shouldConvertConceptToInteger() {
		Assert.assertNull(EmrUtils.whoStage(Dictionary.getConcept(Dictionary.CD4_COUNT)));
		Assert.assertEquals(new Integer(1), EmrUtils.whoStage(Dictionary.getConcept(Dictionary.WHO_STAGE_1_PEDS)));
		Assert.assertEquals(new Integer(2), EmrUtils.whoStage(Dictionary.getConcept(Dictionary.WHO_STAGE_2_ADULT)));
		Assert.assertEquals(new Integer(3), EmrUtils.whoStage(Dictionary.getConcept(Dictionary.WHO_STAGE_3_PEDS)));
		Assert.assertEquals(new Integer(4), EmrUtils.whoStage(Dictionary.getConcept(Dictionary.WHO_STAGE_4_ADULT)));
	}

	/**
	 * @see EmrUtils#parseConceptList(String)
	 */
	@Test
	public void parseConceptList_shouldParseListCorrectly() {
		// Empty list
		List<Concept> concepts = EmrUtils.parseConceptList("");
		Assert.assertEquals(0, concepts.size());

		// No spaces
		concepts = EmrUtils.parseConceptList("5497,730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,5356");
		Assert.assertEquals(3, concepts.size());
		Assert.assertEquals(Dictionary.getConcept("5497AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), concepts.get(0));
		Assert.assertEquals(Dictionary.getConcept("730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), concepts.get(1));
		Assert.assertEquals(Dictionary.getConcept("5356AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), concepts.get(2));

		// Some spaces
		concepts = EmrUtils.parseConceptList(" 5497,  730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\t , 5356   \t");
		Assert.assertEquals(3, concepts.size());
		Assert.assertEquals(Dictionary.getConcept("5497AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), concepts.get(0));
		Assert.assertEquals(Dictionary.getConcept("730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), concepts.get(1));
		Assert.assertEquals(Dictionary.getConcept("5356AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), concepts.get(2));
	}

	@Test
	public void firstObsInEncounter_shouldFindFirstObsWithConcept() {
		Encounter e = new Encounter();

		// Test empty encounter
		Assert.assertNull(EmrUtils.firstObsInEncounter(e, Dictionary.getConcept(Dictionary.CD4_COUNT)));

		// Add obs to encounter
		Obs obs0 = new Obs();
		obs0.setConcept(Dictionary.getConcept(Dictionary.CD4_PERCENT));
		obs0.setValueNumeric(50.0);
		e.addObs(obs0);
		Obs obs1 = new Obs();
		obs1.setConcept(Dictionary.getConcept(Dictionary.CD4_COUNT));
		obs1.setValueNumeric(123.0);
		e.addObs(obs1);

		Assert.assertEquals(new Double(123.0), EmrUtils.firstObsInEncounter(e, Dictionary.getConcept(Dictionary.CD4_COUNT)).getValueNumeric());
	}

	@Test
	public void allObsInEncounter_shouldFindAllObsWithConcept() {
		Encounter e = new Encounter();

		// Test empty encounter
		Assert.assertEquals(new ArrayList<Obs>(), EmrUtils.allObsInEncounter(e, Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_USED_IN_PREGNANCY)));

		// Add 2 obs to encounter
		Obs obs0 = new Obs();
		obs0.setConcept(Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_USED_IN_PREGNANCY));
		obs0.setValueCoded(Dictionary.getConcept(Dictionary.NEVIRAPINE));
		e.addObs(obs0);
		Obs obs1 = new Obs();
		obs1.setConcept(Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_USED_IN_PREGNANCY));
		obs1.setValueCoded(Dictionary.getConcept(Dictionary.ZIDOVUDINE));
		e.addObs(obs1);

		Assert.assertEquals(2, EmrUtils.allObsInEncounter(e, Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_USED_IN_PREGNANCY)).size());
	}


	/**
	 * @see EmrUtils#firstObsInProgram(org.openmrs.PatientProgram, org.openmrs.Concept)
	 */
	@Test
	public void firstObsInProgram_shouldFindFirstObsWithConcept() {
		Patient patient = Context.getPatientService().getPatient(6);
		Program tbProgram = MetadataUtils.getProgram(Metadata.Program.TB);

		PatientProgram enrollment = TestUtils.enrollInProgram(patient, tbProgram, TestUtils.date(2012, 1, 1), TestUtils.date(2012, 4, 1));

		// Test with no saved obs
		Assert.assertNull(EmrUtils.firstObsInProgram(enrollment, Dictionary.getConcept(Dictionary.CD4_COUNT)));

		// Before enrollment
		Obs obs0 = TestUtils.saveObs(patient, Dictionary.getConcept(Dictionary.CD4_COUNT), 123.0, TestUtils.date(2011, 12, 1));
		// Wrong concept
		Obs obs1 = TestUtils.saveObs(patient, Dictionary.getConcept(Dictionary.CD4_PERCENT), 50.0, TestUtils.date(2012, 1, 15));
		// During enrollment
		Obs obs2 = TestUtils.saveObs(patient, Dictionary.getConcept(Dictionary.CD4_COUNT), 234.0, TestUtils.date(2012, 2, 1));
		Obs obs3 = TestUtils.saveObs(patient, Dictionary.getConcept(Dictionary.CD4_COUNT), 345.0, TestUtils.date(2012, 3, 1));

		Assert.assertEquals(obs2, EmrUtils.firstObsInProgram(enrollment, Dictionary.getConcept(Dictionary.CD4_COUNT)));

		// Test again with no enrollment end date
		enrollment = TestUtils.enrollInProgram(patient, tbProgram, TestUtils.date(2012, 1, 1));
		Assert.assertEquals(obs2, EmrUtils.firstObsInProgram(enrollment, Dictionary.getConcept(Dictionary.CD4_COUNT)));
	}

	/**
	 * @see EmrUtils#lastEncounter(org.openmrs.Patient, org.openmrs.EncounterType)
	 */
	@Test
	public void lastEncounter_shouldFindLastEncounterWithType() {
		Patient patient = TestUtils.getPatient(6);
		EncounterType triageEncType = MetadataUtils.getEncounterType(Metadata.EncounterType.TRIAGE);
		EncounterType tbScreenEncType = MetadataUtils.getEncounterType(Metadata.EncounterType.TB_SCREENING);

		// Test with no saved encounters
		Assert.assertNull(EmrUtils.lastEncounter(patient, tbScreenEncType));

		Encounter enc1 = TestUtils.saveEncounter(patient, triageEncType, TestUtils.date(2012, 3, 1));
		Encounter enc2 = TestUtils.saveEncounter(patient, tbScreenEncType, TestUtils.date(2012, 2, 1));
		Encounter enc3 = TestUtils.saveEncounter(patient, tbScreenEncType, TestUtils.date(2012, 1, 1));

		Assert.assertEquals(enc2, EmrUtils.lastEncounter(patient, tbScreenEncType));
	}

	/**
	 * @see EmrUtils#lastEncounterInProgram(org.openmrs.PatientProgram, org.openmrs.EncounterType)
	 */
	@Test
	public void lastEncounterInProgram_shouldFindLastEncounterWithType() {
		Patient patient = TestUtils.getPatient(6);
		Program tbProgram = MetadataUtils.getProgram(Metadata.Program.TB);
		EncounterType tbScreenEncType = MetadataUtils.getEncounterType(Metadata.EncounterType.TB_SCREENING);

		PatientProgram enrollment = TestUtils.enrollInProgram(patient, tbProgram, TestUtils.date(2012, 1, 1), TestUtils.date(2012, 4, 1));

		// Test with no saved encounters
		Assert.assertNull(EmrUtils.lastEncounterInProgram(enrollment, tbScreenEncType));

		// Before enrollment
		Encounter enc0 = TestUtils.saveEncounter(patient, tbScreenEncType, TestUtils.date(2011, 12, 1));
		// During enrollment
		Encounter enc1 = TestUtils.saveEncounter(patient, tbScreenEncType, TestUtils.date(2012, 2, 1));
		Encounter enc2 = TestUtils.saveEncounter(patient, tbScreenEncType, TestUtils.date(2012, 3, 1));
		// After enrollment
		Encounter enc3 = TestUtils.saveEncounter(patient, tbScreenEncType, TestUtils.date(2012, 5, 1));

		Assert.assertEquals(enc2, EmrUtils.lastEncounterInProgram(enrollment, tbScreenEncType));

		// Test again with no enrollment end date
		enrollment = TestUtils.enrollInProgram(patient, tbProgram, TestUtils.date(2012, 1, 1));
		Assert.assertEquals(enc3, EmrUtils.lastEncounterInProgram(enrollment, tbScreenEncType));
	}
}