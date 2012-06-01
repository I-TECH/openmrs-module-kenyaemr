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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.annotation.Validate;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.openmrs.ui.framework.session.Session;
import org.openmrs.validator.PatientValidator;
import org.openmrs.validator.ValidateUtil;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Helpful utility actions for the registration app
 */
public class RegistrationUtilFragmentController {
	
	public List<SimpleObject> activeVisitTypes(UiUtils ui) {
		Map<VisitType, Integer> activeVisitTypes = new HashMap<VisitType, Integer>();
		
		List<Visit> activeVisits = Context.getVisitService().getVisits(null, null, null, null, null, null, null, null, null, false, false);
		for (Visit v : activeVisits) {
			Integer count = activeVisitTypes.get(v.getVisitType());
			count = count == null ? 1 : count + 1;
			activeVisitTypes.put(v.getVisitType(), count);
		}
		
		List<SimpleObject> ret = new ArrayList<SimpleObject>();
		for (Map.Entry<VisitType, Integer> e : activeVisitTypes.entrySet()) {
			SimpleObject so = SimpleObject.fromObject(e.getKey(), ui, "visitTypeId", "name");
			so.put("count", e.getValue());
			ret.add(so);
		}
		return ret;
	}
	
	public Object closeActiveVisits(@RequestParam("visitType") List<VisitType> visitTypesToClose) {
		if (CollectionUtils.isEmpty(visitTypesToClose)) {
			return new FailureResult("You didn't choose any types");
		}
		Date toStop = new Date();
		VisitService vs = Context.getVisitService();
		List<Visit> activeVisits = vs.getVisits(null, null, null, null, null, null, null, null, null, false, false);
		int numClosed = 0;
		for (Visit v : activeVisits) {
			if (visitTypesToClose.contains(v.getVisitType())) {
				v.setStopDatetime(toStop);
				vs.saveVisit(v);
				numClosed += 1;
			}
		}
		String msg = "Closed " + numClosed + " visit";
		if (numClosed > 1)
			msg += "s";
		return new SuccessResult(msg);
	}
	
	public SimpleObject createPatient(UiUtils ui,
	                                  @MethodParam("createPatientCommand") @BindParams("patient") @Validate(PatientValidator.class) Patient pat) {
		ui.validate(pat, new CreatePatientValidator(), "patient");
		Context.getPatientService().savePatient(pat);
		return SimpleObject.create("patientId", pat.getPatientId());
	}
	
	public Patient createPatientCommand(@RequestParam(required=false, value="birthdate") Date birthdate,
	                                    @RequestParam(required=false, value="age") Integer age,
	                                    HttpServletRequest req,
	                                    Session session) {
		Patient pat = new Patient();
		pat.addName(new PersonName()); // will be bound by create patient fragment action
		
		if (birthdate != null) {
			pat.setBirthdate(birthdate);
		} else if (age != null) {
			pat.setBirthdateFromAge(age, new Date());
		}
			
		for (PatientIdentifierType pit : Context.getPatientService().getAllPatientIdentifierTypes()) {
			String identifier = req.getParameter("identifier." + pit.getId());
			if (StringUtils.isNotBlank(identifier)) {
				pat.addIdentifier(new PatientIdentifier(identifier, pit, getCurrentLocation(session)));
			}
		}
		if (pat.getIdentifiers().size() > 0)
			pat.getIdentifiers().iterator().next().setPreferred(true);
		
		return pat;
	}
	

	/**
	 * For some reason PatientValidator is letting me create a patient without a birthdate/age, so
	 * I'm going to write a stricter validator here
	 */
	public class CreatePatientValidator implements Validator {

	   /**
	     * @see org.springframework.validation.Validator#supports(java.lang.Class)
	     */
	    @Override
	    public boolean supports(Class<?> clazz) {
	        return clazz.equals(Patient.class);
	    }
	    
	    /**
	    * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	    */
	    @Override
	    public void validate(Object target, Errors errors) {
	    	ValidateUtil.validate(target, errors);
	    	ValidationUtils.rejectIfEmpty(errors, "birthdate", "error.null");
	    }
   }


	/**
	 * Creates a new visit
	 * 
	 * @param ui
	 * @param visit
	 * @return
	 */
	public Object startVisit(UiUtils ui,
	                         Session session,
	                         @BindParams("visit") @Validate Visit visit) {
		if (visit.getLocation() == null)
			visit.setLocation(getCurrentLocation(session));
		Visit saved = Context.getVisitService().saveVisit(visit);
		return simpleVisit(ui, saved);
	}
	
	/**
	 * Edits an existing visit
	 * 
	 * @param ui
	 * @param visit
	 * @return
	 */
	public Object editVisit(UiUtils ui,
	                        @RequestParam("visit.visitId") @BindParams("visit") @Validate Visit visit) {
		Visit saved = Context.getVisitService().saveVisit(visit);
		return simpleVisit(ui, saved);
	}
	
	/**
     * Simplifies a visit so it can be sent to the client via json
     * 
     * @param visit
     * @return
     */
    private SimpleObject simpleVisit(UiUtils ui, Visit visit) {
    	return SimpleObject.fromObject(visit, ui, "visitId", "visitType", "startDatetime", "stopDatetime");
    }

    public static Location getCurrentLocation(Session session) {
    	Location loc = Context.getLocationService().getLocation(1); // TODO fix this
    	if (loc == null)
    		throw new RuntimeException("Error in temp hack: no location with id=1");
    	return loc;
    }
}
