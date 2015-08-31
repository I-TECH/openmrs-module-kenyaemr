package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
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
public class FirstProgramEnrollment extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        CalculationResultMap lastProgramEnrollment = Calculations.firstEnrollments(hivProgram, cohort, context);

        for(Integer ptId: cohort) {
           Date enrolledDate = null;
            PatientProgram patientProgram = EmrCalculationUtils.resultForPatient(lastProgramEnrollment, ptId);
            if(patientProgram != null) {
                enrolledDate = patientProgram.getDateEnrolled();
            }

            ret.put(ptId, new SimpleResult(enrolledDate, this));

        }
        return  ret;
    }
}
