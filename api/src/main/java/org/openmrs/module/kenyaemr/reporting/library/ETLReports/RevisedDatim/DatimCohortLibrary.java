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
import org.openmrs.module.kenyacore.report.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.kenyaemr.calculation.library.ovc.OnOVCProgramCalculation;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.KPTypeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.DurationToNextAppointmentDataDefinition;
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

    final String TRANSGENDER_SW = "\"Transgender\" and c.year_started_sex_work is not null";

    final String TRANSGENDER_NOT_SW = "\"Transgender\" and c.year_started_sex_work is null";

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
                "                left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_name='HIV' \n" +
                "                left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id  \n" +
                "                left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id  \n" +
                "                where date(e.date_started) between date(:startDate) and :endDate\n" +
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
                "                left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_name='HIV' \n" +
                "                left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id  \n" +
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

        String sqlQuery = "select n.patient_id from (select e.patient_id,e.latest_enrollment_date,t.test_date,e.1st_anc_visit from\n" +
                "(select e.patient_id, max(e.visit_date) as latest_enrollment_date,mid(max(concat(e.visit_date,e.first_anc_visit_date)),11) as 1st_anc_visit from kenyaemr_etl.etl_mch_enrollment e\n" +
                "group by e.patient_id) e\n" +
                "inner join\n" +
                "(select av.patient_id,av.visit_date as visit_date, av.final_test_result as hiv_status from kenyaemr_etl.etl_mch_antenatal_visit av where av.anc_visit_number = 1\n" +
                "  and av.visit_date between date_sub(:endDate, interval 3 MONTH) and date(:endDate)\n" +
                "group by av.patient_id) av on e.patient_id = av.patient_id\n" +
                "left join (select t.visit_date as test_date,t.patient_id, t.final_test_result as test_result from kenyaemr_etl.etl_hts_test t\n" +
                "where t.visit_date between date_sub(:endDate, interval 3 MONTH) and date(:endDate)\n" +
                "and t.hts_entry_point ='PMTCT ANC'\n" +
                "and t.final_test_result = 'Positive') t on e.patient_id = t.patient_id\n" +
                "where ((t.test_date = av.visit_date and t.test_result = 'Positive') or (av.hiv_status = 'Positive'))\n" +
                "group by e.patient_id\n" +
                "having latest_enrollment_date between date_sub(:endDate, interval 3 MONTH) and date(:endDate)\n" +
                "and ( 1st_anc_visit is null or 1st_anc_visit = latest_enrollment_date)\n" +
                ")n;";
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

        String sqlQuery = "select n.patient_id from (select e.patient_id,e.latest_enrollment_date,t.test_date,e.1st_anc_visit from\n" +
                "(select e.patient_id, max(e.visit_date) as latest_enrollment_date,mid(max(concat(e.visit_date,e.first_anc_visit_date)),11) as 1st_anc_visit from kenyaemr_etl.etl_mch_enrollment e\n" +
                "group by e.patient_id) e\n" +
                "inner join\n" +
                "(select av.patient_id,av.visit_date as visit_date, av.final_test_result as hiv_status from kenyaemr_etl.etl_mch_antenatal_visit av where av.anc_visit_number = 1\n" +
                "        and av.visit_date between date_sub(:endDate, interval 3 MONTH) and date(:endDate)\n" +
                "group by av.patient_id) av on e.patient_id = av.patient_id\n" +
                "left join (select t.visit_date as test_date,t.patient_id, t.final_test_result as test_result from kenyaemr_etl.etl_hts_test t\n" +
                "    where t.visit_date between date_sub(:endDate, interval 3 MONTH) and date(:endDate)\n" +
                "    and t.hts_entry_point ='PMTCT ANC'\n" +
                "    and t.final_test_result = 'Negative') t on e.patient_id = t.patient_id\n" +
                "where ((t.test_date = av.visit_date and t.test_result = 'Negative') or (av.hiv_status = 'Negative'))\n" +
                "group by e.patient_id\n" +
                "having latest_enrollment_date between date_sub(:endDate, interval 3 MONTH) and date(:endDate)\n" +
                "and ( 1st_anc_visit is null or 1st_anc_visit = latest_enrollment_date)\n" +
                " )n;";
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

        String sqlQuery = "select n.patient_id from (select e.patient_id,e.latest_enrollment_date,t.test_date,e.1st_anc_visit,av.visit_date from\n" +
                "(select e.patient_id, max(e.visit_date) as latest_enrollment_date,mid(max(concat(e.visit_date,e.first_anc_visit_date)),11) as 1st_anc_visit,\n" +
                " mid(max(concat(e.visit_date,e.hiv_status)),11) as HIV_status from kenyaemr_etl.etl_mch_enrollment e\n" +
                "group by e.patient_id having HIV_status = 703) e\n" +
                "inner join\n" +
                "(select av.patient_id,av.visit_date as visit_date, av.final_test_result as hiv_status from kenyaemr_etl.etl_mch_antenatal_visit av where av.anc_visit_number = 1\n" +
                "        and av.visit_date between date_sub(:endDate, interval 3 MONTH) and date(:endDate)\n" +
                "group by av.patient_id) av on e.patient_id = av.patient_id\n" +
                "left join (select max(t.visit_date) as test_date,t.patient_id, mid(max(concat(t.visit_date,t.final_test_result)),11) as test_result from kenyaemr_etl.etl_hts_test t\n" +
                "   group by t.patient_id\n" +
                "     having test_result = 'Positive') t on e.patient_id = t.patient_id\n" +
                "where ((t.test_date < av.visit_date) or (e.HIV_status = 703))\n" +
                "group by e.patient_id\n" +
                "having coalesce(1st_anc_visit,visit_date) between date_sub(:endDate, interval 3 MONTH) and date(:endDate)\n" +
                ")n;";

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
    //TODO add mch_enrollment ==>max(mch_enrollment)
    //TODO subquery to get last enrollment
    public CohortDefinition newANCClients() {

        String sqlQuery = "select d.patient_id from\n" +
                "  (select e.patient_id, max(e.visit_date) as latest_enrollment_date,av.visit_date as 1st_anc_visit from kenyaemr_etl.etl_mch_enrollment e\n" +
                "inner join\n" +
                " (select av.patient_id,av.visit_date as visit_date from kenyaemr_etl.etl_mch_antenatal_visit av where av.anc_visit_number = 1\n" +
                " and av.visit_date between date_sub(:endDate, interval 3 MONTH) and date(:endDate)\n" +
                " group by av.patient_id) av on e.patient_id = av.patient_id\n" +
                "group by e.patient_id\n" +
                "having 1st_anc_visit between date_sub(:endDate, interval 3 MONTH) and date(:endDate))d;";
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

    public CohortDefinition firstTimescreenedCXCANegative() {

        String sqlQuery = "select t.patient_id from (select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                          max(fup.visit_date) as latest_vis_date,\n" +
                "                          mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                          max(d.visit_date) as date_discontinued,\n" +
                "                          d.patient_id as disc_patient,\n" +
                "                          de.patient_id as started_on_drugs\n" +
                "                   from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                          join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                          join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                          left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                          left outer JOIN\n" +
                "                            (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                             where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                             group by patient_id\n" +
                "                            ) d on d.patient_id = fup.patient_id\n" +
                "                   group by patient_id\n" +
                "                   having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                " ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) )) )\n" +
                " ) t\n" +
                "inner join\n" +
                "  (select s.patient_id,s.screening_type as screening_type from kenyaemr_etl.etl_cervical_cancer_screening s\n" +
                "   where s.screening_result ='Negative' and date(s.visit_date) between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate)\n" +
                "    group by s.patient_id\n" +
                "    having count(s.patient_id) = 1 or screening_type = \"First time screening\")sc\n" +
                "   on t.patient_id = sc.patient_id group by t.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("firstTimescreenedCXCANegative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive women on ART screened Negative for cervical cancer 1st time");
        return cd;

    }

    public CohortDefinition firstTimescreenedCXCAPositive() {

        String sqlQuery = "select t.patient_id from (select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                         max(fup.visit_date) as latest_vis_date,\n" +
                "                         mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                         max(d.visit_date) as date_discontinued,\n" +
                "                         d.patient_id as disc_patient,\n" +
                "                         de.patient_id as started_on_drugs\n" +
                "                  from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                         join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                         join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                         left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                         left outer JOIN\n" +
                "                           (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                            where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                            group by patient_id\n" +
                "                           ) d on d.patient_id = fup.patient_id\n" +
                "                  group by patient_id\n" +
                "                  having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                " ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) )))\n" +
                " ) t\n" +
                "inner join\n" +
                " (select s.patient_id,s.screening_type as screening_type from kenyaemr_etl.etl_cervical_cancer_screening s where s.screening_result ='Positive' and date(s.visit_date) between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate)\n" +
                " group by s.patient_id\n" +
                " having count(s.patient_id) = 1 or screening_type = \"First time screening\")sc\n" +
                " on t.patient_id = sc.patient_id\n" +
                " group by t.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("firstTimescreenedCXCAPositive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive women on ART screened Positive for cervical cancer 1st time");
        return cd;

    }

    public CohortDefinition firstTimescreenedCXCAPresumed() {

        String sqlQuery = "select t.patient_id from (select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                          max(fup.visit_date) as latest_vis_date,\n" +
                "                          mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                          max(d.visit_date) as date_discontinued,\n" +
                "                          d.patient_id as disc_patient,\n" +
                "                          de.patient_id as started_on_drugs\n" +
                "                   from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                          join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                          join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                          left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                          left outer JOIN\n" +
                "                            (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                             where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                             group by patient_id\n" +
                "                            ) d on d.patient_id = fup.patient_id\n" +
                "                   group by patient_id\n" +
                "                   having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                " ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) )))\n" +
                " ) t\n" +
                "inner join\n" +
                "(select s.patient_id,s.screening_type as screening_type from kenyaemr_etl.etl_cervical_cancer_screening s where s.screening_result ='Presumed' and date(s.visit_date) between date_sub(date(:endDate),INTERVAL 6 MONTH) and date(:endDate)\n" +
                "group by s.patient_id\n" +
                "having count(s.patient_id) = 1 or screening_type = \"First time screening\")sc\n" +
                "on t.patient_id = sc.patient_id\n" +
                "group by t.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("firstTimescreenedCXCAPresumed");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive women on ART with Presumed cervical cancer 1st time screening");
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
    public CohortDefinition rescreenedCXCANegative() {

        String sqlQuery = "select t.patient_id from (select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                  max(fup.visit_date) as latest_vis_date,\n" +
                "                  mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                  max(d.visit_date) as date_discontinued,\n" +
                "                  d.patient_id as disc_patient,\n" +
                "                  de.patient_id as started_on_drugs\n" +
                "                from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                  join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                  left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                  left outer JOIN\n" +
                "                    (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                     where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                     group by patient_id\n" +
                "                    ) d on d.patient_id = fup.patient_id\n" +
                "                group by patient_id\n" +
                "                having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "  ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) )))\n" +
                ") t\n" +
                "inner join\n" +
                " (select s.patient_id,s.screening_type as screening_type from kenyaemr_etl.etl_cervical_cancer_screening s\n" +
                " group by s.patient_id\n" +
                " having (mid(max(concat(s.visit_date,s.screening_result)),11) ='Negative' and mid(max(concat(s.visit_date,s.previous_screening_result)),11)='Negative')\n" +
                "        or (mid(max(concat(s.visit_date,s.screening_result)),11) ='Negative' and screening_type = \"Rescreening\")) scr\n" +
                "  on t.patient_id = scr.patient_id\n" +
                "  group by t.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("rescreenedCXCANegative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive women on ART with Negative cervical cancer results during re-screening");
        return cd;

    }
    public CohortDefinition rescreenedCXCAPositive() {

        String sqlQuery = "select t.patient_id from (select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                  max(fup.visit_date) as latest_vis_date,\n" +
                "                  mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                  max(d.visit_date) as date_discontinued,\n" +
                "                  d.patient_id as disc_patient,\n" +
                "                  de.patient_id as started_on_drugs\n" +
                "                from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                  join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                  left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                  left outer JOIN\n" +
                "                    (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                     where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                     group by patient_id\n" +
                "                    ) d on d.patient_id = fup.patient_id\n" +
                "                group by patient_id\n" +
                "                having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "  ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) )))\n" +
                ") t\n" +
                "inner join\n" +
                " (select s.patient_id,s.screening_type as screening_type from kenyaemr_etl.etl_cervical_cancer_screening s\n" +
                " group by s.patient_id\n" +
                " having (mid(max(concat(s.visit_date,s.screening_result)),11) ='Positive' and mid(max(concat(s.visit_date,s.previous_screening_result)),11)='Negative')\n" +
                "        or (mid(max(concat(s.visit_date,s.screening_result)),11) ='Positive' and screening_type = \"Rescreening\")) scr\n" +
                "  on t.patient_id = scr.patient_id\n" +
                "  group by t.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("rescreenedCXCAPositive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive women on ART with Positive cervical cancer results during re-screening");
        return cd;
    }
    public CohortDefinition rescreenedCXCAPresumed() {

        String sqlQuery = "select t.patient_id from (select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                  max(fup.visit_date) as latest_vis_date,\n" +
                "                  mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                  max(d.visit_date) as date_discontinued,\n" +
                "                  d.patient_id as disc_patient,\n" +
                "                  de.patient_id as started_on_drugs\n" +
                "                from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                  join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                  left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                  left outer JOIN\n" +
                "                    (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                     where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                     group by patient_id\n" +
                "                    ) d on d.patient_id = fup.patient_id\n" +
                "                group by patient_id\n" +
                "                having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "  ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) )))\n" +
                ") t\n" +
                "inner join\n" +
                " (select s.patient_id,s.screening_type as screening_type from kenyaemr_etl.etl_cervical_cancer_screening s\n" +
                " group by s.patient_id\n" +
                " having (mid(max(concat(s.visit_date,s.screening_result)),11) ='Presumed' and mid(max(concat(s.visit_date,s.previous_screening_result)),11)='Negative')\n" +
                "        or (mid(max(concat(s.visit_date,s.screening_result)),11) ='Presumed' and screening_type = \"Rescreening\")) scr\n" +
                "  on t.patient_id = scr.patient_id\n" +
                "  group by t.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("rescreenedCXCAPresumed");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive women on ART with Presumed cervical cancer during re-screening");
        return cd;

    }

    public CohortDefinition postTreatmentCXCANegative() {

        String sqlQuery = "select t.patient_id from (select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                  max(fup.visit_date) as latest_vis_date,\n" +
                "                  mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                  max(d.visit_date) as date_discontinued,\n" +
                "                  d.patient_id as disc_patient,\n" +
                "                  de.patient_id as started_on_drugs\n" +
                "                from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                  join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                  left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                  left outer JOIN\n" +
                "                    (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                     where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                     group by patient_id\n" +
                "                    ) d on d.patient_id = fup.patient_id\n" +
                "                group by patient_id\n" +
                "                having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "  ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) )))\n" +
                ") t\n" +
                "inner join\n" +
                " (select s.patient_id,s.screening_type as screening_type from kenyaemr_etl.etl_cervical_cancer_screening s\n" +
                " group by s.patient_id\n" +
                " having (mid(max(concat(s.visit_date,s.screening_result)),11) ='Negative' and mid(max(concat(s.visit_date,s.previous_screening_result)),11)='Positive')\n" +
                "        or (mid(max(concat(s.visit_date,s.screening_result)),11) ='Negative' and screening_type = \"Post treatment followup\")) scr\n" +
                "  on t.patient_id = scr.patient_id\n" +
                "  group by t.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("rescreenedCXCANegative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive women on ART with Negative cervical cancer results after Cervix Cancer treatment");
        return cd;

    }
    public CohortDefinition postTreatmentCXCAPositive() {

        String sqlQuery = "select t.patient_id from (select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                  max(fup.visit_date) as latest_vis_date,\n" +
                "                  mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                  max(d.visit_date) as date_discontinued,\n" +
                "                  d.patient_id as disc_patient,\n" +
                "                  de.patient_id as started_on_drugs\n" +
                "                from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                  join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                  left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                  left outer JOIN\n" +
                "                    (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                     where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                     group by patient_id\n" +
                "                    ) d on d.patient_id = fup.patient_id\n" +
                "                group by patient_id\n" +
                "                having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "  ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) )))\n" +
                ") t\n" +
                "inner join\n" +
                " (select s.patient_id,s.screening_type as screening_type from kenyaemr_etl.etl_cervical_cancer_screening s\n" +
                " group by s.patient_id\n" +
                " having (mid(max(concat(s.visit_date,s.screening_result)),11) ='Positive' and mid(max(concat(s.visit_date,s.previous_screening_result)),11)='Positive')\n" +
                "        or (mid(max(concat(s.visit_date,s.screening_result)),11) ='Positive' and screening_type = \"Post treatment followup\")) scr\n" +
                "  on t.patient_id = scr.patient_id\n" +
                "  group by t.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("rescreenedCXCAPositive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive women on ART with Positive cervical cancer results after Cervix Cancer treatment");
        return cd;
    }
    public CohortDefinition postTreatmentCXCAPresumed() {

        String sqlQuery = "select t.patient_id from (select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                  max(fup.visit_date) as latest_vis_date,\n" +
                "                  mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                  max(d.visit_date) as date_discontinued,\n" +
                "                  d.patient_id as disc_patient,\n" +
                "                  de.patient_id as started_on_drugs\n" +
                "                from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                  join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                  left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                  left outer JOIN\n" +
                "                    (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                     where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                     group by patient_id\n" +
                "                    ) d on d.patient_id = fup.patient_id\n" +
                "                group by patient_id\n" +
                "                having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "  ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) )))\n" +
                ") t\n" +
                "inner join\n" +
                " (select s.patient_id,s.screening_type as screening_type from kenyaemr_etl.etl_cervical_cancer_screening s\n" +
                " group by s.patient_id\n" +
                " having (mid(max(concat(s.visit_date,s.screening_result)),11) ='Presumed' and mid(max(concat(s.visit_date,s.previous_screening_result)),11)='Positive')\n" +
                "        or (mid(max(concat(s.visit_date,s.screening_result)),11) ='Presumed' and screening_type = \"Post treatment followup\")) scr\n" +
                "  on t.patient_id = scr.patient_id\n" +
                "  group by t.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("rescreenedCXCAPresumed");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive women on ART with Presumed cervical cancer after Cervix Cancer treatment");
        return cd;

    }

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

    //TODO Review with BA why all HTS indicators are based on PITC strategy

    //TODO : Review all HTS indicators with BA
    /* Inpatient Services Negative*/
    public CohortDefinition testedNegativeInpatientServices() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Negative\"\n" +
                "  and hts.patient_given_result =\"Yes\"\n" +
                "  and hts.hts_entry_point=\"In Patient Department(IPD)\"\n" +
                "  and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_Inpatient_Negative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Negative Inpatient Services");
        return cd;

    }

    /* Inpatient Services Positive*/
    public CohortDefinition testedPositiveInpatientServices() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Positive\"\n" +
                "  and hts.patient_given_result =\"Yes\"\n" +
                "  and hts.hts_entry_point=\"In Patient Department(IPD)\"\n" +
                "  and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_Inpatient_Positive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive Inpatient Services");
        return cd;

    }

    /* Paediatric services Positive <5*/
    public CohortDefinition testedPositivePaediatricServices() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts\n" +
                "            inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = hts.patient_id\n" +
                "            where hts.final_test_result =\"Positive\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.hts_entry_point =\"Peadiatric Clinic\"\n" +
                "and timestampdiff(year,d.DOB,:endDate)<5\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_Paediatric_Positive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive Paediatric Services");
        return cd;

    }

    /* Paediatric services Negative <5*/
    public CohortDefinition testedNegativePaediatricServices() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts\n" +
                "            inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = hts.patient_id\n" +
                "            where hts.final_test_result =\"Negative\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.hts_entry_point =\"Peadiatric Clinic\"\n" +
                "and timestampdiff(year,d.DOB,:endDate)<5\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_Paediatric_Negative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Negative Paediatric Services");
        return cd;

    }

    /* Malnutrition Clinics Negative <5*/
    public CohortDefinition testedNegativeMalnutritionClinics() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts\n" +
                "            inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = hts.patient_id\n" +
                "            where hts.final_test_result =\"Negative\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.hts_entry_point =\"Nutrition Clinic\"\n" +
                "and timestampdiff(year,d.DOB,:endDate)<5\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_Malnutrition_Negative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Negative Malnutrition Clinics");
        return cd;

    }

    /* Malnutrition Clinics Positive <5*/
    public CohortDefinition testedPositiveMalnutritionClinics() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts\n" +
                "            inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = hts.patient_id\n" +
                "            where hts.final_test_result =\"Positive\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.hts_entry_point =\"Nutrition Clinic\"\n" +
                "and timestampdiff(year,d.DOB,:endDate)<5\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_Malnutrition_Positive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive Malnutrition Clinics");
        return cd;

    }

    /* TB Clinic Negative*/
    public CohortDefinition testedNegativeTBClinic() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Negative\"\n" +
                "   and hts.patient_given_result =\"Yes\"\n" +
                "     and hts.hts_entry_point =\"TB\"\n" +
                "   and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_TB_Negative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Negative at TB Clinic");
        return cd;

    }

    /* TB Clinic Positive*/
    public CohortDefinition testedPositiveTBClinic() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Positive\"\n" +
                "   and hts.patient_given_result =\"Yes\"\n" +
                "     and hts.hts_entry_point =\"TB\"\n" +
                "   and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_TB_Positive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive at TB Clinic");
        return cd;

    }

    /*Tested Negative Other*/
    public CohortDefinition testedNagativeOther() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Negative\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.hts_entry_point in (\"Other\",\"Out Patient Department(OPD)\")\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_Other_Negative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Negative Other");
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
    /*Tested Positive Other*/
    public CohortDefinition testedPositiveOther() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Positive\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.hts_entry_point in (\"Other\",\"Out Patient Department(OPD)\")\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_Other_Positive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive Other");
        return cd;

    }

    /*Tested Negative VCT*/
    public CohortDefinition testedNagativeVCT() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Negative\"\n" +
                "   and hts.patient_given_result =\"Yes\"\n" +
                "   and hts.hts_entry_point in (\"VCT\",\"CCC\")\n" +
                "   and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_VCT_Negative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Negative VCT");
        return cd;

    }

    /*Tested Positive VCT*/
    public CohortDefinition testedPositiveVCT() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Positive\"\n" +
                "   and hts.patient_given_result =\"Yes\"\n" +
                "   and hts.hts_entry_point in (\"VCT\",\"CCC\")\n" +
                "   and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_VCT_Positive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive VCT");
        return cd;

    }

    /* Index Negative*/
    public CohortDefinition indexTestedNegative() {

        String sqlQuery = "select patient_id from (select c.patient_id,max(t.visit_date) as latest_hts\n" +
                "                                    from kenyaemr_hiv_testing_patient_contact c inner join kenyaemr_etl.etl_hts_test t on c.patient_id = t.patient_id\n" +
                "                                    where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "                                      and (t.final_test_result = \"Negative\")\n" +
                "                          and t.patient_given_result ='Yes'\n" +
                "                          and t.voided=0 and c.voided = 0 \n" +
                "                          group by c.id\n" +
                "                        having latest_hts between date(:startDate) and date(:endDate)) t";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_Index_Negative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Index Tested Negative");
        return cd;

    }

    /* Index Positive*/
    public CohortDefinition indextestedPositive() {

        String sqlQuery = "select patient_id from (select c.patient_id,max(t.visit_date) as latest_hts\n" +
                "                                    from kenyaemr_hiv_testing_patient_contact c inner join kenyaemr_etl.etl_hts_test t on c.patient_id = t.patient_id\n" +
                "                                    where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "                                      and (t.final_test_result = \"Positive\")\n" +
                "                          and t.patient_given_result ='Yes'\n" +
                "                          and t.voided=0 and c.voided = 0 \n" +
                "                          group by c.id\n" +
                "                        having latest_hts between date(:startDate) and date(:endDate)) t";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_Index_Positive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Index Tested Positive");
        return cd;

    }
    /*Mobile Outreach Positive*/
    public CohortDefinition testedPositiveMobile() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Positive\"\n" +
                "       and hts.patient_given_result =\"Yes\"\n" +
                "       and hts.hts_entry_point =\"Mobile Outreach\"\n" +
                "       and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_MOBILE_POSITIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive Mobile Outreach");
        return cd;
    }
    /*Mobile Outreach Negative*/
    public CohortDefinition testedNegativeMobile() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Negative\"\n" +
                "       and hts.patient_given_result =\"Yes\"\n" +
                "       and hts.hts_entry_point =\"Mobile Outreach\"\n" +
                "       and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_MOBILE_NEGATIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Negative Mobile Outreach");
        return cd;
    }

    /*Social Networks Positive*/
    public CohortDefinition testedPositiveSNS() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Positive\"\n" +
                "       and hts.patient_given_result =\"Yes\"\n" +
                "       and hts.test_strategy =\"SNS - Social Networks\"\n" +
                "       and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_SNS_POSITIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive Social Network");
        return cd;
    }

    /*Social Networks Negative*/
    public CohortDefinition testedNegativeSNS() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Negative\"\n" +
                "       and hts.patient_given_result =\"Yes\"\n" +
                "       and hts.test_strategy =\"SNS - Social Networks\"\n" +
                "       and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_SNS_NEGATIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Negative Social Network");
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
//TODO: To review with startedOnART on ETLDAtimCohortLibrary add filter for TB : Done
    /*Disaggregated by Age / Sex*/

    public CohortDefinition newlyStartedARTByAgeSex() {

        String sqlQuery = "select net.patient_id   \n" +
                "         from (\n" +
                "         select e.patient_id,e.date_started,\n" +
                "         e.gender,\n" +
                "         e.dob,\n" +
                "         d.visit_date as dis_date,\n" +
                "         if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "         e.regimen, e.regimen_line, e.alternative_regimen,\n" +
                "         mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "         max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art,\n" +
                "         max(if(enr.transfer_in_date is not null, 1, 0)) as TIn,\n" +
                "         max(enr.patient_type) as latest_patient_type,\n" +
                "         max(fup.visit_date) as latest_vis_date\n" +
                "         from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started,\n" +
                "         mid(min(concat(e.date_started,e.regimen_name)),11) as regimen,\n" +
                "         mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line,   \n" +
                "         max(if(discontinued,1,0))as alternative_regimen\n" +
                "         from kenyaemr_etl.etl_drug_event e\n" +
                "         join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "         where e.program = 'HIV'\n" +
                "         group by e.patient_id) e\n" +
                "         left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_name='HIV'\n" +
                "         left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "         left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "         where date(e.date_started) between date(:startDate) and :endDate\n" +
                "         group by e.patient_id\n" +
                "         having TI_on_art=0 and latest_patient_type in (164144,160563,159833)\n" +
                "         )net;";
//TODO: To review with startedOnART on ETLDAtimCohortLibrary : Done
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_New_Sex_Age");
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

    /*HIV Infected HEI Cohort*/
    public CohortDefinition hivInfectedHEICohort() {

        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_hei_enrollment e inner join kenyaemr_etl.etl_patient_demographics pd\n" +
                "                                on e.patient_id = pd.patient_id where timestampdiff(month ,pd.DOB, e.visit_date)>= 0 and timestampdiff(month ,pd.DOB, e.visit_date)<=18\n" +
                "                                and pd.dead = 0 and timestampdiff(month,pd.dob,:endDate) =24\n" +
                "                                and substr(pd.dob,6,2)= substr(:endDate,6,2)\n" +
                "                                and e.hiv_status_at_exit = \"Positive\"\n" +
                "group by e.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PMTCT_FO_INFECTED_HEI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Infected HEI Cohort");
        return cd;

    }

    /*Uninfected HEI Cohort*/
    public CohortDefinition hivUninfectedHEICohort() {

        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_hei_enrollment e inner join kenyaemr_etl.etl_patient_demographics pd\n" +
                "                      on e.patient_id = pd.patient_id where timestampdiff(month ,pd.DOB, e.visit_date)>= 0 and timestampdiff(month ,pd.DOB, e.visit_date)<=18\n" +
                "                      and pd.dead = 0 and timestampdiff(month,pd.dob,:endDate) =24\n" +
                "                      and substr(pd.dob,6,2)= substr(:endDate,6,2)\n" +
                "                      and e.hiv_status_at_exit =\"Negative\"\n" +
                "group by e.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PMTCT_FO_UNINFECTED_HEI");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Uninfected HEI Cohort");
        return cd;

    }

    /*Unknown HIV Status HEI Cohort*/
    public CohortDefinition unknownHIVStatusHEICohort() {
        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_hei_enrollment e inner join kenyaemr_etl.etl_patient_demographics pd\n" +
                "                                    on e.patient_id = pd.patient_id where timestampdiff(month ,pd.DOB, e.visit_date)>= 0 and timestampdiff(month ,pd.DOB, e.visit_date)<=18\n" +
                "        and pd.dead = 0 and timestampdiff(month,pd.dob,:endDate) =24\n" +
                "        and substr(pd.dob,6,2)= substr(:endDate,6,2)\n" +
                "        and e.hiv_status_at_exit is NULL or e.hiv_status_at_exit not in (\"Positive\",\"Negative\")\n" +
                "group by e.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PMTCT_FO_HEI_UNKNOWN_HIV_STATUS");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Unknown HIV Status HEI Cohort");
        return cd;

    }

    /*HEI died with Unknown HIV Status*/
    public CohortDefinition heiDiedWithUnknownStatus() {

        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_hei_enrollment e inner join kenyaemr_etl.etl_patient_demographics pd\n" +
                "                       on e.patient_id = pd.patient_id where timestampdiff(month ,pd.DOB, e.visit_date)>= 0 and timestampdiff(month ,pd.DOB, e.visit_date)<=18\n" +
                "                       and timestampdiff(month,pd.dob,:endDate) =24\n" +
                "                       and substr(pd.dob,6,2)= substr(:endDate,6,2)\n" +
                "                       and (e.exit_reason = 160034 or pd.dead = 1)\n" +
                "                       and e.hiv_status_at_exit is NULL or e.hiv_status_at_exit not in (\"Positive\",\"Negative\")\n" +
                "group by e.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PMTCT_FO_HEI_DIED_HIV_STATUS_UNKNOWN");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hei died with Unknown HIV Status");
        return cd;

    }
   //Number restarted Treatment during the reporting period
    public CohortDefinition txRTT() {

        String sqlQuery = "select k.patient_id\n" +
                "from (\n" +
                " -- Here is the lftu query\n" +
                " select t.patient_id,\n" +
                "t.latest_tca,\n" +
                "t.latest_vis_date,\n" +
                "t.date_discontinued\n" +
                " from(\n" +
                " select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "d.patient_id as disc_patient,\n" +
                "d.effective_disc_date as effective_disc_date,\n" +
                "max(d.visit_date) as date_discontinued,\n" +
                " d.discontinuation_reason,\n" +
                "de.patient_id as started_on_drugs\n" +
                " from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                " join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                " join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                " left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(curdate())\n" +
                " left outer JOIN\n" +
                " (select patient_id,\n" +
                " coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date,\n" +
                " discontinuation_reason from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                " where date(visit_date) <= date_sub(:startDate, INTERVAL 30 DAY) and program_name='HIV'\n" +
                " group by patient_id\n" +
                " ) d on d.patient_id = fup.patient_id\n" +
                "   where fup.visit_date <= date(:startDate)\n" +
                " group by patient_id\n" +
                " having (\n" +
                "  ( date_add(latest_tca, INTERVAL 30 DAY) < date(:startDate))\n" +
                " and (((date(d.effective_disc_date) > date(:startDate) or date(enroll_date) > date(d.effective_disc_date)) and d.discontinuation_reason = 5240) or d.effective_disc_date is null)\n" +
                " )) t\n" +
                "-- Here is the RTT\n" +
                "inner join kenyaemr_etl.etl_patient_hiv_followup f on f.patient_id=t.patient_id and date(f.visit_date) between date(latest_tca) and date(:endDate)\n" +
                "inner join kenyaemr_etl.etl_patient_hiv_followup r on r.patient_id=t.patient_id and date(r.visit_date) between date(:startDate) and date(:endDate)\n" +
                " group by t.patient_id) k;\n";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_RTT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number restarted Treatment during the reporting period");
        return cd;

    }
    /*Alive, Pregnant and on ART for last 12 months*/
    public CohortDefinition pregnantAliveOnARTLast12Months() {

        String sqlQuery = "select  net.patient_id\n" +
                "from (\n" +
                "     select e.patient_id,e.date_started, d.visit_date as dis_date, if(d.visit_date is not null and d.discontinuation_reason=159492, 1, 0) as TOut, d.date_died,\n" +
                "            mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "            if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date\n" +
                "     from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started\n" +
                "           from kenyaemr_etl.etl_drug_event e\n" +
                "                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "                  join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id = e.patient_id and v.visit_date between date(:startDate) and date(:endDate)\n" +
                "           where e.program='HIV'\n" +
                "           group by e.patient_id) e\n" +
                "            left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_uuid='2bdada65-4c72-4a48-8730-859890e25cee'\n" +
                "            left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "           left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "     where  date(e.date_started) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "     group by e.patient_id\n" +
                "     having   (dis_date>date(:endDate) or dis_date is null or TOut=0 ) and (\n" +
                "         (date(latest_tca) > date(:endDate) and (date(latest_tca) > date(dis_date) or dis_date is null ))  or\n" +
                "         (((date(latest_tca) between date(:startDate) and date(:endDate)) and (date(latest_tca) >= date(latest_vis_date)) ) ) or\n" +
                "         (((date(latest_tca) between date(:startDate) and date(:endDate)) and (date(latest_vis_date) >= date(latest_tca)) or date(latest_tca) > curdate()) ) and\n" +
                "         (date(latest_tca) > date(dis_date) or dis_date is null )\n" +
                "         )\n" +
                "     )net;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_RET_PREGNANT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Alive, Pregnant and on ART for last 12 months");
        return cd;

    }

    /*Alive, Breastfeeding and on ART for last 12 months*/
    public CohortDefinition bfAliveOnARTLast12Months() {

        String sqlQuery = "select  net.patient_id\n" +
                "from (\n" +
                "       select e.patient_id,e.date_started,d.visit_date as dis_date,if(d.visit_date is not null, 1, 0) as TOut,e.regimen, e.regimen_line\n" +
                "         ,e.alternative_regimen,mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date\n" +
                "       from (select de.patient_id,p.dob,p.Gender,min(de.date_started) as date_started,\n" +
                "                                                mid(min(concat(de.date_started,de.regimen_name)),11) as regimen,\n" +
                "                                                mid(min(concat(de.date_started,de.regimen_line)),11) as regimen_line,\n" +
                "                                                max(if(de.discontinued,1,0))as alternative_regimen\n" +
                "             from kenyaemr_etl.etl_drug_event de\n" +
                "               join kenyaemr_etl.etl_patient_demographics p on p.patient_id=de.patient_id\n" +
                "               join kenyaemr_etl.etl_mch_postnatal_visit pv on pv.patient_id = de.patient_id\n" +
                "               where pv.baby_feeding_method in (5526,6046) and de.program = 'HIV'\n" +
                "             group by de.patient_id) e\n" +
                "         left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id\n" +
                "         left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "         left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "\n" +
                "       where  date(e.date_started) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "       group by e.patient_id\n" +
                "       having   (dis_date>date(:endDate) or dis_date is null) )net;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_RET_BREASTFEEDING");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Alive, Breastfeeding and on ART for last 12 months");
        return cd;

    }

    /*Alive on ART in last 12 months by Age / Sex*/
    public CohortDefinition aliveOnARTInLast12MonthsByAgeSex() {

        String sqlQuery = "select  net.patient_id\n" +
                "from (\n" +
                "     select e.patient_id,e.date_started, d.visit_date as dis_date, if(d.visit_date is not null and d.discontinuation_reason=159492, 1, 0) as TOut, d.date_died,\n" +
                "            mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "            if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date\n" +
                "         from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started\n" +
                "         from kenyaemr_etl.etl_drug_event e\n" +
                "         join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "         where e.program='HIV'\n" +
                "         group by e.patient_id) e\n" +
                "         left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_uuid='2bdada65-4c72-4a48-8730-859890e25cee'\n" +
                "         left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "         left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "         where  date(e.date_started) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "         group by e.patient_id\n" +
                "         having   (dis_date>date(:endDate) or dis_date is null or TOut=0 ) and (\n" +
                "         (date(latest_tca) > date(:endDate) and (date(latest_tca) > date(dis_date) or dis_date is null ))  or\n" +
                "         (((date(latest_tca) between date(:startDate) and date(:endDate)) and (date(latest_tca) >= date(latest_vis_date)) ) ) or\n" +
                "         (((date(latest_tca) between date(:startDate) and date(:endDate)) and (date(latest_vis_date) >= date(latest_tca)) or date(latest_tca) > curdate()) ) and\n" +
                "         (date(latest_tca) > date(dis_date) or dis_date is null )\n" +
                "         )\n" +
                "     )net;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_RET_ALIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Alive on ART in last 12 months by Age / Sex");
        return cd;

    }

    /*Total started ART in last 12 months and Breastfeeding*/
    public CohortDefinition breastfeedingAndstartedARTinLast12Months() {

        String sqlQuery = "select net.patient_id\n" +
                "from (\n" +
                "     select e.patient_id,e.date_started, e.gender,e.dob,d.visit_date as dis_date, if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "            e.regimen, e.regimen_line, e.alternative_regimen, mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "            if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date\n" +
                "     from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started,\n" +
                "                  mid(min(concat(e.date_started,e.regimen_name)),11) as regimen,\n" +
                "                  mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line,\n" +
                "                  max(if(discontinued,1,0))as alternative_regimen\n" +
                "           from kenyaemr_etl.etl_drug_event e\n" +
                "                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "    where e.program = 'HIV'\n" +
                "           group by e.patient_id) e\n" +
                "            join kenyaemr_etl.etl_mch_postnatal_visit v on v.patient_id = e.patient_id\n" +
                "            left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_name='HIV'\n" +
                "            left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "            left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "     where  v.baby_feeding_method in (5526,6046)\n" +
                "       and date(e.date_started) between date_sub(:startDate , interval 1 year) and date_sub(:endDate , interval 1 year)\n" +
                "     group by e.patient_id\n" +
                "     )net;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_RET_BF");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total started ART in last 12 months and Breastfeeding");
        return cd;

    }

    /*Total started ART in last 12 months and Pregnant*/
    public CohortDefinition pregnantAndstartedARTinLast12Months() {

        String sqlQuery = "select  net.patient_id\n" +
                "from (\n" +
                "     select e.patient_id,e.date_started, d.visit_date as dis_date, if(d.visit_date is not null and d.discontinuation_reason=159492, 1, 0) as TOut, d.date_died,\n" +
                "            mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "            if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date\n" +
                "     from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started\n" +
                "           from kenyaemr_etl.etl_drug_event e\n" +
                "                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "           where e.program='HIV'\n" +
                "           group by e.patient_id) e\n" +
                "            left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_name='HIV'\n" +
                "            left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "            left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "            left outer join kenyaemr_etl.etl_mch_enrollment mch on mch.patient_id=e.patient_id\n" +
                "     where  date(e.date_started) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year)\n" +
                "                 and ((fup.pregnancy_status =1065 and fup.visit_date between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate)) OR\n" +
                "                 mch.visit_date between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate) )\n" +
                "     group by e.patient_id\n" +
                "     having   (dis_date>date(:endDate) or dis_date is null or TOut=0 )\n" +
                "     )net;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_RET_DENOMINATOR_PREGNANT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total started ART in last 12 months and Pregnant");
        return cd;

    }

    /*Total started ART in last 12 months by age / sex*/
    public CohortDefinition totalOnARTLast12MonthsByAgeSex() {

        String sqlQuery = "select  net.patient_id\n" +
                "from ( \n" +
                "select e.patient_id,e.date_started, d.visit_date as dis_date, if(d.visit_date is not null and d.discontinuation_reason=159492, 1, 0) as TOut, d.date_died,\n" +
                "mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca, \n" +
                "if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date\n" +
                " from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started\n" +
                " from kenyaemr_etl.etl_drug_event e \n" +
                " join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                " where e.program='HIV' \n" +
                " group by e.patient_id) e \n" +
                " left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_name = 'HIV'\n" +
                " left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id \n" +
                " left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id \n" +
                " where  date(e.date_started) between date_sub(date(:startDate) , interval 1 year) and date_sub(date(:endDate) , interval 1 year) \n" +
                " group by e.patient_id \n" +
                " having   (dis_date>date(:endDate) or dis_date is null or TOut=0 ) \n" +
                " )net;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_RET_ART_ALL");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Total on ART in last 12 months by Age / Sex");
        return cd;

    }

    // FIXME: 28/11/2018 Lines 1116-1479 Queries require the urgency: Routine, Targeted or Undocumented
    /*Patients on ART with Suppressed routine VL within last 12 Months*/
    //TODO update with reference with base queries
    public CohortDefinition onARTWithSuppressedRoutineVLLast12Months() {
        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                "  inner join\n" +
                "  (\n" +
                "    select\n" +
                "      patient_id,\n" +
                "      visit_date,\n" +
                "      if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "      urgency\n" +
                "    from kenyaemr_etl.etl_laboratory_extract\n" +
                "    where lab_test in (1305, 856) and urgency = 'ROUTINE'\n" +
                "  ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "where e.program='HIV' and timestampdiff(MONTH , e.date_started, :endDate)>3 and (vl_result.visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "group by e.patient_id\n" +
                "having mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)=\"LDL\" or mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)<1000;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_SUPP_ROUTINE_ALL");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients on ART with Suppressed routine VL within last 12 Months");
        return cd;

    }

    /*Patients on ART with Suppressed targeted VL within last 12 Months*/
    public CohortDefinition onARTWithSuppressedTargetedVLLast12Months() {

        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                "  inner join\n" +
                "  (\n" +
                "    select\n" +
                "      patient_id,\n" +
                "      visit_date,\n" +
                "      if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "      urgency\n" +
                "    from kenyaemr_etl.etl_laboratory_extract\n" +
                "    where lab_test in (1305, 856) and urgency = 'IMMEDIATELY'\n" +
                "  ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "where e.program='HIV' and timestampdiff(MONTH , e.date_started, :endDate)>3 and (vl_result.visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "group by e.patient_id\n" +
                "having mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)=\"LDL\" or mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)<1000;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_SUPP_TARGETED_ALL");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients on ART with Suppressed targeted VL within last 12 Months");
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
    /*Pregnant Women on ART with Suppressed Routine VL within last 12 Months*/
    public CohortDefinition pregnantAndBFOnARTWithSuppressedVLLast12Months(String testType) {

        String sqlQuery = "select a.patient_id as patient_id\n" +
                "from(select t.patient_id,vl.vl_date,vl.vl_result,vl.urgency,vl.order_reason from (\n" +
                "   select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "          greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "          greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "          greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "          d.patient_id as disc_patient,\n" +
                "          d.effective_disc_date as effective_disc_date,\n" +
                "          max(d.visit_date) as date_discontinued,\n" +
                "          de.patient_id as started_on_drugs,\n" +
                "          de.date_started\n" +
                "   from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
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
                "                       mid(max(concat(visit_date,urgency)),11) as urgency,\n" +
                "                       mid(max(concat(visit_date,order_reason)),11) as order_reason\n" +
                "                from kenyaemr_etl.etl_laboratory_extract\n" +
                "                group by patient_id\n" +
                "                having mid(max(concat(visit_date,lab_test)),11) in (1305,856) and max(visit_date) between\n" +
                "                    date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "                )vl\n" +
                "       on t.patient_id = vl.patient_id  where urgency ="+testType+" and (vl_result < 1000 or vl_result='LDL') and order_reason in (1434,159882))a;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_SUPP_PREGNANT_BF");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Pregnant Women on ART with Suppressed VL within last 12 Months");
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

    /*On ART with Suppressed VL within last 12 Months by sex/age*/
    public CohortDefinition onARTSuppVLAgeSex(String testType) {

        String sqlQuery = "  select a.patient_id as patient_id\n" +
                "     from(select t.patient_id,vl.vl_date,vl.vl_result,vl.urgency from (\n" +
                "                      select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "                             greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "                             greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "                             greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "                             d.patient_id as disc_patient,\n" +
                "                             d.effective_disc_date as effective_disc_date,\n" +
                "                             max(d.visit_date) as date_discontinued,\n" +
                "                             de.patient_id as started_on_drugs,\n" +
                "                             de.date_started\n" +
                "                      from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                             join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                             join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                             left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:startDate)\n" +
                "                             left outer JOIN\n" +
                "                               (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                group by patient_id\n" +
                "                               ) d on d.patient_id = fup.patient_id\n" +
                "                      where fup.visit_date <= date(:endDate)\n" +
                "                      group by patient_id\n" +
                "                      having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "                          (\n" +
                "                              ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "                                and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "                              )\n" +
                "                          ) order by date_started desc\n" +
                "                      ) t\n" +
                "                        inner join (\n" +
                "                                   select\n" +
                "                                          b.patient_id,\n" +
                "                                          max(b.visit_date) as vl_date,\n" +
                "                                          mid(max(concat(b.visit_date,b.lab_test)),11) as lab_test,\n" +
                "                                          if(mid(max(concat(b.visit_date,b.lab_test)),11) = 856, mid(max(concat(b.visit_date,b.test_result)),11), if(mid(max(concat(b.visit_date,b.lab_test)),11)=1305 and mid(max(concat(visit_date,test_result)),11) = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                                          mid(max(concat(b.visit_date,b.urgency)),11) as urgency\n" +
                "                                   from (select x.patient_id as patient_id,x.visit_date as visit_date,x.lab_test as lab_test, x.test_result as test_result,urgency as urgency\n" +
                "                                         from kenyaemr_etl.etl_laboratory_extract x where x.lab_test in (1305,856)\n" +
                "                                         group by x.patient_id,x.visit_date order by visit_date desc)b\n" +
                "                                   group by patient_id\n" +
                "                                   having mid(max(concat(visit_date,lab_test)),11) in (1305,856) and max(visit_date) between\n" +
                "                                       date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "                                   )vl\n" +
                "                          on t.patient_id = vl.patient_id  where urgency = "+testType+" and (vl_result < 1000 or vl_result='LDL'))a;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_SUPP");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("On ART with Suppressed VL within last 12 Months by sex/age");
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

    /*TX_ML Number of ART patients with no clinical contact since their last expected contact */
    public CohortDefinition txML() {

        String sqlQuery = "select  e.patient_id\n" +
                "from (\n" +
                "select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "max(fup.visit_date) as latest_vis_date,\n" +
                "mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "max(d.visit_date) as date_discontinued,\n" +
                "d.patient_id as disc_patient\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "left outer JOIN\n" +
                "(select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "where date(visit_date) <= curdate()  and program_name='HIV'\n" +
                "group by patient_id \n" +
                ") d on d.patient_id = fup.patient_id\n" +
                "where fup.visit_date <= :endDate and (:endDate BETWEEN date_sub(:endDate , interval 3 MONTH) and :endDate)\n" +
                "group by patient_id\n" +
                "having (\n" +
                "(((date(latest_tca) < :endDate) and (date(latest_vis_date) < date(latest_tca))) ) and ((date(latest_tca) > date(date_discontinued) and date(latest_vis_date) > date(date_discontinued)) or disc_patient is null ) and datediff(:endDate, date(latest_tca)) > 0)\n" +
                ") e;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_ML");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of ART patients with no clinical contact since their last expected contact");
        return cd;

    }

    /*Number of ART patients with no clinical contact since their last expected contact due to death */
    public CohortDefinition txMlDied() {

        String sqlQuery = "select tx_ml.patient_id from (select t.patient_id from(\n" +
                "       select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "              max(fup.visit_date) as latest_vis_date,\n" +
                "              mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "              max(d.visit_date) as date_discontinued,\n" +
                "              d.patient_id as disc_patient,\n" +
                "              de.patient_id as started_on_drugs\n" +
                "       from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "              join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "              join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "              left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "              left outer JOIN\n" +
                "                (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                 where visit_date <= date_sub(date(:endDate),INTERVAL 3 MONTH) and program_name='HIV'\n" +
                "                 group by patient_id\n" +
                "                ) d on d.patient_id = fup.patient_id\n" +
                "       where fup.visit_date <= date(:endDate)\n" +
                "       group by patient_id\n" +
                "       having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "           ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date_sub(date(:endDate),INTERVAL 3 MONTH)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date_sub(date(:endDate), INTERVAL 3 MONTH)))\n" +
                "           )\n" +
                "       ) t\n" +
                "           #Missed appointment\n" +
                "         inner join\n" +
                "           (\n" +
                "           select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                  max(fup.visit_date) as latest_vis_date,\n" +
                "                  mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                  max(d.visit_date) as date_discontinued,\n" +
                "                  d.patient_id as disc_patient\n" +
                "           from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                  join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                    -- ensure those discontinued are catered for\n" +
                "                  left outer JOIN\n" +
                "                    (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                     where visit_date <= :endDate  and program_name='HIV' and discontinuation_reason not in (809,159492,160034,5240)\n" +
                "                     group by patient_id -- check if this line is necessary\n" +
                "                    ) d on d.patient_id = fup.patient_id\n" +
                "           where fup.visit_date <= :endDate\n" +
                "           group by patient_id\n" +
                "               --  we may need to filter lost to follow-up using this\n" +
                "           having (\n" +
                "                      (((date(latest_tca) < :endDate) and (date(latest_vis_date) < date(latest_tca))) ) and ((date(latest_tca) > date(date_discontinued) and date(latest_vis_date) > date(date_discontinued)) or disc_patient is null ) and datediff(:endDate, date(latest_tca)) between 1 and 90)\n" +
                "           -- drop missd completely\n" +
                "           ) e on t.patient_id = e.patient_id) tx_ml\n" +
                "left outer join\n" +
                "           (select dt.patient_id from kenyaemr_etl.etl_ccc_defaulter_tracing dt where dt.is_final_trace =1267 and dt.true_status =160432 and date(dt.visit_date) between date(:startDate) and date(:endDate) group by dt.patient_id )dt on tx_ml.patient_id = dt.patient_id\n" +
                "left outer join\n" +
                "           (select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d group by d.patient_id having mid(max(concat(date(d.visit_date),d.discontinuation_reason)),11)=160034 and max(date(d.visit_date)) between date(:startDate) and date(:endDate))dis on tx_ml.patient_id = dis.patient_id\n" +
                "where (dis.patient_id is not null or dt.patient_id is not null)\n" +
                "group by tx_ml.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_ML_DIED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of ART patients with no clinical contact since their last expected contact due to death");
        return cd;

    }
    //Number of ART patients with no clinical contact since their last expected contact and have been on drugs for less than 3 months
