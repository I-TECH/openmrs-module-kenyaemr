/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.otz;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Created by dev on 11/28/19.
 */

/**
 * Library of OTZ related indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
@Component
public class ETLOtzIndicatorLibrary {
    @Autowired
    private ETLOtzCohortLibrary otzCohorts;

 // Baseline Information
    public CohortIndicator newOtzEnrollment() {
        return cohortIndicator("New Enrollment in otz (excludes transfers)", ReportUtils.map(otzCohorts.otzEnrollment(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator patientWithVLResultsLast6MonthsAtEnrollment() {
        return cohortIndicator("VL Results 6monthsAtEnrollment", ReportUtils.map(otzCohorts.patientWithVLResultWithinLast6MonthsAtEnrollment(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator patientWithVLResultsLessThan1000AtEnrollment() {
        return cohortIndicator("VL Results 6monthsAtEnrollment", ReportUtils.map(otzCohorts.patientWithVLlessThan1000AtEnrollment(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator patientWithVLResultsLessThan400AtEnrollment() {
        return cohortIndicator("VL Results 6monthsAtEnrollment", ReportUtils.map(otzCohorts.patientWithVLlessThan400AtEnrollment(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator patientWithVLResultsLDLAtEnrollment() {
        return cohortIndicator("VL Results 6monthsAtEnrollment", ReportUtils.map(otzCohorts.patientWithLDLAtEnrollment(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator attendSupportGroup() {
        return cohortIndicator("Attended Support Group", ReportUtils.map(otzCohorts.attendedSupportGroup(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator transferout() {
        return cohortIndicator("Transfer Out", ReportUtils.map(otzCohorts.transferredOut(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator ltfu() {
        return cohortIndicator("Lost Follow up", ReportUtils.map(otzCohorts.otzLostToFollowup(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator adultCare() {
        return cohortIndicator("Transitioned to adult care", ReportUtils.map(otzCohorts.transitionedToAdultCare(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator dead() {
        return cohortIndicator("Reported dead", ReportUtils.map(otzCohorts.reportedDead(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator optOut() {
        return cohortIndicator("Opt Out", ReportUtils.map(otzCohorts.optedOutOfOtz(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator appointmentsHonored() {
        return cohortIndicator("Honored Appointments", ReportUtils.map(otzCohorts.honoredAppointments(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator adolecentsInProgram() {
        return cohortIndicator("Adolescents In program", ReportUtils.map(otzCohorts.numberOfAdolescentsInotzProgram(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator exitedPostotz() {
        return cohortIndicator("Exited Post OTZ", ReportUtils.map(otzCohorts.exitedPostOtz(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator adolescentsAdherenceGood() {
        return cohortIndicator("Adherence Good", ReportUtils.map(otzCohorts.adherenceGreaterThan90(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator bookedAppointments() {
        return cohortIndicator("Booked Appointments", ReportUtils.map(otzCohorts.bookedForAppointmentInTheMonth(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator patientsWithValidVLOnReportingPeriod() {
        return cohortIndicator("Valid VL", ReportUtils.map(otzCohorts.patientWithValidVL(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator patientsWithValidVLLess1000() {
        return cohortIndicator("Valid VL less 1000", ReportUtils.map(otzCohorts.patientWithValidVLLess1000(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator patientsWithValidVLLess400() {
        return cohortIndicator("Valid VL Less 400", ReportUtils.map(otzCohorts.patientWithValidVLLess400(), "startDate=${startDate},endDate=${endDate}"));
    }








}


