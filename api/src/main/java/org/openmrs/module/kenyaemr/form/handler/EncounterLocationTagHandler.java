/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.form.handler;

import org.openmrs.module.htmlformentry.BadFormDesignException;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionController;
import org.openmrs.module.htmlformentry.handler.SubstitutionTagHandler;
import org.openmrs.module.kenyaemr.form.element.EncounterLocationSubmissionElement;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Encounter location tag handler
 */
@Component
public class EncounterLocationTagHandler extends SubstitutionTagHandler {

	@Override
	protected String getSubstitution(FormEntrySession session, FormSubmissionController submissionController, Map<String, String> parameters) throws BadFormDesignException {
		EncounterLocationSubmissionElement element = new EncounterLocationSubmissionElement(session.getContext(), parameters);
		submissionController.addAction(element);
		return element.generateHtml(session.getContext());
	}
}