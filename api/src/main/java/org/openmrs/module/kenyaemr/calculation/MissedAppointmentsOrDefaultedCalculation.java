package org.openmrs.module.kenyaemr.calculation;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.MetadataConstants;

/**
 * Created with IntelliJ IDEA.
 * User: ningosi
 * Date: 9/21/12
 * Time: 9:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class MissedAppointmentsOrDefaultedCalculation extends KenyaEmrCalculation {

    @Override
    public String getShortMessage() {
        return "Missed Appointments" ;
    }

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        Program hivProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.HIV_PROGRAM_UUID);
        Set<Integer> inHivProgram = patientsThatPass(lastProgramEnrollment(hivProgram, cohort, context));
        Set<Integer> alive = alivePatients(cohort, context);
        CalculationResultMap lastObs = lastObs(MetadataConstants.RETURN_VISIT_DATE_CONCEPT_UUID, cohort, context);
        
        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
            boolean missedVisit=false;
            if(inHivProgram.contains(ptId) && alive.contains(ptId) && (!(lastObs.isEmpty()))){
            		try{
                     Date returnDate=obsResultForPatient(lastObs,ptId).getValueDatetime();
                     missedVisit = (daysSince(returnDate,context)) > 0 && (daysSince(returnDate,context)) < 90;
            		}
            		catch(Exception ex){
            			ex.toString();
            		}
            }
                ret.put(ptId, new SimpleResult(missedVisit, this, context));

        }
        return ret;
    }
}
