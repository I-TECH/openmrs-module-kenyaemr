package org.openmrs.module.kenyaemr.calculation.library.tb;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MissingPyridoxineCalculation extends AbstractPatientCalculation implements PatientFlagCalculation{

	@Override
	public String getFlagMessage() {
		return "Missing Pyridoxine";
	}
	
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
			PatientCalculationContext context) {
		
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
		
		// Get all patients who are alive and initiated into IPT
		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inTbProgram = Filters.inProgram(tbProgram, alive, context);
		
		Concept pyridoxine = Dictionary.getConcept(Dictionary.PYRIDOXINE);
		Concept nutritionalSupport = Dictionary.getConcept(Dictionary.NUTRITIONAL_SUPPORT);
		CalculationResultMap nutritionSupportObs = Calculations.allObs(nutritionalSupport, inTbProgram, context);
		CalculationResultMap ret = new CalculationResultMap();
		
		for(Integer ptId: cohort){
			
			Boolean missingPyridoxine = false;
			
			if(inTbProgram.contains(ptId) ){
				
				ListResult listResult = (ListResult) nutritionSupportObs.get(ptId);
				List<Obs> allNSObs = CalculationUtils.extractResultValues(listResult);
				
				for (Obs obs : allNSObs) {
					
					if(obs.getValueCoded().equals(pyridoxine)){
						
						missingPyridoxine = true;
					}
					
                }
				
			}
			
			ret.put(ptId, new BooleanResult(missingPyridoxine, this, context));
		}
		return ret;
	}


}
