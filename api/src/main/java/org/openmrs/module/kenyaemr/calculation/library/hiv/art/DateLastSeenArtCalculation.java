package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Encounter;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by codehub on 9/8/15.
 */
public class DateLastSeenArtCalculation extends AbstractPatientCalculation {


    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

        Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;
        CalculationResultMap initialArtStart = calculate(new InitialArtStartDateCalculation(), cohort, context);

        if(outcomePeriod != null){
            context.setNow(DateUtil.adjustDate(DateUtil.getStartOfMonth(context.getNow()), outcomePeriod, DurationUnit.MONTHS));
        }

        CalculationResultMap lastEncounter = Calculations.allEncounters(null, cohort, context);


        CalculationResultMap result = new CalculationResultMap();
        for (Integer ptId : cohort) {
            ListResult allEncounters = (ListResult) lastEncounter.get(ptId);
            List<Encounter> encounterList = CalculationUtils.extractResultValues(allEncounters);
            Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(initialArtStart, ptId);
            if(outcomePeriod != null && artStartDate != null) {
                Date futureDate = DateUtil.adjustDate(DateUtil.adjustDate(artStartDate, outcomePeriod, DurationUnit.MONTHS), 1, DurationUnit.DAYS);
                Date encounterDate = null;
                List<Encounter> targetedEncounters = new ArrayList<Encounter>();
                if (encounterList.size() > 0) {
                    for (Encounter encounter : encounterList) {
                        if (encounter.getEncounterDatetime().before(futureDate)) {
                            targetedEncounters.add(encounter);
                        }
                    }
                    if (targetedEncounters.size() > 0) {
                        encounterDate = targetedEncounters.get(targetedEncounters.size() - 1).getEncounterDatetime();
                    }
                }
                if(encounterDate == null){
                    encounterDate = artStartDate;
                }

                result.put(ptId, new SimpleResult(encounterDate, this));
            }
        }
        return  result;
    }
}
