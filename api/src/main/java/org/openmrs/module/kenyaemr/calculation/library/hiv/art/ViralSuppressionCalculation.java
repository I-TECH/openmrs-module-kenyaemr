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
public class ViralSuppressionCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params,PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        CalculationResultMap artStartDateMap = calculate(new InitialArtStartDateCalculation(), cohort, context);

        Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;
        if(outcomePeriod != null) {
            context.setNow(DateUtil.adjustDate(context.getNow(), outcomePeriod, DurationUnit.MONTHS));
        }

        CalculationResultMap allVL = Calculations.allObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), cohort, context);

        for(Integer ptId:cohort) {
            String suppressed = null;
            Cd4ValueAndDate vlValue = null;
            ListResult listResult = (ListResult) allVL.get(ptId);
            List<Obs> obsList = CalculationUtils.extractResultValues(listResult);
            List<Obs> validVls = new ArrayList<Obs>();
            Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(artStartDateMap, ptId);
            if(obsList.size() > 0 && artStartDate != null && outcomePeriod != null) {
                Date outcomeDate = DateUtil.adjustDate(DateUtil.adjustDate(artStartDate, outcomePeriod, DurationUnit.MONTHS), 1, DurationUnit.DAYS);


                for(Obs obs: obsList) {
                    if(obs.getObsDatetime().before(outcomeDate) && obs.getObsDatetime().after(dateLimit(outcomeDate, -366))) {
                        validVls.add(obs);
                    }
                }

                if(validVls.size() > 0) {
                    vlValue = new Cd4ValueAndDate(validVls.get(validVls.size() -1).getValueNumeric(),validVls.get(validVls.size() -1).getObsDatetime() );
                }
                if(vlValue != null && vlValue.getCd4Value() < 1000) {
                    suppressed = "Yes";
                }

                if(vlValue != null && vlValue.getCd4Value() >= 1000) {
                    suppressed = "No";
                }
            }

            ret.put(ptId, new SimpleResult(suppressed, this));
        }
        return  ret;

    }

    private  Date dateLimit(Date date1, Integer days) {

        return DateUtil.adjustDate(date1, days, DurationUnit.DAYS);
    }
}
