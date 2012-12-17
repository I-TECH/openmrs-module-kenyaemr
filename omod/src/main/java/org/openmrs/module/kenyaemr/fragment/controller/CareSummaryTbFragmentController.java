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

import org.openmrs.Patient;
import org.openmrs.calculation.InvalidCalculationException;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.calculation.KenyaEmrCalculationProvider;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for TB care summary
 */
public class CareSummaryTbFragmentController {

	public void controller(@FragmentParam("patient") Patient patient,
						   FragmentModel model,
						   @SpringBean("org.openmrs.module.kenyaemr.calculation.KenyaEmrCalculationProvider") KenyaEmrCalculationProvider calculationProvider)
			throws InvalidCalculationException {

		Map<String, CalculationResult> calculationResults = new HashMap<String, CalculationResult>();

		//calculationResults.put("lastWHOStage", CalculationUtils.evaluateForPatient(calculationProvider, "lastWHOStage", null, patient.getPatientId()));
		//calculationResults.put("lastCD4Count", CalculationUtils.evaluateForPatient(calculationProvider, "lastCD4Count", null, patient.getPatientId()));
		//calculationResults.put("lastCD4Percent", CalculationUtils.evaluateForPatient(calculationProvider, "lastCD4Percent", null, patient.getPatientId()));

		model.addAttribute("calculations", calculationResults);
	}
}