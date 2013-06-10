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

package org.openmrs.module.kenyaemr.page.controller;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.kenyaemr.EmrWebConstants;
import org.openmrs.module.kenyaemr.KenyaEmr;
import org.openmrs.module.kenyaemr.reporting.builder.ReportBuilder;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

/**
 * Homepage for the reports app
 */
@AppPage(EmrWebConstants.APP_REPORTS)
public class ReportsHomePageController {

	public void controller(PageModel model, @SpringBean KenyaEmr emr) {
		model.addAttribute("mohReports", getReportDefinitionSummaries(emr, "moh"));
		model.addAttribute("facilityReports", getReportDefinitionSummaries(emr, "facility"));
	}

	/**
	 * Fetches all definition summaries for reports with a given tag
	 * @param tag the report tag
	 * @return the definition summaries
	 */
    private List<SimpleObject> getReportDefinitionSummaries(KenyaEmr emr, String tag) {
    	List<SimpleObject> ret = new ArrayList<SimpleObject>();
		for (ReportBuilder reportBuilder : emr.getReportManager().getReportBuildersByTag(tag)) {
			ret.add(SimpleObject.create("name", reportBuilder.getReportDefinitionSummary().getName(), "builder", reportBuilder.getClass().getName()));
		}
		return ret;
    }
}