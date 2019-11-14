/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.otz;

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.Program;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.Visit;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;


import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Calculates whether patients are eligible for the OTZ program
 */
public class EligibleForOtzProgramCalculation extends AbstractPatientCalculation {
	static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");


	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(Collection, Map, PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
		PatientService patientService = Context.getPatientService();
		VisitService visitService = Context.getVisitService();
		CalculationResultMap ret = new CalculationResultMap();
		Set<Integer> alive = Filters.alive(cohort, context);
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Set<Integer> inOtzProgram = Filters.inProgram(hivProgram, alive, context);



		for (int ptId : cohort) {
			Patient patient = patientService.getPatient(ptId);
			Date currentDate = new Date();
			boolean onOtz = false;

			List<Visit> activeVisit = visitService.getActiveVisitsByPatient(patient);
			if (activeVisit.size() > 0) {
				for (Visit v : activeVisit) {
					if (!DATE_FORMAT.format(v.getStartDatetime()).equalsIgnoreCase(DATE_FORMAT.format(currentDate))) {
						if(inOtzProgram.contains(ptId) && patient.getAge() >= 10) {
							onOtz = true;
						}

					}else {
						if(inOtzProgram.contains(ptId) && patient.getAge() >= 10 && patient.getAge() <= 19) {
							onOtz = true;
						}

					}

				}
			}


			ret.put(ptId, new BooleanResult(onOtz, this));
		}

		return ret;
	}

}