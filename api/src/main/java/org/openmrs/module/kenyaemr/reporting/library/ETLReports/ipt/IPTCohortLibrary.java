/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.ipt;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by dev on 22/09/19.
 */

/**
 * Library of cohort definitions used specifically in the TPT register summaries
 */
@Component


public class IPTCohortLibrary {
    public CohortDefinition patientsOnIPT(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select init.patient_id from kenyaemr_etl.etl_ipt_initiation init inner join kenyaemr_etl.etl_patient_demographics d on init.patient_id = d.patient_id and init.voided = 0 and d.voided = 0\n" +
                "where init.visit_date between date(:startDate) and date(:endDate);";
        cd.setName("patientsOnIPT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients on IPT");

        return cd;
    }

    public CohortDefinition PLHIVInitiatedIPT(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select init.patient_id from kenyaemr_etl.etl_ipt_initiation init inner join kenyaemr_etl.etl_patient_demographics d on init.patient_id = d.patient_id and init.voided = 0 and d.voided = 0\n" +
                "        where init.ipt_indication = 138571 and init.visit_date between date(:startDate) and date(:endDate);";
        cd.setName("patientsOnIPT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("PLHIV patients on IPT");

        return cd;
    }
    public CohortDefinition prisonersInitiatedIPT(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select init.patient_id from kenyaemr_etl.etl_ipt_initiation init inner join kenyaemr_etl.etl_patient_demographics d on init.patient_id = d.patient_id and init.voided = 0 and d.voided = 0\n" +
                "        where init.ipt_indication = 162277 and init.visit_date between date(:startDate) and date(:endDate);";
        cd.setName("patientsOnIPT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Prisoners on IPT");

        return cd;
    }

    public CohortDefinition hcwInitiatedIPT(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select init.patient_id from kenyaemr_etl.etl_ipt_initiation init inner join kenyaemr_etl.etl_patient_demographics d on init.patient_id = d.patient_id and init.voided = 0 and d.voided = 0\n" +
                "        where init.ipt_indication = \"hcw\" and init.visit_date between date(:startDate) and date(:endDate);";
        cd.setName("patientsOnIPT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Prisoners on IPT");

        return cd;
    }
    public CohortDefinition childrenExposedTB(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select init.patient_id from kenyaemr_etl.etl_ipt_initiation init inner join kenyaemr_etl.etl_patient_demographics d on init.patient_id = d.patient_id and init.voided = 0 and d.voided = 0\n" +
                "        where init.ipt_indication = 162278 and init.visit_date between date(:startDate) and date(:endDate);";
        cd.setName("patientsOnIPT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Children Exposed to TB");

        return cd;
    }
    public CohortDefinition completedIPT(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select init.patient_id from kenyaemr_etl.etl_ipt_initiation init left outer join kenyaemr_etl.etl_ipt_outcome o on init.patient_id = o.patient_id\n" +
                "where o.outcome = 1267;";
        cd.setName("patientsOnIPT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Completed IPT");

        return cd;
    }
}
