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

package org.openmrs.module.kenyaemr;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

/**
 * Controller for dynamically generated form fields
 *
 * This should probably be converted to a GSP backed partial page now that UIFR supports undecorated pages
 */
@Controller
public class FieldGeneratorController {

	protected static final Log log = LogFactory.getLog(FieldGeneratorController.class);

	protected final static String VIEW_PATH = "/module/kenyaemr/generateField";

	@RequestMapping("/kenyaemr/generateField.htm")
	public String generateField(@RequestParam(value = "id", required = false) String id,
								@RequestParam(value = "name", required = true) String name,
								@RequestParam(value = "conceptId", required = true) Concept concept,
								@RequestParam(value = "initialValue", required = false) String initialValueText,
								@RequestParam(value = "readOnly", required = false) Boolean readOnly,
								Model model) {

		if (id == null) {
			id = UUID.randomUUID().toString();
		}
		if (readOnly == null) {
			readOnly = false;
		}

		Object initialValue = null;

		// Parse initial value depending on concept datatype
		if (StringUtils.isNotEmpty(initialValueText)) {
			if (concept.getDatatype().isText()) {
				initialValue = initialValueText;
			} else if (concept.getDatatype().isNumeric()) {
				if (((ConceptNumeric) concept).isPrecise()) {
					initialValue = Double.parseDouble(initialValueText);
				}
				else {
					initialValue = new Integer((int)Double.parseDouble(initialValueText));
				}
			} else if (concept.getDatatype().isCoded()) {
				initialValue = Context.getConceptService().getConcept(Integer.valueOf(initialValueText));
			}
		}

		model.addAttribute("id", id);
		model.addAttribute("name", name);
		model.addAttribute("concept", concept);
		model.addAttribute("initialValue", initialValue);
		model.addAttribute("readOnly", readOnly);

		return VIEW_PATH;
	}
}