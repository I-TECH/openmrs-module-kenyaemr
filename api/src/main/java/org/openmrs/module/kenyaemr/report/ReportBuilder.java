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

package org.openmrs.module.kenyaemr.report;

import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

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
	 * Gets the Excel template
	 * @return the excel template for rendering this report, or null if excel is not supported
	 */
	public byte[] getExcelTemplate() {
		return null;
	}

	/**
     * Gets the filename to use for Excel downloads
	 * @param ec the evaluation context
     * @return the filename
     */
	public String getExcelFilename(EvaluationContext ec) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
		return getName() + " " + df.format(ec.getParameterValue("startDate")) + ".xls";
	}

	/**
	 * Maps a parameterizable item using a string list of parameters and values
	 * @param parameterizable the parameterizable item
	 * @param mappings the string list of mappings
	 * @param <T>
	 * @return the mapped item
	 */
	protected static <T extends Parameterizable> Mapped<T> map(T parameterizable, String mappings) {
		if (parameterizable == null) {
			throw new NullPointerException("Programming error: missing parameterizable");
		}
		if (mappings == null) {
			mappings = ""; // probably not necessary, just to be safe
		}
		return new Mapped<T>(parameterizable, ParameterizableUtil.createParameterMappings(mappings));
	}
}
