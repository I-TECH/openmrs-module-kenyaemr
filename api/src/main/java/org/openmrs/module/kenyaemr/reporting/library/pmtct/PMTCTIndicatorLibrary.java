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
public class PMTCTIndicatorLibrary {
    @Autowired
    private PMTCTCohortLibrary pmtctCohorts;

    // ANC INDICATORS
    public CohortIndicator newClientsANC() {
        return cohortIndicator("New Clients", ReportUtils.<CohortDefinition>map(pmtctCohorts.newClientsANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator revisitsANC() {
        return cohortIndicator("Revisit Clients", ReportUtils.<CohortDefinition>map(pmtctCohorts.revisitClientsANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator completed4AntenatalVisits() {
        return cohortIndicator("Completed 4th Antenatal visit", ReportUtils.<CohortDefinition>map(pmtctCohorts.completed4AntenatalVisitsANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator testedSyphilisANC() {
        return cohortIndicator("Tested for syphilis", ReportUtils.<CohortDefinition>map(pmtctCohorts.testedSyphilisANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator positiveSyphilisANC() {
        return cohortIndicator("Syphilis positive", ReportUtils.<CohortDefinition>map(pmtctCohorts.syphilisPositiveANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator treatedSyphilisANC() {
        return cohortIndicator("Syphilis treated", ReportUtils.<CohortDefinition>map(pmtctCohorts.syphilisTreatedANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator knownPositivesFirstANC() {
        return cohortIndicator("Known Positives First ANC", ReportUtils.<CohortDefinition>map(pmtctCohorts.knownPositivesFirstANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator initialTestANC() {
        return cohortIndicator("Initial Test at ANC", ReportUtils.<CohortDefinition>map(pmtctCohorts.initialTestANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator positiveTestANC() {
        return cohortIndicator("Positive Test at ANC", ReportUtils.<CohortDefinition>map(pmtctCohorts.positiveTestANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator onARVatFirstANC() {
        return cohortIndicator("On ARV at First ANC", ReportUtils.<CohortDefinition>map(pmtctCohorts.onARVFirstANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator startedHAARTInANC() {
        return cohortIndicator("Started HAART in ANC", ReportUtils.<CohortDefinition>map(pmtctCohorts.startedHAARTInANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator aztBabyGivenAtANC() {
        return cohortIndicator("AZT given to baby at ANC", ReportUtils.<CohortDefinition>map(pmtctCohorts.aztBabyANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator nvpBabyGivenAtANC() {
        return cohortIndicator("NVP given to baby at ANC", ReportUtils.<CohortDefinition>map(pmtctCohorts.nvpBabyANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator screenedForTBAtANC() {
        return cohortIndicator("Screened for TB at ANC", ReportUtils.<CohortDefinition>map(pmtctCohorts.screenedTbANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator screenedForCaCxPAPAtANC() {
        return cohortIndicator("Screened for CaCx PAP at ANC", ReportUtils.<CohortDefinition>map(pmtctCohorts.screenedCaCxPapANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator screenedForCaCxVIAAtANC() {
        return cohortIndicator("Screened for CaCx VIA at ANC", ReportUtils.<CohortDefinition>map(pmtctCohorts.screenedCaCxViaANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator screenedForCaCxViliAtANC() {
        return cohortIndicator("Screened for CaCx Vili at ANC", ReportUtils.<CohortDefinition>map(pmtctCohorts.screenedCaCxViliANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator givenIPT1AtANC() {
        return cohortIndicator("Given IPT1 at ANC", ReportUtils.<CohortDefinition>map(pmtctCohorts.givenIPT1ANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator givenIPT2AtANC() {
        return cohortIndicator("Given IPT2 at ANC", ReportUtils.<CohortDefinition>map(pmtctCohorts.givenIPT2ANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator givenITNAtANC() {
        return cohortIndicator("Given ITN at ANC", ReportUtils.<CohortDefinition>map(pmtctCohorts.givenITNANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator partnerTestedAtANC() {
        return cohortIndicator("Partner Tested at ANC", ReportUtils.<CohortDefinition>map(pmtctCohorts.partnerTestedANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator partnerKnownPositiveAtANC() {
        return cohortIndicator("Partner Known Positive at ANC", ReportUtils.<CohortDefinition>map(pmtctCohorts.partnerKnownPositiveANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator adolescentsKnownPositive_10_19_AtANC() {
        return cohortIndicator("Adolescents Known Positive 10 - 19 at ANC", ReportUtils.<CohortDefinition>map(pmtctCohorts.adolescentsKnownPositive_10_19_AtANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator adolescentsTestedPositive_10_19_AtANC() {
        return cohortIndicator("Adolescents Tested Positive 10 - 19 at ANC", ReportUtils.<CohortDefinition>map(pmtctCohorts.adolescentsTestedPositive_10_19_AtANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator adolescentsStartedHaart_10_19_AtANC() {
        return cohortIndicator("Adolescents Started 10 - 19 at ANC", ReportUtils.<CohortDefinition>map(pmtctCohorts.adolescentsStartedHaart_10_19_AtANCCohortDefinition(), "startDate=${startDate},endDate=${endDate}"));
    }

//MATERNITY INDICATORS
    public CohortIndicator clientsWithAPH() {
        return cohortIndicator("Clients with APH", ReportUtils.<CohortDefinition>map(pmtctCohorts.clientsWithAPH(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator clientsWithPPH() {
        return cohortIndicator("Clients with PPH", ReportUtils.<CohortDefinition>map(pmtctCohorts.clientsWithPPH(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator clientsWithEclampsia() {
        return cohortIndicator("Clients with Eclampsia", ReportUtils.<CohortDefinition>map(pmtctCohorts.clientsWithEclampsia(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator clientsWithRapturedUterus() {
        return cohortIndicator("Clients with raptured uterus", ReportUtils.<CohortDefinition>map(pmtctCohorts.clientsWithRapturedUterus(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator clientsWithObstructedLabour() {
        return cohortIndicator("Clients with obstructed Labour", ReportUtils.<CohortDefinition>map(pmtctCohorts.clientsWithObstructedLabour(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator clientsWithSepsis() {
        return cohortIndicator("Clients with Sepsis", ReportUtils.<CohortDefinition>map(pmtctCohorts.clientsWithSepsis(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator clientsAlive() {
        return cohortIndicator("Clients Alive", ReportUtils.<CohortDefinition>map(pmtctCohorts.clientsAlive(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator clientsDead() {
        return cohortIndicator("Clients Dead", ReportUtils.<CohortDefinition>map(pmtctCohorts.clientsDead(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator preTermBabies() {
        return cohortIndicator("Pre-Term Babies", ReportUtils.<CohortDefinition>map(pmtctCohorts.preTermBabies(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator underWeightBabies() {
        return cohortIndicator("Under Weight Babies", ReportUtils.<CohortDefinition>map(pmtctCohorts.underWeightBabies(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator liveBirths() {
        return cohortIndicator("Live Births", ReportUtils.<CohortDefinition>map(pmtctCohorts.underWeightBabies(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator stillBirths() {
        return cohortIndicator("Still Births", ReportUtils.<CohortDefinition>map(pmtctCohorts.stillBirths(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator initialTestAtMaternity() {
        return cohortIndicator("Initial Test at Maternity", ReportUtils.<CohortDefinition>map(pmtctCohorts.initialTestAtMaternity(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator positiveResultsAtMaternity() {
        return cohortIndicator("Positive Results At Maternity", ReportUtils.<CohortDefinition>map(pmtctCohorts.positiveResultsAtMaternity(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator hivPositiveDeliveries() {
        return cohortIndicator("HIV Positive Deliveries", ReportUtils.<CohortDefinition>map(pmtctCohorts.hivPositiveDeliveries(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator adolescentsNewHivPositiveAtMaternity() {
        return cohortIndicator("Adolescents (10-19 Years) New HIV+ Maternity ", ReportUtils.<CohortDefinition>map(pmtctCohorts.adolescentsNewHivPositiveAtMaternity(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator startedHAARTMaternity() {
        return cohortIndicator("Started on HAART at Maternity", ReportUtils.<CohortDefinition>map(pmtctCohorts.startedHAARTMaternity(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator infantARVProphylaxisMaternity() {
        return cohortIndicator("Infant ARV Prophylaxis Maternity", ReportUtils.<CohortDefinition>map(pmtctCohorts.infantARVProphylaxisMaternity(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator normalDeliveries() {
        return cohortIndicator("Normal Deliveries", ReportUtils.<CohortDefinition>map(pmtctCohorts.normalDeliveries(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator caesareanSections() {
        return cohortIndicator("Caesarean Sections", ReportUtils.<CohortDefinition>map(pmtctCohorts.caesareanSections(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator breechDeliveries() {
        return cohortIndicator("Breech Deliveries", ReportUtils.<CohortDefinition>map(pmtctCohorts.breechDeliveries(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator assistedVaginalDeliveries() {
        return cohortIndicator("Assisted Vaginal Deliveries", ReportUtils.<CohortDefinition>map(pmtctCohorts.assistedVaginalDeliveries(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator uterotonicGiven() {
        return cohortIndicator("Uterotonic given", ReportUtils.<CohortDefinition>map(pmtctCohorts.uterotonicGiven(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator carbetocin() {
        return cohortIndicator("Cabertocin given", ReportUtils.<CohortDefinition>map(pmtctCohorts.carbetocin(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator oxytocin() {
        return cohortIndicator("oxytocin given", ReportUtils.<CohortDefinition>map(pmtctCohorts.oxytocin(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator maternityClients() {
        return cohortIndicator("Maternity clients", ReportUtils.<CohortDefinition>map(pmtctCohorts.maternityClients(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator deformity() {
        return cohortIndicator("Deformity", ReportUtils.<CohortDefinition>map(pmtctCohorts.deformity(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator maceratedStillbirth() {
        return cohortIndicator("Macerated Stillbirth", ReportUtils.<CohortDefinition>map(pmtctCohorts.maceratedStillbirth(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator lowApgar() {
        return cohortIndicator("Low Apgar", ReportUtils.<CohortDefinition>map(pmtctCohorts.lowApgar(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator deaths10to14Years() {
        return cohortIndicator("Maternal deaths 10-14Years", ReportUtils.<CohortDefinition>map(pmtctCohorts.deaths10to14Years(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator deaths15to19Years() {
        return cohortIndicator("Maternal deaths 15-19Years", ReportUtils.<CohortDefinition>map(pmtctCohorts.deaths15to19Years(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator deaths20toplus() {
        return cohortIndicator("Maternal deaths 20 years plus", ReportUtils.<CohortDefinition>map(pmtctCohorts.deaths20toplus(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator deathAudited() {
        return cohortIndicator("Maternal death audited", ReportUtils.<CohortDefinition>map(pmtctCohorts.deathAudited(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator appliedChlorhexidine() {
        return cohortIndicator("Babies applied chlorhexidine for cord care", ReportUtils.<CohortDefinition>map(pmtctCohorts.appliedChlorhexidine(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator givenTetracycline() {
        return cohortIndicator("Babies given tetracycline at birth ", ReportUtils.<CohortDefinition>map(pmtctCohorts.givenTetracycline(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator infantsIntiatiedOnBreastfeeding() {
        return cohortIndicator("Infants intiatied on breastfeeding within 1 hour after birth", ReportUtils.<CohortDefinition>map(pmtctCohorts.infantsIntiatiedOnBreastfeeding(), "startDate=${startDate},endDate=${endDate}"));
    }
    public CohortIndicator vitaminK() {
        return cohortIndicator("Vitamin K given", ReportUtils.<CohortDefinition>map(pmtctCohorts.vitaminK(), "startDate=${startDate},endDate=${endDate}"));
    }

    //PNC INDICTORS
    public CohortIndicator pncClients() {
        return cohortIndicator("PNC Clients", ReportUtils.<CohortDefinition>map(pmtctCohorts.pncClients(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator newClientsPNC() {
        return cohortIndicator("New Clients", ReportUtils.<CohortDefinition>map(pmtctCohorts.newPNCClients(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator revisitsPNC() {
        return cohortIndicator("Revisit Clients", ReportUtils.<CohortDefinition>map(pmtctCohorts.revisitsPNC(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator pncMotherNewVisitWithin48hrs() {
        return cohortIndicator("New PNC visit for mothers within 48 hrs", ReportUtils.<CohortDefinition>map(pmtctCohorts.pncMotherNewVisitWithin48hrs(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator pncMotherNewVisitBetween3DaysUnder6Weeks() {
        return cohortIndicator("New PNC visit for mothers between 3 days and under 6 weeks", ReportUtils.<CohortDefinition>map(pmtctCohorts.pncMotherNewVisitBtwn3DaysUnder6Weeks(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator pncMotherNewVisitAfter6Weeks() {
        return cohortIndicator("New PNC visit for mothers after 6 weeks", ReportUtils.<CohortDefinition>map(pmtctCohorts.pncMotherNewVisitAfter6Weeks(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator pncBabyNewVisitWithin48hrs() {
        return cohortIndicator("New PNC visit for babies within 48 hrs", ReportUtils.<CohortDefinition>map(pmtctCohorts.pncBabyNewVisitWithin48hrs(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator pncBabyNewVisitBetween3DaysUnder6Weeks() {
        return cohortIndicator("New PNC visit for babies between 3 days and under 6 weeks", ReportUtils.<CohortDefinition>map(pmtctCohorts.pncBabyNewVisitBtwn3DaysUnder6Weeks(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator pncBabyNewVisitAfter6Weeks() {
        return cohortIndicator("New PNC visit for babies after 6 weeks", ReportUtils.<CohortDefinition>map(pmtctCohorts.pncBabyNewVisitAfter6Weeks(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator initialTestsAtPNC() {
        return cohortIndicator("Initial tests at PNC", ReportUtils.<CohortDefinition>map(pmtctCohorts.initialTestsAtPNC(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator hivPositiveResultAtPNC() {
        return cohortIndicator("Partner tested at PNC", ReportUtils.<CohortDefinition>map(pmtctCohorts.hivPositiveResultAtPNC(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator partnerTestedAtPNC() {
        return cohortIndicator("Partner tested at PNC", ReportUtils.<CohortDefinition>map(pmtctCohorts.partnerTestedAtPNC(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator startedHAARTPNC() {
        return cohortIndicator("Started HAART PNC", ReportUtils.map(pmtctCohorts.startedHAARTAtPNC(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator infantARVProphylaxis() {
        return cohortIndicator("AZT given to baby at ANC", ReportUtils.<CohortDefinition>map(pmtctCohorts.infantARVProphylaxis(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator cacxPAP() {
        return cohortIndicator("Screened for Cervical Cancer (PAP)", ReportUtils.<CohortDefinition>map(pmtctCohorts.cacxPAP(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator cacxVIA() {
        return cohortIndicator("Screened for Cervical Cancer (VIA)", ReportUtils.<CohortDefinition>map(pmtctCohorts.cacxVIA(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator cacxVILI() {
        return cohortIndicator("Screened for Cervical Cancer (VILI)", ReportUtils.<CohortDefinition>map(pmtctCohorts.cacxVILI(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator cacxHPV() {
        return cohortIndicator("Screened for Cervical Cancer (HPV)", ReportUtils.<CohortDefinition>map(pmtctCohorts.cacxHPV(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator receivedFPMethod() {
        return cohortIndicator("Received FP method", ReportUtils.<CohortDefinition>map(pmtctCohorts.receivedFPMethod(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator hivNegativeTest1() {
        return cohortIndicator("HIV Negative Test-1", ReportUtils.<CohortDefinition>map(pmtctCohorts.hivNegativeTest1(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator hivPositiveTest1() {
        return cohortIndicator("HIV Positive Test-1", ReportUtils.<CohortDefinition>map(pmtctCohorts.hivPositiveTest1(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator hivInvalidTest1() {
        return cohortIndicator("HIV Invalid Test-1", ReportUtils.<CohortDefinition>map(pmtctCohorts.hivInvalidTest1(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator hivWastedTest1() {
        return cohortIndicator("HIV wasted Test-1", ReportUtils.<CohortDefinition>map(pmtctCohorts.hivWastedTest1(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator hivNegativeTest2() {
        return cohortIndicator("HIV Negative Test-2", ReportUtils.<CohortDefinition>map(pmtctCohorts.hivNegativeTest2(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator hivPositiveTest2() {
        return cohortIndicator("HIV Positive Test-2", ReportUtils.<CohortDefinition>map(pmtctCohorts.hivPositiveTest2(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator hivInvalidTest2() {
        return cohortIndicator("HIV Invalid Test-2", ReportUtils.<CohortDefinition>map(pmtctCohorts.hivInvalidTest2(), "startDate=${startDate},endDate=${endDate}"));
    }

    public CohortIndicator hivWastedTest2() {
        return cohortIndicator("HIV wasted Test-2", ReportUtils.<CohortDefinition>map(pmtctCohorts.hivWastedTest2(), "startDate=${startDate},endDate=${endDate}"));
    }
    //MATERNITY INDICTORS

}