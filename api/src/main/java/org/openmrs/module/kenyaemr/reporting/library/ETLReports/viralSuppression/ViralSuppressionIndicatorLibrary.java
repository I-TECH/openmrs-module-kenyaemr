/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.viralSuppression;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of HIV related indicator definitions.
 */
@Component
public class ViralSuppressionIndicatorLibrary {
    @Autowired
    private ViralSuppressionCohortLibrary cohortLibrary;

    /**
     * Number of patients with suppressed viral load
     * @return the indicator
     */
    public CohortIndicator suppressed() {
        return cohortIndicator("Patient suppressed", ReportUtils.map(cohortLibrary.suppressed(), "endDate=${endDate}"));
    }

    /**
     * Number of patients with unsuppressed viral load
     * @return
     */
    public  CohortIndicator unsuppressed() {
        return cohortIndicator("Unsuppressed VL", ReportUtils.map(cohortLibrary.unsuppressed(), "endDate=${endDate}"));
    }


    /**
     * Number of patients with no current vl result
     * @return the indicator
     */
    public CohortIndicator noCurrentVLResults() {
        return cohortIndicator("No Current VL Results", ReportUtils.map(cohortLibrary.noCurrentVLResults(), "endDate=${endDate}"));
    }

    /**
     * Number of patients with no vl result
     * @return the indicator
     */
    public CohortIndicator noVLResults() {
        return cohortIndicator("No VL Results", ReportUtils.map(cohortLibrary.noVLResults(), "endDate=${endDate}"));
    }


}
