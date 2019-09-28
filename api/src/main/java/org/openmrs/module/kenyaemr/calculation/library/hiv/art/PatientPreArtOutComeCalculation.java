/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

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
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.HivConstants;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.DateOfDeathCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
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

/**
 * Calculate possible patient outcomes at the end of the cohort period
 */
public class PatientPreArtOutComeCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

		CalculationResultMap enrolledHere = Calculations.firstEnrollments(hivProgram, cohort, context);

		if(outcomePeriod != null){
			context.setNow(DateUtil.adjustDate(context.getNow(), outcomePeriod, DurationUnit.MONTHS));
		}


		CalculationResultMap onART = calculate(new InitialArtStartDateCalculation(), cohort, context);
		CalculationResultMap deadPatients = calculate(new DateOfDeathCalculation(), cohort, context);
		CalculationResultMap transferredOut = calculate(new TransferOutDateCalculation(), cohort, context);
		CalculationResultMap defaulted = defaultedMap(cohort, context, outcomePeriod);
		CalculationResultMap ltfu = ltfuMap(cohort, context, outcomePeriod);

		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptId : cohort) {
		   	String status = "Alive and not on ART";
			Date dateLost = null;
			TreeMap<Date, String> preArtOutcomes = new TreeMap<Date, String>();

			PatientProgram patientProgramDate = EmrCalculationUtils.resultForPatient(enrolledHere, ptId);

			if(patientProgramDate != null && outcomePeriod != null) {
				Date initialArtStart = EmrCalculationUtils.datetimeResultForPatient(onART, ptId);
				Date dod = EmrCalculationUtils.datetimeResultForPatient(deadPatients, ptId);
				Date dateTo = EmrCalculationUtils.datetimeResultForPatient(transferredOut, ptId);
				Date defaultedDate = EmrCalculationUtils.datetimeResultForPatient(defaulted, ptId);
				Date classifiedLTFU = EmrCalculationUtils.datetimeResultForPatient(ltfu, ptId);
				if(classifiedLTFU != null) {
					dateLost = classifiedLTFU;
				}
				//get future date that would be used as a limit
				Date futureDate = DateUtil.adjustDate(DateUtil.adjustDate(patientProgramDate.getDateEnrolled(), outcomePeriod, DurationUnit.MONTHS), 1, DurationUnit.DAYS);


				//start looping through to get outcomes
				if(initialArtStart != null && initialArtStart.before(futureDate) && initialArtStart.after(patientProgramDate.getDateEnrolled())) {
					preArtOutcomes.put(initialArtStart, "Initiated ART");
				}

				if(dod != null && dateTo != null && dod.before(dateTo) && dod.before(futureDate) && dod.after(patientProgramDate.getDateEnrolled())) {
					preArtOutcomes.put(dod, "Died");
				}
				if(dod != null && dateTo != null && dateTo.before(dod) && dateTo.before(futureDate) && dateTo.after(patientProgramDate.getDateEnrolled())) {
					preArtOutcomes.put(dateTo, "Transferred out");
				}

				if(dod != null && dod.before(futureDate) && dod.after(patientProgramDate.getDateEnrolled())){
					preArtOutcomes.put(dod, "Died");
				}
				try {
					if(dateTo != null && dateTo.before(futureDate) && (dateTo.after(patientProgramDate.getDateEnrolled()) || dateOnlyDate(dateTo.toString()).equals(dateOnlyDate(patientProgramDate.getDateEnrolled().toString())))){
                        preArtOutcomes.put(dateTo, "Transferred out");
                    }
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if(defaultedDate != null && dateLost != null && defaultedDate.before(dateLost) && defaultedDate.before(futureDate) && defaultedDate.after(patientProgramDate.getDateEnrolled())){
					preArtOutcomes.put(defaultedDate, "Defaulted");
				}

				if(defaultedDate != null && dateLost != null && dateLost.before(defaultedDate) && dateLost.before(futureDate) && dateLost.after(patientProgramDate.getDateEnrolled()) && dateLost.before(new Date())){
					preArtOutcomes.put(dateLost, "LTFU");
				}
				if(defaultedDate != null && defaultedDate.before(futureDate) && defaultedDate.after(patientProgramDate.getDateEnrolled()) && defaultedDate.before(new Date())) {
					preArtOutcomes.put(defaultedDate, "Defaulted");
				}

				if(dateLost != null && dateLost.before(futureDate) && dateLost.after(patientProgramDate.getDateEnrolled()) && dateLost.before(new Date())) {
					preArtOutcomes.put(dateLost, "LTFU");
				}
				if(initialArtStart != null && dateTo != null) {
					preArtOutcomes.remove(initialArtStart);
				}
				//pick the last item in the tree map
				//check first if it is null
				if(preArtOutcomes.size() > 0) {
					Map.Entry<Date, String> values = preArtOutcomes.lastEntry();
					if(values != null){
						status = values.getValue();
					}
					if(preArtOutcomes.containsValue("Transferred out")){
						status ="Transferred out";
					}
					if(preArtOutcomes.containsValue("Died")){
						status ="Died";
					}
					if(preArtOutcomes.containsValue("Initiated ART")){
						status ="Initiated ART";
					}
				}
				ret.put(ptId, new SimpleResult(status, this));
			}

		}
		 return  ret;
	}

	CalculationResultMap ltfuMap(Collection<Integer> cohort, PatientCalculationContext context, Integer period) {
		CalculationResultMap ret = new CalculationResultMap();
		CalculationResultMap resultMap = returnVisitDate(cohort, context, period);
		Set<Integer> isTransferOut = CalculationUtils.patientsThatPass(calculate(new IsTransferOutCalculation(), cohort, context));
		for (Integer ptId : cohort) {
			Date dateLost = null;
			SimpleResult lastScheduledReturnDateResults = (SimpleResult) resultMap.get(ptId);
			Date lastScheduledReturnDate = (Date) lastScheduledReturnDateResults.getValue();
			if (lastScheduledReturnDate != null && !(isTransferOut.contains(ptId))) {
				dateLost = DateUtil.adjustDate(lastScheduledReturnDate, HivConstants.LOST_TO_FOLLOW_UP_THRESHOLD_DAYS, DurationUnit.DAYS );
			}
			ret.put(ptId, new SimpleResult(dateLost, this));
		}
		return ret;
	}

	CalculationResultMap returnVisitDate(Collection<Integer> cohort, PatientCalculationContext context, Integer period) {
		CalculationResultMap ret = new CalculationResultMap();

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		CalculationResultMap enrolledHere = Calculations.firstEnrollments(hivProgram, cohort, context);
		CalculationResultMap dateLastSeen = dateLastSeen(cohort, context);

		Set<Integer> alive = Filters.alive(cohort, context);
		Concept RETURN_VISIT_DATE = Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE);
		Set<Integer> transferredOut = CalculationUtils.patientsThatPass(calculate(new IsTransferOutCalculation(), cohort, context));
		for(Integer ptId: cohort) {
			PatientProgram patientProgram = EmrCalculationUtils.resultForPatient(enrolledHere, ptId);
			Date returnVisitDate = null;
			List<Visit> allVisits = Context.getVisitService().getVisitsByPatient(Context.getPatientService().getPatient(ptId));
			Date lastSeenDate = EmrCalculationUtils.datetimeResultForPatient(dateLastSeen, ptId);
			List<Visit> requiredVisits = new ArrayList<Visit>();
			if(alive.contains(ptId) && !(transferredOut.contains(ptId)) && period != null && patientProgram != null) {
				Date futureDate = DateUtil.adjustDate(DateUtil.adjustDate(patientProgram.getDateEnrolled(), period, DurationUnit.MONTHS), 1, DurationUnit.DAYS);
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
							returnVisitDate =null;

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
				if (returnVisitDate == null) {
					returnVisitDate = DateUtil.adjustDate(lastSeenDate, 30, DurationUnit.DAYS);
				}
			}
			ret.put(ptId, new SimpleResult(returnVisitDate, this));
		}
		return ret;
	}
	CalculationResultMap defaultedMap(Collection<Integer> cohort, PatientCalculationContext context, Integer period) {
		CalculationResultMap ret = new CalculationResultMap();
		CalculationResultMap resultMap = returnVisitDate(cohort, context, period);
		Set<Integer> isTransferOut = CalculationUtils.patientsThatPass(calculate(new IsTransferOutCalculation(), cohort, context));
		for (Integer ptId : cohort) {
			Date dateDefaulted = null;
			SimpleResult lastScheduledReturnDateResults = (SimpleResult) resultMap.get(ptId);
			if (lastScheduledReturnDateResults != null) {
				Date lastScheduledReturnDate = (Date) lastScheduledReturnDateResults.getValue();
				if(lastScheduledReturnDate != null && !(isTransferOut.contains(ptId))) {
					dateDefaulted = CoreUtils.dateAddDays(lastScheduledReturnDate, 30);
				}
			}

			ret.put(ptId, new SimpleResult(dateDefaulted, this));
		}
		return ret;
	}

	Date dateOnlyDate(String string) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.parse(string);

	}

	CalculationResultMap dateLastSeen(Collection<Integer> cohort, PatientCalculationContext context){
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		CalculationResultMap dateEnrolledMap = Calculations.firstEnrollments(hivProgram, cohort, context);

		CalculationResultMap lastEncounter = Calculations.lastEncounter(null, cohort, context);
		CalculationResultMap initialArtStart = calculate(new InitialArtStartDateCalculation(), cohort, context);

		CalculationResultMap result = new CalculationResultMap();
		for (Integer ptId : cohort) {
			Encounter encounter = EmrCalculationUtils.encounterResultForPatient(lastEncounter, ptId);
			PatientProgram patientProgram = EmrCalculationUtils.resultForPatient(dateEnrolledMap, ptId);
			Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(initialArtStart, ptId);
			Date encounterDate = null;
			if(patientProgram != null) {
				if (encounter != null) {

						if(artStartDate != null && artStartDate.after(encounter.getEncounterDatetime())) {
							encounterDate = artStartDate;
						}
						else {
							encounterDate = encounter.getEncounterDatetime();
						}
				}
				if(encounterDate == null && artStartDate != null) {
					encounterDate = artStartDate;
				}

				if(encounterDate == null){
					encounterDate = patientProgram.getDateEnrolled();
				}
			}
			result.put(ptId, new SimpleResult(encounterDate, this));
		}
		return result;
	}

	int daysBetweenDates(Date d1, Date d2) {
		DateTime dateTime1 = new DateTime(d1.getTime());
		DateTime dateTime2 = new DateTime(d2.getTime());
		return Math.abs(Days.daysBetween(dateTime1, dateTime2).getDays());
	}

}
