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

import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.*;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.session.Session;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class Moh257FragmentController {
	
	public void controller(@FragmentParam("patient")
						   Patient patient,
						   FragmentModel model,
						   UiUtils ui,
						   @SpringBean KenyaEmr emr,
						   @SpringBean KenyaEmrUiUtils kenyaUi) {

		model.addAttribute("newREVisit", newRetrospectiveVisitCommandObject(patient));

		String[] page1Forms = { Metadata.FAMILY_HISTORY_FORM, Metadata.HIV_PROGRAM_ENROLLMENT_FORM };

		List<SimpleObject> page1AvailableForms = new ArrayList<SimpleObject>();
		List<Encounter> page1Encounters = new ArrayList<Encounter>();

		for (String page1Form : page1Forms) {
			List<Encounter> formEncounters = getPatientEncounterByForm(patient, Metadata.getForm(page1Form));

			if (formEncounters.size() == 0) {
				page1AvailableForms.add(kenyaUi.simpleForm(emr.getFormManager().getFormDescriptor(page1Form), ui));
			}
			else {
				page1Encounters.addAll(formEncounters);
			}
		}

		Form moh257VisitForm = Metadata.getForm(Metadata.MOH_257_VISIT_SUMMARY_FORM);
		List<Encounter> moh257VisitSummaryEncounters = getPatientEncounterByForm(patient, moh257VisitForm);

		model.addAttribute("page1AvailableForms", page1AvailableForms);
		model.addAttribute("page1Encounters", page1Encounters);
		model.addAttribute("page2Form", moh257VisitForm);
		model.addAttribute("page2Encounters", moh257VisitSummaryEncounters);

		Concept masterSet = emr.getRegimenManager().getMasterSetConcept("ARV");
		RegimenChangeHistory arvHistory = RegimenChangeHistory.forPatient(patient, masterSet);
		model.addAttribute("arvHistory", arvHistory);
	}

	/**
	 * Convenience method to get encounters from the given form
	 * @param patient the patient
	 * @param form the form
	 * @return the encounters
	 */
	private static List<Encounter> getPatientEncounterByForm(Patient patient, Form form) {
		return Context.getEncounterService().getEncounters(patient, null, null, null, Collections.singleton(form), null, null, null, null, false);
	}

	/**
	 * Helper method to create a new form object
	 * @return the form object
	 */
	public RetrospectiveVisit newRetrospectiveVisitCommandObject(@RequestParam("visit.patientId") Patient patient) {
		return new RetrospectiveVisit(patient);
	}

	/**
	 * Creates a new retrospective visit
	 * @param command the command object
	 * @param ui the UI utils
	 * @return the simplified visit
	 */
	public SimpleObject createRetrospectiveVisit(@MethodParam("newRetrospectiveVisitCommandObject")
												 @BindParams("visit") RetrospectiveVisit command,
												 UiUtils ui,
												 @SpringBean KenyaEmrUiUtils kenyaUi) {
		ui.validate(command, command, "visit");

		Visit visit = command.applyAndReturnVisit();
		return ui.convert(visit, SimpleObject.class);
	}

	/**
	 * We'll create retrospective visits with a single date value, rather than start/stop times
	 */
	public class RetrospectiveVisit extends ValidatingCommandObject {

		private Patient patient;

		private VisitType visitType;

		private Location location;

		private Date visitDate;

		public RetrospectiveVisit(Patient patient) {
			this.patient = patient;
			this.visitType = Metadata.getVisitType(Metadata.OUTPATIENT_VISIT_TYPE);
			this.location = Context.getService(KenyaEmrService.class).getDefaultLocation();
			this.visitDate = OpenmrsUtil.firstSecondOfDay(new Date());
		}

		@Override
		public void validate(Object o, Errors errors) {
			require(errors, "visitType");
			require(errors, "location");
			require(errors, "visitDate");

			if (visitDate.after(OpenmrsUtil.firstSecondOfDay(new Date()))) {
				errors.rejectValue("visitDate", "Date cannot be in the future");
			}
		}

		/**
		 * Converts command object to actual visit. If there are no existing visits on that date, it will create a new visit
		 * otherwise it returns the first visit on that date
		 * @return the visit
		 */
		public Visit applyAndReturnVisit() {
			List<Visit> existingVisitsOnDay = Context.getService(KenyaEmrService.class).getVisitsByPatientAndDay(patient, visitDate);

			if (existingVisitsOnDay.size() == 0) {
				Visit visit = new Visit();
				visit.setVisitType(visitType);
				visit.setLocation(location);
				visit.setPatient(patient);
				visit.setStartDatetime(OpenmrsUtil.firstSecondOfDay(visitDate));
				visit.setStopDatetime(OpenmrsUtil.getLastMomentOfDay(visitDate));
				Context.getVisitService().saveVisit(visit);
				return visit;
			}
			else {
				// Return first visit from that day
				return existingVisitsOnDay.get(0);
			}
		}

		public Integer getPatientId() {
			return patient.getPatientId();
		}

		public VisitType getVisitType() {
			return visitType;
		}

		public void setVisitType(VisitType visitType) {
			this.visitType = visitType;
		}

		public Location getLocation() {
			return location;
		}

		public void setLocation(Location location) {
			this.location = location;
		}

		public Date getVisitDate() {
			return visitDate;
		}

		public void setVisitDate(Date visitDate) {
			this.visitDate = visitDate;
		}
	}
}