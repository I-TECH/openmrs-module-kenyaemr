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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.openmrs.module.kenyaemr.KenyaEmrConstants;
import org.openmrs.module.kenyaui.KenyaUiConstants;
import org.openmrs.util.OpenmrsUtil;

import java.util.Set;

/**
 * Describes how a form can be used in the EMR
 */
public class FormDescriptor implements Comparable<FormDescriptor> {

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

	private Frequency frequency = Frequency.UNLIMITED;

	private Set<String> apps;

	private String programUuid;

	private Gender gender = Gender.BOTH;

	private String iconProvider = KenyaUiConstants.MODULE_ID;

	private String icon = "forms/generic.png";

	private String resourceProvider = KenyaEmrConstants.MODULE_ID;

	private String resource;

	private Integer order;

	/**
	 * Default constructor
	 */
	public FormDescriptor() {
	}

	/**
	 * Gets the form UUID
	 * @return the form UUID
	 */
	public String getFormUuid() {
		return formUuid;
	}

	/**
	 * Sets the form UUID
	 * @param formUuid the form UUID
	 */
	public void setFormUuid(String formUuid) {
		this.formUuid = formUuid;
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
	 * Gets the program UUID
	 * @return the program UUID
	 */
	public String getProgramUuid() {
		return programUuid;
	}

	/**
	 * Set the program UUID
	 * @param programUuid the program UUID
	 */
	public void setProgramUuid(String programUuid) {
		this.programUuid = programUuid;
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

	/**
	 * Gets the order
	 * @return the order
	 */
	public Integer getOrder() {
		return order;
	}

	/**
	 * Sets the order
	 * @param order the order
	 */
	public void setOrder(Integer order) {
		this.order = order;
	}

	/**
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(FormDescriptor form) {
		return OpenmrsUtil.compareWithNullAsGreatest(order, form.order);
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("formUuid", formUuid).toString();
	}
}