/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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