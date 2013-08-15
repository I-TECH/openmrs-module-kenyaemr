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

package org.openmrs.module.kenyaemr.fragment.controller.program.hei;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HEI program discontinuation summary fragment
 */

public class HeiCompletionSummaryFragmentController {

	public String controller(@FragmentParam("patientProgram") PatientProgram enrollment,
							 @FragmentParam(value = "encounter", required = false) Encounter encounter,
							 @FragmentParam("showClinicalData") boolean showClinicalData,
							 FragmentModel model) {

		Map<String, Object> dataPoints = new LinkedHashMap<String, Object>();

		dataPoints.put("Completed", enrollment.getDateCompleted());

		if (showClinicalData && enrollment.getOutcome() != null) {
			dataPoints.put("Outcome", enrollment.getOutcome());
		}

		if (encounter != null) {
			Obs reasonObs = EmrUtils.firstObsInEncounter(encounter, Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION));
			if (reasonObs != null) {
				dataPoints.put("Reason", reasonObs.getValueCoded());
			}
		}

		model.put("dataPoints", dataPoints);
		return "view/dataPoints";
	}
}