/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.facilityDashboard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.reporting.builder.hiv.DashBoardCohorts;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Facility dashboard page controller
 */
public class MiniFacilityDashboardFragmentController {

	private final Log log = LogFactory.getLog(this.getClass());
	
	public String controller(FragmentModel model, UiUtils ui, HttpSession session, @SpringBean KenyaUiUtils kenyaUi) {


		Integer  allPatients = 0,  patientsOnArt = 0,
				patientsInCare = 0, patientsNewOnArt = 0, vlInLast12Months = 0,
				suppressedInLast12Months = 0, patientsScheduled =0, patientsSeen = 0,
				checkedIn =0 , unscheduledVisits=0, enrolledInHiv = 0, newlyEnrolledInHiv = 0;
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


		evaluationContext.addParameterValue("endDate", new Date());

		// external link for data tool
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(EmrConstants.GP_DATA_TOOL_URL);
		String datatoolUrl = null;
		if(gp != null) {
			datatoolUrl = gp.getPropertyValue();
		}

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

		model.addAttribute("dataToolUrl", datatoolUrl);


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