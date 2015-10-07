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
import org.openmrs.module.kenyaemr.calculation.library.models.TransferInAndDate;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by codehub on 10/6/15.
 */
public class IsArtTransferOutAndHasDateCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params,PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;
        Concept transferOutStatus = Dictionary.getConcept(Dictionary.TRANSFERRED_OUT);
        Concept transferOutDate = Dictionary.getConcept(Dictionary.DATE_TRANSFERRED_OUT);
        Concept reasonForExit = Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION);
        CalculationResultMap artStartDateMap = calculate(new InitialArtStartDateCalculation(), cohort, context);


        if(outcomePeriod != null) {

            context.setNow(DateUtil.adjustDate(DateUtil.getStartOfMonth(context.getNow()), outcomePeriod, DurationUnit.MONTHS));
        }

        CalculationResultMap lastReasonForExit = Calculations.lastObs(reasonForExit, cohort, context);
        CalculationResultMap lastTransferOutDate = Calculations.lastObs(transferOutDate, cohort, context);

        for(Integer ptId : cohort) {
            TransferInAndDate transferOutAndDate = null;
            Date transferOutDateValid = null;

            Obs reasonForExitObs = EmrCalculationUtils.obsResultForPatient(lastReasonForExit, ptId);
            Obs transferOutDateObs = EmrCalculationUtils.obsResultForPatient(lastTransferOutDate, ptId);
            Date artStartDate = EmrCalculationUtils.resultForPatient(artStartDateMap, ptId);

            if(artStartDate != null && outcomePeriod != null) {
                Date futureDate = DateUtil.adjustDate(artStartDate, outcomePeriod, DurationUnit.MONTHS);

                if(transferOutDateObs != null && transferOutDateObs.getValueDatetime().before(futureDate)){
                    transferOutDateValid = transferOutDateObs.getValueDatetime();
                }

                if(reasonForExitObs != null && transferOutDateValid != null && reasonForExitObs.getValueCoded().equals(transferOutStatus)) {
                    transferOutAndDate = new TransferInAndDate("Yes", transferOutDateValid);
                }
                else if(reasonForExitObs != null && transferOutDateValid == null && reasonForExitObs.getValueCoded().equals(transferOutStatus) && reasonForExitObs.getObsDatetime().before(futureDate)) {
                    transferOutAndDate = new TransferInAndDate("Yes", reasonForExitObs.getObsDatetime());
                }

                else if(reasonForExitObs == null && transferOutDateValid != null) {
                    transferOutAndDate = new TransferInAndDate("Yes", transferOutDateValid);
                }
                else {
                    transferOutAndDate = new TransferInAndDate("No", null);
                }

            }
            ret.put(ptId, new SimpleResult(transferOutAndDate, this));

        }
        return ret;
    }
}