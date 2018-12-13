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
import org.openmrs.module.kenyaemr.reporting.cohort.definition.pmtct.maternity.*;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library for Maternity related indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
@Component
public class MaternityIndicatorLibrary {


	public CohortIndicator clientsWithAPH() {
		return cohortIndicator("Clients with APH", ReportUtils.<CohortDefinition>map(new MaternityClientsWithAPHCohortDefinition(), ""));
	}

    public CohortIndicator clientsWithPPH() {
        return cohortIndicator("Clients with PPH", ReportUtils.<CohortDefinition>map(new MaternityClientsWithPPHCohortDefinition(), ""));
    }

    public CohortIndicator clientsWithEclampsia() {
        return cohortIndicator("Clients with Eclampsia", ReportUtils.<CohortDefinition>map(new MaternityClientsWithEclampsiaCohortDefinition(), ""));
    }

    public CohortIndicator clientsWithRapturedUterus() {
        return cohortIndicator("Clients with raptured uterus", ReportUtils.<CohortDefinition>map(new MaternityClientsWithRapturedUterusCohortDefinition(), ""));
    }

    public CohortIndicator clientsWithObstructedLabour() {
        return cohortIndicator("Clients with obstructed Labour", ReportUtils.<CohortDefinition>map(new MaternityClientsWithObstructedLabourCohortDefinition(), ""));
    }

    public CohortIndicator clientsWithSepsis() {
        return cohortIndicator("Clients with Sepsis", ReportUtils.<CohortDefinition>map(new MaternityClientsWithSepsisCohortDefinition(), ""));
    }

    public CohortIndicator clientsAlive() {
        return cohortIndicator("Clients Alive", ReportUtils.<CohortDefinition>map(new MaternityAliveCohortDefinition(), ""));
    }

    public CohortIndicator clientsDead() {
        return cohortIndicator("Clients Dead", ReportUtils.<CohortDefinition>map(new MaternityDeathsCohortDefinition(), ""));
    }

    public CohortIndicator preTermBabies() {
        return cohortIndicator("Pre-Term Babies", ReportUtils.<CohortDefinition>map(new PreTermBabiesCohortDefinition(), ""));
    }

    public CohortIndicator underWeightBabies() {
        return cohortIndicator("Under Weight Babies", ReportUtils.<CohortDefinition>map(new UnderWeightBabiesCohortDefinition(), ""));
    }

    public CohortIndicator liveBirths() {
        return cohortIndicator("Live Births", ReportUtils.<CohortDefinition>map(new LiveBirthsCohortDefinition(), ""));
    }

    public CohortIndicator stillBirths() {
        return cohortIndicator("Still Births", ReportUtils.<CohortDefinition>map(new StillBirthsCohortDefinition(), ""));
    }

    public CohortIndicator initialTestAtMaternity() {
        return cohortIndicator("Initial Test at Maternity", ReportUtils.<CohortDefinition>map(new InitialTestAtMaternityCohortDefinition(), ""));
    }

    public CohortIndicator positiveResultsAtMaternity() {
        return cohortIndicator("Positive Results At Maternity", ReportUtils.<CohortDefinition>map(new PositiveResultsAtMaternityCohortDefinition(), ""));
    }

    public CohortIndicator hivPositiveDeliveries() {
        return cohortIndicator("HIV Positive Deliveries", ReportUtils.<CohortDefinition>map(new HivPositiveDeliveriesCohortDefinition(), ""));
    }

    public CohortIndicator adolescentsNewHivPositiveAtMaternity() {
        return cohortIndicator("Adolescents (10-19 Years) New HIV+ Maternity ", ReportUtils.<CohortDefinition>map(new AdolescentsNewHIVPositiveAtMaternityCohortDefinition(), ""));
    }
    public CohortIndicator startedHAARTMaternity() {
        return cohortIndicator("Started on HAART at Maternity", ReportUtils.<CohortDefinition>map(new StartedHAARTAtMaternityCohortDefinition(), ""));
    }

    public CohortIndicator infantARVProphylaxisMaternity() {
        return cohortIndicator("Infant ARV Prophylaxis Maternity", ReportUtils.<CohortDefinition>map(new InfantARVProphylaxisAtMaternityCohortDefinition(), ""));
    }

    public CohortIndicator normalDeliveries() {
        return cohortIndicator("Normal Deliveries", ReportUtils.<CohortDefinition>map(new NormalDeliveriesCohortDefinition(), ""));
    }

    public CohortIndicator caesareanSections() {
        return cohortIndicator("Caesarean Sections", ReportUtils.<CohortDefinition>map(new CaesareanSectionsCohortDefinition(), ""));
    }

    public CohortIndicator breechDeliveries() {
        return cohortIndicator("Breech Deliveries", ReportUtils.<CohortDefinition>map(new BreechDeliveriesCohortDefinition(), ""));
    }

    public CohortIndicator assistedVaginalDeliveries() {
        return cohortIndicator("Assisted Vaginal Deliveries",ReportUtils.<CohortDefinition>map(new AssistedVaginalDeliveriesCohortDefinition(),""));
    }


}