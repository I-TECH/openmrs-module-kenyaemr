package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 9/8/15.
 */
public class LastReturnVisitDateArtAnalysisCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

        Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;

        CalculationResultMap initialArtStart = calculate(new InitialArtStartDateCalculation(), cohort, context);



        if(outcomePeriod != null){
            context.setNow(DateUtil.adjustDate(context.getNow(), outcomePeriod, DurationUnit.MONTHS));
        }
        Set<Integer> alive = Filters.alive(cohort, context);
        CalculationResultMap returnVisitMap = Calculations.allObs(Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE), alive, context);
        Set<Integer> transferredOut = CalculationUtils.patientsThatPass(calculate(new IsTransferOutCalculation(), cohort, context));

        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId: cohort) {
            ListResult listResult = (ListResult) returnVisitMap.get(ptId);
            Date artStartDate = EmrCalculationUtils.resultForPatient(initialArtStart, ptId);
            List<Obs> allReturnVisits = CalculationUtils.extractResultValues(listResult);
            List<Obs> correctList = new ArrayList<Obs>();
            Date returnVisitDate = null;
            if(artStartDate != null && outcomePeriod != null && allReturnVisits.size() > 0) {
                Date futureDate = DateUtil.adjustDate(DateUtil.adjustDate(artStartDate, outcomePeriod, DurationUnit.MONTHS), 1, DurationUnit.DAYS);
                for(Obs obs:allReturnVisits) {
                    if(obs.getObsDatetime().before(futureDate)) {
                        correctList.add(obs);
                    }
                }
                if(correctList.size() > 0) {
                    returnVisitDate = correctList.get(correctList.size() - 1).getValueDatetime();
                }
                if(transferredOut.contains(ptId)) {
                    returnVisitDate = null;
                }
            }
            ret.put(ptId, new SimpleResult(returnVisitDate, this));

        }
        return ret;
    }
}
