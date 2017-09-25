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

package org.openmrs.module.kenyaemr.fragment.controller.program.hiv;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastCd4CountCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastCd4PercentageCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastWhoStageCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.*;
import org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for HIV care summary
 */
public class HivCarePanelFragmentController {

	public void controller(@FragmentParam("patient") Patient patient,
						   @FragmentParam("complete") Boolean complete,
						   FragmentModel model,
						   @SpringBean RegimenManager regimenManager) {

		Map<String, CalculationResult> calculationResults = new HashMap<String, CalculationResult>();

		if (complete != null && complete.booleanValue()) {
			calculationResults.put("initialArtRegimen", EmrCalculationUtils.evaluateForPatient(InitialArtRegimenCalculation.class, null, patient));
			calculationResults.put("initialArtStartDate", EmrCalculationUtils.evaluateForPatient(InitialArtStartDateCalculation.class, null, patient));
		}

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

		Concept medSet = regimenManager.getMasterSetConcept("ARV");
		RegimenChangeHistory history = RegimenChangeHistory.forPatient(patient, medSet);
		model.addAttribute("regimenHistory", history);

		model.addAttribute("graphingConcepts", Dictionary.getConcepts(Dictionary.WEIGHT_KG, Dictionary.CD4_COUNT, Dictionary.CD4_PERCENT, Dictionary.HIV_VIRAL_LOAD));
	}
}