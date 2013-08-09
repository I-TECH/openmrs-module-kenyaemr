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
import org.openmrs.module.appframework.AppDescriptor;
import org.openmrs.module.kenyacore.AbstractEntityDescriptor;
import org.openmrs.module.kenyacore.UIResource;
import org.openmrs.module.kenyautil.MetadataUtils;

import java.util.Set;

/**
 * Describes how a form can be used in the EMR. Each form should have a component of this type in the application
 * context.
 */
public class FormDescriptor extends AbstractEntityDescriptor<Form> {

	/**
	 * Possible gender usages for a form
	 */
	public enum Gender {
		BOTH, MALE, FEMALE
	}

	private Set<AppDescriptor> apps;

	private Gender gender = Gender.BOTH;

	private String autoCreateVisitTypeUuid;

	private UIResource icon;

	private UIResource htmlform;

	/**
	 * @see org.openmrs.module.kenyacore.AbstractEntityDescriptor#getTarget()
	 */
	@Override
	public Form getTarget() {
		return MetadataUtils.getForm(targetUuid);
	}

	/**
	 * Gets the apps
	 * @return the apps descriptors
	 */
	public Set<AppDescriptor> getApps() {
		return apps;
	}

	/**
	 * Sets the apps
	 * @param apps the app descriptors
	 */
	public void setApps(Set<AppDescriptor> apps) {
		this.apps = apps;
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
	 * Gets the icon resource
	 * @return the icon
	 */
	public UIResource getIcon() {
		return icon;
	}

	/**
	 * Sets the icon resource
	 * @param icon the icon
	 */
	public void setIcon(UIResource icon) {
		this.icon = icon;
	}

	/**
	 * Gets the htmlform resource
	 * @return the htmlform
	 */
	public UIResource getHtmlform() {
		return htmlform;
	}

	/**
	 * Sets the htmlform resource
	 * @param htmlform the htmlform
	 */
	public void setHtmlform(UIResource htmlform) {
		this.htmlform = htmlform;
	}
}