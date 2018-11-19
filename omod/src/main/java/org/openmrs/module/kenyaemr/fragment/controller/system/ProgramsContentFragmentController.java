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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.openmrs.Program;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyacore.program.ProgramDescriptor;
import org.openmrs.module.kenyacore.program.ProgramManager;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Controller for displaying all program content
 */
public class ProgramsContentFragmentController {

	public void controller(FragmentModel model, @SpringBean ProgramManager programManager) {
		List<SimpleObject> programs = new ArrayList<SimpleObject>();
		for (ProgramDescriptor descriptor : programManager.getAllProgramDescriptors()) {
			Program program = descriptor.getTarget();
			Collection<String> visitForms;

			if (descriptor.getVisitForms() != null) {
				visitForms = CollectionUtils.collect(descriptor.getVisitForms(), new Transformer() {
					@Override
					public Object transform(Object o) {
						return ((FormDescriptor) o).getTarget().getName();
					}
				});
			} else {
				visitForms = Collections.emptyList();
			}

			programs.add(SimpleObject.create(
					"name", program.getName(),
					"enrollmentForm", descriptor.getDefaultEnrollmentForm().getTarget().getName(),
					"visitForms", visitForms,
					"completionForm", descriptor.getDefaultCompletionForm().getTarget().getName()
			));
		}

		model.addAttribute("programs", programs);
	}
}