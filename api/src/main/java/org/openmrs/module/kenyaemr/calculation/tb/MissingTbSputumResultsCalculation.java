package org.openmrs.module.kenyaemr.calculation.tb;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ObsResult;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.calculation.BaseAlertCalculation;
import org.openmrs.module.kenyaemr.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.CalculationUtils;

public class MissingTbSputumResultsCalculation extends BaseAlertCalculation {
	/**
	 * @see org.openmrs.module.kenyaemr.calculation.BaseAlertCalculation#getAlertMessage()
	 */
	@Override
	public String getAlertMessage() {
		return "Missing TB Sputum Results";
	}
	/**
	 * @see org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation#getName()
	 */
	@Override
	public String getName() {
		return "Patients With Missing TB Sputum Results";
	}
	@Override
	public String[] getTags() {
		return new String[] { "tb" };
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
		Program tbProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.TB_PROGRAM_UUID);

		// Get all patients who are alive and in TB program
		Set<Integer> alive = alivePatients(cohort, context);
		Set<Integer> inTbProgram = CalculationUtils.patientsThatPass(lastProgramEnrollment(tbProgram, alive, context));
		
		//get last disease classification
		CalculationResultMap lastDiseaseClassiffication = lastObs(getConcept(Dictionary.SITE_OF_TUBERCULOSIS_DISEASE), inTbProgram, context);
		
		//get the results for pulmonary tb that is either positive or negative
		CalculationResultMap lastTbPulmonayResult = lastObs(getConcept(Dictionary.RESULTS_TUBERCULOSIS_CULTURE), inTbProgram, context);
		
		//get concepts for positive and negative
		Concept smearPositive = getConcept(Dictionary.POSITIVE);
		Concept smearNegative = getConcept(Dictionary.NEGATIVE);
		Concept pulmonaryTb = getConcept(Dictionary.PULMONARY_TB);
		
		//get the last encounter which might have recorded sputum results
		CalculationResultMap lastSputumResults = lastObs(getConcept(Dictionary.SPUTUM_FOR_ACID_FAST_BACILLI), inTbProgram, context);
		
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
