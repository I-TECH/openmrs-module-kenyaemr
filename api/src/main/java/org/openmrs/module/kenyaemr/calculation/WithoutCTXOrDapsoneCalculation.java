package org.openmrs.module.kenyaemr.calculation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.MetadataConstants;

public class WithoutCTXOrDapsoneCalculation extends BaseKenyaEmrCalculation {

	@SuppressWarnings("unchecked")
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort,Map<String, Object>  parameterValues, PatientCalculationContext context) {
		Program hivProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.HIV_PROGRAM_UUID);
		Set<Integer> inHivProgram = CalculationUtils.patientsThatPass(lastProgramEnrollment(hivProgram, cohort, context));
		Set<Integer> alive = alivePatients(cohort, context);
		CalculationResultMap medicationorder = allObs(MetadataConstants.MEDICATION_ORDERS_CONCEPT_UUID, cohort, context);
		CalculationResultMap ret = new CalculationResultMap();
		
		//for (Integer ptId : cohort) {
			Object value=null;
			Integer ptId=null;
			Obs obs=null;
			Integer dapsone=Context.getConceptService().getConceptByUuid(MetadataConstants.DAPSONE_CONCEPT_UUID).getConceptId();
			Integer ctx=Context.getConceptService().getConceptByUuid(MetadataConstants.SULFAMETHOXAZOLE_TRIMETHOPRIM_CONCEPT_UUID).getConceptId();
			
			//to cater for those without any medication orders
			for (Integer ptIds : cohort) {
				if(inHivProgram.contains(ptIds) && alive.contains(ptIds)){
					try{
						obs=obsResultForPatient(medicationorder,ptIds);
						if(obs==null){
							ret.put(ptIds, new SimpleResult(ptIds, null));
						}
					}
					catch(Exception e){
						e.toString();
					}
					
					
					
				}
				
			}
			
				// to cater for those with medication orders but non of them is dapsone or ctx
				for(Map.Entry<Integer, CalculationResult> e : medicationorder.entrySet()){
					
					if(inHivProgram.contains(e.getKey()) && alive.contains(e.getKey())){
						ptId = e.getKey();
			    		ListResult result = (ListResult) e.getValue();
			    		for (SimpleResult r : (List<SimpleResult>) result.getValue()) {
			    			
			    			value=((Obs)r.getValue()).getValueCoded();
			    			if((value.toString().equals(dapsone.toString()))||(value.toString().equals(ctx.toString()))){
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
		return "Patients Without CTX or Dapsone";
	}

}
