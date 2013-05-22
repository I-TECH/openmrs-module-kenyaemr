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

import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.KenyaEmrConstants;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.ui.framework.fragment.FragmentModel;

/**
 * Controller for help fragment
 */
public class HelpFragmentController {

	public void controller(FragmentModel model) {
		String facilityCode = Context.getService(KenyaEmrService.class).getDefaultLocationMflCode();
		String supportNumber = Context.getAdministrationService().getGlobalProperty(KenyaEmrConstants.GP_SUPPORT_PHONE_NUMBER, KenyaEmrConstants.DEFAULT_SUPPORT_PHONE_NUMBER);
		String supportEmail = Context.getAdministrationService().getGlobalProperty(KenyaEmrConstants.GP_SUPPORT_EMAIL_ADDRESS, KenyaEmrConstants.DEFAULT_SUPPORT_EMAIL_ADDRESS);

		model.put("facilityCode", facilityCode);
		model.put("supportNumber", supportNumber);
		model.put("supportEmail", supportEmail);
	}
}