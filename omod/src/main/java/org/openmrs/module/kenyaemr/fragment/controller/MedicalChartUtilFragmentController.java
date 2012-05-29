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

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.session.Session;


/**
 * Helpful utility actions for the medical chart app
 */
public class MedicalChartUtilFragmentController {
	
	public List<SimpleObject> recentlyViewed(UiUtils ui,Session session) {
		List<Integer> recent = session.getAttribute("kenyaemr.medicalChart.recentlyViewedPatients", List.class);
		List<Patient> pats = new ArrayList<Patient>();
		for (Integer ptId : recent) {
			pats.add(Context.getPatientService().getPatient(ptId));
		}
		return simplePatientList(ui, pats);
	}
	
	private List<SimpleObject> simplePatientList(UiUtils ui, List<Patient> pts) {
    	return SimpleObject.fromCollection(pts, ui, "patientId", "personName", "age", "birthdate", "gender", "activeIdentifiers.identifierType", "activeIdentifiers.identifier");
    }
}
