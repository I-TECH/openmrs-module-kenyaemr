package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.*;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;

import java.util.*;

/**
 * Calculates whether a patient is pregnant on NOT at art initiation
 */
public class CurrentPregnancyStatusAtARTEnrolmentCalculation extends AbstractPatientCalculation{

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> map, PatientCalculationContext context) {

        Set<Integer> aliveAndFemale = Filters.female(Filters.alive(cohort, context), context);

        Concept yes = Dictionary.getConcept(Dictionary.YES);
        CalculationResultMap pregStatusObss = Calculations.allObs(Dictionary.getConcept(Dictionary.PREGNANCY_STATUS), aliveAndFemale, context);

        CalculationResultMap artStartDateMap = calculate(new InitialArtStartDateCalculation(), cohort, context);
        CalculationResultMap ret = new CalculationResultMap();

        for (Integer ptId : cohort) {
            boolean result = false;
            ListResult pregResults = (ListResult) pregStatusObss.get(ptId);
            List<Obs> pregStatusObsList = CalculationUtils.extractResultValues(pregResults);
            Date arvStartDate = EmrCalculationUtils.datetimeResultForPatient(artStartDateMap, ptId);

            for(Obs obs : pregStatusObsList) {
                if(arvStartDate != null && (obs.getValueCoded().equals(yes)) && (obs.getObsDatetime().equals(arvStartDate))) {
                    result = true;
                    break;
                }
            }

            ret.put(ptId, new BooleanResult(result, this));
        }
        return ret;
    }
}
