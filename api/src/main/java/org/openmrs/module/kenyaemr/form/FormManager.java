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

package org.openmrs.module.kenyaemr.form;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.form.FormDescriptor.Frequency;
import org.openmrs.module.kenyaemr.form.FormDescriptor.Gender;
import org.openmrs.module.kenyaemr.form.handler.DynamicObsContainerTagHandler;
import org.openmrs.module.kenyaemr.form.handler.IfModeTagHandler;
import org.openmrs.module.kenyaemr.form.handler.LabTestPickerTagHandler;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Forms manager
 */
@Component
public class FormManager {

	protected static final Log log = LogFactory.getLog(FormManager.class);

	private Map<String, FormDescriptor> forms = new LinkedHashMap<String, FormDescriptor>();

	/**
	 * Updates form manager after context refresh
	 */
	public synchronized void refresh() throws Exception {
		forms.clear();

		List<FormDescriptor> descriptors = Context.getRegisteredComponents(FormDescriptor.class);

		// Sort by form descriptor order
		Collections.sort(descriptors);

		// Load form descriptor beans
		for (FormDescriptor formDescriptor : descriptors) {
			Form form = Context.getFormService().getFormByUuid(formDescriptor.getFormUuid());

			if (form == null) {
				throw new RuntimeException("No such form with UUID: " + formDescriptor.getFormUuid());
			}

			// Because of TRUNK-3889, singleton beans get instantiated twice. Re-enable this check once fixed.
			//if (forms.containsKey(formDescriptor.getFormUuid())) {
			//	throw new RuntimeException("Form " + formDescriptor.getFormUuid() + " already registered");
			//}

			forms.put(form.getUuid(), formDescriptor);

			// Attach form resource if descriptor specifies one
			if (formDescriptor.getResourceProvider() != null && formDescriptor.getResource() != null) {
				FormUtils.setFormXmlPath(form, formDescriptor.getResourceProvider() + ":" + formDescriptor.getResource());
			}

			log.warn("Registered form '" + form.getName() + "' (" + form.getUuid() + ")");
		}

		// Register custom tags
		HtmlFormEntryUtil.getService().addHandler("dynamicObsContainer", new DynamicObsContainerTagHandler());
		HtmlFormEntryUtil.getService().addHandler("labTestPicker", new LabTestPickerTagHandler());
		HtmlFormEntryUtil.getService().addHandler("ifMode", new IfModeTagHandler()); // Override one from HFE
	}

	/**
	 * Gets the form descriptor for the form with the given UUID
	 * @param formUuid the form UUID
	 * @return the form descriptor
	 */
	public FormDescriptor getFormDescriptor(String formUuid) {
		return forms.get(formUuid);
	}

	/**
	 * Gets the form descriptor for the given form
	 * @param form the form
	 * @return the form descriptor
	 */
	public FormDescriptor getFormDescriptor(Form form) {
		return getFormDescriptor(form.getUuid());
	}

	/**
	 * Gets all registered form descriptors
	 * @return the form descriptors
	 */
	public List<FormDescriptor> getAllFormDescriptors() {
		return new ArrayList<FormDescriptor>(forms.values());
	}

	/**
	 * Gets all registered form descriptors for the given app
	 * @return the form descriptors
	 */
	public List<FormDescriptor> getFormDescriptorsForApp(String appKey) {
		List<FormDescriptor> descriptors = new ArrayList<FormDescriptor>();
		for (FormDescriptor descriptor : forms.values()) {
			if (descriptor.getApps().contains(appKey)) {
				descriptors.add(descriptor);
			}
		}
		return descriptors;
	}

	/**
	 * Gets the forms for the given application and patient
	 * @param appKey the application key
	 * @param patient the patient
	 * @param includeFrequencies the set of form frequencies to include (may be null)
	 * @return the forms
	 */
	public List<FormDescriptor> getFormsForPatient(String appKey, Patient patient, Set<Frequency> includeFrequencies) {
		List<FormDescriptor> patientForms = new ArrayList<FormDescriptor>();
		for (Map.Entry<String, FormDescriptor> entry : forms.entrySet()) {
			FormDescriptor form = entry.getValue();

			// Filter by app id
			if (appKey != null && !form.getApps().contains(appKey)) {
				continue;
			}

			// Filter by patient gender
			if (patient.getGender() != null) {
				if (patient.getGender().equals("F") && form.getGender() == Gender.MALE)
					continue;
				else if (patient.getGender().equals("M") && form.getGender() == Gender.FEMALE)
					continue;
			}

			// Filter by frequency
			if (includeFrequencies != null && !includeFrequencies.contains(form.getFrequency())) {
				continue;
			}

			patientForms.add(form);
		}

		return patientForms;
	}
}