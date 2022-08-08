/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller;

import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaui.annotation.AppAction;
import org.openmrs.module.kenyaui.annotation.SharedAction;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.Validate;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helpful utility actions for the registration app
 */
public class RegistrationUtilFragmentController {

	/**
	 * Gets a list of visit types with open visits
	 * @param ui the UI utils
	 * @return the visit types as simple objects
	 */
	@AppAction(EmrConstants.APP_REGISTRATION)
	public List<SimpleObject> getActiveVisitTypes(UiUtils ui) {
		Map<VisitType, Integer> activeVisitTypes = new HashMap<VisitType, Integer>();
		
		List<Visit> activeVisits = Context.getVisitService().getVisits(null, null, null, null, null, null, null, null, null, false, false);
		for (Visit v : activeVisits) {
			Integer count = activeVisitTypes.get(v.getVisitType());
			count = count == null ? 1 : count + 1;
			activeVisitTypes.put(v.getVisitType(), count);
		}
		
		List<SimpleObject> ret = new ArrayList<SimpleObject>();
		for (Map.Entry<VisitType, Integer> e : activeVisitTypes.entrySet()) {
			SimpleObject so = ui.simplifyObject(e.getKey());
			so.put("count", e.getValue());
			ret.add(so);
		}
		return ret;
	}

	/**
	 * Handles requests to close all open visits of the given types
	 * @param visitTypesToClose the visit types to close
	 * @return success or failure message
	 */
	@AppAction(EmrConstants.APP_REGISTRATION)
	public SimpleObject closeActiveVisits(@RequestParam(value = "typeIds", required = false) List<VisitType> visitTypesToClose) {
		Date toStop = new Date();
		VisitService vs = Context.getVisitService();
		List<Visit> activeVisits = vs.getVisits(null, null, null, null, null, null, null, null, null, false, false);
		int numClosed = 0;
		int numFailed = 0;
		for (Visit v : activeVisits) {
			if (visitTypesToClose.contains(v.getVisitType())) {
				try {
					v.setStopDatetime(toStop);
					vs.saveVisit(v);
					numClosed += 1;
				} catch (Exception ex) {
					numFailed += 1;
				}
			}
		}
		String msg = "Closed " + numClosed + " visit";
		if (numClosed > 1) {
			msg += "s";
		}
		if (numFailed > 0) {
			msg += ". Failed to close " + numFailed + " visit";
			if (numFailed > 1) {
				msg += "s";
			}
		}
		return SimpleObject.create("message", msg);
	}

	/**
	 * Starts a new visit
	 * @param ui the UI utils
	 * @param visit the visit
	 * @return the simplified visit
	 */
	@SharedAction({EmrConstants.APP_REGISTRATION, EmrConstants.APP_INTAKE, EmrConstants.APP_CLINICIAN, EmrConstants.APP_HIV_TESTING,EmrConstants.APP_PREP,EmrConstants.APP_KP, EmrConstants.APP_ADHERENCE_COUNSELOR, EmrConstants.APP_COVID})
	public SimpleObject startVisit(@BindParams("visit") @Validate Visit visit, UiUtils ui) {
		if (visit.getLocation() == null)
			visit.setLocation(Context.getService(KenyaEmrService.class).getDefaultLocation());

		ui.validate(visit, new StartVisitValidator(), "visit");

		Context.getVisitService().saveVisit(visit);
		return ui.simplifyObject(visit);
	}
	
	/**
	 * Edits an existing visit
	 * @param ui the UI utils
	 * @param visit the visit
	 * @return the simplified visit
	 */
	@SharedAction({EmrConstants.APP_REGISTRATION, EmrConstants.APP_INTAKE, EmrConstants.APP_CLINICIAN, EmrConstants.APP_HIV_TESTING, EmrConstants.APP_PREP,EmrConstants.APP_KP, EmrConstants.APP_ADHERENCE_COUNSELOR, EmrConstants.APP_COVID})
	public SimpleObject stopVisit(@RequestParam("visitId") Visit visit, @RequestParam("stopDatetime") Date stopDatetime, UiUtils ui) {
		visit.setStopDatetime(stopDatetime);

		ui.validate(visit, new StopVisitValidator(), null);

		Context.getVisitService().saveVisit(visit);
		return ui.simplifyObject(visit);
	}

	/**
	 * Validation for starting visits
	 */
	public class StartVisitValidator implements Validator {
		@Override
		public boolean supports(Class<?> aClass) {
			return aClass.equals(Visit.class);
		}

		@Override
		public void validate(Object obj, Errors errors) {
			Visit visit = (Visit)obj;

			if (Context.getVisitService().getActiveVisitsByPatient(visit.getPatient()).size() > 0) {
				errors.reject("Patient already has an active visit");
			}

			if (visit.getStartDatetime().after(new Date())) {
				errors.rejectValue("startDatetime", "Start date cannot be in the future");
			}
		}
	}

	/**
	 * Validation for stopping visits
	 */
	public class StopVisitValidator implements Validator {
		@Override
		public boolean supports(Class<?> aClass) {
			return aClass.equals(Visit.class);
		}

		@Override
		public void validate(Object obj, Errors errors) {
			Visit visit = (Visit)obj;

			if (visit.getStopDatetime() == null) {
				errors.rejectValue("stopDatetime", "Stop date cannot be empty");
			}
			if (visit.getStopDatetime() != null && visit.getStopDatetime().after(new Date())) {
				errors.rejectValue("stopDatetime", "Stop date cannot be in the future");
			}
		}
	}
}