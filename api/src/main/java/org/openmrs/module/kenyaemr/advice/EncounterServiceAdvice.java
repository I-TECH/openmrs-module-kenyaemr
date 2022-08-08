/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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