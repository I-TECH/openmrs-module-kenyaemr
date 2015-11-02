package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.IsTransferInAndHasDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.models.TransferInAndDate;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by codehub on 10/13/15.
 * This class will get those patient who are transferred in bit had started art from their facility
 * if artstart date is before transfer in date should return true
 * such that we can combine with other definition using AND NOT
 */
public class TransferredInAfterArtStartCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();
        CalculationResultMap transferIns = calculate(new IsTransferInAndHasDateCalculation(), cohort, context);
        CalculationResultMap artStart = calculate(new InitialArtStartDateCalculation(), cohort, context);
        for(Integer ptId: cohort) {
            boolean isTransferredInWhileOnArt = false;
            TransferInAndDate transferInAndDate = EmrCalculationUtils.resultForPatient(transferIns, ptId);
            Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(artStart, ptId);
            if(transferInAndDate != null && transferInAndDate.getDate() !=  null && artStartDate != null) {
                if(artStartDate.before(transferInAndDate.getDate())) {
                    isTransferredInWhileOnArt = true;
                }
            }
            ret.put(ptId, new BooleanResult(isTransferredInWhileOnArt, this));


        }
        return ret;
    }
}
