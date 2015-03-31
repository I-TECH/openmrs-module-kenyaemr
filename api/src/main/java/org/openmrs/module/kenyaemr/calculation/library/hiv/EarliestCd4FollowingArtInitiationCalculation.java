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
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.reporting.model.EarliestCd4;

import java.util.*;

/**
 * Calculates the date and value of earliest cd4 prior to art initiation
 */
public class EarliestCd4FollowingArtInitiationCalculation extends AbstractPatientCalculation{

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> map, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        CalculationResultMap artStartDateMap = calculate(new InitialArtStartDateCalculation(), cohort, context);
        CalculationResultMap cd4Obs = Calculations.allObs(Dictionary.getConcept(Dictionary.CD4_COUNT), cohort,context);

        for(Integer ptId:cohort) {
            Date arvStartDate = EmrCalculationUtils.datetimeResultForPatient(artStartDateMap, ptId);
            EarliestCd4 earliestCd4 = null;

            ListResult listResultObs = (ListResult) cd4Obs.get(ptId);
            List<Obs> obsList = CalculationUtils.extractResultValues(listResultObs);

            List<Obs> filteredList = new ArrayList<Obs>();
            if (arvStartDate != null) {

                for(Obs obs:obsList) {
                    if((obs.getObsDatetime().after(arvStartDate) || obs.getObsDatetime().equals(arvStartDate)) && (obs.getObsDatetime().before(context.getNow()))) {
                        filteredList.add(obs);
                    }
                }

                if(!(filteredList.isEmpty())) {
                    earliestCd4 = new EarliestCd4(filteredList.get(0).getObsDatetime(), filteredList.get(0).getValueNumeric());
                }

            }

            ret.put(ptId, new SimpleResult(earliestCd4, this));
        }
        return ret;
    }
}
