package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.models.Cd4ValueAndDate;

import java.util.Collection;
import java.util.Map;

/**
 * Created by codehub on 06/07/15.
 */
public class ChangeInCd4CountCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        CalculationResultMap ret = new CalculationResultMap();

        CalculationResultMap lastCd4 = calculate(new LastCd4Calculation(), cohort, context);
        CalculationResultMap baselineCd4 = calculate(new BaselineCd4CountAndDateCalculation(), cohort, context);

        for(Integer ptId: cohort) {
            Double diff = null;
            Double lastCd4Count = null;
            Double baselineCd4Count = null;

            Cd4ValueAndDate cd4ValueAndDateLastCd4 = EmrCalculationUtils.resultForPatient(lastCd4, ptId);
            Cd4ValueAndDate cd4ValueAndDateBaselineCd4 = EmrCalculationUtils.resultForPatient(baselineCd4, ptId);

            if(cd4ValueAndDateLastCd4 != null && cd4ValueAndDateBaselineCd4 != null) {
                lastCd4Count = cd4ValueAndDateLastCd4.getCd4Value();
                baselineCd4Count = cd4ValueAndDateBaselineCd4.getCd4Value();
            }

            if(lastCd4Count != null && baselineCd4Count != null) {
                diff = lastCd4Count - baselineCd4Count;
            }


            ret.put(ptId, new SimpleResult(diff, this));
        }
        return ret;
    }
}
