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

package org.openmrs.module.kenyaemr.wrapper;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.module.kenyacore.wrapper.AbstractObjectWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class for encounters
 */
public class EncounterWrapper extends AbstractObjectWrapper<Encounter> {

	/**
	 * Creates a encounter wrapper
	 * @param target the encounter
	 */
	public EncounterWrapper(Encounter target) {
		super(target);
	}

	/**
	 * Finds the first obs in the encounter with the given concept
	 * @param concept the obs concept
	 * @return the obs
	 */
	public Obs firstObs(Concept concept) {
		for (Obs obs : target.getAllObs()) {
			if (obs.getConcept().equals(concept)) {
				return obs;
			}
		}
		return null;
	}

	/**
	 * Finds all obs in the encounter with the given concept
	 * @param concept the obs concept
	 * @return the obs list
	 */
	public List<Obs> allObs(Concept concept) {
		List<Obs> obsList = new ArrayList<Obs>();
		for (Obs obs : target.getAllObs()) {
			if (obs.getConcept().equals(concept)) {
				obsList.add(obs);
			}
		}
		return obsList;
	}
}