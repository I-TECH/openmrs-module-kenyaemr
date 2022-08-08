/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.program.hiv;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastCd4CountCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastCd4PercentageCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastWhoStageCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.ViralLoadAndLdlCalculation;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.util.EncounterBasedRegimenUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for HIV care summary
 */
public class HivCarePanelFragmentController {
	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");

	public void controller(@FragmentParam("patient") Patient patient,
						   @FragmentParam("complete") Boolean complete,
						   FragmentModel model,
						   @SpringBean RegimenManager regimenManager) {

		Map<String, CalculationResult> calculationResults = new HashMap<String, CalculationResult>();

		SimpleObject firstEncDetails = null;


		if (complete != null && complete.booleanValue()) {
			Encounter firstEnc = EncounterBasedRegimenUtils.getFirstEncounterForCategory(patient, "ARV");

			if (firstEnc != null) {
				firstEncDetails = EncounterBasedRegimenUtils.buildRegimenChangeObject(firstEnc.getObs(), firstEnc);

			}
		}
		model.put("firstEnc", firstEncDetails);

		calculationResults.put("lastWHOStage", EmrCalculationUtils.evaluateForPatient(LastWhoStageCalculation.class, null, patient));
		calculationResults.put("lastCD4Count", EmrCalculationUtils.evaluateForPatient(LastCd4CountCalculation.class, null, patient));
		calculationResults.put("lastCD4Percent", EmrCalculationUtils.evaluateForPatient(LastCd4PercentageCalculation.class, null, patient));
		CalculationResult lastViralLoad = EmrCalculationUtils.evaluateForPatient(ViralLoadAndLdlCalculation.class, null, patient);
		String valuesRequired = "None";
		Date datesRequired = null;
		if(!lastViralLoad.isEmpty()) {
			calculationResults.put("lastViralLoad", lastViralLoad);
		}

		model.addAttribute("calculations", calculationResults);

		if(!lastViralLoad.isEmpty()){
			String values = lastViralLoad.getValue().toString();
			//split by brace
			String value = values.replaceAll("\\{", "").replaceAll("\\}","");
			//split by equal sign
			if(!value.isEmpty()) {
				String[] splitByEqualSign = value.split("=");
				valuesRequired = splitByEqualSign[0];
				//for a date from a string
				String dateSplitedBySpace = splitByEqualSign[1].split(" ")[0].trim();
				String yearPart = dateSplitedBySpace.split("-")[0].trim();
				String monthPart = dateSplitedBySpace.split("-")[1].trim();
				String dayPart = dateSplitedBySpace.split("-")[2].trim();

				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, Integer.parseInt(yearPart));
				calendar.set(Calendar.MONTH, Integer.parseInt(monthPart) - 1);
				calendar.set(Calendar.DATE, Integer.parseInt(dayPart));

				datesRequired = calendar.getTime();
			}
		}

		// get default LDL value
		AdministrationService as = Context.getAdministrationService();
		Double ldl_default_value = Double.parseDouble(as.getGlobalProperty("kenyaemr.LDL_default_value"));

		model.addAttribute("ldl_default_value", ldl_default_value);
		model.addAttribute("value", valuesRequired);
		model.addAttribute("date", datesRequired);

		model.addAttribute("graphingConcepts", Dictionary.getConcepts(Dictionary.WEIGHT_KG, Dictionary.CD4_COUNT, Dictionary.CD4_PERCENT, Dictionary.HIV_VIRAL_LOAD));

		List<SimpleObject> obshistory = EncounterBasedRegimenUtils.getRegimenHistoryFromObservations(patient, "ARV");
		model.put("regimenFromObs", obshistory);
		Encounter lastEnc = EncounterBasedRegimenUtils.getLastEncounterForCategory(patient, "ARV");
		SimpleObject lastEncDetails = null;
		if (lastEnc != null) {
			lastEncDetails = EncounterBasedRegimenUtils.buildRegimenChangeObject(lastEnc.getObs(), lastEnc);
		}
		model.put("lastEnc", lastEncDetails);
	}
}