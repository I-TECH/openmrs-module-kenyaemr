/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller;

import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.wrapper.VisitWrapper;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.PrivilegeConstants;

/**
 * Visit summary fragment
 */
public class VisitSummaryFragmentController {
	
	public void controller(@FragmentParam("visit") Visit visit, FragmentModel model) {

		model.addAttribute("visit", visit);
		model.addAttribute("sourceForm", new VisitWrapper(visit).getSourceForm());
		model.addAttribute("allowVoid", Context.hasPrivilege(PrivilegeConstants.DELETE_VISITS));
	}
}