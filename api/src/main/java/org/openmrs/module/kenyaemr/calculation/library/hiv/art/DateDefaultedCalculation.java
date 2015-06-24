package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.MissedLastAppointmentCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastReturnVisitDateCalculation;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 24/06/15.
 */
public class DateDefaultedCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        CalculationResultMap ret = new CalculationResultMap();
        //find the return visit date from the last encounter
        CalculationResultMap resultMap = calculate(new LastReturnVisitDateCalculation(), cohort, context);

        //find lost to follow up patients
        Set<Integer> defaulters = CalculationUtils.patientsThatPass(calculate(new MissedLastAppointmentCalculation(), cohort, context));

        for (Integer ptId : cohort) {

            Date dateDefaulted = null;

            if(defaulters.contains(ptId)) {

                SimpleResult lastScheduledReturnDateResults = (SimpleResult) resultMap.get(ptId);

                if (lastScheduledReturnDateResults != null) {
                    Date lastScheduledReturnDate = (Date) lastScheduledReturnDateResults.getValue();
                    if(lastScheduledReturnDate != null) {
                        Calendar dateClassified = Calendar.getInstance();
                        dateClassified.setTime(lastScheduledReturnDate);
                        dateClassified.add(Calendar.DATE, 30);

                        dateDefaulted = dateClassified.getTime();
                    }
                }
            }

            ret.put(ptId, new SimpleResult(dateDefaulted, this));

        }

        return ret;
    }
}
