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
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether a mother is on HAART. Calculation returns true if mother
 * is alive, enrolled in the MCH program, is HIV+ and indicated in the last MCH
 * encounter to be on HAART.
 */
public class OnHaartCalculation extends BaseEmrCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program mchmsProgram = MetadataUtils.getProgram(MchMetadata.Program.MCHMS);

		Set<Integer> alive = alivePatients(cohort, context);
		Set<Integer> inMchmsProgram = CalculationUtils.patientsThatPass(Calculations.activeEnrollment(mchmsProgram, alive, context));

		CalculationResultMap lastHivStatusObss = Calculations.lastObs(getConcept(Dictionary.HIV_STATUS), inMchmsProgram, context);
		CalculationResultMap artStatusObss = Calculations.lastObs(getConcept(Dictionary.ANTIRETROVIRAL_USE_IN_PREGNANCY), inMchmsProgram, context);

		Concept hivPositiveConcept = Dictionary.getConcept(Dictionary.POSITIVE);
		Concept onHaartConcept = Dictionary.getConcept(Dictionary.MOTHER_ON_HAART);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			// Is patient alive and in MCH program?
			boolean onHaart = false;
			if (inMchmsProgram.contains(ptId)) {
				Concept lastHivStatus = EmrCalculationUtils.codedObsResultForPatient(lastHivStatusObss, ptId);
				Concept lastArtStatus = EmrCalculationUtils.codedObsResultForPatient(artStatusObss, ptId);
				boolean hivPositive = false;
				if (lastHivStatus != null) {
					hivPositive = lastHivStatus.equals(hivPositiveConcept);
					if (lastArtStatus != null) {
						onHaart = lastArtStatus.equals(onHaartConcept);
					}
				}
				onHaart = hivPositive && onHaart;
			}
			ret.put(ptId, new BooleanResult(onHaart, this, context));
		}
		return ret;
	}
}
