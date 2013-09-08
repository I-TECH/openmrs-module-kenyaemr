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

import org.apache.commons.io.IOUtils;
import org.openmrs.module.kenyacore.report.AbstractReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Base implementation for indicator reports
 */
public abstract class BaseIndicatorReport extends AbstractReportDescriptor implements ReportBuilder {

	protected Boolean configured = Boolean.FALSE;

	protected ReportDefinition reportDefinition;

	/**
	 * Convenience method to see if this report can be rendered as Excel
	 * @return true if report is renderable in Excel
	 */
	@Override
	public boolean isExcelRenderable() {
		return getExcelTemplateResourcePath() != null;
	}

	/**
	 * Gets the report definition. This method may be slow, so consider calling {@link #getDefinitionSummary()} if you
	 * don't need to run it
	 * @return the report definition
	 */
	@Override
	public ReportDefinition getDefinition() {
		synchronized (configured) {
			if (!configured) {
				reportDefinition = buildReportDefinition();
				configured = true;
			}
		}
		return reportDefinition;
	}

	/**
	 * @see ReportBuilder
	 */
	protected ReportDefinition buildReportDefinition() {
		ReportDefinition rd = new ReportDefinition();
		rd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		rd.addParameter(new Parameter("endDate", "End Date", Date.class));
		rd.setName(getName());
		rd.setDescription(getDescription());

		for (DataSetDefinition dsd : buildDataSets()) {
			rd.addDataSetDefinition(dsd, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));
		}
		return rd;
	}

	/**
	 * Gets the Excel template resource path
	 * @return the excel template for rendering this report, or null if excel is not supported
	 */
	public String getExcelTemplateResourcePath() {
		return null;
	}

	/**
	 * Loads the Excel template if a resource path is specified, else returns null
	 * @return the excel template for rendering this report, or null if excel is not supported
	 */
	public byte[] loadExcelTemplate() {
		String templatePath = getExcelTemplateResourcePath();
		if (templatePath == null) {
			return null;
		}

		try {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(templatePath);
			byte[] contents = IOUtils.toByteArray(is);
			IOUtils.closeQuietly(is);
			return contents;
		} catch (IOException ex) {
			throw new RuntimeException("Error loading excel template", ex);
		}
	}

	/**
	 * Gets the filename to use for Excel downloads
	 * @param ec the evaluation context
	 * @return the filename
	 */
	public String getExcelDownloadFilename(EvaluationContext ec) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
		return getName() + " " + df.format(ec.getParameterValue("startDate")) + ".xls";
	}

	/**
	 * Builds the data sets
	 * @return the data sets
	 */
	protected abstract List<DataSetDefinition> buildDataSets();
}