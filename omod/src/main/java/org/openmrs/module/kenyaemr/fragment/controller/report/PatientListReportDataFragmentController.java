/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.report;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.Map;

/**
 * Patient list report fragment
 */
public class PatientListReportDataFragmentController {

	public void controller(@FragmentParam("reportData") ReportData reportData, FragmentModel model) {

		// We assume that this kind of report produces a single SimpleDataSet
		SimpleDataSet dataSet = (SimpleDataSet) reportData.getDataSets().entrySet().iterator().next().getValue();

		model.addAttribute("definition", reportData.getDefinition());
		model.addAttribute("dataSet", dataSet);
		model.addAttribute("summary", createSummary(dataSet));
		model.addAttribute("isCohortReport", isCohortAnalysisReport(reportData.getDefinition()));
	}

	/**
	 * Creates a summary of a patient data set
	 * @param dataSet the data set
	 * @return the summary
	 */
	protected Map<String, Integer> createSummary(SimpleDataSet dataSet) {
		Map<String, Integer> summary = new HashedMap();

		int males = 0, females = 0;
		for (DataSetRow row : dataSet.getRows()) {
			String gender = (String) row.getColumnValue("Sex");
			if (gender != "" && gender != null) {
				if (gender.equals("M")) {
					++males;
				} else if (gender.equals("F")) {
					++females;
				}
			}
		}
		summary.put("total", dataSet.getRows().size());
		summary.put("males", males);
		summary.put("females", females);
		return summary;
	}

	boolean isCohortAnalysisReport(ReportDefinition reportDefinition){
		boolean isCohortReport = false;
		if(reportDefinition.getName().contains("cohort")){
			isCohortReport = true;
		}
		return isCohortReport;
	}
}