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
import org.openmrs.module.kenyacore.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyacore.AbstractEntityDescriptor;

/**
 * Describes how a program can be used in the EMR. Each program should have a component of this type in the application
 * context.
 */
public class ProgramDescriptor extends AbstractEntityDescriptor<Program> {

	private Class<? extends BaseEmrCalculation> eligibilityCalculation;

	private FormDescriptor enrollmentForm;

	private FormDescriptor discontinuationForm;

	private String careSummaryFragment;

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
	 * Gets the enrollment form
	 * @return the enrollment form
	 */
	public FormDescriptor getEnrollmentForm() {
		return enrollmentForm;
	}

	/**
	 * Sets the enrollment form
	 * @param enrollmentForm the enrollment form
	 */
	public void setEnrollmentForm(FormDescriptor enrollmentForm) {
		this.enrollmentForm = enrollmentForm;
	}

	/**
	 * Gets the discontinuation form
	 * @return the discontinuation form
	 */
	public FormDescriptor getDiscontinuationForm() {
		return discontinuationForm;
	}

	/**
	 * Sets the discontinuation form
	 * @param discontinuationForm the discontinuation form
	 */
	public void setDiscontinuationForm(FormDescriptor discontinuationForm) {
		this.discontinuationForm = discontinuationForm;
	}

	/**
	 * Gets the care summary fragment (provider:fragment)
	 * @return the fragment
	 */
	public String getCareSummaryFragment() {
		return careSummaryFragment;
	}

	/**
	 * Sets the care summary fragment (provider:fragment)
	 * @param careSummaryFragment the care summary fragment
	 */
	public void setCareSummaryFragment(String careSummaryFragment) {
		this.careSummaryFragment = careSummaryFragment;
	}
}