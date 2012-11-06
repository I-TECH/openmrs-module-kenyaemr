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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageModel;


/**
 * This code is quite old-school, copied over from a proof-of-concept in the original ui2 work
 */
public class ObsTableByDateFragmentController {

	public void controller(PageModel pageModel, FragmentModel model, FragmentConfiguration config) {
		Patient patient = (Patient) pageModel.getAttribute("patient");
		List<?> conceptConfig = (List<?>) config.getAttribute("concepts");
		if (conceptConfig == null)
			throw new IllegalArgumentException("concepts is required");
		List<Concept> concepts = getConcepts(conceptConfig);
		model.addAttribute("concepts", concepts);
		model.addAttribute("data", getObsAsTable(patient, concepts));
	}
	
	private List<Concept> getConcepts(List<?> conceptConfig) {
		List<Concept> concepts = new ArrayList<Concept>();
		for (Object o : conceptConfig) {
			if (o instanceof Concept) {
				concepts.add((Concept) o);
			} else {
				Concept c = Context.getConceptService().getConcept(Integer.valueOf(o.toString()));
				concepts.add(c);
			}
		}
		return concepts;
	}
	
	private SortedMap<Date, Map<Concept, Obs>> getObsAsTable(Person person, List<Concept> concepts) {
		SortedMap<Date, Map<Concept, Obs>> byDate = new TreeMap<Date, Map<Concept, Obs>>(new Comparator<Date>() {
			
			@Override
			public int compare(Date left, Date right) {
				return right.compareTo(left);
			}
		});
		for (Concept c : concepts)
			helper(byDate, person, c);
		return byDate;
	}
	
	private void helper(SortedMap<Date, Map<Concept, Obs>> byDate, Person p, Concept c) {
		List<Obs> obs = Context.getObsService().getObservationsByPersonAndConcept(p, c);
		for (Obs o : obs) {
			Map<Concept, Obs> onDate = byDate.get(o.getObsDatetime());
			if (onDate == null) {
				onDate = new HashMap<Concept, Obs>();
				byDate.put(o.getObsDatetime(), onDate);
			}
			onDate.put(c, o);
		}
	}

}
