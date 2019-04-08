/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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