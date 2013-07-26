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
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyacore.CoreContext;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastWhoStageCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtRegimenCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastCd4CountCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastCd4PercentageCalculation;
import org.openmrs.module.kenyacore.regimen.RegimenChangeHistory;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for HIV care summary
 */
public class HivCarePanelFragmentController {

	public void controller(@FragmentParam("patient") Patient patient,
						   @FragmentParam("complete") Boolean complete,
						   FragmentModel model,
						   @SpringBean CoreContext emr) {

		Map<String, CalculationResult> calculationResults = new HashMap<String, CalculationResult>();

		if (complete != null && complete.booleanValue()) {
			calculationResults.put("initialArtRegimen", CalculationUtils.evaluateForPatient(InitialArtRegimenCalculation.class, null, patient));
			calculationResults.put("initialArtStartDate", CalculationUtils.evaluateForPatient(InitialArtStartDateCalculation.class, null, patient));
		}

		calculationResults.put("lastWHOStage", CalculationUtils.evaluateForPatient(LastWhoStageCalculation.class, null, patient));
		calculationResults.put("lastCD4Count", CalculationUtils.evaluateForPatient(LastCd4CountCalculation.class, null, patient));
		calculationResults.put("lastCD4Percent", CalculationUtils.evaluateForPatient(LastCd4PercentageCalculation.class, null, patient));

		model.addAttribute("calculations", calculationResults);

		Concept medSet = emr.getRegimenManager().getMasterSetConcept("ARV");
		RegimenChangeHistory history = RegimenChangeHistory.forPatient(patient, medSet);
		model.addAttribute("regimenHistory", history);
	}
}