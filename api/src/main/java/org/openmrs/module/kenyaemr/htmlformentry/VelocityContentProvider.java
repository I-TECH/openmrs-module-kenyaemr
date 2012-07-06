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
package org.openmrs.module.kenyaemr.htmlformentry;

import org.apache.velocity.VelocityContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.velocity.VelocityContextContentProvider;
import org.springframework.stereotype.Component;


/**
 * Adds:
 *   kenyaemr.obsToday(Integer conceptId)
 */
@Component("kenyaemr.velocityContentProvider")
public class VelocityContentProvider implements VelocityContextContentProvider {
	
	/**
	 * @see org.openmrs.module.htmlformentry.velocity.VelocityContextContentProvider#populateContext(org.apache.velocity.VelocityContext)
	 */
	@Override
	public void populateContext(FormEntrySession session, VelocityContext velocityContext) {
		velocityContext.put("kenyaemr", new KenyaEmrVelocityFunctions(session));
	}
	
}
