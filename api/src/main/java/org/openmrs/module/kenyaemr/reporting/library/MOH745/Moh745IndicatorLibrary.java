/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.MOH745;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.openmrs.module.kenyacore.report.ReportUtils;

@Component
public class Moh745IndicatorLibrary {

    @Autowired
    private Moh745CohortLibrary Moh745CohortLibrary;

    /*Received VIA Screening */
    public CohortIndicator receivedScreeningVIA(String[] indicatorVal, String visitType) {
        return cohortIndicator("Received VIA Screening", ReportUtils.map(Moh745CohortLibrary.patientScreenedByVIA(indicatorVal , visitType), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*Received PAP Smear Screening */
    public CohortIndicator receivedScreeningPap(String[] indicatorVal, String visitType) {
        return cohortIndicator("Received Pap Smear Screening", ReportUtils.map(Moh745CohortLibrary.patientScreenedByPapSmear(indicatorVal , visitType), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*Received Hpv Screening */
    public CohortIndicator receivedScreeningHpv(String[] indicatorVal, String visitType) {
        return cohortIndicator("Received Hpv Screening", ReportUtils.map(Moh745CohortLibrary.patientScreenedByHpv(indicatorVal , visitType), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*Received Positive VIA Screening Result*/
    public CohortIndicator receivedPositiveScreeningVIA(String[] indicatorVal, String visitType) {

        return cohortIndicator("Received Positive VIA Screening", ReportUtils.map(Moh745CohortLibrary.patientPositiveScreenedVia(indicatorVal, visitType), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*Received Positive Colposcopy Screening Result*/
    public CohortIndicator receivedPositiveScreeningColposcopy(String[] indicatorVal, String visitType) {

        return cohortIndicator("Received Positive Colposcopy Screening", ReportUtils.map(Moh745CohortLibrary.patientPositiveScreenedColposcopy(indicatorVal, visitType), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*Received Positive Hpv Screening Result*/
    public CohortIndicator receivedPositiveScreeningHpv(String[] indicatorVal, String visitType) {

        return cohortIndicator("Received Positive Hpv Screening", ReportUtils.map(Moh745CohortLibrary.patientPositiveScreenedHpv(indicatorVal, visitType), "startDate=${startDate},endDate=${endDate}")
        );
    }

      /*Suspicious Screening Result*/
    public CohortIndicator receivedSuspiciousScreening(String visitType) {

        return cohortIndicator("Received Suspicious Screening", ReportUtils.map(Moh745CohortLibrary.suspiciousScreeningCl(visitType), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*Treatment Method */
    public CohortIndicator treatedMethod(String[] treatmentMethod, String visitType) {

        return cohortIndicator("Treatment Method", ReportUtils.map(Moh745CohortLibrary.treatmentMethodCl(treatmentMethod, visitType), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*HIV Positive Clients Screened*/
    public CohortIndicator HIVPositiveClientsScreened(String visitType) {

        return cohortIndicator("HIV Positive Clients Screened",map(Moh745CohortLibrary.HIVPositiveClientsScreenedCl(visitType), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*HIV Positive With Positive Screening Results*/
    public CohortIndicator HIVPositiveClientsScreenedWithPositiveResults(String visitType) {

        return cohortIndicator("HIV Positive With Positive Screening Results",map(Moh745CohortLibrary.HIVPositiveClientsScreenedWithPositiveResultsCl(visitType), "startDate=${startDate},endDate=${endDate}")
        );
    }

}


