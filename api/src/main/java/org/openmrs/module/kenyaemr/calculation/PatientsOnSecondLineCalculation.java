package org.openmrs.module.kenyaemr.calculation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.regimen.RegimenChange;
import org.openmrs.module.kenyaemr.regimen.RegimenHistory;

public class PatientsOnSecondLineCalculation extends KenyaEmrCalculation{

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort,Map<String, Object> arg1, PatientCalculationContext ctx) {
		Concept arvSet = Context.getConceptService().getConceptByUuid(MetadataConstants.ANTIRETROVIRAL_DRUGS_CONCEPT_UUID);
		
		
		Program hivProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.HIV_PROGRAM_UUID);
        Set<Integer> inHivProgram = patientsThatPass(lastProgramEnrollment(hivProgram, cohort, ctx));
        Set<Integer> alive = alivePatients(cohort, ctx);
        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
        	
        	if(inHivProgram.contains(ptId) && alive.contains(ptId)){
        		
        		RegimenHistory history = RegimenHistory.forPatient(Context.getPatientService().getPatient(ptId), arvSet);
        		List<RegimenChange> changes=new ArrayList<RegimenChange>(history.getChanges());
        		for(RegimenChange regimenChange:changes){
        			if(regimenChange.getStopped() !=null && ((regimenChange.getChangeReasonsNonCoded().size() >0)||(regimenChange.getChangeReasons().size() > 0))){
	        			ret.put(ptId, new SimpleResult(ptId, null));
        			}
        		}
        	}
        }
		return ret;
	}

	@Override
	public String getShortMessage() {
		return "Patients on Second Line";
	}

}
