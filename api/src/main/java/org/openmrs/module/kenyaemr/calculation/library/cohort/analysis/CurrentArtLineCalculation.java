package org.openmrs.module.kenyaemr.calculation.library.cohort.analysis;

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnOriginalFirstLineArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnSecondLineArtCalculation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculates the current line
 */
public class CurrentArtLineCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

        CalculationResultMap ret = new CalculationResultMap();

        CalculationResultMap initialArtStartDate = calculate(new InitialArtStartDateCalculation(), cohort, context);

        for(Integer ptId: cohort) {
            String line = null;

            Date initialRegimenDate = EmrCalculationUtils.resultForPatient(initialArtStartDate, ptId);

            if(initialRegimenDate != null){

                CalculationResultMap onFirstLine = calculate(new OnOriginalFirstLineArtCalculation(), Arrays.asList(ptId), context);
                CalculationResultMap onSecondLine = calculate(new OnSecondLineArtCalculation(), Arrays.asList(ptId), context);

                Boolean onFirstLineResults = (Boolean) onFirstLine.get(ptId).getValue();
                Boolean onSecondLineResults = (Boolean) onSecondLine.get(ptId).getValue();

                if (onFirstLineResults != null && onFirstLineResults.equals(Boolean.TRUE) && onSecondLineResults.equals(Boolean.FALSE)) {
                    line = "1st";
                }
                else if(onSecondLineResults != null  && onSecondLineResults.equals(Boolean.TRUE) && onFirstLineResults.equals(Boolean.FALSE)) {
                    line = "2nd";
                }
                else if(onFirstLineResults != null && onSecondLineResults != null && onSecondLineResults.equals(Boolean.TRUE) && onSecondLineResults.equals(Boolean.TRUE)) {
                    line = "2nd";
                }
                ret.put(ptId, new SimpleResult(line, this));
            }

            else {
                ret.put(ptId, null);
            }

        }

        return ret;
    }
}
