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

package org.openmrs.module.kenyacore.reporting;

import org.apache.commons.io.IOUtils;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

/**
 * Implementations of this should be instantiated as spring beans, and expect to function as singletons
 */
public abstract class ReportBuilder {

	protected Boolean configured = Boolean.FALSE;

	protected ReportDefinition reportDefinition;

	/**
	 * @return tags that categorize this report manager
	 */
	public abstract String[] getTags();

	/**
	 * Gets the report name
	 * @return the report name
	 */
	public abstract String getName();

	/**
	 * Gets the report description
	 * @return the report description
	 */
	public abstract String getDescription();

	/**
	 * Gets the Excel template resource path
	 * @return the excel template for rendering this report, or null if excel is not supported
	 */
	public String getExcelTemplateResourcePath() {
		return null;
	}

	/**
	 * Convenience method to see if this report can be rendered as Excel
	 * @return true if report is renderable in Excel
	 */
	public boolean isExcelRenderable() {
		return getExcelTemplateResourcePath() != null;
	}
	
	/**
	 * Gets a lightweight summary of the report definition
	 * @return the definition summary
	 */
	public DefinitionSummary getReportDefinitionSummary() {
		DefinitionSummary ret = new DefinitionSummary();
		ret.setName(getName());
		ret.setDescription(getDescription());
		ret.setUuid(getClass().getName());
		return ret;
	}

	/**
	 * Gets the report definition
	 *
	 * This method may be slow, so consider calling {@link #getReportDefinitionSummary()} if you don't need to run it
	 *
	 * @return the reportDefinition
	 */
	public ReportDefinition getReportDefinition() {
		synchronized (configured) {
			if (!configured) {
				reportDefinition = buildReportDefinition();
				configured = true;
			}
		}
		return reportDefinition;
	}

	/**
	 * Builds the report definition
	 */
	protected abstract ReportDefinition buildReportDefinition();
	
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
}