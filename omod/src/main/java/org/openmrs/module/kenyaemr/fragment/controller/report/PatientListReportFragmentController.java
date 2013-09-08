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

import org.apache.commons.collections.map.HashedMap;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyaemr.reporting.BasePatientListReport;
import org.openmrs.module.kenyaemr.reporting.ReportBuilder;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.Map;

/**
 * Patient list report fragment
 */
public class PatientListReportFragmentController {

	public void controller(@FragmentParam("builder") ReportBuilder builder,
						   FragmentModel model) throws EvaluationException {

		ReportDefinition definition = builder.getDefinition();

		// Evaluate the report
		EvaluationContext ec = new EvaluationContext();
		ReportData data = Context.getService(ReportDefinitionService.class).evaluate(definition, ec);

		// We assume that this kind of report produces a single SimpleDataSet
		SimpleDataSet dataSet = (SimpleDataSet) data.getDataSets().entrySet().iterator().next().getValue();

		model.addAttribute("definition", definition);
		model.addAttribute("dataSet", dataSet);
		model.addAttribute("summary", createSummary(dataSet));
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
			if (gender.equals("M")) {
				++males;
			} else if (gender.equals("F")) {
				++females;
			}
		}

		summary.put("total", dataSet.getRows().size());
		summary.put("males", males);
		summary.put("females", females);
		return summary;
	}
}