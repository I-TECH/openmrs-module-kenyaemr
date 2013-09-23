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
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether a patien't partner HIV status is unknown. Calculation returns true if patient
 * is alive, enrolled in the MCH program and her partner's HIV status is indicated as unknown.
 */
public class UnKnownPartnerHivStatusCalculation extends BaseEmrCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program mchmsProgram = MetadataUtils.getProgram(Metadata.Program.MCHMS);

		Set<Integer> alive = alivePatients(cohort, context);
		Set<Integer> inMchmsProgram = CalculationUtils.patientsThatPass(Calculations.activeEnrollment(mchmsProgram, alive, context));

		CalculationResultMap partnerHivStatusObs = Calculations.lastObs(getConcept(Dictionary.PARTNER_HIV_STATUS), inMchmsProgram, context);
		Concept unknownConcept = Dictionary.getConcept(Dictionary.UNKNOWN);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			// Is patient alive and in MCH program?
			boolean partnerHivStatusUnknown = false;
			if (inMchmsProgram.contains(ptId)) {
				Concept partnerHivStatus = EmrCalculationUtils.codedObsResultForPatient(partnerHivStatusObs, ptId);
				if (partnerHivStatus != null) {
					partnerHivStatusUnknown = partnerHivStatus.equals(unknownConcept);
				}
			}
			ret.put(ptId, new BooleanResult(partnerHivStatusUnknown, this, context));
		}
		return ret;
	}
}
