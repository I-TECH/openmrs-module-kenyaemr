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

package org.openmrs.module.kenyaemr.test;

import org.junit.Assert;
import org.junit.Ignore;
import org.openmrs.Cohort;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.renderer.TsvReportRenderer;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Reporting specific utility methods for unit tests
 */
@Ignore
public class ReportingTestUtils {

	/**
	 * Creates a reporting context
	 * @param cohort the base cohort
	 * @param startDate the start date
	 * @param endDate the end date
	 * @return the context
	 */
	public static EvaluationContext reportingContext(Collection<Integer> cohort, Date startDate, Date endDate) {
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("startDate", startDate);
		context.addParameterValue("endDate", endDate);
		context.setNow(endDate);
		context.setEvaluationDate(endDate);
		context.setBaseCohort(new Cohort(cohort));
		return context;
	}

	/**
	 * Asserts that a cohort contains all of the expected member ids
	 * @param expected the expected member ids
	 * @param actual the actual cohort
	 */
	public static void assertCohortEquals(Collection<Integer> expected, Cohort actual) {
		Assert.assertEquals(new HashSet<Integer>(expected), actual.getMemberIds());
	}

	/**
	 * Checks a patient alert list report
	 * @param expectedIds the set of expected patient ids
	 * @param data the report data
	 */
	public static void checkPatientListReport(Set<Integer> expectedIds, ReportData data) {
		// Check report has one data set
		Assert.assertEquals(1, data.getDataSets().values().size());
		DataSet set = data.getDataSets().values().iterator().next();

		// Make mutable copy
		expectedIds = new HashSet<Integer>(expectedIds);

		// Check the patient name of each row is in the expected set
		for (DataSetRow row : set) {
			Integer patientId = (Integer) row.getColumnValue("id");
			Assert.assertTrue("Patient identifier '" + patientId + "' not expected", expectedIds.contains(patientId));
			expectedIds.remove(patientId);
		}
	}

	/**
	 * Prints report data to the console
	 * @param data the report data
	 * @throws java.io.IOException if error occurs
	 */
	public static void printReport(ReportData data) throws IOException {
		System.out.println("------------ " + data.getDefinition().getName() + " -------------");
		new TsvReportRenderer().render(data, null, System.out);
		System.out.println("-------------------------------");
	}
}