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
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleClassLoader;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.module.kenyaemr.KenyaEmr;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.form.FormDescriptor.Frequency;
import org.openmrs.module.kenyaemr.form.FormDescriptor.Gender;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Forms manager
 */
@Component
public class FormManager {

	protected static final Log log = LogFactory.getLog(FormManager.class);

	private Map<String, FormDescriptor> forms = new LinkedHashMap<String, FormDescriptor>();

	/**
	 * Clears all forms
	 */
	public synchronized void clear() {
		forms.clear();
	}

	/**
	 * Refreshes the list of form descriptors from the application context
	 */
	public synchronized void refreshFormDescriptors(ResourceFactory resourceFactory) throws Exception {
		clear();

		for (FormDescriptor formDescriptor : Context.getRegisteredComponents(FormDescriptor.class)) {
			forms.put(formDescriptor.getFormUuid(), formDescriptor);

			// Load form resource if descriptor specifies one
			if (formDescriptor.getResourceProvider() != null && formDescriptor.getResource() != null) {
				loadFormFromUiResource(formDescriptor.getFormUuid(), resourceFactory, formDescriptor.getResourceProvider(), formDescriptor.getResource());
			}

			log.info("Found form descriptor:" + formDescriptor.getFormUuid());
		}

		/**
		 * Because we haven't yet made beans for all forms...
		 */
		setupStandardForms();
	}

	/**
	 * Called from the module activator to register all forms. In future this could be loaded from an XML file
	 */
	public void setupStandardForms() {
		/**
		 * Once-ever forms
		 */
		registerForm(
				MetadataConstants.FAMILY_HISTORY_FORM_UUID,
				Frequency.ONCE_EVER,
				new String[] { "kenyaemr.medicalChart" },
				null,
				Gender.BOTH,
				"kenyaui", "forms/family_history.png"
		);
		registerForm(
				MetadataConstants.OBSTETRIC_HISTORY_FORM_UUID,
				Frequency.ONCE_EVER,
				new String[] { "kenyaemr.medicalChart" },
				null,
				Gender.FEMALE,
				"kenyaui", "forms/obstetric.png"
		);

		/**
		 * Visit forms
		 */
		registerForm(
				MetadataConstants.TRIAGE_FORM_UUID,
				Frequency.VISIT,
				new String[] { "kenyaemr.registration", "kenyaemr.intake" }
		);
		registerForm(
				MetadataConstants.CLINICAL_ENCOUNTER_FORM_UUID,
				Frequency.VISIT,
				new String[] { "kenyaemr.medicalEncounter", "kenyaemr.medicalChart" }
		);
		registerForm(
				MetadataConstants.CLINICAL_ENCOUNTER_HIV_ADDENDUM_FORM_UUID,
				Frequency.VISIT,
				new String[] { "kenyaemr.medicalEncounter", "kenyaemr.medicalChart" },
				MetadataConstants.HIV_PROGRAM_UUID,
				Gender.BOTH,
				"kenyaui", "forms/generic.png"
		);
		registerForm(
				MetadataConstants.OTHER_MEDICATIONS_FORM_UUID,
				Frequency.VISIT,
				new String[] { "kenyaemr.medicalEncounter", "kenyaemr.medicalChart" }
		);
		registerForm(
				MetadataConstants.MOH_257_ENCOUNTER_ORDER_LAB_INVESTIGATIONS_FORM_UUID,
				Frequency.VISIT,
				new String[] { "kenyaemr.intake", "kenyaemr.medicalEncounter", "kenyaemr.medicalChart" },
				null,
				Gender.BOTH,
				"kenyaui", "forms/labresults.png"
		);
		registerForm(
				MetadataConstants.PROGRESS_NOTE_FORM_UUID,
				Frequency.VISIT,
				new String[] { "kenyaemr.intake", "kenyaemr.medicalEncounter", "kenyaemr.medicalChart" }
		);
		/*registerForm(
				MetadataConstants.MOH_257_VISIT_SUMMARY_FORM_UUID,
				Frequency.VISIT,
				new String[] { "kenyaemr.medicalChart" },
				MetadataConstants.HIV_PROGRAM_UUID,
				Gender.BOTH,
				"kenyaui", "forms/moh257.png"
		);*/
		registerForm(
				MetadataConstants.TB_SCREENING_FORM_UUID,
				Frequency.VISIT,
				new String[] { "kenyaemr.intake", "kenyaemr.medicalEncounter", "kenyaemr.medicalChart" }
		);
		registerForm(
				MetadataConstants.TB_VISIT_FORM_UUID,
				Frequency.VISIT,
				new String[] { "kenyaemr.intake", "kenyaemr.medicalEncounter", "kenyaemr.medicalChart" },
				MetadataConstants.TB_PROGRAM_UUID,
				Gender.BOTH,
				"kenyaui", "forms/generic.png"
		);

		/**
		 * Program forms
		 */
		registerForm(
				MetadataConstants.HIV_PROGRAM_ENROLLMENT_FORM_UUID,
				Frequency.PROGRAM,
				new String[] { "kenyaemr.medicalChart" },
				MetadataConstants.HIV_PROGRAM_UUID,
				Gender.BOTH,
				"kenyaui", "forms/generic.png"
		);
		registerForm(
				MetadataConstants.HIV_PROGRAM_DISCONTINUATION_FORM_UUID,
				Frequency.PROGRAM,
				new String[] { "kenyaemr.medicalChart" },
				MetadataConstants.HIV_PROGRAM_UUID,
				Gender.BOTH,
				"kenyaui", "forms/discontinue.png"
		);
		registerForm(
				MetadataConstants.TB_ENROLLMENT_FORM_UUID,
				Frequency.PROGRAM,
				new String[] { "kenyaemr.medicalChart" },
				MetadataConstants.TB_PROGRAM_UUID,
				Gender.BOTH,
				"kenyaui", "forms/generic.png"
		);
		registerForm(
				MetadataConstants.TB_COMPLETION_FORM_UUID,
				Frequency.PROGRAM,
				new String[] { "kenyaemr.medicalChart" },
				MetadataConstants.TB_PROGRAM_UUID,
				Gender.BOTH,
				"kenyaui", "forms/discontinue.png"
		);
	}

