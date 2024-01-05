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
import org.openmrs.module.kenyacore.UiResource;
import org.openmrs.module.kenyacore.program.ProgramDescriptor;
import org.openmrs.module.kenyacore.program.ProgramManager;
import org.openmrs.module.kenyaemr.EmrWebConstants;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Program care summaries fragment
 */
public class ProgramCarePanelsFragmentController {

	public void controller(FragmentModel model,
						   @FragmentParam("patient") Patient patient,
						   @FragmentParam("complete") boolean complete,
						   @FragmentParam("activeOnly") boolean activeOnly,
						   @SpringBean ProgramManager programManager) {

		List<UiResource> carePanels = new ArrayList<UiResource>();

		Collection<ProgramDescriptor> programs = activeOnly
				? programManager.getPatientActivePrograms(patient)
				: programManager.getPatientPrograms(patient);

		for (ProgramDescriptor programDescriptor : programs) {
			carePanels.add(programDescriptor.getFragments().get(EmrWebConstants.PROGRAM_CARE_PANEL_FRAGMENT));
		}

		model.addAttribute("patient", patient);
		model.addAttribute("carePanels", carePanels);
		model.addAttribute("complete", complete);
	}
}