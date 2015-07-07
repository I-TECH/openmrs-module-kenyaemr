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
public class ChangeInCd4PercentCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        CalculationResultMap ret = new CalculationResultMap();

        CalculationResultMap lastCd4Percent = calculate(new LastCd4PercentCalculation(), cohort, context);
        CalculationResultMap baselineCd4Percent = calculate(new BaselineCd4PercentAndDateCalculation(), cohort, context);

        for(Integer ptId: cohort) {
            Double diff = null;
            Double lastCd4Count = null;
            Double baselineCd4Count = null;

            Cd4ValueAndDate cd4ValueAndDateLastCd4Percent = EmrCalculationUtils.resultForPatient(lastCd4Percent, ptId);
            Cd4ValueAndDate cd4ValueAndDateBaselineCd4Percent = EmrCalculationUtils.resultForPatient(baselineCd4Percent, ptId);

            if(cd4ValueAndDateLastCd4Percent != null && cd4ValueAndDateBaselineCd4Percent != null) {
                lastCd4Count = cd4ValueAndDateLastCd4Percent.getCd4Value();
                baselineCd4Count = cd4ValueAndDateBaselineCd4Percent.getCd4Value();
            }

            if(lastCd4Count != null && baselineCd4Count != null) {
                diff = lastCd4Count - baselineCd4Count;
            }


            ret.put(ptId, new SimpleResult(diff, this));
        }
        return ret;
    }
}
