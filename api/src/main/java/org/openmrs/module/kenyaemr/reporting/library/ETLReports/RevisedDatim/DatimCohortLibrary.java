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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.KPTypeDataDefinition;
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
    public  CohortDefinition startedOnART() {
        String sqlQuery="select net.patient_id  \n" +
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
    public  CohortDefinition previouslyOnART() {
        String sqlQuery="select net.patient_id  \n" +
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
     * Patients started on ART during the reporting period (last 3 months) and are pregnant during that period
     * TX_New Datim indicator
     * @return
     */
    public  CohortDefinition startedOnARTAndPregnant() {
        String sqlQuery="select net.patient_id\n" +
                "       from (\n" +
                "       select e.patient_id,e.date_started,\n" +
                "       e.gender,\n" +
                "       e.dob,\n" +
                "       d.visit_date as dis_date,\n" +
                "       if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "       e.regimen, e.regimen_line, e.alternative_regimen,\n" +
                "       fup.visit_date, fup.pregnancy_status,\n" +
                "       mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "       max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art,\n" +
                "       max(if(enr.transfer_in_date is not null, 1, 0)) as TIn,\n" +
                "       max(fup.visit_date) as latest_vis_date\n" +
                "       from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started,\n" +
                "       mid(min(concat(e.date_started,e.regimen_name)),11) as regimen,\n" +
                "       mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line,\n" +
                "       max(if(discontinued,1,0))as alternative_regimen\n" +
                "       from kenyaemr_etl.etl_drug_event e\n" +
                "       join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "        where e.program = 'HIV'\n" +
                "       group by e.patient_id) e\n" +
                "       left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_name='HIV'\n" +
                "       left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "       left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "       left outer join kenyaemr_etl.etl_mch_enrollment mch on mch.patient_id=e.patient_id\n" +
                "       where date(e.date_started) between :startDate and date(:endDate)\n" +
                "       and ((fup.pregnancy_status =1065 and fup.visit_date between :startDate and date(:endDate)) OR\n" +
                "            mch.visit_date between :startDate and date(:endDate) )\n" +
                "       group by e.patient_id\n" +
                "       having TI_on_art=0\n" +
                "       )net;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_New_pregnant");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Started on ART in the last 3 months and are pregnant");
        return cd;
    }

    /**
     * Patients started on ART during the reporting period (last 3 months) and are on new or existing TB cases
     * TX_New Datim indicator
     * @return
     */
    public  CohortDefinition startedOnARTAndTBCoinfected() {
        String sqlQuery="select net.patient_id\n" +
                "       from (\n" +
                "       select e.patient_id,e.date_started,\n" +
                "       e.gender,\n" +
                "       e.dob,\n" +
                "       d.visit_date as dis_date,\n" +
                "       if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "       e.regimen, e.regimen_line, e.alternative_regimen,\n" +
                "       fup.visit_date, fup.on_anti_tb_drugs,\n" +
                "       mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "       max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art,\n" +
                "       max(if(enr.transfer_in_date is not null, 1, 0)) as TIn,\n" +
                "       max(fup.visit_date) as latest_vis_date\n" +
                "       from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started,\n" +
                "       mid(min(concat(e.date_started,e.regimen_name)),11) as regimen,\n" +
                "       mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line,\n" +
                "       max(if(discontinued,1,0))as alternative_regimen\n" +
                "       from kenyaemr_etl.etl_drug_event e\n" +
                "       join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "       where e.program = 'HIV'\n" +
                "       group by e.patient_id) e\n" +
                "       left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_name='HIV'\n" +
                "       left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "       left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "       left outer join kenyaemr_etl.etl_tb_enrollment tbenr on tbenr.patient_id = e.patient_id\n" +
                "       where date(e.date_started) between date(:startDate) and date(:endDate)\n" +
                "       and fup.on_anti_tb_drugs =1065 or  tbenr.visit_date < tbenr.date_of_discontinuation\n" +
                "       group by e.patient_id\n" +
                "       having TI_on_art=0\n" +
                "       )net;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_New_TB_co_infected");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Started on ART in the last 3 months and are pregnant");
        return cd;
    }
    /**
     * Patients currently on ART
     * TX_Curr Datim indicator
     * @return
     */
    public CohortDefinition currentlyOnArt() {
        SqlCohortDefinition cd = new SqlCohortDefinition();

        String sqlQuery="select t.patient_id\n" +
                "from(\n" +
                "    select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "           greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "           greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "           greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "           d.patient_id as disc_patient,\n" +
                "           d.effective_disc_date as effective_disc_date,\n" +
                "           max(d.visit_date) as date_discontinued,\n" +
                "           de.patient_id as started_on_drugs\n" +
                "    from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "           join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "           join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "           left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "           left outer JOIN\n" +
                "             (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "              where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "              group by patient_id\n" +
                "             ) d on d.patient_id = fup.patient_id\n" +
                "    where fup.visit_date <= date(:endDate)\n" +
                "    group by patient_id\n" +
                "    having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "        (\n" +
                "            ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "              and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "            )\n" +
                "        )\n" +
                "    ) t;";

        cd.setName("TX_Curr");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("currently on ART");
        return cd;
    }

    /**
     * TX_RET Denominator
     * Includes patients who started art 12 months ago irrespective of their active status
     * @return
     */
    protected CohortDefinition art12MonthCohort() {

        String sqlQuery = "  ";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_RET_Denominator");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("art 12 Months Net Cohort Denominator");
        return cd;

    }

    /**
     * TX_RET Datim Indicator
     * Patients who were started on treatment 12 months ago and are still on treatment
     * @return
     */
    protected CohortDefinition onTherapyAt12Months() {

        String sqlQuery = "select  net.patient_id, date_started, dis_date, TOut, date_died, latest_vis_date, latest_tca\n" +
                "  from ( \n" +
                "  select e.patient_id,e.date_started, d.visit_date as dis_date, if(d.visit_date is not null and d.discontinuation_reason=159492, 1, 0) as TOut, d.date_died,\n" +
                " mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca, \n" +
                " if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date\n" +
                "   from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started\n" +
                "   from kenyaemr_etl.etl_drug_event e \n" +
                "   join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "   where e.program='HIV' \n" +
                "   group by e.patient_id) e \n" +
                "   left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_uuid='2bdada65-4c72-4a48-8730-859890e25cee' \n" +
                "   left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id \n" +
                "   left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id \n" +
                "   where  date(e.date_started) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year) \n" +
                "   group by e.patient_id \n" +
                "   having   (dis_date>date(:endDate) or dis_date is null or TOut=0 ) and (\n" +
                "        (date(latest_tca) > date(:endDate) and (date(latest_tca) > date(dis_date) or dis_date is null ))  or \n" +
                "         (((date(latest_tca) between date(:startDate) and date(:endDate)) and (date(latest_vis_date) >= date(latest_tca)) or date(latest_tca) > curdate()) ) and \n" +
                "         (date(latest_tca) > date(dis_date) or dis_date is null )\n" +
                "        )\n" +
                "   )net; ";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_RET_Numerator");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("on Therapy At 12 Months");
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

    /**
     * TX_PVLS
     * Patients with VL in the last 12 months
     * @return
     */
    protected CohortDefinition viralLoadResultsInLast12Months() {

        String sqlQuery =" select patient_id \n" +
                "from kenyaemr_etl.etl_laboratory_extract \n" +
                "where (visit_date BETWEEN date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate)) \n" +
                "and (lab_test in (856, 1305));";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_Denominator");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("VL in last 12 months");
        return cd;

    }

    /**
     * TX_PVLS Datim Indicator
     * Patients with viral suppression in last 12 months
     * @return
     */
    //TODO refer below
    protected CohortDefinition viralSuppressionInLast12Months() {

        String sqlQuery = " select patient_id \n" +
                "from kenyaemr_etl.etl_laboratory_extract \n" +
                "where (visit_date BETWEEN date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate)) \n" +
                "and ((lab_test=856 and test_result < 1000) or (lab_test=1305 and test_result=1302));";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_Numerator");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("on Therapy At 12 Months");
        return cd;

    }

    /**
     * TODO: review this query. it is a killer
     * @return
     */
    public CohortDefinition startingARTPregnant() {

        String sqlQuery = " select  fup.patient_id " +
                "from kenyaemr_etl.etl_patient_hiv_followup fup " +
                "join (select patient_id from kenyaemr_etl.etl_drug_event e " +
                "where e.program = 'HIV' and date_started between date(:startDate) and date(:endDate)) started_art on  " +
                "started_art.patient_id = fup.patient_id " +
                "where fup.pregnancy_status =1065 " +
                "and fup.visit_date between date(:startDate) and date(:endDate);";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("startingARTPregnant");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Pregnant Women Started on ART");
        return cd;

    }

    public CohortDefinition startingARTWhileTbPatient() {

        String sqlQuery = "select  fup.patient_id \n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup \n" +
                "join (select patient_id from kenyaemr_etl.etl_drug_event e \n" +
                "where e.program = 'HIV' and date_started between date(:startDate) and date(:endDate)) started_art on  \n" +
                "started_art.patient_id = fup.patient_id \n" +
                "join kenyaemr_etl.etl_tb_enrollment tb on tb.patient_id=fup.patient_id\n" +
                "where fup.visit_date between date(:startDate) and date(:endDate);";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("startingARTonTb");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tb positive Started on ART");
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
    public CohortDefinition knownStatusAtANC() {

        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "       join kenyaemr_etl.etl_mch_antenatal_visit v on e.patient_id = v.patient_id\n" +
                "where e.hiv_status in (664, 703)\n" +
                "group by e.patient_id\n" +
                "having  min(v.visit_date) between date_sub(:endDate, interval 3 MONTH) and date(:endDate);";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("knownHIVStatusAtANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients with Known HIV status at ANC");
        return cd;

    }
    public CohortDefinition unKnownStatusAtANC() {

        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "join kenyaemr_etl.etl_mch_antenatal_visit v on e.patient_id = v.patient_id\n" +
                "where (e.hiv_status = 1067 )\n" +
                "and v.visit_date between date_sub(:endDate, interval 3 MONTH) and date(:endDate)\n" +
                "group by e.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("unknownHIVStatusAtANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients with Known HIV status at ANC");
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

    //Clients with negative HIV status before ANC-1
    public CohortDefinition negativeHivStatusBeforeAnc1() {

        String sqlQuery = "select n.patient_id from (select e.patient_id,e.latest_enrollment_date,t.latest_test_date,av.visit_date as anc_visit_date,e.1st_anc_visit from\n" +
                "(select e.patient_id, max(e.visit_date) as latest_enrollment_date,e.visit_date as 1st_anc_visit from kenyaemr_etl.etl_mch_enrollment e\n" +
                "group by e.patient_id) e\n" +
                "inner join\n" +
                "(select av.patient_id,av.visit_date as visit_date from kenyaemr_etl.etl_mch_antenatal_visit av where av.anc_visit_number = 1\n" +
                " and av.visit_date between date_sub(:endDate, interval 3 MONTH) and date(:endDate)\n" +
                "group by av.patient_id) av on e.patient_id = av.patient_id\n" +
                "inner join (select max(t.visit_date) as latest_test_date,t.patient_id from kenyaemr_etl.etl_hts_test t\n" +
                "group by t.patient_id having mid(max(concat(t.visit_date,t.final_test_result)),11) = 'Negative') t on e.patient_id = t.patient_id\n" +
                "group by e.patient_id\n" +
                "having coalesce(1st_anc_visit,anc_visit_date) between date_sub(:endDate, interval 3 MONTH) and date(:endDate)\n" +
                "and timestampdiff(MONTH,t.latest_test_date,anc_visit_date)< 3)n;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PMTCT_STAT_RECENT_NEGATIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients with negative HIV status before ANC-1");
        return cd;

    }

    /**
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
    //TODO use the orders table to pick sample_date () or discontinued=null)
    //TODO for +or- results  (if there are  result(orders.order_id with corresponding obs.order_id)
    public CohortDefinition infantVirologyNegativeResults() {

        String sqlQuery = "select hv.patient_id from kenyaemr_etl.etl_hei_follow_up_visit hv\n" +
                "inner join kenyaemr_etl.etl_patient_demographics de on de.patient_id = hv.patient_id\n" +
                "where hv.dna_pcr_result=664\n" +
                "  and timestampdiff(month, de.DOB,hv.dna_pcr_sample_date) <=12\n" +
                "and hv.visit_date between date(:startDate) and date(:endDate) group by hv.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("infantVirologyNegativeResults12m");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infants with negative Virology test result");
        return cd;

    }

    public CohortDefinition infantVirologyPositiveResults() {

        String sqlQuery = "select hv.patient_id from kenyaemr_etl.etl_hei_follow_up_visit hv\n" +
                "inner join kenyaemr_etl.etl_patient_demographics de on de.patient_id = hv.patient_id\n" +
                "where hv.dna_pcr_result=703\n" +
                "  and timestampdiff(month, de.DOB,hv.dna_pcr_sample_date) <=12\n" +
                "and hv.visit_date between date(:startDate) and date(:endDate) group by hv.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("infantVirologyPositiveResults12m");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infants with Positive Virology test result");
        return cd;

    }

    public CohortDefinition infantVirologyNoResults() {

        String sqlQuery = "select hv.patient_id from kenyaemr_etl.etl_hei_follow_up_visit hv\n" +
                "inner join kenyaemr_etl.etl_patient_demographics de on de.patient_id = hv.patient_id\n" +
                "where hv.dna_pcr_result in (1138,1304)\n" +
                "  and timestampdiff(month, de.DOB,hv.dna_pcr_sample_date) <=12\n" +
                "and hv.visit_date between date(:startDate) and date(:endDate) group by hv.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("infantVirologyWithNoResults");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infants with Positive Virology test result");
        return cd;

    }

    public CohortDefinition infantsTurnedHIVPositive() {

        String sqlQuery = "select t.patient_id from (select e.patient_id,timestampdiff(MONTH,d.dob,max(f.dna_pcr_sample_date)) months,f.dna_pcr_results_date results_date,e.exit_date exit_date,f.dna_pcr_contextual_status test_type from kenyaemr_etl.etl_hei_enrollment e inner join\n" +
                "                                                           kenyaemr_etl.etl_patient_demographics d on e.patient_id = d.patient_id\n" +
                "                                                inner join kenyaemr_etl.etl_hei_follow_up_visit f on e.patient_id = f.patient_id where (e.hiv_status_at_exit = 'Positive' or f.dna_pcr_result= 703)\n" +
                "                         group by e.patient_id)t\n" +
                "where test_type in (162081,162083,162080) and\n" +
                "    t.months <=12 and ((t.results_date between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate)) or (t.exit_date between date_sub(date(:endDate), interval 3 MONTH) and date(:endDate)))\n" +
                "group by t.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("infantsTurnedHIVPositive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infants Turned HIV Positive within 12 months of birth");
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

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and  s.screening_result in ('Negative','Normal')\n" +
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

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and  s.screening_result in ('Positive','Abnormal','Invasive Cancer')\n" +
                "                         and s.screening_type = 'First time screening';";

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

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and  s.screening_result in ('Suspicious for cancer','Low grade lesion','High grade lesion','Presumed Cancer')\n" +
                "                         and s.screening_type = 'First time screening';";

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

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and  s.screening_result in ('Negative','Normal')\n" +
                "                         and s.screening_type = 'Rescreening';";

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

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and  s.screening_result in ('Positive','Abnormal','Invasive Cancer')\n" +
                "                         and s.screening_type = 'Rescreening';";

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

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and  s.screening_result in ('Suspicious for cancer','Low grade lesion','High grade lesion','Presumed Cancer')\n" +
                "                          and s.screening_type = 'Rescreening';";

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

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and  s.screening_result in ('Negative','Normal')\n" +
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

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and  s.screening_result in ('Positive','Abnormal','Invasive Cancer')\n" +
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

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and  s.screening_result in ('Suspicious for cancer','Low grade lesion','High grade lesion','Presumed Cancer')\n" +
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
     *Screened negative for CXCA for the first time
     * @return
     */
    public CohortDefinition firstTimeCXCASCRNNegative() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
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

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and  s.treatment_method in ('Cryotherapy performed','Cryotherapy performed (single Visit)')\n" +
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

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and  s.treatment_method ='Thermocoagulation'\n" +
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

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and  s.treatment_method ='LEEP'\n" +
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

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and  s.treatment_method in ('Cryotherapy performed','Cryotherapy performed (single Visit)')\n" +
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

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and  s.treatment_method ='Thermocoagulation'\n" +
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

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and  s.treatment_method = 'LEEP'\n" +
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

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and  s.treatment_method in ('Cryotherapy performed','Cryotherapy performed (single Visit)')\n" +
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

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and  s.treatment_method ='Thermocoagulation'\n" +
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

        String sqlQuery = "select s.patient_id from kenyaemr_etl.etl_cervical_cancer_screening s where s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and  s.treatment_method ='LEEP'\n" +
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
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("firstScreeningCXCATXCryotherapySql",ReportUtils.map(firstScreeningCXCATXCryotherapySql(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("firstScreeningCXCATXThermocoagulationSql",ReportUtils.map(firstScreeningCXCATXThermocoagulationSql(),"startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("firstScreeningCXCATXLEEPSql",ReportUtils.map(firstScreeningCXCATXLEEPSql(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("rescreenedCXCATXCryotherapySql",ReportUtils.map(rescreenedCXCATXCryotherapySql(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("rescreenedCXCATXLEEPSql",ReportUtils.map(rescreenedCXCATXLEEPSql(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("postTxFollowupCXCATxCryotherapySql",ReportUtils.map(postTxFollowupCXCATxCryotherapySql(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("postTxFollowupCXCATXThermocoagulationSql",ReportUtils.map(postTxFollowupCXCATXThermocoagulationSql(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("postTxFollowupCXCATXLEEPSql",ReportUtils.map(postTxFollowupCXCATXLEEPSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("womenEnrolledInHIVProgram", ReportUtils.map(womenEnrolledInHIVProgram(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND womenEnrolledInHIVProgram AND postTxFollowupCXCATXLEEPSql");
        return cd;
    }
    /**
     * @return
     */
    public CohortDefinition infantsTurnedHIVPositiveOnART() {

        String sqlQuery = "select t.patient_id from (select e.patient_id,timestampdiff(MONTH,d.dob,max(f.dna_pcr_sample_date)) months,f.dna_pcr_results_date results_date,e.exit_date exit_date,f.dna_pcr_contextual_status test_type from kenyaemr_etl.etl_hei_enrollment e inner join\n" +
                "                                       kenyaemr_etl.etl_patient_demographics d on e.patient_id = d.patient_id\n" +
                "       inner join kenyaemr_etl.etl_hei_follow_up_visit f on e.patient_id = f.patient_id\n" +
                "                                       inner join (select net.patient_id\n" +
                "                                                   from (\n" +
                "                                                        select e.patient_id,e.date_started,\n" +
                " e.gender,\n" +
                " e.dob,\n" +
                " d.visit_date as dis_date,\n" +
                " if(d.visit_date is not null, 1, 0) as TOut,\n" +
                " e.regimen, e.regimen_line, e.alternative_regimen,\n" +
                " mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                " max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art,\n" +
                " max(if(enr.transfer_in_date is not null, 1, 0)) as TIn,\n" +
                " max(fup.visit_date) as latest_vis_date\n" +
                "                                                        from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started,\n" +
                "       mid(min(concat(e.date_started,e.regimen_name)),11) as regimen,\n" +
                "       mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line,\n" +
                "       max(if(discontinued,1,0))as alternative_regimen\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                "       join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "where e.program = 'HIV'\n" +
                "group by e.patient_id) e\n" +
                " left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_name='HIV'\n" +
                " left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                " left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "group by e.patient_id\n" +
                "                                            having TI_on_art=0\n" +
                "                                            )net) onart on e.patient_id = onart.patient_id\n" +
                "                           where (e.hiv_status_at_exit = 'Positive' or f.dna_pcr_result= 703)\n" +
                "                           group by e.patient_id)t\n" +
                " where t.test_type in (162081,162083,162080) and\n" +
                "       t.months <=12 and ((t.results_date between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate)) or (t.exit_date between date_sub(date(:endDate), interval 3 MONTH) and date(:endDate)))\n" +
                " group by t.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("infantsTurnedHIVPositiveOnART");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infants Turned HIV Positive within 12 months of birth and on ART");
        return cd;

    }
    public CohortDefinition infantVirologySampleTaken() {

        String sqlQuery = "select dm.patient_id from kenyaemr_etl.etl_patient_demographics  dm\n" +
                "  left join kenyaemr_etl.etl_hei_follow_up_visit hf on hf.patient_id = dm.patient_id\n" +
                "  left join openmrs.orders od on od.patient_id = dm.patient_id\n" +
                "where (od.concept_id =844 and timestampdiff(month, dm.DOB,od.date_activated) <=12 and substr(od.date_created,1,10) between (:startDate) and (:endDate))\n" +
                "     or(hf.dna_pcr_result=664 and timestampdiff(month, dm.DOB,hf.dna_pcr_sample_date) <=12 and hf.visit_date between date(:startDate) and date(:endDate))\n" +
                "group by dm.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("infantVirologySampleTaken");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infants with Virology sample taken");
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
     *Auto-Calculate Number of TB cases with documented HIV-positive status who start or continue ART during the reporting period.
     * TB_ART_NEW-ON_ART
     */
    public CohortDefinition newOnARTTBInfected() {

        String sqlQuery = "select tb.patient_id from kenyaemr_etl.etl_tb_enrollment tb\n" +
                "                              join\n" +
                "                          (   select e.patient_id,e.date_started,\n" +
                "                                     e.gender,\n" +
                "                                     e.dob,\n" +
                "                                     d.visit_date as dis_date,\n" +
                "                                     if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "                                     e.regimen, e.regimen_line, e.alternative_regimen,\n" +
                "                                     mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                                     max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art,\n" +
                "                                     max(if(enr.transfer_in_date is not null, 1, 0)) as TIn,\n" +
                "                                     max(fup.visit_date) as latest_vis_date\n" +
                "                              from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started,\n" +
                "                                           mid(min(concat(e.date_started,e.regimen_name)),11) as regimen,\n" +
                "                                           mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line,\n" +
                "                                           max(if(discontinued,1,0))as alternative_regimen\n" +
                "                                    from kenyaemr_etl.etl_drug_event e\n" +
                "                                             join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "                                    group by e.patient_id) e\n" +
                "                                       left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id\n" +
                "                                       left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "                                       left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "                              where  date(e.date_started)  between date_sub(date(:endDate), INTERVAL 3 MONTH ) and date(:endDate)\n" +
                "                              group by e.patient_id\n" +
                "                              having TI_on_art=0\n" +
                "                          ) t on tb.patient_id = t.patient_id\n" +
                "group by tb.patient_id\n" +
                "having max(tb.visit_date) between date(:startDate) and date(:endDate);";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("newOnARTTBInfected");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("TB patients new on ART");
        return cd;

    }

    /**
     *  Auto-Calculate Number of TB cases with documented HIV-positive status who start or continue ART during the reporting period.
     * TB_ART_ALREADY-ON_ART
     */
    public CohortDefinition alreadyOnARTTBInfected() {

        String sqlQuery = "select tb.patient_id from kenyaemr_etl.etl_tb_enrollment tb\n" +
                "                              join\n" +
                "                          (   select e.patient_id,e.date_started,\n" +
                "                                     e.gender,\n" +
                "                                     e.dob,\n" +
                "                                     d.visit_date as dis_date,\n" +
                "                                     if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "                                     e.regimen, e.regimen_line, e.alternative_regimen,\n" +
                "                                     mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                                     max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art,\n" +
                "                                     max(if(enr.transfer_in_date is not null, 1, 0)) as TIn,\n" +
                "                                     max(fup.visit_date) as latest_vis_date\n" +
                "                              from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started,\n" +
                "                                           mid(min(concat(e.date_started,e.regimen_name)),11) as regimen,\n" +
                "                                           mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line,\n" +
                "                                           max(if(discontinued,1,0))as alternative_regimen\n" +
                "                                    from kenyaemr_etl.etl_drug_event e\n" +
                "                                             join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "                                    group by e.patient_id) e\n" +
                "                                       left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id\n" +
                "                                       left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "                                       left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "                              where  date(e.date_started)  < date(:startDate)\n" +
                "                              group by e.patient_id\n" +
                "                              having TI_on_art=0\n" +
                "                          ) t on tb.patient_id = t.patient_id\n" +
                "group by tb.patient_id\n" +
                "having max(tb.visit_date) between date(:startDate) and date(:endDate);";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("alreadyOnARTTBInfected");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("TB patients already on ART");
        return cd;

    }

    /**
     *  Starting TB treatment newly on ART
     *
     */
    public CohortDefinition startingTBTreatmentNewOnART() {

        String sqlQuery = "select a.patient_id\n" +
                "from\n" +
                "     (select fup.visit_date,fup.patient_id,\n" +
                "             greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "             greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "             greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "             d.patient_id as disc_patient,\n" +
                "             d.effective_disc_date as effective_disc_date,\n" +
                "             max(d.visit_date) as date_discontinued,\n" +
                "             de.patient_id as started_on_drugs,\n" +
                "             min(de.date_started) as art_start_date,\n" +
                "             mid(max(concat(fup.visit_date, ifnull(fup.tb_status,''))), 11) screened_using_consultation,\n" +
                "             mid(max(concat(fup.visit_date, ifnull(fup.person_present,''))), 11) lv_person_presen\n" +
                "      from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "             join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "             join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "             left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "             left outer JOIN\n" +
                "               (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                group by patient_id\n" +
                "               ) d on d.patient_id = fup.patient_id\n" +
                "      where fup.visit_date <= date(:endDate)\n" +
                "      group by patient_id\n" +
                "      having (started_on_drugs is not null and started_on_drugs <> '' and min(date_started) >= date_sub(date(:endDate),interval 6 month))\n" +
                "         and (timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30)\n" +
                "         and (\n" +
                "                 (\n" +
                "                     ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(latest_enrolment_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                       and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                     )\n" +
                "                 ))a\n" +
                "       inner join\n" +
                "         (select tb.patient_id from kenyaemr_etl.etl_tb_screening tb where tb.person_present = 978 and tb.resulting_tb_status in(1662,142177) and tb.started_anti_TB = 1065 and\n" +
                "             (tb.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate) or tb.tb_treatment_start_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate)) group by tb.patient_id)t\n" +
                "       on a.patient_id = t.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("startingTBTreatmentNewOnART");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Starting TB treatment newly started ART");
        return cd;

    }

    /**
     *  Starting TB treatment previously on ART
     *
     */
    public CohortDefinition startingTBTreatmentPrevOnART() {

        String sqlQuery = "select a.patient_id\n" +
                "from\n" +
                "        (select fup.visit_date,fup.patient_id,\n" +
                "           greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "           greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "           greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "           d.patient_id as disc_patient,\n" +
                "           d.effective_disc_date as effective_disc_date,\n" +
                "           max(d.visit_date) as date_discontinued,\n" +
                "           de.patient_id as started_on_drugs,\n" +
                "           min(de.date_started) as art_start_date,\n" +
                "           mid(max(concat(fup.visit_date, ifnull(fup.tb_status,''))), 11) screened_using_consultation,\n" +
                "           mid(max(concat(fup.visit_date, ifnull(fup.person_present,''))), 11) lv_person_presen\n" +
                "               from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "           join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "           join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "           left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "             left outer JOIN\n" +
                "             (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "              where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "              group by patient_id\n" +
                "             ) d on d.patient_id = fup.patient_id\n" +
                "/*           left join (select f.patient_id,f.visit_date,f.started_anti_TB as started_anti_tb,f.screened_for_tb as screened_for_tb,f.tb_status as tb_results from kenyaemr_etl.etl_patient_hiv_followup f\n" +
                "                 where started_anti_TB = 1065 and f.screened_for_tb='Yes' and f.tb_status=1662 and f.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate))f on fup.patient_id = f.patient_id*/\n" +
                "    where fup.visit_date <= date(:endDate)\n" +
                "    group by patient_id\n" +
                "    having (started_on_drugs is not null and started_on_drugs <> '' and min(date_started) < date_sub(:endDate,interval 6 month))\n" +
                "       and (timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30)\n" +
                "       and (\n" +
                "               (\n" +
                "                   ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(latest_enrolment_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                     and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                   )\n" +
                "               ))a\n" +
                "          inner join\n" +
                "            (select tb.patient_id from kenyaemr_etl.etl_tb_screening tb where tb.person_present = 978 and tb.resulting_tb_status in(1662,142177) and tb.started_anti_TB = 1065 and\n" +
                "                    (tb.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate) or tb.tb_treatment_start_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate)) group by tb.patient_id)t\n" +
                "            on a.patient_id = t.patient_id group by a.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("startingTBTreatmentPrevOnART");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Starting TB treatment currently on ART");
        return cd;

    }

    /**
     *  New on ART Screened Positive
     *
     */
    public CohortDefinition newOnARTScreenedPositive() {

        String sqlQuery = "select a.patient_id\n" +
                "from\n" +
                "     (select fup.visit_date,fup.patient_id,\n" +
                "             greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "             greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "             greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "             d.patient_id as disc_patient,\n" +
                "             d.effective_disc_date as effective_disc_date,\n" +
                "             max(d.visit_date) as date_discontinued,\n" +
                "             de.patient_id as started_on_drugs,\n" +
                "             min(de.date_started) as art_start_date,\n" +
                "             mid(max(concat(fup.visit_date, ifnull(fup.tb_status,''))), 11) screened_using_consultation,\n" +
                "             mid(max(concat(fup.visit_date, ifnull(fup.person_present,''))), 11) lv_person_presen\n" +
                "      from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "             join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "             join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "             left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "             left outer JOIN\n" +
                "               (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                group by patient_id\n" +
                "               ) d on d.patient_id = fup.patient_id\n" +
                "             left join (select f.patient_id,f.visit_date,f.started_anti_TB as started_anti_tb,f.screened_for_tb as screened_for_tb,f.tb_status as tb_results from kenyaemr_etl.etl_patient_hiv_followup f\n" +
                "                        where started_anti_TB = 1065 and f.screened_for_tb='Yes' and f.tb_status=1662 and f.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate))f on fup.patient_id = f.patient_id\n" +
                "      where fup.visit_date <= date(:endDate)\n" +
                "      group by patient_id\n" +
                "      having (started_on_drugs is not null and started_on_drugs <> '' and min(date_started) between date_sub(:endDate,interval 6 month) and date(:endDate))\n" +
                "         and (timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30)\n" +
                "         and (\n" +
                "                 (\n" +
                "                     ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(latest_enrolment_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                       and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                     )\n" +
                "                 ))a\n" +
                "inner join\n" +
                "         (select tb.patient_id from kenyaemr_etl.etl_tb_screening tb where tb.person_present=978\n" +
                "         and tb.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate) and tb.resulting_tb_status in (1662,142177) group by tb.patient_id)tb on a.patient_id = tb.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("newOnARTScreenedPositive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New on ART Screened Positive");
        return cd;

    }

    /**
     *  Previously on ART Screened Positive
     *
     */
    public CohortDefinition prevOnARTScreenedPositive() {

        String sqlQuery = "select a.patient_id\n" +
                "from\n" +
                "     (select fup.visit_date,fup.patient_id,\n" +
                "             greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "             greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "             greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "             d.patient_id as disc_patient,\n" +
                "             d.effective_disc_date as effective_disc_date,\n" +
                "             max(d.visit_date) as date_discontinued,\n" +
                "             de.patient_id as started_on_drugs,\n" +
                "             min(de.date_started) as art_start_date,\n" +
                "             mid(max(concat(fup.visit_date, ifnull(fup.tb_status,''))), 11) screened_using_consultation,\n" +
                "             mid(max(concat(fup.visit_date, ifnull(fup.person_present,''))), 11) lv_person_presen\n" +
                "      from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "             join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "             join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "             left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "             left outer JOIN\n" +
                "               (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                group by patient_id\n" +
                "               ) d on d.patient_id = fup.patient_id\n" +
                "             left join (select f.patient_id,f.visit_date,f.started_anti_TB as started_anti_tb,f.screened_for_tb as screened_for_tb,f.tb_status as tb_results from kenyaemr_etl.etl_patient_hiv_followup f\n" +
                "                        where started_anti_TB = 1065 and f.screened_for_tb='Yes' and f.tb_status=1662 and f.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate))f on fup.patient_id = f.patient_id\n" +
                "      where fup.visit_date <= date(:endDate)\n" +
                "      group by patient_id\n" +
                "      having (started_on_drugs is not null and started_on_drugs <> '' and min(date_started) < date_sub(:endDate,interval 6 month))\n" +
                "         and (timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30)\n" +
                "         and (\n" +
                "                 (\n" +
                "                     ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(latest_enrolment_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                       and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                     )\n" +
                "                 ))a\n" +
                "       inner join\n" +
                "         (select tb.patient_id from kenyaemr_etl.etl_tb_screening tb where tb.person_present=978\n" +
                "         and tb.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate) and tb.resulting_tb_status in (1662,142177) group by tb.patient_id)tb on a.patient_id = tb.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("prevOnARTScreenedPositive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Previously on ART Screened Positive");
        return cd;

    }

    /**
     *  New on ART Screened Negative
     *
     */
    public CohortDefinition newOnARTScreenedNegative() {

        String sqlQuery = "select a.patient_id\n" +
                "from\n" +
                "     (select fup.visit_date,fup.patient_id,\n" +
                "             greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "             greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "             greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "             d.patient_id as disc_patient,\n" +
                "             d.effective_disc_date as effective_disc_date,\n" +
                "             max(d.visit_date) as date_discontinued,\n" +
                "             de.patient_id as started_on_drugs,\n" +
                "             min(de.date_started) as art_start_date,\n" +
                "             mid(max(concat(fup.visit_date, ifnull(fup.tb_status,''))), 11) screened_using_consultation,\n" +
                "             mid(max(concat(fup.visit_date, ifnull(fup.person_present,''))), 11) lv_person_presen\n" +
                "      from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "             join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "             join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "             left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "             left outer JOIN\n" +
                "               (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                group by patient_id\n" +
                "               ) d on d.patient_id = fup.patient_id\n" +
                "             left join (select f.patient_id,f.visit_date,f.started_anti_TB as started_anti_tb,f.screened_for_tb as screened_for_tb,f.tb_status as tb_results from kenyaemr_etl.etl_patient_hiv_followup f\n" +
                "                        where started_anti_TB = 1065 and f.screened_for_tb='Yes' and f.tb_status=1662 and f.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate))f on fup.patient_id = f.patient_id\n" +
                "      where fup.visit_date <= date(:endDate)\n" +
                "      group by patient_id\n" +
                "      having (started_on_drugs is not null and started_on_drugs <> '' and min(date_started) between date_sub(:endDate,interval 6 month) and date(:endDate))\n" +
                "         and (timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30)\n" +
                "         and (\n" +
                "                 (\n" +
                "                     ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(latest_enrolment_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                       and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                     )\n" +
                "                 ))a\n" +
                "       inner join\n" +
                "         (select n.patient_id from (select tb.patient_id from kenyaemr_etl.etl_tb_screening tb where tb.person_present=978\n" +
                "                                                and tb.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate) and tb.resulting_tb_status =1660 group by tb.patient_id)n\n" +
                "                                                  left join (select tb.patient_id from kenyaemr_etl.etl_tb_screening tb where tb.person_present=978\n" +
                "                                                            and tb.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate) and tb.resulting_tb_status in (1662,142177) group by tb.patient_id)p\n" +
                "                                                    on n.patient_id = p.patient_id where p.patient_id is null group by n.patient_id)tb on a.patient_id = tb.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("newOnARTScreenedNegative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("New on ART Screened Negative");
        return cd;

    }

    /**
     *  Previously on ART Screened Negative
     *
     */
    public CohortDefinition prevOnARTScreenedNegative() {

        String sqlQuery = "select a.patient_id\n" +
                "from\n" +
                "     (select fup.visit_date,fup.patient_id,\n" +
                "             greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "             greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "             greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "             d.patient_id as disc_patient,\n" +
                "             d.effective_disc_date as effective_disc_date,\n" +
                "             max(d.visit_date) as date_discontinued,\n" +
                "             de.patient_id as started_on_drugs,\n" +
                "             min(de.date_started) as art_start_date,\n" +
                "             mid(max(concat(fup.visit_date, ifnull(fup.tb_status,''))), 11) screened_using_consultation,\n" +
                "             mid(max(concat(fup.visit_date, ifnull(fup.person_present,''))), 11) lv_person_presen\n" +
                "      from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "             join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "             join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "             left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "             left outer JOIN\n" +
                "               (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                group by patient_id\n" +
                "               ) d on d.patient_id = fup.patient_id\n" +
                "             left join (select f.patient_id,f.visit_date,f.started_anti_TB as started_anti_tb,f.screened_for_tb as screened_for_tb,f.tb_status as tb_results from kenyaemr_etl.etl_patient_hiv_followup f\n" +
                "                        where started_anti_TB = 1065 and f.screened_for_tb='Yes' and f.tb_status=1662 and f.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate))f on fup.patient_id = f.patient_id\n" +
                "      where fup.visit_date <= date(:endDate)\n" +
                "      group by patient_id\n" +
                "      having (started_on_drugs is not null and started_on_drugs <> '' and min(date_started) < date_sub(:endDate,interval 6 month))\n" +
                "         and (timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30)\n" +
                "         and (\n" +
                "                 (\n" +
                "                     ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(latest_enrolment_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                       and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                     )\n" +
                "                 ))a\n" +
                "       inner join\n" +
                "         (select n.patient_id from (select tb.patient_id from kenyaemr_etl.etl_tb_screening tb where tb.person_present=978\n" +
                "                                                and tb.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate) and tb.resulting_tb_status =1660 group by tb.patient_id)n\n" +
                "                                                  left join (select tb.patient_id from kenyaemr_etl.etl_tb_screening tb where tb.person_present=978\n" +
                "                                                            and tb.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate) and tb.resulting_tb_status in (1662,142177) group by tb.patient_id)p\n" +
                "                                                    on n.patient_id = p.patient_id where p.patient_id is null group by n.patient_id)tb on a.patient_id = tb.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("prevOnARTScreenedNegative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Previously on ART Screened Negative");
        return cd;

    }
    /**
     *  Specimen sent for bacteriologic diagnosis of active TB
     *
     */
    public CohortDefinition specimenSent() {

        String sqlQuery = "select t.patient_id\n" +
                "             from(\n" +
                "                 select fup.visit_date,fup.patient_id,\n" +
                "                        greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "                        greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "                        greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "                        d.patient_id as disc_patient,\n" +
                "                        d.effective_disc_date as effective_disc_date,\n" +
                "                        max(d.visit_date) as date_discontinued,\n" +
                "                        de.patient_id as started_on_drugs,\n" +
                "                        min(de.date_started) as art_start_date\n" +
                "                 from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                        join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                        join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                        left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                        left outer JOIN\n" +
                "                          (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                           where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                           group by patient_id\n" +
                "                          ) d on d.patient_id = fup.patient_id\n" +
                "                     where fup.visit_date <= date(:endDate)\n" +
                "                 group by patient_id\n" +
                "                 having (started_on_drugs is not null and started_on_drugs <> '')\n" +
                "                    and (timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30)\n" +
                "                    and (\n" +
                "                            (\n" +
                "                                ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(latest_enrolment_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                                  and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                                )\n" +
                "                            )\n" +
                "                 ) t\n" +
                "            inner join (select patient_id from kenyaemr_etl.etl_tb_screening s\n" +
                "            where (s.genexpert_ordered = 162202 or s.spatum_smear_ordered = 307)\n" +
                "            and s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate) group by s.patient_id\n" +
                "            union all\n" +
                "            select x.patient_id from kenyaemr_etl.etl_laboratory_extract x where x.lab_test in (307,1465,162202)\n" +
                "            and x.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate) group by x.patient_id)s on s.patient_id = t.patient_id\n" +
                "group by t.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("specimenSent");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Specimen sent for bacteriologic diagnosis of active TB");
        return cd;

    }

    /**
     *  GeneXpert MTB/RIF assay (with or without other testing
     *
     */
    public CohortDefinition geneXpertMTBRIF() {

        String sqlQuery = "select t.patient_id\n" +
                "from(\n" +
                "    select fup.visit_date,fup.patient_id,\n" +
                "           greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "           greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "           greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "           d.patient_id as disc_patient,\n" +
                "           d.effective_disc_date as effective_disc_date,\n" +
                "           max(d.visit_date) as date_discontinued,\n" +
                "           de.patient_id as started_on_drugs,\n" +
                "           min(de.date_started) as art_start_date\n" +
                "    from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "           join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "           join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "           left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "           left outer JOIN\n" +
                "             (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "              where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "              group by patient_id\n" +
                "             ) d on d.patient_id = fup.patient_id\n" +
                "    where fup.visit_date <= date(:endDate)\n" +
                "    group by patient_id\n" +
                "    having (started_on_drugs is not null and started_on_drugs <> '')\n" +
                "       and (timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30)\n" +
                "       and (\n" +
                "               (\n" +
                "                   ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(latest_enrolment_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                     and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                   )\n" +
                "               )\n" +
                "    ) t\n" +
                "      inner join (select patient_id from kenyaemr_etl.etl_tb_screening s\n" +
                "                  where s.genexpert_ordered = 162202\n" +
                "                    and s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate) group by s.patient_id\n" +
                "                  union all\n" +
                "                  select patient_id from kenyaemr_etl.etl_laboratory_extract x where x.lab_test=162202\n" +
                "                   and x.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate) group by x.patient_id)s on s.patient_id = t.patient_id\n" +
                "group by t.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("geneXpertMTBRIF");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("GeneXpert MTB/RIF assay (with or without other testing");
        return cd;

    }

    /**
     *  Smear microscopy only
     *
     */
    public CohortDefinition smearMicroscopy() {

        String sqlQuery = "select t.patient_id\n" +
                "from(\n" +
                "    select fup.visit_date,fup.patient_id,\n" +
                "           greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "           greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "           greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "           d.patient_id as disc_patient,\n" +
                "           d.effective_disc_date as effective_disc_date,\n" +
                "           max(d.visit_date) as date_discontinued,\n" +
                "           de.patient_id as started_on_drugs,\n" +
                "           min(de.date_started) as art_start_date\n" +
                "    from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "           join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "           join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "           left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "           left outer JOIN\n" +
                "             (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "              where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "              group by patient_id\n" +
                "             ) d on d.patient_id = fup.patient_id\n" +
                "    where fup.visit_date <= date(:endDate)\n" +
                "    group by patient_id\n" +
                "    having (started_on_drugs is not null and started_on_drugs <> '')\n" +
                "       and (timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30)\n" +
                "       and (\n" +
                "               (\n" +
                "                   ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(latest_enrolment_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                     and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                   )\n" +
                "               )\n" +
                "    ) t\n" +
                "      inner join (select a.patient_id from (select patient_id from kenyaemr_etl.etl_tb_screening s\n" +
                "                                            where (s.spatum_smear_ordered = 307 and ifnull(s.genexpert_ordered,0) !=162202 and ifnull(s.chest_xray_ordered,0) !=12)\n" +
                "                                              and s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) group by s.patient_id\n" +
                "                                            union all\n" +
                "                                            select patient_id from kenyaemr_etl.etl_laboratory_extract x where x.lab_test =307\n" +
                "    and x.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate) )a\n" +
                "                                            )b on t.patient_id = b.patient_id group by t.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("smearMicroscopy");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Smear microscopy only");
        return cd;

    }
    /**
     *  Additional test other than GeneXpert
     *
     */
    public CohortDefinition additionalTBTests() {

        String sqlQuery = "select a.patient_id from (select t.patient_id\n" +
                "             from(\n" +
                "                 select fup.visit_date,fup.patient_id,\n" +
                "                        greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "                        greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "                        greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "                        d.patient_id as disc_patient,\n" +
                "                        d.effective_disc_date as effective_disc_date,\n" +
                "                        max(d.visit_date) as date_discontinued,\n" +
                "                        de.patient_id as started_on_drugs,\n" +
                "                        min(de.date_started) as art_start_date\n" +
                "                 from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                        join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                        join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                        left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                        left outer JOIN\n" +
                "                          (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                           where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                           group by patient_id\n" +
                "                          ) d on d.patient_id = fup.patient_id\n" +
                "                           where fup.visit_date <= date(:endDate)\n" +
                "                 group by patient_id\n" +
                "                 having (started_on_drugs is not null and started_on_drugs <> '')\n" +
                "                    and (timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30)\n" +
                "                    and (\n" +
                "                            (\n" +
                "                                ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(latest_enrolment_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                                  and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                                )\n" +
                "                            )\n" +
                "                 ) t\n" +
                "                   inner join (select patient_id from kenyaemr_etl.etl_laboratory_extract x where x.lab_test=1465\n" +
                "                         and x.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate))x on t.patient_id = x.patient_id)a\n" +
                "group by a.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("additionalTBTests");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Additional test other than GeneXpert");
        return cd;

    }

    /**
     *  Starting TB treatment previously on ART
     *
     */
    public CohortDefinition resultsReturned() {

        String sqlQuery = "select a.patient_id\n" +
                "             from(select t.patient_id from (\n" +
                "                 select fup.visit_date,fup.patient_id,\n" +
                "                        greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "                        greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "                        greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "                        d.patient_id as disc_patient,\n" +
                "                        d.effective_disc_date as effective_disc_date,\n" +
                "                        max(d.visit_date) as date_discontinued,\n" +
                "                        de.patient_id as started_on_drugs,\n" +
                "                        min(de.date_started) as art_start_date\n" +
                "                 from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                        join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                        join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                        left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                        left outer JOIN\n" +
                "                          (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                           where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                           group by patient_id\n" +
                "                          ) d on d.patient_id = fup.patient_id\n" +
                "                 where fup.visit_date <= date(:endDate)\n" +
                "                 group by patient_id\n" +
                "                 having (started_on_drugs is not null and started_on_drugs <> '')\n" +
                "                    and (timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30)\n" +
                "                    and (\n" +
                "                            (\n" +
                "                                ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(latest_enrolment_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                                  and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                                )\n" +
                "                            )\n" +
                "                 ) t\n" +
                "                   join (select patient_id from kenyaemr_etl.etl_tb_screening s\n" +
                "                         where (s.genexpert_result in(162203,162204,164104) or s.spatum_smear_result = 703)\n" +
                "                           and s.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate)\n" +
                "                         union all\n" +
                "                         select patient_id from kenyaemr_etl.etl_laboratory_extract x where x.lab_test in (162202,1465,307) and x.test_result in (162203,162204,162104,703,1362,1363,1364)\n" +
                "                          and x.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH ) and date(:endDate))f on t.patient_id = f.patient_id)a\n" +
                "group by a.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("resultsReturned");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Positive result returned for bacteriologic diagnosis of active TB");
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
    public CohortDefinition testedPmtct() {

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
    }
    /**
     * Tested positive PMTCT at ANC-1
     */
    public CohortDefinition testedPositivePmtctANC1() {
        String sqlQuery = "select v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "  left join (select t.patient_id, t.visit_date from kenyaemr_etl.etl_hts_test t where t.test_type = 2 and t.final_test_result = 'Positive' and t.hts_entry_point = 160538 and  t.visit_date <= date(:endDate))t\n" +
                "    on v.patient_id = t.patient_id and v.visit_date = t.visit_date\n" +
                "where v.anc_visit_number = 1 and v.visit_date between date (:startDate) and date(:endDate) and (v.final_test_result = 'Positive' or t.patient_id is not null);";
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
        String sqlQuery = "select v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "  left join (select t.patient_id, t.visit_date from kenyaemr_etl.etl_hts_test t where t.test_type = 1 and t.final_test_result = 'Negative' and t.hts_entry_point = 160538 and  t.visit_date <= date(:endDate))t\n" +
                "    on v.patient_id = t.patient_id and v.visit_date = t.visit_date\n" +
                "where v.anc_visit_number = 1 and v.visit_date between date (:startDate) and date(:endDate) and (v.final_test_result = 'Negative' or t.patient_id is not null);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HTS Negative at PMTCT ANC-1");
        return cd;
    }
    /**
     * Tested Positive PMTCT post ANC-1
     */
    public CohortDefinition testedPositivePmtctPostANC1() {
        String sqlQuery = "select e.patient_id from\n" +
                "kenyaemr_etl.etl_mch_enrollment e\n" +
                "left join (select av.patient_id from kenyaemr_etl.etl_mch_antenatal_visit av where av.anc_visit_number >1\n" +
                "   and av.visit_date between date_sub(date(:endDate),interval 3 MONTH) and date(:endDate) group by av.patient_id having\n" +
                "   mid(max(concat(av.visit_date,av.final_test_result)),11) ='Positive')av on av.patient_id = e.patient_id\n" +
                "left join (select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where\n" +
                "   d.visit_date between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate) group by d.patient_id having\n" +
                "   mid(max(concat(d.visit_date,d.final_test_result)),11) ='Positive')d on d.patient_id = e.patient_id\n" +
                "left join (select pv.patient_id from kenyaemr_etl.etl_mch_postnatal_visit pv where\n" +
                "   pv.visit_date between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate) group by pv.patient_id having\n" +
                "   mid(max(concat(pv.visit_date,pv.final_test_result)),11) ='Positive') pv on pv.patient_id = e.patient_id\n" +
                "left join (select t.patient_id,t.visit_date from kenyaemr_etl.etl_hts_test t where\n" +
                "   t.visit_date between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate) and t.hts_entry_point in (160538,160456,1623) group by t.patient_id having\n" +
                "   mid(max(concat(t.visit_date,t.final_test_result)),11) ='Positive' and mid(max(concat(t.visit_date,t.test_type)),11) =2)t on t.patient_id = e.patient_id\n" +
                "left join (select v.patient_id,v.visit_date from kenyaemr_etl.etl_mch_antenatal_visit v where v.anc_visit_number =1)v on e.patient_id = v.patient_id\n" +
                "where av.patient_id is not null or d.patient_id is not null or pv.patient_id is not null or (t.visit_date > v.visit_date and t.patient_id is not null)\n" +
                "group by e.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested positive at PMTCT post ANC-1");
        return cd;
    }
    /**
     * Tested Negative PMTCT post ANC-1
     */
    public CohortDefinition testedNegativePmtctPostANC1() {
        String sqlQuery = "select e.patient_id from\n" +
                "kenyaemr_etl.etl_mch_enrollment e\n" +
                "left join (select av.patient_id from kenyaemr_etl.etl_mch_antenatal_visit av where av.anc_visit_number >1\n" +
                "   and av.visit_date between date_sub(date(:endDate),interval 3 MONTH) and date(:endDate) group by av.patient_id having\n" +
                "   mid(max(concat(av.visit_date,av.final_test_result)),11) ='Negative')av on av.patient_id = e.patient_id\n" +
                "left join (select d.patient_id from kenyaemr_etl.etl_mchs_delivery d where\n" +
                "   d.visit_date between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate) group by d.patient_id having\n" +
                "   mid(max(concat(d.visit_date,d.final_test_result)),11) ='Negative')d on d.patient_id = e.patient_id\n" +
                "left join (select pv.patient_id from kenyaemr_etl.etl_mch_postnatal_visit pv where\n" +
                "   pv.visit_date between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate) group by pv.patient_id having\n" +
                "   mid(max(concat(pv.visit_date,pv.final_test_result)),11) ='Negative') pv on pv.patient_id = e.patient_id\n" +
                "left join (select t.patient_id,t.visit_date from kenyaemr_etl.etl_hts_test t where\n" +
                "   t.visit_date between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate) and t.hts_entry_point in (160538,160456,1623) group by t.patient_id having\n" +
                "   mid(max(concat(t.visit_date,t.final_test_result)),11) ='Negative')t on t.patient_id = e.patient_id\n" +
                "left join (select v.patient_id,v.visit_date from kenyaemr_etl.etl_mch_antenatal_visit v where v.anc_visit_number =1)v on e.patient_id = v.patient_id\n" +
                "where av.patient_id is not null or d.patient_id is not null or pv.patient_id is not null or (t.visit_date > v.visit_date and t.patient_id is not null)\n" +
                "group by e.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested negative at PMTCT post ANC-1");
        return cd;
    }

    /**
     *HTS TEST Compositions by Strategies
     * HP: Hospital Patient Testing
     * Compositions for HTS_TST Datim indicators
     *
     * @return
     */
    public CohortDefinition testedHospitalPatient() {

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

    /**HTS Strategies
     *NP: HTS for non-patients
     * Compositions for HTS_TST Datim indicators
     *
     * @return
     */
    public CohortDefinition testedNonPatient() {

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
    }
    /**HTS Strategies
     * VI:Integrated VCT Center
     * Compositions for HTS_TST Datim indicators
     *
     * @return
     */
    public CohortDefinition testedIntegratedVCT() {

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
    }
    /**HTS Strategies
     * Stand Alone VCT Center
     * Compositions for HTS_TST Datim indicators
     *
     * @return
     */
    public CohortDefinition testedStandAloneVCT() {

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
    }
    /**HTS Strategies
     * HB: Home Based Testing
     * Compositions for HTS_TST Datim indicators
     *
     * @return
     */
    public CohortDefinition testedHomeBasedTesting() {

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
    }
    /**HTS Strategies
     * MO: Mobile Outreach HTS
     * Compositions for HTS_TST Datim indicators
     *
     * @return
     */
    public CohortDefinition testedMobileOutreach() {

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
    }
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
    public CohortDefinition testedOthers() {

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
        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.test_type =2 and hts.final_test_result ='Positive' and hts.patient_given_result ='Yes'" +
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
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialNegativeHIVTestResult",ReportUtils.map(initialNegativeHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResult",ReportUtils.map(positiveHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("testedIntegratedVCT",ReportUtils.map(testedIntegratedVCT(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedStandAloneVCT",ReportUtils.map(testedStandAloneVCT(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialNegativeHIVTestResult",ReportUtils.map(initialNegativeHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedIntegratedVCT OR testedStandAloneVCT) AND initialNegativeHIVTestResult");
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
        cd.addSearch("testedIntegratedVCT",ReportUtils.map(testedIntegratedVCT(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedStandAloneVCT",ReportUtils.map(testedStandAloneVCT(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResult",ReportUtils.map(positiveHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedIntegratedVCT OR testedStandAloneVCT) AND positiveHIVTestResult");
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
        cd.addSearch("initialNegativeHIVTestResultInfants",ReportUtils.map(initialNegativeHIVTestResultInfants(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedPediatricClinics",ReportUtils.map(testedPediatricClinics(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedPediatricClinics AND initialNegativeHIVTestResultInfants) AND NOT (testedIndexTesting OR testedSocialNetworks)");
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
        cd.addSearch("testedPediatricClinics",ReportUtils.map(testedPediatricClinics(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResultInfants",ReportUtils.map(positiveHIVTestResultInfants(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedPediatricClinics AND positiveHIVTestResultInfants) AND NOT (testedIndexTesting OR testedSocialNetworks)");
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
        cd.addSearch("testedMalnutritionClinics",ReportUtils.map(testedMalnutritionClinics(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialNegativeHIVTestResultInfants",ReportUtils.map(initialNegativeHIVTestResultInfants(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedMalnutritionClinics AND initialNegativeHIVTestResultInfants) AND NOT (testedIndexTesting OR testedSocialNetworks)");
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
        cd.addSearch("testedMalnutritionClinics",ReportUtils.map(testedMalnutritionClinics(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResultInfants",ReportUtils.map(positiveHIVTestResultInfants(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedMalnutritionClinics AND positiveHIVTestResultInfants) AND NOT (testedIndexTesting OR testedSocialNetworks)");
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
        cd.addSearch("testedTBClinic",ReportUtils.map(testedTBClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialNegativeHIVTestResult",ReportUtils.map(initialNegativeHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedTBClinic AND initialNegativeHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks)");
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
        cd.addSearch("testedTBClinic",ReportUtils.map(testedTBClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResult",ReportUtils.map(positiveHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedTBClinic AND positiveHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks)");
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
        cd.addSearch("testedInpatientServices",ReportUtils.map(testedInpatientServices(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialNegativeHIVTestResult",ReportUtils.map(initialNegativeHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedInpatientServices AND initialNegativeHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks)");
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
        cd.addSearch("testedInpatientServices",ReportUtils.map(testedInpatientServices(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResult",ReportUtils.map(positiveHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedInpatientServices AND positiveHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks)");
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
        cd.addSearch("testedOthersOPD",ReportUtils.map(testedOthersOPD(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialNegativeHIVTestResult",ReportUtils.map(initialNegativeHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedOthersOPD AND initialNegativeHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks)");
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
        cd.addSearch("testedOthersOPD",ReportUtils.map(testedOthersOPD(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResult",ReportUtils.map(positiveHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedOthersOPD AND positiveHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks)");
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
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResult",ReportUtils.map(positiveHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialNegativeHIVTestResult",ReportUtils.map(initialNegativeHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("testedSTIClinic",ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResult",ReportUtils.map(positiveHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedSTIClinic AND positiveHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks)");
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
        cd.addSearch("testedSTIClinic",ReportUtils.map(testedSTIClinic(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialNegativeHIVTestResult",ReportUtils.map(initialNegativeHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedSTIClinic AND initialNegativeHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks)");
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
        cd.addSearch("testedEmergencyWard",ReportUtils.map(testedEmergencyWard(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResult",ReportUtils.map(positiveHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedEmergencyWard AND positiveHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks)");
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
        cd.addSearch("testedEmergencyWard",ReportUtils.map(testedEmergencyWard(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialNegativeHIVTestResult",ReportUtils.map(initialNegativeHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedEmergencyWard AND initialNegativeHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks)");
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
        cd.addSearch("testedVMMCServices",ReportUtils.map(testedVMMCServices(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("positiveHIVTestResult",ReportUtils.map(positiveHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedVMMCServices AND positiveHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks)");
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
        cd.addSearch("testedVMMCServices",ReportUtils.map(testedVMMCServices(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("initialNegativeHIVTestResult",ReportUtils.map(initialNegativeHIVTestResult(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(testedVMMCServices AND initialNegativeHIVTestResult) AND NOT (testedIndexTesting OR testedSocialNetworks)");
        return cd;
    }
    /*Newly Started ART While Pregnant*/
    public CohortDefinition newlyStartedARTWhilePregnant() {
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
                "            left outer join kenyaemr_etl.etl_mch_postnatal_visit pv on pv.patient_id=e.patient_id\n" +
                "     where  fup.pregnancy_status = 1065\n" +
                "            or (e.date_started >= enr.lst_mch_visit_date)\n" +
                "            and (e.date_started < dl.lst_del_visit_date or dl.lst_del_visit_date is null)\n" +
                "            and (e.date_started < psnv.lst_pv_visit_date or psnv.lst_pv_visit_date is null )\n" +
                "            and date(e.date_started) between date(:startDate) and :endDate\n" +
                "     group by e.patient_id\n" +
                "     having TI_on_art=0\n" +
                "     )net;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_New_Pregnant");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Newly Started ART While Pregnant");
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

    /*Annual Cohort Indicators*/

    /*Total HEI Cohort*/
    public CohortDefinition totalHEICohort() {

        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_hei_enrollment e\n" +
                "join kenyaemr_etl.etl_patient_demographics de on de.patient_id = e.patient_id\n" +
                "where timestampdiff(month,de.dob,:endDate) =24 and e.visit_date between date(:startDate) and date(:endDate);";
//TODO age == 24 months and (enrollmentdate == 24 months ago or transfer in is true),
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PMTCT_FO_HEI_COHORT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total HEI Cohort");
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
    public CohortDefinition hivInfectedHEICohortSql() {

        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_hei_enrollment e where e.hiv_status_at_exit ='Positive';";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PMTCT_FO_INFECTED_HEI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Infected HEI Cohort");
        return cd;

    }

    /*Uninfected HEI Cohort*/
    public CohortDefinition hivUninfectedHEICohortSql() {

        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_hei_enrollment e where e.hiv_status_at_exit ='Negative';";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PMTCT_FO_UNINFECTED_HEI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Uninfected HEI Cohort");
        return cd;

    }

    /*Unknown HIV Status HEI Cohort*/
    public CohortDefinition unknownHIVStatusHEICohortSql() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_hei_enrollment e where e.hiv_status_at_exit not in ('Negative','Positive');";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PMTCT_FO_HEI_UNKNOWN_HIV_STATUS");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Unknown HIV Status HEI Cohort");
        return cd;

    }

    /*HEI died*/
    public CohortDefinition heiDiedSql() {

        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_hei_enrollment e where e.exit_reason = 160034;";

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
        cd.addSearch("pmtctFoDenominator",ReportUtils.map(pmtctFoDenominator(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("hivInfectedHEICohortSql", ReportUtils.map(hivInfectedHEICohortSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("pmtctFoDenominator AND hivInfectedHEICohortSql");
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
        cd.addSearch("pmtctFoDenominator",ReportUtils.map(pmtctFoDenominator(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("hivUninfectedHEICohortSql",ReportUtils.map(hivUninfectedHEICohortSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("pmtctFoDenominator AND hivUninfectedHEICohortSql");
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
        cd.addSearch("pmtctFoDenominator",ReportUtils.map(pmtctFoDenominator(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("unknownHIVStatusHEICohortSql",ReportUtils.map(unknownHIVStatusHEICohortSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("pmtctFoDenominator AND unknownHIVStatusHEICohortSql");
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
        cd.addSearch("pmtctFoDenominator",ReportUtils.map(pmtctFoDenominator(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("unknownHIVStatusHEICohort",ReportUtils.map(unknownHIVStatusHEICohort(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("heiDiedSql",ReportUtils.map(heiDiedSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("pmtctFoDenominator AND unknownHIVStatusHEICohort AND heiDiedSql");
        return cd;
    }
    /**
     * Number of patients who experienced interruption in treatment in the previous reporting period
     * @return
     */
    public CohortDefinition experiencedIITPreviousReportingPeriod() {

        String sqlQuery = "select  e.patient_id\n" +
                "             from (\n" +
                "             select fup_prev_period.patient_id,\n" +
                "                    max(fup_prev_period.visit_date) as prev_period_latest_vis_date,\n" +
                "                    mid(max(concat(fup_prev_period.visit_date,fup_prev_period.next_appointment_date)),11) as prev_period_latest_tca,\n" +
                "                    max(d.visit_date) as date_discontinued,\n" +
                "                    d.patient_id as disc_patient,\n" +
                "                    fup_reporting_period.first_visit_after_IIT as first_visit_after_IIT\n" +
                "             from kenyaemr_etl.etl_patient_hiv_followup fup_prev_period\n" +
                "                    join (select fup_reporting_period.patient_id,min(fup_reporting_period.visit_date) as first_visit_after_IIT from kenyaemr_etl.etl_patient_hiv_followup fup_reporting_period where fup_reporting_period.visit_date >= date_sub(date(:endDate) , interval 3 MONTH) group by fup_reporting_period.patient_id)fup_reporting_period on fup_reporting_period.patient_id = fup_prev_period.patient_id\n" +
                "                    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup_prev_period.patient_id\n" +
                "                    join kenyaemr_etl.etl_hiv_enrollment e on fup_prev_period.patient_id=e.patient_id\n" +
                "                    left outer JOIN\n" +
                "                      (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                       where date(visit_date) <= curdate()  and program_name='HIV'\n" +
                "                       group by patient_id\n" +
                "                      ) d on d.patient_id = fup_prev_period.patient_id\n" +
                "             where fup_prev_period.visit_date < date_sub(date(:endDate) , interval 3 MONTH)\n" +
                "             group by patient_id\n" +
                "             having (\n" +
                "                        (((date(prev_period_latest_tca) < date(:endDate)) and (date(prev_period_latest_vis_date) < date(prev_period_latest_tca)))) and ((date(prev_period_latest_tca) > date(date_discontinued) and date(prev_period_latest_vis_date) > date(date_discontinued)) or disc_patient is null )\n" +
                "                          and timestampdiff(day, date(prev_period_latest_tca),date(:startDate)) > 30)\n" +
                "             )e;";

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
     * Number of ART patients who experienced IIT during any previous reporting period, who successfully restarted ARVs within the reporting period and remained on treatment until the end of the reporting period
     * @return
     */
    public CohortDefinition txRTT() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("experiencedIITPreviousReportingPeriod", ReportUtils.map(experiencedIITPreviousReportingPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND experiencedIITPreviousReportingPeriod");
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
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("experiencedIITPreviousReportingPeriod", ReportUtils.map(experiencedIITPreviousReportingPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsExperiencedIITAtleast6Months", ReportUtils.map(patientsExperiencedIITAtleast6Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND experiencedIITPreviousReportingPeriod AND patientsExperiencedIITAtleast6Months");
        return cd;
    }

    /*Patients on ART with Suppressed undocumented VL within last 12 Months*/
    public CohortDefinition onARTWithSuppressedUndocumentedVLLast12Months() {

        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                "       inner join\n" +
                "         (\n" +
                "         select\n" +
                "                patient_id,\n" +
                "                visit_date,\n" +
                "                if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                urgency\n" +
                "         from kenyaemr_etl.etl_laboratory_extract\n" +
                "         where lab_test in (1305, 856)  and visit_date between  date_sub(:endDate , interval 12 MONTH) and visit_date\n" +
                "           and urgency is null or urgency not in ('IMMEDIATELY','ROUTINE')\n" +
                "         ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "\n" +
                "       left JOIN\n" +
                "         (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "          where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "          group by patient_id\n" +
                "         ) d on d.patient_id = e.patient_id\n" +
                "where e.program = 'HIV'  and date(e.date_started) <= date_sub(:endDate, interval 3 MONTH)\n" +
                "group by e.patient_id\n" +
                "having mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)=\"LDL\" or mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)<1000;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_SUPP_UNDOCUMENTED_ALL");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients on ART with Suppressed undocumented VL within last 12 Months");
        return cd;

    }
    /**
     * Women on ART and pregnant or breastfeeding during their current VL test by urgency/test type
     * */
    public CohortDefinition pregnantOnARTWithSuppressedVLLast12Months(String testType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentSuppVLResultByTestType", ReportUtils.map(currentSuppVLResultByTestType(testType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTXAtleast3Months", ReportUtils.map(patientInTXAtleast3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentVLTestPregnantOrderReason", ReportUtils.map(currentVLTestPregnantOrderReason(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND currentSuppVLResultByTestType AND patientInTXAtleast3Months AND currentVLTestPregnantOrderReason");
        return cd;
    }
    /**
     * Women on ART and pregnant or breastfeeding during their current VL test by urgency/test type
     * */
    public CohortDefinition breastfeedingOnARTWithSuppressedVLLast12Months(String testType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentSuppVLResultByTestType", ReportUtils.map(currentSuppVLResultByTestType(testType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTXAtleast3Months", ReportUtils.map(patientInTXAtleast3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentVLTestBreastfeedingOrderReason", ReportUtils.map(currentVLTestBreastfeedingOrderReason(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND currentSuppVLResultByTestType AND patientInTXAtleast3Months AND currentVLTestBreastfeedingOrderReason");
        return cd;
    }

    /*Pregnant Women on ART with Suppressed targeted VL within last 12 Months*/
    public CohortDefinition pregnantOnARTWithSuppressedTargetedVLLast12Months() {

        String sqlQuery = "select vl.patient_id from\n" +
                "                          (select e.patient_id from kenyaemr_etl.etl_drug_event e\n" +
                "                                                      inner join\n" +
                "  (\n" +
                "  select\n" +
                " l.patient_id,\n" +
                " l.visit_date,\n" +
                " if(l.lab_test = 856, l.test_result, if(l.lab_test=1305 and l.test_result = 1302, \"LDL\",\"\")) as vl_result,\n" +
                " l.urgency\n" +
                "  from kenyaemr_etl.etl_laboratory_extract l\n" +
                "  where l.lab_test in (1305, 856)  and l.visit_date between  date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "and l.urgency='IMMEDIATELY'\n" +
                "  ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "left JOIN\n" +
                "  (select pd.patient_id, pd.visit_date from kenyaemr_etl.etl_patient_program_discontinuation pd\n" +
                "   where date(visit_date) <= date(:endDate) and pd.program_name='HIV'\n" +
                "   group by pd.patient_id\n" +
                "  ) d on d.patient_id = e.patient_id\n" +
                "where e.program = 'HIV'  and date(e.date_started) <= date_sub(:endDate, interval 3 MONTH)\n" +
                "group by e.patient_id\n" +
                "having mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)=\"LDL\" or mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)<1000) vl\n" +
                " inner join\n" +
                "   (select mid(max(concat(en.visit_date,en.patient_id)),11 )latest_enr\n" +
                "    from kenyaemr_etl.etl_mch_enrollment en\n" +
                "           left outer join (select mid(max(concat(del.visit_date,del.patient_id)),11 )latest_del, max(visit_date) lst_del_visit_date\n" +
                " from kenyaemr_etl.etl_mchs_delivery del group by del.patient_id) dl on dl.latest_del = en.patient_id\n" +
                "           left outer join (select mid(max(concat(pv.visit_date,pv.patient_id)),11 )latest_pv, max(visit_date) lst_pv_visit_date\n" +
                " from kenyaemr_etl.etl_mch_postnatal_visit pv group by pv.patient_id) psnv on psnv.lst_pv_visit_date = en.patient_id\n" +
                "    group by en.patient_id)t on t.latest_enr = vl.patient_id\n" +
                " left join (select fup.patient_id from kenyaemr_etl.etl_patient_hiv_followup fup where fup.pregnancy_status =1065 and fup.voided = 0\n" +
                "            group by fup.patient_id having max(fup.visit_date) between date_sub(:endDate, interval 12 MONTH )\n" +
                "            and date(:endDate)) f on vl.patient_id = f.patient_id ;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_SUPP_PREGNANT_TARGETED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Pregnant Women on ART with Suppressed targeted VL within last 12 Months");
        return cd;

    }
    /**
     * TX_PVLS (suppressed / numerator) by test type
     * @param
     * @return
     */
    public CohortDefinition currentSuppVLResultByTestType(String testType) {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select a.patient_id from (select\n" +
                "b.patient_id,\n" +
                "b.latest_visit_date,\n" +
                "b.vl_result,\n" +
                "b.urgency as urgency\n" +
                "from (select x.patient_id as patient_id,max(x.visit_date) as latest_visit_date,mid(max(concat(x.visit_date,x.lab_test)),11) as lab_test, mid(max(concat(x.visit_date,x.urgency)),11) as urgency,\n" +
                "if(mid(max(concat(x.visit_date,x.lab_test)),11) = 856, mid(max(concat(x.visit_date,x.test_result)),11), if(mid(max(concat(x.visit_date,x.lab_test)),11)=1305 and mid(max(concat(x.visit_date,x.test_result)),11) = 1302, 'LDL','')) as vl_result\n" +
                "from kenyaemr_etl.etl_laboratory_extract x where x.lab_test in (1305,856) and x.visit_date <= date(:endDate)\n" +
                "group by x.patient_id)b\n" +
                "group by b.patient_id\n" +
                "having b.latest_visit_date between\n" +
                "date_sub(:endDate , interval 12 MONTH) and date(:endDate) and b.urgency = '"+testType+"' and (b.vl_result < 1000 or b.vl_result='LDL'))a;";
        cd.setName("txpvlsNumByTestType");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("txpvlsNumByTestType");

        return cd;
    }
    /**
     * Pregnant during last VL test
     * @param
     * @return
     */
    public CohortDefinition currentVLTestPregnantOrderReason() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select a.patient_id\n" +
                "from (select b.patient_id, b.latest_visit_date, b.order_reason\n" +
                "      from (select x.patient_id                                       as patient_id,\n" +
                "                   max(x.visit_date)                                  as latest_visit_date,\n" +
                "                   mid(max(concat(x.visit_date, x.order_reason)), 11) as order_reason\n" +
                "            from kenyaemr_etl.etl_laboratory_extract x\n" +
                "            where x.lab_test in (1305, 856)\n" +
                "              and x.visit_date <= date(:endDate)\n" +
                "            group by x.patient_id)b\n" +
                "      group by b.patient_id\n" +
                "      having b.latest_visit_date between\n" +
                "                 date_sub(:endDate, interval 12 MONTH) and date(:endDate)\n" +
                "         and order_reason = 1434)a;";
        cd.setName("currentVLTestPregnantOrderReason");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Pregnant during last VL test");

        return cd;
    }
    /**
     * Breastfeeding during last VL test
     * @param
     * @return
     */
    public CohortDefinition currentVLTestBreastfeedingOrderReason() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select a.patient_id\n" +
                "from (select b.patient_id, b.latest_visit_date, b.order_reason\n" +
                "      from (select x.patient_id                                       as patient_id,\n" +
                "                   max(x.visit_date)                                  as latest_visit_date,\n" +
                "                   mid(max(concat(x.visit_date, x.order_reason)), 11) as order_reason\n" +
                "            from kenyaemr_etl.etl_laboratory_extract x\n" +
                "            where x.lab_test in (1305, 856)\n" +
                "              and x.visit_date <= date(:endDate)\n" +
                "            group by x.patient_id)b\n" +
                "      group by b.patient_id\n" +
                "      having b.latest_visit_date between\n" +
                "                 date_sub(:endDate, interval 12 MONTH) and date(:endDate)\n" +
                "         and order_reason = 159882)a;";
        cd.setName("currentVLTestBreastfeedingOrderReason");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Breastfeeding during last VL test");

        return cd;
    }

    public CohortDefinition kpOnARTSuppVLLast12Months(String testType,KPTypeDataDefinition kpType) {

        String sqlQuery = "select a.patient_id as patient_id\n" +
                "            from(select t.patient_id,vl.vl_date,vl.vl_result,vl.urgency,t.kp_type from (\n" +
                "                         select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "                                greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "                                greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "                                greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "                                d.patient_id as disc_patient,\n" +
                "                                d.effective_disc_date as effective_disc_date,\n" +
                "                                max(d.visit_date) as date_discontinued,\n" +
                "                                de.patient_id as started_on_drugs,\n" +
                "                                de.date_started,\n" +
                "                                mid(max(concat(fup.visit_date,fup.key_population_type)),11) as kp_type\n" +
                "                         from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                                join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                                join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                                left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:startDate)\n" +
                "                                left outer JOIN\n" +
                "                                  (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                   where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                   group by patient_id\n" +
                "                                  ) d on d.patient_id = fup.patient_id\n" +
                "                         where fup.visit_date <= date(:endDate)\n" +
                "                         group by patient_id\n" +
                "                         having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "                             (\n" +
                "                                 ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                                   and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                                 )\n" +
                "                             ) order by date_started desc\n" +
                "                         ) t\n" +
                "                           inner join (\n" +
                "                                      select\n" +
                "                                             b.patient_id,\n" +
                "                                             max(b.visit_date) as vl_date,\n" +
                "                                             mid(max(concat(b.visit_date,b.lab_test)),11) as lab_test,\n" +
                "                                             if(mid(max(concat(b.visit_date,b.lab_test)),11) = 856, mid(max(concat(b.visit_date,b.test_result)),11), if(mid(max(concat(b.visit_date,b.lab_test)),11)=1305 and mid(max(concat(visit_date,test_result)),11) = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                                             mid(max(concat(b.visit_date,b.urgency)),11) as urgency\n" +
                "                                      from (select x.patient_id as patient_id,x.visit_date as visit_date,x.lab_test as lab_test, x.test_result as test_result,urgency as urgency\n" +
                "                                            from kenyaemr_etl.etl_laboratory_extract x where x.lab_test in (1305,856)\n" +
                "                                            group by x.patient_id,x.visit_date order by visit_date desc)b\n" +
                "                                      group by patient_id\n" +
                "                            having mid(max(concat(visit_date,lab_test)),11) in (1305,856) and max(visit_date) between\n" +
                "                                date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "                            )vl\n" +
                "                   on t.patient_id = vl.patient_id  where urgency = "+testType+" and kp_type = "+kpType.getKpTypeConcept()+" and (vl_result < 1000 or vl_result='LDL'))a;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_SUPP_KP");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Pregnant Women on ART with Suppressed undocumented VL within last 12 Months");
        return cd;

    }
    /**
     * TX_PVLS NUMERATOR BY TEST TYPE
     * @param testType
     * @return
     */

    public CohortDefinition onARTSuppVLAgeSex(String testType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentSuppVLResultByTestType", ReportUtils.map(currentSuppVLResultByTestType(testType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTXAtleast3Months", ReportUtils.map(patientInTXAtleast3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND currentSuppVLResultByTestType AND patientInTXAtleast3Months");
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
                "on e.patient_id = f.patient_id and e.date_started = f.visit_date and f.key_population_type ="+kpType+";";
        cd.setName("kpByTypeAtARTInitiation");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("KPs by type at ART initiation");

        return cd;
    }
    /*Pregnant or Breastfeeding mother on ART with Routine VL within last 12 Months*/
    public CohortDefinition breastfeedingOnARTVLLast12Months(String testType) {

        String sqlQuery = "select a.patient_id as patient_id\n" +
                "from(select t.patient_id,vl.vl_date,vl.vl_result,vl.urgency,vl.order_reason from (\n" +
                "             select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "                    greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "                    greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "                    greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "                    d.patient_id as disc_patient,\n" +
                "                    d.effective_disc_date as effective_disc_date,\n" +
                "                    max(d.visit_date) as date_discontinued,\n" +
                "                    de.patient_id as started_on_drugs,\n" +
                "                    de.date_started\n" +
                "             from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                    join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                    left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:startDate)\n" +
                "                    left outer JOIN\n" +
                "                      (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                       where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                       group by patient_id\n" +
                "                      ) d on d.patient_id = fup.patient_id\n" +
                "             where fup.visit_date <= date(:endDate)\n" +
                "             group by patient_id\n" +
                "             having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "                 (\n" +
                "                     ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                       and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                     )\n" +
                "                 ) order by date_started desc\n" +
                "             ) t\n" +
                "               inner join (\n" +
                "                          select\n" +
                "                                 patient_id,encounter_id,\n" +
                "                                 max(visit_date) as vl_date,\n" +
                "                                 date_sub(:endDate , interval 12 MONTH),\n" +
                "                                 if(mid(max(concat(visit_date,lab_test)),11) = 856, mid(max(concat(visit_date,test_result)),11), if(mid(max(concat(visit_date,lab_test)),11)=1305 and mid(max(concat(visit_date,test_result)),11) = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                                 mid(max(concat(visit_date,urgency)),11) as urgency,\n" +
                "                                 mid(max(concat(visit_date,order_reason)),11) as order_reason\n" +
                "                          from kenyaemr_etl.etl_laboratory_extract\n" +
                "                          group by patient_id\n" +
                "                          having mid(max(concat(visit_date,lab_test)),11) in (1305,856) and max(visit_date) between\n" +
                "                              date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "                          )vl\n" +
                "                 on t.patient_id = vl.patient_id where urgency = "+testType+" and order_reason in (1434,159882))a;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_DENOMINATOR_BF_PG");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Breastfeeding Women on ART with VL within last 12 Months");
        return cd;
    }
    /*Breastfeeding Mother on ART with Undocumented VL within last 12 Months*/
    public CohortDefinition kpWithVLLast12Months(String testType,KPTypeDataDefinition kpType) {

        String sqlQuery = "select a.patient_id as patient_id\n" +
                "from(select t.patient_id,vl.vl_date,vl.vl_result,vl.urgency,t.kp_type from (\n" +
                "   select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "          greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "          greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "          greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "          d.patient_id as disc_patient,\n" +
                "          d.effective_disc_date as effective_disc_date,\n" +
                "          max(d.visit_date) as date_discontinued,\n" +
                "          de.patient_id as started_on_drugs,\n" +
                "          de.date_started,\n" +
                "          mid(max(concat(fup.visit_date,fup.key_population_type)),11) as kp_type\n" +
                "     from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "          join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "          join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "          left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:startDate)\n" +
                "          left outer JOIN\n" +
                "            (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "             where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "             group by patient_id\n" +
                "            ) d on d.patient_id = fup.patient_id\n" +
                "   where fup.visit_date <= date(:endDate)\n" +
                "   group by patient_id\n" +
                "   having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "       (\n" +
                "           ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "             and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "           )\n" +
                "       ) order by date_started desc\n" +
                "   ) t\n" +
                "     inner join (\n" +
                "                select\n" +
                "                       patient_id,encounter_id,\n" +
                "                       max(visit_date) as vl_date,\n" +
                "                       date_sub(:endDate , interval 12 MONTH),\n" +
                "                       if(mid(max(concat(visit_date,lab_test)),11) = 856, mid(max(concat(visit_date,test_result)),11), if(mid(max(concat(visit_date,lab_test)),11)=1305 and mid(max(concat(visit_date,test_result)),11) = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                       mid(max(concat(visit_date,urgency)),11) as urgency\n" +
                "                from kenyaemr_etl.etl_laboratory_extract\n" +
                "                group by patient_id\n" +
                "                having mid(max(concat(visit_date,lab_test)),11) in (1305,856) and max(visit_date) between\n" +
                "                    date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "                )vl\n" +
                "       on t.patient_id = vl.patient_id  where urgency = "+testType+" and kp_type = "+kpType.getKpTypeConcept()+")a;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_DENOMINATOR_BF_UNDOCUMENTED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Breastfeeding Women on ART with Undocumented VL within last 12 Months");
        return cd;
    }
    /*On ART with Routine VL within last 12 Months by sex/age*/
    public CohortDefinition onARTAndVLLast12MonthsbyAgeSex(String testType) {

        String sqlQuery = "select a.patient_id as patient_id\n" +
                "from(select t.patient_id,vl.vl_date,vl.vl_result,vl.urgency from (\n" +
                "    select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "           greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "           greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "           greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "           d.patient_id as disc_patient,\n" +
                "           d.effective_disc_date as effective_disc_date,\n" +
                "           max(d.visit_date) as date_discontinued,\n" +
                "           de.patient_id as started_on_drugs,\n" +
                "           de.date_started\n" +
                "    from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "           join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "           join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "           left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:startDate)\n" +
                "           left outer JOIN\n" +
                "             (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "              where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "              group by patient_id\n" +
                "             ) d on d.patient_id = fup.patient_id\n" +
                "    where fup.visit_date <= date(:endDate)\n" +
                "    group by patient_id\n" +
                "    having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "        (\n" +
                "            ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "              and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "            )\n" +
                "        ) order by date_started desc\n" +
                "    ) t\n" +
                "inner join (\n" +
                "           select\n" +
                "                  patient_id,encounter_id,\n" +
                "                  max(visit_date) as vl_date,\n" +
                "                  date_sub(:endDate , interval 12 MONTH),\n" +
                "                  if(mid(max(concat(visit_date,lab_test)),11) = 856, mid(max(concat(visit_date,test_result)),11), if(mid(max(concat(visit_date,lab_test)),11)=1305 and mid(max(concat(visit_date,test_result)),11) = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                  mid(max(concat(visit_date,urgency)),11) as urgency\n" +
                "           from kenyaemr_etl.etl_laboratory_extract\n" +
                "             group by patient_id\n" +
                "           having mid(max(concat(visit_date,lab_test)),11) in (1305,856) and max(visit_date) between\n" +
                "               date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "    )vl\n" +
                "on t.patient_id = vl.patient_id  where urgency = "+testType+")a;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_DENOMINATOR");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("On ART with Routine VL within last 12 Months by sex/age");
        return cd;
    }
    /**
     * Patients with VL results within the last 12 months
     * @return
     */
    public CohortDefinition currentVLResultLast12Months() {

        String sqlQuery = "select patient_id from kenyaemr_etl.etl_laboratory_extract where lab_test in (1305,856) and visit_date between\n" +
                "date_sub(:endDate,interval 12 MONTH) and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("withVLResultLast12Months");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("On ART with VL result within last 12 Months");
        return cd;
    }
    /**
     * Patients with VL results within the last 12 months by test type/indication
     * @return
     */
    public CohortDefinition currentVLResultLast12MonthsByTestType(String testType) {

        String sqlQuery = "select patient_id from kenyaemr_etl.etl_laboratory_extract where lab_test in (1305,856) and visit_date between\n" +
                "                date_sub(:endDate,interval 12 MONTH) and date(:endDate) and urgency = '"+testType+"';";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("currentVLResultLast12MonthsByTestType");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("On ART with VL result within last 12 Months by Test type");
        return cd;
    }

    /**Number of ART patients with a VL result documented in the medical or laboratory records/LIS within the past 12 months.
     * TX_PVLS DENOMINATOR
     * @return
     */
    public CohortDefinition txpvlsDenominator(String testType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTXAtleast3Months", ReportUtils.map(patientInTXAtleast3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentVLResultLast12MonthsByTestType", ReportUtils.map(currentVLResultLast12MonthsByTestType(testType), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND patientInTXAtleast3Months AND currentVLResultLast12MonthsByTestType");
        return cd;
    }
    /**Number of Pregnant ART patients with a VL result documented in the medical or laboratory records/LIS within the past 12 months.
     * TX_PVLS DENOMINATOR
     * @return
     */
    public CohortDefinition txpvlsDenominatorPregnant(String testType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTXAtleast3Months", ReportUtils.map(patientInTXAtleast3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentVLResultLast12MonthsByTestType", ReportUtils.map(currentVLResultLast12MonthsByTestType(testType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentVLTestPregnantOrderReason", ReportUtils.map(currentVLTestPregnantOrderReason(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND patientInTXAtleast3Months AND currentVLResultLast12MonthsByTestType AND currentVLTestPregnantOrderReason");
        return cd;
    }
    /**Number of Breastfeeding ART patients with a VL result documented in the medical or laboratory records/LIS within the past 12 months.
     * TX_PVLS DENOMINATOR
     * @return
     */
    public CohortDefinition txpvlsDenominatorBreastfeeding(String testType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTXAtleast3Months", ReportUtils.map(patientInTXAtleast3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentVLResultLast12MonthsByTestType", ReportUtils.map(currentVLResultLast12MonthsByTestType(testType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentVLTestBreastfeedingOrderReason", ReportUtils.map(currentVLTestBreastfeedingOrderReason(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND patientInTXAtleast3Months AND currentVLResultLast12MonthsByTestType AND currentVLTestBreastfeedingOrderReason");
        return cd;
    }
    /**
     * TX_PVLS NUMERATOR Indication by Key Population and test Type
     * @param testType
     * @param kpType
     * @return
     */

    public CohortDefinition onARTKpWithSuppVLLast12Months(String testType, Integer kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("keyPop",
                ReportUtils.map(kpByKPType(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentSuppVLResultByTestType", ReportUtils.map(currentSuppVLResultByTestType(testType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTXAtleast3Months", ReportUtils.map(patientInTXAtleast3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND keyPop AND currentSuppVLResultByTestType AND patientInTXAtleast3Months");
        return cd;
    }

    /**
     * TX_PVLS NUMERATOR Indication by Key Population and test Type
     * @param testType
     * @param kpType
     * @return
     */

    public CohortDefinition kpOnARTVLLast12Months(String testType, Integer kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("keyPop",ReportUtils.map(kpByKPType(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("currentVLResultLast12MonthsByTestType", ReportUtils.map(currentVLResultLast12MonthsByTestType(testType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTXAtleast3Months", ReportUtils.map(patientInTXAtleast3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txcurr AND keyPop AND currentVLResultLast12MonthsByTestType AND patientInTXAtleast3Months");
        return cd;
    }
    /**
     * Patients current on ART at the beginning of the reporting period . This is a component of TX_ML
     * @return
     */
    public CohortDefinition currentOnARTAtStartOfReportingPeriod() {

        String sqlQuery = "SELECT a.patient_id from (select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "max(fup.visit_date) as latest_vis_date,\n" +
                "mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "max(d.visit_date) as date_discontinued,\n" +
                "d.patient_id as disc_patient,\n" +
                "de.patient_id as started_on_drugs,\n" +
                "de.date_started\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "left outer JOIN\n" +
                "(select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "where visit_date <= date_sub(date(:endDate),INTERVAL 3 MONTH) and program_name='HIV'\n" +
                "group by patient_id\n" +
                ") d on d.patient_id = fup.patient_id\n" +
                "where fup.visit_date <= date(:endDate)\n" +
                "group by patient_id\n" +
                "having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date_sub(date(:endDate),INTERVAL 3 MONTH)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date_sub(date(:endDate), INTERVAL 3 MONTH)))\n" +
                "))a;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("currentOnARTAtStartOfReportingPeriod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients current on ART at the beginning of the reporting period");
        return cd;

    }

    /**
     * Patients with IIT by the end of the reporting period. A component of TX_ML
     * @return
     */
    public CohortDefinition iitAtEndOfReportingPeriod() {

        String sqlQuery = "select b.patient_id from (select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "max(fup.visit_date) as latest_vis_date,\n" +
                "mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "max(d.visit_date) as date_discontinued,\n" +
                "d.patient_id as disc_patient\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "left outer JOIN\n" +
                "(select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "where visit_date <= :endDate  and program_name='HIV'\n" +
                "group by patient_id\n" +
                ") d on d.patient_id = fup.patient_id\n" +
                "where fup.visit_date <= date(:endDate)\n" +
                "group by patient_id\n" +
                "having (\n" +
                "(((date(latest_tca) < :endDate) and (date(latest_vis_date) < date(latest_tca))) ) and ((date(latest_tca) > date(date_discontinued) and date(latest_vis_date) > date(date_discontinued)) or disc_patient is null ) and datediff(date(:endDate), date(latest_tca)) between 1 and 90))b;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("iitAtEndOfReportingPeriod");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients with IIT by the end of the reporting period");
        return cd;
    }

    /**
     * Number of New On ART patients who are not Current on At
     * A component of TxML
     * @return
     */
    public CohortDefinition newOnARTAndNotTxCur() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txcurr",
                ReportUtils.map(currentlyOnArt(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("newlyStartedArt", ReportUtils.map(startedOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("newlyStartedArt AND NOT txcurr");
        return cd;
    }

    /**
     * Number of ART patients with no clinical contact since their last expected contact
     * @return
     */
    public CohortDefinition currentOnARTAtStartIitAtEndOfReportingPeriod() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("currentOnARTAtStartOfReportingPeriod",ReportUtils.map(currentOnARTAtStartOfReportingPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("iitAtEndOfReportingPeriod", ReportUtils.map(iitAtEndOfReportingPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("currentOnARTAtStartOfReportingPeriod AND iitAtEndOfReportingPeriod");
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
        cd.addSearch("currentOnARTAtStartIitAtEndOfReportingPeriod",ReportUtils.map(currentOnARTAtStartIitAtEndOfReportingPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("newOnARTAndNotTxCur", ReportUtils.map(newOnARTAndNotTxCur(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("transferredOutAndVerified", ReportUtils.map(transferredOutAndVerified(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("currentOnARTAtStartIitAtEndOfReportingPeriod OR newOnARTAndNotTxCur OR transferredOutAndVerified");
        return cd;
    }
    /**
     * TX_ML patients by Treatment stop reason
     * @return
     */

    public CohortDefinition txmlPatientByTXStopReason(Integer reason) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("patientByTXStopReason",
                ReportUtils.map(patientByTXStopReason(reason), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txML", ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("patientByTXStopReason AND txML");
        return cd;
    }
    /**
     * TX_ML IIT KP patients in Tx for less than 3 months
     * @return
     */
    public CohortDefinition txMLIITKpUnder3MonthsInTx(Integer iit,Integer kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("kp", ReportUtils.map(kpByKPType(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txML", ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTXLessThan3Months", ReportUtils.map(patientInTXLessThan3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientByTXStopReason",ReportUtils.map(patientByTXStopReason(iit), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txML AND kp AND patientInTXLessThan3Months AND patientByTXStopReason");
        return cd;
    }
    /**
     * TX_ML IIT KP patients in Tx fro 3-5 months
     * @return
     */
    public CohortDefinition txMLIITKp3To5MonthsInTx(Integer iit,Integer kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("kp", ReportUtils.map(kpByKPType(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txML", ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTX3To5Months", ReportUtils.map(patientInTX3To5Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientByTXStopReason",ReportUtils.map(patientByTXStopReason(iit), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txML AND kp AND patientInTX3To5Months AND patientByTXStopReason");
        return cd;
    }
    /**
     * TX_ML IIT KP patients in Tx for 6+ months
     * @return
     */
    public CohortDefinition txMLIITKpAtleast6Months(Integer iit,Integer kpType) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("kp", ReportUtils.map(kpByKPType(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txML", ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTXAtleast6Months", ReportUtils.map(patientInTXAtleast6Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientByTXStopReason",ReportUtils.map(patientByTXStopReason(iit), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txML AND kp AND patientInTXAtleast6Months AND patientByTXStopReason");
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
        cd.addSearch("patientByDeathReason",ReportUtils.map(patientByDeathReason(causeOfDeath), "startDate=${startDate},endDate=${endDate}"));
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
    public CohortDefinition txmlKPStopReason(Integer kpType, Integer stopReason) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("kp",ReportUtils.map(kpByKPType(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txml",ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientByTXStopReason", ReportUtils.map(patientByTXStopReason(stopReason), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txml AND kp AND patientByTXStopReason");
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
        cd.addSearch("kp",ReportUtils.map(kpByKPType(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("txmlPatientDied", ReportUtils.map(txmlPatientDied(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txmlPatientDied AND kp");
        return cd;
    }

    /**
     *IIT After being on Treatment for <3 months
     * @param iit
     * @return
     */
    public CohortDefinition txMLIITUnder3MonthsInTx(Integer iit) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txML", ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientByTXStopReason", ReportUtils.map(patientByTXStopReason(iit), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTXLessThan3Months", ReportUtils.map(patientInTXLessThan3Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txML AND patientByTXStopReason AND patientInTXLessThan3Months");
        return cd;
    }
    /**
     *IIT After being on Treatment for 3-5 months
     * @param iit
     * @return
     */
    public CohortDefinition txMLIIT3To5MonthsInTx(Integer iit) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txML", ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientByTXStopReason", ReportUtils.map(patientByTXStopReason(iit), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTX3To5Months", ReportUtils.map(patientInTX3To5Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txML AND patientByTXStopReason AND patientInTX3To5Months");
        return cd;
    }
    /**
     *IIT After being on Treatment for 6+ months
     * @param iit
     * @return
     */
    public CohortDefinition txMLIITAtleast6MonthsInTx(Integer iit) {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("txML", ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientByTXStopReason", ReportUtils.map(patientByTXStopReason(iit), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientInTXAtleast6Months", ReportUtils.map(patientInTXAtleast6Months(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("txML AND patientByTXStopReason AND patientInTXAtleast6Months");
        return cd;
    }

    /**
     * Patients by treatment stop reason
     * @param reason
     * @return
     */
    public CohortDefinition patientByTXStopReason(Integer reason) {

        String sqlQuery = "select dt.patient_id from kenyaemr_etl.etl_ccc_defaulter_tracing dt where dt.is_final_trace =1267 and dt.true_status ="+reason+" and date(dt.visit_date) between date(:startDate) and date(:endDate);" ;
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("treatmentStopReason");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients by treatment stop reason");
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

        String sqlQuery = "select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d where d.program_name = 'HIV' and date(d.visit_date) between date(:startDate) and date(:endDate) and d.discontinuation_reason = 160034 and d.death_reason ="+deathReason+";";
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

        String sqlQuery = "select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d where d.program_name = 'HIV' and date(d.visit_date) between date(:startDate) and date(:endDate) and d.specific_death_cause= "+specificCauseOfDeath+";";
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
        cd.addSearch("txml",ReportUtils.map(txML(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("patientsDiscontinuedOfDeath",ReportUtils.map(patientsDiscontinuedOfDeath(), "startDate=${startDate},endDate=${endDate}"));
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

    //HTS_INDEX_POSITIVE Number of individuals who were tested Positive using Index testing services
    public CohortDefinition hivPositiveContact() {

        String sqlQuery = "select c.id from kenyaemr_hiv_testing_patient_contact c inner join kenyaemr_etl.etl_hts_test t on c.patient_id = t.patient_id\n" +
                "                         where (c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)) and c.voided = 0 and t.voided =0\n" +
                "                         group by c.id\n" +
                "                           having mid(max(concat(t.visit_date,t.final_test_result)),11) = 'Positive' and mid(max(concat(t.visit_date,t.test_strategy)),11) = 161557\n" +
                "  and max(t.visit_date) between date_sub( date(:endDate), INTERVAL  3 MONTH )and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_POSITIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of individuals who were tested Positive using Index testing services");
        return cd;

    }

    //Number of individuals who were tested HIV Negative using Index testing services
    public CohortDefinition hivNegativeContact() {

        String sqlQuery = "select c.id from kenyaemr_hiv_testing_patient_contact c inner join kenyaemr_etl.etl_hts_test t on c.patient_id = t.patient_id\n" +
                "                         where (c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)) and c.voided = 0 and t.voided =0\n" +
                "                         group by c.id\n" +
                "                           having mid(max(concat(t.visit_date,t.final_test_result)),11) = 'Negative' and mid(max(concat(t.visit_date,t.test_strategy)),11) = 161557\n" +
                "  and max(t.visit_date) between date_sub( date(:endDate), INTERVAL  3 MONTH )and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_NEGATIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of individuals who were tested HIV Negative using Index testing services");
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
     *Proportion of  clients who have a hiv test documented in the EMR
     * Composition component - documented tested
     * @return
     */
    public CohortDefinition clientsWithDocumentedTest() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts\n" +
                "where hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("DOCUMENTED_TESTED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients who have a hiv test documented in the EMR");
        return cd;

    }
    /**
     *Proportion of Children 1-14 yrs with reported Negative status and without any documented status in EMR
     * HTS_INDEX_DOCUMENTED_NEGATIVE Datim indicator
     * Composition startedOnART + prevOnIPTandCompleted
     * @return
     */
    public CohortDefinition  contactsReportedNegativeUndocumented() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("contactsReportedNegative", ReportUtils.map(contactsReportedNegative(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("contactsReportedNegative");
        return cd;
    }

    //Known HIV Positive contacts
    public CohortDefinition knownPositiveContact() {

        String sqlQuery = "select c.id from kenyaemr_hiv_testing_patient_contact c left join\n" +
                "                     (select t.patient_id, max(t.visit_date) as hts_date, mid(max(concat(t.visit_date,t.final_test_result)),11) as test_result, mid(max(concat(t.visit_date,t.test_strategy)),11) as test_strategy\n" +
                "              from kenyaemr_etl.etl_hts_test t group by t.patient_id having hts_date < date_sub(date(:endDate), INTERVAL 3 MONTH))t on c.patient_id = t.patient_id\n" +
                "              left join (select tr.client_id, mid(max(concat(date(tr.date_created),tr.status)),11) as trace_status from kenyaemr_hiv_testing_client_trace tr where date(tr.date_created) between date_sub(date(:endDate), INTERVAL 3 MONTH)\n" +
                "                    and date(:endDate) group by tr.client_id)tr on c.id = tr.client_id\n" +
                "              where (c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)) and c.voided = 0 and ((t.test_result = 'Positive' and test_strategy = 161557) or tr.trace_status = 'Contacted and Linked' or c.baseline_hiv_status ='Positive')\n" +
                "              group by c.id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_KNOWN_POSITIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of Contacts tested through Index Services");
        return cd;

    }

    /*HTS_RECENT Persons aged ≥15 years newly diagnosed with HIV-1 infection who have a test for recent infection */
    public CohortDefinition recentHIVInfections() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts\n" +
                "inner join kenyaemr_etl.etl_patient_demographics d\n" +
                "on d.patient_id = hts.patient_id and YEAR(:startDate)-YEAR(d.DOB)>= 15\n" +
                "where hts.final_test_result = \"Positive\" group by hts.patient_id having mid(min(concat(hts.visit_date,hts.patient_id)),1,10) > :startDate;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_RECENT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Persons ≥15 years newly diagnosed with HIV-1 infection");
        return cd;

    }

    /*Number Tested Negative PMTCT services ANC-1 only*/
    public CohortDefinition negativePMTCTANC1() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedNegativePmtctANC1",ReportUtils.map(testedNegativePmtctANC1(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedNegativePmtctANC1 AND NOT (testedIndexTesting OR testedSocialNetworks)");
        return cd;

    }

    /*Number Tested Positive PMTCT services ANC-1 only*/
    public CohortDefinition positivePMTCTANC1() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedPositivePmtctANC1",ReportUtils.map(testedPositivePmtctANC1(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedPositivePmtctANC1 AND NOT (testedIndexTesting OR testedSocialNetworks)");
        return cd;

    }

    /*Number Tested Negative PMTCT services Post ANC-1 (including labour and delivery and BF)*/
    public CohortDefinition negativePMTCTPostANC1() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedNegativePmtctPostANC1",ReportUtils.map(testedNegativePmtctPostANC1(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedNegativePmtctPostANC1 AND NOT (testedIndexTesting OR testedSocialNetworks)");
        return cd;

    }

    /*Number Tested Positive PMTCT services Post ANC-1 (including labour and delivery and BF)*/
    public CohortDefinition positivePMTCTPostANC1() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("testedPositivePmtctPostANC1",ReportUtils.map(testedPositivePmtctPostANC1(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedIndexTesting",ReportUtils.map(testedIndexTesting(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("testedSocialNetworks",ReportUtils.map(testedSocialNetworks(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("testedPositivePmtctPostANC1 AND NOT (testedIndexTesting OR testedSocialNetworks)");
        return cd;

    }
    /**
     * Get KPs by type from the last HIV follow-up visit
     * @param kpType
     * @return
     */
    public CohortDefinition kpByKPType(Integer kpType) {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select kp.patient_id from (select fup.patient_id,mid(max(concat(fup.visit_date,fup.key_population_type)),11) as kp_type " +
                "from kenyaemr_etl.etl_patient_hiv_followup fup where fup.visit_date <= date(:endDate) group by fup.patient_id\n" +
                "having kp_type = "+kpType+")kp;";
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
     * TX_PVLS NUMERATOR BY KP TEST AND KP TYPE
     * @param kpType
     * @return
     */
    /**
     *TX_NEW Patients started ART within the reporting period
     * @return
     */
    public CohortDefinition txNew() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("newlyStartedArt", ReportUtils.map(startedOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("newlyStartedArt");
        return cd;
    }
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
        String sqlQuery="select f.patient_id from (select f.patient_id,timestampdiff(MONTH,max(f.visit_date),mid(max(concat(f.visit_date,f.next_appointment_date)),11))  months_tca from kenyaemr_etl.etl_patient_hiv_followup f\n" +
                "where f.visit_date <=date(:endDate) and f.next_appointment_date is not null group by f.patient_id having months_tca < 3)f;";
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
        String sqlQuery="select f.patient_id from (select f.patient_id,timestampdiff(MONTH,max(f.visit_date),mid(max(concat(f.visit_date,f.next_appointment_date)),11))  months_tca from kenyaemr_etl.etl_patient_hiv_followup f\n" +
                "where f.visit_date <=date(:endDate) and f.next_appointment_date is not null group by f.patient_id having months_tca between 3 and 5)f;";
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
        String sqlQuery="select f.patient_id from (select f.patient_id,timestampdiff(MONTH,max(f.visit_date),mid(max(concat(f.visit_date,f.next_appointment_date)),11))  months_tca from kenyaemr_etl.etl_patient_hiv_followup f\n" +
                "where f.visit_date <=date(:endDate) and f.next_appointment_date is not null group by f.patient_id having months_tca >= 6)f;";
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
     *//**
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

        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_prep_enrolment e where e.visit_date <= date(:endDate) group by e.patient_id having mid(max(concat(e.visit_date,e.kp_type)),11) = "+kpType+";";
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

    /**
     *Proportion of ART patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy
     * TB_PREV_COM Datim indicator
     * @return
     */
    public CohortDefinition previouslyOnIPTandCompleted() {

        String sqlQuery = "\n" +
                "select i.patient_id from\n" +
                "(select i.patient_id, max(i.visit_date) as initiation_date,max(o.visit_date),o.outcome\n" +
                "     from kenyaemr_etl.etl_ipt_initiation i join kenyaemr_etl.etl_ipt_outcome o\n" +
                "         on o.patient_id =i.patient_id and o.outcome = 1267\n" +
                "     group by i.patient_id\n" +
                "            having max(i.visit_date) between date_sub(:startDate , interval 6 MONTH) and date_sub(:endDate, interval 6 MONTH)\n" +
                "       and max(o.visit_date) between date(:startDate) and date(:endDate)) i\n" +
                "  join(\n" +
                "select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "       max(fup.visit_date) as latest_vis_date,\n" +
                "       mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "       max(d.visit_date) as date_discontinued,\n" +
                "       d.patient_id as disc_patient,\n" +
                "       de.patient_id as started_on_drugs\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "       join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "       join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "       left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "       left outer JOIN\n" +
                "         (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "          where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "          group by patient_id\n" +
                "         ) d on d.patient_id = fup.patient_id\n" +
                "group by patient_id\n" +
                "having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "    ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) ))\n" +
                "    )\n" +
                ") t\n" +
                "on t.patient_id = i.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TB_PREV_ENROLLED_COMPLETED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("previously enrolled on IPT and have completed");
        return cd;

    }
    /**
     *Proportion of patients who Initiated TPT within 6 months of starting ART
     * @return
     */
    public CohortDefinition initiatedTPTWithin6MonthsStartingART() {

        String sqlQuery = "select t.patient_id from\n" +
                "  (select p.patient_id,p.initiation_date as initiation_date,de.arv_start_date as arv_start_date\n" +
                "   from (select p.patient_id, max(p.date_enrolled) as initiation_date\n" +
                "         from kenyaemr_etl.etl_patient_program p where  p.program ='TPT' and p.date_enrolled <= date(:endDate)\n" +
                "         group by p.patient_id)p\n" +
                "    inner join (select de.patient_id,min(de.date_started) as arv_start_date from kenyaemr_etl.etl_drug_event de\n" +
                "     where de.program ='HIV' and de.date_started <=date(:endDate) group by de.patient_id)de on p.patient_id = de.patient_id\n" +
                "  group by patient_id\n" +
                "  having date(initiation_date) >= date(arv_start_date) and timestampdiff(MONTH,date(arv_start_date),date(initiation_date)) < 6)t;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TB_PREV_NEWLY_ENROLLED_ART_INITIATED_TPT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Initiated TPT within 6 months of starting ART");
        return cd;

    }

    /**
     *Proportion of patients who Initiated TPT Afetr 6 months of starting ART
     *  @return
     */
    public CohortDefinition initiatedTPTAfter6MonthsStartingART() {

        String sqlQuery = "select t.patient_id from\n" +
                "  (select p.patient_id,p.initiation_date as initiation_date,de.arv_start_date as arv_start_date\n" +
                "   from (select p.patient_id, max(p.date_enrolled) as initiation_date\n" +
                "         from kenyaemr_etl.etl_patient_program p where  p.program ='TPT' and p.date_enrolled <= date(:endDate) group by p.patient_id)p\n" +
                "    inner join (select de.patient_id,min(de.date_started) as arv_start_date\n" +
                "                from kenyaemr_etl.etl_drug_event de where de.program ='HIV' and de.date_started <=date(:endDate)\n" +
                "                group by de.patient_id)de on p.patient_id = de.patient_id\n" +
                "  group by patient_id\n" +
                "  having date(initiation_date) >= date(arv_start_date) and timestampdiff(MONTH,date(arv_start_date),date(initiation_date)) >= 6)t;";

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
    public CohortDefinition  newOnARTprevOnIPTandInitiated() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("initiatedTPTWithin6MonthsStartingART", ReportUtils.map(initiatedTPTWithin6MonthsStartingART(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("initiatedTPTWithin6MonthsStartingART");
        return cd;
    }
    /**
     *Proportion of PREVIOUS ON ART patients who started on a standard course of TB Preventive Treatment (TPT)
     * Afetr 6 months of starting ART
     * TB_PREV_ENROLLED_ART_INITIATED_TPT Datim indicator
     * Composition startedOnART + initiatedTPTAfter6MonthsStartingART
     * @return
     */
    public CohortDefinition  previousOnARTandIPTandInitiated() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("initiatedTPTAfter6MonthsStartingART", ReportUtils.map(initiatedTPTAfter6MonthsStartingART(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("initiatedTPTAfter6MonthsStartingART");
        return cd;
    }
    /**
     *Proportion of  patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy
     * Composition
     * @return
     */
    public CohortDefinition prevOnIPTandCompleted() {

        String sqlQuery = "select i.patient_id from\n" +
                "            (select i.patient_id, max(i.date_enrolled) as initiation_date,max(o.visit_date),o.outcome\n" +
                "               from kenyaemr_etl.etl_patient_program i join kenyaemr_etl.etl_ipt_outcome o\n" +
                "                  on o.patient_id =i.patient_id and o.outcome = 1267\n" +
                "                   group by i.patient_id\n" +
                "                     having initiation_date between date_sub(:startDate , interval 6 MONTH) and date_sub(:endDate, interval 6 MONTH)\n" +
                "                          and max(o.visit_date) between date(:startDate) and date(:endDate)) i;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TB_PREV_NEWLY_ENROLLED_COMPLETED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Newly on ART previously enrolled on IPT and have completed");
        return cd;

    }

    /**
     *Proportion of NEW ON ART patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy
     * TB_PREV_NEWLY_ENROLLED_ART_COMPLETED_TPT Datim indicator
     * Composition startedOnART + prevOnIPTandCompleted
     * @return
     */
    public CohortDefinition  newOnARTprevOnIPTandCompleted() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("prevOnIPTandCompleted", ReportUtils.map(prevOnIPTandCompleted(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("prevOnIPTandCompleted");
        return cd;
    }
    /**
     *Proportion of PREVIOUS ON ART patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy
     * TB_PREV_ENROLLED_ART_COMPLETED_TPT Datim indicator
     * Composition startedOnART + prevOnIPTandCompleted
     * @return
     */
    public CohortDefinition  previousOnARTandIPTandCompleted() {
        CompositionCohortDefinition cd = new CompositionCohortDefinition();
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addSearch("prevOnIPTandCompleted", ReportUtils.map(prevOnIPTandCompleted(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("prevOnIPTandCompleted");
        return cd;
    }
    /**
     *Number of beneficiaries served by PEPFAR OVC Comprehensive programs for children and families affected by HIV
     * DATIM_OVC_SERV Datim indicator
     */
    public CohortDefinition totalBeneficiaryOfOVCComprehensiveProgram(){
        String sqlQuery ="select e.patient_id from kenyaemr_etl.etl_ovc_enrolment e where e.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and e.ovc_comprehensive_program = 'Yes';";
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
    public CohortDefinition totalBeneficiaryOfOVCDreamsProgram(){
        String sqlQuery ="select e.patient_id from kenyaemr_etl.etl_ovc_enrolment e where e.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and e.dreams_program = 'Yes';";
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
    public CohortDefinition totalBeneficiaryOfOVCPreventiveProgram(){
        String sqlQuery ="select e.patient_id from kenyaemr_etl.etl_ovc_enrolment e where e.visit_date between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate) and e.ovc_preventive_program = 'Yes';";
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
                "                                             when 2 then replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR))) when 3 then replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))\n" +
                "                                             else date(:startDate) end) and date(:endDate))\n" +
                "or (p.first_peer_enc between (CASE MONTH(date(:startDate)) when 5 then replace(date(:startDate), MONTH(date(:startDate)),4) when 6 then replace(date(:startDate), MONTH(date(:startDate)),4)\n" +
                "                                            when 7 then replace(date(:startDate), MONTH(date(:startDate)),4) when 8 then replace(date(:startDate), MONTH(date(:startDate)),4) when 9 then replace(date(:startDate), MONTH(date(:startDate)),4) when 11 then replace(date(:startDate), MONTH(date(:startDate)),10) when 12 then replace(date(:startDate), MONTH(date(:startDate)),10) when 1 then (replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR))))\n" +
                "                                            when 2 then replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR))) when 3 then replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))\n" +
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
                "    when 11 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),10) when 12 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),10) when 1 then date_sub((replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))), INTERVAL 6 MONTH)\n" +
                "    when 2 then date_sub((replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))), INTERVAL 6 MONTH) when 3 then date_sub((replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))), INTERVAL 6 MONTH)\n" +
                "    else date_sub(date(:startDate), INTERVAL 6 MONTH) end) and date_sub(date(:endDate), INTERVAL 6 MONTH))\n" +
                "         or (p.first_peer_enc between (CASE MONTH(date(:startDate)) when 5 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4) when 6 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4)\n" +
                "    when 7 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4) when 8 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4) when 9 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),4)\n" +
                "    when 11 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),10) when 12 then replace(date(:startDate), MONTH(date_sub(date(:startDate), INTERVAL 6 MONTH)),10) when 1 then date_sub((replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))), INTERVAL 6 MONTH)\n" +
                "    when 2 then date_sub((replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))), INTERVAL 6 MONTH) when 3 then date_sub((replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))), INTERVAL 6 MONTH)\n" +
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
        String sqlQuery = "select v.client_id from kenyaemr_etl.etl_clinical_visit v where timestampdiff(MONTH,v.visit_date,date(:endDate)) <3 and v.counselled_for_hiv = 'Yes' and v.hiv_tested in ('Yes','Declined','Referred for testing');";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("kpPrevOfferedHTSServices");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("KP offered HTS");
        return cd;
    }
    /**
     * KPs in KP program who were newly tested/referred for HTS within the last 3 months
     * @return
     */
    public CohortDefinition kpPrevNewlyTestedOrReferredSql() {
        String sqlQuery = "select a.client_id from (select v.client_id as client_id from (select v.client_id from kenyaemr_etl.etl_clinical_visit v where timestampdiff(MONTH,v.visit_date,date(:endDate)) <3 and v.hiv_tested in ('Yes','Referred for testing'))v\n" +
                "left join\n" +
                "    (select t.patient_id from kenyaemr_etl.etl_hts_test t where timestampdiff(MONTH,t.visit_date,date(:endDate)) <3)t on v.client_id = t.patient_id)a group by a.client_id;";
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
                "       where (v.condom_use_education = 'Yes' or v.post_abortal_care = 'Yes' or v.female_condoms_no > 0 or v.male_condoms_no > 0 or v.lubes_no > 0 or v.sti_screened = 'Y' or v.sti_treated ='Yes' or v.sti_referred = 'Yes'\n" +
                "    or v.linked_to_art='Yes' or v.tb_screened='Y' or v.tb_treated = 'Y' or v.tb_referred = 'Yes'\n" +
                "          or (v.hepatitisB_screened = 'Y' and v.hepatitisB_treated = 'Vaccinated') or v.hepatitisB_referred ='Yes'\n" +
                "          or (v.hepatitisC_screened = 'Y' and v.hepatitisC_treated = 'Vaccinated') or v.hepatitisC_referred ='Yes'\n" +
                "          or (v.fp_screened = 'Yes' and v.fp_eligibility = 'Eligible' and v.fp_treated in ('Y','on-going'))) and\n" +
                "             v.visit_date between (CASE MONTH(date(:startDate)) when 5 then replace(date(:startDate), MONTH(date(:startDate)),4) when 6 then replace(date(:startDate), MONTH(date(:startDate)),4)\n" +
                "    when 7 then replace(date(:startDate), MONTH(date(:startDate)),4) when 8 then replace(date(:startDate), MONTH(date(:startDate)),4) when 9 then replace(date(:startDate), MONTH(date(:startDate)),4) when 11 then replace(date(:startDate), MONTH(date(:startDate)),10)\n" +
                "    when 12 then replace(date(:startDate), MONTH(date(:startDate)),10) when 1 then (replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR))))\n" +
                "    when 2 then replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR))) when 3 then replace('"+startOfYear+"', '0000',YEAR(date_sub(date(:startDate), INTERVAL 1 YEAR)))\n" +
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
        String sqlQuery = "select c.client_id from kenyaemr_etl.etl_contact c where c.visit_date <= date(:endDate) group by c.client_id having mid(max(concat(c.visit_date,c.key_population_type)),11) = '"+kpType+"';";
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
        cd.addSearch("kpPrevCurrentPeriod",ReportUtils.map(kpPrevCurrentPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevPreviousPeriod",ReportUtils.map(kpPrevPreviousPeriod(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpProgramByKpType",ReportUtils.map(kpProgramByKpType(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevReceivedService",ReportUtils.map(kpPrevReceivedService(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevOfferedHTSServices",ReportUtils.map(kpPrevOfferedHTSServices(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevKnownPositiveSql",ReportUtils.map(kpPrevKnownPositiveSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(kpPrevCurrentPeriod AND NOT kpPrevPreviousPeriod) AND kpProgramByKpType AND ((kpPrevReceivedService AND kpPrevOfferedHTSServices) OR (kpPrevReceivedService AND kpPrevKnownPositiveSql))");
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
        cd.addSearch("kpPrevKnownPositiveSql",ReportUtils.map(kpPrevKnownPositiveSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrev",ReportUtils.map(kpPrev(kpType), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("kpPrev",ReportUtils.map(kpPrev(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevNewlyTestedOrReferred",ReportUtils.map(kpPrevNewlyTestedOrReferredSql(), "startDate=${startDate},endDate=${endDate}"));
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
        cd.addSearch("kpPrev",ReportUtils.map(kpPrev(kpType), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("kpPrevDeclinedTesting",ReportUtils.map(kpPrevDeclinedTestingSql(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("(kpPrev AND kpPrevDeclinedTesting");
        return cd;
    }
    /**
     *Physical and/or emotional violence (other Post-GBV) care
     * GEND_GBV_SEXUAL_VIOLENCE Disaggreagtion
     */
    public CohortDefinition sexualGBV(){
        String sqlQuery ="select s.patient_id from kenyaemr_etl.etl_gbv_screening s join\n" +
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
    public CohortDefinition physicalEmotionalGBV(){
        String sqlQuery ="select s.patient_id from kenyaemr_etl.etl_gbv_screening s join\n" +
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
    public CohortDefinition receivedPEP(){
        String sqlQuery ="select s.patient_id from kenyaemr_etl.etl_gbv_screening s join\n" +
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
    public CohortDefinition transferredOutAndVerified(){
        String sqlQuery ="select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d\n" +
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

}


