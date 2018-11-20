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

import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BirthdateConverter implements DataConverter {
	@Override
	public Object convert(Object obj) {
		 Birthdate birthdate = (Birthdate) obj;
		return formatDate(birthdate.getBirthdate());
	}

	@Override
	public Class<?> getInputDataType() {
		return Birthdate.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}

	private String formatDate(Date date) {
		DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
		return date == null?"":dateFormatter.format(date);
	}
}
