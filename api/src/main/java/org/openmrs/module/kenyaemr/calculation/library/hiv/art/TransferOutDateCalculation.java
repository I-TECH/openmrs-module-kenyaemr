package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Created by derric on 6/5/14.
 */

/**
 * Calculates transfer out date for a patient
 */
public class TransferOutDateCalculation extends AbstractPatientCalculation {
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> stringObjectMap, PatientCalculationContext context) {

        Concept transferReasonConcept = Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION);
        EncounterType encounterType = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_DISCONTINUATION);

        CalculationResultMap lastEncounters = Calculations.lastEncounter(encounterType, cohort, context);
        CalculationResultMap transferReasons = Calculations.lastObs(transferReasonConcept, cohort, context);

        CalculationResultMap result = new CalculationResultMap();
        for (Integer ptId : cohort) {

            Encounter hivDicontinuation = EmrCalculationUtils.encounterResultForPatient(lastEncounters,ptId);//lastEncounters.get(ptId).getValue();
            Obs transferReason = EmrCalculationUtils.obsResultForPatient(transferReasons,ptId);

            if (hivDicontinuation != null && transferReason != null) {

                    if (hivDicontinuation.equals(transferReason.getEncounter())) {
                        Concept transfer = transferReason.getValueCoded();
                        if (transfer != null) {
                            if (transfer.equals(Dictionary.getConcept(Dictionary.TRANSFERRED_OUT))) {
                                result.put(ptId, new SimpleResult(hivDicontinuation.getEncounterDatetime(), this));
                            }
                        }
                    }

            }
        }
        return result;
    }
}
