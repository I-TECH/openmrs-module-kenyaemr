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
package org.openmrs.module.kenyaemr.form;

import java.util.Set;

/**
 * A form which can be displayed and entered
 */
public class FormConfig {

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

	private String formUuid;

	private Frequency frequency;

	private Set<String> forApps;

	private String forProgramUuid;

	private Gender forGender;

	private String iconProvider;

	private String icon;

	/**
	 * Creates a new form configuration
	 * @param formUuid the form UUID
	 * @param frequency the form usage frequency
	 * @param forProgramUuid the form program usage (may be null)
	 * @param forGender the gender usage
	 * @param iconProvider the icon provider id
	 * @param icon the icon file
	 */
	public FormConfig(String formUuid, Frequency frequency, Set<String> forApps, String forProgramUuid, Gender forGender, String iconProvider, String icon) {
		this.formUuid = formUuid;
		this.frequency = frequency;
		this.forApps = forApps;
		this.forProgramUuid = forProgramUuid;
		this.forGender = forGender;
		this.iconProvider = iconProvider;
		this.icon = icon;
	}

	/**
	 * Gets the form UUID
	 * @return the formUuid
	 */
	public String getFormUuid() {
		return formUuid;
	}

	/**
	 * @return the frequency
	 */
	public Frequency getFrequency() {
		return frequency;
	}

	/**
	 * @return the app ids
	 */
	public Set<String> getForApps() {
		return forApps;
	}

	/**
	 * @return the forProgram
	 */
	public String getForProgramUuid() {
		return forProgramUuid;
	}

	/**
	 * Gets the gender usage
	 * @return the gender usage
	 */
	public Gender getGender() {
		return forGender;
	}

	/**
	 * @return the iconProvider
	 */
	public String getIconProvider() {
		return iconProvider;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}
}