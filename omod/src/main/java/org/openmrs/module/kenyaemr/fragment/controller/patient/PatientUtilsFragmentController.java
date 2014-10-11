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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.CalculationManager;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.calculation.library.ScheduledVisitOnDayCalculation;
import org.openmrs.module.kenyaemr.calculation.library.VisitsOnDayCalculation;
import org.openmrs.module.kenyaui.annotation.AppAction;
import org.openmrs.module.kenyaui.annotation.SharedAction;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.session.Session;
import org.openmrs.util.PersonByNameComparator;
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
	@SharedAction
	public List<SimpleObject> getFlags(@RequestParam("patientId") Integer patientId, @SpringBean CalculationManager calculationManager) {

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
	 * Gets scheduled patients
	 * @param date the date
	 * @param ui the UI utils
	 * @return the simplified patients
	 */
	@SharedAction
	public List<SimpleObject> getScheduled(@RequestParam("date") Date date, UiUtils ui) {
		// Run the calculations to get patients with scheduled visits
		PatientCalculationService cs = Context.getService(PatientCalculationService.class);
		Set<Integer> allPatients = Context.getPatientSetService().getAllPatients().getMemberIds();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", date);
		PatientCalculationContext calcContext = cs.createCalculationContext();

		Set<Integer> scheduled = CalculationUtils.patientsThatPass(cs.evaluate(allPatients, new ScheduledVisitOnDayCalculation(), params, calcContext));
		CalculationResultMap actual = cs.evaluate(scheduled, new VisitsOnDayCalculation(), params, calcContext);

		// Sort patients and convert to simple objects
		List<Patient> scheduledPatients = Context.getPatientSetService().getPatients(scheduled);
		Collections.sort(scheduledPatients, new PersonByNameComparator());

		List<SimpleObject> simplified = new ArrayList<SimpleObject>();
		for (Patient p : scheduledPatients) {
			SimpleObject so = ui.simplifyObject(p);

			ListResult visitsResult = (ListResult) actual.get(p.getPatientId());
			List<Visit> visits = CalculationUtils.extractResultValues(visitsResult);
			so.put("visits", ui.simplifyCollection(visits));

			simplified.add(so);
		}

		return simplified;
	}

	/**
	 * Gets the recently viewed patient list
	 * @return the simple patients
	 */
	@AppAction(EmrConstants.APP_CHART)
	public SimpleObject[] recentlyViewed(UiUtils ui, Session session) {
		String attrName = EmrConstants.APP_CHART + ".recentlyViewedPatients";

		List<Integer> recent = session.getAttribute(attrName, List.class);
		List<Patient> pats = new ArrayList<Patient>();
		if (recent != null) {
			for (Integer ptId : recent) {
				pats.add(Context.getPatientService().getPatient(ptId));
			}
		}

		return ui.simplifyCollection(pats);
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
					if (relationship.getPersonB().getGender().equals("F")) {
						people.add(relationship.getPersonB());
					}
				}
				if (relationship.getRelationshipType().getaIsToB().equals("Parent")) {
					if (relationship.getPersonA().getGender().equals("F")) {
						people.add(relationship.getPersonA());
					}
				}
			}
		return ui.simplifyCollection(people);
	}

	/**
	 * Look for the fathers name for an infant from the relationship defined
	 * @param patient
	 * @param now
	 * @return list of fathers
	 */
	public SimpleObject[] getFathers(@RequestParam("patientId") Patient patient,UiUtils ui) {
		List<Person> people = new ArrayList<Person>();
		for (Relationship relationship : Context.getPersonService().getRelationshipsByPerson(patient)) {
			if (relationship.getRelationshipType().getbIsToA().equals("Parent")) {
				if (relationship.getPersonB().getGender().equals("M")) {
					people.add(relationship.getPersonB());
				}
			}
			if (relationship.getRelationshipType().getaIsToB().equals("Parent")) {
				if (relationship.getPersonA().getGender().equals("M")) {
					people.add(relationship.getPersonA());
				}
			}
		}
		return ui.simplifyCollection(people);
	}
}