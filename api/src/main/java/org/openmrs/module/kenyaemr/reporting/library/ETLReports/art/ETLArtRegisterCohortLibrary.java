/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.art;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by dev on 24/09/19.
 */

/**
 * Library of cohort definitions used specifically in the ART Cohort register summaries
 */
@Component


public class ETLArtRegisterCohortLibrary {

    public  CohortDefinition originalArtCohort() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery="select patient_id\n" +
                "from\n" +
                "(select e.patient_id,\n" +
                "        e.date_started,\n" +
                "        p.DOB as DOB,\n" +
                " max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art\n" +
                " from\n" +
                "      (select e.patient_id, min(e.date_started) as date_started\n" +
                "        from kenyaemr_etl.etl_drug_event e\n" +
                "        join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "        where e.program = 'HIV'\n" +
                "        group by e.patient_id) e\n" +
                " inner join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                " inner join kenyaemr_etl.etl_patient_demographics p on p.patient_id = e.patient_id and  p.voided = 0\n" +
                " where date(e.date_started) between date(:startDate) and date(:endDate)\n" +
                " group by e.patient_id\n" +
                " having TI_on_art=0) a;";

        cd.setName("originalARTCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Original ART Cohort");
        return cd;
    }

    public  CohortDefinition transferInArtCohort() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery="select patient_id\n" +
                "from\n" +
                "(select e.patient_id,\n" +
                "        e.date_started,\n" +
                "        p.DOB as DOB,\n" +
                " max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art\n" +
                " from\n" +
                "      (select e.patient_id, min(e.date_started) as date_started\n" +
                "        from kenyaemr_etl.etl_drug_event e\n" +
                "        join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "        where e.program = 'HIV'\n" +
                "        group by e.patient_id) e\n" +
                " inner join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                " inner join kenyaemr_etl.etl_patient_demographics p on p.patient_id = e.patient_id and  p.voided = 0\n" +
                " where date(enr.date_started_art_at_transferring_facility) between date(:startDate) and date(:endDate)\n" +
                " group by e.patient_id\n" +
                " having TI_on_art=1) a;";

        cd.setName("originalARTCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Original ART Cohort");
        return cd;
    }

    public  CohortDefinition transferOutArtCohort() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery="select patient_id\n" +
                "from\n" +
                "  (select e.patient_id,\n" +
                "     e.date_started,\n" +
                "     p.DOB as DOB\n" +
                "   from\n" +
                "     (select e.patient_id,\n" +
                "             min(e.date_started) as date_started,\n" +
                "             mid(max(concat(pd.transfer_date,pd.discontinuation_reason)),11) as lastDiscontinuationsReason\n" +
                "         from kenyaemr_etl.etl_drug_event e\n" +
                "         join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "         join kenyaemr_etl.etl_patient_program_discontinuation pd on pd.patient_id = e.patient_id\n" +
                "         where e.program = 'HIV'\n" +
                "         group by e.patient_id) e\n" +
                "     inner join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "     inner join kenyaemr_etl.etl_patient_demographics p on p.patient_id = e.patient_id and  p.voided = 0\n" +
                "   where  e.lastDiscontinuationsReason = 159492 and (date(e.date_started) between date(:startDate) and date(:endDate) or date(enr.date_started_art_at_transferring_facility) between date(:startDate) and date(:endDate))\n" +
                "   group by e.patient_id) a;";

        cd.setName("originalARTCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Original ART Cohort");
        return cd;
    }

    public  CohortDefinition originalFirstLineArtCohort() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery="select\n" +
                "  fdr.patient_id\n" +
                "from  (SELECT patient_id,\n" +
                "         mid(max(concat(visit_date,regimen)),11) as firstSubstitution,\n" +
                "         mid(max(concat(visit_date,date_started)),11) as dateStarted,\n" +
                "         mid(max(concat(visit_date,regimen_line)),11) as regimenLine,\n" +
                "         COUNT(patient_id) as p_id FROM kenyaemr_etl.etl_drug_event WHERE regimen_line=\"First line\" GROUP BY patient_id  HAVING p_id = 1) fdr\n" +
                "GROUP BY fdr.patient_id;";

        cd.setName("originalFirstLineARTCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Original First Line ART Cohort");
        return cd;
    }

    public  CohortDefinition alternateFirstLineArtCohort() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery="select\n" +
                "  fdr.patient_id\n" +
                "from  (SELECT patient_id,\n" +
                "         mid(max(concat(visit_date,regimen)),11) as firstSubstitution,\n" +
                "         mid(max(concat(visit_date,date_started)),11) as dateStarted,\n" +
                "         mid(max(concat(visit_date,regimen_line)),11) as regimenLine,\n" +
                "         COUNT(patient_id) as p_id FROM kenyaemr_etl.etl_drug_event WHERE regimen_line=\"First line\" GROUP BY patient_id  HAVING p_id = 2) fdr\n" +
                "GROUP BY fdr.patient_id;";

        cd.setName("alternateFirstLineARTCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Alternate First Line ART Cohort");
        return cd;
    }

    public  CohortDefinition secondLineArtCohort() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery="select\n" +
                "  fdr.patient_id\n" +
                "from  (SELECT  patient_id,\n" +
                "         mid(max(concat(visit_date,regimen)),11) as firstSwitch,\n" +
                "         mid(max(concat(visit_date,date_started)),11) as dateStarted,\n" +
                "         mid(max(concat(visit_date,regimen_line)),11) as regimenLine,\n" +
                "         COUNT(patient_id) as p_id FROM kenyaemr_etl.etl_drug_event WHERE regimen_line=\"Second line\" GROUP BY patient_id  HAVING p_id > 0) fdr\n" +
                "GROUP BY fdr.patient_id;";

        cd.setName("secondFirstLineARTCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Second Line ART Cohort");
        return cd;
    }

    public  CohortDefinition thirdLineArtCohort() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery="select\n" +
                "  fdr.patient_id\n" +
                "from  (SELECT  patient_id,\n" +
                "         mid(max(concat(visit_date,regimen)),11) as firstSwitch,\n" +
                "         mid(max(concat(visit_date,date_started)),11) as dateStarted,\n" +
                "         mid(max(concat(visit_date,regimen_line)),11) as regimenLine,\n" +
                "         COUNT(patient_id) as p_id FROM kenyaemr_etl.etl_drug_event WHERE regimen_line=\"Third line\" GROUP BY patient_id  HAVING p_id > 0) fdr\n" +
                "GROUP BY fdr.patient_id;";

        cd.setName("secondFirstLineARTCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Third Line ART Cohort");
        return cd;
    }

    public  CohortDefinition withVlResultsArtCohort() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery="select patient_id\n" +
                "from\n" +
                "  (select e.patient_id,\n" +
                "     e.date_started,\n" +
                "     p.DOB as DOB\n" +
                "   from\n" +
                "     (select e.patient_id, min(e.date_started) as date_started\n" +
                "      from kenyaemr_etl.etl_drug_event e\n" +
                "        join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "      where e.program = 'HIV'\n" +
                "      group by e.patient_id) e\n" +
                "     inner join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "     inner join kenyaemr_etl.etl_laboratory_extract lb on lb.patient_id=e.patient_id\n" +
                "     inner join kenyaemr_etl.etl_patient_demographics p on p.patient_id = e.patient_id and  p.voided = 0\n" +
                "   where date(e.date_started) between date(:startDate) and date(:endDate) and (lb.visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and :endDate)\n" +
                "         and (lb.lab_test in (856, 1305))\n" +
                "   group by e.patient_id) a;";

        cd.setName("ARTCohortWithVLResilts");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("ART Cohort With VL Results");
        return cd;
    }

    public CohortDefinition suppressedArtCohort(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id\n" +
                "from\n" +
                "  (select e.patient_id,\n" +
                "     e.date_started,\n" +
                "     p.DOB as DOB,\n" +
                "   if(lb.lab_test = 856, lb.test_result, if(lb.lab_test=1305 and lb.test_result = 1302, \"LDL\", \"\")) as vl_result\n" +
                "   from\n" +
                "     (select e.patient_id, min(e.date_started) as date_started\n" +
                "      from kenyaemr_etl.etl_drug_event e\n" +
                "        join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "      where e.program = 'HIV'\n" +
                "      group by e.patient_id) e\n" +
                "     inner join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "     inner join kenyaemr_etl.etl_laboratory_extract lb on lb.patient_id=e.patient_id\n" +
                "     inner join kenyaemr_etl.etl_patient_demographics p on p.patient_id = e.patient_id and  p.voided = 0\n" +
                "   where date(e.date_started) between date(:startDate) and date(:endDate) and (lb.visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and :endDate)\n" +
                "         and (lb.lab_test in (856, 1305))\n" +
                "   group by e.patient_id\n" +
                "   having mid(max(concat(lb.visit_date, lb.test_result)), 11)=\"LDL\" or mid(max(concat(lb.visit_date, lb.test_result)), 11)<1000) a;";
        cd.setName("suppressedArtCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("SuppressedArtCohort");

        return cd;
    }

    public CohortDefinition stoppedArt(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id\n" +
                "from\n" +
                "  (select e.patient_id,\n" +
                "     e.date_started,\n" +
                "     p.DOB as DOB\n" +
                "   from\n" +
                "     (select e.patient_id, min(e.date_started) as date_started\n" +
                "      from kenyaemr_etl.etl_drug_event e\n" +
                "        join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "      where e.program = 'HIV' and e.reason_discontinued != \"\"\n" +
                "      group by e.patient_id) e\n" +
                "     inner join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "     inner join kenyaemr_etl.etl_patient_demographics p on p.patient_id = e.patient_id and  p.voided = 0\n" +
                "   where date(e.date_started) between date(:startDate) and date(:endDate)\n" +
                "   group by e.patient_id) a;";
        cd.setName("stoppedArt");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("StoppedArt");

        return cd;
    }

    public CohortDefinition defaulterArtCohort(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id\n" +
                "from\n" +
                "  (select e.patient_id,\n" +
                "     e.date_started,\n" +
                "     p.DOB as DOB\n" +
                "   from\n" +
                "     (select e.patient_id, min(e.date_started) as date_started\n" +
                "      from kenyaemr_etl.etl_drug_event e\n" +
                "        join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "      where e.program = 'HIV' and e.reason_discontinued != \"\"\n" +
                "      group by e.patient_id) e\n" +
                "     inner join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "     inner join kenyaemr_etl.etl_patient_demographics p on p.patient_id = e.patient_id and  p.voided = 0\n" +
                "   where date(e.date_started) between date(:startDate) and date(:endDate)\n" +
                "   group by e.patient_id) a;";
        cd.setName("defaulterArt");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("DefaulterArt");

        return cd;
    }
    public CohortDefinition deadArtCohort(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id\n" +
                "from\n" +
                "  (select e.patient_id,\n" +
                "     e.date_started,\n" +
                "     p.DOB as DOB\n" +
                "   from\n" +
                "     (select e.patient_id, min(e.date_started) as date_started\n" +
                "      from kenyaemr_etl.etl_drug_event e\n" +
                "        join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "      where e.program = 'HIV'\n" +
                "      group by e.patient_id) e\n" +
                "     inner join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "     inner join kenyaemr_etl.etl_patient_demographics p on p.patient_id = e.patient_id and  p.voided = 0\n" +
                "     inner join kenyaemr_etl.etl_patient_program_discontinuation pd on pd.patient_id = e.patient_id\n" +
                "   where date(e.date_started) between date(:startDate) and date(:endDate) and pd.discontinuation_reason=160034\n" +
                "   group by e.patient_id) a;";
        cd.setName("deadArtCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("DeadArtCohort");

        return cd;
    }

    public CohortDefinition ltfuArtCohort(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select patient_id\n" +
                "from\n" +
                "  (select e.patient_id,\n" +
                "     e.date_started,\n" +
                "     p.DOB as DOB\n" +
                "   from\n" +
                "     (select e.patient_id, min(e.date_started) as date_started\n" +
                "      from kenyaemr_etl.etl_drug_event e\n" +
                "        join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "      where e.program = 'HIV'\n" +
                "      group by e.patient_id) e\n" +
                "     inner join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "     inner join kenyaemr_etl.etl_patient_demographics p on p.patient_id = e.patient_id and  p.voided = 0\n" +
                "     inner join kenyaemr_etl.etl_patient_program_discontinuation pd on pd.patient_id = e.patient_id\n" +
                "   where date(e.date_started) between date(:startDate) and date(:endDate) and pd.discontinuation_reason=5240\n" +
                "   group by e.patient_id) a;";
        cd.setName("ltfuArtCohort");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("LtfuArtCohort");

        return cd;
    }
}
