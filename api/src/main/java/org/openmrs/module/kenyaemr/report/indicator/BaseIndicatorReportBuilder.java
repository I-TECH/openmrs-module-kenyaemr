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

package org.openmrs.module.kenyaemr.report.indicator;

import org.openmrs.module.kenyaemr.report.ReportBuilder;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;

import java.util.Date;

/**
 * Base implementation for indicator reports
 */
public abstract class BaseIndicatorReportBuilder extends ReportBuilder {

	/**
	 * @see org.openmrs.module.kenyaemr.report.ReportBuilder#buildReportDefinition()
	 */
	@Override
	protected ReportDefinition buildReportDefinition() {
		ReportDefinition rd = new ReportDefinition();
		rd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		rd.addParameter(new Parameter("endDate", "End Date", Date.class));
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.addDataSetDefinition(buildDataSet(), ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		return rd;
	}

	/**
	 * Builds the data set
	 * @return the data set
	 */
	protected abstract DataSetDefinition buildDataSet();
}