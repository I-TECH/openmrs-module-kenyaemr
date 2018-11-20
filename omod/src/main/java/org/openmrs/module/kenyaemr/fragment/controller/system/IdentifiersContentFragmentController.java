/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.system;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.kenyacore.identifier.IdentifierDescriptor;
import org.openmrs.module.kenyacore.identifier.IdentifierManager;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for displaying all identifier content
 */
public class IdentifiersContentFragmentController {

	public void controller(FragmentModel model, @SpringBean IdentifierManager identifierManager) {
		List<SimpleObject> identifiers = new ArrayList<SimpleObject>();
		for (IdentifierDescriptor descriptor : identifierManager.getAllIdentifierDescriptors()) {
			PatientIdentifierType pidType = descriptor.getTarget();

			identifiers.add(SimpleObject.create(
					"name", pidType.getName(),
					"format", pidType.getFormat(),
					"required", pidType.getRequired()
			));
		}

		model.addAttribute("identifiers", identifiers);
	}
}