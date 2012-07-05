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

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.Visit;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;


/**
 *
 */
public class PatientOverallDetailsFragmentController {
	
	public void controller(@FragmentParam("patient") Patient patient,
	                       @FragmentParam("visit") Visit visit,
	                       @FragmentParam("activeVisits") List<Visit> activeVisits,
	                       FragmentModel model) {

		model.addAttribute("patient", patient);
		model.addAttribute("person", patient);
		
		PatientService ps = Context.getPatientService();
		model.addAttribute("clinicNumberIdType", ps.getPatientIdentifierTypeByUuid(MetadataConstants.PATIENT_CLINIC_NUMBER_UUID));
		model.addAttribute("hivNumberIdType", ps.getPatientIdentifierTypeByUuid(MetadataConstants.UNIQUE_PATIENT_NUMBER_UUID));
		
		model.addAttribute("MetadataConstants", new MetadataConstants());
		
		ProgramWorkflowService pws = Context.getProgramWorkflowService(); 
		Program hivProgram = pws.getPrograms("HIV Program").get(0);
		model.addAttribute("hivProgram", hivProgram);
	}
	
}
