package org.openmrs.module.kenyaemr.calculation.library.tb;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ObsResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

public class TbSuspectCalculation extends AbstractPatientCalculation{

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
			PatientCalculationContext context) {
		//Get TB Program
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
		
		//Get all patients who are alive and not in TB Program
		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inTbProgram = Filters.inProgram(tbProgram, cohort, context);
		
		Concept tbSuspect = Dictionary.getConcept(Dictionary.DISEASE_SUSPECTED);
		
		CalculationResultMap lastTbDiseaseStatus = Calculations.lastObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS), alive, context); 
		
		CalculationResultMap ret = new CalculationResultMap();
		
		for (Integer ptId : cohort) {
			
			Boolean isTBSuspectNotInProgram = false;
			
			ObsResult obsResultTbDiseaseStatus = (ObsResult)lastTbDiseaseStatus.get(ptId); 
			
			if (obsResultTbDiseaseStatus != null) {
				
				if (obsResultTbDiseaseStatus.getValue().getValueCoded().equals(tbSuspect)) {
					
					//Check if patient is not enrolled in TB program
					if ( ! inTbProgram.contains(ptId)) {
						
						isTBSuspectNotInProgram = true;
						
					}
					
				}
				
			}
			
			ret.put(ptId, new BooleanResult(isTBSuspectNotInProgram, this, context));
			
		}
		
		return ret;
	}

}
