package org.openmrs.module.kenyaemr.reporting.calculation.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Converter to get who stage from an observation
 */
public class WHOStageDataConverter implements DataConverter {

	private Log log = LogFactory.getLog(getClass());

	public WHOStageDataConverter() {}

	/**
	 * @should return a blank string if valueNumeric is null
	 */
	@Override
	public Object convert(Object original) {

		Obs o = (Obs) original;

		if (o == null)
			return "Missing";

		Concept answer = o.getValueCoded();

		if (answer == null)
			return "Missing";

		String value;

		if (answer.getId() ==1204 || answer.getId() == 1220 ){
			value = "I";
		} else if (answer.getId() ==1205 || answer.getId() == 1221) {
			value = "II";
		} else if (answer.getId() ==1206 || answer.getId() == 1222) {
			value = "III";
		} else if (answer.getId() ==1207 || answer.getId() == 1223) {
			value = "IV";
		} else {
			value = "Missing";
		}

		return value;
	}

	@Override
	public Class<?> getInputDataType() {
		return Obs.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}



}
