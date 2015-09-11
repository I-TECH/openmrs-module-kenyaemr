package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.HivConstants;
import org.openmrs.module.kenyaemr.calculation.library.models.LostToFU;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculates the date a patient was declared lost
 */
public class DateClassifiedLTFUCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        //find the return visit date from the last encounter
        CalculationResultMap resultMap = calculate(new LastReturnVisitDateCalculation(), cohort, context);
        //find lost to follow up patients
        Set<Integer> lostPatients = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));

        CalculationResultMap ret = new CalculationResultMap();

        for (Integer ptId : cohort) {
            LostToFU classifiedLTFU = null;
            if (lostPatients.contains(ptId)) {
                SimpleResult lastScheduledReturnDateResults = (SimpleResult) resultMap.get(ptId);

                if (lastScheduledReturnDateResults != null) {
                    Date lastScheduledReturnDate = (Date) lastScheduledReturnDateResults.getValue();
                    if(lastScheduledReturnDate != null) {
                        classifiedLTFU = new LostToFU(true, DateUtil.adjustDate(lastScheduledReturnDate, HivConstants.LOST_TO_FOLLOW_UP_THRESHOLD_DAYS, DurationUnit.DAYS ));
                    }
                }

            }
            else {
                classifiedLTFU = new LostToFU(false, null);
            }

            ret.put(ptId, new SimpleResult(classifiedLTFU, this));
        }
        return ret;
    }
}