package org.openmrs.module.kenyaemr.calculation.library.cohort.analysis;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Years;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnOriginalFirstLineArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnSecondLineArtCalculation;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculates the line on which a patient is on
 */
public class ArtLineYearsCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

        Integer years = (params != null && params.containsKey("years")) ? (Integer) params.get("years") : null;
        CalculationResultMap ret = new CalculationResultMap();

        CalculationResultMap initialArtStartDate = calculate(new InitialArtStartDateCalculation(), cohort, context);

        PatientCalculationService patientCalculationService = Context.getService(PatientCalculationService.class);

        PatientCalculationContext context1 = patientCalculationService.createCalculationContext();
        context1.setNow(context.getNow());

        for(Integer ptId: cohort) {
            String line = null;
            Date initialRegimenDate = EmrCalculationUtils.resultForPatient(initialArtStartDate, ptId);
            if(initialRegimenDate != null && years != null) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(initialRegimenDate);
                calendar.add(Calendar.YEAR, years);
                context.setNow(calendar.getTime());

                CalculationResultMap onFirstLine = calculate(new OnOriginalFirstLineArtCalculation(), Arrays.asList(ptId), context);
                CalculationResultMap onSecondLine = calculate(new OnSecondLineArtCalculation(), Arrays.asList(ptId), context);

                Boolean onFirstLineResults = (Boolean) onFirstLine.get(ptId).getValue();
                Boolean onSecondLineResults = (Boolean) onSecondLine.get(ptId).getValue();

                if (onFirstLineResults != null && onFirstLineResults.equals(Boolean.TRUE) && onSecondLineResults.equals(Boolean.FALSE) && (yearsSince(initialRegimenDate, context1.getNow()) > (years * 12))) {
                    line = "1st";
                }
                else if(onSecondLineResults != null  && onSecondLineResults.equals(Boolean.TRUE) && onFirstLineResults.equals(Boolean.FALSE) && (yearsSince(initialRegimenDate, context1.getNow()) > (years * 12))) {
                    line = "2nd";
                }
                else if(onFirstLineResults != null && onSecondLineResults != null && onSecondLineResults.equals(Boolean.TRUE) && onSecondLineResults.equals(Boolean.TRUE) && (yearsSince(initialRegimenDate, context1.getNow()) > (years * 12))) {
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

    private  int yearsSince(Date date1, Date date2) {
        DateTime d1 = new DateTime(date1.getTime());
        DateTime d2 = new DateTime(date2.getTime());
        return Months.monthsBetween(d1, d2).getMonths();
    }
}
