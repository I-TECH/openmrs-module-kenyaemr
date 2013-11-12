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

package org.openmrs.module.kenyaemr.page.controller.dialog;

import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaui.annotation.PublicPage;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.PrivilegeConstants;

/**
 * Controller for help dialog
 */
@PublicPage
public class HelpDialogPageController {

	public void controller(PageModel model) {

		try {
			Context.addProxyPrivilege(PrivilegeConstants.VIEW_GLOBAL_PROPERTIES);

			String facilityCode = Context.getService(KenyaEmrService.class).getDefaultLocationMflCode();
			String supportNumber = Context.getAdministrationService().getGlobalProperty(EmrConstants.GP_SUPPORT_PHONE_NUMBER, EmrConstants.DEFAULT_SUPPORT_PHONE_NUMBER);
			String supportEmail = Context.getAdministrationService().getGlobalProperty(EmrConstants.GP_SUPPORT_EMAIL_ADDRESS, EmrConstants.DEFAULT_SUPPORT_EMAIL_ADDRESS);

			model.put("facilityCode", facilityCode);
			model.put("supportNumber", supportNumber);
			model.put("supportEmail", supportEmail);
		}
		finally {
			Context.removeProxyPrivilege(PrivilegeConstants.VIEW_GLOBAL_PROPERTIES);
		}
	}
}