/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter;

import org.openmrs.module.kenyaemr.reporting.RDQAReportUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDateStringConverter implements DataConverter{
	@Override
	public Object convert(Object obj) {

		if (obj == null) {
			return "";
		}

		String value = String.valueOf(obj);
		Date dateValue = null;
		try {
			dateValue = new SimpleDateFormat("yyyy-MM-dd").parse(value);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return RDQAReportUtils.formatdates((Date) dateValue, "dd/MM/yyyy");
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
