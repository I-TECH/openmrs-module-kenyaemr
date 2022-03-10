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

@Component
public class Moh745IndicatorLibrary {

    @Autowired
    private Moh745CohortLibrary Moh745CohortLibrary;

    /*Received VIA or VIA/ VILI Screening*/
    public CohortIndicator receivedVIAScreening() {

        return cohortIndicator("Received VIA or VIA/ VILI Screening",map(Moh745CohortLibrary.receivedVIAScreeningCl(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*Received Pap smear Screening*/
    public CohortIndicator receivedPapSmearScreening() {

        return cohortIndicator("Received Pap Smear Screening",map(Moh745CohortLibrary.receivedPapSmearScreeningCl(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*Received HPV Test*/
    public CohortIndicator receivedHPVTest() {

        return cohortIndicator("Positive VIA or VIA/VILI Result", map(Moh745CohortLibrary.receivedHPVTestCl(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*Positive VIA or VIA/VILI result*/
    public CohortIndicator positiveVIAresult() {

        return cohortIndicator("Positive VIA or VIA/VILI Result",map(Moh745CohortLibrary.positiveVIAresultCl(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*Positive Cytology result*/
    public CohortIndicator positiveCytologyResult() {

        return cohortIndicator("Positive Cytology result",map(Moh745CohortLibrary.positiveCytologyResultCl(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*Positive HPV*/
    public CohortIndicator positiveHPV() {

        return cohortIndicator("Positive HPV Result",map(Moh745CohortLibrary.positiveHPVResultCl(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*Suspicious cancer lesions*/
    public CohortIndicator suspiciousCancerLesions() {

        return cohortIndicator("Have Suspicious Cancer Lesions",map(Moh745CohortLibrary.suspiciousCancerLesionsCl(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*Treated Using Cryotherapy*/
    public CohortIndicator treatedUsingCryotherapy() {

        return cohortIndicator("Treated Using Cryotherapy",map(Moh745CohortLibrary.treatedUsingCryotherapyCl(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*Treated Using Thermocoagulation*/
    public CohortIndicator treatedUsingThermocoagulation() {

        return cohortIndicator("Treated Using Thermocoagulation",map(Moh745CohortLibrary.treatedUsingThermocoagulationCl(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*Treated using LEEP*/
    public CohortIndicator treatedUsingLEEP() {

        return cohortIndicator("Treated using LEEP",map(Moh745CohortLibrary.treatedUsingLEEPCl(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*Other Treatment Given*/
    public CohortIndicator otherTreatmentGiven() {

        return cohortIndicator("Other Treatment Given",map(Moh745CohortLibrary.otherTreatmentGivenCl(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*HIV Positive Clients Screened*/
    public CohortIndicator HIVPositiveClientsScreened() {

        return cohortIndicator("HIV Positive Clients Screened",map(Moh745CohortLibrary.HIVPositiveClientsScreenedCl(), "startDate=${startDate},endDate=${endDate}")
        );
    }

    /*HIV Positive With Positive Screening Results*/
    public CohortIndicator HIVPositiveClientsScreenedWithResults() {

        return cohortIndicator("HIV Positive With Positive Screening Results",map(Moh745CohortLibrary.HIVPositiveClientsScreenedWithResultsCl(), "startDate=${startDate},endDate=${endDate}")
        );
    }

}


