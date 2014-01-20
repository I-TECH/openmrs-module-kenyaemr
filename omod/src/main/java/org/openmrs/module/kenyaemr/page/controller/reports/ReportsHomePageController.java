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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Program;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.kenyacore.program.ProgramDescriptor;
import org.openmrs.module.kenyacore.program.ProgramManager;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportManager;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;

/**
 * Homepage for the reports app
 */
@AppPage(EmrConstants.APP_REPORTS)
public class ReportsHomePageController {

	public void controller(PageModel model, UiUtils ui,
						   @SpringBean ReportManager reportManager,
						   @SpringBean ProgramManager programManager,
						   @SpringBean KenyaUiUtils kenyaUi,
						   PageRequest pageRequest) {

		AppDescriptor currentApp = kenyaUi.getCurrentApp(pageRequest);

		List<SimpleObject> commonReports = new ArrayList<SimpleObject>();

		for (ReportDescriptor report : reportManager.getCommonReports(currentApp)) {
			commonReports.add(ui.simplifyObject(report));
		}

		Map<String, SimpleObject[]> programReports = new LinkedHashMap<String, SimpleObject[]>();

		for (ProgramDescriptor programDescriptor : programManager.getAllProgramDescriptors()) {
			Program program = programDescriptor.getTarget();
			List<ReportDescriptor> reports = reportManager.getProgramReports(currentApp, program);

			if (reports.size() > 0) {
				programReports.put(program.getName(), ui.simplifyCollection(reports));
			}
		}

		model.addAttribute("commonReports", commonReports);
		model.addAttribute("programReports", programReports);
	}
}