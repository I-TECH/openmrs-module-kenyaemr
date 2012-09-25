package org.openmrs.module.kenyaemr.calculation;

import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ObsResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.MetadataConstants;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

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
        CalculationResultMap firstObs = firstObs(MetadataConstants.CD4_CONCEPT_UUID, cohort, context);
        CalculationResultMap lastObs = lastObs(MetadataConstants.CD4_CONCEPT_UUID, cohort, context);
        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
            ObsResult latest = (ObsResult) lastObs.get(ptId);
            ObsResult first = (ObsResult) firstObs.get(ptId);

                boolean declining=false;
                if(inHivProgram.contains(ptId) && alive.contains(ptId) && (!(latest.isEmpty()) || !(first.isEmpty()))){

                    declining =latest.getValue().getValueNumeric() < first.getValue().getValueNumeric();

                    ret.put(ptId, new SimpleResult(declining, this, context));

               }

        }

        return ret;
    }
}
