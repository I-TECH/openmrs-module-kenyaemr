package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.*;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.*;

/**
 * Calculates the pregnancy and edd.
 */
public class PregnancyAndEDDCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Set<Integer> aliveAndFemale = Filters.female(cohort, context);

        Concept yes = Dictionary.getConcept(Dictionary.YES);
        CalculationResultMap pregStatusObss = Calculations.allObs(Dictionary.getConcept(Dictionary.PREGNANCY_STATUS), aliveAndFemale, context);
        CalculationResultMap edd = Calculations.allObs(Dictionary.getConcept(Dictionary.EXPECTED_DATE_OF_DELIVERY), aliveAndFemale, context);

        CalculationResultMap ret = new CalculationResultMap();

        for(Integer ptId:cohort) {
            String result = null;
            ListResult listResult = (ListResult) pregStatusObss.get(ptId);
            ListResult listResultEdd = (ListResult) edd.get(ptId);
            //Obs eddDate = EmrCalculationUtils.obsResultForPatient(edd, ptId);

            List<Obs> pregnanciesIn2012 = new ArrayList<Obs>();
            List<Obs> eddsIn2012 = new ArrayList<Obs>();

            if (listResult != null) {
                List<Obs> listOfPregnancies = CalculationUtils.extractResultValues(listResult);

                for(Obs obs : listOfPregnancies ) {

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(obs.getObsDatetime());
                    int year = cal.get(Calendar.YEAR);

                    if(year == 2012) {
                        pregnanciesIn2012.add(obs);
                    }
                }
            }

            if(listResultEdd != null) {
                List<Obs> listOfPregnanciesEdd = CalculationUtils.extractResultValues(listResultEdd);

                for(Obs obs : listOfPregnanciesEdd ) {

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(obs.getObsDatetime());
                    int year = cal.get(Calendar.YEAR);

                    if(year == 2012) {
                        eddsIn2012.add(obs);
                    }
                }
            }

            //go through the populated lists by picking the last observation
            if( !(pregnanciesIn2012.isEmpty()) && !(eddsIn2012.isEmpty())) {

                result ="Y"+"="+eddsIn2012.get(eddsIn2012.size() -1).getValueDatetime();
            }


            ret.put(ptId, new SimpleResult(result, this));
        }

        return ret;
    }
}
