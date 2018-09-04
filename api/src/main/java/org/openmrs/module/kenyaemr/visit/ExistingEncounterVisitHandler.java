/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.visit;

import org.openmrs.Encounter;

/**
 * We have a special requirement for certain encounters to be able to move between visits. The regular OpenMRS model
 * of encounter visit handlers doesn't allow for this as handlers are only invoked for new encounters. Thus we have this
 * additional interface which allows a visit handler to support existing encounters. AOP advice on the encounter service
 * will invoke this method on the visit handler.
 */
public interface ExistingEncounterVisitHandler {

	/**
	 * Called before an existing encounter is saved
	 * @param encounter the encounter
	 */
	public void beforeEditEncounter(Encounter encounter);
}