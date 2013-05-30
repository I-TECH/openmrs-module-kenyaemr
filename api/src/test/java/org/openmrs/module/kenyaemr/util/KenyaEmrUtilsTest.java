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
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Tests for {@link KenyaEmrUtils}
 */
public class KenyaEmrUtilsTest extends BaseModuleContextSensitiveTest {

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
	}

	@Test
	public void integration() {
		new KenyaEmrUtils();
	}

	/**
	 * @see KenyaEmrUtils#dateAddDays(java.util.Date, int)
	 * @verifies shift the date by the number of days
	 */
	@Test
	public void dateAddDays_shouldShiftDateByNumberOfDays() {
		Assert.assertEquals(TestUtils.date(2012, 1, 2), KenyaEmrUtils.dateAddDays(TestUtils.date(2012, 1, 1), 1));
		Assert.assertEquals(TestUtils.date(2012, 2, 1), KenyaEmrUtils.dateAddDays(TestUtils.date(2012, 1, 1), 31));
		Assert.assertEquals(TestUtils.date(2011, 12, 31), KenyaEmrUtils.dateAddDays(TestUtils.date(2012, 1, 1), -1));
	}

	/**
	 * @see KenyaEmrUtils#isSameDay(java.util.Date, java.util.Date)
	 * @verifies return false if either date is null
	 */
	@Test
	public void isSameDay_shouldReturnFalseIfEitherDateIsNull() {
		Assert.assertFalse(KenyaEmrUtils.isSameDay(null, TestUtils.date(2012, 1, 2)));
		Assert.assertFalse(KenyaEmrUtils.isSameDay(TestUtils.date(2012, 1, 2), null));
	}

	/**
	 * @see KenyaEmrUtils#isSameDay(java.util.Date, java.util.Date)
	 * @verifies return true only for two dates that are on the same day
	 */
	@Test
	public void isSameDay_shouldReturnTrueOnlyForDatesOnSameDay() {
		Assert.assertTrue(KenyaEmrUtils.isSameDay(TestUtils.date(2012, 1, 1), TestUtils.date(2012, 1, 1)));
		Assert.assertTrue(KenyaEmrUtils.isSameDay(TestUtils.date(2012, 1, 1, 10, 30, 0), TestUtils.date(2012, 1, 1, 11, 45, 0)));
		Assert.assertFalse(KenyaEmrUtils.isSameDay(TestUtils.date(2012, 1, 1), TestUtils.date(2012, 1, 2)));
	}

	/**
	 * @see KenyaEmrUtils#isToday(java.util.Date)
	 * @verifies return true only for dates that are today
	 */
	@Test
	public void isToday_shouldReturnTrueOnlyForDatesThatAreToday() {
		Assert.assertTrue(KenyaEmrUtils.isToday(new Date()));
		Assert.assertFalse(KenyaEmrUtils.isToday(TestUtils.date(2012, 1, 1)));
	}

	/**
	 * @see KenyaEmrUtils#isPatientInProgram(org.openmrs.Patient, org.openmrs.Program)
	 */
	@Test
	public void isPatientInProgram() {
		Program tbProgram = Metadata.getProgram(Metadata.TB_PROGRAM);
		Patient patient = Context.getPatientService().getPatient(6);

		// Check with no enrollments
		Assert.assertFalse(KenyaEmrUtils.isPatientInProgram(patient, tbProgram));

		// Check with non-active enrollment
		TestUtils.enrollInProgram(patient, tbProgram, TestUtils.date(2011, 1, 1), TestUtils.date(2011, 12, 1));
		Assert.assertFalse(KenyaEmrUtils.isPatientInProgram(patient, tbProgram));

		// Check with active enrollment
		TestUtils.enrollInProgram(patient, tbProgram, TestUtils.date(2012, 1, 1));
		Assert.assertTrue(KenyaEmrUtils.isPatientInProgram(patient, tbProgram));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.util.KenyaEmrUtils#isRetrospectiveVisit(org.openmrs.Visit)
	 */
	@Test
	public void isRetrospectiveVisit() {
		Date date1 = TestUtils.date(2011, 1, 1, 10, 0, 0); // Jan 1st, 10:00am
		Date date2 = TestUtils.date(2011, 1, 1, 11, 0, 0); // Jan 1st, 11:00am

		// Check visit with no stop date
		Visit visit = new Visit();
		visit.setStartDatetime(date1);
		Assert.assertFalse(KenyaEmrUtils.isRetrospectiveVisit(visit));

		// Check visit with regular stop and start times
		visit.setStartDatetime(date1);
		visit.setStopDatetime(date2);
		Assert.assertFalse(KenyaEmrUtils.isRetrospectiveVisit(visit));

		// Check visit with absolute start but regular end date
		visit.setStartDatetime(OpenmrsUtil.firstSecondOfDay(date1));
		visit.setStopDatetime(date2);
		Assert.assertFalse(KenyaEmrUtils.isRetrospectiveVisit(visit));

		// Check visit with absolute start and end dates
		visit.setStartDatetime(OpenmrsUtil.firstSecondOfDay(date1));
		visit.setStopDatetime(OpenmrsUtil.getLastMomentOfDay(date1));
		Assert.assertTrue(KenyaEmrUtils.isRetrospectiveVisit(visit));

		// Check case when stop date has been persisted and lost its milliseconds
		Calendar stopFromSql = Calendar.getInstance();
		stopFromSql.setTime(OpenmrsUtil.getLastMomentOfDay(date1));
		stopFromSql.set(Calendar.MILLISECOND, 0);

		visit.setStartDatetime(OpenmrsUtil.firstSecondOfDay(date1));
		visit.setStopDatetime(stopFromSql.getTime());
		Assert.assertTrue(KenyaEmrUtils.isRetrospectiveVisit(visit));
	}

	/**
	 * @see KenyaEmrUtils#visitWillOverlap(org.openmrs.Visit)
	 */
	@Test
	public void visitWillOverlap_shouldReturnTrueIfVisitOverlaps() {

		Patient patient8 = Context.getPatientService().getPatient(8);
		VisitType outpatient = Metadata.getVisitType(Metadata.OUTPATIENT_VISIT_TYPE);

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

		Assert.assertFalse(KenyaEmrUtils.visitWillOverlap(visit));

		// Test overlap with #1
		visit.setStartDatetime(TestUtils.date(2011, 1, 2));
		visit.setStopDatetime(TestUtils.date(2011, 1, 4));

		Assert.assertTrue(KenyaEmrUtils.visitWillOverlap(visit));

		// Test touching #1 (visit dates are inclusive so counts as overlap)
		visit.setStartDatetime(TestUtils.date(2011, 1, 3));
		visit.setStopDatetime(TestUtils.date(2011, 1, 4));

		Assert.assertTrue(KenyaEmrUtils.visitWillOverlap(visit));

		// Test overlap with unstopped #3
		visit.setStartDatetime(TestUtils.date(2011, 2, 2));
		visit.setStopDatetime(TestUtils.date(2011, 2, 4));

		Assert.assertTrue(KenyaEmrUtils.visitWillOverlap(visit));

		// Check overlapping itself doesn't return true
		Assert.assertFalse(KenyaEmrUtils.visitWillOverlap(visit2));
	}

	@Test
	public void whoStage_shouldConvertConceptToInteger() {
		Assert.assertNull(KenyaEmrUtils.whoStage(Dictionary.getConcept(Dictionary.CD4_COUNT)));
		Assert.assertEquals(new Integer(1), KenyaEmrUtils.whoStage(Dictionary.getConcept(Dictionary.WHO_STAGE_1_PEDS)));
		Assert.assertEquals(new Integer(2), KenyaEmrUtils.whoStage(Dictionary.getConcept(Dictionary.WHO_STAGE_2_ADULT)));
		Assert.assertEquals(new Integer(3), KenyaEmrUtils.whoStage(Dictionary.getConcept(Dictionary.WHO_STAGE_3_PEDS)));
		Assert.assertEquals(new Integer(4), KenyaEmrUtils.whoStage(Dictionary.getConcept(Dictionary.WHO_STAGE_4_ADULT)));
	}

	/**
	 * @see KenyaEmrUtils#parseConceptList(String)
	 */
	@Test
	public void parseConceptList_shouldParseListCorrectly() {
		// Empty list
		List<Concept> concepts = KenyaEmrUtils.parseConceptList("");
		Assert.assertEquals(0, concepts.size());

		// No spaces
		concepts = KenyaEmrUtils.parseConceptList("5497,730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,5356");
		Assert.assertEquals(3, concepts.size());
		Assert.assertEquals(Dictionary.getConcept("5497AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), concepts.get(0));
		Assert.assertEquals(Dictionary.getConcept("730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), concepts.get(1));
		Assert.assertEquals(Dictionary.getConcept("5356AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), concepts.get(2));

		// Some spaces
		concepts = KenyaEmrUtils.parseConceptList(" 5497,  730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\t , 5356   \t");
		Assert.assertEquals(3, concepts.size());
		Assert.assertEquals(Dictionary.getConcept("5497AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), concepts.get(0));
		Assert.assertEquals(Dictionary.getConcept("730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), concepts.get(1));
		Assert.assertEquals(Dictionary.getConcept("5356AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), concepts.get(2));
	}

	@Test
	public void firstObsInEncounter_shouldFindFirstObsWithConcept() {
		Encounter e = new Encounter();

		// Test empty encounter
		Assert.assertNull(KenyaEmrUtils.firstObsInEncounter(e, Dictionary.getConcept(Dictionary.CD4_COUNT)));

		// Add obs to encounter
		Obs obs0 = new Obs();
		obs0.setConcept(Dictionary.getConcept(Dictionary.CD4_PERCENT));
		obs0.setValueNumeric(50.0);
		e.addObs(obs0);
		Obs obs1 = new Obs();
		obs1.setConcept(Dictionary.getConcept(Dictionary.CD4_COUNT));
		obs1.setValueNumeric(123.0);
		e.addObs(obs1);

		Assert.assertEquals(new Double(123.0), KenyaEmrUtils.firstObsInEncounter(e, Dictionary.getConcept(Dictionary.CD4_COUNT)).getValueNumeric());
	}

	@Test
	public void firstObsInProgram_shouldFindFirstObsWithConcept() {
		Patient patient = Context.getPatientService().getPatient(6);
		Program tbProgram = Metadata.getProgram(Metadata.TB_PROGRAM);

		PatientProgram enrollment = TestUtils.enrollInProgram(patient, tbProgram, TestUtils.date(2012, 1, 1), TestUtils.date(2012, 4, 1));

		// Test with no saved obs
		Assert.assertNull(KenyaEmrUtils.firstObsInProgram(enrollment, Dictionary.getConcept(Dictionary.CD4_COUNT)));

		// Before enrollment
		Obs obs0 = TestUtils.saveObs(patient, Dictionary.getConcept(Dictionary.CD4_COUNT), 123.0, TestUtils.date(2011, 12, 1));
		// Wrong concept
		Obs obs1 = TestUtils.saveObs(patient, Dictionary.getConcept(Dictionary.CD4_PERCENT), 50.0, TestUtils.date(2012, 1, 15));
		// During enrollment
		Obs obs2 = TestUtils.saveObs(patient, Dictionary.getConcept(Dictionary.CD4_COUNT), 234.0, TestUtils.date(2012, 2, 1));
		Obs obs3 = TestUtils.saveObs(patient, Dictionary.getConcept(Dictionary.CD4_COUNT), 345.0, TestUtils.date(2012, 3, 1));

		Assert.assertEquals(obs2, KenyaEmrUtils.firstObsInProgram(enrollment, Dictionary.getConcept(Dictionary.CD4_COUNT)));

		// Test again with no enrollment end date
		enrollment = TestUtils.enrollInProgram(patient, tbProgram, TestUtils.date(2012, 1, 1));
		Assert.assertEquals(obs2, KenyaEmrUtils.firstObsInProgram(enrollment, Dictionary.getConcept(Dictionary.CD4_COUNT)));
	}

	@Test
	public void lastEncounterInProgram_shouldFindLastEncounterWithType() {
		Patient patient = Context.getPatientService().getPatient(6);
		Program tbProgram = Metadata.getProgram(Metadata.TB_PROGRAM);
		EncounterType tbScreenEncType = Metadata.getEncounterType(Metadata.TB_SCREENING_ENCOUNTER_TYPE);

		PatientProgram enrollment = TestUtils.enrollInProgram(patient, tbProgram, TestUtils.date(2012, 1, 1), TestUtils.date(2012, 4, 1));

		// Test with no saved encounters
		Assert.assertNull(KenyaEmrUtils.lastEncounterInProgram(enrollment, tbScreenEncType));

		// Before enrollment
		Encounter enc0 = TestUtils.saveEncounter(patient, tbScreenEncType, TestUtils.date(2011, 12, 1));
		// During enrollment
		Encounter enc1 = TestUtils.saveEncounter(patient, tbScreenEncType, TestUtils.date(2012, 2, 1));
		Encounter enc2 = TestUtils.saveEncounter(patient, tbScreenEncType, TestUtils.date(2012, 3, 1));
		// After enrollment
		Encounter enc3 = TestUtils.saveEncounter(patient, tbScreenEncType, TestUtils.date(2012, 5, 1));

		Assert.assertEquals(enc2, KenyaEmrUtils.lastEncounterInProgram(enrollment, tbScreenEncType));

		// Test again with no enrollment end date
		enrollment = TestUtils.enrollInProgram(patient, tbProgram, TestUtils.date(2012, 1, 1));
		Assert.assertEquals(enc3, KenyaEmrUtils.lastEncounterInProgram(enrollment, tbScreenEncType));
	}

	@Test
	public void checkCielVersion_shouldReturnFalseIfVersionIsNotParseable() {
		Assert.assertFalse(KenyaEmrUtils.checkCielVersions("20130101", null));
		Assert.assertFalse(KenyaEmrUtils.checkCielVersions("20130101", "x"));
	}

	@Test
	public void checkCielVersion_shouldReturnTrueIfFoundVersionIsGreaterOrEqual() {
		Assert.assertFalse(KenyaEmrUtils.checkCielVersions("20130101", "20121201"));
		Assert.assertTrue(KenyaEmrUtils.checkCielVersions("20130101", "20130101"));
		Assert.assertTrue(KenyaEmrUtils.checkCielVersions("20130101", "20130102"));
	}
}