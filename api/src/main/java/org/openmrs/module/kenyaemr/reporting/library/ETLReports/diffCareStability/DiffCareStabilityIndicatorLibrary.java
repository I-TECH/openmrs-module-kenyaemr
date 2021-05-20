/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.diffCareStability;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of HIV related indicator definitions.
 */
@Component
public class DiffCareStabilityIndicatorLibrary {
    @Autowired
    private DiffCareStabilityCohortLibrary cohortLibrary;

    /*stableUnder1Monthtca*/
    /**
     * Number of stable patients in care with under 1 month prescription
     * @return the indicator
     */
    public CohortIndicator stableUnder1Monthtca() {
        return cohortIndicator("Stable patients with under 1 month prescription", ReportUtils.map(cohortLibrary.stableUnder1Monthtca(), ""));
    }

    /**
     * Number of stable patients in care with over 4 months prescription
     * @return the indicator
     */
    public CohortIndicator stableOver6Monthstca() {
        return cohortIndicator("Stable patients 6+ months prescription", ReportUtils.map(cohortLibrary.stableOver6Monthstca(), ""));
    }

    /**
     * Number of stable patients in care with less than 4 months prescription
     * @return
     */
    public  CohortIndicator stableUnder4Monthstca() {
        return cohortIndicator("Stable patients <4 months prescription", ReportUtils.map(cohortLibrary.stableUnder4Monthstca(), ""));
    }


    /**
     * Number of unstable patients in care
     * @return the indicator
     */
    public CohortIndicator unstable() {
        return cohortIndicator("Unstable Patients" , ReportUtils.map(cohortLibrary.unstable(), ""));
    }

    /**
     * Number of patients in care with undocumented stability in the last follow-up visit
     * @return the indicator
     */
    public CohortIndicator undocumentedStability() {
        return cohortIndicator("Undocumented stability" , ReportUtils.map(cohortLibrary.undocumentedStability(), ""));
    }

    /**
     * Number of patients in care and have multi-month appointment
     * @return the indicator
     */
    public CohortIndicator stablePatientsMultiMonthAppointments(Integer month) {
        return cohortIndicator("Stable with Multi-month appointment" , ReportUtils.map(cohortLibrary.stablePatientsMultiMonthAppointments(month), ""));
    }


}
