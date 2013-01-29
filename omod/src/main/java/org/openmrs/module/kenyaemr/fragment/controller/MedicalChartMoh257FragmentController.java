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
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.session.Session;

import java.util.Date;

/**
 *
 */
public class MedicalChartMoh257FragmentController {
	
	public void controller(@FragmentParam("patient") Patient patient, FragmentModel model, UiUtils ui, Session session) {

		Visit newVisit = new Visit();
		newVisit.setPatient(patient);
		newVisit.setStartDatetime(new Date());
		newVisit.setVisitType(Context.getVisitService().getVisitTypeByUuid(MetadataConstants.OUTPATIENT_VISIT_TYPE_UUID));
		model.addAttribute("newREVisit", newVisit);
	}
}