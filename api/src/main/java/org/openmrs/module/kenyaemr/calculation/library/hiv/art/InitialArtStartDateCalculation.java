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

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.util.EncounterBasedRegimenUtils;
import org.openmrs.ui.framework.SimpleObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculates the date on which a patient first started ART
 */
public class InitialArtStartDateCalculation extends BaseEmrCalculation {
	static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @should return null for patients who have not started ART
	 * @should return start date for patients who have started ART
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
	                                     PatientCalculationContext context) {

		Date startDate = null;
		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			Encounter lastDrugRegimenEditorEncounter = EncounterBasedRegimenUtils.getLastEncounterForCategory(Context.getPatientService().getPatient(ptId), "ARV");   //last DRUG_REGIMEN_EDITOR encounter

			if (lastDrugRegimenEditorEncounter != null) {
				SimpleObject o = EncounterBasedRegimenUtils.buildRegimenChangeObject(lastDrugRegimenEditorEncounter.getAllObs(), lastDrugRegimenEditorEncounter);
				try {
					startDate = DATE_FORMAT.parse(o.get("startDate").toString());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (startDate != null) {
					ret.put(ptId, new SimpleResult(startDate, this, context));
				} else {
					ret.put(ptId, null);
				}
			}
		}
		return ret;
	}
}