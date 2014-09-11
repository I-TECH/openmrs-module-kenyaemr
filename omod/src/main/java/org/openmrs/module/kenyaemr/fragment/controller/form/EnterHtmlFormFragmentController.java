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

package org.openmrs.module.kenyaemr.fragment.controller.form;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntryContext.Mode;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionError;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyacore.form.FormManager;
import org.openmrs.module.kenyacore.form.FormUtils;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.wrapper.EncounterWrapper;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.SharedAction;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentActionRequest;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.*;

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
						   FragmentConfiguration config,
						   FragmentModel model,
						   HttpSession httpSession,
						   PageRequest pageRequest,
						   @SpringBean ResourceFactory resourceFactory,
						   @SpringBean KenyaUiUtils kenyaUi,
						   @SpringBean FormManager formManager) throws Exception {

		config.require("patient", "formUuid | encounter");

		// Get form from either the encounter or the form UUID
		Form form = (encounter != null) ? encounter.getForm() : Context.getFormService().getFormByUuid(formUuid);
		FormDescriptor formDescriptor = formManager.getFormDescriptor(form);

		CoreUtils.checkAccess(formDescriptor, kenyaUi.getCurrentApp(pageRequest));

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

		// Ensure we've generated the form's HTML (and thus set up the submission actions, etc) before we do anything
		fes.getHtmlToDisplay();

		//Context.setVolatileUserData(FORM_IN_PROGRESS_KEY, session);

		model.addAttribute("command", fes);
		model.addAttribute("visit", visit);
	}

	/**
	 * Handles a form submission
	 * @return form errors in a simple object
	 * @throws Exception
	 */
	@SharedAction
	public SimpleObject submit(@RequestParam("personId") Patient patient,
						 @RequestParam("formId") Form form,
						 @RequestParam(value = "encounterId", required = false) Encounter encounter,
						 @RequestParam(value = "visitId", required = false) Visit visit,
						 @RequestParam(value = "returnUrl", required = false) String returnUrl,
						 @SpringBean ResourceFactory resourceFactory,
						 @SpringBean KenyaUiUtils kenyaUi,
						 @SpringBean FormManager formManager,
						 FragmentActionRequest actionRequest) throws Exception {

		// TODO formModifiedTimestamp and encounterModifiedTimestamp

		FormDescriptor formDescriptor = formManager.getFormDescriptor(form);

		CoreUtils.checkAccess(formDescriptor, kenyaUi.getCurrentApp(actionRequest));

		// Get html form from database or UI resource
		HtmlForm hf = FormUtils.getHtmlForm(form, resourceFactory);

		FormEntrySession fes;
		if (encounter != null) {
			fes = new FormEntrySession(patient, encounter, Mode.EDIT, hf, actionRequest.getHttpRequest().getSession());
		} else {
			fes = new FormEntrySession(patient, hf, Mode.ENTER, actionRequest.getHttpRequest().getSession());
		}

		if (returnUrl != null) {
			fes.setReturnUrl(returnUrl);
		}

		// Ensure we've generated the form's HTML (and thus set up the submission actions, etc) before we do anything
		fes.getHtmlToDisplay();

		// Validate submission
		List<FormSubmissionError> validationErrors = fes.getSubmissionController().validateSubmission(fes.getContext(), actionRequest.getHttpRequest());

		// If there are validation errors, abort submit and display them
		if (validationErrors.size() > 0) {
			return simplifyErrors(validationErrors, fes.getContext());
		}

		// No validation errors found so continue process of form submission
		fes.prepareForSubmit();
		fes.getSubmissionController().handleFormSubmission(fes, actionRequest.getHttpRequest());

		// Check this form will actually create an encounter if its supposed to
		if (fes.getContext().getMode() == Mode.ENTER && fes.hasEncouterTag() && (fes.getSubmissionActions().getEncountersToCreate() == null || fes.getSubmissionActions().getEncountersToCreate().size() == 0)) {
			throw new IllegalArgumentException("This form is not going to create an encounter");
		}

		// Get the encounter that will be saved
		Encounter formEncounter = fes.getContext().getMode() == Mode.ENTER ? fes.getSubmissionActions().getEncountersToCreate().get(0) : encounter;
		EncounterWrapper wrapped = new EncounterWrapper(formEncounter);

		// We allow forms to not include <encounterProvider> tags, in which case provider defaults to current user
		if (wrapped.getProvider() == null) {
			wrapped.setProvider(EmrUtils.getProvider(Context.getAuthenticatedUser()));
		}

		// Perform our own custom validation checks
		validationErrors = extraValidation(formEncounter, formDescriptor, visit);

		// Once again, if there are validation errors, abort submit and display them
		if (validationErrors.size() > 0) {
			return simplifyErrors(validationErrors, fes.getContext());
		}

		// Do actual encounter creation/updating
		fes.applyActions();

		return SimpleObject.create("success", true);
	}

	/**
	 * Custom server-side validation
	 * @param formEncounter the encounter being edited/created
	 * @param formDescriptor the form descriptor
	 * @param visit the associated visit
	 * @return any validation errors
	 */
	protected List<FormSubmissionError> extraValidation(Encounter formEncounter, FormDescriptor formDescriptor, Visit visit) {
		List<FormSubmissionError> validationErrors = new ArrayList<FormSubmissionError>();
		EncounterWrapper wrapped = new EncounterWrapper(formEncounter);

		if (wrapped.getProvider() == null) {
			validationErrors.add(new FormSubmissionError("general-form-error", "Current user is not a provider and no other provider was specified"));
		}

		if (formDescriptor.getAutoCreateVisitTypeUuid() != null) {
			// Don't do validation against the visit because the encounter can be moved
		}
		else if (visit != null) {
			// If encounter is for a specific visit then check encounter date is valid for that visit. The visit handler
			// will ensure that the encounter is actually saved into that visit
			Date formEncounterDateTime = formEncounter.getEncounterDatetime();

			if (formEncounterDateTime.before(visit.getStartDatetime())) {
				validationErrors.add(new FormSubmissionError("general-form-error", "Encounter datetime should be after the visit start date"));
			}
			if (visit.getStopDatetime() != null && formEncounterDateTime.after(visit.getStopDatetime())) {
				validationErrors.add(new FormSubmissionError("general-form-error", "Encounter datetime should be before the visit stop date"));
			}
		}

		return validationErrors;
	}

	/**
	 * Creates a simplified error response from validation errors
	 * @param validationErrors the validation errors
	 * @param context the form entry context
	 * @return the simplified errors
	 */
	protected SimpleObject simplifyErrors(List<FormSubmissionError> validationErrors, FormEntryContext context) {
		Map<String, String> errors = new HashMap<String, String>();

		for (FormSubmissionError err : validationErrors) {
			if (err.getSourceWidget() != null) {
				errors.put(context.getErrorFieldId(err.getSourceWidget()), err.getError());
			}
			else {
				errors.put(err.getId(), err.getError());
			}
		}

		return SimpleObject.create("success", false, "errors", errors);
	}
}