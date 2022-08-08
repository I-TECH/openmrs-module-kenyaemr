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
import org.openmrs.module.kenyaemr.reporting.cohort.definition.otz.*;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public CohortIndicator ltfu(Integer month) {
        return cohortIndicator("Lost Follow up", ReportUtils.map(otzCohorts.otzLostToFollowup(month), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator adultCare(Integer month) {
        return cohortIndicator("Transitioned to adult care", ReportUtils.map(otzCohorts.transitionedToAdultCare(month), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator dead(Integer month) {
        return cohortIndicator("Dead", ReportUtils.map(otzCohorts.reportedDead(month), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator optOut(Integer month) {
        return cohortIndicator("Opt Out", ReportUtils.map(otzCohorts.optedOutOfOtz(month), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator appointmentsHonored() {
        return cohortIndicator("Honored Appointments", ReportUtils.map(otzCohorts.honoredAppointments(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator adolecentsInProgram(Integer month) {
        return cohortIndicator("Person age 20-24 still in OTZ program", ReportUtils.map(otzCohorts.numberOfAdolescentsInotzProgram(month), "startDate=${startDate},endDate=${endDate}"));

    }

    public CohortIndicator exitedPostotz(Integer month) {
        return cohortIndicator("Exited Post OTZ", ReportUtils.map(otzCohorts.exitedPostOtz(month), "startDate=${startDate},endDate=${endDate}"));

    }

    public CohortIndicator adolescentsAdherenceGood() {
        return cohortIndicator("Adherence Good", ReportUtils.map(otzCohorts.adherenceGreaterThan90(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator bookedAppointments() {
        return cohortIndicator("Booked Appointments", ReportUtils.map(otzCohorts.bookedForAppointmentInTheMonth(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator patientsWithValidVLOnReportingPeriod(Integer month) {
        return cohortIndicator("Valid VL on reporting month", ReportUtils.map(otzCohorts.patientWithValidVL(month), "startDate=${startDate},endDate=${endDate}"));

    }

    public CohortIndicator patientsWithValidVLLess1000(Integer month) {
        return cohortIndicator("Valid VL Less 1000 on reporting month",  ReportUtils.map(otzCohorts.patientWithValidVLLess1000(month), "startDate=${startDate},endDate=${endDate}"));

    }

    public CohortIndicator patientsWithValidVLLess400(Integer month) {
        return cohortIndicator("Valid VL Less 400 on reporting month", ReportUtils.map(otzCohorts.patientWithValidVLLess400(month), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator patientsSwithedToSecondlineArt(Integer month) {
        return cohortIndicator("Switch to second line ART", ReportUtils.map(otzCohorts.patientSwitchToSecondLineART(month), "startDate=${startDate},endDate=${endDate}"));

    }

    public CohortIndicator patientsSwithedToThirdlineArt(Integer month) {
        return cohortIndicator("Switch to third line ART", ReportUtils.map(otzCohorts.patientSwitchToThirdLineART(month), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator patientWithRepeatVLasLDL(Integer month) {
        return cohortIndicator("Patient with Repeat LV as LDL", ReportUtils.map(otzCohorts.patientWithRepeatVLasLDL(month), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator patientWithRepeatVLResults(Integer month) {
        return cohortIndicator("Patient with Repeat VL results", ReportUtils.map(otzCohorts.patientWithRepeatVLasLDL(month), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator patientWithRepeatVLMoreThan1000(Integer month) {
        return cohortIndicator("Repeat VL less than 1000 copies/ml", ReportUtils.map(otzCohorts.patientWithRepeatVLMoreThan1000(month), "startDate=${startDate},endDate=${endDate}"));

    }
    public CohortIndicator patientWithRepeatVLLessThan1000(Integer month) {
        return cohortIndicator("Repeat VL less than 1000 copies/ml", ReportUtils.map(otzCohorts.patientWithRepeatVLLessThan1000(month), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator patientWithRepeatVLLessThan400(Integer month) {
        return cohortIndicator("Repeat VL less than 400 copies/ml", ReportUtils.map(otzCohorts.patientWithRepeatVLLessThan400(month), "startDate=${startDate},endDate=${endDate}"));

    }

    public CohortIndicator patientWithRoutineFollowupVLLessThan400(Integer month) {
        return cohortIndicator("Routine Follow up VL less than 400 copies/ml", ReportUtils.map(otzCohorts.patientWithRoutineFollowupVLLessThan400(month), "startDate=${startDate},endDate=${endDate}"));

    }

    public CohortIndicator patientWithRoutineFollowupVLLessThan1000(Integer month) {
        return cohortIndicator("Routine Follow up VL less than 1000 copies/ml", ReportUtils.map(otzCohorts.patientWithRoutineFollowupVLLessThan1000(month), "startDate=${startDate},endDate=${endDate}"));

    }

    public CohortIndicator patientWithRoutineFollowupVLGreaterThan1000(Integer month) {
        return cohortIndicator("Routine Follow up VL more or equal to 1000 copies/ml", ReportUtils.map(otzCohorts.patientWithRoutineFollowupVLGreaterThan1000(month), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator patientWithRoutineFollowupVL(Integer month) {
        return cohortIndicator("Patient with Routine Follow up VL", ReportUtils.map(otzCohorts.patientWithRoutineFollowupVL(month), "startDate=${startDate},endDate=${endDate}"));

    }

    public CohortIndicator patientSamplesTakenForRoutineVL(Integer month) {
        return cohortIndicator("Patients sample taken for Routine VL", ReportUtils.map(otzCohorts.patientSamplesTakenForRoutineVL(month), "startDate=${startDate},endDate=${endDate}"));

    }

    public CohortIndicator patientEligibleForRoutineVL(Integer month) {
        return cohortIndicator("Patients Eligible for Routine VL", ReportUtils.map(otzCohorts.patientEligibleForRoutineVL(month), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator patientWithRoutineVLResultsLDL(Integer month) {
        return cohortIndicator("Patients with Routine VL Results as LDL", ReportUtils.map(otzCohorts.patientWithRoutineVLResultsLDL(month), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator patientWithVLMoreThan1000AtEnrollment(Integer month) {
        return cohortIndicator("Patients with VL Results more than 1000 copies at enrollment", ReportUtils.map(otzCohorts.patientWithVLMoreThan1000AtEnrollment(month), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator otzMembersWhoCompletedAllModules(Integer month) {
        return cohortIndicator("Patients who completed all the 8 OTZ modules", ReportUtils.map(otzCohorts.otzMembersWhoCompletedAllModules(month), "startDate=${startDate},endDate=${endDate}"));

    }

    public CohortIndicator patientWithValidVLasLDL(Integer month) {
        return cohortIndicator("Patients with valid vl reported as LDL", ReportUtils.map(otzCohorts.patientWithValidVLasLDL(), "startDate=${startDate},endDate=${endDate}"));

    }

    public CohortIndicator patientWithValidVLGreaterThan1000(Integer month) {
        return cohortIndicator("Patients with valid vl greater than or equals to 1000 copies", ReportUtils.map(otzCohorts.patientWithValidVLGreaterThan1000(), "startDate=${startDate},endDate=${endDate}"));

    }

    public CohortIndicator patientTransferredOut(Integer month) {
        return cohortIndicator("patients Transferred out of OTZ", ReportUtils.map(otzCohorts.transferredOut(month), "startDate=${startDate},endDate=${endDate}"));
    }


}


