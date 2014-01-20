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
import org.openmrs.Form;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyacore.form.FormManager;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Controller for displaying all form content
 */
public class FormsContentFragmentController {

	public void controller(FragmentModel model, @SpringBean FormManager formManager) {
		List<SimpleObject> forms = new ArrayList<SimpleObject>();
		for (FormDescriptor descriptor : formManager.getAllFormDescriptors()) {
			Form form = descriptor.getTarget();

			Collection<String> allowedApps = CollectionUtils.collect(descriptor.getApps(), new Transformer() {
				@Override
				public Object transform(Object o) {
					return ((AppDescriptor) o).getLabel();
				}
			});

			forms.add(SimpleObject.create("name", form.getName(), "encounterType", form.getEncounterType().getName(), "allowedApps", allowedApps));
		}

		model.addAttribute("forms", forms);
	}
}