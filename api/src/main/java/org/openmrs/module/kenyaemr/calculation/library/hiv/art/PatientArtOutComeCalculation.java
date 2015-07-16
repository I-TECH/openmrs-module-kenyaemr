package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.AliveAndOnFollowUpCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.DateClassifiedLTFUCalculation;
import org.openmrs.module.kenyaemr.calculation.library.models.LostToFU;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.DateOfDeathCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 06/07/15.
 */
public class PatientArtOutComeCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Integer months = (parameterValues != null && parameterValues.containsKey("months")) ? (Integer) parameterValues.get("months") : null;

        PatientCalculationService patientCalculationService = Context.getService(PatientCalculationService.class);

        PatientCalculationContext context1 = patientCalculationService.createCalculationContext();
        if(months == null) {
            months = 0;
        }
        CalculationResultMap enrolledHere = calculate( new DateOfEnrollmentCalculation(),cohort, context);
        CalculationResultMap onARTInitial = calculate(new InitialArtStartDateCalculation(), cohort, context);

        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
            String status = null;
            Date dateLost = null;

            //find the initial art start date
            Date initialArtStart = EmrCalculationUtils.datetimeResultForPatient(onARTInitial, ptId);

            Date patientProgramDate = EmrCalculationUtils.resultForPatient(enrolledHere, ptId);

            if(initialArtStart != null && monthsSince(initialArtStart, new Date()) >= months) {

                Calendar calendarAfterART = Calendar.getInstance();
                calendarAfterART.setTime(initialArtStart);
                calendarAfterART.add(Calendar.MONTH, months);
                context1.setNow(calendarAfterART.getTime());

                CalculationResultMap deadPatients = calculate(new DateOfDeathCalculation(), cohort, context1);
                Date dod = EmrCalculationUtils.datetimeResultForPatient(deadPatients, ptId);

                CalculationResultMap transferredOut = calculate(new TransferOutDateCalculation(), cohort, context1);
                //find date transferred out
                Date dateTo = EmrCalculationUtils.datetimeResultForPatient(transferredOut, ptId);

                CalculationResultMap defaulted = calculate(new DateDefaultedCalculation(), cohort, context1);
                //find date defaulted
                Date defaultedDate = EmrCalculationUtils.datetimeResultForPatient(defaulted, ptId);

                CalculationResultMap ltfu = calculate(new DateClassifiedLTFUCalculation(), cohort, context1);
                LostToFU classifiedLTFU = EmrCalculationUtils.resultForPatient(ltfu, ptId);
                if(classifiedLTFU != null) {
                    dateLost = (Date) classifiedLTFU.getDateLost();
                }

                Set<Integer> onART = CalculationUtils.patientsThatPass(calculate(new OnArtCalculation(), cohort, context1));

                status = "Alive and on ART";

                if(!(onART.contains(ptId))) {
                    status = "Stopped ART";
                }

                if(dateTo != null && (dateTo.before(calendarAfterART.getTime()) || dateTo.equals(calendarAfterART.getTime()))) {
                    status = "Transferred out";
                }

                if(defaultedDate != null && (defaultedDate.before(calendarAfterART.getTime()) || defaultedDate.equals(calendarAfterART.getTime()))){
                    status = "Defaulted";
                }

                if(dateLost != null && (dateLost.before(calendarAfterART.getTime()) || dateLost.equals(calendarAfterART.getTime()))){
                    status = "Lost to follow up";
                }

                if(dod != null && (dod.before(calendarAfterART.getTime()) || dod.equals(calendarAfterART.getTime()))) {
                    status = "Died";
                }

            }
            ret.put(ptId, new SimpleResult(status, this));
        }
        return  ret;
    }

    private  int monthsSince(Date date1, Date date2) {
        DateTime d1 = new DateTime(date1.getTime());
        DateTime d2 = new DateTime(date2.getTime());
        return Months.monthsBetween(d1, d2).getMonths();
    }
}
