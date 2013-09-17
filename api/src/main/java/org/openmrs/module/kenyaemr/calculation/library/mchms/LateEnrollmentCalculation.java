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

package org.openmrs.module.kenyaemr.calculation.library.mchms;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.util.EmrUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether a mother enrolled into the program at gestation greater than 28 weeks.
 * Calculation returns true if mother is alive, enrolled in the MCH program and had gestation
 * greater than 28 weeks at enrollment.
 */
public class LateEnrollmentCalculation extends BaseEmrCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program mchmsProgram = MetadataUtils.getProgram(Metadata.Program.MCHMS);

		Set<Integer> alive = alivePatients(cohort, context);
		Set<Integer> inMchmsProgram = CalculationUtils.patientsThatPass(Calculations.activeEnrollment(mchmsProgram, alive, context));

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean lateEnrollment = false;

			// Is patient alive and in MCH program?
			if (inMchmsProgram.contains(ptId)) {
				lateEnrollment = gestationAtEnrollmentWasGreaterThan28Weeks(ptId);
			}
			ret.put(ptId, new BooleanResult(lateEnrollment, this, context));
		}
		return ret;
	}

	private boolean gestationAtEnrollmentWasGreaterThan28Weeks(Integer patientId) {
		Patient patient = Context.getPatientService().getPatient(patientId);
		EncounterService encounterService = Context.getEncounterService();
		EncounterType encounterType = encounterService.getEncounterTypeByUuid(Metadata.EncounterType.MCHMS_ENROLLMENT);
		Encounter lastMchEnrollment = EmrUtils.lastEncounter(patient, encounterType);
		Obs lmpObs = EmrUtils.firstObsInEncounter(lastMchEnrollment, Dictionary.getConcept(Dictionary.LAST_MONTHLY_PERIOD));
		if (lmpObs != null) {
			Weeks weeks = Weeks.weeksBetween(new DateTime(lmpObs.getValueDate()), new DateTime(lastMchEnrollment.getDateCreated()));
			if (weeks.getWeeks() > 28) {
				return true;
			}
		}
		return false;
	}
}