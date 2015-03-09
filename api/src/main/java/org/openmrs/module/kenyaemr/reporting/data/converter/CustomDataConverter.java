package org.openmrs.module.kenyaemr.reporting.data.converter;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Created by codehub on 09/03/15.
 */
public class CustomDataConverter implements DataConverter {

	@Override
	public Object convert(Object obj) {

		if (obj == null) {
			return "Missing";
		}

		Concept value = ((Obs) obj).getValueCoded();

		if(value != null && value.getName() != null) {
			return value.getName().getName();
		}

		return null;
	}

	@Override
	public Class<?> getInputDataType() {
		return Object.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}
