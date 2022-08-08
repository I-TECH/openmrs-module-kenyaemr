/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.shared.hiv;

import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of DHIS2 related indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
@Component
public class Dhis2IndicatorLibrary {
	 @Autowired
	 Dhis2CohortLibrary dhis2CohortLibrary;

	public CohortIndicator dummyCohortIndicatorMethod(){
		return  cohortIndicator("Dummy Indicator", map(dhis2CohortLibrary.dummyCohortDefinitionMethod(), "onOrAfter=${startDate},onOrBefore=${endDate}"));
	}
}
