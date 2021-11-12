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
import org.openmrs.module.kenyaemr.reporting.builder.hiv.DashBoardCohorts;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

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


		Integer htsTested = 0,htsPositive = 0, htsLinked = 0, allPatients = 0,  patientsOnArt = 0,
				patientsInCare = 0, patientsNewOnArt = 0, vlInLast12Months = 0,
				suppressedInLast12Months = 0, patientsScheduled =0, patientsSeen = 0,
				checkedIn =0 , unscheduledVisits=0, enrolledInHiv = 0, newlyEnrolledInHiv = 0,
				htsTestedFamily =0,htsTestedPartners =0,htsTestedIDU =0, htsPositiveFamily = 0,htsPositivePartner = 0,
				htsPositiveIDU = 0, htsUnknownStatusFamily = 0,htsUnknownStatusPartner = 0, htsUnknownStatusIDU = 0,htsLinkedFamily = 0,
				htsLinkedPartner = 0, htsLinkedIDU = 0, unstableUnder15 = 0, unstableFemales15Plus = 0, unstableMales15Plus = 0, stableUnder4mtca = 0, stableOver4mtca = 0, currInCareOnART  = 0,
				stableOver4mtcaBelow15 = 0, stableOver4mtcaOver15M = 0, stableOver4mtcaOver15F = 0, stableUnder4mtcaBelow15 = 0,
				stableUnder4mtcaOver15M = 0,stableUnder4mtcaOver15F = 0, currInCareOnARTUnder15 = 0,currInCareOnARTOver15M = 0,
				currInCareOnARTOver15F = 0, undocumentedStability = 0, fullyVaccinatedCovid19 = 0,partiallyVaccinatedCovid19 = 0,notVaccinatedCovid19 = 0,
				everPositiveForCovid19 = 0,everHospitalizedOfCovid19 = 0,diedOfCovid19 = 0;
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

		Set<Integer> htsTotalTestedFamily = DashBoardCohorts.htsTotalTestedFamily(evaluationContext).getMemberIds();
		htsTestedFamily = htsTotalTestedFamily != null? htsTotalTestedFamily.size(): 0;

		Set<Integer> htsTotalPositiveFamily = DashBoardCohorts.htsTotalPositiveFamily(evaluationContext).getMemberIds();
		htsPositiveFamily = htsTotalPositiveFamily != null? htsTotalPositiveFamily.size(): 0;

		Set<Integer> htsUnknownStatusFamilyContact = DashBoardCohorts.htsUnknownStatusFamily(evaluationContext).getMemberIds();
		htsUnknownStatusFamily = htsUnknownStatusFamilyContact != null? htsUnknownStatusFamilyContact.size(): 0;

		Set<Integer> htsTotalLinkedFamily = DashBoardCohorts.htsTotalLinkedFamily(evaluationContext).getMemberIds();
		htsLinkedFamily = htsTotalLinkedFamily != null? htsTotalLinkedFamily.size(): 0;

		Set<Integer> htsTotalTestedPartners = DashBoardCohorts.htsTotalTestedPartner(evaluationContext).getMemberIds();
		htsTestedPartners = htsTotalTestedPartners != null? htsTotalTestedPartners.size(): 0;

		Set<Integer> htsPositivePartners = DashBoardCohorts.htsTotalPositivePartner(evaluationContext).getMemberIds();
		htsPositivePartner = htsPositivePartners != null? htsPositivePartners.size(): 0;

		Set<Integer> htsUnknownStatusPartnerContact = DashBoardCohorts.htsUnknownStatusPartner(evaluationContext).getMemberIds();
		htsUnknownStatusPartner = htsUnknownStatusPartnerContact != null? htsUnknownStatusPartnerContact.size(): 0;

		Set<Integer> htsTotalLinkedPartners = DashBoardCohorts.htsTotalLinkedPartners(evaluationContext).getMemberIds();
		htsLinkedPartner = htsTotalLinkedPartners != null? htsTotalLinkedPartners.size(): 0;

		Set<Integer> htsTestedIDUs = DashBoardCohorts.htsTotalTestedIDU(evaluationContext).getMemberIds();
		htsTestedIDU = htsTestedIDUs != null? htsTestedIDUs.size(): 0;

		Set<Integer> htsPositiveIDUs = DashBoardCohorts.htsTotalPositiveIDU(evaluationContext).getMemberIds();
		htsPositiveIDU = htsPositiveIDUs != null? htsPositiveIDUs.size(): 0;

		Set<Integer> htsUnknownStatusIDUs = DashBoardCohorts.htsUnknownStatusIDU(evaluationContext).getMemberIds();
		htsUnknownStatusIDU = htsUnknownStatusIDUs != null? htsUnknownStatusIDUs.size(): 0;

		Set<Integer> htsLinkedIDUs = DashBoardCohorts.htsTotalLinkedIDU(evaluationContext).getMemberIds();
		htsLinkedIDU = htsLinkedIDUs != null? htsLinkedIDUs.size(): 0;


		Set<Integer> stableOver4monthsTca = DashBoardCohorts.stableOver4Monthstca(evaluationContext).getMemberIds();
		stableOver4mtca = stableOver4monthsTca != null? stableOver4monthsTca.size(): 0;

		Set<Integer> stableUnder4monthsTca = DashBoardCohorts.stableUnder4Monthstca(evaluationContext).getMemberIds();
		stableUnder4mtca = stableUnder4monthsTca != null? stableUnder4monthsTca.size(): 0;

		Set<Integer> unstablePatientsUnder15 = DashBoardCohorts.unstablePatientsUnder15(evaluationContext).getMemberIds();
		unstableUnder15 = unstablePatientsUnder15 != null? unstablePatientsUnder15.size(): 0;

		Set<Integer> unstableFemalePatients15Plus = DashBoardCohorts.unstableFemalePatients15Plus(evaluationContext).getMemberIds();
		unstableFemales15Plus = unstableFemalePatients15Plus != null? unstableFemalePatients15Plus.size(): 0;

		Set<Integer> unstableMalePatients15Plus = DashBoardCohorts.unstableMalePatients15Plus(evaluationContext).getMemberIds();
		unstableMales15Plus = unstableMalePatients15Plus != null? unstableMalePatients15Plus.size(): 0;

		Set<Integer> currentInCareOnART = DashBoardCohorts.currentInCareOnART(evaluationContext).getMemberIds();
		currInCareOnART = currentInCareOnART != null? currentInCareOnART.size(): 0;

		Set<Integer> stableOver4mtcaBelow15y = DashBoardCohorts.stableOver4MonthstcaUnder15(evaluationContext).getMemberIds();
		stableOver4mtcaBelow15 = stableOver4mtcaBelow15y != null? stableOver4mtcaBelow15y.size(): 0;

		Set<Integer> stableOver4mtcaOver15yM = DashBoardCohorts.stableOver4MonthstcaOver15Male(evaluationContext).getMemberIds();
		stableOver4mtcaOver15M = stableOver4mtcaOver15yM != null? stableOver4mtcaOver15yM.size(): 0;

		Set<Integer> stableOver4mtcaOver15yF = DashBoardCohorts.stableOver4MonthstcaOver15Female(evaluationContext).getMemberIds();
		stableOver4mtcaOver15F = stableOver4mtcaOver15yF != null? stableOver4mtcaOver15yF.size(): 0;

		Set<Integer> stableUnder4mtcaBelow15y = DashBoardCohorts.stableUnder4MonthstcaUnder15(evaluationContext).getMemberIds();
		stableUnder4mtcaBelow15 = stableUnder4mtcaBelow15y != null? stableUnder4mtcaBelow15y.size(): 0;

		Set<Integer> stableUnder4mtcaOver15My = DashBoardCohorts.stableUnder4MonthstcaOver15Male(evaluationContext).getMemberIds();
		stableUnder4mtcaOver15M = stableUnder4mtcaOver15My != null? stableUnder4mtcaOver15My.size(): 0;

		Set<Integer> stableUnder4mtcaOver15yF = DashBoardCohorts.stableUnder4MonthstcaOver15Female(evaluationContext).getMemberIds();
		stableUnder4mtcaOver15F = stableUnder4mtcaOver15yF != null? stableUnder4mtcaOver15yF.size(): 0;

		Set<Integer> currInCareOnARTUnder15y = DashBoardCohorts.currentInCareOnARTUnder15(evaluationContext).getMemberIds();
		currInCareOnARTUnder15 = currInCareOnARTUnder15y != null? currInCareOnARTUnder15y.size(): 0;

		Set<Integer> currInCareOnARTOver15yM = DashBoardCohorts.currentInCareOnARTOver15Male(evaluationContext).getMemberIds();
		currInCareOnARTOver15M = currInCareOnARTOver15yM != null? currInCareOnARTOver15yM.size(): 0;

		Set<Integer> currInCareOnARTOver15yF = DashBoardCohorts.currentInCareOnARTOver15Female(evaluationContext).getMemberIds();
		currInCareOnARTOver15F = currInCareOnARTOver15yF != null? currInCareOnARTOver15yF.size(): 0;


		Set<Integer> undocumentedPatientStability = DashBoardCohorts.undocumentedPatientStability(evaluationContext).getMemberIds();
		undocumentedStability = undocumentedPatientStability != null? undocumentedPatientStability.size(): 0;
		DashBoardCohorts dashBoardCohorts = new DashBoardCohorts();

		evaluationContext.addParameterValue("endDate", new Date());

		Set<Integer> fullyVaccinated = dashBoardCohorts.fullyVaccinated(evaluationContext).getMemberIds();
		fullyVaccinatedCovid19 = fullyVaccinated != null? fullyVaccinated.size(): 0;

		Set<Integer> partiallyVaccinated = dashBoardCohorts.partiallyVaccinated(evaluationContext).getMemberIds();
		partiallyVaccinatedCovid19 = partiallyVaccinated != null? partiallyVaccinated.size(): 0;

		Set<Integer> notVaccinated = dashBoardCohorts.notVaccinated(evaluationContext).getMemberIds();
		notVaccinatedCovid19 = notVaccinated != null? notVaccinated.size(): 0;

		Set<Integer> everTestedPositiveCovid19 = dashBoardCohorts.everTestedCovid19(evaluationContext).getMemberIds();
		everPositiveForCovid19 = everTestedPositiveCovid19 != null? everTestedPositiveCovid19.size(): 0;

		Set<Integer> everHospitalized = dashBoardCohorts.everHospitalizedOfCovid19(evaluationContext).getMemberIds();
		everHospitalizedOfCovid19 = everHospitalized != null? everHospitalized.size(): 0;

		Set<Integer> covid19Deaths = dashBoardCohorts.diedOfCovid19(evaluationContext).getMemberIds();
		diedOfCovid19 = covid19Deaths != null? covid19Deaths.size(): 0;

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
		model.addAttribute("htsTestedFamily", htsTestedFamily);
		model.addAttribute("htsPositiveFamily", htsPositiveFamily);
		model.addAttribute("htsUnknownStatusFamily", htsUnknownStatusFamily);
		model.addAttribute("htsLinkedFamily", htsLinkedFamily);
		model.addAttribute("htsTestedPartners", htsTestedPartners);
		model.addAttribute("htsPositivePartner", htsPositivePartner);
		model.addAttribute("htsUnknownStatusPartner", htsUnknownStatusPartner);
		model.addAttribute("htsLinkedPartner", htsLinkedPartner);
		model.addAttribute("htsTestedIDU", htsTestedIDU);
		model.addAttribute("htsPositiveIDU", htsPositiveIDU);
		model.addAttribute("htsUnknownStatusIDU", htsUnknownStatusIDU);
		model.addAttribute("htsLinkedIDU", htsLinkedIDU);
		model.addAttribute("stableOver4mtca", stableOver4mtca);
		model.addAttribute("stableUnder4mtca", stableUnder4mtca);
		model.addAttribute("unstableUnder15", unstableUnder15);
		model.addAttribute("unstableFemales15Plus", unstableFemales15Plus);
		model.addAttribute("unstableMales15Plus", unstableMales15Plus);
		model.addAttribute("currInCareOnART", currInCareOnART);
		model.addAttribute("stableOver4mtcaBelow15", stableOver4mtcaBelow15);
		model.addAttribute("stableOver4mtcaOver15M", stableOver4mtcaOver15M);
		model.addAttribute("stableOver4mtcaOver15F", stableOver4mtcaOver15F);
		model.addAttribute("stableUnder4mtcaBelow15", stableUnder4mtcaBelow15);
		model.addAttribute("stableUnder4mtcaOver15M", stableUnder4mtcaOver15M);
		model.addAttribute("stableUnder4mtcaOver15F", stableUnder4mtcaOver15F);
		model.addAttribute("currInCareOnARTUnder15", currInCareOnARTUnder15);
		model.addAttribute("currInCareOnARTOver15M", currInCareOnARTOver15M);
		model.addAttribute("currInCareOnARTOver15F", currInCareOnARTOver15F);
		model.addAttribute("undocumentedStability", undocumentedStability);
		model.addAttribute("fullyVaccinatedCovid19", fullyVaccinatedCovid19);
		model.addAttribute("partiallyVaccinatedCovid19", partiallyVaccinatedCovid19);
		model.addAttribute("notVaccinatedCovid19", notVaccinatedCovid19);
		model.addAttribute("everPositiveForCovid19", everPositiveForCovid19);
		model.addAttribute("everHospitalizedOfCovid19", everHospitalizedOfCovid19);
		model.addAttribute("diedOfCovid19", diedOfCovid19);

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