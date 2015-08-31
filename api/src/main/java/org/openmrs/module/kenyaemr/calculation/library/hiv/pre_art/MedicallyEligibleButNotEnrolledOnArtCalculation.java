package org.openmrs.module.kenyaemr.calculation.library.hiv.pre_art;

import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.DateAndReasonFirstMedicallyEligibleForArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.models.PatientEligibility;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Find date of hiv eligibilty if <= date of forst outcome
 */
public class MedicallyEligibleButNotEnrolledOnArtCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();
        Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;
        //find a new patient calculation context
        PatientCalculationService service = Context.getService(PatientCalculationService.class);
        PatientCalculationContext newContext = service.createCalculationContext();
        if(outcomePeriod == null){
            newContext = context;
        }
        else {
            newContext.setNow(DateUtil.adjustDate(DateUtil.getStartOfMonth(context.getNow()), outcomePeriod, DurationUnit.MONTHS));
        }

        CalculationResultMap eligibilityCriteria = calculate(new DateAndReasonFirstMedicallyEligibleForArtCalculation(), cohort, newContext);
        CalculationResultMap startedART = calculate(new InitialArtStartDateCalculation(), cohort, newContext);

        for(Integer ptId: cohort) {
            boolean eligibleButNotEnrolled = false;
            Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(startedART, ptId);
            PatientEligibility patientEligibility = EmrCalculationUtils.resultForPatient(eligibilityCriteria, ptId);
            if(patientEligibility != null && patientEligibility.getEligibilityDate() != null && startedART == null) {
                eligibleButNotEnrolled = true;
            }
            ret.put(ptId, new BooleanResult(eligibleButNotEnrolled, this));
        }
        return ret;
    }
}
