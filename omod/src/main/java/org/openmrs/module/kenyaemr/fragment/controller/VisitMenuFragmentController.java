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
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.Date;

/**
 * Visit menu (check-in / check-out etc)
 */
public class VisitMenuFragmentController {
	
	public void controller(FragmentModel model, @FragmentParam("patient") Patient patient, @FragmentParam(value = "visit", required = false) Visit visit) {

		model.addAttribute("patient", patient);
		model.addAttribute("visit", visit);

		Visit newVisit = new Visit();
		newVisit.setPatient(patient);
		newVisit.setStartDatetime(new Date());
		newVisit.setVisitType(MetadataUtils.getVisitType(Metadata.VisitType.OUTPATIENT));
		model.addAttribute("newCurrentVisit", newVisit);
	}
}