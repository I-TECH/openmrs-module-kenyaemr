/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyaemr.reporting.library.pmtct;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.*;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.pmtct.anc.*;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of ANC related indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
@Component
public class ANCIndicatorLibrary {


	public CohortIndicator newClientsANC() {
		return cohortIndicator("New Clients", ReportUtils.<CohortDefinition>map(new NewClientsANCCohortDefinition(), ""));
	}

    public CohortIndicator revisitsANC() {
        return cohortIndicator("Revisit Clients", ReportUtils.<CohortDefinition>map(new RevisitClientsANCCohortDefinition(), ""));
    }

    public CohortIndicator completed4AntenatalVisits() {
        return cohortIndicator("Completed 4th Antenatal visit", ReportUtils.<CohortDefinition>map(new Completed4AntenatalVisitsANCCohortDefinition(), ""));
    }

    public CohortIndicator testedSyphilisANC() {
        return cohortIndicator("Tested for syphilis", ReportUtils.<CohortDefinition>map(new TestedSyphilisANCCohortDefinition(), ""));
    }

    public CohortIndicator positiveSyphilisANC() {
        return cohortIndicator("Syphilis positive", ReportUtils.<CohortDefinition>map(new SyphilisPositiveANCCohortDefinition(), ""));
    }

    public CohortIndicator treatedSyphilisANC() {
        return cohortIndicator("Syphilis treated", ReportUtils.<CohortDefinition>map(new SyphilisTreatedANCCohortDefinition(), ""));
    }

    public CohortIndicator knownPositivesFirstANC() {
        return cohortIndicator("Known Positives First ANC", ReportUtils.<CohortDefinition>map(new KnownPositivesFirstANCCohortDefinition(), ""));
    }

    public CohortIndicator initialTestANC() {
        return cohortIndicator("Initial Test at ANC", ReportUtils.<CohortDefinition>map(new InitialTestANCCohortDefinition(), ""));
    }

    public CohortIndicator positiveTestANC() {
        return cohortIndicator("Positive Test at ANC", ReportUtils.<CohortDefinition>map(new PositiveTestANCCohortDefinition(), ""));
    }

    public CohortIndicator onARVatFirstANC() {
        return cohortIndicator("On ARV at First ANC", ReportUtils.<CohortDefinition>map(new OnARVFirstANCCohortDefinition(), ""));
    }

    public CohortIndicator startedHAARTInANC() {
        return cohortIndicator("Started HAART in ANC", ReportUtils.<CohortDefinition>map(new StartedHAARTInANCCohortDefinition(), ""));
    }

    public CohortIndicator aztBabyGivenAtANC() {
        return cohortIndicator("AZT given to baby at ANC", ReportUtils.<CohortDefinition>map(new AztBabyANCCohortDefinition(), ""));
    }

    public CohortIndicator nvpBabyGivenAtANC() {
        return cohortIndicator("NVP given to baby at ANC", ReportUtils.<CohortDefinition>map(new NvpBabyANCCohortDefinition(), ""));
    }

    public CohortIndicator screenedForTBAtANC() {
        return cohortIndicator("Screened for TB at ANC", ReportUtils.<CohortDefinition>map(new ScreenedTbANCCohortDefinition(), ""));
    }

    public CohortIndicator screenedForCaCxPAPAtANC() {
        return cohortIndicator("Screened for CaCx PAP at ANC", ReportUtils.<CohortDefinition>map(new ScreenedCaCxPapANCCohortDefinition(), ""));
    }

    public CohortIndicator screenedForCaCxVIAAtANC() {
        return cohortIndicator("Screened for CaCx VIA at ANC", ReportUtils.<CohortDefinition>map(new ScreenedCaCxViaANCCohortDefinition(), ""));
    }
    public CohortIndicator screenedForCaCxViliAtANC() {
        return cohortIndicator("Screened for CaCx Vili at ANC", ReportUtils.<CohortDefinition>map(new ScreenedCaCxViliANCCohortDefinition(), ""));
    }

    public CohortIndicator givenIPT1AtANC() {
        return cohortIndicator("Given IPT1 at ANC", ReportUtils.<CohortDefinition>map(new GivenIPT1ANCCohortDefinition(), ""));
    }

    public CohortIndicator givenIPT2AtANC() {
        return cohortIndicator("Given IPT2 at ANC", ReportUtils.<CohortDefinition>map(new GivenIPT2ANCCohortDefinition(), ""));
    }

    public CohortIndicator givenITNAtANC() {
        return cohortIndicator("Given ITN at ANC", ReportUtils.<CohortDefinition>map(new GivenITNANCCohortDefinition(), ""));
    }

    public CohortIndicator partnerTestedAtANC() {
        return cohortIndicator("Partner Tested at ANC", ReportUtils.<CohortDefinition>map(new PartnerTestedANCCohortDefinition(), ""));
    }

    public CohortIndicator partnerKnownPositiveAtANC() {
        return cohortIndicator("Partner Known Positive at ANC", ReportUtils.<CohortDefinition>map(new PartnerKnownPositiveANCCohortDefinition(), ""));
    }

    public CohortIndicator adolescentsKnownPositive_10_19_AtANC() {
        return cohortIndicator("Adolescents Known Positive 10 - 19 at ANC", ReportUtils.<CohortDefinition>map(new AdolescentsKnownPositive_10_19_AtANCCohortDefinition(), ""));
    }

    public CohortIndicator adolescentsTestedPositive_10_19_AtANC() {
        return cohortIndicator("Adolescents Tested Positive 10 - 19 at ANC", ReportUtils.<CohortDefinition>map(new AdolescentsTestedPositive_10_19_AtANCCohortDefinition(), ""));
    }

    public CohortIndicator adolescentsStartedHaart_10_19_AtANC() {
        return cohortIndicator("Adolescents Started 10 - 19 at ANC", ReportUtils.<CohortDefinition>map(new AdolescentsStartedHaart_10_19_AtANCCohortDefinition(), ""));
    }

}