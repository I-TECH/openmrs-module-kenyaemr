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
package org.openmrs.module.kenyaemr.fragment.controller;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntryContext.Mode;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.ui.framework.SimpleObject;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 */
public class ShowHtmlFormFragmentController {
	
	public void controller() {
		// do nothing
	}
	
	public SimpleObject viewFormHtml(@RequestParam("encounterId") Encounter enc) throws Exception {
		HtmlForm hf = Context.getService(HtmlFormEntryService.class).getHtmlFormByForm(enc.getForm());
		FormEntrySession fes = new FormEntrySession(enc.getPatient(), enc, Mode.VIEW, hf);
		String html = fes.getHtmlToDisplay();
		return SimpleObject.create("html", html);
	}
	
	public SimpleObject deleteEncounter(@RequestParam("encounterId") Encounter enc) {
		Context.getEncounterService().voidEncounter(enc, "Kenya EMR UI");
		return SimpleObject.create("encounterId", enc.getEncounterId());
	}
	
}
