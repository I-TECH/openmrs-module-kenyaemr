/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.Datim;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;


/**
 * Library of DATIM related indicator definitions for TX_CURR line lists at different reporting periods
 */
@Component
public class TXCurrLinelistIndicatorLibrary {
    @Autowired
    private TXCurrLinelistCohortLibrary datimCohorts;


    /**
     * Present in current but missing in previous report
     */
    public CohortIndicator txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport() {
        return cohortIndicator("Present in current but missing in previous report", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * present in previous but missing in present report
     */
    public CohortIndicator txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport() {
        return cohortIndicator("present in previous but missing in present report", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Present in current but missing in previous report - monthly
     */
    public CohortIndicator txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly() {
        return cohortIndicator("Present in current but missing in previous report", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * present in previous but missing in present report - monthly
     */
    public CohortIndicator txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly() {
        return cohortIndicator("present in previous but missing in present report", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly(), "startDate=${startDate},endDate=${endDate}"));
    }

}
