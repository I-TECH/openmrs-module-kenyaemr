/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.program;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyacore.form.FormManager;
import org.openmrs.module.kenyacore.program.ProgramDescriptor;
import org.openmrs.module.kenyacore.program.ProgramManager;
import org.openmrs.module.kenyaemr.EmrActivator;
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
	protected static final Log log = LogFactory.getLog(EmrActivator.class);

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

		log.info("Program ==> "+program);
		log.info("Programs descriptor ==> "+descriptor);
		log.info("Programs patientIsEligible ==> "+patientIsEligible);
		log.info("Current app ==> "+currentApp);

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