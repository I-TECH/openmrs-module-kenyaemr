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
import org.openmrs.module.kenyaemr.KenyaEmrUiUtils;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.ValidatingCommandObject;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.form.FormManager;
import org.openmrs.module.kenyaemr.regimen.RegimenOrderHistory;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.util.KenyaEmrUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.MethodParam;
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
public class MedicalChartMoh257FragmentController {
	
	public void controller(@FragmentParam("patient") Patient patient, FragmentModel model, UiUtils ui, Session session) {

		model.addAttribute("newREVisit", newRetrospectiveVisitCommandObject(patient));

		String[] page1FormUuids = {
				MetadataConstants.FAMILY_HISTORY_FORM_UUID,
				MetadataConstants.HIV_PROGRAM_ENROLLMENT_FORM_UUID
		};

		List<SimpleObject> page1AvailableForms = new ArrayList<SimpleObject>();
		List<Encounter> page1Encounters = new ArrayList<Encounter>();

		for (String page1FormUuid : page1FormUuids) {
			List<Encounter> formEncounters = getPatientEncounterByForm(patient, Context.getFormService().getFormByUuid(page1FormUuid));

			if (formEncounters.size() == 0) {
				page1AvailableForms.add(KenyaEmrUiUtils.simpleForm(FormManager.getFormConfig(page1FormUuid), ui));
			}
			else {
				page1Encounters.addAll(formEncounters);
			}
		}

		List<Encounter> moh257VisitSummaryEncounters = getPatientEncounterByForm(patient, Context.getFormService().getFormByUuid(MetadataConstants.MOH_257_VISIT_SUMMARY_FORM_UUID));

		model.addAttribute("page1AvailableForms", page1AvailableForms);
		model.addAttribute("page1Encounters", page1Encounters);
		model.addAttribute("page2Encounters", moh257VisitSummaryEncounters);

		Concept masterSet = RegimenManager.getMasterSetConcept("ARV");
		RegimenOrderHistory arvHistory = RegimenOrderHistory.forPatient(patient, masterSet);
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
	 * @param ui the UI utils
	 * @param command the command object
	 * @return the simplified visit
	 */
	public SimpleObject createRetrospectiveVisit(UiUtils ui, @MethodParam("newRetrospectiveVisitCommandObject") @BindParams("visit") RetrospectiveVisit command) {
		ui.validate(command, command, "visit");

		Visit visit = command.toVisit();
		Context.getVisitService().saveVisit(visit);
		return KenyaEmrUiUtils.simpleVisit(visit, ui);
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
			this.visitType = Context.getVisitService().getVisitTypeByUuid(MetadataConstants.OUTPATIENT_VISIT_TYPE_UUID);
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

			if (KenyaEmrUtils.visitWillOverlap(toVisit())) {
				errors.rejectValue("visitDate", "Date cannot overlap with the patient's existing visits");
			}
		}

		/**
		 * Converts command object to actual visit
		 * @return the actual visit
		 */
		public Visit toVisit() {
			Visit visit = new Visit();
			visit.setVisitType(visitType);
			visit.setLocation(location);
			visit.setPatient(patient);
			visit.setStartDatetime(OpenmrsUtil.firstSecondOfDay(visitDate));
			visit.setStopDatetime(OpenmrsUtil.getLastMomentOfDay(visitDate));
			return visit;
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