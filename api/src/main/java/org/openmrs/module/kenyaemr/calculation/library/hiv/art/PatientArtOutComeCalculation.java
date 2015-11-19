package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.HivConstants;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.models.LostToFU;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.DateOfDeathCalculation;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils.daysSince;

/**
 * Created by codehub on 06/07/15.
 */
public class PatientArtOutComeCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Integer outcomePeriod = (parameterValues != null && parameterValues.containsKey("outcomePeriod")) ? (Integer) parameterValues.get("outcomePeriod") : null;
        CalculationResultMap onARTInitial = calculate(new InitialArtStartDateCalculation(), cohort, context);

        if(outcomePeriod != null) {
            context.setNow(DateUtil.adjustDate(context.getNow(), outcomePeriod, DurationUnit.MONTHS));
        }
        CalculationResultMap deadPatients = calculate(new DateOfDeathCalculation(), cohort, context);
        CalculationResultMap defaulted = defaultedMap(cohort, context,outcomePeriod);
        CalculationResultMap ltfu = ltfuMap(cohort, context, outcomePeriod);
        CalculationResultMap stoppedArtMap = calculate(new StoppedARTDateCalculation(), cohort, context);
        CalculationResultMap transferredOutMap = calculate(new TransferOutDateCalculation(), cohort, context);

        CalculationResultMap ret = new CalculationResultMap();

        for (Integer ptId : cohort) {
            String status = null;
            Date dateLost = null;
            TreeMap<Date, String> outComes = new TreeMap<Date, String>();

            Date dateTranOut = EmrCalculationUtils.datetimeResultForPatient(transferredOutMap, ptId);

            Date initialArtStart = EmrCalculationUtils.datetimeResultForPatient(onARTInitial, ptId);
            Date dod = EmrCalculationUtils.datetimeResultForPatient(deadPatients, ptId);
            Date defaultedDate = EmrCalculationUtils.datetimeResultForPatient(defaulted, ptId);
            LostToFU classifiedLTFU = EmrCalculationUtils.resultForPatient(ltfu, ptId);
            if(classifiedLTFU != null) {
                dateLost = (Date) classifiedLTFU.getDateLost();
            }
            String stoppedDate = EmrCalculationUtils.resultForPatient(stoppedArtMap, ptId);

            if(initialArtStart != null && outcomePeriod != null) {
                Date futureDate = DateUtil.adjustDate(DateUtil.adjustDate(initialArtStart, outcomePeriod, DurationUnit.MONTHS), 1, DurationUnit.DAYS);
                status = "Alive and on ART";

                if(dod != null && (dod.before(futureDate)) && dod.after(initialArtStart)) {
                    outComes.put(dod, "Died");
                }
                if(dateTranOut != null && dateTranOut.after(initialArtStart) && dateTranOut.before(futureDate)){
                    outComes.put(dateTranOut, "Transferred out");

                }

                if(stoppedDate != null && dateLost != null) {
                    try {
                        Date stDate = artStoppedDate(stoppedDate);
                        if(stDate.after(initialArtStart) && stDate.before(futureDate) && stDate.before(dateLost) && dateLost.before(new Date())) {
                            outComes.put(dateLost, "LTFU");
                        }
                        else if(stDate.after(initialArtStart) && stDate.before(futureDate) && stDate.after(dateLost)) {
                            outComes.put(dateLost, "Stopped ART");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(stoppedDate != null) {
                    try {
                        Date stDate = artStoppedDate(stoppedDate);
                        if(stDate.after(initialArtStart) && stDate.before(futureDate)) {
                            outComes.put(artStoppedDate(stoppedDate), "Stopped ART");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if(dateLost != null && (dateLost.before(futureDate)) && dateLost.after(initialArtStart) && dateLost.before(new Date())){
                    outComes.put(dateLost, "LTFU");
                }
                if(defaultedDate != null && (defaultedDate.before(futureDate)) && defaultedDate.after(initialArtStart) && defaultedDate.before(new Date())){
                    outComes.put(defaultedDate, "Defaulted");
                }

            }
            //now check for things in the map and have the results recorded
            //since the map is sorted ascending, we pick the last item in the list
            if(outComes.size() > 0) {
                Map.Entry<Date, String> values = outComes.lastEntry();
                if (values != null) {
                    status = values.getValue();
                }
            }
            ret.put(ptId, new SimpleResult(status, this));
        }
        return  ret;
    }

    Date artStoppedDate(String string) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.parse(string);

    }

    CalculationResultMap returnVisitDate(Collection<Integer> cohort, PatientCalculationContext context, Integer period) {
        CalculationResultMap ret = new CalculationResultMap();

        CalculationResultMap initialArtStart = calculate(new InitialArtStartDateCalculation(), cohort, context);
        Set<Integer> alive = Filters.alive(cohort, context);
        Concept RETURN_VISIT_DATE = Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE);
        CalculationResultMap transferredOutMap = calculate(new TransferOutDateCalculation(), cohort, context);
        CalculationResultMap lastSeen = dateLastSeen(cohort, context);
        for(Integer ptId: cohort) {
            Date artStartDate = EmrCalculationUtils.resultForPatient(initialArtStart, ptId);
            Date returnVisitDate = null;
            List<Visit> allVisits = Context.getVisitService().getVisitsByPatient(Context.getPatientService().getPatient(ptId));
            Date trasnOut = EmrCalculationUtils.datetimeResultForPatient(transferredOutMap, ptId);
            Date lastSeenDate = EmrCalculationUtils.datetimeResultForPatient(lastSeen, ptId);
            List<Visit> requiredVisits = new ArrayList<Visit>();
            if(alive.contains(ptId) && period != null && artStartDate != null) {
                Date futureDate = DateUtil.adjustDate(DateUtil.adjustDate(artStartDate, period, DurationUnit.MONTHS), 1, DurationUnit.DAYS);
                for(Visit visit:allVisits) {
                    if(visit.getStartDatetime().before(futureDate)) {
                        requiredVisits.add(visit);
                    }
                }
                if (requiredVisits.size() > 0) {

                    //pick the last visit
                    Set<Encounter> lastVisitEncounters = requiredVisits.get(0).getEncounters();
                    if (lastVisitEncounters.size() > 0) {
                        Set<Obs> allObs;
                        for (Encounter encounter : lastVisitEncounters) {
                            allObs = encounter.getAllObs();
                            for (Obs obs : allObs) {
                                if (obs.getConcept().equals(RETURN_VISIT_DATE)) {
                                    returnVisitDate = obs.getValueDatetime();
                                    break;
                                }
                            }
                        }
                        if(returnVisitDate != null && lastSeenDate != null && returnVisitDate.before(lastSeenDate)){
                            returnVisitDate = DateUtil.adjustDate(lastSeenDate, 30, DurationUnit.DAYS);
                        }
                    }
                }
                if (returnVisitDate == null) {
                    returnVisitDate = DateUtil.adjustDate(lastSeenDate, 30, DurationUnit.DAYS);
                }


            }
            if(trasnOut != null && artStartDate != null && trasnOut.after(artStartDate)) {
                returnVisitDate = null;
            }
            ret.put(ptId, new SimpleResult(returnVisitDate, this));
        }
        return ret;
    }
    CalculationResultMap defaultedMap(Collection<Integer> cohort, PatientCalculationContext context, Integer period) {
        CalculationResultMap ret = new CalculationResultMap();
        Set<Integer> isTransferOut = CalculationUtils.patientsThatPass(calculate(new IsTransferOutCalculation(), cohort, context));
        CalculationResultMap resultMap = returnVisitDate(cohort, context, period);
        for (Integer ptId : cohort) {
            Date dateDefaulted = null;
            SimpleResult lastScheduledReturnDateResults = (SimpleResult) resultMap.get(ptId);
            if (lastScheduledReturnDateResults != null) {
                Date lastScheduledReturnDate = (Date) lastScheduledReturnDateResults.getValue();
                if(!(isTransferOut.contains(ptId)) && lastScheduledReturnDate != null) {
                    dateDefaulted = CoreUtils.dateAddDays(lastScheduledReturnDate, 30);
                }
            }

            ret.put(ptId, new SimpleResult(dateDefaulted, this));
        }
        return ret;
    }

    CalculationResultMap ltfuMap(Collection<Integer> cohort, PatientCalculationContext context, Integer period) {
        CalculationResultMap ret = new CalculationResultMap();
        Set<Integer> isTransferOut = CalculationUtils.patientsThatPass(calculate(new IsTransferOutCalculation(), cohort, context));
        CalculationResultMap resultMap = returnVisitDate(cohort, context, period);
        for (Integer ptId : cohort) {
            LostToFU classifiedLTFU;
            SimpleResult lastScheduledReturnDateResults = (SimpleResult) resultMap.get(ptId);
            Date lastScheduledReturnDate = (Date) lastScheduledReturnDateResults.getValue();
            if (lastScheduledReturnDate != null && (daysSince(lastScheduledReturnDate, context) > HivConstants.LOST_TO_FOLLOW_UP_THRESHOLD_DAYS) && !(isTransferOut.contains(ptId))) {
                classifiedLTFU = new LostToFU(true, DateUtil.adjustDate(lastScheduledReturnDate, HivConstants.LOST_TO_FOLLOW_UP_THRESHOLD_DAYS, DurationUnit.DAYS ));
            }

            else {
                classifiedLTFU = new LostToFU(false, null);
            }

            ret.put(ptId, new SimpleResult(classifiedLTFU, this));
        }
        return ret;
    }

    int daysBetweenDates(Date d1, Date d2) {
        DateTime dateTime1 = new DateTime(d1.getTime());
        DateTime dateTime2 = new DateTime(d2.getTime());
        return Math.abs(Days.daysBetween(dateTime1, dateTime2).getDays());
    }

    CalculationResultMap dateLastSeen(Collection<Integer> cohort, PatientCalculationContext context) {
        CalculationResultMap initialArtStart = calculate(new InitialArtStartDateCalculation(), cohort, context);
        CalculationResultMap lastEncounter = Calculations.lastEncounter(null, cohort, context);

        CalculationResultMap result = new CalculationResultMap();
        for (Integer ptId : cohort) {
            Encounter encounter = EmrCalculationUtils.encounterResultForPatient(lastEncounter, ptId);
            Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(initialArtStart, ptId);
            if(artStartDate != null) {
                Date encounterDate;
                if (encounter != null && encounter.getEncounterDatetime().after(artStartDate)) {
                    encounterDate = encounter.getEncounterDatetime();
                }
                else{
                    encounterDate = artStartDate;
                }

                result.put(ptId, new SimpleResult(encounterDate, this));
            }
        }
        return  result;


    }

}
