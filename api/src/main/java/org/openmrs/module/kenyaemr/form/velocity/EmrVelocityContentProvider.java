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

import org.apache.velocity.VelocityContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.velocity.VelocityContextContentProvider;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Registers module specific Velocity functions
 */
@Component
public class EmrVelocityContentProvider implements VelocityContextContentProvider {

	@Autowired
	private UiUtils ui;
	
	/**
	 * @see org.openmrs.module.htmlformentry.velocity.VelocityContextContentProvider#populateContext(org.openmrs.module.htmlformentry.FormEntrySession, org.apache.velocity.VelocityContext)
	 */
	@Override
	public void populateContext(FormEntrySession session, VelocityContext velocityContext) {

		velocityContext.put(EmrConstants.MODULE_ID, new EmrVelocityFunctions(session));
		velocityContext.put("ui", new UiVelocityFunctions(session, ui));
	}
}