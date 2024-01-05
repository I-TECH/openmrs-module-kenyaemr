/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.RevisedDatim;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by dev on 1/10/18.
 */

/**
 * Library of cohort definitions used specifically in Datim Reports
 */
@Component
public class DatimCohortLibrary {

    static String startOfYear = "0000-10-01";

    /**
     * Patients started on ART during the reporting period (last 3 months)
     * TX_New Datim indicator
     * @return
     */
    public CohortDefinition startedOnART() {
        String sqlQuery = "select net.patient_id  \n" +
                "                from (  \n" +
                "                select e.patient_id,e.date_started,  \n" +
                "                e.gender, \n" +
                "                e.dob, \n" +
                "                d.visit_date as dis_date,  \n" +
                "                if(d.visit_date is not null, 1, 0) as TOut, \n" +
                "                e.regimen, e.regimen_line, e.alternative_regimen,  \n" +
                "                mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,  \n" +
                "                max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art, \n" +
                "                max(if(enr.transfer_in_date is not null, 1, 0)) as TIn,  \n" +
                "                max(fup.visit_date) as latest_vis_date \n" +
                "                from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started,  \n" +
                "                mid(min(concat(e.date_started,e.regimen_name)),11) as regimen,  \n" +
                "                mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line,  \n" +
                "                max(if(discontinued,1,0))as alternative_regimen  \n" +
                "                from kenyaemr_etl.etl_drug_event e\n" +
                "                join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "                where e.program = 'HIV'\n" +
                "                group by e.patient_id) e  \n" +
                "                inner join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id  \n" +
                "                left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_name='HIV' \n" +
                "                left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id  \n" +
                "                where date(e.date_started) between date(:startDate) and date(:endDate)\n" +
                "                group by e.patient_id  \n" +
                "                having TI_on_art=0 \n" +
                "                )net;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_New");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Started on ART in the last 3 months");
        return cd;
    }

    /**
     * Patients previously started on ART before the reporting period
     * TX_PREV
     * @return
     */
    public CohortDefinition previouslyOnART() {
        String sqlQuery = "select net.patient_id  \n" +
                "                from (  \n" +
                "                select e.patient_id,e.date_started,  \n" +
                "                e.gender, \n" +
                "                e.dob, \n" +
                "                d.visit_date as dis_date,  \n" +
                "                if(d.visit_date is not null, 1, 0) as TOut, \n" +
                "                e.regimen, e.regimen_line, e.alternative_regimen,  \n" +
                "                mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,  \n" +
                "                max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art, \n" +
                "                max(if(enr.transfer_in_date is not null, 1, 0)) as TIn,  \n" +
                "                max(fup.visit_date) as latest_vis_date \n" +
                "                from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started,  \n" +
                "                mid(min(concat(e.date_started,e.regimen_name)),11) as regimen,  \n" +
                "                mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line,  \n" +
                "                max(if(discontinued,1,0))as alternative_regimen  \n" +
                "                from kenyaemr_etl.etl_drug_event e\n" +
                "                join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "                where e.program = 'HIV'\n" +
                "                group by e.patient_id) e  \n" +
                "                inner join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id  \n" +
                "                left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_name='HIV' \n" +
                "                left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id  \n" +
                "                where date(e.date_started) < date(:startDate) \n" +
                "                group by e.patient_id  \n" +
                "                having TI_on_art=0 \n" +
                "                )net;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_Prev");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Previously Started on ART before reporting period");
        return cd;
    }
    /**
     * Patients currently on ART
     * TX_Curr Datim indicator
     * @return
     */
    public CohortDefinition currentlyOnArt() {
        SqlCohortDefinition cd = new SqlCohortDefinition();

        String sqlQuery = "select t.patient_id\n" +
                "from (select fup.visit_date,\n" +
                "             fup.patient_id,\n" +
                "             max(e.visit_date)                                                                as enroll_date,\n" +
                "             mid(max(concat(e.visit_date, e.patient_type)), 11)  as patient_type,\n" +
                "             greatest(max(fup.visit_date), ifnull(max(d.visit_date), '0000-00-00'))           as latest_vis_date,\n" +
                "             greatest(mid(max(concat(fup.visit_date, fup.next_appointment_date)), 11),\n" +
                "                      ifnull(max(d.visit_date), '0000-00-00'))                                as latest_tca,\n" +
                "             d.patient_id                                                                     as disc_patient,\n" +
                "             d.effective_disc_date                                                            as effective_disc_date,\n" +
                "             max(d.visit_date)                                                                as date_discontinued,\n" +
                "             mid(max(concat(date(de.date_started), ifnull(de.discontinued, 0))), 11) as on_drugs\n" +
                "      from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "               join kenyaemr_etl.etl_patient_demographics p on p.patient_id = fup.patient_id\n" +
                "               join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                           inner join kenyaemr_etl.etl_drug_event de\n" +
                "                          on e.patient_id = de.patient_id and de.program = 'HIV' and date(de.date_started) <= date(:endDate)\n" +
                "               left outer JOIN\n" +
                "           (select patient_id,\n" +
                "                   coalesce(date(effective_discontinuation_date), visit_date) visit_date,\n" +
                "                   max(date(effective_discontinuation_date)) as               effective_disc_date\n" +
                "            from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "            where date(visit_date) <= date(:endDate)\n" +
                "              and program_name = 'HIV'\n" +
                "            group by patient_id) d on d.patient_id = fup.patient_id\n" +
                "      where fup.visit_date <= date(:endDate)\n" +
                "      group by patient_id\n" +
                "      having patient_type != 164931 and on_drugs != 1\n" +
                "         and (\n" +
                "          (\n" +
                "                  (timestampdiff(DAY, date(latest_tca), date(:endDate)) <= 30 and\n" +
                "                   ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or\n" +
                "                    d.effective_disc_date is null))\n" +
                "                  and\n" +
                "                  (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or\n" +
                "                   disc_patient is null)\n" +
                "              )\n" +
                "          )) t;";

        cd.setName("TX_Curr");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("currently on ART");
        return cd;
    }

    /**
     * Patients in Tx for atleast 3 months
     * @return
     */
    public CohortDefinition patientInTXAtleast3Months() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select e.patient_id from (select e.patient_id,min(date(e.date_started)) as date_started from kenyaemr_etl.etl_drug_event e where e.program ='HIV' and date(e.date_started) <= DATE(:endDate)\n" +
                "                group by e.patient_id having timestampdiff(MONTH,date_started,DATE(:endDate)) >= 3)e;";
        cd.setName("patientInTXAtleast3Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients in treatment for atleast 3 months");

        return cd;
    }

    /**
     * Patients in Tx for less than 3 months
     * @return
     */
    public CohortDefinition patientInTXLessThan3Months() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select e.patient_id from (select e.patient_id,min(date(e.date_started)) as date_started from kenyaemr_etl.etl_drug_event e where e.program ='HIV' and date(e.date_started) <= DATE(:endDate)\n" +
                "                group by e.patient_id having timestampdiff(MONTH,date_started,DATE(:endDate)) < 3)e;";
        cd.setName("patientInTXLessThan3Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients in treatment for less than 3 months");

        return cd;
    }

    /**
     * Patients in Tx for 3 to 5 months
     * @param
     * @return
     */
    public CohortDefinition patientInTX3To5Months() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select e.patient_id from (select e.patient_id,min(date(e.date_started)) as date_started from kenyaemr_etl.etl_drug_event e where e.program ='HIV' and date(e.date_started) <= DATE(:endDate)\n" +
                "                group by e.patient_id having timestampdiff(MONTH,date_started,DATE(:endDate)) between 3 and 5)e;";
        cd.setName("patienInTX3To5Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients in treatment for 3 - 5 months");

        return cd;
    }

    /**
     * Patients in Tx for atleast 6 months
     * @return
     */
    public CohortDefinition patientInTXAtleast6Months() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select e.patient_id from (select e.patient_id,min(date(e.date_started)) as date_started from kenyaemr_etl.etl_drug_event e where e.program ='HIV' and date(e.date_started) <= DATE(:endDate)\n" +
                "          group by e.patient_id having timestampdiff(MONTH,date_started,DATE(:endDate)) >= 6)e;";
        cd.setName("patientInTXAtleast6Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients in Tx for atleast 6 months");

        return cd;
    }

    /*PMTCT ANC only*/
