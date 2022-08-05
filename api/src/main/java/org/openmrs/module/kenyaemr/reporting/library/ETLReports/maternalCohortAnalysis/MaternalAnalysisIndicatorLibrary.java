/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.kenyaemr.reporting.library.ETLReports.maternalCohortAnalysis;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of maternal cohort related indicator definitions.
 */
@Component
public class MaternalAnalysisIndicatorLibrary {
    @Autowired
    private MaternalAnalysisCohortLibrary cohortLibrary;

    /**
     * Number in Maternal Cohort 12 months KP
     * @return the indicator
     */
    public CohortIndicator originaCohortKp12Months() {
        return cohortIndicator("Original cohort-KP (12 months)", ReportUtils.map(cohortLibrary.originalMaternalKpCohort(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort 12 months NP
     * @return the indicator
     */
    public CohortIndicator originaCohortNp12Months() {
        return cohortIndicator("Original cohort-NP (12 months)", ReportUtils.map(cohortLibrary.originalMaternalNpCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort TI 3 months KP
     * @return the indicator
     */
    public CohortIndicator transferInMaternalKp3MonthsCohort() {
        return cohortIndicator("Transfer In Maternal Kp 3 Months Cohort", ReportUtils.map(cohortLibrary.transferInMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort TI 3 months NP
     * @return the indicator
     */
    public CohortIndicator transferInMaternalNp3MonthsCohort() {
        return cohortIndicator("Transfer In Maternal Np 3 Months Cohort", ReportUtils.map(cohortLibrary.transferInMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort TO 3 months KP
     * @return the indicator
     */
    public CohortIndicator transferOutMaternalKp3MonthsCohort() {
        return cohortIndicator("Transfer Out Maternal Kp 3 Months Cohort", ReportUtils.map(cohortLibrary.transferOutMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort TO 3 months NP
     * @return the indicator
     */
    public CohortIndicator transferOutMaternalNp3MonthsCohort() {
        return cohortIndicator("Transfer Out Maternal Np 3 Months Cohort", ReportUtils.map(cohortLibrary.transferOutMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Discharge to CCC 3 months KP
     * @return the indicator
     */
    public CohortIndicator dischargedToCCCMaternalKp3MonthsCohort() {
        return cohortIndicator("Discharged to CCC Maternal Kp 3 Months Cohort", ReportUtils.map(cohortLibrary.dischargedToCCCMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Discharged to CCC 3 months NP
     * @return the indicator
     */
    public CohortIndicator dischargedToCCCMaternalNp3MonthsCohort() {
        return cohortIndicator("Discharged to CCC Maternal Np 3 Months Cohort", ReportUtils.map(cohortLibrary.dischargedToCCCMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Net Cohort  3 months KP
     * @return the indicator
     */
    public CohortIndicator netCohortMaternalKp3MonthsCohort() {
        return cohortIndicator("Net Cohort Maternal Kp 3 Months Cohort", ReportUtils.map(cohortLibrary.netCohortMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Net Cohort 3 months NP
     * @return the indicator
     */
    public CohortIndicator netCohortMaternalNp3MonthsCohort() {
        return cohortIndicator("Net Cohort Maternal Np 3 Months Cohort", ReportUtils.map(cohortLibrary.netCohortMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort LTFU  3 months KP
     * @return the indicator
     */
    public CohortIndicator ltfuMaternalKp3MonthsCohort() {
        return cohortIndicator("Interruption In treatment (LTFU) Maternal Kp 3 Months Cohort", ReportUtils.map(cohortLibrary.ltfuMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort LTFU 3 months NP
     * @return the indicator
     */
    public CohortIndicator ltfuMaternalNp3MonthsCohort() {
        return cohortIndicator("Interruption In treatment (LTFU) Maternal Np 3 Months Cohort", ReportUtils.map(cohortLibrary.ltfuMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort reported deceased  3 months KP
     * @return the indicator
     */
    public CohortIndicator deceasedMaternalKp3MonthsCohort() {
        return cohortIndicator("Reported dead Maternal Kp 3 Months Cohort", ReportUtils.map(cohortLibrary.deceasedMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort deceased 3 months NP
     * @return the indicator
     */
    public CohortIndicator deceasedMaternalNp3MonthsCohort() {
        return cohortIndicator("Reported dead Maternal Np 3 Months Cohort", ReportUtils.map(cohortLibrary.deceasedMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Stopped treatment  3 months KP
     * @return the indicator
     */
    public CohortIndicator stoppedTreatmentMaternalKp3MonthsCohort() {
        return cohortIndicator("Stopped treatment Maternal Kp 3 Months Cohort", ReportUtils.map(cohortLibrary.stoppedTreatmentMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Stopped treatment 3 months NP
     * @return the indicator
     */
    public CohortIndicator stoppedTreatmentMaternalNp3MonthsCohort() {
        return cohortIndicator("Stopped treatment Maternal Np 3 Months Cohort", ReportUtils.map(cohortLibrary.stoppedTreatmentMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Alive and Active treatment  3 months KP
     * @return the indicator
     */
    public CohortIndicator aliveAndActiveOnTreatmentMaternalKp3MonthsCohort() {
        return cohortIndicator("Alive and Active on treatment Maternal Kp 3 Months Cohort", ReportUtils.map(cohortLibrary.aliveAndActiveOnTreatmentMaternalKp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Alive and Active treatment 3 months NP
     * @return the indicator
     */
    public CohortIndicator aliveAndActiveOnTreatmentMaternalNp3MonthsCohort() {
        return cohortIndicator("Alive and Active on treatment Maternal Np 3 Months Cohort", ReportUtils.map(cohortLibrary.aliveAndActiveOnTreatmentMaternalNp3MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort Vl samples taken 3 months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithViralLoadSamplesCollectedKp3Months() {
        return cohortIndicator("Vl samples collected Maternal Kp 3 Months Cohort", ReportUtils.map(cohortLibrary.maternalCohortWithViralLoadSamplesCollectedKp3Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Vl samples taken 3 months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithViralLoadSamplesCollectedNp3Months() {
        return cohortIndicator("Vl samples collected Maternal Np 3 Months Cohort", ReportUtils.map(cohortLibrary.maternalCohortWithViralLoadSamplesCollectedNp3Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort Vl results received 3 months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithViralLoadResultsReceivedKp3Months() {
        return cohortIndicator("Vl results received Maternal Kp 3 Months Cohort", ReportUtils.map(cohortLibrary.maternalCohortWithViralLoadResultsReceivedKp3Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Vl results received 3 months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithViralLoadResultsReceivedNp3Months() {
        return cohortIndicator("Vl results received Maternal Np 3 Months Cohort", ReportUtils.map(cohortLibrary.maternalCohortWithViralLoadResultsReceivedNp3Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort VL results < 1000 3 months KP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan1000Kp3Months() {
        return cohortIndicator("Viral Load results  <1000 copies/ml Kp (3 months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan10003Kp3Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results < 1000 3 months NP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan1000Np3Months() {
        return cohortIndicator("Viral Load results  <1000 copies/ml Np (3 months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan10003Np3Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort VL results < 400 3 months KP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan400Kp3Months() {
        return cohortIndicator("Viral Load results  <400 copies/ml Kp (3 months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan400Kp3Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results < 400 3 months NP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan400Np3Months() {
        return cohortIndicator("Viral Load results  <400 copies/ml Np (3 months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan400Np3Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort VL results < 50 3 months KP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan50Kp3Months() {
        return cohortIndicator("Viral Load results  <50 copies/ml Kp (3 months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan50Kp3Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results < 50 3 months NP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan50Np3Months() {
        return cohortIndicator("Viral Load results  <50 copies/ml Np (3 months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan50Np3Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort VL results >= 1000 3 months KP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsMoreThan1000Kp3Months() {
        return cohortIndicator("Viral Load results  >=1000 copies/ml Kp (3 months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000Kp3Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results >= 1000 3 months NP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsMoreThan1000Np3Months() {
        return cohortIndicator("Viral Load results  >=1000 copies/ml Np (3 months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000Np3Months(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number in Maternal Cohort VL results >= 400 3 months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan400WithEACsKp3Months() {
        return cohortIndicator("Viral Load results  >=400 copies/ml with EACs Kp (3 months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan400WithEACsKp3Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results >= 400 3 months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan400WithEACsNp3Months() {
        return cohortIndicator("Viral Load results  >=400 copies/ml with EACs Np (3 months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan400WithEACsNp3Months(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number in Maternal Cohort VL results >=1000 with STF 3 months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan1000WithSTFKp3Months() {
        return cohortIndicator("Viral Load results >=1000 copies/ml with STF Kp (3 months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFKp3Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results >=1000 with STF 3 months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan1000WithSTFNp3Months() {
        return cohortIndicator("Viral Load results >=1000 copies/ml with STF Np (3 months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFNp3Months(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number in Maternal Cohort VL results >=1000 with STF and Repeat Vl 3 months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlKp3Months() {
        return cohortIndicator("Viral Load results >=1000 copies/ml with STF and Repeat VL Kp (3 months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlKp3Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results >=1000 with STF and Repeat Vl 3 months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlNp3Months() {
        return cohortIndicator("Viral Load results >=1000 copies/ml with STF and Repeat VL Np (3 months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlNp3Months(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number switched regimen line after confirmed treatment failure 3 months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortSwitchedRegimenLineAfterConfirmedSTFKp3Months() {
        return cohortIndicator("Number Switched Regimen Line after Confirmed Treatment Failure Kp (3 months)", ReportUtils.map(cohortLibrary.maternalCohortSwitchedRegimenLineAfterConfirmedSTFKp3Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number switched regimen line after confirmed treatment failure 3 months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortSwitchedRegimenLineAfterConfirmedSTFNp3Months() {
        return cohortIndicator("Number Switched Regimen Line after Confirmed Treatment Failure Np (3 months)", ReportUtils.map(cohortLibrary.maternalCohortSwitchedRegimenLineAfterConfirmedSTFNp3Months(), "startDate=${startDate},endDate=${endDate}"));
    }
}
