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
public class PNCIndicatorLibrary {
    @Autowired
    private PNCCohortLibrary pncCohorts;

    public CohortIndicator pncClients() {
        return cohortIndicator("PNC Clients", ReportUtils.<CohortDefinition>map(pncCohorts.pncClients(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator newClientsPNC() {
        return cohortIndicator("New Clients", ReportUtils.<CohortDefinition>map(pncCohorts.newPNCClients(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator revisitsPNC() {
        return cohortIndicator("Revisit Clients", ReportUtils.<CohortDefinition>map(pncCohorts.revisitsPNC(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator pncMotherNewVisitWithin48hrs() {
        return cohortIndicator("New PNC visit for mothers within 48 hrs", ReportUtils.<CohortDefinition>map(pncCohorts.pncMotherNewVisitWithin48hrs(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator pncMotherNewVisitBetween3DaysUnder6Weeks() {
        return cohortIndicator("New PNC visit for mothers between 3 days and under 6 weeks", ReportUtils.<CohortDefinition>map(pncCohorts.pncMotherNewVisitBtwn3DaysUnder6Weeks(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator pncMotherNewVisitAfter6Weeks() {
        return cohortIndicator("New PNC visit for mothers after 6 weeks", ReportUtils.<CohortDefinition>map(pncCohorts.pncMotherNewVisitAfter6Weeks(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator pncBabyNewVisitWithin48hrs() {
        return cohortIndicator("New PNC visit for babies within 48 hrs", ReportUtils.<CohortDefinition>map(pncCohorts.pncBabyNewVisitWithin48hrs(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator pncBabyNewVisitBetween3DaysUnder6Weeks() {
        return cohortIndicator("New PNC visit for babies between 3 days and under 6 weeks", ReportUtils.<CohortDefinition>map(pncCohorts.pncBabyNewVisitBtwn3DaysUnder6Weeks(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator pncBabyNewVisitAfter6Weeks() {
        return cohortIndicator("New PNC visit for babies after 6 weeks", ReportUtils.<CohortDefinition>map(pncCohorts.pncBabyNewVisitAfter6Weeks(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator initialTestsAtPNC() {
        return cohortIndicator("Initial tests at PNC", ReportUtils.<CohortDefinition>map(pncCohorts.initialTestsAtPNC(),"startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator hivPositiveResultAtPNC() {
        return cohortIndicator("Partner tested at PNC", ReportUtils.<CohortDefinition>map(pncCohorts.hivPositiveResultAtPNC(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator partnerTestedAtPNC() {
        return cohortIndicator("Partner tested at PNC", ReportUtils.<CohortDefinition>map(pncCohorts.partnerTestedAtPNC(), "startDate=${startDate},endDate=${endDate}"));
    }
     public CohortIndicator startedHAARTPNC() {
        return cohortIndicator("Started HAART PNC", ReportUtils.<CohortDefinition>map(pncCohorts.startedHAARTAtPNC(),"startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator infantARVProphylaxis() {
        return cohortIndicator("AZT given to baby at ANC", ReportUtils.<CohortDefinition>map(pncCohorts.infantARVProphylaxis(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator cacxPAP() {
        return cohortIndicator("Screened for Cervical Cancer (PAP)", ReportUtils.<CohortDefinition>map(pncCohorts.cacxPAP(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator cacxVIA() {
        return cohortIndicator("Screened for Cervical Cancer (VIA)", ReportUtils.<CohortDefinition>map(pncCohorts.cacxVIA(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator cacxVILI() {
        return cohortIndicator("Screened for Cervical Cancer (VILI)", ReportUtils.<CohortDefinition>map(pncCohorts.cacxVILI(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator cacxHPV() {
        return cohortIndicator("Screened for Cervical Cancer (HPV)", ReportUtils.<CohortDefinition>map(pncCohorts.cacxHPV(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator receivedFPMethod() {
        return cohortIndicator("Received FP method", ReportUtils.<CohortDefinition>map(pncCohorts.receivedFPMethod(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator hivNegativeTest1() {
        return cohortIndicator("HIV Negative Test-1", ReportUtils.<CohortDefinition>map(pncCohorts.hivNegativeTest1(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator hivPositiveTest1() {
        return cohortIndicator("HIV Positive Test-1", ReportUtils.<CohortDefinition>map(pncCohorts.hivPositiveTest1(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator hivInvalidTest1() {
        return cohortIndicator("HIV Invalid Test-1", ReportUtils.<CohortDefinition>map(pncCohorts.hivInvalidTest1(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator hivWastedTest1() {
        return cohortIndicator("HIV wasted Test-1", ReportUtils.<CohortDefinition>map(pncCohorts.hivWastedTest1(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator hivNegativeTest2() {
        return cohortIndicator("HIV Negative Test-2", ReportUtils.<CohortDefinition>map(pncCohorts.hivNegativeTest2(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator hivPositiveTest2() {
        return cohortIndicator("HIV Positive Test-2", ReportUtils.<CohortDefinition>map(pncCohorts.hivPositiveTest2(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator hivInvalidTest2() {
        return cohortIndicator("HIV Invalid Test-2", ReportUtils.<CohortDefinition>map(pncCohorts.hivInvalidTest2(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator hivWastedTest2() {
        return cohortIndicator("HIV wasted Test-2", ReportUtils.<CohortDefinition>map(pncCohorts.hivWastedTest2(), "startDate=${startDate},endDate=${endDate}"));
    }
}