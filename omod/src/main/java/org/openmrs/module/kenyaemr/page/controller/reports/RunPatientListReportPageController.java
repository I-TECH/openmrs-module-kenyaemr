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

package org.openmrs.module.kenyaemr.page.controller.reports;

import org.openmrs.module.kenyacore.report.ReportManager;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyacore.report.ReportBuilder;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Run patient list report page
 */
@AppPage(EmrConstants.APP_REPORTS)
public class RunPatientListReportPageController {

	public void controller(@RequestParam("builder") String builderClassname, PageModel model, @SpringBean ReportManager reportManager) {
		ReportBuilder builder = reportManager.getReportBuilder(builderClassname);
		model.addAttribute("builder", builder);
	}
}