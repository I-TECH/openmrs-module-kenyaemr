/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
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
