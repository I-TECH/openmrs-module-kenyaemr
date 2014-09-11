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