/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.kenyaemr.reporting.library.ETLReports.publicHealthActionReport;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of HIV related indicator definitions.
 */
@Component
public class PublicHealthActionIndicatorLibrary {
    @Autowired
    private PublicHealthActionCohortLibrary cohortLibrary;

    /**
     * Number of HIV+ patients not linked to care
     * @return the indicator
     */
    public CohortIndicator notLinked() {
        return cohortIndicator("HIV+ patients not linked to care", ReportUtils.map(cohortLibrary.notLinked(), ""));
    }

    /**
     * Number of HEIs with undocumented status
     * @return
     */
    public CohortIndicator undocumentedHEIStatus() {
        return cohortIndicator("HEIs with undocumented status", ReportUtils.map(cohortLibrary.undocumentedHEIStatus(), ""));
    }

    /**
     * Number of patients with no current vl result
     * Valid means VL was taken <= 12 months ago and invalid means VL was taken > 12 months ago
     * @return the indicator
     */
    public CohortIndicator invalidVL() {
        return cohortIndicator("No Current VL Results", ReportUtils.map(cohortLibrary.invalidVL(), ""));
    }

    /**
     * Number of patients with unsuppressed VL result in their last VL. Indicated if valid or invalid vl.
     * Valid means VL was taken <= 12 months and invalid means VL was taken > 12 months ago
     * @return the indicator
     */
    public CohortIndicator unsuppressedWithValidVL() {
        return cohortIndicator("Unsuppressed VL result", ReportUtils.map(cohortLibrary.unsuppressedWithValidVL(), ""));
    }

    /**
     * Number of patients with unsuppressed VL result in their last VL. Indicated if valid or invalid vl.
     * Valid means VL was taken <= 12 months and invalid means VL was taken > 12 months ago
     * @return the indicator
     */
    public CohortIndicator unsuppressedWithoutValidVL() {
        return cohortIndicator("Unsuppressed VL result", ReportUtils.map(cohortLibrary.unsuppressedWithoutValidVL(), ""));
    }

    /**
     * Number of undocumented LTFU patients
     * @return the indicator
     */
    public CohortIndicator undocumentedLTFU() {
        return cohortIndicator("Undocumented LTFU patients", ReportUtils.map(cohortLibrary.undocumentedLTFU(), ""));
    }

    /**
     * Number of patients who missed HIV appointments
     * @return the indicator
     */
    public CohortIndicator recentDefaulters() {
        return cohortIndicator("Missed appointments", ReportUtils.map(cohortLibrary.recentDefaulters(), ""));
    }

    /**
     * Number of HEIs not linked to Mothers
     * @return the indicator
     */
    public CohortIndicator unlinkedHEI() {
        return cohortIndicator("HEIs not linked to Mothers", ReportUtils.map(cohortLibrary.unlinkedHEI(), ""));
    }

    /**
     * Number of adolescents not in OTZ
     * @return the indicator
     */
    public CohortIndicator adolescentsNotInOTZ() {
        return cohortIndicator("Adolescents not in OTZ", ReportUtils.map(cohortLibrary.adolescentsNotInOTZ(), ""));
    }

    /**
     * Number of children not in OVC
     * @return the indicator
     */
    public CohortIndicator childrenNotInOVC() {
        return cohortIndicator("Children not in OVC", ReportUtils.map(cohortLibrary.childrenNotInOVC(), ""));
    }

    /**
     * Number of contacts with undocumented HIV status
     * @return the indicator
     */
    public CohortIndicator contactsUndocumentedHIVStatus() {
        return cohortIndicator("Contacts with undocumented HIV status", ReportUtils.map(cohortLibrary.contactsUndocumentedHIVStatus(), ""));
    }

    /**
     * Number of SNS Contacts with undocumented HIV status
     * @return the indicator
     */
    public CohortIndicator snsContactsUndocumentedHIVStatus() {
        return cohortIndicator("SNS Contacts with undocumented HIV status", ReportUtils.map(cohortLibrary.snsContactsUndocumentedHIVStatus(), ""));
    }

    /**
     * Number of TX_CURR clients without NUPI
     * @return the indicator
     */
    public CohortIndicator txCurrclientsWithoutNUPI() {
        return cohortIndicator("TX_Curr Clients without NUPI", ReportUtils.map(cohortLibrary.txCurrclientsWithoutNUPI(), ""));
    }
}
