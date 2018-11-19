/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.header;

import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.ui.framework.fragment.FragmentModel;

/**
 * Controller for the app header, which includes user menu
 */
public class HeaderMenuFragmentController {

	public void controller(FragmentModel model) {
		model.put("externalHelpUrl", Context.getAdministrationService().getGlobalProperty(EmrConstants.GP_EXTERNAL_HELP_URL, EmrConstants.DEFAULT_EXTERNAL_HELP_URL));
	}
}