package org.openmrs.module.kenyaemr.calculation.library.hiv;

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
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 20/03/15.
 */
public class LastReturnVisitDateCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> collection, Map<String, Object> map, PatientCalculationContext patientCalculationContext) {

        EncounterType hivConsultation = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_CONSULTATION);

        CalculationResultMap lastEncounter = Calculations.lastEncounter(hivConsultation, collection, patientCalculationContext);

        Concept returnVisitDateConcept = Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE);

        CalculationResultMap ret = new CalculationResultMap();

        for(Integer ptId: collection){
            Date returnVisitDate = null;

            Encounter hivEncounter = EmrCalculationUtils.encounterResultForPatient(lastEncounter, ptId);
            if(hivEncounter != null) {
                if(!(hivEncounter.getAllObs().isEmpty())) {
                    //loop through each obs and find the return visit date value
                    for(Obs obs:hivEncounter.getAllObs()) {
                        if(obs.getConcept().equals(returnVisitDateConcept)){
                            returnVisitDate = obs.getValueDatetime();
                            break;
                        }
                    }
                }
            }
            ret.put(ptId, new SimpleResult(returnVisitDate, this));
        }

        return ret;
    }
}