public CohortDefinition txMLLTFUonDrugsUnder3Months() {

    String sqlQuery = "select tx_ml.patient_id from (select t.patient_id,t.date_started,t.latest_vis_date,t.latest_tca from(\n" +
            "      select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
            "             max(fup.visit_date) as latest_vis_date,\n" +
            "             mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
            "             max(d.visit_date) as date_discontinued,\n" +
            "             d.patient_id as disc_patient,\n" +
            "             de.patient_id as started_on_drugs,\n" +
            "             de.date_started\n" +
            "      from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
            "             join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
            "             join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
            "             left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
            "             left outer JOIN\n" +
            "               (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
            "                where visit_date <= date_sub(date(:endDate),INTERVAL 3 MONTH) and program_name='HIV'\n" +
            "                group by patient_id\n" +
            "               ) d on d.patient_id = fup.patient_id\n" +
            "      where fup.visit_date <= date(:endDate)\n" +
            "      group by patient_id\n" +
            "      having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
            "          ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date_sub(date(:endDate),INTERVAL 3 MONTH)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date_sub(date(:endDate), INTERVAL 3 MONTH)))\n" +
            "          )\n" +
            "      ) t\n" +
            "          #Missed appointment\n" +
            "        inner join\n" +
            "          (\n" +
            "          select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
            "                 max(fup.visit_date) as latest_vis_date,\n" +
            "                 mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
            "                 max(d.visit_date) as date_discontinued,\n" +
            "                 d.patient_id as disc_patient\n" +
            "          from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
            "                 join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
            "                 join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
            "                   -- ensure those discontinued are catered for\n" +
            "                 left outer JOIN\n" +
            "                   (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
            "                    where visit_date <= :endDate  and program_name='HIV'\n" +
            "                    group by patient_id -- check if this line is necessary\n" +
            "                   ) d on d.patient_id = fup.patient_id\n" +
            "          where fup.visit_date <= :endDate\n" +
            "          group by patient_id\n" +
            "              --  we may need to filter lost to follow-up using this\n" +
            "          having (\n" +
            "                     (((date(latest_tca) < :endDate) and (date(latest_vis_date) < date(latest_tca))) ) and ((date(latest_tca) > date(date_discontinued) and date(latest_vis_date) > date(date_discontinued)) or disc_patient is null ) and datediff(:endDate, date(latest_tca)) between 1 and 90)\n" +
            "          -- drop missd completely\n" +
            "          ) e on t.patient_id = e.patient_id) tx_ml\n" +
            "left outer join\n" +
            "(select dt.patient_id from kenyaemr_etl.etl_ccc_defaulter_tracing dt where dt.is_final_trace =1267 and dt.true_status =5240 and date(dt.visit_date) between date(:startDate) and date(:endDate) group by dt.patient_id )dt on tx_ml.patient_id = dt.patient_id\n" +
            "left outer join\n" +
            "(select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d group by d.patient_id having mid(max(concat(date(d.visit_date),d.discontinuation_reason)),11)=5240 and max(date(d.visit_date)) between date(:startDate) and date(:endDate))dis on tx_ml.patient_id = dis.patient_id\n" +
            "where (dis.patient_id is not null or dt.patient_id is not null or datediff(:endDate, date(tx_ml.latest_tca))>30) and datediff(tx_ml.latest_vis_date,tx_ml.date_started) < 90\n" +
            "group by tx_ml.patient_id;";
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("TX_ML_LTFU_ONDRUGS_UNDER3MONTHS");
    cd.setQuery(sqlQuery);
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.setDescription("Number of ART patients with no clinical contact since their last expected contact and have been on drugs for less than 3 months");
    return cd;

}

    //Number of ART patients with no clinical contact since their last expected contact and have been on drugs for more than 3 months
