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

import org.openmrs.module.kenyacore.CoreContext;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

/**
 * System header - used to display a system wide error message
 */
public class SystemHeaderFragmentController {
	
	public void controller(FragmentModel model, @SpringBean CoreContext emr) {
		String systemMessage = null;

		if (!emr.isRefreshed()) {
			systemMessage = "System did not properly load. Please inform a system administrator immediately.";
		}

		model.put("systemMessage", systemMessage);
	}
}