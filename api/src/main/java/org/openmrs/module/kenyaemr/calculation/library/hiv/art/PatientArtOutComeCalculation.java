package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.DateClassifiedLTFUCalculation;
import org.openmrs.module.kenyaemr.calculation.library.models.LostToFU;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.DateOfDeathCalculation;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by codehub on 06/07/15.
 */
public class PatientArtOutComeCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Integer months = (parameterValues != null && parameterValues.containsKey("months")) ? (Integer) parameterValues.get("months") : null;
        CalculationResultMap onARTInitial = calculate(new InitialArtStartDateCalculation(), cohort, context);

        if(months != null) {
            context.setNow(DateUtil.adjustDate(context.getNow(), months, DurationUnit.MONTHS));
        }
        CalculationResultMap deadPatients = calculate(new DateOfDeathCalculation(), cohort, context);
        CalculationResultMap transferredOut = calculate(new TransferOutDateCalculation(), cohort, context);
        CalculationResultMap defaulted = calculate(new DateDefaultedCalculation(), cohort, context);
        CalculationResultMap ltfu = calculate(new DateClassifiedLTFUCalculation(), cohort, context);
        CalculationResultMap stoppedArtMap = calculate(new StoppedARTDateCalculation(), cohort, context);


        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
            String status = null;
            Date dateLost = null;
            Date initialArtStart = EmrCalculationUtils.datetimeResultForPatient(onARTInitial, ptId);
            Date dod = EmrCalculationUtils.datetimeResultForPatient(deadPatients, ptId);
            Date dateTo = EmrCalculationUtils.datetimeResultForPatient(transferredOut, ptId);
            Date defaultedDate = EmrCalculationUtils.datetimeResultForPatient(defaulted, ptId);
            LostToFU classifiedLTFU = EmrCalculationUtils.resultForPatient(ltfu, ptId);
            if(classifiedLTFU != null) {
                dateLost = (Date) classifiedLTFU.getDateLost();
            }
            String stoppedDate = EmrCalculationUtils.resultForPatient(stoppedArtMap, ptId);
            System.out.println("The patient::::"+ptId);
            System.out.println("Art start date is ::::"+initialArtStart);
            System.out.println("Dead date is ::::"+dod);
            System.out.println("To date is ::::"+dateTo);
            System.out.println("Defaulted  date is ::::"+defaultedDate);
            System.out.println("LTFU date is "+dateLost);

            if(initialArtStart != null && months != null) {
                Date futureDate = DateUtil.adjustDate(DateUtil.adjustDate(initialArtStart, months, DurationUnit.MONTHS), 1, DurationUnit.DAYS);

                status = "Alive and on ART";

                if(dod != null && (dod.before(futureDate)) && dod.after(initialArtStart)) {
                    status = "Died";
                }
                else if(dateTo != null && (dateTo.before(futureDate)) && dateTo.after(initialArtStart)) {
                    status = "Transferred out";
                }

                else if(stoppedDate != null && dateLost != null) {
                    try {
                        Date stDate = artStoppedDate(stoppedDate);
                        if(stDate.after(initialArtStart) && stDate.before(futureDate) && stDate.before(dateLost) && dateLost.before(new Date())) {
                            status = "LTFU";
                        }
                        else if(stDate.after(initialArtStart) && stDate.before(futureDate) && stDate.after(dateLost)) {
                            status = "Stopped ART";
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else if(stoppedDate != null) {
                    try {
                        Date stDate = artStoppedDate(stoppedDate);
                        if(stDate.after(initialArtStart) && stDate.before(futureDate)) {
                            status = "Stopped ART";
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                else if(dateLost != null && (dateLost.before(futureDate)) && dateLost.after(initialArtStart) && dateLost.before(new Date())){
                    status = "LTFU";
                }
                else if(defaultedDate != null && (defaultedDate.before(futureDate)) && defaultedDate.after(initialArtStart) && defaultedDate.before(new Date())){
                    status = "Defaulted";
                }



            }
            ret.put(ptId, new SimpleResult(status, this));
        }
        return  ret;
    }

    Date artStoppedDate(String string) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.parse(string);

    }

}
