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

package org.openmrs.module.kenyacore.form;

import org.openmrs.module.kenyacore.program.ProgramDescriptor;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Configuration for forms
 */
public class FormConfiguration {

	private List<FormDescriptor> generalPatientForms;

	private List<FormDescriptor> generalVisitForms;

	private Map<ProgramDescriptor, Set<FormDescriptor>> additionalProgramForms;

	public List<FormDescriptor> getGeneralPatientForms() {
		return generalPatientForms;
	}

	public void setGeneralPatientForms(List<FormDescriptor> generalPatientForms) {
		this.generalPatientForms = generalPatientForms;
	}

	public List<FormDescriptor> getGeneralVisitForms() {
		return generalVisitForms;
	}

	public void setGeneralVisitForms(List<FormDescriptor> generalVisitForms) {
		this.generalVisitForms = generalVisitForms;
	}

	public Map<ProgramDescriptor, Set<FormDescriptor>> getAdditionalProgramForms() {
		return additionalProgramForms;
	}

	public void setAdditionalProgramForms(Map<ProgramDescriptor, Set<FormDescriptor>> additionalProgramForms) {
		this.additionalProgramForms = additionalProgramForms;
	}
}