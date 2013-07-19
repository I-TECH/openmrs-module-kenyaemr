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

package org.openmrs.module.kenyaemr.converter;

import org.openmrs.Form;
import org.openmrs.module.kenyaemr.form.FormDescriptor;
import org.openmrs.module.kenyaemr.form.FormManager;
import org.openmrs.ui.framework.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts a form to a simple object
 */
@Component
public class FormToSimpleObjectConverter implements Converter<Form, SimpleObject> {

	@Autowired
	private FormManager formManager;

	/**
	 * @see org.springframework.core.convert.converter.Converter#convert(Object)
	 */
	@Override
	public SimpleObject convert(Form form) {
		SimpleObject so = SimpleObject.create("formUuid", form.getUuid(), "name", form.getName());

		FormDescriptor descriptor = formManager.getFormDescriptor(form.getUuid());
		if (descriptor != null) {
			so.put("iconProvider", descriptor.getIconProvider());
			so.put("icon", descriptor.getIcon());
		}

		return so;
	}
}