	/**
	 * Registers a form for use in the EMR
	 * @param formUuid the form UUID
	 * @param frequency the form usage frequency
	 * @param forApps the applications to use this form
	 */
	public void registerForm(String formUuid, Frequency frequency, String[] forApps) {
		registerForm(formUuid, frequency, forApps, null, Gender.BOTH, "kenyaui", "forms/generic.png");
	}

	/**
	 * Registers a form for use in the EMR
	 * @param formUuid the form UUID
	 * @param frequency the form usage frequency
	 * @param forApps the applications to use this form
	 * @param forProgramUuid the form program usage (may be null)
	 * @param forGender the gender usage
	 * @param iconProvider the icon provider id
	 * @param icon the icon file
	 */
	public void registerForm(String formUuid, Frequency frequency, String[] forApps, String forProgramUuid, Gender forGender, String iconProvider, String icon) {
		if (forms.containsKey(formUuid)) {
			throw new RuntimeException("Form already registered");
		}

		FormDescriptor formDescriptor = new FormDescriptor(formUuid, frequency, new HashSet<String>(Arrays.asList(forApps)), forProgramUuid, forGender, iconProvider, icon);

		forms.put(formUuid, formDescriptor);

		log.info("Registered form: " + formDescriptor);
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
	 * Gets all registered form descriptors
	 * @return the form descriptors
	 */
	public List<FormDescriptor> getAllFormDescriptors() {
		return new ArrayList<FormDescriptor>(forms.values());
	}

	/**
	 * Gets the forms for the given application and patient
	 * @param appId the application ID
	 * @param patient the patient
	 * @param includeFrequencies the set of form frequencies to include (may be null)
	 * @return the forms
	 */
	public List<FormDescriptor> getFormsForPatient(String appId, Patient patient, Set<Frequency> includeFrequencies) {
		List<FormDescriptor> patientForms = new ArrayList<FormDescriptor>();
		for (FormDescriptor form : forms.values()) {

			// Filter by app id
			if (appId != null && !form.getApps().contains(appId)) {
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

	/**
	 * Loads a form from a UI resource
	 * @param formUuid the form UUID to save to
	 * @apram resourceFactory the resource factory
	 * @param providerName the resource provider name
	 * @param resourcePath the resource path
	 * @return the HTML form
	 * @throws IOException if an error occurs
	 */
	public HtmlForm loadFormFromUiResource(String formUuid, ResourceFactory resourceFactory, String providerName, String resourcePath) throws IOException {

		// TODO temporary workaround until UIFR-102 or TRUNK-3922 is fixed.
		// Instead of using ResourceFactory.getResourceAsString we manually load the resource

		ModuleClassLoader moduleClassLoader = ModuleFactory.getModuleClassLoader(providerName);
		moduleClassLoader.findResource("web/module/resources/" + resourcePath);
		File folderForModule = ModuleClassLoader.getLibCacheFolderForModule(moduleClassLoader.getModule());
		File resourceFile = new File(folderForModule, "web/module/resources/" + resourcePath);
		String xml = OpenmrsUtil.getFileAsString(resourceFile);

		//String xml = resourceFactory.getResourceAsString(providerName, resourcePath);

		if (xml == null) {
			throw new IllegalArgumentException("No resource found at " + providerName + ":" + resourcePath);
		}

		try {
			Document doc = HtmlFormEntryUtil.stringToDocument(xml);
			Node htmlFormNode = HtmlFormEntryUtil.findChild(doc, "htmlform");

			String formName = getAttributeValue(htmlFormNode, "formName");
			String formDescription = getAttributeValue(htmlFormNode, "formDescription");
			String formVersion = getAttributeValue(htmlFormNode, "formVersion");
			String formEncounterType = getAttributeValue(htmlFormNode, "formEncounterType");

			Form form = Context.getFormService().getFormByUuid(formUuid);
			if (form == null) {
				form = new Form();
				form.setUuid(formUuid);
			}

			form.setName(formName);
			form.setDescription(formDescription);
			form.setVersion(formVersion);
			form.setEncounterType(HtmlFormEntryUtil.getEncounterType(formEncounterType));
			Context.getFormService().saveForm(form);

			HtmlForm htmlForm = Context.getService(HtmlFormEntryService.class).getHtmlFormByForm(form);
			if (htmlForm == null) {
				htmlForm = new HtmlForm();
				htmlForm.setForm(form);
			}
			htmlForm.setXmlData(xml);

			Context.getService(HtmlFormEntryService.class).saveHtmlForm(htmlForm);
			return htmlForm;

		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to parse XML and build Form and HtmlForm", e);
		}
	}

	/**
	 * Convenience method to get the named attribute value of a DOM node
	 * @param node the DOM node
	 * @param attributeName the attribute name
	 * @return the attribute value
	 */
	private static String getAttributeValue(Node node, String attributeName) {
		Node item = node.getAttributes().getNamedItem(attributeName);
		return item == null ? null : item.getNodeValue();
	}
}