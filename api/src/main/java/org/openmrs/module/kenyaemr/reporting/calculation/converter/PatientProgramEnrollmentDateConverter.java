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

import org.openmrs.PatientProgram;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.reporting.RDQAReportUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.Date;
import java.util.List;

/**
 * Returns a list of programs a patient is enrolled and the dates of enrollment
 * Where there is more than one program and HIV is part, date enrolled in hiv takes precedence
 */
public class PatientProgramEnrollmentDateConverter implements DataConverter {

	public static final String HIV = "dfdc6d40-2f2f-463d-ba90-cc97350441a8";

	@Override
	public Object convert(Object original) {

		SimpleResult baseResult = (SimpleResult) original;
		if (baseResult == null)
			return "Missing";

		List<PatientProgram> result = (List<PatientProgram>)baseResult.getValue();

		if (result.size() == 0)
			return "Missing";

		if (result.size() == 1) {
			Date date = result.get(0).getDateEnrolled();
			if (date != null)
				return RDQAReportUtils.formatdates(result.get(0).getDateEnrolled(), RDQAReportUtils.DATE_FORMAT);
		}

		Date dateEnrolled = findHIVProgramAndReturnDate(result);
		if (dateEnrolled != null)
			return RDQAReportUtils.formatdates(findHIVProgramAndReturnDate(result), RDQAReportUtils.DATE_FORMAT);
		return "Missing";
	}

	@Override
	public Class<?> getInputDataType() {
		return List.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}

	private Date findHIVProgramAndReturnDate(List<PatientProgram> enrolledPrograms){

		Date enrollmentDate = null;
		for (PatientProgram p : enrolledPrograms) {
			if (p.getProgram().getUuid().equals(HIV)) {
				enrollmentDate = p.getDateEnrolled();
				break;
			}
		}

		if (enrollmentDate == null)
			enrollmentDate = enrolledPrograms.get(0).getDateEnrolled();

		return enrollmentDate;
	}
}