public CohortDefinition txMLLTFUonDrugsOver3Months() {

    String sqlQuery = "select tx_ml.patient_id from (select t.patient_id,t.date_started,t.latest_vis_date,t.latest_tca from(\n" +
            "      select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
            "             max(fup.visit_date) as latest_vis_date,\n" +
            "             mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
            "             max(d.visit_date) as date_discontinued,\n" +
            "             d.patient_id as disc_patient,\n" +
            "             de.patient_id as started_on_drugs,\n" +
            "             de.date_started\n" +
            "      from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
            "             join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
            "             join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
            "             left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
            "             left outer JOIN\n" +
            "               (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
            "                where visit_date <= date_sub(date(:endDate),INTERVAL 3 MONTH) and program_name='HIV'\n" +
            "                group by patient_id\n" +
            "               ) d on d.patient_id = fup.patient_id\n" +
            "      where fup.visit_date <= date(:endDate)\n" +
            "      group by patient_id\n" +
            "      having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
            "          ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date_sub(date(:endDate),INTERVAL 3 MONTH)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date_sub(date(:endDate), INTERVAL 3 MONTH)))\n" +
            "          )\n" +
            "      ) t\n" +
            "          #Missed appointment\n" +
            "        inner join\n" +
            "          (\n" +
            "          select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
            "                 max(fup.visit_date) as latest_vis_date,\n" +
            "                 mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
            "                 max(d.visit_date) as date_discontinued,\n" +
            "                 d.patient_id as disc_patient\n" +
            "          from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
            "                 join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
            "                 join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
            "                   -- ensure those discontinued are catered for\n" +
            "                 left outer JOIN\n" +
            "                   (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
            "                    where visit_date <= :endDate  and program_name='HIV'\n" +
            "                    group by patient_id -- check if this line is necessary\n" +
            "                   ) d on d.patient_id = fup.patient_id\n" +
            "          where fup.visit_date <= :endDate\n" +
            "          group by patient_id\n" +
            "              --  we may need to filter lost to follow-up using this\n" +
            "          having (\n" +
            "                     (((date(latest_tca) < :endDate) and (date(latest_vis_date) < date(latest_tca))) ) and ((date(latest_tca) > date(date_discontinued) and date(latest_vis_date) > date(date_discontinued)) or disc_patient is null ) and datediff(:endDate, date(latest_tca)) between 1 and 90)\n" +
            "          -- drop missd completely\n" +
            "          ) e on t.patient_id = e.patient_id) tx_ml\n" +
            "left outer join\n" +
            "(select dt.patient_id from kenyaemr_etl.etl_ccc_defaulter_tracing dt where dt.is_final_trace =1267 and dt.true_status =5240 and date(dt.visit_date) between date(:startDate) and date(:endDate) group by dt.patient_id )dt on tx_ml.patient_id = dt.patient_id\n" +
            "left outer join\n" +
            "(select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d group by d.patient_id having mid(max(concat(date(d.visit_date),d.discontinuation_reason)),11)=5240 and max(date(d.visit_date)) between date(:startDate) and date(:endDate))dis on tx_ml.patient_id = dis.patient_id\n" +
            "where (dis.patient_id is not null or dt.patient_id is not null  or datediff(:endDate, date(tx_ml.latest_tca))>30) and datediff(tx_ml.latest_vis_date,tx_ml.date_started) >90\n" +
            "group by tx_ml.patient_id;";
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("TX_ML_LTFU_ONDRUGS_OVER3MONTHS");
    cd.setQuery(sqlQuery);
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.setDescription("Number of ART patients with no clinical contact since their last expected contact and have been on drugs for more than 3 months");
    return cd;

}

