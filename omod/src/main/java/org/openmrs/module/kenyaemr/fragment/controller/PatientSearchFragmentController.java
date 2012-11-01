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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Handles AJAX patient searches
 */
public class PatientSearchFragmentController {
	
	public void controller() {
	}
	
	public List<SimpleObject> search(@RequestParam(value="q", required=false) String query,
									 @RequestParam(value="which", required=false) String which,
									 @RequestParam(value="age", required=false) Integer age,
									 @RequestParam(value="ageWindow", defaultValue="5") int ageWindow,
									 UiUtils ui) {
		if ("checked-in".equals(which)) {
			return withActiveVisits(query, age, ageWindow, ui);
		}
		if (StringUtils.isBlank(query)) {
			return Collections.emptyList();
		}
		
		List<Patient> ret = Context.getPatientService().getPatients(query);
		if (age != null) {
			List<Patient> similar = new ArrayList<Patient>();
			for (Patient p : ret) {
				if (Math.abs(p.getAge() - age) <= ageWindow)
					similar.add(p);
			}
			ret = similar;
		}
		
		List<Visit> activeVisits = Context.getVisitService().getVisits(null, null, null, null, null, null, null, null, null, false, false);
		final Map<String, Visit> ptIds = new HashMap<String, Visit>();
		for (Visit v : activeVisits) {
			ptIds.put(v.getPatient().getPatientId().toString(), v);
		}
		
		List<SimpleObject> matching = simplePatientList(ui, ret);

		for (SimpleObject so : matching) {
			Visit v = ptIds.get(so.get("patientId"));
			if (v != null) {
				String imgUrl = ui.resourceLink("kenyaemr", "images/visit.png");
				so.put("extra", "<img src=\"" + imgUrl + "\"/> <small>" + ui.format(v.getVisitType()) + "<br/>" + ui.format(v.getStartDatetime()) + "</small>");
			}
		}
		
		return matching;
	}
	
	public List<SimpleObject> withActiveVisits(@RequestParam(value = "q", required = false) String query,
	                                           @RequestParam(value = "age", required = false) Integer age,
	                                           @RequestParam(value = "ageWindow", defaultValue = "5") int ageWindow,
	                                           UiUtils ui) {
		
		// TODO refactor so it performs faster
		
		List<SimpleObject> matching = search(query, null, age, ageWindow, ui);			

		List<Visit> activeVisits = Context.getVisitService().getVisits(null, null, null, null, null, null, null, null, null, false, false);

		// no query, so we start with all patients with active visits
		if (matching == null || matching.size() == 0) {
			List<Patient> ret = new ArrayList<Patient>();
			for (Visit v : activeVisits) {
				if (!ret.contains(v.getPatient()))
					ret.add(v.getPatient());
			}
			matching = simplePatientList(ui, ret);
		}
		
		// intersect query with active visits
		Map<String, Visit> ptIds = new HashMap<String, Visit>();
		for (Visit v : activeVisits) {
			ptIds.put(v.getPatient().getPatientId().toString(), v);
		}
		for (Iterator<SimpleObject> i = matching.iterator(); i.hasNext(); ) {
			SimpleObject candidate = i.next();
			Visit v = ptIds.get(candidate.get("patientId"));
			if (v == null) {
				i.remove();
			} else {
				String imgUrl = ui.resourceLink("kenyaemr", "images/visit.png");
				candidate.put("extra", "<img src=\"" + imgUrl + "\"/> <small>" + ui.format(v.getVisitType()) + "<br/>" + ui.format(v.getStartDatetime()) + "</small>");
			}
		}
		return matching;
	}

	/**
     * Simplifies a list of patients so it can be sent to the client via json
     * 
     * @param ui
     * @param pts
     * @return
     */
    private List<SimpleObject> simplePatientList(UiUtils ui, List<Patient> pts) {
    	List<SimpleObject> ret = new ArrayList<SimpleObject>();
    	long now = System.currentTimeMillis();
    	for (Patient pt : pts) {
    		SimpleObject so = SimpleObject.fromObject(pt, ui, "patientId", "personName", "age", "birthdate", "gender", "activeIdentifiers.identifierType", "activeIdentifiers.identifier");
    		Period p = new Period(pt.getBirthdate().getTime(), now, PeriodType.yearMonthDay());
    		so.put("ageMonths", p.getMonths());
    		so.put("ageDays", p.getDays());
    		ret.add(so);
    	}
    	return ret;
    }

}
