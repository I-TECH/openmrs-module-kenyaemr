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
import org.openmrs.module.kenyacore.report.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.kenyaemr.calculation.library.ovc.OnOVCProgramCalculation;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.KPTypeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.DurationToNextAppointmentDataDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
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
                "                where date(e.date_started) between date_sub(:endDate , interval 3 MONTH) and :endDate\n" +
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
                "       where date(e.date_started) between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate)\n" +
                "       and ((fup.pregnancy_status =1065 and fup.visit_date between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate)) OR\n" +
                "            mch.visit_date between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate) )\n" +
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
                "       where date(e.date_started) between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate)\n" +
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

        String sqlQuery="select patient_id from(\n" +
                "select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "      max(fup.visit_date) as latest_vis_date,\n" +
                "      mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "      max(d.visit_date) as date_discontinued,\n" +
                "      d.patient_id as disc_patient,\n" +
                "    de.patient_id as started_on_drugs\n" +
                "  from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "  join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "  left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "  left outer JOIN\n" +
                "  (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "  where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "  group by patient_id\n" +
                "  ) d on d.patient_id = fup.patient_id\n" +
                "  where fup.visit_date <= date(:endDate)\n" +
                "  group by patient_id\n" +
                "  having (started_on_drugs is not null and started_on_drugs <> \"\") and ( \n" +
                "  ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) ))\n" +
                "  )\n" +
                ") t;";

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

        String sqlQuery = "select v.patient_id\n" +
                "                from kenyaemr_etl.etl_mch_antenatal_visit v where v.visit_date  between date_sub(:endDate, interval 3 MONTH) and date(:endDate)\n" +
                "                and v.patient_given_result =\"Yes\" group by v.patient_id\n" +
                "                having mid(min(concat(v.visit_date,final_test_result)),11)=\"Positive\"\n" +
                "      and mid(min(concat(v.visit_date,v.final_test_result)),1,10) between date_sub(:endDate, interval 3 MONTH) and date(:endDate);";
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

        String sqlQuery = "select v.patient_id\n" +
                "                from kenyaemr_etl.etl_mch_antenatal_visit v where v.visit_date  between date_sub(:endDate, interval 3 MONTH) and date(:endDate)\n" +
                "                and v.patient_given_result =\"Yes\" group by v.patient_id\n" +
                "                having mid(min(concat(v.visit_date,final_test_result)),11)=\"Negative\"\n" +
                "      and mid(min(concat(v.visit_date,v.final_test_result)),1,10) between date_sub(:endDate, interval 3 MONTH) and date(:endDate);";
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

        String sqlQuery = "select e.patient_id\n" +
                "                from kenyaemr_etl.etl_mch_enrollment e\n" +
                "                       join kenyaemr_etl.etl_mch_antenatal_visit v on e.patient_id = v.patient_id\n" +
                "                where e.hiv_status =703\n" +
                "                group by e.patient_id\n" +
                "                having min(v.visit_date) between date_sub(:endDate, interval 3 MONTH) and date(:endDate)\n" +
                "                      and min(v.visit_date) > max(e.visit_date);";

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

        String sqlQuery = "select v.patient_id from kenyaemr_etl.etl_mch_antenatal_visit v inner join kenyaemr_etl.etl_hts_test t on v.patient_id=t.patient_id\n" +
                "where t.final_test_result = \"Negative\"  and t.patient_given_result=\"Yes\"\n" +
                "group by v.patient_id\n" +
                "having  timestampdiff(month,max(t.visit_date), min(v.visit_date) )< 3\n" +
                "and min(v.visit_date) between date_sub(:endDate, interval 3 MONTH) and date(:endDate);";

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

        String sqlQuery = "select av.patient_id from kenyaemr_etl.etl_mch_antenatal_visit av\n" +
                "inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = av.patient_id\n" +
                "where av.anc_visit_number = 1 and av.visit_date\n" +
                "                   between date_sub(:endDate, interval 3 MONTH) and date(:endDate)\n" +
                "group by e.patient_id\n" +
                "having max(e.visit_date) between date_sub(:endDate, interval 3 MONTH) and date(:endDate);";
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
                "    ) t\n" +
                "join\n" +
                "(select fup.patient_id from kenyaemr_etl.etl_patient_hiv_followup fup where fup.cacx_screening =664 group by fup.patient_id having count(fup.cacx_screening)=1\n" +
                "                       union all  select anc.patient_id from kenyaemr_etl.etl_mch_antenatal_visit anc where anc.cacx_screening = 664 group by anc.patient_id\n" +
                "       having count(anc.cacx_screening) =1\n" +
                "                       union all select pnc.patient_id from kenyaemr_etl.etl_mch_postnatal_visit pnc where pnc.cacx_screening =664 group by pnc.patient_id\n" +
                "       having count(pnc.cacx_screening) = 1)cacx on t.patient_id = cacx.patient_id;";

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
                "                                    where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                    group by patient_id\n" +
                "                                   ) d on d.patient_id = fup.patient_id\n" +
                "                          group by patient_id\n" +
                "                          having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "                              ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) ))\n" +
                "                              )\n" +
                "                         ) t\n" +
                "                           join\n" +
                "                             (select fup.patient_id from kenyaemr_etl.etl_patient_hiv_followup fup where fup.cacx_screening =703 group by fup.patient_id having count(fup.cacx_screening)=1\n" +
                "                              union all  select anc.patient_id from kenyaemr_etl.etl_mch_antenatal_visit anc where anc.cacx_screening =703 group by anc.patient_id\n" +
                "                                         having count(anc.cacx_screening) =1\n" +
                "                              union all select pnc.patient_id from kenyaemr_etl.etl_mch_postnatal_visit pnc where pnc.cacx_screening =703 group by pnc.patient_id\n" +
                "                                          having count(pnc.cacx_screening) = 1)cacx on t.patient_id = cacx.patient_id;";

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
                "                                    where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                    group by patient_id\n" +
                "                                   ) d on d.patient_id = fup.patient_id\n" +
                "                          group by patient_id\n" +
                "                          having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "                              ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) ))\n" +
                "                              )\n" +
                "                         ) t\n" +
                "                           join\n" +
                "                             (select fup.patient_id from kenyaemr_etl.etl_patient_hiv_followup fup where fup.cacx_screening = 159393 group by fup.patient_id having count(fup.cacx_screening)=1\n" +
                "                              union all  select anc.patient_id from kenyaemr_etl.etl_mch_antenatal_visit anc where anc.cacx_screening = 159393 group by anc.patient_id\n" +
                "                                         having count(anc.cacx_screening) =1\n" +
                "                              union all select pnc.patient_id from kenyaemr_etl.etl_mch_postnatal_visit pnc where pnc.cacx_screening = 159393 group by pnc.patient_id\n" +
                "                                        having count(pnc.cacx_screening) = 1)cacx on t.patient_id = cacx.patient_id;";

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
                "                                    where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                    group by patient_id\n" +
                "                                   ) d on d.patient_id = fup.patient_id\n" +
                "                          group by patient_id\n" +
                "                          having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "                              ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) ))\n" +
                "                              )\n" +
                "                         ) t\n" +
                "    join\n" +
                "(select z.patient_id from\n" +
                "(select t.patient_id,t.visit_date,t.cacx_screening\n" +
                " from (select fup.*,\n" +
                "              (@rn := if(@v = patient_id, @rn + 1,\n" +
                "                         if(@v := patient_id, 1, 1)\n" +
                "                  )\n" +
                "                  ) as rn\n" +
                "       from kenyaemr_etl.etl_patient_hiv_followup fup cross join\n" +
                "                (select @v := -1, @rn := 0) params\n" +
                "where fup.voided = 0\n" +
                "       order by fup.patient_id,fup.visit_date desc\n" +
                "      ) t where rn=1 group by t.patient_id) z\n" +
                "     join\n" +
                "(select x.patient_id,x.visit_date,x.cacx_screening\n" +
                " from (select fup.*,\n" +
                "              (@rn := if(@v = patient_id, @rn + 1,\n" +
                "                         if(@v := patient_id, 1, 1)\n" +
                "                  )\n" +
                "                  ) as rn\n" +
                "       from kenyaemr_etl.etl_patient_hiv_followup fup cross join\n" +
                "                (select @v := -1, @rn := 0) params\n" +
                "       where fup.voided = 0\n" +
                "       order by fup.patient_id, fup.visit_date desc\n" +
                "      ) x\n" +
                " where rn=2 )y on y.patient_id = z.patient_id\n" +
                "where z.cacx_screening = 664 and y.cacx_screening = 664\n" +
                "    group by z.patient_id\n" +
                "union all\n" +
                "select z.patient_id from\n" +
                "                         (select t.patient_id,t.visit_date,t.cacx_screening\n" +
                "                          from (select anc.*,\n" +
                "                                       (@rn := if(@v = patient_id, @rn + 1,\n" +
                "                                                  if(@v := patient_id, 1, 1)\n" +
                "                                           )\n" +
                "                                           ) as rn\n" +
                "                                from kenyaemr_etl.etl_mch_antenatal_visit anc cross join\n" +
                "                                         (select @v := -1, @rn := 0) params\n" +
                "                                order by anc.patient_id,anc.visit_date desc\n" +
                "                               ) t where rn=1 group by t.patient_id) z\n" +
                "                           join\n" +
                "                             (select x.patient_id,x.visit_date,x.cacx_screening\n" +
                "                              from (select anc.*,\n" +
                "                                           (@rn := if(@v = patient_id, @rn + 1,\n" +
                "                                                      if(@v := patient_id, 1, 1)\n" +
                "                                               )\n" +
                "                                               ) as rn\n" +
                "                                    from kenyaemr_etl.etl_mch_antenatal_visit anc cross join\n" +
                "                                             (select @v := -1, @rn := 0) params\n" +
                "                                        order by anc.patient_id, anc.visit_date desc\n" +
                "                                   ) x\n" +
                "                              where rn=2 )y on y.patient_id = z.patient_id\n" +
                "where z.cacx_screening = 664 and y.cacx_screening =664\n" +
                "group by z.patient_id\n" +
                "union all\n" +
                "select z.patient_id from\n" +
                "                         (select t.patient_id,t.visit_date,t.cacx_screening\n" +
                "                          from (select pnc.*,\n" +
                "                                       (@rn := if(@v = patient_id, @rn + 1,\n" +
                "                                                  if(@v := patient_id, 1, 1)\n" +
                "                                           )\n" +
                "                                           ) as rn\n" +
                "                                from kenyaemr_etl.etl_mch_postnatal_visit pnc cross join\n" +
                "                                         (select @v := -1, @rn := 0) params\n" +
                "                                order by pnc.patient_id,pnc.visit_date desc\n" +
                "                               ) t where rn=1 group by t.patient_id) z\n" +
                "                           join\n" +
                "                             (select x.patient_id,x.visit_date,x.cacx_screening\n" +
                "                              from (select pnc.*,\n" +
                "                                           (@rn := if(@v = patient_id, @rn + 1,\n" +
                "                                                      if(@v := patient_id, 1, 1)\n" +
                "                                               )\n" +
                "                                               ) as rn\n" +
                "                                    from kenyaemr_etl.etl_mch_postnatal_visit pnc cross join\n" +
                "                                             (select @v := -1, @rn := 0) params\n" +
                "                                    order by pnc.patient_id, pnc.visit_date desc\n" +
                "                                   ) x\n" +
                "                              where rn=2 )y on y.patient_id = z.patient_id\n" +
                "where z.cacx_screening = 664 and y.cacx_screening =664\n" +
                "group by z.patient_id)cacx on t.patient_id = cacx.patient_id;";

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
                "                                    where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                    group by patient_id\n" +
                "                                   ) d on d.patient_id = fup.patient_id\n" +
                "                          group by patient_id\n" +
                "                          having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "                              ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) ))\n" +
                "                              )\n" +
                "                         ) t\n" +
                "                           join\n" +
                "                             (select z.patient_id from\n" +
                "                                                       (select t.patient_id,t.visit_date,t.cacx_screening\n" +
                "                                                        from (select fup.*,\n" +
                "                                                                     (@rn := if(@v = patient_id, @rn + 1,\n" +
                "                                                                                if(@v := patient_id, 1, 1)\n" +
                "                                                                         )\n" +
                "                                                                         ) as rn\n" +
                "                                                              from kenyaemr_etl.etl_patient_hiv_followup fup cross join\n" +
                "                                                                       (select @v := -1, @rn := 0) params\n" +
                "                                                              where fup.voided = 0\n" +
                "                                                              order by fup.patient_id,fup.visit_date desc\n" +
                "                                                             ) t where rn=1 group by t.patient_id) z\n" +
                "                                                         join\n" +
                "                                                           (select x.patient_id,x.visit_date,x.cacx_screening\n" +
                "                                                            from (select fup.*,\n" +
                "                                                                         (@rn := if(@v = patient_id, @rn + 1,\n" +
                "                                                                                    if(@v := patient_id, 1, 1)\n" +
                "                                                                             )\n" +
                "                                                                             ) as rn\n" +
                "                                                                  from kenyaemr_etl.etl_patient_hiv_followup fup cross join\n" +
                "                                                                           (select @v := -1, @rn := 0) params\n" +
                "                                                                  where fup.voided = 0\n" +
                "                                                                  order by fup.patient_id, fup.visit_date desc\n" +
                "                                                                 ) x\n" +
                "                                                            where rn=2 )y on y.patient_id = z.patient_id\n" +
                "                              where z.cacx_screening = 703 and y.cacx_screening = 664\n" +
                "                              group by z.patient_id\n" +
                "                              union all\n" +
                "                              select z.patient_id from\n" +
                "                                                       (select t.patient_id,t.visit_date,t.cacx_screening\n" +
                "                                                        from (select anc.*,\n" +
                "                                                                     (@rn := if(@v = patient_id, @rn + 1,\n" +
                "                                                                                if(@v := patient_id, 1, 1)\n" +
                "                                                                         )\n" +
                "                                                                         ) as rn\n" +
                "                                                              from kenyaemr_etl.etl_mch_antenatal_visit anc cross join\n" +
                "                                                                       (select @v := -1, @rn := 0) params\n" +
                "                                                              order by anc.patient_id,anc.visit_date desc\n" +
                "                                                             ) t where rn=1 group by t.patient_id) z\n" +
                "                                                         join\n" +
                "                                                           (select x.patient_id,x.visit_date,x.cacx_screening\n" +
                "                                                            from (select anc.*,\n" +
                "                                                                         (@rn := if(@v = patient_id, @rn + 1,\n" +
                "                                                                                    if(@v := patient_id, 1, 1)\n" +
                "                                                                             )\n" +
                "                                                                             ) as rn\n" +
                "                                                                  from kenyaemr_etl.etl_mch_antenatal_visit anc cross join\n" +
                "                                                                           (select @v := -1, @rn := 0) params\n" +
                "                                                                  order by anc.patient_id, anc.visit_date desc\n" +
                "                                                                 ) x\n" +
                "                                                            where rn=2 )y on y.patient_id = z.patient_id\n" +
                "                              where z.cacx_screening = 703 and y.cacx_screening =664\n" +
                "                              group by z.patient_id\n" +
                "                              union all\n" +
                "                              select z.patient_id from\n" +
                "                                                       (select t.patient_id,t.visit_date,t.cacx_screening\n" +
                "                                                        from (select pnc.*,\n" +
                "                                                                     (@rn := if(@v = patient_id, @rn + 1,\n" +
                "                                                                                if(@v := patient_id, 1, 1)\n" +
                "                                                                         )\n" +
                "                                                                         ) as rn\n" +
                "                                                              from kenyaemr_etl.etl_mch_postnatal_visit pnc cross join\n" +
                "                                                                       (select @v := -1, @rn := 0) params\n" +
                "                                                              order by pnc.patient_id,pnc.visit_date desc\n" +
                "                                                             ) t where rn=1 group by t.patient_id) z\n" +
                "                                                         join\n" +
                "                                                           (select x.patient_id,x.visit_date,x.cacx_screening\n" +
                "                                                            from (select pnc.*,\n" +
                "                                                                         (@rn := if(@v = patient_id, @rn + 1,\n" +
                "                                                                                    if(@v := patient_id, 1, 1)\n" +
                "                                                                             )\n" +
                "                                                                             ) as rn\n" +
                "                                                                  from kenyaemr_etl.etl_mch_postnatal_visit pnc cross join\n" +
                "                                                                           (select @v := -1, @rn := 0) params\n" +
                "                                                                  order by pnc.patient_id, pnc.visit_date desc\n" +
                "                                                                 ) x\n" +
                "                                                            where rn=2 )y on y.patient_id = z.patient_id\n" +
                "                              where z.cacx_screening = 703 and y.cacx_screening =664\n" +
                "                              group by z.patient_id)cacx on t.patient_id = cacx.patient_id;\n";

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
                "                                    where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                    group by patient_id\n" +
                "                                   ) d on d.patient_id = fup.patient_id\n" +
                "                          group by patient_id\n" +
                "                          having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "                              ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) ))\n" +
                "                              )\n" +
                "                         ) t\n" +
                "                           join\n" +
                "                             (select z.patient_id from\n" +
                "                                                       (select t.patient_id,t.visit_date,t.cacx_screening\n" +
                "                                                        from (select fup.*,\n" +
                "                                                                     (@rn := if(@v = patient_id, @rn + 1,\n" +
                "                                                                                if(@v := patient_id, 1, 1)\n" +
                "                                                                         )\n" +
                "                                                                         ) as rn\n" +
                "                                                              from kenyaemr_etl.etl_patient_hiv_followup fup cross join\n" +
                "                                                                       (select @v := -1, @rn := 0) params\n" +
                "                                                              where fup.voided = 0\n" +
                "                                                              order by fup.patient_id,fup.visit_date desc\n" +
                "                                                             ) t where rn=1 group by t.patient_id) z\n" +
                "                                                         join\n" +
                "                                                           (select x.patient_id,x.visit_date,x.cacx_screening\n" +
                "                                                            from (select fup.*,\n" +
                "                                                                         (@rn := if(@v = patient_id, @rn + 1,\n" +
                "                                                                                    if(@v := patient_id, 1, 1)\n" +
                "                                                                             )\n" +
                "                                                                             ) as rn\n" +
                "                                                                  from kenyaemr_etl.etl_patient_hiv_followup fup cross join\n" +
                "                                                                           (select @v := -1, @rn := 0) params\n" +
                "                                                                  where fup.voided = 0\n" +
                "                                                                  order by fup.patient_id, fup.visit_date desc\n" +
                "                                                                 ) x\n" +
                "                                                            where rn=2 )y on y.patient_id = z.patient_id\n" +
                "                              where z.cacx_screening = 159393 and y.cacx_screening = 664\n" +
                "                              group by z.patient_id\n" +
                "                              union all\n" +
                "                              select z.patient_id from\n" +
                "                                                       (select t.patient_id,t.visit_date,t.cacx_screening\n" +
                "                                                        from (select anc.*,\n" +
                "                                                                     (@rn := if(@v = patient_id, @rn + 1,\n" +
                "                                                                                if(@v := patient_id, 1, 1)\n" +
                "                                                                         )\n" +
                "                                                                         ) as rn\n" +
                "                                                              from kenyaemr_etl.etl_mch_antenatal_visit anc cross join\n" +
                "                                                                       (select @v := -1, @rn := 0) params\n" +
                "                                                              order by anc.patient_id,anc.visit_date desc\n" +
                "                                                             ) t where rn=1 group by t.patient_id) z\n" +
                "                                                         join\n" +
                "                                                           (select x.patient_id,x.visit_date,x.cacx_screening\n" +
                "                                                            from (select anc.*,\n" +
                "                                                                         (@rn := if(@v = patient_id, @rn + 1,\n" +
                "                                                                                    if(@v := patient_id, 1, 1)\n" +
                "                                                                             )\n" +
                "                                                                             ) as rn\n" +
                "                                                                  from kenyaemr_etl.etl_mch_antenatal_visit anc cross join\n" +
                "                                                                           (select @v := -1, @rn := 0) params\n" +
                "                                                                  order by anc.patient_id, anc.visit_date desc\n" +
                "                                                                 ) x\n" +
                "                                                            where rn=2 )y on y.patient_id = z.patient_id\n" +
                "                              where z.cacx_screening = 159393 and y.cacx_screening =664\n" +
                "                              group by z.patient_id\n" +
                "                              union all\n" +
                "                              select z.patient_id from\n" +
                "                                                       (select t.patient_id,t.visit_date,t.cacx_screening\n" +
                "                                                        from (select pnc.*,\n" +
                "                                                                     (@rn := if(@v = patient_id, @rn + 1,\n" +
                "                                                                                if(@v := patient_id, 1, 1)\n" +
                "                                                                         )\n" +
                "                                                                         ) as rn\n" +
                "                                                              from kenyaemr_etl.etl_mch_postnatal_visit pnc cross join\n" +
                "                                                                       (select @v := -1, @rn := 0) params\n" +
                "                                                              order by pnc.patient_id,pnc.visit_date desc\n" +
                "                                                             ) t where rn=1 group by t.patient_id) z\n" +
                "                                                         join\n" +
                "                                                           (select x.patient_id,x.visit_date,x.cacx_screening\n" +
                "                                                            from (select pnc.*,\n" +
                "                                                                         (@rn := if(@v = patient_id, @rn + 1,\n" +
                "                                                                                    if(@v := patient_id, 1, 1)\n" +
                "                                                                             )\n" +
                "                                                                             ) as rn\n" +
                "                                                                  from kenyaemr_etl.etl_mch_postnatal_visit pnc cross join\n" +
                "                                                                           (select @v := -1, @rn := 0) params\n" +
                "                                                                  order by pnc.patient_id, pnc.visit_date desc\n" +
                "                                                                 ) x\n" +
                "                                                            where rn=2 )y on y.patient_id = z.patient_id\n" +
                "                              where z.cacx_screening = 159393 and y.cacx_screening =664\n" +
                "                              group by z.patient_id)cacx on t.patient_id = cacx.patient_id;\n";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("rescreenedCXCAPresumed");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive women on ART with Presumed cervical cancer during re-screening");
        return cd;

    }
    public CohortDefinition infantsTurnedHIVPositiveOnART() {

        String sqlQuery = "select t.patient_id from (select e.patient_id,timestampdiff(MONTH,d.dob,max(f.dna_pcr_sample_date)) months,f.dna_pcr_results_date results_date,e.exit_date exit_date,f.dna_pcr_contextual_status test_type from kenyaemr_etl.etl_hei_enrollment e inner join\n" +
                "                                                                                                                                                                                                                                 kenyaemr_etl.etl_patient_demographics d on e.patient_id = d.patient_id\n" +
                "                                                                                                                                                                                                                                                               inner join kenyaemr_etl.etl_hei_follow_up_visit f on e.patient_id = f.patient_id\n" +
                "                                       inner join (select net.patient_id\n" +
                "                                                   from (\n" +
                "                                                        select e.patient_id,e.date_started,\n" +
                "                                                               e.gender,\n" +
                "                                                               e.dob,\n" +
                "                                                               d.visit_date as dis_date,\n" +
                "                                                               if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "                                                               e.regimen, e.regimen_line, e.alternative_regimen,\n" +
                "                                                               mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                                                               max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art,\n" +
                "                                                               max(if(enr.transfer_in_date is not null, 1, 0)) as TIn,\n" +
                "                                                               max(fup.visit_date) as latest_vis_date\n" +
                "                                                        from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started,\n" +
                "                                                                     mid(min(concat(e.date_started,e.regimen_name)),11) as regimen,\n" +
                "                                                                     mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line,\n" +
                "                                                                     max(if(discontinued,1,0))as alternative_regimen\n" +
                "                                                              from kenyaemr_etl.etl_drug_event e\n" +
                "                                                                     join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "                                                              where e.program = 'HIV'\n" +
                "                                                              group by e.patient_id) e\n" +
                "                                                               left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_name='HIV'\n" +
                "                                                               left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "                                                               left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "                                                              group by e.patient_id\n" +
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
    public CohortDefinition newOnARTTBInfected() {

        String sqlQuery = "select tb.patient_id from kenyaemr_etl.etl_tb_enrollment tb\n" +
                "                            join\n" +
                "                              (select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                                      max(fup.visit_date) as latest_vis_date,\n" +
                "                                      mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                                      max(d.visit_date) as date_discontinued,\n" +
                "                                      d.patient_id as disc_patient,\n" +
                "                                      de.patient_id as started_on_drugs\n" +
                "                               from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                                      join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                                      join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                                      left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program in ('HIV') and date(date_started) between date(:startDate) and date(:endDate)\n" +
                "                                      left outer JOIN\n" +
                "                                        (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                         where date(visit_date) <= date(:endDate) and program_name in ('HIV','TB')\n" +
                "                                         group by patient_id\n" +
                "                                        ) d on d.patient_id = fup.patient_id\n" +
                "                               group by patient_id\n" +
                "                               having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "                                   ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) ))\n" +
                "                                   )\n" +
                "                              ) t on tb.patient_id = t.patient_id\n" +
                "group by tb.patient_id\n" +
                "having min(tb.visit_date) < min(t.enroll_date);";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("newOnARTTBInfected");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("TB patients new on ART");
        return cd;

    }
    public CohortDefinition alreadyOnARTTBInfected() {

        String sqlQuery = "select tb.patient_id from kenyaemr_etl.etl_tb_enrollment tb\n" +
                " join\n" +
                "(select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "       max(fup.visit_date) as latest_vis_date,\n" +
                "       mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "       max(d.visit_date) as date_discontinued,\n" +
                "       d.patient_id as disc_patient,\n" +
                "       de.patient_id as started_on_drugs\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "       join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "       join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "       left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program in ('HIV') and date(date_started) <= date(:startDate)\n" +
                "       left outer JOIN\n" +
                "         (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "          where date(visit_date) <= date(:endDate) and program_name in ('HIV','TB')\n" +
                "          group by patient_id\n" +
                "         ) d on d.patient_id = fup.patient_id\n" +
                "group by patient_id\n" +
                "having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "    ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) ))\n" +
                "    )\n" +
                "    ) t on tb.patient_id = t.patient_id\n" +
                "   group by tb.patient_id\n" +
                "   having min(tb.visit_date) < min(t.enroll_date);";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("alreadyOnARTTBInfected");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("TB patients already on ART");
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
    /*PITC Inpatient Services Negative*/
    public CohortDefinition testedNegativeAtPITCInpatientServices() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Negative\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.test_strategy=\"Provider Initiated Testing(PITC)\"\n" +
                "and hts.hts_entry_point=\"In Patient Department(IPD)\"\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTC_TST_Inpatient_Negative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Negative at PITC Inpatient Services");
        return cd;

    }

    /*PITC Inpatient Services Positive*/
    public CohortDefinition testedPositiveAtPITCInpatientServices() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Positive\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.test_strategy =\"Provider Initiated Testing(PITC)\"\n" +
                "and hts.hts_entry_point =\"In Patient Department(IPD)\"\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTC_TST_Inpatient_Positive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive at PITC Inpatient Services");
        return cd;

    }

    /*PITC Paediatric services Positive <5*/
    public CohortDefinition testedPositiveAtPITCPaediatricServices() {

        String sqlQuery = "select  hts.patient_id from kenyaemr_etl.etl_hts_test hts\n" +
                "inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = hts.patient_id\n" +
                "where hts.final_test_result =\"Positive\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.test_strategy =\"Provider Initiated Testing(PITC)\"\n" +
                "and hts.hts_entry_point =\"Peadiatric Clinic\"\n" +
                "and timestampdiff(year,d.DOB,:endDate)<5\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;\n";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTC_TST_Paediatric_Positive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive at PITC Paediatric Services");
        return cd;

    }

    /*PITC Paediatric services Negative <5*/
    public CohortDefinition testedNegativeAtPITCPaediatricServices() {

        String sqlQuery = "select  hts.patient_id from kenyaemr_etl.etl_hts_test hts\n" +
                "inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = hts.patient_id\n" +
                "where hts.final_test_result =\"Negative\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.test_strategy =\"Provider Initiated Testing(PITC)\"\n" +
                "and hts.hts_entry_point =\"Peadiatric Clinic\"\n" +
                "and timestampdiff(year,d.DOB,:endDate)<5\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTC_TST_Paediatric_Negative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Negative at PITC Paediatric Services");
        return cd;

    }

    /*PITC Malnutrition Clinics Negative <5*/
    public CohortDefinition testedNegativeAtPITCMalnutritionClinics() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts\n" +
                "inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = hts.patient_id\n" +
                "where hts.final_test_result =\"Negative\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.test_strategy =\"Provider Initiated Testing(PITC)\"\n" +
                "and hts.hts_entry_point =\"Nutrition Clinic\"\n" +
                "and timestampdiff(year,d.DOB,:endDate)<5\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTC_TST_Malnutrition_Negative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Negative at PITC Malnutrition Clinics");
        return cd;

    }

    /*PITC Malnutrition Clinics Positive <5*/
    public CohortDefinition testedPositiveAtPITCMalnutritionClinics() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts\n" +
                "inner join kenyaemr_etl.etl_patient_demographics d on d.patient_id = hts.patient_id\n" +
                "where hts.final_test_result =\"Positive\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.test_strategy =\"Provider Initiated Testing(PITC)\"\n" +
                "and hts.hts_entry_point =\"Nutrition Clinic\"\n" +
                "and timestampdiff(year,d.DOB,:endDate)<5\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTC_TST_Malnutrition_Positive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive at PITC Malnutrition Clinics");
        return cd;

    }

    /*PITC TB Clinic Negative*/
    public CohortDefinition testedNegativeAtPITCTBClinic() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Negative\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.test_strategy =\"Provider Initiated Testing(PITC)\"\n" +
                "and hts.hts_entry_point =\"TB\"\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTC_TST_TB_Negative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Negative at TB Clinic");
        return cd;

    }

    /*PITC TB Clinic Positive*/
    public CohortDefinition testedPositiveAtPITCTBClinic() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Positive\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.test_strategy =\"Provider Initiated Testing(PITC)\"\n" +
                "and hts.hts_entry_point =\"TB\"\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTC_TST_TB_Positive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive at TB Clinic");
        return cd;

    }

    /*Tested Negative at PITC Other*/
    public CohortDefinition testedNagativeAtPITCOther() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Negative\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.test_strategy =\"Provider Initiated Testing(PITC)\"\n" +
                "and hts.hts_entry_point =\"Other\"\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTC_TST_Other_Negative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Negative at PITC Other");
        return cd;

    }

    /*Tested Positive at PITC Other*/
    public CohortDefinition testedPositiveAtPITCOther() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Positive\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.test_strategy =\"Provider Initiated Testing(PITC)\"\n" +
                "and hts.hts_entry_point =\"Other\"\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTC_TST_Other_Positive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive at PITC Other");
        return cd;

    }

    /*Tested Negative at PITC VCT*/
    public CohortDefinition testedNagativeAtPITCVCT() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Negative\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.test_strategy =\"Provider Initiated Testing(PITC)\"\n" +
                "and hts.hts_entry_point =\"VCT\"\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTC_TST_VCT_Negative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Negative at PITC VCT");
        return cd;

    }

    /*Tested Positive at PITC VCT*/
    public CohortDefinition testedPositiveAtPITCVCT() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Positive\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.test_strategy =\"Provider Initiated Testing(PITC)\"\n" +
                "and hts.hts_entry_point =\"VCT\"\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTC_TST_VCT_Positive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive at PITC VCT");
        return cd;

    }

    /*PITC Index Negative*/
    public CohortDefinition indexTestedNegative() {

        String sqlQuery = "select patient_id from (select c.patient_id\n" +
                "                        from kenyaemr_hiv_testing_patient_contact c inner join kenyaemr_etl.etl_hts_test t on c.patient_id = t.patient_id\n" +
                "                        where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "                          and (t.final_test_result = \"Negative\" and t.visit_date > c.date_created)\n" +
                "                          and t.patient_given_result ='Yes'\n" +
                "                          and t.voided=0 and c.voided = 0 \n" +
                "                          and date(t.visit_date) between date_sub( date(:endDate), INTERVAL  3 MONTH )and date(:endDate)\n" +
                "                        group by c.id ) t;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTC_TST_Index_Negative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Index Tested Negative");
        return cd;

    }

    /*PITC Index Positive*/
    public CohortDefinition indextestedPositive() {

        String sqlQuery = "select patient_id from (select c.patient_id\n" +
                "                        from kenyaemr_hiv_testing_patient_contact c inner join kenyaemr_etl.etl_hts_test t on c.patient_id = t.patient_id\n" +
                "                        where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "                          and (t.final_test_result = \"Positive\" and t.visit_date > c.date_created)\n" +
                "                          and t.patient_given_result ='Yes'\n" +
                "                          and t.voided=0 and c.voided = 0 \n" +
                "                          and date(t.visit_date) between date_sub( date(:endDate), INTERVAL  3 MONTH )and date(:endDate)\n" +
                "                        group by c.id ) t;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTC_TST_Index_Positive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Index Tested Positive");
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
                "            and date(e.date_started) between date_sub(:endDate , interval 3 MONTH) and :endDate\n" +
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
                "            and date(e.date_started) between date_sub(:endDate , interval 3 MONTH) and :endDate\n" +
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
                "     where date(e.date_started) between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate)\n" +
                "       and ((fup.on_anti_tb_drugs =1065 and\n" +
                "             tbenr.visit_date between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate) ))\n" +
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
                "                 from (   \n" +
                "                 select e.patient_id,e.date_started,   \n" +
                "                 e.gender,  \n" +
                "                 e.dob,  \n" +
                "                 d.visit_date as dis_date,   \n" +
                "                 if(d.visit_date is not null, 1, 0) as TOut,  \n" +
                "                 e.regimen, e.regimen_line, e.alternative_regimen,   \n" +
                "                 mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,   \n" +
                "                 max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art,  \n" +
                "                 max(if(enr.transfer_in_date is not null, 1, 0)) as TIn,   \n" +
                "                 max(fup.visit_date) as latest_vis_date  \n" +
                "                 from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started,   \n" +
                "                 mid(min(concat(e.date_started,e.regimen_name)),11) as regimen,   \n" +
                "                 mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line,   \n" +
                "                 max(if(discontinued,1,0))as alternative_regimen   \n" +
                "                 from kenyaemr_etl.etl_drug_event e \n" +
                "                 join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "                 where e.program = 'HIV' \n" +
                "                 group by e.patient_id) e   \n" +
                "                 left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id and d.program_name='HIV'  \n" +
                "                 left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id   \n" +
                "                 left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id   \n" +
                "                 where date(e.date_started) between date_sub(:endDate , interval 3 MONTH) and :endDate \n" +
                "                 group by e.patient_id   \n" +
                "                 having TI_on_art=0  \n" +
                "                 )net;";
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
                "                                                                      and pd.dead = 0 and timestampdiff(month,pd.dob,:endDate) =24\n" +
                "                                                                      and substr(pd.dob,6,2)= substr(:endDate,6,2)\n" +
                "                                                                      and e.hiv_status_at_exit is NULL or e.hiv_status_at_exit not in (\"Positive\",\"Negative\")\n" +
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
    public CohortDefinition returnedToTreatment() {

        String sqlQuery = "select  e.patient_id\n" +
                "    from (\n" +
                "          select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "           max(fup.visit_date) as latest_vis_date,\n" +
                "           mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "           max(d.visit_date) as date_discontinued,\n" +
                "           d.patient_id as disc_patient\n" +
                "          from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "          join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "          join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                  left outer JOIN\n" +
                "          (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "          where date(visit_date) <= date_sub(:startDate, INTERVAL 1 DAY)  and program_name='HIV'\n" +
                "          group by patient_id -- check if this line is necessary\n" +
                "          ) d on d.patient_id = fup.patient_id\n" +
                "          where fup.visit_date <= date_sub(:startDate, INTERVAL 1 DAY)\n" +
                "          group by patient_id\n" +
                "          having (\n" +
                "          (((date(latest_tca) < date_sub(:startDate, INTERVAL 1 DAY)) and (date(latest_vis_date) < date(latest_tca))) ) and ((date(latest_tca) > date(date_discontinued) and date(latest_vis_date) > date(date_discontinued)) or disc_patient is null ) and datediff(date_sub(:startDate, INTERVAL 1 DAY), date(latest_tca)) > 30)\n" +
                "              ) e inner join kenyaemr_etl.etl_patient_hiv_followup r on r.patient_id=e.patient_id and date(r.visit_date) between date(:startDate) and date(:endDate);";

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
    public CohortDefinition pregnantOnARTWithSuppressedRoutineVLLast12Months() {

        String sqlQuery = "select vl.patient_id from\n" +
                "                          (select e.patient_id from kenyaemr_etl.etl_drug_event e\n" +
                "                                                      inner join\n" +
                "                                                        (\n" +
                "                                                        select\n" +
                "                                                               l.patient_id,\n" +
                "                                                               l.visit_date,\n" +
                "                                                               if(l.lab_test = 856, l.test_result, if(l.lab_test=1305 and l.test_result = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                                                               l.urgency\n" +
                "                                                        from kenyaemr_etl.etl_laboratory_extract l\n" +
                "                                                        where l.lab_test in (1305, 856)  and l.visit_date between  date_sub(:endDate , interval 12 MONTH) and  date(:endDate)\n" +
                "                                                                and l.urgency ='ROUTINE'\n" +
                "                                                        ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "\n" +
                "                                                      left JOIN\n" +
                "                                                        (select pd.patient_id, pd.visit_date from kenyaemr_etl.etl_patient_program_discontinuation pd\n" +
                "                                                         where date(visit_date) <= date(:endDate) and pd.program_name='HIV'\n" +
                "                                                         group by pd.patient_id\n" +
                "                                                        ) d on d.patient_id = e.patient_id\n" +
                "                           where e.program = 'HIV'  and date(e.date_started) <= date_sub(:endDate, interval 3 MONTH)\n" +
                "                           group by e.patient_id\n" +
                "                           having mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)=\"LDL\" or mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)<1000) vl\n" +
                "                            inner join\n" +
                "                              (select mid(max(concat(en.visit_date,en.patient_id)),11 )latest_enr\n" +
                "                               from kenyaemr_etl.etl_mch_enrollment en\n" +
                "                                      left outer join (select mid(max(concat(del.visit_date,del.patient_id)),11 )latest_del, max(visit_date) lst_del_visit_date\n" +
                "                                                       from kenyaemr_etl.etl_mchs_delivery del group by del.patient_id) dl on dl.latest_del = en.patient_id\n" +
                "                                      left outer join (select mid(max(concat(pv.visit_date,pv.patient_id)),11 )latest_pv, max(visit_date) lst_pv_visit_date\n" +
                "                                                       from kenyaemr_etl.etl_mch_postnatal_visit pv group by pv.patient_id) psnv on psnv.lst_pv_visit_date = en.patient_id\n" +
                "                               group by en.patient_id)t on t.latest_enr = vl.patient_id\n" +
                "                            left join (select fup.patient_id from kenyaemr_etl.etl_patient_hiv_followup fup where fup.pregnancy_status =1065 and fup.voided = 0\n" +
                "                                       group by fup.patient_id having max(fup.visit_date) between date_sub(:endDate, interval 12 MONTH )\n" +
                "                                                                          and date(:endDate)) f on vl.patient_id = f.patient_id ;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_SUPP_PREGNANT_ROUTINE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Pregnant Women on ART with Suppressed Routine VL within last 12 Months");
        return cd;

    }

    /*Pregnant Women on ART with Suppressed targeted VL within last 12 Months*/
    public CohortDefinition pregnantOnARTWithSuppressedTargetedVLLast12Months() {

        String sqlQuery = "select vl.patient_id from\n" +
                "                          (select e.patient_id from kenyaemr_etl.etl_drug_event e\n" +
                "                                                      inner join\n" +
                "                                                        (\n" +
                "                                                        select\n" +
                "                                                               l.patient_id,\n" +
                "                                                               l.visit_date,\n" +
                "                                                               if(l.lab_test = 856, l.test_result, if(l.lab_test=1305 and l.test_result = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                                                               l.urgency\n" +
                "                                                        from kenyaemr_etl.etl_laboratory_extract l\n" +
                "                                                        where l.lab_test in (1305, 856)  and l.visit_date between  date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "                                                              and l.urgency='IMMEDIATELY'\n" +
                "                                                        ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "\n" +
                "                                                      left JOIN\n" +
                "                                                        (select pd.patient_id, pd.visit_date from kenyaemr_etl.etl_patient_program_discontinuation pd\n" +
                "                                                         where date(visit_date) <= date(:endDate) and pd.program_name='HIV'\n" +
                "                                                         group by pd.patient_id\n" +
                "                                                        ) d on d.patient_id = e.patient_id\n" +
                "                           where e.program = 'HIV'  and date(e.date_started) <= date_sub(:endDate, interval 3 MONTH)\n" +
                "                           group by e.patient_id\n" +
                "                           having mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)=\"LDL\" or mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)<1000) vl\n" +
                "                            inner join\n" +
                "                              (select mid(max(concat(en.visit_date,en.patient_id)),11 )latest_enr\n" +
                "                               from kenyaemr_etl.etl_mch_enrollment en\n" +
                "                                      left outer join (select mid(max(concat(del.visit_date,del.patient_id)),11 )latest_del, max(visit_date) lst_del_visit_date\n" +
                "                                                       from kenyaemr_etl.etl_mchs_delivery del group by del.patient_id) dl on dl.latest_del = en.patient_id\n" +
                "                                      left outer join (select mid(max(concat(pv.visit_date,pv.patient_id)),11 )latest_pv, max(visit_date) lst_pv_visit_date\n" +
                "                                                       from kenyaemr_etl.etl_mch_postnatal_visit pv group by pv.patient_id) psnv on psnv.lst_pv_visit_date = en.patient_id\n" +
                "                               group by en.patient_id)t on t.latest_enr = vl.patient_id\n" +
                "                            left join (select fup.patient_id from kenyaemr_etl.etl_patient_hiv_followup fup where fup.pregnancy_status =1065 and fup.voided = 0\n" +
                "                                       group by fup.patient_id having max(fup.visit_date) between date_sub(:endDate, interval 12 MONTH )\n" +
                "                                                                          and date(:endDate)) f on vl.patient_id = f.patient_id ;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_SUPP_PREGNANT_TARGETED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Pregnant Women on ART with Suppressed targeted VL within last 12 Months");
        return cd;

    }

    /*Pregnant Women on ART with Suppressed undocumented VL within last 12 Months*/
    public CohortDefinition pregnantOnARTWithSuppressedUndocumentedVLLast12Months() {

        String sqlQuery = "select vl.patient_id from\n" +
                "                          (select e.patient_id from kenyaemr_etl.etl_drug_event e\n" +
                "                                                      inner join\n" +
                "                                                        (\n" +
                "                                                        select\n" +
                "                                                               l.patient_id,\n" +
                "                                                               l.visit_date,\n" +
                "                                                               if(l.lab_test = 856, l.test_result, if(l.lab_test=1305 and l.test_result = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                                                               l.urgency\n" +
                "                                                        from kenyaemr_etl.etl_laboratory_extract l\n" +
                "                                                        where l.lab_test in (1305, 856)  and l.visit_date between  date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "                                                                and l.urgency is null or l.urgency not in ('IMMEDIATELY','ROUTINE')\n" +
                "                                                        ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "\n" +
                "                                                      left JOIN\n" +
                "                                                        (select pd.patient_id, pd.visit_date from kenyaemr_etl.etl_patient_program_discontinuation pd\n" +
                "                                                         where date(visit_date) <= date(:endDate) and pd.program_name='HIV'\n" +
                "                                                         group by pd.patient_id\n" +
                "                                                        ) d on d.patient_id = e.patient_id\n" +
                "                           where e.program = 'HIV'  and date(e.date_started) <= date_sub(:endDate, interval 3 MONTH)\n" +
                "                           group by e.patient_id\n" +
                "                           having mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)=\"LDL\" or mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)<1000) vl\n" +
                "                            inner join\n" +
                "                              (select mid(max(concat(en.visit_date,en.patient_id)),11 )latest_enr\n" +
                "                               from kenyaemr_etl.etl_mch_enrollment en\n" +
                "                                      left outer join (select mid(max(concat(del.visit_date,del.patient_id)),11 )latest_del, max(visit_date) lst_del_visit_date\n" +
                "                                                       from kenyaemr_etl.etl_mchs_delivery del group by del.patient_id) dl on dl.latest_del = en.patient_id\n" +
                "                                      left outer join (select mid(max(concat(pv.visit_date,pv.patient_id)),11 )latest_pv, max(visit_date) lst_pv_visit_date\n" +
                "                                                       from kenyaemr_etl.etl_mch_postnatal_visit pv group by pv.patient_id) psnv on psnv.lst_pv_visit_date = en.patient_id\n" +
                "                               group by en.patient_id)t on t.latest_enr = vl.patient_id\n" +
                "                            left join (select fup.patient_id from kenyaemr_etl.etl_patient_hiv_followup fup where fup.pregnancy_status =1065 and fup.voided = 0\n" +
                "                                       group by fup.patient_id having max(fup.visit_date) between date_sub(:endDate, interval 12 MONTH )\n" +
                "                                                                          and date(:endDate)) f on vl.patient_id = f.patient_id ;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_SUPP_PREGNANT_UNDOCUMENTED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Pregnant Women on ART with Suppressed undocumented VL within last 12 Months");
        return cd;

    }
    /*Breastfeeding mother on ART with Suppressed Routine VL within last 12 Months*/
    public CohortDefinition bfOnARTSuppRoutineVL() {
        String sqlQuery = "select e.patient_id\n" +
                "                from kenyaemr_etl.etl_drug_event e\n" +
                "                  inner join\n" +
                "                  (select\n" +
                "                      patient_id,\n" +
                "                      visit_date,\n" +
                "                      if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\",\"\")) as vl_result, urgency\n" +
                "        from kenyaemr_etl.etl_laboratory_extract l where l.visit_date between  date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "        and lab_test in (1305, 856) and urgency = 'ROUTINE'\n" +
                "      ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "      inner join kenyaemr_etl.etl_mch_enrollment mch on mch.patient_id=e.patient_id and mch.date_of_discontinuation is NULL\n" +
                "      inner join kenyaemr_etl.etl_mch_postnatal_visit v on v.patient_id = e.patient_id and v.baby_feeding_method in (5526,6046)\n" +
                "    where e.program = 'HIV' and v.visit_date > mch.visit_date\n" +
                "          and date(e.date_started) <= date_sub(:endDate, interval 3 MONTH)\n" +
                "    group by e.patient_id\n" +
                "    having mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)=\"LDL\" or mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)<1000;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_SUPP_BF_ROUTINE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Breastfeeding mother on ART with Suppressed Routine VL within last 12 Months");
        return cd;
    }
    /*Breastfeeding Mother on ART with Suppressed Targeted VL within last 12 Months*/
    public CohortDefinition bfOnARTSuppTargetedVL() {

        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                "  inner join\n" +
                "  (\n" +
                "    select\n" +
                "      patient_id,\n" +
                "      visit_date,\n" +
                "      if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "      urgency\n" +
                "    from kenyaemr_etl.etl_laboratory_extract l where l.visit_date between  date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "    and lab_test in (1305, 856) and urgency = 'IMMEDIATELY'\n" +
                "  ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "  inner join kenyaemr_etl.etl_mch_enrollment mch on mch.patient_id=e.patient_id and mch.date_of_discontinuation is NULL\n" +
                "  inner join kenyaemr_etl.etl_mch_postnatal_visit v on v.patient_id = e.patient_id and v.baby_feeding_method in (5526,6046)\n" +
                "where e.program = 'HIV' and v.visit_date > mch.visit_date\n" +
                "      and timestampdiff(MONTH , e.date_started, :endDate)>3\n" +
                "group by e.patient_id\n" +
                "having mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)=\"LDL\" or mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)<1000;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_SUPP_BF_TARGETED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Breastfeeding mother on ART with Suppressed Targeted VL within last 12 Months");
        return cd;
    }
    /*Breastfeeding mother on ART with Suppressed undocumented VL within last 12 Months*/
    public CohortDefinition bfOnARTSuppUndocumentedVL() {

        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                "  inner join\n" +
                "  (\n" +
                "    select\n" +
                "      patient_id,\n" +
                "      visit_date,\n" +
                "      if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "      urgency\n" +
                "    from kenyaemr_etl.etl_laboratory_extract l where l.visit_date between  date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "    and lab_test in (1305, 856) and urgency is null or urgency not in ('IMMEDIATELY','ROUTINE')\n" +
                "  ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "  inner join kenyaemr_etl.etl_mch_enrollment mch on mch.patient_id=e.patient_id and mch.date_of_discontinuation is NULL\n" +
                "  inner join kenyaemr_etl.etl_mch_postnatal_visit v on v.patient_id = e.patient_id and v.baby_feeding_method in (5526,6046)\n" +
                "where e.program = 'HIV' and v.visit_date > mch.visit_date\n" +
                "      and timestampdiff(MONTH , e.date_started, :endDate)>3\n" +
                "group by e.patient_id\n" +
                "having mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)=\"LDL\" or mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)<1000;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_SUPP_BF_UNDOCUMENTED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Breastfeeding mother on ART with Suppressed undocumented VL within last 12 Months");
        return cd;

    }
    /*On Routine ART with Suppressed VL within last 12 Months by sex/age*/
    public CohortDefinition onARTSuppRoutineVLBySex() {

        String sqlQuery = "select e.patient_id\n" +
                "                from kenyaemr_etl.etl_drug_event e\n" +
                "                  inner join\n" +
                "                  (\n" +
                "                    select\n" +
                "                      patient_id,\n" +
                "                      visit_date,\n" +
                "                      if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "          urgency\n" +
                "        from kenyaemr_etl.etl_laboratory_extract\n" +
                "       where lab_test in (1305, 856)  and visit_date between  date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "             and urgency = 'ROUTINE'\n" +
                "      ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "\n" +
                "                  left JOIN\n" +
                "                         (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                          where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                          group by patient_id\n" +
                "                         ) d on d.patient_id = e.patient_id\n" +
                "    where e.program = 'HIV'  and date(e.date_started) <= date_sub(:endDate, interval 3 MONTH)\n" +
                "    group by e.patient_id\n" +
                "    having mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)=\"LDL\" or mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)<1000;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_SUPP_ROUTINE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("On ART with Suppressed Routine VL within last 12 Months by sex/age");
        return cd;
    }
    /*On ART with Suppressed Targeted VL within last 12 Months by sex/age*/
    public CohortDefinition onARTSuppTargetedVLBySex() {

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
                "         where lab_test in (1305, 856)  and visit_date between  date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "           and urgency = 'IMMEDIATELY'\n" +
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
        cd.setName("TX_PVLS_SUPP_TARGETED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("On ART with Suppressed Targeted VL within last 12 Months by sex/age");
        return cd;
    }
    /*On ART with Suppressed undocumented VL within last 12 Months by sex/age*/
    public CohortDefinition onARTSuppUndocumentedVLBySex() {

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
                "         where lab_test in (1305, 856)  and visit_date between  date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
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
        cd.setName("TX_PVLS_SUPP_UNDOCUMENTED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients on ART with Suppressed undocumented VL within last 12 Months by sex/age");
        return cd;
    }
    /*On ART with Routine VL within last 12 Months*/
    public CohortDefinition onARTWithRoutineVLLast12Months() {

        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                "inner join kenyaemr_etl.etl_laboratory_extract le on e.patient_id = le.patient_id and le.urgency = 'ROUTINE'\n" +
                "where e.program = 'HIV' and (le.visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and date(:endDate))\n" +
                "and (le.lab_test in (856, 1305))\n" +
                "and timestampdiff(MONTH , e.date_started, :endDate)>3\n" +
                "group by e.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_DENOMINATOR_ROUTINE_ALL");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients on ART with Routine VL within last 12 Months");
        return cd;
    }
    /*On ART with Targeted VL within last 12 Months*/
    public CohortDefinition onARTWithTargetedVLLast12Months() {

        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                "  inner join kenyaemr_etl.etl_laboratory_extract le on e.patient_id = le.patient_id and le.urgency = 'IMMEDIATELY'\n" +
                "where e.program = 'HIV' and (le.visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and date(:endDate))\n" +
                " and (le.lab_test in (856, 1305))\n" +
                " and timestampdiff(MONTH , e.date_started, :endDate)>3\n" +
                "group by e.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_DENOMINATOR_TARGETED_ALL");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients on ART with Targeted VL within last 12 Months");
        return cd;
    }
    /*Patients on ART with undocumented VL within last 12 Months*/
    public CohortDefinition totalOnARTWithUndocumentedVLLast12Months() {

        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                "  inner join kenyaemr_etl.etl_laboratory_extract le on e.patient_id = le.patient_id and le.urgency is null or le.urgency not in ('IMMEDIATELY','ROUTINE')\n" +
                "where e.program = 'HIV' and (le.visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and date(:endDate))\n" +
                "      and (le.lab_test in (856, 1305))\n" +
                "      and timestampdiff(MONTH , e.date_started, :endDate)>3\n" +
                "group by e.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_DENOMINATOR_UNDOCUMENTED_ALL");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients on ART with undocumented VL within last 12 Months");
        return cd;
    }
    /*Pregnant Women on ART with Routine VL within last 12 Months*/
    public CohortDefinition pregnantOnARTWithRoutineVLLast12Months() {

        String sqlQuery = "select vl.patient_id from\n" +
                "                          (select e.patient_id from kenyaemr_etl.etl_drug_event e\n" +
                "                                                      inner join\n" +
                "                                                        (\n" +
                "                                                        select\n" +
                "                                                               l.patient_id,\n" +
                "                                                               l.visit_date,\n" +
                "                                                               if(l.lab_test = 856, l.test_result, if(l.lab_test=1305 and l.test_result = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                                                               l.urgency\n" +
                "                                                        from kenyaemr_etl.etl_laboratory_extract l\n" +
                "                                                        where l.lab_test in (1305, 856)  and l.visit_date between  date_sub(:endDate , interval 12 MONTH) and l.visit_date\n" +
                "                                                                and l.urgency='ROUTINE'\n" +
                "                                                        ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "\n" +
                "                                                      left JOIN\n" +
                "                                                        (select pd.patient_id, pd.visit_date from kenyaemr_etl.etl_patient_program_discontinuation pd\n" +
                "                                                         where date(visit_date) <= date(:endDate) and pd.program_name='HIV'\n" +
                "                                                         group by pd.patient_id\n" +
                "                                                        ) d on d.patient_id = e.patient_id\n" +
                "                           where e.program = 'HIV'  and date(e.date_started) <= date_sub(:endDate, interval 3 MONTH)\n" +
                "                           group by e.patient_id\n" +
                "                          ) vl\n" +
                "                            inner join\n" +
                "                              (select mid(max(concat(en.visit_date,en.patient_id)),11 )latest_enr\n" +
                "                               from kenyaemr_etl.etl_mch_enrollment en\n" +
                "                                      left outer join (select mid(max(concat(del.visit_date,del.patient_id)),11 )latest_del, max(visit_date) lst_del_visit_date\n" +
                "                                                       from kenyaemr_etl.etl_mchs_delivery del group by del.patient_id) dl on dl.latest_del = en.patient_id\n" +
                "                                      left outer join (select mid(max(concat(pv.visit_date,pv.patient_id)),11 )latest_pv, max(visit_date) lst_pv_visit_date\n" +
                "                                                       from kenyaemr_etl.etl_mch_postnatal_visit pv group by pv.patient_id) psnv on psnv.lst_pv_visit_date = en.patient_id\n" +
                "                               group by en.patient_id)t on t.latest_enr = vl.patient_id\n" +
                "                            left join (select fup.patient_id from kenyaemr_etl.etl_patient_hiv_followup fup where fup.pregnancy_status =1065 and fup.voided = 0\n" +
                "                                       group by fup.patient_id having max(fup.visit_date) between date_sub(:endDate, interval 12 MONTH )\n" +
                "                                                                          and date(:endDate)) f on vl.patient_id = f.patient_id ;\n";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_DENOMINATOR_PREGNANT_ROUTINE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Pregnant Women on ART with Routine VL within last 12 Months");
        return cd;
    }
    /*Pregnant Women on ART with Targeted VL within last 12 Months*/
    public CohortDefinition pregnantOnARTWithTargetedVLLast12Months() {

        String sqlQuery = "select vl.patient_id from\n" +
                "                          (select e.patient_id from kenyaemr_etl.etl_drug_event e\n" +
                "                                                      inner join\n" +
                "                                                        (\n" +
                "                                                        select\n" +
                "                                                               l.patient_id,\n" +
                "                                                               l.visit_date,\n" +
                "                                                               if(l.lab_test = 856, l.test_result, if(l.lab_test=1305 and l.test_result = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                                                               l.urgency\n" +
                "                                                        from kenyaemr_etl.etl_laboratory_extract l\n" +
                "                                                        where l.lab_test in (1305, 856)  and l.visit_date between  date_sub(:endDate , interval 12 MONTH) and l.visit_date\n" +
                "                                                                and l.urgency ='IMMEDIATELY'\n" +
                "                                                        ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "\n" +
                "                                                      left JOIN\n" +
                "                                                        (select pd.patient_id, pd.visit_date from kenyaemr_etl.etl_patient_program_discontinuation pd\n" +
                "                                                         where date(visit_date) <= date(:endDate) and pd.program_name='HIV'\n" +
                "                                                         group by pd.patient_id\n" +
                "                                                        ) d on d.patient_id = e.patient_id\n" +
                "                           where e.program = 'HIV'  and date(e.date_started) <= date_sub(:endDate, interval 3 MONTH)\n" +
                "                           group by e.patient_id\n" +
                "                          ) vl\n" +
                "                            inner join\n" +
                "                              (select mid(max(concat(en.visit_date,en.patient_id)),11 )latest_enr\n" +
                "                               from kenyaemr_etl.etl_mch_enrollment en\n" +
                "                                      left outer join (select mid(max(concat(del.visit_date,del.patient_id)),11 )latest_del, max(visit_date) lst_del_visit_date\n" +
                "                                                       from kenyaemr_etl.etl_mchs_delivery del group by del.patient_id) dl on dl.latest_del = en.patient_id\n" +
                "                                      left outer join (select mid(max(concat(pv.visit_date,pv.patient_id)),11 )latest_pv, max(visit_date) lst_pv_visit_date\n" +
                "                                                       from kenyaemr_etl.etl_mch_postnatal_visit pv group by pv.patient_id) psnv on psnv.lst_pv_visit_date = en.patient_id\n" +
                "                               group by en.patient_id)t on t.latest_enr = vl.patient_id\n" +
                "                            left join (select fup.patient_id from kenyaemr_etl.etl_patient_hiv_followup fup where fup.pregnancy_status =1065 and fup.voided = 0\n" +
                "                                       group by fup.patient_id having max(fup.visit_date) between date_sub(:endDate, interval 12 MONTH )\n" +
                "                                                                          and date(:endDate)) f on vl.patient_id = f.patient_id ;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_DENOMINATOR_PREGNANT_TARGETED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Pregnant Women on ART with Targeted VL within last 12 Months");
        return cd;
    }
    /*Pregnant Women on ART with Undocumented VL within last 12 Months*/
    public CohortDefinition pregnantARTWithUndocumentedVLLast12Months() {

        String sqlQuery = "select vl.patient_id from\n" +
                "                          (select e.patient_id from kenyaemr_etl.etl_drug_event e\n" +
                "                                                      inner join\n" +
                "                                                        (\n" +
                "                                                        select\n" +
                "                                                               l.patient_id,\n" +
                "                                                               l.visit_date,\n" +
                "                                                               if(l.lab_test = 856, l.test_result, if(l.lab_test=1305 and l.test_result = 1302, \"LDL\",\"\")) as vl_result,\n" +
                "                                                               l.urgency\n" +
                "                                                        from kenyaemr_etl.etl_laboratory_extract l\n" +
                "                                                        where l.lab_test in (1305, 856)  and l.visit_date between  date_sub(:endDate , interval 12 MONTH) and l.visit_date\n" +
                "                                                                and l.urgency is null or l.urgency not in ('IMMEDIATELY','ROUTINE')\n" +
                "                                                        ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "\n" +
                "                                                      left JOIN\n" +
                "                                                        (select pd.patient_id, pd.visit_date from kenyaemr_etl.etl_patient_program_discontinuation pd\n" +
                "                                                         where date(visit_date) <= date(:endDate) and pd.program_name='HIV'\n" +
                "                                                         group by pd.patient_id\n" +
                "                                                        ) d on d.patient_id = e.patient_id\n" +
                "                           where e.program = 'HIV'  and date(e.date_started) <= date_sub(:endDate, interval 3 MONTH)\n" +
                "                           group by e.patient_id\n" +
                "                          ) vl\n" +
                "                            inner join\n" +
                "                              (select mid(max(concat(en.visit_date,en.patient_id)),11 )latest_enr\n" +
                "                               from kenyaemr_etl.etl_mch_enrollment en\n" +
                "                                      left outer join (select mid(max(concat(del.visit_date,del.patient_id)),11 )latest_del, max(visit_date) lst_del_visit_date\n" +
                "                                                       from kenyaemr_etl.etl_mchs_delivery del group by del.patient_id) dl on dl.latest_del = en.patient_id\n" +
                "                                      left outer join (select mid(max(concat(pv.visit_date,pv.patient_id)),11 )latest_pv, max(visit_date) lst_pv_visit_date\n" +
                "                                                       from kenyaemr_etl.etl_mch_postnatal_visit pv group by pv.patient_id) psnv on psnv.lst_pv_visit_date = en.patient_id\n" +
                "                               group by en.patient_id)t on t.latest_enr = vl.patient_id\n" +
                "                            left join (select fup.patient_id from kenyaemr_etl.etl_patient_hiv_followup fup where fup.pregnancy_status =1065 and fup.voided = 0\n" +
                "                                       group by fup.patient_id having max(fup.visit_date) between date_sub(:endDate, interval 12 MONTH )\n" +
                "                                                                          and date(:endDate)) f on vl.patient_id = f.patient_id ;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_DENOMINATOR_PREGNANT_UNDOCUMENTED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Pregnant Women on ART with Undocumented VL within last 12 Months");
        return cd;
    }
    /*Breastfeeding mother on ART with Routine VL within last 12 Months*/
    public CohortDefinition breastfeedingOnARTWithRoutineVLLast12Months() {

        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                "       inner join\n" +
                "         (select\n" +
                "                 patient_id,\n" +
                "                 visit_date,\n" +
                "                 if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\",\"\")) as vl_result, urgency\n" +
                "          from kenyaemr_etl.etl_laboratory_extract l where l.visit_date between  date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "                                                       and lab_test in (1305, 856) and urgency = 'ROUTINE'\n" +
                "         ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "       inner join kenyaemr_etl.etl_mch_enrollment mch on mch.patient_id=e.patient_id and mch.date_of_discontinuation is NULL\n" +
                "       inner join kenyaemr_etl.etl_mch_postnatal_visit v on v.patient_id = e.patient_id and v.baby_feeding_method in (5526,6046)\n" +
                "where e.program = 'HIV' and v.visit_date > mch.visit_date\n" +
                "  and date(e.date_started) <= date_sub(:endDate, interval 3 MONTH)\n" +
                "group by e.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_DENOMINATOR_BF_ROUTINE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Breastfeeding Women on ART with Routine VL within last 12 Months");
        return cd;
    }
    /*Breastfeeding Mother on ART with Targeted VL within last 12 Months*/
    public CohortDefinition breastfeedingOnARTWithTargetedVLLast12Months() {

        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                "       inner join\n" +
                "         (select\n" +
                "                 patient_id,\n" +
                "                 visit_date,\n" +
                "                 if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\",\"\")) as vl_result, urgency\n" +
                "          from kenyaemr_etl.etl_laboratory_extract l where l.visit_date between  date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "                                                       and lab_test in (1305, 856) and urgency = 'IMMEDIATELY'\n" +
                "         ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "       inner join kenyaemr_etl.etl_mch_enrollment mch on mch.patient_id=e.patient_id and mch.date_of_discontinuation is NULL\n" +
                "       inner join kenyaemr_etl.etl_mch_postnatal_visit v on v.patient_id = e.patient_id and v.baby_feeding_method in (5526,6046)\n" +
                "where e.program = 'HIV' and v.visit_date > mch.visit_date\n" +
                "  and date(e.date_started) <= date_sub(:endDate, interval 3 MONTH)\n" +
                "group by e.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_DENOMINATOR_BF_TARGETED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Breastfeeding Women on ART with Targeted VL within last 12 Months");
        return cd;
    }
    /*Breastfeeding Mother on ART with Undocumented VL within last 12 Months*/
    public CohortDefinition breastfeedingOnARTWithUndocumentedVLLast12Months() {

        String sqlQuery = "select e.patient_id\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                "       inner join\n" +
                "         (select\n" +
                "                 patient_id,\n" +
                "                 visit_date,\n" +
                "                 if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\",\"\")) as vl_result, urgency\n" +
                "          from kenyaemr_etl.etl_laboratory_extract l where l.visit_date between  date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "                                                       and lab_test in (1305, 856) and urgency is null or urgency NOT IN ('ROUTINE','IMMEDIATELY')\n" +
                "         ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "       inner join kenyaemr_etl.etl_mch_enrollment mch on mch.patient_id=e.patient_id and mch.date_of_discontinuation is NULL\n" +
                "       inner join kenyaemr_etl.etl_mch_postnatal_visit v on v.patient_id = e.patient_id and v.baby_feeding_method in (5526,6046)\n" +
                "where e.program = 'HIV' and v.visit_date > mch.visit_date\n" +
                "  and date(e.date_started) <= date_sub(:endDate, interval 3 MONTH)\n" +
                "group by e.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_DENOMINATOR_BF_UNDOCUMENTED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Breastfeeding Women on ART with Undocumented VL within last 12 Months");
        return cd;
    }
    /*On ART with Routine VL within last 12 Months by sex/age*/
    public CohortDefinition onARTWithRoutineVLLast12MonthsByAgeSex() {

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
                "         where lab_test in (1305, 856)  and visit_date between  date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "           and urgency = 'ROUTINE'\n" +
                "         ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "\n" +
                "       left JOIN\n" +
                "         (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "          where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "          group by patient_id\n" +
                "         ) d on d.patient_id = e.patient_id\n" +
                "where e.program = 'HIV'  and date(e.date_started) <= date_sub(:endDate, interval 3 MONTH)\n" +
                "group by e.patient_id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_DENOMINATOR_ROUTINE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("On ART with Routine VL within last 12 Months by sex/age");
        return cd;
    }
    /*Patients on ART with Targeted VL within last 12 Months by sex/age*/
    public CohortDefinition onARTWithTargetedVLLast12MonthsByAgeSex() {

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
                "         where lab_test in (1305, 856)  and visit_date between  date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "           and urgency = 'IMMEDIATELY'\n" +
                "         ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "\n" +
                "       left JOIN\n" +
                "         (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "          where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "          group by patient_id\n" +
                "         ) d on d.patient_id = e.patient_id\n" +
                "where e.program = 'HIV'  and date(e.date_started) <= date_sub(:endDate, interval 3 MONTH)\n" +
                "group by e.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_DENOMINATOR_TARGETED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients on ART with Targeted VL within last 12 Months by sex/age");
        return cd;
    }
    /*Patients on ART with undocumented VL within last 12 Months by sex/age*/
    public CohortDefinition onARTWithUndocumentedVLLast12MonthsByAgeSex() {

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
                "         where lab_test in (1305, 856)  and visit_date between  date_sub(:endDate , interval 12 MONTH) and date(:endDate)\n" +
                "           and urgency is null or urgency not in ('ROUTINE','IMMEDIATELY')\n" +
                "         ) vl_result on vl_result.patient_id = e.patient_id\n" +
                "\n" +
                "       left JOIN\n" +
                "         (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "          where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "          group by patient_id\n" +
                "         ) d on d.patient_id = e.patient_id\n" +
                "where e.program = 'HIV'  and date(e.date_started) <= date_sub(:endDate, interval 3 MONTH)\n" +
                "group by e.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_DENOMINATOR_UNDOCUMENTED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients on ART with undocumented VL within last 12 Months by sex/age");
        return cd;

    }

    /*TX_ML Number of ART patients with no clinical contact since their last expected contact */
    public CohortDefinition onARTMissedAppointment() {

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
    public CohortDefinition onARTMissedAppointmentDied() {

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
                "                                                       and dt.is_final_trace = 1267 and dt.tracing_outcome = 1267;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_ML_DIED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of ART patients with no clinical contact since their last expected contact due to death");
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
                "                                                                   and dt.is_final_trace = 1267 and dt.tracing_outcome = 1267\n" +
                "                                                                   and dt.cause_of_death = 162574;";
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
    public CohortDefinition onARTMissedAppointmentTransferred() {

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
                "     ) e  inner join kenyaemr_etl.etl_ccc_defaulter_tracing dt on dt.patient_id = e.patient_id and dt.true_status = 1693\n" +
                "                                                       and dt.is_final_trace = 1267 and dt.tracing_outcome = 1267;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_ML_PREV_UNDOCUMENTED_TRF");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of ART patients with no clinical contact since their last expected contact due to undocumented transfer");
        return cd;

    }

    /*Number of ART patients with no clinical contact since their last expected contact because they stopped treatment */
    public CohortDefinition onARTMissedAppointmentStoppedTreatment() {

        String sqlQuery = "select  e.patient_id\n" +
                "          from (\n" +
                "               select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                      max(fup.visit_date) as latest_vis_date,\n" +
                "                      mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                      max(d.visit_date) as date_discontinued,\n" +
                "                      d.patient_id as disc_patient\n" +
                "               from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                      join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                      join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                      left outer JOIN\n" +
                "                        (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                         where date(visit_date) <= curdate()  and program_name='HIV'\n" +
                "                         group by patient_id\n" +
                "                        ) d on d.patient_id = fup.patient_id\n" +
                "          \n" +
                "               where fup.visit_date <= :endDate and (:endDate BETWEEN date_sub(:endDate , interval 3 MONTH) and :endDate)\n" +
                "               group by patient_id\n" +
                "               having (\n" +
                "                          (((date(latest_tca) < :endDate) and (date(latest_vis_date) < date(latest_tca))) ) and ((date(latest_tca) > date(date_discontinued) and date(latest_vis_date) > date(date_discontinued)) or disc_patient is null )\n" +
                "                            and timestampdiff(day, date(latest_tca),:endDate) > 28 and latest_tca between date_sub(date(:endDate),interval  6 MONTH) and date(:endDate))\n" +
                "               ) e  inner join kenyaemr_etl.etl_ccc_defaulter_tracing dt on dt.patient_id = e.patient_id and dt.true_status = 164435\n" +
                "                                                                 and dt.is_final_trace = 1267 and dt.tracing_outcome = 1267;";
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
                    "on c.patient_related_to = t.patient_id where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                    "and t.final_test_result = \"Positive\" and t.voided=0 and c.voided = 0 and t.test_type = 2 and c.date_created\n" +
                    "between date_sub( date(:endDate), INTERVAL  3 MONTH )and date(:endDate) group by t.patient_id;";
            SqlCohortDefinition cd = new SqlCohortDefinition();
            cd.setName("HTS_INDEX_OFFERED");
            cd.setQuery(sqlQuery);
            cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
            cd.addParameter(new Parameter("endDate", "End Date", Date.class));
            cd.setDescription("Number of indexes offered Index Services");
            return cd;

        }
    //HTS_INDEX_ELICITED_MALE_CONTACTS_UNDER15
    public CohortDefinition maleContactsUnder15() {

        String sqlQuery = "select c.id from kenyaemr_hiv_testing_patient_contact c\n" +
                "where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "  and c.voided = 0 and c.sex = \"M\" and timestampdiff(YEAR ,date(:endDate),c.birth_date) < 15\n" +
                "  and date(c.date_created) between date_sub( date(:endDate), INTERVAL  3 MONTH )and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_ELICITED_MALE_CONTACTS_UNDER15");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of male contacts under 15 years elicited");
        return cd;

    }

    //HTS_INDEX_ELICITED_MALE_CONTACTS_15+
    public CohortDefinition maleContacts15AndAbove() {

        String sqlQuery = "select c.id from kenyaemr_hiv_testing_patient_contact c\n" +
                "where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "  and c.voided = 0 and c.sex = \"M\" and timestampdiff(YEAR ,date(:endDate),c.birth_date) >= 15\n" +
                "  and date(c.date_created) between date_sub( date(:endDate), INTERVAL  3 MONTH )and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_ELICITED_MALE_CONTACTS_15+");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of male contacts 15+ years elicited");
        return cd;

    }

    //HTS_INDEX_ELICITED_FEMALE_CONTACTS_UNDER15
    public CohortDefinition femaleContactsUnder15() {

        String sqlQuery = "select c.id from kenyaemr_hiv_testing_patient_contact c\n" +
                "where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "  and c.voided = 0 and c.sex = \"F\" and timestampdiff(YEAR ,date(:endDate),c.birth_date) < 15\n" +
                "  and date(c.date_created) between date_sub( date(:endDate), INTERVAL  3 MONTH )and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_ELICITED_FEMALE_CONTACTS_UNDER15");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of female contacts under 15 elicited");
        return cd;

    }

    //HTS_INDEX_ELICITED_FEMALE_CONTACTS_15+
    public CohortDefinition femaleContacts15AndAbove() {

        String sqlQuery = "select c.id from kenyaemr_hiv_testing_patient_contact c\n" +
                "where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "  and c.voided = 0 and c.sex = \"F\" and timestampdiff(YEAR ,date(:endDate),c.birth_date) >= 15\n" +
                "  and date(c.date_created) between date_sub( date(:endDate), INTERVAL  3 MONTH )and date(:endDate);";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_ELICITED_FEMALE_CONTACTS_15+");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of elicited female contacts 15+ years");
        return cd;

    }
    /*HTS_INDEX_ACCEPTED Number of individuals who were offered and accepted index testing services */
    public CohortDefinition acceptedIndexServices() {

        String sqlQuery = "select c.patient_related_to from kenyaemr_hiv_testing_patient_contact c\n" +
                "                                     where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "                                          and c.date_created between date_sub( date(:endDate), INTERVAL  3 MONTH )and date(:endDate) group by c.patient_related_to;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_ACCEPTED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of indexes accepted Index Services");
        return cd;

    }
