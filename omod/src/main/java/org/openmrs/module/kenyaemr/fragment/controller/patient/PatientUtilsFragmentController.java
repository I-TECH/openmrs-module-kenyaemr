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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.PatientService;
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
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.regimen.RegimenChange;
import org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.util.EmrUiUtils;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.AppAction;
import org.openmrs.module.kenyaui.annotation.SharedAction;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.session.Session;
import org.openmrs.util.PersonByNameComparator;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	@SharedAction
	public List<SimpleObject> getSeenPatients(@RequestParam("date") Date date, UiUtils ui) {

		Date startOfDay = DateUtil.getStartOfDay(date);
		Date endOfDay = DateUtil.getEndOfDay(date);
		List<Patient> allPatients = Context.getPatientService().getAllPatients();
		List<Patient> requiredPatients = new ArrayList<Patient>();
		List<SimpleObject> simplifiedObj = new ArrayList<SimpleObject>();

		// look for visits that started before endOfDay and ended after startOfDay
		List<Visit> visits = Context.getVisitService().getVisits(null, allPatients, null, null, startOfDay, endOfDay, null, null, null, true, true);

		if(visits.size() > 0){
			for (Visit visit : visits) {
				requiredPatients.add(visit.getPatient());
			}
		}
		for(Patient p:requiredPatients) {
			SimpleObject so = ui.simplifyObject(p);
			List<Visit> patientVisit = Context.getVisitService().getVisits(null, Arrays.asList(p), null, null, startOfDay, endOfDay, null, null, null, true, true);
			so.put("visits", ui.simplifyCollection(patientVisit));
			simplifiedObj.add(so);

		}


		return simplifiedObj;
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
	/**
	 * Look for the guardians name for an infant from the relationship defined
	 * @param patient
	 * @param now
	 * @return list of guardians
	 */
	public SimpleObject[] getGuardians(@RequestParam("patientId") Patient patient,UiUtils ui) {
		List<Person> people = new ArrayList<Person>();
		for (Relationship relationship : Context.getPersonService().getRelationshipsByPerson(patient)) {
			if (relationship.getRelationshipType().getbIsToA().equals("Guardian")) {
					people.add(relationship.getPersonB());
			}
			if (relationship.getRelationshipType().getaIsToB().equals("Guardian")) {
					people.add(relationship.getPersonA());
			}
		}
		return ui.simplifyCollection(people);
	}
	/**
	 * Check mothers is alive for an infant from the relationship defined
	 * @param patient
	 * @param now
	 * @return list of mothers
	 */
	public SimpleObject[] getMothersLiveStatus(@RequestParam("patientId") Patient patient,UiUtils ui) {
		List<Person> people = new ArrayList<Person>();
		for (Relationship relationship : Context.getPersonService().getRelationshipsByPerson(patient)) {
			if (relationship.getRelationshipType().getbIsToA().equals("Parent")) {
				if (relationship.getPersonB().getGender().equals("F")) {
					if (!relationship.getPersonB().isDead()) {
						people.add(relationship.getPersonB());
					}
				}
			}
			if (relationship.getRelationshipType().getaIsToB().equals("Parent")) {
				if (relationship.getPersonA().getGender().equals("F")) {
					if (!relationship.getPersonB().isDead()) {
						people.add(relationship.getPersonA());
					}
				}
			}
		}
			return ui.simplifyCollection(people);
	}
	/**
	 * Check mothers is CCC number for an infant from the relationship defined
	 * @param patient
	 * @param now
	 * @return list of mothers
	 */

public String getMothersUniquePatientNumber(@RequestParam("patientId") Patient patient,UiUtils ui) {

		String cccNumber = "";
		for (Relationship relationship : Context.getPersonService().getRelationshipsByPerson(patient)) {

			if (relationship.getRelationshipType().getbIsToA().equals("Parent")) {
				if (relationship.getPersonB().getGender().equals("F")) {
					if (!relationship.getPersonB().isDead()) {

						Integer personId = relationship.getPersonB().getPersonId();
						//Patient mother = Context.getPatientService().getPatient(personId);
						if(Context.getPatientService().getPatient(personId) != null) {
							Patient mother = Context.getPatientService().getPatient(personId);
							PatientIdentifierType pit = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
							PatientIdentifier cccObject = mother.getPatientIdentifier(pit);
							cccNumber = cccObject.getIdentifier();
						}
					}
				}
			}
			if (relationship.getRelationshipType().getaIsToB().equals("Parent")) {
				if (relationship.getPersonA().getGender().equals("F")) {
					if (!relationship.getPersonA().isDead()) {

						Integer personId = relationship.getPersonA().getPersonId();
						//Patient mother = Context.getPatientService().getPatient(personId);
						if(Context.getPatientService().getPatient(personId) != null){
							Patient mother = Context.getPatientService().getPatient(personId);
							PatientIdentifierType pit = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
						    PatientIdentifier cccObject = mother.getPatientIdentifier(pit);
						     cccNumber = cccObject.getIdentifier();

						}
					}
				}
			}
		}
		return cccNumber;
	}
	/**
	 * Check mothers current ARV regimen
	 * @param patient
	 * @param now
	 * @return list of mothers
	 */
public SimpleObject currentMothersArvRegimen(@RequestParam("patientId") Patient patient, @RequestParam("now") Date now, @SpringBean RegimenManager regimenManager, @SpringBean EmrUiUtils kenyaEmrUi, @SpringBean KenyaUiUtils kenyaUi, UiUtils ui) {
	SimpleObject obj = new SimpleObject();
		for (Relationship relationship : Context.getPersonService().getRelationshipsByPerson(patient)) {

			if (relationship.getRelationshipType().getbIsToA().equals("Parent")) {
				if (relationship.getPersonB().getGender().equals("F")) {
					if (!relationship.getPersonB().isDead()) {

						Integer personId = relationship.getPersonB().getPersonId();
						//Patient mother = Context.getPatientService().getPatient(personId);
						if(Context.getPatientService().getPatient(personId) != null) {
							Patient mother = Context.getPatientService().getPatient(personId);
							Concept arvs = regimenManager.getMasterSetConcept("ARV");
							RegimenChangeHistory history = RegimenChangeHistory.forPatient(mother, arvs);
							RegimenChange current = history.getLastChangeBeforeDate(now);
							obj = SimpleObject.create(
									"regimen", current != null ? kenyaEmrUi.formatRegimenShort(current.getStarted(), ui) : null,
									"duration", current != null ? kenyaUi.formatInterval(current.getDate(), now) : null
							);
						}
					}
				}
			}
			if (relationship.getRelationshipType().getaIsToB().equals("Parent")) {
				if (relationship.getPersonA().getGender().equals("F")) {
					if (!relationship.getPersonA().isDead()) {

						Integer personId = relationship.getPersonA().getPersonId();
						//Patient mother = Context.getPatientService().getPatient(personId);
						if(Context.getPatientService().getPatient(personId) != null){
							Patient mother = Context.getPatientService().getPatient(personId);
							Concept arvs = regimenManager.getMasterSetConcept("ARV");
							RegimenChangeHistory history = RegimenChangeHistory.forPatient(mother, arvs);
							RegimenChange current = history.getLastChangeBeforeDate(now);
							 obj = SimpleObject.create(
									"regimen", current != null ? kenyaEmrUi.formatRegimenShort(current.getStarted(), ui) : null,
									"duration", current != null ? kenyaUi.formatInterval(current.getDate(), now) : null
							);
						}
					}
				}
			}
		}
		return obj;
	}
	/**
	 * Gets a patient by their id
	 * @param patientId the patient
	 * @param ui the UI utils
	 * @return the simplified patient
	 */
	public String patientExist(@RequestParam("patientId") String patientId,UiUtils ui) {
		String givenName = "";
		List<Patient> patients = Context.getPatientService().getPatients(patientId);
		if(patients.size() > 0){
			givenName = patients.get(0).getGivenName();
		}
		return givenName;
	}
}