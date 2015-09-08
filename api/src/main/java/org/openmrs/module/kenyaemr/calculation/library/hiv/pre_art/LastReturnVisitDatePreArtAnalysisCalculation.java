package org.openmrs.module.kenyaemr.calculation.library.hiv.pre_art;

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
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by codehub on 9/8/15.
 */
public class LastReturnVisitDatePreArtAnalysisCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

        Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        CalculationResultMap hivenrollment = Calculations.firstEnrollments(hivProgram, cohort, context);

        if(outcomePeriod != null){
            context.setNow(DateUtil.adjustDate(DateUtil.getStartOfMonth(context.getNow()), outcomePeriod, DurationUnit.MONTHS));
        }

        CalculationResultMap returnVisitMap = Calculations.allObs(Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE), cohort, context);
        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId: cohort) {
            ListResult listResult = (ListResult) returnVisitMap.get(ptId);
            PatientProgram patientProgram = EmrCalculationUtils.resultForPatient(hivenrollment, ptId);
            List<Obs> allReturnVisits = CalculationUtils.extractResultValues(listResult);
            List<Obs> correctList = new ArrayList<Obs>();
            Date returnVisitDate = null;
            if(patientProgram != null && outcomePeriod != null && allReturnVisits.size() > 0) {
                Date futureDate = DateUtil.adjustDate(DateUtil.adjustDate(patientProgram.getDateEnrolled(), outcomePeriod, DurationUnit.MONTHS), 1, DurationUnit.DAYS);
                for(Obs obs:allReturnVisits) {
                    if(obs.getObsDatetime().before(futureDate)) {
                        correctList.add(obs);
                    }
                }
                if(correctList.size() > 0) {
                    returnVisitDate = correctList.get(correctList.size() - 1).getValueDatetime();
                }
            }
            ret.put(ptId, new SimpleResult(returnVisitDate, this));

        }
        return ret;
    }
}
