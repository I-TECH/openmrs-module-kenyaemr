package org.openmrs.module.kenyaemr.reporting.calculation.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.PatientProgram;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Returns a list of programs a patient is enrolled and the dates of enrollment
 * Where there is more than one program and HIV is part, date enrolled in hiv takes precedence
 */
public class PatientProgramEnrollmentConverter implements DataConverter {

	public static final String INTER_CELL_SEPARATOR = "\n";

	public static final char DEFAULT_CSV_DELIMITER = ',';

	@Override
	public Object convert(Object original) {

		SimpleResult baseResult = (SimpleResult) original;
		if (baseResult == null)
			return null;

		List<PatientProgram> result = (List<PatientProgram>)baseResult.getValue();

		Set<String> programs = new HashSet<String>();
		for (PatientProgram p : result){
			programs.add(p.getProgram().getName());
		}

		return StringUtils.join(programs, DEFAULT_CSV_DELIMITER);
	}

	@Override
	public Class<?> getInputDataType() {
		return List.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}
