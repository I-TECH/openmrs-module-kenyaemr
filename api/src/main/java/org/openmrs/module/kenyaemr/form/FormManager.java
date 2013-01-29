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

import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.form.FormConfig.Frequency;
import org.openmrs.module.kenyaemr.form.FormConfig.Gender;

import java.util.*;

/**
 * Forms manager
 */
public class FormManager {

	private static Map<String, List<FormConfig>> appForms = new HashMap<String, List<FormConfig>>();

	/**
	 * Called from the module activator to register all forms
	 */
	public static void setupStandardForms() {
		Program hivProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.HIV_PROGRAM_UUID);
		Program tbProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.TB_PROGRAM_UUID);

		appForms.clear();
		registerForm("kenyaemr.registration", new FormConfig(MetadataConstants.TRIAGE_FORM_UUID, Frequency.VISIT));

		registerForm("kenyaemr.intake", new FormConfig(MetadataConstants.TRIAGE_FORM_UUID, Frequency.VISIT));
		registerForm("kenyaemr.intake", new FormConfig(MetadataConstants.TB_SCREENING_FORM_UUID, Frequency.VISIT));
		registerForm("kenyaemr.intake", new FormConfig(MetadataConstants.PROGRESS_NOTE_FORM_UUID, Frequency.VISIT));
		registerForm("kenyaemr.intake", new FormConfig(MetadataConstants.MOH_257_ENCOUNTER_ORDER_LAB_INVESTIGATIONS_FORM_UUID, Frequency.VISIT));
		registerForm("kenyaemr.intake", new FormConfig(MetadataConstants.TB_VISIT_FORM_UUID, Frequency.VISIT, tbProgram, Gender.BOTH, null, null));

		registerForm("kenyaemr.medicalEncounter", new FormConfig(MetadataConstants.CLINICAL_ENCOUNTER_FORM_UUID, Frequency.VISIT));
		registerForm("kenyaemr.medicalEncounter", new FormConfig(MetadataConstants.CLINICAL_ENCOUNTER_HIV_ADDENDUM_FORM_UUID, Frequency.VISIT, hivProgram, Gender.BOTH, null, null));
		registerForm("kenyaemr.medicalEncounter", new FormConfig(MetadataConstants.TB_SCREENING_FORM_UUID, Frequency.VISIT));
		registerForm("kenyaemr.medicalEncounter", new FormConfig(MetadataConstants.PROGRESS_NOTE_FORM_UUID, Frequency.VISIT));
		registerForm("kenyaemr.medicalEncounter", new FormConfig(MetadataConstants.MOH_257_ENCOUNTER_ORDER_LAB_INVESTIGATIONS_FORM_UUID, Frequency.VISIT));
		registerForm("kenyaemr.medicalEncounter", new FormConfig(MetadataConstants.OTHER_MEDICATIONS_FORM_UUID, Frequency.VISIT));
		registerForm("kenyaemr.medicalEncounter", new FormConfig(MetadataConstants.TB_VISIT_FORM_UUID, Frequency.VISIT, tbProgram, Gender.BOTH, null, null));

		registerForm("kenyaemr.medicalChart", new FormConfig(MetadataConstants.FAMILY_HISTORY_FORM_UUID, Frequency.ONCE_EVER, null, Gender.BOTH, "kenyaemr", "forms/family_history.png"));
		registerForm("kenyaemr.medicalChart", new FormConfig(MetadataConstants.OBSTETRIC_HISTORY_FORM_UUID, Frequency.ONCE_EVER, null, Gender.FEMALE, null, null));
		registerForm("kenyaemr.medicalChart", new FormConfig(MetadataConstants.CLINICAL_ENCOUNTER_FORM_UUID, Frequency.VISIT));
		registerForm("kenyaemr.medicalChart", new FormConfig(MetadataConstants.CLINICAL_ENCOUNTER_HIV_ADDENDUM_FORM_UUID, Frequency.VISIT, hivProgram, Gender.BOTH, null, null));
		registerForm("kenyaemr.medicalChart", new FormConfig(MetadataConstants.TB_SCREENING_FORM_UUID, Frequency.VISIT));
		registerForm("kenyaemr.medicalChart", new FormConfig(MetadataConstants.PROGRESS_NOTE_FORM_UUID, Frequency.VISIT));
		registerForm("kenyaemr.medicalChart", new FormConfig(MetadataConstants.MOH_257_ENCOUNTER_ORDER_LAB_INVESTIGATIONS_FORM_UUID, Frequency.VISIT));
		registerForm("kenyaemr.medicalChart", new FormConfig(MetadataConstants.OTHER_MEDICATIONS_FORM_UUID, Frequency.VISIT));
		registerForm("kenyaemr.medicalChart", new FormConfig(MetadataConstants.TB_VISIT_FORM_UUID, Frequency.VISIT, tbProgram, Gender.BOTH, null, null));

		// Hidden until 2013.1.1
		//registerForm("kenyaemr.medicalChart", new FormConfig(MetadataConstants.RETROSPECTIVE_257_FORM_UUID, Frequency.VISIT, hivProgram, Gender.BOTH, null, null));
	}

	/**
	 * Registers the given form for the given application
	 * @param appId the application ID
	 * @param formConfig the form configuration
	 */
	public static void registerForm(String appId, FormConfig formConfig) {
		List<FormConfig> forms = appForms.get(appId);
		if (forms == null) {
			forms = new ArrayList<FormConfig>();
			appForms.put(appId, forms);
		}

		forms.add(formConfig);
	}

	/**
	 * Gets the forms for the given application and patient
	 * @param appId the application ID
	 * @param patient the patient
	 * @param includeFrequencies the set of form frequencies to include (may be null)
	 * @return the forms
	 */
	public static List<FormConfig> getFormsForPatient(String appId, Patient patient, Set<Frequency> includeFrequencies) {
		List<FormConfig> allForms = appForms.get(appId);
		if (allForms == null)
			return new ArrayList<FormConfig>();

		List<FormConfig> patientForms = new ArrayList<FormConfig>();
		for (FormConfig form : allForms) {

			// Filter by patient gender
			if (patient.getGender() != null) {
				if (patient.getGender().equals("F") && form.getGender() == Gender.MALE)
					continue;
				else if (patient.getGender().equals("M") && form.getGender() == Gender.FEMALE)
					continue;
			}

			// Filter by frequency
			if (includeFrequencies != null && !includeFrequencies.contains(form.getFrequency()))
				continue;

			patientForms.add(form);
		}

		return patientForms;
	}
}