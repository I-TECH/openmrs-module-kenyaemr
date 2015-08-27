package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
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
 * Created by codehub on 27/08/15.
 */
public class IsTransferInAndHasDateCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        Concept transferInStatus = Dictionary.getConcept(Dictionary.TRANSFER_IN);
        Concept transferInDate = Dictionary.getConcept(Dictionary.TRANSFER_IN_DATE);
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        EncounterType enrollmentEncounter = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_ENROLLMENT);
        Set<Integer> inHivProgram = Filters.inProgram(hivProgram, cohort, context);

        CalculationResultMap allEnrollments = Calculations.allEnrollments(hivProgram, cohort, context);
        CalculationResultMap allEncounters = Calculations.allEncountersOnOrAfter(enrollmentEncounter, DateUtil.adjustDate(DateUtil.getStartOfMonth(context.getNow()), -1, DurationUnit.DAYS), cohort, context);

        CalculationResultMap transferInStatusResults = Calculations.lastObs(transferInStatus, cohort, context);
        CalculationResultMap transferInDateResults = Calculations.lastObs(transferInDate, cohort, context);

        for(Integer ptId : cohort) {
            TransferInAndDate transferInAndDate = null;
            //Date dateEnrolled = null;
            Encounter requiredEncounter = null;


            Obs transferInStatusResultsObs = null;
            Obs transferInDateResultsObs = null;
           // ListResult listResultPrograms = (ListResult) allEnrollments.get(ptId);
            ListResult listResultEncounters = (ListResult) allEncounters.get(ptId);

           // List<PatientProgram> allPrograms = CalculationUtils.extractResultValues(listResultPrograms);
            List<Encounter> allEncountersList = CalculationUtils.extractResultValues(listResultEncounters);

            for(Encounter encounter: allEncountersList){
                if(encounter.getEncounterDatetime().before(DateUtil.adjustDate(context.getNow(), 1, DurationUnit.DAYS)) && encounter.getEncounterDatetime().after(DateUtil.adjustDate(DateUtil.getStartOfMonth(context.getNow()), -1, DurationUnit.DAYS))){
                    requiredEncounter = encounter;
                    break;
                }
            }

            if(inHivProgram.contains(ptId) && requiredEncounter != null) {
                Set<Obs> encounterObs = requiredEncounter.getAllObs();
                for(Obs obs: encounterObs){
                    if(obs.getConcept().equals(transferInStatus)){
                        transferInStatusResultsObs = obs;
                    }

                    else if(obs.getConcept().equals(transferInDate)) {
                        transferInDateResultsObs = obs;
                    }
                    //break;
                }

                if(transferInStatusResultsObs != null && transferInDateResultsObs != null && transferInStatusResultsObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.YES))) {
                    transferInAndDate = new TransferInAndDate("Yes", transferInDateResultsObs.getValueDatetime());
                }
                if(transferInStatusResultsObs != null && transferInDateResultsObs == null && transferInStatusResultsObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.YES))) {
                    transferInAndDate = new TransferInAndDate("Yes", transferInStatusResultsObs.getObsDatetime());
                }

                if(transferInStatusResultsObs == null && transferInDateResultsObs != null) {
                    transferInAndDate = new TransferInAndDate("Yes", transferInDateResultsObs.getValueDatetime());
                }
                if((transferInStatusResultsObs == null && transferInDateResultsObs == null) || (transferInStatusResultsObs !=null && transferInStatusResultsObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.NO)))) {
                    transferInAndDate = new TransferInAndDate("No", null);
                }
            }
            ret.put(ptId, new SimpleResult(transferInAndDate, this));

        }
        return ret;
    }
}
