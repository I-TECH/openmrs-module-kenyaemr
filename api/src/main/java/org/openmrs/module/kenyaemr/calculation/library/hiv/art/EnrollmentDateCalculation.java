package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.calculation.BaseCalculation;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by agnes on 6/5/14.
 */
public class EnrollmentDateCalculation extends BaseCalculation implements PatientCalculation {
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> stringObjectMap, PatientCalculationContext context) {
        EncounterType encounterType = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_ENROLLMENT);

        CalculationResultMap lastEncounters = Calculations.lastEncounter(encounterType, cohort, context);

        CalculationResultMap result = new CalculationResultMap();

        for (Integer ptId : cohort){

            CalculationResult cr = lastEncounters.get(ptId);

            Date lastEncounterDate = null;
            if (cr != null){
                Encounter lastEncounter = (Encounter) cr.getValue();
                lastEncounterDate = lastEncounter.getEncounterDatetime();
            }
            result.put(ptId, new SimpleResult(lastEncounterDate, this));
        }
        return result;
    }
}
