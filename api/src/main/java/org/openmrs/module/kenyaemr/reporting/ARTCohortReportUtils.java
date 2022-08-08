/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Add description of the class
 */
public class ARTCohortReportUtils {

	public static final String DATE_FORMAT = "MM/yyyy";

	public static String formatdates(Date date, String format){
		if (date == null)
			return "Missing";

		Format formatter;
		formatter = new SimpleDateFormat(format);
		String s = formatter.format(date);

		return s;

	}
}
