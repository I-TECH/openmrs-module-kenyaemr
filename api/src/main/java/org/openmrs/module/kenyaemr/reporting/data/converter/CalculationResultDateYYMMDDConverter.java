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

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalculationResultDateYYMMDDConverter implements DataConverter{
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
			return formatDate((Date) value);
		}
		else if (value instanceof Concept) {

			return ((Concept) value).getName();
		}
		else if (value instanceof String) {
			return value.toString();
		}
		else if (value instanceof Double) {
			return ((Double) value);
		}
		else if (value instanceof Integer){
			return ((Integer) value);
		}
		else if (value instanceof Location){
			return ((Location) value).getName();
		}
		else if (value instanceof SimpleResult) {
			return ((SimpleResult) value).getValue();
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

	private String formatDate(Date date) {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
		return date == null?"":dateFormatter.format(date);
	}
}
