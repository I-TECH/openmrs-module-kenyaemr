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
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 9/8/15.
 */
public class LastReturnVisitDateArtAnalysisCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

        Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;

        CalculationResultMap initialArtStart = calculate(new InitialArtStartDateCalculation(), cohort, context);



        if(outcomePeriod != null){
            context.setNow(DateUtil.adjustDate(context.getNow(), outcomePeriod, DurationUnit.MONTHS));
        }


        Set<Integer> alive = Filters.alive(cohort, context);
        Concept RETURN_VISIT_DATE = Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE);
        CalculationResultMap transferredOutMap = calculate(new TransferOutDateCalculation(), cohort, context);
        CalculationResultMap dateLastSeen = dateLastSeen(cohort, context);

        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId: cohort) {
            Date artStartDate = EmrCalculationUtils.resultForPatient(initialArtStart, ptId);
            Date returnVisitDate = null;
            List<Visit> allVisits = Context.getVisitService().getVisitsByPatient(Context.getPatientService().getPatient(ptId));
            List<Visit> requiredVisits = new ArrayList<Visit>();
            Date lastSeenDate = EmrCalculationUtils.datetimeResultForPatient(dateLastSeen, ptId);
            Date transferOutDate = EmrCalculationUtils.datetimeResultForPatient(transferredOutMap, ptId);
            Date futureDate = null;
            if(artStartDate != null && outcomePeriod != null) {
                futureDate = DateUtil.adjustDate(DateUtil.adjustDate(artStartDate, outcomePeriod, DurationUnit.MONTHS), 1, DurationUnit.DAYS);
                for(Visit visit:allVisits) {
                    if(visit.getStartDatetime().before(futureDate)) {
                        requiredVisits.add(visit);
                    }
                }
                if(requiredVisits.size() > 0) {
                    //pick the last visit
                    Set<Encounter> lastVisitEncounters = requiredVisits.get(0).getEncounters();
                    if(lastVisitEncounters.size() > 0) {
                        Set<Obs> allObs;
                        for(Encounter encounter: lastVisitEncounters) {
                            allObs = encounter.getAllObs();
                            for(Obs obs: allObs){
                                if(obs.getConcept().equals(RETURN_VISIT_DATE)) {
                                    returnVisitDate = obs.getValueDatetime();
                                    break;
                                }
                            }
                        }
                        if(returnVisitDate != null && lastSeenDate != null && returnVisitDate.before(lastSeenDate)){
                            returnVisitDate = null;
                        }
                    }
                    if(returnVisitDate == null && requiredVisits.size() > 1){
                        Date lastVisitDate = requiredVisits.get(0).getStartDatetime();
                        Date priorVisitDate1 = requiredVisits.get(1).getStartDatetime();
                        int dayDiff = daysBetweenDates(lastVisitDate, priorVisitDate1);
                        //get the prior visit
                        Set<Encounter> priorVisitEncounters = requiredVisits.get(1).getEncounters();
                        Date priorReturnDate1 = null;
                        if (priorVisitEncounters.size() > 0) {
                            Set<Obs> allObs;
                            for (Encounter encounter : priorVisitEncounters) {
                                allObs = encounter.getAllObs();
                                for (Obs obs : allObs) {
                                    if (obs.getConcept().equals(RETURN_VISIT_DATE)) {
                                        priorReturnDate1 = obs.getValueDatetime();
                                        break;
                                    }
                                }
                            }
                            if (priorReturnDate1 != null) {
                                returnVisitDate = DateUtil.adjustDate(priorReturnDate1, dayDiff, DurationUnit.DAYS);
                            }
                        }
                        if(returnVisitDate != null && lastSeenDate != null && returnVisitDate.before(lastSeenDate)){
                            returnVisitDate = DateUtil.adjustDate(lastSeenDate, 30, DurationUnit.DAYS);
                        }
                    }
                }
                if(returnVisitDate == null && lastSeenDate != null){
                    returnVisitDate = DateUtil.adjustDate(lastSeenDate, 30, DurationUnit.DAYS);
                }

            }
            if((transferOutDate != null && futureDate != null &&  transferOutDate.after(artStartDate) && transferOutDate.before(futureDate)) || !(alive.contains(ptId))) {
                returnVisitDate = null;
            }
            ret.put(ptId, new SimpleResult(returnVisitDate, this));

        }
        return ret;
    }

    CalculationResultMap dateLastSeen(Collection<Integer> cohort, PatientCalculationContext context) {
        CalculationResultMap initialArtStart = calculate(new InitialArtStartDateCalculation(), cohort, context);
        CalculationResultMap lastEncounter = Calculations.lastEncounter(null, cohort, context);

        CalculationResultMap result = new CalculationResultMap();
        for (Integer ptId : cohort) {
            Encounter encounter = EmrCalculationUtils.encounterResultForPatient(lastEncounter, ptId);
            Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(initialArtStart, ptId);
            if(artStartDate != null) {
                Date encounterDate = null;
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
    int daysBetweenDates(Date d1, Date d2) {
        DateTime dateTime1 = new DateTime(d1.getTime());
        DateTime dateTime2 = new DateTime(d2.getTime());
        return Math.abs(Days.daysBetween(dateTime1, dateTime2).getDays());
    }
}