//TODO find max test - Done
    public CohortDefinition patientHIVPositiveResultsAtANC() {

        String sqlQuery = "select v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "left join (select t.patient_id,t.visit_date from kenyaemr_etl.etl_hts_test t where t.test_type = 1 and t.final_test_result='Positive' and t.hts_entry_point = 160538)t on v.patient_id = t.patient_id and v.visit_date = t.visit_date\n" +
                "    where v.anc_visit_number = 1 and v.visit_date between date(:startDate) and date(:endDate) and (v.final_test_result = 'Positive' or t.patient_id is not null);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("testPositiveResultsANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive Results at ANC");
        return cd;

    }

    //TODO find latest test - Done
    public CohortDefinition patientHIVNegativeResultsATANC() {

        String sqlQuery = "select v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "left join (select t.patient_id,t.visit_date from kenyaemr_etl.etl_hts_test t where t.test_type = 1 and t.final_test_result='Negative' and t.hts_entry_point = 160538)t on v.patient_id = t.patient_id and v.visit_date = t.visit_date\n" +
                "    where v.anc_visit_number = 1 and v.visit_date between date(:startDate) and date(:endDate) and (v.final_test_result = 'Negative' or t.patient_id is not null);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("testNegativeResultsANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Negative Results at ANC");
        return cd;

    }

    //Clients with positive HIV status before ANC-1
    public CohortDefinition positiveHivStatusBeforeAnc1() {

        String sqlQuery = "select a.patient_id\n" +
                "from (select mch.patient_id, mch.latest_mch_enrolment_date, mch.service_type, e.latest_hiv_enrollment_date,mch.hiv_status_at_enrolment,t.test_result,t.latest_hiv_test_date\n" +
                "from (select mch.patient_id,\n" +
                "     max(mch.visit_date)                                       latest_mch_enrolment_date,\n" +
                "     mid(max(concat(mch.visit_date, mch.service_type)), 11) as service_type,\n" +
                "     mch.hiv_status as hiv_status_at_enrolment\n" +
                "from kenyaemr_etl.etl_mch_enrollment mch where mch.visit_date between date(:startDate) and date(:endDate)\n" +
                "group by mch.patient_id)mch\n" +
                "left join (select e.patient_id, max(e.visit_date) as latest_hiv_enrollment_date\n" +
                "           from kenyaemr_etl.etl_hiv_enrollment e where e.visit_date < date(:endDate)\n" +
                "           group by e.patient_id)e on mch.patient_id = e.patient_id\n" +
                "left join (select t.patient_id, max(t.visit_date) as latest_hiv_test_date,mid(max(concat(t.visit_date,t.final_test_result)),11) as test_result\n" +
                "          from kenyaemr_etl.etl_hts_test t where t.visit_date < date(:endDate)\n" +
                "          group by t.patient_id)t on mch.patient_id = t.patient_id\n" +
                "group by mch.patient_id\n" +
                "having mch.service_type = 1622\n" +
                "and (mch.latest_mch_enrolment_date > e.latest_hiv_enrollment_date or hiv_status_at_enrolment = 703 or (mch.latest_mch_enrolment_date > t.latest_hiv_test_date\n" +
                "and t.test_result = 'Positive')))a;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PMTCT_STAT_KNOWN_POSITIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients with positive HIV status before ANC-1");
        return cd;

    }
    /**
     * PMTCT_STAT DENOMINATOR - Number of new ANC clients
     * PMTCT_STAT DENOMINATOR - Number of new ANC clients
     * @return
     */
    public CohortDefinition newANCClients() {

        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_mch_enrollment e where e.service_type = 1622 and e.visit_date between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("newANCClients");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients newly enrolled for ANC");
        return cd;

    }

    /**
     * New and relapsed TB cases who are Known positive
     * @return
     */
    public CohortDefinition tbSTATKnownPositive() {
        String sqlQuery = "\n" +
                "select e.patient_id\n" +
                "from kenyaemr_etl.etl_tb_enrollment e\n" +
                "         left join (select h.patient_id,\n" +
                "                           coalesce(date(h.date_first_enrolled_in_care),\n" +
                "                                    min(date(visit_date))) as hiv_enr_date\n" +
                "                    from kenyaemr_etl.etl_hiv_enrollment h\n" +
                "                    where h.visit_date <= date(:endDate)\n" +
                "                    group by h.patient_id) h on e.patient_id = h.patient_id\n" +
                "         left join (select t.patient_id,\n" +
                "                           max(date(visit_date)) as latest_hiv_test_date\n" +
                "                    from kenyaemr_etl.etl_hts_test t\n" +
                "                    where t.visit_date <= date(:endDate)\n" +
                "                      and t.final_test_result = 'Positive'\n" +
                "                    group by t.patient_id) t on e.patient_id = t.patient_id\n" +
                "         left join (select patient_id,\n" +
                "                           max(date(visit_date)) as disc_date\n" +
                "                    from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "                    where date(visit_date) <= date(:endDate)\n" +
                "                      and program_name = 'TB'\n" +
                "                    group by patient_id) d on e.patient_id = d.patient_id\n" +
                "where e.patient_classification in (159878, 159877, 159876)\n" +
                "  and e.visit_date between date(:startDate) and date(:endDate)\n" +
                "  and (d.patient_id is null or d.disc_date < e.visit_date or d.disc_date > date(:endDate))\n" +
                "  and ((timestampdiff(WEEK, t.latest_hiv_test_date, h.hiv_enr_date) > 6) or\n" +
                "       timestampdiff(WEEK, h.hiv_enr_date, e.visit_date) > 6)\n" +
                "group by e.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("tbSTATKnownPositive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New and relapsed TB cases who are Known positive");
        return cd;
    }

    /**
     * New and relapsed TB cases newly tested positive
     * @return
     */
    public CohortDefinition tbSTATNewPositive() {
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_tb_enrollment e\n" +
                "         left join (select h.patient_id,\n" +
                "                           coalesce(mid(min(concat(date(visit_date), date(h.date_first_enrolled_in_care))), 11),\n" +
                "                                    min(date(visit_date))) as hiv_enr_date\n" +
                "                    from kenyaemr_etl.etl_hiv_enrollment h\n" +
                "                    where h.visit_date between date(:startDate) and date(:endDate)\n" +
                "                    group by h.patient_id) h on e.patient_id = h.patient_id and hiv_enr_date >= e.visit_date\n" +
                "         left join (select t.patient_id,\n" +
                "                           max(date(visit_date))                                       as latest_hiv_test_date,\n" +
                "                           mid(max(concat(date(visit_date), t.final_test_result)), 11) as latest_hiv_test_results\n" +
                "                    from kenyaemr_etl.etl_hts_test t\n" +
                "                    group by t.patient_id\n" +
                "                    having latest_hiv_test_date between date(:startDate) and date(:endDate)\n" +
                "                       and latest_hiv_test_results = 'Positive') t\n" +
                "                   on e.patient_id = t.patient_id and t.latest_hiv_test_date >= e.visit_date\n" +
                "         left join (select patient_id,\n" +
                "                           max(date(visit_date)) as disc_date\n" +
                "                    from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "                    where date(visit_date) <= date(:endDate)\n" +
                "                      and program_name = 'TB'\n" +
                "                    group by patient_id) d on e.patient_id = d.patient_id\n" +
                "where e.patient_classification in (159878, 159877, 159876)\n" +
                "  and e.visit_date between date(:startDate) and date(:endDate)\n" +
                "  and (d.patient_id is null or d.disc_date < e.visit_date or d.disc_date > date(:endDate))\n" +
                "  and (h.patient_id is not null or t.patient_id is not null)\n" +
                "group by e.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("tbSTATNewPositive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New and relapsed TB cases newly tested positive");
        return cd;
    }

    /**
     * New and relapsed TB cases newly tested negative
     * @return
     */
    public CohortDefinition tbSTATNewNegative() {
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_tb_enrollment e\n" +
                "         inner join (select t.patient_id,\n" +
                "                            max(date(visit_date))                                       as latest_hiv_test_date,\n" +
                "                            mid(max(concat(date(visit_date), t.final_test_result)), 11) as latest_hiv_test_results\n" +
                "                     from kenyaemr_etl.etl_hts_test t\n" +
                "                     group by t.patient_id\n" +
                "                     having latest_hiv_test_date between date(:startDate) and date(:endDate)\n" +
                "                        and latest_hiv_test_results = 'Negative') t\n" +
                "                    on e.patient_id = t.patient_id and t.latest_hiv_test_date >= e.visit_date\n" +
                "         left join (select patient_id,\n" +
                "                           max(date(visit_date)) as disc_date\n" +
                "                    from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "                    where date(visit_date) <= date(:endDate)\n" +
                "                      and program_name = 'TB'\n" +
                "                    group by patient_id) d on e.patient_id = d.patient_id\n" +
                "where e.patient_classification in (159878, 159877, 159876)\n" +
                "  and e.visit_date between date(:startDate) and date(:endDate)\n" +
                "  and (d.patient_id is null or d.disc_date < e.visit_date or d.disc_date > date(:endDate))\n" +
                "group by e.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("tbSTATNewNegative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New and relapsed TB cases newly tested positive");
        return cd;
    }

    /**
     * New and relapsed TB cases recently tested negative
     * @return
     */
    public CohortDefinition tbSTATRecentNegative() {
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_tb_enrollment e\n" +
                "         left join (select h.patient_id,\n" +
                "                           coalesce(mid(min(concat(date(visit_date), date(h.date_first_enrolled_in_care))), 11),\n" +
                "                                    min(date(visit_date))) as hiv_enr_date\n" +
                "                    from kenyaemr_etl.etl_hiv_enrollment h\n" +
                "                    where h.visit_date <= date(:endDate)\n" +
                "                    group by h.patient_id) h on e.patient_id = h.patient_id\n" +
                "         left join (select t.patient_id,\n" +
                "                           max(date(visit_date)) as latest_hiv_test_date\n" +
                "                    from kenyaemr_etl.etl_hts_test t\n" +
                "                    where t.visit_date <= date(:endDate)\n" +
                "                      and t.final_test_result = 'Negative'\n" +
                "                    group by t.patient_id) t on e.patient_id = t.patient_id\n" +
                "         left join (select patient_id,\n" +
                "                           max(date(visit_date)) as disc_date\n" +
                "                    from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "                    where date(visit_date) <= date(:endDate)\n" +
                "                      and program_name = 'TB'\n" +
                "                    group by patient_id) d on e.patient_id = d.patient_id\n" +
                "where e.patient_classification in (159878, 159877, 159876)\n" +
                "  and e.visit_date between date(:startDate) and date(:endDate)\n" +
                "  and (d.patient_id is null or d.disc_date < e.visit_date or d.disc_date > date(:endDate))\n" +
                "  and (t.latest_hiv_test_date < e.visit_date and\n" +
                "       (timestampdiff(WEEK, t.latest_hiv_test_date, e.visit_date) <= 6) or\n" +
                "       (h.hiv_enr_date < e.visit_date and\n" +
                "        timestampdiff(WEEK, h.hiv_enr_date, e.visit_date) <= 6))\n" +
                "group by e.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("tbSTATRecentNegative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New and relapsed TB cases newly tested negative");
        return cd;
    }

    /**
     * Total number of new and relapsed TB cases, during the reporting period
     * @return
     */
    public CohortDefinition tbSTATDenominator() {
        String sqlQuery = "select a.patient_id\n" +
                "from (select e.patient_id,\n" +
                "             coalesce(mid(max(concat(date(e.visit_date), date(e.date_first_enrolled_in_tb_care))), 11),\n" +
                "                      max(date(e.visit_date))) as enrollment_date,\n" +
                "             d.patient_id                      as disc_patient,\n" +
                "             d.disc_date\n" +
                "      from kenyaemr_etl.etl_tb_enrollment e\n" +
                "               left join (select patient_id,\n" +
                "                                 max(date(visit_date)) as disc_date\n" +
                "                          from kenyaemr_etl.etl_patient_program_discontinuation D\n" +
                "                          where date(visit_date) <= date(:endDate)\n" +
                "                            and program_name = 'TB'\n" +
                "                          group by patient_id) d on e.patient_id = d.patient_id\n" +
                "      where e.patient_classification in (159878, 159877, 159876)\n" +
                "      group by e.patient_id) a\n" +
                "where a.enrollment_date between date(:startDate) and date(:endDate)\n" +
                "  and (a.disc_patient is null or a.disc_date < a.enrollment_date or a.disc_date > date(:endDate));";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("tbSTATDenominator");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total number of new and relapsed TB cases, during the reporting period");
        return cd;
    }

    public CohortDefinition ovcOnART() {

        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_ovc_enrolment e\n" +
                "                            join\n" +
                "                             (select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                                     greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "                                     greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "                                     max(d.visit_date) as date_discontinued,\n" +
                "                                     d.patient_id as disc_patient,\n" +
                "                                     de.patient_id as started_on_drugs\n" +
                "                              from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                                     join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                                     join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                                     left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program in ('HIV') and date(date_started) <= date(:endDate)\n" +
                "                                     left outer JOIN\n" +
                "                                       (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                        where date(visit_date) <= date(:endDate) and program_name in ('HIV','OVC')\n" +
                "                                        group by patient_id\n" +
                "                                       ) d on d.patient_id = fup.patient_id\n" +
                "                              group by patient_id\n" +
                "                              having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "                                  ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) >= date(date_discontinued) and date(latest_vis_date)>= date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) ))\n" +
                "                                  )\n" +
                "                             ) t on e.patient_id = t.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("ovcOnART");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of OVC Current on ART reported to implementing partner");
        return cd;
    }

    public CohortDefinition ovcNotOnART() {

        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_ovc_enrolment e\n" +
                "                         left  join\n" +
                "                             (select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                                     max(fup.visit_date) as latest_vis_date,\n" +
                "                                     mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                                     max(d.visit_date) as date_discontinued,\n" +
                "                                     d.patient_id as disc_patient,\n" +
                "                                     de.patient_id as started_on_drugs\n" +
                "                              from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                                     join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                                     join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                                     left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program in ('HIV') and date(date_started) <= date(:endDate)\n" +
                "                                     left outer JOIN\n" +
                "                                       (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                        where date(visit_date) <= date(:endDate) and program_name in ('HIV','OVC')\n" +
                "                                        group by patient_id\n" +
                "                                       ) d on d.patient_id = fup.patient_id\n" +
                "                              group by patient_id\n" +
                "                              having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "                                  ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) ))\n" +
                "                                  )\n" +
                "                             ) t on e.patient_id = t.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("ovcNotOnART");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of OVC Not on ART reported to implementing partner");
        return cd;
    }

    /**
     * Women enrolled in HIV program as of some effective date - excludes those discontinued from the program
     * @return
     */
    public CohortDefinition womenEnrolledInHIVProgram() {

        String sqlQuery = "select d.patient_id\n" +
                "from kenyaemr_etl.etl_patient_demographics d\n" +
                "       inner join (select e.patient_id, max(e.visit_date) as enroll_date\n" +
                "                   from kenyaemr_etl.etl_hiv_enrollment e\n" +
                "                   group by e.patient_id)e on d.patient_id = e.patient_id\n" +
                "       left outer join (select dis.patient_id,\n" +
                "                               coalesce(date(dis.effective_discontinuation_date), dis.visit_date) visit_date,\n" +
                "                               max(date(dis.effective_discontinuation_date)) as                   effective_disc_date\n" +
                "                        from kenyaemr_etl.etl_patient_program_discontinuation dis\n" +
                "                        where date(dis.visit_date) <= date(:endDate)\n" +
                "                          and dis.program_name = 'HIV'\n" +
                "                        group by dis.patient_id) dis on d.patient_id = dis.patient_id\n" +
                "where d.Gender = 'F' and\n" +
                "      (((date(dis.effective_disc_date) > date(:endDate) or date(e.enroll_date) > date(dis.effective_disc_date)) or\n" +
                "       dis.effective_disc_date is null)\n" +
                "   or dis.patient_id is null);";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("womenEnrolledInHIVProgram");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive women enrolled in HIV program");
        return cd;

    }

    /**
     *
     * @return
     */
    public CohortDefinition firstTimeScreenedCXCASCRNNegativeSql() {

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) " +
                "and coalesce(s.via_vili_screening_result,s.hpv_screening_result,s.pap_smear_screening_result,s.colposcopy_screening_result) in ('Negative','Normal')\n" +
                "and s.screening_type = 'First time screening';";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("firstTimeScreenedCXCANegativeSql");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive women on ART screened Negative for cervical cancer 1st time");
        return cd;

    }

    /**
     *
     * @return
     */
    public CohortDefinition firstTimeScreenedCXCASCRNPositiveSql() {

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) " +
                "and  coalesce(s.via_vili_screening_result,s.hpv_screening_result,s.pap_smear_screening_result,s.colposcopy_screening_result) in ('Positive','Abnormal','Invasive Cancer')\n" +
                "and s.screening_type = 'First time screening';";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("firstTimescreenedCXCAPositive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive women on ART screened Positive for cervical cancer 1st time");
        return cd;

    }

    /**
     *
     * @return
     */
    public CohortDefinition firstTimeScreenedCXCASCRNPresumedSql() {

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) " +
                "and  coalesce(s.via_vili_screening_result,s.hpv_screening_result,s.pap_smear_screening_result,s.colposcopy_screening_result) in ('Suspicious for cancer','Low grade lesion','High grade lesion','Presumed Cancer')\n" +
                "and s.screening_type = 'First time screening';";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("firstTimeScreenedCXCAPresumedSql");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive women on ART with Presumed cervical cancer 1st time screening");
        return cd;
    }

    /**
     * @return
     */
    public CohortDefinition rescreenedCXCASCRNNegativeSql() {

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) " +
                "and  coalesce(s.via_vili_screening_result,s.hpv_screening_result,s.pap_smear_screening_result,s.colposcopy_screening_result) in ('Negative','Normal')\n" +
                "and s.screening_type = 'Rescreening';";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("rescreenedCXCANegativeSql");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive women on ART with Negative cervical cancer results during re-screening");
        return cd;

    }

    /**
     * @return
     */
    public CohortDefinition rescreenedCXCASCRNPositiveSql() {

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) " +
                "and  coalesce(s.via_vili_screening_result,s.hpv_screening_result,s.pap_smear_screening_result,s.colposcopy_screening_result) in ('Positive','Abnormal','Invasive Cancer')\n" +
                "and s.screening_type = 'Rescreening';";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("rescreenedCXCAPositiveSql");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive women on ART with Positive cervical cancer results during re-screening");
        return cd;
    }

    /**
     *
     * @return
     */
    public CohortDefinition rescreenedCXCASCRNPresumedSql() {

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) " +
                "and  coalesce(s.via_vili_screening_result,s.hpv_screening_result,s.pap_smear_screening_result,s.colposcopy_screening_result) in ('Suspicious for cancer','Low grade lesion','High grade lesion','Presumed Cancer')\n" +
                "and s.screening_type = 'Rescreening';";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("rescreenedCXCAPresumed");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive women on ART with Presumed cervical cancer during re-screening");
        return cd;

    }

    /**
     *
     * @return
     */
    public CohortDefinition postTreatmentCXCASCRNNegativeSql() {

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) " +
                "and  coalesce(s.via_vili_screening_result,s.hpv_screening_result,s.pap_smear_screening_result,s.colposcopy_screening_result) in ('Negative','Normal')\n" +
                "and s.screening_type = 'Post treatment followup';";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("postTreatmentCXCANegativeSql");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive women on ART with Negative cervical cancer results after Cervix Cancer treatment");
        return cd;

    }

    /**
     *
     * @return
     */
    public CohortDefinition postTreatmentCXCASCRNPositiveSql() {

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) " +
                "and  coalesce(s.via_vili_screening_result,s.hpv_screening_result,s.pap_smear_screening_result,s.colposcopy_screening_result) in ('Positive','Abnormal','Invasive Cancer')\n" +
                "and s.screening_type = 'Post treatment followup';";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("postTreatmentCXCAPositiveSql");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive women on ART with Positive cervical cancer results after Cervix Cancer treatment");
        return cd;
    }

    /**
     *
     * @return
     */
    public CohortDefinition postTreatmentCXCASCRNPresumedSql() {

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) " +
                "and  coalesce(s.via_vili_screening_result,s.hpv_screening_result,s.pap_smear_screening_result,s.colposcopy_screening_result) in ('Suspicious for cancer','Low grade lesion','High grade lesion','Presumed Cancer')\n" +
                "and s.screening_type = 'Post treatment followup';";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("postTreatmentCXCAPresumedSql");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive women on ART with Presumed cervical cancer after Cervix Cancer treatment");
        return cd;

    }

    /**
     *  Number of Infants tested by 12 months of age and results returned
     * @return
     */
    public CohortDefinition infantsTestedAndResultsReturned() {
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_hei_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics d on e.patient_id = d.patient_id\n" +
                "         inner join (select x.patient_id,\n" +
                "                            mid(max(concat(date(x.visit_date), date(x.date_test_requested))), 11)       as sample_date,\n" +
                "                            mid(max(concat(date(x.visit_date), date(x.date_test_result_received))), 11) as results_Date,\n" +
                "                            mid(max(concat(date(x.visit_date), x.test_result)), 11)                     as test_results\n" +
                "                     from kenyaemr_etl.etl_laboratory_extract x\n" +
                "                     where x.lab_test = 1030\n" +
                "                       and x.order_reason in (1040, 1326, 844)\n" +
                "                       and x.date_test_result_received between date(:startDate) and date(:endDate)\n" +
                "                       and x.test_result in (703, 664)\n" +
                "                     group by x.patient_id) x on e.patient_id = x.patient_id\n" +
                "where timestampdiff(DAY, d.DOB, x.sample_date) <= 365;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("infantsTestedAndResultsReturned");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV-exposed infants with a virologic HIV test result returned in the reporting period, whose diagnostic sample was collected by 12 months of age");
        return cd;
    }

    /**
     * HIV-exposed infants with a virologic Negative HIV test result returned in the reporting period, whose diagnostic sample was collected by 2 months of age
     * @return
     */
    public CohortDefinition infantsTestedNegativeby2MonthsOfAge() {
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_hei_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics d on e.patient_id = d.patient_id\n" +
                "         inner join (select x.patient_id,\n" +
                "                            mid(max(concat(date(x.visit_date), date(x.date_test_requested))), 11)       as sample_date,\n" +
                "                            mid(max(concat(date(x.visit_date), date(x.date_test_result_received))), 11) as results_Date,\n" +
                "                            mid(max(concat(date(x.visit_date), x.test_result)), 11)                     as test_results\n" +
                "                     from kenyaemr_etl.etl_laboratory_extract x\n" +
                "                     where x.lab_test = 1030\n" +
                "                       and x.order_reason in (1040, 1326, 844)\n" +
                "                       and x.date_test_result_received between date(:startDate) and date(:endDate)\n" +
                "                       and x.test_result = 664\n" +
                "                     group by x.patient_id) x on e.patient_id = x.patient_id\n" +
                "where timestampdiff(DAY, d.DOB, x.sample_date) <= 60;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("infantsTestedNegativeby2MonthsOfAge");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV-exposed infants with a virologic Negative HIV test result returned in the reporting period, whose diagnostic sample was collected by 2 months of age");
        return cd;
    }

    /**
     *HIV-exposed infants with a virologic Negative HIV test result returned in the reporting period, whose diagnostic sample was collected at 3-12 months of age
     * @return
     */
    public CohortDefinition infantsTestedNegativeby3To12MonthsOfAge() {
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_hei_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics d on e.patient_id = d.patient_id\n" +
                "         inner join (select x.patient_id,\n" +
                "                            mid(max(concat(date(x.visit_date), date(x.date_test_requested))), 11)       as sample_date,\n" +
                "                            mid(max(concat(date(x.visit_date), date(x.date_test_result_received))), 11) as results_Date,\n" +
                "                            mid(max(concat(date(x.visit_date), x.test_result)), 11)                     as test_results\n" +
                "                     from kenyaemr_etl.etl_laboratory_extract x\n" +
                "                     where x.lab_test = 1030\n" +
                "                       and x.order_reason in (1040, 1326, 844)\n" +
                "                       and x.date_test_result_received between date(:startDate) and date(:endDate)\n" +
                "                       and x.test_result = 664\n" +
                "                     group by x.patient_id) x on e.patient_id = x.patient_id\n" +
                "where timestampdiff(DAY, d.DOB, x.sample_date) between 61 and 365;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("infantsTestedNegativeby3To12MonthsOfAge");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV-exposed infants with a virologic Negative HIV test result returned in the reporting period, whose diagnostic sample was collected by 3- 12 months of age");
        return cd;
    }

    /**
     * HIV-exposed infants with a virologic Positive HIV test result returned in the reporting period, whose diagnostic sample was collected by 2 months of age
     * @return
     */
    public CohortDefinition infantsTestedPositiveby2MonthsOfAge() {
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_hei_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics d on e.patient_id = d.patient_id\n" +
                "         inner join (select x.patient_id,\n" +
                "                            mid(max(concat(date(x.visit_date), date(x.date_test_requested))), 11)       as sample_date,\n" +
                "                            mid(max(concat(date(x.visit_date), date(x.date_test_result_received))), 11) as results_Date,\n" +
                "                            mid(max(concat(date(x.visit_date), x.test_result)), 11)                     as test_results\n" +
                "                     from kenyaemr_etl.etl_laboratory_extract x\n" +
                "                     where x.lab_test = 1030\n" +
                "                       and x.order_reason in (1040, 1326, 844)\n" +
                "                       and x.date_test_result_received between date(:startDate) and date(:endDate)\n" +
                "                       and x.test_result = 703\n" +
                "                     group by x.patient_id) x on e.patient_id = x.patient_id\n" +
                "where timestampdiff(DAY, d.DOB, x.sample_date) <= 60;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("infantsTestedPositiveby2MonthsOfAge");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV-exposed infants with a virologic Positive HIV test result returned in the reporting period, whose diagnostic sample was collected by 2 months of age");
        return cd;
    }

    /**
     * HIV-exposed infants with a virologic Positive HIV test result returned in the reporting period, whose diagnostic sample was collected at 3-12 months of age
     * @return
     */
    public CohortDefinition infantsTestedPositiveby3To12MonthsOfAge() {
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_hei_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics d on e.patient_id = d.patient_id\n" +
                "         inner join (select x.patient_id,\n" +
                "                            mid(max(concat(date(x.visit_date), date(x.date_test_requested))), 11)       as sample_date,\n" +
                "                            mid(max(concat(date(x.visit_date), date(x.date_test_result_received))), 11) as results_Date,\n" +
                "                            mid(max(concat(date(x.visit_date), x.test_result)), 11)                     as test_results\n" +
                "                     from kenyaemr_etl.etl_laboratory_extract x\n" +
                "                     where x.lab_test = 1030\n" +
                "                       and x.order_reason in (1040, 1326, 844)\n" +
                "                       and x.date_test_result_received between date(:startDate) and date(:endDate)\n" +
                "                       and x.test_result = 703\n" +
                "                     group by x.patient_id) x on e.patient_id = x.patient_id\n" +
                "where timestampdiff(DAY, d.DOB, x.sample_date) between 61 and 365;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("infantsTestedPositiveby3To12MonthsOfAge");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV-exposed infants with a virologic Positive HIV test result returned in the reporting period, whose diagnostic sample was collected at 3-12 months of age");
        return cd;
    }

    /**
     * HIV Positive started ART in the reporting period, whose diagnostic sample was collected by 2 months of age.
     * @return
     */
    public CohortDefinition infantsInitiatedARTTestedPositiveby2MonthsOfAge() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("startedOnART", ReportUtils.map(startedOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("infantsTestedPositiveby2MonthsOfAge", ReportUtils.map(infantsTestedPositiveby2MonthsOfAge(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("startedOnART AND infantsTestedPositiveby2MonthsOfAge");
        return cd;
    }

    /**
     * HIV Positive started ART in the reporting period, whose diagnostic sample was collected at 3-12 months of age.
     * @return
     */
    public CohortDefinition infantsInitiatedARTTestedPositiveby3To12MonthsOfAge() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("startedOnART", ReportUtils.map(startedOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("infantsTestedPositiveby3To12MonthsOfAge", ReportUtils.map(infantsTestedPositiveby3To12MonthsOfAge(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("startedOnART AND infantsTestedPositiveby3To12MonthsOfAge");
        return cd;
    }
    /**
     *Screened negative for CXCA for the first time
     * @return
     */
    public CohortDefinition firstTimeCXCASCRNNegative() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("womenEnrolledInHIVProgram", ReportUtils.map(womenEnrolledInHIVProgram(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("firstTimeScreenedCXCASCRNNegativeSql", ReportUtils.map(firstTimeScreenedCXCASCRNNegativeSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND womenEnrolledInHIVProgram AND firstTimeScreenedCXCASCRNNegativeSql");
        return cd;
    }

    /**
     *Screened positive for CXCA for the first time
     * @return
     */
    public CohortDefinition firstTimeCXCASCRNPositive() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("womenEnrolledInHIVProgram", ReportUtils.map(womenEnrolledInHIVProgram(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("firstTimeScreenedCXCASCRNPositiveSql", ReportUtils.map(firstTimeScreenedCXCASCRNPositiveSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND womenEnrolledInHIVProgram AND firstTimeScreenedCXCASCRNPositiveSql");
        return cd;
    }

    /**
     *Screened for CXCA for the first time with presumed or suspected result
     * @return
     */
    public CohortDefinition firstTimeCXCASCRNPresumed() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("womenEnrolledInHIVProgram", ReportUtils.map(womenEnrolledInHIVProgram(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("firstTimeScreenedCXCASCRNPresumedSql", ReportUtils.map(firstTimeScreenedCXCASCRNPresumedSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND womenEnrolledInHIVProgram AND firstTimeScreenedCXCASCRNPresumedSql");
        return cd;
    }

    /**
     * Re-screened negative for CXCA
     * @return
     */
    public CohortDefinition rescreenedCXCASCRNNegative() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("womenEnrolledInHIVProgram", ReportUtils.map(womenEnrolledInHIVProgram(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("rescreenedCXCASCRNNegativeSql", ReportUtils.map(rescreenedCXCASCRNNegativeSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND womenEnrolledInHIVProgram AND rescreenedCXCASCRNNegativeSql");
        return cd;
    }

    /**
     *Re-screened positive for CXCA
     * @return
     */
    public CohortDefinition rescreenedCXCASCRNPositive() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("womenEnrolledInHIVProgram", ReportUtils.map(womenEnrolledInHIVProgram(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("rescreenedCXCASCRNPositiveSql", ReportUtils.map(rescreenedCXCASCRNPositiveSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND womenEnrolledInHIVProgram AND rescreenedCXCASCRNPositiveSql");
        return cd;
    }

    /**
     * Re-screened for CXCA with presumed/suspected result
     * @return
     */
    public CohortDefinition rescreenedCXCASCRNPresumed() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("womenEnrolledInHIVProgram", ReportUtils.map(womenEnrolledInHIVProgram(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("rescreenedCXCASCRNPresumedSql", ReportUtils.map(rescreenedCXCASCRNPresumedSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND womenEnrolledInHIVProgram AND rescreenedCXCASCRNPresumedSql");
        return cd;
    }

    /**
     * Post treatment CXCA screening with a negative result
     */
    public CohortDefinition postTreatmentCXCASCRNNegative() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("womenEnrolledInHIVProgram", ReportUtils.map(womenEnrolledInHIVProgram(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("postTreatmentCXCASCRNNegativeSql", ReportUtils.map(postTreatmentCXCASCRNNegativeSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND womenEnrolledInHIVProgram AND postTreatmentCXCASCRNNegativeSql");
        return cd;
    }

    /**
     * Post treatment CXCA screening with a positive result
     * @return
     */
    public CohortDefinition postTreatmentCXCASCRNPositive() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("womenEnrolledInHIVProgram", ReportUtils.map(womenEnrolledInHIVProgram(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("postTreatmentCXCASCRNPositiveSql", ReportUtils.map(postTreatmentCXCASCRNPositiveSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND womenEnrolledInHIVProgram AND postTreatmentCXCASCRNPositiveSql");
        return cd;
    }

    /**
     * Post treatment CXCA TX with presumed result
     * @return
     */
    public CohortDefinition postTreatmentCXCASCRNPresumed() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("womenEnrolledInHIVProgram", ReportUtils.map(womenEnrolledInHIVProgram(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("postTreatmentCXCASCRNPresumedSql", ReportUtils.map(postTreatmentCXCASCRNPresumedSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND womenEnrolledInHIVProgram AND postTreatmentCXCASCRNPresumedSql");
        return cd;
    }

    /**
     * CXCA_TX First screening Cryotherapy
     * @return
     */
    public CohortDefinition firstScreeningCXCATXCryotherapySql() {

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) " +
                "and coalesce(s.via_vili_treatment_method,s.pap_smear_treatment_method,s.hpv_treatment_method,s.colposcopy_treatment_method) in ('Cryotherapy postponed', 'Cryotherapy performed (single Visit)', 'Cryotherapy performed', 'Cryotherapy performed (SVA)', 'Cryotherapy performed (previously postponed)')\n" +
                "and s.screening_type = 'First time screening';";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("firstScreeningCXCATXCryotherapySql");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("CXCA_TX Fisrt screening Cryotherapy");
        return cd;

    }

    /**
     * CXCA_TX Fisrt screening Thermocoagulation
     * @return
     */
    public CohortDefinition firstScreeningCXCATXThermocoagulationSql() {

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) " +
                "and coalesce(s.via_vili_treatment_method,s.pap_smear_treatment_method,s.hpv_treatment_method,s.colposcopy_treatment_method) in ('Thermocoagulation','Thermal ablation performed (SVA)','Thermal ablation')\n" +
                "and s.screening_type = 'First time screening';";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("firstScreeningCXCATXThermocoagulationSql");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("CXCA_TX Fisrt screening Thermocoagulation");
        return cd;

    }

    /**
     * CXCA_TX Fisrt screening LEEP
     * @return
     */
    public CohortDefinition firstScreeningCXCATXLEEPSql() {

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) " +
                "and coalesce(s.via_vili_treatment_method,s.pap_smear_treatment_method,s.hpv_treatment_method,s.colposcopy_treatment_method) in ('LEEP','LEEP performed')\n" +
                "and s.screening_type = 'First time screening';";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("firstScreeningCXCATXLEEPSql");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("CXCA_TX Fisrt screening LEEP");
        return cd;

    }

    /**
     * CXCA_TX rescreened after first screening negative treated with Cryotherapy
     * @return
     */
    public CohortDefinition rescreenedCXCATXCryotherapySql() {

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) " +
                "and coalesce(s.via_vili_treatment_method,s.pap_smear_treatment_method,s.hpv_treatment_method,s.colposcopy_treatment_method) in ('Cryotherapy postponed', 'Cryotherapy performed (single Visit)', 'Cryotherapy performed', 'Cryotherapy performed (SVA)', 'Cryotherapy performed (previously postponed)')\n" +
                "      and s.screening_type = 'Rescreening';";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("rescreenedCXCATXCryotherapySql");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("CXCA_TX rescreened after first screening negative treated with Cryotherapy");
        return cd;

    }

    /**
     * CXCA_TX rescreened after first screening negative treated with Thermocoagulation
     * @return
     */
    public CohortDefinition rescreenedCXCATXThermoSql() {

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate)" +
                "and coalesce(s.via_vili_treatment_method,s.pap_smear_treatment_method,s.hpv_treatment_method,s.colposcopy_treatment_method) in ('Thermocoagulation','Thermal ablation performed (SVA)','Thermal ablation')\n" +
                "and s.screening_type = 'Rescreening';";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("rescreenedCXCATXThermoSql");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("CXCA_TX rescreened after first screening negative treated with Thermocoagulation");
        return cd;

    }

    /**
     * CXCA_TX rescreened after first screening negative treated with LEEP
     * @return
     */
    public CohortDefinition rescreenedCXCATXLEEPSql() {

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) " +
                "and coalesce(s.via_vili_treatment_method,s.pap_smear_treatment_method,s.hpv_treatment_method,s.colposcopy_treatment_method) in ('LEEP','LEEP performed')\n" +
                "and s.screening_type = 'Rescreening';";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("rescreenedCXCATXLEEPSql");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("CXCA_TX rescreened after first screening negative treated with LEEP");
        return cd;

    }

    /**
     * CXCA_TX Post TX follow-up treated with Cryotherapy
     * @return
     */
    public CohortDefinition postTxFollowupCXCATxCryotherapySql() {

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) " +
                "and coalesce(s.via_vili_treatment_method,s.pap_smear_treatment_method,s.hpv_treatment_method,s.colposcopy_treatment_method) in ('Cryotherapy postponed', 'Cryotherapy performed (single Visit)', 'Cryotherapy performed', 'Cryotherapy performed (SVA)', 'Cryotherapy performed (previously postponed)')\n" +
                "and s.screening_type = 'Post treatment followup';";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("postTxFollowupCXCATxCryotherapySql");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("CXCA_TX Post TX follow-up treated with Cryotherapy");
        return cd;

    }

    /**
     * CXCA_TX Post TX follow-up with Thermocoagulation
     * @return
     */
    public CohortDefinition postTxFollowupCXCATXThermocoagulationSql() {

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) " +
                "and coalesce(s.via_vili_treatment_method,s.pap_smear_treatment_method,s.hpv_treatment_method,s.colposcopy_treatment_method) in ('Thermocoagulation','Thermal ablation performed (SVA)','Thermal ablation')\n" +
                "and s.screening_type = 'Post treatment followup';";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("postTxFollowupCXCATXThermocoagulationSql");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("CXCA_TX Post TX follow-up with Thermocoagulation");
        return cd;

    }

    /**
     * CXCA_TX Post TX follow-up treated with LEEP
     * @return
     */
    public CohortDefinition postTxFollowupCXCATXLEEPSql() {

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) " +
                "and coalesce(s.via_vili_treatment_method,s.pap_smear_treatment_method,s.hpv_treatment_method,s.colposcopy_treatment_method) in ('LEEP','LEEP performed')\n" +
                "and s.screening_type = 'Post treatment followup';";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("postTxFollowupCXCATXLEEPSql");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("CXCA_TX Post TX follow-up treated with LEEP");
        return cd;

    }

    /**
     * @return
     */
    public CohortDefinition firstScreeningCXCATXCryotherapy() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("firstScreeningCXCATXCryotherapySql", ReportUtils.map(firstScreeningCXCATXCryotherapySql(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("womenEnrolledInHIVProgram", ReportUtils.map(womenEnrolledInHIVProgram(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("firstTimeCXCASCRNPositive", ReportUtils.map(firstTimeCXCASCRNPositive(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND womenEnrolledInHIVProgram AND firstScreeningCXCATXCryotherapySql AND firstTimeCXCASCRNPositive");
        return cd;
    }

    /**
     *
     * @return
     */
    public CohortDefinition firstScreeningCXCATXThermocoagulation() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("firstScreeningCXCATXThermocoagulationSql", ReportUtils.map(firstScreeningCXCATXThermocoagulationSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("womenEnrolledInHIVProgram", ReportUtils.map(womenEnrolledInHIVProgram(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("firstTimeCXCASCRNPositive", ReportUtils.map(firstTimeCXCASCRNPositive(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND womenEnrolledInHIVProgram AND firstScreeningCXCATXThermocoagulationSql AND firstTimeCXCASCRNPositive");
        return cd;
    }

    /**
     *
     * @return
     */
    public CohortDefinition firstScreeningCXCATXLEEP() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("firstScreeningCXCATXLEEPSql", ReportUtils.map(firstScreeningCXCATXLEEPSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("womenEnrolledInHIVProgram", ReportUtils.map(womenEnrolledInHIVProgram(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("firstTimeCXCASCRNPositive", ReportUtils.map(firstTimeCXCASCRNPositive(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND womenEnrolledInHIVProgram AND firstScreeningCXCATXLEEPSql AND firstTimeCXCASCRNPositive");
        return cd;
    }

    /**
     *
     * @return
     */
    public CohortDefinition rescreenedCXCATXCryotherapy() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("rescreenedCXCATXCryotherapySql", ReportUtils.map(rescreenedCXCATXCryotherapySql(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("womenEnrolledInHIVProgram", ReportUtils.map(womenEnrolledInHIVProgram(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND womenEnrolledInHIVProgram AND rescreenedCXCATXCryotherapySql");
        return cd;
    }

    /**
     *
     * @return
     */
    public CohortDefinition rescreenedCXCATXThermocoagulation() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("womenEnrolledInHIVProgram", ReportUtils.map(womenEnrolledInHIVProgram(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("rescreenedCXCATXThermoSql", ReportUtils.map(rescreenedCXCATXThermoSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND womenEnrolledInHIVProgram AND rescreenedCXCATXThermoSql");
        return cd;
    }

    /**
     *
     * @return
     */
    public CohortDefinition rescreenedCXCATXLEEP() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("rescreenedCXCATXLEEPSql", ReportUtils.map(rescreenedCXCATXLEEPSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("womenEnrolledInHIVProgram", ReportUtils.map(womenEnrolledInHIVProgram(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND womenEnrolledInHIVProgram AND rescreenedCXCATXLEEPSql");
        return cd;
    }

    /**
     *
     * @return
     */
    public CohortDefinition postTxFollowupCXCATxCryotherapy() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("postTxFollowupCXCATxCryotherapySql", ReportUtils.map(postTxFollowupCXCATxCryotherapySql(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("womenEnrolledInHIVProgram", ReportUtils.map(womenEnrolledInHIVProgram(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND womenEnrolledInHIVProgram AND postTxFollowupCXCATxCryotherapySql");
        return cd;
    }

    /**
     *
     * @return
     */
    public CohortDefinition postTxFollowupCXCATXThermocoagulation() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("postTxFollowupCXCATXThermocoagulationSql", ReportUtils.map(postTxFollowupCXCATXThermocoagulationSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("womenEnrolledInHIVProgram", ReportUtils.map(womenEnrolledInHIVProgram(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND womenEnrolledInHIVProgram AND postTxFollowupCXCATXThermocoagulationSql");
        return cd;
    }

    /**
     *
     * @return
     */
    public CohortDefinition postTxFollowupCXCATXLEEP() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("postTxFollowupCXCATXLEEPSql", ReportUtils.map(postTxFollowupCXCATXLEEPSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("womenEnrolledInHIVProgram", ReportUtils.map(womenEnrolledInHIVProgram(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND womenEnrolledInHIVProgram AND postTxFollowupCXCATXLEEPSql");
        return cd;
    }

    public CohortDefinition infantFirstVirologicTestWithin2Months() {
        String sqlQuery = "select od.patient_id as patient\n" +
                "from orders od\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics dm on od.patient_id = dm.patient_id\n" +
                "where od.concept_id = 1030\n" +
                "  and od.order_reason in (1040,1326,844)\n" +
                "  and timestampdiff(DAY, dm.DOB, date(od.date_activated)) <= 60\n" +
                "  and substr(date(od.date_activated), 1, 10) between (:startDate) and (:endDate)\n" +
                "  and od.voided = 0\n" +
                "group by dm.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("FirstVirologicSampleTakenWithin2Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infants with 1st Virologic sample taken within 2 months");
        return cd;

    }
    public CohortDefinition infantVirologicTest3To12Months() {
        String sqlQuery = "select od.patient_id as patient\n" +
                "from orders od\n" +
                "         inner join kenyaemr_etl.etl_patient_demographics dm on od.patient_id = dm.patient_id\n" +
                "where od.concept_id = 1030\n" +
                "  and od.order_reason in (1040,1326,844)\n" +
                "  and timestampdiff(DAY, dm.DOB, date(od.date_activated)) between 61 and 365\n" +
                "  and substr(date(od.date_activated), 1, 10) between (:startDate) and (:endDate)\n" +
                "  and od.voided = 0\n" +
                "group by dm.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("infantFirstVirologicTest3To12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infants with Virologic sample taken at 3-12 months");
        return cd;

    }
    public CohortDefinition infantFirstVirologicTest3To12Months() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("infantVirologicTest3To12Months", ReportUtils.map(infantVirologicTest3To12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("infantFirstVirologicTestWithin2Months", ReportUtils.map(infantFirstVirologicTestWithin2Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("infantVirologicTest3To12Months AND NOT infantFirstVirologicTestWithin2Months");
        return cd;

    }
    public CohortDefinition atleast2InfantVirologicTestWithin2Months() {
        String sqlQuery = "select dm.patient_id\n" +
                "from kenyaemr_etl.etl_patient_demographics dm\n" +
                "         left join (select od.patient_id, od.concept_id, od.order_reason\n" +
                "                    from orders od\n" +
                "                             inner join kenyaemr_etl.etl_patient_demographics dm on dm.patient_id = od.patient_id\n" +
                "                    where od.concept_id = 1030\n" +
                "                      and od.order_reason = 1040\n" +
                "                      and timestampdiff(DAY, dm.DOB, date(od.date_activated)) <= 60\n" +
                "                      and substr(date(od.date_activated), 1, 10) between (:startDate) and (:endDate)\n" +
                "                      and od.voided = 0\n" +
                "                    group by dm.patient_id, od.order_reason) od on od.patient_id = dm.patient_id\n" +
                "         left join (select od1.patient_id, od1.concept_id, od1.order_reason\n" +
                "                    from orders od1\n" +
                "                             inner join kenyaemr_etl.etl_patient_demographics dm on dm.patient_id = od1.patient_id\n" +
                "                    where od1.concept_id = 1030\n" +
                "                      and od1.order_reason = 1326\n" +
                "                      and timestampdiff(DAY, dm.DOB, date(od1.date_activated)) <= 60\n" +
                "                      and substr(date(od1.date_activated), 1, 10) between (:startDate) and (:endDate)\n" +
                "                      and od1.voided = 0\n" +
                "                    group by dm.patient_id, od1.order_reason) od1 on od1.patient_id = dm.patient_id\n" +
                "         left join (select od2.patient_id, od2.concept_id, od2.order_reason\n" +
                "                    from orders od2\n" +
                "                             inner join kenyaemr_etl.etl_patient_demographics dm on dm.patient_id = od2.patient_id\n" +
                "                    where od2.concept_id = 1030\n" +
                "                      and od2.order_reason = 844\n" +
                "                      and timestampdiff(DAY, dm.DOB, date(od2.date_activated)) <= 60\n" +
                "                      and substr(date(od2.date_activated), 1, 10) between (:startDate) and (:endDate)\n" +
                "                      and od2.voided = 0\n" +
                "                    group by dm.patient_id, od2.order_reason) od2 on od2.patient_id = dm.patient_id\n" +
                "where (od.patient_id is not null\n" +
                "    and (od1.patient_id is not null or od2.patient_id is not null))\n" +
                "   or (od1.patient_id is not null and (od.patient_id is not null or od2.patient_id is not null))\n" +
                "   or (od2.patient_id is not null and (od.patient_id is not null or od1.patient_id is not null))\n" +
                " group by dm.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("atleast2InfantVirologicTestWithin2Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infants with atleast 2 Virologic samples taken within 2 months");
        return cd;
    }

    public CohortDefinition atleast2InfantVirologicTestsAt3To12Months() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("infantFirstVirologicTestWithin2Months", ReportUtils.map(infantFirstVirologicTestWithin2Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("atleast2InfantVirologicTestWithin2Months", ReportUtils.map(atleast2InfantVirologicTestWithin2Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("infantFirstVirologicTestWithin2Months OR ");
        return cd;
    }

    public CohortDefinition firstInfantVirologicTestsAt12Months() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("infantFirstVirologicTestWithin2Months", ReportUtils.map(infantFirstVirologicTestWithin2Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("infantFirstVirologicTest3To12Months", ReportUtils.map(infantFirstVirologicTest3To12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(infantFirstVirologicTestWithin2Months OR infantFirstVirologicTest3To12Months");
        return cd;
    }
    public CohortDefinition alreadyOnARTAtBeginningOfPregnacy() {

        String sqlQuery = "select t.patient_id from(\n" +
                "                        select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                               max(fup.visit_date) as latest_vis_date,\n" +
                "                               mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                               max(d.visit_date) as date_discontinued,\n" +
                "                               d.patient_id as disc_patient,\n" +
                "                               de.patient_id as started_on_drugs,\n" +
                "                               de.date_started as date_started_drugs\n" +
                "                        from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                               join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                               join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                               left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV'\n" +
                "                               left outer JOIN\n" +
                "                                 (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                  where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                  group by patient_id\n" +
                "                                 ) d on d.patient_id = fup.patient_id\n" +
                "                        where fup.visit_date <= date(:endDate)\n" +
                "                        group by patient_id\n" +
                "                        having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "                            ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) ))\n" +
                "                            )\n" +
                "                        ) t\n" +
                "                          join (select av.patient_id,max(av.visit_date) ltst_anc, min(av.visit_date) 1st_anc\n" +
                "                                from kenyaemr_etl.etl_mch_antenatal_visit av\n" +
                "                                       inner join kenyaemr_etl.etl_mch_enrollment mch on av.patient_id = mch.patient_id and mch.date_of_discontinuation is null\n" +
                "                                    where av.visit_date between date_sub(date(:endDate), INTERVAL 3 MONTH ) and date(:endDate)\n" +
                "                               group by av.patient_id)anc\n" +
                "                                on anc.patient_id = t.patient_id and t.date_started_drugs < anc.1st_anc;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("alreadyOnARTBeforePregancy");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mothers Already on ART at the start of current Pregnancy");
        return cd;

    }

    /**
     * Patients started TB within the last 12 months
     * @return
     */
    public CohortDefinition startedTBTxLast12months() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e\n" +
                "    left join\n" +
                "    (select tb.patient_id from kenyaemr_etl.etl_tb_enrollment tb where tb.visit_date between date_sub(date(:endDate),INTERVAL 12 MONTH) and date(:endDate))tb on e.patient_id = tb.patient_id\n" +
                "    left join (select s.patient_id from kenyaemr_etl.etl_tb_screening s where s.resulting_tb_status in (1662,142177) and s.started_anti_TB = 1065 and\n" +
                "    s.visit_date between date_sub(date(:endDate),INTERVAL 12 MONTH) and date(:endDate) group by s.patient_id) s on e.patient_id = s.patient_id\n" +
                "    where tb.patient_id is not null or s.patient_id is not null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("startedTBTxLast12months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients started TB tx within the last 12 months");
        return cd;

    }

    /**
     * Patients tested Positive for TB and started treatment within the last 6 months
     * @return
     */
    public CohortDefinition startedTBTxLast6Months() {
        String sqlQuery = "select tb.patient_id from kenyaemr_etl.etl_tb_screening tb where tb.resulting_tb_status in(1662,142177) and tb.started_anti_TB = 1065 and\n" +
                "            (tb.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) or tb.tb_treatment_start_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate)) group by tb.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("startedTBTxLast6mONTHS");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("TB patients new last 6 months");
        return cd;

    }

    /**
     * Patients screened Positive for TB within the last 6 months
     * @return
     */
    public CohortDefinition screenedTBPositiveLast6Months() {
        String sqlQuery = "select tb.patient_id from kenyaemr_etl.etl_tb_screening tb where tb.resulting_tb_status = 1662 and\n" +
                "            tb.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) group by tb.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("screenedTBPositiveLast6Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients screened TB positive last 6 months");
        return cd;

    }

    /**
     * Patients screened Negative for TB within the last 6 months
     * @return
     */
    public CohortDefinition screenedTBNegativeLast6Months() {
        String sqlQuery = "select tb.patient_id from kenyaemr_etl.etl_tb_screening tb where tb.resulting_tb_status = 1660 and\n" +
                "            tb.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) group by tb.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("screenedTBNegativeLast6Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients screened TB Negative last 6 months");
        return cd;

    }

    /**
     * Patients screened TB and specimen sent for bacteriologic diagnosis
     * @return
     */
    public CohortDefinition specimenSentForTBDiagnosis() {
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_tb_screening s\n" +
                "where (s.genexpert_ordered = 162202 or s.spatum_smear_ordered = 307)\n" +
                "and s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate) group by s.patient_id\n" +
                "union all\n" +
                "select x.patient_id from kenyaemr_etl.etl_laboratory_extract x where x.lab_test in (307,1465,162202)\n" +
                "and x.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate) group by x.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("specimenSentForTBBacteriologicTests");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients screened TB and specimen sent for bacteriologic diagnosis");
        return cd;
    }

    /**
     * Patients screened TB and GeneXpert specimen sent for bacteriologic diagnosis
     * @return
     */
    public CohortDefinition geneXpertSpecimenSentForTBDiagnosis() {
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_tb_screening s\n" +
                "where s.genexpert_ordered = 162202\n" +
                "and s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate) group by s.patient_id\n" +
                "union all\n" +
                "select patient_id from kenyaemr_etl.etl_laboratory_extract x where x.lab_test=162202\n" +
                "and x.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate) group by x.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("geneXpertSpecimenSentForTBBacteriologicTests");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients screened TB and GeneXpert specimen sent for bacteriologic diagnosis");
        return cd;
    }

    /**
     *Patients screened for TB and smear microscopy tests done
     * @return
     */
    public CohortDefinition smearMicroscopySpecimenSentForTBDiagnosis() {
        String sqlQuery = "select a.patient_id from (select patient_id from kenyaemr_etl.etl_tb_screening s\n" +
                "where (s.spatum_smear_ordered = 307 and ifnull(s.genexpert_ordered,0) !=162202 and ifnull(s.chest_xray_ordered,0) !=12)\n" +
                "and s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) group by s.patient_id\n" +
                "union all\n" +
                "select patient_id from kenyaemr_etl.etl_laboratory_extract x where x.lab_test =307\n" +
                "and x.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate))a;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("smearMicroscopySpecimenSentForTBDiagnosis");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients screened for TB and smear microscopy tests done for bacteriologic diagnosis");
        return cd;
    }

    /**
     * Patients screened for TB using chest X-Ray
     * @return
     */
    public CohortDefinition chestXray() {
        String sqlQuery = "\n" +
                "select patient_id\n" +
                "from kenyaemr_etl.etl_tb_screening s\n" +
                "where s.chest_xray_ordered = 12\n" +
                "  and s.visit_date between date_sub(date(:endDate), INTERVAL 6 MONTH) and date(:endDate)\n" +
                "group by s.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("chestXray");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients screened for TB and chest X-Ray diagnosis done");
        return cd;
    }
    /**
     * Patients screened TB and additional tests for TB done other than GeneXpert
     * @return
     */
    public CohortDefinition additionalTestsForTBDiagnosis() {
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_laboratory_extract x where x.lab_test=167459\n" +
                "and x.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("additionalTestsForTBDiagnosis");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients screened for TB and other tests for TB diagnosis done");
        return cd;
    }

    /**
     * Patients screened for TB with specimen collected and result returned
     * @return
     */
    public CohortDefinition tbResultReturned() {
        String sqlQuery = "select patient_id from kenyaemr_etl.etl_tb_screening s\n" +
                "    where (s.genexpert_result in(162203,162204,164104) or s.spatum_smear_result = 703)\n" +
                "    and s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate)\n" +
                "    union all\n" +
                "    select patient_id from kenyaemr_etl.etl_laboratory_extract x where x.lab_test in (162202,1465,307) and x.test_result in (162203,162204,162104,703,1362,1363,1364)\n" +
                "    and x.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("tbResultReturned");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients screened for TB with specimen collected and result returned");
        return cd;
    }

    /**
     *Auto-Calculate Number of TB cases with documented HIV-positive status who start or continue ART during the reporting period.
     * TB_ART_NEW-ON_ART
     */
    public CohortDefinition newOnARTTBInfected() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("startedTBTxLast12months", ReportUtils.map(startedTBTxLast12months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("newlyStartedOnART", ReportUtils.map(startedOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("startedTBTxLast12months AND newlyStartedOnART");
        return cd;

    }

    /**
     *  Auto-Calculate Number of TB cases with documented HIV-positive status who start or continue ART during the reporting period.
     * TB_ART_ALREADY-ON_ART
     */
    public CohortDefinition alreadyOnARTTBInfected() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("startedTBTxLast12months", ReportUtils.map(startedTBTxLast12months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("previouslyOnART", ReportUtils.map(previouslyOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("startedTBTxLast12months AND previouslyOnART");
        return cd;
    }

    /**
     *  Starting TB treatment within the last 6 months and newly on ART
     */
    public CohortDefinition startingTBTreatmentNewOnART() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("startedTBTxLast6Months", ReportUtils.map(startedTBTxLast6Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("newlyOnART", ReportUtils.map(startedOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("startedTBTxLast6Months AND newlyOnART");
        return cd;
    }

    /**
     *  Starting TB treatment within the last 6 months and previously on ART
     */
    public CohortDefinition startingTBTreatmentPrevOnART() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("startedTBTxLast6Months", ReportUtils.map(startedTBTxLast6Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("previouslyOnART", ReportUtils.map(previouslyOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("startedTBTxLast6Months AND previouslyOnART");
        return cd;
    }

    /**
     *  New on ART Screened TB Positive
     */
    public CohortDefinition newOnARTScreenedPositive() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("screenedTBPositiveLast6Months", ReportUtils.map(screenedTBPositiveLast6Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("newOnART", ReportUtils.map(startedOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("screenedTBPositiveLast6Months AND newOnART");
        return cd;
    }

    /**
     *  Previously on ART Screened TB Positive
     */
    public CohortDefinition prevOnARTScreenedPositive() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("screenedTBPositiveLast6Months", ReportUtils.map(screenedTBPositiveLast6Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("previouslyOnART", ReportUtils.map(previouslyOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentlyOnArt", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("currentlyOnArt AND screenedTBPositiveLast6Months AND previouslyOnART");
        return cd;

    }

    /**
     *  New on ART Screened Negative for TB
     */
    public CohortDefinition newOnARTScreenedNegative() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("screenedTBNegativeLast6Months", ReportUtils.map(screenedTBNegativeLast6Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("startedOnART", ReportUtils.map(startedOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("screenedTBNegativeLast6Months AND startedOnART");
        return cd;
    }

    /**
     *  Previously on ART Screened Negative for TB
     */
    public CohortDefinition prevOnARTScreenedNegative() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("screenedTBNegativeLast6Months", ReportUtils.map(screenedTBNegativeLast6Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("previouslyOnART", ReportUtils.map(previouslyOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentlyOnArt", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("currentlyOnArt AND screenedTBNegativeLast6Months AND previouslyOnART");
        return cd;
    }

    /**
     *  Specimen sent for bacteriologic diagnosis of active TB for TX_CURR patients
     */
    public CohortDefinition specimenSent() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("specimenSentForTBDiagnosis", ReportUtils.map(specimenSentForTBDiagnosis(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentlyOnArt", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("specimenSentForTBDiagnosis AND currentlyOnArt");
        return cd;
    }

    /**
     *  GeneXpert MTB/RIF assay (with or without other testing for TX_CURR patients
     */
    public CohortDefinition geneXpertMTBRIF() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("geneXpertSpecimenSentForTBDiagnosis", ReportUtils.map(geneXpertSpecimenSentForTBDiagnosis(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentlyOnArt", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("geneXpertSpecimenSentForTBDiagnosis AND currentlyOnArt");
        return cd;
    }

    /**
     *  Smear microscopy only for TX_CURR patients
     */
    public CohortDefinition smearMicroscopy() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("smearMicroscopySpecimenSentForTBDiagnosis", ReportUtils.map(smearMicroscopySpecimenSentForTBDiagnosis(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentlyOnArt", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("smearMicroscopySpecimenSentForTBDiagnosis AND currentlyOnArt");
        return cd;
    }

    /**
     * Chest xRay for TX_CURR patients
     * @return
     */
    public CohortDefinition onARTChestXrayDone() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("chestXray", ReportUtils.map(chestXray(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentlyOnArt", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("chestXray AND currentlyOnArt");
        return cd;
    }
    /**
     *  Additional test other than GeneXpert for TX_CURR patients
     */
    public CohortDefinition additionalTBTests() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("additionalTestsForTBDiagnosis", ReportUtils.map(additionalTestsForTBDiagnosis(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentlyOnArt", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("additionalTestsForTBDiagnosis AND currentlyOnArt");
        return cd;
    }

    /**
     *  Starting TB treatment previously on ART for TX_CURR
     */
    public CohortDefinition resultsReturned() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("tbResultReturned", ReportUtils.map(tbResultReturned(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentlyOnArt", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("tbResultReturned AND currentlyOnArt");
        return cd;
    }

    //TODO enroolnemt date > art date : Done
    public CohortDefinition newOnARTDuringPregnancy() {

        String sqlQuery = "select t.patient_id from(\n" +
                "                        select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                               max(fup.visit_date) as latest_vis_date,\n" +
                "                               mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                               max(d.visit_date) as date_discontinued,\n" +
                "                               d.patient_id as disc_patient,\n" +
                "                               de.patient_id as started_on_drugs,\n" +
                "                               de.date_started as date_started_drugs\n" +
                "                        from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                               join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                               join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                               left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV'\n" +
                "                               left outer JOIN\n" +
                "                                 (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                  where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                  group by patient_id\n" +
                "                                 ) d on d.patient_id = fup.patient_id\n" +
                "                        where fup.visit_date <= date(:endDate)\n" +
                "                        group by patient_id\n" +
                "                        having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "                            ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) ))\n" +
                "                            )\n" +
                "                        ) t\n" +
                "                          join (select av.patient_id,max(av.visit_date) ltst_anc, min(av.visit_date) 1st_anc\n" +
                "                                from kenyaemr_etl.etl_mch_antenatal_visit av\n" +
                "                                       inner join kenyaemr_etl.etl_mch_enrollment mch on av.patient_id = mch.patient_id and mch.date_of_discontinuation is null\n" +
                "                                where av.visit_date between date_sub(date(:endDate), INTERVAL 3 MONTH ) and date(:endDate)\n" +
                "                                group by av.patient_id)anc\n" +
                "                            on anc.patient_id = t.patient_id and t.date_started_drugs between anc.1st_anc and ltst_anc;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("newOnARTDuringPregnancy");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mothers new on ART during current pregnancy");
        return cd;
//TODO: de.visitdate =>last(enrollment.visitdate), left join on delivery + postnatal  dates which should be greater than art date : Done
    }

    /**
     * Infants tested HIV Negative < 5 for Pediatrics and malnutrition clinics
     * @return
     */
    public CohortDefinition initialNegativeHIVTestResultInfants() {
        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts\n" +
                "    inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = hts.patient_id\n" +
                "    where hts.test_type =1 and hts.final_test_result ='Negative' and hts.patient_given_result ='Yes'\n" +
                "    and timestampdiff(year,d.DOB,date(:endDate))<5\n" +
                "    and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_Malnutrition_Negative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Negative Malnutrition Clinics");
        return cd;

    }

    /**
     * Infants tested HIV Positive < 5 for Pediatrics and malnutrition clinics
     * @return
     */
    public CohortDefinition positiveHIVTestResultInfants() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts\n" +
                "    inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = hts.patient_id\n" +
                "    where hts.test_type =2 and hts.final_test_result ='Positive' and hts.patient_given_result ='Yes'\n" +
                "    and timestampdiff(year,d.DOB,date(:endDate))<5\n" +
                "    and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_Malnutrition_Positive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive Malnutrition Clinics");
        return cd;
    }

    public CohortDefinition pwidTestedPositive() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Positive\"\n" +
                "       and hts.patient_given_result =\"Yes\"\n" +
                "       and hts.key_population_type =105\n" +
                "       and hts.test_type =1\n" +
                "       and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_KP_PWID_POS");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("PWID Tested Positive");
        return cd;
    }

    public CohortDefinition pwidTestedNegative() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Negative\"\n" +
                "       and hts.patient_given_result =\"Yes\"\n" +
                "       and hts.key_population_type =105\n" +
                "       and hts.test_type =1\n" +
                "       and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_KP_PWID_NEG");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("PWID Tested Negative");
        return cd;
    }

    public CohortDefinition msmTestedPositive() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Positive\"\n" +
                "       and hts.patient_given_result =\"Yes\"\n" +
                "       and hts.key_population_type =160578\n" +
                "       and hts.test_type =1\n" +
                "       and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_KP_MSM_POS");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("MSM Tested Positive");
        return cd;
    }

    public CohortDefinition msmTestedNegative() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Negative\"\n" +
                "       and hts.patient_given_result =\"Yes\"\n" +
                "       and hts.key_population_type =160578\n" +
                "       and hts.test_type =1\n" +
                "       and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_KP_MSM_NEG");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("MSM Tested Negative");
        return cd;
    }

    public CohortDefinition fswTestedPositive() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Positive\"\n" +
                "       and hts.patient_given_result =\"Yes\"\n" +
                "       and hts.key_population_type =160579\n" +
                "       and hts.test_type =1\n" +
                "       and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_KP_FSW_POS");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("MSM Tested Positive");
        return cd;
    }

    public CohortDefinition fswTestedNegative() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Negative\"\n" +
                "       and hts.patient_given_result =\"Yes\"\n" +
                "       and hts.key_population_type =160579\n" +
                "       and hts.test_type =1\n" +
                "       and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_KP_FSW_NEG");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("FSW Tested Negative");
        return cd;
    }

    public CohortDefinition tgTestedNegative() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Negative\"\n" +
                "       and hts.patient_given_result =\"Yes\"\n" +
                "       and hts.key_population_type =5622\n" +
                "       and hts.test_type =1\n" +
                "       and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_KP_TG_NEG");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("TG Tested Negative");
        return cd;
    }

    public CohortDefinition tgTestedPositive() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Positive\"\n" +
                "       and hts.patient_given_result =\"Yes\"\n" +
                "       and hts.key_population_type =5622\n" +
                "       and hts.test_type =1\n" +
                "       and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_KP_TG_POS");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("TG Tested Positive");
        return cd;
    }

    public CohortDefinition prisonersTestedNegative() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Negative\"\n" +
                "       and hts.patient_given_result =\"Yes\"\n" +
                "       and hts.key_population_type =162277\n" +
                "       and hts.test_type =1\n" +
                "       and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_KP_PRISONERS_NEG");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("PRISONERS Tested Negative");
        return cd;
    }

    public CohortDefinition prisonersTestedPositive() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Positive\"\n" +
                "       and hts.patient_given_result =\"Yes\"\n" +
                "       and hts.key_population_type =162277\n" +
                "       and hts.test_type =1\n" +
                "       and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_KP_PRISONERS_POS");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("PRISONERS Tested Positive");
        return cd;
    }

    /**
     *HTS TEST Compositions by Entry Points
     * PMTCT which is a combination of PMTCT ANC,PMTCT MAT,PMTCT PNC
     * Compositions for HTS_TEST excluding pmtct
     *
     * @return
     */
/*    public CohortDefinition testedPmtct() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts\n" +
                "        where hts.hts_entry_point in (160538,160456,1623)\n" +
                "        and hts.patient_given_result ='Yes'\n" +
                "        and hts.voided =0 and hts.visit_date\n" +
                "        between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested HTS");
        return cd;
    }*/

    /**
     * Tested positive PMTCT at ANC-1
     */
    public CohortDefinition testedPositivePmtctANC1() {
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "         left join (select v.patient_id, v.visit_date, v.anc_visit_number, v.final_test_result\n" +
                "                    from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "                    where date(v.visit_date) between date(:startDate) and date(:endDate)) v\n" +
                "                   on e.patient_id = v.patient_id\n" +
                "         left join (select t.patient_id, t.visit_date\n" +
                "                    from kenyaemr_etl.etl_hts_test t\n" +
                "                    where t.test_type = 1\n" +
                "                      and t.final_test_result = 'Positive'\n" +
                "                      and t.hts_entry_point = 160538\n" +
                "                      and t.visit_date between date(:startDate) and date(:endDate)) t\n" +
                "                   on v.patient_id = t.patient_id\n" +
                "where (v.anc_visit_number = 1 and (v.final_test_result = 'Positive' or v.visit_date = t.visit_date))\n" +
                "   or (e.first_anc_visit_date = t.visit_date);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HTS Positive at PMTCT ANC-1");
        return cd;
    }

    /**
     * Tested negative PMTCT at ANC-1
     */
    public CohortDefinition testedNegativePmtctANC1() {
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "         left join (select v.patient_id, v.visit_date, v.anc_visit_number, v.final_test_result\n" +
                "                    from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "                    where date(v.visit_date) between date(:startDate) and date(:endDate)) v\n" +
                "                   on e.patient_id = v.patient_id\n" +
                "         left join (select t.patient_id, t.visit_date\n" +
                "                    from kenyaemr_etl.etl_hts_test t\n" +
                "                    where t.test_type = 1\n" +
                "                      and t.final_test_result = 'Negative'\n" +
                "                      and t.hts_entry_point = 160538\n" +
                "                      and t.visit_date between date(:startDate) and date(:endDate)) t\n" +
                "                   on v.patient_id = t.patient_id\n" +
                "where (v.anc_visit_number = 1 and (v.final_test_result = 'Negative' or v.visit_date = t.visit_date))\n" +
                "   or (e.first_anc_visit_date = t.visit_date);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HTS Negative at PMTCT ANC-1");
        return cd;
    }
    /**
     * Tested Positive at PMTCT post ANC-1 Pregnant & Labour/delivery
     * @return
     */
    public CohortDefinition testedPositivePmtctPostANC1PregLabourAndDelivery() {
        String sqlQuery = "select e.patient_id\n" +
                "       from kenyaemr_etl.etl_mch_enrollment e\n" +
                "                left join (select av.patient_id\n" +
                "                           from kenyaemr_etl.etl_mch_antenatal_visit av\n" +
                "                           where av.anc_visit_number > 1\n" +
                "                             and av.visit_date between date(:startDate) and date(:endDate)\n" +
                "                           group by av.patient_id\n" +
                "                           having mid(max(concat(av.visit_date, av.final_test_result)), 11) = 'Positive') av\n" +
                "                          on av.patient_id = e.patient_id\n" +
                "                left join (select d.patient_id\n" +
                "                           from kenyaemr_etl.etl_mchs_delivery d\n" +
                "                           where d.visit_date between date(:startDate) and date(:endDate)\n" +
                "                           group by d.patient_id\n" +
                "                           having mid(max(concat(d.visit_date, d.final_test_result)), 11) = 'Positive') d\n" +
                "                          on d.patient_id = e.patient_id\n" +
                "                left join (select t.patient_id, t.visit_date\n" +
                "                           from kenyaemr_etl.etl_hts_test t\n" +
                "                           where t.visit_date between date(:startDate) and date(:endDate)\n" +
                "                             and t.hts_entry_point in (160538, 160456, 1623)\n" +
                "                           group by t.patient_id\n" +
                "                           having mid(max(concat(t.visit_date, t.final_test_result)), 11) = 'Positive') t\n" +
                "                          on t.patient_id = e.patient_id\n" +
                "       where av.patient_id is not null\n" +
                "          or d.patient_id is not null\n" +
                "          or (t.visit_date > date(e.first_anc_visit_date) and t.patient_id is not null)\n" +
                "       group by e.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive at PMTCT post ANC-1 Pregnant & Labour/delivery");
        return cd;
    }

    /**
     * Tested positive at PMTCT post ANC-1 Breastfeeding
     * @return
     */
    public CohortDefinition testedPositivePmtctPostANC1BreastFeeding() {
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "         left join (select pv.patient_id\n" +
                "                    from kenyaemr_etl.etl_mch_postnatal_visit pv\n" +
                "                    where pv.visit_date between date(:startDate) and date(:endDate)\n" +
                "                    group by pv.patient_id\n" +
                "                    having mid(max(concat(pv.visit_date, pv.final_test_result)), 11) = 'Positive') pv\n" +
                "                   on pv.patient_id = e.patient_id\n" +
                "         left join (select t.patient_id, t.visit_date\n" +
                "                    from kenyaemr_etl.etl_hts_test t\n" +
                "                    where t.visit_date between date(:startDate) and date(:endDate)\n" +
                "                      and t.hts_entry_point in (160538, 160456, 1623)\n" +
                "                    group by t.patient_id\n" +
                "                    having mid(max(concat(t.visit_date, t.final_test_result)), 11) = 'Positive') t\n" +
                "                   on t.patient_id = e.patient_id\n" +
                "where pv.patient_id is not null\n" +
                "   or (t.visit_date > date(e.first_anc_visit_date) and t.patient_id is not null)\n" +
                "group by e.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested positive at PMTCT post ANC-1 Breastfeeding");
        return cd;
    }
    /**
     * Tested Positive PMTCT post ANC-1
     */
    public CohortDefinition testedPositivePmtctPostANC1() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedPositivePmtctPostANC1PregLabourAndDelivery", ReportUtils.map(testedPositivePmtctPostANC1PregLabourAndDelivery(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedPositivePmtctPostANC1BreastFeeding", ReportUtils.map(testedPositivePmtctPostANC1BreastFeeding(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedPositivePmtctPostANC1PregLabourAndDelivery OR testedPositivePmtctPostANC1BreastFeeding");
        return cd;
    }

    /**
     * Tested negative at PMTCT post ANC-1 Pregnant & Labour/delivery
     * @return
     */
    public CohortDefinition testedNegativePmtctPostANC1PregLabourAndDelivery() {
        String sqlQuery = "select e.patient_id\n" +
                "       from kenyaemr_etl.etl_mch_enrollment e\n" +
                "                left join (select av.patient_id\n" +
                "                           from kenyaemr_etl.etl_mch_antenatal_visit av\n" +
                "                           where av.anc_visit_number > 1\n" +
                "                             and av.visit_date between date(:startDate) and date(:endDate)\n" +
                "                           group by av.patient_id\n" +
                "                           having mid(max(concat(av.visit_date, av.final_test_result)), 11) = 'Negative') av\n" +
                "                          on av.patient_id = e.patient_id\n" +
                "                left join (select d.patient_id\n" +
                "                           from kenyaemr_etl.etl_mchs_delivery d\n" +
                "                           where d.visit_date between date(:startDate) and date(:endDate)\n" +
                "                           group by d.patient_id\n" +
                "                           having mid(max(concat(d.visit_date, d.final_test_result)), 11) = 'Negative') d\n" +
                "                          on d.patient_id = e.patient_id\n" +
                "                left join (select t.patient_id, t.visit_date\n" +
                "                           from kenyaemr_etl.etl_hts_test t\n" +
                "                           where t.visit_date between date(:startDate) and date(:endDate)\n" +
                "                             and t.hts_entry_point in (160538, 160456, 1623)\n" +
                "                           group by t.patient_id\n" +
                "                           having mid(max(concat(t.visit_date, t.final_test_result)), 11) = 'Negative') t\n" +
                "                          on t.patient_id = e.patient_id\n" +
                "       where av.patient_id is not null\n" +
                "          or d.patient_id is not null\n" +
                "          or (t.visit_date > date(e.first_anc_visit_date) and t.patient_id is not null)\n" +
                "       group by e.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested negative at PMTCT post ANC-1 Pregnant & Labour/delivery");
        return cd;
    }

    /**
     * Tested negative at PMTCT post ANC-1 Breastfeeding
     * @return
     */
    public CohortDefinition testedNegativePmtctPostANC1BreastFeeding() {
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "         left join (select pv.patient_id\n" +
                "                    from kenyaemr_etl.etl_mch_postnatal_visit pv\n" +
                "                    where pv.visit_date between date(:startDate) and date(:endDate)\n" +
                "                    group by pv.patient_id\n" +
                "                    having mid(max(concat(pv.visit_date, pv.final_test_result)), 11) = 'Negative') pv\n" +
                "                   on pv.patient_id = e.patient_id\n" +
                "         left join (select t.patient_id, t.visit_date\n" +
                "                    from kenyaemr_etl.etl_hts_test t\n" +
                "                    where t.visit_date between date(:startDate) and date(:endDate)\n" +
                "                      and t.hts_entry_point in (160538, 160456, 1623)\n" +
                "                    group by t.patient_id\n" +
                "                    having mid(max(concat(t.visit_date, t.final_test_result)), 11) = 'Negative') t\n" +
                "                   on t.patient_id = e.patient_id\n" +
                "where pv.patient_id is not null\n" +
                "   or (t.visit_date > date(e.first_anc_visit_date) and t.patient_id is not null)\n" +
                "group by e.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested negative at PMTCT post ANC-1 Breastfeeding");
        return cd;
    }
    /**
     * testedNegativePmtctPostANC1
     * Tested Negative PMTCT post ANC-1
     */
    public CohortDefinition testedNegativePmtctPostANC1() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedNegativePmtctPostANC1PregLabourAndDelivery", ReportUtils.map(testedNegativePmtctPostANC1PregLabourAndDelivery(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedNegativePmtctPostANC1BreastFeeding", ReportUtils.map(testedNegativePmtctPostANC1BreastFeeding(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedNegativePmtctPostANC1PregLabourAndDelivery OR testedNegativePmtctPostANC1BreastFeeding");
        return cd;
    }

    /**
     *HTS TEST Compositions by Strategies
     * HP: Hospital Patient Testing
     * Compositions for HTS_TST Datim indicators
     *
     * @return
     */
 /*   public CohortDefinition testedHospitalPatient() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts \n" +
                "                where hts.test_strategy =164163\n" +
                "                and hts.patient_given_result ='Yes'\n" +
                "                and hts.voided =0 and hts.visit_date\n" +
                "                between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_HP");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested HP");
        return cd;
    }
*/
    /**HTS Strategies
     *NP: HTS for non-patients
     * Compositions for HTS_TST Datim indicators
     *
     * @return
     */
  /*  public CohortDefinition testedNonPatient() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts \n" +
                "                where hts.test_strategy =164953\n" +
                "                and hts.patient_given_result ='Yes'\n" +
                "                and hts.voided =0 and hts.visit_date\n" +
                "                between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_NP");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested NP");
        return cd;
    }*/

    /**HTS Strategies
     * VI:Integrated VCT Center
     * Compositions for HTS_TST Datim indicators
     *
     * @return
     */
  /*  public CohortDefinition testedIntegratedVCT() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts \n" +
                "                where hts.test_strategy =164954\n" +
                "                and hts.patient_given_result ='Yes'\n" +
                "                and hts.voided =0 and hts.visit_date\n" +
                "                between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_VI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested VI");
        return cd;
    }*/

    /**HTS Strategies
     * Stand Alone VCT Center
     * Compositions for HTS_TST Datim indicators
     *
     * @return
     */
  /*  public CohortDefinition testedStandAloneVCT() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts \n" +
                "                where hts.test_strategy =164955\n" +
                "                and hts.patient_given_result ='Yes'\n" +
                "                and hts.voided =0 and hts.visit_date\n" +
                "                between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_VS");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested VS");
        return cd;
    }*/

    /**HTS Strategies
     * HB: Home Based Testing
     * Compositions for HTS_TST Datim indicators
     *
     * @return
     */
  /*  public CohortDefinition testedHomeBasedTesting() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts \n" +
                "                where hts.test_strategy =159938\n" +
                "                and hts.patient_given_result ='Yes'\n" +
                "                and hts.voided =0 and hts.visit_date\n" +
                "                between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_HB");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested HB");
        return cd;
    }*/

    /**HTS Strategies
     * MO: Mobile Outreach HTS
     * Compositions for HTS_TST Datim indicators
     *
     * @return
     */
  /*  public CohortDefinition testedMobileOutreach() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts \n" +
                "                where hts.test_strategy =159939\n" +
                "                and hts.patient_given_result ='Yes'\n" +
                "                and hts.voided =0 and hts.visit_date\n" +
                "                between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_MO");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested MO");
        return cd;
    }*/

    /**HTS Strategies
     * Index testing
     * Compositions for HTS_TST Datim indicators
     *
     * @return
     */
    public CohortDefinition testedIndexTesting() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts \n" +
                "                where hts.test_strategy =161557\n" +
                "                and hts.patient_given_result ='Yes'\n" +
                "                and hts.voided =0 and hts.visit_date\n" +
                "                between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_IT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested IT");
        return cd;
    }

    /**HTS Strategies
     * Social Networks
     * Compositions for HTS_TST Datim indicators
     *
     * @return
     */
    public CohortDefinition testedSocialNetworks() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts \n" +
                "                where hts.test_strategy =166606\n" +
                "                and hts.patient_given_result ='Yes'\n" +
                "                and hts.voided =0 and hts.visit_date\n" +
                "                between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_SNS");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested SNS");
        return cd;
    }

    /**HTS Strategies
     * O:Others
     * Compositions for HTS_TST Datim indicators
     *
     * @return
     */
   /* public CohortDefinition testedOthers() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts \n" +
                "                where hts.test_strategy =5622\n" +
                "                and hts.patient_given_result ='Yes'\n" +
                "                and hts.voided =0 and hts.visit_date\n" +
                "                between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_O");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested O");
        return cd;
    }*/

    public CohortDefinition testedVCTEntryPoint() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts \n" +
                "                where hts.hts_entry_point =159940\n" +
                "                and hts.patient_given_result ='Yes'\n" +
                "                and hts.voided =0 and hts.visit_date\n" +
                "                between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_VCT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested at VCT");
        return cd;
    }

    /**
     * Tested at Pediatrics clinic
     * @return
     */
    public CohortDefinition testedPediatricClinics() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts \n" +
                "                where hts.hts_entry_point =162181\n" +
                "                and hts.patient_given_result ='Yes'\n" +
                "                and hts.voided =0 and hts.visit_date\n" +
                "                between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_PEDIATRICS_CLINICS");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested at Pediatric clinics");
        return cd;
    }

    /**
     * Tested at Malnutrition clinic
     * @return
     */
    public CohortDefinition testedMalnutritionClinics() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts \n" +
                "                where hts.hts_entry_point =160552\n" +
                "                and hts.patient_given_result ='Yes'\n" +
                "                and hts.voided =0 and hts.visit_date\n" +
                "                between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_MALNUTRITION_CLINICS");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested at Malnutrition clinics");
        return cd;
    }

    /**
     * Tested at TB clinic
     * @return
     */
    public CohortDefinition testedTBClinic() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts \n" +
                "                where hts.hts_entry_point =160541\n" +
                "                and hts.patient_given_result ='Yes'\n" +
                "                and hts.voided =0 and hts.visit_date\n" +
                "                between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_TB_CLINIC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested at TB clinic");
        return cd;
    }

    /**
     * Tested HIV at in-patient department
     * @return
     */
    public CohortDefinition testedInpatientServices() {
        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.hts_entry_point = 5485 and hts.patient_given_result ='Yes'\n" +
                "    and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_INPATIENT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Inpatient Services");
        return cd;
    }

    /**
     * Tested HIV at others - Others, OPD
     * @return
     */
    public CohortDefinition testedOthersOPD() {
        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.hts_entry_point in (5622,160542) and hts.patient_given_result ='Yes'\n" +
                "    and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_OTHERS");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested in Others or OPD");
        return cd;
    }

    /**
     *HTS_TST Tested at STI Clinic
     * @return
     */
    public CohortDefinition testedSTIClinic() {
        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.hts_entry_point =160546 and hts.patient_given_result ='Yes' \n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_STI_CLINIC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested at STI Clinic");
        return cd;
    }

    /**
     *HTS_TST Tested at emergency ward
     * @return
     */
    public CohortDefinition testedEmergencyWard() {
        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.hts_entry_point = 160522 and hts.patient_given_result ='Yes'\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_EMERGENCY_WARD");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested at Emergency ward");
        return cd;
    }

    /**
     *HTS_TST Tested at VMMC services
     * @return
     */
    public CohortDefinition testedVMMCServices() {
        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.hts_entry_point = 162223 and hts.patient_given_result ='Yes'\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_VMMC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested at VMMC services");
        return cd;
    }

    /**
     * Patients who received initial HIV test with Negative result within a reporting period
     */
    public CohortDefinition initialNegativeHIVTestResult() {
        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.test_type =1 and hts.final_test_result ='Negative' and hts.patient_given_result ='Yes'" +
                " and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_NEGATIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested negative for HIV");
        return cd;
    }

    /**
     * Patients who received initial HIV test with Positive result within a reporting period
     */
    public CohortDefinition positiveHIVTestResult() {
        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.test_type =1 and hts.final_test_result ='Positive' and hts.patient_given_result ='Yes'" +
                " and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_Positive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive for HIV");
        return cd;
    }

    /**
     * HTS_TST_INDEX_TESTING clients who tested HIV Negative
     * @return
     */
    public CohortDefinition indexTestedNegative() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialNegativeHIVTestResult", ReportUtils.map(initialNegativeHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedIndexTesting AND initialNegativeHIVTestResult");
        return cd;
    }

    /**
     * HTS_TST_INDEX_TESTING clients who tested HIV Positive
     * @return
     */
    public CohortDefinition indexTestedPositive() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResult", ReportUtils.map(positiveHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedIndexTesting AND positiveHIVTestResult");
        return cd;
    }

    /**
     * HTS_TST_VCT_TESTING clients who tested HIV Negative
     * @return
     */
    public CohortDefinition testedNegativeVCT() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("initialNegativeHIVTestResult", ReportUtils.map(initialNegativeHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedVCTEntryPoint", ReportUtils.map(testedVCTEntryPoint(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedVCTEntryPoint AND initialNegativeHIVTestResult AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic)");
        return cd;
    }

    /**
     * HTS_TST_VCT_TESTING clients who tested HIV Positive
     * @return
     */
    public CohortDefinition testedPositiveVCT() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("positiveHIVTestResult", ReportUtils.map(positiveHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedVCTEntryPoint", ReportUtils.map(testedVCTEntryPoint(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedVCTEntryPoint AND positiveHIVTestResult AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic)");
        return cd;
    }

    /**
     * Patients tested Negative at the pediatrics clinic and not in either HTS_index or sns_testing
     * @return
     */
    public CohortDefinition testedNegativePaediatricServices() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("initialNegativeHIVTestResultInfants", ReportUtils.map(initialNegativeHIVTestResultInfants(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedPediatricClinics", ReportUtils.map(testedPediatricClinics(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedPediatricClinics AND initialNegativeHIVTestResultInfants) AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic)");
        return cd;
    }

    /**
     * Patients tested positive at the pediatrics clinic and not in either HTS_index or sns_testing
     * @return
     */
    public CohortDefinition testedPositivePaediatricServices() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedPediatricClinics", ReportUtils.map(testedPediatricClinics(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResultInfants", ReportUtils.map(positiveHIVTestResultInfants(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedPediatricClinics AND positiveHIVTestResultInfants) AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic)");
        return cd;
    }

    /**
     * Patients tested negative at the malnutrition clinic and not in either HTS_index or sns_testing
     * @return
     */
    public CohortDefinition testedNegativeMalnutritionClinics() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedMalnutritionClinics", ReportUtils.map(testedMalnutritionClinics(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialNegativeHIVTestResultInfants", ReportUtils.map(initialNegativeHIVTestResultInfants(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedMalnutritionClinics AND initialNegativeHIVTestResultInfants) AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic)");
        return cd;
    }

    /**
     * Patients tested positive at the malnutrition clinic and not in either HTS_index or sns_testing
     * @return
     */
    public CohortDefinition testedPositiveMalnutritionClinics() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedMalnutritionClinics", ReportUtils.map(testedMalnutritionClinics(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResultInfants", ReportUtils.map(positiveHIVTestResultInfants(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedMalnutritionClinics AND positiveHIVTestResultInfants) AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic)");
        return cd;
    }

    /**
     * Patients tested Negative at the TB clinic and not in either HTS_index or sns_testing
     * @return
     */
    public CohortDefinition testedNegativeTBClinic() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedTBClinic", ReportUtils.map(testedTBClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialNegativeHIVTestResult", ReportUtils.map(initialNegativeHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedTBClinic AND initialNegativeHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic)");
        return cd;
    }

    /**
     * Patients tested positive at the malnutrition clinic and not in either HTS_index or sns_testing
     * @return
     */
    public CohortDefinition testedPositiveTBClinic() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedTBClinic", ReportUtils.map(testedTBClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResult", ReportUtils.map(positiveHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedTBClinic AND positiveHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic)");
        return cd;
    }

    /**
     * Patients tested Negative at the in-patient department and not in either HTS_index or sns_testing
     * @return
     */
    public CohortDefinition testedNegativeInpatientServices() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedInpatientServices", ReportUtils.map(testedInpatientServices(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialNegativeHIVTestResult", ReportUtils.map(initialNegativeHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedInpatientServices AND initialNegativeHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic)");
        return cd;
    }

    /**
     * Patients tested positive at the in-patient department and not in either HTS_index or sns_testing
     * @return
     */
    public CohortDefinition testedPositiveInpatientServices() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedInpatientServices", ReportUtils.map(testedInpatientServices(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResult", ReportUtils.map(positiveHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedInpatientServices AND positiveHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic)");
        return cd;
    }

    /**
     *Number of males circumcised
     * @return
     */
    public CohortDefinition malesCircumcised() {
        String sqlQuery = "select p.patient_id from kenyaemr_etl.etl_vmmc_circumcision_procedure p where p.visit_date between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("VMMC_CIRC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of males circumcised");
        return cd;
    }

    /**
     *Number tested HIV POSITIVE at VMMC site
     * @return
     */
    public CohortDefinition testedHIVPositiveAtVMMCSite() {
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_vmmc_circumcision_procedure e\n" +
                "         left join (select h.patient_id, h.hiv_test_date\n" +
                "                    from kenyaemr_etl.etl_vmmc_medical_history h\n" +
                "                    where h.hiv_status = 703\n" +
                "                      and date(h.hiv_test_date) between date(:startDate) and date(:endDate)) h\n" +
                "                   on e.patient_id = h.patient_id\n" +
                "         left join (select t.patient_id,t.visit_date\n" +
                "                    from kenyaemr_etl.etl_hts_test t\n" +
                "                    where t.final_test_result = 'Positive'\n" +
                "                      and t.test_type = 2\n" +
                "                      and t.hts_entry_point = 162223\n" +
                "                      and t.visit_date between date(:startDate) and date(:endDate)) t on e.patient_id = t.patient_id\n" +
                "where e.visit_date between date(:startDate) and date(:endDate) and ((h.patient_id is not null and e.visit_date >= date(h.hiv_test_date)) or (t.patient_id is not null and e.visit_date >= date(t.visit_date)));";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("VMMC_SITE_HIV_POSITIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number tested HIV POSITIVE at VMMC site");
        return cd;
    }

    /**
     *Number tested HIV Negative at VMMC site
     * @return
     */
    public CohortDefinition testedHIVNegativeAtVMMCSite() {
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_vmmc_circumcision_procedure e\n" +
                "         left join (select h.patient_id, h.hiv_test_date\n" +
                "                    from kenyaemr_etl.etl_vmmc_medical_history h\n" +
                "                    where h.hiv_status = 664\n" +
                "                      and date(h.hiv_test_date) between date(:startDate) and date(:endDate)) h\n" +
                "                   on e.patient_id = h.patient_id\n" +
                "         left join (select t.patient_id,t.visit_date\n" +
                "                    from kenyaemr_etl.etl_hts_test t\n" +
                "                    where t.final_test_result = 'Negative'\n" +
                "                      and t.hts_entry_point = 162223\n" +
                "                      and t.visit_date between date(:startDate) and date(:endDate)) t on e.patient_id = t.patient_id\n" +
                "where e.visit_date between date(:startDate) and date(:endDate) and ((h.patient_id is not null and e.visit_date >= date(h.hiv_test_date)) or (t.patient_id is not null and e.visit_date >= date(t.visit_date)));";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("VMMC_SITE_HIV_NEGATIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number tested HIV NEGATIVE at VMMC site");
        return cd;
    }

    /**
     *Number of males circumcised and tested HIV positive at VMMC site
     * @return
     */
    public CohortDefinition malesCircumcisedTestedHIVPositive() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("malesCircumcised", ReportUtils.map(malesCircumcised(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedHIVPositiveAtVMMCSite", ReportUtils.map(testedHIVPositiveAtVMMCSite(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("malesCircumcised AND testedHIVPositiveAtVMMCSite");
        return cd;
    }

    /**
     *Number of males circumcised and tested HIV negative at VMMC site
     * @return
     */
    public CohortDefinition malesCircumcisedTestedHIVNegative() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("malesCircumcised", ReportUtils.map(malesCircumcised(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedHIVNegativeAtVMMCSite", ReportUtils.map(testedHIVNegativeAtVMMCSite(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("malesCircumcised AND testedHIVNegativeAtVMMCSite");
        return cd;
    }

    /**
     *Number of males circumcised with indeterminate HIV result at VMMC site or not tested at VMMC site
     * @return
     */
    public CohortDefinition malesCircumcisedIndeterminateHIVResult() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("malesCircumcised", ReportUtils.map(malesCircumcised(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedHIVPositiveAtVMMCSite", ReportUtils.map(testedHIVPositiveAtVMMCSite(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedHIVNegativeAtVMMCSite", ReportUtils.map(testedHIVNegativeAtVMMCSite(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("malesCircumcised AND NOT (testedHIVPositiveAtVMMCSite OR testedHIVNegativeAtVMMCSite)");
        return cd;
    }

    /**
     *Number of males circumcised through surgical procedure
     * @return
     */
    public CohortDefinition vmmcSurgical() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_vmmc_enrolment e inner join kenyaemr_etl.etl_vmmc_circumcision_procedure c on e.patient_id = c.patient_id\n" +
                "where c.visit_date between date(:startDate) and date(:endDate) and c.circumcision_method = 167119;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("VMMC_SURGICAL");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of males circumcised through surgical procedure");
        return cd;
    }

    /**
     *Number of males circumcised using device
     * @return
     */
    public CohortDefinition vmmcDevice() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_vmmc_enrolment e inner join kenyaemr_etl.etl_vmmc_circumcision_procedure c on e.patient_id = c.patient_id\n" +
                "where c.visit_date between date(:startDate) and date(:endDate) and c.circumcision_method = 167120;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("VMMC_DEVICE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of males circumcised using device");
        return cd;
    }

    /**
     *Number of clients who followed up within 14 days of VMMC procedure
     * @return
     */
    public CohortDefinition followedUpWithin14daysOfVMMCProcedure() {
        String sqlQuery = "select c.patient_id from kenyaemr_etl.etl_vmmc_circumcision_procedure c\n" +
                "inner join kenyaemr_etl.etl_vmmc_client_followup f on c.patient_id = f.patient_id\n" +
                "where c.visit_date between date(:startDate) and date(:endDate) and timestampdiff(DAY ,c.visit_date,f.visit_date) <= 14;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("VMMC_FOLLOWUP_WITHIN_14_DAYS");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of clients who followed up within 14 days of VMMC procedure");
        return cd;
    }

    /**
     *Number of clients who did not follow up within 14 days of VMMC procedure or within the reporting period
     * @return
     */
    public CohortDefinition noFollowUpWithin14daysOfVMMCProcedure() {
        String sqlQuery = "select c.patient_id from kenyaemr_etl.etl_vmmc_circumcision_procedure c\n" +
                "                             left join (select f.patient_id, f.visit_date from kenyaemr_etl.etl_vmmc_client_followup f where f.visit_date between date(:startDate) and date(:endDate))f on c.patient_id = f.patient_id\n" +
                "where c.visit_date between date(:startDate) and date(:endDate) and (timestampdiff(DAY ,c.visit_date,f.visit_date) >14 or f.patient_id is null);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("VMMC_NO_FOLLOWUP_WITHIN_14_DAYS");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of clients who did not follow up within 14 days of VMMC procedure or within the reporting period");
        return cd;
    }

    /**
     *Number of males circumcised through surgical procedure and followed up within 14 days
     * @return
     */
    public CohortDefinition vmmcSurgicalFollowupWithin14Days() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("vmmcSurgical", ReportUtils.map(vmmcSurgical(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("followedUpWithin14daysOfVMMCProcedure", ReportUtils.map(followedUpWithin14daysOfVMMCProcedure(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("vmmcSurgical AND followedUpWithin14daysOfVMMCProcedure");
        return cd;
    }

    /**
     *Number of males circumcised through surgical procedure and did not follow up within 14 days
     * @return
     */
    public CohortDefinition vmmcSurgicalNoFollowupWithin14Days() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("vmmcSurgical", ReportUtils.map(vmmcSurgical(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("noFollowUpWithin14daysOfVMMCProcedure", ReportUtils.map(noFollowUpWithin14daysOfVMMCProcedure(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("vmmcSurgical AND noFollowUpWithin14daysOfVMMCProcedure");
        return cd;
    }

    /**
     *Number of males circumcised using device and followed up within 14 days
     * @return
     */
    public CohortDefinition vmmcDeviceFollowupWithin14Days() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("vmmcDevice", ReportUtils.map(vmmcDevice(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("followedUpWithin14daysOfVMMCProcedure", ReportUtils.map(followedUpWithin14daysOfVMMCProcedure(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("vmmcDevice AND followedUpWithin14daysOfVMMCProcedure");
        return cd;
    }

    /**
     *Number of males circumcised using device and did not follow up within 14 days
     * @return
     */
    public CohortDefinition vmmcDeviceNoFollowupWithin14Days() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("vmmcDevice", ReportUtils.map(vmmcDevice(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("noFollowUpWithin14daysOfVMMCProcedure", ReportUtils.map(noFollowUpWithin14daysOfVMMCProcedure(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("vmmcDevice AND noFollowUpWithin14daysOfVMMCProcedure");
        return cd;
    }

    /**
     * Patients tested Negative at Others or OPD and not in either HTS_index or sns_testing
     * @return
     */
    public CohortDefinition testedNegativeOther() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedOthersOPD", ReportUtils.map(testedOthersOPD(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialNegativeHIVTestResult", ReportUtils.map(initialNegativeHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedOthersOPD AND initialNegativeHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic)");
        return cd;
    }

    /**
     * Patients tested positive at the in-patient department and not in either HTS_index or sns_testing
     * @return
     */
    public CohortDefinition testedPositiveOther() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedOthersOPD", ReportUtils.map(testedOthersOPD(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResult", ReportUtils.map(positiveHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedOthersOPD AND positiveHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic)");
        return cd;
    }

    /**
     *HTS_TST: Tested positive under Social network service modality
     * @return
     */
    public CohortDefinition testedPositiveSNS() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResult", ReportUtils.map(positiveHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedSocialNetworks AND positiveHIVTestResult");
        return cd;
    }

    /**
     * HTS_TST: Tested negative under Social network service modality
     * @return
     */
    public CohortDefinition testedNegativeSNS() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialNegativeHIVTestResult", ReportUtils.map(initialNegativeHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedSocialNetworks AND initialNegativeHIVTestResult");
        return cd;
    }

    /**
     *HTS_TST: Tested positive in STI clinic and not in either HTS_index or sns_testing
     * @return
     */
    public CohortDefinition testedPositiveSTIClinic() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResult", ReportUtils.map(positiveHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedSTIClinic AND positiveHIVTestResult");
        return cd;
    }

    /**
     * HTS_TST: Tested negative in STI Clinic and not in either HTS_index or sns_testing
     * @return
     */
    public CohortDefinition testedNegativeSTIClinic() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialNegativeHIVTestResult", ReportUtils.map(initialNegativeHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedSTIClinic AND initialNegativeHIVTestResult");
        return cd;
    }

    /**
     *HTS_TST: Tested positive in Emergency ward and not in either HTS_index or sns_testing
     * @return
     */
    public CohortDefinition testedPositiveEmergencyWard() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedEmergencyWard", ReportUtils.map(testedEmergencyWard(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResult", ReportUtils.map(positiveHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedEmergencyWard AND positiveHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic)");
        return cd;
    }

    /**
     * HTS_TST: Tested negative in Emergency ward and not in either HTS_index or sns_testing
     * @return
     */
    public CohortDefinition testedNegativeEmergencyWard() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedEmergencyWard", ReportUtils.map(testedEmergencyWard(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialNegativeHIVTestResult", ReportUtils.map(initialNegativeHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedEmergencyWard AND initialNegativeHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic)");
        return cd;
    }

    /**
     *HTS_TST: Tested positive in VMMC Services and not in either HTS_index or sns_testing
     * @return
     */
    public CohortDefinition testedPositiveVMMCServices() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedVMMCServices", ReportUtils.map(testedVMMCServices(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResult", ReportUtils.map(positiveHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedVMMCServices AND positiveHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic)");
        return cd;
    }

    /**
     * HTS_TST: Tested negative in VMMC services and not in either HTS_index or sns_testing
     * @return
     */
    public CohortDefinition testedNegativeVMMCServices() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedVMMCServices", ReportUtils.map(testedVMMCServices(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialNegativeHIVTestResult", ReportUtils.map(initialNegativeHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedVMMCServices AND initialNegativeHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic)");
        return cd;
    }
//TODO: To review with startedOnART on ETLDAtimCohortLibrary add filter for pregnancy :Done
    /*Newly Started ART While BreastFeeding - redo query*/

    public CohortDefinition newlyStartedARTWhileBreastFeeding() {

        String sqlQuery = "select net.patient_id\n" +
                "from (\n" +
                "     select e.patient_id,e.date_started,\n" +
                "            e.gender,\n" +
                "            e.dob,\n" +
                "            d.visit_date as dis_date,\n" +
                "            if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "            e.regimen, e.regimen_line, e.alternative_regimen,\n" +
                "            mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "            max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art,\n" +
                "            max(if(enr.transfer_in_date is not null, 1, 0)) as TIn,\n" +
                "            max(fup.visit_date) as latest_vis_date\n" +
                "     from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started,\n" +
                "                  mid(min(concat(e.date_started,e.regimen_name)),11) as regimen,\n" +
                "                  mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line,\n" +
                "                  max(if(discontinued,1,0))as alternative_regimen\n" +
                "           from kenyaemr_etl.etl_drug_event e\n" +
                "                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "                 where e.program = 'HIV'\n" +
                "           group by e.patient_id) e\n" +
                "           left outer join (select mid(max(concat(en.visit_date,en.patient_id)),11 )latest_enr, max(visit_date) lst_mch_visit_date\n" +
                "                  from kenyaemr_etl.etl_mch_enrollment en group by en.patient_id) enr on enr.latest_enr = e.patient_id\n" +
                "           left outer join (select mid(max(concat(del.visit_date,del.patient_id)),11 )latest_del, max(visit_date) lst_del_visit_date\n" +
                "                  from  kenyaemr_etl.etl_mchs_delivery del group by del.patient_id) dl on dl.latest_del = e.patient_id\n" +
                "           left outer join (select mid(max(concat(pv.visit_date,pv.patient_id)),11 )latest_pv, max(visit_date) lst_pv_visit_date\n" +
                "                            from  kenyaemr_etl.etl_mch_postnatal_visit pv group by pv.patient_id) psnv on psnv.lst_pv_visit_date = e.patient_id\n" +
                "            left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_name='HIV'\n" +
                "            left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "            left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "            inner join kenyaemr_etl.etl_mch_postnatal_visit pv on pv.patient_id=e.patient_id\n" +
                "     where\n" +
                "            e.date_started >= enr.lst_mch_visit_date\n" +
                "            and (e.date_started > dl.lst_del_visit_date or dl.lst_del_visit_date is null)\n" +
                "            and (e.date_started >= psnv.lst_pv_visit_date and baby_feeding_method in (5526,6046))\n" +
                "            and date(e.date_started) between date(:startDate) and :endDate\n" +
                "     group by e.patient_id\n" +
                "     having TI_on_art=0\n" +
                "     )net;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_New_BF");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Newly Started ART While BreastFeeding");
        return cd;

    }
//TODO: To review with startedOnART on ETLDAtimCohortLibrary add filter breastfeeding : Done
    /*Newly Started ART While Confirmed TB and / or TB Treated*/

    public CohortDefinition newlyStartedARTWithTB() {

        String sqlQuery = "select net.patient_id\n" +
                "from (\n" +
                "     select e.patient_id,e.date_started,\n" +
                "            e.gender,\n" +
                "            e.dob,\n" +
                "            d.visit_date as dis_date,\n" +
                "            if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "            e.regimen, e.regimen_line, e.alternative_regimen,\n" +
                "            fup.visit_date, fup.tb_status,\n" +
                "            mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "            max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art,\n" +
                "            max(if(enr.transfer_in_date is not null, 1, 0)) as TIn,\n" +
                "            max(fup.visit_date) as latest_vis_date\n" +
                "     from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started,\n" +
                "                  mid(min(concat(e.date_started,e.regimen_name)),11) as regimen,\n" +
                "                  mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line,\n" +
                "                  max(if(discontinued,1,0))as alternative_regimen\n" +
                "           from kenyaemr_etl.etl_drug_event e\n" +
                "                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "           where e.program = 'HIV'\n" +
                "           group by e.patient_id) e\n" +
                "            left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_name='HIV'\n" +
                "            left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "            left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "            left outer join kenyaemr_etl.etl_tb_enrollment tbenr on tbenr.patient_id = e.patient_id\n" +
                "     where date(e.date_started) between :startDate and date(:endDate)\n" +
                "       and ((fup.on_anti_tb_drugs =1065 and\n" +
                "             tbenr.visit_date between :startDate and date(:endDate) ))\n" +
                "         group by e.patient_id\n" +
                "         having TI_on_art=0\n" +
                "               )net;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_New_TB");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Newly Started ART While Confirmed TB and / or TB Treated");
        return cd;

    }

    /*Number of HIV-exposed infants who were born 24 months prior to the reporting period and registered in the birth cohort*/
    public CohortDefinition pmtctFoDenominator() {

        String sqlQuery = "select d.patient_id from kenyaemr_etl.etl_patient_demographics d inner join kenyaemr_etl.etl_hei_enrollment e on d.patient_id = e.patient_id where date(d.dob)\n" +
                "    between date_sub(DATE_SUB(LAST_DAY(:endDate),INTERVAL DAY(LAST_DAY(:endDate))-1 DAY) , interval 24 month) and date_sub(date(:endDate) , interval 24 month)\n" +
                "and timestampdiff(month, (d.dob), date(e.visit_date)) between 0 and 18;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PMTCT_FO_INFECTED_HEI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Infected HEI Cohort");
        return cd;

    }

    /*HIV Infected HEI Cohort*/
    public CohortDefinition hivInfectedHEIOutcomeSql() {
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_hei_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_patient_program_discontinuation d on e.patient_id = d.patient_id\n" +
                "where d.program_name in ('MCH Child HEI', 'MCH Child')\n" +
                "  and d.discontinuation_reason = 138571;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PMTCT_FO_INFECTED_HEI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Infected HEI Cohort");
        return cd;
    }

    public CohortDefinition hivInfectedHEILabs() {
        String sqlQuery = "select x.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract x\n" +
                "where x.order_reason in (1040, 1326, 844, 164860)\n" +
                "  and x.test_result = 703\n" +
                "  and date(coalesce(x.date_test_requested, x.visit_date)) <= date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("hivInfectedHEILabs");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Infected HEI");
        return cd;
    }

    public CohortDefinition hivInfectedHEIHTS() {
        String sqlQuery = "select d.patient_id\n" +
                "from kenyaemr_etl.etl_patient_demographics d\n" +
                "         inner join kenyaemr_etl.etl_hts_test t on d.patient_id = t.patient_id\n" +
                "where timestampdiff(MONTH, date(d.dob), date(t.visit_date)) between 18 and 24\n" +
                "group by t.patient_id\n" +
                "having mid(max(concat(date(t.visit_date), t.final_test_result)), 11) = 'Positive';";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PMTCT_FO_INFECTED_HEI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Infected HEI HTS");
        return cd;
    }

    /*Uninfected HEI Cohort*/
    public CohortDefinition hivUninfectedHEIOutcomeSql() {
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_hei_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_patient_program_discontinuation d on e.patient_id = d.patient_id\n" +
                "where d.program_name in ('MCH Child HEI', 'MCH Child')\n" +
                "  and d.discontinuation_reason = 1403;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PMTCT_FO_UNINFECTED_HEI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Uninfected HEI Cohort");
        return cd;
    }

    public CohortDefinition hivUninfectedHEILabsSql() {
        String sqlQuery = "select x.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract x\n" +
                "where x.order_reason = 164860\n" +
                "  and x.test_result = 664\n" +
                "  and date(coalesce(x.date_test_requested, x.visit_date)) <= date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("hivUninfectedHEILabsSql");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV uninfected HEI");
        return cd;
    }

    public CohortDefinition hivUninfectedHEIHTS() {
        String sqlQuery = "select d.patient_id\n" +
                "from kenyaemr_etl.etl_patient_demographics d\n" +
                "         inner join kenyaemr_etl.etl_hts_test t on d.patient_id = t.patient_id\n" +
                "where timestampdiff(MONTH, date(d.dob), date(t.visit_date)) between 18 and 24\n" +
                "group by t.patient_id\n" +
                "having mid(max(concat(date(t.visit_date), t.final_test_result)), 11) = 'Negative';";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("hivUninfectedHEIHTS");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV uninfected HEI hts");
        return cd;
    }

    /*HEI died*/
    public CohortDefinition heiDiedWithUnknownHIVStatus() {
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_hei_enrollment e\n" +
                "         inner join kenyaemr_etl.etl_patient_program_discontinuation d on e.patient_id = d.patient_id\n" +
                "where d.program_name in ('MCH Child HEI', 'MCH Child')\n" +
                "  and d.discontinuation_reason = 160432\n" +
                "  and e.hiv_status_at_exit = 'Inconclusive';";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PMTCT_FO_HEI_DIED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hei died");
        return cd;

    }

    /**
     * Number of HIV-exposed infants identified as HIV-infected at any point during follow-up
     * @return
     */
    public CohortDefinition hivInfectedHEICohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("pmtctFoDenominator", ReportUtils.map(pmtctFoDenominator(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("hivInfectedHEIOutcomeSql", ReportUtils.map(hivInfectedHEIOutcomeSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("hivInfectedHEILabs", ReportUtils.map(hivInfectedHEILabs(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("hivInfectedHEIHTS", ReportUtils.map(hivInfectedHEIHTS(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("pmtctFoDenominator AND (hivInfectedHEILabs OR hivInfectedHEIOutcomeSql OR hivInfectedHEIHTS)");
        return cd;
    }

    /**
     * Number of HIV-exposed infants with a negative 18-month antibody test documented.
     * @return
     */
    public CohortDefinition hivUninfectedHEICohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("pmtctFoDenominator", ReportUtils.map(pmtctFoDenominator(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("hivUninfectedHEIOutcomeSql", ReportUtils.map(hivUninfectedHEIOutcomeSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("hivUninfectedHEILabsSql", ReportUtils.map(hivUninfectedHEILabsSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("hivUninfectedHEIHTS", ReportUtils.map(hivUninfectedHEIHTS(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("pmtctFoDenominator AND (hivUninfectedHEILabsSql OR hivUninfectedHEIOutcomeSql OR hivUninfectedHEIHTS)");
        return cd;
    }

    /**
     * HEI Wwith HIV final status unknown at 18 months
     * @return
     */
    public CohortDefinition unknownHIVStatusHEICohort() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("pmtctFoDenominator", ReportUtils.map(pmtctFoDenominator(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("hivInfectedHEICohort", ReportUtils.map(hivInfectedHEICohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("hivUninfectedHEICohort", ReportUtils.map(hivUninfectedHEICohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("pmtctFoDenominator AND NOT (hivInfectedHEICohort OR hivUninfectedHEICohort)");
        return cd;
    }

    /**
     *Number of HIV-exposed infants who are documented to have died without confirmation of HIV-infection between 0 and 18 months
     * @return
     */
    public CohortDefinition heiDiedWithUnknownStatus() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("pmtctFoDenominator", ReportUtils.map(pmtctFoDenominator(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiDiedWithUnknownHIVStatus", ReportUtils.map(heiDiedWithUnknownHIVStatus(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("pmtctFoDenominator AND heiDiedWithUnknownHIVStatus");
        return cd;
    }

    /**
     * Number of patients who experienced interruption in treatment in the previous reporting period
     * @return
     */
    public CohortDefinition experiencedIITPreviousReportingPeriod() {

        String sqlQuery = "select  e.patient_id\n" +
                "from (\n" +
                "         select fup_prev_period.patient_id,\n" +
                "                max(fup_prev_period.visit_date) as prev_period_latest_vis_date,\n" +
                "                mid(max(concat(fup_prev_period.visit_date,fup_prev_period.next_appointment_date)),11) as prev_period_latest_tca,\n" +
                "                max(d.visit_date) as date_discontinued,\n" +
                "                d.patient_id as disc_patient,\n" +
                "                fup_reporting_period.first_visit_after_IIT as first_visit_after_IIT,\n" +
                "                fup_reporting_period.first_tca_after_IIT as first_tca_after_IIT\n" +
                "         from kenyaemr_etl.etl_patient_hiv_followup fup_prev_period\n" +
                "                  join (select fup_reporting_period.patient_id,min(fup_reporting_period.visit_date) as first_visit_after_IIT,min(fup_reporting_period.next_appointment_date) as first_tca_after_IIT from kenyaemr_etl.etl_patient_hiv_followup fup_reporting_period where fup_reporting_period.visit_date >= date_sub(date(:endDate) , interval 3 MONTH) group by fup_reporting_period.patient_id)fup_reporting_period on fup_reporting_period.patient_id = fup_prev_period.patient_id\n" +
                "                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup_prev_period.patient_id\n" +
                "                  join kenyaemr_etl.etl_hiv_enrollment e on fup_prev_period.patient_id=e.patient_id\n" +
                "                  left outer JOIN\n" +
                "              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "               where date(visit_date) <= curdate()  and program_name='HIV'\n" +
                "               group by patient_id\n" +
                "              ) d on d.patient_id = fup_prev_period.patient_id\n" +
                "         where fup_prev_period.visit_date < date(:startDate)\n" +
                "         group by patient_id\n" +
                "         having (\n" +
                "                        (((date(prev_period_latest_tca) < date(:endDate)) and\n" +
                "                          (date(prev_period_latest_vis_date) < date(prev_period_latest_tca)))) and\n" +
                "                        ((date(fup_reporting_period.first_visit_after_IIT) > date(date_discontinued) and\n" +
                "                          date(fup_reporting_period.first_tca_after_IIT) > date(date_discontinued)) or\n" +
                "                         disc_patient is null)\n" +
                "                     and timestampdiff(day, date(prev_period_latest_tca),date(:startDate)) > 30)\n" +
                "     )e;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("experiencedIITPreviousReportingPeriod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Experienced IIT in previous reporting period");
        return cd;

    }

    /**
     * Number of Patients Experienced IIT for less than 3 months before returning to Tx
     * @return
     */
    public CohortDefinition patientExperiencedIITUnder3Months() {

        String sqlQuery = "select e.patient_id\n" +
                "from (\n" +
                "     select fup_prev_period.patient_id,\n" +
                "            max(fup_prev_period.visit_date) as prev_period_latest_vis_date,\n" +
                "            mid(max(concat(fup_prev_period.visit_date,fup_prev_period.next_appointment_date)),11) as prev_period_latest_tca,\n" +
                "            max(d.visit_date) as date_discontinued,\n" +
                "            d.patient_id as disc_patient,\n" +
                "            fup_reporting_period.first_visit_after_IIT as first_visit_after_IIT\n" +
                "     from kenyaemr_etl.etl_patient_hiv_followup fup_prev_period\n" +
                "            join (select fup_reporting_period.patient_id,min(fup_reporting_period.visit_date) as first_visit_after_IIT from kenyaemr_etl.etl_patient_hiv_followup fup_reporting_period where fup_reporting_period.visit_date >= date_sub(:endDate , interval 3 MONTH) group by fup_reporting_period.patient_id)fup_reporting_period on fup_reporting_period.patient_id = fup_prev_period.patient_id\n" +
                "            left outer JOIN\n" +
                "              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "               where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "               group by patient_id\n" +
                "              ) d on d.patient_id = fup_prev_period.patient_id\n" +
                "     where fup_prev_period.visit_date < date_sub(date(:endDate) , interval 3 MONTH)\n" +
                "     group by patient_id\n" +
                "     having\n" +
                "         ((date(prev_period_latest_tca) < date(:endDate) and date(prev_period_latest_vis_date) < date(prev_period_latest_tca))) and ((date(prev_period_latest_tca) > date(date_discontinued) and date(prev_period_latest_vis_date) > date(date_discontinued)) or disc_patient is null) and\n" +
                "         timestampdiff(day, date(prev_period_latest_tca),date(:startDate)) > 30 and timestampdiff(MONTH, date(prev_period_latest_tca),date(first_visit_after_IIT)) < 3\n" +
                "     )e;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientExperiencedIITUnder3Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Experienced IIT for less than 3 months before returning to Tx");
        return cd;

    }

    /**
     * Number of Patients Experienced IIT for 3-5 months before returning to Tx
     * @return
     */
    public CohortDefinition patientsExperiencedIIT3To5Months() {

        String sqlQuery = "select e.patient_id\n" +
                "from (\n" +
                "     select fup_prev_period.patient_id,\n" +
                "            max(fup_prev_period.visit_date) as prev_period_latest_vis_date,\n" +
                "            mid(max(concat(fup_prev_period.visit_date,fup_prev_period.next_appointment_date)),11) as prev_period_latest_tca,\n" +
                "            max(d.visit_date) as date_discontinued,\n" +
                "            d.patient_id as disc_patient,\n" +
                "            fup_reporting_period.first_visit_after_IIT as first_visit_after_IIT\n" +
                "     from kenyaemr_etl.etl_patient_hiv_followup fup_prev_period\n" +
                "            join (select fup_reporting_period.patient_id,min(fup_reporting_period.visit_date) as first_visit_after_IIT from kenyaemr_etl.etl_patient_hiv_followup fup_reporting_period where fup_reporting_period.visit_date >= date_sub(:endDate , interval 3 MONTH) group by fup_reporting_period.patient_id)fup_reporting_period on fup_reporting_period.patient_id = fup_prev_period.patient_id\n" +
                "            left outer JOIN\n" +
                "              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "               where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "               group by patient_id\n" +
                "              ) d on d.patient_id = fup_prev_period.patient_id\n" +
                "     where fup_prev_period.visit_date < date_sub(date(:endDate) , interval 3 MONTH)\n" +
                "     group by patient_id\n" +
                "     having\n" +
                "         ((date(prev_period_latest_tca) < date(:endDate) and date(prev_period_latest_vis_date) < date(prev_period_latest_tca))) and ((date(prev_period_latest_tca) > date(date_discontinued) and date(prev_period_latest_vis_date) > date(date_discontinued)) or disc_patient is null) and\n" +
                "         timestampdiff(day, date(prev_period_latest_tca),date(:startDate)) > 30 and timestampdiff(MONTH, date(prev_period_latest_tca),date(first_visit_after_IIT)) between 3 and 5\n" +
                "     )e;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientsExperiencedIIT3To5Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Experienced IIT 3-5 months before returning to Tx");
        return cd;

    }

    /**
     * Number of Patients Experienced IIT for atleast 6 months before returning to Tx
     * @return
     */
    public CohortDefinition patientsExperiencedIITAtleast6Months() {

        String sqlQuery = "select e.patient_id\n" +
                "from (\n" +
                "     select fup_prev_period.patient_id,\n" +
                "            max(fup_prev_period.visit_date) as prev_period_latest_vis_date,\n" +
                "            mid(max(concat(fup_prev_period.visit_date,fup_prev_period.next_appointment_date)),11) as prev_period_latest_tca,\n" +
                "            max(d.visit_date) as date_discontinued,\n" +
                "            d.patient_id as disc_patient,\n" +
                "            fup_reporting_period.first_visit_after_IIT as first_visit_after_IIT\n" +
                "     from kenyaemr_etl.etl_patient_hiv_followup fup_prev_period\n" +
                "            join (select fup_reporting_period.patient_id,min(fup_reporting_period.visit_date) as first_visit_after_IIT from kenyaemr_etl.etl_patient_hiv_followup fup_reporting_period where fup_reporting_period.visit_date >= date_sub(:endDate , interval 3 MONTH) group by fup_reporting_period.patient_id)fup_reporting_period on fup_reporting_period.patient_id = fup_prev_period.patient_id\n" +
                "            left outer JOIN\n" +
                "              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "               where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "               group by patient_id\n" +
                "              ) d on d.patient_id = fup_prev_period.patient_id\n" +
                "     where fup_prev_period.visit_date < date_sub(date(:endDate) , interval 3 MONTH)\n" +
                "     group by patient_id\n" +
                "     having\n" +
                "         ((date(prev_period_latest_tca) < date(:endDate) and date(prev_period_latest_vis_date) < date(prev_period_latest_tca))) and ((date(prev_period_latest_tca) > date(date_discontinued) and date(prev_period_latest_vis_date) > date(date_discontinued)) or disc_patient is null) and\n" +
                "         timestampdiff(day, date(prev_period_latest_tca),date(:startDate)) > 30 and timestampdiff(MONTH, date(prev_period_latest_tca),date(first_visit_after_IIT)) >= 6\n" +
                "     )e;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientsExperiencedIITAtleast6Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Experienced IIT for atleast 6 months before returning to Tx");
        return cd;
    }

    /**
     * Experienced IIT for at least 3 months
     * @return
     */
    public CohortDefinition patientsExperiencedIITAtleast3Months() {
        String sqlQuery = "select e.patient_id\n" +
                "from (\n" +
                "     select fup_prev_period.patient_id,\n" +
                "            max(fup_prev_period.visit_date) as prev_period_latest_vis_date,\n" +
                "            mid(max(concat(fup_prev_period.visit_date,fup_prev_period.next_appointment_date)),11) as prev_period_latest_tca,\n" +
                "            max(d.visit_date) as date_discontinued,\n" +
                "            d.patient_id as disc_patient,\n" +
                "            fup_reporting_period.first_visit_after_IIT as first_visit_after_IIT\n" +
                "     from kenyaemr_etl.etl_patient_hiv_followup fup_prev_period\n" +
                "            join (select fup_reporting_period.patient_id,min(fup_reporting_period.visit_date) as first_visit_after_IIT from kenyaemr_etl.etl_patient_hiv_followup fup_reporting_period where fup_reporting_period.visit_date >= date_sub(:endDate , interval 3 MONTH) group by fup_reporting_period.patient_id)fup_reporting_period on fup_reporting_period.patient_id = fup_prev_period.patient_id\n" +
                "            left outer JOIN\n" +
                "              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "               where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "               group by patient_id\n" +
                "              ) d on d.patient_id = fup_prev_period.patient_id\n" +
                "     where fup_prev_period.visit_date < date_sub(date(:endDate) , interval 3 MONTH)\n" +
                "     group by patient_id\n" +
                "     having\n" +
                "         ((date(prev_period_latest_tca) < date(:endDate) and date(prev_period_latest_vis_date) < date(prev_period_latest_tca))) and ((date(prev_period_latest_tca) > date(date_discontinued) and date(prev_period_latest_vis_date) > date(date_discontinued)) or disc_patient is null) and\n" +
                "         timestampdiff(day, date(prev_period_latest_tca),date(:startDate)) > 30 and timestampdiff(MONTH, date(prev_period_latest_tca),date(first_visit_after_IIT)) >= 3\n" +
                "     )e;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientsExperiencedIITAtleast3Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients Experienced IIT for atleast 3 months before returning to Tx");
        return cd;
    }
    /**
     * Patients with CD4 count >= 200 within the reporting period after > 3 months IIT
     * @return
     */
    public CohortDefinition cd4Count200AndAboveAfterIITOver3Months() {
        String sqlQuery = "select a.patient_id\n" +
                "from (select x.patient_id,\n" +
                "             mid(min(concat(coalesce(date(date_test_requested), date(visit_date)),\n" +
                "                            if(lab_test = 167718 and test_result = 1254, '>200',\n" +
                "                               if(lab_test = 167718 and test_result = 167717, '<=200',\n" +
                "                                  if(lab_test = 5497, test_result, ''))),\n" +
                "                            '')),\n" +
                "                 11) as cd4_after_IIT,\n" +
                "             mid(max(concat(date(x.visit_date),x.order_reason)),11) as order_reason\n" +
                "      from kenyaemr_etl.etl_laboratory_extract x\n" +
                "      where lab_test in (167718, 5497)\n" +
                "        and date(x.date_test_requested) between date(:startDate) and date(:endDate)\n" +
                "      GROUP BY x.patient_id\n" +
                "      having (cd4_after_IIT >= 200\n" +
                "          or cd4_after_IIT = '>200') and order_reason = 160740) a;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("cd4Count200AndAboveAfterIITOver3Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients with CD4 count >= 200 within the reporting period after > 3 months IIT");
        return cd;
    }
    /**
     * Patients with CD4 count <200 within the reporting period after > 3 months IIT
     * @return
     */
    public CohortDefinition cd4CountBelow200AfterIITOver3Months() {
        String sqlQuery = "select a.patient_id\n" +
                "from (select x.patient_id,\n" +
                "             mid(max(concat(coalesce(date(date_test_requested), date(visit_date)),\n" +
                "                            if(lab_test = 167718 and test_result = 1254, '>200',\n" +
                "                               if(lab_test = 167718 and test_result = 167717, '<=200',\n" +
                "                                  if(lab_test = 5497, test_result, ''))),\n" +
                "                            '')),\n" +
                "                 11) as cd4_after_IIT,\n" +
                "          mid(max(concat(date(x.visit_date),x.order_reason)),11) as order_reason\n" +
                "      from kenyaemr_etl.etl_laboratory_extract x\n" +
                "      where lab_test in (167718, 5497) and date(x.date_test_requested) between date(:startDate) and date(:endDate)\n" +
                "      GROUP BY x.patient_id\n" +
                "      having (cd4_after_IIT < 200\n" +
                "          or cd4_after_IIT = '<=200') and order_reason = 160740) a;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("cd4CountBelow200AfterIITOver3Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients with CD4 count < 200 within the reporting period after > 3 months IIT");
        return cd;
    }

    /**
     * Number of ART patients who experienced IIT during any previous reporting period, who successfully restarted ARVs within the reporting period and remained on treatment until the end of the reporting period
     * @return
     */
    public CohortDefinition txRTT() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("experiencedIITPreviousReportingPeriod", ReportUtils.map(experiencedIITPreviousReportingPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND experiencedIITPreviousReportingPeriod");
        return cd;
    }

    /**
     * Patient re-enrolled in HIV program
     * @return
     */
    public CohortDefinition reenrolledInHIV() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_hiv_enrollment e where e.patient_type = 159833 and date(e.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("reenrolledInHIV");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients re-enrolled in HIV");
        return cd;
    }

    /**
     * TXRTT - Re-enrolled during the reporting period
     * @return
     */
    public CohortDefinition txCurrMissingInPreviousPeriodTxCurrReenrollment() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurrThisPeriodNotTXCurrPreviousPeriod", ReportUtils.map(txCurrThisPeriodNotTXCurrPreviousPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("reenrolledInHIV", ReportUtils.map(reenrolledInHIV(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txCurrThisPeriodNotTXCurrPreviousPeriod AND reenrolledInHIV");
        return cd;
    }
    /**
     * Number restarted Treatment during the reporting period with CD4 count <200
     * @return
     */
    public CohortDefinition txRTTCD4Below200() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txRTT", ReportUtils.map(txRTT(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsExperiencedIITAtleast3Months", ReportUtils.map(patientsExperiencedIITAtleast3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("cd4CountBelow200AfterIITOver3Months", ReportUtils.map(cd4CountBelow200AfterIITOver3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txRTT AND cd4CountBelow200AfterIITOver3Months AND patientsExperiencedIITAtleast3Months");
        return cd;
    }

    /**
     * Number restarted Treatment during the reporting period with CD4 count >=200
     * @return
     */
    public CohortDefinition txRTTCD4200AndAbove() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txRTT", ReportUtils.map(txRTT(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsExperiencedIITAtleast3Months", ReportUtils.map(patientsExperiencedIITAtleast3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("cd4Count200AndAboveAfterIITOver3Months", ReportUtils.map(cd4Count200AndAboveAfterIITOver3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txRTT AND patientsExperiencedIITAtleast3Months AND cd4Count200AndAboveAfterIITOver3Months");
        return cd;
    }

    /**
     * Number restarted Treatment during the reporting period with CD4 unknown
     * @return
     */
    public CohortDefinition txRTTCD4Unknown() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txRTT", ReportUtils.map(txRTT(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("childrenAgedUnder5Years", ReportUtils.map(childrenAgedUnder5Years(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("cd4CountBelow200AfterIITOver3Months", ReportUtils.map(cd4CountBelow200AfterIITOver3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("cd4Count200AndAboveAfterIITOver3Months", ReportUtils.map(cd4Count200AndAboveAfterIITOver3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txRTTIneligibleForCD4", ReportUtils.map(txRTTIneligibleForCD4(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txRTT AND (childrenAgedUnder5Years OR NOT (cd4CountBelow200AfterIITOver3Months OR cd4Count200AndAboveAfterIITOver3Months OR txRTTIneligibleForCD4))");
        return cd;
    }
    /**
     * Number restarted Treatment during the reporting period not eligible for CD4
     * @return
     */
    public CohortDefinition txRTTIneligibleForCD4() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("childrenAgedUnder5Years", ReportUtils.map(childrenAgedUnder5Years(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txRTTIITBelow3Months", ReportUtils.map(txRTTIITBelow3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txRTTIITBelow3Months AND NOT childrenAgedUnder5Years");
        return cd;
    }
    /**
     * Number of KPs ART patients who experienced IIT during any previous reporting period, who successfully restarted ARVs within the reporting period and remained on treatment until the end of the reporting period
     * @return
     */
    public CohortDefinition txRTTKP(Integer kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("experiencedIITPreviousReportingPeriod", ReportUtils.map(experiencedIITPreviousReportingPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kp", ReportUtils.map(kpByKPType(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND kp AND experiencedIITPreviousReportingPeriod");
        return cd;
    }

    /**
     * TX_RTT by Duration of treatment interruption before returning to treatment after IIT for less than 3 months
     * @return
     */
    public CohortDefinition txRTTIITBelow3Months() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("experiencedIITPreviousReportingPeriod", ReportUtils.map(experiencedIITPreviousReportingPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientExperiencedIITUnder3Months", ReportUtils.map(patientExperiencedIITUnder3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND experiencedIITPreviousReportingPeriod AND patientExperiencedIITUnder3Months");
        return cd;
    }

    /**
     * TX_RTT_3_TO_5_MONTHS by Duration of treatment interruption before returning to treatment after IIT for 3-5 months
     * @return
     */
    public CohortDefinition txRTTIIT3To5Months() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("experiencedIITPreviousReportingPeriod", ReportUtils.map(experiencedIITPreviousReportingPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsExperiencedIIT3To5Months", ReportUtils.map(patientsExperiencedIIT3To5Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND experiencedIITPreviousReportingPeriod AND patientsExperiencedIIT3To5Months");
        return cd;
    }

    /**
     * TX_RTT by Duration of treatment interruption before returning to treatment after IIT for more than 3 months
     * @return
     */
    public CohortDefinition txRTTIITAtleast6Months() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("experiencedIITPreviousReportingPeriod", ReportUtils.map(experiencedIITPreviousReportingPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsExperiencedIITAtleast6Months", ReportUtils.map(patientsExperiencedIITAtleast6Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND experiencedIITPreviousReportingPeriod AND patientsExperiencedIITAtleast6Months");
        return cd;
    }

    /**
     * Current suppressed vl (Within 12 months)
     * @return
     */
    public CohortDefinition currentSuppressedVL() {
        String sqlQuery = "select a.patient_id\n" +
                "from (select b.patient_id,\n" +
                "             b.latest_visit_date,\n" +
                "             b.order_date,\n" +
                "             b.vl_result,\n" +
                "             b.order_reason as order_reason\n" +
                "      from (select f.patient_id                                        as patient_id,\n" +
                "                   max(f.visit_date)                                   as latest_visit_date,\n" +
                "                   x.order_date,\n" +
                "                   x.vl_result,\n" +
                "                   x.order_reason\n" +
                "            from kenyaemr_etl.etl_patient_hiv_followup f\n" +
                "                     inner join (select x.patient_id                                       as patient_id,\n" +
                "                                        max(x.date_test_requested)                         as order_date,\n" +
                "                                        mid(max(concat(x.visit_date, x.lab_test)), 11)     as lab_test,\n" +
                "                                        mid(max(concat(x.visit_date, x.order_reason)), 11) as order_reason,\n" +
                "                                        if(mid(max(concat(x.visit_date, x.lab_test)), 11) = 856,\n" +
                "                                           mid(max(concat(x.visit_date, x.test_result)), 11), if(\n" +
                "                                                           mid(max(concat(x.visit_date, x.lab_test)), 11) =\n" +
                "                                                           1305 and\n" +
                "                                                           mid(max(concat(x.visit_date, x.test_result)), 11) =\n" +
                "                                                           1302, 'LDL', ''))               as vl_result\n" +
                "                                 from kenyaemr_etl.etl_laboratory_extract x\n" +
                "                                 where x.lab_test in (1305, 856)\n" +
                "                                   and x.visit_date <= date(:endDate)\n" +
                "                                 group by x.patient_id) x on f.patient_id = x.patient_id\n" +
                "            where f.visit_date <= date(order_date)\n" +
                "            group by f.patient_id) b\n" +
                "      group by b.patient_id\n" +
                "      having b.order_date between\n" +
                "          date_sub(:endDate, interval 12 MONTH) and date(:endDate)\n" +
                "         and (b.vl_result < 1000 or b.vl_result = 'LDL')) a;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_SUPP_PREG");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients with current Suppressed VL");
        return cd;

    }

    /**
     * Women on ART and pregnant during their current suppressed VL test
     * */
    public CohortDefinition pregnantOnARTWithSuppressedVLLast12Months() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txpvlsDenominatorPregnant", ReportUtils.map(txpvlsDenominatorPregnant(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentSuppressedVL", ReportUtils.map(currentSuppressedVL(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txpvlsDenominatorPregnant AND currentSuppressedVL");
        return cd;
    }

     /**
     * Women on ART and breastfeeding during their current suppressed VL test
     * */
    public CohortDefinition breastfeedingOnARTSuppressedVLLast12Months() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txpvlsDenominatorBreastfeeding", ReportUtils.map(txpvlsDenominatorBreastfeeding(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentSuppressedVL", ReportUtils.map(currentSuppressedVL(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txpvlsDenominatorBreastfeeding AND currentSuppressedVL");
        return cd;
    }

    /**
     * TX_PVLS (suppressed / numerator)
     * @param
     * @return
     */
    public CohortDefinition currentSuppVLResult() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select a.patient_id\n" +
                "from (select b.patient_id,\n" +
                "             b.latest_visit_date,\n" +
                "             b.vl_result,\n" +
                "             b.order_reason as order_reason\n" +
                "      from (select x.patient_id                                                                             as patient_id,\n" +
                "                   max(x.visit_date)                                                                        as latest_visit_date,\n" +
                "                   mid(max(concat(x.visit_date, x.lab_test)), 11)                                           as lab_test,\n" +
                "                   mid(max(concat(x.visit_date, x.order_reason)), 11)                                       as order_reason,\n" +
                "                   if(mid(max(concat(x.visit_date, x.lab_test)), 11) = 856,\n" +
                "                      mid(max(concat(x.visit_date, x.test_result)), 11), if(\n" +
                "                                      mid(max(concat(x.visit_date, x.lab_test)), 11) = 1305 and\n" +
                "                                      mid(max(concat(x.visit_date, x.test_result)), 11) = 1302, 'LDL', '')) as vl_result\n" +
                "            from kenyaemr_etl.etl_laboratory_extract x\n" +
                "            where x.lab_test in (1305, 856)\n" +
                "              and x.visit_date <= date(:endDate)\n" +
                "            group by x.patient_id) b\n" +
                "      group by b.patient_id\n" +
                "      having b.latest_visit_date between\n" +
                "          date_sub(:endDate, interval 12 MONTH) and date(:endDate)\n" +
                "         and (b.vl_result < 1000 or b.vl_result = 'LDL')) a;";
        cd.setName("currentSuppVLResult");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("currentSuppVLResult");

        return cd;
    }
    /**
     * Pregnant with VL order
     * @param
     * @return
     */
    public CohortDefinition pregnantVLOrder() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select a.patient_id\n" +
                "from (select b.patient_id, b.latest_visit_date, b.pregnant_status\n" +
                "      from (select f.patient_id                                           as patient_id,\n" +
                "                   max(f.visit_date)                                      as latest_visit_date,\n" +
                "                   mid(max(concat(f.visit_date, f.pregnancy_status)), 11) as pregnant_status,\n" +
                "                   l.order_date\n" +
                "            from kenyaemr_etl.etl_patient_hiv_followup f\n" +
                "            inner join (select patient_id,date_test_requested as order_date,order_reason\n" +
                "                        from kenyaemr_etl.etl_laboratory_extract\n" +
                "                        where lab_test in (1305, 856)\n" +
                "                          and visit_date between\n" +
                "                            date_sub(:endDate, interval 12 MONTH) and date(:endDate)\n" +
                "                          )l on f.patient_id = l.patient_id\n" +
                "            where f.visit_date <= date(order_date)\n" +
                "            group by f.patient_id) b\n" +
                "      group by b.patient_id\n" +
                "      having b.latest_visit_date between\n" +
                "          date_sub(:endDate, interval 12 MONTH) and date(:endDate)\n" +
                "         and pregnant_status = 1065) a;";
        cd.setName("Pregnant");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Pregnant during last VL test");

        return cd;
    }

    public CohortDefinition breastfeedingVLOrder() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select a.patient_id\n" +
                "from (select b.patient_id, b.latest_visit_date, b.breastfeeding_status\n" +
                "      from (select f.patient_id                                           as patient_id,\n" +
                "                   max(f.visit_date)                                      as latest_visit_date,\n" +
                "                   mid(max(concat(f.visit_date, f.breastfeeding)), 11) as breastfeeding_status,\n" +
                "                   l.order_date\n" +
                "            from kenyaemr_etl.etl_patient_hiv_followup f\n" +
                "                     inner join (select patient_id,date_test_requested as order_date,order_reason\n" +
                "                                 from kenyaemr_etl.etl_laboratory_extract\n" +
                "                                 where lab_test in (1305, 856)\n" +
                "                                   and visit_date between\n" +
                "                                     date_sub(:endDate, interval 12 MONTH) and date(:endDate)\n" +
                "                                   )l on f.patient_id = l.patient_id\n" +
                "            where f.visit_date <= date(order_date)\n" +
                "            group by f.patient_id) b\n" +
                "      group by b.patient_id\n" +
                "      having b.latest_visit_date between\n" +
                "          date_sub(:endDate, interval 12 MONTH) and date(:endDate)\n" +
                "         and breastfeeding_status = 1065) a;";
        cd.setName("BF");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("BF");

        return cd;
    }

    /**
     * TX_PVLS NUMERATOR
     * @return
     */
    public CohortDefinition onARTSuppVLAgeSex() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentSuppVLResult", ReportUtils.map(currentSuppVLResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTXAtleast3Months", ReportUtils.map(patientInTXAtleast3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND currentSuppVLResult AND patientInTXAtleast3Months");
        return cd;
    }

    /**
     * Gets KPs by type at ART initiation from HIV follow-up visit
     * @param kpType
     * @return
     */
    public CohortDefinition kpByTypeAtARTInitiation(Integer kpType) {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select e.patient_id from (select e.patient_id,min(e.date_started) as date_started\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                "where e.program = 'HIV' and date_started between  date_sub(date(:endDate),interval 3 MONTH) and date(:endDate)\n" +
                "group by e.patient_id)e\n" +
                "inner join (select f.patient_id,f.visit_date,f.key_population_type from kenyaemr_etl.etl_patient_hiv_followup f where f.visit_date <= date(:endDate))f\n" +
                "on e.patient_id = f.patient_id and e.date_started = f.visit_date and f.key_population_type =" + kpType + ";";
        cd.setName("kpByTypeAtARTInitiation");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("KPs by type at ART initiation");

        return cd;
    }

    /**
     * Patients with VL results within the last 12 months
     * @return
     */
   /* public CohortDefinition currentVLResultLast12Months() {

        String sqlQuery = "select patient_id from kenyaemr_etl.etl_laboratory_extract where lab_test in (1305,856) and visit_date between\n" +
                "date_sub(:endDate,interval 12 MONTH) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("withVLResultLast12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("On ART with VL result within last 12 Months");
        return cd;
    }*/

    /**
     * Patients with VL results within the last 12 months
     * @return
     */
    public CohortDefinition currentVLLast12Months() {

        String sqlQuery = "select patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract\n" +
                "where lab_test in (1305, 856)\n" +
                "  and visit_date between\n" +
                "    date_add(date_sub(:endDate, interval 12 MONTH), interval 1 day) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("currentVLLast12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("On ART with VL result within last 12 Months");
        return cd;
    }

    /**Number of ART patients with a VL result documented in the medical or laboratory records/LIS within the past 12 months.
     * TX_PVLS DENOMINATOR
     * @return
     */
    public CohortDefinition txpvlsDenominator() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTXAtleast3Months", ReportUtils.map(patientInTXAtleast3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentVLLast12Months", ReportUtils.map(currentVLLast12Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND patientInTXAtleast3Months AND currentVLLast12Months");
        return cd;
    }

    /**Number of Pregnant ART patients with a VL result documented in the medical or laboratory records/LIS within the past 12 months.
     * TX_PVLS DENOMINATOR
     * @return
     */
    public CohortDefinition txpvlsDenominatorPregnant() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTXAtleast3Months", ReportUtils.map(patientInTXAtleast3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("pregnantVLOrder", ReportUtils.map(pregnantVLOrder(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND patientInTXAtleast3Months AND pregnantVLOrder");
        return cd;
    }
    public CohortDefinition txpvlsDenominatorBreastfeeding() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTXAtleast3Months", ReportUtils.map(patientInTXAtleast3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("breastfeedingVLOrder", ReportUtils.map(breastfeedingVLOrder(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND patientInTXAtleast3Months AND breastfeedingVLOrder");
        return cd;
    }

    /**
     * TX_PVLS NUMERATOR Indication by Key Population
     * @param kpType
     * @return
     */
    public CohortDefinition onARTKpWithSuppVLLast12Months(Integer kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("onARTSuppVLAgeSex", ReportUtils.map(onARTSuppVLAgeSex(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("keyPop", ReportUtils.map(kpByKPType(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("onARTSuppVLAgeSex AND keyPop");
        return cd;
    }

    public CohortDefinition kpOnARTWithVLLast12Months(Integer kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("keyPop", ReportUtils.map(kpByKPType(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txpvlsDenominator", ReportUtils.map(txpvlsDenominator(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("keyPop AND txpvlsDenominator");
        return cd;
    }

    /**
     * Patients current on ART at the beginning of the reporting period . This is a component of TX_ML
     * @return
     */
    public CohortDefinition currentOnARTAtStartOfReportingPeriod() {

        String sqlQuery = "select t.patient_id\n" +
                "               from (select fup.visit_date,\n" +
                "                            fup.patient_id,\n" +
                "                            max(e.visit_date)                                                      as enroll_date,\n" +
                "                            mid(max(concat(e.visit_date, e.patient_type)), 11)  as patient_type,\n" +
                "                            greatest(max(fup.visit_date), ifnull(max(d.visit_date), '0000-00-00')) as latest_vis_date,\n" +
                "                            greatest(mid(max(concat(fup.visit_date, fup.next_appointment_date)), 11),\n" +
                "                                     ifnull(max(d.visit_date), '0000-00-00'))                      as latest_tca,\n" +
                "                            d.patient_id                                                           as disc_patient,\n" +
                "                            d.effective_disc_date                                                  as effective_disc_date,\n" +
                "                            max(d.visit_date)                                                      as date_discontinued,\n" +
                "                            de.patient_id                                                          as started_on_drugs,\n" +
                "                            mid(max(concat(date(de.date_started), ifnull(de.discontinued, 0))), 11) as on_drugs\n" +
                "                     from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                              join kenyaemr_etl.etl_patient_demographics p on p.patient_id = fup.patient_id\n" +
                "                              join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id = e.patient_id\n" +
                "                              inner join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program = 'HIV' and\n" +
                "                                                                                date(date_started) < date(:endDate)\n" +
                "                              left outer JOIN\n" +
                "                          (select patient_id,\n" +
                "                                  coalesce(date(effective_discontinuation_date), visit_date) visit_date,\n" +
                "                                  max(date(effective_discontinuation_date)) as               effective_disc_date\n" +
                "                           from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                           where date(visit_date) < date(:startDate)\n" +
                "                             and program_name = 'HIV'\n" +
                "                           group by patient_id) d on d.patient_id = fup.patient_id\n" +
                "                     where fup.visit_date < date(:startDate)\n" +
                "                     group by patient_id\n" +
                "                     having patient_type != 164931 and on_drugs != 1\n" +
                "                        and ((((timestampdiff(DAY, date(latest_tca), date(:startDate)) <=\n" +
                "                                30) and\n" +
                "                               ((date(d.effective_disc_date) > date(:startDate) or\n" +
                "                                 date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                         and\n" +
                "                              (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or\n" +
                "                               disc_patient is null)\n" +
                "                         )\n" +
                "                         )) t;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("currentOnARTAtStartOfReportingPeriod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients current on ART at the beginning of the reporting period");
        return cd;

    }

    /**
     * Number of New On ART patients who are not Current on Art
     * A component of TxML
     * @return
     */
   /* public CohortDefinition newOnARTAndNotTxCur() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("newlyStartedArt", ReportUtils.map(startedOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("newlyStartedArt AND NOT txcurr");
        return cd;
    }
*/

    public CohortDefinition txCurrThisPeriodNotTXCurrPreviousPeriod() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("currentOnARTAtStartOfReportingPeriod", ReportUtils.map(currentOnARTAtStartOfReportingPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentlyOnART", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("currentlyOnART AND NOT currentOnARTAtStartOfReportingPeriod");
        return cd;
    }

    public CohortDefinition txCurrThisPeriodNotTXCurrPreviousPeriodNewOnART() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txCurrThisPeriodNotTXCurrPreviousPeriod", ReportUtils.map(txCurrThisPeriodNotTXCurrPreviousPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("startedOnART", ReportUtils.map(startedOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txCurrThisPeriodNotTXCurrPreviousPeriod AND startedOnART");
        return cd;
    }

    /**
     * Number of ART patients with no clinical contact since their last expected contact
     * @return
     */
    public CohortDefinition txML() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("currentOnARTAtStartOfReportingPeriod", ReportUtils.map(currentOnARTAtStartOfReportingPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("startedOnART", ReportUtils.map(startedOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentlyOnArt", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(currentOnARTAtStartOfReportingPeriod OR startedOnART) AND NOT currentlyOnArt");
        return cd;
    }

    /**
     * Patients who either trf out, died or stopped treatment
     * @return
     */
    public CohortDefinition patientsDiedTrfStoppedTx() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("trfOut", ReportUtils.map(transferredOutAndVerified(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("died", ReportUtils.map(patientsDiscontinuedOfDeath(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("stoppedTx", ReportUtils.map(patientStoppedTreatment(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("trfOut OR died OR stoppedTx");
        return cd;
    }

    /**
     * TX_ML patients by Treatment stop reason
     * @return
     */
    public CohortDefinition txmlPatientByTXStopReason() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("patientStoppedTreatment",
                ReportUtils.map(patientStoppedTreatment(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txML", ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("patientStoppedTreatment AND txML");
        return cd;
    }

    /**
     * TX_ML IIT KP patients in Tx for less than 3 months
     * @return
     */
    public CohortDefinition txMLIITKpUnder3MonthsInTx(Integer kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("kp", ReportUtils.map(kpByKPType(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txML", ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTXLessThan3Months", ReportUtils.map(patientInTXLessThan3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsDiedTrfStoppedTx", ReportUtils.map(patientsDiedTrfStoppedTx(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(txML AND kp AND patientInTXLessThan3Months) AND NOT patientsDiedTrfStoppedTx");
        return cd;
    }

    /**
     * TX_ML IIT KP patients in Tx for 3-5 months
     * @return
     */
    public CohortDefinition txMLIITKp3To5MonthsInTx(Integer kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("kp", ReportUtils.map(kpByKPType(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txML", ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTX3To5Months", ReportUtils.map(patientInTX3To5Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsDiedTrfStoppedTx", ReportUtils.map(patientsDiedTrfStoppedTx(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(txML AND kp AND patientInTX3To5Months) AND NOT patientsDiedTrfStoppedTx");
        return cd;
    }

    /**
     * TX_ML IIT KP patients in Tx for 6+ months
     * @return
     */
    public CohortDefinition txMLIITKpAtleast6Months(Integer kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("kp", ReportUtils.map(kpByKPType(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txML", ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTXAtleast6Months", ReportUtils.map(patientInTXAtleast6Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsDiedTrfStoppedTx", ReportUtils.map(patientsDiedTrfStoppedTx(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(txML AND kp AND patientInTXAtleast6Months) AND NOT patientsDiedTrfStoppedTx");
        return cd;
    }

    /**
     * TX_ML patients by death reason
     * @return
     */
    public CohortDefinition txmlPatientByCauseOfDeath(Integer causeOfDeath) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("patientByDeathReason", ReportUtils.map(patientByDeathReason(causeOfDeath), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txML", ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txML AND patientByDeathReason");
        return cd;
    }

    /**
     * TX_ML patients by specific death reason
     * @return
     */
    public CohortDefinition txMLSpecificCauseOfDeath(Integer specificCauseOfDeath) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("patientByTXStopSpecificDeathReason",
                ReportUtils.map(patientByTXStopSpecificDeathReason(specificCauseOfDeath), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txML", ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txML AND patientByTXStopSpecificDeathReason");
        return cd;
    }

    /**
     * TX_ML KP patients who died
     * @return
     */
    public CohortDefinition txmlKPSTransferredOut(Integer kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("kp", ReportUtils.map(kpByKPType(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txml", ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferredOutAndVerified", ReportUtils.map(transferredOutAndVerified(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txml AND kp AND transferredOutAndVerified");
        return cd;
    }

    /**
     * TX_ML KP patients who stopped treatment
     * @return
     */
    public CohortDefinition txmlKPStopReason(Integer kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("kp", ReportUtils.map(kpByKPType(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txml", ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientStoppedTreatment", ReportUtils.map(patientStoppedTreatment(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txml AND kp AND patientStoppedTreatment");
        return cd;
    }

    /**
     * TX_ML KP patients who died
     * @return
     */
    public CohortDefinition txmlKPPatientDied(Integer kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("kp", ReportUtils.map(kpByKPType(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txmlPatientDied", ReportUtils.map(txmlPatientDied(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txmlPatientDied AND kp");
        return cd;
    }

    /**
     *IIT After being on Treatment for <3 months
     * @param
     * @return
     */
    public CohortDefinition txMLIITUnder3MonthsInTx() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txML", ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTXLessThan3Months", ReportUtils.map(patientInTXLessThan3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsDiedTrfStoppedTx", ReportUtils.map(patientsDiedTrfStoppedTx(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(txML AND patientInTXLessThan3Months) AND NOT patientsDiedTrfStoppedTx");
        return cd;
    }

    /**
     *IIT After being on Treatment for 3-5 months
     * @param
     * @return
     */
    public CohortDefinition txMLIIT3To5MonthsInTx() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txML", ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTX3To5Months", ReportUtils.map(patientInTX3To5Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsDiedTrfStoppedTx", ReportUtils.map(patientsDiedTrfStoppedTx(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(txML AND patientInTX3To5Months) AND NOT patientsDiedTrfStoppedTx");
        return cd;
    }

    /**
     *IIT After being on Treatment for 6+ months
     * @param
     * @return
     */
    public CohortDefinition txMLIITAtleast6MonthsInTx() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txML", ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTXAtleast6Months", ReportUtils.map(patientInTXAtleast6Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsDiedTrfStoppedTx", ReportUtils.map(patientsDiedTrfStoppedTx(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(txML AND patientInTXAtleast6Months) AND NOT patientsDiedTrfStoppedTx");
        return cd;
    }

    public CohortDefinition txMLIIT() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txMLIITUnder3MonthsInTx", ReportUtils.map(txMLIITUnder3MonthsInTx(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txMLIIT3To5MonthsInTx", ReportUtils.map(txMLIIT3To5MonthsInTx(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txMLIITAtleast6MonthsInTx", ReportUtils.map(txMLIITAtleast6MonthsInTx(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(txMLIITUnder3MonthsInTx OR txMLIIT3To5MonthsInTx OR txMLIITAtleast6MonthsInTx");
        return cd;
    }

    /**
     *TX_ML due to transfer out
     * @param
     * @return
     */
    public CohortDefinition txmlTrfOut() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txML", ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferredOutAndVerified", ReportUtils.map(transferredOutAndVerified(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txML AND transferredOutAndVerified");
        return cd;
    }

    /**
     * Patient stopped treatment
     * @param
     * @return
     */
    public CohortDefinition patientStoppedTreatment() {

        String sqlQuery = "select d.patient_id\n" +
                "from kenyaemr_etl.etl_patient_demographics d\n" +
                "         left join (select dt.patient_id\n" +
                "                    from kenyaemr_etl.etl_ccc_defaulter_tracing dt\n" +
                "                    where dt.is_final_trace = 1267\n" +
                "                      and dt.true_status = 164435\n" +
                "                      and date(dt.visit_date) between date(:startDate) and date(:endDate)) dt\n" +
                "                   on d.patient_id = dt.patient_id\n" +
                "         left join\n" +
                "     (select dc.patient_id\n" +
                "      from kenyaemr_etl.etl_patient_program_discontinuation dc\n" +
                "      where dc.discontinuation_reason = 164349\n" +
                "        and date(dc.visit_date) between date(:startDate) and date(:endDate)) dc on d.patient_id = dc.patient_id\n" +
                "where dt.patient_id is not null\n" +
                "   or dc.patient_id is not null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("treatmentStoppedReason");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients who stopped treatment");
        return cd;
    }

    /**
     * Dead Patients discontinued within the reporting period
     * @return
     */
    public CohortDefinition patientsDiscontinuedOfDeath() {

        String sqlQuery = "select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d where d.program_name = 'HIV' and date(d.visit_date) between date(:startDate) and date(:endDate) and d.discontinuation_reason = 160034;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientsDiscontinuedOfDeath");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients discontinued because of death");
        return cd;

    }

    /**
     * Patients by cause of death
     * @param deathReason
     * @return
     */
    public CohortDefinition patientByDeathReason(Integer deathReason) {

        String sqlQuery = "select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d where d.program_name = 'HIV' and date(d.visit_date) between date(:startDate) and date(:endDate) and d.discontinuation_reason = 160034 and d.death_reason =" + deathReason + ";";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientByDeathReason");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients stopped treatment with death reason");
        return cd;

    }

    /**
     * Patients by specific cause of death
     * @param specificCauseOfDeath
     * @return
     */
    public CohortDefinition patientByTXStopSpecificDeathReason(Integer specificCauseOfDeath) {

        String sqlQuery = "select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d where d.program_name = 'HIV' and date(d.visit_date) between date(:startDate) and date(:endDate) and d.specific_death_cause= " + specificCauseOfDeath + ";";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("treatmentStopReason");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients by treatment stopped specific death reason");
        return cd;
    }

    /**
     * @return
     */
    public CohortDefinition txmlPatientDied() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txml", ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsDiscontinuedOfDeath", ReportUtils.map(patientsDiscontinuedOfDeath(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txml AND patientsDiscontinuedOfDeath");
        return cd;
    }

    /*HTS_INDEX_OFFERED Number of individuals who were offered index testing services */
    public CohortDefinition offeredIndexServices() {

        String sqlQuery = "select c.patient_related_to from kenyaemr_hiv_testing_patient_contact c where c.relationship_type in (971, 972, 1528, 162221, 163565, 970, 5617) and c.voided = 0 and date(c.date_created)\n" +
                "between date_sub( date(:endDate), INTERVAL  3 MONTH ) and date(:endDate) group by c.patient_related_to;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_OFFERED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of indexes offered Index Services");
        return cd;

    }

    //HTS_INDEX_ELICITED_MALE_CONTACTS_UNDER15
    public CohortDefinition htsIndexContactsElicited() {

        String sqlQuery = "select c.id from kenyaemr_hiv_testing_patient_contact c\n" +
                "where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "  and c.voided = 0" +
                "  and date(c.date_created) between date_sub( date(:endDate), INTERVAL  3 MONTH )and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_ELICITED_MALE_CONTACTS_UNDER15");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of male contacts under 15 years elicited");
        return cd;

    }

    /**
     * Number of patient contacts
     * @return
     */
    public CohortDefinition patientContactCohort() {
        String sqlQuery = "select patient_id from kenyaemr_hiv_testing_patient_contact c\n" +
                "                   where (c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617))\n" +
                "                         and c.patient_id is not NULL\n" +
                "                         and c.voided = 0\n" +
                "                    group by c.patient_id;\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PATIENT_CONTACT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of individuals who are patient contacts");
        return cd;
    }

    /**
     * Patient contacts accepted index testing
     * @return
     */
    public CohortDefinition contactsAcceptedIndexTesting() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("patientContactCohort", ReportUtils.map(patientContactCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("patientContactCohort AND testedIndexTesting");
        return cd;

    }

    /**
     * HIV Positive patient contacts
     * @return
     */
    public CohortDefinition hivPositiveContact() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("patientContactCohort", ReportUtils.map(patientContactCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResult", ReportUtils.map(positiveHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("patientContactCohort AND positiveHIVTestResult AND testedIndexTesting");
        return cd;

    }

    /**
     * Contacts tested HIV negative
     * @return
     */
    public CohortDefinition hivNegativeContact() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("patientContactCohort", ReportUtils.map(patientContactCohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialNegativeHIVTestResult", ReportUtils.map(initialNegativeHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("patientContactCohort AND initialNegativeHIVTestResult AND testedIndexTesting");
        return cd;

    }

    /**
     *     Number of individuals children or siblings who were reported HIV Negative using Index testing services
     *     Composition component - reported Negative
     */

    public CohortDefinition contactsReportedNegative() {

        String sqlQuery = "select c.id from kenyaemr_hiv_testing_patient_contact c  where c.relationship_type in(972, 1528) and c.voided = 0 and timestampdiff(YEAR,date(c.birth_date),date(:endDate)) between 0 and 14\n" +
                "           group by c.id\n" +
                "           having mid(max(concat(date(c.date_created),c.baseline_hiv_status)),11) ='Negative'\n" +
                "           and max(date(c.date_created)) between date_sub(date(:endDate), INTERVAL 3 MONTH )and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_DOCUMENTED_NEGATIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of children contacts with documented negative status below 14 yrs using Index testing services");
        return cd;

    }

    /**
     *Proportion of Children 1-14 yrs with reported Negative status and without any documented status in EMR
     * HTS_INDEX_DOCUMENTED_NEGATIVE Datim indicator
     * Composition startedOnART + prevOnIPTandCompleted
     * @return
     */
    public CohortDefinition contactsReportedNegativeUndocumented() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("contactsReportedNegative", ReportUtils.map(contactsReportedNegative(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("contactsReportedNegative");
        return cd;
    }

    //Known HIV Positive contacts
    public CohortDefinition knownPositiveContact() {

        String sqlQuery = "select c.id from kenyaemr_hiv_testing_patient_contact c where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617) and c.voided = 0 " +
                "and c.baseline_hiv_status ='Positive' and date(c.date_created) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_KNOWN_POSITIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of Contacts tested through Index Services");
        return cd;

    }

    /*Number Tested Negative PMTCT services ANC-1 only*/
    public CohortDefinition negativePMTCTANC1() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedNegativePmtctANC1", ReportUtils.map(testedNegativePmtctANC1(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedNegativePmtctANC1 AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic)");
        return cd;

    }

    /*Number Tested Positive PMTCT services ANC-1 only*/
    public CohortDefinition positivePMTCTANC1() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedPositivePmtctANC1", ReportUtils.map(testedPositivePmtctANC1(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedPositivePmtctANC1 AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic)");
        return cd;

    }

    /**
     * Number Tested Negative PMTCT services Post ANC-1 (Breastfeeding)
     * @return
     */
    public CohortDefinition negativePMTCTPostANC1PregnantAndLabourAndDelivery() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedNegativePmtctPostANC1BreastFeeding", ReportUtils.map(testedNegativePmtctPostANC1BreastFeeding(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedNegativePmtctPostANC1PregLabourAndDelivery", ReportUtils.map(testedNegativePmtctPostANC1PregLabourAndDelivery(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedNegativePmtctPostANC1PregLabourAndDelivery AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic OR testedNegativePmtctPostANC1BreastFeeding)");
        return cd;
    }
    /**
     * Number Tested Negative PMTCT services Post ANC-1 (Breastfeeding)
     * @return
     */
    public CohortDefinition negativePMTCTPostANC1Breastfeeding() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedNegativePmtctPostANC1BreastFeeding", ReportUtils.map(testedNegativePmtctPostANC1BreastFeeding(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedNegativePmtctPostANC1PregLabourAndDelivery", ReportUtils.map(testedNegativePmtctPostANC1PregLabourAndDelivery(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedNegativePmtctPostANC1BreastFeeding AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic or testedNegativePmtctPostANC1PregLabourAndDelivery)");
        return cd;
    }
    /**
     * Number Tested Positive PMTCT services Post ANC-1 (Breastfeeding)
     * @return
     */
    public CohortDefinition positivePMTCTPostANC1PregnantAndLabourAndDelivery() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedPositivePmtctPostANC1BreastFeeding", ReportUtils.map(testedPositivePmtctPostANC1BreastFeeding(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedPositivePmtctPostANC1PregLabourAndDelivery", ReportUtils.map(testedPositivePmtctPostANC1PregLabourAndDelivery(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedPositivePmtctPostANC1PregLabourAndDelivery AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic OR testedPositivePmtctPostANC1BreastFeeding)");
        return cd;
    }
    /**
     * Number Tested Positive PMTCT services Post ANC-1 (Breastfeeding)
     * @return
     */
    public CohortDefinition positivePMTCTPostANC1Breastfeeding() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedPositivePmtctPostANC1BreastFeeding", ReportUtils.map(testedPositivePmtctPostANC1BreastFeeding(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting", ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks", ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSTIClinic", ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedPositivePmtctPostANC1PregLabourAndDelivery", ReportUtils.map(testedPositivePmtctPostANC1PregLabourAndDelivery(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedPositivePmtctPostANC1BreastFeeding AND NOT (testedIndexTesting OR testedSocialNetworks OR testedSTIClinic or testedPositivePmtctPostANC1PregLabourAndDelivery)");
        return cd;
    }

    /**
     * Get KPs by type from the last HIV follow-up visit
     * @param kpType
     * @return
     */
    public CohortDefinition kpByKPType(Integer kpType) {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select c.client_id from kenyaemr_etl.etl_contact c\n" +
                "    where date (c.visit_date) <= date (:endDate)\n" +
                "    group by c.client_id having mid(max(concat(date (c.visit_date), c.key_population_type)), 11) = " + kpType + ";";
        cd.setName("kpByKPType");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("kpByKPType");

        return cd;
    }

    /**
     *TX_CURR_KP
     * @return
     */
    public CohortDefinition currentlyOnARTKP(Integer kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("keyPop",
                ReportUtils.map(kpByKPType(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND keyPop");
        return cd;
    }

    /**
     *TX_CURR_MMD
     * @return
     */
    public CohortDefinition currentlyOnARTUnder3MonthsMMD() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("tcaUnder3Months",
                ReportUtils.map(tcaUnder3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND tcaUnder3Months");
        return cd;
    }

    /**
     *TX_CURR_MMD
     * @return
     */
    public CohortDefinition currentlyOnART3To5MonthsMMD() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("tca3To5Months",
                ReportUtils.map(tca3To5Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND tca3To5Months");
        return cd;
    }

    /**
     *TX_CURR_MMD
     * @return
     */
    public CohortDefinition currentlyOnART6MonthsAndAboveMMD() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("tca6MonthsAndAbove",
                ReportUtils.map(tca6MonthsAndAbove(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND tca6MonthsAndAbove");
        return cd;
    }

    /**
     * Baseline CD4 < 200
     * @return
     */
    public CohortDefinition baselineCD4Under200() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select a.patient_id\n" +
                "from (select x.patient_id,\n" +
                "             mid(min(concat(coalesce(date(date_test_requested), date(visit_date)),\n" +
                "                            if(lab_test = 167718 and test_result = 1254, '>200',\n" +
                "                               if(lab_test = 167718 and test_result = 167717, '<=200',\n" +
                "                                  if(lab_test = 5497, test_result, ''))),\n" +
                "                            '')),\n" +
                "                 11) as baseline_cd4\n" +
                "      from kenyaemr_etl.etl_laboratory_extract x\n" +
                "      where lab_test in (167718, 5497) and date(x.date_test_requested) <= date(:endDate)\n" +
                "      GROUP BY x.patient_id\n" +
                "      having baseline_cd4 < 200\n" +
                "          or baseline_cd4 = '<=200') a;";
        cd.setName("baselineCD4Under200");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Baseline CD4 < 200");
        return cd;
    }
    /**
     * Baseline CD4 > 200
     * @return
     */
    public CohortDefinition baselineCD4200AndAbove() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select a.patient_id\n" +
                "from (select x.patient_id,\n" +
                "             mid(min(concat(coalesce(date(date_test_requested), date(visit_date)),\n" +
                "                            if(lab_test = 167718 and test_result = 1254, '>200',\n" +
                "                               if(lab_test = 167718 and test_result = 167717, '<=200',\n" +
                "                                  if(lab_test = 5497, test_result, ''))),\n" +
                "                            '')),\n" +
                "                 11) as baseline_cd4\n" +
                "      from kenyaemr_etl.etl_laboratory_extract x\n" +
                "      where lab_test in (167718, 5497)\n" +
                "        and date(x.date_test_requested) <= date(:endDate)\n" +
                "      GROUP BY x.patient_id\n" +
                "      having baseline_cd4 >= 200\n" +
                "          or baseline_cd4 = '>200') a;";
        cd.setName("baselineCD4200AndAbove");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Baseline CD4 > 200");
        return cd;
    }

    /**
     * Children aged under 5 years
     * @return
     */
    public CohortDefinition childrenAgedUnder5Years() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select d.patient_id from kenyaemr_etl.etl_patient_demographics d where timestampdiff(YEAR, date(d.dob),date(:endDate)) < 5;";
        cd.setName("childrenAgedUnder5Years");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Children aged under 5 years");
        return cd;
    }
    /**
     * Newly Started ART baseline CD4 < 200
     * @return
     */
    public CohortDefinition newlyStartedARTCD4Under200() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("newlyStartedArt", ReportUtils.map(startedOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("baselineCD4Under200", ReportUtils.map(baselineCD4Under200(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("newlyStartedArt AND baselineCD4Under200");
        return cd;
    }

    /**
     * Newly Started ART baseline CD4 >= 200
     * @return
     */
    public CohortDefinition newlyStartedARTCD4200AndAbove() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("newlyStartedArt", ReportUtils.map(startedOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("baselineCD4200AndAbove", ReportUtils.map(baselineCD4200AndAbove(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("newlyStartedArt AND baselineCD4200AndAbove");
        return cd;
    }

    /**
     * Newly Started ART baseline CD4 Unknown
     * @return
     */
    public CohortDefinition newlyStartedARTCD4Unknown() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("newlyStartedArt", ReportUtils.map(startedOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("baselineCD4Under200", ReportUtils.map(baselineCD4Under200(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("baselineCD4200AndAbove", ReportUtils.map(baselineCD4200AndAbove(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("childrenAgedUnder5Years", ReportUtils.map(childrenAgedUnder5Years(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("newlyStartedArt AND (childrenAgedUnder5Years OR NOT (baselineCD4200AndAbove OR baselineCD4200AndAbove))");
        return cd;
    }
    /**
     * TX_PVLS NUMERATOR BY KP TEST AND KP TYPE
     * @param kpType
     * @return
     */
    public CohortDefinition kpNewlyStartedART(Integer kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("kpByTypeAtARTInitiation",
                ReportUtils.map(kpByTypeAtARTInitiation(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("newlyStartedArt", ReportUtils.map(startedOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("newlyStartedArt AND kpByTypeAtARTInitiation");
        return cd;
    }

    /**
     * Create dis-aggregations by number of months of drugs dispensed
     * TX_CURR_UNDER_3_MONTHS_MMD
     * @return
     */
    public CohortDefinition tcaUnder3Months() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select f.patient_id from (select f.patient_id,timestampdiff(DAY,max(f.visit_date),mid(max(concat(f.visit_date,f.next_appointment_date)),11)) days_tca from kenyaemr_etl.etl_patient_hiv_followup f\n" +
                "    where f.visit_date <= date (:endDate) and f.next_appointment_date is not null group by f.patient_id having days_tca < 90) f;";
        cd.setName("TX_CURR_UNDER_3_MONTHS_MMD");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Under 3 Months tca");
        return cd;
    }

    /**
     * Create dis-aggregations by number of months of drugs dispensed
     * TX_CURR_3_TO_5_MONTHS_TCA
     * @return
     */
    public CohortDefinition tca3To5Months() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select f.patient_id from (select f.patient_id,timestampdiff(DAY,max(f.visit_date),mid(max(concat(f.visit_date,f.next_appointment_date)),11)) days_tca from kenyaemr_etl.etl_patient_hiv_followup f\n" +
                            "where f.visit_date <= date(:endDate) and f.next_appointment_date is not null group by f.patient_id having days_tca between 90 and 179)f;";
        cd.setName("TX_CURR_3_TO_5_MONTHS_TCA");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Under 3 to 5 Months tca");
        return cd;
    }

    /**
     * Create dis-aggregations by number of months of drugs dispensed
     * TX_CURR_6_AND_ABOVE_MONTHS_TCA
     * @return
     */
    public CohortDefinition tca6MonthsAndAbove() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select f.patient_id from (select f.patient_id,timestampdiff(DAY,max(f.visit_date),mid(max(concat(f.visit_date,f.next_appointment_date)),11)) days_tca from kenyaemr_etl.etl_patient_hiv_followup f\n" +
                            "where f.visit_date <= date (:endDate) and f.next_appointment_date is not null group by f.patient_id having days_tca >= 180) f;";
        cd.setName("TX_CURR_6_AND_ABOVE_MONTHS_TCA");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("6 months and above tca");
        return cd;
    }

    /**
     * Patients previously enrolled in PrEP (more than three months before effective date)
     *
     * @return
     */
    public CohortDefinition previouslyOnPrEP() {

        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_prep_enrolment e group by e.patient_id having max(e.visit_date) < date(:startDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("previouslyOnPrEP");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients previously enrolled in PrEP");
        return cd;

    }

    /**
     * Patients re-enrolled enrolled in PrEP within the reporting period
     *
     * @return
     */
    public CohortDefinition reenrolledOnPrEP() {

        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_prep_enrolment e group by e.patient_id having min(e.visit_date) < date(:startDate) and max(e.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("reenrolledOnPrEP");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients re-enrolled in PrEP");
        return cd;

    }

    /**
     * Patients with a PrEP followup visit within the reporting period
     *
     * @return
     */
    public CohortDefinition patientWithPrEPFollowup() {

        String sqlQuery = "select f.patient_id from kenyaemr_etl.etl_prep_followup f where f.visit_date between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientWithPrEPFollowup");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients re-enrolled in PrEP");
        return cd;
    }

    /**
     * Patients with a PrEP followup visit within the reporting period and reported pregnancy
     *
     * @return
     */
    public CohortDefinition pregnantPatientInPrEPFollowup() {

        String sqlQuery = "select f.patient_id from kenyaemr_etl.etl_prep_followup f group by f.patient_id having max(f.visit_date) between date(:startDate) and date(:endDate) and mid(max(concat(f.visit_date,f.pregnant)),11)= 'Yes';";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("pregnantPatientInPrEPFollowup");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Pregnant PrEP client during followup");
        return cd;
    }

    /**
     * Patients with a PrEP followup visit within the reporting period and reported breastfeeding
     *
     * @return
     */
    public CohortDefinition breastfeedingPatientInPrEPFollowup() {

        String sqlQuery = "select f.patient_id from kenyaemr_etl.etl_prep_followup f group by f.patient_id having max(f.visit_date) between date(:startDate) and date(:endDate) and mid(max(concat(f.visit_date,f.breastfeeding)),11)= 'Yes';";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("breastfeedingPatientInPrEPFollowup");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Breastfeeding PrEP client during followup");
        return cd;
    }

    /**
     * Patients with a PrEP followup visit and tested HIV Negative
     *
     * @return
     */
    /**
     * Patients with a PrEP followup visit and not tested for HIV during that visit
     *
     * @return
     */
    public CohortDefinition patientInPrEPFollowupNoHIVTest() {

        String sqlQuery = " select f.patient_id from kenyaemr_etl.etl_prep_followup f\n" +
                "    left join (select t.patient_id as hts_done, t.visit_date as hts_date, t.final_test_result from kenyaemr_etl.etl_hts_test t where t.visit_date <= date(:endDate))t on f.patient_id = t.hts_done and f.visit_date = t.hts_date\n" +
                "    where t.hts_done is null\n" +
                "    group by f.patient_id\n" +
                "    having max(f.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientInPrEPFollowupHIVPositive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("PrEP patient tested HIV Positive during PrEP visit");
        return cd;
    }

    /**
     * Patients with a PrEP followup visit and Positive HIV test result during that visit
     *
     * @return
     */
    public CohortDefinition patientInPrEPFollowupHIVPositive() {

        String sqlQuery = "select f.patient_id from kenyaemr_etl.etl_prep_followup f\n" +
                "inner join (select t.patient_id as hts_done, t.visit_date as hts_date, t.final_test_result from kenyaemr_etl.etl_hts_test t where t.visit_date <= date(:endDate))t on f.patient_id = t.hts_done and f.visit_date = t.hts_date\n" +
                "where t.final_test_result = 'Positive' \n" +
                "group by f.patient_id\n" +
                "having max(f.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientInPrEPFollowupHtsStatus");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("PrEP patient by HTS status");
        return cd;
    }

    /**
     * Patients with a PrEP followup visit and Negative HIV test result during that visit
     *
     * @return
     */
    public CohortDefinition patientInPrEPFollowupHIVNegative() {

        String sqlQuery = "select f.patient_id from kenyaemr_etl.etl_prep_followup f\n" +
                "inner join (select t.patient_id as hts_done, t.visit_date as hts_date, t.final_test_result from kenyaemr_etl.etl_hts_test t where t.visit_date <= date(:endDate))t on f.patient_id = t.hts_done and f.visit_date = t.hts_date\n" +
                "where t.final_test_result = 'Negative' \n" +
                "group by f.patient_id\n" +
                "having max(f.visit_date) between date(:startDate) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientInPrEPFollowupHtsStatus");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("PrEP patient by HTS status");
        return cd;
    }

    /**
     * Patients with a PrEP followup visit and their HIV test status during that visit
     *
     * @return
     */
    public CohortDefinition patientInPrEPByKPType(Integer kpType) {

        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_prep_enrolment e where e.visit_date <= date(:endDate) group by e.patient_id having mid(max(concat(e.visit_date,e.kp_type)),11) = " + kpType + ";";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("patientInPrEPByKPType");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("PrEP patient by KP type");
        return cd;
    }

    /**
     * Number of individuals who were already enrolled on oral antiretroviral pre-exposure prophylaxis (PrEP) to prevent HIV infection and came for PrEP followup or re-initiation during the reporting period
     * PrEP_CT indicator
     * @return
     */
    public CohortDefinition prepCT() {

        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("previouslyOnPrEP", ReportUtils.map(previouslyOnPrEP(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("reenrolledOnPrEP", ReportUtils.map(reenrolledOnPrEP(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientWithPrEPFollowup", ReportUtils.map(patientWithPrEPFollowup(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(previouslyOnPrEP AND patientWithPrEPFollowup) OR reenrolledOnPrEP");
        return cd;

    }

    /**
     * Number of individuals who were already enrolled on oral antiretroviral pre-exposure prophylaxis (PrEP) to prevent HIV infection and came for PrEP followup or re-initiation during the reporting period disagreggated by HIV Positive status
     * PrEP_CT_HIV_POS indicator
     * @return
     */
    public CohortDefinition prepCTByHIVPositiveStatus() {

        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("prepCT", ReportUtils.map(prepCT(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInPrEPFollowupHIVPositive", ReportUtils.map(patientInPrEPFollowupHIVPositive(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("prepCT AND patientInPrEPFollowupHIVPositive");
        return cd;
    }

    /**
     * Number of individuals who were already enrolled on oral antiretroviral pre-exposure prophylaxis (PrEP) to prevent HIV infection and came for PrEP followup or re-initiation during the reporting period disagreggated by HIV Negative status
     * PrEP_CT_HIV_NEG indicator
     * @return
     */
    public CohortDefinition prepCTByHIVNegativeStatus() {

        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("prepCT", ReportUtils.map(prepCT(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInPrEPFollowupHIVNegative", ReportUtils.map(patientInPrEPFollowupHIVNegative(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("prepCT AND patientInPrEPFollowupHIVNegative");
        return cd;
    }

    /**
     * Number of KP individuals who were already enrolled on oral antiretroviral pre-exposure prophylaxis (PrEP) to prevent HIV infection and came for PrEP followup or re-initiation during the reporting period
     * PrEP_CT_KP indicator
     * @return
     */
    public CohortDefinition newlyEnrolledInPrEPKP(Integer kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("newlyEnrolledInPrEP", ReportUtils.map(newlyEnrolledInPrEP(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInPrEPByKPType", ReportUtils.map(patientInPrEPByKPType(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("newlyEnrolledInPrEP AND patientInPrEPByKPType");
        return cd;

    }

    /**
     * Number of individuals who were already enrolled on oral antiretroviral pre-exposure prophylaxis (PrEP) to prevent HIV infection and came for PrEP followup or re-initiation during the reporting period disagreggated by HIV status
     * PrEP_CT_HIV_OTHER indicator
     * @return
     */
    public CohortDefinition prepCTNotTestedForHIV() {

        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("prepCT", ReportUtils.map(prepCT(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInPrEPFollowupNoHIVTest", ReportUtils.map(patientInPrEPFollowupNoHIVTest(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("prepCT AND patientInPrEPFollowupNoHIVTest");
        return cd;
    }

    /**
     * Number of KP individuals who were already enrolled on oral antiretroviral pre-exposure prophylaxis (PrEP) to prevent HIV infection and came for PrEP followup or re-initiation during the reporting period
     * PrEP_CT_KP indicator
     * @return
     */
    public CohortDefinition prepCTKP(Integer kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("prepCT", ReportUtils.map(prepCT(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInPrEPByKPType", ReportUtils.map(patientInPrEPByKPType(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("prepCT AND patientInPrEPByKPType");
        return cd;

    }

    /**
     * Number of individuals who were already enrolled on oral antiretroviral pre-exposure prophylaxis (PrEP) to prevent HIV infection and came for PrEP followup or re-initiation during the reporting period while pregnant
     * PrEP_CT_PG indicator
     * @return
     */
    public CohortDefinition prepCTPregnant() {

        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("prepCT", ReportUtils.map(prepCT(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("pregnantPatientInPrEPFollowup", ReportUtils.map(pregnantPatientInPrEPFollowup(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("prepCT AND pregnantPatientInPrEPFollowup");
        return cd;
    }

    /**
     * Number of individuals who were already enrolled on oral antiretroviral pre-exposure prophylaxis (PrEP) to prevent HIV infection and came for PrEP followup or re-initiation during the reporting period while Breastfeeding
     * PrEP_CT_BF indicator
     * @return
     */
    public CohortDefinition prepCTBreastfeeding() {

        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("prepCT", ReportUtils.map(prepCT(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("breastfeedingPatientInPrEPFollowup", ReportUtils.map(breastfeedingPatientInPrEPFollowup(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("prepCT AND breastfeedingPatientInPrEPFollowup");
        return cd;
    }
    /**
     * Returned and on Oral PrEP
     * @return
     */
    public CohortDefinition prepCTOnOralPrEP() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("prepCT", ReportUtils.map(prepCT(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("onOralPrEP", ReportUtils.map(onOralPrEP(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("prepCT AND onOralPrEP");
        return cd;
    }
    /**
     * Returned and on CAB-LA Injectable PrEP
     * @return
     */
    public CohortDefinition prepCTOnCABLAInjectablePrEP() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("prepCT", ReportUtils.map(prepCT(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("onCABLAInjectablePrEP", ReportUtils.map(onCABLAInjectablePrEP(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("prepCT AND onCABLAInjectablePrEP");
        return cd;
    }
    /**
     * Returned and on other forms of PrEP
     * @return
     */
    public CohortDefinition prepCTOnOtherPrEP() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("prepCT", ReportUtils.map(prepCT(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("onCABLAInjectablePrEP", ReportUtils.map(onCABLAInjectablePrEP(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("onOralPrEP", ReportUtils.map(onOralPrEP(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("prepCT AND NOT (onOralPrEP OR onCABLAInjectablePrEP)");
        return cd;
    }
    /**
     * Number of individuals who were newly enrolled on oral antiretroviral pre-exposure prophylaxis (PrEP) to prevent HIV infection in the reporting period
     * PrEP_NEWLY_ENROLLED indicator
     * @return
     */
    public CohortDefinition newlyEnrolledInPrEP() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_prep_enrolment e\n" +
                "group by e.patient_id\n" +
                "having min(date(e.visit_date)) between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PrEP_NEWLY_ENROLLED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Newly enrolled on PrEP");
        return cd;
    }
    public CohortDefinition onOralPrEP() {
        String sqlQuery = "select a.patient_id\n" +
                "from (select patient_id, mid(max(concat(latest_date, prep_type)), 11) as prep_type\n" +
                "      from (select f.patient_id                                          as patient_id,\n" +
                "                   max(f.visit_date)                                     as latest_date,\n" +
                "                   mid(max(concat(date(f.visit_date), f.prep_type)), 11) as prep_type\n" +
                "            from kenyaemr_etl.etl_prep_followup f\n" +
                "            where date(f.visit_date) between date(:startDate) and date(:endDate)\n" +
                "            group by f.patient_id\n" +
                "            union all\n" +
                "            select r.patient_id                                          as patient_id,\n" +
                "                   max(r.visit_date)                                     as latest_date,\n" +
                "                   mid(max(concat(date(r.visit_date), r.prep_type)), 11) as prep_type\n" +
                "            from kenyaemr_etl.etl_prep_monthly_refill r\n" +
                "            where date(r.visit_date) between date(:startDate) and date(:endDate)\n" +
                "            group by r.patient_id) a\n" +
                "      group by patient_id\n" +
                "      having prep_type = 'Daily Oral PrEP') a;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("onOralPrEP");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("On Oral PrEP");
        return cd;
    }
    public CohortDefinition onCABLAInjectablePrEP() {
        String sqlQuery = "select a.patient_id\n" +
                "from (select patient_id, mid(max(concat(latest_date, prep_type)), 11) as prep_type\n" +
                "      from (select f.patient_id                                          as patient_id,\n" +
                "                   max(f.visit_date)                                     as latest_date,\n" +
                "                   mid(max(concat(date(f.visit_date), f.prep_type)), 11) as prep_type\n" +
                "            from kenyaemr_etl.etl_prep_followup f\n" +
                "            where date(f.visit_date) between date(:startDate) and date(:endDate)\n" +
                "            group by f.patient_id\n" +
                "            union all\n" +
                "            select r.patient_id                                          as patient_id,\n" +
                "                   max(r.visit_date)                                     as latest_date,\n" +
                "                   mid(max(concat(date(r.visit_date), r.prep_type)), 11) as prep_type\n" +
                "            from kenyaemr_etl.etl_prep_monthly_refill r\n" +
                "            where date(r.visit_date) between date(:startDate) and date(:endDate)\n" +
                "            group by r.patient_id) a\n" +
                "      group by patient_id\n" +
                "      having prep_type = 'CAB-LA') a;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("onCABLAInjectablePrEP");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("On CAB-LA Injectable PrEP");
        return cd;
    }
    /**
     * Newly enrolled on PrEP and pregnant
     * @return
     */
    public CohortDefinition newlyEnrolledOnPrEPPregnant() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("newlyEnrolledInPrEP", ReportUtils.map(newlyEnrolledInPrEP(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("pregnantPatientInPrEPFollowup", ReportUtils.map(pregnantPatientInPrEPFollowup(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("newlyEnrolledInPrEP AND pregnantPatientInPrEPFollowup");
        return cd;
    }
    /**
     * Newly enrolled on PrEP and breastfeeding
     * @return
     */
    public CohortDefinition newlyEnrolledInPrEPBreastFeeding() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("newlyEnrolledInPrEP", ReportUtils.map(newlyEnrolledInPrEP(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("breastfeedingPatientInPrEPFollowup", ReportUtils.map(breastfeedingPatientInPrEPFollowup(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("newlyEnrolledInPrEP AND breastfeedingPatientInPrEPFollowup");
        return cd;
    }
    /**
     * Newly enrolled on oral PrEP
     * @return
     */
    public CohortDefinition newlyEnrolledOnOralPrEP() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("newlyEnrolledInPrEP", ReportUtils.map(newlyEnrolledInPrEP(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("onOralPrEP", ReportUtils.map(onOralPrEP(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("newlyEnrolledInPrEP AND onOralPrEP");
        return cd;
    }

    /**
     * Newly enrolled on CAB-LA Injectable PrEP
     * @return
     */
    public CohortDefinition newlyEnrolledOnCABLAInjectablePrEP() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("newlyEnrolledInPrEP", ReportUtils.map(newlyEnrolledInPrEP(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("onCABLAInjectablePrEP", ReportUtils.map(onCABLAInjectablePrEP(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("newlyEnrolledInPrEP AND onCABLAInjectablePrEP");
        return cd;
    }

    /**
     * Newly enrolled on other forms of PrEP
     * @return
     */
    public CohortDefinition newlyEnrolledOnOtherPrEP() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("newlyEnrolledInPrEP", ReportUtils.map(newlyEnrolledInPrEP(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("onCABLAInjectablePrEP", ReportUtils.map(onCABLAInjectablePrEP(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("onOralPrEP", ReportUtils.map(onOralPrEP(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("newlyEnrolledInPrEP AND NOT (onCABLAInjectablePrEP OR onOralPrEP)");
        return cd;
    }
    /**
     *Proportion of ART patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy
     * TB_PREV_COM Datim indicator
     * @return
     */
    public CohortDefinition onARTAndCompletedTPT() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("currentlyOnART", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initiatedTPTWithin6MonthsStartingART", ReportUtils.map(initiatedTPTWithin6MonthsStartingART(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initiatedTPTAfter6MonthsStartingART", ReportUtils.map(initiatedTPTAfter6MonthsStartingART(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("completedTPTCurrentOrPrevPeriod", ReportUtils.map(completedTPTCurrentOrPrevPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("currentlyOnART AND (initiatedTPTAfter6MonthsStartingART OR initiatedTPTWithin6MonthsStartingART) AND completedTPTCurrentOrPrevPeriod");
        return cd;
    }

    /**
     *Proportion of patients who Initiated TPT within 6 months of starting ART
     * @return
     */
    public CohortDefinition initiatedTPTWithin6MonthsStartingART() {

        String sqlQuery = "select i.patient_id\n" +
                "from kenyaemr_etl.etl_patient_program i\n" +
                "         inner join (select de.patient_id, min(de.date_started) as arv_start_date\n" +
                "                     from kenyaemr_etl.etl_drug_event de\n" +
                "                     where de.program = 'HIV'\n" +
                "                       and date(de.date_started) <= date(:endDate)\n" +
                "                     group by de.patient_id) de on i.patient_id = de.patient_id\n" +
                "where i.program = 'TPT'\n" +
                "  and date(i.date_enrolled) between date_add(date_sub(:endDate, interval 12 MONTH), interval  1 day) and LAST_DAY(date_sub(date(:endDate), interval 6 MONTH))\n" +
                "  and timestampdiff(MONTH, date(de.arv_start_date),date(i.date_enrolled)) < 6;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TB_PREV_NEWLY_ENROLLED_ART_INITIATED_TPT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Initiated TPT within 6 months of starting ART");
        return cd;

    }

    /**
     *Proportion of patients who Initiated TPT after 6 months of starting ART
     *  @return
     */
    public CohortDefinition initiatedTPTAfter6MonthsStartingART() {

        String sqlQuery = "select i.patient_id\n" +
                "from kenyaemr_etl.etl_patient_program i\n" +
                "         inner join (select de.patient_id, min(de.date_started) as arv_start_date\n" +
                "                     from kenyaemr_etl.etl_drug_event de\n" +
                "                     where de.program = 'HIV'\n" +
                "                       and date(de.date_started) <= date(:endDate)\n" +
                "                     group by de.patient_id) de on i.patient_id = de.patient_id\n" +
                "where i.program = 'TPT'\n" +
                "  and date(i.date_enrolled) between date_add(date_sub(:endDate, interval 12 MONTH), interval  1 day) and LAST_DAY(date_sub(date(:endDate), interval 6 MONTH))\n" +
                "  and timestampdiff(MONTH, date(de.arv_start_date), date(i.date_enrolled)) >= 6;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TB_PREV_ENROLLED_ART_INITIATED_TPT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Initiated TPT After 6 months of starting ART");
        return cd;

    }

    /**
     *Proportion of NEW ON ART patients who started on a standard course of TB Preventive Treatment (TPT)
     * within 6 months of starting ART
     * TB_PREV_NEWLY_ENROLLED_ART_INITIATED_TPT Datim indicator
     * Composition startedOnART + initiatedTPTWithin6MonthsStartingART
     * @return
     */
    public CohortDefinition newOnARTAndInitiatedTPT() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("currentlyOnArt", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initiatedTPTWithin6MonthsStartingART", ReportUtils.map(initiatedTPTWithin6MonthsStartingART(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("currentlyOnArt AND initiatedTPTWithin6MonthsStartingART");
        return cd;
    }

    /**
     *Proportion of PREVIOUS ON ART patients who started on a standard course of TB Preventive Treatment (TPT)
     * Afetr 6 months of starting ART
     * TB_PREV_ENROLLED_ART_INITIATED_TPT Datim indicator
     * Composition startedOnART + initiatedTPTAfter6MonthsStartingART
     * @return
     */
    public CohortDefinition previouslyOnARTAndInitiatedTPT() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("currentlyOnART", ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initiatedTPTAfter6MonthsStartingART", ReportUtils.map(initiatedTPTAfter6MonthsStartingART(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("currentlyOnART AND initiatedTPTAfter6MonthsStartingART");
        return cd;
    }

    /**
     *Proportion of  patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy either within the current or previous reporting period
     * Composition
     * @return
     */
    public CohortDefinition completedTPTCurrentOrPrevPeriod() {
        String sqlQuery = "select o.patient_id\n" +
                "from kenyaemr_etl.etl_ipt_outcome o\n" +
                "where date(o.visit_date) between date_add(date_sub(:endDate, interval 12 MONTH), interval  1 day)\n" +
                "    and date(:endDate)\n" +
                "  and o.outcome = 1267;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("completedTPTCurrentOrPrevPeriod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Completed IPT in previous or current period");
        return cd;

    }

    /**
     *Proportion of NEW ON ART patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy
     * TB_PREV_NEWLY_ENROLLED_ART_COMPLETED_TPT Datim indicator
     * Composition startedOnART + prevOnIPTandCompleted
     * @return
     */
    public CohortDefinition newOnARTAndCompletedTPT() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("newOnARTAndInitiatedTPT", ReportUtils.map(newOnARTAndInitiatedTPT(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("completedTPTCurrentOrPrevPeriod", ReportUtils.map(completedTPTCurrentOrPrevPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("newOnARTAndInitiatedTPT AND completedTPTCurrentOrPrevPeriod");
        return cd;
    }

    /**
     *Proportion of PREVIOUS ON ART patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy
     * TB_PREV_ENROLLED_ART_COMPLETED_TPT Datim indicator
     * Composition startedOnART + prevOnIPTandCompleted
     * @return
     */
    public CohortDefinition previouslyOnARTAndCompletedTPT() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("previouslyOnARTAndInitiatedTPT", ReportUtils.map(previouslyOnARTAndInitiatedTPT(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("completedTPTCurrentOrPrevPeriod", ReportUtils.map(completedTPTCurrentOrPrevPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("previouslyOnARTAndInitiatedTPT AND completedTPTCurrentOrPrevPeriod");
        return cd;
    }

    /**
     *Number of beneficiaries served by PEPFAR OVC Comprehensive programs for children and families affected by HIV
     * DATIM_OVC_SERV Datim indicator
     */
    public CohortDefinition totalBeneficiaryOfOVCComprehensiveProgram() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_ovc_enrolment e where e.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and e.ovc_comprehensive_program = 'Yes';";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("DATIM_OVC_SERV_COMPREHENSIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients on OVC program");
        return cd;
    }

    /**
     *Number of beneficiaries served by PEPFAR OVC DREAMS programs for children and families affected by HIV
     * DATIM_OVC_SERV Datim indicator
     */
    public CohortDefinition totalBeneficiaryOfOVCDreamsProgram() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_ovc_enrolment e where e.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and e.dreams_program = 'Yes';";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("DATIM_OVC_SERV_DREAMS");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients on OVC program");
        return cd;
    }

    /**
     *Number of beneficiaries served by PEPFAR OVC Preventive programs for children and families affected by HIV
     * DATIM_OVC_SERV Datim indicator
     */
    public CohortDefinition totalBeneficiaryOfOVCPreventiveProgram() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_ovc_enrolment e where e.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and e.ovc_preventive_program = 'Yes';";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("DATIM_OVC_SERV_PREVENTIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients on OVC program");
        return cd;
    }

    /**
     * KP_PREV for the current semi-annual reporting period
     * @return
     */
    public CohortDefinition kpPrevCurrentPeriod() {
        String sqlQuery = "select c.client_id from kenyaemr_etl.etl_contact c\n" +
                "  inner join (select e.client_id from kenyaemr_etl.etl_client_enrollment e where e.visit_date <= date(:endDate)) e on c.client_id = e.client_id\n" +
                "  left join (select v.client_id,v.visit_date from kenyaemr_etl.etl_clinical_visit v where v.visit_date <= date(:endDate))v on c.client_id = v.client_id\n" +
                "  left join (select p.client_id, p.visit_date as first_peer_enc from kenyaemr_etl.etl_peer_calendar p where p.visit_date <= date(:endDate))p on c.client_id = p.client_id\n" +
                "where(((v.visit_date between (CASE MONTH(date(:startDate)) when 5 then replace(date(:startDate), MONTH(date(:startDate)),4) when 6 then replace(date(:startDate), MONTH(date(:startDate)),4)\n" +
                "                                             when 7 then replace(date(:startDate), MONTH(date(:startDate)),4) when 8 then replace(date(:startDate), MONTH(date(:startDate)),4) when 9 then replace(date(:startDate), MONTH(date(:startDate)),4) when 11 then replace(date(:startDate), MONTH(date(:startDate)),10) when 12 then replace(date(:startDate), MONTH(date(:startDate)),10) when 1 then (replace(@startOfYear, '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR))))\n" +
                "                                             when 2 then replace('" + startOfYear + "', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR))) when 3 then replace('" + startOfYear + "', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))\n" +
                "                                             else date(:startDate) end) and date(:endDate))\n" +
                "or (p.first_peer_enc between (CASE MONTH(date(:startDate)) when 5 then replace(date(:startDate), MONTH(date(:startDate)),4) when 6 then replace(date(:startDate), MONTH(date(:startDate)),4)\n" +
                "                                            when 7 then replace(date(:startDate), MONTH(date(:startDate)),4) when 8 then replace(date(:startDate), MONTH(date(:startDate)),4) when 9 then replace(date(:startDate), MONTH(date(:startDate)),4) when 11 then replace(date(:startDate), MONTH(date(:startDate)),10) when 12 then replace(date(:startDate), MONTH(date(:startDate)),10) when 1 then (replace('" + startOfYear + "', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR))))\n" +
                "                                            when 2 then replace('" + startOfYear + "', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR))) when 3 then replace('" + startOfYear + "', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))\n" +
                "                                            else date(:startDate) end) and date(:endDate)) and c.voided=0)) group by c.client_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("kpPrevCurrentPeriod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("KPs with visit within the reporting period");
        return cd;
    }

    /**
     * KP_PREV for the previous period (Half year). This is required for de-duplication when getting KP_PREV clients
     * @return
     */
    public CohortDefinition kpPrevPreviousPeriod() {
        String sqlQuery = "select c.client_id from kenyaemr_etl.etl_contact c\n" +
                "                          inner join (select e.client_id from kenyaemr_etl.etl_client_enrollment e where e.visit_date <= date(:endDate)) e on c.client_id = e.client_id\n" +
                "                          left join (select v.client_id,v.visit_date from kenyaemr_etl.etl_clinical_visit v where v.visit_date <= date(:endDate))v on c.client_id = v.client_id\n" +
                "                          left join (select p.client_id, p.visit_date as first_peer_enc from kenyaemr_etl.etl_peer_calendar p where p.visit_date <= date(:endDate))p on c.client_id = p.client_id\n" +
                "where(((v.visit_date between (CASE MONTH(date(:startDate)) when 5 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4) when 6 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4)\n" +
                "    when 7 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4) when 8 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4) when 9 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4)\n" +
                "    when 11 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),10) when 12 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),10) when 1 then date_sub((replace('" + startOfYear + "', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))), INTERVAL 6 MONTH)\n" +
                "    when 2 then date_sub((replace('" + startOfYear + "', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))), INTERVAL 6 MONTH) when 3 then date_sub((replace('" + startOfYear + "', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))), INTERVAL 6 MONTH)\n" +
                "    else date_sub(date(:startDate), INTERVAL 6 MONTH) end) and date_sub(date(:endDate), INTERVAL 6 MONTH))\n" +
                "         or (p.first_peer_enc between (CASE MONTH(date(:startDate)) when 5 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4) when 6 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4)\n" +
                "    when 7 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4) when 8 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4) when 9 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4)\n" +
                "    when 11 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),10) when 12 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),10) when 1 then date_sub((replace('" + startOfYear + "', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))), INTERVAL 6 MONTH)\n" +
                "    when 2 then date_sub((replace('" + startOfYear + "', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))), INTERVAL 6 MONTH) when 3 then date_sub((replace('" + startOfYear + "', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))), INTERVAL 6 MONTH)\n" +
                "    else date_sub(date(:startDate), INTERVAL 6 MONTH) end) and date_sub(date(:endDate), INTERVAL 6 MONTH)) and c.voided=0)) group by c.client_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("kpPrevPreviousPeriod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("KPs with visit during previous reporting period");
        return cd;
    }

    /**
     * KPs in KP program who were newly tested/referred for HTS within the last 3 months
     * @return
     */
    public CohortDefinition kpPrevOfferedHTSServices() {
        String sqlQuery = "select v.client_id from kenyaemr_etl.etl_clinical_visit v where timestampdiff(MONTH,v.visit_date,date(:endDate)) <3 and v.hiv_tested = 'Yes';";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("kpPrevOfferedHTSServices");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("KP offered HTS");
        return cd;
    }

    /**
     * KPs self-tested within the last 3 months
     * @return
     */
    public CohortDefinition kpPrevOfferedSelfTestServices() {
        String sqlQuery = "select v.client_id from kenyaemr_etl.etl_clinical_visit v where timestampdiff(MONTH,v.visit_date,date(:endDate)) <3 and v.self_use_kits > 0;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("kpPrevOfferedSelfTestServices");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("KP self tested");
        return cd;
    }
    /**
     * KPs in KP program who were newly tested/referred for HTS within the last 3 months
     * @return
     */
    public CohortDefinition kpPrevNewlyTestedOrReferredSql() {
        String sqlQuery = "select a.client_id from (select v.client_id as client_id from (select v.client_id from kenyaemr_etl.etl_clinical_visit v where timestampdiff(MONTH,v.visit_date,date(:endDate)) <3 and v.hiv_tested in ('Yes','Referred for testing'))v\n" +
                "left join\n" +
                "    (select t.patient_id from kenyaemr_etl.etl_hts_test t where t.final_test_result in ('Positive','Negative') and timestampdiff(MONTH,t.visit_date,date(:endDate)) <3)t on v.client_id = t.patient_id)a group by a.client_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("kpPrevNewlyTestedOrReferred");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("KP newly tested or referred");
        return cd;
    }

    /**
     * KPs in KP program  who received a clinical service within the reporting period
     * @return
     */
    public CohortDefinition kpPrevReceivedService() {
        String sqlQuery = "select v.client_id from kenyaemr_etl.etl_clinical_visit v\n" +
                "       where (v.female_condoms_no > 0 or v.male_condoms_no > 0 or v.lubes_no > 0 or prep_treated = 'Y') and \n" +
                "             v.visit_date between (CASE MONTH(date(:startDate)) when 5 then replace(date(:startDate), MONTH(date(:startDate)),4) when 6 then replace(date(:startDate), MONTH(date(:startDate)),4)\n" +
                "    when 7 then replace(date(:startDate), MONTH(date(:startDate)),4) when 8 then replace(date(:startDate), MONTH(date(:startDate)),4) when 9 then replace(date(:startDate), MONTH(date(:startDate)),4) when 11 then replace(date(:startDate), MONTH(date(:startDate)),10)\n" +
                "    when 12 then replace(date(:startDate), MONTH(date(:startDate)),10) when 1 then (replace('" + startOfYear + "', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR))))\n" +
                "    when 2 then replace('" + startOfYear + "', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR))) when 3 then replace('" + startOfYear + "', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))\n" +
                "    else date(:startDate) end) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("kpPrevReceivedService");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("KP received service");
        return cd;
    }

    /**
     * KPs who are known positive prior to enrolment into the program.
     * @return
     */
    public CohortDefinition kpPrevKnownPositiveSql() {
        String sqlQuery = "select c.client_id from kenyaemr_etl.etl_contact c\n" +
                "                left join (select e.client_id from kenyaemr_etl.etl_client_enrollment e where e.visit_date <= date(:endDate) and e.share_test_results = 'Yes I tested positive')e  on c.client_id = e.client_id\n" +
                "left join (select h.patient_id from kenyaemr_etl.etl_hiv_enrollment h where h.visit_date < date(:startDate)) h on c.client_id = h.patient_id\n" +
                "where e.client_id is not null or h.patient_id is not null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("kpPrevKnownPositiveSql");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("KPs known positive");
        return cd;
    }

    /**
     * KPs who had a a service within the reporting period and declined HTS and /or referral
     * @return
     */
    public CohortDefinition kpPrevDeclinedTestingSql() {
        String sqlQuery = "select a.client_id from (select v.client_id as client_id from (select v.client_id from kenyaemr_etl.etl_clinical_visit v where timestampdiff(MONTH,v.visit_date,date(:endDate)) <3 and v.hiv_tested ='Declined')v\n" +
                "       left join\n" +
                "        (select t.patient_id from kenyaemr_etl.etl_hts_test t where timestampdiff(MONTH,t.visit_date,date(:endDate)) <3)t on v.client_id = t.patient_id)a group by a.client_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("kpPrevDeclinedTestingSql");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("KPs declined testing");
        return cd;
    }

    /**
     * Get KPs by KP type in KP Program
     * @param kpType
     * @return
     */
    public CohortDefinition kpProgramByKpType(String kpType) {
        String sqlQuery = "select c.client_id from kenyaemr_etl.etl_contact c where c.visit_date <= date(:endDate) group by c.client_id having mid(max(concat(c.visit_date,c.key_population_type)),11) = '" + kpType + "';";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("kpProgramByKpType");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("KPs by type from KP Program");
        return cd;
    }

    /**
     * Number of key populations reached with individual and/or small group-level HIV prevention interventions designed for the target population
     * @param kpType
     * @return
     */
    public CohortDefinition kpPrev(String kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("kpPrevCurrentPeriod", ReportUtils.map(kpPrevCurrentPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevPreviousPeriod", ReportUtils.map(kpPrevPreviousPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpProgramByKpType", ReportUtils.map(kpProgramByKpType(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevReceivedService", ReportUtils.map(kpPrevReceivedService(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevOfferedHTSServices", ReportUtils.map(kpPrevOfferedHTSServices(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevOfferedSelfTestServices", ReportUtils.map(kpPrevOfferedSelfTestServices(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevKnownPositiveSql", ReportUtils.map(kpPrevKnownPositiveSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(kpPrevCurrentPeriod AND NOT kpPrevPreviousPeriod) AND kpProgramByKpType AND ((kpPrevReceivedService AND (kpPrevOfferedHTSServices OR kpPrevOfferedSelfTestServices)) OR (kpPrevReceivedService AND kpPrevKnownPositiveSql))");
        return cd;
    }

    /**
     * KP_PREV by KPs known positive by MSM, TG, FSW, PWID, people in prisons and other closed settings
     * @param kpType
     * @return
     */
    public CohortDefinition kpPrevKnownPositive(String kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("kpPrevKnownPositiveSql", ReportUtils.map(kpPrevKnownPositiveSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrev", ReportUtils.map(kpPrev(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("kpPrev AND kpPrevKnownPositiveSql");
        return cd;
    }

    /**
     *KP_PREV by KPs newly tested and/or referred for testing by MSM, TG, FSW, PWID
     * @param kpType
     * @return
     */
    public CohortDefinition kpPrevNewlyTestedOrReferred(String kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("kpPrev", ReportUtils.map(kpPrev(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevNewlyTestedOrReferred", ReportUtils.map(kpPrevNewlyTestedOrReferredSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("kpPrev AND kpPrevNewlyTestedOrReferred");
        return cd;
    }

    /**
     * KP_PREV by KPs declined testing and/or referral by MSM, TG, FSW, PWID, people in prisons and other closed settings
     * @param kpType
     * @return
     */
    public CohortDefinition kpPrevDeclinedTesting(String kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("kpPrev", ReportUtils.map(kpPrev(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevDeclinedTesting", ReportUtils.map(kpPrevDeclinedTestingSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(kpPrev AND kpPrevDeclinedTesting");
        return cd;
    }

    /**
     *Physical and/or emotional violence (other Post-GBV) care
     * GEND_GBV_SEXUAL_VIOLENCE Disaggreagtion
     */
    public CohortDefinition sexualGBV() {
        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_gbv_screening s join\n" +
                "(select a.patient_id as patient_id,a.visit_id as visit_id, a.visit_date as visit_date,group_concat(a.action_taken) as action_taken from kenyaemr_etl.etl_gbv_screening_action a\n" +
                "where a.action_taken is not null group by a.patient_id,a.visit_id)a on s.patient_id = a.patient_id and s.visit_id = a.visit_id\n" +
                "where s.sexual_ipv = 152370 and FIND_IN_SET('1185',action_taken) !=0 and FIND_IN_SET('1356',action_taken) !=0 and FIND_IN_SET('127910',action_taken) !=0 and FIND_IN_SET('160570',action_taken) !=0 and FIND_IN_SET('165171',action_taken) !=0\n" +
                "and FIND_IN_SET('165184',action_taken) !=0 and FIND_IN_SET('165200',action_taken) !=0 and s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate)\n" +
                "group by s.patient_id,s.visit_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("GEND_GBV_SEXUAL_GBV");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Received post rape care");
        return cd;
    }

    /**
     *Number of beneficiaries served by PEPFAR OVC Preventive programs for children and families affected by HIV
     * GEND_GBV_PHY_EMOTIONAL_VIOLENCE Disaggregation
     */
    public CohortDefinition physicalEmotionalGBV() {
        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_gbv_screening s join\n" +
                "             (select a.patient_id as patient_id,a.visit_id as visit_id, a.visit_date as visit_date,group_concat(a.action_taken) as action_taken from kenyaemr_etl.etl_gbv_screening_action a\n" +
                "             where a.action_taken is not null group by a.patient_id,a.visit_id)a on s.patient_id = a.patient_id and s.visit_id = a.visit_id\n" +
                "             where FIND_IN_SET('1185',action_taken) !=0 and FIND_IN_SET('1356',action_taken) !=0\n" +
                "             and FIND_IN_SET('165184',action_taken) !=0 and FIND_IN_SET('165200',action_taken) !=0\n" +
                "               and (ifnull(s.ipv,0) = 1065 or ifnull(s.physical_ipv,0) = 158358 or ifnull(s.emotional_ipv,0) = 118688 or ifnull(s.ipv_relationship,0) = 1582)\n" +
                "               and (FIND_IN_SET('127910',action_taken) =0 or FIND_IN_SET('160570',action_taken) =0 or FIND_IN_SET('165171',action_taken) =0)\n" +
                "             and s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate)\n" +
                "             group by s.patient_id,s.visit_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("GEND_GBV_PHY_EMOTIONAL_GBV");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Physical and/or emotional violence (other Post-GBV) care");
        return cd;
    }

    /**
     *Number of People Receiving Post-exposure prophylaxis (PEP) Services. Disaggregate of the Sexual Violence Service Type
     * GEND_GBV PEP Disaggregation
     */
    public CohortDefinition receivedPEP() {
        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_gbv_screening s join\n" +
                "(select a.patient_id as patient_id,a.visit_id as visit_id, a.visit_date as visit_date,group_concat(a.action_taken) as action_taken from kenyaemr_etl.etl_gbv_screening_action a\n" +
                "where a.action_taken is not null group by a.patient_id,a.visit_id)a on s.patient_id = a.patient_id and s.visit_id = a.visit_id\n" +
                "where s.sexual_ipv = 152370 and FIND_IN_SET('165171',action_taken) !=0 and s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate)\n" +
                "                group by s.patient_id,s.visit_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("GEND_GBV_PEP");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Receiving Post-exposure prophylaxis (PEP)");
        return cd;
    }

    /**
     * Number of People Trasferred within the reporting period and verified
     * @return
     */
    public CohortDefinition transferredOutAndVerified() {
        String sqlQuery = "select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
                "                    where d.program_name = 'HIV'\n" +
                "                          and date(d.effective_discontinuation_date)\n" +
                "                          between date(:startDate)\n" +
                "                          and date(:endDate)\n" +
                "                          and d.discontinuation_reason = 159492 and d.trf_out_verified =1065;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TRANS_VERIFIED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Transferred and Verified within period");
        return cd;
    }

    /**
     * Returns all priority populations. Excludes AGYW
     * @return
     */
    public CohortDefinition allPriorityPopulations() {
        String sqlQuery = "select c.client_id from kenyaemr_etl.etl_contact c where c.visit_date <= date(:endDate) group by c.client_id having mid(max(concat(c.visit_date,c.priority_population_type)),11) in (\"Fisher Folk\",\"Truck Driver\",\"Prisoner\");";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("ppType");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Priority populations type");
        return cd;
    }
    /**
     * PP_PREV
     * @return
     */
    public CohortDefinition ppPrev() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("ppPrevCurrentPeriod",ReportUtils.map(ppPrevCurrentPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("ppPrevPreviousPeriod",ReportUtils.map(ppPrevPreviousPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("allPriorityPopulations",ReportUtils.map(allPriorityPopulations(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevReceivedService",ReportUtils.map(kpPrevReceivedService(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevOfferedHTSServices",ReportUtils.map(kpPrevOfferedHTSServices(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevKnownPositiveSql",ReportUtils.map(kpPrevKnownPositiveSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("allPriorityPopulations AND (ppPrevCurrentPeriod AND NOT ppPrevPreviousPeriod) AND ((kpPrevReceivedService AND kpPrevOfferedHTSServices) OR (kpPrevReceivedService AND kpPrevKnownPositiveSql))");
        return cd;

    }
    /**
     * PP_PREV for the current semi-annual reporting period
     * @return
     */
    public CohortDefinition ppPrevCurrentPeriod() {
        String sqlQuery = "select c.client_id from kenyaemr_etl.etl_contact c\n" +
                "  left join (select v.client_id,v.visit_date from kenyaemr_etl.etl_clinical_visit v where v.visit_date <= date(:endDate))v on c.client_id = v.client_id\n" +
                "  left join (select p.client_id, p.visit_date as first_peer_enc from kenyaemr_etl.etl_peer_calendar p where p.visit_date <= date(:endDate))p on c.client_id = p.client_id\n" +
                "where(((v.visit_date between (CASE MONTH(date(:startDate)) when 5 then replace(date(:startDate), MONTH(date(:startDate)),4) when 6 then replace(date(:startDate), MONTH(date(:startDate)),4)\n" +
                "                                             when 7 then replace(date(:startDate), MONTH(date(:startDate)),4) when 8 then replace(date(:startDate), MONTH(date(:startDate)),4) when 9 then replace(date(:startDate), MONTH(date(:startDate)),4) when 11 then replace(date(:startDate), MONTH(date(:startDate)),10) when 12 then replace(date(:startDate), MONTH(date(:startDate)),10) when 1 then (replace(@startOfYear, '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR))))\n" +
                "                                             when 2 then replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR))) when 3 then replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))\n" +
                "                                             else date(:startDate) end) and date(:endDate))\n" +
                "or (p.first_peer_enc between (CASE MONTH(date(:startDate)) when 5 then replace(date(:startDate), MONTH(date(:startDate)),4) when 6 then replace(date(:startDate), MONTH(date(:startDate)),4)\n" +
                "                                            when 7 then replace(date(:startDate), MONTH(date(:startDate)),4) when 8 then replace(date(:startDate), MONTH(date(:startDate)),4) when 9 then replace(date(:startDate), MONTH(date(:startDate)),4) when 11 then replace(date(:startDate), MONTH(date(:startDate)),10) when 12 then replace(date(:startDate), MONTH(date(:startDate)),10) when 1 then (replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR))))\n" +
                "                                            when 2 then replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR))) when 3 then replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))\n" +
                "                                            else date(:startDate) end) and date(:endDate)) and c.voided=0)) group by c.client_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("ppPrevCurrentPeriod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("PPs with visit within the reporting period");
        return cd;
    }

    /**
     * PP_PREV for the previous period (Half year). This is required for de-duplication when getting PP_PREV clients
     * @return
     */
    public CohortDefinition ppPrevPreviousPeriod() {
        String sqlQuery = "select c.client_id from kenyaemr_etl.etl_contact c\n" +
                "                          left join (select v.client_id,v.visit_date from kenyaemr_etl.etl_clinical_visit v where v.visit_date <= date(:endDate))v on c.client_id = v.client_id\n" +
                "                          left join (select p.client_id, p.visit_date as first_peer_enc from kenyaemr_etl.etl_peer_calendar p where p.visit_date <= date(:endDate))p on c.client_id = p.client_id\n" +
                "where(((v.visit_date between (CASE MONTH(date(:startDate)) when 5 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4) when 6 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4)\n" +
                "    when 7 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4) when 8 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4) when 9 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4)\n" +
                "    when 11 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),10) when 12 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),10) when 1 then date_sub((replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))), INTERVAL 6 MONTH)\n" +
                "    when 2 then date_sub((replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))), INTERVAL 6 MONTH) when 3 then date_sub((replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))), INTERVAL 6 MONTH)\n" +
                "    else date_sub(date(:startDate), INTERVAL 6 MONTH) end) and date_sub(date(:endDate), INTERVAL 6 MONTH))\n" +
                "         or (p.first_peer_enc between (CASE MONTH(date(:startDate)) when 5 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4) when 6 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4)\n" +
                "    when 7 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4) when 8 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4) when 9 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4)\n" +
                "    when 11 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),10) when 12 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),10) when 1 then date_sub((replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))), INTERVAL 6 MONTH)\n" +
                "    when 2 then date_sub((replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))), INTERVAL 6 MONTH) when 3 then date_sub((replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))), INTERVAL 6 MONTH)\n" +
                "    else date_sub(date(:startDate), INTERVAL 6 MONTH) end) and date_sub(date(:endDate), INTERVAL 6 MONTH)) and c.voided=0)) group by c.client_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("ppPrevPreviousPeriod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("PPs with visit during previous reporting period");
        return cd;
    }
    /**
     * PP_PREV by PP type
     * @return
     */
    public CohortDefinition ppPrevByType(String ppType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("ppPrevCurrentPeriod",ReportUtils.map(ppPrevCurrentPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("ppPrevPreviousPeriod",ReportUtils.map(ppPrevPreviousPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("priorityPopulationByType",ReportUtils.map(priorityPopulationByType(ppType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevReceivedService",ReportUtils.map(kpPrevReceivedService(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevOfferedHTSServices",ReportUtils.map(kpPrevOfferedHTSServices(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevKnownPositiveSql",ReportUtils.map(kpPrevKnownPositiveSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("priorityPopulationByType AND (ppPrevCurrentPeriod AND NOT ppPrevPreviousPeriod) AND ((kpPrevReceivedService AND kpPrevOfferedHTSServices) OR (kpPrevReceivedService AND kpPrevKnownPositiveSql))");
        return cd;

    }

    /**
     * Returns Priority populations by type
     * @param ppType
     * @return
     */
    public CohortDefinition priorityPopulationByType(String ppType) {
        String sqlQuery = "select c.client_id from kenyaemr_etl.etl_contact c where c.visit_date <= date(:endDate) group by c.client_id having mid(max(concat(c.visit_date,c.priority_population_type)),11) in (\"" + ppType + "\");";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("priorityPopulationByType");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Priority pops by type");
        return cd;
    }

    /** Other pririty popultaions. Excludes AGYW
     * @return
     */
    public CohortDefinition otherPriorityPopulation() {
        String sqlQuery = "select c.client_id from kenyaemr_etl.etl_contact c where c.visit_date <= date(:endDate) group by c.client_id having mid(max(concat(c.visit_date,c.priority_population_type)),11) in ('Prisoner');";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("otherPriorityPopulation");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Other priority populations");
        return cd;
    }
// TODO: 02/03/2023 : Fix max_packet size overflow error in mysql caused by pp_prev queries. Once done uncomment PP_PREV Indicators 
    /* *//**
     * PP_PREV known positives
     * @return
     */
    public CohortDefinition ppPrevKnownPositive() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("ppPrev",ReportUtils.map(ppPrev(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevKnownPositiveSql",ReportUtils.map(kpPrevKnownPositiveSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("ppPrev AND kpPrevKnownPositiveSql");
        return cd;
    }

    /**
     * PP_PREV newly tested or referred
     * @return
     */
    public CohortDefinition ppPrevNewlyTestedOrReferred() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("ppPrev",ReportUtils.map(ppPrev(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevNewlyTestedOrReferredSql",ReportUtils.map(kpPrevNewlyTestedOrReferredSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("ppPrev AND kpPrevNewlyTestedOrReferredSql");
        return cd;
    }

    /**
     * PP_PREV declined testing
     * @return
     */
    public CohortDefinition ppPrevDeclinedTesting() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("ppPrev",ReportUtils.map(ppPrev(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevDeclinedTestingSql",ReportUtils.map(kpPrevDeclinedTestingSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("ppPrev AND kpPrevDeclinedTestingSql");
        return cd;
    }

    /**
     * Screened ineligible for HIV testing
     * @return
     */
    private CohortDefinition testNotRequiredSql() {
        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_hts_eligibility_screening s where s.visit_date <= date(:endDate) group by s.patient_id\n" +
                "having mid(max(concat(s.visit_date,s.eligible_for_test)),11) = 1066;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("testNotRequired");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV test not required based on risk assessment");
        return cd;
    }
// TODO: 02/03/2023 : Fix max_packet size overflow error in mysql caused by pp_prev queries. Once done uncomment PP_PREV Indicators 
    /*  *//**
     * PP_PREV test not required based on HTS eligibility screening
     * @return
     */
    public CohortDefinition ppPrevTestNotRequired() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("ppPrev",ReportUtils.map(ppPrev(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testNotRequired",ReportUtils.map(testNotRequiredSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("ppPrev AND testNotRequired");
        return cd;
    }

    /**
     * PP_prev_Other
     * @return
     */
    public CohortDefinition ppPrevOther() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("ppPrev",ReportUtils.map(ppPrev(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("otherPriorityPopulation",ReportUtils.map(otherPriorityPopulation(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("ppPrev AND otherPriorityPopulation");
        return cd;
    }
}