/*

    //HTS_INDEX_CONTACTS_MALE_POSITIVE_UNDER15 HIV+ male contacts under 15 years
    public CohortDefinition positiveMaleContactsUnder15() {

        String sqlQuery = "select c.id from  kenyaemr_hiv_testing_patient_contact c where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "    and c.baseline_hiv_status = \"Positive\" and c.voided = 0 and c.sex = \"M\" and timestampdiff(YEAR ,date(:endDate),c.birth_date) < 15 and c.date_created\n" +
                "    between date_sub( date(:endDate), INTERVAL  3 MONTH ) and date(:endDate) group by c.id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_CONTACTS_MALE_POSITIVE_UNDER15");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Male Contacts under 15 years and HIV+");
        return cd;

    }

    //HTS_INDEX_CONTACTS_MALE_POSITIVE_OVER15 HIV+ male contacts 15+ years
    public CohortDefinition positiveMaleContactsOver15() {

        String sqlQuery = "select c.id from  kenyaemr_hiv_testing_patient_contact c where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "                        and c.baseline_hiv_status = \"Positive\" and c.voided = 0 and c.sex = \"M\" and timestampdiff(YEAR , date(:endDate), c.birth_date) >= 15 and c.date_created\n" +
                "                        between date_sub( date(:endDate), INTERVAL  3 MONTH ) and date(:endDate) group by c.id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_CONTACTS_MALE_POSITIVE_OVER15");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Male Contacts over 15 years and HIV+");
        return cd;
    }

    //HTS_INDEX_CONTACTS_MALE_NEGATIVE_UNDER15 HIV Negative male contacts under 15 years
    public CohortDefinition negativeMaleContactsUnder15() {

        String sqlQuery = "select c.id from  kenyaemr_hiv_testing_patient_contact c where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "                                                           and c.baseline_hiv_status = \"Negative\" and c.voided = 0 and c.sex = \"M\" and timestampdiff(YEAR , date(:endDate), c.birth_date) < 15 and c.date_created\n" +
                "                                                                   between date_sub( date(:endDate), INTERVAL  3 MONTH ) and date(:endDate) group by c.id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_CONTACTS_MALE_NEGATIVE_UNDER15");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Male Contacts under 15 years and HIV negative");
        return cd;

    }

    //HTS_INDEX_CONTACTS_MALE_NEGATIVE_OVER15 HIV Negative male contacts 15+ years
    public CohortDefinition negativeMaleContactsOver15() {

        String sqlQuery = "select c.id from  kenyaemr_hiv_testing_patient_contact c where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "                        and c.baseline_hiv_status = \"Negative\" and c.voided = 0 and c.sex = \"M\" and timestampdiff(YEAR , date(:endDate), c.birth_date) >= 15 and c.date_created\n" +
                "                        between date_sub( date(:endDate), INTERVAL  3 MONTH ) and date(:endDate) group by c.id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_CONTACTS_MALE_NEGATIVE_OVER15");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Male Contacts over 15 years and HIV negative");
        return cd;

    }

    //HTS_INDEX_CONTACTS_MALE_UNKNOWN_UNDER15 HIV Unknown status male contacts under 15 years
    public CohortDefinition unknownStatusMaleContactsUnder15() {

        String sqlQuery = "select c.id from  kenyaemr_hiv_testing_patient_contact c where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "                                                           and c.baseline_hiv_status in (\"Unknown\",\"Exposed\") and c.voided = 0 and c.sex = \"M\" and timestampdiff(YEAR , date(:endDate), c.birth_date) < 15 and c.date_created\n" +
                "                                                                   between date_sub( date(:endDate), INTERVAL  3 MONTH ) and date(:endDate) group by c.id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_CONTACTS_MALE_UNKNOWN_UNDER15");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Male Contacts under 15 years with Unknown HIV status");
        return cd;

    }

    //HTS_INDEX_CONTACTS_MALE_UNKNOWN_OVER15 HIV Unknown status male contacts Over 15 years
    public CohortDefinition unknownStatusMaleContactsOver15() {

        String sqlQuery = "select c.id from  kenyaemr_hiv_testing_patient_contact c where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "                                                           and c.baseline_hiv_status in (\"Unknown\",\"Exposed\") and c.voided = 0 and c.sex = \"M\" and timestampdiff(YEAR , date(:endDate), c.birth_date) >= 15 and c.date_created\n" +
                "                                                                   between date_sub( date(:endDate), INTERVAL  3 MONTH ) and date(:endDate) group by c.id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_CONTACTS_MALE_UNKNOWN_OVER15");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Male Contacts over 15 years with Unknown HIV status");
        return cd;

    }

    //HTS_INDEX_CONTACTS_FEMALE_POSITIVE_UNDER15 HIV+ female contacts under 15 years
    public CohortDefinition positiveFemaleContactsUnder15() {

        String sqlQuery = "select c.id from  kenyaemr_hiv_testing_patient_contact c where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "                                                           and c.baseline_hiv_status = \"Positive\" and c.voided = 0 and c.sex = \"F\" and timestampdiff(YEAR , date(:endDate), c.birth_date) < 15 and c.date_created\n" +
                "                                                                   between date_sub( date(:endDate), INTERVAL  3 MONTH ) and date(:endDate) group by c.id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_CONTACTS_FEMALE_POSITIVE_UNDER15");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Female Contacts under 15 years and HIV+");
        return cd;

    }

    //HTS_INDEX_CONTACTS_FEMALE_POSITIVE_OVER15 HIV+ female contacts 15+ years
    public CohortDefinition positiveFemaleContactsOver15() {

        String sqlQuery = "select c.id from  kenyaemr_hiv_testing_patient_contact c where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "                                                           and c.baseline_hiv_status = \"Positive\" and c.voided = 0 and c.sex = \"F\" and timestampdiff(YEAR , date(:endDate), c.birth_date) >=15 and c.date_created\n" +
                "                                                                   between date_sub( date(:endDate), INTERVAL  3 MONTH ) and date(:endDate) group by c.id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_CONTACTS_FEMALE_POSITIVE_OVER15");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Female Contacts over 15 years and HIV+");
        return cd;
    }

    //HTS_INDEX_CONTACTS_FEMALE_NEGATIVE_UNDER15 HIV Negative female contacts under 15 years
    public CohortDefinition negativeFemaleContactsUnder15() {

        String sqlQuery = "select c.id from  kenyaemr_hiv_testing_patient_contact c where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "                                                           and c.baseline_hiv_status = \"Negative\" and c.voided = 0 and c.sex = \"F\" and timestampdiff(YEAR , date(:endDate), c.birth_date) < 15 and c.date_created\n" +
                "                                                                   between date_sub( date(:endDate), INTERVAL  3 MONTH ) and date(:endDate) group by c.id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_CONTACTS_FEMALE_NEGATIVE_UNDER15");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Female Contacts under 15 years and HIV negative");
        return cd;

    }

    //HTS_INDEX_CONTACTS_FEMALE_NEGATIVE_OVER15 HIV Negative female contacts 15+ years
    public CohortDefinition negativeFemaleContactsOver15() {

        String sqlQuery = "select c.id from  kenyaemr_hiv_testing_patient_contact c where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "                                                           and c.baseline_hiv_status = \"Negative\" and c.voided = 0 and c.sex = \"F\" and timestampdiff(YEAR , date(:endDate), c.birth_date) >=15 and c.date_created\n" +
                "                                                                   between date_sub( date(:endDate), INTERVAL  3 MONTH ) and date(:endDate) group by c.id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_CONTACTS_FEMALE_NEGATIVE_OVER15");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Female Contacts over 15 years and HIV negative");
        return cd;

    }

    //HTS_INDEX_CONTACTS_FEMALE_UNKNOWN_UNDER15 HIV Unknown status female contacts under 15 years
    public CohortDefinition unknownStatusFemaleContactsUnder15() {

        String sqlQuery = "select c.id from  kenyaemr_hiv_testing_patient_contact c where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "                                                           and c.baseline_hiv_status in (\"Unknown\",\"Exposed\") and c.voided = 0 and c.sex = \"F\" and timestampdiff(YEAR , date(:endDate), c.birth_date) < 15 and c.date_created\n" +
                "                                                                   between date_sub( date(:endDate), INTERVAL  3 MONTH ) and date(:endDate) group by c.id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_CONTACTS_FEMALE_UNKNOWN_UNDER15");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Female Contacts under 15 years with Unknown HIV status");
        return cd;

    }

    //HTS_INDEX_CONTACTS_FEMALE_UNKNOWN_OVER15 HIV Unknown status female contacts 15+ years
    public CohortDefinition unknownStatusFemaleContactsOver15() {

        String sqlQuery = "select c.id from  kenyaemr_hiv_testing_patient_contact c where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "                                                           and c.baseline_hiv_status in (\"Unknown\",\"Exposed\") and c.voided = 0 and c.sex = \"F\" and timestampdiff(YEAR , date(:endDate), c.birth_date) >=15 and c.date_created\n" +
                "                                                                   between date_sub( date(:endDate), INTERVAL  3 MONTH ) and date(:endDate) group by c.id;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX_CONTACTS_FEMALE_UNKNOWN_OVER15");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Female Contacts over 15 years with Unknown HIV status");
        return cd;

    }

    */
