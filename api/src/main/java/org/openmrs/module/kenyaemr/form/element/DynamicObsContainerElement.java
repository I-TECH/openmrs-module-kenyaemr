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