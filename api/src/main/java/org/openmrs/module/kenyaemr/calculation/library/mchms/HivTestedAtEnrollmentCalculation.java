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

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
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
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.util.EmrUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether a mother is HIV+ but is not on ART. Calculation returns true if mother
 * is alive, enrolled in the MCH program, gestation is greater than 14 weeks, is HIV+ and was
 * not indicated as being on ART in the last encounter.
 */
public class HivTestedAtEnrollmentCalculation extends BaseEmrCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program mchmsProgram = MetadataUtils.getProgram(Metadata.Program.MCHMS);

		Set<Integer> alive = alivePatients(cohort, context);
		Set<Integer> inMchmsProgram = CalculationUtils.patientsThatPass(Calculations.activeEnrollment(mchmsProgram, alive, context));

		CalculationResultMap hivStatusObs = Calculations.lastObs(getConcept(Dictionary.HIV_STATUS), inMchmsProgram, context);
		CalculationResultMap hivTestDateObs = Calculations.lastObs(getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS), inMchmsProgram, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean hivTestedAtEnrollment = false;

			// Is patient alive and in MCH program?
			if (inMchmsProgram.contains(ptId)) {
				Concept hivStatus = EmrCalculationUtils.codedObsResultForPatient(hivStatusObs, ptId);
				Date hivTestDate = EmrCalculationUtils.datetimeObsResultForPatient(hivTestDateObs, ptId);
				if (hivStatus != null && !hivStatus.equals(Dictionary.getConcept(Dictionary.NOT_HIV_TESTED))) {
					if (hivTestDate != null) {
						Date enrollmentDate = getLatestMchmsEnrollmentDate(ptId);
						hivTestedAtEnrollment = (hivTestDate.before(enrollmentDate)
								|| hivTestDate.equals(enrollmentDate));
					}
				}
			}
			ret.put(ptId, new BooleanResult(hivTestedAtEnrollment, this, context));
		}
		return ret;
	}

	private Date getLatestMchmsEnrollmentDate(Integer patientId) {
		Patient patient = Context.getPatientService().getPatient(patientId);
		EncounterService encounterService = Context.getEncounterService();
		EncounterType encounterType = encounterService.getEncounterTypeByUuid(Metadata.EncounterType.MCHMS_ENROLLMENT);
		Encounter lastMchEnrollment = EmrUtils.lastEncounter(patient, encounterType);
		return  lastMchEnrollment.getDateCreated();
	}
}
