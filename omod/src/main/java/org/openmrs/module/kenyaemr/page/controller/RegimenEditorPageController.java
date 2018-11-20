/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Role;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.CoreConstants;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.EmrWebConstants;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.regimen.RegimenChange;
import org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaui.annotation.SharedPage;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Controller for regimen editor page
 */
@SharedPage({EmrConstants.APP_CLINICIAN, EmrConstants.APP_CHART})
public class RegimenEditorPageController {

	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");
	public void controller(@RequestParam("category") String category,
						   @RequestParam("returnUrl") String returnUrl,
						   PageModel model,
						   @SpringBean RegimenManager regimenManager) {

		Patient patient = (Patient) model.getAttribute(EmrWebConstants.MODEL_ATTR_CURRENT_PATIENT);

		model.addAttribute("category", category);
		model.addAttribute("returnUrl", returnUrl);

		Concept masterSet = regimenManager.getMasterSetConcept(category);
		RegimenChangeHistory history = RegimenChangeHistory.forPatient(patient, masterSet);
		model.addAttribute("history", history);

		RegimenChange lastChange = history.getLastChange();
		Date lastChangeDate =  (lastChange != null) ? lastChange.getDate() : null;
		Date now = new Date();
		boolean futureChanges = OpenmrsUtil.compareWithNullAsEarliest(lastChangeDate, now) >= 0;

		model.addAttribute("initialDate", futureChanges ? lastChangeDate : now);

		try {
			boolean isManager = false;
			for(Role role: Context.getAllRoles(Context.getAuthenticatedUser())) {
				if(role.getName().equals("Manager") || role.getName().equals("System Developer")) {
					isManager = true;
					break;
				}
			}
			model.addAttribute("isManager", isManager);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<SimpleObject> obshistory = getRegimenHistoryFromObservations(patient, category);
		model.put("regimenFromObs", obshistory);
	}

	public List<SimpleObject> getRegimenHistoryFromObservations (Patient patient, String category) {

		FormService formService = Context.getFormService();
		EncounterService encounterService = Context.getEncounterService();
		String ARV_TREATMENT_PLAN_EVENT_CONCEPT = "1255AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		String TB_TREATMENT_PLAN_CONCEPT = "1268AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		List<SimpleObject> history = new ArrayList<SimpleObject>();
		String categoryConceptUuid = category.equals("ARV")? ARV_TREATMENT_PLAN_EVENT_CONCEPT : TB_TREATMENT_PLAN_CONCEPT;

		EncounterType et = encounterService.getEncounterTypeByUuid(CommonMetadata._EncounterType.CONSULTATION);
		Form form = formService.getFormByUuid(CommonMetadata._Form.DRUG_REGIMEN_EDITOR);

		List<Encounter> regimenChangeHistory = EmrUtils.AllEncounters(patient, et, form);
		if (regimenChangeHistory != null && regimenChangeHistory.size() > 0) {
			for (Encounter e : regimenChangeHistory) {
				Set<Obs> obs = e.getObs();
				if (programEncounterMatching(obs, categoryConceptUuid)) {
					SimpleObject object = buildRegimenChangeObject(obs, e);
					if (object != null)
						history.add(object);
				}
			}
			return history;
		}
		return null;
	}

	public SimpleObject getLastRegimenFromObservations (Patient patient, String category) {

		FormService formService = Context.getFormService();
		EncounterService encounterService = Context.getEncounterService();
		String ARV_TREATMENT_PLAN_EVENT_CONCEPT = "1255AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		String TB_TREATMENT_PLAN_CONCEPT = "1268AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		List<SimpleObject> history = new ArrayList<SimpleObject>();
		String categoryConceptUuid = category.equals("ARV")? ARV_TREATMENT_PLAN_EVENT_CONCEPT : TB_TREATMENT_PLAN_CONCEPT;

		EncounterType et = encounterService.getEncounterTypeByUuid(CommonMetadata._EncounterType.CONSULTATION);
		Form form = formService.getFormByUuid(CommonMetadata._Form.DRUG_REGIMEN_EDITOR);

		Encounter e = EmrUtils.lastEncounter(patient, et, form);
		if (e != null) {
				Set<Obs> obs = e.getObs();
				if (programEncounterMatching(obs, categoryConceptUuid)) {
					SimpleObject object = buildRegimenChangeObject(obs, e);
					if (object != null)
						return object;
				}
		}
		return null;
	}


	private boolean programEncounterMatching(Set<Obs> obs, String conceptUuidToMatch) {
		for (Obs o : obs) {
			if (o.getConcept().getUuid().equals(conceptUuidToMatch)) {
				return true;
			}
		}
		return false;
	}

	private SimpleObject buildRegimenChangeObject(Set<Obs> obsList, Encounter e) {

		String CURRENT_DRUGS = "1193AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		String START_DRUGS = "1256AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		String STOP_DRUGS = "1260AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		String CHANGE_REGIMEN = "1259AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";


		String regimen = null;
		String regimenShort = null;
		String regimenUuid = null;
		String endDate = null;
		String startDate = e != null? DATE_FORMAT.format(e.getEncounterDatetime()) : "";
		String changeReason = null;


		for(Obs obs:obsList) {

			if (obs.getConcept().getUuid().equals(CURRENT_DRUGS) ) {
				regimen = obs.getValueCoded() != null ? obs.getValueCoded().getFullySpecifiedName(CoreConstants.LOCALE).getName() : "";
				regimenShort = obs.getValueCoded() != null && obs.getValueCoded().getShortNameInLocale(CoreConstants.LOCALE) != null ? obs.getValueCoded().getShortNameInLocale(CoreConstants.LOCALE).getName() : null;
				regimenUuid = obs.getValueCoded() != null ? obs.getValueCoded().getUuid() : "";
			}
		}
		if(regimen != null) {
			return SimpleObject.create(
					"startDate", startDate,
					"endDate", "",
					"regimenShortDisplay", regimenShort != null ? regimenShort : regimen,
					"regimenLongDisplay", regimen,
					"changeReasons", "",
					"regimenUuid", regimenUuid,
					"current",false

			);
		}
		return null;
	}
}