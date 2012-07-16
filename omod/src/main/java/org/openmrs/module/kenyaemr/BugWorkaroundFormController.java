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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.module.htmlformentry.web.controller.HtmlFormSearchController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Workarounds for various bugs
 * TODO: document them on JIRA
 */
@Controller
public class BugWorkaroundFormController {
	
	/**
	 * The HTML Form Entry widget does this search relative to the current directory, but this doesn't work
	 * for us since we have a custom HTML Form Entry page.
	 */
	@RequestMapping("/pages/conceptSearch.form")
	public void htmlFormEntryConceptSearch(
			ModelMap model,
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true, value = "term") String query,
			@RequestParam(required = false, value = "answerids") String allowedconceptids,
			@RequestParam(required = false, value = "answerclasses") String answerclasses)
			throws Exception {
		new HtmlFormSearchController().conceptSearch(model, request, response, query, allowedconceptids, answerclasses);
	}
	
}
