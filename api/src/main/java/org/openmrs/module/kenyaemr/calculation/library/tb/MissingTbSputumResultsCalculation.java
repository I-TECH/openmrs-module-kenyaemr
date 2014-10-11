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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ObsResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;

/**
 * Calculates which patients are missing TB sputum results
 */
public class MissingTbSputumResultsCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

	/**
	 * @see org.openmrs.module.kenyacore.calculation.PatientFlagCalculation#getFlagMessage()
	 */
	@Override
	public String getFlagMessage() {
		return "Missing TB Sputum Results";
	}

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @should determine with missing sputum results
	 * patients are in tb program
	 * disease classification pulmonary tb and results either smear positive or smear negative
	 * there last encounter not having recorded sputum results
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
		// Get TB program
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);

		// Get all patients who are alive and in TB program
		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inTbProgram = Filters.inProgram(tbProgram, alive, context);
		
		//get last disease classification
		CalculationResultMap lastDiseaseClassiffication = Calculations.lastObs(Dictionary.getConcept(Dictionary.SITE_OF_TUBERCULOSIS_DISEASE), inTbProgram, context);
		
		//get the results for pulmonary tb that is either positive or negative
		CalculationResultMap lastTbPulmonayResult = Calculations.lastObs(Dictionary.getConcept(Dictionary.RESULTS_TUBERCULOSIS_CULTURE), inTbProgram, context);
		
		//get concepts for positive and negative
		Concept smearPositive = Dictionary.getConcept(Dictionary.POSITIVE);
		Concept smearNegative = Dictionary.getConcept(Dictionary.NEGATIVE);
		Concept pulmonaryTb = Dictionary.getConcept(Dictionary.PULMONARY_TB);
		
		//get the last encounter which might have recorded sputum results
		CalculationResultMap lastSputumResults = Calculations.lastObs(Dictionary.getConcept(Dictionary.SPUTUM_FOR_ACID_FAST_BACILLI), inTbProgram, context);
		
		CalculationResultMap ret = new CalculationResultMap();
		
		for (Integer ptId : cohort) {
			
			boolean missingSputumResults = false;
			
			//get the observation results stored for disease classification and the results
			ObsResult obsResultsClassification = (ObsResult) lastDiseaseClassiffication.get(ptId);
			ObsResult obsResultspulmonry = (ObsResult) lastTbPulmonayResult.get(ptId);
			ObsResult obsResultLastSputumResults = (ObsResult) lastSputumResults.get(ptId);
			
			//make sure no null values are picked to avoid NPE error
			if (obsResultsClassification != null && obsResultspulmonry != null) {
				
				if ((obsResultsClassification.getValue().getValueCoded().equals(pulmonaryTb))
						&& (obsResultLastSputumResults == null)
						&& ((obsResultspulmonry.getValue().getValueCoded().equals(smearPositive)) 
							|| (obsResultspulmonry.getValue().getValueCoded().equals(smearNegative)))) {
					
					missingSputumResults = true;
					
				}
				
			}
			
			ret.put(ptId, new BooleanResult(missingSputumResults, this, context));
		}
		return ret;
	}


}
