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

package org.openmrs.module.kenyaemr.fragment.controller.program.tb;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.tb.TbDiseaseClassificationCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.TbPatientClassificationCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.TbTreatmentNumberCalculation;
import org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for TB care summary
 */
public class TbCarePanelFragmentController {

	public void controller(@FragmentParam("patient") Patient patient,
						   @FragmentParam("complete") Boolean complete,
						   FragmentModel model,
						   @SpringBean RegimenManager regimenManager) {

		Map<String, Object> calculationResults = new HashMap<String, Object>();

		CalculationResult result = EmrCalculationUtils.evaluateForPatient(TbDiseaseClassificationCalculation.class, null, patient);
		calculationResults.put("tbDiseaseClassification", result != null ? result.getValue() : null);

		result = EmrCalculationUtils.evaluateForPatient(TbPatientClassificationCalculation.class, null, patient);
		calculationResults.put("tbPatientClassification", result != null ? result.getValue() : null);

		result = EmrCalculationUtils.evaluateForPatient(TbTreatmentNumberCalculation.class, null, patient);
		calculationResults.put("tbTreatmentNumber", result != null ? result.getValue() : null);

		model.addAttribute("calculations", calculationResults);

		Concept medSet = regimenManager.getMasterSetConcept("TB");
		RegimenChangeHistory history = RegimenChangeHistory.forPatient(patient, medSet);
		model.addAttribute("regimenHistory", history);
	}
}