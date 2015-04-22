package org.openmrs.module.kenyaemr.calculation.library.cohort.analysis;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Years;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.MissedLastAppointmentCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.AliveAndOnFollowUpCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.DateClassifiedLTFUCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.TransferOutDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.DateOfDeathCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.reporting.model.LostToFU;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramEnrollmentsForPatientDataDefinition;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;


/**
 * Calculates the statuses of pre art patients
 */
public class PreARTYearsCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

        Integer days = (params != null && params.containsKey("days")) ? (Integer) params.get("days") : null;
        CalculationResultMap ret = new CalculationResultMap();
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);


        Calendar ctx = Calendar.getInstance();
        ctx.setTime(context.getNow());
        ctx.add(Calendar.MONTH, -83);
        Date thresholdDate = ctx.getTime();

        Calendar max = Calendar.getInstance();
        max.setTime(context.getNow());
        max.add(Calendar.MONTH, -12);


        //find the last hiv program enrollment
        EncounterType hivEnrollmentEncounterType = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_ENROLLMENT);
        EncountersForPatientDataDefinition def = new EncountersForPatientDataDefinition();
        def.setWhich(TimeQualifier.FIRST);
        def.setTypes(Arrays.asList(hivEnrollmentEncounterType));
        def.setOnOrAfter(thresholdDate);
        def.setOnOrBefore(max.getTime());

        //now put the def into an evaluation map
        CalculationResultMap EncounterEnrollment = CalculationUtils.evaluateWithReporting(def, cohort, params, null, context);

        PatientCalculationService patientCalculationService = Context.getService(PatientCalculationService.class);

        PatientCalculationContext context1 = patientCalculationService.createCalculationContext();
        context1.setNow(context.getNow());



        ////find the program map enrolled in
        CalculationResultMap deadPatients = calculate(new DateOfDeathCalculation(), cohort, context);
        CalculationResultMap defaulted = calculate(new DateDefaultedCalculation(), cohort, context);
        CalculationResultMap ltfu = calculate(new DateClassifiedLTFUCalculation(), cohort, context);
        CalculationResultMap transferredOut = calculate(new TransferOutDateCalculation(), cohort, context);
        CalculationResultMap onART = calculate(new InitialArtStartDateCalculation(), cohort, context);
        CalculationResultMap aliveAndOnFollowUp = calculate(new AliveAndOnFollowUpCalculation(), cohort, context);

        for(Integer ptId : cohort) {
            String value = null;
            Date enrollmentDate = null;
            Date resultsDate = null;
            Date dateLost = null;
            //Date runAfter = null;

            Encounter patientProgramEncounter = EmrCalculationUtils.encounterResultForPatient(EncounterEnrollment, ptId);
            Date dod = EmrCalculationUtils.datetimeResultForPatient(deadPatients, ptId);

            LostToFU classifiedLTFU = EmrCalculationUtils.resultForPatient(ltfu, ptId);

            //find date transferred out
            Date dateTo = EmrCalculationUtils.datetimeResultForPatient(transferredOut, ptId);

            //find the initial art start date
            Date initialArtStart = EmrCalculationUtils.datetimeResultForPatient(onART, ptId);

            //find date defaulted
            Date defaultedDate = EmrCalculationUtils.datetimeResultForPatient(defaulted, ptId);

            if(classifiedLTFU != null) {
                dateLost = (Date) classifiedLTFU.getDateLost();
            }

            //get those who are alive and follow up
            Boolean aliveAndOnFollowUpBool = (Boolean) aliveAndOnFollowUp.get(ptId).getValue();


            if(patientProgramEncounter != null && days != null){
                enrollmentDate = patientProgramEncounter.getEncounterDatetime();
                if(enrollmentDate != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(enrollmentDate);
                    calendar.add(Calendar.DATE, days);
                    resultsDate = calendar.getTime();
                }

            }
            if(enrollmentDate != null && (daysSince(enrollmentDate, context1.getNow()) > days)) {

                if (resultsDate != null && initialArtStart != null  && initialArtStart.before(resultsDate)) {
                    value = "A";
                }else if(enrollmentDate != null && dod != null && resultsDate != null && dod.before(resultsDate) && dod.after(enrollmentDate)) {
                    value = "D";
                }else  if (resultsDate != null && dateTo != null && dateTo.before(resultsDate)) {
                    value = "T";
                } else if (resultsDate != null && dateLost != null && dateLost.before(resultsDate)) {
                    value = "L";
                } else if (resultsDate != null && defaultedDate != null) {
                    value = "F";
                } else {
                    value = "V";
                }
            }

            ret.put(ptId, new SimpleResult(value, this));
        }

        return ret;
    }

    private  int daysSince(Date date1, Date date2) {
        DateTime d1 = new DateTime(date1.getTime());
        DateTime d2 = new DateTime(date2.getTime());
        return Days.daysBetween(d1, d2).getDays();
    }
}
