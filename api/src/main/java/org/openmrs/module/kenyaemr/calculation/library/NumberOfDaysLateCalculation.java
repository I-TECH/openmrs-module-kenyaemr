package org.openmrs.module.kenyaemr.calculation.library;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastReturnVisitDateCalculation;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by codehub on 10/7/15.
 */
public class NumberOfDaysLateCalculation extends AbstractPatientCalculation {
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> map, PatientCalculationContext context) {
        
        CalculationResultMap ret = new CalculationResultMap();
        CalculationResultMap returnDate = calculate(new LastReturnVisitDateCalculation(), cohort, context);
        for(Integer ptId: cohort){
            Integer days = null;
            Date returnDateValue = EmrCalculationUtils.datetimeResultForPatient(returnDate, ptId);
            if(returnDateValue != null) {
                days = days(returnDateValue, context);
            }
            ret.put(ptId, new SimpleResult(days, this));
            
        }
        return ret;
        
    }

    Integer days(Date d1, PatientCalculationContext context) {
        DateTime dateTime1 = new DateTime(d1.getTime());
        DateTime dateTime2 = new DateTime(context.getNow().getTime());
        return Math.abs(Days.daysBetween(dateTime1, dateTime2).getDays());
    }
}
