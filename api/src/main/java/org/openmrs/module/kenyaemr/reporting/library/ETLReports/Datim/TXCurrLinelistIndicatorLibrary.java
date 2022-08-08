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
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.RevisedDatim.DatimCohortLibrary;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.Datim.TXCurrLinelistCohortLibrary;

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
        return cohortIndicator("Present in current but missing in previous report", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReport(), "endDate=${endDate}"));
    }

    /**
     * Present in current but missing in previous report - New enrolment
     */
    public CohortIndicator txCurLinelistForPatientsPresentInCurrentButMissingInPreviousQuarterlyNewlyEnrolledReport() {
        return cohortIndicator("Present in current but missing in previous report - newly enrolled", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInCurrentButMissingInPreviousQuarterlyNewlyEnrolledReport(), "endDate=${endDate}"));
    }
    /**
     * Present in current but missing in previous report - Transfer in
     */
    public CohortIndicator txCurLinelistForPatientsPresentInCurrentButMissingInPreviousTrfInQuarterlyReport() {
        return cohortIndicator("Present in current but missing in previous report as a result of transfer in", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportTrfIn(), "endDate=${endDate}"));
    }
    /**
     * Present in current but missing in previous report - returned to care
     */
    public CohortIndicator txCurLinelistForPatientsPresentInCurrentButMissingInPreviousQuarterlyReEnrollmentReport() {
        return cohortIndicator("Present in current but missing in previous report as a result of returning to care", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportReEnrollment(), "endDate=${endDate}"));
    }

    /**
     * present in previous but missing in present report
     */
    public CohortIndicator txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport() {
        return cohortIndicator("present in previous but missing in present report", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReport(), "endDate=${endDate}"));
    }
    /**
     * present in previous but missing in present report due to death - Quarterly
     */
    public CohortIndicator txCurLinelistForPatientsPresentInPreviousButMissingInCurrentDueToDeathReport() {
        return cohortIndicator("present in previous but missing in present report due to death", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInPreviousButMissingInCurrentDueToDeathReport(), "endDate=${endDate}"));
    }
    /**
     * present in previous but missing in present report due to LTFU - Quarterly
     */
    public CohortIndicator txCurLinelistForPatientsPresentInPreviousButMissingInCurrentLTFUReport() {
        return cohortIndicator("present in previous but missing in present report due to LTFU", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInPreviousButMissingInCurrentLTFUReport(), "endDate=${endDate}"));
    }
    /**
     * present in previous but missing in present report due to Transfer Out - Quarterly
     */
    public CohortIndicator txCurLinelistForPatientsPresentInPreviousButMissingInCurrentTrfOutReport() {
        return cohortIndicator("present in previous but missing in present report due to Transfer out", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInPreviousButMissingInCurrentTrfOutReport(), "endDate=${endDate}"));
    }
    /**
     * present in previous but missing in present report due to stopping treatment - Quarterly
     */
    public CohortIndicator txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportStoppedTxQuarterly() {
        return cohortIndicator("present in previous but missing in present report due to Stopping Treatment", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportStoppedTxQuarterly(), "endDate=${endDate}"));
    }
    /**
     * Present in current but missing in previous report - monthly
     */
    public CohortIndicator txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly() {
        return cohortIndicator("Present in current but missing in previous report", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthly(), "endDate=${endDate}"));
    }


    /**
     * present in previous but missing in present report - monthly
     */
    public CohortIndicator txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly() {
        return cohortIndicator("present in previous but missing in present report", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportMonthly(), "endDate=${endDate}"));
    }

    /**
     * TX_CURR Present in Current period but missing in previous period - Re-enrollment/return To care
     */
    public CohortIndicator txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthlyReEnrollment() {
        return cohortIndicator("TX_CURR Present in Current period but missing in previous period - Re-enrollment/return To care", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthlyReEnrollment(), "endDate=${endDate}"));
    }
    /**
     * TX_CURR Present in Current period but missing in previous period - TI
     */
    public CohortIndicator txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthlyTrfIn() {
        return cohortIndicator("TX_CURR Present in Current period but missing in previous period - TI", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthlyTrfIn(), "endDate=${endDate}"));
    }
    /**
     * TX_CURR Present in Current period but missing in previous period - newly enrolled
     */
    public CohortIndicator txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthlyNewlyEnrolled() {
        return cohortIndicator("TX_CURR Present in Current period but missing in previous period - newly enrolled", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInCurrentButMissingInPreviousReportMonthlyNewlyEnrolled(), "endDate=${endDate}"));
    }
    /**
     * present in previous but missing in present report due to death - monthly
     */
    public CohortIndicator txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportDiedMonthly() {
        return cohortIndicator("present in previous but missing in present report due to death", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportDueToDeathMonthly(), "endDate=${endDate}"));
    }

    /**
     * present in previous but missing in present report due to LTFU - monthly
     */
    public CohortIndicator txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportLTFUMonthly() {
        return cohortIndicator("present in previous but missing in present report due to LTFU", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportDueToLTFUMonthly(), "endDate=${endDate}"));
    }

    /**
     * present in previous but missing in present report due to Transferred out - monthly
     */
    public CohortIndicator txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportTrfOutMonthly() {
        return cohortIndicator("present in previous but missing in present report due to Tfr out", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportDueToTrfOutMonthly(), "endDate=${endDate}"));
    }
    /**
     * present in previous but missing in present report due to stopping treatment - Monthly
     */
    public CohortIndicator txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportStoppedTxMonthly() {
        return cohortIndicator("present in previous but missing in present report due to Stopping Treatment", ReportUtils.map(datimCohorts.txCurLinelistForPatientsPresentInPreviousButMissingInCurrentReportStoppedTxMonthly(), "endDate=${endDate}"));
    }
}
