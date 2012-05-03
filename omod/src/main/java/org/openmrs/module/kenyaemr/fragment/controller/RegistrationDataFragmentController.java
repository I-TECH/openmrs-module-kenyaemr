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
package org.openmrs.module.kenyaemr.fragment.controller;

import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.Validate;
import org.openmrs.validator.VisitValidator;


/**
 *
 */
public class RegistrationDataFragmentController {
	
	public Object startVisit(UiUtils ui,
	                         @BindParams("visit") @Validate(VisitValidator.class) Visit visit) {
		Visit saved = Context.getVisitService().saveVisit(visit);
		return SimpleObject.fromObject(saved, ui, "visitId", "visitType", "startDatetime", "stopDatetime");
	}
	
}
