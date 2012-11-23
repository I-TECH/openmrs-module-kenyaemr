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
import org.openmrs.module.kenyaemr.calculation.BaseKenyaEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.KenyaEmrCalculationProvider;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 */
public class ClinicalAlertsFragmentController {
	
	public void controller() {
		// do nothing, this will load via ajax
	}
	
	public List<SimpleObject> getAlerts(@RequestParam("patientId") Integer ptId,
	                                    UiUtils ui,
	                                    @SpringBean("org.openmrs.module.kenyaemr.calculation.KenyaEmrCalculationProvider") KenyaEmrCalculationProvider calcs) {
		List<BaseKenyaEmrCalculation> alerts = new ArrayList<BaseKenyaEmrCalculation>();
		for (BaseKenyaEmrCalculation calc : calcs.getAllCalculations()) {
			CalculationResult result = Context.getService(PatientCalculationService.class).evaluate(ptId, calc);
			if ((Boolean) result.getValue()) {
				alerts.add(calc);
			}
		}
		return SimpleObject.fromCollection(alerts, ui, "shortMessage", "detailedMessage");
		
	}
	
}
