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

import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.reporting.ARTCohortReportUtils;
import org.openmrs.module.kenyaemr.reporting.RDQAReportUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.Date;

/**
 * Converter to get obsDatetime from an observation
 */
public class ArtCohortStartMonthYearDateConverter implements DataConverter {
	@Override
	public Object convert(Object original) {
		if (original == null)
			return "Missing";

		Object value = ((CalculationResult) original).getValue();

		if (value == null)
			return "Missing";

		return RDQAReportUtils.formatdates((Date) value, ARTCohortReportUtils.DATE_FORMAT);
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
