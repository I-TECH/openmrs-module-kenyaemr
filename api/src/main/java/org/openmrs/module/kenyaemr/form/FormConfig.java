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

import org.openmrs.Program;

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

	public static final String DEFAULT_ICON_PROVIDER = "kenyaemr";

	public static final String DEFAULT_ICON = "buttons/form_enter.png";

	private String formUuid;

	private Frequency frequency;

	private Program forProgram;

	private Gender forGender;

	private String iconProvider;

	private String icon;

	/**
	 * Creates a new form configuration for both genders with the default icon
	 * @param formUuid the form UUID
	 * @param frequency the form usage frequency
	 */
	public FormConfig(String formUuid, Frequency frequency) {
		this.formUuid = formUuid;
		this.frequency = frequency;
		this.forProgram = null;
		this.forGender = Gender.BOTH;
		this.iconProvider = DEFAULT_ICON_PROVIDER;
		this.icon = DEFAULT_ICON;
	}

	/**
	 * Creates a new form configuration
	 * @param formUuid the form UUID
	 * @param frequency the form usage frequency
	 * @param forProgram the form program usage (may be null)
	 * @param forGender the gender usage
	 * @param iconProvider the icon provider id
	 * @param icon the icon file
	 */
	public FormConfig(String formUuid, Frequency frequency, Program forProgram, Gender forGender, String iconProvider, String icon) {
		this.formUuid = formUuid;
		this.frequency = frequency;
		this.forProgram = forProgram;
		this.forGender = forGender;
		this.iconProvider = iconProvider != null ? iconProvider : DEFAULT_ICON_PROVIDER;
		this.icon = icon != null ? icon : DEFAULT_ICON;
	}

	/**
	 * Gets the form UUID
	 * @return the formUuid
	 */
	public String getFormUuid() {
		return formUuid;
	}

	/**
	 * Sets the form UUID
	 * @param formUuid the formUuid
	 */
	public void setFormUuid(String formUuid) {
		this.formUuid = formUuid;
	}

	/**
	 * @return the frequency
	 */
	public Frequency getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency the frequency
	 */
	public void setFrequency(Frequency frequency) {
		this.frequency = frequency;
	}

	/**
	 * @return the forProgram
	 */
	public Program getForProgram() {
		return forProgram;
	}

	/**
	 * @param forProgram the forProgram to set
	 */
	public void setForProgram(Program forProgram) {
		this.forProgram = forProgram;
	}

	/**
	 * Gets the gender usage
	 * @return the gender usage
	 */
	public Gender getGender() {
		return forGender;
	}

	/**
	 * Sets the gender usage
	 * @param forGender the gender usage
	 */
	public void setGender(Gender forGender) {
		this.forGender = forGender;
	}

	/**
	 * @return the iconProvider
	 */
	public String getIconProvider() {
		return iconProvider;
	}

	/**
	 * @param iconProvider the iconProvider to set
	 */
	public void setIconProvider(String iconProvider) {
		this.iconProvider = iconProvider;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}
}
