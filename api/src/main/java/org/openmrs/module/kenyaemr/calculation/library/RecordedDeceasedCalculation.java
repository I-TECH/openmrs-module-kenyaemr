/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculates patients who have been recorded as deceased in a program, i.e. they have an exit reason
 * obs with value = deceased, but the system sees them as alive because their registration hasn't been updated
 */
public class RecordedDeceasedCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		Set<Integer> alive = Filters.alive(cohort, context);
		CalculationResultMap exitObss = Calculations.allObs(Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION), alive, context);
		Concept died = Dictionary.getConcept(Dictionary.DIED);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean recordedAsDead = false;

			ListResult exitObssForPt = (ListResult) exitObss.get(ptId);
			if (exitObssForPt != null) {
				List<Obs> exitObsList = CalculationUtils.extractResultValues(exitObssForPt);
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