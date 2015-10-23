package org.openmrs.module.kenyaemr.reporting.builder.hiv;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by codehub on 10/23/15.
 * Supply a concept id and get a list of obs
 */
public class ConceptObsListCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> map, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        Concept returnVisitDateConcept = Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE);

        CalculationResultMap returnVisitDateMap = Calculations.allObs(returnVisitDateConcept, cohort, context);

        for(Integer ptId: cohort){
            ListResult listResult = (ListResult) returnVisitDateMap.get(ptId);
            Date returnDate=null;
            List<Obs> allObs = CalculationUtils.extractResultValues(listResult);
            if(allObs.size() > 0){

                if(allObs.size() == 1){
                    returnDate = allObs.get(0).getValueDatetime();
                }
                else {
                    returnDate = allObs.get(allObs.size() - 2).getValueDatetime();
                }

            }
            ret.put(ptId, new SimpleResult(returnDate, this));
        }
        return ret;
    }
}
