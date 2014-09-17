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
package org.openmrs.module.kenyaemr.reporting.library.shared.hiv;

import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of Quality Improvement indicators for HIV care patients in MCHMS and MCHCS
 */
@Component
public class QiEmtctIndicatorLibrary {

	@Autowired
	private QiEmtctCohortLibrary qiEmtctCohortLibrary;

	/**
	 *% of pregnant women attending at least four ANC visits
	 * @return org.openmrs.module.reporting.indicator.CohortIndicator
	 */
	public CohortIndicator patientsAttendingAtLeast4AncVisitsAndPregnant() {
		return cohortIndicator("Pregnant women who had at least 4 during the review period",
				map(qiEmtctCohortLibrary.patientsAttendingAtLeastAncVisitsAndPregnant(4), "onDate=${endDate}" ),
				map(qiEmtctCohortLibrary.patientsAttendingAtLeastAncVisitsAndPregnant(0), "onDate=${endDate}" )
		);
	}

	/**
	 * % of skilled deliveries within the facility catchment population
	 * @return CohortIndicator
	 */
	public CohortIndicator skilledDeliveriesWithinFacility() {
		return cohortIndicator("Skilled deliveries within the facility",
				map(qiEmtctCohortLibrary.womenDeliveredInFacility(), "onOrBefore=${endDate}" ),
				map(qiEmtctCohortLibrary.numberOfExpectedDeliveriesInTheFacilityCatchmentPopulationDuringTheReviewPeriod(), "onOrAfter=${endDate-6m},onOrBefore=${endDate}" )
		);
	}
}
