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
        return cohortIndicator("Patient suppressed", ReportUtils.map(cohortLibrary.suppressed(), ""));
    }

    /**
     * Number of patients with unsuppressed viral load
     * @return
     */
    public  CohortIndicator unsuppressed() {
        return cohortIndicator("New Enrollment in care (excludes transfers)", ReportUtils.map(cohortLibrary.unsuppressed(), ""));
    }


    /**
     * Number of patients with no vl result
     * @return the indicator
     */
    public CohortIndicator noVLResults() {
        return cohortIndicator("Currently on ART", ReportUtils.map(cohortLibrary.noVLResults(), ""));
    }




}
