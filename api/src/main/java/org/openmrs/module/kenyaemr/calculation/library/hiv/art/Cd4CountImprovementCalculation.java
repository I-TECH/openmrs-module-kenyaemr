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
public class Cd4CountImprovementCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;
        CalculationResultMap artInitiationDate = calculate(new InitialArtStartDateCalculation(), cohort, context);

        if(outcomePeriod != null) {
            context.setNow(DateUtil.adjustDate(context.getNow(), outcomePeriod, DurationUnit.MONTHS));
        }


        CalculationResultMap allCd4s = Calculations.allObs(Dictionary.getConcept(Dictionary.CD4_COUNT), cohort, context);
        CalculationResultMap baselineCd4 = calculate(new BaselineCd4CountAndDateCalculation(), cohort, context);

        for(Integer ptId: cohort) {
            String hasImproved = null;
            Date artInitiationDt = EmrCalculationUtils.datetimeResultForPatient(artInitiationDate, ptId);
            ListResult listResult = (ListResult) allCd4s.get(ptId);
            List<Obs> allObsList = CalculationUtils.extractResultValues(listResult);
            List<Obs> validListObsCurrentCd4 = new ArrayList<Obs>();
            Double baselineCd4Count = null;
            Double currentCd4Count = null;
            Double diff = null;
            Cd4ValueAndDate cd4ValueAndDate = EmrCalculationUtils.resultForPatient(baselineCd4, ptId);

            if(allObsList.size() > 0 && artInitiationDt != null && outcomePeriod != null ) {
                Date outcomeDate = DateUtil.adjustDate(DateUtil.adjustDate(artInitiationDt, outcomePeriod, DurationUnit.MONTHS), 1, DurationUnit.DAYS);
                for(Obs obs:allObsList) {
                    if(obs.getObsDatetime().before(outcomeDate) && obs.getObsDatetime().after(dateLimit(outcomeDate, -184))) {
                        validListObsCurrentCd4.add(obs);
                    }
                }

            }
            if(validListObsCurrentCd4.size() > 0) {
                currentCd4Count = validListObsCurrentCd4.get(validListObsCurrentCd4.size() - 1).getValueNumeric();
            }
            if(cd4ValueAndDate != null) {
                baselineCd4Count = cd4ValueAndDate.getCd4Value();
            }

            if(currentCd4Count != null && baselineCd4Count != null) {
                diff = currentCd4Count - baselineCd4Count;
            }

            if(diff != null && diff > 0.0) {
                hasImproved = "Yes";
            }
            else if(diff != null && diff <= 0.0) {
                hasImproved = "No";
            }

            ret.put(ptId, new SimpleResult(hasImproved, this));
        }

        return ret;
    }
    private  Date dateLimit(Date date1, Integer days) {

        return DateUtil.adjustDate(date1, days, DurationUnit.DAYS);
    }
}
