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

package org.openmrs.module.kenyaemr.fragment.controller.patient;

import java.lang.System;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyacore.calculation.CalculationManager;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * AJAX utility methods for patients
 */
public class PatientUtilsFragmentController {

	protected static final Log log = LogFactory.getLog(PatientUtilsFragmentController.class);

	/**
	 * Gets the patient flags for the given patient. If any of the calculations throws an exception, this will return a single
	 * flag with a message with the name of the offending calculation
	 * @param patientId the patient id
	 * @param calculationManager the calculation manager
	 * @return the flags as simple objects
	 */
	public List<SimpleObject> flags(@RequestParam("patientId") Integer patientId, @SpringBean CalculationManager calculationManager) {

		List<SimpleObject> flags = new ArrayList<SimpleObject>();

		// Gather all flag calculations that evaluate to true
		for (PatientFlagCalculation calc : calculationManager.getFlagCalculations()) {
			try {
				CalculationResult result = Context.getService(PatientCalculationService.class).evaluate(patientId, calc);
				if (result != null && (Boolean) result.getValue()) {
					flags.add(SimpleObject.create("message", calc.getFlagMessage()));
				}
			}
			catch (Exception ex) {
				log.error("Error evaluating " + calc.getClass(), ex);
				return Collections.singletonList(SimpleObject.create("message", "ERROR EVALUATING '" +  calc.getFlagMessage() + "'"));
			}
		}
		return flags;
	}

	/**
	 * Gets the patient's age on the given date
	 * @param patient the patient
	 * @param now the current time reference
	 * @return
	 */
	public SimpleObject age(@RequestParam("patientId") Patient patient, @RequestParam("now") Date now) {
		return SimpleObject.create("age", patient.getAge(now));
	}

	/**
	 * Look for the mothers name for an infant from the relationship defined
	 * @param patient
	 * @param now
	 * @return list of mothers
	 */
	public SimpleObject[] getMothers(@RequestParam("patientId") Patient patient,UiUtils ui) {
		List<Person> people = new ArrayList<Person>();
			for (Relationship relationship : Context.getPersonService().getRelationshipsByPerson(patient)) {
				if (relationship.getRelationshipType().getbIsToA().equals("Parent")) {
					if (relationship.getPersonB().getGender().equals("F"))
						people.add(relationship.getPersonB());
				}
				if (relationship.getRelationshipType().getaIsToB().equals("Parent")) {
					if (relationship.getPersonA().getGender().equals("F"))
						people.add(relationship.getPersonA());
				}
			}
		return ui.simplifyCollection(people);
	}
}