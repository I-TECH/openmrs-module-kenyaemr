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
        String sqlQuery = "select patient_id from(\n" +
                "                      select c.patient_id,f.stability stability,f.person_present patient_present,c.latest_vis_date latest_visit_date,f.visit_date fup_visit_date,c.latest_tca ltca\n" +
                "                                    from kenyaemr_etl.etl_current_in_care c\n" +
                "                             inner join kenyaemr_etl.etl_patient_hiv_followup f on f.patient_id = c.patient_id and c.latest_vis_date =f.visit_date\n" +
                "                      where c.started_on_drugs is not null  and f.voided = 0 group by c.patient_id) cic where cic.stability=1\n" +
                "                                                                                                          and cic.patient_present = 978\n" +
                "                                                                                                          and timestampdiff(month,cic.latest_visit_date,cic.ltca) <4;";
        cd.setName("stableUnder4Monthstca");
        cd.setQuery(sqlQuery);
        cd.setDescription("Stable with <4 months prescription");

        return cd;
    }

    public  CohortDefinition stableOver6Monthstca() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery="select patient_id from(\n" +
                "                      select c.patient_id,f.stability stability,f.person_present patient_present,c.latest_vis_date latest_visit_date,f.visit_date fup_visit_date,c.latest_tca ltca\n" +
                "                                    from kenyaemr_etl.etl_current_in_care c\n" +
                "                             inner join kenyaemr_etl.etl_patient_hiv_followup f on f.patient_id = c.patient_id and c.latest_vis_date =f.visit_date\n" +
                "                      where c.started_on_drugs is not null  and f.voided = 0 group by c.patient_id) cic where cic.stability=1\n" +
                "                                                                                                          and cic.patient_present = 978\n" +
                "                                                                                                          and timestampdiff(month,cic.latest_visit_date,cic.ltca) > 6;" ;
        cd.setName("stableOver6Monthstca");
        cd.setQuery(sqlQuery);
        cd.setDescription("Stable with 6+ months prescription");

        return cd;
    }

    public  CohortDefinition stablePatientsMultiMonthAppointments(Integer month) {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery="select patient_id from(\n" +
                "                      select c.patient_id,f.stability stability,f.person_present patient_present,c.latest_vis_date latest_visit_date,f.visit_date fup_visit_date,c.latest_tca ltca\n" +
                "                                    from kenyaemr_etl.etl_current_in_care c\n" +
                "                             inner join kenyaemr_etl.etl_patient_hiv_followup f on f.patient_id = c.patient_id and c.latest_vis_date =f.visit_date\n" +
                "                      where c.started_on_drugs is not null  and f.voided = 0 group by c.patient_id) cic where cic.stability=1\n" +
                "                                                                                                          and cic.patient_present = 978\n" +
                "                                                                                                          and timestampdiff(month,cic.latest_visit_date,cic.ltca) =" + month + ";" ;
        cd.setName("multimonthTCA");
        cd.setQuery(sqlQuery);
        cd.setDescription("Stable with and monthly appointments");

        return cd;
    }

    public  CohortDefinition unstable() {
        String sqlQuery="\n" +
                "select patient_id from(\n" +
                "                      select c.patient_id,f.stability stability,c.latest_vis_date latest_visit_date,f.visit_date fup_visit_date\n" +
                "                      from kenyaemr_etl.etl_current_in_care c\n" +
                "                             inner join kenyaemr_etl.etl_patient_hiv_followup f on f.patient_id = c.patient_id and c.latest_vis_date =f.visit_date\n" +
                "                      where c.started_on_drugs is not null  and f.voided = 0 group by c.patient_id) cic where cic.stability=2;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("unstable");
        cd.setQuery(sqlQuery);
        cd.setDescription("Unstable Patients");
        return cd;
    }

    public  CohortDefinition undocumentedStability() {
        String sqlQuery="select patient_id from(\n" +
                "                      select c.patient_id,f.stability stability from kenyaemr_etl.etl_current_in_care c\n" +
                "                                                                inner join kenyaemr_etl.etl_patient_hiv_followup f on f.patient_id = c.patient_id and c.latest_vis_date =f.visit_date\n" +
                "                      where c.started_on_drugs is not null  and f.voided = 0 group by c.patient_id) cic where cic.stability is null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Undocumented stability");
        cd.setQuery(sqlQuery);
        cd.setDescription("Undocumented stability");
        return cd;
    }

}
