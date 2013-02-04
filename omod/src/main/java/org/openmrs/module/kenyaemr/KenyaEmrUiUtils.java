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

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.PrettyTime;
import org.openmrs.*;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.kenyaemr.form.FormConfig;
import org.openmrs.module.kenyaemr.regimen.*;
import org.openmrs.module.kenyaemr.util.KenyaEmrUtils;
import org.openmrs.ui.framework.FormatterImpl;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.WebConstants;

import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * UI utility methods for web pages
 */
public class KenyaEmrUiUtils {

	private static final DateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");

	private static final DateFormat timeFormatter = new SimpleDateFormat("HH:mm");

	/**
	 * Sets the notification success message
	 * @param session the session
	 * @param message the message
	 */
	public static void notifySuccess(HttpSession session, String message) {
		session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, message);
	}

	/**
	 * Sets the notification error message
	 * @param session the session
	 * @param message the message
	 */
	public static void notifyError(HttpSession session, String message) {
		session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, message);
	}

	/**
	 * Formats a date time
	 * @param date the date
	 * @return the string value
	 */
	public static String formatDateTime(Date date) {
		if (date == null)
			return "";

		return dateFormatter.format(date) + " " + timeFormatter.format(date);
	}

	/**
	 * Formats a date ignoring any time information
	 * @param date the date
	 * @return the string value
	 * @should format date as a string without time information
	 * @should format null date as empty string
	 */
	public static String formatDate(Date date) {
		if (date == null)
			return "";

		return dateFormatter.format(date);
	}

	/**
	 * Formats a date as a time value only
	 * @param date the date
	 * @return the string value
	 * @should format date as a string without time information
	 */
	public static String formatTime(Date date) {
		if (date == null)
			return "";

		return timeFormatter.format(date);
	}

	/**
	 * @deprecated
	 */
	public static String formatDateNoTime(Date date) {
		return formatDate(date);
	}

	/**
	 * Formats a date interval
	 * @param date the date relative to now
	 */
	public static String formatInterval(Date date) {
		PrettyTime t = new PrettyTime(new Date());
		return t.format(date);
	}

	/**
	 * Formats the dates of the given visit
	 * @param visit the visit
	 * @return the string value
	 */
	public static String formatVisitDates(Visit visit) {
		if (isRetrospectiveVisit(visit)) {
			return formatDate(visit.getStartDatetime());
		}
		else {
			StringBuilder sb = new StringBuilder();
			sb.append(formatDateTime(visit.getStartDatetime()));

			if (visit.getStopDatetime() != null) {
				sb.append(" \u2192 ");

				if (KenyaEmrUtils.isSameDay(visit.getStartDatetime(), visit.getStopDatetime())) {
					sb.append(formatTime(visit.getStopDatetime()));
				}
				else {
					sb.append(formatDateTime(visit.getStopDatetime()));
				}
			}

			return sb.toString();
		}
	}

	/**
	 * Formats a regimen in long format
	 * @param regimen the regimen
	 * @param ui the UI utils
	 * @return the string value
	 */
	public static String formatRegimenShort(Regimen regimen, UiUtils ui) {
		if (CollectionUtils.isEmpty(regimen.getDrugOrders())) {
			return "Empty";
		}
		List<String> components = new ArrayList<String>();
		for (DrugOrder o : regimen.getDrugOrders()) {
			ConceptName cn = o.getConcept().getPreferredName(Context.getLocale());
			if (cn == null) {
				cn = o.getConcept().getName(Context.getLocale());
			}
			components.add(cn.getName());
		}
		return OpenmrsUtil.join(components, ", ");
	}

	/**
	 * Formats a regimen in long format
	 * @param regimen the regimen
	 * @param ui the UI utils
	 * @return the string value
	 */
	public static String formatRegimenLong(Regimen regimen, UiUtils ui) {
		if (CollectionUtils.isEmpty(regimen.getDrugOrders())) {
			return "Empty";
		}
		List<String> components = new ArrayList<String>();
		for (DrugOrder o : regimen.getDrugOrders()) {
			String s = RegimenManager.findDrugCode("ARV", o.getConcept());
			if (s == null) {
				s = o.getConcept().getName(Context.getLocale()).getName();
			}
			if (o.getDose() != null) {
				s += " " + ui.format(o.getDose()) + o.getUnits();
			}
			if (o.getFrequency() != null) {
				s += " " + o.getFrequency();
			}
			components.add(s);
		}
		return OpenmrsUtil.join(components, " + ");
	}

	/**
	 * Simplifies a list of patients
	 * @param patients the patients
	 * @param ui the UI utils
	 * @return
	 */
	public static List<SimpleObject> simplePatients(Collection<Patient> patients, UiUtils ui) {
		List<SimpleObject> ret = new ArrayList<SimpleObject>();
		long now = System.currentTimeMillis();
		for (Patient patient : patients) {
			SimpleObject so = SimpleObject.fromObject(patient, ui, "patientId", "personName", "age", "birthdate", "birthdateEstimated", "gender", "activeIdentifiers.identifierType", "activeIdentifiers.identifier");
			Period p = new Period(patient.getBirthdate().getTime(), now, PeriodType.yearMonthDay());
			so.put("ageMonths", p.getMonths());
			so.put("ageDays", p.getDays());
			ret.add(so);
		}
		return ret;
	}

	/**
	 * Simplifies a location
	 * @param location the location
	 * @param mfcAttrType the MFL code attribute type
	 * @param ui the UI utils
	 * @return the simple object
	 */
	public static SimpleObject simpleLocation(Location location, LocationAttributeType mfcAttrType, UiUtils ui) {
		List<LocationAttribute> attrs = location.getActiveAttributes(mfcAttrType);
		String facilityCode = attrs.size() > 0 ? (String)attrs.get(0).getValue() : null;
		String display = location.getName() + " (" + (facilityCode != null ? facilityCode : "?") + ")";

		return SimpleObject.create("value", location.getLocationId(), "label", display);
	}

	/**
	 * Simplifies a form configuration
	 * @param config the form config
	 * @param ui the UI utils
	 * @return the simple object
	 */
	public static SimpleObject simpleForm(FormConfig config, UiUtils ui) {
		Form form = Context.getFormService().getFormByUuid(config.getFormUuid());
		HtmlForm htmlForm = Context.getService(HtmlFormEntryService.class).getHtmlFormByForm(form);

		return SimpleObject.create(
				"formUuid", config.getFormUuid(),
				"htmlFormId", htmlForm.getId(),
				"label", htmlForm.getName(),
				"iconProvider", config.getIconProvider(),
				"icon", config.getIcon());
	}

	/**
	 * Simplifies a visit
	 * @param visit the visit
	 * @param ui the UI utils
	 * @return the simple object
	 */
	public static SimpleObject simpleVisit(Visit visit, UiUtils ui) {
		return SimpleObject.fromObject(visit, ui, "visitId", "visitType", "startDatetime", "stopDatetime");
	}

	/**
	 * Creates a simple regimen object
	 * @param regimen the regimen
	 * @param ui the UI utils
	 * @return the simple object with { shortDisplay, longDisplay }
	 */
	public static SimpleObject simpleRegimen(Regimen regimen, UiUtils ui) {
		if (regimen == null) {
			return SimpleObject.create("shortDisplay", "None", "longDisplay", "None");
		} else {
			return SimpleObject.create("shortDisplay", formatRegimenShort(regimen, ui), "longDisplay", formatRegimenLong(regimen, ui));
		}
	}

	/**
	 * Converts the given regimen history to simple objects
	 * @param history the regimen history
	 * @param ui the UI utils
	 * @return a list of objects with { startDate, endDate, shortDisplay, longDisplay, changeReasons[] }
	 */
	public static List<SimpleObject> simpleRegimenHistory(RegimenHistory history, UiUtils ui) {
		List<RegimenChange> changes = history.getChanges();

		List<SimpleObject> ret = new ArrayList<SimpleObject>();

		if (changes.size() == 0) {
			return ret;
		}

		for (int i = 0; i < changes.size(); ++i) {
			RegimenChange change = changes.get(i);
			Date startDate = change.getDate();
			Regimen regimen = change.getStarted();
			Date endDate = null;
			List<String> changeReasons = new ArrayList<String>();
			if (i + 1 < changes.size()) {
				RegimenChange next = changes.get(i + 1);
				endDate = next.getDate();
				if (next.getChangeReasons() != null) {
					for (Concept c : next.getChangeReasons()) {
						changeReasons.add(ui.format(c));
					}
				}
				if (next.getChangeReasonsNonCoded() != null) {
					changeReasons.addAll(next.getChangeReasonsNonCoded());
				}
			}
			ret.add(SimpleObject.create(
				"startDate", KenyaEmrUiUtils.formatDateNoTime(startDate),
				"endDate", KenyaEmrUiUtils.formatDateNoTime(endDate),
				"regimen", simpleRegimen(regimen, ui),
				"changeReasons", changeReasons
			));
		}

		return ret;
	}

	/**
	 * Converts regimen definitions to simple objects
	 * @param definitions the regimen definitions
	 * @param ui the UI utils
	 * @return a list of objects with { name, suitability, components.conceptId, components.dose, components.units }
	 */
	public static List<SimpleObject> simpleRegimenDefinitions(Collection<RegimenDefinition> definitions, UiUtils ui) {
		return SimpleObject.fromCollection(definitions, ui,
				"name", "group", "components.conceptId", "components.dose", "components.units", "components.frequency"
		);
	}

	/**
	 * Checks if a visit has been entered retrospectively. Visits entered retrospectively are entered with just a single
	 * date value and are always stopped
	 * @param visit the visit
	 * @return true if visit was entered retrospectively
	 */
	public static boolean isRetrospectiveVisit(Visit visit) {
		if (visit.getStopDatetime() == null) {
			return false;
		}

		// Check that start is first second of day
		// Note that we don't compare milliseconds as these are lost in persistence
		Calendar start = Calendar.getInstance();
		start.setTime(visit.getStartDatetime());
		if (start.get(Calendar.HOUR_OF_DAY) != 0 || start.get(Calendar.MINUTE) != 0 || start.get(Calendar.SECOND) != 0) {
			return false;
		}

		// Check that stop is last second of day
		Calendar stop = Calendar.getInstance();
		stop.setTime(visit.getStopDatetime());
		if (stop.get(Calendar.HOUR_OF_DAY) != 23 || stop.get(Calendar.MINUTE) != 59 || stop.get(Calendar.SECOND) != 59) {
			return false;
		}

		// Check start is same day as stop
		return start.get(Calendar.YEAR) == stop.get(Calendar.YEAR)
				&& start.get(Calendar.MONTH) == stop.get(Calendar.MONTH)
				&& start.get(Calendar.DAY_OF_MONTH) == stop.get(Calendar.DAY_OF_MONTH);
	}
}