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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntryContext.Mode;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionError;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.kenyacore.form.FormUtils;
import org.openmrs.module.kenyaemr.KenyaEmrUiUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *  Controller for HTML Form Entry form submissions
 */
public class EnterHtmlFormFragmentController {

	protected final Log log = LogFactory.getLog(EnterHtmlFormFragmentController.class);

	public void controller(@FragmentParam("patient") Patient patient,
						   @FragmentParam(value = "formUuid", required = false) String formUuid,
						   @FragmentParam(value = "encounter", required = false) Encounter encounter,
						   @FragmentParam(value = "visit", required = false) Visit visit,
						   @FragmentParam(value = "returnUrl", required = false) String returnUrl,
						   @SpringBean ResourceFactory resourceFactory,
						   @SpringBean KenyaEmrUiUtils emrUi,
						   FragmentConfiguration config,
						   FragmentModel model,
						   HttpSession httpSession,
						   PageRequest pageRequest) throws Exception {

		config.require("patient", "formUuid | encounter");

		// Get form from either the encounter or the form UUID
		Form form = (encounter != null) ? encounter.getForm() : Context.getFormService().getFormByUuid(formUuid);

		// Check that form can be accessed in the current app context
		emrUi.checkFormAccess(pageRequest, form);

		// Get html form from database or UI resource
		HtmlForm hf = FormUtils.getHtmlForm(form, resourceFactory);

		if (hf == null) {
			throw new RuntimeException("Form " + form.getName() + " has no associated htmlform");
		}

		// The code below doesn't handle the HFFS case where you might want to _add_ data to an existing encounter
		FormEntrySession fes;
		if (encounter != null) {
			fes = new FormEntrySession(patient, encounter, Mode.EDIT, hf, httpSession);
		}
		else {
			fes = new FormEntrySession(patient, hf, httpSession);
		}

		if (returnUrl != null) {
			fes.setReturnUrl(returnUrl);
		}

		model.addAttribute("command", fes);
		model.addAttribute("visit", visit);
	}

	/**
	 * Handles a form submission
	 * @return form errors in a simple object
	 * @throws Exception
	 */
	public Object submit(@RequestParam("personId") Patient patient,
						 @RequestParam("formId") Form form,
						 @RequestParam(value = "encounterId", required = false) Encounter encounter,
						 @RequestParam(value = "visitId", required = false) Visit visit,
						 @RequestParam(value = "returnUrl", required = false) String returnUrl,
						 @SpringBean ResourceFactory resourceFactory,
						 @SpringBean KenyaEmrUiUtils emrUi,
						 PageRequest pageRequest) throws Exception {

		// Check that form can be accessed in the current app context
		emrUi.checkFormAccess(pageRequest, form);

		// TODO formModifiedTimestamp and encounterModifiedTimestamp

		// Get html form from database or UI resource
		HtmlForm hf = FormUtils.getHtmlForm(form, resourceFactory);

		FormEntrySession fes;
		if (encounter != null) {
			fes = new FormEntrySession(patient, encounter, Mode.EDIT, hf, pageRequest.getRequest().getSession());
		} else {
			fes = new FormEntrySession(patient, hf, Mode.ENTER, pageRequest.getRequest().getSession());
		}

		if (returnUrl != null) {
			fes.setReturnUrl(returnUrl);
		}

		// Validate submission
		List<FormSubmissionError> validationErrors = fes.getSubmissionController().validateSubmission(fes.getContext(), pageRequest.getRequest());

		// If there are validation errors, abort submit and display them
		if (validationErrors.size() > 0) {
			return returnHelper(validationErrors, fes.getContext());
		}

		// No validation errors found so continue process of form submission
		fes.prepareForSubmit();
		fes.getSubmissionController().handleFormSubmission(fes, pageRequest.getRequest());

		// Check this form will actually create an encounter if its supposed to
		if (fes.getContext().getMode() == Mode.ENTER && fes.hasEncouterTag() && (fes.getSubmissionActions().getEncountersToCreate() == null || fes.getSubmissionActions().getEncountersToCreate().size() == 0)) {
			throw new IllegalArgumentException("This form is not going to create an encounter");
		}

		// If encounter is for a specific visit then check encounter date is valid for that visit
		if (visit != null) {
			Encounter formEncounter = fes.getContext().getMode() == Mode.ENTER ? fes.getSubmissionActions().getEncountersToCreate().get(0) : encounter;
			Date formEncounterDateTime = formEncounter.getEncounterDatetime();

			if (formEncounterDateTime.before(visit.getStartDatetime())) {
				validationErrors.add(new FormSubmissionError("general-form-error", "Encounter datetime should be after the visit start date"));
			}
			if (visit.getStopDatetime() != null && formEncounterDateTime.after(visit.getStopDatetime())) {
				validationErrors.add(new FormSubmissionError("general-form-error", "Encounter datetime should be before the visit stop date"));
			}
		}

		// Once again, if there are validation errors, abort submit and display them
		if (validationErrors.size() > 0) {
			return returnHelper(validationErrors, fes.getContext());
		}

		// Do actual encounter creation/updating
		fes.applyActions();

		// Add created encounter to the specified visit
		if (encounter == null && visit != null) {
			encounter = fes.getEncounter();
			encounter.setVisit(visit);
			Context.getEncounterService().saveEncounter(encounter);
		}

		return returnHelper(null, null);
	}

	/**
	 *
	 * @param validationErrors
	 * @param context
	 * @return
	 */
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