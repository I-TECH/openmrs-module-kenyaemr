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
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;


/**
 * 
 */
public class ProgramSummaryFragmentController {
	
	public void controller(FragmentModel model,
	                       @FragmentParam("patient") Patient patient,
	                       @FragmentParam("program") Program program,
	                       @FragmentParam(required=false, value="registrationFormUuid") String regFormUuid,
	                       @FragmentParam(required=false, value="exitFormUuid") String exitFormUuid) {
		
		ProgramWorkflowService pws = Context.getProgramWorkflowService();
		PatientProgram currentEnrollment = null;
		List<PatientProgram> pastEnrollments = new ArrayList<PatientProgram>();
		for (PatientProgram pp : pws.getPatientPrograms(patient, program, null, null, null, null, false)) {
			if (pp.getActive()) {
				currentEnrollment = pp;
			} else {
				pastEnrollments.add(pp);
			}
		}
	
		model.addAttribute("patient", patient);
		model.addAttribute("program", program);
		model.addAttribute("registrationFormUuid", regFormUuid);
		model.addAttribute("exitFormUuid", exitFormUuid);
		model.addAttribute("currentEnrollment", currentEnrollment);
		model.addAttribute("pastEnrollments", pastEnrollments);
	}
	
}
