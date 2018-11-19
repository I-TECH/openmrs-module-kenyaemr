/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller;

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
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
		newVisit.setVisitType(MetadataUtils.existing(VisitType.class, CommonMetadata._VisitType.OUTPATIENT));
		model.addAttribute("newCurrentVisit", newVisit);
	}
}