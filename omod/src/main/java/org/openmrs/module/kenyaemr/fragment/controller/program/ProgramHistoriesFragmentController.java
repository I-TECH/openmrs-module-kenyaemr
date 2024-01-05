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