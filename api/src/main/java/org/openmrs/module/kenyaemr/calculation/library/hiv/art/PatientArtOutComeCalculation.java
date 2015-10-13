package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
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

        Integer outcomePeriod = (parameterValues != null && parameterValues.containsKey("outcomePeriod")) ? (Integer) parameterValues.get("outcomePeriod") : null;
        CalculationResultMap onARTInitial = calculate(new InitialArtStartDateCalculation(), cohort, context);

        if(outcomePeriod != null) {
            context.setNow(DateUtil.adjustDate(context.getNow(), outcomePeriod, DurationUnit.MONTHS));
        }
        CalculationResultMap deadPatients = calculate(new DateOfDeathCalculation(), cohort, context);
        CalculationResultMap defaulted = calculate(new DateDefaultedCalculation(), cohort, context);
        CalculationResultMap ltfu = calculate(new DateClassifiedLTFUCalculation(), cohort, context);
        CalculationResultMap stoppedArtMap = calculate(new StoppedARTDateCalculation(), cohort, context);

        //code transfer out information
        Concept transferOutStatus = Dictionary.getConcept(Dictionary.TRANSFERRED_OUT);
        Concept transferOutDate = Dictionary.getConcept(Dictionary.DATE_TRANSFERRED_OUT);
        Concept reasonForExit = Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION);

        CalculationResultMap lastReasonForExit = Calculations.lastObs(reasonForExit, cohort, context);
        CalculationResultMap lastTransferOutDate = Calculations.lastObs(transferOutDate, cohort, context);


        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
            String status = null;
            Date dateLost = null;

            Date transferOutDateValid = null;
            Obs reasonForExitObs = EmrCalculationUtils.obsResultForPatient(lastReasonForExit, ptId);
            Obs transferOutDateObs = EmrCalculationUtils.obsResultForPatient(lastTransferOutDate, ptId);

            Date initialArtStart = EmrCalculationUtils.datetimeResultForPatient(onARTInitial, ptId);
            Date dod = EmrCalculationUtils.datetimeResultForPatient(deadPatients, ptId);
           // TransferInAndDate transferOutAndDate = EmrCalculationUtils.resultForPatient(transferredOut, ptId);
            Date defaultedDate = EmrCalculationUtils.datetimeResultForPatient(defaulted, ptId);
            LostToFU classifiedLTFU = EmrCalculationUtils.resultForPatient(ltfu, ptId);
            if(classifiedLTFU != null) {
                dateLost = (Date) classifiedLTFU.getDateLost();
            }
            String stoppedDate = EmrCalculationUtils.resultForPatient(stoppedArtMap, ptId);

            if(initialArtStart != null && outcomePeriod != null) {
                Date futureDate = DateUtil.adjustDate(DateUtil.adjustDate(initialArtStart, outcomePeriod, DurationUnit.MONTHS), 1, DurationUnit.DAYS);
                status = "Alive and on ART";

                if(transferOutDateObs != null && transferOutDateObs.getValueDatetime().before(futureDate)){
                    transferOutDateValid = transferOutDateObs.getValueDatetime();
                }

                else if(reasonForExitObs != null && transferOutDateObs != null && reasonForExitObs.getValueCoded().equals(transferOutStatus)) {
                    transferOutDateValid = transferOutDateObs.getValueDatetime();
                }
                else if(reasonForExitObs != null && transferOutDateObs == null && reasonForExitObs.getValueCoded().equals(transferOutStatus) && reasonForExitObs.getObsDatetime().before(futureDate)) {
                    transferOutDateValid = reasonForExitObs.getObsDatetime();
                }

                if(dod != null && (dod.before(futureDate)) && dod.after(initialArtStart)) {
                    status = "Died";
                }
                else if(transferOutDateValid != null){
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
