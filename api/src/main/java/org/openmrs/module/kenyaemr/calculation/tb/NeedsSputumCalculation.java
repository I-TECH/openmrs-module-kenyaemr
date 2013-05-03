package org.openmrs.module.kenyaemr.calculation.tb;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ObsResult;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.calculation.BaseAlertCalculation;
import org.openmrs.module.kenyaemr.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.CalculationUtils;

/**
 * Calculate whether patients are due for a sputum. Calculation returns true if  the patient
 * is alive, and screened fo tb, and has cough of any duration
 */
public class NeedsSputumCalculation extends BaseAlertCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @should determine whether patients need sputum test
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		EncounterType screeningEncType = Context.getEncounterService().getEncounterTypeByUuid(MetadataConstants.TB_SCREENING_ENCOUNTER_TYPE_UUID);
		
		Set<Integer> alive = alivePatients(cohort, context);
		Set<Integer> wasScreened = CalculationUtils.patientsThatPass(allEncounters(screeningEncType, cohort, context));
		
		CalculationResultMap lastObs_symptomp_name = lastObs(getConcept(MetadataConstants.SIGN_SYMPTOM_NAME_CONCEPT_UUID), cohort, context);
		CalculationResultMap lastObs_symptomp_present= lastObs(getConcept(MetadataConstants.SIGN_SYMPTOM_PRESENT_CONCEPT_UUID), cohort, context);
		
		CalculationResultMap ret = new CalculationResultMap();
		
		for (Integer ptId : cohort) {
			boolean needsSputum = false;
			
			// Is patient alive and was screened for TB
						if (wasScreened.contains(ptId)) {
							//go through a list of possible con
							Concept cough_present = CalculationUtils.codedObsResultForPatient(lastObs_symptomp_name, ptId);
							Concept yes_cough     = CalculationUtils.codedObsResultForPatient(lastObs_symptomp_present, ptId);
							
							if (cough_present != null && yes_cough != null && yes_cough.getConceptId() == 1065 ) {
								
								needsSputum = true;
							}
						}
						ret.put(ptId, new BooleanResult(needsSputum, this, context));
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
