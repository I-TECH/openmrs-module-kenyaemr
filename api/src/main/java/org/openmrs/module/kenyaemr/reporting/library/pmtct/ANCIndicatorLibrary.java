/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.pmtct;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of ANC related indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
@Component
public class ANCIndicatorLibrary {

    @Autowired
    private ANCCohortLibrary ancCohorts;

	public CohortIndicator newClientsANC() {
		return cohortIndicator("New Clients", ReportUtils.<CohortDefinition>map(ancCohorts.newClientsANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
	}

    public CohortIndicator revisitsANC() {
        return cohortIndicator("Revisit Clients", ReportUtils.<CohortDefinition>map(ancCohorts.revisitClientsANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator completed4AntenatalVisits() {
        return cohortIndicator("Completed 4th Antenatal visit", ReportUtils.<CohortDefinition>map(ancCohorts.completed4AntenatalVisitsANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator testedSyphilisANC() {
        return cohortIndicator("Tested for syphilis", ReportUtils.<CohortDefinition>map(ancCohorts.testedSyphilisANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator positiveSyphilisANC() {
        return cohortIndicator("Syphilis positive", ReportUtils.<CohortDefinition>map(ancCohorts.syphilisPositiveANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator treatedSyphilisANC() {
        return cohortIndicator("Syphilis treated", ReportUtils.<CohortDefinition>map(ancCohorts.syphilisTreatedANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator knownPositivesFirstANC() {
        return cohortIndicator("Known Positives First ANC", ReportUtils.<CohortDefinition>map(ancCohorts.knownPositivesFirstANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator initialTestANC() {
        return cohortIndicator("Initial Test at ANC", ReportUtils.<CohortDefinition>map(ancCohorts.initialTestANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator positiveTestANC() {
        return cohortIndicator("Positive Test at ANC", ReportUtils.<CohortDefinition>map(ancCohorts.positiveTestANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator onARVatFirstANC() {
        return cohortIndicator("On ARV at First ANC", ReportUtils.<CohortDefinition>map(ancCohorts.onARVFirstANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator startedHAARTInANC() {
        return cohortIndicator("Started HAART in ANC", ReportUtils.<CohortDefinition>map(ancCohorts.startedHAARTInANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator aztBabyGivenAtANC() {
        return cohortIndicator("AZT given to baby at ANC", ReportUtils.<CohortDefinition>map(ancCohorts.aztBabyANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator nvpBabyGivenAtANC() {
        return cohortIndicator("NVP given to baby at ANC", ReportUtils.<CohortDefinition>map(ancCohorts.nvpBabyANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator screenedForTBAtANC() {
        return cohortIndicator("Screened for TB at ANC", ReportUtils.<CohortDefinition>map(ancCohorts.screenedTbANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator screenedForCaCxPAPAtANC() {
        return cohortIndicator("Screened for CaCx PAP at ANC", ReportUtils.<CohortDefinition>map(ancCohorts.screenedCaCxPapANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator screenedForCaCxVIAAtANC() {
        return cohortIndicator("Screened for CaCx VIA at ANC", ReportUtils.<CohortDefinition>map(ancCohorts.screenedCaCxViaANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator screenedForCaCxViliAtANC() {
        return cohortIndicator("Screened for CaCx Vili at ANC", ReportUtils.<CohortDefinition>map(ancCohorts.screenedCaCxViliANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator givenIPT1AtANC() {
        return cohortIndicator("Given IPT1 at ANC", ReportUtils.<CohortDefinition>map(ancCohorts.givenIPT1ANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator givenIPT2AtANC() {
        return cohortIndicator("Given IPT2 at ANC", ReportUtils.<CohortDefinition>map(ancCohorts.givenIPT2ANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator givenITNAtANC() {
        return cohortIndicator("Given ITN at ANC", ReportUtils.<CohortDefinition>map(ancCohorts.givenITNANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator partnerTestedAtANC() {
        return cohortIndicator("Partner Tested at ANC", ReportUtils.<CohortDefinition>map(ancCohorts.partnerTestedANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator partnerKnownPositiveAtANC() {
        return cohortIndicator("Partner Known Positive at ANC", ReportUtils.<CohortDefinition>map(ancCohorts.partnerKnownPositiveANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator adolescentsKnownPositive_10_19_AtANC() {
        return cohortIndicator("Adolescents Known Positive 10 - 19 at ANC", ReportUtils.<CohortDefinition>map(ancCohorts.adolescentsKnownPositive_10_19_AtANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator adolescentsTestedPositive_10_19_AtANC() {
        return cohortIndicator("Adolescents Tested Positive 10 - 19 at ANC", ReportUtils.<CohortDefinition>map(ancCohorts.adolescentsTestedPositive_10_19_AtANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator adolescentsStartedHaart_10_19_AtANC() {
        return cohortIndicator("Adolescents Started 10 - 19 at ANC", ReportUtils.<CohortDefinition>map(ancCohorts.adolescentsStartedHaart_10_19_AtANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

}