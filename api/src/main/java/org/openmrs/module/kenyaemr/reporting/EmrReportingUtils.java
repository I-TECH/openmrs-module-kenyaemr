package org.openmrs.module.kenyaemr.reporting;

import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility methods for reporting
 */
public class EmrReportingUtils {

	/**
	 * Maps a parameterizable item with no parameters
	 * @param parameterizable the parameterizable item
	 * @param <T>
	 * @return the mapped item
	 */
	public static <T extends Parameterizable> Mapped<T> map(T parameterizable) {
		if (parameterizable == null) {
			throw new IllegalArgumentException("Parameterizable cannot be null");
		}
		return new Mapped<T>(parameterizable, null);
	}

	/**
	 * Maps a parameterizable item using a string list of parameters and values
	 * @param parameterizable the parameterizable item
	 * @param mappings the string list of mappings
	 * @param <T>
	 * @return the mapped item
	 */
	public static <T extends Parameterizable> Mapped<T> map(T parameterizable, String mappings) {
		if (parameterizable == null) {
			throw new IllegalArgumentException("Parameterizable cannot be null");
		}
		if (mappings == null) {
			mappings = ""; // probably not necessary, just to be safe
		}
		return new Mapped<T>(parameterizable, ParameterizableUtil.createParameterMappings(mappings));
	}

	/**
	 * Maps a parameterizable item using a string list of parameters and values
	 * @param parameterizable the parameterizable item
	 * @param mappings the string list of mappings
	 * @param <T>
	 * @return the mapped item
	 */
	public static <T extends Parameterizable> Mapped<T> map(T parameterizable, Object ... mappings) {
		if (parameterizable == null) {
			throw new IllegalArgumentException("Parameterizable cannot be null");
		}

		Map<String, Object> paramMap = new HashMap<String, Object>();
		for (int m = 0; m < mappings.length; m += 2) {
			String param = (String) mappings[m];
			Object value = mappings[m + 1];
			paramMap.put(param, value);
		}
		return new Mapped<T>(parameterizable, paramMap);
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