package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
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
        CalculationResultMap artStartDateMap = calculate(new InitialArtStartDateCalculation(), cohort, context);


        if(outcomePeriod != null) {

            context.setNow(DateUtil.adjustDate(DateUtil.getStartOfMonth(context.getNow()), outcomePeriod, DurationUnit.MONTHS));
        }

        CalculationResultMap transferredOutMap = calculate(new TransferOutDateCalculation(), cohort, context);

        for(Integer ptId : cohort) {
            TransferInAndDate transferOutAndDate = null;
            Date artStartDate = EmrCalculationUtils.resultForPatient(artStartDateMap, ptId);
            Date transOutDate = EmrCalculationUtils.datetimeResultForPatient(transferredOutMap, ptId);

            if(artStartDate != null && outcomePeriod != null) {
                Date futureDate = DateUtil.adjustDate(DateUtil.adjustDate(artStartDate, outcomePeriod, DurationUnit.MONTHS), 1, DurationUnit.DAYS);

                if(transOutDate != null && transOutDate.after(artStartDate) && transOutDate.before(futureDate)) {
                    transferOutAndDate = new TransferInAndDate("Yes", transOutDate);
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