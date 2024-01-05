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
     * FIRST REVIEW: 3 Months Cohort
     */
    /**
     * Number in Maternal Cohort 12 months KP
     * @return the indicator
     */
    public CohortIndicator originalCohortKp12Months() {
        return cohortIndicator("Original cohort-KP (12 months)", ReportUtils.map(cohortLibrary.originalMaternalKpCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort 12 months NP
     * @return the indicator
     */
    public CohortIndicator originalCohortNp12Months() {
        return cohortIndicator("Original cohort-NP (12 months)", ReportUtils.map(cohortLibrary.originalMaternalNpCohort12Months(), "startDate=${startDate},endDate=${endDate}"));
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


    /**
     * FIRST REVIEW: 6 Months Cohort
     */
    /**
     * Number in Maternal Cohort TI 6 Months KP
     * @return the indicator
     */
    public CohortIndicator transferInMaternalKp6MonthsCohort() {
        return cohortIndicator("Transfer In Maternal Kp 6 Months Cohort", ReportUtils.map(cohortLibrary.transferInMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort TI 6 Months NP
     * @return the indicator
     */
    public CohortIndicator transferInMaternalNp6MonthsCohort() {
        return cohortIndicator("Transfer In Maternal Np 6 Months Cohort", ReportUtils.map(cohortLibrary.transferInMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort TO 6 Months KP
     * @return the indicator
     */
    public CohortIndicator transferOutMaternalKp6MonthsCohort() {
        return cohortIndicator("Transfer Out Maternal Kp 6 Months Cohort", ReportUtils.map(cohortLibrary.transferOutMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort TO 6 Months NP
     * @return the indicator
     */
    public CohortIndicator transferOutMaternalNp6MonthsCohort() {
        return cohortIndicator("Transfer Out Maternal Np 6 Months Cohort", ReportUtils.map(cohortLibrary.transferOutMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Discharge to CCC 6 Months KP
     * @return the indicator
     */
    public CohortIndicator dischargedToCCCMaternalKp6MonthsCohort() {
        return cohortIndicator("Discharged to CCC Maternal Kp 6 Months Cohort", ReportUtils.map(cohortLibrary.dischargedToCCCMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Discharged to CCC 6 Months NP
     * @return the indicator
     */
    public CohortIndicator dischargedToCCCMaternalNp6MonthsCohort() {
        return cohortIndicator("Discharged to CCC Maternal Np 6 Months Cohort", ReportUtils.map(cohortLibrary.dischargedToCCCMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Net Cohort  6 Months KP
     * @return the indicator
     */
    public CohortIndicator netCohortMaternalKp6MonthsCohort() {
        return cohortIndicator("Net Cohort Maternal Kp 6 Months Cohort", ReportUtils.map(cohortLibrary.netCohortMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Net Cohort 6 Months NP
     * @return the indicator
     */
    public CohortIndicator netCohortMaternalNp6MonthsCohort() {
        return cohortIndicator("Net Cohort Maternal Np 6 Months Cohort", ReportUtils.map(cohortLibrary.netCohortMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort LTFU  6 Months KP
     * @return the indicator
     */
    public CohortIndicator ltfuMaternalKp6MonthsCohort() {
        return cohortIndicator("Interruption In treatment (LTFU) Maternal Kp 6 Months Cohort", ReportUtils.map(cohortLibrary.ltfuMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort LTFU 6 Months NP
     * @return the indicator
     */
    public CohortIndicator ltfuMaternalNp6MonthsCohort() {
        return cohortIndicator("Interruption In treatment (LTFU) Maternal Np 6 Months Cohort", ReportUtils.map(cohortLibrary.ltfuMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort reported deceased  6 Months KP
     * @return the indicator
     */
    public CohortIndicator deceasedMaternalKp6MonthsCohort() {
        return cohortIndicator("Reported dead Maternal Kp 6 Months Cohort", ReportUtils.map(cohortLibrary.deceasedMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort deceased 6 Months NP
     * @return the indicator
     */
    public CohortIndicator deceasedMaternalNp6MonthsCohort() {
        return cohortIndicator("Reported dead Maternal Np 6 Months Cohort", ReportUtils.map(cohortLibrary.deceasedMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Stopped treatment  6 Months KP
     * @return the indicator
     */
    public CohortIndicator stoppedTreatmentMaternalKp6MonthsCohort() {
        return cohortIndicator("Stopped treatment Maternal Kp 6 Months Cohort", ReportUtils.map(cohortLibrary.stoppedTreatmentMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Stopped treatment 6 Months NP
     * @return the indicator
     */
    public CohortIndicator stoppedTreatmentMaternalNp6MonthsCohort() {
        return cohortIndicator("Stopped treatment Maternal Np 6 Months Cohort", ReportUtils.map(cohortLibrary.stoppedTreatmentMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Alive and Active treatment  6 Months KP
     * @return the indicator
     */
    public CohortIndicator aliveAndActiveOnTreatmentMaternalKp6MonthsCohort() {
        return cohortIndicator("Alive and Active on treatment Maternal Kp 6 Months Cohort", ReportUtils.map(cohortLibrary.aliveAndActiveOnTreatmentMaternalKp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Alive and Active treatment 6 Months NP
     * @return the indicator
     */
    public CohortIndicator aliveAndActiveOnTreatmentMaternalNp6MonthsCohort() {
        return cohortIndicator("Alive and Active on treatment Maternal Np 6 Months Cohort", ReportUtils.map(cohortLibrary.aliveAndActiveOnTreatmentMaternalNp6MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort Vl samples taken 6 Months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithViralLoadSamplesCollectedKp6Months() {
        return cohortIndicator("Vl samples collected Maternal Kp 6 Months Cohort", ReportUtils.map(cohortLibrary.maternalCohortWithViralLoadSamplesCollectedKp6Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Vl samples taken 6 Months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithViralLoadSamplesCollectedNp6Months() {
        return cohortIndicator("Vl samples collected Maternal Np 6 Months Cohort", ReportUtils.map(cohortLibrary.maternalCohortWithViralLoadSamplesCollectedNp6Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort Vl results received 6 Months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithViralLoadResultsReceivedKp6Months() {
        return cohortIndicator("Vl results received Maternal Kp 6 Months Cohort", ReportUtils.map(cohortLibrary.maternalCohortWithViralLoadResultsReceivedKp6Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Vl results received 6 Months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithViralLoadResultsReceivedNp6Months() {
        return cohortIndicator("Vl results received Maternal Np 6 Months Cohort", ReportUtils.map(cohortLibrary.maternalCohortWithViralLoadResultsReceivedNp6Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort VL results < 1000 6 Months KP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan1000Kp6Months() {
        return cohortIndicator("Viral Load results  <1000 copies/ml Kp (6 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan1000Kp6Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results < 1000 6 Months NP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan1000Np6Months() {
        return cohortIndicator("Viral Load results  <1000 copies/ml Np (6 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan1000Np6Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort VL results < 400 6 Months KP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan400Kp6Months() {
        return cohortIndicator("Viral Load results  <400 copies/ml Kp (6 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan400Kp6Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results < 400 6 Months NP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan400Np6Months() {
        return cohortIndicator("Viral Load results  <400 copies/ml Np (6 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan400Np6Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort VL results < 50 6 Months KP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan50Kp6Months() {
        return cohortIndicator("Viral Load results  <50 copies/ml Kp (6 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan50Kp6Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results < 50 6 Months NP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan50Np6Months() {
        return cohortIndicator("Viral Load results  <50 copies/ml Np (6 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan50Np6Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort VL results >= 1000 6 Months KP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsMoreThan1000Kp6Months() {
        return cohortIndicator("Viral Load results  >=1000 copies/ml Kp (6 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000Kp6Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results >= 1000 6 Months NP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsMoreThan1000Np6Months() {
        return cohortIndicator("Viral Load results  >=1000 copies/ml Np (6 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000Np6Months(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number in Maternal Cohort VL results >= 400 6 Months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan400WithEACsKp6Months() {
        return cohortIndicator("Viral Load results  >=400 copies/ml with EACs Kp (6 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan400WithEACsKp6Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results >= 400 6 Months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan400WithEACsNp6Months() {
        return cohortIndicator("Viral Load results  >=400 copies/ml with EACs Np (6 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan400WithEACsNp6Months(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number in Maternal Cohort VL results >=1000 with STF 6 Months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan1000WithSTFKp6Months() {
        return cohortIndicator("Viral Load results >=1000 copies/ml with STF Kp (6 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFKp6Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results >=1000 with STF 6 Months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan1000WithSTFNp6Months() {
        return cohortIndicator("Viral Load results >=1000 copies/ml with STF Np (6 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFNp6Months(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number in Maternal Cohort VL results >=1000 with STF and Repeat Vl 6 Months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlKp6Months() {
        return cohortIndicator("Viral Load results >=1000 copies/ml with STF and Repeat VL Kp (6 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlKp6Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results >=1000 with STF and Repeat Vl 6 Months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlNp6Months() {
        return cohortIndicator("Viral Load results >=1000 copies/ml with STF and Repeat VL Np (6 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlNp6Months(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number switched regimen line after confirmed treatment failure 6 Months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortSwitchedRegimenLineAfterConfirmedSTFKp6Months() {
        return cohortIndicator("Number Switched Regimen Line after Confirmed Treatment Failure Kp (6 Months)", ReportUtils.map(cohortLibrary.maternalCohortSwitchedRegimenLineAfterConfirmedSTFKp6Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number switched regimen line after confirmed treatment failure 6 Months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortSwitchedRegimenLineAfterConfirmedSTFNp6Months() {
        return cohortIndicator("Number Switched Regimen Line after Confirmed Treatment Failure Np (6 Months)", ReportUtils.map(cohortLibrary.maternalCohortSwitchedRegimenLineAfterConfirmedSTFNp6Months(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * FIRST REVIEW: 12 Months Cohort
     */
    /**
     * Number in Maternal Cohort TI 12 Months KP
     * @return the indicator
     */
    public CohortIndicator transferInMaternalKp12MonthsCohort() {
        return cohortIndicator("Transfer In Maternal Kp 12 Months Cohort", ReportUtils.map(cohortLibrary.transferInMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort TI 12 Months NP
     * @return the indicator
     */
    public CohortIndicator transferInMaternalNp12MonthsCohort() {
        return cohortIndicator("Transfer In Maternal Np 12 Months Cohort", ReportUtils.map(cohortLibrary.transferInMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort TO 12 Months KP
     * @return the indicator
     */
    public CohortIndicator transferOutMaternalKp12MonthsCohort() {
        return cohortIndicator("Transfer Out Maternal Kp 12 Months Cohort", ReportUtils.map(cohortLibrary.transferOutMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort TO 12 Months NP
     * @return the indicator
     */
    public CohortIndicator transferOutMaternalNp12MonthsCohort() {
        return cohortIndicator("Transfer Out Maternal Np 12 Months Cohort", ReportUtils.map(cohortLibrary.transferOutMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Discharge to CCC 12 Months KP
     * @return the indicator
     */
    public CohortIndicator dischargedToCCCMaternalKp12MonthsCohort() {
        return cohortIndicator("Discharged to CCC Maternal Kp 12 Months Cohort", ReportUtils.map(cohortLibrary.dischargedToCCCMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Discharged to CCC 12 Months NP
     * @return the indicator
     */
    public CohortIndicator dischargedToCCCMaternalNp12MonthsCohort() {
        return cohortIndicator("Discharged to CCC Maternal Np 12 Months Cohort", ReportUtils.map(cohortLibrary.dischargedToCCCMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Net Cohort  12 Months KP
     * @return the indicator
     */
    public CohortIndicator netCohortMaternalKp12MonthsCohort() {
        return cohortIndicator("Net Cohort Maternal Kp 12 Months Cohort", ReportUtils.map(cohortLibrary.netCohortMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Net Cohort 12 Months NP
     * @return the indicator
     */
    public CohortIndicator netCohortMaternalNp12MonthsCohort() {
        return cohortIndicator("Net Cohort Maternal Np 12 Months Cohort", ReportUtils.map(cohortLibrary.netCohortMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort LTFU  12 Months KP
     * @return the indicator
     */
    public CohortIndicator ltfuMaternalKp12MonthsCohort() {
        return cohortIndicator("Interruption In treatment (LTFU) Maternal Kp 12 Months Cohort", ReportUtils.map(cohortLibrary.ltfuMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort LTFU 12 Months NP
     * @return the indicator
     */
    public CohortIndicator ltfuMaternalNp12MonthsCohort() {
        return cohortIndicator("Interruption In treatment (LTFU) Maternal Np 12 Months Cohort", ReportUtils.map(cohortLibrary.ltfuMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort reported deceased  12 Months KP
     * @return the indicator
     */
    public CohortIndicator deceasedMaternalKp12MonthsCohort() {
        return cohortIndicator("Reported dead Maternal Kp 12 Months Cohort", ReportUtils.map(cohortLibrary.deceasedMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort deceased 12 Months NP
     * @return the indicator
     */
    public CohortIndicator deceasedMaternalNp12MonthsCohort() {
        return cohortIndicator("Reported dead Maternal Np 12 Months Cohort", ReportUtils.map(cohortLibrary.deceasedMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Stopped treatment  12 Months KP
     * @return the indicator
     */
    public CohortIndicator stoppedTreatmentMaternalKp12MonthsCohort() {
        return cohortIndicator("Stopped treatment Maternal Kp 12 Months Cohort", ReportUtils.map(cohortLibrary.stoppedTreatmentMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Stopped treatment 12 Months NP
     * @return the indicator
     */
    public CohortIndicator stoppedTreatmentMaternalNp12MonthsCohort() {
        return cohortIndicator("Stopped treatment Maternal Np 12 Months Cohort", ReportUtils.map(cohortLibrary.stoppedTreatmentMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Alive and Active treatment  12 Months KP
     * @return the indicator
     */
    public CohortIndicator aliveAndActiveOnTreatmentMaternalKp12MonthsCohort() {
        return cohortIndicator("Alive and Active on treatment Maternal Kp 12 Months Cohort", ReportUtils.map(cohortLibrary.aliveAndActiveOnTreatmentMaternalKp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Alive and Active treatment 12 Months NP
     * @return the indicator
     */
    public CohortIndicator aliveAndActiveOnTreatmentMaternalNp12MonthsCohort() {
        return cohortIndicator("Alive and Active on treatment Maternal Np 12 Months Cohort", ReportUtils.map(cohortLibrary.aliveAndActiveOnTreatmentMaternalNp12MonthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort Vl samples taken 12 Months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithViralLoadSamplesCollectedKp12Months() {
        return cohortIndicator("Vl samples collected Maternal Kp 12 Months Cohort", ReportUtils.map(cohortLibrary.maternalCohortWithViralLoadSamplesCollectedKp12Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Vl samples taken 12 Months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithViralLoadSamplesCollectedNp12Months() {
        return cohortIndicator("Vl samples collected Maternal Np 12 Months Cohort", ReportUtils.map(cohortLibrary.maternalCohortWithViralLoadSamplesCollectedNp12Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort Vl results received 12 Months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithViralLoadResultsReceivedKp12Months() {
        return cohortIndicator("Vl results received Maternal Kp 12 Months Cohort", ReportUtils.map(cohortLibrary.maternalCohortWithViralLoadResultsReceivedKp12Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Vl results received 12 Months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithViralLoadResultsReceivedNp12Months() {
        return cohortIndicator("Vl results received Maternal Np 12 Months Cohort", ReportUtils.map(cohortLibrary.maternalCohortWithViralLoadResultsReceivedNp12Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort VL results < 1000 12 Months KP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan1000Kp12Months() {
        return cohortIndicator("Viral Load results  <1000 copies/ml Kp (12 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan1000Kp12Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results < 1000 12 Months NP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan1000Np12Months() {
        return cohortIndicator("Viral Load results  <1000 copies/ml Np (12 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan1000Np12Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort VL results < 400 12 Months KP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan400Kp12Months() {
        return cohortIndicator("Viral Load results  <400 copies/ml Kp (12 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan400Kp12Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results < 400 12 Months NP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan400Np12Months() {
        return cohortIndicator("Viral Load results  <400 copies/ml Np (12 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan400Np12Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort VL results < 50 12 Months KP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan50Kp12Months() {
        return cohortIndicator("Viral Load results  <50 copies/ml Kp (12 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan50Kp12Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results < 50 12 Months NP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan50Np12Months() {
        return cohortIndicator("Viral Load results  <50 copies/ml Np (12 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan50Np12Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort VL results >= 1000 12 Months KP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsMoreThan1000Kp12Months() {
        return cohortIndicator("Viral Load results  >=1000 copies/ml Kp (12 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000Kp12Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results >= 1000 12 Months NP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsMoreThan1000Np12Months() {
        return cohortIndicator("Viral Load results  >=1000 copies/ml Np (12 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000Np12Months(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number in Maternal Cohort VL results >= 400 12 Months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan400WithEACsKp12Months() {
        return cohortIndicator("Viral Load results  >=400 copies/ml with EACs Kp (12 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan400WithEACsKp12Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results >= 400 12 Months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan400WithEACsNp12Months() {
        return cohortIndicator("Viral Load results  >=400 copies/ml with EACs Np (12 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan400WithEACsNp12Months(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number in Maternal Cohort VL results >=1000 with STF 12 Months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan1000WithSTFKp12Months() {
        return cohortIndicator("Viral Load results >=1000 copies/ml with STF Kp (12 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFKp12Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results >=1000 with STF 12 Months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan1000WithSTFNp12Months() {
        return cohortIndicator("Viral Load results >=1000 copies/ml with STF Np (12 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFNp12Months(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number in Maternal Cohort VL results >=1000 with STF and Repeat Vl 12 Months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlKp12Months() {
        return cohortIndicator("Viral Load results >=1000 copies/ml with STF and Repeat VL Kp (12 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlKp12Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results >=1000 with STF and Repeat Vl 12 Months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlNp12Months() {
        return cohortIndicator("Viral Load results >=1000 copies/ml with STF and Repeat VL Np (12 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlNp12Months(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number switched regimen line after confirmed treatment failure 12 Months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortSwitchedRegimenLineAfterConfirmedSTFKp12Months() {
        return cohortIndicator("Number Switched Regimen Line after Confirmed Treatment Failure Kp (12 Months)", ReportUtils.map(cohortLibrary.maternalCohortSwitchedRegimenLineAfterConfirmedSTFKp12Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number switched regimen line after confirmed treatment failure 12 Months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortSwitchedRegimenLineAfterConfirmedSTFNp12Months() {
        return cohortIndicator("Number Switched Regimen Line after Confirmed Treatment Failure Np (12 Months)", ReportUtils.map(cohortLibrary.maternalCohortSwitchedRegimenLineAfterConfirmedSTFNp12Months(), "startDate=${startDate},endDate=${endDate}"));
    }



    /**
     * SECOND REVIEW
     */
    /**
     * Number in Maternal Cohort 24 months KP
     * @return the indicator
     */
    public CohortIndicator originalCohortKp24Months() {
        return cohortIndicator("Original cohort-KP (24 months)", ReportUtils.map(cohortLibrary.originalMaternalKpCohort24months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort 24 months NP
     * @return the indicator
     */
    public CohortIndicator originalCohortNp24Months() {
        return cohortIndicator("Original cohort-NP (24 months)", ReportUtils.map(cohortLibrary.originalMaternalNpCohort24months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort TI 24 Months KP
     * @return the indicator
     */
    public CohortIndicator transferInMaternalKp24MonthsCohort() {
        return cohortIndicator("Transfer In Maternal Kp 24 Months Cohort", ReportUtils.map(cohortLibrary.transferInMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort TI 24 Months NP
     * @return the indicator
     */
    public CohortIndicator transferInMaternalNp24MonthsCohort() {
        return cohortIndicator("Transfer In Maternal Np 24 Months Cohort", ReportUtils.map(cohortLibrary.transferInMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort TO 24 Months KP
     * @return the indicator
     */
    public CohortIndicator transferOutMaternalKp24MonthsCohort() {
        return cohortIndicator("Transfer Out Maternal Kp 24 Months Cohort", ReportUtils.map(cohortLibrary.transferOutMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort TO 24 Months NP
     * @return the indicator
     */
    public CohortIndicator transferOutMaternalNp24MonthsCohort() {
        return cohortIndicator("Transfer Out Maternal Np 24 Months Cohort", ReportUtils.map(cohortLibrary.transferOutMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Discharge to CCC 24 Months KP
     * @return the indicator
     */
    public CohortIndicator dischargedToCCCMaternalKp24MonthsCohort() {
        return cohortIndicator("Discharged to CCC Maternal Kp 24 Months Cohort", ReportUtils.map(cohortLibrary.dischargedToCCCMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Discharged to CCC 24 Months NP
     * @return the indicator
     */
    public CohortIndicator dischargedToCCCMaternalNp24MonthsCohort() {
        return cohortIndicator("Discharged to CCC Maternal Np 24 Months Cohort", ReportUtils.map(cohortLibrary.dischargedToCCCMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Net Cohort  24 Months KP
     * @return the indicator
     */
    public CohortIndicator netCohortMaternalKp24MonthsCohort() {
        return cohortIndicator("Net Cohort Maternal Kp 24 Months Cohort", ReportUtils.map(cohortLibrary.netCohortMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Net Cohort 24 Months NP
     * @return the indicator
     */
    public CohortIndicator netCohortMaternalNp24MonthsCohort() {
        return cohortIndicator("Net Cohort Maternal Np 24 Months Cohort", ReportUtils.map(cohortLibrary.netCohortMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort LTFU  24 Months KP
     * @return the indicator
     */
    public CohortIndicator ltfuMaternalKp24MonthsCohort() {
        return cohortIndicator("Interruption In treatment (LTFU) Maternal Kp 24 Months Cohort", ReportUtils.map(cohortLibrary.ltfuMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort LTFU 24 Months NP
     * @return the indicator
     */
    public CohortIndicator ltfuMaternalNp24MonthsCohort() {
        return cohortIndicator("Interruption In treatment (LTFU) Maternal Np 24 Months Cohort", ReportUtils.map(cohortLibrary.ltfuMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort reported deceased  24 Months KP
     * @return the indicator
     */
    public CohortIndicator deceasedMaternalKp24MonthsCohort() {
        return cohortIndicator("Reported dead Maternal Kp 24 Months Cohort", ReportUtils.map(cohortLibrary.deceasedMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort deceased 24 Months NP
     * @return the indicator
     */
    public CohortIndicator deceasedMaternalNp24MonthsCohort() {
        return cohortIndicator("Reported dead Maternal Np 24 Months Cohort", ReportUtils.map(cohortLibrary.deceasedMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Stopped treatment  24 Months KP
     * @return the indicator
     */
    public CohortIndicator stoppedTreatmentMaternalKp24MonthsCohort() {
        return cohortIndicator("Stopped treatment Maternal Kp 24 Months Cohort", ReportUtils.map(cohortLibrary.stoppedTreatmentMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Stopped treatment 24 Months NP
     * @return the indicator
     */
    public CohortIndicator stoppedTreatmentMaternalNp24MonthsCohort() {
        return cohortIndicator("Stopped treatment Maternal Np 24 Months Cohort", ReportUtils.map(cohortLibrary.stoppedTreatmentMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Alive and Active treatment  24 Months KP
     * @return the indicator
     */
    public CohortIndicator aliveAndActiveOnTreatmentMaternalKp24MonthsCohort() {
        return cohortIndicator("Alive and Active on treatment Maternal Kp 24 Months Cohort", ReportUtils.map(cohortLibrary.aliveAndActiveOnTreatmentMaternalKp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Alive and Active treatment 24 Months NP
     * @return the indicator
     */
    public CohortIndicator aliveAndActiveOnTreatmentMaternalNp24MonthsCohort() {
        return cohortIndicator("Alive and Active on treatment Maternal Np 24 Months Cohort", ReportUtils.map(cohortLibrary.aliveAndActiveOnTreatmentMaternalNp24monthsCohort(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort Vl samples taken 24 Months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithViralLoadSamplesCollectedKp24Months() {
        return cohortIndicator("Vl samples collected Maternal Kp 24 Months Cohort", ReportUtils.map(cohortLibrary.maternalCohortWithViralLoadSamplesCollectedKp24Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Vl samples taken 24 Months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithViralLoadSamplesCollectedNp24Months() {
        return cohortIndicator("Vl samples collected Maternal Np 24 Months Cohort", ReportUtils.map(cohortLibrary.maternalCohortWithViralLoadSamplesCollectedNp24Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort Vl results received 24 Months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithViralLoadResultsReceivedKp24Months() {
        return cohortIndicator("Vl results received Maternal Kp 24 Months Cohort", ReportUtils.map(cohortLibrary.maternalCohortWithViralLoadResultsReceivedKp24Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort Vl results received 24 Months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithViralLoadResultsReceivedNp24Months() {
        return cohortIndicator("Vl results received Maternal Np 24 Months Cohort", ReportUtils.map(cohortLibrary.maternalCohortWithViralLoadResultsReceivedNp24Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort VL results < 1000 24 Months KP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan1000Kp24Months() {
        return cohortIndicator("Viral Load results  <1000 copies/ml Kp (24 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan1000Kp24Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results < 1000 24 Months NP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan1000Np24Months() {
        return cohortIndicator("Viral Load results  <1000 copies/ml Np (24 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan1000Np24Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort VL results < 400 24 Months KP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan400Kp24Months() {
        return cohortIndicator("Viral Load results  <400 copies/ml Kp (24 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan400Kp24Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results < 400 24 Months NP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan400Np24Months() {
        return cohortIndicator("Viral Load results  <400 copies/ml Np (24 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan400Np24Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort VL results < 50 24 Months KP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan50Kp24Months() {
        return cohortIndicator("Viral Load results  <50 copies/ml Kp (24 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan50Kp24Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results < 50 24 Months NP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsLessThan50Np24Months() {
        return cohortIndicator("Viral Load results  <50 copies/ml Np (24 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsLessThan50Np24Months(), "startDate=${startDate},endDate=${endDate}"));
    }

    /**
     * Number in Maternal Cohort VL results >= 1000 24 Months KP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsMoreThan1000Kp24Months() {
        return cohortIndicator("Viral Load results  >=1000 copies/ml Kp (24 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000Kp24Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results >= 1000 24 Months NP
     * @return the indicator
     */
    public CohortIndicator viralLoadResultsMoreThan1000Np24Months() {
        return cohortIndicator("Viral Load results  >=1000 copies/ml Np (24 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000Np24Months(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number in Maternal Cohort VL results >= 400 24 Months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan400WithEACsKp24Months() {
        return cohortIndicator("Viral Load results  >=400 copies/ml with EACs Kp (24 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan400WithEACsKp24Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results >= 400 24 Months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan400WithEACsNp24Months() {
        return cohortIndicator("Viral Load results  >=400 copies/ml with EACs Np (24 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan400WithEACsNp24Months(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number in Maternal Cohort VL results >=1000 with STF 24 Months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan1000WithSTFKp24Months() {
        return cohortIndicator("Viral Load results >=1000 copies/ml with STF Kp (24 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFKp24Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results >=1000 with STF 24 Months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan1000WithSTFNp24Months() {
        return cohortIndicator("Viral Load results >=1000 copies/ml with STF Np (24 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFNp24Months(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number in Maternal Cohort VL results >=1000 with STF and Repeat Vl 24 Months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlKp24Months() {
        return cohortIndicator("Viral Load results >=1000 copies/ml with STF and Repeat VL Kp (24 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlKp24Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number in Maternal Cohort VL results >=1000 with STF and Repeat Vl 24 Months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlNp24Months() {
        return cohortIndicator("Viral Load results >=1000 copies/ml with STF and Repeat VL Np (24 Months)", ReportUtils.map(cohortLibrary.maternalCohortWithvlResultsMoreThan1000WithSTFAndRepeatVlNp24Months(), "startDate=${startDate},endDate=${endDate}"));
    }


    /**
     * Number switched regimen line after confirmed treatment failure 24 Months KP
     * @return the indicator
     */
    public CohortIndicator maternalCohortSwitchedRegimenLineAfterConfirmedSTFKp24Months() {
        return cohortIndicator("Number Switched Regimen Line after Confirmed Treatment Failure Kp (24 Months)", ReportUtils.map(cohortLibrary.maternalCohortSwitchedRegimenLineAfterConfirmedSTFKp24Months(), "startDate=${startDate},endDate=${endDate}"));
    }
    /**
     * Number switched regimen line after confirmed treatment failure 24 Months NP
     * @return the indicator
     */
    public CohortIndicator maternalCohortSwitchedRegimenLineAfterConfirmedSTFNp24Months() {
        return cohortIndicator("Number Switched Regimen Line after Confirmed Treatment Failure Np (24 Months)", ReportUtils.map(cohortLibrary.maternalCohortSwitchedRegimenLineAfterConfirmedSTFNp24Months(), "startDate=${startDate},endDate=${endDate}"));
    }
}
