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

package org.openmrs.module.kenyaemr.page.controller.registration;

import org.openmrs.Relationship;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaui.annotation.SharedPage;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for relationship edit page
 */
@SharedPage({EmrConstants.APP_REGISTRATION, EmrConstants.APP_INTAKE, EmrConstants.APP_CLINICIAN})
public class EditRelationshipPageController {

	public void controller(@RequestParam(value = "relationshipId", required = false) Relationship relationship,
						   @RequestParam("returnUrl") String returnUrl,
						   PageModel model) {

		model.addAttribute("relationship", relationship);
		model.addAttribute("returnUrl", returnUrl);
	}
}