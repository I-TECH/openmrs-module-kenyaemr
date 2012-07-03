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
import org.openmrs.module.reporting.report.definition.ReportDefinition;


/**
 * Implementations of this should be instantiated as spring beans, and expect to function as singletons
 */
public interface IndicatorReportManager {
	
	/**
	 * @return a lightweight summary of the report definition, suitable for pick lists
	 */
	public DefinitionSummary getReportDefinitionSummary();
	
	/**
	 * This method may be slow, so consider calling {@link #getReportDefinitionSummary()} if you don't need to run it
	 * @return the definition of this report
	 */
	public ReportDefinition getReportDefinition();
	
}
