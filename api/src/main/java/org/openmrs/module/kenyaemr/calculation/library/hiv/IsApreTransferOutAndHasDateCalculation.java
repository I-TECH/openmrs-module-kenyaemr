package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TransferOutDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.models.TransferInAndDate;
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
public class IsApreTransferOutAndHasDateCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params,PatientCalculationContext context) {
        CalculationResultMap ret = new CalculationResultMap();

        Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        CalculationResultMap enrollmentDateMap = Calculations.firstEnrollments(hivProgram, cohort, context);


       if(outcomePeriod != null) {

           context.setNow(DateUtil.adjustDate(DateUtil.getStartOfMonth(context.getNow()), outcomePeriod, DurationUnit.MONTHS));
       }

        CalculationResultMap transferredOut = calculate(new TransferOutDateCalculation(), cohort, context);

        for(Integer ptId : cohort) {
            TransferInAndDate transferOutAndDate = null;

            Date dateTo = EmrCalculationUtils.datetimeResultForPatient(transferredOut, ptId);
            PatientProgram patientProgram = EmrCalculationUtils.resultForPatient(enrollmentDateMap, ptId);
            Date enrolledDate = null;
            if(patientProgram != null) {
                enrolledDate = patientProgram.getDateEnrolled();
            }

            if(enrolledDate != null && outcomePeriod != null) {
                Date futureDate = DateUtil.adjustDate(enrolledDate, outcomePeriod, DurationUnit.MONTHS);
                if(dateTo != null && dateTo.before(futureDate)){
                    transferOutAndDate = new TransferInAndDate("Yes", dateTo);
                }

                else {
                    transferOutAndDate = new TransferInAndDate("No", null);
                }
            }
            ret.put(ptId, new SimpleResult(transferOutAndDate, this));

        }
        return ret;
    }
}