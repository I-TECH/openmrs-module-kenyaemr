package org.openmrs.module.kenyaemr.calculation.library.hiv.pre_art;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
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
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.IsTransferOutCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
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
public class LastReturnVisitDatePreArtAnalysisCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

        Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        CalculationResultMap hivenrollment = Calculations.firstEnrollments(hivProgram, cohort, context);

        if(outcomePeriod != null){
            context.setNow(DateUtil.adjustDate(DateUtil.getStartOfMonth(context.getNow()), outcomePeriod, DurationUnit.MONTHS));
        }

        Set<Integer> alive = Filters.alive(cohort, context);
        Concept RETURN_VISIT_DATE = Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE);
        Set<Integer> transferredOut = CalculationUtils.patientsThatPass(calculate(new IsTransferOutCalculation(), cohort, context));
        CalculationResultMap initialArtStart = calculate(new InitialArtStartDateCalculation(), cohort, context);
        CalculationResultMap dateLastSeenMap = lastSeenDateMap(cohort, context, outcomePeriod);

        CalculationResultMap ret = new CalculationResultMap();
        for(Integer ptId: cohort) {
            PatientProgram patientProgram = EmrCalculationUtils.resultForPatient(hivenrollment, ptId);
            Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(initialArtStart, ptId);
            Date lastSeenDate = EmrCalculationUtils.datetimeResultForPatient(dateLastSeenMap, ptId);
            Date returnVisitDate = null;
            List<Visit> allVisits = Context.getVisitService().getVisitsByPatient(Context.getPatientService().getPatient(ptId));
            List<Visit> requiredVisits = new ArrayList<Visit>();
            if(patientProgram != null && outcomePeriod != null) {
                Date futureDate = DateUtil.adjustDate(DateUtil.adjustDate(patientProgram.getDateEnrolled(), outcomePeriod, DurationUnit.MONTHS), 1, DurationUnit.DAYS);
                for(Visit visit:allVisits) {
                    if(visit.getStartDatetime().before(futureDate)) {
                        requiredVisits.add(visit);
                    }
                }
                if(requiredVisits.size() > 0) {

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
                    }

                    //check if this patient has more than one visit in the
                    if (returnVisitDate == null && requiredVisits.size() > 1) {
                        //get the visit date of the last visit
                        Date lastVisitDate = requiredVisits.get(0).getStartDatetime();
                        Date priorVisitDate1 = requiredVisits.get(1).getStartDatetime();
                        int dayDiff = daysBetweenDates(lastVisitDate, priorVisitDate1);
                        //get the prior visit
                        Set<Encounter> priorVisitEncounters = requiredVisits.get(1).getEncounters();
                        Date priorReturnDate1 = null;
                        if (priorVisitEncounters.size() > 0) {
                            if (lastSeenDate != null && artStartDate != null && lastSeenDate.after(artStartDate)) {
                                priorReturnDate1 = lastSeenDate;
                            } else if (lastSeenDate != null && artStartDate != null && artStartDate.after(lastSeenDate)) {
                                priorReturnDate1 = artStartDate;
                            } else if (lastSeenDate != null && artStartDate == null) {
                                priorReturnDate1 = lastSeenDate;
                            } else {
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
                            }
                            if (priorReturnDate1 != null) {
                                if (dayDiff < 30) {
                                    dayDiff = 30;
                                }
                                returnVisitDate = DateUtil.adjustDate(priorReturnDate1, dayDiff, DurationUnit.DAYS);
                            }
                        }

                    }
                }
                //check if return visit date is null and last seen date has some values
                else if(lastSeenDate != null){
                    returnVisitDate = DateUtil.adjustDate(lastSeenDate, 30, DurationUnit.DAYS);
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

    CalculationResultMap lastSeenDateMap(Collection<Integer> cohort, PatientCalculationContext context, Integer outcomePeriod ){
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        CalculationResultMap dateEnrolledMap = Calculations.firstEnrollments(hivProgram, cohort, context);

        CalculationResultMap lastEncounter = Calculations.allEncounters(null, cohort, context);
        CalculationResultMap initialArtStart = calculate(new InitialArtStartDateCalculation(), cohort, context);

        CalculationResultMap result = new CalculationResultMap();
        for (Integer ptId : cohort) {
            ListResult allEncounters = (ListResult) lastEncounter.get(ptId);
            List<Encounter> encounterList = CalculationUtils.extractResultValues(allEncounters);
            PatientProgram patientProgram = EmrCalculationUtils.resultForPatient(dateEnrolledMap, ptId);
            Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(initialArtStart, ptId);
            if(outcomePeriod != null && patientProgram != null) {
                Date futureDate = DateUtil.adjustDate(DateUtil.adjustDate(patientProgram.getDateEnrolled(), outcomePeriod, DurationUnit.MONTHS), 1, DurationUnit.DAYS);
                Date encounterDate = null;
                List<Encounter> targetedEncounters = new ArrayList<Encounter>();
                if (encounterList.size() > 0) {
                    for (Encounter encounter : encounterList) {
                        if (encounter.getEncounterDatetime().before(futureDate) && (encounter.getEncounterDatetime().after(patientProgram.getDateEnrolled()) || encounter.getEncounterDatetime().equals(patientProgram.getDateEnrolled()))) {
                            targetedEncounters.add(encounter);
                        }
                    }
                    if (targetedEncounters.size() > 0) {
                        Date encounterDateRequired = targetedEncounters.get(targetedEncounters.size() - 1).getEncounterDatetime();
                        if(artStartDate != null && artStartDate.after(encounterDateRequired)) {
                            encounterDate = artStartDate;
                        }
                        else {
                            encounterDate = encounterDateRequired;
                        }
                    }
                }
                if(encounterDate == null && artStartDate != null) {
                    encounterDate = artStartDate;
                }

                if(encounterDate == null){
                    encounterDate = patientProgram.getDateEnrolled();
                }

                result.put(ptId, new SimpleResult(encounterDate, this));
            }
        }
        return result;
    }
}
