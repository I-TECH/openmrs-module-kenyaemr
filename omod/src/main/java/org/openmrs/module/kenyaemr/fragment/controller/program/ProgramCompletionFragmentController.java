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

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.PatientProgram;
import org.openmrs.module.kenyacore.program.ProgramDescriptor;
import org.openmrs.module.kenyacore.program.ProgramManager;
import org.openmrs.module.kenyaemr.EmrWebConstants;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

/**
 * Program completion fragment
 */
public class ProgramCompletionFragmentController {
	
	public void controller(@FragmentParam("patientProgram") PatientProgram enrollment,
						   @FragmentParam("showClinicalData") boolean showClinicalData,
						   @SpringBean ProgramManager programManager,
						   FragmentModel model) {

		ProgramDescriptor programDescriptor = programManager.getProgramDescriptor(enrollment.getProgram());
		Form defaultCompletionForm = programDescriptor.getDefaultCompletionForm().getTarget();

		// Might not be the default completion form, but should have the same encounter type
		Encounter encounter = EmrUtils.lastEncounterInProgram(enrollment, defaultCompletionForm.getEncounterType());

		model.put("summaryFragment", programDescriptor.getFragments().get(EmrWebConstants.PROGRAM_COMPLETION_SUMMARY_FRAGMENT));
		model.put("enrollment", enrollment);
		model.put("encounter", encounter);
		model.put("showClinicalData", showClinicalData);
	}
}