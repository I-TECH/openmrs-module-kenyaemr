/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.ovc;

import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether patients are eligible for the OTZ program
 */
public class EligibleForOvcProgramCalculation extends AbstractPatientCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(Collection, Map, PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
		PatientService patientService = Context.getPatientService();
		CalculationResultMap ret = new CalculationResultMap();
		Set<Integer> alive = Filters.alive(cohort, context);
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Set<Integer> inOvcProgram = Filters.inProgram(hivProgram, alive, context);



		for (int ptId : cohort) {
			Patient patient = patientService.getPatient(ptId);
			boolean onOvc = false;
			if(inOvcProgram.contains(ptId) && patient.getAge() <= 17) {
				onOvc = true;
			}


			ret.put(ptId, new BooleanResult(onOvc, this));
		}

		return ret;
	}

}