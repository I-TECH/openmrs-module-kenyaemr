/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.ETLReports.DatimQ4;

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
public class ETLDatimQ4CohortLibrary {
     /**
     * Patients started on ART during the reporting period (last 3 months)
     * TX_New Datim indicator
     * @return
     */
    public  CohortDefinition startedOnART() {
        String sqlQuery="select net.patient_id \n" +
                "from ( \n" +
                "select e.patient_id,e.date_started, \n" +
                "e.gender,\n" +
                "e.dob,\n" +
                "d.visit_date as dis_date, \n" +
                "if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "e.regimen, e.regimen_line, e.alternative_regimen, \n" +
                "mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca, \n" +
                "max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art,\n" +
                "max(if(enr.transfer_in_date is not null, 1, 0)) as TIn, \n" +
                "max(fup.visit_date) as latest_vis_date\n" +
                "from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started, \n" +
                "mid(min(concat(e.date_started,e.regimen_name)),11) as regimen, \n" +
                "mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line, \n" +
                "max(if(discontinued,1,0))as alternative_regimen \n" +
                "from kenyaemr_etl.etl_drug_event e \n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "group by e.patient_id) e \n" +
                "left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id \n" +
                "left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id \n" +
                "left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id \n" +
                "where e.program = 'HIV' and date(e.date_started) between date_sub(:endDate , interval 3 MONTH) and :endDate \n" +
                "group by e.patient_id \n" +
                "having TI_on_art=0\n" +
                ")net;";
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
        String sqlQuery="select net.patient_id \n" +
                "       from ( \n" +
                "       select e.patient_id,e.date_started, \n" +
                "       e.gender,\n" +
                "       e.dob,\n" +
                "       d.visit_date as dis_date, \n" +
                "       if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "       e.regimen, e.regimen_line, e.alternative_regimen, \n" +
                "       fup.visit_date, fup.pregnancy_status,\n" +
                "       mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca, \n" +
                "       max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art,\n" +
                "       max(if(enr.transfer_in_date is not null, 1, 0)) as TIn, \n" +
                "       max(fup.visit_date) as latest_vis_date\n" +
                "       from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started, \n" +
                "       mid(min(concat(e.date_started,e.regimen_name)),11) as regimen, \n" +
                "       mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line, \n" +
                "       max(if(discontinued,1,0))as alternative_regimen \n" +
                "       from kenyaemr_etl.etl_drug_event e \n" +
                "       join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "       group by e.patient_id) e \n" +
                "       left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id \n" +
                "       left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id \n" +
                "       left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id \n" +
                "       where  e.program = 'HIV' and date(e.date_started) between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate) \n" +
                "       and fup.pregnancy_status =1065 and fup.visit_date between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate)\n" +
                "       group by e.patient_id \n" +
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
        String sqlQuery="select net.patient_id \n" +
                "       from ( \n" +
                "       select e.patient_id,e.date_started, \n" +
                "       e.gender,\n" +
                "       e.dob,\n" +
                "       d.visit_date as dis_date, \n" +
                "       if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "       e.regimen, e.regimen_line, e.alternative_regimen, \n" +
                "       fup.visit_date, fup.tb_status,\n" +
                "       mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca, \n" +
                "       max(if(enr.date_started_art_at_transferring_facility is not null and enr.facility_transferred_from is not null, 1, 0)) as TI_on_art,\n" +
                "       max(if(enr.transfer_in_date is not null, 1, 0)) as TIn, \n" +
                "       max(fup.visit_date) as latest_vis_date\n" +
                "       from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started, \n" +
                "       mid(min(concat(e.date_started,e.regimen_name)),11) as regimen, \n" +
                "       mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line, \n" +
                "       max(if(discontinued,1,0))as alternative_regimen \n" +
                "       from kenyaemr_etl.etl_drug_event e \n" +
                "       join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "       group by e.patient_id) e \n" +
                "       left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id \n" +
                "       left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id \n" +
                "       left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id \n" +
                "       left outer join kenyaemr_etl.etl_tb_enrollment tbenr on tbenr.patient_id = e.patient_id\n" +
                "       where  e.program = 'HIV' and date(e.date_started) between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate) \n" +
                "       and ((fup.pregnancy_status =1662 and fup.visit_date between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate)) or \n" +
                "       tbenr.visit_date between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate) )\n" +
                "       group by e.patient_id \n" +
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

        String sqlQuery=" select  e.patient_id\n" +
                "from (\n" +
                "select fup.visit_date,fup.patient_id, min(e.visit_date) as enroll_date,\n" +
                "    max(fup.visit_date) as latest_vis_date,\n" +
                "    mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca,\n" +
                "    max(d.visit_date) as date_discontinued,\n" +
                "    d.patient_id as disc_patient,\n" +
                "  de.patient_id as started_on_drugs\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and date(date_started) <= date(:endDate)\n" +
                "left outer JOIN\n" +
                "(select patient_id, visit_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "group by patient_id\n" +
                ") d on d.patient_id = fup.patient_id\n" +
                "where fup.visit_date <= date(:endDate)\n" +
                "group by patient_id\n" +
                "having (started_on_drugs is not null and started_on_drugs <> \"\") and (\n" +
                "(date(latest_tca) > date(:endDate) and (date(latest_tca) > date(date_discontinued) or disc_patient is null )) or\n" +
                "(((date(latest_tca) between date_sub(date(:endDate) , interval 3 MONTH) and date(:endDate)) and (date(latest_vis_date) >= date(latest_tca))) ) and (date(latest_tca) > date(date_discontinued) or disc_patient is null ))\n" +
                ") e\n" +
                ";";

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

        String sqlQuery = "  select net.patient_id " +
                "  from ( " +
                "  select e.patient_id,e.date_started, e.gender,e.dob,d.visit_date as dis_date, if(d.visit_date is not null, 1, 0) as TOut," +
                "   e.regimen, e.regimen_line, e.alternative_regimen, mid(max(concat(fup.visit_date,fup.next_appointment_date)),11) as latest_tca, "+
                "  if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date" +
                "    from (select e.patient_id,p.dob,p.Gender,min(e.date_started) as date_started, " +
                "    mid(min(concat(e.date_started,e.regimen_name)),11) as regimen, " +
                "    mid(min(concat(e.date_started,e.regimen_line)),11) as regimen_line, " +
                "    max(if(discontinued,1,0))as alternative_regimen " +
                "    from kenyaemr_etl.etl_drug_event e " +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "    group by e.patient_id) e " +
                "    left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id " +
                "    left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id " +
                "    left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id " +
                "    where e.program = 'HIV' and date(e.date_started) between date_sub(:startDate , interval 1 year) and date_sub(:endDate , interval 1 year) " +
                "    group by e.patient_id " +
                "    )net; ";
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

        String sqlQuery = "  select net.patient_id " +
                "  from (" +
                "  select e.patient_id,e.date_started, p.gender,p.dob,d.visit_date as dis_date, if(d.visit_date is not null, 1, 0) as TOut," +
                "  if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date, max(fup.next_appointment_date) as latest_tca" +
                "    from kenyaemr_etl.etl_drug_event e " +
                "    join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id " +
                "    left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id " +
                "    left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id " +
                "    left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id " +
                "    where e.program = 'HIV' and date(e.date_started) between date_sub(:startDate , interval 1 year) and date_sub(:endDate , interval 1 year) " +
                "    group by e.patient_id " +
                "    having   (dis_date>:endDate or dis_date is null) and (datediff(latest_tca,:endDate)<=90))net; ";

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

    /*PMTCT*/
    public CohortDefinition patientHIVPositiveResultsAtANC() {

        String sqlQuery = "select v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit v where v.final_test_result =\"Positive\" and v.visit_date between\n" +
                "    date(:startDate) and date(:endDate) group by v.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("testPositiveResultsANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("HIV Positive Results at ANC");
        return cd;

    }
    public CohortDefinition patientHIVNegativeResultsATANC() {

        String sqlQuery = "select v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit v where v.final_test_result =\"Negative\"and v.visit_date between\n" +
                "date(:startDate) and date(:endDate) group by v.patient_id;";

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
                "        join kenyaemr_etl.etl_mch_antenatal_visit v on e.patient_id = v.patient_id\n" +
                "where (e.hiv_status = 703 or e.hiv_status =664) or v.final_test_result in (\"Negative\",\"Positive\")\n" +
                "and     v.visit_date between\n" +
                "date(:startDate) and date(:endDate)\n" +
                "group by e.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("knownHIVStatusAtANC");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients with Known HIV status at ANC");
        return cd;

    }

    public CohortDefinition newANCClients() {

        String sqlQuery = "select  v.patient_id\n" +
                "from kenyaemr_etl.etl_mch_antenatal_visit v\n" +
                "group by v.patient_id\n" +
                "having mid(min(concat(v.visit_date,v.patient_id)),1,10) between date(:startDate) and date(:endDate);";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("newANCClients");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Clients newly enrolled for ANC");
        return cd;

    }

    public CohortDefinition infantVirologyNegativeResults() {

        String sqlQuery = "select hv.patient_id from kenyaemr_etl.etl_hei_follow_up_visit hv\n" +
                "inner join kenyaemr_etl.etl_patient_demographics de on de.patient_id = hv.patient_id\n" +
                "where and hv.dna_pcr_result=664 and\n" +
                "hv.visit_date between date(:startDate) and date(:endDate) group by hv.patient_id;";

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
                "and hv.dna_pcr_result=703 and hv.visit_date and " +
                "hv.visit_date between\n" +
                "date(:startDate) and date(:endDate) group by hv.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("infantVirologyPositiveResults12m");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infants with Positive Virology test result");
        return cd;

    }

    public CohortDefinition infantVirologyNoResults() {

        String sqlQuery = "select distinct hv.patient_id from kenyaemr_etl.etl_hei_follow_up_visit hv\n" +
                "inner join kenyaemr_etl.etl_patient_demographics de on de.patient_id = hv.patient_id\n" +
                "and hv.dna_pcr_result in (1138,1304)and hv.visit_date between\n" +
                "    date(:startDate) and date(:endDate) group by hv.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("infantVirologyWithNoResults");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Infants with Positive Virology test result");
        return cd;

    }

    public CohortDefinition alreadyOnARTAtBeginningOfPregnacy() {

        String sqlQuery = "select\n" +
                "     e.patient_id\n" +
                "from kenyaemr_etl.etl_mch_enrollment e\n" +
                "       inner join kenyaemr_etl.etl_drug_event d on d.patient_id= e.patient_id\n" +
                "where d.program = 'HIV' and e.date_of_discontinuation is null\n" +
                "  and d.date_started < e.visit_date\n" +
                "  and e.visit_date between  date(:startDate) and date(:endDate)\n" +
                "group by e.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("alreadyOnARTBeforePregancy");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mothers Already on ART at the start of current Pregnancy");
        return cd;

    }

    public CohortDefinition newOnARTDuringPregnancy() {

        String sqlQuery = "select fup.patient_id\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "join (select patient_id from kenyaemr_etl.etl_drug_event e\n" +
                "where e.program = 'HIV' and date_started between date(:startDate) and date(:endDate)) started_art on\n" +
                "started_art.patient_id = fup.patient_id\n" +
                "where fup.pregnancy_status =1065\n" +
                "and fup.visit_date between date(:startDate) and date(:endDate) group by fup.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("newOnARTDuringPregnancy");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Mothers new on ART during current pregnancy");
        return cd;

    }
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

    /*PITC Paediatric services Positive*/
    public CohortDefinition testedPositiveAtPITCPaediatricServices() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Positive\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.test_strategy =\"Provider Initiated Testing(PITC)\"\n" +
                "and hts.hts_entry_point =\"Peadiatric Clinic\"\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTC_TST_Paediatric_Positive");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Positive at PITC Paediatric Services");
        return cd;

    }

    /*PITC Paediatric services Negative*/
    public CohortDefinition testedNegativeAtPITCPaediatricServices() {

        String sqlQuery = "select  hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Negative\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.test_strategy =\"Provider Initiated Testing(PITC)\"\n" +
                "and hts.hts_entry_point =\"Peadiatric Clinic\"\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTC_TST_Paediatric_Negative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Negative at PITC Paediatric Services");
        return cd;

    }

    /*PITC Malnutrition Clinics Negative*/
    public CohortDefinition testedNegativeAtPITCMalnutritionClinics() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Negative\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.test_strategy =\"Provider Initiated Testing(PITC)\"\n" +
                "and hts.hts_entry_point =\"Nutrition Clinic\"\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("HTC_TST_Malnutrition_Negative");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Tested Negative at PITC Malnutrition Clinics");
        return cd;

    }

    /*PITC Malnutrition Clinics Positive*/
    public CohortDefinition testedPositiveAtPITCMalnutritionClinics() {

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts where hts.final_test_result =\"Positive\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.test_strategy =\"Provider Initiated Testing(PITC)\"\n" +
                "and hts.hts_entry_point =\"Nutrition Clinic\"\n" +
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

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts\n" +
                "join openmrs.kenyaemr_hiv_testing_patient_contact pc on pc.patient_related_to = hts.patient_id\n" +
                "where hts.final_test_result =\"Negative\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

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

        String sqlQuery = "select hts.patient_id from kenyaemr_etl.etl_hts_test hts\n" +
                "join openmrs.kenyaemr_hiv_testing_patient_contact pc on pc.patient_related_to = hts.patient_id\n" +
                "where hts.final_test_result =\"Positive\"\n" +
                "and hts.patient_given_result =\"Yes\"\n" +
                "and hts.voided =0 and hts.visit_date between date(:startDate) and date(:endDate) group by hts.patient_id;";

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
        String sqlQuery = "select  fup.patient_id\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "            join (select patient_id from kenyaemr_etl.etl_drug_event e\n" +
                "                  where e.program = 'HIV' and date_started between date(:startDate) and date(:endDate)) started_art on\n" +
                "        started_art.patient_id = fup.patient_id\n" +
                "where fup.pregnancy_status =1065\n" +
                "  and fup.visit_date between date(:startDate) and date(:endDate) group by fup.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_New_Pregnant");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Newly Started ART While Pregnant");
        return cd;

    }

    /*Newly Started ART While BreastFeeding - redo query*/

    public CohortDefinition newlyStartedARTWhileBreastFeeding() {

        String sqlQuery = "select  fup.patient_id\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "join  kenyaemr_etl.etl_drug_event e\n" +
                "on  e.patient_id = fup.patient_id\n" +
                "join  kenyaemr_etl.etl_mch_postnatal_visit pv on pv.patient_id = fup.patient_id\n" +
                "where  pv.baby_feeding_method in (5526,6046)\n" +
                "and e.program = 'HIV' and e.date_started between date(:startDate) and date(:endDate)\n" +
                "and fup.visit_date between date(:startDate) and date(:endDate);";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_New_BF");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Newly Started ART While BreastFeeding");
        return cd;

    }

    /*Newly Started ART While Confirmed TB and / or TB Treated*/

    public CohortDefinition newlyStartedARTWithTB() {

        String sqlQuery = "select  e.patient_id\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                "join kenyaemr_etl.etl_tb_enrollment tb on tb.patient_id = e.patient_id\n" +
                "where e.program = 'HIV' and e.date_started between date(:startDate) and date(:endDate)\n" +
                "and e.date_started > tb.visit_date\n" +
                "and tb.date_of_discontinuation is null\n" +
                "group by e.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_New_TB");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Newly Started ART While Confirmed TB and / or TB Treated");
        return cd;

    }

    /*Disaggregated by Age / Sex*/

    public CohortDefinition newlyStartedARTByAgeSex() {

        String sqlQuery = "select  fup.patient_id\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "join (select patient_id from kenyaemr_etl.etl_drug_event e\n" +
                "where e.program = 'HIV' and date_started between date(:startDate) and date(:endDate)) started_art on\n" +
                "started_art.patient_id = fup.patient_id group by fup.patient_id;";

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
                "where timestampdiff(month,de.dob,:startDate) >=24 and e.visit_date between date(:startDate) and date(:endDate);";

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

        String sqlQuery = "select v.patient_id from kenyaemr_etl.etl_hei_follow_up_visit v\n" +
                "join kenyaemr_etl.etl_patient_demographics de on de.patient_id = v.patient_id\n" +
                "and timestampdiff(month,de.dob,:startDate) >=24 and de.dead =0\n" +
                "and v.final_antibody_result = 703 and v.visit_date between date(:startDate) and date(:endDate)\n" +
                "group by v.patient_id;";

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

        String sqlQuery = "select v.patient_id from kenyaemr_etl.etl_hei_follow_up_visit v\n" +
                "join kenyaemr_etl.etl_patient_demographics de on de.patient_id = v.patient_id\n" +
                "and timestampdiff(month,de.dob,:startDate) >=24 and de.dead =0\n" +
                "and v.final_antibody_result = 664 and v.visit_date between date(:startDate) and date(:endDate)\n" +
                "group by v.patient_id;";

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
        String sqlQuery = "select v.patient_id from kenyaemr_etl.etl_hei_follow_up_visit v\n" +
                "join kenyaemr_etl.etl_patient_demographics de on de.patient_id = v.patient_id\n" +
                "and timestampdiff(month,de.dob,:startDate) >=24 and de.dead =0\n" +
                "and v.final_antibody_result = 1067 and v.visit_date between date(:startDate) and date(:endDate)\n" +
                "group by v.patient_id;";
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

        String sqlQuery = "select v.patient_id from kenyaemr_etl.etl_hei_follow_up_visit v\n" +
                "join kenyaemr_etl.etl_patient_demographics de on de.patient_id = v.patient_id\n" +
                "and timestampdiff(month,de.dob,:startDate) >=24 and de.dead =1\n" +
                "and v.final_antibody_result = 1067 and v.visit_date between date(:startDate) and date(:endDate)\n" +
                "group by v.patient_id;";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("PMTCT_FO_HEI_DIED_HIV_STATUS_UNKNOWN");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Hei died with Unknown HIV Status");
        return cd;

    }
    /*Alive, Pregnant and on ART for last 12 months*/
    public CohortDefinition pregnantAliveOnARTLast12Months() {

        String sqlQuery = "select net.patient_id from (\n" +
                "select e.patient_id,e.date_started, p.gender,p.dob,d.visit_date as dis_date, if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date, max(fup.next_appointment_date) as latest_tca\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id = e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "where  e.program = 'HIV' and p.dead = 0 and date(e.date_started) between date_sub(:startDate , interval 1 year) and date_sub(:endDate , interval 1 year)\n" +
                "group by e.patient_id\n" +
                "having   (dis_date>:endDate or dis_date is null) and (datediff(latest_tca,:endDate)<=90))net;";

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

        String sqlQuery = "select net.patient_id from (\n" +
                "select e.patient_id,e.date_started, p.gender,p.dob,d.visit_date as dis_date, if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date, max(fup.next_appointment_date) as latest_tca\n" +
                "from kenyaemr_etl.etl_drug_event e\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id\n" +
                "join kenyaemr_etl.etl_mch_postnatal_visit v on v.patient_id = e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "where  e.program = 'HIV' and p.dead = 0 and v.baby_feeding_method in (5526,6046)\n" +
                "and date(e.date_started) between date_sub(:startDate , interval 1 year) and date_sub(:endDate , interval 1 year)\n" +
                "group by e.patient_id\n" +
                "having   (dis_date>:endDate or dis_date is null) and (datediff(latest_tca,:endDate)<=90))net;";

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

        String sqlQuery = "  select net.patient_id from (\n" +
                "select e.patient_id,e.date_started, p.gender,p.dob,d.visit_date as dis_date, if(d.visit_date is not null, 1, 0) as TOut,\n" +
                "if(enr.transfer_in_date is not null, 1, 0) as TIn, max(fup.visit_date) as latest_vis_date, max(fup.next_appointment_date) as latest_tca\n" +
                "  from kenyaemr_etl.etl_drug_event e \n" +
                "  join kenyaemr_etl.etl_patient_demographics p on p.patient_id=e.patient_id \n" +
                "  left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id \n" +
                "  left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id \n" +
                "  left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id \n" +
                "  where  e.program = 'HIV' and p.dead = 0 and date(e.date_started) between date_sub(:startDate , interval 1 year) and date_sub(:endDate , interval 1 year) \n" +
                "  group by e.patient_id \n" +
                "  having   (dis_date>:endDate or dis_date is null) and (datediff(latest_tca,:endDate)<=90))net;\n";

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
                "           group by e.patient_id) e\n" +
                "            join kenyaemr_etl.etl_mch_postnatal_visit v on v.patient_id = e.patient_id\n" +
                "            left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id\n" +
                "            left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "            left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "     where  e.program = 'HIV' and v.baby_feeding_method in (5526,6046)\n" +
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
                "           group by e.patient_id) e\n" +
                "            join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id = e.patient_id\n" +
                "            left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id\n" +
                "            left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "            left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "     where e.program = 'HIV' and date(e.date_started) between date_sub(:startDate , interval 1 year) and date_sub(:endDate , interval 1 year)\n" +
                "     group by e.patient_id\n" +
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
                "           group by e.patient_id) e\n" +
                "            left outer join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id=e.patient_id\n" +
                "            left outer join kenyaemr_etl.etl_hiv_enrollment enr on enr.patient_id=e.patient_id\n" +
                "            left outer join kenyaemr_etl.etl_patient_hiv_followup fup on fup.patient_id=e.patient_id\n" +
                "     where  e.program = 'HIV' and date(e.date_started) between date_sub(:startDate , interval 1 year) and date_sub(:endDate , interval 1 year)\n" +
                "     group by e.patient_id\n" +
                "     )net;";

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
    public CohortDefinition onARTWithSuppressedRoutineVLLast12Months() {

        String sqlQuery = "select x.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract x\n" +
                "inner join kenyaemr_etl.etl_drug_event e on e.patient_id = x.patient_id\n" +
                "where  e.program ='HIV' and ((lab_test=856 and test_result < 1000) or (lab_test=1305 and test_result=1302))\n" +
                "group by x.patient_id\n" +
                "having mid(max(concat(x.visit_date,x.patient_id)),1,10) BETWEEN date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate);";

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

        String sqlQuery = "select x.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract x\n" +
                "inner join kenyaemr_etl.etl_drug_event e on e.patient_id = x.patient_id\n" +
                "where  e.program ='HIV' and ((lab_test=856 and test_result < 1000) or (lab_test=1305 and test_result=1302))\n" +
                "group by x.patient_id\n" +
                "having mid(max(concat(x.visit_date,x.patient_id)),1,10) BETWEEN date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate);";

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

        String sqlQuery = "select x.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract x\n" +
                "inner join kenyaemr_etl.etl_drug_event e on e.patient_id = x.patient_id\n" +
                "where  e.program ='HIV' and ((lab_test=856 and test_result < 1000) or (lab_test=1305 and test_result=1302))\n" +
                "group by x.patient_id\n" +
                "having mid(max(concat(x.visit_date,x.patient_id)),1,10) BETWEEN date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate);";

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

        String sqlQuery = "select ex.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract ex\n" +
                "       inner join kenyaemr_etl.etl_drug_event e on e.patient_id = ex.patient_id\n" +
                "       inner join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id = ex.patient_id\n" +
                "where  e.program ='HIV' and (ex.visit_date BETWEEN date_sub(date(:startDate) , interval 12 MONTH) and date(:endDate))\n" +
                "  and ((lab_test=856 and test_result < 1000) or (lab_test=1305 and test_result=1302))\n" +
                "group by ex.patient_id\n" +
                "having mid(max(concat(ex.visit_date,ex.patient_id)),1,10) BETWEEN date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate);";

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

        String sqlQuery = "select ex.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract ex\n" +
                "       inner join kenyaemr_etl.etl_drug_event e on e.patient_id = ex.patient_id\n" +
                "       inner join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id = ex.patient_id\n" +
                "where  e.program ='HIV' and (ex.visit_date BETWEEN date_sub(date(:startDate) , interval 12 MONTH) and date(:endDate))\n" +
                "  and ((lab_test=856 and test_result < 1000) or (lab_test=1305 and test_result=1302))\n" +
                "group by ex.patient_id\n" +
                "having mid(max(concat(ex.visit_date,ex.patient_id)),1,10) BETWEEN date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate);";

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

        String sqlQuery = "select ex.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract ex\n" +
                "       inner join kenyaemr_etl.etl_drug_event e on e.patient_id = ex.patient_id\n" +
                "       inner join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id = ex.patient_id\n" +
                "where  e.program ='HIV' and (ex.visit_date BETWEEN date_sub(date(:startDate) , interval 12 MONTH) and date(:endDate))\n" +
                "  and ((lab_test=856 and test_result < 1000) or (lab_test=1305 and test_result=1302))\n" +
                "group by ex.patient_id\n" +
                "having mid(max(concat(ex.visit_date,ex.patient_id)),1,10) BETWEEN date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate);";

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

        String sqlQuery = "select ex.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract ex\n" +
                "       inner join kenyaemr_etl.etl_drug_event e on e.patient_id = ex.patient_id\n" +
                "       inner join kenyaemr_etl.etl_mch_postnatal_visit v on v.patient_id = ex.patient_id\n" +
                "where v.baby_feeding_method in (5526,6046) and  (ex.visit_date BETWEEN date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate))\n" +
                "  and e.program ='HIV'\n" +
                "  and ((lab_test=856 and test_result < 1000) or (lab_test=1305 and test_result=1302))\n" +
                "group by ex.patient_id\n" +
                "having mid(max(concat(ex.visit_date,ex.patient_id)),1,10) BETWEEN date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate);";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_SUPP_BF_ROUTINE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Breastfeeding mother on ART with Suppressed Routine VL within last 12 Months");
        return cd;

    }

    /*Breastfeeding mother on ART with Suppressed Targeted VL within last 12 Months*/
    public CohortDefinition bfOnARTSuppTargetedVL() {

        String sqlQuery = "select ex.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract ex\n" +
                "       inner join kenyaemr_etl.etl_drug_event e on e.patient_id = ex.patient_id\n" +
                "       inner join kenyaemr_etl.etl_mch_postnatal_visit v on v.patient_id = ex.patient_id\n" +
                "where v.baby_feeding_method in (5526,6046) and  (ex.visit_date BETWEEN date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate))\n" +
                "  and e.program ='HIV'\n" +
                "  and ((lab_test=856 and test_result < 1000) or (lab_test=1305 and test_result=1302))\n" +
                "group by ex.patient_id\n" +
                "having mid(max(concat(ex.visit_date,ex.patient_id)),1,10) BETWEEN date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate);";

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

        String sqlQuery = "select ex.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract ex\n" +
                "       inner join kenyaemr_etl.etl_drug_event e on e.patient_id = ex.patient_id\n" +
                "       inner join kenyaemr_etl.etl_mch_postnatal_visit v on v.patient_id = ex.patient_id\n" +
                "where v.baby_feeding_method in (5526,6046) and  (ex.visit_date BETWEEN date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate))\n" +
                "  and e.program ='HIV'\n" +
                "  and ((lab_test=856 and test_result < 1000) or (lab_test=1305 and test_result=1302))\n" +
                "group by ex.patient_id\n" +
                "having mid(max(concat(ex.visit_date,ex.patient_id)),1,10) BETWEEN date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate);";

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

         String sqlQuery = "select ex.patient_id\n" +
                 "from kenyaemr_etl.etl_laboratory_extract ex\n" +
                 "      inner join kenyaemr_etl.etl_drug_event e on e.patient_id = ex.patient_id\n" +
                 "      where e.program ='HIV'\n" +
                 "and ((lab_test=856 and test_result < 1000) or (lab_test=1305 and test_result=1302))\n" +
                 "group by ex.patient_id\n" +
                 "having mid(max(concat(ex.visit_date,ex.patient_id)),1,10) BETWEEN date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate);";

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

        String sqlQuery = "select ex.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract ex\n" +
                "      inner join kenyaemr_etl.etl_drug_event e on e.patient_id = ex.patient_id\n" +
                "      where e.program ='HIV'\n" +
                "and ((lab_test=856 and test_result < 1000) or (lab_test=1305 and test_result=1302))\n" +
                "group by ex.patient_id\n" +
                "having mid(max(concat(ex.visit_date,ex.patient_id)),1,10) BETWEEN date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate);";

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

        String sqlQuery = "select ex.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract ex\n" +
                "      inner join kenyaemr_etl.etl_drug_event e on e.patient_id = ex.patient_id\n" +
                "      where e.program ='HIV'\n" +
                "and ((lab_test=856 and test_result < 1000) or (lab_test=1305 and test_result=1302))\n" +
                "group by ex.patient_id\n" +
                "having mid(max(concat(ex.visit_date,ex.patient_id)),1,10) BETWEEN date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate);";

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

        String sqlQuery = "select x.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract x\n" +
                "join kenyaemr_etl.etl_drug_event e on e.patient_id = x.patient_id\n" +
                "where (x.visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and :endDate)\n" +
                "and e.program = 'HIV'  and (x.lab_test in (856, 1305));";

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

        String sqlQuery = "select x.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract x\n" +
                "join kenyaemr_etl.etl_drug_event e on e.patient_id = x.patient_id\n" +
                "where e.program = 'HIV' and (x.visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and :endDate)\n" +
                "  and (x.lab_test in (856, 1305));";

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

        String sqlQuery = "select x.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract x\n" +
                "join kenyaemr_etl.etl_drug_event e on e.patient_id = x.patient_id\n" +
                "where e.program = 'HIV' and (x.visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and :endDate)\n" +
                "  and (x.lab_test in (856, 1305));";

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

        String sqlQuery = "select x.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract x\n" +
                "       join kenyaemr_etl.etl_drug_event e on e.patient_id = x.patient_id\n" +
                "       join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id = x.patient_id\n" +
                "where e.program = 'HIV' and (x.visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and :endDate)\n" +
                "and (x.lab_test in (856, 1305))\n" +
                "having mid(max(concat(v.visit_date,v.patient_id)),1,10) between date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate);";

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

        String sqlQuery = "select x.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract x\n" +
                "       join kenyaemr_etl.etl_drug_event e on e.patient_id = x.patient_id\n" +
                "       join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id = x.patient_id\n" +
                "where e.program = 'HIV' and (x.visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and :endDate)\n" +
                "and (x.lab_test in (856, 1305))\n" +
                "having mid(max(concat(v.visit_date,v.patient_id)),1,10) between date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate);";

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

        String sqlQuery = "select x.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract x\n" +
                "       join kenyaemr_etl.etl_drug_event e on e.patient_id = x.patient_id\n" +
                "       join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id = x.patient_id\n" +
                "where e.program = 'HIV' and (x.visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and :endDate)\n" +
                "and (x.lab_test in (856, 1305))\n" +
                "having mid(max(concat(v.visit_date,v.patient_id)),1,10) between date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate);";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_DENOMINATOR_PREGNANT_UNDOCUMENTED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Pregnant Women on ART with Undocumented VL within last 12 Months");
        return cd;

    }

    /*Breastfeeding Women on ART with Routine VL within last 12 Months*/
    public CohortDefinition breastfeedingOnARTWithRoutineVLLast12Months() {

        String sqlQuery = "select x.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract x\n" +
                "       join kenyaemr_etl.etl_drug_event e on e.patient_id = x.patient_id\n" +
                "       join kenyaemr_etl.etl_mch_postnatal_visit v on v.patient_id = x.patient_id\n" +
                "where e.program = 'HIV' and (v.baby_feeding_method in (5526,6046))\n" +
                "  and (x.visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and :endDate)\n" +
                "  and (x.lab_test in (856, 1305))\n" +
                "group by x.patient_id\n" +
                "having mid(max(concat(v.visit_date,v.patient_id)),1,10) between date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate);";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_DENOMINATOR_BF_ROUTINE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Breastfeeding Women on ART with Routine VL within last 12 Months");
        return cd;

    }

    /*Breastfeeding Women on ART with Targeted VL within last 12 Months*/
    public CohortDefinition breastfeedingOnARTWithTargetedVLLast12Months() {

        String sqlQuery = "select x.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract x\n" +
                "       join kenyaemr_etl.etl_drug_event e on e.patient_id = x.patient_id\n" +
                "       join kenyaemr_etl.etl_mch_postnatal_visit v on v.patient_id = x.patient_id\n" +
                "where e.program = 'HIV' and (v.baby_feeding_method in (5526,6046))\n" +
                "  and (x.visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and :endDate)\n" +
                "  and (x.lab_test in (856, 1305))\n" +
                "group by x.patient_id\n" +
                "having mid(max(concat(v.visit_date,v.patient_id)),1,10) between date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate);";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_DENOMINATOR_BF_TARGETED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Breastfeeding Women on ART with Targeted VL within last 12 Months");
        return cd;

    }

    /*Breastfeeding Women on ART with Undocumented VL within last 12 Months*/
    public CohortDefinition breastfeedingOnARTWithUndocumentedVLLast12Months() {

        String sqlQuery = "select x.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract x\n" +
                "       join kenyaemr_etl.etl_drug_event e on e.patient_id = x.patient_id\n" +
                "       join kenyaemr_etl.etl_mch_postnatal_visit v on v.patient_id = x.patient_id\n" +
                "where e.program = 'HIV' and (v.baby_feeding_method in (5526,6046))\n" +
                "  and (x.visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and :endDate)\n" +
                "  and (x.lab_test in (856, 1305))\n" +
                "group by x.patient_id\n" +
                "having mid(max(concat(v.visit_date,v.patient_id)),1,10) between date_sub(date(:endDate) , interval 12 MONTH) and date(:endDate);";

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

        String sqlQuery = "select x.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract x\n" +
                "join kenyaemr_etl.etl_drug_event e on e.patient_id = x.patient_id\n" +
                "where e.program = 'HIV' and (x.visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and :endDate)\n" +
                "  and (x.lab_test in (856, 1305));";

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

        String sqlQuery = "select x.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract x\n" +
                "join kenyaemr_etl.etl_drug_event e on e.patient_id = x.patient_id\n" +
                "where e.program = 'HIV' and (x.visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and :endDate)\n" +
                "  and (x.lab_test in (856, 1305));";

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

        String sqlQuery = "select x.patient_id\n" +
                "from kenyaemr_etl.etl_laboratory_extract x\n" +
                "join kenyaemr_etl.etl_drug_event e on e.patient_id = x.patient_id\n" +
                "where e.program = 'HIV' and (x.visit_date BETWEEN date_sub(:endDate , interval 12 MONTH) and :endDate)\n" +
                "and (x.lab_test in (856, 1305));";

        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("TX_PVLS_DENOMINATOR_UNDOCUMENTED");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Patients on ART with undocumented VL within last 12 Months by sex/age");
        return cd;

    }
}
