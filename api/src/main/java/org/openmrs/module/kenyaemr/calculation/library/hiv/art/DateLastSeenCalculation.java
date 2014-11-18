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
package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculates the date a patients was seen last by a provider
 */
public class DateLastSeenCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
										 PatientCalculationContext context) {
		CalculationResultMap lastEncounter = Calculations.lastEncounter(MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_CONSULTATION), cohort, context);
		CalculationResultMap result = new CalculationResultMap();
		for (Integer ptId : cohort) {
			Encounter encounterInfo = EmrCalculationUtils.encounterResultForPatient(lastEncounter, ptId);
			Date dateLastSeen = null;
			if(encounterInfo != null){
				dateLastSeen = encounterInfo.getEncounterDatetime();
			}
			result.put(ptId, new SimpleResult(dateLastSeen, this));
		}
		return  result;
	}
}
