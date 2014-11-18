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

package org.openmrs.module.kenyaemr.converter.simplifier;

import org.openmrs.Form;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyacore.form.FormManager;
import org.openmrs.module.kenyaui.simplifier.AbstractSimplifier;
import org.openmrs.ui.framework.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Converts a form to a simple object
 */
@Component
public class FormSimplifier extends AbstractSimplifier<Form> {

	@Autowired
	private FormManager formManager;

	/**
	 * @see AbstractSimplifier#simplify(Object)
	 */
	@Override
	protected SimpleObject simplify(Form form) {
		SimpleObject ret = new SimpleObject();
		ret.put("id", form.getId());
		ret.put("name", form.getName());
		ret.put("uuid", form.getUuid());
		ret.put("formUuid", form.getUuid()); // For backwards compatibility

		FormDescriptor descriptor = formManager.getFormDescriptor(form);
		if (descriptor != null) {
			ret.put("iconProvider", descriptor.getIcon().getProvider());
			ret.put("icon", descriptor.getIcon().getPath());
		}

		return ret;
	}
}