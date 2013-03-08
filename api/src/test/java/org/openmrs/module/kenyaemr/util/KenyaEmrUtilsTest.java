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
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsUtil;

import java.util.*;

public class KenyaEmrUtilsTest extends BaseModuleContextSensitiveTest {

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
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
	 * @see KenyaEmrUtils#fetchConcepts(java.util.Collection)
	 * @verifies fetch from concepts, integers or strings
	 */
	@Test
	public void fetchConcepts_shouldFetchFromConceptsIntegersOrStrings() {
		Concept cd4 = Context.getConceptService().getConcept(5497);
		List<Object> conceptsOrIds = new ArrayList<Object>();
		conceptsOrIds.add(cd4);
		conceptsOrIds.add(5497);
		conceptsOrIds.add("5497");
		List<Concept> concepts = KenyaEmrUtils.fetchConcepts(conceptsOrIds);
		Assert.assertEquals(cd4, concepts.get(0));
		Assert.assertEquals(cd4, concepts.get(1));
		Assert.assertEquals(cd4, concepts.get(2));
	}

	/**
	 * @see KenyaEmrUtils#fetchConcepts(java.util.Collection)
	 * @verifies throw exception for non concepts, integers or strings
	 */
	@Test
	public void fetchConcepts_shouldThrowExceptionForNonConceptsIntegersOrString() {
		try {
			KenyaEmrUtils.fetchConcepts(Collections.singletonList(new Date()));
			Assert.fail();
		}
		catch (IllegalArgumentException ex) {
		}
	}

	/**
	 * @see org.openmrs.module.kenyaemr.util.KenyaEmrUtils#isRetrospectiveVisit(org.openmrs.Visit)
	 */
	@Test
	public void isRetrospectiveVisit() {
		Date date1 = TestUtils.date(2011, 1, 1, 10, 0, 0); // Jan 1st, 10:00am
		Date date2 = TestUtils.date(2011, 1, 1, 11, 0, 0); // Jan 1st, 11:00am

		Visit visit1 = new Visit();
		visit1.setStartDatetime(date1);
		visit1.setStopDatetime(date2);

		Assert.assertFalse(KenyaEmrUtils.isRetrospectiveVisit(visit1));

		Visit visit2 = new Visit();
		visit2.setStartDatetime(OpenmrsUtil.firstSecondOfDay(date1));
		visit2.setStopDatetime(OpenmrsUtil.getLastMomentOfDay(date1));

		Assert.assertTrue(KenyaEmrUtils.isRetrospectiveVisit(visit2));

		// Check case when stop date has been persisted and lost its milliseconds
		Calendar stopFromSql = Calendar.getInstance();
		stopFromSql.setTime(OpenmrsUtil.getLastMomentOfDay(date1));
		stopFromSql.set(Calendar.MILLISECOND, 0);

		Visit visit3 = new Visit();
		visit3.setStartDatetime(OpenmrsUtil.firstSecondOfDay(date1));
		visit3.setStopDatetime(stopFromSql.getTime());

		Assert.assertTrue(KenyaEmrUtils.isRetrospectiveVisit(visit3));
	}

	/**
	 * @see KenyaEmrUtils#visitWillOverlap(org.openmrs.Visit)
	 */
	@Test
	public void visitWillOverlap_shouldReturnTrueIfVisitOverlaps() {

		Patient patient8 = Context.getPatientService().getPatient(8);
		VisitType outpatient = Context.getVisitService().getVisitTypeByUuid(MetadataConstants.OUTPATIENT_VISIT_TYPE_UUID);

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
}