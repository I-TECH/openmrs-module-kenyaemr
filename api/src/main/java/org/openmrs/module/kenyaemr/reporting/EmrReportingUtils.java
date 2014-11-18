package org.openmrs.module.kenyaemr.reporting;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;

import java.util.Date;
import java.util.List;

/**
 * Utility methods for reporting
 */
public class EmrReportingUtils {

	/**
	 * Creates a new cohort indicator
	 * @param name the indicator name
	 * @param cohort the mapped cohort
	 * @return the cohort indicator
	 */
	public static CohortIndicator cohortIndicator(String name, Mapped<CohortDefinition> cohort) {
		CohortIndicator ind = new CohortIndicator(name);
		ind.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ind.addParameter(new Parameter("endDate", "End Date", Date.class));
		ind.setCohortDefinition(cohort);
		return ind;
	}

	/**
	 * Creates a new cohort indicator with numerator and a denominator cohort
	 * @param name the indicator name
	 * @param numeratorCohort the mapped numerator cohort
	 * @param denominatorCohort the mapped denominator cohort
	 * @return the cohort indicator
	 */
	public static CohortIndicator cohortIndicator(String name, Mapped<CohortDefinition> numeratorCohort, Mapped<CohortDefinition> denominatorCohort) {
		CohortIndicator ind = cohortIndicator(name, numeratorCohort);
		ind.setDenominator(denominatorCohort);
		ind.setType(CohortIndicator.IndicatorType.FRACTION);
		return ind;
	}

	/**
	 * Adds a row to a dataset based on an indicator and a list of column parameters (generates column name from the column parameters)
	 * @param cohortDsd the dataset
	 * @param baseName the base columm name
	 * @param baseLabel the base column label
	 * @param indicator the indicator
	 * @param columns the column parameters
	 */
	public static void addRow(CohortIndicatorDataSetDefinition cohortDsd, String baseName, String baseLabel, Mapped<CohortIndicator> indicator, List<ColumnParameters> columns) {
		addRow(cohortDsd, baseName, baseLabel, indicator, columns, null);
	}

	/**
	 * Adds a row to a dataset based on an indicator and a list of column parameters
	 * @param cohortDsd the dataset
	 * @param baseName the base columm name
	 * @param baseLabel the base column label
	 * @param indicator the indicator
	 * @param columns the column parameters
	 * @param columnNames the column names
	 */
	public static void addRow(CohortIndicatorDataSetDefinition cohortDsd, String baseName, String baseLabel, Mapped<CohortIndicator> indicator, List<ColumnParameters> columns, List<String> columnNames) {
		int c = 0;
		for (ColumnParameters column : columns) {
			String name = baseName + "-" + (columnNames != null ? columnNames.get(c++) : column.getName());
			String label = baseLabel + " (" + column.getLabel() + ")";
			cohortDsd.addColumn(name, label, indicator, column.getDimensions());
		}
	}
}