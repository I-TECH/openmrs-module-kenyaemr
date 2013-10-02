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
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
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

		Program mchmsProgram = MetadataUtils.getProgram(MchMetadata._Program.MCHMS);

		Set<Integer> alive = alivePatients(cohort, context);
		Set<Integer> inMchmsProgram = CalculationUtils.patientsThatPass(Calculations.activeEnrollment(mchmsProgram, alive, context));

		CalculationResultMap ret = new CalculationResultMap();
		CalculationResultMap crm = Calculations.lastEncounter(MetadataUtils.getEncounterType(MchMetadata._EncounterType.MCHMS_ENROLLMENT), cohort, context);
		for (Integer ptId : cohort) {
			// Is patient alive and in MCH program?
			boolean lateEnrollment = false;
			if (inMchmsProgram.contains(ptId)) {
				lateEnrollment = gestationAtEnrollmentWasGreaterThan28Weeks(ptId, crm);
			}
			ret.put(ptId, new BooleanResult(lateEnrollment, this, context));
		}
		return ret;
	}

	/**
	 * @return true if the given patient's gestation at enrollment was greater than 28 weeks at enrollment and false
	 * otherwise.
	 * */
	protected boolean gestationAtEnrollmentWasGreaterThan28Weeks(Integer patientId, CalculationResultMap crm) {
		Encounter lastMchEnrollment = (Encounter) crm.get(patientId).getValue();
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
