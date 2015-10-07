package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ObsResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.HivConstants;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 10/7/15.
 */
public class Cd4DueDateCalculation extends AbstractPatientCalculation {
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> map, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

        Set<Integer> alive = Filters.alive(cohort, context);
        CalculationResultMap inHivProgram = Calculations.firstEnrollments(hivProgram, alive, context);

        CalculationResultMap lastObsCount = Calculations.lastObs(Dictionary.getConcept(Dictionary.CD4_COUNT), cohort, context);
        CalculationResultMap lastObsPercent = Calculations.lastObs(Dictionary.getConcept(Dictionary.CD4_PERCENT), cohort, context);

        for(Integer ptId: cohort) {
            Date dueDate = null;
            PatientProgram patientProgram = EmrCalculationUtils.resultForPatient(inHivProgram, ptId);

            if(patientProgram != null){

                ObsResult r = (ObsResult) lastObsCount.get(ptId);
                ObsResult p = (ObsResult) lastObsPercent.get(ptId);

                Date dateCount = r != null ? r.getDateOfResult() : null;
                Date datePercent = p != null ? p.getDateOfResult() : null;

                Date lastResultDate = CoreUtils.latest(dateCount, datePercent);
                if(lastResultDate == null){
                    lastResultDate = patientProgram.getDateEnrolled();
                }


                    dueDate = DateUtil.adjustDate(lastResultDate, HivConstants.NEEDS_CD4_COUNT_AFTER_DAYS, DurationUnit.DAYS);


            }
            ret.put(ptId, new SimpleResult(dueDate, this));


        }
        return ret;
    }
}
