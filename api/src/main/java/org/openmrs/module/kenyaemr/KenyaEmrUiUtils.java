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
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
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
	KenyaUiUtils kenyaUi;

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
		return drugRef.isConceptOnly() ? drugRef.getConcept().getPreferredName(MetadataConstants.LOCALE).getName() : drugRef.getDrug().getName();
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
			ConceptName cn = o.getConcept().getPreferredName(MetadataConstants.LOCALE);
			if (cn == null) {
				cn = o.getConcept().getName(MetadataConstants.LOCALE);
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

			ConceptName cn = o.getConcept().getShortNameInLocale(MetadataConstants.LOCALE);
			if (cn == null) {
				cn = o.getConcept().getName(MetadataConstants.LOCALE);
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
	public SimpleObject simpleLocation(Location location, LocationAttributeType mfcAttrType, UiUtils ui) {
		List<LocationAttribute> attrs = location.getActiveAttributes(mfcAttrType);
		String facilityCode = attrs.size() > 0 ? (String)attrs.get(0).getValue() : null;
		String display = location.getName() + " (" + (facilityCode != null ? facilityCode : "?") + ")";

		return SimpleObject.create("value", location.getLocationId(), "label", display);
	}

	/**
	 * Simplifies a form
	 * @param form the form
	 * @param ui the UI utils
	 * @return the simple object
	 */
	public SimpleObject simpleForm(Form form, UiUtils ui) {
		FormDescriptor config = KenyaEmr.getInstance().getFormManager().getFormDescriptor(form.getUuid());
		HtmlForm htmlForm = Context.getService(HtmlFormEntryService.class).getHtmlFormByForm(form);

		return SimpleObject.create(
				"formUuid", form.getUuid(),
				"htmlFormId", htmlForm.getId(),
				"label", htmlForm.getName(),
				"iconProvider", (config != null ? config.getIconProvider() : KenyaEmrConstants.MODULE_ID),
				"icon", (config != null ? config.getIcon() : "forms/generic.png"));
	}

	/**
	 * Simplifies a form
	 * @param config the form config
	 * @param ui the UI utils
	 * @return the simple object
	 */
	public SimpleObject simpleForm(FormDescriptor config, UiUtils ui) {
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
	public SimpleObject simpleVisit(Visit visit, UiUtils ui) {
		return SimpleObject.fromObject(visit, ui, "visitId", "visitType", "startDatetime", "stopDatetime");
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