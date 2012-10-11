package org.openmrs.module.kenyaemr.calculation;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.MetadataConstants;

public class Summary254Calculation extends KenyaEmrCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort,Map<String, Object> parameterValues, PatientCalculationContext cxt) {
		
		Program hivProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.HIV_PROGRAM_UUID);
        Set<Integer> inHivProgram = patientsThatPass(lastProgramEnrollment(hivProgram, cohort, cxt));
        Set<Integer> alive = alivePatients(cohort, cxt);
        
        CalculationResultMap summary = new CalculationResultMap();
        for (Integer ptId : cohort) {
	        if(inHivProgram.contains(ptId) && alive.contains(ptId)){
	        	
	        }
			
			
        }
        return summary;
	}

	@Override
	public String getShortMessage() {
		return "MoH 257 Report for each Patient";
	}

}
