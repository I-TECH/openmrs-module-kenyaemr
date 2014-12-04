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
package org.openmrs.module.kenyaemr.calculation.library.hiv.cqi;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Calculates patients with their last visit information
 */
public class PatientLastVisitCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId: cohort) {
			boolean hadIcfCardCompleted = false;
			List<Visit> visits = Context.getVisitService().getVisitsByPatient(Context.getPatientService().getPatient(ptId), true, true);
			 if(visits.size() > 0) {
				 Visit lastVisit = visits.get(0);
				 	for(Encounter encounter: lastVisit.getEncounters()) {
						if (encounter.getEncounterType().equals(MetadataUtils.existing(EncounterType.class, TbMetadata._EncounterType.TB_SCREENING))) {
							hadIcfCardCompleted = true;
						}
					}
			 }
			ret.put(ptId, new BooleanResult(hadIcfCardCompleted, this, context));
		}
		return ret;
	}
}
