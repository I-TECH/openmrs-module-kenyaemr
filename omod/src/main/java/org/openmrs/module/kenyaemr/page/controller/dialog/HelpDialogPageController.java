/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller.dialog;

import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaui.annotation.PublicPage;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for help dialog
 */
@PublicPage
public class HelpDialogPageController {

	public void controller(@RequestParam(value = "appId", required = false) String appId,
						   PageModel model) {
		try {
			Context.addProxyPrivilege(PrivilegeConstants.GET_GLOBAL_PROPERTIES);

			String facilityCode = Context.getService(KenyaEmrService.class).getDefaultLocationMflCode();
			String supportNumber = Context.getAdministrationService().getGlobalProperty(EmrConstants.GP_SUPPORT_PHONE_NUMBER, EmrConstants.DEFAULT_SUPPORT_PHONE_NUMBER);
			String supportEmail = Context.getAdministrationService().getGlobalProperty(EmrConstants.GP_SUPPORT_EMAIL_ADDRESS, EmrConstants.DEFAULT_SUPPORT_EMAIL_ADDRESS);
			String externalHelpUrl = Context.getAdministrationService().getGlobalProperty(EmrConstants.GP_EXTERNAL_HELP_URL, EmrConstants.DEFAULT_EXTERNAL_HELP_URL);

			model.put("appId", appId);
			model.put("facilityCode", facilityCode);
			model.put("supportNumber", supportNumber);
			model.put("supportEmail", supportEmail);
			model.put("externalHelpUrl", externalHelpUrl);
		}
		finally {
			Context.removeProxyPrivilege(PrivilegeConstants.GET_GLOBAL_PROPERTIES);
		}
	}
}