/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.vmmc;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Library of cohort definitions for vmmc adverse events
 */
@Component
public class VMMCAdverseEventsCohortLibrary {
    public CohortDefinition getClientsWithVMMCAdverseEvent(Integer adverseEvent, Integer severity, String form) {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_adverse_events e where e.visit_date between date(:startDate) and date(:endDate) and e.adverse_event = "+adverseEvent+" and e.severity = "+severity+" and e.form = '"+form+"';";
        cd.setName("ClientsWithVMMCAdverseEvent");
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.setQuery(sqlQuery);
        cd.setDescription("ClientsWithVMMCAdverseEvent");

        return cd;
    }
}
