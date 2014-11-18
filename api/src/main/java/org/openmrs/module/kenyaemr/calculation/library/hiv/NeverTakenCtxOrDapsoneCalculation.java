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

package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether patients have taken CTX or Dapsone
 */
public class NeverTakenCtxOrDapsoneCalculation extends AbstractPatientCalculation {

	@SuppressWarnings("unchecked")
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);

		CalculationResultMap medOrdersObss = Calculations.allObs(Dictionary.getConcept(Dictionary.MEDICATION_ORDERS), cohort, context);

		CalculationResultMap ctxProphylaxisObss = Calculations.allObs(Dictionary.getConcept(Dictionary.COTRIMOXAZOLE_DISPENSED), cohort, context);

		Set<Integer> ltfu = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));

		// Get concepts...
		Concept yes = Dictionary.getConcept(Dictionary.YES);
		Concept dapsone = Dictionary.getConcept(Dictionary.DAPSONE);
		Concept ctx = Dictionary.getConcept(Dictionary.SULFAMETHOXAZOLE_TRIMETHOPRIM);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean neverTakenCtxOrDapsone = false;

			// Is patient alive and in the HIV program
			if (inHivProgram.contains(ptId)) {
				neverTakenCtxOrDapsone = true;

				// First look to see if they have an obs for taking as prophylaxis
				ListResult ctxProphylaxis = (ListResult) ctxProphylaxisObss.get(ptId);
				if (ctxProphylaxis != null) {
					List<Obs> ctxProphylaxisObsList = CalculationUtils.extractResultValues(ctxProphylaxis);
					for (Obs ctxProphylaxisObs : ctxProphylaxisObsList) {
						if (ctxProphylaxisObs.getValueCoded().equals(yes)) {
							neverTakenCtxOrDapsone = false;
							break;
						}
					}
				}

				// Failing that, look for a med order
				if (neverTakenCtxOrDapsone) {
					ListResult patientMedOrders = (ListResult) medOrdersObss.get(ptId);
					if (patientMedOrders != null) {
						// Look through list of medication order obs for any Dapsone or CTX
						List<Obs> medOrderObsList = CalculationUtils.extractResultValues(patientMedOrders);
						for (Obs medOrderObs : medOrderObsList) {
							if (medOrderObs.getValueCoded().equals(dapsone) || medOrderObs.getValueCoded().equals(ctx)) {
								neverTakenCtxOrDapsone = false;
								break;
							}
						}
					}
				}
			}

			if(ltfu.contains(ptId)){
				neverTakenCtxOrDapsone = false;
			}

			ret.put(ptId, new BooleanResult(neverTakenCtxOrDapsone, this));
		}
		return ret;
	}
}