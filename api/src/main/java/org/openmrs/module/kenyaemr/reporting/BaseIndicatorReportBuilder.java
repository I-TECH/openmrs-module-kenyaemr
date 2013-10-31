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

package org.openmrs.module.kenyaemr.reporting;

import org.openmrs.module.kenyacore.report.IndicatorReportDescriptor;
import org.openmrs.module.kenyacore.report.builder.ReportBuilder;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Base implementation for indicator report builders
 */
public abstract class BaseIndicatorReportBuilder implements ReportBuilder {

	protected IndicatorReportDescriptor report;

	/**
	 * Gets the report definition.
	 * @return the report definition
	 */
	@Override
	public ReportDefinition getDefinition(ReportDescriptor report) {
		this.report = (IndicatorReportDescriptor) report;

		ReportDefinition rd = new ReportDefinition();
		rd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		rd.addParameter(new Parameter("endDate", "End Date", Date.class));
		rd.setName(report.getName());
		rd.setDescription(report.getDescription());

		for (DataSetDefinition dsd : buildDataSets()) {
			rd.addDataSetDefinition(dsd, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		}
		return rd;
	}

	/**
	 * Builds the data sets
	 * @return the data sets
	 */
	protected abstract List<DataSetDefinition> buildDataSets();
}