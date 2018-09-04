/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.converter.simplifier;

import org.openmrs.module.kenyaui.simplifier.AbstractSimplifier;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.ui.framework.SimpleObject;
import org.springframework.stereotype.Component;

/**
 * Converts a report definition to a simple object
 */
@Component
public class ReportDefinitionSimplifier extends AbstractSimplifier<ReportDefinition> {

	/**
	 * @see AbstractSimplifier#simplify(Object)
	 */
	@Override
	protected SimpleObject simplify(ReportDefinition definition) {
		SimpleObject ret = new SimpleObject();
		ret.put("id", definition.getId());
		ret.put("name", definition.getName());
		ret.put("description", definition.getDescription());
		ret.put("uuid", definition.getUuid());
		return ret;
	}
}