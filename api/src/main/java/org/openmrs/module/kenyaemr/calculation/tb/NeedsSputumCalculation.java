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
package org.openmrs.module.kenyaemr.calculation.tb;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.calculation.BaseAlertCalculation;
import org.openmrs.module.kenyaemr.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.CalculationUtils;

/**
 * Calculate whether patients are due for a sputum test. Calculation returns true if  the patient
 * is alive, and screened for tb, and has cough of any duration probably 2 weeks
 * during the 2 weeks then there should have been no sputum results recorded
 */
public class NeedsSputumCalculation extends BaseAlertCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @should determine whether patients need sputum test
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
		//get into the tb program
		Program tbProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.TB_PROGRAM_UUID);
		//get the tb screening encounter type
		EncounterType screeningEncType = Context.getEncounterService().getEncounterTypeByUuid(MetadataConstants.TB_SCREENING_ENCOUNTER_TYPE_UUID);
		//get set of patients who are alive
		Set<Integer> alive = alivePatients(cohort, context);
		//only patients who are screened for Tb
		Set<Integer> wasScreened = CalculationUtils.patientsThatPass(allEncounters(screeningEncType, cohort, context));
		//get concept for cough for two weeks and with the response yes
		Concept twoWeeksCough = Context.getConceptService().getConceptByUuid(MetadataConstants.COUGH_LASTING_MORE_THAN_TWO_WEEKS_UUID);
		Concept YES = Context.getConceptService().getConceptByUuid(MetadataConstants.YES_CONCEPT_UUID);
		//find patients who are in TB program
		Set<Integer> inTbProgram = CalculationUtils.patientsThatPass(lastProgramEnrollment(tbProgram, alive, context));
		//check if there is any observation recorded per the sputum results for only patients enrolled in tb progarm and were screened
		CalculationResultMap lastObs_sputum = lastObs(getConcept(MetadataConstants.SPUTUM_FOR_ACID_FAST_BACILLI_CONCEPT_UUID), inTbProgram, context);
		//get all the observations per the symptom name concept,only screened patient
		CalculationResultMap lastObs_sign_symptom_name = allObs(getConcept(MetadataConstants.SIGN_SYMPTOM_NAME_CONCEPT_UUID), wasScreened, context);
		//get all obs per the symptom present and screened for tb
		CalculationResultMap lastObs_sign_symptom_present = allObs(getConcept(MetadataConstants.SIGN_SYMPTOM_PRESENT_CONCEPT_UUID), wasScreened, context);
		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean needsSputum_cough = false;
			boolean hasTwoWeeksCough = false;
			Obs sameObsGroup = null;
			
			//check if a patient is alive
				if (alive.contains(ptId)) {
					//get the list of all the observations per the symptom name
					ListResult symptomNameObss = (ListResult) lastObs_sign_symptom_name.get(ptId);
					//get observation of coughing more that 2 weeks from a collection
					for (Obs obsName : CalculationUtils.<Obs>extractListResultValues(symptomNameObss)) {
						if(obsName.getValueCoded() == twoWeeksCough){
							sameObsGroup = obsName.getObsGroup();
						}
					}
					//get the list of all the observations per the present symptoms and should not have had any sputum results
					ListResult symptomPresentObss = (ListResult) lastObs_sign_symptom_present.get(ptId);
					for (Obs obsPresent : CalculationUtils.<Obs>extractListResultValues(symptomPresentObss)) {
						if(obsPresent.getValueCoded() == YES && obsPresent.getObsGroup() == sameObsGroup && (lastObs_sputum.isEmpty())){
							hasTwoWeeksCough = true;
						}
					}
						
						if(hasTwoWeeksCough){
							needsSputum_cough = true;
						}
						
					}
						ret.put(ptId, new BooleanResult(needsSputum_cough, this, context));
		}
		return ret;
	}
	/**
	 * @see org.openmrs.module.kenyaemr.calculation.BaseAlertCalculation#getAlertMessage()
	 */
	@Override
	public String getAlertMessage() {
		return "Due for Sputum";
	}
	/**
	 * @see org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation#getName()
	 */
	@Override
	public String getName() {
		return "Patients Due for Sputum";
	}
	@Override
	public String[] getTags() {
		return new String[] { "alert", "hiv" };
	}

}
