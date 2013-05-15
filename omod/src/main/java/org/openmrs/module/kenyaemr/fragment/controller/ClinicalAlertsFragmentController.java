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
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

	protected static final Log log = LogFactory.getLog(ClinicalAlertsFragmentController.class);

	/**
	 * Do nothing, this will load via ajax
	 */
	public void controller() {
	}

	/**
	 * Gets the clinical alerts for the given patient. If any of the calculations throws an exception, this will return a single
	 * alert message with the name of the offending calculation
	 * @param patientId the patient id
	 * @param emr the Kenya EMR
	 * @return the alerts as simple objects
	 */
	public List<SimpleObject> getAlerts(@RequestParam("patientId") Integer patientId, @SpringBean KenyaEmr emr) {

		List<SimpleObject> alerts = new ArrayList<SimpleObject>();

		// Gather all alert calculations that evaluate to true
		for (BaseAlertCalculation calc : emr.getCalculationManager().getAlertCalculations()) {
			try {
				CalculationResult result = Context.getService(PatientCalculationService.class).evaluate(patientId, calc);
				if (result != null && (Boolean) result.getValue()) {
						alerts.add(SimpleObject.create("message", calc.getAlertMessage()));
				}
			}
			catch (Exception ex) {
				log.error("Error evaluating " + calc.getClass(), ex);
				return Collections.singletonList(SimpleObject.create("message", "ERROR EVALUATING '" + calc.getAlertMessage() + "'"));
			}
		}
		return alerts;
	}
}