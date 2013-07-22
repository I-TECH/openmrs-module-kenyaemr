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
import org.openmrs.module.kenyacore.CoreContext;
import org.openmrs.module.kenyacore.calculation.BaseFlagCalculation;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for patient flags
 */
public class PatientFlagsFragmentController {

	protected static final Log log = LogFactory.getLog(PatientFlagsFragmentController.class);

	/**
	 * Do nothing, this will load via ajax
	 */
	public void controller() {
	}

	/**
	 * Gets the patient flags for the given patient. If any of the calculations throws an exception, this will return a single
	 * flag with a message with the name of the offending calculation
	 * @param patientId the patient id
	 * @param emr the KenyaEMR
	 * @return the flags as simple objects
	 */
	public List<SimpleObject> getFlags(@RequestParam("patientId") Integer patientId, @SpringBean CoreContext emr) {

		List<SimpleObject> alerts = new ArrayList<SimpleObject>();

		// Gather all flag calculations that evaluate to true
		for (BaseFlagCalculation calc : emr.getCalculationManager().getFlagCalculations()) {
			try {
				CalculationResult result = Context.getService(PatientCalculationService.class).evaluate(patientId, calc);
				if (result != null && (Boolean) result.getValue()) {
						alerts.add(SimpleObject.create("message", calc.getFlagMessage()));
				}
			}
			catch (Exception ex) {
				log.error("Error evaluating " + calc.getClass(), ex);
				return Collections.singletonList(SimpleObject.create("message", "ERROR EVALUATING '" + calc.getFlagMessage() + "'"));
			}
		}
		return alerts;
	}
}