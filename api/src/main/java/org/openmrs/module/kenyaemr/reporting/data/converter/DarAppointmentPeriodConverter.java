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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Returns a longer version of gender i.e Male for M and Female for F
 */
public class DarAppointmentPeriodConverter implements DataConverter {

	private Log log = LogFactory.getLog(getClass());

	public DarAppointmentPeriodConverter() {}

	/**
	 * @should return a blank string if valueNumeric is null
	 */
	@Override
	public Object convert(Object original) {

		if (original == null) {
			return "";
		}

		if (original instanceof String) {
			String valString = (String) original;// check for R in the response
			if (valString.equals("R") || valString.equals("")) {
				return original;
			}
			//attempt getting the int value of string
			Integer strInt = Integer.valueOf(valString);
			if (strInt.equals(0)){
				return 1;
			} else {
				return strInt.intValue();
			}
		}
		if (original instanceof Number) {
			Integer o = (Integer) original;

			if (o == null)
				return "";

			if (o.equals(0)){
				return 1;
			} else {
				return o.intValue();
			}
		}
		return original;
	}

	@Override
	public Class<?> getInputDataType() {
		return Integer.class;
	}

	@Override
	public Class<?> getDataType() {
		return Integer.class;
	}



}
