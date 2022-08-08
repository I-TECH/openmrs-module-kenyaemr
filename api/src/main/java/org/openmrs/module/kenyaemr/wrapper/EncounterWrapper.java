/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.wrapper;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.Obs;
import org.openmrs.Provider;
import org.openmrs.module.kenyacore.wrapper.AbstractObjectWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

	/**
	 * Gets the provider of the encounter. OpenMRS supports a much more complex model of the encounter - provider
	 * relationship than we are interested in.
	 * @return the provider or null
	 */
	public Provider getProvider() {
		EncounterRole unknownRole = MetadataUtils.existing(EncounterRole.class, EncounterRole.UNKNOWN_ENCOUNTER_ROLE_UUID);
		Set<Provider> providers = target.getProvidersByRole(unknownRole);
		return providers.size() > 0 ? providers.iterator().next() : null;
	}

	/**
	 * Sets the provider of the encounter. OpenMRS supports a much more complex model of the encounter - provider
	 * relationship than we are interested in.
	 * @param provider the provider or null
	 */
	public void setProvider(Provider provider) {
		EncounterRole unknownRole = MetadataUtils.existing(EncounterRole.class, EncounterRole.UNKNOWN_ENCOUNTER_ROLE_UUID);
		if (provider != null) {
			target.setProvider(unknownRole, provider);
		}
		else {
			// Void all provider connections
			for (Provider p : target.getProvidersByRole(unknownRole)) {
				target.removeProvider(unknownRole, p);
			}
		}
	}
}