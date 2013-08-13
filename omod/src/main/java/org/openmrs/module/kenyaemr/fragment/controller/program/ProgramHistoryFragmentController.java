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

package org.openmrs.module.kenyaemr.fragment.controller.program;

import java.util.*;

import org.openmrs.*;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.CoreContext;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyautil.MetadataUtils;
import org.openmrs.module.kenyacore.program.ProgramDescriptor;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

/**
 * Patient program history fragment
 */
public class ProgramHistoryFragmentController {

	public void controller(FragmentModel model,
						   @FragmentParam("patient") Patient patient,
						   @FragmentParam("program") Program program,
						   @FragmentParam("showClinicalData") boolean showClinicalData,
						   UiUtils ui,
						   @SpringBean CoreContext emr) {

		ProgramDescriptor descriptor = emr.getProgramManager().getProgramDescriptor(program);
		boolean patientIsEligible = emr.getProgramManager().isPatientEligibleFor(patient, program);

		PatientProgram currentEnrollment = null;

		// Gather all program enrollments for this patient and program
		List<PatientProgram> enrollments = emr.getProgramManager().getPatientEnrollments(patient, program);
		for (PatientProgram enrollment : enrollments) {
			if (enrollment.getActive()) {
				currentEnrollment = enrollment;
			}
		}

		// Per-patient forms need simplified and sorted if there are any
		List<SimpleObject> patientForms = new ArrayList<SimpleObject>();

		if (descriptor.getPatientForms() != null) {
			Set<FormDescriptor> sortedPatientForms = new TreeSet<FormDescriptor>();
			sortedPatientForms.addAll(descriptor.getPatientForms());

			for (FormDescriptor form : sortedPatientForms) {
				patientForms.add(ui.simplifyObject(form.getTarget()));
			}
		}

		model.addAttribute("patient", patient);
		model.addAttribute("program", program);
		model.addAttribute("defaultEnrollmentForm", descriptor.getDefaultEnrollmentForm());
		model.addAttribute("defaultCompletionForm", descriptor.getDefaultCompletionForm());
		model.addAttribute("patientForms", patientForms);
		model.addAttribute("showClinicalData", showClinicalData);
		model.addAttribute("patientIsEligible", patientIsEligible);
		model.addAttribute("currentEnrollment", currentEnrollment);
		model.addAttribute("enrollments", enrollments);
	}
}