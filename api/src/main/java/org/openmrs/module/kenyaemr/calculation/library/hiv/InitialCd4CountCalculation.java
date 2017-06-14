package org.openmrs.module.kenyaemr.calculation.library.hiv;

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
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.DateOfEnrollmentArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.models.Cd4ValueAndDate;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by codehub on 18/06/15.
 */
public class InitialCd4CountCalculation  extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();
        CalculationResultMap allCd4s = Calculations.allObs(Dictionary.getConcept(Dictionary.CD4_COUNT), cohort, context);
        CalculationResultMap enrollment = calculate(new DateOfEnrollmentArtCalculation(), cohort, context);

        for(Integer ptId:cohort) {

            Cd4ValueAndDate cd4ValueAndDate = null;
            ListResult listResult = (ListResult) allCd4s.get(ptId);
            Date enrollmentDate = EmrCalculationUtils.datetimeResultForPatient(enrollment, ptId);
            List<Obs> allObsAsList = CalculationUtils.extractResultValues(listResult);
            List<Obs> cd4sNearest = new ArrayList<Obs>();
            for(Obs obs:allObsAsList){
                Date obsDate = obs.getObsDatetime();
                if(enrollmentDate != null && obsDate.before(dateLimit(enrollmentDate, 91))) {
                    cd4sNearest.add(obs);
                }
            }
            if(cd4sNearest.size() > 0) {
                cd4ValueAndDate = new Cd4ValueAndDate((cd4sNearest.get(cd4sNearest.size() -1)).getValueNumeric(), (cd4sNearest.get(cd4sNearest.size() -1)).getObsDatetime());
            }
            ret.put(ptId, new SimpleResult(cd4ValueAndDate, this));

        }
        return ret;
    }

    private  Date dateLimit(Date date1, Integer days) {

        return DateUtil.adjustDate(date1, days, DurationUnit.DAYS);
    }

}
