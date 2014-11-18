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
import org.openmrs.module.kenyacore.program.ProgramDescriptor;
import org.openmrs.module.kenyacore.program.ProgramManager;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Program history fragment
 */
public class ProgramHistoriesFragmentController {

	public void controller(FragmentModel model,
						   @FragmentParam("patient") Patient patient,
						   @FragmentParam("showClinicalData") boolean showClinicalData,
						   @SpringBean ProgramManager programManager) {

		List<ProgramDescriptor> programs = new ArrayList<ProgramDescriptor>();

		if (!patient.isVoided()) {
			Collection<ProgramDescriptor> activePrograms = programManager.getPatientActivePrograms(patient);
			Collection<ProgramDescriptor> eligiblePrograms = programManager.getPatientEligiblePrograms(patient);

			// Display active programs on top
			programs.addAll(activePrograms);

			// Don't add duplicates for programs for which patient is both active and eligible
			for (ProgramDescriptor descriptor : eligiblePrograms) {
				if (!programs.contains(descriptor)) {
					programs.add(descriptor);
				}
			}
		}

		model.addAttribute("patient", patient);
		model.addAttribute("programs", programs);
		model.addAttribute("showClinicalData", showClinicalData);
	}
}