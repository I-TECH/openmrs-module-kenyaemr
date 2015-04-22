package org.openmrs.module.kenyaemr.calculation.library.cohort.analysis;

import org.joda.time.DateTime;
import org.joda.time.Days;
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

        Integer days = (params != null && params.containsKey("days")) ? (Integer) params.get("days") : null;
        CalculationResultMap ret = new CalculationResultMap();


        ////find the program map enrolled in
        CalculationResultMap deadPatients = calculate(new DateOfDeathCalculation(), cohort, context);
        CalculationResultMap defaulted = calculate(new DateDefaultedCalculation(), cohort, context);
        CalculationResultMap ltfu = calculate(new DateClassifiedLTFUCalculation(), cohort, context);
        CalculationResultMap transferredOut = calculate(new TransferOutDateCalculation(), cohort, context);
        CalculationResultMap onART = calculate(new InitialArtStartDateCalculation(), cohort, context);

        for(Integer ptId : cohort) {
            String value = null;
            Date resultsDate;
            Date dateLost = null;

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

            if(initialArtStart != null && days != null && (daysSince(initialArtStart, context.getNow()) > days)) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(initialArtStart);
                calendar.add(Calendar.DATE, days);
                resultsDate = calendar.getTime();


                //if dead in the first year after enrollment, we cascade that for the all the years
                if (dod != null && resultsDate != null && dod.before(resultsDate) && dod.after(initialArtStart)) {
                    value = "D";
                }

                else if (dateTo != null && dateTo.before(resultsDate)) {
                        value = "T";
                    }
                else if (dateLost != null && dateLost.before(resultsDate)) {
                        value = "L";
                    }
                else if (defaultedDate != null && defaultedDate.after(initialArtStart)) {
                        value = "F";
                    }
                else {
                        value = "V";

                    }
            }

            ret.put(ptId, new SimpleResult(value, this));
        }

        return ret;
    }

    private  int daysSince(Date date1, Date date2) {
        DateTime d1 = new DateTime(date1.getTime());
        DateTime d2 = new DateTime(date2.getTime());
        return Days.daysBetween(d1, d2).getDays();
    }
}
