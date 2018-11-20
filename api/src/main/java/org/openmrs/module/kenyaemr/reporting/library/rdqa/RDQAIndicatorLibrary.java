/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.rdqa;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.*;
import org.openmrs.module.kenyaemr.reporting.library.shared.hiv.PwpCohortLibrary;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.kenyacore.report.ReportUtils.map;
import static org.openmrs.module.kenyaemr.reporting.EmrReportingUtils.cohortIndicator;

/**
 * Library of PwP related indicator definitions. All indicators require parameters ${startDate} and ${endDate}
 */
@Component
public class RDQAIndicatorLibrary {


	public CohortIndicator patientsOnCTX() {
		return cohortIndicator("patients on CTX", ReportUtils.<CohortDefinition>map(new RDQAPatientsOnCTXCohortDefinition(), ""));
	}

    public CohortIndicator currentOnART() {
        return cohortIndicator("Currently on ART", ReportUtils.<CohortDefinition>map(new RDQACurrentOnARTCohortDefinition(), ""));
    }

    public CohortIndicator cumulativeOnART() {
        return cohortIndicator("Cumulative ever on ART", ReportUtils.<CohortDefinition>map(new RDQACummulativeOnARTCohortDefinition(), ""));
    }

    public CohortIndicator screenedForTB() {
        return cohortIndicator("Screened for TB", ReportUtils.<CohortDefinition>map(new RDQAScreenedForTBCohortDefinition(), ""));
    }

    public CohortIndicator currentInCare() {
        return cohortIndicator("Current In Care", ReportUtils.<CohortDefinition>map(new RDQACurrentInCareCohortDefinition(), ""));
    }

    public CohortIndicator enrolledInCare() {
        return cohortIndicator("Enrolled In Care", ReportUtils.<CohortDefinition>map(new RDQAEnrolledInCareCohortDefinition(), ""));
    }

    public CohortIndicator knownPositives() {
        return cohortIndicator("Known Positives", ReportUtils.<CohortDefinition>map(new RDQAKnownPositivesCohortDefinition(), ""));
    }

    public CohortIndicator sampleFrame() {
        return cohortIndicator("Sample Frame", ReportUtils.<CohortDefinition>map(new RDQACohortSampleFrameDefinition(), ""));
    }

    public CohortIndicator sampleSize() {
        return cohortIndicator("Sample Size", ReportUtils.<CohortDefinition>map(new RDQACohortDefinition(), ""));
    }
}