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

import java.util.*;

import org.openmrs.*;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

/**
 * Program history fragment
 */
public class ProgramHistoryFragmentController {

	public void controller(FragmentModel model,
						   @FragmentParam("patient") Patient patient,
						   @FragmentParam("program") Program program,
						   @FragmentParam("showClinicalData") boolean showClinicalData,
						   @FragmentParam("enrollmentForm") Form enrollmentForm,
						   @FragmentParam("discontinuationForm") Form discontinuationForm) {

		model.addAttribute("patient", patient);
		model.addAttribute("program", program);
		model.addAttribute("showClinicalData", showClinicalData);
		model.addAttribute("enrollmentForm", enrollmentForm);
		model.addAttribute("discontinuationForm", discontinuationForm);

		ProgramWorkflowService pws = Context.getProgramWorkflowService();
		PatientProgram currentEnrollment = null;

		// Gather all program enrollments for this patient and program
		List<PatientProgram> enrollments = new ArrayList<PatientProgram>();
		for (PatientProgram pp : pws.getPatientPrograms(patient, program, null, null, null, null, false)) {
			enrollments.add(pp);

			if (pp.getActive()) {
				currentEnrollment = pp;
			}
		}

		model.addAttribute("currentEnrollment", currentEnrollment);
		model.addAttribute("enrollments", enrollments);
	}
}