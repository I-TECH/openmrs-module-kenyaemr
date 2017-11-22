/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyaemr.fragment.controller.facilityDashboard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.kenyaemr.reporting.builder.hiv.DashBoardCohorts;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageModel;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Facility dashboard page controller
 */
public class FacilityDashboardFragmentController {

	private final Log log = LogFactory.getLog(this.getClass());
	
	public String controller(FragmentModel model, UiUtils ui, HttpSession session, @SpringBean KenyaUiUtils kenyaUi) {


		Integer allPatients = 0,  patientsOnArt = 0,
				patientsInCare = 0, patientsNewOnArt = 0, vlInLast12Months = 0,
				suppressedInLast12Months = 0, patientsScheduled =0, patientsSeen = 0,
				checkedIn =0 , unscheduledVisits=0, enrolledInHiv = 0, newlyEnrolledInHiv = 0,
				htsTested =0, htsPositive = 0, htsLinked = 0;
		EvaluationContext evaluationContext = new EvaluationContext();
		Calendar calendar = Calendar.getInstance();
		int thisMonth = calendar.get(calendar.MONTH);


		SimpleDateFormat todayFormat = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Date> dateMap = getReportDates(thisMonth - 1);
		Date startDate = dateMap.get("startDate");
		Date endDate = dateMap.get("endDate");
		Date todaysDate = todaysDate();
		SimpleDateFormat df = new SimpleDateFormat("MMM-yyyy");
		String reportingPeriod = df.format(endDate);


		evaluationContext.addParameterValue("startDate", startDate);
		evaluationContext.addParameterValue("endDate", endDate);
		evaluationContext.addParameterValue("enrolledOnOrBefore", endDate);


		Set<Integer> all = DashBoardCohorts.allPatients(evaluationContext).getMemberIds();
		allPatients = all != null? all.size(): 0;

		Set<Integer> onArt = DashBoardCohorts.onART(evaluationContext).getMemberIds();
		patientsOnArt = onArt != null? onArt.size(): 0;

		Set<Integer> inCare = DashBoardCohorts.inCare(evaluationContext).getMemberIds();
		patientsInCare = inCare != null? inCare.size(): 0;

		Set<Integer> startingArt = DashBoardCohorts.newOnART(evaluationContext).getMemberIds();
		patientsNewOnArt = startingArt != null? startingArt.size(): 0;

		Set<Integer> vlResultsInLast12Months = DashBoardCohorts.viralLoadResultsIn12Months(evaluationContext).getMemberIds();
		vlInLast12Months = vlResultsInLast12Months != null? vlResultsInLast12Months.size(): 0;

		Set<Integer> viralSuppressionInLast12Months = DashBoardCohorts.viralLoadSuppressionIn12Months(evaluationContext).getMemberIds();
		suppressedInLast12Months = viralSuppressionInLast12Months != null? viralSuppressionInLast12Months.size(): 0;

		Set<Integer> patientsScheduledToday = DashBoardCohorts.patientsScheduledToday(evaluationContext).getMemberIds();
		patientsScheduled = patientsScheduledToday != null? patientsScheduledToday.size(): 0;

		Set<Integer> patientsSeenToday = DashBoardCohorts.patientsSeen(evaluationContext).getMemberIds();
		patientsSeen = patientsSeenToday != null? patientsSeenToday.size(): 0;

		Set<Integer> patientsCheckedIn = DashBoardCohorts.checkedInAppointments(evaluationContext).getMemberIds();
		checkedIn = patientsCheckedIn != null? patientsCheckedIn.size(): 0;

		Set<Integer> patientsWithUnscheduledVisit = DashBoardCohorts.unscheduledAppointments(evaluationContext).getMemberIds();
		unscheduledVisits = patientsWithUnscheduledVisit != null? patientsWithUnscheduledVisit.size(): 0;

		Set<Integer> cummulativeEnrolledInHiv = DashBoardCohorts.enrolledInHiv(evaluationContext).getMemberIds();
		enrolledInHiv = cummulativeEnrolledInHiv != null? cummulativeEnrolledInHiv.size(): 0;

		Set<Integer> newEnrollmentsInHiv = DashBoardCohorts.newlyEnrolledInHiv(evaluationContext).getMemberIds();
		newlyEnrolledInHiv = newEnrollmentsInHiv != null? newEnrollmentsInHiv.size(): 0;

		Set<Integer> htsTotalTested = DashBoardCohorts.htsTotalTested(evaluationContext).getMemberIds();
		htsTested = htsTotalTested != null? htsTotalTested.size(): 0;

		Set<Integer> htsTotalPositive = DashBoardCohorts.htsTotalPositive(evaluationContext).getMemberIds();
		htsPositive = htsTotalPositive != null? htsTotalPositive.size(): 0;

		Set<Integer> htsTotalLinked = DashBoardCohorts.htsTotalLinked(evaluationContext).getMemberIds();
		htsLinked = htsTotalLinked != null? htsTotalLinked.size(): 0;

		model.addAttribute("allPatients", allPatients);
		model.addAttribute("inCare", patientsInCare);
		model.addAttribute("onArt", patientsOnArt);
		model.addAttribute("newOnArt", patientsNewOnArt);
		model.addAttribute("cumulativeEnrolledInHiv", enrolledInHiv);
		model.addAttribute("newlyEnrolledInHiv", newlyEnrolledInHiv);
		model.addAttribute("reportPeriod", reportingPeriod);
		model.addAttribute("vlResults", vlInLast12Months);
		model.addAttribute("suppressedVl", suppressedInLast12Months);
		model.addAttribute("patientsScheduled", patientsScheduled);
		model.addAttribute("patientsSeen", patientsSeen);
		model.addAttribute("checkedIn", checkedIn);
		model.addAttribute("unscheduled", unscheduledVisits);
		model.addAttribute("htsTested", htsTested);
		model.addAttribute("htsPositive", htsPositive);
		model.addAttribute("htsLinked", htsLinked);

		return null;
	}

	private Map<String, Date> getReportDates(int month){
		Map<String, Date> reportDates = new HashMap<String, Date>();
		Calendar gc = new GregorianCalendar();
		gc.set(Calendar.MONTH, month);
		gc.set(Calendar.DAY_OF_MONTH, 1);
		gc.clear(Calendar.HOUR);
		gc.clear(Calendar.HOUR_OF_DAY);
		gc.clear(Calendar.MINUTE);
		gc.clear(Calendar.SECOND);
		gc.clear(Calendar.MILLISECOND);
		Date monthStart = gc.getTime();
		reportDates.put("startDate", monthStart);
		gc.add(Calendar.MONTH, 1);
		gc.add(Calendar.DAY_OF_MONTH, -1);
		Date monthEnd = gc.getTime();
		reportDates.put("endDate", monthEnd);
		return reportDates;
	}

	private Date todaysDate(){
		Calendar gc = new GregorianCalendar();
		gc.clear(Calendar.HOUR);
		gc.clear(Calendar.HOUR_OF_DAY);
		gc.clear(Calendar.MINUTE);
		gc.clear(Calendar.SECOND);
		gc.clear(Calendar.MILLISECOND);
		Date today = gc.getTime();

		return today;
	}

}