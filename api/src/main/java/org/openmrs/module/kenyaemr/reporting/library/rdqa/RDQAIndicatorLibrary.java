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