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

import org.openmrs.Form;
import org.openmrs.module.kenyacore.AbstractEntityDescriptor;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyacore.program.ProgramDescriptor;

import java.util.Set;

/**
 * Describes how a form can be used in the EMR. Each form should have a component of this type in the application
 * context.
 */
public class FormDescriptor extends AbstractEntityDescriptor<Form> {

	/**
	 * Possible usage frequencies for a form
	 */
	public enum Frequency {
		ONCE_EVER, PROGRAM, VISIT, UNLIMITED
	}

	/**
	 * Possible gender usages for a form
	 */
	public enum Gender {
		BOTH, MALE, FEMALE
	}

	private Frequency frequency = Frequency.UNLIMITED;

	private Set<String> apps;

	private ProgramDescriptor program;

	private Gender gender = Gender.BOTH;

	private String autoCreateVisitTypeUuid;

	private String iconProvider;

	private String icon;

	private String resourceProvider;

	private String resource;

	/**
	 * @see org.openmrs.module.kenyacore.AbstractEntityDescriptor#getTarget()
	 */
	@Override
	public Form getTarget() {
		return MetadataUtils.getForm(targetUuid);
	}

	/**
	 * Gets the frequency
	 * @return the frequency
	 */
	public Frequency getFrequency() {
		return frequency;
	}

	/**
	 * Sets the frequency
	 * @param frequency the frequency
	 */
	public void setFrequency(Frequency frequency) {
		this.frequency = frequency;
	}

	/**
	 * Gets the app ids
	 * @return the app ids
	 */
	public Set<String> getApps() {
		return apps;
	}

	/**
	 * Sets the app ids
	 * @param apps the app ids
	 */
	public void setApps(Set<String> apps) {
		this.apps = apps;
	}

	/**
	 * Gets the program
	 * @return the program
	 */
	public ProgramDescriptor getProgram() {
		return program;
	}

	/**
	 * Set the program
	 * @param program the program
	 */
	public void setProgram(ProgramDescriptor program) {
		this.program = program;
	}

	/**
	 * Gets the gender usage
	 * @return the gender usage
	 */
	public Gender getGender() {
		return gender;
	}

	/**
	 * Sets the gender usage
	 * @param gender the gender usage
	 */
	public void setGender(Gender gender) {
		this.gender = gender;
	}

	/**
	 * Sets the auto-create visit type UUID
	 * @return the visit type UUID
	 */
	public String getAutoCreateVisitTypeUuid() {
		return autoCreateVisitTypeUuid;
	}

	/**
	 * Gets the auto-create visit type UUID
	 * @param autoCreateVisitTypeUuid the visit type UUID
	 */
	public void setAutoCreateVisitTypeUuid(String autoCreateVisitTypeUuid) {
		this.autoCreateVisitTypeUuid = autoCreateVisitTypeUuid;
	}

	/**
	 * Gets the icon provider
	 * @return the iconProvider
	 */
	public String getIconProvider() {
		return iconProvider;
	}

	/**
	 * Sets the icon provider
	 * @param iconProvider the icon provider
	 */
	public void setIconProvider(String iconProvider) {
		this.iconProvider = iconProvider;
	}

	/**
	 * Gets the icon
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * Sets the icon
	 * @param icon the icon
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * Gets the resource provider
	 * @return the resource provider
	 */
	public String getResourceProvider() {
		return resourceProvider;
	}

	/**
	 * Sets the resource provider
	 * @param resourceProvider the resource provider
	 */
	public void setResourceProvider(String resourceProvider) {
		this.resourceProvider = resourceProvider;
	}

	/**
	 * Gets the resource
	 * @return the resource
	 */
	public String getResource() {
		return resource;
	}

	/**
	 * Sets the resource
	 * @param resource the resource
	 */
	public void setResource(String resource) {
		this.resource = resource;
	}
}