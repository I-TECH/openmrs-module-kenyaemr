package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Program;
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
import org.openmrs.module.kenyaemr.calculation.library.MissedLastAppointmentCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaui.KenyaUiActivator;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * A calculation that returns persons who are eligible for HIV enrollment
 * Eligibility criteria include:
 * Having  done the Hiv reconfirmatory test -option yes
 * Positive Reconfirmatory test result
 */
public class EligibleForEnrollmetHivCalculation extends AbstractPatientCalculation  {

    protected static final Log log = LogFactory.getLog(EligibleForEnrollmetHivCalculation.class);

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Concept ConfirmatoryTest = Context.getConceptService().getConcept(159427);
        CalculationResultMap ret = new CalculationResultMap();
        Set<Integer> alive = Filters.alive(cohort, context);
        CalculationResultMap lastConfirmatoryTest = Calculations.lastObs(ConfirmatoryTest ,alive, context);
        for(Integer ptId: cohort){

            Double cTest = EmrCalculationUtils.numericObsResultForPatient(lastConfirmatoryTest, ptId);
            boolean eligible= false
            Obs lastConfirmatoryTest = null;

            // get latest confirmatory test
          if ( confirmatoryTest!= null) {
              lastConfirmatoryTest = EmrCalculationUtils.obsResultForPatient(cTest, ptId);
            }


            if(lastConfirmatoryTest != null && (lastConfirmatoryTest.getValueCoded == 703)) {
                confirmatoryTestQualifer = true;
            }

            if(confirmatoryTestQualifer)
                eligible = true;

            ret.put(ptId, new BooleanResult(eligible, this));
        }
        return ret;
    }
}