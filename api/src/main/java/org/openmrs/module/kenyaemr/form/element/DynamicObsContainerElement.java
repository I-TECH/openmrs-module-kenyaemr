/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.form.element;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.element.HtmlGeneratorElement;

import java.util.Map;


/**
 *
 */
public class DynamicObsContainerElement implements HtmlGeneratorElement {

	private String id;

	public DynamicObsContainerElement(FormEntryContext context, Map<String, String> parameters) {
		id = parameters.get("id");

		if (StringUtils.isEmpty(id)) {
			throw new RuntimeException("Id attribute required");
		}
	}

	/**
	 * @see HtmlGeneratorElement#generateHtml(org.openmrs.module.htmlformentry.FormEntryContext)
	 */
	@Override
	public String generateHtml(FormEntryContext context) {
		StringBuilder sb = new StringBuilder();

		// Generate HTML for container for dynamic fields
		sb.append("<div id=\"" + id + "\"></div>\n");

		return sb.toString();
	}
}