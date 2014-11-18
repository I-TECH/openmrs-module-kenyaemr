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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionError;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.module.htmlformentry.action.FormSubmissionControllerAction;
import org.openmrs.module.htmlformentry.element.HtmlGeneratorElement;
import org.openmrs.module.htmlformentry.widget.ErrorWidget;
import org.openmrs.module.kenyaemr.form.widget.ObjectSearchWidget;
import org.openmrs.module.kenyaemr.wrapper.EncounterWrapper;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Overrides regular <encounterProvider> tag
 */
public class EncounterProviderSubmissionElement implements HtmlGeneratorElement, FormSubmissionControllerAction {

	protected static final Log log = LogFactory.getLog(EncounterProviderSubmissionElement.class);

	private String id;

	private ObjectSearchWidget widget;

	private ErrorWidget errorWidget;

	/**
	 * Constructs new provider submission element
	 * @param context the form entry context
	 * @param parameters the tag parameters
	 */
	public EncounterProviderSubmissionElement(FormEntryContext context, Map<String, String> parameters) {

		widget = new ObjectSearchWidget(Provider.class);
		errorWidget = new ErrorWidget();

		// Parse default value
		Provider defaultProvider = null;
		if (context.getExistingEncounter() != null) {
			EncounterWrapper wrapped = new EncounterWrapper(context.getExistingEncounter());
			defaultProvider = wrapped.getProvider();
		} else {
			String defaultProviderId = parameters.get("default");
			if (StringUtils.hasText(defaultProviderId)) {
				if ("currentUser".equals(defaultProviderId)) {
					Person currentPerson = Context.getAuthenticatedUser().getPerson();
					Collection<Provider> currentPersonProviders = Context.getProviderService().getProvidersByPerson(currentPerson);
					defaultProvider = currentPersonProviders.size() > 0 ? currentPersonProviders.iterator().next() : null;
				}
				else {
					defaultProvider = getProvider(defaultProviderId);
				}
			}
		}

		widget.setInitialValue(defaultProvider);

		context.registerWidget(widget);
		context.registerErrorWidget(widget, errorWidget);

		// Set the id, if it has been specified
		if (parameters.get("id") != null) {
			id = parameters.get("id");
		}
	}

	/**
	 * @see org.openmrs.module.htmlformentry.element.HtmlGeneratorElement#generateHtml(org.openmrs.module.htmlformentry.FormEntryContext)
	 */
	@Override
	public String generateHtml(FormEntryContext context) {
		StringBuilder ret = new StringBuilder();

		if (id != null) {
			ret.append("<span id=\"" + id + "\">");
			context.registerPropertyAccessorInfo(id + ".value", context.getFieldNameIfRegistered(widget), null, null, null);
			context.registerPropertyAccessorInfo(id + ".error", context.getFieldNameIfRegistered(errorWidget), null, null, null);
		}

		ret.append(widget.generateHtml(context));

		if (context.getMode() != FormEntryContext.Mode.VIEW) {
			ret.append(errorWidget.generateHtml(context));
		}

		if (id != null) {
			ret.append("</span>");
		}

		return ret.toString();
	}

	/**
	 * @see org.openmrs.module.htmlformentry.action.FormSubmissionControllerAction#validateSubmission(org.openmrs.module.htmlformentry.FormEntryContext, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public Collection<FormSubmissionError> validateSubmission(FormEntryContext context, HttpServletRequest request) {
		List<FormSubmissionError> errors = new ArrayList<FormSubmissionError>();
		try {
			Object value = widget.getValue(context, request);
			Provider provider = getProvider(value.toString().trim());
			if (provider == null) {
				throw new Exception("required");
			}
		} catch (Exception ex) {
			errors.add(new FormSubmissionError(context.getFieldName(errorWidget), Context.getMessageSourceService().getMessage(ex.getMessage())));
		}
		return errors;
	}

	/**
	 * @see org.openmrs.module.htmlformentry.action.FormSubmissionControllerAction#handleSubmission(org.openmrs.module.htmlformentry.FormEntrySession, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void handleSubmission(FormEntrySession session, HttpServletRequest request) {
		Object value = widget.getValue(session.getContext(), request);
		Provider provider = getProvider(value.toString().trim());

		EncounterWrapper wrapped = new EncounterWrapper(session.getSubmissionActions().getCurrentEncounter());
		wrapped.setProvider(provider);
	}

	/**
	 * Gets a specified provider
	 * @param identifier the identifier
	 * @return the provider
	 */
	public Provider getProvider(String identifier) {
		if (HtmlFormEntryUtil.isValidUuidFormat(identifier)) {
			return Context.getProviderService().getProviderByUuid(identifier);
		}

		try {
			// Try parsing as an integer id
			return Context.getProviderService().getProvider(Integer.parseInt(identifier));
		}
		catch (NumberFormatException ex) { }

		return null;
	}
}