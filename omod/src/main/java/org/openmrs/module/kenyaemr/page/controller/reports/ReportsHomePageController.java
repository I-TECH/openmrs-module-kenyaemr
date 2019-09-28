/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller.reports;

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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

		Map<String, List<SimpleObject>> reportsByProgram = new LinkedHashMap<String, List<SimpleObject>>();

		List<SimpleObject> common = new ArrayList<SimpleObject>();
		for (ReportDescriptor report : reportManager.getCommonReports(currentApp)) {
			common.add(ui.simplifyObject(report));
		}
		List<SimpleObject> cohortAnalysis = new ArrayList<SimpleObject>();
		for (ReportDescriptor report : reportManager.getCohortAnalysisReports(currentApp)) {
			cohortAnalysis.add(ui.simplifyObject(report));
		}
		reportsByProgram.put("Common", common);
		reportsByProgram.put("Cohort Analysis", cohortAnalysis);

		for (ProgramDescriptor programDescriptor : programManager.getAllProgramDescriptors()) {
			Program program = programDescriptor.getTarget();
			List<ReportDescriptor> reports = reportManager.getProgramReports(currentApp, program);

			if (reports.size() > 0) {
				List<SimpleObject> forProgram = new ArrayList<SimpleObject>();

				// We're not calling ui.simplifyCollection because it doesn't play well with subclasses
				for (ReportDescriptor report : reports) {
					forProgram.add(ui.simplifyObject(report));
				}

				reportsByProgram.put(program.getName(), forProgram);
			}
		}

		model.addAttribute("reportsByProgram", reportsByProgram);
	}
}