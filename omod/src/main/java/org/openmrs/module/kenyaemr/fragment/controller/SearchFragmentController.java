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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

/**
 * Fragment actions specifically for searching for OpenMRS objects
 */
public class SearchFragmentController {

	protected static final Log log = LogFactory.getLog(SearchFragmentController.class);

	/**
	 * Gets a patient by their id
	 * @param patient the patient
	 * @param ui the UI utils
	 * @return the simplified location
	 */
	public SimpleObject patient(@RequestParam("id") Patient patient, UiUtils ui) {
		return ui.simplifyObject(patient);
	}

	/**
	 * Searches for patients by name, identifier, age, visit status
	 * @param query the name or identifier
	 * @param which "checked-in" to return only those patients with an active visit
	 * @param age
	 * @param ageWindow
	 * @param ui the UI utils
	 * @return the simple patients
	 */
	public List<SimpleObject> patients(@RequestParam(value = "q", required = false) String query,
									   @RequestParam(value = "which", required = false) String which,
									   @RequestParam(value = "age", required = false) Integer age,
									   @RequestParam(value = "ageWindow", defaultValue = "5") int ageWindow,
									   UiUtils ui) {

		if (StringUtils.isBlank(query)) {
			return Collections.emptyList();
		}

		boolean onlyCheckedIn = "checked-in".equals(which);

		// Run main patient search query based on id/name
		List<Patient> matchingPatients = Context.getPatientService().getPatients(query);

		// Augment age query
		if (age != null) {
			List<Patient> similar = new ArrayList<Patient>();
			for (Patient p : matchingPatients) {
				if (Math.abs(p.getAge() - age) <= ageWindow)
					similar.add(p);
			}
			matchingPatients = similar;
		}

		// Gather up active visits for all patients
		List<Visit> activeVisits = Context.getVisitService().getVisits(null, null, null, null, null, null, null, null, null, false, false);
		final Map<Integer, Visit> patientActiveVisits = new HashMap<Integer, Visit>();
		for (Visit v : activeVisits) {
			patientActiveVisits.put(v.getPatient().getPatientId(), v);
		}

		List<SimpleObject> simplePatients = new ArrayList<SimpleObject>();

		// Attach active visits to patient objects
		for (Patient patient : matchingPatients) {
			Visit activeVisit = patientActiveVisits.get(patient.getId());

			if (onlyCheckedIn && activeVisit == null) {
				continue;
			}

			SimpleObject simplePatient = ui.simplifyObject(patient);

			if (activeVisit != null) {
				simplePatient.put("extra", "<div class='ke-tag ke-visittag'>" + ui.format(activeVisit.getVisitType()) + "<br/><small>" + ui.format(activeVisit.getStartDatetime()) + "</small></div>");
			}

			simplePatients.add(simplePatient);
		}

		return simplePatients;
	}

	/**
	 * Gets a location by it's id
	 * @param location the location
	 * @param ui the UI utils
	 * @return the simplified location
	 */
	public SimpleObject location(@RequestParam("id") Location location, UiUtils ui) {
		return ui.simplifyObject(location);
	}

	/**
	 * Searches for locations by name or MFL code
	 * @param query the search query
	 * @param ui the UI utils
	 * @return the list of locations as simple objects
	 */
	public List<SimpleObject> locations(@RequestParam("q") String query, UiUtils ui) {
		LocationService svc = Context.getLocationService();

		// Results will be sorted by name
		Set<Location> results = new TreeSet<Location>(new Comparator<Location>() {
			@Override
			public int compare(Location location1, Location location2) {
			return location1.getName().compareTo(location2.getName());
			}
		});

		// If term looks like an MFL code, add location with that code
		if (StringUtils.isNumeric(query) && query.length() >= 5) {
			Location locationByMflCode = Context.getService(KenyaEmrService.class).getLocationByMflCode(query);
			if (locationByMflCode != null) {
				results.add(locationByMflCode);
			}
		}

		// Add first 20 results of search by name
		if (StringUtils.isNotBlank(query)) {
			results.addAll(svc.getLocations(query, true, 0, 20));
		}

		// Convert to simple objects
		List<SimpleObject> ret = new ArrayList<SimpleObject>();
		for (Location l : results) {
			ret.add(ui.simplifyObject(l));
		}
		return ret;
	}
}