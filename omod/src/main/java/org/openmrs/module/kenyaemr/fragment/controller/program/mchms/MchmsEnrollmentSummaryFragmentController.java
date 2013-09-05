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

package org.openmrs.module.kenyaemr.fragment.controller.program.mchms;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MCH program enrollment fragment
 */
public class MchmsEnrollmentSummaryFragmentController {

	public String controller(@FragmentParam("patientProgram") PatientProgram enrollment,
							 @FragmentParam(value = "encounter", required = false) Encounter encounter,
							 @FragmentParam("showClinicalData") boolean showClinicalData,
							 FragmentModel model) {

		Map<String, Object> dataPoints = new LinkedHashMap<String, Object>();
		dataPoints.put("Enrolled", enrollment.getDateEnrolled());

		Obs ancNoObs = EmrUtils.firstObsInProgram(enrollment, Dictionary.getConcept(Dictionary.ANTENATAL_CASE_NUMBER));
		if (ancNoObs != null) {
			dataPoints.put("ANC No", ancNoObs.getValueNumeric());
		}
		Obs lmpObs = EmrUtils.firstObsInProgram(enrollment, Dictionary.getConcept(Dictionary.LAST_MONTHLY_PERIOD));
		if (lmpObs != null) {
			dataPoints.put("LMP", lmpObs.getValueDate());
			dataPoints.put("EDD (LMP)", CalculationUtils.dateAddDays(lmpObs.getValueDate(), 280));
		}
		Obs eddUsoundObs = EmrUtils.firstObsInProgram(enrollment, Dictionary.getConcept(Dictionary.EXPECTED_DATE_OF_DELIVERY));
		if (eddUsoundObs != null) {
			dataPoints.put("EDD (Ultrasound)", eddUsoundObs.getValueDate());
		}
		Obs gravidaObs = EmrUtils.firstObsInProgram(enrollment, Dictionary.getConcept(Dictionary.GRAVIDA));
		if (gravidaObs != null) {
			dataPoints.put("Gravida", gravidaObs.getValueNumeric().intValue());
		}
		Obs parityTermObs = EmrUtils.firstObsInProgram(enrollment, Dictionary.getConcept(Dictionary.PARITY_TERM));
		Obs parityAbortionObs = EmrUtils.firstObsInProgram(enrollment, Dictionary.getConcept(Dictionary.PARITY_ABORTION));
		if (parityTermObs != null && parityAbortionObs != null) {
			dataPoints.put("Parity", parityTermObs.getValueNumeric().intValue() + " + " + parityAbortionObs.getValueNumeric().intValue());
		}
		model.put("dataPoints", dataPoints);
		return "view/dataPoints";
	}
}