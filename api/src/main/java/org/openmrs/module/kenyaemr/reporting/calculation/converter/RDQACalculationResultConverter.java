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

import org.openmrs.Concept;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.reporting.RDQAReportUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.Date;

public class RDQACalculationResultConverter implements DataConverter{
	@Override
	public Object convert(Object obj) {

		if (obj == null) {
			return "Missing";
		}

		Object value = ((CalculationResult) obj).getValue();

		if (value instanceof Boolean) {
			return (Boolean) value ? "Yes" : "No";
		}
		else if (value instanceof Date) {
			return RDQAReportUtils.formatdates((Date) value, "dd/MM/yyyy");
		}
		else if (value instanceof Concept) {

			return ((Concept) value).getName();
		}
		else if (value instanceof String) {
			return value;
		}
		else if (value instanceof Double) {
			return value;
		}

		return "Missing";
	}

	@Override
	public Class<?> getInputDataType() {
		return CalculationResult.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}
