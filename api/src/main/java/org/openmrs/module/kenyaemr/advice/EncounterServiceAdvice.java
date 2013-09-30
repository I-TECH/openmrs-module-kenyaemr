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

package org.openmrs.module.kenyaemr.advice;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.api.handler.EncounterVisitHandler;
import org.openmrs.module.kenyaemr.visit.ExistingEncounterVisitHandler;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 *
 */
public class EncounterServiceAdvice implements MethodBeforeAdvice {

	/**
	 * @see MethodBeforeAdvice#before(java.lang.reflect.Method, Object[], Object)
	 */
	@Override
	public void before(Method method, Object[] args, Object o) throws Throwable {
		if (method.getName().equals("saveEncounter")) {
			Encounter encounter = (Encounter) args[0];
			beforeSaveEncounter(encounter);
		}
	}

	/**
	 * Invoked before any call to save encounter
	 * @param encounter the encounter
	 */
	protected void beforeSaveEncounter(Encounter encounter) {

		// If new encounter, EncounterServiceImpl will invoke the visit handler. If not we invoke it ourselves here so
		// it is always called regardless
		if (encounter.getEncounterId() != null) {

			EncounterVisitHandler visitHandler = Context.getEncounterService().getActiveEncounterVisitHandler();
			if (visitHandler != null && visitHandler instanceof ExistingEncounterVisitHandler) {
				((ExistingEncounterVisitHandler) visitHandler).beforeEditEncounter(encounter);

				// Does the new visit new persisted?
				if (encounter.getVisit() != null && encounter.getVisit().getVisitId() == null) {
					Context.getVisitService().saveVisit(encounter.getVisit());
				}
			}
		}
	}
}