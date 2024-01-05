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

import org.openmrs.Encounter;
import org.openmrs.module.kenyaemr.reporting.RDQAReportUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Converter to get obsDatetime from an encounter
 */
public class EncounterDatetimeConverter implements DataConverter {
	@Override
	public Object convert(Object original) {
		Encounter e = (Encounter) original;

		if (e == null)
			return null;

		return RDQAReportUtils.formatdates(e.getEncounterDatetime(), RDQAReportUtils.DATE_FORMAT);
	}

	@Override
	public Class<?> getInputDataType() {
		return Encounter.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}
