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
    public CohortDefinition stableUnder1Monthtca(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_current_in_care c where c.stability =1 and c.started_on_drugs is not null\n" +
                "                  and timestampdiff(month,c.latest_vis_date,c.latest_tca) <1 group by c.patient_id;";
        cd.setName("stableUnder1Monthtca");
        cd.setQuery(sqlQuery);
        cd.setDescription("Stable with <1 month prescription");

        return cd;
    }

    public CohortDefinition stableUnder4Monthstca(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_current_in_care c where c.stability =1 and c.started_on_drugs is not null\n" +
                "                  and timestampdiff(month,c.latest_vis_date,c.latest_tca) <4 group by c.patient_id;";
        cd.setName("stableUnder4Monthstca");
        cd.setQuery(sqlQuery);
        cd.setDescription("Stable with <4 months prescription");

        return cd;
    }

    public  CohortDefinition stableOver6Monthstca() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery="select patient_id from kenyaemr_etl.etl_current_in_care c where c.stability =1 and c.started_on_drugs is not null\n" +
                "and timestampdiff(month,c.latest_vis_date,c.latest_tca) >6 group by c.patient_id;" ;
        cd.setName("stableOver6Monthstca");
        cd.setQuery(sqlQuery);
        cd.setDescription("Stable with 4+ months prescription");

        return cd;
    }

    public  CohortDefinition stablePatientsMultiMonthAppointments(Integer month) {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery="select patient_id from kenyaemr_etl.etl_current_in_care c where c.stability =1 and c.started_on_drugs is not null\n" +
                "and timestampdiff(month,c.latest_vis_date,c.latest_tca) =" + month + " group by c.patient_id;" ;
        cd.setName("multimonthTCA");
        cd.setQuery(sqlQuery);
        cd.setDescription("Stable with and monthly appointments");

        return cd;
    }

    public  CohortDefinition unstable() {
        String sqlQuery="select patient_id from kenyaemr_etl.etl_current_in_care c where c.stability =2 and c.started_on_drugs is not null group by c.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("unstable");
        cd.setQuery(sqlQuery);
        cd.setDescription("Unstable Patients");
        return cd;
    }

    public  CohortDefinition undocumentedStability() {
        String sqlQuery="select patient_id from kenyaemr_etl.etl_current_in_care c where c.stability is null and c.started_on_drugs is not null group by c.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Undocumented stability");
        cd.setQuery(sqlQuery);
        cd.setDescription("Undocumented stability");
        return cd;
    }

}
