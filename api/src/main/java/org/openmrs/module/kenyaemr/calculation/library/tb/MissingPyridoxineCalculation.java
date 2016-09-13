package org.openmrs.module.kenyaemr.calculation.library.tb;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
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
import org.openmrs.module.kenyaemr.metadata.IPTMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

public class MissingPyridoxineCalculation extends AbstractPatientCalculation implements PatientFlagCalculation{

	@Override
	public String getFlagMessage() {
		return "Missing Pyridoxine";
	}
	
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
			PatientCalculationContext context) {
		
		Boolean missingPyridoxine = false;
		
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
		
		// Get all patients who are alive and initiated into IPT
		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inTbProgram = Filters.inProgram(tbProgram, alive, context);
				
		CalculationResultMap nutritionSupportObs = Calculations.allObs(Dictionary.getConcept(Dictionary.NUTRITIONAL_SUPPORT), inTbProgram, context); 
		Concept pyridoxine = Dictionary.getConcept(Dictionary.PYRIDOXINE);
		
		CalculationResultMap ret = new CalculationResultMap();
		for(Integer ptId: cohort){
			if(inTbProgram.contains(ptId) ){
				ListResult listResult = (ListResult) nutritionSupportObs.get(ptId);
				List<Obs> allNSObs = CalculationUtils.extractResultValues(listResult);
				for (Obs obs : allNSObs) {
					if(obs.getValueCoded().equals(pyridoxine)){
						missingPyridoxine = true;
					}
                }
			}
			
			ret.put(ptId, new BooleanResult(missingPyridoxine, this));
		}

		return ret;
	}


}
