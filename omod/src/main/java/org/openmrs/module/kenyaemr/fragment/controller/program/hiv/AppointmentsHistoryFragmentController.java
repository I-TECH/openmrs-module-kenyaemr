/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.program.hiv;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.wrapper.PatientWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * serves appointmentsHistory fragment
 */
public class AppointmentsHistoryFragmentController {
	
	ConceptService conceptService = Context.getConceptService();
	
	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");
	
	public void controller(FragmentModel model, @FragmentParam("patient") Patient patient) {
		
		PatientWrapper patientWrapper = new PatientWrapper(patient);
		
		Form hivGreenCardForm = MetadataUtils.existing(Form.class, HivMetadata._Form.HIV_GREEN_CARD);
		List<Encounter> hivClinicalEncounters = patientWrapper.allEncounters(hivGreenCardForm);
		Collections.reverse(hivClinicalEncounters);
		
		// get hiv greencard list of observations
		List<SimpleObject> encDetails = new ArrayList<SimpleObject>();
		//List<SimpleObject> encDetails = new ArrayList<SimpleObject>();
		if (hivClinicalEncounters != null) {
			for (int i = 0; i < hivClinicalEncounters.size(); i++) {
				Encounter enc = hivClinicalEncounters.get(i);
				SimpleObject o = getEncDetails(enc.getObs(), enc, hivClinicalEncounters);
				encDetails.add(o);
				if (i == 2) {
					break;
				}
			}
		}
		model.put("encounters", encDetails);
	}
	
	/**
	 * Extract TCA information from encounters and order them based on Date
	 * 
	 * @param
	 * @return
	 */
	SimpleObject getEncDetails(Set<Obs> obsList, Encounter e, List<Encounter> allClinicalEncounters) {
		
		Integer tcaDateConcept = 5096;
		String tcaDateString = null;
		Date tcaDate = null;
		int appointmentDuration = 0;
		String appointmentHonoured = "";
		for (Obs obs : obsList) {
			
			if (obs.getConcept().getConceptId().equals(tcaDateConcept)) {
				tcaDate = obs.getValueDate();
				tcaDateString = tcaDate != null ? DATE_FORMAT.format(tcaDate) : "";
				appointmentDuration = Days.daysBetween(new LocalDate(e.getEncounterDatetime()), new LocalDate(tcaDate))
				        .getDays();
				if (hasVisitOnDate(tcaDate, e.getPatient(), allClinicalEncounters)) {
					appointmentHonoured = "Yes";
				}
			}
		}
		return SimpleObject.create("encDate", DATE_FORMAT.format(e.getEncounterDatetime()), "tcaDate",
		    tcaDateString != null ? tcaDateString : "", "encounter", Arrays.asList(e), "form", e.getForm(), "patientId", e
		            .getPatient().getPatientId(), "appointmentPeriod", appointmentDuration, "honoured", appointmentHonoured);
	}
	
	private boolean hasVisitOnDate(Date appointmentDate, Patient patient, List<Encounter> allEncounters) {
		boolean hasVisitOnDate = false;
		for (Encounter e : allEncounters) {
			int sameDay = new LocalDate(e.getEncounterDatetime()).compareTo(new LocalDate(appointmentDate));
			
			if (sameDay == 0) {
				hasVisitOnDate = true;
				break;
			}
		}
		return hasVisitOnDate;
	}
}
