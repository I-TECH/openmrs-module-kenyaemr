/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.hivTesting;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.PrivilegeConstants;

import java.text.SimpleDateFormat;

/**
 * mUzima error queue fragment
 */
public class MuzimaQueueFragmentController {

	public void controller(FragmentModel model) {

		Context.addProxyPrivilege(PrivilegeConstants.SQL_LEVEL_ACCESS);

		String regStr = "select count(*) from medic_error_data where discriminator='json-registration';";
		String allErrors = "select count(*) from medic_error_data;";
		String queueData = "select count(*) from medic_queue_data;";
		Long totalErrors = (Long) Context.getAdministrationService().executeSQL(allErrors, true).get(0).get(0);
		Long registrationErrors = (Long) Context.getAdministrationService().executeSQL(regStr, true).get(0).get(0);
		Long queueDataTotal = (Long) Context.getAdministrationService().executeSQL(queueData, true).get(0).get(0);


		model.put("totalErrors", totalErrors.intValue());
		model.put("registrationErrors", registrationErrors.intValue());
		model.put("queueData", queueDataTotal.intValue());
		Context.removeProxyPrivilege(PrivilegeConstants.SQL_LEVEL_ACCESS);

	}

}
