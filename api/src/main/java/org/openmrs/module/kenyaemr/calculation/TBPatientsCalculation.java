package org.openmrs.module.kenyaemr.calculation;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.MetadataConstants;

public class TBPatientsCalculation extends KenyaEmrCalculation {
	
	@Override
	public String getShortMessage() {
		return "TB Patients";
	}

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort,Map<String, Object> arg1, PatientCalculationContext ctx) {
		Program tbProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.TB_PROGRAM_UUID);
        Set<Integer> inTbProgram = patientsThatPass(lastProgramEnrollment(tbProgram, cohort, ctx));
        Set<Integer> alive = alivePatients(cohort, ctx);
        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
        	if(inTbProgram.contains(ptId) && alive.contains(ptId)){
        		ret.put(ptId, new SimpleResult(ptId, null));
        		
        	}
        }
        
		return ret;
	}	

	

}
