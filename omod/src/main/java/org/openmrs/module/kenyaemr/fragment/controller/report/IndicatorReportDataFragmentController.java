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

package org.openmrs.module.kenyaemr.fragment.controller.report;

import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

/**
 * Monthly indicator report fragment
 */
public class IndicatorReportDataFragmentController {
	
	public void controller(@FragmentParam("reportRequest") ReportRequest reportRequest,
						   @FragmentParam("reportData") ReportData reportData,
						   FragmentModel model) {

		model.addAttribute("reportRequest", reportRequest);
		model.addAttribute("reportData", reportData);
		model.addAttribute("definition", reportData.getDefinition());
	}
}