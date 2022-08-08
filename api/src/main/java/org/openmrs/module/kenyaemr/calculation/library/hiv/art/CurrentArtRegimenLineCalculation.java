/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.util.EncounterBasedRegimenUtils;
import org.openmrs.ui.framework.SimpleObject;

import java.util.Collection;
import java.util.Map;

/**
 * Calculates the current ART regimen Line of each patient.
 */
public class CurrentArtRegimenLineCalculation extends BaseEmrCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(Collection,
	 * Map, PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
										 PatientCalculationContext context) {

		String regimenLine = null;
		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			Encounter lastDrugRegimenEditorEncounter = EncounterBasedRegimenUtils.getLastEncounterForCategory(Context.getPatientService().getPatient(ptId), "ARV");   //last DRUG_REGIMEN_EDITOR encounter

			if (lastDrugRegimenEditorEncounter != null) {
				SimpleObject o = EncounterBasedRegimenUtils.buildRegimenChangeObject(lastDrugRegimenEditorEncounter.getAllObs(), lastDrugRegimenEditorEncounter);
				regimenLine = o.get("regimenLine").toString();

				if (regimenLine != null) {
					ret.put(ptId, new SimpleResult(regimenLine, this, context));
				} else {
					ret.put(ptId, null);
				}
			}
		}
		return ret;
	}
}