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

package org.openmrs.module.kenyaemr.datatype;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.SerializingCustomDatatype;
import org.springframework.stereotype.Component;

/**
 * Custom datatype for {@link org.openmrs.Form}.
 * (This should be moved to the OpenMRS core.)
 */
@Component
public class FormDatatype extends SerializingCustomDatatype<Form> {

	/**
	 * @see org.openmrs.customdatatype.SerializingCustomDatatype#deserialize(String)
	 */
	@Override
	public Form deserialize(String serializedValue) {
		if (StringUtils.isEmpty(serializedValue)) {
			return null;
		}

		return Context.getFormService().getForm(Integer.valueOf(serializedValue));
	}

	/**
	 * @see org.openmrs.customdatatype.SerializingCustomDatatype#serialize(Object)
	 */
	@Override
	public String serialize(Form typedValue) {
		if (typedValue == null) {
			return null;
		}

		return typedValue.getFormId().toString();
	}
}