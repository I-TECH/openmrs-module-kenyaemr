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

import org.openmrs.module.kenyaui.annotation.SharedPage;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for cohort dialog
 */
@SharedPage
public class ReportErrorDialogPageController {

	public void controller(@RequestParam("request") ReportRequest reportRequest,
						   PageModel model,
						   @SpringBean ReportService reportService) {

		String message = reportService.loadReportError(reportRequest);

		model.addAttribute("message", message);
	}
}