package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
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
            String suppressed = "";
            Double cd4ValueAndDate = EmrCalculationUtils.resultForPatient(viralLoad, ptId);

            if(cd4ValueAndDate != null && cd4ValueAndDate < 1000) {
                suppressed = "Yes";
            }
            else if(cd4ValueAndDate != null && cd4ValueAndDate >= 1000){
                suppressed = "No";
            }

            ret.put(ptId, new SimpleResult(suppressed, this));
        }
        return  ret;

    }
}
