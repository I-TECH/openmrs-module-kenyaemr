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

package org.openmrs.module.kenyaemr.calculation.library.mchcs;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Determines whether HEI exposed infants are enrolled on CTX
 */
public class InfantNeverTakenCTXCalculation extends BaseEmrCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program mchcsProgram = MetadataUtils.getProgram(MchMetadata._Program.MCHCS);

		Set<Integer> alive = alivePatients(cohort, context);
		Set<Integer> inMchcsProgram = CalculationUtils.patientsThatPass(Calculations.activeEnrollment(mchcsProgram, alive, context));

		// Get whether the child is HIV Exposed
		CalculationResultMap lastChildHivStatus = Calculations.lastObs(getConcept(Dictionary.CHILDS_CURRENT_HIV_STATUS), inMchcsProgram, context);
		CalculationResultMap medOrdersObss = Calculations.allObs(getConcept(Dictionary.MEDICATION_ORDERS), cohort, context);

		// Get concepts for  medication
		Concept ctx = getConcept(Dictionary.SULFAMETHOXAZOLE_TRIMETHOPRIM);
		Concept hivExposed = getConcept(Dictionary.EXPOSURE_TO_HIV);

		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptId : cohort) {
			boolean notTakingCtx = false;

			// Is patient alive and in the MCHCS program and HEI?
			Obs hivStatusObs = EmrCalculationUtils.obsResultForPatient(lastChildHivStatus, ptId);
			if (inMchcsProgram.contains(ptId) && lastChildHivStatus != null && hivStatusObs !=null && (hivStatusObs.getValueCoded().equals(hivExposed))) {
				notTakingCtx = true ;
				ListResult patientMedOrders = (ListResult) medOrdersObss.get(ptId);
				if (patientMedOrders != null) {
					// Look through list of medication order obs for any  CTX
					List<Obs> medOrderObsList = EmrCalculationUtils.extractListResultValues(patientMedOrders);
					for (Obs medOrderObs : medOrderObsList) {
						if (medOrderObs.getValueCoded().equals(ctx)) {
							notTakingCtx = false;
							break;
						}
					}
				}
			}
			ret.put(ptId, new BooleanResult(notTakingCtx, this, context));
		}

		return ret;
	}
}