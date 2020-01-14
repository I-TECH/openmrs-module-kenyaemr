/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.ipt;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.pmtct.anc.*;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of IPT related indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
@Component
public class IPTIndicatorLibrary {
@Autowired
IPTCohortLibrary iptCohorts;

	public CohortIndicator numberOnIPT() {
		return cohortIndicator("No.on IPT", map(iptCohorts.patientsOnIPT(), "startDate=${startDate},endDate=${endDate}"));
	}
	public CohortIndicator plhivOnIPT() {
		return cohortIndicator("PLHIV on IPT", map(iptCohorts.PLHIVInitiatedIPT(), "startDate=${startDate},endDate=${endDate}"));
	}
	public CohortIndicator prisonersOnIPT() {
		return cohortIndicator("Prisoners on IPT", map(iptCohorts.prisonersInitiatedIPT(), "startDate=${startDate},endDate=${endDate}"));
	}
	public CohortIndicator hcwOnIPT() {
		return cohortIndicator("HCW on IPT", map(iptCohorts.hcwInitiatedIPT(), "startDate=${startDate},endDate=${endDate}"));
	}
	public CohortIndicator childrenExposedTB() {
		return cohortIndicator("Children Exposed TB", map(iptCohorts.childrenExposedTB(), "startDate=${startDate},endDate=${endDate}"));
	}
	public CohortIndicator completedIPT() {
		return cohortIndicator("Completed IPT", map(iptCohorts.completedIPT(), "startDate=${startDate},endDate=${endDate}"));
	}
}