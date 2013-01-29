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
import org.openmrs.module.kenyaemr.util.KenyaEmrUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.*;

/**
 * Controller for the obsGraphByDate fragment
 */
public class ObsGraphByDateFragmentController {

	public void controller(@FragmentParam("patient") Patient patient, @FragmentParam("concepts") List<?> conceptConfig, FragmentModel model) {
		if (conceptConfig.size() < 1)
			throw new IllegalArgumentException("Concept list must be non-empty");

		List<Concept> concepts = KenyaEmrUtils.fetchConcepts(conceptConfig);
		model.addAttribute("concepts", concepts);
		model.addAttribute("data", getObsAsSeries(patient, concepts));
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
