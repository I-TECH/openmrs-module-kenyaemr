/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.patient;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.ResultUtil;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyacore.form.FormManager;
import org.openmrs.module.kenyaemr.calculation.library.MissingAllRegistrationIdentifiersCalculation;
import org.openmrs.module.kenyaemr.calculation.library.RecordedDeceasedCalculation;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Patient summary fragment
 */
public class PatientSummaryFragmentController {
	
	public void controller(@FragmentParam("patient") Patient patient,
						   @SpringBean FormManager formManager,
						   @SpringBean KenyaUiUtils kenyaUi,
						   PageRequest pageRequest,
						   UiUtils ui,
						   FragmentModel model) {

		AppDescriptor currentApp = kenyaUi.getCurrentApp(pageRequest);

		// Get all common per-patient forms as simple objects
		List<SimpleObject> forms = new ArrayList<SimpleObject>();
		for (FormDescriptor formDescriptor : formManager.getCommonFormsForPatient(currentApp, patient)) {
			forms.add(ui.simplifyObject(formDescriptor.getTarget()));
		}

		model.addAttribute("patient", patient);
		model.addAttribute("recordedAsDeceased", hasBeenRecordedAsDeceased(patient));
		model.addAttribute("missingIdentifiers", hasNoRequiredRegistrationIdentifiers(patient));
		model.addAttribute("forms", forms);
	}

	/**
	 * Checks if a patient has been recorded as deceased by a program
	 * @param patient the patient
	 * @return true if patient was recorded as deceased
	 */
	protected boolean hasBeenRecordedAsDeceased(Patient patient) {
		PatientCalculation calc = CalculationUtils.instantiateCalculation(RecordedDeceasedCalculation.class, null);
		return ResultUtil.isTrue(Context.getService(PatientCalculationService.class).evaluate(patient.getId(), calc));
	}
	/**
	 * Checks if a patient has been recorded required patient registration identifiers
	 * Patient Clinic Number
	 * National ID Number
	 * Passport Number
	 * Huduma Number
	 * Birth Certificate Number
	 *
	 * @param patient the patient
	 * @return true if patient has no recorded registration identifiers
	 */
	protected boolean hasNoRequiredRegistrationIdentifiers(Patient patient) {
		PatientCalculation calc = CalculationUtils.instantiateCalculation(MissingAllRegistrationIdentifiersCalculation.class, null);
		return ResultUtil.isTrue(Context.getService(PatientCalculationService.class).evaluate(patient.getId(), calc));
	}
}