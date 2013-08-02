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

import org.openmrs.module.kenyacore.AbstractContentConfiguration;
import org.openmrs.module.kenyacore.program.ProgramDescriptor;

import java.util.Map;
import java.util.Set;

/**
 * Configuration for forms
 */
public class FormConfiguration extends AbstractContentConfiguration {

	private Set<FormDescriptor> generalPatientForms;

	private Set<FormDescriptor> generalVisitForms;

	private Map<ProgramDescriptor, Set<FormDescriptor>> programVisitForms;

	/**
	 * Gets the general per-patient forms
	 * @return the form descriptors
	 */
	public Set<FormDescriptor> getGeneralPatientForms() {
		return generalPatientForms;
	}

	/**
	 * Sets the general per-patient forms
	 * @param generalPatientForms the form descriptors
	 */
	public void setGeneralPatientForms(Set<FormDescriptor> generalPatientForms) {
		this.generalPatientForms = generalPatientForms;
	}

	/**
	 * Gets the general pre-visit forms
	 * @return the form descriptors
	 */
	public Set<FormDescriptor> getGeneralVisitForms() {
		return generalVisitForms;
	}

	/**
	 * Sets the general per-visit forms
	 * @param generalVisitForms the form descriptors
	 */
	public void setGeneralVisitForms(Set<FormDescriptor> generalVisitForms) {
		this.generalVisitForms = generalVisitForms;
	}

	/**
	 * Gets the program specific per-visit forms
	 * @return the map of program and form descriptors
	 */
	public Map<ProgramDescriptor, Set<FormDescriptor>> getProgramVisitForms() {
		return programVisitForms;
	}

	/**
	 * Sets the program specific per-visit forms
	 * @param programVisitForms the map of program and form descriptors
	 */
	public void setProgramVisitForms(Map<ProgramDescriptor, Set<FormDescriptor>> programVisitForms) {
		this.programVisitForms = programVisitForms;
	}
}