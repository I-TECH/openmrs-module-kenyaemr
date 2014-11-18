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
package org.openmrs.module.kenyaemr.calculation.library.tb;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LostToFollowUpCalculation;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculates patients who are hiv positive, have tb and on ctx
 */
public class OnCPTCalculation extends AbstractPatientCalculation {
	/**
	* @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	*/
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
		//only deal with the alive patients
		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inTbProgram = Filters.inProgram(MetadataUtils.existing(Program.class, TbMetadata._Program.TB), alive, context);
		//a list of lost to follow up patients
		Set<Integer> ltfu = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));
		//declare the concepts required to check whether a patient is hiv positive
		Concept hivStatus = Dictionary.getConcept(Dictionary.HIV_STATUS);
		Concept hivInfected = Dictionary.getConcept(Dictionary.HIV_INFECTED);
		Concept hivPositive = Dictionary.getConcept(Dictionary.POSITIVE);
		//get the several map representation of the results
		CalculationResultMap hivStatusMap = Calculations.lastObs(hivStatus, alive, context);
		//get maps for ctx
		CalculationResultMap medOrdersObss = Calculations.allObs(Dictionary.getConcept(Dictionary.MEDICATION_ORDERS), alive, context);
		CalculationResultMap ctxProphylaxisObss = Calculations.allObs(Dictionary.getConcept(Dictionary.COTRIMOXAZOLE_DISPENSED), alive, context);
		//concepts to allow manipulate teh ctx
		Concept yes = Dictionary.getConcept(Dictionary.YES);
		Concept ctx = Dictionary.getConcept(Dictionary.SULFAMETHOXAZOLE_TRIMETHOPRIM);

		CalculationResultMap ret = new CalculationResultMap();
		for(int ptId: cohort){
			boolean onCpt = false;
			if(inTbProgram.contains(ptId)) {
				Obs hivStatusObs = EmrCalculationUtils.obsResultForPatient(hivStatusMap, ptId);
				if(hivStatusObs != null && hivStatusObs.getValueCoded().equals(hivPositive)) {
					// First look to see if they have an obs for taking as prophylaxis
					ListResult ctxProphylaxis = (ListResult) ctxProphylaxisObss.get(ptId);
					if (ctxProphylaxis != null) {
						List<Obs> ctxProphylaxisObsList = CalculationUtils.extractResultValues(ctxProphylaxis);
						for (Obs ctxProphylaxisObs : ctxProphylaxisObsList) {
							if (ctxProphylaxisObs.getValueCoded().equals(yes)) {
								onCpt = true;
								break;
							}
						}
					}
					// Failing that, look for a med order
					ListResult patientMedOrders = (ListResult) medOrdersObss.get(ptId);
					if (patientMedOrders != null) {
						// Look through list of medication order obs for any CTX
						List<Obs> medOrderObsList = CalculationUtils.extractResultValues(patientMedOrders);
						for (Obs medOrderObs : medOrderObsList) {
							if (medOrderObs.getValueCoded().equals(ctx)) {
								onCpt = true;
								break;
							}
						}
					}
					//check if the patient is lost to follow up
					if(ltfu.contains(ptId)){
						onCpt = false;
					}

					ret.put(ptId, new BooleanResult(onCpt, this, context));
				}
			}
		}
		return ret;
	}
}
