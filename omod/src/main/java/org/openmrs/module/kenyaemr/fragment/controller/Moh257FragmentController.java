/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.CoreConstants;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.util.EncounterBasedRegimenUtils;
import org.openmrs.module.kenyaemr.wrapper.PatientWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * MOH257 fragment
 */
public class Moh257FragmentController {

	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");

	public void controller(@FragmentParam("patient")
						   Patient patient,
						   FragmentModel model,
						   UiUtils ui,
						   @SpringBean RegimenManager regimenManager) {

		// removing face page from list of available forms
		String[] page1FormUuids = {
				/*HivMetadata._Form.MOH_257_FACE_PAGE,
				HivMetadata._Form.MOH_257_ARV_THERAPY,*/
				HivMetadata._Form.FAMILY_HISTORY
		};

		List<SimpleObject> page1AvailableForms = new ArrayList<SimpleObject>();
		List<Encounter> page1Encounters = new ArrayList<Encounter>();

		PatientWrapper patientWrapper = new PatientWrapper(patient);

		for (String page1FormUuid : page1FormUuids) {
			Form page1Form = MetadataUtils.existing(Form.class, page1FormUuid);
			List<Encounter> formEncounters = patientWrapper.allEncounters(page1Form);

			if (formEncounters.size() == 0) {
				page1AvailableForms.add(ui.simplifyObject(page1Form));
			}
			else {
				page1Encounters.addAll(formEncounters);
			}
		}

		Form moh257VisitForm = MetadataUtils.existing(Form.class, HivMetadata._Form.MOH_257_VISIT_SUMMARY);
		List<Encounter> moh257VisitSummaryEncounters = patientWrapper.allEncounters(moh257VisitForm);
		Collections.reverse(moh257VisitSummaryEncounters);

		model.addAttribute("page1AvailableForms", page1AvailableForms);
		model.addAttribute("page1Encounters", page1Encounters);
		model.addAttribute("page2Form", moh257VisitForm);
		model.addAttribute("page2Encounters", moh257VisitSummaryEncounters);

		List<SimpleObject> arvHistory = EncounterBasedRegimenUtils.getRegimenHistoryFromObservations(patient, "ARV");
		model.put("arvHistory", arvHistory);
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		model.addAttribute("inHivProgram", Context.getProgramWorkflowService().getPatientPrograms(patient, hivProgram, null, null, null, null, true));
	}
	public List<SimpleObject> getRegimenHistoryFromObservations (Patient patient, String category) {

		FormService formService = Context.getFormService();
		EncounterService encounterService = Context.getEncounterService();
		String ARV_TREATMENT_PLAN_EVENT_CONCEPT = "1255AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		String TB_TREATMENT_PLAN_CONCEPT = "1268AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		List<SimpleObject> history = new ArrayList<SimpleObject>();
		String categoryConceptUuid = category.equals("ARV")? ARV_TREATMENT_PLAN_EVENT_CONCEPT : TB_TREATMENT_PLAN_CONCEPT;

		EncounterType et = encounterService.getEncounterTypeByUuid(CommonMetadata._EncounterType.DRUG_REGIMEN_EDITOR);
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
		return history;
	}

	public SimpleObject getLastRegimenFromObservations (Patient patient, String category) {

		FormService formService = Context.getFormService();
		EncounterService encounterService = Context.getEncounterService();
		String ARV_TREATMENT_PLAN_EVENT_CONCEPT = "1255AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		String TB_TREATMENT_PLAN_CONCEPT = "1268AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		List<SimpleObject> history = new ArrayList<SimpleObject>();
		String categoryConceptUuid = category.equals("ARV")? ARV_TREATMENT_PLAN_EVENT_CONCEPT : TB_TREATMENT_PLAN_CONCEPT;

		EncounterType et = encounterService.getEncounterTypeByUuid(CommonMetadata._EncounterType.DRUG_REGIMEN_EDITOR);
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