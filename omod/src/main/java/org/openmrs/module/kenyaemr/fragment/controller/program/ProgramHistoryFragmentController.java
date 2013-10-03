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

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.module.appframework.AppDescriptor;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyacore.form.FormManager;
import org.openmrs.module.kenyacore.program.ProgramDescriptor;
import org.openmrs.module.kenyacore.program.ProgramManager;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Patient program history fragment
 */
public class ProgramHistoryFragmentController {

	public void controller(FragmentModel model,
						   @FragmentParam("patient") Patient patient,
						   @FragmentParam("program") Program program,
						   @FragmentParam("showClinicalData") boolean showClinicalData,
						   UiUtils ui,
						   PageRequest pageRequest,
						   @SpringBean ProgramManager programManager,
						   @SpringBean FormManager formManager,
						   @SpringBean KenyaUiUtils kenyaUi) {

		AppDescriptor currentApp = kenyaUi.getCurrentApp(pageRequest);

		ProgramDescriptor descriptor = programManager.getProgramDescriptor(program);
		boolean patientIsEligible = programManager.isPatientEligibleFor(patient, program);

		PatientProgram currentEnrollment = null;

		// Gather all program enrollments for this patient and program
		List<PatientProgram> enrollments = programManager.getPatientEnrollments(patient, program);
		for (PatientProgram enrollment : enrollments) {
			if (enrollment.getActive()) {
				currentEnrollment = enrollment;
			}
		}

		// Per-patient forms need simplified if there are any
		List<SimpleObject> patientForms = new ArrayList<SimpleObject>();
		if (descriptor.getPatientForms() != null) {
			for (FormDescriptor form : formManager.getProgramFormsForPatient(currentApp, program, patient)) {
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