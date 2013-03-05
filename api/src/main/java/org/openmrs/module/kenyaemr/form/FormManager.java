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
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.form.FormConfig.Frequency;
import org.openmrs.module.kenyaemr.form.FormConfig.Gender;

import java.util.*;

/**
 * Forms manager
 */
public class FormManager {

	private static Map<String, FormConfig> forms = new LinkedHashMap<String, FormConfig>();

	/**
	 * Called from the module activator to register all forms. In future this could be loaded from an XML file
	 */
	public static void setupStandardForms() {
		/**
		 * Once-ever forms
		 */
		registerForm(
				MetadataConstants.FAMILY_HISTORY_FORM_UUID,
				Frequency.ONCE_EVER,
				new String[] { "kenyaemr.medicalChart" },
				null,
				Gender.BOTH,
				"kenyaemr", "forms/family_history.png"
		);
		registerForm(
				MetadataConstants.OBSTETRIC_HISTORY_FORM_UUID,
				Frequency.ONCE_EVER,
				new String[] { "kenyaemr.medicalChart" },
				null,
				Gender.FEMALE,
				"kenyaemr", "forms/obstetric.png"
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
				"kenyaemr", "forms/generic.png"
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
				"kenyaemr", "forms/labresults.png"
		);
		registerForm(
				MetadataConstants.PROGRESS_NOTE_FORM_UUID,
				Frequency.VISIT,
				new String[] { "kenyaemr.intake", "kenyaemr.medicalEncounter", "kenyaemr.medicalChart" }
		);
		registerForm(
				MetadataConstants.MOH_257_VISIT_SUMMARY_FORM_UUID,
				Frequency.VISIT,
				new String[] { "kenyaemr.medicalChart" },
				MetadataConstants.HIV_PROGRAM_UUID,
				Gender.BOTH,
				"kenyaemr", "forms/moh257.png"
		);
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
				"kenyaemr", "forms/generic.png"
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
				"kenyaemr", "forms/generic.png"
		);
		registerForm(
				MetadataConstants.HIV_PROGRAM_DISCONTINUATION_FORM_UUID,
				Frequency.PROGRAM,
				new String[] { "kenyaemr.medicalChart" },
				MetadataConstants.HIV_PROGRAM_UUID,
				Gender.BOTH,
				"kenyaemr", "forms/discontinue.png"
		);
		registerForm(
				MetadataConstants.TB_ENROLLMENT_FORM_UUID,
				Frequency.PROGRAM,
				new String[] { "kenyaemr.medicalChart" },
				MetadataConstants.TB_PROGRAM_UUID,
				Gender.BOTH,
				"kenyaemr", "forms/generic.png"
		);
		registerForm(
				MetadataConstants.TB_COMPLETION_FORM_UUID,
				Frequency.PROGRAM,
				new String[] { "kenyaemr.medicalChart" },
				MetadataConstants.TB_PROGRAM_UUID,
				Gender.BOTH,
				"kenyaemr", "forms/discontinue.png"
		);
	}

	/**
	 * Registers a form for use in the EMR
	 * @param formUuid the form UUID
	 * @param frequency the form usage frequency
	 * @param forApps the applications to use this form
	 */
	public static void registerForm(String formUuid, Frequency frequency, String[] forApps) {
		registerForm(formUuid, frequency, forApps, null, Gender.BOTH, "kenyaemr", "forms/generic.png");
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
	public static void registerForm(String formUuid, Frequency frequency, String[] forApps, String forProgramUuid, Gender forGender, String iconProvider, String icon) {
		if (forms.containsKey(formUuid)) {
			throw new RuntimeException("Form already registered");
		}

		forms.put(formUuid, new FormConfig(formUuid, frequency, new HashSet<String>(Arrays.asList(forApps)), forProgramUuid, forGender, iconProvider, icon));
	}

	/**
	 * Clears all registered forms
	 */
	public static void clear() {
		forms.clear();
	}

	/**
	 * Gets the form configuration for the form with the given UUID
	 * @param formUuid the form UUID
	 * @return the form configuration
	 */
	public static FormConfig getFormConfig(String formUuid) {
		return forms.get(formUuid);
	}

	/**
	 * Gets the forms for the given application and patient
	 * @param appId the application ID
	 * @param patient the patient
	 * @param includeFrequencies the set of form frequencies to include (may be null)
	 * @return the forms
	 */
	public static List<FormConfig> getFormsForPatient(String appId, Patient patient, Set<Frequency> includeFrequencies) {
		List<FormConfig> patientForms = new ArrayList<FormConfig>();
		for (FormConfig form : forms.values()) {

			// Filter by app id
			if (appId != null && !form.getForApps().contains(appId)) {
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