package org.openmrs.module.kenyaemr.reporting.calculation.converter;

import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.reporting.RDQAReportUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.Date;

/**
 * Converter to get obsDatetime from an observation
 */
public class DateArtStartDateConverter implements DataConverter {
	@Override
	public Object convert(Object original) {
		if (original == null)
			return "N/A";

		Object value = ((CalculationResult) original).getValue();

		if (value == null)
			return "N/A";

		return RDQAReportUtils.formatdates((Date) value, RDQAReportUtils.DATE_FORMAT);
	}

	@Override
	public Class<?> getInputDataType() {
		return CalculationResult.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}
