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