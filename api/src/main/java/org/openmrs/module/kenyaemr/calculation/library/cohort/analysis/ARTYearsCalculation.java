package org.openmrs.module.kenyaemr.calculation.library.cohort.analysis;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Years;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.AliveAndOnFollowUpCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.DateClassifiedLTFUCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TransferOutDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.DateOfDeathCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.model.LostToFU;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.definition.ProgramEnrollmentsForPatientDataDefinition;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculates those patients on art
 */
public class ARTYearsCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

        Integer years = (params != null && params.containsKey("years")) ? (Integer) params.get("years") : null;
        CalculationResultMap ret = new CalculationResultMap();


        ////find the program map enrolled in
        CalculationResultMap deadPatients = calculate(new DateOfDeathCalculation(), cohort, context);
        CalculationResultMap defaulted = calculate(new DateDefaultedCalculation(), cohort, context);
        CalculationResultMap ltfu = calculate(new DateClassifiedLTFUCalculation(), cohort, context);
        CalculationResultMap transferredOut = calculate(new TransferOutDateCalculation(), cohort, context);
        CalculationResultMap onART = calculate(new InitialArtStartDateCalculation(), cohort, context);
        Set<Integer> onlyThoseOnArt = onART.keySet();

        for(Integer ptId : cohort) {
            String value = null;
            Date artInitiationDate;
            Date resultsDate;
            Date dateLost = null;
            Date runAfter;

            Date dod = EmrCalculationUtils.datetimeResultForPatient(deadPatients, ptId);

            LostToFU classifiedLTFU = EmrCalculationUtils.resultForPatient(ltfu, ptId);

            //find date transferred out
            Date dateTo = EmrCalculationUtils.datetimeResultForPatient(transferredOut, ptId);

            //find the initial art start date
            Date initialArtStart = EmrCalculationUtils.datetimeResultForPatient(onART, ptId);

            //find date defaulted
            Date defaultedDate = EmrCalculationUtils.datetimeResultForPatient(defaulted, ptId);

            if(classifiedLTFU != null) {
                dateLost = (Date) classifiedLTFU.getDateLost();
            }

            if(initialArtStart != null && years != null && onlyThoseOnArt.contains(ptId) && (yearsSince(initialArtStart, context.getNow()) > (years * 12))) {
                artInitiationDate = initialArtStart;

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(artInitiationDate);
                calendar.add(Calendar.YEAR, years);
                //set the context to only use this date
                resultsDate = calendar.getTime();

                Calendar calendarRunAfter = Calendar.getInstance();
                calendarRunAfter.setTime(artInitiationDate);
                calendarRunAfter.add(Calendar.YEAR, (years - 1));
                runAfter = calendarRunAfter.getTime();


                //if dead in the first year after enrollment, we cascade that for the all the years
                if (artInitiationDate != null && dod != null && resultsDate != null && dod.before(resultsDate) && dod.after(artInitiationDate)) {
                    value = "D";
                } else {

                    if (runAfter != null && dateTo != null && dateTo.before(resultsDate) && dateTo.after(runAfter)) {
                        value = "T";
                    } else if (runAfter != null && dateLost != null && dateLost.before(resultsDate) && dateLost.after(runAfter)) {
                        value = "L";
                    } else if (runAfter != null && defaultedDate != null && defaultedDate.after(runAfter)) {
                        value = "F";
                    } else {
                        value = "V";

                    }
                }
            }

            ret.put(ptId, new SimpleResult(value, this));
        }

        return ret;
    }

    private  int yearsSince(Date date1, Date date2) {
        DateTime d1 = new DateTime(date1.getTime());
        DateTime d2 = new DateTime(date2.getTime());
        return Months.monthsBetween(d1, d2).getMonths();
    }
}
