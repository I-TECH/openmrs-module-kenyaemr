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

package org.openmrs.module.kenyaemr.form.handler;

import org.openmrs.module.htmlformentry.BadFormDesignException;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionController;
import org.openmrs.module.htmlformentry.handler.SubstitutionTagHandler;
import org.openmrs.module.kenyaemr.form.element.RecentObsElement;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Last obs tag handler
 */
@Component
public class RecentObsTagHandler extends SubstitutionTagHandler {

	@Override
	protected String getSubstitution(FormEntrySession session, FormSubmissionController submissionController, Map<String, String> parameters) throws BadFormDesignException {
		RecentObsElement element = new RecentObsElement(session.getContext(), parameters);
		return element.generateHtml(session.getContext());
	}
}