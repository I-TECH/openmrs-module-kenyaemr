/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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