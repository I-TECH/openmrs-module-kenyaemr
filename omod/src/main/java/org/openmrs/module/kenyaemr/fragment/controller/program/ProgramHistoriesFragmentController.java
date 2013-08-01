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
import org.openmrs.module.kenyacore.CoreContext;
import org.openmrs.module.kenyacore.program.ProgramDescriptor;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Program history fragment
 */
public class ProgramHistoriesFragmentController {

	public void controller(FragmentModel model,
						   @FragmentParam("patient") Patient patient,
						   @FragmentParam("showClinicalData") boolean showClinicalData,
						   @SpringBean CoreContext emr) {

		Collection<ProgramDescriptor> activePrograms = emr.getProgramManager().getPatientActivePrograms(patient);
		Collection<ProgramDescriptor> eligiblePrograms = emr.getProgramManager().getPatientEligiblePrograms(patient);
		List<ProgramDescriptor> programs = EmrUtils.merge(activePrograms, eligiblePrograms);

		model.addAttribute("patient", patient);
		model.addAttribute("programs", programs);
		model.addAttribute("showClinicalData", showClinicalData);
	}
}