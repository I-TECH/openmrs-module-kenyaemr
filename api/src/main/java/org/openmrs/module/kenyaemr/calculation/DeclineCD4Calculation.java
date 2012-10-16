package org.openmrs.module.kenyaemr.calculation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.MetadataConstants;

/**
 * Created with IntelliJ IDEA.
 * User: ningosi
 * Date: 9/20/12
 * Time: 12:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeclineCD4Calculation extends KenyaEmrCalculation {

    @Override
    public String getShortMessage() {
        return "Declining CD4";
    }

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        Program hivProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.HIV_PROGRAM_UUID);
        Set<Integer> inHivProgram = patientsThatPass(lastProgramEnrollment(hivProgram, cohort, context));
        Set<Integer> alive = alivePatients(cohort, context);
        CalculationResultMap lastCD4 = lastObs(MetadataConstants.CD4_CONCEPT_UUID, cohort, context);
        CalculationResultMap cd4SixMonthsAgo=sixMonthsAgoCD4(MetadataConstants.CD4_CONCEPT_UUID, cohort, context);
        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
                boolean declining=false;
                Double latestCD4=0.0;
                Double sixMonthsAgoCD4=0.0;
                if(inHivProgram.contains(ptId) && alive.contains(ptId) && (!(lastCD4.isEmpty()))){
                	latestCD4=numericObsResultForPatient(lastCD4, ptId);
                	sixMonthsAgoCD4=numericObsResultForPatient(cd4SixMonthsAgo, ptId);
	                if(latestCD4 !=null && sixMonthsAgoCD4 !=null){
	                	declining =latestCD4 < sixMonthsAgoCD4;
	                		ret.put(ptId, new SimpleResult(declining, this, context));
	                		
                	}
                	
            }
        
    }
               return ret;
}
    
}
