package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.models.TransferInAndDate;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 31/08/15.
 */
public class IsTransferOutAndHasDateCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params,PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;


        Concept transferOutStatus = Dictionary.getConcept(Dictionary.TRANSFERRED_OUT);
        Concept transferOutDate = Dictionary.getConcept(Dictionary.DATE_TRANSFERRED_OUT);
        Concept reasonForExit = Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION);
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, cohort, context);

        EncounterType hivDiscontinuationEncounter = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_DISCONTINUATION);

        //have a new patient context

        PatientCalculationService newContextService = Context.getService(PatientCalculationService.class);
        PatientCalculationContext newContext = newContextService.createCalculationContext();
       if(outcomePeriod != null) {

           newContext.setNow(DateUtil.adjustDate(DateUtil.getStartOfMonth(context.getNow()), outcomePeriod, DurationUnit.MONTHS));
       }


        CalculationResultMap lastReasonForExit = Calculations.lastObs(reasonForExit, cohort, newContext);
        CalculationResultMap lastTransferOutDate = Calculations.lastObs(transferOutDate, cohort, newContext);
        CalculationResultMap enrollmentDateMap = calculate(new DateOfEnrollmentHivCalculation(), cohort, context);

        for(Integer ptId : cohort) {
            TransferInAndDate transferOutAndDate = null;
            Date transferOutDateValid = null;

            Obs reasonForExitObs = EmrCalculationUtils.obsResultForPatient(lastReasonForExit, ptId);
            Obs transferOutDateObs = EmrCalculationUtils.obsResultForPatient(lastTransferOutDate, ptId);
            Date enrolledDate = EmrCalculationUtils.datetimeResultForPatient(enrollmentDateMap, ptId);

            if(inHivProgram.contains(ptId) && outcomePeriod != null) {
                Date futureDate = DateUtil.adjustDate(enrolledDate, outcomePeriod, DurationUnit.MONTHS);
                if(transferOutDateObs != null && transferOutDateObs.getValueDatetime().after(DateUtil.adjustDate(DateUtil.getStartOfMonth(context.getNow()), -1, DurationUnit.DAYS)) && transferOutDateObs.getValueDatetime().before(futureDate)){
                    transferOutDateValid = transferOutDateObs.getValueDatetime();
                }

                if(reasonForExitObs != null && transferOutDateValid != null && reasonForExitObs.getValueCoded().equals(transferOutStatus)) {
                    transferOutAndDate = new TransferInAndDate("Yes", transferOutDateValid);
                }
                else if(reasonForExitObs != null && transferOutDateValid == null && reasonForExitObs.getValueCoded().equals(transferOutStatus)) {
                    transferOutAndDate = new TransferInAndDate("Yes", reasonForExitObs.getObsDatetime());
                }

               else if(reasonForExitObs == null && transferOutDateValid != null) {
                    transferOutAndDate = new TransferInAndDate("Yes", transferOutDateValid);
                }
                else {
                    transferOutAndDate = new TransferInAndDate("No", null);
                }
            }

            else {
                transferOutAndDate = new TransferInAndDate("No", null);
            }


            ret.put(ptId, new SimpleResult(transferOutAndDate, this));

        }
        return ret;
    }
}