package org.openmrs.module.kenyaemr.reporting.data.converter;

import org.openmrs.PatientProgram;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.Date;

/**
 * Converter class for PatientProgram
 */
public class DateOfLastEnrollmentConverter implements DataConverter {
	@Override
	public Object convert(Object original) {

		PatientProgram pp = (PatientProgram) original;

		if (pp == null)
			return "";

		if (pp.getDateEnrolled() == null)
			return "";

		return  pp.getDateEnrolled();
	}

	@Override
	public Class<?> getInputDataType() {
		return PatientProgram.class;
	}

	@Override
	public Class<?> getDataType() {
		return Date.class;
	}

}
