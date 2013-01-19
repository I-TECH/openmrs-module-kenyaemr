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

	private static DateFormat timeFormatter = new SimpleDateFormat("HH:mm");

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
	 * Formats a date interval
	 * @param date the date relative to now
	 */
	public static String formatInterval(Date date) {
		PrettyTime t = new PrettyTime(new Date());
		return t.format(date);
	}

	/**
	 * Formats a regimen in long format
	 * @param regimen the regimen
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
	 * @return the simple object with {  }
	 */
	public static SimpleObject simpleLocation(Location location, LocationAttributeType mfcAttrType, UiUtils ui) {
		List<LocationAttribute> attrs = location.getActiveAttributes(mfcAttrType);
		String facilityCode = attrs.size() > 0 ? (String)attrs.get(0).getValue() : null;
		String display = location.getName() + " (" + (facilityCode != null ? facilityCode : "?") + ")";
		return SimpleObject.create("value", location.getLocationId(), "label", display);
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
	 * Checks if the visit has been entered retrospectively
	 * @param visit
	 * @return
	 */
	public static String isRetrospectiveVisit(Visit visit) {
		String retrospective = "false";
		
		for (Encounter e : visit.getEncounters()) {
			if (e.getEncounterType().getUuid().equals(MetadataConstants.HIV_RETROSPECTIVE_ENCOUNTER_TYPE_UUID)) {
				retrospective = "true";
				break;
			}
		}
		return retrospective;		
	}
}