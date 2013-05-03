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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
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
		EncounterType screeningEncType = Context.getEncounterService().getEncounterTypeByUuid(MetadataConstants.TB_SCREENING_ENCOUNTER_TYPE_UUID);
		
		Set<Integer> alive = alivePatients(cohort, context);
		Set<Integer> wasScreened = CalculationUtils.patientsThatPass(allEncounters(screeningEncType, cohort, context));
		Concept SIGN_SYMPTOM_NAME_CONCEPT = Context.getConceptService().getConceptByUuid(MetadataConstants.SIGN_SYMPTOM_NAME_CONCEPT_UUID);
		Concept SIGN_SYMPTOM_PRESENT_CONCEPT = Context.getConceptService().getConceptByUuid(MetadataConstants.SIGN_SYMPTOM_PRESENT_CONCEPT_UUID);
		Concept cough = Context.getConceptService().getConceptByUuid(MetadataConstants.COUGH_LASTING_MORE_THAN_TWO_WEEKS_UUID);
		Concept YES = Context.getConceptService().getConceptByUuid(MetadataConstants.YES_CONCEPT_UUID);
		//find patients who are in TB program
		Set<Integer> inTbProgram = CalculationUtils.patientsThatPass(lastProgramEnrollment(tbProgram, alive, context));
		//check if there is any observation recorded per the sputum results for only patients screened for Tb
		CalculationResultMap lastObs = lastObs(getConcept(MetadataConstants.SPUTUM_FOR_ACID_FAST_BACILLI_CONCEPT_UUID), inTbProgram, context);
		System.out.println("Size of the map is "+lastObs.size());
		
		CalculationResultMap ret = new CalculationResultMap();
		
		for (Integer ptId : cohort) {
			boolean needsSputum_cough = false;
			boolean withSameObsGrp = false;
			Obs obsgroup =null;
			ObsService obsservice= Context.getObsService();
			
			//check if a patient is alive and was screened for TB ie Tb screening form was completed
				if (wasScreened.contains(ptId) && alive.contains(ptId)) {
					//listing symptoms names and their results, this is   depended on the way fields have been mapped on the tb screening form
					List<Obs> observation_cough = obsservice.getObservationsByPersonAndConcept(Context.getPersonService().getPerson(ptId),SIGN_SYMPTOM_NAME_CONCEPT);
				    List<Obs> observation_cough_yes_no = obsservice.getObservationsByPersonAndConcept(Context.getPersonService().getPerson(ptId),SIGN_SYMPTOM_PRESENT_CONCEPT);
				    //loop through the symptoms names and find the one with the 2 weeks cough

						for(Obs o:observation_cough) {
							if(o.getValueCoded().getConceptId() == cough.getConceptId()){
								obsgroup = o.getObsGroup();
							}
							
						}
						//loop through answers and get non voided, value gotten should be yes and having the same obsgroup as the symptom name
						//for this to work there should be no any sputum results
						for(Obs o1:observation_cough_yes_no){
							if(!(o1.isVoided()) && o1.getValueCoded().getConceptId() == YES.getConceptId() && o1.getObsGroup() == obsgroup && (lastObs.isEmpty())){
								withSameObsGrp = true; 
							}
						}
						if(withSameObsGrp){
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
