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

import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Calculates whether a patient was a TB patient on the date they started ARTs
 */
public class TbPatientAtArtStartCalculation extends AbstractPatientCalculation {
	
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
		CalculationResultMap artStartDates = calculate(new InitialArtStartDateCalculation(), cohort, context);
		CalculationResultMap tbEnrollments = Calculations.allEnrollments(tbProgram, cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean result = false;
			Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(artStartDates, ptId);
			ListResult enrollmentsResult = (ListResult) tbEnrollments.get(ptId);

			if (artStartDate != null && enrollmentsResult != null && !enrollmentsResult.isEmpty()) {
				List<PatientProgram> enrollments = CalculationUtils.extractResultValues(enrollmentsResult);
				for (PatientProgram enrollment : enrollments) {
					if (enrollment.getActive(artStartDate)) {
						result = true;
						break;
					}
				}
			}

			ret.put(ptId, new BooleanResult(result, this));
		}
		return ret;
	}
}