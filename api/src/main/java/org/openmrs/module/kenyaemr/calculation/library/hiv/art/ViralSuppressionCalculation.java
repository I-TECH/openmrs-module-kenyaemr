package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.models.Cd4ValueAndDate;

import java.util.Collection;
import java.util.Map;

/**
 * Created by codehub on 06/07/15.
 */
public class ViralSuppressionCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        CalculationResultMap viralLoad = calculate(new ViralLoadCalculation(), cohort, context);

        for(Integer ptId:cohort) {
            boolean suppressed = false;
            Cd4ValueAndDate cd4ValueAndDate = EmrCalculationUtils.resultForPatient(viralLoad, ptId);

            if(cd4ValueAndDate != null && cd4ValueAndDate.getCd4Value() < 1000) {
                suppressed = true;
            }
            ret.put(ptId, new BooleanResult(suppressed, this));
        }
        return  ret;

    }
}
