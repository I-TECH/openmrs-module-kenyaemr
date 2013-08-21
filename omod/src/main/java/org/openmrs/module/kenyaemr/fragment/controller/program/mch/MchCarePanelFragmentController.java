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

package org.openmrs.module.kenyaemr.fragment.controller.program.mch;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.CoreContext;
import org.openmrs.module.kenyacore.regimen.RegimenChangeHistory;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for MCH care summary
 */
public class MchCarePanelFragmentController {

	public void controller(@FragmentParam("patient") Patient patient,
						   @FragmentParam("complete") Boolean complete,
						   FragmentModel model,
						   @SpringBean CoreContext emr) {
		Map<String, Object> calculations = new HashMap<String, Object>();

		EncounterService encounterService = Context.getEncounterService();
		EncounterType encounterType = encounterService.getEncounterTypeByUuid("3ee036d8-7c13-4393-b5d6-036f2fe45126");
		Encounter lastMchEncounter = EmrUtils.lastEncounter(patient, encounterType);
		Obs lmpObs = EmrUtils.firstObsInEncounter(lastMchEncounter, Dictionary.getConcept(Dictionary.LAST_MONTHLY_PERIOD));
		if (lmpObs != null) {
			int daysDiff = Days.daysBetween(new DateTime(lmpObs.getValueDate()), new DateTime(new Date())).getDays();
			calculations.put("gestation", Math.ceil(daysDiff / 7));
		}

		calculations.put("onPmtct", "TODO");
		calculations.put("onArv", "TODO");

		model.addAttribute("calculations", calculations);

		Concept medSet = emr.getRegimenManager().getMasterSetConcept("TB");
		RegimenChangeHistory history = RegimenChangeHistory.forPatient(patient, medSet);
		model.addAttribute("regimenHistory", history);
	}
}