/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.form;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaui.annotation.SharedAction;
import org.openmrs.ui.framework.SimpleObject;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Utility actions for forms
 */
public class FormUtilsFragmentController {

	/**
	 * Deletes (i.e. voids) the specified encounter
	 * @param encounter the encounter
	 * @return simple object { encounterId }
	 */
	@SharedAction
	public SimpleObject deleteEncounter(@RequestParam("encounterId") Encounter encounter) {
		Context.getEncounterService().voidEncounter(encounter, "KenyaEMR");
		return SimpleObject.create("encounterId", encounter.getEncounterId());
	}
}