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
public class BaselineCd4CountAndDateCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();
        Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;

        CalculationResultMap artInitiationDate = calculate(new InitialArtStartDateCalculation(), cohort, context);

        if(outcomePeriod != null) {
            context.setNow(DateUtil.adjustDate(context.getNow(), outcomePeriod, DurationUnit.MONTHS));
        }
        CalculationResultMap allCd4 = Calculations.allObs(Dictionary.getConcept(Dictionary.CD4_COUNT), cohort, context);

        for(Integer ptId: cohort) {
            Cd4ValueAndDate cd4ValueAndDate = null;
            Date artInitiationDt = EmrCalculationUtils.datetimeResultForPatient(artInitiationDate, ptId);
            ListResult listResult = (ListResult) allCd4.get(ptId);
            List<Obs> validCd4 = new ArrayList<Obs>();
            List<Obs> allCd4Obs = CalculationUtils.extractResultValues(listResult);
            if(allCd4Obs.size() > 0 && artInitiationDt != null) {
                for (Obs obs : allCd4Obs) {
                    if (obs.getObsDatetime().before(dateLimit(artInitiationDt, 31)) && obs.getObsDatetime().after(dateLimit(artInitiationDt, -121))) {
                       validCd4.add(obs);
                    }
                }
            }
            if(validCd4.size() > 0) {
                cd4ValueAndDate =  new Cd4ValueAndDate(validCd4.get(validCd4.size() - 1).getValueNumeric(), validCd4.get(validCd4.size() - 1).getObsDatetime());
            }

            ret.put(ptId, new SimpleResult(cd4ValueAndDate, this));


        }
        return ret;
    }


    private  Date dateLimit(Date date1, Integer days) {

        return DateUtil.adjustDate(date1, days, DurationUnit.DAYS);
    }
}
