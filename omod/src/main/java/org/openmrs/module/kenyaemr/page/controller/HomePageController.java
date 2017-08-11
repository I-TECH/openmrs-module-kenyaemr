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

package org.openmrs.module.kenyaemr.page.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.reporting.builder.hiv.DashBoardCohorts;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
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
 * Home page controller
 */
public class HomePageController {

	private final Log log = LogFactory.getLog(this.getClass());
	
	public String controller(PageModel model, UiUtils ui, HttpSession session, @SpringBean KenyaUiUtils kenyaUi) {

		// Redirect to setup page if module is not yet configured
		if (Context.getService(KenyaEmrService.class).isSetupRequired()) {
			kenyaUi.notifySuccess(session, "First-Time Setup Needed");
			return "redirect:" + ui.pageLink(EmrConstants.MODULE_ID, "admin/firstTimeSetup");
		}

		Integer allPatients = 0,  patientsOnArt = 0, patientsInCare = 0, patientsNewOnArt = 0, vlInLast12Months = 0, suppressedInLast12Months = 0;
		EvaluationContext evaluationContext = new EvaluationContext();
		Calendar calendar = Calendar.getInstance();
		int thisMonth = calendar.get(calendar.MONTH);

		Map<String, Date> dateMap = getReportDates(thisMonth - 1);
		Date startDate = dateMap.get("startDate");
		Date endDate = dateMap.get("endDate");
		SimpleDateFormat df = new SimpleDateFormat("MMM-yyyy");
		String reportingPeriod = df.format(endDate);

		log.info("Start Date: " + startDate + ", End Date: " + endDate);

		evaluationContext.addParameterValue("startDate", startDate);
		evaluationContext.addParameterValue("endDate", endDate);

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

		model.addAttribute("allPatients", allPatients);
		model.addAttribute("inCare", patientsInCare);
		model.addAttribute("onArt", patientsOnArt);
		model.addAttribute("newOnArt", patientsNewOnArt);
		model.addAttribute("reportPeriod", reportingPeriod);
		model.addAttribute("vlResults", vlInLast12Months);
		model.addAttribute("suppressedVl", suppressedInLast12Months);
		
		return null;
	}

	private Map<String, Date> getReportDates(int month){
		Map<String, Date> reportDates = new HashMap<String, Date>();
		Calendar gc = new GregorianCalendar();
		gc.set(Calendar.MONTH, month);
		gc.set(Calendar.DAY_OF_MONTH, 1);
		gc.set(Calendar.HOUR, 0);
		gc.set(Calendar.MINUTE, 0);
		gc.set(Calendar.SECOND, 0);
		gc.set(Calendar.MILLISECOND, 0);
		Date monthStart = gc.getTime();
		reportDates.put("startDate", monthStart);
		gc.add(Calendar.MONTH, 1);
		gc.add(Calendar.DAY_OF_MONTH, -1);
		Date monthEnd = gc.getTime();
		reportDates.put("endDate", monthEnd);
		return reportDates;
	}

}