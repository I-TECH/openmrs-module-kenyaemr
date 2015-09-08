package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.joda.time.DateTime;
import org.joda.time.Days;
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
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastCd4CountCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastCd4PercentageCalculation;
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
public class LastCd4PercentCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
        Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;
        CalculationResultMap artInitiationDate = calculate(new InitialArtStartDateCalculation(), cohort, context);
        if(outcomePeriod != null) {
            context.setNow(DateUtil.adjustDate(context.getNow(), outcomePeriod, DurationUnit.MONTHS));
        }

        CalculationResultMap allCd4s = Calculations.allObs(Dictionary.getConcept(Dictionary.CD4_PERCENT), cohort, context);


        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
            Cd4ValueAndDate cd4ValueAndDate = null;
            Date artInitiationDt = EmrCalculationUtils.datetimeResultForPatient(artInitiationDate, ptId);
            ListResult listResult = (ListResult) allCd4s.get(ptId);
            List<Obs> allObsList = CalculationUtils.extractResultValues(listResult);
            List<Obs> validListObs = new ArrayList<Obs>();

            if(allObsList.size() > 0 && artInitiationDt != null ) {
                Date outcomeDate = DateUtil.adjustDate(DateUtil.adjustDate(artInitiationDt, outcomePeriod, DurationUnit.MONTHS), 1, DurationUnit.DAYS);
                for(Obs obs:allObsList) {
                    if(obs.getObsDatetime().before(outcomeDate) && obs.getObsDatetime().after(dateLimit(outcomeDate, -184))) {
                        validListObs.add(obs);
                    }
                }

            }

            if(validListObs.size() > 0) {
                cd4ValueAndDate = new Cd4ValueAndDate(validListObs.get(validListObs.size() - 1).getValueNumeric(), validListObs.get(validListObs.size() - 1).getObsDatetime());
            }
            ret.put(ptId, new SimpleResult(cd4ValueAndDate, this));
        }
        return ret;
    }

    private  Date dateLimit(Date date1, Integer days) {

        return DateUtil.adjustDate(date1, days, DurationUnit.DAYS);
    }
}