/*HTS_INDEX Number of individuals who were identified and tested using Index testing services and received their results *//*

    public CohortDefinition contactIndexTesting() {

        String sqlQuery = "select patient_id from (select c.patient_id\n" +
                "                        from kenyaemr_hiv_testing_patient_contact c inner join kenyaemr_etl.etl_hts_test t on c.patient_id = t.patient_id\n" +
                "                        where c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)\n" +
                "                        and t.patient_given_result ='Yes'\n" +
                "                        and t.voided=0 and c.voided = 0 \n" +
                "                        and date(t.visit_date) between date(:startDate) and date(:endDate)\n" +
                "                        group by c.id ) t;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_INDEX");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of Contacts tested through Index Services");
        return cd;

    }

*/
    //HTS_INDEX_POSITIVE Number of individuals who were tested Positive using Index testing services
    public CohortDefinition hivPositiveContact() {

        String sqlQuery = "select c.patient_id from kenyaemr_hiv_testing_patient_contact c inner join kenyaemr_etl.etl_hts_test t on c.patient_id = t.patient_id\n" +
                "where (c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)) and c.voided = 0 and t.voided =0\n" +
                "group by c.patient_id\n" +
                "  having mid(max(concat(t.visit_date,t.final_test_result)),11) = \"Positive\"\n" +
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

        String sqlQuery = "select c.patient_id from kenyaemr_hiv_testing_patient_contact c inner join kenyaemr_etl.etl_hts_test t on c.patient_id = t.patient_id\n" +
                "where (c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)) and c.voided = 0 and t.voided =0\n" +
                "group by c.patient_id\n" +
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

        String sqlQuery = "select c.patient_id from kenyaemr_hiv_testing_patient_contact c left join kenyaemr_etl.etl_hts_test t on c.patient_id = t.patient_id\n" +
                "left join kenyaemr_hiv_testing_client_trace tr on c.id = tr.client_id\n" +
                "where (c.relationship_type in(971, 972, 1528, 162221, 163565, 970, 5617)) and c.voided = 0 and t.voided =0\n" +
                "group by c.patient_id\n" +
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

    /*Number Tested Negative at PITC PMTCT services ANC-1 only*/
    public CohortDefinition negativeAtPITCPMTCTANC1() {

        String sqlQuery = "select v.patient_id \n" +
                "                from kenyaemr_etl.etl_mch_antenatal_visit v \n" +
                "                    inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = v.patient_id and e.date_of_discontinuation is null \n" +
                "    where v.final_test_result =\"Negative\" and v.anc_visit_number = 1;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_PMTCT_ANC1_Negative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Negative at PITC PMTCT services ANC-1");
        return cd;

    }

    /*Number Tested Positive at PITC PMTCT services ANC-1 only*/
    public CohortDefinition positiveAtPITCPMTCTANC1() {

        String sqlQuery = "select v.patient_id \n" +
                "                from kenyaemr_etl.etl_mch_antenatal_visit v \n" +
                "                    inner join kenyaemr_etl.etl_mch_enrollment e on e.patient_id = v.patient_id and e.date_of_discontinuation is null \n" +
                "    where v.final_test_result =\"Positive\" and v.anc_visit_number = 1;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTS_TST_PMTCT_ANC1_Positive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive at PITC PMTCT services ANC-1");
        return cd;

    }

    /*Number Tested Negative at PITC PMTCT services Post ANC-1 (including labour and delivery and BF)*/
    public CohortDefinition negativeAtPITCPMTCTPostANC1() {

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
        cd.setDescription("Tested Negative at PITC PMTCT services Post ANC-1");
        return cd;

    }

    /*Number Tested Positive at PITC PMTCT services Post ANC-1 (including labour and delivery and BF)*/
    public CohortDefinition positiveAtPITCPMTCTPostANC1() {

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
        cd.setDescription("Tested Positive at PITC PMTCT services Post ANC-1");
        return cd;

    }

    /**
     * Pregnant women currently on ART
     * TX_Curr_Pregnant Datim indicator
     * @return
     */
    public CohortDefinition pregnantCurrentOnArt() {
        SqlCohortDefinition cd = new SqlCohortDefinition();

        String sqlQuery="select patient_id from(\n" +
                "                      select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                             max(fup.visit_date) as latest_vis_date,\n" +
                "                             mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                             max(d.visit_date) as date_discontinued,\n" +
                "                             d.patient_id as disc_patient,\n" +
                "                             de.patient_id as started_on_drugs\n" +
                "                      from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                             join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                             join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                             left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                             left outer JOIN\n" +
                "                               (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                group by patient_id\n" +
                "                               ) d on d.patient_id = fup.patient_id\n" +
                "                             left outer join kenyaemr_etl.etl_mch_enrollment mch on mch.patient_id=de.patient_id\n" +
                "                      where (fup.pregnancy_status = 1065 or mch.visit_date <= date(:endDate)) and fup.visit_date <= date(:endDate)\n" +
                "                      group by patient_id\n" +
                "                      having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "                          ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) ))\n" +
                "                          )\n" +
                "                      ) t;";

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

        String sqlQuery="select patient_id from(\n" +
                "                      select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                             max(fup.visit_date) as latest_vis_date,\n" +
                "                             mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                             max(d.visit_date) as date_discontinued,\n" +
                "                             d.patient_id as disc_patient,\n" +
                "                             de.patient_id as started_on_drugs\n" +
                "                      from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                             join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                             join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                             left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                             left outer JOIN\n" +
                "                               (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                group by patient_id\n" +
                "                               ) d on d.patient_id = fup.patient_id\n" +
                "                             inner join (select mid(max(concat(pv.visit_date,pv.patient_id)),11 )latest_pv, max(visit_date) lst_pv_visit_date, baby_feeding_method\n" +
                "                                         from  kenyaemr_etl.etl_mch_postnatal_visit pv group by pv.patient_id) psnv on psnv.latest_pv = de.patient_id\n" +
                "                      where (de.date_started <= psnv.lst_pv_visit_date and psnv.baby_feeding_method in (5526,6046) and fup.visit_date <= date(:endDate))\n" +
                "                      group by patient_id\n" +
                "                      having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "                          ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) ))\n" +
                "                          )\n" +
                "                      ) t;";

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

        String sqlQuery="select patient_id from(\n" +
                "                      select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                             max(fup.visit_date) as latest_vis_date,\n" +
                "                             mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                             max(d.visit_date) as date_discontinued,\n" +
                "                             d.patient_id as disc_patient,\n" +
                "                             de.patient_id as started_on_drugs\n" +
                "                      from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                             join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                             join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                             left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                             left outer JOIN\n" +
                "                               (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                group by patient_id\n" +
                "                               ) d on d.patient_id = fup.patient_id\n" +
                "                      where fup.visit_date <= date(:endDate) and fup.key_population_type = "+type.getKpTypeConcept()+"\n" +
                "                      group by patient_id\n" +
                "                      having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "                          ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) ))\n" +
                "                          )\n" +
                "                      ) t;";

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
        String sqlQuery="select t.patient_id from(\n" +
                "                      select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,fup.key_population_type as kp,\n" +
                "                             max(fup.visit_date) as latest_vis_date,\n" +
                "                             mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                            timestampdiff(day,max(fup.visit_date),mid(max(concat(fup.visit_date,fup.next_appointment_date)),11)) as days_to_next_appointment,\n" +
                "                             max(d.visit_date) as date_discontinued,\n" +
                "                             d.patient_id as disc_patient,\n" +
                "                             de.patient_id as started_on_drugs\n" +
                "                      from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                             join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                             join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                             left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                             left outer JOIN\n" +
                "                               (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                group by patient_id\n" +
                "                               ) d on d.patient_id = fup.patient_id\n" +
                "                      where fup.visit_date <= date(:endDate)\n" +
                "                      group by patient_id\n" +
                "                      having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "                          ( (disc_patient is null and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate)) or (date(latest_tca) > date(date_discontinued) and date(latest_vis_date)> date(date_discontinued) and date_add(date(latest_tca), interval 30 DAY)  >= date(:endDate) ))\n" +
                "                          )\n" +
                "and timestampdiff(day,max(fup.visit_date),mid(max(concat(fup.visit_date,fup.next_appointment_date)),11)) between "+duration.getDuration()+") t;";

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
                "        having max(date(e.visit_date)) between date_sub(:endDate , interval 6 MONTH) and :endDate;";

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
                "having max(date(e.visit_date)) between date_sub(:endDate , interval 6 MONTH) and :endDate and max(t.visit_date) between max(date(e.visit_date)) and DATE_ADD(max(date(e.visit_date)), INTERVAL 3 MONTH)\n" +
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
                "having max(date(e.visit_date)) between date_sub(:endDate , interval 6 MONTH) and :endDate and max(t.visit_date) between max(date(e.visit_date)) and DATE_ADD(max(date(e.visit_date)), INTERVAL 3 MONTH)\n" +
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

        String sqlQuery = "select e.patient_id from kenyaemr_etl.etl_prep_enrolment e \n" +
                "left join(select d.patient_id,max(date(d.visit_date)) last_disc_date from kenyaemr_etl.etl_prep_discontinuation d ) d on d.patient_id = e.patient_id \n" +
                "where d.patient_id is null \n" +
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
     *Number of beneficiaries served by PEPFAR OVC programs for children and families affected by HIV
     * DATIM_OVC_SERV Datim indicator
     */
    public CohortDefinition beneficiaryOfOVCProgram(){
        CalculationCohortDefinition cd = new CalculationCohortDefinition(new OnOVCProgramCalculation());
        cd.setName("DATIM_OVC_SERV");
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));

        return cd;
    }

    /**
     *  Auto-Calculate Number of ART patients who were screened for TB at least once during the reporting period.
     * Denominator will auto-calculate from Start on ART by Screen Result by Age/Sex.
     * DATIM_TX_TB Datim indicator
     */
    public CohortDefinition artPatientScreenedForTBandResultNegative() {

        String sqlQuery = "\n" +
                "select  e.patient_id\n" +
                "                from (\n" +
                "                         select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                                max(fup.visit_date) as latest_vis_date,\n" +
                "                                mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                                max(d.visit_date) as date_discontinued,\n" +
                "                                d.patient_id as disc_patient,\n" +
                "                                de.patient_id as started_on_drugs\n" +
                "                         from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                                  join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                                  left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                                  inner join kenyaemr_etl.etl_tb_screening tb on tb.patient_id=fup.patient_id\n" +
                "                                  left outer JOIN\n" +
                "                              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                  where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                  group by patient_id\n" +
                "                              ) d on d.patient_id = fup.patient_id\n" +
                "                             where fup.visit_date between date_sub(date(:startDate) , interval 5 MONTH) and date(:endDate) and tb.visit_date between date_sub(:startDate , interval 5 MONTH) and date(:endDate) and tb.resulting_tb_status = 1660\n" +
                "                             group by patient_id\n" +
                "                             having (\n" +
                "                                         (date(latest_tca) > date(:endDate) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )) or\n" +
                "                                         (((date(latest_tca) between date(:startDate) and date(:endDate)) and ((date(latest_vis_date) >= date(latest_tca)) or date(latest_tca) > curdate()) ) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )\n" +
                "                                             ) and started_on_drugs is not null and started_on_drugs <> \"\")\n" +
                "         ) e;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_TB_NEGATIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Auto-Calculate Number of ART patients who were screened for TB at least once during the reporting period");
        return cd;

    }

    /**
     *  Auto-Calculate Number of ART patients who were screened for TB at least once during the reporting period.
     * Denominator will auto-calculate from Start on ART by Screen Result by Age/Sex.
     * DATIM_TX_TB Datim indicator
     */
    public CohortDefinition artPatientScreenedForTBResultPositive() {

        String sqlQuery = "\n" +
                "select  e.patient_id\n" +
                "                from (\n" +
                "                         select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                                max(fup.visit_date) as latest_vis_date,\n" +
                "                                mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                                max(d.visit_date) as date_discontinued,\n" +
                "                                d.patient_id as disc_patient,\n" +
                "                                de.patient_id as started_on_drugs\n" +
                "                         from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                                  join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                                  left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                                  inner join kenyaemr_etl.etl_tb_screening tb on tb.patient_id=fup.patient_id\n" +
                "                                  left outer JOIN\n" +
                "                              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                  where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                  group by patient_id\n" +
                "                              ) d on d.patient_id = fup.patient_id\n" +
                "                             where fup.visit_date between date_sub(date(:startDate) , interval 5 MONTH) and date(:endDate) and tb.visit_date between date_sub(:startDate , interval 5 MONTH) and date(:endDate) and tb.resulting_tb_status in(1662,142177)\n" +
                "                             group by patient_id\n" +
                "                             having (\n" +
                "                                         (date(latest_tca) > date(:endDate) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )) or\n" +
                "                                         (((date(latest_tca) between date(:startDate) and date(:endDate)) and ((date(latest_vis_date) >= date(latest_tca)) or date(latest_tca) > curdate()) ) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )\n" +
                "                                             ) and started_on_drugs is not null and started_on_drugs <> \"\")\n" +
                "         ) e;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_TB_POSITIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Auto-Calculate Number of ART patients who were screened for TB at least once during the reporting period");

        return cd;

    }

    /**
     *  Auto-Calculate Number of ART patients who were screened for TB at least once during the reporting period.
     * Denominator will auto-calculate from Start on ART by Screen Result by Age/Sex.
     * DATIM_TX_TB Datim indicator
     */
    public CohortDefinition previouslyOnArtPatientScreenedForTBResultPositive() {

        String sqlQuery = "\n" +
                "select  e.patient_id\n" +
                "                from (\n" +
                "                         select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                                max(fup.visit_date) as latest_vis_date,\n" +
                "                                mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                                max(d.visit_date) as date_discontinued,\n" +
                "                                d.patient_id as disc_patient,\n" +
                "                                de.patient_id as started_on_drugs\n" +
                "                         from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                                  join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                                  left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                                  inner join kenyaemr_etl.etl_tb_screening tb on tb.patient_id=fup.patient_id\n" +
                "                                  left outer JOIN\n" +
                "                              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                  where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                  group by patient_id\n" +
                "                              ) d on d.patient_id = fup.patient_id\n" +
                "                             where fup.visit_date < date_sub(date(:startDate) , interval 5 MONTH) and tb.visit_date between date_sub(:startDate , interval 5 MONTH) and date(:endDate) and tb.resulting_tb_status = 1660\n" +
                "                             group by patient_id\n" +
                "                             having (\n" +
                "                                         (date(latest_tca) > date(:endDate) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )) or\n" +
                "                                         (((date(latest_tca) between date(:startDate) and date(:endDate)) and ((date(latest_vis_date) >= date(latest_tca)) or date(latest_tca) > curdate()) ) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )\n" +
                "                                             ) and started_on_drugs is not null and started_on_drugs <> \"\")\n" +
                "         ) e;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PREVIOUS_TX_TB_POSITIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Auto-Calculate Number of ART patients who were screened for TB at least once during the reporting period");
        return cd;
    }

    /**
     *  Auto-Calculate Number of ART patients who were screened for TB at least once during the reporting period.
     * Denominator will auto-calculate from Start on ART by Screen Result by Age/Sex.
     * DATIM_TX_TB Datim indicator
     */
    public CohortDefinition previouslyOnArtPatientScreenedForTBandResultNegative() {

        String sqlQuery = "\n" +
                "select  e.patient_id\n" +
                "                from (\n" +
                "                         select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                                max(fup.visit_date) as latest_vis_date,\n" +
                "                                mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                                max(d.visit_date) as date_discontinued,\n" +
                "                                d.patient_id as disc_patient,\n" +
                "                                de.patient_id as started_on_drugs\n" +
                "                         from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                                  join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                                  left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                                  inner join kenyaemr_etl.etl_tb_screening tb on tb.patient_id=fup.patient_id\n" +
                "                                  left outer JOIN\n" +
                "                              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                  where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                  group by patient_id\n" +
                "                              ) d on d.patient_id = fup.patient_id\n" +
                "                             where fup.visit_date < date_sub(date(:startDate) , interval 5 MONTH)  and tb.visit_date between date_sub(:startDate , interval 5 MONTH) and date(:endDate) and tb.resulting_tb_status in(1662,142177)\n" +
                "                             group by patient_id\n" +
                "                             having (\n" +
                "                                         (date(latest_tca) > date(:endDate) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )) or\n" +
                "                                         (((date(latest_tca) between date(:startDate) and date(:endDate)) and ((date(latest_vis_date) >= date(latest_tca)) or date(latest_tca) > curdate()) ) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )\n" +
                "                                             ) and started_on_drugs is not null and started_on_drugs <> \"\")\n" +
                "         ) e;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PREVIOUS_TX_TB_POSITIVE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Auto-Calculate Number of ART patients who were screened for TB at least once during the reporting period");
        return cd;

    }

    // ebrolled to tb
    /**
     *  Auto-Calculate Number of new ART patients who were started on TB treatment during the reporting period.
     * Numerator will autocalculate from Current/New on ART by Age/Sex..
     * TX_TB_NEW(NUMERATOR) (Numerator) Datim indicator
     */
    public CohortDefinition patientNewOnARTEnrolledOnTB_ThisReportingPeriod() {

        String sqlQuery = "\n" +
                "select  e.patient_id\n" +
                "                from (\n" +
                "                         select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                                max(fup.visit_date) as latest_vis_date,\n" +
                "                                mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                                max(d.visit_date) as date_discontinued,\n" +
                "                                d.patient_id as disc_patient,\n" +
                "                                de.patient_id as started_on_drugs\n" +
                "                         from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                                  join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                                  left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                                  inner join kenyaemr_etl.etl_tb_enrollment tb on tb.patient_id=fup.patient_id\n" +
                "                                  left outer JOIN\n" +
                "                              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                  where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                  group by patient_id\n" +
                "                              ) d on d.patient_id = fup.patient_id\n" +
                "                             where fup.visit_date between date_sub(date(:startDate) , interval 5 MONTH) and date(:endDate) and tb.visit_date between date_sub(:startDate , interval 5 MONTH) and date(:endDate) \n" +
                "                             group by patient_id\n" +
                "                             having (\n" +
                "                                         (date(latest_tca) > date(:endDate) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )) or\n" +
                "                                         (((date(latest_tca) between date(:startDate) and date(:endDate)) and ((date(latest_vis_date) >= date(latest_tca)) or date(latest_tca) > curdate()) ) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )\n" +
                "                                             ) and started_on_drugs is not null and started_on_drugs <> \"\")\n" +
                "         ) e;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_TB_NEW(NUMERATOR)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Auto-Calculate Number of ART patients who were screened for TB at least once during the reporting period");
        return cd;

    }


    /**
     *  Auto-Calculate Number of patients previously on ART  who were started on TB treatment during the reporting period.
     * Numerator will autocalculate from Current/prev on ART by Age/Sex..
     * TX_TB_PREV(NUMERATOR) (Numerator) Datim indicator
     */
    public CohortDefinition patientPreviouslyOnART_EnrolledOn_TB_ThisReportingPeriod() {

        String sqlQuery = "\n" +
                "select  e.patient_id\n" +
                "                from (\n" +
                "                         select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                                max(fup.visit_date) as latest_vis_date,\n" +
                "                                mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                                max(d.visit_date) as date_discontinued,\n" +
                "                                d.patient_id as disc_patient,\n" +
                "                                de.patient_id as started_on_drugs\n" +
                "                         from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                                  join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                                  left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                                  inner join kenyaemr_etl.etl_tb_enrollment tb on tb.patient_id=fup.patient_id\n" +
                "                                  left outer JOIN\n" +
                "                              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                  where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                  group by patient_id\n" +
                "                              ) d on d.patient_id = fup.patient_id\n" +
                "                             where fup.visit_date < date_sub(date(:startDate) , interval 5 MONTH) and tb.visit_date between date_sub(:startDate , interval 5 MONTH) and date(:endDate) \n" +
                "                             group by patient_id\n" +
                "                             having (\n" +
                "                                         (date(latest_tca) > date(:endDate) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )) or\n" +
                "                                         (((date(latest_tca) between date(:startDate) and date(:endDate)) and ((date(latest_vis_date) >= date(latest_tca)) or date(latest_tca) > curdate()) ) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )\n" +
                "                                             ) and started_on_drugs is not null and started_on_drugs <> \"\")\n" +
                "         ) e;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_TB_PREV(NUMERATOR)");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Auto-Calculate Number of ART patients who were screened for TB at least once during the reporting period");
        return cd;
    }

    /**
     *  Number of ART patients who had a specimen sent for bacteriologic diagnosis of active TB disease.
     * TX_TB_SPECIMEN Datim indicator
     */
    public CohortDefinition patientsWhoHadSpecimenSentFor_TB_ThisReportingPeriod() {

        String sqlQuery = "\n" +
                "select  e.patient_id\n" +
                "                                from (\n" +
                "                                         select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                                                max(fup.visit_date) as latest_vis_date,\n" +
                "                                                mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                                                max(d.visit_date) as date_discontinued,\n" +
                "                                                d.patient_id as disc_patient,\n" +
                "                                                de.patient_id as started_on_drugs\n" +
                "                                         from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                                                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                                                  join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                                                  left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                                                  inner join kenyaemr_etl.etl_tb_screening tb on tb.patient_id=fup.patient_id\n" +
                "                                                  left outer JOIN\n" +
                "                                              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                                  where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                                  group by patient_id\n" +
                "                                              ) d on d.patient_id = fup.patient_id\n" +
                "                                             where fup.visit_date between date_sub(date(:startDate) , interval 5 MONTH)  and date(:endDate) and tb.visit_date between date_sub(:startDate , interval 5 MONTH) and date(:endDate) and fup.spatum_smear_ordered =1065 and fup.genexpert_ordered=1065\n" +
                "                                             group by patient_id\n" +
                "                                             having (\n" +
                "                                                         (date(latest_tca) > date(:endDate) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )) or\n" +
                "                                                         (((date(latest_tca) between date(:startDate) and date(:endDate)) and ((date(latest_vis_date) >= date(latest_tca)) or date(latest_tca) > curdate()) ) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )\n" +
                "                                                             ) and started_on_drugs is not null and started_on_drugs <> \"\")\n" +
                "             ) e;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_TB_SPECIMEN");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of ART patients who had a specimen sent for bacteriologic diagnosis of active TB disease.");
        return cd;
    }

    /**
     *  Number of ART patients who had a positive result returned for bacteriologic diagnosis of active TB disease.
     * TX_TB_POSITIVE_RESULT_RETURNED Datim indicator
     */
    public CohortDefinition patientsWhoHadPositiveResultForBacteriologicDiagnosis() {

        String sqlQuery = "\n" +
                "select  e.patient_id\n" +
                "                                from (\n" +
                "                                         select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                                                max(fup.visit_date) as latest_vis_date,\n" +
                "                                                mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                                                max(d.visit_date) as date_discontinued,\n" +
                "                                                d.patient_id as disc_patient,\n" +
                "                                                de.patient_id as started_on_drugs\n" +
                "                                         from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                                                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                                                  join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                                                  left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                                                  inner join kenyaemr_etl.etl_tb_screening tb on tb.patient_id=fup.patient_id\n" +
                "                                                  left outer JOIN\n" +
                "                                              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                                  where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                                  group by patient_id\n" +
                "                                              ) d on d.patient_id = fup.patient_id\n" +
                "                                             where fup.visit_date between date_sub(date(:startDate) , interval 5 MONTH)  and date(:endDate) and tb.visit_date between date_sub(:startDate , interval 5 MONTH) and date(:endDate) and fup.spatum_smear_result =703 or fup.genexpert_ordered in (162203,162204,164104)\n" +
                "                                             group by patient_id\n" +
                "                                             having (\n" +
                "                                                         (date(latest_tca) > date(:endDate) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )) or\n" +
                "                                                         (((date(latest_tca) between date(:startDate) and date(:endDate)) and ((date(latest_vis_date) >= date(latest_tca)) or date(latest_tca) > curdate()) ) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )\n" +
                "                                                             ) and started_on_drugs is not null and started_on_drugs <> \"\")\n" +
                "             ) e;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_TB_POSITIVE_RESULT_RETURNED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of ART patients who had a positive result returned for bacteriologic diagnosis of active TB disease.");
        return cd;
    }

    /**
     *  Number of patients whose specimens were sent for  Smear only.
     * TX_TB_SMEAR_SPECIMEN
     */
    public CohortDefinition patientsSpecimenSentForSmearOnly() {

        String sqlQuery = "\n" +
                "select  e.patient_id\n" +
                "                                from (\n" +
                "                                         select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "                                                max(fup.visit_date) as latest_vis_date,\n" +
                "                                                mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "                                                max(d.visit_date) as date_discontinued,\n" +
                "                                                d.patient_id as disc_patient,\n" +
                "                                                de.patient_id as started_on_drugs\n" +
                "                                         from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "                                                  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "                                                  join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "                                                  left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "                                                  inner join kenyaemr_etl.etl_tb_screening tb on tb.patient_id=fup.patient_id\n" +
                "                                                  left outer JOIN\n" +
                "                                              (select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "                                                  where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "                                                  group by patient_id\n" +
                "                                              ) d on d.patient_id = fup.patient_id\n" +
                "                                             where fup.visit_date between date_sub(date(:startDate) , interval 5 MONTH)  and date(:endDate) and tb.visit_date between date_sub(:startDate , interval 5 MONTH) and\n" +
                "                                                 date(:endDate) and fup.spatum_smear_ordered =1065 and fup.genexpert_ordered=1066 and fup.chest_xray_ordered=1066\n" +
                "                                             group by patient_id\n" +
                "                                             having (\n" +
                "                                                         (date(latest_tca) > date(:endDate) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )) or\n" +
                "                                                         (((date(latest_tca) between date(:startDate) and date(:endDate)) and ((date(latest_vis_date) >= date(latest_tca)) or date(latest_tca) > curdate()) ) and\n" +
                "                                                          (date(latest_tca) > date(date_discontinued) or disc_patient is null )\n" +
                "                                                             ) and started_on_drugs is not null and started_on_drugs <> \"\")\n" +
                "             ) e;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_TB_SMEAR_SPECIMEN");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Number of patients whose specimens were sent for  Smear only.");
        return cd;
    }
}


