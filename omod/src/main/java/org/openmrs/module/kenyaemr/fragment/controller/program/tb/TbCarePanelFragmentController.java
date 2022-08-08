/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.program.tb;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.tb.TbDiseaseClassificationCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.TbPatientClassificationCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.TbTreatmentNumberCalculation;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.util.EncounterBasedRegimenUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for TB care summary
 */
public class TbCarePanelFragmentController {
	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");

	public void controller(@FragmentParam("patient") Patient patient,
						   @FragmentParam("complete") Boolean complete,
						   FragmentModel model,
						   @SpringBean RegimenManager regimenManager) {

		Map<String, Object> calculationResults = new HashMap<String, Object>();

		CalculationResult result = EmrCalculationUtils.evaluateForPatient(TbDiseaseClassificationCalculation.class, null, patient);
		calculationResults.put("tbDiseaseClassification", result != null ? result.getValue() : null);

		result = EmrCalculationUtils.evaluateForPatient(TbPatientClassificationCalculation.class, null, patient);
		calculationResults.put("tbPatientClassification", result != null ? result.getValue() : null);
		String message = "None";
		if(result != null){
			Obs obs  = (Obs) result.getValue();
			if(obs.getValueCoded().equals(Dictionary.getConcept(Dictionary.SMEAR_POSITIVE_NEW_TUBERCULOSIS_PATIENT))) {
				message = "New tuberculosis patient";
			}
			else {
				message = obs.getValueCoded().getName().getName();
			}
		}

		result = EmrCalculationUtils.evaluateForPatient(TbTreatmentNumberCalculation.class, null, patient);
		calculationResults.put("tbTreatmentNumber", result != null ? result.getValue() : null);

		model.addAttribute("calculations", calculationResults);
		model.addAttribute("result", message);

		List<SimpleObject> obshistory = EncounterBasedRegimenUtils.getRegimenHistoryFromObservations(patient, "TB");
		model.put("regimenFromObs", obshistory);
		Encounter lastEnc = EncounterBasedRegimenUtils.getLastEncounterForCategory(patient, "TB");
		SimpleObject lastEncDetails = null;
		if (lastEnc != null) {
			lastEncDetails = EncounterBasedRegimenUtils.buildRegimenChangeObject(lastEnc.getObs(), lastEnc);
		}
		model.put("lastEnc", lastEncDetails);
	}
}