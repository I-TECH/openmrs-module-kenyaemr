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

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageModel;

import java.util.*;

/**
 * Controller for the obsGraphByDate fragment
 */
public class ObsGraphByDateFragmentController {

	public void controller(PageModel pageModel, FragmentModel model, FragmentConfiguration config) {
		Patient patient = (Patient) pageModel.getAttribute("patient");
		List<?> conceptConfig = (List<?>) config.getAttribute("concepts");

		if (conceptConfig == null)
			throw new IllegalArgumentException("concepts is required");
        else if (conceptConfig.size() < 1 || conceptConfig.size() > 2)
            throw new IllegalArgumentException("concepts must 1 or 2");

		List<Concept> concepts = getConcepts(conceptConfig);
		model.addAttribute("data", getObsAsSeries(patient, concepts));
	}

    /**
     * Gets a lsit of concepts from a list of concepts or concept identifiers
     * @param conceptConfig the list of concepts or concept identifiers
     * @return the list of concepts
     */
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

    /**
     * Loads the obs for each of the specified concepts for the given person
     * @param person the person
     * @param concepts the concepts
     * @return the map of concepts to lists of obs
     */
	private Map<Concept, List<Obs>> getObsAsSeries(Person person, List<Concept> concepts) {
        Map<Concept, List<Obs>> series = new HashMap<Concept, List<Obs>>();

        for (Concept concept : concepts) {
            List<Obs> obss = Context.getObsService().getObservationsByPersonAndConcept(person, concept);
            series.put(concept, obss);
        }
		return series;
	}
}
