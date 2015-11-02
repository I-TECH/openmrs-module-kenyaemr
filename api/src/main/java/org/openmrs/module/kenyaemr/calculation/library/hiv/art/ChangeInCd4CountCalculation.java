package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.models.Cd4ValueAndDate;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by codehub on 06/07/15.
 */
public class ChangeInCd4CountCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

        CalculationResultMap ret = new CalculationResultMap();

        Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;
        CalculationResultMap artInitiationDate = calculate(new InitialArtStartDateCalculation(), cohort, context);

        if(outcomePeriod != null) {
            context.setNow(DateUtil.adjustDate(context.getNow(), outcomePeriod, DurationUnit.MONTHS));
        }


        CalculationResultMap allCd4s = currentCd4(cohort, context, outcomePeriod);
        CalculationResultMap baselineCd4 = calculate(new BaselineCd4CountAndDateCalculation(), cohort, context);

        for(Integer ptId: cohort) {

            Date artInitiationDt = EmrCalculationUtils.datetimeResultForPatient(artInitiationDate, ptId);
            Double baselineCd4Count = null;
            Double currentCd4Count = null;
            Double diff = null;
            Cd4ValueAndDate cd4ValueAndDate = EmrCalculationUtils.resultForPatient(baselineCd4, ptId);
            Obs cd4Counts = EmrCalculationUtils.obsResultForPatient(allCd4s, ptId);


            if(artInitiationDt != null) {
                if(cd4ValueAndDate != null){
                    baselineCd4Count = cd4ValueAndDate.getCd4Value();
                                    }
                if(cd4Counts != null){
                    currentCd4Count = cd4Counts.getValueNumeric();
                }
            }
            if(baselineCd4Count != null && currentCd4Count != null){
                diff = currentCd4Count - baselineCd4Count;
            }


            ret.put(ptId, new SimpleResult(diff, this));
        }
        return ret;
    }
    CalculationResultMap currentCd4(Collection<Integer> cohort, PatientCalculationContext context, Integer outcomePeriod){
        CalculationResultMap artInitiationDate = calculate(new InitialArtStartDateCalculation(), cohort, context);
        CalculationResultMap allCd4s = Calculations.allObs(Dictionary.getConcept(Dictionary.CD4_COUNT), cohort, context);
        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
            Date artInitiationDt = EmrCalculationUtils.datetimeResultForPatient(artInitiationDate, ptId);
            ListResult listResult = (ListResult) allCd4s.get(ptId);
            List<Obs> allObsList = CalculationUtils.extractResultValues(listResult);
            Obs cd4Val = null;

            List<Obs> validListObs = new ArrayList<Obs>();

            if(allObsList.size() > 0 && artInitiationDt != null && outcomePeriod != null) {
                Date outcomeDate = DateUtil.adjustDate(DateUtil.adjustDate(artInitiationDt, outcomePeriod, DurationUnit.MONTHS), 1, DurationUnit.DAYS);
                for(Obs obs:allObsList) {
                    if(obs.getObsDatetime().before(outcomeDate) && obs.getObsDatetime().after(dateLimit(artInitiationDt, 30))) {
                        validListObs.add(obs);
                    }
                }

            }

            if(validListObs.size() > 0) {
                cd4Val = validListObs.get(validListObs.size() - 1);
            }

            ret.put(ptId, new SimpleResult(cd4Val, this));
        }
        return ret;
    }

    private  Date dateLimit(Date date1, Integer days) {

        return DateUtil.adjustDate(date1, days, DurationUnit.DAYS);
    }
}
