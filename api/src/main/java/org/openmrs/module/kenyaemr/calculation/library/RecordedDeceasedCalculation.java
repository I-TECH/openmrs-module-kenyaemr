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

package org.openmrs.module.kenyaemr.calculation.library;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculates patients who have been recorded as deceased in a program, i.e. they have an exit reason
 * obs with value = deceased, but the system sees them as alive because their registration hasn't been updated
 */
public class RecordedDeceasedCalculation extends BaseEmrCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		Set<Integer> alive = alivePatients(cohort, context);
		CalculationResultMap exitObss = allObs(Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION), alive, context);
		Concept died = Dictionary.getConcept(Dictionary.DIED);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean recordedAsDead = false;

			ListResult exitObssForPt = (ListResult) exitObss.get(ptId);
			if (exitObssForPt != null) {
				List<Obs> exitObsList = EmrCalculationUtils.extractListResultValues(exitObssForPt);
				for (Obs exitObs : exitObsList) {
					if (died.equals(exitObs.getValueCoded())) {
						recordedAsDead = true;
						break;
					}
				}
			}

			ret.put(ptId, new BooleanResult(recordedAsDead, this, context));

		}

		return ret;
	}
}