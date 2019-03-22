/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.diffCareStability;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.springframework.stereotype.Component;

/**
 * Library of cohort definitions for differentiated care
 */
@Component
public class DiffCareStabilityCohortLibrary {
    public CohortDefinition stableUnder4Monthstca(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select c.patient_id from kenyaemr_etl.etl_current_in_care c  inner join kenyaemr_etl.etl_patient_hiv_followup f\n" +
                "on c.patient_id = f.patient_id where f.stability = 1 and f.person_present = 978\n" +
                "and timestampdiff(month,c.latest_vis_date,c.latest_tca) between 0 and 3\n" +
                "and c.started_on_drugs is not null group by c.patient_id;";
        cd.setName("stableUnder4Monthstca");
        cd.setQuery(sqlQuery);
        cd.setDescription("Stable with <4 months prescription");

        return cd;
    }

    public  CohortDefinition stableOver4Monthstca() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery="select c.patient_id from kenyaemr_etl.etl_current_in_care c  inner join kenyaemr_etl.etl_patient_hiv_followup f\n" +
                "on c.patient_id = f.patient_id where f.stability = 1 and f.person_present = 978\n" +
                "and timestampdiff(month,c.latest_vis_date,c.latest_tca) >=4\n" +
                "and c.started_on_drugs is not null group by c.patient_id;" ;
        cd.setName("stableOver4Monthstca");
        cd.setQuery(sqlQuery);
        cd.setDescription("Stable with 4+ months prescription");

        return cd;
    }

    public  CohortDefinition unstable() {
        String sqlQuery="select c.patient_id from kenyaemr_etl.etl_current_in_care c  inner join kenyaemr_etl.etl_patient_hiv_followup f\n" +
                " on c.patient_id = f.patient_id where f.stability = 2\n" +
                " and c.started_on_drugs is not null group by c.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("unstable");
        cd.setQuery(sqlQuery);
        cd.setDescription("Unstable Patients");
        return cd;
    }

}
