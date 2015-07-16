package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.models.PatientEligibility;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by codehub on 16/07/15.
 */
public class DaysFromArtEligibilityToArtInitiationCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        CalculationResultMap artInitiationDate = calculate(new InitialArtStartDateCalculation(), cohort, context);
        CalculationResultMap dateEligibleForArt = calculate(new DateAndReasonFirstMedicallyEligibleForArtCalculation(), cohort, context);

        for(Integer ptId: cohort) {
            Integer days = null;
            Date artInitialDate = EmrCalculationUtils.datetimeResultForPatient(artInitiationDate, ptId);
            PatientEligibility patientEligibility = EmrCalculationUtils.resultForPatient(dateEligibleForArt, ptId);
            if(artInitialDate != null && patientEligibility != null){
                days = daysBetween(patientEligibility.getEligibilityDate(), artInitialDate);
            }
            ret.put(ptId, new SimpleResult(days, this));
        }
        return  ret;
    }

    private int daysBetween(Date date1, Date date2) {
        DateTime d1 = new DateTime(date1.getTime());
        DateTime d2 = new DateTime(date2.getTime());
        return Days.daysBetween(d1, d2).getDays();
    }
}