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

package org.openmrs.module.kenyaemr.fragment.controller.admin;

import org.openmrs.Form;
import org.openmrs.module.kenyacore.CoreContext;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyacore.form.FormUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.resource.ResourceFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for displaying all form content
 */
public class FormsContentFragmentController {

	public void controller(FragmentModel model, @SpringBean CoreContext emr, @SpringBean ResourceFactory resourceFactory) {
		List<SimpleObject> forms = new ArrayList<SimpleObject>();
		for (FormDescriptor descriptor : emr.getFormManager().getAllFormDescriptors()) {
			Form form = descriptor.getTarget();
			boolean loaded = true;
			String error = null;
			try {
				FormUtils.getHtmlForm(form, resourceFactory);
			} catch (Exception ex) {
				loaded = false;
				error = "Unable to load XML";
			}
			forms.add(SimpleObject.create("name", form.getName(), "encounterType", form.getEncounterType().getName(), "loaded", loaded, "error", error));
		}

		model.addAttribute("forms", forms);
	}
}