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
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
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
        Set<Integer> transferredOut = CalculationUtils.patientsThatPass(calculate(new IsTransferOutCalculation(), cohort, context));

        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId: cohort) {
            Date artStartDate = EmrCalculationUtils.resultForPatient(initialArtStart, ptId);
            Date returnVisitDate = null;
            List<Visit> allVisits = Context.getVisitService().getVisitsByPatient(Context.getPatientService().getPatient(ptId));
            List<Visit> requiredVisits = new ArrayList<Visit>();
            if(artStartDate != null && outcomePeriod != null) {
                Date futureDate = DateUtil.adjustDate(DateUtil.adjustDate(artStartDate, outcomePeriod, DurationUnit.MONTHS), 1, DurationUnit.DAYS);
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
                    }
                    //check if this patient has more than one visit in the
                    if(returnVisitDate == null && requiredVisits.size() > 1) {
                        //get the visit date of the last visit
                        Date lastVisitDate = requiredVisits.get(0).getStartDatetime();
                        Date priorVisitDate1 = requiredVisits.get(1).getStartDatetime();
                        int dayDiff = daysBetweenDates(lastVisitDate, priorVisitDate1);
                        //get the prior visit
                        Set<Encounter> priorVisitEncounters = requiredVisits.get(1).getEncounters();
                        Date priorReturnDate1 = null;
                        if(priorVisitEncounters.size() > 0) {
                            Set<Obs> allObs;
                            for (Encounter encounter: priorVisitEncounters){
                                allObs = encounter.getAllObs();
                                for(Obs obs: allObs){
                                    if(obs.getConcept().equals(RETURN_VISIT_DATE)) {
                                        priorReturnDate1 = obs.getValueDatetime();
                                        break;
                                    }
                                }
                            }
                            if(priorReturnDate1 != null) {
                                returnVisitDate = DateUtil.adjustDate(priorReturnDate1, dayDiff, DurationUnit.DAYS);
                            }
                        }

                    }
                    //if return visit date still missing we will check the 3rd prior visit
                    if(returnVisitDate == null && requiredVisits.size() > 2) {
                        //get the visit date of the last visit
                        Date lastVisitDate0 = requiredVisits.get(0).getStartDatetime();
                        Date priorVisitDate1 = requiredVisits.get(1).getStartDatetime();
                        Date lastVisitDate1 = requiredVisits.get(1).getStartDatetime();
                        Date priorVisitDate2 = requiredVisits.get(2).getStartDatetime();

                        int day2= daysBetweenDates(lastVisitDate1, priorVisitDate2);
                        int day1 = daysBetweenDates(lastVisitDate0, priorVisitDate1);
                        int dayDiff =day2+day1;
                        //get the prior visit
                        Set<Encounter> priorVisitEncounters = requiredVisits.get(2).getEncounters();
                        Date priorReturnDate2 = null;
                        if(priorVisitEncounters.size() > 0) {
                            Set<Obs> allObs;
                            for (Encounter encounter: priorVisitEncounters){
                                allObs = encounter.getAllObs();
                                for(Obs obs: allObs){
                                    if(obs.getConcept().equals(RETURN_VISIT_DATE)) {
                                        priorReturnDate2 = obs.getValueDatetime();
                                        break;
                                    }
                                }
                            }
                            if(priorReturnDate2 != null) {
                                returnVisitDate = DateUtil.adjustDate(priorReturnDate2, dayDiff, DurationUnit.DAYS);
                            }
                        }

                    }
                    //if return visit date still missing we will check the 4th prior visit
                    if(returnVisitDate == null && requiredVisits.size() > 3) {
                        //get the visit date of the last visit
                        Date lastVisitDate0 = requiredVisits.get(0).getStartDatetime();
                        Date priorVisitDate1 = requiredVisits.get(1).getStartDatetime();
                        Date lastVisitDate1 = requiredVisits.get(1).getStartDatetime();
                        Date priorVisitDate2 = requiredVisits.get(2).getStartDatetime();
                        Date lastVisitDate2 = requiredVisits.get(2).getStartDatetime();
                        Date priorVisitDate3 = requiredVisits.get(3).getStartDatetime();

                        int day3 = daysBetweenDates(lastVisitDate2, priorVisitDate3);
                        int day2= daysBetweenDates(lastVisitDate1, priorVisitDate2);
                        int day1 = daysBetweenDates(lastVisitDate0, priorVisitDate1);
                        int dayDiff =day2 + day1 + day3;
                        //get the prior visit
                        Set<Encounter> priorVisitEncounters = requiredVisits.get(3).getEncounters();
                        Date priorReturnDate3 = null;
                        if(priorVisitEncounters.size() > 0) {
                            Set<Obs> allObs;
                            for (Encounter encounter: priorVisitEncounters){
                                allObs = encounter.getAllObs();
                                for(Obs obs: allObs){
                                    if(obs.getConcept().equals(RETURN_VISIT_DATE)) {
                                        priorReturnDate3 = obs.getValueDatetime();
                                        break;
                                    }
                                }
                            }
                            if(priorReturnDate3 != null) {
                                returnVisitDate = DateUtil.adjustDate(priorReturnDate3, dayDiff, DurationUnit.DAYS);
                            }
                        }

                    }
                    //if return visit date still missing we will pick the 5th visit
                    if(returnVisitDate == null && requiredVisits.size() > 4) {
                        //get the visit date of the last visit
                        Date lastVisitDate0 = requiredVisits.get(0).getStartDatetime();
                        Date priorVisitDate1 = requiredVisits.get(1).getStartDatetime();
                        Date lastVisitDate1 = requiredVisits.get(1).getStartDatetime();
                        Date priorVisitDate2 = requiredVisits.get(2).getStartDatetime();
                        Date lastVisitDate2 = requiredVisits.get(2).getStartDatetime();
                        Date priorVisitDate3 = requiredVisits.get(3).getStartDatetime();
                        Date lastVisitDate3 = requiredVisits.get(3).getStartDatetime();
                        Date priorVisitDate4 = requiredVisits.get(4).getStartDatetime();

                        int day4 = daysBetweenDates(lastVisitDate3, priorVisitDate4);
                        int day3 = daysBetweenDates(lastVisitDate2, priorVisitDate3);
                        int day2= daysBetweenDates(lastVisitDate1, priorVisitDate2);
                        int day1 = daysBetweenDates(lastVisitDate0, priorVisitDate1);
                        int dayDiff =day2 + day1 + day3+day4;
                        //get the prior visit
                        Set<Encounter> priorVisitEncounters = requiredVisits.get(4).getEncounters();
                        Date priorReturnDate4 = null;
                        if(priorVisitEncounters.size() > 0) {
                            Set<Obs> allObs;
                            for (Encounter encounter: priorVisitEncounters){
                                allObs = encounter.getAllObs();
                                for(Obs obs: allObs){
                                    if(obs.getConcept().equals(RETURN_VISIT_DATE)) {
                                        priorReturnDate4 = obs.getValueDatetime();
                                        break;
                                    }
                                }
                            }
                            if(priorReturnDate4 != null) {
                                returnVisitDate = DateUtil.adjustDate(priorReturnDate4, dayDiff, DurationUnit.DAYS);
                            }
                        }

                    }

                }
                if(returnVisitDate == null){
                    //pick the art start date and add 30 days
                    returnVisitDate = DateUtil.adjustDate(artStartDate, 30, DurationUnit.DAYS);
                }

            }
            if(transferredOut.contains(ptId) || !(alive.contains(ptId))) {
                returnVisitDate = null;
            }
            ret.put(ptId, new SimpleResult(returnVisitDate, this));

        }
        return ret;
    }

    int daysBetweenDates(Date d1, Date d2) {
        DateTime dateTime1 = new DateTime(d1.getTime());
        DateTime dateTime2 = new DateTime(d2.getTime());
        return Math.abs(Days.daysBetween(dateTime1, dateTime2).getDays());
    }
}
