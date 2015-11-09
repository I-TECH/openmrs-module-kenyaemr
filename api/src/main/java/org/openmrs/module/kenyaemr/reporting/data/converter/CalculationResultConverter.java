/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
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

public class CalculationResultConverter implements DataConverter{
	@Override
	public Object convert(Object obj) {
		KenyaUiUtils kenyaui = Context.getRegisteredComponents(KenyaUiUtils.class).get(0);

		if (obj == null) {
			return "";
		}

		Object value = ((CalculationResult) obj).getValue();

		if (value instanceof Boolean) {
			return (Boolean) value ? "Y" : "N";
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
		DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
		return date == null?"":dateFormatter.format(date);
	}
}
