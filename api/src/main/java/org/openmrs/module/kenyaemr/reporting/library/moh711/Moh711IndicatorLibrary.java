/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.moh711;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Indicators specific to the MOH711 report
 */
@Component
public class Moh711IndicatorLibrary {

	@Autowired
	private Moh711CohortLibrary moh711Cohorts;

	/**
	 * No.of New ANC Clients (First ANC visit)
	 * @return the indicator
	 */
	public CohortIndicator noOfNewANCClients() {
		return cohortIndicator("Number of new ANC clients (First ANC Visit)", ReportUtils.map(moh711Cohorts.noOfANCClients(), "startDate=${startDate},endDate=${endDate}"));
	}
		/**
	 * No.of revisiting ANC Clients
	 * @return the indicator
	 */
	public CohortIndicator noOfANCClientsRevisits() {
		return cohortIndicator("Number of revisiting ANC clients", ReportUtils.map(moh711Cohorts.noOfANCClientsRevisits(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No.of Clients given IPT (1st dose)
	 */
	public CohortIndicator noOfANCClientsGivenIPT1stDose() {
		return cohortIndicator("No.of Clients given IPT (1st dose)", ReportUtils.map(moh711Cohorts.noOfANCClientsGivenIPT1stDose(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 *No.of Clients given IPT (2nd dose)
	 */
	public CohortIndicator noOfANCClientsGivenIPT2ndDose() {
		return cohortIndicator("No.of Clients given IPT (2nd dose)", ReportUtils.map(moh711Cohorts.noOfANCClientsGivenIPT2ndDose(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 *No.of Clients given IPT (3rd dose)
	 * @return
	 */
	public CohortIndicator noOfANCClientsGivenIPT3rdDose() {
		return cohortIndicator("No.of Clients given IPT (3rd dose)", ReportUtils.map(moh711Cohorts.noOfANCClientsGivenIPT3rdDose(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 *No.of Clients with Hb < 11 g/dl
	 */
	public CohortIndicator noOfANCClientsLowHB() {
		return cohortIndicator("No.of Clients with Hb < 11 g/dl", ReportUtils.map(moh711Cohorts.noOfANCClientsLowHB(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 *No.of Clients completed 4 Antenatal Visits
	 */
	public CohortIndicator ancClientsCompleted4Visits() {
		return cohortIndicator("No.of Clients completed 4 Antenatal Visits", ReportUtils.map(moh711Cohorts.ancClientsCompleted4Visits(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 *No.LLINs distributed to under 1 year
	 */
	public CohortIndicator distributedLLINsUnder1Year() {
		return cohortIndicator("No.of Clients completed 4 Antenatal Visits", ReportUtils.map(moh711Cohorts.distributedLLINsUnder1Year(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 *No.of LLINs distributed to ANC clients
	 */
	public CohortIndicator distributedLLINsToANCClients() {
		return cohortIndicator("No.of LLINs distributed to ANC clients", ReportUtils.map(moh711Cohorts.distributedLLINsToANCClients(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No.of clients Tested for Syphilis
	 */
	public CohortIndicator ancClientsTestedForSyphillis() {
		return cohortIndicator("No.of clients Tested for Syphilis", ReportUtils.map(moh711Cohorts.ancClientsTestedForSyphillis(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 *No.of clients Tested Positive for Syphilis
	 */
	public CohortIndicator ancClientsTestedSyphillisPositive() {
		return cohortIndicator("No.of clients Tested Positive for Syphilis", ReportUtils.map(moh711Cohorts.ancClientsTestedSyphillisPositive(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 *Total women done breast examination
	 */
	public CohortIndicator breastExaminationDone() {
		return cohortIndicator("Total women done breast examination", ReportUtils.map(moh711Cohorts.breastExaminationDone(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No.of adolescents (10-14 years) presenting with pregnancy at 1st ANC Visit
	 */
	public CohortIndicator adolescents10To14FirstANC() {
		return cohortIndicator("No.of adolescents (10-14 years) presenting with pregnancy at 1st ANC Visit", ReportUtils.map(moh711Cohorts.adolescents10To14FirstANC(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 *No.of adolescents (15-19 years) presenting with pregnancy at 1st ANC Visit
	 */
	public CohortIndicator adolescents15To19FirstANC() {
		return cohortIndicator("No.of adolescents (15-19 years) presenting with pregnancy at 1st ANC Visit", ReportUtils.map(moh711Cohorts.adolescents15To19FirstANC(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 *No.of youth (20-24 years) presenting with pregnancy at 1st ANC Visit
	 */
	public CohortIndicator youth20To24FirstANC() {
		return cohortIndicator("No.of youth (20-24 years) presenting with pregnancy at 1st ANC Visit", ReportUtils.map(moh711Cohorts.youth20To24FirstANC(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * No.of Women presenting with pregnancy at 1ST ANC in the First Trimeseter(<= 12 Weeks)
	 * @return
	 */
	public CohortIndicator presentingPregnancy1stANC1stTrimester() {
		return cohortIndicator("No.of youth (20-24 years) presenting with pregnancy at 1st ANC Visit", ReportUtils.map(moh711Cohorts.presentingPregnancy1stANC1stTrimester(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * No.of clients issued with Iron
	 * @return
	 */
	public CohortIndicator ancClientsIssuedWithIron() {
		return cohortIndicator("No.of clients issued with Iron", ReportUtils.map(moh711Cohorts.ancClientsIssuedWithIron(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * No.of clients issued with Folic
	 * @return
	 */
	public CohortIndicator ancClientsIssuedWithFolic() {
		return cohortIndicator("No.of clients issued with Folic", ReportUtils.map(moh711Cohorts.ancClientsIssuedWithFolic(), "startDate=${startDate},endDate=${endDate}"));
	}

	/**
	 * No.of clients issued with Combined Ferrous Folate
	 * @return
	 */
	public CohortIndicator ancClientsIssuedWithFerrousFolic() {
		return cohortIndicator("No.of clients issued with Combined Ferrous Folate", ReportUtils.map(moh711Cohorts.ancClientsIssuedWithFerrousFolic(), "startDate=${startDate},endDate=${endDate}"));
	}
	/**
	 * No.of pregnant women presenting in ANC with complication associated with FGM
	 * @return
	 */
	public CohortIndicator ancClientsWithFGMRelatedComplications() {
		return cohortIndicator("No.of pregnant women presenting in ANC with complication associated with FGM", ReportUtils.map(moh711Cohorts.ancClientsWithFGMRelatedComplications(), "startDate=${startDate},endDate=${endDate}"));
	}
	/*
	*//**
	 * Number of patients who are ART revisits
	 * @return the indicator
	 *//*
	public CohortIndicator revisitsArt() {
		return cohortIndicator("Revisits ART", ReportUtils.map(moh711Cohorts.revisitsArt(), "fromDate=${startDate},toDate=${endDate}"));
	}

	*//**
	 * Number of patients who are currently on ART
	 * @return the indicator
	 *//*
	public CohortIndicator currentlyOnArt() {
		return cohortIndicator("Currently on ART", ReportUtils.map(moh711Cohorts.currentlyOnArt(), "fromDate=${startDate},toDate=${endDate}"));
	}

	*//**
	 * Cumulative number of patients on ART
	 * @return the indicator
	 *//*
	public CohortIndicator cumulativeOnArt() {
		return cohortIndicator("Cumulative ever on ART", ReportUtils.map(artCohorts.startedArtExcludingTransferinsOnDate(), "onOrBefore=${endDate}"));
	}

	*//**
	 * Number of patients in the ART 12 month cohort
	 * @return the indicator
	 *//*
	public CohortIndicator art12MonthNetCohort() {
		//add a hacky way to determine if art start date is at the end of every month then add one day
		//to avoid reporting twice in the previouse and the following month
		return cohortIndicator("ART 12 Month Net Cohort", ReportUtils.map(artCohorts.netCohortMonths(12), "onDate=${endDate + 1d}"));
	}

	*//**
	 * Number of patients in the 12 month cohort who are on their original first-line regimen
	 * @return the indicator
	 *//*
	public CohortIndicator onOriginalFirstLineAt12Months() {
		return cohortIndicator("On original 1st line at 12 months", ReportUtils.map(moh711Cohorts.onOriginalFirstLineAt12Months(), "fromDate=${startDate},toDate=${endDate + 1d}"));
	}

	*//**
	 * Number of patients in the 12 month cohort who are on an alternate first-line regimen
	 * @return the indicator
	 *//*
	public CohortIndicator onAlternateFirstLineAt12Months() {
		return cohortIndicator("On alternate 1st line at 12 months", ReportUtils.map(moh711Cohorts.onAlternateFirstLineAt12Months(), "fromDate=${startDate},toDate=${endDate + 1d}"));
	}

	*//**
	 * Number of patients in the 12 month cohort who are on a second-line regimen
	 * @return the indicator
	 *//*
	public CohortIndicator onSecondLineAt12Months() {
		return cohortIndicator("On 2nd line at 12 months", ReportUtils.map(moh711Cohorts.onSecondLineAt12Months(), "fromDate=${startDate},toDate=${endDate + 1d}"));
	}

	*//**
	 * Number of patients in the 12 month cohort who are on ART
	 * @return the indicator
	 *//*
	public CohortIndicator onTherapyAt12Months() {
		return cohortIndicator("On therapy at 12 months", ReportUtils.map(moh711Cohorts.onTherapyAt12Months(), "fromDate=${startDate},toDate=${endDate + 1d}"));
	}

	*//**
	 * Number of HIV care visits for females aged 18 and over
	 * @return the indicator
	 *//*
	public Indicator hivCareVisitsFemale18() {
		HivCareVisitsIndicator ind = new HivCareVisitsIndicator();
		ind.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ind.addParameter(new Parameter("endDate", "End Date", Date.class));
		ind.setFilter(HivCareVisitsIndicator.Filter.FEMALES_18_AND_OVER);
		return ind;
	}

	*//**
	 * Number of scheduled HIV care visits
	 * @return the indicator
	 *//*
	public Indicator hivCareVisitsScheduled() {
		HivCareVisitsIndicator ind = new HivCareVisitsIndicator();
		ind.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ind.addParameter(new Parameter("endDate", "End Date", Date.class));
		ind.setFilter(HivCareVisitsIndicator.Filter.SCHEDULED);
		return ind;
	}

	*//**
	 * Number of unscheduled HIV care visits
	 * @return the indicator
	 *//*
	public Indicator hivCareVisitsUnscheduled() {
		HivCareVisitsIndicator ind = new HivCareVisitsIndicator();
		ind.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ind.addParameter(new Parameter("endDate", "End Date", Date.class));
		ind.setFilter(HivCareVisitsIndicator.Filter.UNSCHEDULED);
		return ind;
	}

	*//**
	 * Total number of HIV care visits
	 * @return the indicator
	 *//*
	public Indicator hivCareVisitsTotal() {
		HivCareVisitsIndicator ind = new HivCareVisitsIndicator();
		ind.addParameter(new Parameter("startDate", "Start Date", Date.class));
		ind.addParameter(new Parameter("endDate", "End Date", Date.class));
		return ind;
	}*/
}