/*Number of ART patients with no clinical contact since their last expected contact due to death as a result of TB */
    public CohortDefinition onARTMissedAppointmentDiedTB() {

        String sqlQuery = "select  e.patient_id\n" +
                "from (\n" +
                "     select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "            max(fup.visit_date) as latest_vis_date,\n" +
                "            mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "            max(d.visit_date) as date_discontinued,\n" +
                "            d.patient_id as disc_patient\n" +
                "     from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "            join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "            join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "            left outer JOIN\n" +
                "              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "               where date(visit_date) <= curdate()  and program_name='HIV'\n" +
                "               group by patient_id\n" +
                "              ) d on d.patient_id = fup.patient_id\n" +
                "\n" +
                "     where fup.visit_date <= :endDate and (:endDate BETWEEN date_sub(:endDate , interval 3 MONTH) and :endDate)\n" +
                "     group by patient_id\n" +
                "     having (\n" +
                "                (((date(latest_tca) < :endDate) and (date(latest_vis_date) < date(latest_tca))) ) and ((date(latest_tca) > date(date_discontinued) and date(latest_vis_date) > date(date_discontinued)) or disc_patient is null )\n" +
                "                  and timestampdiff(day, date(latest_tca),:endDate) > 28 and latest_tca between date_sub(date(:endDate),interval  6 MONTH) and date(:endDate))\n" +
                "     ) e  inner join kenyaemr_etl.etl_ccc_defaulter_tracing dt on dt.patient_id = e.patient_id and dt.true_status = 160432\n" +
                "                                                       and dt.is_final_trace = 1267 and dt.tracing_outcome = 1267\n" +
                "                                                      and dt.cause_of_death = 164500;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_ML_DIED_TB");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of ART patients with no clinical contact since their last expected contact due to death as a result of TB");
        return cd;

    }

    /*Number of ART patients with no clinical contact since their last expected contact due to death as a result of as cancer */
    public CohortDefinition onARTMissedAppointmentDiedCancer() {

        String sqlQuery = "select  e.patient_id\n" +
                "from (\n" +
                "     select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "            max(fup.visit_date) as latest_vis_date,\n" +
                "            mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "            max(d.visit_date) as date_discontinued,\n" +
                "            d.patient_id as disc_patient\n" +
                "     from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "            join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "            join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "            left outer JOIN\n" +
                "              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "               where date(visit_date) <= curdate()  and program_name='HIV'\n" +
                "               group by patient_id\n" +
                "              ) d on d.patient_id = fup.patient_id\n" +
                "\n" +
                "     where fup.visit_date <= :endDate and (:endDate BETWEEN date_sub(:endDate , interval 3 MONTH) and :endDate)\n" +
                "     group by patient_id\n" +
                "     having (\n" +
                "                (((date(latest_tca) < :endDate) and (date(latest_vis_date) < date(latest_tca))) ) and ((date(latest_tca) > date(date_discontinued) and date(latest_vis_date) > date(date_discontinued)) or disc_patient is null )\n" +
                "                  and timestampdiff(day, date(latest_tca),:endDate) > 28 and latest_tca between date_sub(date(:endDate),interval  6 MONTH) and date(:endDate))\n" +
                "     ) e  inner join kenyaemr_etl.etl_ccc_defaulter_tracing dt on dt.patient_id = e.patient_id and dt.true_status = 160432\n" +
                "                                                       and dt.is_final_trace = 1267 and dt.tracing_outcome = 1267\n" +
                "                                                         and dt.cause_of_death = 116030;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_ML_DIED_CANCER");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of ART patients with no clinical contact since their last expected contact due to death as a result of cancer");
        return cd;

    }

    /*Number of ART patients with no clinical contact since their last expected contact due to death as result of other infectious disease */
    public CohortDefinition onARTMissedAppointmentDiedOtherInfectious() {

        String sqlQuery = "select  e.patient_id\n" +
                "from (\n" +
                "     select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "            max(fup.visit_date) as latest_vis_date,\n" +
                "            mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "            max(d.visit_date) as date_discontinued,\n" +
                "            d.patient_id as disc_patient\n" +
                "     from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "            join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "            join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "            left outer JOIN\n" +
                "              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "               where date(visit_date) <= curdate()  and program_name='HIV'\n" +
                "               group by patient_id\n" +
                "              ) d on d.patient_id = fup.patient_id\n" +
                "\n" +
                "     where fup.visit_date <= :endDate and (:endDate BETWEEN date_sub(:endDate , interval 3 MONTH) and :endDate)\n" +
                "     group by patient_id\n" +
                "     having (\n" +
                "                (((date(latest_tca) < :endDate) and (date(latest_vis_date) < date(latest_tca))) ) and ((date(latest_tca) > date(date_discontinued) and date(latest_vis_date) > date(date_discontinued)) or disc_patient is null )\n" +
                "                  and timestampdiff(day, date(latest_tca),:endDate) > 28 and latest_tca between date_sub(date(:endDate),interval  6 MONTH) and date(:endDate))\n" +
                "     ) e  inner join kenyaemr_etl.etl_ccc_defaulter_tracing dt on dt.patient_id = e.patient_id and dt.true_status = 160432\n" +
                "                                                       and dt.is_final_trace = 1267 and dt.tracing_outcome = 1267\n" +
                "                                                       and dt.cause_of_death = 151522;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_ML_DIED_OTHER_INFECTIOUS_DISEASE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of ART patients with no clinical contact since their last expected contact due to death as a result of other infectious disease");
        return cd;

    }

    /*Number of ART patients with no clinical contact since their last expected contact due to death of as result of other disease/condition*/
    public CohortDefinition onARTMissedAppointmentDiedOtherDisease() {

        String sqlQuery = "select  e.patient_id\n" +
                "            from (\n" +
                "                 select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                        max(fup.visit_date) as latest_vis_date,\n" +
                "                        mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                        max(d.visit_date) as date_discontinued,\n" +
                "                        d.patient_id as disc_patient\n" +
                "                 from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                        join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                        join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                        left outer JOIN\n" +
                "                          (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                           where date(visit_date) <= curdate()  and program_name='HIV'\n" +
                "                           group by patient_id\n" +
                "                          ) d on d.patient_id = fup.patient_id\n" +
                "            \n" +
                "                 where fup.visit_date <= :endDate and (:endDate BETWEEN date_sub(:endDate , interval 3 MONTH) and :endDate)\n" +
                "                 group by patient_id\n" +
                "                 having (\n" +
                "                            (((date(latest_tca) < :endDate) and (date(latest_vis_date) < date(latest_tca))) ) and ((date(latest_tca) > date(date_discontinued) and date(latest_vis_date) > date(date_discontinued)) or disc_patient is null )\n" +
                "                              and timestampdiff(day, date(latest_tca),:endDate) > 28 and latest_tca between date_sub(date(:endDate),interval  6 MONTH) and date(:endDate))\n" +
                "                 ) e  inner join kenyaemr_etl.etl_ccc_defaulter_tracing dt on dt.patient_id = e.patient_id and dt.true_status = 160432\n" +
                "     and dt.is_final_trace = 1267 and dt.tracing_outcome = 1267\n" +
                "     and dt.cause_of_death = 162574;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_ML_DIED_OTHER_DISEASE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of ART patients with no clinical contact since their last expected contact due to death as a result of other disease/condition");
        return cd;

    }
    /*Number of ART patients with no clinical contact since their last expected contact due to death as result of natural causes*/
    public CohortDefinition onARTMissedAppointmentDiedNatural() {

        String sqlQuery = "select  e.patient_id\n" +
                "from (\n" +
                "     select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "            max(fup.visit_date) as latest_vis_date,\n" +
                "            mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "            max(d.visit_date) as date_discontinued,\n" +
                "            d.patient_id as disc_patient\n" +
                "     from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "            join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "            join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "            left outer JOIN\n" +
                "              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "               where date(visit_date) <= curdate()  and program_name='HIV'\n" +
                "               group by patient_id\n" +
                "              ) d on d.patient_id = fup.patient_id\n" +
                "\n" +
                "     where fup.visit_date <= :endDate and (:endDate BETWEEN date_sub(:endDate , interval 3 MONTH) and :endDate)\n" +
                "     group by patient_id\n" +
                "     having (\n" +
                "                (((date(latest_tca) < :endDate) and (date(latest_vis_date) < date(latest_tca))) ) and ((date(latest_tca) > date(date_discontinued) and date(latest_vis_date) > date(date_discontinued)) or disc_patient is null )\n" +
                "                  and timestampdiff(day, date(latest_tca),:endDate) > 28 and latest_tca between date_sub(date(:endDate),interval  6 MONTH) and date(:endDate))\n" +
                "     ) e  inner join kenyaemr_etl.etl_ccc_defaulter_tracing dt on dt.patient_id = e.patient_id and dt.true_status = 160432\n" +
                "                                                       and dt.is_final_trace = 1267 and dt.tracing_outcome = 1267\n" +
                "                                                         and dt.cause_of_death = 133481;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_ML_DIED_NATURAL");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of ART patients with no clinical contact since their last expected contact due to death as a result of natural causes");
        return cd;

    }

    /*Number of ART patients with no clinical contact since their last expected contact due to death as result of non-natural causes*/
    public CohortDefinition onARTMissedAppointmentDiedNonNatural() {

        String sqlQuery = "select  e.patient_id\n" +
                "from (\n" +
                "     select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "            max(fup.visit_date) as latest_vis_date,\n" +
                "            mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "            max(d.visit_date) as date_discontinued,\n" +
                "            d.patient_id as disc_patient\n" +
                "     from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "            join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "            join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "            left outer JOIN\n" +
                "              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "               where date(visit_date) <= curdate()  and program_name='HIV'\n" +
                "               group by patient_id\n" +
                "              ) d on d.patient_id = fup.patient_id\n" +
                "\n" +
                "     where fup.visit_date <= :endDate and (:endDate BETWEEN date_sub(:endDate , interval 3 MONTH) and :endDate)\n" +
                "     group by patient_id\n" +
                "     having (\n" +
                "                (((date(latest_tca) < :endDate) and (date(latest_vis_date) < date(latest_tca))) ) and ((date(latest_tca) > date(date_discontinued) and date(latest_vis_date) > date(date_discontinued)) or disc_patient is null )\n" +
                "                  and timestampdiff(day, date(latest_tca),:endDate) > 28 and latest_tca between date_sub(date(:endDate),interval  6 MONTH) and date(:endDate))\n" +
                "     ) e  inner join kenyaemr_etl.etl_ccc_defaulter_tracing dt on dt.patient_id = e.patient_id and dt.true_status = 160432\n" +
                "                                                       and dt.is_final_trace = 1267 and dt.tracing_outcome = 1267\n" +
                "                                                         and dt.cause_of_death = 1603;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_ML_DIED_NONNATURAL");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of ART patients with no clinical contact since their last expected contact due to death as a result of non-natural causes");
        return cd;

    }

    /*Number of ART patients with no clinical contact since their last expected contact due to death as result of unknown causes*/
    public CohortDefinition onARTMissedAppointmentDiedUnknown() {

        String sqlQuery = "select  e.patient_id\n" +
                "from (\n" +
                "     select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "            max(fup.visit_date) as latest_vis_date,\n" +
                "            mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "            max(d.visit_date) as date_discontinued,\n" +
                "            d.patient_id as disc_patient\n" +
                "     from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "            join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "            join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "            left outer JOIN\n" +
                "              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "               where date(visit_date) <= curdate()  and program_name='HIV'\n" +
                "               group by patient_id\n" +
                "              ) d on d.patient_id = fup.patient_id\n" +
                "\n" +
                "     where fup.visit_date <= :endDate and (:endDate BETWEEN date_sub(:endDate , interval 3 MONTH) and :endDate)\n" +
                "     group by patient_id\n" +
                "     having (\n" +
                "                (((date(latest_tca) < :endDate) and (date(latest_vis_date) < date(latest_tca))) ) and ((date(latest_tca) > date(date_discontinued) and date(latest_vis_date) > date(date_discontinued)) or disc_patient is null )\n" +
                "                  and timestampdiff(day, date(latest_tca),:endDate) > 28 and latest_tca between date_sub(date(:endDate),interval  6 MONTH) and date(:endDate))\n" +
                "     ) e  inner join kenyaemr_etl.etl_ccc_defaulter_tracing dt on dt.patient_id = e.patient_id and dt.true_status = 160432\n" +
                "                                                       and dt.is_final_trace = 1267 and dt.tracing_outcome = 1267\n" +
                "                                                       and dt.cause_of_death = 5622;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_ML_DIED_UNKNOWN");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of ART patients with no clinical contact since their last expected contact due to death as a result of unknown causes");
        return cd;

    }

    /*Number of ART patients with no clinical contact since their last expected contact due to undocumented transfer */
    public CohortDefinition txMLTrfOut() {

        String sqlQuery = "select tx_ml.patient_id from (select t.patient_id from(\n" +
                "      select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "             max(fup.visit_date) as latest_vis_date,\n" +
                "             mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "             max(d.visit_date) as date_discontinued,\n" +
                "             d.patient_id as disc_patient,\n" +
                "             de.patient_id as started_on_drugs,\n" +
                "             de.date_started\n" +
                "      from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "             join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "             join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "             left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "             left outer JOIN\n" +
                "               (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                where visit_date <= date_sub(date(:endDate),INTERVAL 3 MONTH) and program_name='HIV'\n" +
                "                group by patient_id\n" +
                "               ) d on d.patient_id = fup.patient_id\n" +
                "      where fup.visit_date <= date(:endDate)\n" +
                "      group by patient_id\n" +
                "      having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "          ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date_sub(date(:endDate),INTERVAL 3 MONTH)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date_sub(date(:endDate), INTERVAL 3 MONTH)))\n" +
                "          )\n" +
                "      ) t\n" +
                "          #Missed appointment\n" +
                "        inner join\n" +
                "          (\n" +
                "          select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                 max(fup.visit_date) as latest_vis_date,\n" +
                "                 mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                 max(d.visit_date) as date_discontinued,\n" +
                "                 d.patient_id as disc_patient\n" +
                "          from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                 join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                 join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                   -- ensure those discontinued are catered for\n" +
                "                 left outer JOIN\n" +
                "                   (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                    where visit_date <= :endDate  and program_name='HIV'\n" +
                "                    group by patient_id -- check if this line is necessary\n" +
                "                   ) d on d.patient_id = fup.patient_id\n" +
                "          where fup.visit_date <= :endDate\n" +
                "          group by patient_id\n" +
                "              --  we may need to filter lost to follow-up using this\n" +
                "          having (\n" +
                "                     (((date(latest_tca) < :endDate) and (date(latest_vis_date) < date(latest_tca))) ) and ((date(latest_tca) > date(date_discontinued) and date(latest_vis_date) > date(date_discontinued)) or disc_patient is null ) and datediff(:endDate, date(latest_tca)) between 1 and 90)\n" +
                "          -- drop missd completely\n" +
                "          ) e on t.patient_id = e.patient_id) tx_ml\n" +
                "left outer join\n" +
                "(select dt.patient_id from kenyaemr_etl.etl_ccc_defaulter_tracing dt where dt.is_final_trace =1267 and dt.true_status =1693 and date(dt.visit_date) between date(:startDate) and date(:endDate) group by dt.patient_id )dt on tx_ml.patient_id = dt.patient_id\n" +
                "left outer join\n" +
                "(select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d group by d.patient_id having mid(max(concat(date(d.visit_date),d.discontinuation_reason)),11)=159492 and max(date(d.visit_date)) between date(:startDate) and date(:endDate))dis on tx_ml.patient_id = dis.patient_id\n" +
                "where (dis.patient_id is not null or dt.patient_id is not null)\n" +
                "group by tx_ml.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_ML_TRF_OUT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of ART patients with no clinical contact since their last expected contact due to undocumented transfer");
        return cd;

    }

    /*Number of ART patients with no clinical contact since their last expected contact because they stopped treatment */
    public CohortDefinition txMLStoppedTreatment() {

        String sqlQuery = "select tx_ml.patient_id from (select t.patient_id from(\n" +
                "                          select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                                 max(fup.visit_date) as latest_vis_date,\n" +
                "                                 mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                                 max(d.visit_date) as date_discontinued,\n" +
                "                                 d.patient_id as disc_patient,\n" +
                "                                 de.patient_id as started_on_drugs\n" +
                "                          from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                                 join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                                 join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                                 left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                                 left outer JOIN\n" +
                "                                   (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                    where visit_date <= date_sub(date(:endDate),INTERVAL 3 MONTH) and program_name='HIV'\n" +
                "                                    group by patient_id\n" +
                "                                   ) d on d.patient_id = fup.patient_id\n" +
                "                          where fup.visit_date <= date(:endDate)\n" +
                "                          group by patient_id\n" +
                "                          having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "                              ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date_sub(date(:endDate),INTERVAL 3 MONTH)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date_sub(date(:endDate), INTERVAL 3 MONTH)))\n" +
                "                              )\n" +
                "                          ) t\n" +
                "                              #Missed appointment\n" +
                "                            inner join\n" +
                "                              (\n" +
                "                              select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                                     max(fup.visit_date) as latest_vis_date,\n" +
                "                                     mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                                     max(d.visit_date) as date_discontinued,\n" +
                "                                     d.patient_id as disc_patient\n" +
                "                              from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                                     join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                                     join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                                       -- ensure those discontinued are catered for\n" +
                "                                     left outer JOIN\n" +
                "                                       (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                        where visit_date <= :endDate  and program_name='HIV' and discontinuation_reason not in (809,159492,160034,5240)\n" +
                "                                        group by patient_id -- check if this line is necessary\n" +
                "                                       ) d on d.patient_id = fup.patient_id\n" +
                "                              where fup.visit_date <= :endDate\n" +
                "                              group by patient_id\n" +
                "                                  --  we may need to filter lost to follow-up using this\n" +
                "                              having (\n" +
                "                                         (((date(latest_tca) < :endDate) and (date(latest_vis_date) < date(latest_tca))) ) and ((date(latest_tca) > date(date_discontinued) and date(latest_vis_date) > date(date_discontinued)) or disc_patient is null ) and datediff(:endDate, date(latest_tca)) between 1 and 90)\n" +
                "                              -- drop missd completely\n" +
                "                              ) e on t.patient_id = e.patient_id) tx_ml\n" +
                "   left outer join\n" +
                "     (select dt.patient_id from kenyaemr_etl.etl_ccc_defaulter_tracing dt where dt.is_final_trace =1267 and dt.true_status =164435 and date(dt.visit_date) between date(:startDate) and date(:endDate) group by dt.patient_id )dt on tx_ml.patient_id = dt.patient_id\n" +
                "   left outer join\n" +
                "     (select d.patient_id from kenyaemr_etl.etl_patient_program_discontinuation d group by d.patient_id having mid(max(concat(date(d.visit_date),d.discontinuation_reason)),11)=819 and max(date(d.visit_date)) between date(:startDate) and date(:endDate))dis on tx_ml.patient_id = dis.patient_id\n" +
                "where (dis.patient_id is not null or dt.patient_id is not null)\n" +
                "group by tx_ml.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_ML_STOPPED_TREATMENT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of ART patients with no clinical contact since their last expected contact because they stopped treatment");
        return cd;

    }

    /*Number of ART patients with no clinical contact since their last expected contact due to un-traceability*/
    public CohortDefinition onARTMissedAppointmentUntraceable() {

        String sqlQuery = "select  e.patient_id\n" +
                "from (\n" +
                "     select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "            max(fup.visit_date) as latest_vis_date,\n" +
                "            mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "            max(d.visit_date) as date_discontinued,\n" +
                "            d.patient_id as disc_patient\n" +
                "     from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "            join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "            join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "            left outer JOIN\n" +
                "              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "               where date(visit_date) <= curdate()  and program_name='HIV'\n" +
                "               group by patient_id\n" +
                "              ) d on d.patient_id = fup.patient_id\n" +
                "\n" +
                "     where fup.visit_date <= :endDate and (:endDate BETWEEN date_sub(:endDate , interval 3 MONTH) and :endDate)\n" +
                "     group by patient_id\n" +
                "     having (\n" +
                "                (((date(latest_tca) < :endDate) and (date(latest_vis_date) < date(latest_tca))) ) and ((date(latest_tca) > date(date_discontinued) and date(latest_vis_date) > date(date_discontinued)) or disc_patient is null )\n" +
                "                  and timestampdiff(day, date(latest_tca),:endDate) > 28 and latest_tca between date_sub(date(:endDate),interval  6 MONTH) and date(:endDate))\n" +
                "     ) e  inner join kenyaemr_etl.etl_ccc_defaulter_tracing dt on dt.patient_id = e.patient_id and dt.true_status = 5240 and dt.is_final_trace = 1267\n" +
                "                                                            and dt.tracing_outcome = 1118 and dt.attempt_number >=3\n" +
                "                                                            and dt.tracing_type in (1650,\"eb113c76-aef8-4890-a611-fe22ba003123\",161642);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_ML_TRACED_UNLOCATED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of ART patients with no clinical contact since their last expected contact due to un-traceability");
        return cd;

    }

    /*Number of ART patients with no clinical contact since their last expected contact and no trace attempted*/
    public CohortDefinition onARTMissedAppointmentNotTraced() {

        String sqlQuery = "\n" +
                "select  e.patient_id\n" +
                "from (\n" +
                "     select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "            max(fup.visit_date) as latest_vis_date,\n" +
                "            mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "            max(d.visit_date) as date_discontinued,\n" +
                "            d.patient_id as disc_patient\n" +
                "     from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "            join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "            join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "            left outer JOIN\n" +
                "              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "               where date(visit_date) <= curdate()  and program_name='HIV'\n" +
                "               group by patient_id\n" +
                "              ) d on d.patient_id = fup.patient_id\n" +
                "\n" +
                "     where fup.visit_date <= :endDate and (:endDate BETWEEN date_sub(:endDate , interval 3 MONTH) and :endDate)\n" +
                "     group by patient_id\n" +
                "     having (\n" +
                "                (((date(latest_tca) < :endDate) and (date(latest_vis_date) < date(latest_tca))) ) and ((date(latest_tca) > date(date_discontinued) and date(latest_vis_date) > date(date_discontinued)) or disc_patient is null )\n" +
                "                  and timestampdiff(day, date(latest_tca),:endDate) > 28 and latest_tca between date_sub(date(:endDate),interval  6 MONTH) and date(:endDate))\n" +
                "     ) e  left outer join kenyaemr_etl.etl_ccc_defaulter_tracing dt on dt.patient_id = e.patient_id where  dt.patient_id is null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_ML_NO_TRACE_ATTEMPTED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of ART patients with no clinical contact since their last expected contact and no tracing attempted");
        return cd;

    }
        /*HTS_INDEX_OFFERED Number of individuals who were offered index testing services */
        public CohortDefinition offeredIndexServices() {

            String sqlQuery = "select c.patient_related_to from kenyaemr_hiv_testing_patient_contact c inner join kenyaemr_etl.etl_hts_test t\n" +
                    "               on c.patient_id = t.patient_id where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                    "               and t.final_test_result = \"Positive\" and t.voided=0 and c.voided = 0 and t.test_type = 2 and date(c.date_created)\n" +
                    "    between date_sub( date(:endDate), INTERVAL  3 MONTH )and date(:endDate) group by t.patient_id;";
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
                "  and c.voided = 0 and timestampdiff(YEAR ,date(:endDate),c.birth_date) < 15\n" +
                "  and date(c.date_created) between date_sub( date(:endDate), INTERVAL  3 MONTH )and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_ELICITED_MALE_CONTACTS_UNDER15");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of male contacts under 15 years elicited");
        return cd;

    }

    //HTS_INDEX_ACCEPTED Number of individuals who were offered and accepted index testing services
    public CohortDefinition acceptedIndexServices() {

        String sqlQuery = "select c.patient_related_to from kenyaemr_hiv_testing_patient_contact c\n" +
                "                                     where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "                                          and date(c.date_created) between date_sub( date(:endDate), INTERVAL  3 MONTH )and date(:endDate) group by c.patient_related_to;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_ACCEPTED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of indexes accepted Index Services");
        return cd;

    }

    //HTS_INDEX_POSITIVE Number of individuals who were tested Positive using Index testing services
    public CohortDefinition hivPositiveContact() {

        String sqlQuery = "select c.id from kenyaemr_hiv_testing_patient_contact c inner join kenyaemr_etl.etl_hts_test t on c.patient_id = t.patient_id\n" +
                "             where (c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)) and c.voided = 0 and t.voided =0\n" +
                "             group by c.id\n" +
                "               having mid(max(concat(t.visit_date,t.final_test_result)),11) = \"Positive\"\n" +
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
                "where (c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)) and c.voided = 0 and t.voided =0\n" +
                "group by c.id\n" +
                "having mid(max(concat(t.visit_date,t.final_test_result)),11) = \"Negative\"\n" +
                "   and max(t.visit_date) between date_sub( date(:endDate), INTERVAL  3 MONTH )and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_NEGATIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of individuals who were tested HIV Negative using Index testing services");
        return cd;

    }
    //Known HIV Positive contacts
    public CohortDefinition knownPositiveContact() {

        String sqlQuery = "select c.id from kenyaemr_hiv_testing_patient_contact c left join kenyaemr_etl.etl_hts_test t on c.patient_id = t.patient_id\n" +
                "left join kenyaemr_hiv_testing_client_trace tr on c.id = tr.client_id\n" +
                "where (c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)) and c.voided = 0 and t.voided =0\n" +
                "group by c.id\n" +
                "having   mid(max(concat(date(tr.date_created),tr.status)),11) ='Contacted and Linked' and max(date(tr.date_created)) between date_sub( date(:endDate), INTERVAL  3 MONTH )and date(:endDate)\n" +
                "    or (max(t.visit_date) < date_sub( date(:endDate), INTERVAL  3 MONTH ) and\n" +
                "    mid(max(concat(t.visit_date,t.final_test_result)),11) = \"Positive\");";
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

        String sqlQuery = "select v.patient_id \n" +
                "                from kenyaemr_etl.etl_mch_antenatal_visit v \n" +
                "                    inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = v.patient_id and e.date_of_discontinuation is null \n" +
                "    where v.final_test_result =\"Negative\" and v.anc_visit_number = 1;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_PMTCT_ANC1_Negative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Negative PMTCT services ANC-1");
        return cd;

    }

    /*Number Tested Positive PMTCT services ANC-1 only*/
    public CohortDefinition positivePMTCTANC1() {

        String sqlQuery = "select v.patient_id \n" +
                "                from kenyaemr_etl.etl_mch_antenatal_visit v \n" +
                "                    inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = v.patient_id and e.date_of_discontinuation is null \n" +
                "    where v.final_test_result =\"Positive\" and v.anc_visit_number = 1;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_PMTCT_ANC1_Positive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive PMTCT services ANC-1");
        return cd;

    }

    /*Number Tested Negative PMTCT services Post ANC-1 (including labour and delivery and BF)*/
    public CohortDefinition negativePMTCTPostANC1() {

        String sqlQuery = "select e.patient_id from \n" +
                "    kenyaemr_etl.etl_mch_enrollment e \n" +
                "        left join kenyaemr_etl.etl_mch_antenatal_visit av on av.patient_id = e.patient_id and av.anc_visit_number >1 \n" +
                "        left join kenyaemr_etl.etl_mchs_delivery d on d.patient_id = e.patient_id \n" +
                "        left join kenyaemr_etl.etl_mch_postnatal_visit pv on pv.patient_id = e.patient_id \n" +
                "    where \n" +
                "        (av.final_test_result = \"Negative\" and av.visit_date between date_sub(:endDate , interval 3 MONTH) and :endDate) \n" +
                "             or (d.final_test_result = \"Negative\" and d.visit_date between date_sub(:endDate , interval 3 MONTH) and :endDate) \n" +
                "             or (pv.final_test_result = \"Negative\" and pv.visit_date between date_sub(:endDate , interval 3 MONTH) and :endDate) \n" +
                "    group by e.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_PMTCT_POSTANC1_Negative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Negative PMTCT services Post ANC-1");
        return cd;

    }

    /*Number Tested Positive PMTCT services Post ANC-1 (including labour and delivery and BF)*/
    public CohortDefinition positivePMTCTPostANC1() {

        String sqlQuery = "select e.patient_id from \n" +
                "    kenyaemr_etl.etl_mch_enrollment e \n" +
                "        left join kenyaemr_etl.etl_mch_antenatal_visit av on av.patient_id = e.patient_id and av.anc_visit_number >1 \n" +
                "        left join kenyaemr_etl.etl_mchs_delivery d on d.patient_id = e.patient_id \n" +
                "        left join kenyaemr_etl.etl_mch_postnatal_visit pv on pv.patient_id = e.patient_id \n" +
                "    where \n" +
                "        (av.final_test_result = \"Positive\" and av.visit_date between date_sub(:endDate , interval 3 MONTH) and :endDate) \n" +
                "             or (d.final_test_result = \"Positive\" and d.visit_date between date_sub(:endDate , interval 3 MONTH) and :endDate) \n" +
                "             or (pv.final_test_result = \"Positive\" and pv.visit_date between date_sub(:endDate , interval 3 MONTH) and :endDate) \n" +
                "    group by e.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_PMTCT_POSTANC1_Positive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive PMTCT services Post ANC-1");
        return cd;

    }

    /**
     * Pregnant women currently on ART
     * TX_Curr_Pregnant Datim indicator
     * @return
     */
    public CohortDefinition pregnantCurrentOnArt() {
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
                "           left outer join kenyaemr_etl.etl_mch_enrollment mch on mch.patient_id=de.patient_id\n" +
                "    where  (fup.pregnancy_status = 1065 or mch.visit_date <= date(:endDate)) and fup.visit_date <= date(:endDate)\n" +
                "    group by patient_id\n" +
                "    having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "        (\n" +
                "            ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "              and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "            )\n" +
                "        )\n" +
                "    ) t;";

        cd.setName("TX_CURR_PREGNANT");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Currently on ART and Pregnant");
        return cd;
    }


    /**
     * Breast Feeding Mothers currently on ART
     * TX_Curr_BF Datim indicator
     * @return
     */
    public CohortDefinition bfCurrentOnArt() {
        SqlCohortDefinition cd = new SqlCohortDefinition();

        String sqlQuery="select t.patient_id\n" +
                "from(\n" +
                "select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "       greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "       greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "       greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "       d.patient_id as disc_patient,\n" +
                "       d.effective_disc_date as effective_disc_date,\n" +
                "       max(d.visit_date) as date_discontinued,\n" +
                "       de.patient_id as started_on_drugs\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "       join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "       join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "       left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "       left outer JOIN\n" +
                "         (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "          where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "          group by patient_id\n" +
                "         ) d on d.patient_id = fup.patient_id inner join (select mid(max(concat(pv.visit_date,pv.patient_id)),11 )latest_pv, max(visit_date) lst_pv_visit_date, baby_feeding_method\n" +
                "                                             from  kenyaemr_etl.etl_mch_postnatal_visit pv group by pv.patient_id) psnv on psnv.latest_pv = de.patient_id\n" +
                "                          where de.date_started <= psnv.lst_pv_visit_date and psnv.baby_feeding_method in (5526,6046) and fup.visit_date <= date(:endDate)\n" +
                "    group by patient_id\n" +
                "    having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "    ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "    and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null))\n" +
                "    ) t;";

        cd.setName("TX_CURR_BF");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Currently on ART and Breastfeeding");
        return cd;
    }
    /**
     * Patients currently on ART
     * TX_Curr Datim indicator
     * @return
     */
    public CohortDefinition kpCurrentOnArt(KPTypeDataDefinition type) {
        SqlCohortDefinition cd = new SqlCohortDefinition();

        String sqlQuery="select t.patient_id\n" +
                "from(\n" +
                "select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "       greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "       greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "       greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "       d.patient_id as disc_patient,\n" +
                "       d.effective_disc_date as effective_disc_date,\n" +
                "       max(d.visit_date) as date_discontinued,\n" +
                "       de.patient_id as started_on_drugs\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "       join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "       join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "       left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "       left outer JOIN\n" +
                "         (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "          where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "          group by patient_id\n" +
                "         ) d on d.patient_id = fup.patient_id\n" +
                "where fup.visit_date <= date(:endDate) and fup.key_population_type = "+type.getKpTypeConcept()+"\n" +
                "group by patient_id\n" +
                "having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "    (\n" +
                "        ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "          and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "        )\n" +
                "    )\n" +
                ") t;";

        cd.setName("TX_Curr_kp");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("currently on ART");
        return cd;
    }


    /**
     * Create dis-aggregations by number of months of drugs dispensed
     * TX_CURR_MONTHS_DRUGS indicator
     * @return
     */
    public CohortDefinition drugDurationCurrentOnArt(DurationToNextAppointmentDataDefinition duration) {
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
                "            ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "              and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                "        )\n" +
                "       and timestampdiff(day,greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')),greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00'))) " +duration.getDuration()+"\n" +
                "    ) t;";

        cd.setName("TX_CURR_MONTHS_DRUGS");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Currently on ART and Breastfeeding");
        return cd;
    }

    /**
     * Number of individuals who were newly enrolled on oral antiretroviral pre-exposure prophylaxis (PrEP) to prevent HIV infection in the reporting period
     * PrEP_NEWLY_ENROLLED indicator
     * @return
     */
    public CohortDefinition newlyEnrolledInPrEP() {

        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_prep_enrolment e\n" +
                "        left join(select d.patient_id,max(date(d.visit_date)) last_disc_date from kenyaemr_etl.etl_prep_discontinuation d ) d on d.patient_id = e.patient_id\n" +
                "group by e.patient_id\n" +
                "        having max(date(e.visit_date)) between date_sub(:endDate , interval 3 MONTH) and :endDate;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PrEP_NEWLY_ENROLLED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Newly enrolled on PrEP");
        return cd;

    }
    /**
     * Newly eonrolled to prep with a recent HIV Positive results within 3 months into enrolment
     * PrEP_NEWLY_ENROLLED indicator
     * @return
     */
    public CohortDefinition newlyEnrolledInPrEPHIVPos() {

        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_prep_enrolment e\n" +
                "                           left join(select d.patient_id,max(date(d.visit_date)) last_disc_date from kenyaemr_etl.etl_prep_discontinuation d ) d on d.patient_id = e.patient_id\n" +
                "                           join kenyaemr_etl.etl_hts_test t on t.patient_id = e.patient_id\n" +
                "where e.voided = 0 and t.voided = 0\n" +
                "group by e.patient_id\n" +
                "having max(date(e.visit_date)) between date_sub(:endDate , interval 3 MONTH) and :endDate and max(t.visit_date) between max(date(e.visit_date)) and DATE_ADD(max(date(e.visit_date)), INTERVAL 3 MONTH)\n" +
                "   and mid(max(concat(t.visit_date,t.final_test_result)),11) = \"Positive\";";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PrEP_NEWLY_ENROLLED_HIVPOS");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Newly enrolled on PrEP HIV Positive 3 months after enrolment");
        return cd;

    }
    /**
     * Newly eonrolled to prep with a recent HIV Negative results within 3 months into enrolment
     * PrEP_NEWLY_ENROLLED indicator
     * @return
     */
    public CohortDefinition newlyEnrolledInPrEPHIVNeg() {

        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_prep_enrolment e\n" +
                "                           left join(select d.patient_id,max(date(d.visit_date)) last_disc_date from kenyaemr_etl.etl_prep_discontinuation d ) d on d.patient_id = e.patient_id\n" +
                "                           join kenyaemr_etl.etl_hts_test t on t.patient_id = e.patient_id\n" +
                "where e.voided = 0 and t.voided = 0\n" +
                "group by e.patient_id\n" +
                "having max(date(e.visit_date)) between date_sub(:endDate , interval 3 MONTH) and :endDate and max(t.visit_date) between max(date(e.visit_date)) and DATE_ADD(max(date(e.visit_date)), INTERVAL 3 MONTH)\n" +
                "   and mid(max(concat(t.visit_date,t.final_test_result)),11) = \"Negative\";";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PrEP_NEWLY_ENROLLED_HIVNEG");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Newly enrolled on PrEP HIV Negative 3 months after enrolment");
        return cd;

    }

    /**
     * Number of individuals who were currently enrolled on oral antiretroviral pre-exposure prophylaxis (PrEP) to prevent HIV infection in the reporting period
     * PrEP_CURR_ENROLLED indicator
     * @return
     */
    public CohortDefinition currEnrolledInPrEP() {

        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_prep_enrolment e\n" +
                "left join(select d.patient_id,max(date(d.visit_date)) last_disc_date from kenyaemr_etl.etl_prep_discontinuation d ) d on d.patient_id = e.patient_id\n" +
                "where d.patient_id is null  and (e.visit_date between\n" +
                "    date(case MONTH(:startDate) when 1 then replace('"+startOfYear+"','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR)))) when 2 then replace('"+startOfYear+"','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR))))\n" +
                "                                when 3 then replace('"+startOfYear+"','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR)))) when 4 then replace('"+startOfYear+"','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR))))\n" +
                "                                when 5 then replace('"+startOfYear+"','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR)))) when 6 then replace('"+startOfYear+"','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR))))\n" +
                "                                when 7 then replace('"+startOfYear+"','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR)))) when 8 then replace('"+startOfYear+"','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR))))\n" +
                "                                when 9 then replace('"+startOfYear+"','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR)))) when 10 then replace('"+startOfYear+"','0000',YEAR(:startDate))\n" +
                "                                when 11 then replace('"+startOfYear+"','0000',YEAR(:startDate))\n" +
                "                                when 12 then replace('"+startOfYear+"','0000',YEAR(:startDate)) else null end) and date(:endDate))\n" +
                "group by e.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PrEP_CURR_ENROLLED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Currently enrolled on PrEP");
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
     *Proportion of  patients who started on a standard course of TB Preventive Treatment (TPT) in the previous reporting period who completed therapy
     * Composition
     * @return
     */
    public CohortDefinition prevOnIPTandCompleted() {

        String sqlQuery = "select i.patient_id from\n" +
                "  (select i.patient_id, max(i.visit_date) as initiation_date,max(o.visit_date),o.outcome\n" +
                "     from kenyaemr_etl.etl_ipt_initiation i join kenyaemr_etl.etl_ipt_outcome o\n" +
                "         on o.patient_id =i.patient_id and o.outcome = 1267\n" +
                "     group by i.patient_id\n" +
                "     having max(i.visit_date) between date_sub(:startDate , interval 6 MONTH) and date_sub(:endDate, interval 6 MONTH)\n" +
                "            and max(o.visit_date) between date(:startDate) and date(:endDate)) i;;";

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
        cd.addSearch("startedOnART", ReportUtils.map(startedOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("prevOnIPTandCompleted", ReportUtils.map(prevOnIPTandCompleted(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("startedOnART AND prevOnIPTandCompleted");
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
        cd.addSearch("startedOnART", ReportUtils.map(startedOnART(), "startDate=${startDate},endDate=${endDate}"));
        cd.addSearch("prevOnIPTandCompleted", ReportUtils.map(prevOnIPTandCompleted(), "startDate=${startDate},endDate=${endDate}"));
        cd.setCompositionString("previouslyOnART AND prevOnIPTandCompleted");
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
     *  Number of KPs who received prevention services
     * KP_PREV
     */
    public CohortDefinition kpPrev(String kpType) {
        if (kpType.equals("TRANSGENDER_SW")) {
            kpType = TRANSGENDER_SW;
        } else if (kpType.equals("TRANSGENDER_NOT_SW")) {
            kpType = TRANSGENDER_NOT_SW;
        }
        SqlCohortDefinition cd = new SqlCohortDefinition();

        String sqlQuery = "select c.client_id from kenyaemr_etl.etl_contact c\n"
                + "    inner join (select e.client_id,max(e.visit_date) as enrolment_date,mid(max(concat(e.visit_date,e.ever_tested_for_hiv)),11) as ever_tested_for_hiv,mid(max(concat(e.visit_date,e.share_test_results)),11) as hiv_status_at_enrolment from kenyaemr_etl.etl_client_enrollment e group by e.client_id ) e on c.client_id = e.client_id\n"
                + "    left join (select t.patient_id,min(t.visit_date) as first_hts_date,mid(min(concat(t.final_test_result)),11) as first_hiv_results from kenyaemr_etl.etl_hts_test t group by t.patient_id)t on c.client_id = t.patient_id\n"
                + "    left join (select v.client_id, min(v.visit_date) as first_clinical_visit_date from kenyaemr_etl.etl_clinical_visit v group by v.client_id)v on c.client_id = v.client_id\n"
                + "    left join (select p.client_id, min(p.visit_date) as first_peer_enc from kenyaemr_etl.etl_peer_calendar p group by p.client_id)p on c.client_id = p.client_id\n"
                + "where((((e.ever_tested_for_hiv = 'No' or e.hiv_status_at_enrolment in('Yes I tested negative','No I do not want to share',null)) and (t.first_hts_date between\n"
                + "date(case MONTH(:startDate) when 1 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR)))) when 2 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR))))\n"
                + "        when 3 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR)))) when 4 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR))))\n"
                + "        when 5 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR)))) when 6 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR))))\n"
                + "        when 7 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR)))) when 8 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR))))\n"
                + "        when 9 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR)))) when 10 then replace('"
                + startOfYear
                + "','0000',YEAR(:startDate))\n"
                + "        when 11 then replace('"
                + startOfYear
                + "','0000',YEAR(:startDate)) when 12 then replace('"
                + startOfYear
                + "','0000',YEAR(:startDate)) else null end) and date(:endDate)))\n"
                + "or (v.first_clinical_visit_date between (case MONTH(:startDate) when 1 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR)))) when 2 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR))))\n"
                + "       when 3 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR)))) when 4 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR))))\n"
                + "       when 5 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR)))) when 6 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR))))\n"
                + "       when 7 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR)))) when 8 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR))))\n"
                + "       when 9 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR)))) when 10 then replace('"
                + startOfYear
                + "','0000',YEAR(:startDate))\n"
                + "       when 11 then replace('"
                + startOfYear
                + "','0000',YEAR(:startDate)) when 12 then replace('"
                + startOfYear
                + "','0000',YEAR(:startDate)) else null end) and date(:endDate))\n"
                + "or (p.first_peer_enc between (case MONTH(:startDate) when 1 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR)))) when 2 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR))))\n"
                + "        when 3 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR)))) when 4 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR))))\n"
                + "        when 5 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR)))) when 6 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR))))\n"
                + "        when 7 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR)))) when 8 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR))))\n"
                + "        when 9 then replace('"
                + startOfYear
                + "','0000',(YEAR(date_sub(:startDate, INTERVAL 1 YEAR)))) when 10 then replace('"
                + startOfYear
                + "','0000',YEAR(:startDate))\n"
                + "        when 11 then replace('"
                + startOfYear
                + "','0000',YEAR(:startDate)) when 12 then replace('"
                + startOfYear
                + "','0000',YEAR(:startDate)) else null end) and date(:endDate))) and c.key_population_type = "
                + kpType
                + " and c.voided=0)\n" + "group by c.client_id;\n";
        cd.setName("kpPrev");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("kpPrev");

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

}


