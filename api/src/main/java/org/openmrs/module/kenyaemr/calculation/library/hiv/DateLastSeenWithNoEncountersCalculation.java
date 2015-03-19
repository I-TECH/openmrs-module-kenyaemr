package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculates the last date of the encounter regardless of the encounter types taken.
 */
public class DateLastSeenWithNoEncountersCalculation  extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
                                         PatientCalculationContext context) {
        CalculationResultMap lastEncounter = Calculations.lastEncounter(null, cohort, context);
        CalculationResultMap result = new CalculationResultMap();
        for (Integer ptId : cohort) {
            Encounter encounterInfo = EmrCalculationUtils.encounterResultForPatient(lastEncounter, ptId);
            Date dateLastSeen = null;
            if(encounterInfo != null){
                dateLastSeen = encounterInfo.getEncounterDatetime();
            }
            result.put(ptId, new SimpleResult(dateLastSeen, this));
        }
        return  result;
    }
}

