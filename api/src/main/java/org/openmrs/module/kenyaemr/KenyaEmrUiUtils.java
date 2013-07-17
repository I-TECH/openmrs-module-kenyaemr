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
import org.apache.commons.lang3.BooleanUtils;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.form.FormDescriptor;
import org.openmrs.module.kenyaemr.regimen.*;
import org.openmrs.module.kenyaemr.util.KenyaEmrUtils;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Date;

/**
 * UI utility methods for web pages
 */
@Component
public class KenyaEmrUiUtils {

	@Autowired
	private KenyaUiUtils kenyaUi;

	@Autowired
	private KenyaEmr emr;

	/**
	 * Formats a person's name
	 * @param name the person name
	 * @return the string value
	 */
	public String formatPersonName(PersonName name) {
		List<String> items = new ArrayList<String>();
		if (name.getFamilyName() != null) {
			items.add(name.getFamilyName() + ",");
		}
		if (name.getGivenName() != null) {
			items.add(name.getGivenName());
		}
		if (name.getMiddleName() != null) {
			items.add(name.getMiddleName());
		}
		return OpenmrsUtil.join(items, " ");
	}

	/**
	 * Formats a person's age
	 * @param person the person
	 * @return the string value
	 */
	public String formatPersonAge(Person person) {
		String prefix = BooleanUtils.isTrue(person.isBirthdateEstimated()) ? "~" : "";
		int ageYears = person.getAge();

		if (ageYears < 1) {
			Period p = new Period(person.getBirthdate().getTime(), System.currentTimeMillis(), PeriodType.yearMonthDay());
			return prefix + p.getMonths() + " month(s), " + p.getDays() + " day(s)";
		}
		else {
			return prefix + ageYears + " year(s)";
		}
	}

	/**
	 * Formats a person's birth date
	 * @param person the person
	 * @return the string value
	 */
	public String formatPersonBirthdate(Person person) {
		return (BooleanUtils.isTrue(person.isBirthdateEstimated()) ? "approx " : "") + kenyaUi.formatDate(person.getBirthdate());
	}

	/**
	 * Formats the dates of the given visit
	 * @param visit the visit
	 * @return the string value
	 */
	public String formatVisitDates(Visit visit) {
		if (KenyaEmrUtils.isRetrospectiveVisit(visit)) {
			return kenyaUi.formatDate(visit.getStartDatetime());
		}
		else {
			StringBuilder sb = new StringBuilder();
			sb.append(kenyaUi.formatDateTime(visit.getStartDatetime()));

			if (visit.getStopDatetime() != null) {
				sb.append(" \u2192 ");

				if (KenyaEmrUtils.isSameDay(visit.getStartDatetime(), visit.getStopDatetime())) {
					sb.append(kenyaUi.formatTime(visit.getStopDatetime()));
				}
				else {
					sb.append(kenyaUi.formatDateTime(visit.getStopDatetime()));
				}
			}

			return sb.toString();
		}
	}

	/**
	 * Formats a drug reference
	 * @param drugRef the drug reference
	 * @return the string value
	 */
	public String formatDrug(DrugReference drugRef, UiUtils ui) {
		return drugRef.isConceptOnly() ? drugRef.getConcept().getPreferredName(Metadata.LOCALE).getName() : drugRef.getDrug().getName();
	}

