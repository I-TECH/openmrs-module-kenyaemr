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
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;

/**
 * Base implementation for indicator reports
 */
public abstract class IndicatorReportBuilder implements ReportBuilder {

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
