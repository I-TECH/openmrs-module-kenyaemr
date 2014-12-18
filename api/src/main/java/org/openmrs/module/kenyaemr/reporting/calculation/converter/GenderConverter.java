package org.openmrs.module.kenyaemr.reporting.calculation.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Returns a longer version of gender i.e Male for M and Female for F
 */
public class GenderConverter implements DataConverter {

	private Log log = LogFactory.getLog(getClass());

	public GenderConverter() {}

	/**
	 * @should return a blank string if valueNumeric is null
	 */
	@Override
	public Object convert(Object original) {

		String o = (String) original;

		if (o == null)
			return "Missing";

		if (o.equals("M")){
			return "Male";
		} else {
			return "Female";
		}
	}

	@Override
	public Class<?> getInputDataType() {
		return String.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}



}
