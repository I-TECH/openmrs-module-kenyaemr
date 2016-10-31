package org.openmrs.module.kenyaemr.calculation.library.hiv.pre_art;

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.FirstProgramEnrollment;
import org.openmrs.module.kenyaemr.calculation.library.hiv.IsTransferInAndHasDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.models.TransferInAndDate;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by codehub on 11/19/15.
 */
public class TransferredInAfterEnrollmentCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
        Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;
        CalculationResultMap ret = new CalculationResultMap();
        CalculationResultMap transferIns = calculate(new IsTransferInAndHasDateCalculation(), cohort, context);
        CalculationResultMap lastProgramEnrollmentDate = calculate(new FirstProgramEnrollment(), cohort, context);
        for(Integer ptId: cohort) {
            boolean isTransferredInAfterEnrollment = false;
            TransferInAndDate transferInAndDate = EmrCalculationUtils.resultForPatient(transferIns, ptId);
            Date enrollmentDate = EmrCalculationUtils.datetimeResultForPatient(lastProgramEnrollmentDate, ptId);

            if(transferInAndDate != null && transferInAndDate.getDate() !=  null && enrollmentDate != null && outcomePeriod != null) {
                if(transferInAndDate.getDate().after(DateUtil.adjustDate(enrollmentDate, outcomePeriod, DurationUnit.MONTHS))) {
                    isTransferredInAfterEnrollment = true;
                }
            }
            ret.put(ptId, new BooleanResult(isTransferredInAfterEnrollment, this));


        }
        return ret;
    }
}