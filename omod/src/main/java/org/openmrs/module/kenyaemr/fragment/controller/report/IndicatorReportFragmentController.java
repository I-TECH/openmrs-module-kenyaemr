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

import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.report.IndicatorReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportManager;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Date;

/**
 * Monthly indicator report fragment
 */
public class IndicatorReportFragmentController {
	
	public void controller(@FragmentParam("report") IndicatorReportDescriptor report,
						   @RequestParam(required = false, value = "startDate") Date startDate,
						   FragmentModel model,
						   @SpringBean ReportManager reportManager) throws EvaluationException, IOException {

		ReportDefinition definition = reportManager.getReportDefinition(report);
		ReportData data = null;
		
		if (startDate != null) {
			// generate the report
			EvaluationContext ec = new EvaluationContext();
			ec.addParameterValue("startDate", startDate);
			ec.addParameterValue("endDate", DateUtil.getEndOfMonth(startDate));
			data = Context.getService(ReportDefinitionService.class).evaluate(definition, ec);
		}

		model.addAttribute("report", report);
		model.addAttribute("definition", definition);
		model.addAttribute("data", data);
	}
}