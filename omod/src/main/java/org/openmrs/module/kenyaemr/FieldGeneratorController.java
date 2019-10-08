/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
				if (((ConceptNumeric) concept).getAllowDecimal()) {
					initialValue = Double.parseDouble(initialValueText);
				}
				else {
					initialValue = new Integer((int)Double.parseDouble(initialValueText));
				}
				initialValue = new Integer((int)Double.parseDouble(initialValueText));
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