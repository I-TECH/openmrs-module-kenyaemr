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