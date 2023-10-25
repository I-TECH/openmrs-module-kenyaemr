/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.rdqa;

import org.openmrs.PatientProgram;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Returns a list of all programs a patient is enrolled in plus date of enrollment.
 *
 */
public class PatientProgramEnrollmentCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		ProgramWorkflowService service = Context.getProgramWorkflowService();
		PatientService patientService = Context.getPatientService();

		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptId : cohort) {
			List<PatientProgram> programs = service.getPatientPrograms(patientService.getPatient(ptId), null, null, null, new Date(),null, false);
			ret.put(ptId, new SimpleResult(programs, this));
		}

		return ret;
	}

}
