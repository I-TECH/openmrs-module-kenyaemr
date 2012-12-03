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
package org.openmrs.module.kenyaemr;

import org.openmrs.DrugOrder;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyaemr.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.util.KenyaEmrUtils;
import org.openmrs.ui.framework.FormatterImpl;
import org.openmrs.util.OpenmrsUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * UI utility methods for web pages
 */
public class KenyaEmrUiUtils {

	private static DateFormat timeFormatter = new SimpleDateFormat("HH:mm");

	/**
	 * Formats a date ignoring any time information
	 * @param date the date
	 * @return the string value
	 * @should format date as a string without time information
	 * @should format null date as empty string
	 */
	public static String formatDateNoTime(Date date) {
		if (date == null)
			return "";

		Date dateOnly = KenyaEmrUtils.dateStartOfDay(date);
		return new FormatterImpl().format(dateOnly);
	}

	/**
	 * Formats a date as a time value only
	 * @param date the date
	 * @return the string value
	 * @should format date as a string without time information
	 */
	public static String formatTime(Date date) {
		return timeFormatter.format(date);
	}

	/**
	 * Formats a regimen
	 * @param regimen the regimen as a list result of drug orders
	 * @return the string value
	 */
	public static String formatRegimen(ListResult regimen) {
		return formatRegimen(CalculationUtils.<DrugOrder>extractListResultValues(regimen));
	}

	/**
	 * Formats a regimen
	 * @param regimen the regimen as a list of drug orders
	 * @return the string value
	 */
	public static String formatRegimen(List<DrugOrder> regimen) {
		List<String> components = new ArrayList<String>();
		for (DrugOrder order : regimen) {
			String component = order.getDrug() != null ? order.getDrug().getName() : order.getConcept().getName().getName();
			components.add(component);
		}

		return OpenmrsUtil.join(components, " + ");
	}
}