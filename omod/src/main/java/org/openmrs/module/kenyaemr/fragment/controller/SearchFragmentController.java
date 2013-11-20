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

import net.sf.cglib.core.CollectionUtils;
import net.sf.cglib.core.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptSearchResult;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.CoreConstants;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.PersonByNameComparator;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
	 * @param which all|checked-in
	 * @param ui the UI utils
	 * @return the simple patients
	 */
	public List<SimpleObject> patients(@RequestParam(value = "q", required = false) String query,
									   @RequestParam(value = "which", required = false) String which,
									   UiUtils ui) {

		// Return empty list if we don't have enough input to search on
		if (StringUtils.isBlank(query) && "all".equals(which)) {
			return Collections.emptyList();
		}

		boolean onlyCheckedIn = "checked-in".equals(which);

		// Run main patient search query based on id/name
		List<Patient> matchedByNameOrID = Context.getPatientService().getPatients(query);

		// Gather up active visits for all patients
		List<Visit> activeVisits = Context.getVisitService().getVisits(null, null, null, null, null, null, null, null, null, false, false);
		Map<Patient, Visit> patientActiveVisits = new HashMap<Patient, Visit>();
		for (Visit v : activeVisits) {
			patientActiveVisits.put(v.getPatient(), v);
		}

		Set<Patient> checkedIn = patientActiveVisits.keySet();

		List<Patient> matched;

		// If query wasn't long enough to be searched on, just use list of checked-in patients
		if (query.length() < getMinSearchCharacters()) {
			matched = new ArrayList<Patient>(checkedIn);

			// List needs sorted by person name
			Collections.sort(matched, new PersonByNameComparator());
		}
		// If it was then combine returned results with checked-in set
		else {
			matched = new ArrayList<Patient>();
			for (Patient patient : matchedByNameOrID) {
				if (!onlyCheckedIn || checkedIn.contains(patient)) {
					matched.add(patient);
				}
			}
		}

		// Simplify and attach active visits to patient objects
		List<SimpleObject> simplePatients = new ArrayList<SimpleObject>();
		for (Patient patient : matched) {
			SimpleObject simplePatient = ui.simplifyObject(patient);

			Visit activeVisit = patientActiveVisits.get(patient);
			if (activeVisit != null) {
				simplePatient.put("extra", "<div class='ke-tag ke-visittag'>" + ui.format(activeVisit.getVisitType()) + "<br/><small>" + ui.format(activeVisit.getStartDatetime()) + "</small></div>");
			}

			simplePatients.add(simplePatient);
		}

		return simplePatients;
	}

	/**
	 * Gets a person by their id
	 * @param person the person
	 * @param ui the UI utils
	 * @return the simplified person
	 */
	public SimpleObject person(@RequestParam("id") Person person, UiUtils ui) {
		return ui.simplifyObject(person);
	}

	/**
	 * Searches for persons by name
	 * @param query the name
	 * @param which all|non-patients
	 * @param ui the UI utils
	 * @return the simple patients
	 */
	public SimpleObject[] persons(@RequestParam(value = "q", required = false) String query,
								  @RequestParam(value = "which", required = false) String which,
								  UiUtils ui) {

		Collection<Person> results = Context.getPersonService().getPeople(query, null);

		if ("non-patients".equals(which)) {
			results = CollectionUtils.filter(results, new Predicate() {
				@Override
				public boolean evaluate(Object obj) {
					return !((Person) obj).isPatient();
				}
			});
		}

		// Convert to simple objects
		return ui.simplifyCollection(results);

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
	 * @return the simplified locations
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

	/**
	 * Searches for accounts by name
	 * @param query the name query
	 * @param which users|providers|both
	 * @param ui
	 * @return
	 */
	public List<SimpleObject> accounts(@RequestParam(value = "q", required = false) String query,
									   @RequestParam(value = "which", required = false) String which,
									   UiUtils ui) {

		Map<Person, User> userAccounts = new HashMap<Person, User>();
		Map<Person, Provider> providerAccounts = new HashMap<Person, Provider>();

		if ("both".equals(which) || "users".equals(which)) {
			List<User> users = Context.getUserService().getUsers(query, null, true);
			for (User u : users) {
				if (!"daemon".equals(u.getUsername())) {
					userAccounts.put(u.getPerson(), u);
				}
			}
		}

		if ("both".equals(which) || "providers".equals(which)) {
			List<Provider> providers = Context.getProviderService().getProviders(query, null, null, null);
			for (Provider p : providers) {
				if (p.getPerson() != null) {
					providerAccounts.put(p.getPerson(), p);
				}
			}
		}

		Set<Person> persons = new TreeSet<Person>(new PersonByNameComparator());
		persons.addAll(userAccounts.keySet());
		persons.addAll(providerAccounts.keySet());

		List<SimpleObject> ret = new ArrayList<SimpleObject>();
		for (Person p : persons) {
			SimpleObject account = ui.simplifyObject(p);

			User user = userAccounts.get(p);
			if (user != null) {
				account.put("user", SimpleObject.fromObject(user, ui, "username"));
			}

			Provider provider = providerAccounts.get(p);
			if (provider != null) {
				account.put("provider", SimpleObject.fromObject(provider, ui, "identifier"));
			}

			ret.add(account);
		}

		return ret;
	}

	/**
	 * Gets a concept by it's id
	 * @param concept the concept
	 * @param ui the UI utils
	 * @return the simplified concept
	 */
	public SimpleObject concept(@RequestParam("id") Concept concept, UiUtils ui) {
		return ui.simplifyObject(concept);
	}

	/**
	 * Searches for concept by name and class
	 * @param query the name query
	 * @param conceptClass the concept class
	 * @param ui the UI utils
	 * @return the simplified concepts
	 */
	public List<SimpleObject> concepts(@RequestParam(value = "q", required = false) String query,
									   @RequestParam(value = "class", required = false) ConceptClass conceptClass,
									   @RequestParam(value = "answerTo", required = false) Concept answerTo,
									   @RequestParam(value = "size", required = false) Integer size,
									   UiUtils ui) {

		ConceptService conceptService = Context.getConceptService();

		List<ConceptClass> conceptClasses = conceptClass != null ? Collections.singletonList(conceptClass) : null;

		List<ConceptSearchResult> results = conceptService.getConcepts(query, Collections.singletonList(CoreConstants.LOCALE), false,
				conceptClasses, null, null, null, answerTo, 0, size);

		// Simplify results
		List<SimpleObject> simpleConcepts = new ArrayList<SimpleObject>();
		for (ConceptSearchResult result : results) {
			simpleConcepts.add(ui.simplifyObject(result.getConcept()));
		}

		return simpleConcepts;
	}

	/**
	 * Gets the minimum number of query characters required for a service search method
	 * @return the value of min search characters
	 */
	protected static int getMinSearchCharacters() {
		int minSearchCharacters = OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_MIN_SEARCH_CHARACTERS;
		String minSearchCharactersStr = Context.getAdministrationService().getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_MIN_SEARCH_CHARACTERS);

		try {
			minSearchCharacters = Integer.valueOf(minSearchCharactersStr);
		}
		catch (NumberFormatException e) {
			//do nothing
		}
		return minSearchCharacters;
	}
}