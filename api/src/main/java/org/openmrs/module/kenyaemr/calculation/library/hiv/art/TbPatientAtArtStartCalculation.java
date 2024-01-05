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

import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

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