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

package org.openmrs.module.kenyacore.program;

import org.openmrs.Program;
import org.openmrs.module.kenyacore.UIResource;
import org.openmrs.module.kenyacore.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyacore.AbstractEntityDescriptor;

import java.util.List;
import java.util.Map;

/**
 * Describes how a program can be used in the EMR. Each program should have a component of this type in the application
 * context.
 */
public class ProgramDescriptor extends AbstractEntityDescriptor<Program> {

	private Class<? extends BaseEmrCalculation> eligibilityCalculation;

	private FormDescriptor defaultEnrollmentForm;

	private FormDescriptor defaultCompletionForm;

	private List<FormDescriptor> visitForms;

	private Map<String, UIResource> fragments;

	/**
	 * @see org.openmrs.module.kenyacore.AbstractEntityDescriptor#getTarget()
	 */
	@Override
	public Program getTarget() {
		return MetadataUtils.getProgram(targetUuid);
	}

	/**
	 * Gets the eligibility calculation class
	 * @return the eligibility calculation class
	 */
	public Class<? extends BaseEmrCalculation> getEligibilityCalculation() {
		return eligibilityCalculation;
	}

	/**
	 * Sets the eligibility calculation class
	 * @param eligibilityCalculation the eligibility calculation class
	 */
	public void setEligibilityCalculation(Class<? extends BaseEmrCalculation> eligibilityCalculation) {
		this.eligibilityCalculation = eligibilityCalculation;
	}

	/**
	 * Gets the default enrollment form
	 * @return the form
	 */
	public FormDescriptor getDefaultEnrollmentForm() {
		return defaultEnrollmentForm;
	}

	/**
	 * Sets the default enrollment form
	 * @param defaultEnrollmentForm the form
	 */
	public void setDefaultEnrollmentForm(FormDescriptor defaultEnrollmentForm) {
		this.defaultEnrollmentForm = defaultEnrollmentForm;
	}

	/**
	 * Gets the default completion form
	 * @return the form
	 */
	public FormDescriptor getDefaultCompletionForm() {
		return defaultCompletionForm;
	}

	/**
	 * Sets the default completion form
	 * @param defaultCompletionForm the form
	 */
	public void setDefaultCompletionForm(FormDescriptor defaultCompletionForm) {
		this.defaultCompletionForm = defaultCompletionForm;
	}

	/**
	 * Gets the visit forms
	 * @return the visit forms
	 */
	public List<FormDescriptor> getVisitForms() {
		return visitForms;
	}

	/**
	 * Sets the visit forms
	 * @param visitForms the visit forms
	 */
	public void setVisitForms(List<FormDescriptor> visitForms) {
		this.visitForms = visitForms;
	}

	/**
	 * Gets the fragments
	 * @return the fragment resources
	 */
	public Map<String, UIResource> getFragments() {
		return fragments;
	}

	/**
	 * Sets the fragments
	 * @param fragments the fragment resources
	 */
	public void setFragments(Map<String, UIResource> fragments) {
		this.fragments = fragments;
	}
}