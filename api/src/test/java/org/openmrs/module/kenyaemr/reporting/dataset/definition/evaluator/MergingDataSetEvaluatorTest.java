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

package org.openmrs.module.kenyaemr.reporting.dataset.definition.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.reporting.dataset.definition.MergingDataSetDefinition;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Tests for {@link MergingDataSetEvaluator}
 */
public class MergingDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {

	private CohortIndicatorDataSetDefinition cohortDsd1, cohortDsd2;

	private EvaluationContext evaluationContext;

	private MergingDataSetEvaluator evaluator = new MergingDataSetEvaluator();

	/**
	 * Setups up to very simple cohort indicator definitions and an evaluation context
	 */
	@Before
	public void setup() {
		GenderCohortDefinition malesCohort = new GenderCohortDefinition();
		malesCohort.setName("Gender = Male");
		malesCohort.setMaleIncluded(true);

		GenderCohortDefinition femalesCohort = new GenderCohortDefinition();
		femalesCohort.setName("Gender = Female");
		femalesCohort.setFemaleIncluded(true);

		AgeCohortDefinition pedsCohort = new AgeCohortDefinition();
		pedsCohort.setName("Age < 15");
		pedsCohort.addParameter(new Parameter("effectiveDate", "Date", Date.class));
		pedsCohort.setMaxAge(14);

		CohortIndicator malesIndicator = new CohortIndicator("Count of males");
		malesIndicator.setCohortDefinition(malesCohort, "");

		CohortIndicator femalesIndicator = new CohortIndicator("Count of females");
		femalesIndicator.setCohortDefinition(femalesCohort, "");

		CohortIndicator pedsIndicator = new CohortIndicator("Count of peds");
		pedsIndicator.addParameter(new Parameter("date", "Date", Date.class));
		pedsIndicator.setCohortDefinition(pedsCohort, "effectiveDate=${date}");

		cohortDsd1 = new CohortIndicatorDataSetDefinition();
		cohortDsd1.setName("Cohort DSD1");
		cohortDsd1.addColumn("test-1", "Count of males", new Mapped<CohortIndicator>(malesIndicator, null), "");
		cohortDsd1.addColumn("test-3", "Count of peds", ReportUtils.map(pedsIndicator, "date=${date}"), "");

		cohortDsd2 = new CohortIndicatorDataSetDefinition();
		cohortDsd2.setName("Cohort DSD2");
		cohortDsd1.addColumn("test-2", "Count of females", new Mapped<CohortIndicator>(femalesIndicator, null), "");

		evaluationContext = new EvaluationContext();
		evaluationContext.addParameterValue("date", TestUtils.date(2012, 1, 1));
		evaluationContext.setBaseCohort(new Cohort(Context.getPatientService().getAllPatients()));
	}

	@Test
	public void evaluate_shouldOrderColumnsAccordingToMergeOrder() throws EvaluationException {
		MergingDataSetDefinition mergedDsd = new MergingDataSetDefinition();
		mergedDsd.addDataSetDefinition(cohortDsd1);
		mergedDsd.addDataSetDefinition(cohortDsd2);

		// Test with no sorting
		MapDataSet dataSet = evaluator.evaluate(mergedDsd, evaluationContext);
		checkIndicatorDataSet(Arrays.asList("test-1", "test-3", "test-2"), Arrays.asList(2, 1, 2), dataSet);

		// Test sorting by name
		mergedDsd.setMergeOrder(MergingDataSetDefinition.MergeOrder.NAME);

		dataSet = evaluator.evaluate(mergedDsd, evaluationContext);
		checkIndicatorDataSet(Arrays.asList("test-1", "test-2", "test-3"), Arrays.asList(2, 2, 1), dataSet);

		// Test sorting by label
		mergedDsd.setMergeOrder(MergingDataSetDefinition.MergeOrder.LABEL);

		dataSet = evaluator.evaluate(mergedDsd, evaluationContext);
		checkIndicatorDataSet(Arrays.asList("test-2", "test-1", "test-3"), Arrays.asList(2, 2, 1), dataSet);
	}

	/**
	 * Checks the column names and values of a indicator data set
	 * @param expectedColumnNames the expected column names
	 * @param expectedColumnValues the expected column values
	 * @param dataSet the data set to be checked
	 */
	private void checkIndicatorDataSet(List<String> expectedColumnNames, List<Integer> expectedColumnValues, MapDataSet dataSet) {

		Assert.assertEquals(expectedColumnNames.size(), dataSet.getMetaData().getColumnCount());

		for (int col = 0; col < expectedColumnNames.size(); col++) {
			DataSetColumn column = dataSet.getMetaData().getColumns().get(col);

			// Check column name
			Assert.assertEquals(expectedColumnNames.get(col), column.getName());

			// Check column data value
			IndicatorResult result = (IndicatorResult) dataSet.getData(column);
			Assert.assertEquals(expectedColumnValues.get(col).intValue(), result.getValue().intValue());
		}
	}
}
