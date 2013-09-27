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