package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Checks if a patient is negative and not enrolled
 */
public class HIVNegativePatientsCalculation extends AbstractPatientCalculation {

    protected static final Log log = LogFactory.getLog(HIVNegativePatientsCalculation.class);

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        EncounterService encounterService = Context.getEncounterService();

        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId: cohort){
            boolean notEnrolled = true;
            List<Encounter> enrollmentEncounters = encounterService.getEncounters(
                    Context.getPatientService().getPatient(ptId),
                    null,
                    null,
                    null,
                    null,
                    Arrays.asList(Context.getEncounterService().getEncounterTypeByUuid("de78a6be-bfc5-4634-adc3-5f1a280455cc")),
                    null,
                    null,
                    null,
                    false
            );
            if(enrollmentEncounters.size() > 0) {
                notEnrolled = false;
            }

            ret.put(ptId, new BooleanResult(notEnrolled, this));
        }
        return ret;
    }

}