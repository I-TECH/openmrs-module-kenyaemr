package org.openmrs.module.kenyaemr.calculation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.MetadataConstants;

public class HIVPatientsWhoHaveNeverScreenedForTBCalculation extends KenyaEmrCalculation{

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort,Map<String, Object> arg1, PatientCalculationContext context) {
		Program hivProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.HIV_PROGRAM_UUID);
		Form tbScreeningForm=Context.getFormService().getFormByUuid(MetadataConstants.TB_SCREENING_FORM_UUID);
        Set<Integer> inHivProgram = patientsThatPass(lastProgramEnrollment(hivProgram, cohort, context));
        Set<Integer> alive = alivePatients(cohort, context);
        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
        	if(inHivProgram.contains(ptId) && alive.contains(ptId)){
        		
        		List<Encounter> encounter =Context.getEncounterService().getEncountersByPatientId(ptId);
        		
        		for(Encounter e:encounter){
        			if(e.getForm().getFormId() == tbScreeningForm.getId()){
        				break;
        			}
        			else{
        			ret.put(ptId, new SimpleResult(ptId, null));
        			}
        		}
        		
        	}
        	
        }
		return ret;
	}

	@Override
	public String getShortMessage() {
		
		return "Patients Never TB Screened";
	}

}
