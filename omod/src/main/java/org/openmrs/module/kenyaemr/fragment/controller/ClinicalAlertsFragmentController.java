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
package org.openmrs.module.kenyaemr.fragment.controller;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.KenyaEmr;
import org.openmrs.module.kenyaemr.calculation.BaseAlertCalculation;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for requests for clinical alerts
 */
public class ClinicalAlertsFragmentController {
	
	public void controller() {
		// Do nothing, this will load via ajax
	}

	/**
	 * Gets the clinical alerts for the given patient
	 * @param ptId the patient id
	 * @param ui the UI utils
	 * @param emr the Kenya EMR
	 * @return the alerts as simple objects
	 */
	public List<SimpleObject> getAlerts(@RequestParam("patientId") Integer ptId,  UiUtils ui, @SpringBean KenyaEmr emr) {

		List<SimpleObject> alerts = new ArrayList<SimpleObject>();

		// Gather all alert calculations that evaluate to true
		for (BaseAlertCalculation calc : emr.getCalculationManager().getAlertCalculations()) {
			CalculationResult result = Context.getService(PatientCalculationService.class).evaluate(ptId, calc);
			if (result != null && (Boolean) result.getValue()) {
				alerts.add(SimpleObject.create("message", calc.getAlertMessage()));
			}
		}

		return alerts;
	}
}