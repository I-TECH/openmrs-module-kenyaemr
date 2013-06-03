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

import org.openmrs.Patient;
import org.openmrs.module.kenyaemr.KenyaEmr;
import org.openmrs.module.kenyaemr.KenyaEmrUiUtils;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.form.FormDescriptor;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Patient summary fragment
 */
public class PatientSummaryFragmentController {
	
	public void controller(@FragmentParam("patient") Patient patient, @SpringBean KenyaEmr emr, @SpringBean KenyaEmrUiUtils kenyaUi, UiUtils ui, FragmentModel model) {

		List<SimpleObject> forms = new ArrayList<SimpleObject>();
		forms.add(simpleForm(emr, kenyaUi, ui, Metadata.FAMILY_HISTORY_FORM));

		if (patient.getGender().equals("F")) {
			forms.add(simpleForm(emr, kenyaUi, ui, Metadata.OBSTETRIC_HISTORY_FORM));
		}

		model.addAttribute("patient", patient);
		model.addAttribute("forms", forms);

		model.addAttribute("clinicNumberIdType", Metadata.getPatientIdentifierType(Metadata.PATIENT_CLINIC_NUMBER_IDENTIFIER_TYPE));
		model.addAttribute("hivNumberIdType", Metadata.getPatientIdentifierType(Metadata.UNIQUE_PATIENT_NUMBER_IDENTIFIER_TYPE));
	}

	private static SimpleObject simpleForm(KenyaEmr emr, KenyaEmrUiUtils kenyaUi, UiUtils ui, String formIdentifier) {
		FormDescriptor formDescriptor = emr.getFormManager().getFormDescriptor(formIdentifier);
		return kenyaUi.simpleForm(formDescriptor, ui);
	}
}