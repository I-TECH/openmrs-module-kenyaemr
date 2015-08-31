package org.openmrs.module.kenyaemr.calculation.library.hiv.pre_art;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by codehub on 31/08/15.
 */
public class ReturnVisitDateBasedOnPeriodCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();
        EncounterType hivConsultation = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_CONSULTATION);

        Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;
        //find a new patient calculation context
        PatientCalculationService service = Context.getService(PatientCalculationService.class);
        PatientCalculationContext newContext = service.createCalculationContext();
        if(outcomePeriod == null){
            newContext = context;
        }
        else {
            newContext.setNow(DateUtil.adjustDate(DateUtil.getStartOfMonth(context.getNow()), outcomePeriod, DurationUnit.MONTHS));
        }
        Concept returnVisitDateConcept = Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE);

        CalculationResultMap lastObs = Calculations.lastObs(returnVisitDateConcept, cohort, newContext);



        for(Integer ptId: cohort) {
            Date returnVisitDate = null;
            Obs obs = EmrCalculationUtils.obsResultForPatient(lastObs, ptId);
            if(obs != null) {
                returnVisitDate = obs.getValueDatetime();
            }
            ret.put(ptId, new SimpleResult(returnVisitDate, this));
        }

        return ret;
    }

}
