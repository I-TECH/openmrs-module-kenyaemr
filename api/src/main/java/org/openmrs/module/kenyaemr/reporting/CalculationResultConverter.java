package org.openmrs.module.kenyaemr.reporting;

import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.Date;

public class CalculationResultConverter implements DataConverter{
	@Override
	public Object convert(Object obj) {
		KenyaUiUtils kenyaui = Context.getRegisteredComponents(KenyaUiUtils.class).get(0);

		if (obj == null) {
			return "";
		}

		Object value = ((CalculationResult) obj).getValue();

		if (value instanceof Boolean) {
			return (Boolean) value ? "Yes" : "No";
		}
		else if (value instanceof Date) {
			return kenyaui.formatDate((Date) value);
		}

		return null;
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