	/**
	 * Formats a regimen in long format
	 * @param regimen the regimen
	 * @param ui the UI utils
	 * @return the string value
	 */
	public String formatRegimenShort(RegimenOrder regimen, UiUtils ui) {
		if (CollectionUtils.isEmpty(regimen.getDrugOrders())) {
			return "Empty";
		}
		List<String> components = new ArrayList<String>();
		for (DrugOrder o : regimen.getDrugOrders()) {
			ConceptName cn = o.getConcept().getPreferredName(Metadata.LOCALE);
			if (cn == null) {
				cn = o.getConcept().getName(Metadata.LOCALE);
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
	public String formatRegimenLong(RegimenOrder regimen, UiUtils ui) {
		if (CollectionUtils.isEmpty(regimen.getDrugOrders())) {
			return "Empty";
		}
		List<String> components = new ArrayList<String>();
		for (DrugOrder o : regimen.getDrugOrders()) {
			StringBuilder sb = new StringBuilder();

			ConceptName cn = o.getConcept().getShortNameInLocale(Metadata.LOCALE);
			if (cn == null) {
				cn = o.getConcept().getName(Metadata.LOCALE);
			}
			sb.append(cn.getName());

			if (o.getDrug() != null) {
				sb.append(" " + o.getDrug().getDoseStrength() + o.getDrug().getUnits());
			}

			if (o.getDose() != null) {
				// If dose is a whole number, don't format with decimals... e.g. 3.0tabs looks weird
				boolean hasDecimals = Math.floor(o.getDose()) != o.getDose();
				String dose = hasDecimals ? ui.format(o.getDose()) : ui.format(o.getDose().intValue());

				sb.append(" " + dose + o.getUnits());
			}
			if (o.getFrequency() != null) {
				sb.append(" " + o.getFrequency());
			}
			components.add(sb.toString());
		}
		return OpenmrsUtil.join(components, " + ");
	}

	/**
	 * Simplifies a list of patients
	 * @param patients the patients
	 * @param ui the UI utils
	 * @return
	 */
	public List<SimpleObject> simplePatients(Collection<Patient> patients, UiUtils ui) {
		List<SimpleObject> ret = new ArrayList<SimpleObject>();
		for (Patient patient : patients) {
			ret.add(simplePatient(patient, ui));
		}
		return ret;
	}

	/**
	 * Simplifies a patient
	 * @param patient the patient
	 * @param ui the UI utils
	 * @return the simple object
	 */
	public SimpleObject simplePatient(Patient patient, UiUtils ui) {
		List<PatientIdentifier> identifiers = emr.getIdentifierManager().getPatientDisplayIdentifiers(patient);

		SimpleObject so = SimpleObject.fromObject(patient, ui, "patientId", "gender");

		// Add formatted name, age and birth date values
		so.put("name", formatPersonName(patient.getPersonName()));
		so.put("age", formatPersonAge(patient));
		so.put("birthdate", formatPersonBirthdate(patient));

		// Add display identifiers
		so.put("identifiers", SimpleObject.fromCollection(identifiers, ui, "identifierType", "identifier"));

		return so;
	}

	/**
	 * Simplifies a location
	 * @param location the location
	 * @param mfcAttrType the MFL code attribute type
	 * @param ui the UI utils
	 * @return the simple object
	 */
	public SimpleObject simpleLocation(Location location, LocationAttributeType mfcAttrType, UiUtils ui) {
		List<LocationAttribute> attrs = location.getActiveAttributes(mfcAttrType);
		String facilityCode = attrs.size() > 0 ? (String)attrs.get(0).getValue() : null;
		return SimpleObject.create("id", location.getId(), "name", location.getName(), "code", (facilityCode != null ? facilityCode : "?"));
	}

	/**
	 * Simplifies a form
	 * @param form the form
	 * @param ui the UI utils
	 * @return the simple object
	 */
	public SimpleObject simpleForm(Form form, UiUtils ui) {
		FormDescriptor config = emr.getFormManager().getFormDescriptor(form.getUuid());
		return SimpleObject.create("formUuid", form.getUuid(), "label", form.getName(), "iconProvider", config.getIconProvider(), "icon", config.getIcon());
	}

	/**
	 * Simplifies a form
	 * @param config the form config
	 * @param ui the UI utils
	 * @return the simple object
	 */
	public SimpleObject simpleForm(FormDescriptor config, UiUtils ui) {
		Form form = Metadata.getForm(config.getFormUuid());
		return simpleForm(form, ui);
	}

	/**
	 * Creates a simple regimen object
	 * @param regimen the regimen
	 * @param ui the UI utils
	 * @return the simple object with { shortDisplay, longDisplay }
	 */
	public SimpleObject simpleRegimen(RegimenOrder regimen, UiUtils ui) {
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
	public List<SimpleObject> simpleRegimenHistory(RegimenChangeHistory history, UiUtils ui) {
		List<RegimenChange> changes = history.getChanges();
		List<SimpleObject> ret = new ArrayList<SimpleObject>();

		if (changes.size() == 0) {
			return ret;
		}

		Date now = new Date();

		for (int i = 0; i < changes.size(); ++i) {
			RegimenChange change = changes.get(i);
			Date startDate = change.getDate();
			RegimenOrder regimen = change.getStarted();
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

			boolean current = OpenmrsUtil.compare(startDate, now) <= 0 && (endDate == null || OpenmrsUtil.compare(endDate, now) > 0);

			ret.add(SimpleObject.create(
				"startDate", kenyaUi.formatDate(startDate),
				"endDate", kenyaUi.formatDate(endDate),
				"regimen", simpleRegimen(regimen, ui),
				"changeReasons", changeReasons,
				"current", current
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
	public List<SimpleObject> simpleRegimenDefinitions(Collection<RegimenDefinition> definitions, UiUtils ui) {
		return SimpleObject.fromCollection(definitions, ui,
				"name", "group.code", "components.drugRef", "components.dose", "components.units", "components.frequency"
		);
	}
}