package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by codehub on 10/7/15.
 */
public class LastReturnVisitDateNthPositionCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> map, PatientCalculationContext context) {

        Concept returnVisitDateConcept = Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE);

        CalculationResultMap returnVisitDateMap = Calculations.allObs(returnVisitDateConcept, cohort, context);

        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId: cohort){
            Date visitDate = null;
            ListResult listResult = (ListResult) returnVisitDateMap.get(ptId);
            List<Obs> returnVisitDateFromLastDate = CalculationUtils.extractResultValues(listResult);
            if(returnVisitDateFromLastDate.size() == 1){
                visitDate = returnVisitDateFromLastDate.get(0).getValueDatetime();
            }
            else if (returnVisitDateFromLastDate.size() > 1) {
                visitDate = returnVisitDateFromLastDate.get(returnVisitDateFromLastDate.size() - 1).getValueDatetime();
            }

            ret.put(ptId, new SimpleResult(visitDate, this));

        }
        return ret;
    }
}
