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

package org.openmrs.module.kenyaemr.converter.simplifier;

import org.openmrs.module.kenyacore.report.IndicatorReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyaui.simplifier.AbstractSimplifier;
import org.openmrs.ui.framework.SimpleObject;
import org.springframework.stereotype.Component;

/**
 * Converts a report descriptor to a simple object
 */
@Component
public class ReportDescriptorSimplifier extends AbstractSimplifier<ReportDescriptor> {

	/**
	 * @see AbstractSimplifier#simplify(Object)
	 */
	@Override
	protected SimpleObject simplify(ReportDescriptor report) {
		return SimpleObject.create(
				"id", report.getId(),
				"name", report.getName(),
				"description", report.getDescription(),
				"isIndicator", report instanceof IndicatorReportDescriptor,
				"definitionUuid", report.getTargetUuid()
		);
	}
}