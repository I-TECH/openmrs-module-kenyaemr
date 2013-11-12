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

import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportManager;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.SharedPage;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller for cohort dialog
 */
@SharedPage
public class CohortPageController {

	public void controller(@RequestParam("request") ReportRequest reportRequest,
						   @RequestParam("dataset") String dataSetName,
						   @RequestParam("column") String columnName,
						   PageRequest pageRequest,
						   PageModel model,
						   UiUtils ui,
						   @SpringBean ReportManager reportManager,
						   @SpringBean KenyaUiUtils kenyaUi,
						   @SpringBean ReportService reportService) {

		ReportDefinition definition = reportRequest.getReportDefinition().getParameterizable();
		ReportDescriptor report = reportManager.getReportDescriptor(definition);

		CoreUtils.checkAccess(report, kenyaUi.getCurrentApp(pageRequest));

		ReportData reportData = reportService.loadReportData(reportRequest);

		MapDataSet dataSet = (MapDataSet) reportData.getDataSets().get(dataSetName);

		DataSetColumn dataSetColumn = dataSet.getMetaData().getColumn(columnName);
		Object result = dataSet.getData(dataSetColumn);

		Cohort cohort = null;
		if (result instanceof CohortIndicatorAndDimensionResult) {
			CohortIndicatorAndDimensionResult cidr = (CohortIndicatorAndDimensionResult) dataSet.getData(dataSetColumn);
			cohort = cidr.getCohortIndicatorAndDimensionCohort();
		}
		else if (result instanceof Cohort) {
			cohort = (Cohort) result;
		}

		List<Patient> patients = Context.getPatientSetService().getPatients(cohort.getMemberIds());

		model.addAttribute("column", dataSetColumn);
		model.addAttribute("cohort", cohort);
		model.addAttribute("patients", ui.simplifyCollection(patients));
	}
}