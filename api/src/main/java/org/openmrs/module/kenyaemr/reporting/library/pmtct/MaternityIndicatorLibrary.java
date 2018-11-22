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