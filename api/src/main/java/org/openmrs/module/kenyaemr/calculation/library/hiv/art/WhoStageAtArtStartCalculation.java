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
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.util.EmrUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Calculates a patient's WHO stage on the date they started ARTs
 */
public class WhoStageAtArtStartCalculation extends AbstractPatientCalculation {
	
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Concept currentWhoStage = Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE);
		CalculationResultMap artStartDates = calculate(new InitialArtStartDateCalculation(), cohort, context);
		CalculationResultMap whoStageObss = Calculations.allObs(currentWhoStage, cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			SimpleResult result = null;
			Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(artStartDates, ptId);
			ListResult whoStageObssResult = (ListResult) whoStageObss.get(ptId);

			if (artStartDate != null && whoStageObssResult != null && !whoStageObssResult.isEmpty()) {
				List<Obs> whoStages = CalculationUtils.extractResultValues(whoStageObssResult);
				Obs lastBeforeArtStart = EmrCalculationUtils.findLastOnOrBefore(whoStages, artStartDate);

				if (lastBeforeArtStart != null) {
					Integer whoStage = EmrUtils.whoStage(lastBeforeArtStart.getValueCoded());
					if (whoStage != null) {
						result = new SimpleResult(whoStage, this);
					}
				}
			}

			ret.put(ptId, result);
		}
		return ret;
	}
}