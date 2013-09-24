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

import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * Calculates whether a patient was a TB patient on the date they started ARTs
 */
public class TbPatientAtArtStartCalculation extends BaseEmrCalculation {
	
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program tbProgram = MetadataUtils.getProgram(TbMetadata.Program.TB);
		CalculationResultMap artStartDates = calculate(new InitialArtStartDateCalculation(), cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean result = false;
			Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(artStartDates, ptId);

			if (artStartDate != null) {
				CalculationResultMap enrollment = Calculations.activeEnrollmentOn(tbProgram, artStartDate, Collections.singleton(ptId), context);

				if (enrollment.get(ptId) != null) {
					result = true;
				}
			}

			ret.put(ptId, new BooleanResult(result, this));
		}
		return ret;
	}
}