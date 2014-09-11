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