package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 06/07/15.
 */
public class CurrentArtLineCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

        CalculationResultMap ret = new CalculationResultMap();

        CalculationResultMap initialArtStartDate = calculate(new InitialArtStartDateCalculation(), cohort, context);
        Set<Integer> onFirstLine = CalculationUtils.patientsThatPass(calculate(new OnOriginalFirstLineArtCalculation(), cohort, context));
        Set<Integer> onSecondLine = CalculationUtils.patientsThatPass(calculate(new OnSecondLineArtCalculation(), cohort, context));

        for(Integer ptId: cohort) {
            String line = "Other";

            Date initialRegimenDate = EmrCalculationUtils.resultForPatient(initialArtStartDate, ptId);

            if(initialRegimenDate != null){

                if (onFirstLine != null && onFirstLine.contains(ptId)) {
                    line = "1st";
                }
                if(onSecondLine != null  && onSecondLine.contains(ptId)) {
                    line = "2nd";
                }
                if(onFirstLine != null && onSecondLine != null && onFirstLine.contains(ptId) && onSecondLine.contains(ptId)) {
                    line = "2nd";
                }

                ret.put(ptId, new SimpleResult(line, this));
            }

        }

        return ret;
    }
}