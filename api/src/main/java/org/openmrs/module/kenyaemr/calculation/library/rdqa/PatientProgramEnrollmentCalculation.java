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

		for (Integer ptid : cohort) {
			List<PatientProgram> programs = service.getPatientPrograms(patientService.getPatient(ptid), null, null, null, null,null, false);
			ret.put(ptid, new SimpleResult(programs, this));
		}

		return ret;
	}

}
