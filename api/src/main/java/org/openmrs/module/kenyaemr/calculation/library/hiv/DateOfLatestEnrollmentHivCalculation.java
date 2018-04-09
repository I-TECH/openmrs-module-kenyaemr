package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculate the latest date of enrollment into HIV Program
 */
public class DateOfLatestEnrollmentHivCalculation extends AbstractPatientCalculation {

    protected static final Log log = LogFactory.getLog(StablePatientsCalculation.class);
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {


        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        CalculationResultMap lastEnrollmentEncounter = Calculations.lastEncounter(Context.getEncounterService().getEncounterTypeByUuid("de78a6be-bfc5-4634-adc3-5f1a280455cc"),cohort, context);

        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId:cohort) {
            Date lastEnrollmentDate = null;
             Encounter lastEnrollment = EmrCalculationUtils.encounterResultForPatient(lastEnrollmentEncounter, ptId);

            if(lastEnrollment != null ) {
                lastEnrollmentDate = lastEnrollment.getEncounterDatetime();
            }

            ret.put(ptId, new SimpleResult(lastEnrollmentDate, this));
        }

        return ret;
    }
}
