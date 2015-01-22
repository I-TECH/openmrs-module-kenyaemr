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

package org.openmrs.module.kenyaemr.reporting.data.converter;

import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.data.converter.DataConverter;

public class BirthdateConverter implements DataConverter {
	@Override
	public Object convert(Object obj) {
		KenyaUiUtils kenyaUi = Context.getRegisteredComponents(KenyaUiUtils.class).get(0);
		 Birthdate birthdate = (Birthdate) obj;
		return kenyaUi.formatDate(birthdate.getBirthdate());
	}

	@Override
	public Class<?> getInputDataType() {
		return Birthdate.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}
