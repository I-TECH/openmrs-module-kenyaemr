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

package org.openmrs.module.kenyaemr.form.velocity;

import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.ui.framework.UiUtils;

/**
 * Velocity functions for adding UI functionality to HTML forms
 */
public class UiVelocityFunctions {

	private FormEntrySession session;

	private UiUtils ui;

	/**
	 * Constructs a new functions provider
	 * @param session the form entry session
	 * @param ui the UI utils
	 */
	public UiVelocityFunctions(FormEntrySession session, UiUtils ui) {
		this.session = session;
		this.ui = ui;
	}

	/**
	 * Gets the complete path of a UI resource
	 * @param provider the provider
	 * @param path the path
	 * @return the complete resource path
	 */
	public String resourceLink(String provider, String path) {
		return ui.resourceLink(provider, path);
	}
}