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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntryContext.Mode;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionError;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 */
public class EnterHtmlFormFragmentController {
	
	public void controller(FragmentConfiguration config,
	                       @FragmentParam("patient") Patient patient,
	                       @FragmentParam(required=false, value="htmlFormId") HtmlForm hf,
	                       @FragmentParam(required=false, value="formId") Form form,
	                       @FragmentParam(required=false, value="formUuid") String formUuid,
	                       FragmentModel model) throws Exception {
		config.require("patient", "htmlFormId | formId | formUuid");

		if (hf == null) {
			if (form != null) {
				hf = Context.getService(HtmlFormEntryService.class).getHtmlFormByForm(form);
			} else if (formUuid != null) {
				form = Context.getFormService().getFormByUuid(formUuid);
				hf = Context.getService(HtmlFormEntryService.class).getHtmlFormByForm(form);
			}
		}
		if (hf == null)
			throw new RuntimeException("Could not find HTML Form");
		
		FormEntrySession fes = new FormEntrySession(patient, hf, Mode.ENTER);
		model.addAttribute("command", fes);
	}
	
	public SimpleObject checkIfLoggedIn() {
		return SimpleObject.create("isLoggedIn", Context.isAuthenticated());
	}
	
	public SimpleObject authenticate(@RequestParam("user") String user, @RequestParam("pass") String pass) {
        try {
            Context.authenticate(user, pass);
        } catch (ContextAuthenticationException ex) {
        	// do nothing
        }
        return checkIfLoggedIn();
    }
	
	public Object submit(@RequestParam("personId") Patient patient,
	                           @RequestParam("htmlFormId") HtmlForm hf,
	                           HttpServletRequest request) throws Exception {

		FormEntrySession fes = new FormEntrySession(patient, hf, Mode.ENTER);
		
        List<FormSubmissionError> validationErrors = fes.getSubmissionController().validateSubmission(fes.getContext(), request);
        if (validationErrors != null && validationErrors.size() > 0) {
        	return returnHelper(validationErrors, fes.getContext());
        }
        
        // no errors
        fes.prepareForSubmit();
        if (fes.getContext().getMode() == Mode.ENTER && fes.hasEncouterTag() && (fes.getSubmissionActions().getEncountersToCreate() == null || fes.getSubmissionActions().getEncountersToCreate().size() == 0))
            throw new IllegalArgumentException("This form is not going to create an encounter"); 

        fes.getSubmissionController().handleFormSubmission(fes, request);
        fes.applyActions();
        return returnHelper(null, null);
	}

    private SimpleObject returnHelper(List<FormSubmissionError> validationErrors, FormEntryContext context) {
	    if (validationErrors == null || validationErrors.size() == 0) {
	    	return SimpleObject.create("success", true);
	    } else {
	    	Map<String, String> errors = new HashMap<String, String>();
	    	for (FormSubmissionError err : validationErrors) {
	    		if (err.getSourceWidget() != null)
	    			errors.put(context.getErrorFieldId(err.getSourceWidget()), err.getError());
                else
                	errors.put(err.getId(), err.getError());
	    	}
	    	return SimpleObject.create("success", false, "errors", errors);
	    }
    }
	
}
