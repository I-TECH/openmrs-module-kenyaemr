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
 * Calculates the value and the date of cd4 prior art initiation
 */
public class LatestCd4PriorToArtInitiationCalculation extends AbstractPatientCalculation{

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> map, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        CalculationResultMap artStartDateMap = calculate(new InitialArtStartDateCalculation(), cohort, context);


        for(Integer ptId:cohort) {
            Date arvStartDate = EmrCalculationUtils.datetimeResultForPatient(artStartDateMap, ptId);
            EarliestCd4 earliestCd4 = null;
            if(arvStartDate != null) {
                CalculationResultMap lasCd4BeforeArtInitiation = Calculations.lastObsOnOrBefore(Dictionary.getConcept(Dictionary.CD4_COUNT), arvStartDate, Arrays.asList(ptId), context);

                Obs cd4Obs = EmrCalculationUtils.obsResultForPatient(lasCd4BeforeArtInitiation, ptId);

                if(cd4Obs != null) {
                    earliestCd4 = new EarliestCd4(cd4Obs.getObsDatetime(), cd4Obs.getValueNumeric());
                }
            }

            ret.put(ptId, new SimpleResult(earliestCd4, this));

        }
        return ret;
    }
}
