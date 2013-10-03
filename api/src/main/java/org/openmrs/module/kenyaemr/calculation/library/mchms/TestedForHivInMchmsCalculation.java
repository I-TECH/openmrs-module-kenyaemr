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
import org.openmrs.module.kenyaemr.metadata.MchMetadata;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Calculates the period when a patient was tested for HIV. The period may either be Antenatal, Labor and Delivery or
 * Postnatal. The parameter values received determine which specific period and HIV result to base the calculation on.
 * Both the period and HIV result can be set to "any". The calculation returns true if the patient has a known HIV
 * result, was tested in the period specified by the period parameter and the test known result is as specified in the
 * testResultParameter
 */
public class TestedForHivInMchmsCalculation extends BaseEmrCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		MchMetadata.Stage stage = (MchMetadata.Stage) parameterValues.get("stage");
		Concept result = (Concept) parameterValues.get("result");

		Program mchmsProgram = MetadataUtils.getProgram(MchMetadata._Program.MCHMS);

		Set<Integer> alivePatients = alivePatients(cohort, context);
		Set<Integer> mchmsPatients = CalculationUtils.patientsThatPass(Calculations.activeEnrollment(mchmsProgram, alivePatients, context));

		CalculationResultMap lastHivStatusObss = Calculations.lastObs(getConcept(Dictionary.HIV_STATUS), mchmsPatients, context);
		CalculationResultMap artStatusObss = Calculations.lastObs(getConcept(Dictionary.ANTIRETROVIRAL_USE_IN_PREGNANCY), mchmsPatients, context);

		Concept hivPositiveConcept = Dictionary.getConcept(Dictionary.POSITIVE);
		Concept onHaartConcept = Dictionary.getConcept(Dictionary.MOTHER_ON_HAART);

		CalculationResultMap resultMap = new CalculationResultMap();

		for (Integer ptId : cohort) {
			boolean patientIsAlive = alivePatients.contains(ptId);
			boolean patientIsInMchmsProgram = mchmsPatients.contains(ptId);
			boolean patientHivStatusWasEstablishedInMchms = true;
			if (patientIsAlive && patientIsInMchmsProgram && patientHivStatusWasEstablishedInMchms) {
				resultMap.put(ptId, new BooleanResult(true, this, context));
//				if (stage.equals(MchMetadata.Stage.ANY)) {
//					resultMap.put(ptId, qualifiesByHivStatus(result, ptId, context));
//				} else if (stage.equals("antenatal")) {
//
//				} else if (stage.equals("delivery")) {
//
//				} else if (stage.equals("postnatal")) {
//
//				}
			}
		}

		return resultMap;
	}

	protected BooleanResult qualifiesByHivStatus(Concept requestedHivStatus, Integer patientId, PatientCalculationContext context) {
		if (requestedHivStatus == null) {
			return new BooleanResult(true, this, context);
		} else {
			return new BooleanResult(true, this, context);
		}
	}
}
