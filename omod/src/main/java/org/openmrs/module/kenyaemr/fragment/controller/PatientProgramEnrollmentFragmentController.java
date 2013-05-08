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

import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.util.KenyaEmrUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Patient program enrollment fragment
 */
public class PatientProgramEnrollmentFragmentController {
	
	public void controller(@FragmentParam("patientProgram") PatientProgram enrollment,
						   @FragmentParam("encounterType") EncounterType encounterType,
						   @FragmentParam("showClinicalData") boolean showClinicalData,
						   FragmentModel model) {

		Encounter encounter = KenyaEmrUtils.lastEncounterInProgram(enrollment, encounterType);

		Map<String, Object> dataPoints = new LinkedHashMap<String, Object>();

		dataPoints.put("Enrolled", enrollment.getDateEnrolled());

		if (enrollment.getProgram().getUuid().equals(MetadataConstants.HIV_PROGRAM_UUID)) {
			addHivDataPoints(dataPoints, enrollment, encounter, showClinicalData);
		}
		else if (enrollment.getProgram().getUuid().equals(MetadataConstants.TB_PROGRAM_UUID)) {
			addTbDataPoints(dataPoints, enrollment, encounter, showClinicalData);
		}

		model.put("encounter", encounter);
		model.put("dataPoints", dataPoints);
	}

	private void addHivDataPoints(Map<String, Object> dataPoints, PatientProgram enrollment, Encounter encounter, boolean showClinicalData) {
		if (encounter != null) {
			Obs o = KenyaEmrUtils.firstObsInEncounter(encounter, Dictionary.getConcept(Dictionary.METHOD_OF_ENROLLMENT));
			if (o != null) {
				dataPoints.put("Entry point", o.getValueCoded());
			}
		}

		if (showClinicalData) {
			Obs o = KenyaEmrUtils.firstObsInProgram(enrollment, Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE));
			if (o != null) {
				dataPoints.put("WHO stage", o.getValueCoded());
			}
		}
	}

	private void addTbDataPoints(Map<String, Object> dataPoints, PatientProgram enrollment, Encounter encounter, boolean showClinicalData) {
		Obs o = KenyaEmrUtils.firstObsInProgram(enrollment, Dictionary.getConcept(Dictionary.REFERRING_CLINIC_OR_HOSPITAL));
		if (o != null) {
			dataPoints.put("Referred from", o.getValueCoded());
		}
	}
}