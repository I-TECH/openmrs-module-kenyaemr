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
package org.openmrs.module.kenyaemr.page.controller;

import java.util.*;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.KenyaEmrUiUtils;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.regimen.RegimenDefinition;
import org.openmrs.module.kenyaemr.regimen.RegimenHistory;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for ARV regimen edit screen
 */
public class RegimenEditorPageController {

	public void controller(@RequestParam("patientId") Patient patient,
	                       @RequestParam("returnUrl") String returnUrl,
	                       UiUtils ui,
	                       PageModel model) {

		model.addAttribute("patient", patient);
		model.addAttribute("today", OpenmrsUtil.firstSecondOfDay(new Date()));
		model.addAttribute("returnUrl", returnUrl);

		Concept arvs = Context.getConceptService().getConceptByUuid(MetadataConstants.ANTIRETROVIRAL_DRUGS_CONCEPT_UUID);
		RegimenHistory history = RegimenHistory.forPatient(patient, arvs);
		model.addAttribute("history", history);

		Map<String, Integer> arvConcepts = RegimenManager.getDrugConcepts("ARV");
		List<Concept> arvList = new ArrayList<Concept>();
		for (Integer conceptId : arvConcepts.values()) {
			arvList.add(Context.getConceptService().getConcept(conceptId));
		}

		List<RegimenDefinition> regimenDefinitions = RegimenManager.getRegimenDefinitions("ARV");

		model.addAttribute("arvs", arvList);
		model.addAttribute("regimenDefinitions", regimenDefinitions);
		model.addAttribute("regimenDefinitionsJson", ui.toJson(KenyaEmrUiUtils.simpleRegimenDefinitions(regimenDefinitions, ui)));
	}
}